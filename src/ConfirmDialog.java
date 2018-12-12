import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class ConfirmDialog extends Dialog implements ActionListener{
	// a confirm dialog model. 
	// type 1: only an OK button.
	// type 2: an OK button and a cancel button: 
	//            OK will confirm the operation, 
	//            cancel will cancel the operation. 
	// type 3: an OK button, a cancel, and an apply button: 
	//            OK will confirm the operation, 
	//            cancel will cancel the operation.
	//			  apply will apply the setting, but the dialog won't end.
	private Button okay = new Button("OK");
	private Button cancel = new Button("Cancel");
	private Button apply = new Button("Apply");
	private TextArea ta = new TextArea();
	public boolean isOkay = false;
	public boolean isApply = false;
	private int type = 1;
	private class WindowCloser extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			ConfirmDialog.this.isOkay = false;
			ConfirmDialog.this.isApply = false;
			ConfirmDialog.this.dispose();
		}
	}
	
	public ConfirmDialog(Frame parent, String title, String context) {
		super(parent, title, true);
		ta.setEditable(false);
		ta.setText(context);
		Panel buttons = new Panel();
		buttons.setLayout(new FlowLayout());
		buttons.add(okay);
		okay.addActionListener(this);
		setLayout(new BorderLayout());
		add("Center", ta);
		add("South", buttons);
		addWindowListener(new WindowCloser());
		pack();
		setVisible(true);
	}
	
	public ConfirmDialog(Frame parent, String title, String context, int type) {
		super(parent, title, true);
		this.type = type;
		ta.setEditable(false);
		ta.setText(context);
		Panel buttons = new Panel();
		buttons.setLayout(new FlowLayout());
		buttons.add(okay);
		okay.addActionListener(this);
		if (this.type >= 2) {
			buttons.add(cancel);
			cancel.addActionListener(this);
		}
		if (this.type >= 3) {
			buttons.add(apply);
			apply.addActionListener(this);
		}
		setLayout(new BorderLayout());
		add("Center", ta);
		add("South", buttons);
		addWindowListener(new WindowCloser());
		pack();
		setVisible(true);
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == okay) {
			ConfirmDialog.this.isOkay = true;
			ConfirmDialog.this.isApply = true;
			ConfirmDialog.this.setVisible(false);
		} else if (ae.getSource() == cancel) {
			ConfirmDialog.this.isOkay = false;
			ConfirmDialog.this.isApply = false;
			ConfirmDialog.this.setVisible(false);
		} else if (ae.getSource() == apply) {
			ConfirmDialog.this.isOkay = false;
			ConfirmDialog.this.isApply = true;
			ConfirmDialog.this.setVisible(true);
		}
	}	
}
