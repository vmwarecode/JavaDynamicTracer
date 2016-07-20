/*
 * UI class for enabling tracing in target JVM
 * Author: Kannan Balasubramanian
 */

import java.util.Vector;
import java.util.Hashtable;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import com.vmware.javatracer.SpringUtilities;

//
public class ProfilerPanel extends JPanel {
    Vector columnNames = new Vector();
    Vector methodNames = new Vector();
    Hashtable hashtable = new Hashtable();
    ProfileTableModel model = null;
    JTable table = null;
    JTextField filterClassText = new JTextField();
    JTextField filterMethodText = new JTextField();
    TableRowSorter sorter = null;
    static int MAX_COUNT = 1000;

    public ProfilerPanel() {

          setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
          columnNames.addElement("Class");
          columnNames.addElement("Method");
          columnNames.addElement("Method Count");
          //columnNames.addElement("Graph");
          model = new ProfileTableModel(columnNames, 0);

          table = new JTable(model);
          sorter = new TableRowSorter(model);
          table.setRowSorter(sorter);
          //TableColumn graphColumn = table.getColumn("Graph");
          //graphColumn.setCellRenderer(new GraphCellRenderer());

          JPanel form = new JPanel(new SpringLayout());
          JLabel l1 = new JLabel("Filter Class:", SwingConstants.TRAILING);
          form.add(l1);
          form.add(filterClassText);
	  l1.setDisplayedMnemonic('C');
	  l1.setLabelFor(filterClassText);
          JLabel l2 = new JLabel("Filter Method:", SwingConstants.TRAILING);
          form.add(l2);
          form.add(filterMethodText);
	  l2.setDisplayedMnemonic('M');
	  l2.setLabelFor(filterMethodText);
          SpringUtilities.makeCompactGrid(form, 1, 4, 6, 6, 6, 6);

          add(form);
          JScrollPane spane = new JScrollPane(table);
          add(spane);
          table.getTableHeader().setBackground(Color.CYAN);
          table.setGridColor(Color.BLUE);

          filterClassText.getDocument().addDocumentListener(
              new DocumentListener() {
                 public void changedUpdate(DocumentEvent e) {
                     updateTableFilter(filterClassText, 0);
                 }
                 public void insertUpdate(DocumentEvent e) {
                     updateTableFilter(filterClassText, 0);
                 }
                 public void removeUpdate(DocumentEvent e) {
                     updateTableFilter(filterClassText, 0);
                 }
          });
          filterMethodText.getDocument().addDocumentListener(
              new DocumentListener() {
                 public void changedUpdate(DocumentEvent e) {
                     updateTableFilter(filterMethodText, 1);
                 }
                 public void insertUpdate(DocumentEvent e) {
                     updateTableFilter(filterMethodText, 1);
                 }
                 public void removeUpdate(DocumentEvent e) {
                     updateTableFilter(filterMethodText, 1);
                 }
          });
    }

    public void updateTableFilter(JTextField tf, int col) {
        try {
            RowFilter<ProfileTableModel, Object> rf = null;
            rf = RowFilter.regexFilter(tf.getText(), col);
            sorter.setRowFilter(rf);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
    }

    public void updateTable(String className, String methodName) {
          Object classN = hashtable.get(className);
          int rowCount = model.getRowCount();
          if (classN == null) {
              Vector v = new Vector();
              v.addElement(methodName);
              v.addElement(rowCount);
              hashtable.put(className, v);
              Vector rowV = new Vector();
              rowV.addElement(className);
              rowV.addElement(methodName);
              rowV.addElement(new Integer(1));
              model.addRow(rowV);
          } else {
              Vector v = (Vector)hashtable.get(className);
              int size = v.size();
              for (int i=0; i<v.size(); i=i+2) {
                   if (methodName.equals(v.elementAt(i))) {
                       int row = (Integer)v.elementAt(i+1);
                       int count = (Integer)model.getValueAt(row, 2);
                       try {
                            count++;
                            model.setValueAt(new Integer(count), row, 2);
                            //GraphCellRenderer comp = (GraphCellRenderer)table.getCellRenderer(row, 3);
                            //comp.setValue(count);
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                       return;
                   }
              }
              v.addElement(methodName);
              v.addElement(rowCount);
              Vector rowV = new Vector();
              rowV.addElement(className);
              rowV.addElement(methodName);
              rowV.addElement(new Integer(1));
              model.addRow(rowV);
          }
    }
}

class Graph {

}

class GraphCellRenderer extends DefaultTableCellRenderer {

    int count = 0;
    public boolean isOpaque() {
        return true;
    }
    public void paintComponent1 (Graphics g) {
        double width = ((double)count * (double)getWidth()) / (double)ProfilerPanel.MAX_COUNT;
System.out.println(count + " : " + getWidth() + " : " + width);
        g.setColor(Color.RED);
        g.fillRect(0, 0, (int)width, getHeight());
    }

    public void setValue(int c) {
        count = c;
        repaint();
    }
}

class ProfileTableModel extends DefaultTableModel {

    public ProfileTableModel(Vector columnNames, int rowCount) {
         super(columnNames, rowCount);
    }
    public boolean isCellEditable(int row, int col) {
         return col == 2;
    }
}
