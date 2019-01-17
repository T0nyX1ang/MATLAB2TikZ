import java.awt.image.*;
import java.awt.Image;
import java.io.*;
import javax.imageio.ImageIO;

public class WordRecognition extends CommandExecutor{
	// auto recognize words (OCR) module.
	// This project is PENDING now.
	double xStart = 0;
	double xStop = 0;
	int xStep = 0;
	double yStart = 0;
	double yStop = 0;
	int yStep = 0;
	String xLabel = null;
	String yLabel = null;
	String legendTitle = null;
	String[] legendName = null;
	private PictureAlgorithms pa = new PictureAlgorithms();
	
	public void getXLabelString(BufferedImage xLabelImage) {
		if (xLabelImage == null) {
			System.out.println("X Label doesn't seem to exist. Setting it to empty string.");
			xLabel = "";
			return;
		}
		xLabelImage = pa.resizeImage(xLabelImage, 5, -1, false);
		BufferedImage image = pa.otsu(pa.getGray(xLabelImage), xLabelImage.getWidth(), xLabelImage.getHeight());
		image = pa.convertToBufferedImage(image.getScaledInstance(image.getWidth() * 5, image.getHeight() * 5, Image.SCALE_SMOOTH));
		try {
			ImageIO.write(image, "TIFF", new File("." + File.separator + "test" + File.separator + "XLabel.tiff"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getYLabelString(BufferedImage yLabelImage) {
		if (yLabelImage == null) {
			System.out.println("Y Label doesn't seem to exist. Setting it to empty string.");
			yLabel = "";
			return;
		}
		yLabelImage = pa.resizeImage(yLabelImage, -1, 5, true);
		BufferedImage image = pa.otsu(pa.getGray(yLabelImage), yLabelImage.getWidth(), yLabelImage.getHeight());
		image = pa.convertToBufferedImage(image.getScaledInstance(image.getWidth() * 5, image.getHeight() * 5, Image.SCALE_SMOOTH));
		try {
			ImageIO.write(image, "TIFF", new File("." + File.separator + "test" + File.separator + "YLabel.tiff"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void getXDetail(BufferedImage xAxisImage) {
		xAxisImage = pa.resizeImage(xAxisImage, 5, -1, false);
		BufferedImage image = pa.otsu(pa.getGray(xAxisImage), xAxisImage.getWidth(), xAxisImage.getHeight());
		image = pa.convertToBufferedImage(image.getScaledInstance(image.getWidth() * 5, image.getHeight() * 5, Image.SCALE_SMOOTH));
		try {
			ImageIO.write(image, "TIFF", new File("." + File.separator + "test" + File.separator + "XAxis.tiff"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void getYDetail(BufferedImage yAxisImage) {
		yAxisImage = pa.resizeImage(yAxisImage, -1, 5, false);
		BufferedImage image = pa.otsu(pa.getGray(yAxisImage), yAxisImage.getWidth(), yAxisImage.getHeight());
		image = pa.convertToBufferedImage(image.getScaledInstance(image.getWidth() * 5, image.getHeight() * 5, Image.SCALE_SMOOTH));
		try {
			ImageIO.write(image, "TIFF", new File("." + File.separator + "test" + File.separator + "YAxis.tiff"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getLegendTitleString(BufferedImage legendTitleImage) {
		legendTitleImage = pa.resizeImage(legendTitleImage, 5, 5, false);
		BufferedImage image = pa.otsu(pa.getGray(legendTitleImage), legendTitleImage.getWidth(), legendTitleImage.getHeight());
		image = pa.convertToBufferedImage(image.getScaledInstance(image.getWidth() * 5, image.getHeight() * 5, Image.SCALE_SMOOTH));
		try {
			ImageIO.write(image, "TIFF", new File("." + File.separator + "test" + File.separator + "LegendTitle.tiff"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getLegendNameString(BufferedImage legendNameImage, int number) {
		legendNameImage = pa.resizeImage(legendNameImage, 5, 5, false);
		BufferedImage image = pa.otsu(pa.getGray(legendNameImage), legendNameImage.getWidth(), legendNameImage.getHeight());
		image = pa.convertToBufferedImage(image.getScaledInstance(image.getWidth() * 5, image.getHeight() * 5, Image.SCALE_SMOOTH));
		try {
			ImageIO.write(image, "TIFF", new File("." + File.separator + "test" + File.separator + "LegendName" + number + ".tiff"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
