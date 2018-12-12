import java.io.*;
import org.apache.commons.codec.digest.DigestUtils;

public class TikZConvert {
	// This is a class designed to convert TikZ files to compilable LaTeX files.
	
	public String convert(File file) {
		String hash = null;
		try {
			// create testing directory
			hash = DigestUtils.md5Hex(new FileInputStream(file));
			File new_directory = new File("." + File.separator + "temp" + File.separator + hash).getAbsoluteFile();
			try {
				if (!new_directory.exists() || !new_directory.isDirectory())
					if (!new_directory.mkdirs())
						throw new IOException();
			} catch (SecurityException se) {
				System.err.println("Permission denied. " + se);
			} catch (IOException e) {
				System.err.println("Unknown error. " + e);
			}
			// read
			System.getProperty("line.separator");
			String sep = System.lineSeparator();
			BufferedReader in = new BufferedReader(new FileReader(file));
			// before TikZ
			String texoptions = "%!TeX options = -shell-escape" + sep;
			String documentclasses = "\\documentclass{article}" + sep;
			String packages = "\\usepackage{tikz}" + sep;
			String packagelibraries = "\\usetikzlibrary{external, datavisualization, fpu}" + sep;
			String externalize = "\\tikzexternalize" + sep;
			String start_document = "\\begin{document}" + sep;
			String start_center = "\\begin{center}" + sep;
			// after TikZ
			String end_center = "\\end{center}" + sep;
			String end_document = "\\end{document}" + sep;
			// total string
			String total = texoptions + documentclasses + packages + packagelibraries + 
							externalize + start_document + start_center;
			String line;
			while ((line = in.readLine()) != null) {
				total = total + line + sep;
			}
			total = total + end_center + end_document;
			in.close();
			
			// write
			FileWriter fw = new FileWriter(new_directory + File.separator + "test.tex");
			PrintWriter out = new PrintWriter(new BufferedWriter(fw));
			out.println(total);
			out.close();
			if (out.checkError()) {
				System.err.println("Output error.");
				throw new IOException();
			} else {
				System.out.println("Convert [TikZ->LaTeX] successfully.");
			}
		} catch (NullPointerException npe) {
			System.err.println("Filename not assigned. " + npe);
		} catch (FileNotFoundException fnfe) {
			System.err.println("File not found. " + fnfe);
		} catch (IOException e) {
			System.err.println("Unknown error. " + e);
		}
		return hash;
	}
}
