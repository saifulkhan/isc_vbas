
package uk.ac.isc.agencyrecview;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


/**
 * just a cell renderer, need be revisited
 * @author hui
 */
class BackgroundRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
         //Cells are by default rendered as a JLabel.
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        //Get the status for the current row.
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        if(tableModel.getRowCount()>0)
        {
            if ((Integer)table.getValueAt(row,4) < 1) {
                l.setBackground(new Color(144,238,144));
            } else if ((Integer)table.getValueAt(row,4) < 2) {
                l.setBackground(new Color(245,222,179));
            }
            else {
                l.setBackground(new Color(135,206,250));
            }
        }
        else
        {
            l.setBackground(new Color(196,196,196));
        }
        //Return the JLabel which renders the cell.
        return l;
    }
    
}
