 
package uk.ac.isc.textview;

 
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import uk.ac.isc.seisdata.Global;

 
class HypoTablePopupManager implements ActionListener {
    
    JTable table;
    JPopupMenu popupMenu;

    HypoTableRelocateDialog relocateDialog;
    HypoEditDialog editDialog;
    
  
    public HypoTablePopupManager(JTable hypoTable) {
        table = hypoTable;
        //table.addMouseListener(this);
                
        setPopupMenuVisualAttributes();
        
        relocateDialog = new HypoTableRelocateDialog();
        editDialog = new HypoEditDialog();
    }

    
    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    
    /*
     * Menu item selected.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(Global.debugAt());
        
        // Selected row values
        int selectedRow = table.getSelectedRow();
        int selectedColumn = table.getSelectedColumn();
        
        // TODO: get the selected SeiesEvent from HypoTextViewTopComponent   
        String evid = Global.getSelectedSeisEvent().getEvid().toString();
        String hypid = table.getValueAt(selectedRow, 9).toString();
        String time = table.getValueAt(selectedRow, 1).toString();
        String coord = table.getValueAt(selectedRow, 2).toString() + " " + table.getValueAt(selectedRow, 3).toString();
        String depth = table.getValueAt(selectedRow, 4).toString();
        // TODO: get HypocentresList from HypoTextViewTopComponent   
        String prime = Global.getHypocentresList().getHypocentres().get(selectedRow).getIsPrime().toString();     
        
        // Debug 
        //System.out.println("Selected row/col"+ " "+ selectedRow+ "  " + selectedColumn);
        //Object selectedCellValue=table.getValueAt(selectedRow, selectedColumn);
        //System.out.println("selectedCellValue "+" "+selectedCellValue);
  
        // Location of the dialog
        // TODO: not working
        Rectangle r = table.getCellRect(selectedRow, selectedColumn, true);
        Point p = r.getLocation();

        if("Set Prime".equals(e.getActionCommand())){
            JOptionPane.showMessageDialog(null, "Selected Item: " + e.getActionCommand());
        }
        if("Relocate..".equals(e.getActionCommand())){
            relocateDialog.setLocation(p.x, p.y + r.height);
            relocateDialog.showHypoTableRelocateDialog(evid, hypid, time, coord, depth, prime);
        }
        if("Depricate".equals(e.getActionCommand())){
            JOptionPane.showMessageDialog(null, "Selected Item: " + e.getActionCommand());
        }
        if("Edit..".equals(e.getActionCommand())){
            editDialog.setLocation(p);
            editDialog.showHypoEditDialog(evid, hypid, time, coord, depth, prime);         
        }
        if("Create..".equals(e.getActionCommand())){
            JOptionPane.showMessageDialog(null, "Selected Item: " + e.getActionCommand());
        }
        if("Move..".equals(e.getActionCommand())){
            JOptionPane.showMessageDialog(null, "Selected Item: " + e.getActionCommand());
        }
        
    }
    
    
    private void setPopupMenuVisualAttributes() {
        popupMenu = new JPopupMenu();
                    
        JMenuItem menuItem_setprime = new JMenuItem("Set Prime"); 
        menuItem_setprime.setBackground(new Color(218,83,44));
        menuItem_setprime.setForeground(Color.WHITE);
        menuItem_setprime.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_relocate = new JMenuItem("Relocate..");
        menuItem_relocate.setBackground(new Color(218,83,44));
        menuItem_relocate.setForeground(Color.WHITE);
        menuItem_relocate.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_depricate = new JMenuItem("Depricate");
        menuItem_depricate.setBackground(new Color(218,83,44));
        menuItem_depricate.setForeground(Color.WHITE);
        menuItem_depricate.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_edit = new JMenuItem("Edit..");
        menuItem_edit.setBackground(new Color(218,83,44));
        menuItem_edit.setForeground(Color.WHITE);
        menuItem_edit.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_create = new JMenuItem("Create..");
        menuItem_create.setBackground(new Color(218,83,44));
        menuItem_create.setForeground(Color.WHITE);
        menuItem_create.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_move = new JMenuItem("Move..");
        menuItem_move.setBackground(new Color(218,83,44));
        menuItem_move.setForeground(Color.WHITE);
        menuItem_move.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        
        popupMenu.add(menuItem_setprime);
        //popupMenu.addSeparator();
        popupMenu.add(menuItem_relocate);
        //popupMenu.addSeparator();
        popupMenu.add(menuItem_depricate);
        //popupMenu.addSeparator();
        popupMenu.add(menuItem_edit);
        //popupMenu.addSeparator();
        popupMenu.add(menuItem_create);
        //popupMenu.addSeparator();
        popupMenu.add(menuItem_move);
        
       
        menuItem_setprime.addActionListener(this);
        menuItem_relocate.addActionListener(this);
        menuItem_depricate.addActionListener(this);
        menuItem_edit.addActionListener(this);
        menuItem_create.addActionListener(this);
        menuItem_move.addActionListener(this);
        
        // this will enable selection of all columns
        this.table.setComponentPopupMenu(popupMenu); 
    }
     
        
}

