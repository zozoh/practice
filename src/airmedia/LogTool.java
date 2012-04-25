package airmedia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;

public class LogTool {
	
	private static String namePrefix = "http.log";

	private static List<String> lines = new LinkedList<String>();

	private static String[] subs = null;

	private static String path = "~/workspace/DT/sep-log/";

	private static File logHome = Files.findFile(path);

	public static void main(String[] args) throws Exception {
		File ta = Files.createFileIfNoExists("~/tmp/danoo/DT/http-all");
		BufferedWriter bw = Streams.buffw(Streams.fileOutw(ta));
		File dir = Files.findFile("~/tmp/danoo/DT/http");
		for (File f : dir.listFiles()) {
			if (!f.isDirectory())
				continue;
			String nm = f.getName();
			for (File t : f.listFiles()) {
				String tnm = t.getName();
				System.out.println(nm + " " + tnm + " :: ...");
				BufferedReader br = Streams.buffr(Streams.fileInr(t));
				String line;
				while (null != (line = br.readLine())) {
					if (line.length() == 17)
						bw.write(Strings.trim(line) + "\n");
				}
				Streams.safeClose(br);
			}
		}

		Streams.safeClose(bw);
	}

	static void splitHTTPLogByTime() throws IOException {
		File dir = Files.findFile("zzh/data/http");
		for (File f : dir.listFiles()) {
			if (!f.isFile())
				continue;
			for (int i = 0; i < 24; i++)
				sortOneDay(f.getName(), i);
		}
		System.out.println("~~ ALL DONE !! ~~");
	}

	static void sortOneDay(final String day, int hour) throws IOException {
		lines.clear();
		String fHour = Strings.alignRight("" + hour, 2, '0');
		String sHour = "]" + day.substring(2) + " " + fHour + ":";
		subs = Lang.array(sHour);
		System.out.println();
		System.out.println(Json.toJson(subs, JsonFormat.nice()));
		System.out.println();
		Disks.visitFile(logHome, new FileVisitor() {
			public void visit(File file) {
				System.out.printf("%s > %s", Files.getName(file.getParent()), file.getName());
				BufferedReader br = Streams.buffr(Streams.fileInr(file));
				String line;
				try {
					int nu = 0;
					while (null != (line = br.readLine())) {
						nu++;
						boolean matched = true;
						if (null != subs)
							for (String sub : subs) {
								if (!line.contains(sub)) {
									matched = false;
									break;
								}
							}
						if (matched) {
							if (line.length() > 38) {
								System.out.printf("\n    %d >> %s", nu, line);
								lines.add(line);
							}
						}
					}
				}
				catch (IOException e) {
					throw Lang.wrapThrow(e);
				}
				finally {
					System.out.println();
					Streams.safeClose(br);
				}
			}
		}, new FileFilter() {
			public boolean accept(File f) {
				if (null == namePrefix)
					return true;
				return f.isDirectory() ? true : f.getName().startsWith(namePrefix)
												&& f.getName().endsWith(day);
			}

		});
		System.out.println("Sorting ...");
		Collections.sort(lines, new Comparator<String>() {
			public int compare(String o1, String o2) {
				String s1 = cutTime(o1);
				String s2 = cutTime(o2);
				return s1.compareTo(s2);
			}
		});
		int i = 0;
		System.out.println("Write to >> " + day);
		File f = Files.createFileIfNoExists("~/tmp/danoo/DT/http/" + day + "/" + fHour);
		BufferedWriter bw = Streams.buffw(Streams.fileOutw(f));
		for (String line : lines) {
			// System.out.printf("%8d : %s :: %s...\n", ++i, cutTime(line),
			// line.substring(0,30));
			// System.out.println(cutTime(line));
			bw.write(cutTime(line) + "\n");
			System.out.print('.');
			if (i > 0 && (i % 80 == 0))
				System.out.println();
			i++;
		}
		Streams.safeClose(bw);
		System.out.println("<< Done for write: " + f);
	}

	private static String cutTime(String line) {
		return line.substring(21, 38);
	}
}
