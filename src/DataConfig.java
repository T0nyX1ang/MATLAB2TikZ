import java.awt.BorderLayout;
import java.awt.Color;
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
	int column = 15;
	int row = 1;
	private JLabel xStartLabel = new JLabel("The start number of x axis: ");
	private JTextArea xStartInput = new JTextArea(row, column);
	private JLabel xStopLabel = new JLabel("The stop number of x axis: ");
	private JTextArea xStopInput = new JTextArea(row, column);
	private JLabel xStepLabel = new JLabel("The step of x axis: ");
	private JTextArea xStepInput = new JTextArea(row, column);
	private JLabel xAxisLabel = new JLabel("The label of x axis: ");
	private JTextArea xLabelInput = new JTextArea(row, column);
	private JLabel yStartLabel = new JLabel("The start number of y axis: ");
	private JTextArea yStartInput = new JTextArea(row, column);
	private JLabel yStopLabel = new JLabel("The stop number of y axis: ");
	private JTextArea yStopInput = new JTextArea(row, column);
	private JLabel yStepLabel = new JLabel("The step of y axis: ");
	private JTextArea yStepInput = new JTextArea(row, column);
	private JLabel yAxisLabel = new JLabel("The label of y axis: ");
	private JTextArea yLabelInput = new JTextArea(row, column);
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
		int thickness = 1;
		JLabel help = new JLabel("You can make data settings here.");
		JPanel settings = new JPanel();
		settings.setLayout(new GridLayout(8, 2, 10, 10));
		settings.add(xStartLabel);
		settings.add(xStartInput);
		xStartInput.setBorder(BorderFactory.createLineBorder(Color.black, thickness));
		settings.add(xStopLabel);
		settings.add(xStopInput);
		xStopInput.setBorder(BorderFactory.createLineBorder(Color.black, thickness));
		settings.add(xStepLabel);
		settings.add(xStepInput);
		xStepInput.setBorder(BorderFactory.createLineBorder(Color.black, thickness));
		settings.add(xAxisLabel);
		settings.add(xLabelInput);
		xLabelInput.setBorder(BorderFactory.createLineBorder(Color.black, thickness));
		settings.add(yStartLabel);
		settings.add(yStartInput);
		yStartInput.setBorder(BorderFactory.createLineBorder(Color.black, thickness));
		settings.add(yStopLabel);
		settings.add(yStopInput);
		yStopInput.setBorder(BorderFactory.createLineBorder(Color.black, thickness));
		settings.add(yStepLabel);
		settings.add(yStepInput);
		yStepInput.setBorder(BorderFactory.createLineBorder(Color.black, thickness));
		settings.add(yAxisLabel);
		settings.add(yLabelInput);
		yLabelInput.setBorder(BorderFactory.createLineBorder(Color.black, thickness));
		
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
