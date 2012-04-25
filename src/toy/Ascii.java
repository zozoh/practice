package toy;

import org.nutz.lang.Strings;

public class Ascii {

	public static void main(String[] args) {
		System.out.printf("%d\n\n",(int)'`');
		
		for(int i=1;i<1024;i++){
			String s = String.format("'%c'(%d)", (char)i,i);
			System.out.print(Strings.alignLeft(s, 16, ' '));
			if(i%8==0)
				System.out.println();
		}
	}

}
