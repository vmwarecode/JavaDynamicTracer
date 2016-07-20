package com.vmware.javatracer;

/*
 * Frame for embedding Tracer panel
 * Author: Kannan Balasubramanian
 */


import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JApplet;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import java.net.URI;

public class TracerApplet extends JApplet {

    private static TracerApplet frame;
    TracePanel panel = null;
    public TracerApplet() {
        //setTitle("TracerApplet");
        setBackground(java.awt.Color.RED);
	panel = new TracePanel(null);
        getContentPane().add(panel.getPane());
	setJMenuBar(getMenu());
        //pack();
        setSize(900, 400);
        setVisible(true);
    }

    public JMenuBar getMenu() {
        JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu("File");
	fileMenu.setMnemonic('F');
	JMenuItem fileItem1 = new JMenuItem("Refresh VMs");
	fileItem1.setMnemonic('R');
	fileItem1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			panel.refreshPids();
		}
	});
	fileMenu.add(fileItem1);

	JMenu editMenu = new JMenu("Edit");
	editMenu.setMnemonic('E');
	JMenuItem editItem1 = new JMenuItem("Options");
	editItem1.setMnemonic('O');
	editMenu.add(editItem1);
	editItem1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			panel.showOptionsDialog();
		}
	});

	JMenu helpMenu = new JMenu("Help");
	helpMenu.setMnemonic('H');
	JMenuItem helpItem1 = new JMenuItem("<html><a href> JavaTracer doc</a></html>");
	helpItem1.setMnemonic('D');
	helpItem1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
                    try {
                         Desktop.getDesktop().browse(new URI("http://twiki.corp.yahoo.com/view/WhiteBoxEng/JavaTracer"));
		    } catch (Exception ex) {
                         ex.printStackTrace();
		    }
		}
	});
	JMenuItem helpItem2 = new JMenuItem("<html><a href>How to add plugins</a></html>");
	helpItem2.setMnemonic('H');
	helpItem2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
                    try {
                         Desktop.getDesktop().browse(new URI("http://twiki.corp.yahoo.com/view/WhiteBoxEng/JavaTracer-Plugins"));
		    } catch (Exception ex) {
                         ex.printStackTrace();
		    }

		}
	});
	JMenuItem helpItem3 = new JMenuItem("<html><a href><font color=\"red\">About</a></font></html>");
	helpItem3.setMnemonic('A');
	final JDialog dialog = new JDialog((java.awt.Frame)null, "About", true);
	dialog.getContentPane().add(new AboutPanel());
	dialog.setSize(600, 600);
	helpItem3.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			dialog.setVisible(true);
		}
	});
	helpMenu.add(helpItem1);
	helpMenu.add(helpItem2);
	helpMenu.addSeparator();
	helpMenu.add(helpItem3);
	menuBar.add(fileMenu);
	menuBar.add(editMenu);
	menuBar.add(helpMenu);
	return menuBar;
    }

    public static TracerApplet getInstance() {
        if (frame == null) {
            frame = new TracerApplet();
        }
        return frame;
    }
}
