package zzh.spider.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import zzh.spider.Spider;
import zzh.spider.SpiderAnalyzer;
import zzh.spider.SpiderLink;
import zzh.spider.SpiderPage;

public abstract class SimpleAnalyzer extends SpiderAnalyzer {

	private static final Pattern REG = Pattern.compile(	"(<a[^>]*href=\")([^\"]+)(\"[^>]*>)([^<]+)(</a>)",
														Pattern.CASE_INSENSITIVE);

	public SimpleAnalyzer(Spider spider) {
		super(spider);
	}

	@Override
	public SpiderLink[] analyze(SpiderPage page) {
		spider.logf("Analyzing : %s", page);

		// 读取网页
		spider.log("load HTML ...");
		String html = this.spider.cache().read(page.getLink());

		// 得到 HTML 片段
		spider.log("find nav segment ...");

		// 查找导航条
		String seg = findNavSegment(html);

		// 没找到导航条
		if (Strings.isBlank(seg)) {
			spider.log("Find nothing");
			return new SpiderLink[0];
		}

		// 从片段中获取链接
		spider.log("pick links ...");
		List<SpiderLink> list = new ArrayList<SpiderLink>(90);
		Matcher m = REG.matcher(seg);
		while (m.find()) {
			// spider.logf(" %%>: %s", Dumps.matcher(m));
			String url = m.group(2);
			// 分析 URL
			for (String urlp : urlps) {
				int pos = url.indexOf(urlp);
				if (pos > 0) {
					url = url.substring(pos + urlp.length());
					break;
				}
			}
			// 创建 Link 对象
			String text = m.group(4);
			SpiderLink subUrl = this.born(SpiderLink.class).setText(text).setUrl(url);
			list.add(subUrl);
			// 打印 Log
			spider.logf("    ++ %s", subUrl);
		}

		return list.toArray(new SpiderLink[list.size()]);
	}

	private static final String[] urlps = Lang.array("url=", "Redirect=");

	protected abstract String findNavSegment(String html);

}
