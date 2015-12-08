package mytable;

import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
  

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