package com.vmware.javatracer;

/*
 * Summary: Customized class for displaying all registered component listeners in a tool tip
 * Author: Kannan Balasubramanian
 */

import javax.swing.JWindow;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import java.awt.Window;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.FocusAdapter;
import java.awt.Color;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

//Creating a customized tooltip window that will display all methods or fields of a class
public class ToolTipList extends JWindow {

   private ComponentListPanel panel = null;
   private ToolTipListener listener = null;

   public ToolTipList (Window w) {
        super(w);
        panel = new ComponentListPanel(this);
        setContentPane(panel);
	//Make this window focusable to enable keyboard navigation on its methods or fields list
        setFocusableWindowState(true);
        this.setSize(40, 50);
   }

   //Method for registering listener to the tooltip
   public void addToolTipListener(ToolTipListener l) {
       listener = l;
   }

   //Notify all the registered listeners of the selection made in the JList (containing methods or fields)
   public void notifyListeners(ToolTipEvent event) {
       listener.selectionChanged(event);
   }

   //Method to hide this window
   public void hideToolTip() {
       setVisible(false);
   }

   /*
    * className: name of the fully qualified class whose methods or fields list are to be displayed
    * pt: point(x,y) on the screen where the tooltip window will be displayed
    * partialName: partial or complete name of a method/field
    * bFlag: true => methodname, false => fieldname
    */
   public void showToolTip(String className, Point pt, String partialName, boolean bFlag) {

        final String cName = className;
        final String name = partialName;
        final Point point = pt;
        final boolean flag = bFlag;

        //Make sure the window is shown on EDT
        SwingUtilities.invokeLater(new Runnable() {
           public void run() {
               if (flag) {
                   ToolTipList.this.panel.showMethodsList(cName, name);
               } else {
                   ToolTipList.this.panel.showFieldsList(cName, name);
               }
               ToolTipList.this.pack();
               ToolTipList.this.setLocation(point.x, point.y);
               ToolTipList.this.setVisible(true);
           }
        });
   }

   //Just a test method to run it as a standalone
   public static void main(String [] args) {
        ToolTipList test = new ToolTipList(null);
        //test.showToolTip(new javax.swing.JComboBox(), new Point(50, 50));
   }
}

/*
 * ComponentListPanel: Class for displaying all listeners registered with a component in a JList 
 */
class ComponentListPanel extends JPanel {

   private DefaultListModel model = new DefaultListModel();
   private JList list = null;
   //type: variable indicating method or field type
   private int type = -1;
   private ToolTipList parent = null;

   public ComponentListPanel(ToolTipList p) {
      this.parent = p;
   }

   //Method for displaying all the methods of a given class in the tooltip window
   //partialFieldName: If this is empty, all the methods are shown. If not all the methods matching the given pattern is displayed
   public void showMethodsList(String className, String partialMethodName) {
      if (className == null) {
          return;
      }
      type = ToolTipEvent.METHOD;
      createList();
      DefaultListModel model = (DefaultListModel)list.getModel();
      model.clear();

      Method [] m = null;
      try {
           Class c = Class.forName(className);
           if(c == null) {
              return;
           }
	   //get all the methods of the class (private/protected/public/package)
           m = c.getDeclaredMethods();
      } catch(Exception e) {
          e.printStackTrace();
          return;
      }
      boolean bAllMethods = false;
      if (partialMethodName == null || partialMethodName.trim().length() == 0) {
          bAllMethods = true;
      }
      String methodName = null;
      for (int i=0; i<m.length; i++) {
           methodName = m[i].getName() + "(";
           Class [] carray = m[i].getParameterTypes();
           for (int j=0; j<carray.length; j++) {
                if (j > 0) {
                    methodName = methodName + ", ";
                }
                methodName = methodName + carray[j];
           }
           methodName = methodName + ")";

           if(bAllMethods) {
              model.addElement(methodName);
           } else {
             if(methodName.startsWith(partialMethodName)) {
                model.addElement(methodName);
             } 
           }
      }
      //Set focus on the list to enable keyboard navigation
      list.requestFocus();
   }

   //Method for displaying all the fields of a given class in the tooltip window
   //partialFieldName: If this is empty, all the fields are shown. If not all the fields matching the given pattern is displayed
   public void showFieldsList(String className, String partialFieldName) {
      if (className == null) {
          return;
      }
      type = ToolTipEvent.FIELD;
      createList();
      DefaultListModel model = (DefaultListModel)list.getModel();
      model.clear();

      Field [] f = null;
      try {
           Class c = Class.forName(className);
           if(c == null) {
              return;
           }
	   //get all the fields of the class (private/protected/public/package)
           f = c.getDeclaredFields();
      } catch(Exception e) {
          e.printStackTrace();
          return;
      }
      boolean bAllFields = false;
      if (partialFieldName == null || partialFieldName.trim().length() == 0) {
          bAllFields = true;
      }
      String fieldName = null;
      for (int i=0; i<f.length; i++) {
           fieldName = f[i].getName() + " ( ";
           fieldName = fieldName + f[i].getType();
           fieldName = fieldName + " )";
           if(bAllFields) {
              model.addElement(fieldName);
           } else {
             if(fieldName.startsWith(partialFieldName)) {
                model.addElement(fieldName);
             } 
           }
      }
      //Set focus on the list to enable keyboard navigation
      list.requestFocus();
   }

   //Method to create JList will al necessary keyboard, mouse and focus listeners
   public void createList() {
      if (list == null) {
          list = new JList(model);

	  //add focus listener to the list to hide tooltip window whenever the focus goes out of it
          list.addFocusListener(new FocusAdapter() {
              public void focusLost(FocusEvent e) {
                 parent.hideToolTip();
              }
          });
          list.setBackground(new Color(200, 200, 200));
          add(new JScrollPane(list));

	  //Add mouse listener to the list to notify the registered listeners of the selected item on double click followed by hiding the tooltip window
          list.addMouseListener(new MouseAdapter() {
              public void mouseClicked(MouseEvent e) {
                  if (e.getClickCount() == 2) {
                      parent.notifyListeners(new ToolTipEvent(list.getSelectedValue(), type));
                      parent.hideToolTip(); 
                  }
              }
          });

	  //Add key listener to the list to handle the following cases:
	  //ENTER key on an item in the list will notify the registered listeners with the selected item and hide the tooltip window
	  //ESCAPE key on any item in the list will cancel the action and hide the tooltip window
          list.addKeyListener(new KeyAdapter() {
              public void keyPressed(KeyEvent e) {
                  if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                      parent.notifyListeners(new ToolTipEvent(list.getSelectedValue(), type));
                      parent.hideToolTip(); 
                  } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                      parent.hideToolTip(); 
                  }
              }
          });
      }

   }
}

//Listener for monitoring selection changes made to the JList
interface ToolTipListener {

    public void selectionChanged(ToolTipEvent event);
}

//Event class wrapping information on the object type (METHOD or FIELD) and its value
class ToolTipEvent {

    public final static int METHOD=1;
    public final static int FIELD=2;
    private Object value = null;
    private int type = -1;
    public ToolTipEvent(Object obj, int objtype) {
       value = obj;
       type = objtype;
    }
    public String getValue() {
       return value.toString();
    }
    public int getType() {
       return type;
    }
}
