package xxx;

import java.io.IOException;

import org.nutz.lang.Files;

public class AbcMain {

	public static void main(String[] args) throws IOException {
		Files.cleanAllFolderInSubFolderes(Files.findFile("~/tmp/littlews2010/done"), ".svn");
		System.out.println("done!");
	}

}
