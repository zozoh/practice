package zzh.spider.analyzer;

import zzh.spider.Spider;
import zzh.spider.SpiderPage;

public class AutoIfengAnalyzer extends SimpleAnalyzer {

	private static final String startDiv = "<div class=\"h_autoNav\">";

	public AutoIfengAnalyzer(Spider spider) {
		super(spider);
	}

	@Override
	public boolean accept(SpiderPage page) {
		return page.getLink().getUrl().indexOf("auto.ifeng.com") > 0;
	}

	@Override
	protected String findNavSegment(String html) {
		int start = html.indexOf(startDiv);
		if (start <= 0)
			return null;
		start = html.indexOf("<ul", start);
		int end = html.indexOf("</div>", start + 4);
		return html.substring(start, end);
	}

}
