
package uk.ac.isc.textview;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * This renderer provides the function of rendering buttons in table cells
 * with the renderer, the button can be inserted into Jtable
 * Not implemented yet
 * @author hui
 */
public class JTableButtonRenderer implements TableCellRenderer {

    /*the three reference variables for the use of the actionlistener*/
    //private JTable lctable = null;
    //private int lcRow;
    //private int lcColumn;
    private JButton button;
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        button = (JButton) value;
        ImageIcon img = new ImageIcon(getClass().getClassLoader().getResource("uk/ac/isc/textview/EditPic.png"));
        button.setIcon(img);
        
        //this.lctable = table;
        //this.lcRow = row;
        //this.lcColumn = column;
        
        return button;
    }
    
}
