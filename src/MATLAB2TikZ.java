import java.awt.BorderLayout;
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

	private BufferedImage image;
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem fileImportPic = new JMenuItem("Import");
	private JMenuItem fileGenTikZ = new JMenuItem("Generate TikZ");
	private JMenuItem fileTikZPreview = new JMenuItem("Preview TikZ");
	private JMenuItem fileExit = new JMenuItem("Exit");
	private JMenu configMenu = new JMenu("Settings");
	private JMenu helpMenu = new JMenu("Help");
	private JMenuItem helpAbout = new JMenuItem("About MatLab2TikZ");
	private JMenuItem helpProgram = new JMenuItem("Help");
	public BufferedImage import_image = null;
	public DrawImageWithCanvas diwc = new DrawImageWithCanvas();
	public MATLAB2TikZ(){
		super("MatLab to TikZ");
		fileMenu.add(fileImportPic);
		fileImportPic.addActionListener(this);
		fileMenu.add(fileGenTikZ);
		fileGenTikZ.addActionListener(this);
		fileMenu.add(fileTikZPreview);
		fileTikZPreview.addActionListener(this);
		fileMenu.addSeparator();
		fileMenu.add(fileExit);
		fileExit.addActionListener(this);
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
		add("Center", diwc);
		setSize(1080, 720);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == fileExit) {
			int confirm = JOptionPane.showConfirmDialog(this, "Do you really want to exit?", 
														"Confirm exit", JOptionPane.YES_NO_OPTION);
			if (confirm == 0)
				System.exit(0);
		}
		
		else if (ae.getSource() == fileImportPic)
			importPicture();
		
		else if (ae.getSource() == fileTikZPreview)
			previewTikZ();
		
		else if (ae.getSource() == fileGenTikZ) {
			
		}
		
		else if (ae.getSource() == helpAbout) {
			String sep = System.lineSeparator();
			String aboutContext = "MatLab2TikZ:" + sep +
								  "A program to convert MatLab(R) plots to TikZ codes." + sep +
								  "Version: 0.1.0" + sep +
								  "Author: Tony Xiang(tonyxfy@gmail.com)" + sep;
			JOptionPane.showMessageDialog(this, aboutContext, "About this program", JOptionPane.PLAIN_MESSAGE); 
		}
		
		else if (ae.getSource() == helpProgram) {
			String sep = System.lineSeparator();
			String helpContext = "MatLab2TikZ Help:" + sep +
								 "File Menu:" + sep +
								 "Import: Import a picture." + sep +
								 "Generate TikZ: Generate TikZ codes with the picture imported." + sep +
								 "Preview TikZ: Preview the TikZ codes using LaTeX compiler." + sep + 
								 "Settings Menu:" + sep + 
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
			jfc.showOpenDialog(this);
			File fileLoad = jfc.getSelectedFile();
			if (fileLoad != null) {
				import_image = ImageIO.read(new FileInputStream(fileLoad));
				diwc.getImage(import_image);
				diwc.repaint();
			}
		} catch (Exception e) {
			System.out.println("Unknown error. " + e);
		}		
	}
	
	public void previewTikZ() {
		try {
			JFileChooser jfc = new JFileChooser("Open File");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("TikZ Documents(*.tikz)",
					 													 "tikz");
			jfc.setVisible(true);
			jfc.setFileFilter(filter);
			jfc.showOpenDialog(this);
			File fileLoad = jfc.getSelectedFile();
			if (fileLoad != null) {
				if (fileLoad.getName().endsWith(".tikz")) {
					// Create file handler.
					// Instantiate objects.
					TikZConvert tcvt = new TikZConvert();
					String filehash = tcvt.convert(fileLoad);
					TeXCompiler tc = new TeXCompiler();
					tc.compileTeX("xelatex", filehash);
					PDF2PNG convert = new PDF2PNG();
					convert.pdf2png(new File("." + File.separator + "temp" + File.separator + filehash 
												 + File.separator + "test-figure0.pdf"));
					image = convert.image;
					diwc.getImage(image);
					diwc.repaint();
				}
				else
					System.err.println("File extension error.");
			}
		} catch (NullPointerException npe) {
			System.err.println("File creation error. " + npe);
		} catch (Exception e) {
			System.err.println("Unknown error. " + e);
		}		
	}
	
	public static void main(String[] args) {
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
		MATLAB2TikZ mainwindow = new MATLAB2TikZ();
	}
	
}
