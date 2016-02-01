 
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

 
class PhaseTablePopupManager implements ActionListener {
    
    JTable table;
    JPopupMenu popupMenu;
    PhaseEditDialog editDialog;
    
 
    public PhaseTablePopupManager(JTable phaseTable) {
        table = phaseTable;
        setPopupMenuVisualAttributes();

        editDialog = new PhaseEditDialog();
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
        
        /*
        // TODO: get the selected SeiesEvent from HypoTextViewTopComponent   
        String evid = Global.getSelectedSeisEvent().getEvid().toString();
        String hypid = table.getValueAt(selectedRow, 9).toString();
        String time = table.getValueAt(selectedRow, 1).toString();
        String coord = table.getValueAt(selectedRow, 2).toString() + " " + table.getValueAt(selectedRow, 3).toString();
        String depth = table.getValueAt(selectedRow, 4).toString();
        // TODO: get HypocentresList from HypoTextViewTopComponent   
        String prime = Global.getHypocentresList().getHypocentres().get(selectedRow).getIsPrime().toString();     
        */
        // Debug 
        //System.out.println("Selected row/col"+ " "+ selectedRow+ "  " + selectedColumn);
        //Object selectedCellValue=table.getValueAt(selectedRow, selectedColumn);
        //System.out.println("selectedCellValue "+" "+selectedCellValue);
 
        // Location of the dialog
        // TODO: not working
        Rectangle r = table.getCellRect(selectedRow, selectedColumn, true);
        Point p = r.getLocation();
       
        if("Edit..".equals(e.getActionCommand())){
            
             System.out.println("ROW SELECTION EVENT. ");

                System.out.print(String.format("Lead: %d, %d. ",
                        table.getSelectionModel().getLeadSelectionIndex(),
                        table.getColumnModel().getSelectionModel().
                        getLeadSelectionIndex()));
                System.out.print("Rows:");
                for (int c : table.getSelectedRows()) {
                    System.out.println(String.format(" %d", c));
                }
                System.out.print(". Columns:");
                for (int c : table.getSelectedColumns()) {
                    System.out.print(String.format(" %d", c));
                }
                System.out.print(".\n\n");
                
        }
       
        
    }
    
    
    private void setPopupMenuVisualAttributes() {
        popupMenu = new JPopupMenu();
                    
        JMenuItem menuItem_edit = new JMenuItem("Edit.."); 
        menuItem_edit.setBackground(new Color(218,83,44));
        menuItem_edit.setForeground(Color.WHITE);
        menuItem_edit.setFont(new Font("Sans-serif", Font.PLAIN, 14));
                
        popupMenu.add(menuItem_edit);
        
        menuItem_edit.addActionListener(this);
               
        // this will enable selection of all columns
        this.table.setComponentPopupMenu(popupMenu); 
    }
     
        
}

