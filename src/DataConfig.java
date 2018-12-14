import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class DataConfig extends JDialog implements ActionListener{
	private class WindowCloser extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			DataConfig.this.setVisible(false);
		}
	}

	// basic Dialog control.
	private JLabel xStartLabel = new JLabel("The start number of x axis: ");
	private JTextArea xStartInput = new JTextArea();
	private JLabel xStopLabel = new JLabel("The stop number of x axis: ");
	private JTextArea xStopInput = new JTextArea();
	private JLabel xStepLabel = new JLabel("The step of x axis: ");
	private JTextArea xStepInput = new JTextArea();
	private JLabel xAxisLabel = new JLabel("The label of x axis: ");
	private JTextArea xLabelInput = new JTextArea();
	private JLabel yStartLabel = new JLabel("The start number of y axis: ");
	private JTextArea yStartInput = new JTextArea();
	private JLabel yStopLabel = new JLabel("The stop number of y axis: ");
	private JTextArea yStopInput = new JTextArea();
	private JLabel yStepLabel = new JLabel("The step of y axis: ");
	private JTextArea yStepInput = new JTextArea();
	private JLabel yAxisLabel = new JLabel("The label of y axis: ");
	private JTextArea yLabelInput = new JTextArea();
	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("cancel");
	
	// a setting for data.
	double xStart = 0;
	double xStop = 0;
	double xStep = 0;
	double yStart = 0;
	double yStop = 0;
	double yStep = 0;
	String xLabel = null;
	String yLabel = null;
	boolean dataValidate = false;
	
	public DataConfig(JFrame parent, String title) {
		super(parent, title, true);
		init();		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		addWindowListener(new WindowCloser());
		setResizable(false);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}
	
	// validate
	public boolean validateData() {
		System.out.print("Validating data integrity...");
		return ((xStop > xStart) && (xStep > 0) && (xLabel != null) && 
				(yStop > yStart) && (yStep > 0) && (yLabel != null)); 
	}
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == okButton) {
			// getting the data
			try {
				xStart = Double.parseDouble(xStartInput.getText());
				xStop = Double.parseDouble(xStopInput.getText());
				xStep = Double.parseDouble(xStepInput.getText());
				xLabel = xLabelInput.getText();
				yStart = Double.parseDouble(yStartInput.getText());
				yStop = Double.parseDouble(yStopInput.getText());
				yStep = Double.parseDouble(yStepInput.getText());
				yLabel = yLabelInput.getText();
				dataValidate = true;
				DataConfig.this.setVisible(false);
			} catch (NumberFormatException nfe ) {
				System.err.println("Data format error." + nfe);
			}
		}
		else if (ae.getSource() == cancelButton) {
			DataConfig.this.setVisible(false);
		}
	}
	
	private void init() {
		JLabel help = new JLabel("You can make data settings here.");
		JPanel settings = new JPanel();
		settings.setLayout(new GridLayout(8, 2));
		settings.add(xStartLabel);
		settings.add(xStartInput);
		settings.add(xStopLabel);
		settings.add(xStopInput);
		settings.add(xStepLabel);
		settings.add(xStepInput);
		settings.add(xAxisLabel);
		settings.add(xLabelInput);
		settings.add(yStartLabel);
		settings.add(yStartInput);
		settings.add(yStopLabel);
		settings.add(yStopInput);
		settings.add(yStepLabel);
		settings.add(yStepInput);
		settings.add(yAxisLabel);
		settings.add(yLabelInput);		
		
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