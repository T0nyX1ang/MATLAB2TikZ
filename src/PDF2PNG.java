import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDF2PNG {
	BufferedImage image = null;
	
	public void pdf2png(File file, int DPI){
		try {
			PDDocument pdfdoc = PDDocument.load(file);
			PDFRenderer pdfrenderer = new PDFRenderer(pdfdoc);
			// We assume the page number is 1.
			if (pdfdoc.getNumberOfPages() == 1 && DPI >= 0)
			{
				// User-define DPI mode
				image = pdfrenderer.renderImageWithDPI(0, DPI);
				System.out.println("Converted [pdf->png] successfully.");
			}
			else if (DPI != 0)
				throw new IndexOutOfBoundsException("Page number exceeded.");
			else
				throw new IndexOutOfBoundsException("DPI invalid.");
			pdfdoc.close(); // close the PDF document
		} catch (FileNotFoundException fnfe) {
			System.err.println("File not found due to LaTeX compile error. " + fnfe);
		} catch (IOException e)
		{
			System.err.println("Unknown error: \n" + e);
		}
	}

}
