package zzh.spider;

import org.nutz.lang.Mirror;

/**
 * 所有的爬虫各个逻辑部分，都从这个对象继承
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class SpiderObject {

	/**
	 * 爬虫主程序，用来存放一些全局的变量和操作
	 */
	protected Spider spider;

	public SpiderObject(Spider spider) {
		this.spider = spider;
	}

	/**
	 * 构建一个新得爬虫操作对象，本函数自动填充构造函数的 spider 参数
	 * 
	 * @param <T>
	 * @param classOfT
	 *            操作对象的类型
	 * @return 操作对象
	 */
	public <T extends SpiderObject> T born(Class<T> classOfT) {
		return Mirror.me(classOfT).born(spider);
	}

}
