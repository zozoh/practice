package zzh;

import org.nutz.lang.Strings;
import org.nutz.rain.session.bean.FileObject;

public class ZZHMain {

	public static void main(String[] args) throws Exception {
		long fid = 4294967491L;

		int pureId = (int) (fid & FileObject.MAX_ID);
		String s = Strings.alignRight(Long.toHexString(pureId), 8, '0')
							.replaceAll("\\p{XDigit}{2}", "/$0");
		
		System.out.println(s);

	}
}
