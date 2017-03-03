import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.DefaultListModel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Arrays;

import java.nio.ByteBuffer;

public class AsmMiniGame extends JFrame {

    private int[] inputs;

    private int[] expects;

    private DefaultListModel<Integer> outputs;

    public boolean passFlag = false;

    public AsmMiniGame (int[] inputs, int[] expects) {
	this.inputs = inputs;
	this.expects = expects;
	this.outputs = new DefaultListModel<Integer> ();
	setSize (500, 450);
	setResizable (false);

	final JPanel leftBar = new JPanel (new BorderLayout ());
	leftBar.add (new JList (Arrays.stream (inputs)
				      .boxed().toArray()),
		     BorderLayout.WEST);
	leftBar.add (new JList (Arrays.stream (expects)
				      .boxed().toArray()),
		     BorderLayout.EAST);
	add (leftBar, BorderLayout.WEST);
	add (new JList (outputs), BorderLayout.EAST);

	final JButton execBtn = new JButton ("Run");
	add (execBtn, BorderLayout.SOUTH);

	final JTextArea hexEditor = new JTextArea ();
	add (hexEditor, BorderLayout.CENTER);

	execBtn.addActionListener(new ActionListener ()
	    {
		@Override
		public void actionPerformed (ActionEvent e) {
		    final char[] raw = hexEditor.getText ().toCharArray ();
		    final ByteBuffer buf = ByteBuffer.allocate (raw.length / 2 + raw.length % 2);
		    for (int i = 0; i < raw.length; ++i) {
			byte b = hexToByte (raw[i]);
			if (i < raw.length) {
			    b <<= 4;
			    b |= hexToByte (raw[++i]);
			}
			buf.put(b);
		    }
		    final int[] rst = new AsmExec (buf.array ()).execute (inputs);
		    outputs.clear ();
		    if (rst.length == expects.length) {
			passFlag = false;
			for (int i = 0; i < rst.length; ++i) {
			    if (rst[i] == expects[i]) {
				outputs.addElement (rst[i]);
				passFlag = true;
			    } else {
				break;
			    }
			}
		    }
		}
	    });
    }

    private byte hexToByte (char c) {
	if (c >= '0' && c <= '9') return (byte) (c - '0');
	if (c >= 'a' && c <= 'f') return (byte) (c - 'a' + 10);
	if (c >= 'A' && c <= 'F') return (byte) (c - 'A' + 10);
	return 0;
    }
}
