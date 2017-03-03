import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.DefaultCellEditor;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.Color;
import java.awt.Component;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Arrays;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class AsmMiniGame extends JFrame {

    private int[] inputs;

    private int[] expects;

    private DefaultListModel<Integer> outputs;

    private DefaultTableModel hexPad;

    private Object notify;

    public boolean passFlag = false;

    private static class HexCellEditor extends DefaultCellEditor {

	private static final Border red = new LineBorder(Color.red);

	private static final Border black = new LineBorder (Color.black);

	private JTextField f;

	public HexCellEditor () {
	    super (new JTextField ());
	    f = (JTextField) getComponent();
	    f.setHorizontalAlignment (JTextField.RIGHT);
	}

	@Override
	public boolean stopCellEditing () {
	    final String s = f.getText ().trim();
	    if (!s.matches ("[0-9a-fA-F]{1,2}")) {
		f.setBorder (red);
		return false;
	    }
	    f.setText (s);
	    return super.stopCellEditing ();
	}

	@Override
	public Component getTableCellEditorComponent (JTable tbl,
						      Object value,
						      boolean isSelected,
						      int row,
						      int column) {
	    JTextField f = (JTextField) super.getTableCellEditorComponent (tbl, value, isSelected, row, column);
	    f.setBorder (black);
	    return f;
	}
    }

    public AsmMiniGame (int[] inputs, int[] expects, Object notify) {
	this.inputs = inputs;
	this.expects = expects;
	this.outputs = new DefaultListModel<Integer> ();
	this.notify = notify;

	setSize (500, 450);
	setResizable (false);
	setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);

	final JPanel leftBar = new JPanel (new BorderLayout ());
	leftBar.add (new JList<Object> (Arrays.stream (inputs)
				      .boxed().toArray()),
		     BorderLayout.WEST);
	leftBar.add (new JList<Object> (Arrays.stream (expects)
				      .boxed().toArray()),
		     BorderLayout.EAST);
	add (leftBar, BorderLayout.WEST);

	final JList<Integer> outList = new JList<Integer> (outputs);
        add (outList, BorderLayout.EAST);

	final JPanel bottomBar = new JPanel (new BorderLayout ());
	final JButton execBtn = new JButton ("Run");
	bottomBar.add (execBtn, BorderLayout.SOUTH);
	final JButton addRowBtn = new JButton ("Add row");
	bottomBar.add (addRowBtn, BorderLayout.NORTH);
	add (bottomBar, BorderLayout.SOUTH);

	hexPad = new DefaultTableModel ()
	    {
		@Override
		public Class<?> getColumnClass (int column) {
		    return Integer.class;
		}

		@Override
		public boolean isCellEditable (int row, int column) {
		    if (column == 0) return false;
		    return true;
		}
	    };
	hexPad.addColumn ("");
	for (int i = 0; i < 0x10; ++i) {
	    hexPad.addColumn (Integer.toString (i, 16));
	}
	hexPad.addRow (new Object[] { 0 });
	final JTable tbl = new JTable (hexPad);
	tbl.setDefaultEditor (Integer.class, new HexCellEditor ());
	tbl.getTableHeader ().setReorderingAllowed (false);
	add (new JScrollPane (tbl), BorderLayout.CENTER);

	addRowBtn.addActionListener (new ActionListener ()
	    {
		@Override
		public void actionPerformed (ActionEvent e) {
		    hexPad.addRow (new Object[]
			{
			    Integer.toString (hexPad.getRowCount () * 16, 16)
			});
		}
	    });

	execBtn.addActionListener (new ActionListener ()
	    {
		@Override
		public void actionPerformed (ActionEvent e) {
		    // hexEditor.setEditable (false);
		    final AsmExec session;
		    try (ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
			for (int i = 0; i < hexPad.getRowCount(); ++i) {
			    for (int j = 1; j < hexPad.getColumnCount(); ++j) {
				try {
				    final Object obj = hexPad.getValueAt (i, j);
				    if (obj == null) break;
				    buf.write (Integer.parseInt (obj.toString (), 16));
				} catch (NumberFormatException ex) {
				    break;
				}
			    }
			}
			buf.flush ();
			session = new AsmExec (buf.toByteArray ());
		    } catch (IOException ex) {
			javax.swing.JOptionPane.showMessageDialog (null, "That's awkward.. I could not run your code...");
			return;
		    }

		    final int[] rst;
		    try {
			rst = session.execute (inputs);
		    } catch (RuntimeException ex) {
			javax.swing.JOptionPane.showMessageDialog (null, "CRASH: " + ex);
			return;
		    }
		    outputs.clear ();
		    passFlag = false;
		    for (int i = 0; i < rst.length; ++i) {
			outputs.addElement (rst[i]);
			if (rst[i] == expects[i]) {
			    passFlag = true;
			} else {
			    passFlag = false;
			    break;
			}
		    }
		    if (passFlag && rst.length == expects.length) {
			dispose ();
			synchronized (notify) {
			    notify.notify ();
			}
		    }
		    // hexEditor.setEditable (true);
		}
	    });
    }
}
