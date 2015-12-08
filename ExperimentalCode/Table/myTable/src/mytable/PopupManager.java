 
package mytable;

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;


class PopupManager extends MouseAdapter implements ActionListener {
    JTable table;
    JPopupMenu popupMenu;
    JDialog dialog;
    RelocateFrame relocateFrame;
    //JOptionPane dialog;
  
    public PopupManager(JTable table) {
        this.table = table;
        this.table.addMouseListener(this);
        initPopup();
        initDialog();
        
    }
    
      
    private void initPopup() {
        popupMenu = new JPopupMenu();

        JMenuItem menuItem_setprime = new JMenuItem("Set Prime"); 
        JMenuItem menuItem_relocate = new JMenuItem("Relocate..");
        JMenuItem menuItem_depricate = new JMenuItem("Depricate");
        JMenuItem menuItem_edit = new JMenuItem("Edit..");
        JMenuItem menuItem_create = new JMenuItem("Create..");
        JMenuItem menuItem_move = new JMenuItem("Move..");

        popupMenu.add(menuItem_setprime);
        popupMenu.add(menuItem_relocate);
        popupMenu.add(menuItem_depricate);
        popupMenu.add(menuItem_edit);
        popupMenu.add(menuItem_create);
        popupMenu.add(menuItem_move);
                
        menuItem_setprime.addActionListener(this);
        menuItem_relocate.addActionListener(this);
        menuItem_depricate.addActionListener(this);
        menuItem_edit.addActionListener(this);
        menuItem_create.addActionListener(this);
        menuItem_move.addActionListener(this);               
        
        // this will enable selection of all columns
        //this.table.setComponentPopupMenu(popupMenu); 
    }
  
    
    // TODO: add more dialogs
    private void initDialog() {
        
        relocateFrame = new RelocateFrame();
        
        Frame owner = (Frame)table.getTopLevelAncestor();
        dialog = new JDialog(owner, "title", false);
        //dialog = new JOptionPane();
        JList list = new JList(new DefaultListModel());
        dialog.add(list);
    }
    
    
    /*
     * right click a row for a 'popup menu'.
     */
    
    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("Mouse Pressed.");
        
        Point p = e.getPoint();
        final int row = table.rowAtPoint(p);
        final int col = table.columnAtPoint(p);
        int selectedRow = table.getSelectedRow();
        int selectedCol = table.getSelectedColumn();
        
        // close the opened dialog
        // Saiful: it should be "modal".
        if(dialog.isShowing())
            dialog.dispose();
        
        
        if(popupMenu.isVisible())
            popupMenu.setVisible(false);
        
        // Update the current selection for correct popupMenu behavior
        // in case a new selection is made with the right mouse button.
        if(row != selectedRow || col != selectedCol) {
            EventQueue.invokeLater(new Runnable() {
                
                @Override
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
    
    
    /*
     * Menu item selected.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
     
        // TODO: 
        //showDialog();  
        
        if("Set Prime".equals(e.getActionCommand())){
            JOptionPane.showMessageDialog(null, "Selected Item: " + e.getActionCommand());
        }
        if("Relocate..".equals(e.getActionCommand())){
            //JOptionPane.showMessageDialog(null, "Selected Item: " + e.getActionCommand());
            relocateFrame.setVisible(true);
        }
        
        if("Depricate".equals(e.getActionCommand())){
            JOptionPane.showMessageDialog(null, "Selected Item: " + e.getActionCommand());
        }
        if("Edit..".equals(e.getActionCommand())){
            JOptionPane.showMessageDialog(null, "Selected Item: " + e.getActionCommand());
        }
        if("Create..".equals(e.getActionCommand())){
            JOptionPane.showMessageDialog(null, "Selected Item: " + e.getActionCommand());
        }
        if("Move..".equals(e.getActionCommand())){
            JOptionPane.showMessageDialog(null, "Selected Item: " + e.getActionCommand());
        }
        
    }
  
    
    private void showDialog() {
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();
        Rectangle r = table.getCellRect(row, col, true);
        Point p = r.getLocation();
        SwingUtilities.convertPointToScreen(p, table);

         
        JList list = (JList) dialog.getContentPane().getComponent(0);
        DefaultListModel model = (DefaultListModel) list.getModel();
        model.removeAllElements();
        model.addElement("You selected row " + row);
        model.addElement("You selected column " + col);
        dialog.pack();
        

        dialog.setLocation(p.x, p.y + r.height);
        dialog.setVisible(true);
    }
    
}

