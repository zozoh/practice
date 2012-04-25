package zzh.spider;

import org.nutz.http.Http;

/**
 * 根据 spiderInfo 读取一个 spiderPage，并保存在缓存中
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class SpiderLoader extends SpiderObject {

	public SpiderLoader(Spider spider) {
		super(spider);
	}

	public SpiderPage load(SpiderLink lnk) {
		SpiderPage page = this.born(SpiderPage.class).setLink(lnk);

		// 从网络上读取内容
		if (!spider.cache().has(lnk)) {
			spider.logf(" - send HTTP : %s", lnk);
			spider.cache().write(page, Http.get(lnk.getUrl()).getStream());
		} else {
			spider.logf(" - find in cache : %s", lnk);
		}
		// 获取网页标题
		page.setTitle(spider.cache().getPageTitle(lnk));
		spider.logf(" - title as '%s'", page.getTitle());

		return page;
	}

}
