package mytable;

import javax.swing.SwingUtilities;

/*
public class App {
    
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
         SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MyFrame();
            }
        });
    }
}
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
  
public class App {
    private JScrollPane getContent() {
        JTable table = new JTable(getModel());
        Dimension d = table.getPreferredSize();
        d.width = 360;
        table.setPreferredScrollableViewportSize(d);
        table.setCellSelectionEnabled(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        new PopupManager(table);
        return new JScrollPane(table);
    }
  
    private AbstractTableModel getModel() {
        return new AbstractTableModel() {
            public int getColumnCount() { return 3; }
            public int getRowCount() { return 4;}
            public Object getValueAt(int row, int col) {
                return String.valueOf(row) + col;
            }
        };
    }
  
    public static void main(String[] args) {
        JFrame f = new JFrame("middle column popup");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane(new App().getContent());
        f.pack();
        f.setLocation(200,200);
        f.setVisible(true);
    }
}
  
class PopupManager extends MouseAdapter implements ActionListener {
    JTable table;
    JPopupMenu popupMenu;
    JDialog dialog;
  
    public PopupManager(JTable table) {
        this.table = table;
        this.table.addMouseListener(this);
        initPopup();
        initDialog();
    }
  
    public void actionPerformed(ActionEvent e) {
        showDialog();
    }
  
    private void showDialog() {
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();
        Rectangle r = table.getCellRect(row, col, true);
        JList list = (JList)dialog.getContentPane().getComponent(0);
        DefaultListModel model = (DefaultListModel)list.getModel();
        model.removeAllElements();
        model.addElement("You selected row " + row);
        model.addElement("You selected column " + col);
        dialog.pack();
        Point p = r.getLocation();
        SwingUtilities.convertPointToScreen(p, table);
        dialog.setLocation(p.x, p.y+r.height);
        dialog.setVisible(true);
    }
  
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        final int row = table.rowAtPoint(p);
        final int col = table.columnAtPoint(p);
        int selectedRow = table.getSelectedRow();
        int selectedCol = table.getSelectedColumn();
        if(dialog.isShowing())
            dialog.dispose();
        if(popupMenu.isVisible())
            popupMenu.setVisible(false);
        // Update the current selection for correct popupMenu behavior
        // in case a new selection is made with the right mouse button.
        if(row != selectedRow || col != selectedCol) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    table.changeSelection(row, col, true, false);
                }
            });
        }
        // Specify the condition(s) you want for popupMenu display.
        // For Example: show popupMenu only for view column index 1.
        if(row != -1 && col == 1) {
            if(SwingUtilities.isRightMouseButton(e)) {
                Rectangle r = table.getCellRect(row, col, false);
                popupMenu.show(table, r.x, r.y+r.height);
            } else {
                e.consume();
            }
        }
    }
  
    private void initPopup() {
        popupMenu = new JPopupMenu();
        JMenuItem item = new JMenuItem("item 1");
        item.addActionListener(this);
        popupMenu.add(item);
    }
  
    private void initDialog() {
        Frame owner = (Frame)table.getTopLevelAncestor();
        dialog = new JDialog(owner, "title", false);
        JList list = new JList(new DefaultListModel());
        dialog.add(list);
    }
}