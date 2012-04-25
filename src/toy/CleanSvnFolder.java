package toy;

import java.io.File;
import java.io.IOException;

import org.nutz.lang.Files;

public class CleanSvnFolder {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File dir = Files.findFile(args[0]);
		if (!dir.isDirectory()) {
			System.err.printf("'%s' should be a directory!\n", args[0]);
			System.exit(0);
		}
		System.out.printf("Clearn : '%s'\n", args[0]);
		Files.cleanAllFolderInSubFolderes(dir, ".svn");
		System.out.println("Done!");
	}

}
