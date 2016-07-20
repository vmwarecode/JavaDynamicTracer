package com.vmware.javatracer;

/*
 * Summary: Panel for displaying selected method/field traces in a target VM
 * Author: Kannan Balasubramanian
 */

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


//Panel shown on the right hand side of splitpane where all the traces from the running jvm are dumped.
class LogPanel extends JPanel {
    private DefaultListModel model = new DefaultListModel();
    private JList btraceList = new JList(model);
    private JButton clearBtn = new JButton("Clear");
    private JButton pauseBtn = new JButton("Pause");
    private boolean bPause = false;
    public LogPanel() {
       setLayout(new BorderLayout());
       add(new JScrollPane(btraceList), BorderLayout.CENTER);
       JPanel panel = new JPanel();
       panel.add(clearBtn);
       panel.add(pauseBtn);
       add(panel, BorderLayout.SOUTH);
       clearBtn.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               model.clear();
           }
       });
       clearBtn.setMnemonic('L');
       pauseBtn.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               bPause = !bPause;
               if (bPause) {
                   pauseBtn.setText("Continue");
		   pauseBtn.setMnemonic('O');
               } else {
                   pauseBtn.setText("Pause");
		   pauseBtn.setMnemonic('P');
               }
           }
       });
       pauseBtn.setMnemonic('P');
       CodeTracer.addListener(new BtraceListener() {
           public void eventNotify(int type, String msg) {
                 if (bPause || (type != BtraceListener.CLASS_TYPE)) return;
                 final String message = msg;
                 SwingUtilities.invokeLater(new Runnable() {
                     public void run() {
                          int index = model.size()-1;
                          model.addElement(message);
                          btraceList.ensureIndexIsVisible(model.size()-1) ;
                      }
                 });
           }
       });
    }

    //This method has been added to pass the component to CodeTracer class for populating JList with the traces...
    public JList getComponent() {
       return btraceList;
    }
}
