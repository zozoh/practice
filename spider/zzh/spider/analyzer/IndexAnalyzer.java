package zzh.spider.analyzer;

import zzh.spider.Spider;
import zzh.spider.SpiderPage;

public class IndexAnalyzer extends SimpleAnalyzer {

	private static final String startDiv = "<div class=\"headNav cWhite\">";

	public IndexAnalyzer(Spider spider) {
		super(spider);
	}

	@Override
	public boolean accept(SpiderPage page) {
		return page.getLink().getUrl().startsWith("http://ifeng.com");
	}

	@Override
	protected String findNavSegment(String html) {
		int start = html.indexOf(startDiv);
		if (start <= 0)
			return null;
		int end = html.indexOf("</div>", start + startDiv.length());
		return html.substring(start, end);
	}

}
