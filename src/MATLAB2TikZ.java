import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

@SuppressWarnings("serial")
public class MATLAB2TikZ extends Frame implements ActionListener{
	// This is the main class of the project.
	
	private class WindowCloser extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}
	private BufferedImage image;
	private Menu fileMenu = new Menu("File");
	private MenuItem fileImportPic = new MenuItem("Import");
	private MenuItem fileGenTikZ = new MenuItem("Generate TikZ");
	private MenuItem fileTikZPreview = new MenuItem("Preview TikZ");
	private MenuItem fileExit = new MenuItem("Exit");
	private Menu configMenu = new Menu("Settings");
	private Menu helpMenu = new Menu("Help");
	private MenuItem helpAbout = new MenuItem("About MatLab2TikZ");
	private MenuItem helpProgram = new MenuItem("Help");
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
		MenuBar menu = new MenuBar();
		menu.add(fileMenu);
		menu.add(configMenu);
		menu.add(helpMenu);
		setMenuBar(menu);
		setLayout(new BorderLayout());
		add("Center", diwc);
		addWindowListener(new WindowCloser());
		setSize(1080, 720);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == fileExit) {
			ConfirmDialog exitDialog = new ConfirmDialog(this, "Confirm Exit", 
														 "Do you really want to exit?", 2);
			if (exitDialog.isOkay) {
				exitDialog.dispose();
				System.exit(0);
			}
			else
				exitDialog.dispose();
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
			ConfirmDialog aboutDialog = new ConfirmDialog(this, "About MatLab2TikZ", 
														  aboutContext);
			aboutDialog.dispose();
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
			ConfirmDialog helpDialog = new ConfirmDialog(this, "Help",
														 helpContext);
			helpDialog.dispose();
		}
	}
	
	public void importPicture() {
		try {
			FileDialog fd = new FileDialog(this, "Open File", FileDialog.LOAD);
			fd.setVisible(true);
			if (fd.getFile() != null) {
				File pic = new File(fd.getDirectory() + fd.getFile());
				import_image = ImageIO.read(new FileInputStream(pic));
				diwc.getImage(import_image);
				diwc.repaint();
			}
		} catch (Exception e) {
			System.out.println("Unknown error. " + e);
		}		
	}
	
	public void previewTikZ() {
		try {
			FileDialog fd = new FileDialog(this, "Open File", FileDialog.LOAD);
			fd.setFile("*.tikz");
			fd.setVisible(true);
			if (fd.getFile() != null) {
				if (fd.getFile().endsWith(".tikz")) {
					// Create file handler.
					File file = new File(fd.getDirectory() + fd.getFile());
					// Instantiate objects.
					TikZConvert tcvt = new TikZConvert();
					String filehash = tcvt.convert(file);
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
			fd.dispose();
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
