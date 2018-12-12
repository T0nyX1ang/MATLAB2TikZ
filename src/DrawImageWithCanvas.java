import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class DrawImageWithCanvas extends Canvas{
	BufferedImage image;
	Image zoomed;
	int image_width = 0;
	int image_height = 0;
	
	public void getImage(BufferedImage image) {
		this.image = image;
		this.zoom();
		this.image_width = zoomed.getWidth(null);
		this.image_height = zoomed.getHeight(null);
	}
	
	public void paint(Graphics g) {
		g.drawImage(zoomed, this.getWidth() / 2 - image_width / 2, 
							this.getHeight() / 2 - image_height / 2,
							image_width, image_height, this);
	}
	
	private void zoom() {
		zoomed = image.getScaledInstance(image.getWidth(), image.getHeight(), Image.SCALE_SMOOTH);
	}
	
}
