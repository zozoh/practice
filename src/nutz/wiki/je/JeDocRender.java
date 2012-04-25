package nutz.wiki.je;

import org.nutz.doc.DocRender;
import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZColor;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZFont;
import org.nutz.lang.Strings;

public class JeDocRender implements DocRender<String> {

	@Override
	public String render(ZDoc doc) {
		StringBuilder sb = new StringBuilder();

		// 输出标题
		sb.append("[b][color=gray][size=xx-large]");
		sb.append(doc.getTitle());
		sb.append("[/size][/color][/b]");

		// 循环输出层级
		ZBlock[] ps = doc.root().children();
		for (ZBlock p : ps) {
			renderBlock(sb, p);
		}

		return sb.toString();
	}

	private void renderBlock(StringBuilder sb, ZBlock block) {
		sb.append("\n\n");
		// <Table>
		if (block.isTable()) {
			sb.append("[table]");
			ZBlock[] rows = block.children();
			for (ZBlock row : rows) {
				sb.append("\n");
				for (ZBlock td : row.children()) {
					sb.append('|');
					renderToElement(sb, td.eles());
				}
				sb.append("|");
			}
			sb.append("\n[/table]");
		}
		// <Hr>
		else if (block.isHr()) {
			sb.append(Strings.dup('-', 80));
		}
		// <OL>
		else if (block.isOL()) {
			sb.append("\n[list=1]");
			for (ZBlock li : block.children()) {
				renderListItem(sb, li);
			}
			sb.append("[/list]");
		}
		// <UL>
		else if (block.isUL()) {
			sb.append("[list]");
			for (ZBlock li : block.children()) {
				renderListItem(sb, li);
			}
			sb.append("\n[/list]");
		}
		// <Pre>
		else if (block.isCode()) {
			sb.append("[code]");
			sb.append(block.getText());
			sb.append("[/code]");
		}
		// <H1~6>
		else if (block.isHeading()) {
			sb.append("[b][size=large]");
			sb.append(block.getText());
			sb.append("[/size][/b]");
			ZBlock[] ps = block.children();
			for (ZBlock p : ps)
				renderBlock(sb, p);
		}
		// <P>
		else {
			renderToElement(sb, block.eles());
		}
	}

	private void renderListItem(StringBuilder sb, ZBlock li) {
		sb.append("\n[*]");
		renderToElement(sb, li.eles());
		if (li.hasChildren()) {
			for (ZBlock sub : li.children()) {
				sb.append(" ([b][color=gray] ");
				for(ZBlock subli : sub.children())
				sb.append(subli.getText()).append(" , ");
				sb.append("[/color][/b])");
			}
		}
	}

	private void renderToElement(StringBuilder sb, ZEle[] eles) {
		for (ZEle ele : eles)
			sb.append(ele2str(ele));
	}

	private String ele2str(ZEle ele) {
		String str = ele.getText();
		if (ele.hasHref()) {
			str = wrapText("url", ele.getHref().getPath(), str);
		}
		if (ele.hasStyle()) {
			if (ele.getStyle().hasFont()) {
				ZFont font = ele.getStyle().getFont();
				if (font.isItalic())
					str = wrapText("i", str);
				if (font.hasColor()) {
					ZColor color = font.getColor();
					if (color.getBlue() == color.getGreen() && color.getBlue() == color.getRed())
						str = wrapText("color", "gray", str);
				}
				if (font.isBold()){
					str = wrapText("b", str);
					str = wrapText("color","darkblue",str);
				}
			}
		}
		return str;
	}

	private String wrapText(String tagName, String str) {
		return wrapText(tagName, null, str);
	}

	private String wrapText(String tagName, String attr, String str) {
		StringBuilder sb = new StringBuilder();
		sb.append('[').append(tagName);
		if (!Strings.isBlank(attr))
			sb.append('=').append(attr);
		sb.append(']').append(str);
		sb.append("[/").append(tagName).append("]");
		return sb.toString();
	}

}
