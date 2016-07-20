package com.vmware.javatracer;

/*
 * UI class for enabling tracing in target JVM
 * Author: Kannan Balasubramanian
 */

import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.InputMap;
import javax.swing.ActionMap;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ServiceLoader;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.ServiceConfigurationError;
import java.io.IOException;

//TracePanel is the panel that wraps InputPanel and LogPanel
//InputPanel: panel that takes inputs from user on the required information before attaching to a running VM
//Logpanel:
public class TracePanel extends JPanel {
    InputPanel inputPanel = null;
    LogPanel logPanel = null;
    JTabbedPane tabPane = null;
    public static StringBuffer [] bscripts = null;
    OptionsDialog optionsDialog = null;
    Vector<BtracePlugin> pluginVector = new Vector<BtracePlugin>();
    Vector<JCheckBox> checkVector = new Vector<JCheckBox>();

    public TracePanel(Window w) {
        inputPanel = new InputPanel(w);
        logPanel = new LogPanel();
        JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputPanel, logPanel);
        tabPane = new JTabbedPane();
        tabPane.add("Trace", splitpane);
	String pluginPath = System.getProperty("PluginPath");
	if (pluginPath != null) {
	    initPluginService(pluginPath);
	}
        add(tabPane);
    }

    public JComponent getPane() {
        return tabPane;
    }

    public static void handleKey(JComponent c, Runnable task, int key, int modifiers) {
        final Runnable runnable = task;
        Action keyAction = new AbstractAction() {
               public void actionPerformed(ActionEvent e) {
                   runnable.run();
               }
        };
        KeyStroke keyStroke = KeyStroke.getKeyStroke(key, modifiers, false);
        InputMap inputMap = c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = c.getActionMap();
        if (inputMap != null && actionMap != null) {
            inputMap.put(keyStroke, "action");
            actionMap.put("action", keyAction);
        }
    }

    //A public method added here incase someone wishes to update the jvm ids refreshed whenever they want to
    public void refreshPids() {
        inputPanel.refreshPids();
    }

    private void initPluginService(String pluginPath) {
        if (pluginPath.length() > 0) {
            try {
                ClassLoader pluginCL = new URLClassLoader(pathToURLs(pluginPath));
                ServiceLoader<BtracePlugin> plugins = ServiceLoader.load(BtracePlugin.class, pluginCL);
                // validate all plugins
		int count=0;
    	        for (BtracePlugin p : plugins) {
                     System.out.println("Plugin " + p.getClass() + " loaded.");
                     tabPane.add(p.getTitle(), p.getPanel());
                     CodeTracer.addListener(p.getListener());
		     checkVector.addElement(new JCheckBox(p.getTitle(), true));
		     pluginVector.addElement(p);
		     count++;
                }
		bscripts = new StringBuffer[count];
		count=0;
    	        for (BtracePlugin p : plugins) {
                     bscripts[count] = p.getBtraceScript();
                     count++;
                }
            } catch (ServiceConfigurationError e) {
                    e.printStackTrace();
            } catch (MalformedURLException e) {
                    e.printStackTrace();
            }
        }
    }

   /**
     * Utility method for converting a search path string to an array
     * of directory and JAR file URLs.
     *
     * @param path the search path string
     * @return the resulting array of directory and JAR file URLs
     */
    private URL[] pathToURLs(String path) throws MalformedURLException {
	String[] names = path.split(File.pathSeparator);
	URL[] urls = new URL[names.length];
        int count = 0;
        for (String f : names) {
            URL url = fileToURL(new File(f));
	    urls[count++] = url;
	}
        return urls;
    }

    /**
     * Returns the directory or JAR file URL corresponding to the specified
     * local file name.
     *
     * @param file the File object
     * @return the resulting directory or JAR file URL, or null if unknown
     */
    private URL fileToURL(File file) throws MalformedURLException {
	String name;
	try {
            name = file.getCanonicalPath();
	} catch (IOException e) {
            name = file.getAbsolutePath();
	}
        name = name.replace(File.separatorChar, '/');
	if (!name.startsWith("/")) {
            name = "/" + name;
	}
        // If the file does not exist, then assume that it's a directory
	if (!file.isFile()) {
            name = name + "/";
	}
        return new URL("file", "", name);
    }

    public void showOptionsDialog() {
        if (optionsDialog == null) {
            JCheckBox [] checks = new JCheckBox [checkVector.size()];
	    for (int i=0; i<checks.length; i++) {
		 checks[i] = checkVector.elementAt(i);
	    }
            optionsDialog = new OptionsDialog(checks);
	}
	optionsDialog.setVisible(true);
	if (optionsDialog.isSuccess()) {
	    int tabCount = tabPane.getTabCount();
	    for (int i=1; i<tabCount; i++) {
                 tabPane.remove(1);
	    }
	    int count=0;
	    for (int i=0; i<checkVector.size(); i++) {
                 JCheckBox checkBox = checkVector.elementAt(i);
		 BtracePlugin p = pluginVector.elementAt(i);
		 if (checkBox.isSelected()) {
                     tabPane.add(p.getTitle(), p.getPanel());
		     count++;
		 }
	    }
	    bscripts = new StringBuffer[count];
	    count=0;
	    for (int i=0; i<checkVector.size(); i++) {
                 JCheckBox checkBox = checkVector.elementAt(i);
		 BtracePlugin p = pluginVector.elementAt(i);
		 if (checkBox.isSelected()) {
                     bscripts[count] = p.getBtraceScript();
		     count++;
		 }
	    }
	}
    }
}

//A wrapper class for holding the pid and processname for each VM running on the system. The object of this class is added as an element to pidCombo
class VMItem {

    private int pid = -1;
    private String processName = null;
    public VMItem(int id, String pName) {
       pid = id;
       processName = pName;
    }
    public int getPid() {
       return pid;
    }
    public String getPidString() {
       return String.valueOf(pid);
    }
    public String getProcessName() {
       return processName;
    }
    public String toString() {
       return pid + " : " + processName;
    }
}
