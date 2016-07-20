
/*
 * Frame for embedding Tracer panel
 * Author: Kannan Balasubramanian
 */

package com.vmware.javatracer;

import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.KeyStroke;
import java.net.URI;

public class JavaTracer extends JFrame {

    private static JavaTracer frame;
    TracePanel panel = null;
    public JavaTracer() {
        setTitle("JavaTracer");
        setBackground(java.awt.Color.RED);
	panel = new TracePanel(this);
        getContentPane().add(panel.getPane());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setJMenuBar(getMenu());
        pack();
        setSize(900, 400);
        setVisible(true);
    }

    public JMenuBar getMenu() {
        JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu("File");
	fileMenu.setMnemonic('F');
	JMenuItem fileItem1 = new JMenuItem("Refresh VMs");
	fileItem1.setMnemonic('R');
	fileItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
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
	editItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
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
                         Desktop.getDesktop().browse(new URI("e:/kannan/kannan/JavaTracer/JavaTracer.pdf"));
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
                         Desktop.getDesktop().browse(new URI("e:/kannan/kannan/JavaTracer/JavaTracer-Plugins.pdf"));
		    } catch (Exception ex) {
                         ex.printStackTrace();
		    }

		}
	});
	JMenuItem helpItem3 = new JMenuItem("<html><a href><font color=\"red\">About</a></font></html>");
	helpItem3.setMnemonic('A');
	helpItem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
	final JDialog dialog = new JDialog(JavaTracer.this, "About", true);
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

    public static JavaTracer getInstance() {
        if (frame == null) {
            frame = new JavaTracer();
        }
        return frame;
    }

    public static void main(String [] args) {
         SwingUtilities.invokeLater(new Runnable() {
             public void run() {
                JavaTracer.getInstance();
             }
         });
    }
}
