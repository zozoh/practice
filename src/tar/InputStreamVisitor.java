package tar;

import java.io.InputStream;

public interface InputStreamVisitor {

	void visit(String name, InputStream ins);
	
}
