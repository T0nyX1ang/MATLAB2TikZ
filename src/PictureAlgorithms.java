import java.awt.image.*;  

public class PictureAlgorithms {
	// These utilities are under development now. They are algorithms.
	
	public static int[][] getGray(BufferedImage image) {
		int width = image.getTileWidth();
		int height = image.getHeight();
		int[][] gray = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int nowRed = (int) (image.getRGB(i, j) >> 16) & 0xFF;
				int nowGreen = (int) (image.getRGB(i, j) >> 8) & 0xFF;
				int nowBlue = (int) (image.getRGB(i, j) >> 0) & 0xFF;
				gray[i][j] = (int) Math.pow((Math.pow(nowRed, 2.2) * 0.2973 + 
											 Math.pow(nowGreen, 2.2)* 0.6274 + 
											 Math.pow(nowBlue, 2.2) * 0.0753), 1 / 2.2);
			}
		}
		return gray;
	}
	
	public static BufferedImage otsu(int[][] gray, int width, int height){
		int[] histData = new int[width * height];
		// Calculate histogram
		for (int i = 0; i < width; i++){
			for (int j = 0; j < height; j++){
				int red = 0xFF & gray[i][j];
				histData[red]++;
			}
		}
		// Total number of pixels
		int total = width * height;
		float sum = 0;
		float sumB = 0;
		int wB = 0;
		int wF = 0;
		float varMax = 0;
		int threshold = 0;
		
		for (int t = 0; t < 256; t++)
			sum += t * histData[t];
		for (int t = 0; t < 256; t++){
			wB += histData[t]; // Weight Background
			if (wB == 0)
				continue;
			wF = total - wB; // Weight Foreground
			if (wF == 0)
				break;
			sumB += (float) (t * histData[t]);
			float mB = sumB / wB; // Mean Background
			float mF = (sum - sumB) / wF; // Mean Foreground
			// Calculate Between Class Variance
			float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);
			// Check if new maximum found
			if (varBetween > varMax){
				varMax = varBetween;
				threshold = t;
			}
		}
		// make it a binary picture.
		BufferedImage result = new BufferedImage(width, height, 12);
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) 
				if (gray[i][j] > threshold)
					result.setRGB(i, j, 65535);
				else
					result.setRGB(i, j, 0);
		return result;
	}
	
	public static BufferedImage clearAxisTicks(int[][] gray, int width, int height) {
		// clear redundant axis pixels and tick pixels
		// x, y axis
		for (int i = 0; i < width; i++) {
			gray[i][0] = 255;
			gray[i][height - 1] = 255;
		}
			
		for (int j = 0; j < height; j++) {
			gray[0][j] = 255;
			gray[width - 1][j] = 255;
		}
		
		// x, y ticks
		for (int i = 1; i < width - 1; i++) {
			int top_mark = 1;
			int bottom_mark = height - 2;
			if (gray[i][bottom_mark] == 0) {
				// checking adjacent blocks
				while (gray[i][bottom_mark] == 0) {
					if ((gray[i - 1][bottom_mark] != 0) && (gray[i - 1][bottom_mark - 1] != 0) &&
						(gray[i + 1][bottom_mark] != 0) && (gray[i + 1][bottom_mark - 1] != 0))
						// clear the pixel
						gray[i][bottom_mark] = 255;
					bottom_mark--;
				}
			}
			if (gray[i][top_mark] == 0) {
				while (gray[i][top_mark] == 0) {
					if ((gray[i - 1][top_mark] != 0) && (gray[i - 1][top_mark + 1] != 0) &&
						(gray[i + 1][top_mark] != 0) && (gray[i + 1][top_mark + 1] != 0))
						// clear the pixel
						gray[i][top_mark] = 255;
					top_mark++;
				}				
			}
		}
		
		for (int j = 1; j < height - 1; j++) {
			int left_mark = 1;
			int right_mark = width - 2;
			if (gray[left_mark][j] == 0) {
				while (gray[left_mark][j] == 0) {
					if ((gray[left_mark][j - 1] != 0) && (gray[left_mark + 1][j - 1] != 0) &&
						(gray[left_mark][j + 1] != 0) && (gray[left_mark + 1][j + 1] != 0))
						// clear the pixel
						gray[left_mark][j] = 255;
					left_mark++;
				}
			}
			if (gray[right_mark][j] == 0) {
				while (gray[right_mark][j] == 0) {
					if ((gray[right_mark][j - 1] != 0) && (gray[right_mark - 1][j - 1] != 0) &&
						(gray[right_mark][j + 1] != 0) && (gray[right_mark - 1][j + 1] != 0))
						// clear the pixel
						gray[right_mark][j] = 255;
					right_mark--;
				}
			}
		}
		
		// make it a binary picture.
		BufferedImage result = new BufferedImage(width, height, 12);
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				result.setRGB(i, j, gray[i][j] * 256 + 255);
		return result;
	}
	
	public static BufferedImage hilditch(int[][] gray, int width, int height) {	
		int[][] black = new int[width][height];
		boolean[][] tag = new boolean[width][height];
		int addTagNum = 1;
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) 
				black[i][j] = 1 - gray[i][j] / 255; // mapping (255, 0) to (0, 1)
		// Hilditch algorithm
		while (addTagNum > 0) {
			addTagNum = 0;
			// delete front tagged points
			for (int i = 0; i < width; i++)
				for (int j = 0; j < height; j++) 
					if (tag[i][j])
						black[i][j] = 0;
			
			for (int i = 1; i < width - 1; i++)
				for (int j = 1; j < height - 1; j++) {
					// get adjacent array.
					int[] adjacent = {black[i + 1][j], black[i + 1][j - 1], black[i][j - 1],
									  black[i - 1][j - 1], black[i - 1][j], black[i - 1][j + 1],
									  black[i][j + 1], black[i + 1][j + 1]};
					// Condition 1
					if (black[i][j] == 0) {
						tag[i][j] = false; 
						continue;
					}
					// Condition 2
					if (adjacent[0] * adjacent[2] * adjacent[4] * adjacent[6] == 1) {
						tag[i][j] = false;
						continue;
					}
					// Condition 3
					int adj_size = 0;
					for (int t = 0; t < 8; t++)
						adj_size += adjacent[t];
					if (adj_size < 2) {
						tag[i][j] = false;
						continue;
					}
					// Condition 4
					if (detectConnectivity(adjacent) != 1) {
						tag[i][j] = false;
						continue;
					}
					// Condition 5
					if (tag[i][j - 1]) {
						adjacent[2] = 0;
						if (detectConnectivity(adjacent) != 1) {
							tag[i][j] = false;
							adjacent[2] = 1;
							continue;
						} else
							adjacent[2] = 1;
					}
					// Condition 6
					if (tag[i - 1][j]) {
						adjacent[4] = 0;
						if (detectConnectivity(adjacent) != 1) {
							tag[i][j] = false;
							adjacent[4] = 1;
							continue;
						} else
							adjacent[4] = 1;
					}
					// the point should be tagged here.
					tag[i][j] = true;
					addTagNum++;
				}
		}
		// make it a binary picture.
		BufferedImage result = new BufferedImage(width, height, 12);
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				result.setRGB(i, j, (1 - black[i][j]) * 65535);
		return result;
	}
	
	public static int detectConnectivity(int[] a) {
		int a0 = 1 - a[0];
		int a1 = 1 - a[1];
		int a2 = 1 - a[2];
		int a3 = 1 - a[3];
		int a4 = 1 - a[4];
		int a5 = 1 - a[5];
		int a6 = 1 - a[6];
		int a7 = 1 - a[7];
		int size = a0 - a0 * a1 * a2 + a2 - a2 * a3 * a4 + 
				   a4 - a4 * a5 * a6 + a6 - a6 * a7 * a0;
		return size;
	}

	public static BufferedImage cropImage(BufferedImage bufferedImage, int startX, int endX, int startY, int endY) {
		int height = bufferedImage.getHeight();
		int width = bufferedImage.getWidth();
		if (startX == -1) {
			startX = 0;
		}
		if (startY == -1) {
			startY = 0;
		}
		if (endX == -1) {
			endX = width - 1;
		}
		if (endY == -1) {
			endY = height - 1;
		}
		// Discard alpha mask when generating a new cropped image.
		BufferedImage result = new BufferedImage(endX - startX, endY - startY, 4);
		for (int i = startX; i < endX; i++)
			for (int j = startY; j < endY; j++)
				result.setRGB(i - startX, j - startY, bufferedImage.getRGB(i, j));
		return result;
	}
}