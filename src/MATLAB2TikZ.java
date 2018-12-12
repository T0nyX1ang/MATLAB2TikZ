import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

@SuppressWarnings("serial")
public class MATLAB2TikZ extends Frame implements ActionListener, FilenameFilter{
	// This is the main class of the project.
	
	private class WindowCloser extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}
	private BufferedImage image;
	private Menu fileMenu = new Menu("File");
	private MenuItem fileImportPic = new MenuItem("Import");
	private MenuItem fileTikZPreview = new MenuItem("Preview TikZ");
	private MenuItem fileExit = new MenuItem("Exit");
	public DrawImageWithCanvas diwc = new DrawImageWithCanvas();
	public MATLAB2TikZ(){
		super("MatLab to TikZ");
		fileMenu.add(fileImportPic);
		fileImportPic.addActionListener(this);
		fileMenu.add(fileTikZPreview);
		fileTikZPreview.addActionListener(this);
		fileMenu.addSeparator();
		fileMenu.add(fileExit);
		fileExit.addActionListener(this);
		MenuBar menu = new MenuBar();
		menu.add(fileMenu);
		setMenuBar(menu);
		setLayout(new BorderLayout());
		add("Center", diwc);
		addWindowListener(new WindowCloser());
		setSize(1080, 720);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == fileExit)
			System.exit(0);
		
		else if (ae.getSource() == fileImportPic) {
			try {
				FileDialog fd = new FileDialog(this, "Open File", FileDialog.LOAD);
				fd.setFile("*.png");
				fd.setVisible(true);
				if (fd.getFile() != null) {
					File pic = new File(fd.getDirectory() + fd.getFile());
					BufferedImage import_image = ImageIO.read(new FileInputStream(pic));
					diwc.getImage(import_image);
					diwc.repaint();
				}
			} catch (Exception e) {
				System.out.println("Unknown error. " + e);
			}
		}
		
		else if (ae.getSource() == fileTikZPreview) {
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
	}

	@Override
	public boolean accept(File dir, String name) {
		return (name.endsWith(".pdf"));
	}
	
	public static void main(String[] args) {
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
		MATLAB2TikZ mainwindow = new MATLAB2TikZ();
	}
	
}
