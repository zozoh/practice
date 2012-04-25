package zzh.spider;

import org.nutz.lang.Strings;

/**
 * 爬虫爬下来的东西，封装了这个东西的相关操作
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class SpiderPage extends SpiderObject {

	public SpiderPage(Spider spider) {
		super(spider);
	}

	private SpiderLink link;

	private String title;

	public SpiderLink getLink() {
		return link;
	}

	public SpiderPage setLink(SpiderLink link) {
		this.link = link;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public SpiderPage setTitle(String title) {
		this.title = Strings.trim(title);
		return this;
	}

	public SpiderPage duplicate() {
		SpiderPage page = new SpiderPage(spider);
		page.setTitle(title).setLink(link.duplicate());
		return page;
	}

	public String toString() {
		return String.format("\"%s\" -> %s", title, link);
	}

}
