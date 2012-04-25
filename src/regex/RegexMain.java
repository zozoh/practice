package regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Dumps;
import org.nutz.lang.Strings;

public class RegexMain {
	
	private static final Pattern META = Pattern.compile("^(.*#title)(.*)$");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "﻿#title: 测试 Links ";
		Matcher m = META.matcher(Strings.trim(s));
		System.out.println(Dumps.matcher(m));
	}

}
