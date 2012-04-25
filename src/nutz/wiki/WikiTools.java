package nutz.wiki;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nutz.wiki.je.JeDocRender;

import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.zdoc.ZDocParser;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

@SuppressWarnings("unchecked")
public class WikiTools {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String path = "~/workspace/git/github/nutz/doc/manual/nutz_release_notes.man";
		// printContributor(Files.read(path));
		System.out.println(zdoc2JeBBCode(Files.read(path)));
		// System.out.println(issueTableToWiki("~/tmp/nutz/issues-1.b.43.xml"));
	}

	private static final String url = "https://github.com/nutzam/nutz/issues/";

	private static Map<String, String> dict = (Map<String, String>) Json.fromJson(Streams.fileInr("nutz/wiki/dict.person"));

	static String issueTableToWiki(String path) throws Exception {

		String pathOut = path + ".tidy.xml";
		File out = Files.createFileIfNoExists(pathOut);

		Tidy tidy = new Tidy();
		tidy.setDropFontTags(true);
		tidy.setDropProprietaryAttributes(true);
		tidy.setTabsize(4);
		tidy.setDocType("omit");
		tidy.setHideComments(true);
		tidy.setDocType("");

		Document doc = tidy.parseDOM(Streams.fileInr(path), Streams.fileOutw(out));
		NodeList rows = doc.getDocumentElement().getElementsByTagName("tr");
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < rows.getLength(); i++) {
			Element row = (Element) rows.item(i);
			NodeList cells = row.getElementsByTagName("td");
			String iid = getTdText(cells, 2).substring(1);
			String title = getTdText(cells, 3);
			String str = String.format("\n    * [%s%s Issue %s] %s", url, iid, iid, title);
			sb.append(str.replaceAll("[ ][0-9]+[ \t]+comment[s]?[ ]", ""));
		}

		return sb.toString();
	}

	private static void appendNodeText(Node nd, StringBuilder sb) {
		if (nd.getNodeType() == Node.TEXT_NODE) {
			if ("p".equalsIgnoreCase(nd.getParentNode().getNodeName())
				|| "by".equalsIgnoreCase(Strings.trim(nd.getNodeValue()))) {} else {
				sb.append(nd.getNodeValue());
			}
		} else if (nd.getNodeType() == Node.ELEMENT_NODE) {
			Element ele = (Element) nd;
			if (ele.getNodeName().equals("strong")) {
				sb.append(" {#AAA;by} {*");
				joinText(sb, ele);
				sb.append("} ");
			} else if (ele.getNodeName().equalsIgnoreCase("time")) {
				// skip
			} else {
				joinText(sb, ele);
			}
		}
	}

	private static void joinText(StringBuilder sb, Element ele) {
		NodeList children = ele.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			appendNodeText(children.item(i), sb);
		}
	}

	private static String getTdText(NodeList cells, int index) {
		StringBuilder sb = new StringBuilder();
		appendNodeText(cells.item(index), sb);
		return Strings.trim(sb);
	}

	static void printContributor(String str) {
		List<String> cs = readContributors(str);
		for (String c : cs)
			System.out.println(c);

		System.out.println(Strings.dup('-', 30));
		System.out.println();
		System.out.println(renderContriTable(cs));
	}

	static String zdoc2JeBBCode(String str) {
		ZDoc doc = (new ZDocParser(Lang.context())).parse(Lang.inr(str));
		return (new JeDocRender()).render(doc);
	}

	static String renderContriTable(List<String> names) {
		int max = Strings.maxLength(names);
		String[] titles = Lang.array("贡献者", "问题", "博客", "支持", "代码", "示例", "文档", "测试");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < titles.length; i++) {
			sb.append(" || ").append(titles[i]);
			if (i == 0) {
				sb.append(Strings.dup(' ', max - titles[i].length()));
			}
		}
		sb.append(" ||");

		for (String nm : names) {
			sb.append("\n|| ");
			sb.append(nm);
			sb.append(Strings.dup(' ', max - nm.length()));
			sb.append("\t||");
			for (int i = 1; i < titles.length; i++) {
				sb.append("  ");
				sb.append(i == 1 ? 'O' : '-');
				sb.append("   ||");
			}
		}
		return sb.toString();
	}

	static List<String> readContributors(String str) {
		Pattern regex = Pattern.compile("([{][*])(.*)([}])");
		Matcher m = regex.matcher(str);
		Set<String> set = new HashSet<String>();
		while (m.find()) {
			String nm = m.group(2);
			if (!nm.contains("%"))
				set.add(getRealName(nm));
		}

		List<String> re = new ArrayList<String>(set.size());
		re.addAll(set);
		Collections.sort(re);
		return re;
	}

	static String getRealName(String name) {
		if (dict.containsKey(name))
			return dict.get(name).toString();

		if (name.contains("@"))
			name = name.substring(0, name.indexOf('@'));

		return name;
	}

}
