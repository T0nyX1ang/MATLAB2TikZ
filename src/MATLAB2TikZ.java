import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class MATLAB2TikZ extends JFrame implements ActionListener{
	// This is the main class of the project.

	private class WindowCloser extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			int confirm = JOptionPane.showConfirmDialog(MATLAB2TikZ.this, "Do you really want to exit?", 
														"Confirm exit", JOptionPane.YES_NO_OPTION);
			if (confirm == 0)
				System.exit(0);
		}
	}
	
	private final String VERSION_NUMBER = "0.1.4";
	private JLabel guideline = new JLabel("Guidelines: " +
										  "(1) Import the picture. " +  
										  "(2) Setting the data. " +
										  "(3) Generate TikZ. " + 
										  "(4) Preview TikZ. ");
	private JButton importButton = new JButton("Import");
	private JButton dataButton = new JButton("Data");
	private JButton generateButton = new JButton("Generate");
	private JButton previewButton = new JButton("Preview");
	private JButton exitButton = new JButton("Exit");
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem fileImportPic = new JMenuItem("Import");
	private JMenuItem fileGenTikZ = new JMenuItem("Generate TikZ");
	private JMenuItem fileTikZPreview = new JMenuItem("Preview TikZ");
	private JMenuItem fileExit = new JMenuItem("Exit");
	private JMenu configMenu = new JMenu("Settings");
	private JMenuItem configGeneral = new JMenuItem("General Settings");
	private JMenuItem configData = new JMenuItem("Data Settings");
	private JMenu helpMenu = new JMenu("Help");
	private JMenuItem helpAbout = new JMenuItem("About MatLab2TikZ");
	private JMenuItem helpProgram = new JMenuItem("Help");
	private BufferedImage import_image = null;
	private BufferedImage output_image = null;
	private DrawImageWithCanvas diwc = new DrawImageWithCanvas();
	
	// data settings
	private boolean isXGrid = false;
	private boolean isYGrid = false;
	private double xStart = 0;
	private double xStop = 0;
	private double xStep = 0;
	private double yStart = 0;
	private double yStop = 0;
	private double yStep = 0;
	private String xLabel = null;
	private String yLabel = null;
	
	// general settings
	private int dpiValue = 240;
	private String LaTeXCompiler = "xelatex";
	private boolean autoMode = false;
	private File autoModeFile = null;
	
	public MATLAB2TikZ(){
		super("MatLab to TikZ");
		init();
		setSize(1080, 720);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	
	private void init() {
		JPanel buttons = new JPanel(new FlowLayout());
		buttons.add(importButton);
		importButton.addActionListener(this);
		buttons.add(dataButton);
		dataButton.addActionListener(this);
		buttons.add(generateButton);
		generateButton.addActionListener(this);
		buttons.add(previewButton);
		previewButton.addActionListener(this);
		buttons.add(exitButton);
		exitButton.addActionListener(this);
		fileMenu.add(fileImportPic);
		fileImportPic.addActionListener(this);
		fileMenu.add(fileGenTikZ);
		fileGenTikZ.addActionListener(this);
		fileMenu.add(fileTikZPreview);
		fileTikZPreview.addActionListener(this);
		fileMenu.addSeparator();
		fileMenu.add(fileExit);
		fileExit.addActionListener(this);
		configMenu.add(configGeneral);
		configGeneral.addActionListener(this);
		configMenu.add(configData);
		configData.addActionListener(this);
		helpMenu.add(helpAbout);
		helpAbout.addActionListener(this);
		helpMenu.add(helpProgram);
		helpProgram.addActionListener(this);
		JMenuBar menu = new JMenuBar();
		menu.add(fileMenu);
		menu.add(configMenu);
		menu.add(helpMenu);
		setJMenuBar(menu);
		setLayout(new BorderLayout());
		addWindowListener(new WindowCloser());
		add("North", guideline);
		add("South", buttons);
		add("Center", diwc);		
	}
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == fileExit || ae.getSource() == exitButton) {
			int confirm = JOptionPane.showConfirmDialog(this, "Do you really want to exit?", 
														"Confirm exit", JOptionPane.YES_NO_OPTION);
			if (confirm == 0)
				System.exit(0);
		}
		
		else if (ae.getSource() == fileImportPic || ae.getSource() == importButton)
			importPicture();
		
		else if (ae.getSource() == fileTikZPreview || ae.getSource() == previewButton)
			previewTikZ();
		
		else if (ae.getSource() == fileGenTikZ || ae.getSource() == generateButton)
			generateTikZ();
		
		else if (ae.getSource() == configData || ae.getSource() == dataButton)
			dataConfig();
		
		else if (ae.getSource() == configGeneral)
			generalConfig();
		
		else if (ae.getSource() == helpAbout) {
			String sep = System.lineSeparator();
			String aboutContext = "MatLab2TikZ:" + sep +
								  "A program to convert MatLab(R) plots to TikZ codes." + sep +
								  "Version:" + VERSION_NUMBER + sep +
								  "Author: Tony Xiang(tonyxfy@gmail.com)" + sep;
			JOptionPane.showMessageDialog(this, aboutContext, "About this program", JOptionPane.PLAIN_MESSAGE); 
		}
		
		else if (ae.getSource() == helpProgram) {
			String sep = System.lineSeparator();
			String helpContext = "MatLab2TikZ Help:" + sep + sep +
								 "File Menu:" + sep +
								 "Import: Import a picture." + sep +
								 "Generate TikZ: Generate TikZ codes with the picture imported." + sep +
								 "Preview TikZ: Preview the TikZ codes using LaTeX compiler." + sep + sep +
								 "Settings Menu:" + sep + 
								 "General Settings: Setting general things." + sep +
								 "Data Settings: Setting data passed to the procedure manually." + sep + sep +
								 "Help Menu:" + sep +
								 "About: About the program" + sep +
								 "Help: Show program help" + sep;
			JOptionPane.showMessageDialog(this, helpContext, "Help", JOptionPane.PLAIN_MESSAGE);
		}
	}
	
	public void importPicture() {
		try {
			JFileChooser jfc = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Images(*.jpg, *.gif, *.bmp, *.png)",
																		 "jpg", "gif", "bmp", "png");
			jfc.setVisible(true);
			jfc.setFileFilter(filter);
            jfc.setSelectedFile(new File(System.getProperty("user.dir")));
            jfc.setMultiSelectionEnabled(false);
			int confirm = jfc.showOpenDialog(this);
			File fileLoad = null;
			if (confirm == JOptionPane.OK_OPTION)
				fileLoad = jfc.getSelectedFile();
			else {
				System.out.println("Import cancelled by user.");
				return;
			}
			if (fileLoad != null) {
				import_image = ImageIO.read(new FileInputStream(fileLoad));
				diwc.getImage(import_image);
				diwc.repaint();
				autoModeFile = new File(fileLoad.getPath() + ".output.tikz");
				if (autoMode) {
					// automatic mode
					generateTikZ();
					previewTikZ();
				}
			}
		} catch (FileNotFoundException fnfe) {
			System.err.println("File not found error." + fnfe);
		} catch (Exception e) {
			System.err.println("Unknown error. ");
			e.printStackTrace();
		}		
	}
	
	public void generateTikZ() {
		String sep = System.lineSeparator();
		String sorryMessage = "We are sorry to tell you that" + sep +
							  "the feature hasn't been developed yet." + sep +
							  "Please set the data manually." + sep;
		File fileSave = null;
		try {
			if (autoMode)
				fileSave = autoModeFile;
			else {
				fileSave = null;
				JFileChooser jfc = new JFileChooser("Save File");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("TikZ Documents(*.tikz)",
						 "tikz");
				jfc.setVisible(true);
				jfc.setFileFilter(filter);
	            jfc.setMultiSelectionEnabled(false);
	            jfc.setSelectedFile(autoModeFile);
				int isSelected = jfc.showSaveDialog(this);
				if (isSelected == JFileChooser.APPROVE_OPTION) {
					fileSave = jfc.getSelectedFile();
					while (fileSave.exists() && isSelected == JFileChooser.APPROVE_OPTION) {
						int confirm = JOptionPane.showConfirmDialog(this, "This file already exists" + sep +
																	  "Do you still want to save it here?", 
																	  "Confirm Save", JOptionPane.YES_NO_OPTION);
						if (confirm == 1) {
							isSelected = jfc.showSaveDialog(this);
							fileSave = jfc.getSelectedFile();
						} else
							break;
					}
				}
				// early escape the process when canceling the selection procedure.
				if (isSelected != JFileChooser.APPROVE_OPTION) {
					System.out.println("Generation has been terminated.");
					return;
				}
			}
			
			PictureUtils pu = new PictureUtils();
			WordRecognition wr = new WordRecognition();
			pu.setBasics(import_image);
			// word recognition is PENDING now.
			wr.getXDetail(pu.xStepOrig);
			wr.getYDetail(pu.yStepOrig);
			wr.getXLabelString(pu.xLabel);
			wr.getYLabelString(pu.yLabel);
			isXGrid = pu.isXGrid;
			isYGrid = pu.isYGrid;
			pu.getLegend(pu.MainDataOrig);
			pu.getLegendData(pu.legendDataOrig);

			try {
				if (pu.legendTitleOrig != null)
					ImageIO.write(pu.legendTitleOrig, "TIFF", new File("." + File.separator + "test" + File.separator + "LegendTitle.tiff"));
				else {
					System.out.println("Legend title doesn't exist.");
				}
				for (int i = 0; i < pu.legendCount; i++) {
					System.out.print(pu.legendColor[i] + " ");
					ImageIO.write(pu.legendNameOrig[i], "TIFF", new File("." + File.separator + "test" + File.separator + "LegendName-" + i + ".tiff"));
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
			// Using automatically generated data (PENDING)
			if ((xStop == xStart) || (xStep == 0) || 
				(yStop == yStart) || (yStep == 0) || 
				(xLabel == null) || (yLabel == null)) {
				xStart = wr.xStart;
				xStop = wr.xStop;
				xStep = wr.xStep;
				yStart = wr.yStart;
				yStop = wr.yStop;
				yStep = wr.yStep;
			}
			
			// Using user-input data (Forcibly override automatically generated data)
			if ((xStop == xStart) || (xStep == 0) || 
				(yStop == yStart) || (yStep == 0) || 
				(xLabel == null) || (yLabel == null)) {
				JOptionPane.showMessageDialog(this, sorryMessage, "Sorry", 
											  JOptionPane.ERROR_MESSAGE);
				dataConfig();
			}
			pu.generateTikZ(xStart, xStop, 
							yStart, yStop, 
							xStep, yStep, 
							xLabel, yLabel, 
							isXGrid, isYGrid,
							fileSave);
		} catch (SecurityException se) {
			System.err.println("Permission denied. " + se);
		} catch (NullPointerException npe) {
			System.err.println("File creation error. Make sure you have imported the picture before. " + npe);
		} catch (IndexOutOfBoundsException iobe) {
			System.err.println("Legend counts greater than limit: 10. " + iobe);
		}
		catch (Exception e) {
			System.err.println("Unknown error. ");
			e.printStackTrace();
		}
	}
	
	public void previewTikZ() {
		try {
			File fileLoad = null;
			if (autoMode) 
				fileLoad = autoModeFile;
			else {
				JFileChooser jfc = new JFileChooser("Open File");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("TikZ Documents(*.tikz)",
						 													 "tikz");
				jfc.setVisible(true);
				jfc.setFileFilter(filter);
				jfc.setSelectedFile(autoModeFile);
	            jfc.setMultiSelectionEnabled(false);
				int confirm = jfc.showOpenDialog(this);
				if (confirm == 1) {
					fileLoad = null;
					System.out.println("Preview cancelled by user.");
					return;
				} else
					fileLoad = jfc.getSelectedFile();
			} 
			if (fileLoad != null) {
				if (fileLoad.getName().endsWith(".tikz")) {
					// Create file handler.
					// Instantiate objects.
					TikZConvert tcvt = new TikZConvert();
					String filehash = tcvt.convert(fileLoad);
					TeXCompiler tc = new TeXCompiler();
					tc.compileTeX(LaTeXCompiler, filehash);
					PDF2PNG convert = new PDF2PNG();
					convert.pdf2png(new File("." + File.separator + "temp" + File.separator + filehash 
												 + File.separator + "test-figure0.pdf"), dpiValue);
					output_image = convert.image;
					diwc.getImage(output_image);
					diwc.repaint();
				}
				else
					System.err.println("File extension error.");
			}
		} catch (NullPointerException npe) {
			System.err.println("File creation error. " + npe);
		} catch (Exception e) {
			System.err.println("Unknown error. ");
			e.printStackTrace();
		}
	}

	public void dataConfig() {
		DataConfig dc = new DataConfig(this, "Data Settings");
		// early escape the process when canceling the setting procedure.
		if (!dc.dataValidate)
			return;
		else {
			while (!dc.validateData()) {
				System.err.println("Failed.");
				JOptionPane.showMessageDialog(this, "Your settings are invalid.", "Data Error", JOptionPane.ERROR_MESSAGE);
				dc.setVisible(true);
			}
			System.out.println("Success.");
			xStart = dc.xStart;
			xStop = dc.xStop;
			xStep = dc.xStep;
			xLabel = dc.xLabel;
			yStart = dc.yStart;
			yStop = dc.yStop;
			yStep = dc.yStep;
			yLabel = dc.yLabel;
		}
	}
	
	public void generalConfig() {
		GeneralConfig gc = new GeneralConfig(this, "General Config");
		if (!gc.dataValidate) {
			System.out.println("General config cancelled.");
			return;
		} else {
			autoMode = gc.autoMode;	
			while (autoMode) {
				String sep = System.lineSeparator();
				int confirm = JOptionPane.showConfirmDialog(this, "Entering automatic mode will only require you to import the image." + sep +
						"Your tikz file will be put in the SAME directory where your image is." + sep +
						"So you should confirm that there's no file with the same name in your directory." + sep +
						"You can change the settings in 'General Settings' in the 'Settings' menu." + sep + 
						"Proceed now?" + sep, 
						"Automatic Mode Confirm", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					gc.setVisible(false);
					break;
				} else {
					autoMode = false;
					gc.autoMode = false;
					gc.autoList.setSelectedIndex(1);
					gc.setVisible(true);
					if (!gc.dataValidate) {
						System.out.println("General config cancelled.");
						return;
					} else 
						autoMode = gc.autoMode;
				}
			}
			dpiValue = gc.dpiValue;
			LaTeXCompiler = gc.LaTeXCompiler;
			System.out.println("General config success.");
		}
	}
	
	public static void main(String[] args) {
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
		MATLAB2TikZ mainwindow = new MATLAB2TikZ();
	}
	
}
