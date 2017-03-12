/**
 * MIT License
 *
 * Copyright (c) 2017 Paul T.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package app.entity.gym;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.DefaultListModel;
import javax.swing.DefaultCellEditor;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.Font;
import java.awt.Color;
import java.awt.Component;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Arrays;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class AsmMiniGame extends JFrame {

    private DefaultListModel<Integer> outputs;

    private DefaultTableModel hexPad;

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

    private static Object[] toObjArray (int[] arr) {
	final Object[] rst = new Object[arr.length];
	for (int i = 0; i < arr.length; ++i) {
	    rst[i] = arr;
	}
	return rst;
    }

    public AsmMiniGame (final int[] inputs, final int[] expects,
			final Object notify) {
	this.outputs = new DefaultListModel<Integer> ();

	setSize (500, 450);
	setResizable (false);
	setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);

	final JPanel leftBar = new JPanel (new BorderLayout ());
	leftBar.add (new JList<Object> (toObjArray (inputs)), BorderLayout.WEST);
	leftBar.add (new JList<Object> (toObjArray (expects)), BorderLayout.EAST);
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
		    return column != 0;
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

	final JTabbedPane center = new JTabbedPane ();
        center.addTab ("Code", null, new JScrollPane (tbl), "Your code");
	final JTextArea tmp = new JTextArea(
					    "There are a total of 16 registers. They are 0 to F.\n  - Register C is always going to be zero\n  - Register D is the flag register\n  - Register E is related to the input buffer\n  - Register F is the instruction pointer\n\nHalt execution                      0\n  00                             => Halt\nLoad register                       1\n  01 15                          => Load r5 to r1\nLoad 4-bit number                   2\n  02 3B                          => Load 0x0B to r3\nLoad 8-bit number                   3\n  03 04 25                       => Load 0x25 to r4\nLoad 16-bit number                  4\n  04 02 AB F0                    => Load 0xABF0 to r2\nLoad memory                         5\n  05 00 01 02                    => Load data at 0x0102 to r0\nStore memory                        6\n  06 00 01 02                    => Store data at r0 to 0x0102\nAdd                                 7\n  07 15                          => Store r1 + r5 into r1\nMinus                               8\n  08 15                          => Store r1 - r5 into r1\nMultiply                            9\n  09 15                          => Store r1 * r5 into r1\nDivide                              A\n  0A 15                          => Store r1 / r5 into r1\nRemainder                           B\n  0B 15                          => Store remainder of r1 / r5 into r1\nBitwise and                         C\n  0C 15                          => Store r1 AND r5 into r1\nBitwise or                          D\n  0D 15                          => Store r1 OR r5 into r1\nBitwise xor                         E\n  0E 15                          => Store r1 XOR r5 into r1\nNot / Negate                        F\n  0F 9F                          => Store the bitwise compliment of r9 in r9\n  0F 60                          => Store the negative of r6 in r6\nShift right                         10\n  10 36                          => Shift r3 by r6\nShift left                          11\n  11 36                          => Shift r3 by r6\nShift signed right                  12\n  12 36                          => Shift r3 by r6\nRead / Write                        13\n  13 9F                          => Read and store to r9\n  13 60                          => Write the value of r6\nCompare                             14\n  14 9E                          => Compare r9 and r14\nJump / Goto                         15\n  15 01 02 03 04                 => Jump to address 0x01020304\nJump if equals                      16\n  16 01 02 03 04                 => Jump to address 0x01020304\nJump if not equals                  17\n  17 01 02 03 04                 => Jump to address 0x01020304\nJump if less than                   18\n  18 01 02 03 04                 => Jump to address 0x01020304\nJump if more than                   19\n  19 01 02 03 04                 => Jump to address 0x01020304\nJump if less or equal               1A\n  1A 01 02 03 04                 => Jump to address 0x01020304\nJump if more or equal               1B\n  1B 01 02 03 04                 => Jump to address 0x01020304\nJump if there is nothing to read    1C\n  1C 01 02 03 04                 => Jump to address 0x01020304\nAdd 1 / Minus 1                     1D\n  1D 9F                          => Add 1 to r9\n  1D 60                          => Subtract 1 from r6\n");
	tmp.setEditable (false);
	tmp.setFont (new Font (Font.MONOSPACED, Font.PLAIN, 12));
	center.addTab ("Reference", null, new JScrollPane (tmp),
		       "Like a help file");
	add (center, BorderLayout.CENTER);

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
		}
	    });
    }
}
