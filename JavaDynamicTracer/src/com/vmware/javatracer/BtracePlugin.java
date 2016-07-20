package com.vmware.javatracer;

/*
 * Summary: Class used for adding plugins to JavaTracer
 * Author: Kannan
 * Last Update Date: 14 Dec 2009
 */

import javax.swing.JPanel;

//Abstract class that needs to be extended by plugins 
public abstract class BtracePlugin {
	public abstract BtraceListener getListener();
	public abstract StringBuffer getBtraceScript();
	public abstract JPanel getPanel();
	public abstract String getTitle();
}
