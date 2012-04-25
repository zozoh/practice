package toy;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Files;

public class FindMessageKey {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String txt = Files.read(args[0]);
		Pattern regex = Pattern.compile("([$][.]msg[(]\")(.+)(\"[)])");
		Matcher m = regex.matcher(txt);
		List<String> keys = new LinkedList<String>();
		while (m.find()) {
			keys.add(m.group(2));

		}
		Collections.sort(keys);
		for (String key : keys)
			System.out.printf("%s=%s\n", key, "XXX");
	}

}
