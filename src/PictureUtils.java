import java.awt.Color;
import java.awt.image.*;
import java.io.*;

public class PictureUtils extends PictureAlgorithms{
	// Control constants
	private final double CTRL_ACPT = 0.7;
	private final int WHITE_RGB = Color.WHITE.getRGB();
	private final int LEGEND_ELEM_LIMIT = 10;
	// Picture physical characteristics
	private int height = 0;
	private int width = 0;
	private int[][] gray;
	private boolean[] whiteX;
	private boolean[] whiteY;
	private int[] consecX;
	private int[] consecY;
	// Picture detailed characteristics
	BufferedImage xLabel = null;
	BufferedImage yLabel = null;
	BufferedImage xStepOrig = null;
	BufferedImage yStepOrig = null;
	BufferedImage MainDataOrig = null;
	BufferedImage legendDataOrig = null;
	BufferedImage MainData = null;
	// Data detailed characteristics
	private int originX = 0;
	private int originY = 0;
	// default parameters
	boolean isXGrid = false;
	boolean isYGrid = false;
	String dataString = "";
	// legend parameters
	int legendCount = 0;
	int[] legendColor = new int[LEGEND_ELEM_LIMIT];
	BufferedImage[] legendNameOrig = new BufferedImage[LEGEND_ELEM_LIMIT];
	BufferedImage legendTitleOrig = null;
	
	void setBasics(BufferedImage image) {
		height = image.getHeight();
		width = image.getWidth();
		whiteY = new boolean[width];
		whiteX = new boolean[height];
		consecY = new int[width];
		consecX = new int[height];
		gray = getGray(image);
		
		// get X, Y Pixel
		getXPixel();
		getYPixel();
		
		// get X, Y label
		int nextY = getXLabel(image);
		int nextX = getYLabel(image);
		
		// get the origin
		getOrigin(nextX, nextY);
		
		// get X, Y step info
		int dataY = getXStepOriginal(image, originY, nextY);
		int dataX = getYStepOriginal(image, originX, nextX);
		
		// crop main data
		MainDataOrig = cropImage(image, originX, dataX + 1, dataY, originY + 1);
		
		// Detect X, Y grid
		int vert_lines = detectXGrid();
		int horz_lines = detectYGrid();
		isXGrid = (vert_lines > 2);
		isYGrid = (horz_lines > 2);
		
	}
	
	private void getXPixel() {
		// calculate column white pixels
		for (int i = 0; i < width; i++) {
			int col_counter = 0;
			int consec_col_counter = 0;
			int max_col_consec_counter = 0;
			for (int j = 0; j < height; j++) {
				if (gray[i][j] == 255) {
					col_counter++;
					if (consec_col_counter > max_col_consec_counter)
						max_col_consec_counter = consec_col_counter;
					consec_col_counter = 0;
				}
				else
					// get maximum consecutive non-white pixels.
					consec_col_counter++;
			}
			whiteY[i] = (col_counter >= (int) height);
			consecY[i] = max_col_consec_counter;
		}		
	}
	
	private void getYPixel() {
		// calculate row white pixels
		for (int i = 0; i < height; i++) {
			int row_counter = 0;
			int consec_row_counter = 0;
			int max_row_consec_counter = 0;
			for (int j = 0; j < width; j++) {
				if (gray[j][i] == 255) {
					row_counter++;
					if (consec_row_counter > max_row_consec_counter)
						max_row_consec_counter = consec_row_counter;
					consec_row_counter = 0;
				} else
					// get maximum consecutive non-white colored pixels.				
					consec_row_counter++;					
			}
			whiteX[i] = (row_counter == (int) width);
			consecX[i] = max_row_consec_counter;
		}
	}
	
	private int getXLabel(BufferedImage image) {
		// using whiteX to get axis X's label. Search from bottom.
		int dirY = height - 1;
		// prepare for more sophisticated cropping here.
		int dirXLeft = 0;
		int dirXRight = width - 1;
		while (whiteX[dirY])
			dirY--; // get start point.
		int endY = dirY + 1;
		while (!whiteX[dirY] && dirY > 0)
			dirY--;
		int startY = dirY;
		if (endY - startY > (int) (CTRL_ACPT * height)) {
			xLabel = null; // condition for non-exist X Label
			return endY;
		}
		else
			xLabel = cropImage(image, dirXLeft, dirXRight, startY, endY);
		return dirY;
	}
	
	private int getYLabel(BufferedImage image) {
		// using whiteY to get axis Y's label. Search from left.
		int dirX = 0;
		// prepare for more sophisticated cropping here.
		int dirYTop = 0;
		int dirYBottom = height - 1;
		while (whiteY[dirX])
			dirX++; // get start point.
		int startX = dirX - 1;
		while (!whiteY[dirX] && dirX < width - 1)
			dirX++;
		int endX = dirX;
		if (endX - startX > (int) (CTRL_ACPT * width)) {
			yLabel = null;
			return startX;
		}
		yLabel = cropImage(image, startX, endX, dirYTop, dirYBottom);
		return dirX;
	}
	
	private void getOrigin(int searchFromX, int searchFromY) {
		while ((consecY[searchFromX] < (int) height * CTRL_ACPT) && (searchFromX < width))
			searchFromX++;
		while ((consecX[searchFromY] < (int) width * CTRL_ACPT) && (searchFromY >= 0))
			searchFromY--;
		originX = searchFromX;
		originY = searchFromY;
	}
	
	private int getXStepOriginal(BufferedImage image, int searchFromY, int searchToY) {
		boolean canMove = true;
		int startX = originX;
		int endX = width - 1;
		while ((canMove) && startX > 0) {
			for (int j = searchFromY; j < searchToY; j++)
				if (gray[startX][j] != 255) {
					canMove = true;
					startX--;
					break;
				} else 
					canMove = false;
		}
		startX--; // make larger room
		canMove = true;
		while ((canMove) && (searchFromY < searchToY)) {
			for (int i = startX; i <= endX; i++)
				if (gray[i][searchFromY] != 255) {
					canMove = true;
					searchFromY++;
					break;
				} else 
					canMove = false;
		}
		xStepOrig = cropImage(image, startX, endX, searchFromY, searchToY);
		// Cropping just the center area.
		searchFromY = 0;
		while ((consecX[searchFromY] < (int) width * CTRL_ACPT) && (searchFromY < height))
			searchFromY++;
		return searchFromY;
	}
	
	private int getYStepOriginal(BufferedImage image, int searchFromX, int searchToX) {
		boolean canMove = true;
		int endY = originY;
		int startY = 0;
		while ((canMove) && startY <= endY) {
			for (int i = searchFromX; i > searchToX; i--)
				if (gray[i][endY] != 255) {
					canMove = true;
					endY++;
					break;
				} else 
					canMove = false;
		}
		
		endY++; // make larger room
		canMove = true;
		while ((canMove) && (searchFromX < searchToX)) {
			for (int j = startY; j <= endY; j++)
				if (gray[searchFromX][j] != 255) {
					canMove = true;
					searchFromX--;
					break;
				} else 
					canMove = false;
		}
		yStepOrig = cropImage(image, searchToX, searchFromX, startY, endY);
		// Cropping just the center area.
		searchFromX = width - 1;
		while ((consecY[searchFromX] < (int) height * CTRL_ACPT) && (searchFromX >= 0))
			searchFromX--;
		return searchFromX;
	}
	
	private int detectXGrid() {
		int dirX = originX;
		int col_counter = 0;
		while (dirX < width) {
			// make the condition a bit looser.
			if (consecY[dirX] >= consecY[originX] - 5) {
				col_counter++;
				while (consecY[dirX] >= consecY[originX] - 5)
					dirX++;
			}
			dirX++;
		}
		return col_counter; // only works as a checking tool.
	}
	
	private int detectYGrid() {
		int dirY = originY;
		int row_counter = 0;
		while (dirY >= 0) {
			// make the condition a bit looser.
			if (consecX[dirY] >= consecX[originY] - 5) {
				row_counter++;
				while (consecX[dirY] >= consecX[originY] - 5)
					dirY--;
			}
			dirY--;
		}
		return row_counter; // only works as a checking tool.
	}
	
	void getLegend(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();
		int nowRed = (int) (image.getRGB(0, 1) >> 16) & 0xFF;
		int nowGreen = (int) (image.getRGB(0, 1) >> 8) & 0xFF;
		int nowBlue = (int) (image.getRGB(0, 1) >> 0) & 0xFF;
		int colorgray = getGray(nowRed, nowGreen, nowBlue);
		BufferedImage otsuResult = otsu(getGray(image, colorgray), width, height);
		BufferedImage clearResult = clearAxisTicks(getGray(otsuResult), width, height);
		int[][] imagegray = getGray(clearResult);
		for (int i = 1; i < width - 1; i++)
			for (int j = 1; j < height - 1; j++) {
				int startX = i - 1;
				int startY = j;
				int endX = 0;
				int endY = 0;
				if (imagegray[i][j] == 0) {
					if (imagegray[i + 1][j] != 0)
						continue;
					// Move direction: right
					int nowX = i;
					int nowY = j;
					while (nowX < width - 1 && imagegray[nowX][nowY] == 0)
						nowX++;
					
					if (imagegray[nowX][nowY + 1] != 0)
						continue;
					// Move direction: down
					endX = nowX;
					nowY++;
					while (nowY < height - 1 && imagegray[nowX][nowY] == 0)
						nowY++;
					
					if (imagegray[nowX - 1][nowY] != 0)
						continue;
					// Move direction: left
					nowX--;
					endY = nowY;
					while (nowX > 0 && imagegray[nowX][nowY] == 0)
						nowX--;
					
					if (imagegray[nowX][nowY - 1] != 0)
						continue;
					// Move direction: up
					nowY--;
					while (nowY > 0 && imagegray[nowX][nowY] == 0)
						nowY--;
					
					if (imagegray[nowX + 1][nowY] == 0 && nowX + 1 == i && nowY == j) {
						legendDataOrig = cropImage(image, startX + 1, endX, startY + 1 ,endY);	
						for (int i1 = startX; i1 <= endX; i1++)
							for (int j1 = startY; j1 <= endY; j1++)
								image.setRGB(i1, j1, WHITE_RGB);
						return;
					}
					else
						continue;
				} else
					continue;
			}
	}
	
	void getLegendData(BufferedImage image) {
		int legendHeight = image.getHeight();
		int legendWidth = image.getWidth();
		int sepLine = 0;
		int[][] gray = getGray(image);
		
		for (int j = 0; j < legendHeight; j++)
			if (gray[0][j] != 255) {
				sepLine = j;
				for (int i = 0; i < legendWidth; i++)
					image.setRGB(i, j, WHITE_RGB);
			}
			
		// deal with legend title
		if (sepLine != 0)
			legendTitleOrig = cropImage(image, 0, legendWidth, 0, sepLine);
		
		// deal with legend details
		boolean[] legendWhiteX = new boolean[legendWidth];
		boolean[] legendWhiteY = new boolean[legendHeight];
		for (int i = 0; i < legendWidth; i++) {
			legendWhiteX[i] = true;
			for (int j = sepLine + 1; j < legendHeight; j++) {
				if (gray[i][j] != 255) {
					legendWhiteX[i] = false;
					break;
				}
			}
		}
		
		for (int j = sepLine + 1; j < legendHeight; j++) {
			legendWhiteY[j] = true;
			for (int i = 0; i < legendWidth; i++) {
				if (gray[i][j] != 255) {
					legendWhiteY[j] = false;
					break;
				}
			}
		}
		
		int dirX = 0;
		while (legendWhiteX[dirX] && dirX < legendWidth - 1)
			dirX++;
		int startX = dirX;
		while (!legendWhiteX[dirX] && dirX < legendWidth - 1)
			dirX++;
		int endX = dirX - 1;
		int dirY = sepLine + 1;
		while (!legendWhiteY[dirY] && dirY < legendHeight - 1)
			dirY++;
		while (dirY < legendHeight - 1) {
			while (legendWhiteY[dirY] && dirY < legendHeight - 1)
				dirY++;
			int startY = dirY - 1;
			while (!legendWhiteY[dirY] && dirY < legendHeight - 1)
				dirY++;
			int endY = dirY;
			if (endY - startY > 1) {
				legendNameOrig[legendCount] = cropImage(image, endX + 1, legendWidth - 1, startY, endY + 1);
				for (int k = startY; k < endY; k++)
					if (gray[(startX + endX) / 2][k] != 255)
						legendColor[legendCount] = gray[(startX + endX) / 2][k];
				legendCount++;
			}
		}
		
	}
	
	String getData(BufferedImage image, double xStart, double xStop, double yStart, double yStop) {
		int dataHeight = image.getHeight();
		int dataWidth = image.getWidth();
		String sep = System.lineSeparator();
		String dataString = "";
		int[][] dataGray = getGray(image);
		BufferedImage otsuResult = otsu(dataGray, dataWidth, dataHeight);
		dataGray = getGray(otsuResult);
		BufferedImage clearResult = clearAxisTicks(dataGray, dataWidth, dataHeight);
		dataGray = getGray(clearResult);
		BufferedImage hilditchResult = hilditch(dataGray, dataWidth, dataHeight);
		MainData = hilditchResult;
		dataGray = getGray(MainData);
		for (int i = 0; i < dataWidth; i++) {
			int dirY = dataHeight - 1;
			while (dataGray[i][dirY] == 255) {
				dirY--;
				if (dirY == -1)
					break;
			}
			int startY = dirY;
			if (startY == -1) 
				continue;
			while (dataGray[i][dirY] == 0) {
				dirY--;
				if (dirY == -1)
					break;
			}
			int endY = dirY + 1;
			double dataX = (double) xStart + (xStop - xStart) / dataWidth * i;
			double dataY = (double) yStart + (yStop - yStart) / dataHeight * (dataHeight - (double) (startY + endY) / 2);
			dataString = dataString + "    " + dataX + ", " + dataY + " " + sep;
		}
		return dataString;
	}
	
	void generateTikZ(double xStart, double xStop, 
					  double yStart, double yStop,
				   	  double xStep, double yStep,
					  String xLabel, String yLabel,
					  boolean isXGrid, boolean isYGrid,
					  File file) {
		dataString = getData(MainDataOrig, xStart, xStop, yStart, yStop);
		String sep = System.lineSeparator();
		// setting grids
		String xGridOpen = "%";
		String yGridOpen = "%";
		if (isXGrid)
			xGridOpen = "";
		if (isYGrid)
			yGridOpen = "";
		// setting TikZ model
		String total = "";
		String startTikZ = "\\begin{tikzpicture}" + sep;
		String endTikZ = "\\end{tikzpicture}" + sep;
		String createData = "\\datavisualization [" + sep;
		String axisConfig = "    scientific axes={" + sep + 
							"        inner ticks," + sep +
							"        width=" + (double) width / 100 + "cm," + sep + 
							"        height=" + (double) height / 100 + "cm," + sep +
							"        standard labels," + sep +
							"    }," + sep;
		String xAxisConfig = "    x axis={" + sep + 
							 "        attribute=x," + sep + 
							 "        min value=" + xStart + "," + sep +
							 "        max value=" + xStop + "," + sep +
							 "        ticks={step=" + xStep + "}," + sep + 
				 xGridOpen + "        grid," + sep +
							 "        label=" + xLabel + ", " + sep +
							 "    }," + sep;
		String yAxisConfig = "    y axis={" + sep + 
							 "        attribute=y," + sep +
				 			 "        min value=" + yStart + "," + sep +
				 			 "        max value=" + yStop + "," + sep +
				 			 "        ticks={step=" + yStep + "}," + sep + 
				 yGridOpen + "        grid," + sep +
				 			 "        label=" + yLabel + ", " + sep +
				 			 "    }," + sep;		
		String visualizer = "    visualize as smooth line," + sep +
							"]" + sep;
		String dataConfig = "data [format=table] {" + sep;
		String data = "    x, y" + sep + 
					  dataString + 
					  "};" + sep;
		total = startTikZ + createData + axisConfig + xAxisConfig + yAxisConfig + visualizer 
						  + dataConfig + data + endTikZ;
		// write
		try {
			FileWriter fw = new FileWriter(file);
			PrintWriter out = new PrintWriter(new BufferedWriter(fw));
			out.println(total);
			out.close();
			if (out.checkError()) {
				System.err.println("Output error.");
				throw new IOException();
			} else {
				System.out.println("Generating TikZ successfully.");
			}
		} catch (SecurityException se) {
			System.err.println("Permission denied. " + se);
		} catch (IOException ioe) {
			System.err.println("Unknown error. " + ioe);
		}	
	}
}
