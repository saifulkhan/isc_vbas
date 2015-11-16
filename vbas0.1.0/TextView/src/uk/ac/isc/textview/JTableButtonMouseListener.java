
package uk.ac.isc.textview;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JTable;

/**
 * Not implemented yet
 * @author hui
 */
class JTableButtonMouseListener extends MouseAdapter {

    private final JTable table;
    
    public JTableButtonMouseListener(JTable phasesTable) {
        this.table = phasesTable;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int column = table.getColumnModel().getColumnIndexAtX(e.getX());
        int row    = e.getY()/table.getRowHeight(); 
        
        if(column == 0)
        {
            Object value = table.getValueAt(row,column);
            if (value instanceof JButton) {
                /**Here is the code for popup a dialog to edit the phase reading*/
                //((JButton)value).doClick(); 
                System.out.println("JTableButtonMouseListener: Mouse Clicked." + this.table.getValueAt(row, 2));
            }
        }
    }
    
}
