import java.awt.Color;
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
	private int column = 20;
	private int row = 1;
	private int thickness = 1;
	private int legendCount = 0;
	private final int LEGEND_ELEM_LIMIT = 10;
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
	private JLabel[] legendNameLabel = new JLabel[LEGEND_ELEM_LIMIT];
	private JTextArea[] legendInput = new JTextArea[LEGEND_ELEM_LIMIT];
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
	String[] legendLabel = new String[LEGEND_ELEM_LIMIT];
	boolean dataValidate = false;
	
	public DataConfig(JFrame parent, String title, int legendCount) {
		super(parent, title, true);
		this.legendCount = legendCount;
		init();		
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
			// getting the data
			boolean xStartFormat = false;
			boolean xStopFormat = false;
			boolean xStepFormat = false;
			boolean yStartFormat = false;
			boolean yStopFormat = false;
			boolean yStepFormat = false;
			try {
				borderReset();
				xStart = Double.parseDouble(xStartInput.getText());
				xStartFormat = true;
				xStop = Double.parseDouble(xStopInput.getText());
				xStopFormat = true;
				xStep = Double.parseDouble(xStepInput.getText());
				xStepFormat = true;
				xLabel = xLabelInput.getText();
				yStart = Double.parseDouble(yStartInput.getText());
				yStartFormat = true;
				yStop = Double.parseDouble(yStopInput.getText());
				yStopFormat = true;
				yStep = Double.parseDouble(yStepInput.getText());
				yStepFormat = true;
				yLabel = yLabelInput.getText();
				for (int i = 0; i < legendCount; i++)
					legendLabel[i] = legendInput[i].getText();
				if (xLabel == null || yLabel == null || legendLabel == null ||
					xStop <= xStart || yStop <= yStart || xStep <= 0 || yStep <= 0)
					throw new NullPointerException();
				dataValidate = true;
				DataConfig.this.setVisible(false);
			} catch (NumberFormatException nfe) {
				System.err.println("Data format error. " + nfe);
				if (!xStartFormat)
					xStartInput.setBorder(BorderFactory.createLineBorder(Color.red, thickness));
				
				if (!xStopFormat)
					xStopInput.setBorder(BorderFactory.createLineBorder(Color.red, thickness));
				
				if (!xStepFormat)
					xStepInput.setBorder(BorderFactory.createLineBorder(Color.red, thickness));
				
				if (!yStartFormat)
					yStartInput.setBorder(BorderFactory.createLineBorder(Color.red, thickness));
				
				if (!yStopFormat)
					yStopInput.setBorder(BorderFactory.createLineBorder(Color.red, thickness));
				
				if (!yStepFormat)
					yStepInput.setBorder(BorderFactory.createLineBorder(Color.red, thickness));
				DataConfig.this.setVisible(true);
			} catch (NullPointerException npe) {
				System.err.println("Empty string. " + npe);
				if (xStart >= xStop) {
					xStartInput.setBorder(BorderFactory.createLineBorder(Color.red, thickness));
					xStopInput.setBorder(BorderFactory.createLineBorder(Color.red, thickness));
				}
				
				if (yStart >= yStop) {
					yStartInput.setBorder(BorderFactory.createLineBorder(Color.red, thickness));
					yStopInput.setBorder(BorderFactory.createLineBorder(Color.red, thickness));					
				}
				
				if (xStep <= 0)
					xStepInput.setBorder(BorderFactory.createLineBorder(Color.red, thickness));
				
				if (yStep <= 0)
					yStepInput.setBorder(BorderFactory.createLineBorder(Color.red, thickness));		
				DataConfig.this.setVisible(true);
			}
		}
		else if (ae.getSource() == cancelButton) {
			dataValidate = false;
			DataConfig.this.setVisible(false);
		}
	}
	
	private void init() {
		JLabel help = new JLabel("You can make data settings here.");
		JPanel settings = new JPanel();
		settings.setLayout(new GridLayout(8 + legendCount, 2, 10, 10));
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
		borderReset();
		for (int i = 0; i < legendCount; i++) {
			legendNameLabel[i] = new JLabel("Legend name of dataset " + (i + 1));
			legendInput[i] = new JTextArea(row, column);
			settings.add(legendNameLabel[i]);
			settings.add(legendInput[i]);
			legendInput[i].setBorder(BorderFactory.createLineBorder(Color.black, thickness));
		}
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		buttons.add(okButton);
		buttons.add(cancelButton);
		
		setLayout(new BorderLayout());
		add("North", help);
		add("Center", settings);
		add("South", buttons);
	}
	
	private void borderReset() {
		xStartInput.setBorder(BorderFactory.createLineBorder(Color.black, thickness));
		xStopInput.setBorder(BorderFactory.createLineBorder(Color.black, thickness));
		xStepInput.setBorder(BorderFactory.createLineBorder(Color.black, thickness));
		xLabelInput.setBorder(BorderFactory.createLineBorder(Color.black, thickness));
		yStartInput.setBorder(BorderFactory.createLineBorder(Color.black, thickness));
		yStopInput.setBorder(BorderFactory.createLineBorder(Color.black, thickness));
		yStepInput.setBorder(BorderFactory.createLineBorder(Color.black, thickness));
		yLabelInput.setBorder(BorderFactory.createLineBorder(Color.black, thickness));
	}

}
