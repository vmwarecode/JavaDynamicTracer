package com.vmware.javatracer;

/*
 * Summary: UI Class for taking various inputs from user before attaching to a target VM
 * Author: Kannan Balasubramanian
 */


import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.Action;
import java.awt.Window;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.lang.reflect.Method;
import sun.tools.jconsole.LocalVirtualMachine;

//Panel shown on the left hand side of the splitpane that takes inputs that are eventually used for creating a bscript file
class InputPanel extends JPanel {

    JTextField packageTextField = new JTextField(12);
    JTextField classTextField = new JTextField(12);
    JTextField methodTextField = new JTextField(12);
    JTextField fieldTextField = new JTextField(12);
    JComboBox pidCombo = new JComboBox();
    DefaultListModel model = new DefaultListModel();
    JList list = new JList(model);
    JButton addBtn = new JButton("Add To List");
    JButton removeBtn = new JButton("Remove From List");
    JButton attachBtn = new JButton("Attach to VM");
    JButton detachBtn = new JButton("Detach");
    JFrame barFrame = null;
    Window parent = null;
    String curpid = null;
    File curFile = null; 

    public InputPanel(Window p) {
	if (p == null) {
            this.parent = new JFrame();
	} else {
            this.parent = p;
	}
        Runtime.getRuntime().addShutdownHook(new Thread() {
              public void run() {
                  cleanupTraceFile();
              }
        });
        Dimension dim = new Dimension(50, 10);
        setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.weightx = 0.5;
        g.anchor = GridBagConstraints.FIRST_LINE_START;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(10, 0, 0, 0);
        g.gridx = 0;
        g.gridy = 0;
	JLabel classLabel = new JLabel("Class Name");
        add(classLabel, g);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 1;
        g.gridy = 0;
        add(classTextField, g);
	classLabel.setLabelFor(classTextField);
	classLabel.setDisplayedMnemonic('C');
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;
        g.gridy = 1;
	JLabel methodLabel = new JLabel("Method Name");
        add(methodLabel, g);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 1;
        g.gridy = 1;
        add(methodTextField, g);
	methodLabel.setLabelFor(methodTextField);
	methodLabel.setDisplayedMnemonic('M');
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;
        g.gridy = 2;
	JLabel fieldLabel = new JLabel("Field Name");
        add(fieldLabel, g);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 1;
        g.gridy = 2;
        add(fieldTextField, g);
	fieldLabel.setLabelFor(fieldTextField);
	fieldLabel.setDisplayedMnemonic('F');
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;
        g.gridy = 3;
	JLabel vmLabel = new JLabel("Select vmid");
        add(vmLabel, g);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 1;
        g.gridy = 3;
        pidCombo.setPreferredSize(new Dimension(12, 20));
        add(pidCombo, g);
	vmLabel.setLabelFor(pidCombo);
	vmLabel.setDisplayedMnemonic('S');
        refreshPids();

        /*g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;
        g.gridy = 4;
        add(new JLabel("Class/Methods to trace"), g);*/
        g.fill = GridBagConstraints.HORIZONTAL;
        //g.gridx = 1;
        g.gridx = 0;
        g.gridy = 4;
        g.gridwidth = 2;
        add(new JScrollPane(list), g);
        g.gridwidth = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;
        g.gridy = 5;
        add(addBtn, g);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 1;
        g.gridy = 5;
        add(removeBtn, g);
        g.insets = new Insets(0, 0, 0, 0);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;
        g.gridy = 6;
        add(attachBtn, g);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 1;
        g.gridy = 6;
        add(detachBtn, g);

        setToolTips();
        final ToolTipList tooltip = new ToolTipList(parent);
        tooltip.addToolTipListener(new ToolTipListener() {
            public void selectionChanged(ToolTipEvent e) {
               if (e.getType() == ToolTipEvent.METHOD) {
                   methodTextField.setText(e.getValue());
               } else if (e.getType() == ToolTipEvent.FIELD) {
                   fieldTextField.setText(e.getValue());
               }
            }
        });

        methodTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
               int modifiers = e.getModifiersEx();
               int mask = KeyEvent.CTRL_DOWN_MASK;
               if ((modifiers & mask) == mask) {
                   if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                       if (!isClassNameValid(classTextField.getText())) {
                           return;
                       }
                       String str = methodTextField.getText();
                       Point pt = new Point(str.length()*6, 16);
                       SwingUtilities.convertPointToScreen(pt, methodTextField);
                       tooltip.showToolTip(classTextField.getText(), pt, str, true);
                   }
               } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                   tooltip.hideToolTip();              
               }
            }
        });

        fieldTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
               int modifiers = e.getModifiersEx();
               int mask = KeyEvent.CTRL_DOWN_MASK;
               if ((modifiers & mask) == mask) {
                   if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                       if (!isClassNameValid(classTextField.getText())) {
                           return;
                       }
                       String str = fieldTextField.getText();
                       Point pt = new Point(str.length()*6, 16);
                       SwingUtilities.convertPointToScreen(pt, fieldTextField);
                       tooltip.showToolTip(classTextField.getText(), pt, str, false);
                   }
               } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                   tooltip.hideToolTip();              
               }
            }
        });

        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!verify()) {
                    classTextField.requestFocus();
                    return;
                }
                String className = classTextField.getText().trim();
                String methodName = methodTextField.getText().trim();
                String fieldName = fieldTextField.getText().trim();
                model.addElement(new Element(className, methodName, fieldName));
                classTextField.setText("");
                methodTextField.setText("");
                fieldTextField.setText("");
            }
        });
	addBtn.setMnemonic('A');

        removeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int [] indices = list.getSelectedIndices();
                if (indices == null || indices.length == 0) {
                    return;
                }
                int count = 0;
                for(int i=0; i<indices.length; i++) {
                    model.removeElementAt(indices[i]-count);
                    count++;
                }
            }
        });
	removeBtn.setMnemonic('R');

        attachBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (model.size() == 0) {
                    /*JOptionPane.showMessageDialog(InputPanel.this, "No class/methods specified for tracing!");
                    classTextField.requestFocus(); 
                    return;*/
                }
                if (pidCombo.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(InputPanel.this, "Please select pid of the targetted process!");
                    pidCombo.requestFocus();
                    return;
                }
                Thread t = new Thread(new Runnable() {
                    public void run() {
			 InputPanel.this.showProgressBar();
                         cleanupTraceFile();
                         String str = parseClassName(classTextField.getText());
                         //Detach tracing from the same process before attaching to it again //may be removed later
                         curpid = ((VMItem)pidCombo.getSelectedItem()).getPidString();
			 System.out.println("Attaching to vmid: " + curpid);
			 try {
                              CodeTracer.attachProcess(curpid, null, true);
			      DefaultListModel model = (DefaultListModel)list.getModel();
			      int size = model.size();
			      Element [] elems = new Element[size];
			      for (int i=0; i<elems.length; i++) {
				      elems[i] = (Element)model.getElementAt(i);
			      }
                              curFile = new BtraceFileGenerator().createBTraceFile(curpid, elems, TracePanel.bscripts);
			      System.err.println(curFile.getAbsolutePath()); //kannan
                              CodeTracer.attachProcess(curpid, curFile.getAbsolutePath(), false);
			 } finally {
				 System.err.println("Ever reach here??");
			      InputPanel.this.hideProgressBar();
			 }
                    }
               });
               t.start();
               attachBtn.setEnabled(false);
               //enable detachBtn on EDT
               detachBtn.setEnabled(true);
            }
        });
	attachBtn.setMnemonic('V');

        detachBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Thread t = new Thread(new Runnable() {
                    public void run() {
                         if (curpid != null) {
                             CodeTracer.attachProcess(curpid, null, true);
                             curpid = null;
                         }
                         cleanupTraceFile();
                         detachBtn.setEnabled(false);
                    }
               });
               t.start();
               attachBtn.setEnabled(true);
	       System.err.println("detach button clicked...");
            }
        });
	detachBtn.setMnemonic('D');
        detachBtn.setEnabled(false);
	Runnable task = new Runnable() {
		public void run() {
			refreshPids();
		}
	};
	TracePanel.handleKey(attachBtn, task, KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_DOWN_MASK);
    }

    public void showProgressBar() {
	if (barFrame == null) {
	    JProgressBar pBar = new JProgressBar();
	    pBar.setIndeterminate(true);
	    barFrame = new JFrame("Please Wait...");
	    barFrame.getContentPane().add(pBar);
	    barFrame.pack();
	    Point pt = this.getLocation();
	    pt = SwingUtilities.convertPoint(this, pt.x, pt.y, barFrame);
	    barFrame.setLocation(pt.x, pt.y);
	    barFrame.pack();
	}
	barFrame.setVisible(true);
    }

    public void hideProgressBar() {
	barFrame.setVisible(false);
    }

    //Get the list of all jvm ids running on the local system
    public void refreshPids() {
        Map<Integer, LocalVirtualMachine> map = LocalVirtualMachine.getAllVirtualMachines();
        Iterator iter = map.values().iterator();
        LocalVirtualMachine vm = null;
        pidCombo.removeAllItems();
        while (iter.hasNext()) {
            vm = (LocalVirtualMachine)iter.next();
            //Add all the jvm ids to the list except the current process
            if (vm.displayName().indexOf("JavaTracer") == -1) {
                pidCombo.addItem(new VMItem(vm.vmid(), vm.displayName()));
            }
        }
    }

    //Method to delete the last trace file (that was created on attaching process). This is called on detach or reattach or on app's exit using shutdown hook
    public void cleanupTraceFile() {
        if (curFile != null && curFile.exists()) {
            curFile.delete();
            curFile = null;
        }
    }

    //Add meaningful tooltips to components wherever applicable
    public void setToolTips() {
        classTextField.setToolTipText("Please enter fully qualified class name... [like javax.swing.JFrame]");
        methodTextField.setToolTipText("Press Ctrl-space to get method list for the given class");
        fieldTextField.setToolTipText("Press Ctrl-space to get field list for the given class");
        pidCombo.setToolTipText("Press F5/Ctrl-R to refresh vmids ...");
        attachBtn.setToolTipText("Click to attach to the selected pid of a running jvm");
        detachBtn.setToolTipText("Click to detach the currently traced jvm");
    }

    //Method to validate the necessary field values that are required before attaching to a running jvm.
    public boolean verify() {
        String className = classTextField.getText();
        boolean bFlag = isClassNameValid(className);
        if (!bFlag) {
            JOptionPane.showMessageDialog(this, "Invalid class name: " + className);
            return bFlag;
        }
        String methodName = methodTextField.getText();
        bFlag = isMethodNameValid(className, methodName);
        if (!bFlag) {
            JOptionPane.showMessageDialog(this, "Invalid method name: " + methodName);
        }
        return bFlag;
    }

    //Method to validate a given classname
    public static boolean isClassNameValid(String className) {
        try {
            Class c = Class.forName(className);
            if (c == null) {
                return false;
            }
        } catch (Exception e) {
           return false;
        }
        return true;
    }

    //Method to validate a methodname for a given class name
    public static boolean isMethodNameValid(String className, String methodName) {
        methodName = methodName.trim();
        if (methodName.length() == 0) {
            //This would imply all methods...
            return true;
        }
        try {
            Class c = Class.forName(className);
            if (c == null) {
                return false;
            }
            Method [] m = c.getDeclaredMethods();
            if (m == null) {
                return false;
            }
            String mName = null;
            for (int i=0; i<m.length; i++) {
                 mName = m[i].getName() + "(";
                 Class [] carray = m[i].getParameterTypes();
                 for (int j=0; j<carray.length; j++) {
                      if (j > 0) {
                          mName = mName + ", ";
                      }
                      mName = mName + carray[j];
                 }
                 mName = mName + ")";
                 if (mName.equals(methodName)) {
                     return true;
                 }
            }
        } catch (Exception e) {
           return false;
        }
        return false;
    }

    public static String parseClassName(String className) {
        if (className == null || className.trim().length() == 0) {
            return "";
        }
        String buf = "";
        String tmp = null;
        int count = 0;
        StringTokenizer token = new StringTokenizer(className, ".");
        while (token.hasMoreElements()) {
            if (count > 0) {
               buf = buf + "\\\\.";
            }
            tmp = token.nextToken();
            buf = buf + tmp;
            count++;
        }
        return buf;
    }

    public static String [] getMethodParameters(String methodName) {
        String [] params = null;
        String mName = methodName;
        methodName = methodName.trim();
        return params;
    }

}
