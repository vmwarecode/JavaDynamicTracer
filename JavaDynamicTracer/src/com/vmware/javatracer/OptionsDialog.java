package com.vmware.javatracer;

/*
 * Summary: Class for displaying options of selecting various JavaTracer plugins
 * Author: Kannan
 */

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

public class OptionsDialog extends JDialog {
        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new JPanel();
        JButton selectBtn = new JButton("Select All");
        JButton clearBtn = new JButton("Clear All");
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");
        JCheckBox [] checks = null;
        JCheckBox [] pChecks = null;
        boolean bCommit = false;

	public OptionsDialog(JCheckBox [] checkBoxes) {
            if (checkBoxes == null || checkBoxes.length == 0) {
                return;
            }
            setTitle("Select Options");
	    setModal(true);
            pChecks = checkBoxes;
            checks = new JCheckBox[checkBoxes.length];
            for (int i=0; i<checks.length; i++) {
                 checks[i] = new JCheckBox(checkBoxes[i].getText());
                 checks[i].setSelected(checkBoxes[i].isSelected());
            }

            okBtn.addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
                      bCommit = true;
		      commitContent();
                      hideDialog();
                 }
            });
	    okBtn.setMnemonic('O');

            cancelBtn.addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
                      bCommit = false;
		      resetContent();
                      hideDialog();
                 }
            });
	    cancelBtn.setMnemonic('C');

	    getContentPane().setLayout(new BorderLayout());
            topPanel.setLayout(new GridLayout(Math.round(checkBoxes.length/2),2));
            add(topPanel, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);
            if (checks != null) {
                for (int i=0; i<checks.length; i++) {
	             topPanel.add(checks[i]);
                }
            }

            selectBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (checks != null) {
                        for (int i=0; i<checks.length; i++) {
                             checks[i].setSelected(true);
                        }
                    }
                }
            });
	    selectBtn.setMnemonic('S');

            clearBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (checks != null) {
                        for (int i=0; i<checks.length; i++) {
                             checks[i].setSelected(false);
                        }
                    }
                }
            });
	    clearBtn.setMnemonic('l');

            bottomPanel.add(selectBtn);
            bottomPanel.add(clearBtn);
            bottomPanel.add(okBtn);
            bottomPanel.add(cancelBtn);
	    pack();
            setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            addComponentListener(new ComponentAdapter() {
                public void componentHidden(ComponentEvent e) {

                }
                public void componentShown(ComponentEvent e) {
                    bCommit = false;
                }
            });

	    Utilities.handleEscKey(cancelBtn, new Runnable() {
                public void run() {
                     bCommit = false;
		     resetContent();
                     hideDialog();
		}
	    });
	}

        public void hideDialog() {
            setVisible(false);
        }

	public boolean isSuccess() {
            return bCommit;
	}

        public void commitContent() {
            for (int i=0; i<checks.length; i++) {
                 pChecks[i].setSelected(checks[i].isSelected());
            }
        }

        public void resetContent() {
System.out.println("reset content...");
            for (int i=0; i<checks.length; i++) {
                 checks[i].setSelected(pChecks[i].isSelected());
            }
        }

	public static void main (String [] args) {
            JCheckBox [] boxes = new JCheckBox[17];
            for (int i=0; i<boxes.length; i++) {
                 boxes[i] = new JCheckBox("CheckBox: " + i);
            }
            new OptionsDialog(boxes);
	}
}
