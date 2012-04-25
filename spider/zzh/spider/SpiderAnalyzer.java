package zzh.spider;

/**
 * 抽象分析器
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class SpiderAnalyzer extends SpiderObject {

	public SpiderAnalyzer(Spider spider) {
		super(spider);
	}

	/**
	 * @param page
	 *            爬虫抓下来的页面
	 * @return 是否可以分析一个 Page
	 */
	public abstract boolean accept(SpiderPage page);

	/**
	 * 从一个页面，得到一组 URL，以便爬虫程序继续从互联网读取内容
	 * 
	 * @param page
	 *            页面
	 * @return 一组 URL 描述
	 */
	public abstract SpiderLink[] analyze(SpiderPage page);

}
