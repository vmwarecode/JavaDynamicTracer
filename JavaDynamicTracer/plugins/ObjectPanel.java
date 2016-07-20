
/*
 * Summary: Class for tracking all new objects constructed
 * Author: Kannan Balasubramanian
 */

import java.util.Vector;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.Color;
import com.vmware.javatracer.SpringUtilities;

public class ObjectPanel extends JPanel {
    Vector columnNames = new Vector();
    Vector methodNames = new Vector();
    HashMap<String, Integer> map = new HashMap<String, Integer>();
    ObjectTableModel model = null;
    JTable table = null;
    JTextField filterObjectText = new JTextField();
    TableRowSorter sorter = null;

    JLabel objectCountLabel = null;
    int objectCount = 0;

    public ObjectPanel() {
          setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
          columnNames.addElement("Objects");
          columnNames.addElement("Count");
          model = new ObjectTableModel(columnNames, 0);

          table = new JTable(model);
          sorter = new TableRowSorter(model);
          table.setRowSorter(sorter);

          JPanel topPanel = new JPanel(new SpringLayout());
          JLabel objectLabel = new JLabel("Filter Object:", SwingConstants.TRAILING);
          topPanel.add(objectLabel);
          topPanel.add(filterObjectText);
          SpringUtilities.makeCompactGrid(topPanel, 1, 2, 6, 6, 6, 6);
          add(topPanel);

          JScrollPane spane = new JScrollPane(table);
          add(spane);

          JPanel bottomPanel = new JPanel(new SpringLayout());
          objectCountLabel = new JLabel("No of objects created: 0", SwingConstants.TRAILING);
          bottomPanel.add(objectCountLabel);
          SpringUtilities.makeCompactGrid(bottomPanel, 1, 1, 6, 6, 6, 6);
          add(bottomPanel);
          table.getTableHeader().setBackground(Color.CYAN);
          table.setGridColor(Color.BLUE);

          filterObjectText.getDocument().addDocumentListener(
              new DocumentListener() {
                 public void changedUpdate(DocumentEvent e) {
                     updateTableFilter(filterObjectText, 0);
                 }
                 public void insertUpdate(DocumentEvent e) {
                     updateTableFilter(filterObjectText, 0);
                 }
                 public void removeUpdate(DocumentEvent e) {
                     updateTableFilter(filterObjectText, 0);
                 }
          });
    }

    //Method for updating the total object count in the bottom panel
    public void updateBottomPanel() {
        objectCountLabel.setText("No of objects created: " + objectCount);
    }

    public void updateTableFilter(JTextField tf, int col) {
        try {
             RowFilter<ObjectTableModel, Object> rf = null;
             rf = RowFilter.regexFilter(tf.getText(), col);
             sorter.setRowFilter(rf);
        } catch (java.util.regex.PatternSyntaxException e) {
             e.printStackTrace();
        }
    }

    //Method to update table with every new object constructed in the target VM
    public void updateTable(String objectName) {
          Integer row = map.get(objectName);
          int rowCount = model.getRowCount();
          if (row == null) {
              map.put(objectName, rowCount);
              Vector rowV = new Vector();
              rowV.addElement(objectName);
              rowV.addElement(new Integer(1));
              model.addRow(rowV);
          } else {
              int count = (Integer)model.getValueAt(row, 1) + 1;
              model.setValueAt(count, row, 1);
          }
          objectCount++;
          updateBottomPanel();
    }
}

class ObjectTableModel extends DefaultTableModel {

    public ObjectTableModel(Vector columnNames, int rowCount) {
         super(columnNames, rowCount);
    }
    public boolean isCellEditable(int row, int col) {
         return false;
    }
    public Class getColumnClass(int columnIndex) {
         return columnIndex == 0 ? String.class : Integer.class;
    }
}
