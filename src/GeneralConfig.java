import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class GeneralConfig extends JDialog implements ActionListener, ItemListener{
	private class WindowCloser extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			dataValidate = false;
			GeneralConfig.this.setVisible(false);
		}
	}
	
	// basic Dialog control.
	String[] compilerData = new String[] {"xelatex", "pdflatex"};
	String[] autoData = new String[] {"Automatically", "Manually"};
	Integer[] dpiData = new Integer[] {150, 180, 210, 240, 270, 300};
	
	private JLabel dpiLabel = new JLabel("DPI Value");
	JComboBox<Integer> dpiList = new JComboBox<Integer>(dpiData);
	
	private JLabel compilerLabel = new JLabel("LaTeX Compiler");
	JComboBox<String> compilerList = new JComboBox<String>(compilerData);
	
	private JLabel autoLabel = new JLabel("Automatic Mode");
	JComboBox<String> autoList = new JComboBox<String>(autoData);
	
	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("cancel");
	
	// a setting for data. [Default Value]
	int dpiValue = 240;
	String LaTeXCompiler = "xelatex";
	boolean autoMode = false;
	private String tempCompiler = "xelatex";
	private int tempDPI = 240;
	private boolean tempAutoMode = false;
	boolean dataValidate = false;
	
	public GeneralConfig(JFrame parent, String title) {
		super(parent, title, true);
		init();
		autoList.addItemListener(this);
		autoList.setSelectedIndex(1);
		dpiList.addItemListener(this);
		dpiList.setSelectedIndex(3);
		compilerList.addItemListener(this);
		compilerList.setSelectedIndex(0);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		addWindowListener(new WindowCloser());
		setResizable(false);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == okButton) {
			dataValidate = true;
			dpiValue = tempDPI;
			LaTeXCompiler = tempCompiler;
			autoMode = tempAutoMode;
			GeneralConfig.this.setVisible(false);
		} else if (ae.getSource() == cancelButton) {
			dataValidate = false;
			GeneralConfig.this.setVisible(false);
		}
	}
	
	public void itemStateChanged(ItemEvent ie) {
		if (ie.getStateChange() == ItemEvent.SELECTED) {
			if (ie.getSource() == compilerList) {
				tempCompiler = compilerData[compilerList.getSelectedIndex()];
			}
			else if (ie.getSource() == dpiList) {
				tempDPI = (int) dpiData[dpiList.getSelectedIndex()];
			}
			else if (ie.getSource() == autoList) {
				tempAutoMode = (autoData[autoList.getSelectedIndex()] == "Automatically");
			}
		}
	}
	
	public void init() {
		JLabel help = new JLabel("You can make data settings here.");
		JPanel settings = new JPanel();
		settings.setLayout(new GridLayout(3, 2, 10, 10));
		settings.add(autoLabel);
		settings.add(autoList);
		settings.add(compilerLabel);
		settings.add(compilerList);
		settings.add(dpiLabel);
		settings.add(dpiList);
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		buttons.add(okButton);
		buttons.add(cancelButton);
		setLayout(new BorderLayout());
		add("North", help);
		add("Center", settings);
		add("South", buttons);
	}
	
}
