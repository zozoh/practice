package zzh.spider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Node;
import org.nutz.lang.util.Nodes;
import org.nutz.lang.util.Tag;
import org.nutz.resource.Scans;
import org.nutz.trans.Atom;

import zzh.spider.analyzer.AutoIfengAnalyzer;
import zzh.spider.analyzer.IndexAnalyzer;
import zzh.spider.analyzer.SubPageAnalyzer;

/**
 * 一个简单的爬虫程序
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Spider {

	private static int depth = 0;

	private static final int MAX_DEPTH = 3;

	public void logf(String format, Object... args) {
		System.out.printf(format + "\n", args);
	}

	public void log(String msg) {
		System.out.println(msg);
	}

	public Spider(String home) {
		this.cache = new SpiderCache(home);
		this.loader = new SpiderLoader(this);
		this.root = Nodes.create(null);
		// 记录已经分析的 URL
		sp_map = new HashMap<String, SpiderPage>();
		// 获取分析器列表
		List<Class<?>> ayTypes = Scans.me().scanPackage("zzh.spider.analyzer");
		analyzers = new ArrayList<SpiderAnalyzer>(ayTypes.size());
		analyzers.add(new IndexAnalyzer(this));
		analyzers.add(new AutoIfengAnalyzer(this));
		analyzers.add(new SubPageAnalyzer(this));
	}

	private Map<String, SpiderPage> sp_map;

	/**
	 * 保存缓存数据的目录
	 */
	private SpiderCache cache;

	/**
	 * 记录的爬虫爬下一个站点的树形结构
	 */
	private Node<SpiderPage> root;

	/**
	 * 互联网页面读取器
	 */
	private SpiderLoader loader;

	/**
	 * 分析器列表
	 */
	private List<SpiderAnalyzer> analyzers;

	public SpiderCache cache() {
		return cache;
	}

	private SpiderAnalyzer getAnalyzer(SpiderPage page) {
		for (SpiderAnalyzer ay : analyzers)
			if (ay.accept(page))
				return ay;
		throw Lang.makeThrow("Can not find analyzer for page '%s'", page);
	}

	void doSpider(Node<SpiderPage> node) {
		depth++;

		if (depth <= MAX_DEPTH) {
			SpiderPage page = node.get();

			// 分析
			SpiderAnalyzer ay = getAnalyzer(page);
			SpiderLink[] lnks = ay.analyze(page);
			// 记录分析
			sp_map.put(page.getLink().getUrl(), page);

			if (null != lnks && lnks.length > 0) {
				for (SpiderLink subLnk : lnks) {
					logf(" -> %s", subLnk);
					SpiderPage subPage = loader.load(subLnk);
					node.add(Nodes.create(subPage));
				}
				// 进行递归
				for (Node<SpiderPage> subNode : node.getChildren())
					// 如果没有分析过，递归分析
					if (!sp_map.containsKey(subNode.get().getLink().getUrl())) {
						doSpider(subNode);
					}

			}
		}

		depth--;
	}

	public void joinToHtml(Tag tag, Node<SpiderPage> page) {
		// 自己是个 DIV
		Tag b = Tag.tag("b").setText(page.get().getLink().getText());
		Tag a = Tag.tag("a").setText(">>");
		a.attr("href", page.get().getLink().getUrl()).attr("target", "_blank");
		Tag div = Tag.tag(	"div",
							".site_page",
							".node_" + page.depth(),
							".child_" + page.countChildren());
		div.add(b, a);

		// 如果有 children
		if (page.hasChild()) {
			Tag ul = Tag.tag("ul", ".site_page_sub");
			for (Node<SpiderPage> subPage : page.getChildren()) {
				Tag li = Tag.tag("li", ".site_page_sub_li");
				ul.add(li);
				joinToHtml(li, subPage);
			}
			div.add(ul);
		}

		// 加入父
		tag.add(div);

	}

	/**
	 * 调用函数
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// 得到入口 URL
		String url = args.length > 0 ? args[0] : "http://ifeng.com/";

		// 建立爬虫实例
		final Spider sp = new Spider("~/tmp/zzh/spider");

		// 准备开始递归爬取页面
		final SpiderLink lnk = new SpiderLink(sp).setUrl(url).setText("HOME(" + url + ")");
		SpiderPage page = sp.loader.load(lnk);
		sp.root = Nodes.create(page);

		Stopwatch sw = Stopwatch.run(new Atom() {
			public void run() {
				sp.doSpider(sp.root);
			}
		});

		// 显示成 HTML
		sp.log("Rendering ...");
		Tag meta = Tag.tag("meta");
		meta.attr("http-equiv", "Content-Type");
		meta.attr("content", "text/html; charset=utf-8");

		Tag html = Tag.tag("html");
		Tag head = Tag.tag("head");
		head.add(meta);
		head.add(Tag.tag("title").add(Tag.text("ifeng 链接地图")));
		head.add(Tag.tag("script").attr("language", "Javascript").attr("src", "jquery.js"));
		head.add(Tag.tag("script").attr("language", "Javascript").attr("src", "z.js"));
		head.add(Tag.tag("script").attr("language", "Javascript").attr("src", "ifeng.js"));
		head.add(Tag.tag("link").attr("href", "ifeng.css").attr("rel", "stylesheet"));

		Tag body = Tag.tag("body");
		html.add(head);
		html.add(body);
		body.add(Tag.tag("h1").setText("ifeng 链接地图"));
		sp.joinToHtml(body, sp.root);

		// 输入到文件
		File f = Files.createFileIfNoExists("~/tmp/zzh/ifeng/ifeng_links.html");
		Files.write(f, html);

		// 打印 ...
		sp.log(Strings.dup('=', 80));
		sp.log("all done : " + sw.toString());
		// sp.log("Print Tree");
		// sp.log(sp.root.toString());
	}

}
