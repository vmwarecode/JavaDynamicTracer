/*
 * Summary: A class library of methods 
 * Author: Kannan Balasubramanian
 */

package com.vmware.javatracer;

import javax.swing.JComponent;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

public class Utilities {


    //A method for taking an action when a Esc key is pressed in a dialog
    public static void handleEscKey(JComponent c, Runnable task) {
        final Runnable runnable = task;
        Action cancelKeyAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                runnable.run();
            }
        };
        KeyStroke cancelKeyStroke =
            KeyStroke.getKeyStroke((char)KeyEvent.VK_ESCAPE, 0, false);
        InputMap inputMap =
            c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = c.getActionMap();

        if (inputMap != null && actionMap != null) {
            inputMap.put(cancelKeyStroke, "cancel");
            actionMap.put("cancel", cancelKeyAction);
        }
    }

    //A method that returns all the super classes of a given class name in a tree form using html break tag
    public static String getClassHierarchy(Class cName) {
          String allClasses = cName.toString();
	  while ((cName = cName.getSuperclass()) != null) {
               allClasses = allClasses + ("<br/>" + cName.toString());
	  }
          return allClasses;
    }
}
