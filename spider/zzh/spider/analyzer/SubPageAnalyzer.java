package zzh.spider.analyzer;

import zzh.spider.Spider;
import zzh.spider.SpiderPage;

public class SubPageAnalyzer extends SimpleAnalyzer {

	private static final String startDiv = "<div class=\"h_subNav\">";

	public SubPageAnalyzer(Spider spider) {
		super(spider);
	}

	@Override
	public boolean accept(SpiderPage page) {
		return !page.getLink().getUrl().equalsIgnoreCase("http://ifeng.com/");
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
