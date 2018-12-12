import java.io.*;

public class TeXCompiler extends CommandExecutor{
	// This class is designed to compile LaTeX documents.
	// More properly, it's for previewing TikZ pictures.
	
	public void compileTeX(String backend, String filehash) {
		try {
			TeXCompiler tc = new TeXCompiler();
			final String params = " -shell-escape -interaction=nonstopmode -synctex=1 ";
			final String filename = "test.tex";
			String directory = "." + File.separator + "temp" + File.separator + filehash;
			switch (backend) {
			case "xelatex":
			case "pdflatex":
				System.out.println("Compiling LaTeX document.");
				tc.execute(backend + params + filename, directory);
				if (tc.checkValid(filehash))
					break;
				else
					throw new IOException();
			default:
				System.err.println("Unsupported LaTeX backend. Use 'xelatex' or 'pdflatex' instead.");
				throw new RuntimeException();
			}
		} catch (RuntimeException re) {
			System.err.println("Compile error. " + re);
		} catch (IOException e) {
			System.err.println("Unknown error. " + e);
		}
	}
	
	public boolean checkValid(String filehash) {
		final String filename = "test.pdf";
		final String picturename = "test-figure0.pdf";
		String directory = "." + File.separator + "temp" + File.separator + filehash;
		File pdf_verify = new File(directory + File.separator + filename).getAbsoluteFile();
		File pic_verify = new File(directory + File.separator + picturename).getAbsoluteFile();
		if (pdf_verify.exists() && pdf_verify.isFile())
			if (pic_verify.exists() && pic_verify.isFile()) {
				System.out.println("LaTeX document is compiled successfully.");
				return true;
			}
			else {
				System.err.println("LaTeX syntax error. Please check your file.");
				return false;
			}		
		else {
			System.err.println("Base picture generated error.");
			return false;
		}
	}
	
}
