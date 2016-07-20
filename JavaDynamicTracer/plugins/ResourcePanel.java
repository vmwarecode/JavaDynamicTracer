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
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.Color;
import javax.swing.*;
import com.vmware.javatracer.SpringUtilities;

//
public class ResourcePanel extends JPanel {
    Vector columnNames = new Vector();
    Vector methodNames = new Vector();
    Hashtable hashtable = new Hashtable();
    ResourceTableModel model = null;
    JTable table = null;
    JTextField filterFileText = new JTextField();
    JTextField filterModeText = new JTextField();
    TableRowSorter sorter = null;

    JLabel filesReadLabel = null;
    JLabel filesWriteLabel = null;
    JLabel readModeLabel = null;
    JLabel writeModeLabel = null;

    int filesRead = 0;
    int filesWritten = 0;
    int nReads = 0;
    int nWrites = 0;

    public final static String READ_MODE = "read";
    public final static String WRITE_MODE = "write";

    public ResourcePanel() {

          setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
          columnNames.addElement("FileName");
          columnNames.addElement("Mode");
          columnNames.addElement("Count");
          model = new ResourceTableModel(columnNames, 0);

          table = new JTable(model);
          sorter = new TableRowSorter(model);
          table.setRowSorter(sorter);

          JPanel topPanel = new JPanel(new SpringLayout());
          JLabel fileLabel = new JLabel("Filter File:", SwingConstants.TRAILING);
          topPanel.add(fileLabel);
          topPanel.add(filterFileText);
	  fileLabel.setDisplayedMnemonic('l');
	  fileLabel.setLabelFor(filterFileText);
          JLabel rwLabel = new JLabel("Filter Read/Write:", SwingConstants.TRAILING);
          topPanel.add(rwLabel);
          topPanel.add(filterModeText);
	  rwLabel.setDisplayedMnemonic('R');
	  rwLabel.setLabelFor(filterModeText);
          SpringUtilities.makeCompactGrid(topPanel, 1, 4, 6, 6, 6, 6);
          add(topPanel);

          JScrollPane spane = new JScrollPane(table);
          add(spane);

          JPanel bottomPanel = new JPanel(new SpringLayout());
          filesReadLabel = new JLabel("No of distinct files read: 0", SwingConstants.TRAILING);
          bottomPanel.add(filesReadLabel);
          filesWriteLabel = new JLabel("No of distinct files written: 0", SwingConstants.TRAILING);
          bottomPanel.add(filesWriteLabel);
          readModeLabel = new JLabel("No of read operations: 0", SwingConstants.TRAILING);
          bottomPanel.add(readModeLabel);
          writeModeLabel = new JLabel("No of write operations: 0", SwingConstants.TRAILING);
          bottomPanel.add(writeModeLabel);
          SpringUtilities.makeCompactGrid(bottomPanel, 2, 2, 6, 6, 6, 6);
          add(bottomPanel);
          table.getTableHeader().setBackground(Color.CYAN);
          table.setGridColor(Color.BLUE);

          filterFileText.getDocument().addDocumentListener(
              new DocumentListener() {
                 public void changedUpdate(DocumentEvent e) {
                     updateTableFilter(filterFileText, 0);
                 }
                 public void insertUpdate(DocumentEvent e) {
                     updateTableFilter(filterFileText, 0);
                 }
                 public void removeUpdate(DocumentEvent e) {
                     updateTableFilter(filterFileText, 0);
                 }
          });
          filterModeText.getDocument().addDocumentListener(
              new DocumentListener() {
                 public void changedUpdate(DocumentEvent e) {
                     updateTableFilter(filterModeText, 1);
                 }
                 public void insertUpdate(DocumentEvent e) {
                     updateTableFilter(filterModeText, 1);
                 }
                 public void removeUpdate(DocumentEvent e) {
                     updateTableFilter(filterModeText, 1);
                 }
          });
    }

    public void updateBottomPanel() {
        filesReadLabel.setText("No of distinct files read: " + filesRead);
        filesWriteLabel.setText("No of distinct files written: " + filesWritten);
        readModeLabel.setText("No of read operations: " + nReads);
        writeModeLabel.setText("No of write operations: " + nWrites);
    }

    public void updateTableFilter(JTextField tf, int col) {
        try {
            RowFilter<ResourceTableModel, Object> rf = null;
            rf = RowFilter.regexFilter(tf.getText(), col);
            sorter.setRowFilter(rf);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
    }

    public void updateTable(String fileName, String mode) {
          Object classN = hashtable.get(fileName);
          int rowCount = model.getRowCount();
          if (classN == null) {
              Vector v = new Vector();
              v.addElement(mode);
              v.addElement(rowCount);
              hashtable.put(fileName, v);
              Vector rowV = new Vector();
              rowV.addElement(fileName);
              rowV.addElement(mode);
              rowV.addElement(new Integer(1));
              model.addRow(rowV);
          } else {
              Vector v = (Vector)hashtable.get(fileName);
              int size = v.size();
              for (int i=0; i<v.size(); i=i+2) {
                   if (mode.equals(v.elementAt(i))) {
                       int row = (Integer)v.elementAt(i+1);
                       int count = (Integer)model.getValueAt(row, 2);
                       count++;
                       model.setValueAt(new Integer(count), row, 2);
                       updateBottomPanel();
                       return;
                   }
              }
              v.addElement(mode);
              v.addElement(rowCount);
              Vector rowV = new Vector();
              rowV.addElement(fileName);
              rowV.addElement(mode);
              rowV.addElement(new Integer(1));
              model.addRow(rowV);
          }
          if (mode.equals(READ_MODE)) {
              filesRead++;
          } else if (mode.equals(WRITE_MODE)) {
              filesWritten++;
          }
          updateBottomPanel();
    }
}

class ResourceTableModel extends DefaultTableModel {

    public ResourceTableModel(Vector columnNames, int rowCount) {
         super(columnNames, rowCount);
    }
    public boolean isCellEditable(int row, int col) {
         return false;
    }
}
