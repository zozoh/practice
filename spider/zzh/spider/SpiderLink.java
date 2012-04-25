package zzh.spider;

import org.nutz.lang.Strings;

public class SpiderLink extends SpiderObject {

	public SpiderLink(Spider spider) {
		super(spider);
	}

	private String url;

	private String text;

	public String getUrl() {
		return url;
	}

	public SpiderLink setUrl(String url) {
		this.url = Strings.trim(url);
		return this;
	}

	public String getText() {
		return text;
	}

	public SpiderLink setText(String text) {
		this.text = Strings.trim(text);
		return this;
	}

	public SpiderLink duplicate() {
		return new SpiderLink(spider).setUrl(url).setText(text);
	}

	public String toString() {
		return String.format("[%s :: %s]", text, url);
	}

}
