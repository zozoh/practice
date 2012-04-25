package zzh.spider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

public class SpiderCache {

	/**
	 * 保存缓存数据的目录
	 */
	private File home;

	private static final Pattern REG = Pattern.compile(	"(<title>)([^<]*)(</title>)",
														Pattern.CASE_INSENSITIVE);

	public SpiderCache(String home) {
		this.home = Files.createDirIfNoExists(home);
		// Files.clearDir(this.home);
	}

	private File cacheFile(SpiderLink lnk) {
		String path = lnk.getUrl().replaceAll("[:./\\\\?&=]", "_");
		if (path.startsWith("http___"))
			path = path.substring("http___".length());
		return Files.getFile(home, path);
	}

	public boolean has(SpiderLink lnk) {
		return cacheFile(lnk).exists();
	}

	public String read(SpiderLink lnk) {
		File f = cacheFile(lnk);
		if (f.exists()) {
			return Files.read(f);
		}
		return null;
	}

	public String getPageTitle(SpiderLink lnk) {
		String html = read(lnk);

		// 分析网页标题
		Matcher m = REG.matcher(html);
		if (m.find())
			return m.group(2);

		return "Unknown Spider Title";
	}

	public void write(SpiderPage page, InputStream ins) {
		File f = cacheFile(page.getLink());
		if (!f.exists()) {
			try {
				Files.createNewFile(f);
			}
			catch (IOException e) {
				throw Lang.wrapThrow(e);
			}
			Files.write(f, ins);
		} else {
			Streams.safeClose(ins);
		}
	}

}
