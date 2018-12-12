import java.awt.image.*;
import java.io.*;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class PictureUtils extends PictureAlgorithms{
	// Control constants
	private static final double CTRL_ACPT = 0.7;
	// Picture physical characteristics
	private static int height = 0;
	private static int width = 0;
	private static int[][] gray;
	private static boolean[] whiteX;
	private static boolean[] whiteY;
	private static int[] consecX;
	private static int[] consecY;
	// Picture detailed characteristics
	private static BufferedImage xLabel = null;
	private static BufferedImage yLabel = null;
	private static BufferedImage xStepOrig = null;
	private static BufferedImage yStepOrig = null;
	private static BufferedImage MainDataOrig = null;
	private static BufferedImage MainData = null;
	// Data detailed characteristics
	private static int originX = 0;
	private static int originY = 0;
	private static boolean isXGrid = false;
	private static boolean isYGrid = false;
	private static String dataString = "";
	
	public static void setBasics(BufferedImage image) {
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
		
		try {
			ImageIO.write(xLabel, "png", new File("." + File.separator + "test" + File.separator + "pic" 
													  + File.separator + "xLabel.png"));
			ImageIO.write(yLabel, "png", new File("." + File.separator + "test" + File.separator + "pic" 
					  								  + File.separator + "yLabel.png"));
			ImageIO.write(xStepOrig, "png", new File("." + File.separator + "test" + File.separator + "pic" 
					  									 + File.separator + "xStepOrig.png"));
			ImageIO.write(yStepOrig, "png", new File("." + File.separator + "test" + File.separator + "pic" 
					  									 + File.separator + "yStepOrig.png"));
			ImageIO.write(MainDataOrig, "png", new File("." + File.separator + "test" + File.separator + "pic" 
					  										+ File.separator + "MainDataOrig.png"));
			System.out.println("Setting basics successfully.");
		} catch (IOException ioe) {
			System.err.println("Unknown error." + ioe);
		}
	}
	
	public static void getXPixel() {
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
	
	public static void getYPixel() {
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
	
	public static int getXLabel(BufferedImage image) {
		// using whiteX to get axis X's label. Search from bottom.
		int dirY = height - 1;
		// prepare for more sophisticated cropping here.
		int dirXLeft = 0;
		int dirXRight = width - 1;
		while (whiteX[dirY])
			dirY--; // get start point.
		int endY = dirY + 1;
		while (!whiteX[dirY])
			dirY--;
		int startY = dirY;
		xLabel = cropImage(image, dirXLeft, dirXRight, startY, endY);
		return dirY;
	}
	
	public static int getYLabel(BufferedImage image) {
		// using whiteY to get axis Y's label. Search from left.
		int dirX = 0;
		// prepare for more sophisticated cropping here.
		int dirYTop = 0;
		int dirYBottom = height - 1;
		while (whiteY[dirX])
			dirX++; // get start point.
		int startX = dirX - 1;
		while (!whiteY[dirX])
			dirX++;
		int endX = dirX;
		yLabel = cropImage(image, startX, endX, dirYTop, dirYBottom);
		return dirX;
	}
	
	public static void getOrigin(int searchFromX, int searchFromY) {
		while ((consecY[searchFromX] < (int) height * CTRL_ACPT) && (searchFromX < width))
			searchFromX++;
		while ((consecX[searchFromY] < (int) width * CTRL_ACPT) && (searchFromY >= 0))
			searchFromY--;
		originX = searchFromX;
		originY = searchFromY;
	}
	
	public static int getXStepOriginal(BufferedImage image, int searchFromY, int searchToY) {
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
	
	public static int getYStepOriginal(BufferedImage image, int searchFromX, int searchToX) {
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
	
	public static int detectXGrid() {
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
	
	public static int detectYGrid() {
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
	
	public static String getData(BufferedImage image, double xStart, double xStop, double yStart, double yStop) {
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
		System.out.println("Getting data pixels successfully.");
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
	
	public static void generateTikZ(String dataString, 
									double xStart, double xStop, 
									double yStart, double yStop,
									int xStep, int yStep,
									String xLabel, String yLabel,
									boolean isXGrid, boolean isYGrid,
									String filepath, String filename) {
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
		FileWriter fw;
		try {
			fw = new FileWriter(filepath + File.separator + filename + ".tikz");
			PrintWriter out = new PrintWriter(new BufferedWriter(fw));
			out.println(total);
			out.close();
			if (out.checkError()) {
				System.err.println("Output error.");
				throw new IOException();
			} else {
				System.out.println("Generating TikZ successfully.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static void main(String [] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter file path: ");
		String path = sc.nextLine();
		System.out.println("Enter file name: ");
		String name = sc.nextLine();
		File file = new File(path + File.separator + name);
		BufferedImage image = null;
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		setBasics(image);
		dataString = getData(MainDataOrig, 0, 7, -6, 2);
		System.out.println("Gathering data successfully.");
		generateTikZ(dataString,
					0, 7, -6, 2,
					1, 1,
					"Grid X", "Grid Y",
					isXGrid, isYGrid,
					path, name);
		sc.close();
	}
}
