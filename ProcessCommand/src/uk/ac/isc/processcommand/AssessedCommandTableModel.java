package uk.ac.isc.processcommand;

import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import uk.ac.isc.seisdata.AssessedCommand;
import uk.ac.isc.seisdata.VBASLogger;

public class AssessedCommandTableModel extends AbstractTableModel {

    private final String[] columnNames = {
        "ID",
        "Selected Commands",
        "Type",
        "Analyst",
        "Report"
    };

    private final Class[] columns = new Class[]{
        String.class,
        String.class,
        String.class,
        String.class,
        JButton.class
    };

    public final Object[] longValues = {
        new Integer(999999999),
        new String(new char[30]),
        new String(new char[30]),
        new String(new char[30]),
        new JButton()
    };
    
    private final ArrayList<AssessedCommand> assessedCommandList;

    AssessedCommandTableModel(ArrayList<AssessedCommand> assessedCommandList) {
        this.assessedCommandList = assessedCommandList;
    }

    @Override
    public int getRowCount() {
        return assessedCommandList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    // overide it for setting values in each row and each column
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Object retObject = null;

        switch (columnIndex) {

            case 0:
                retObject = assessedCommandList.get(rowIndex).getId();
                break;
            case 1:
                retObject = assessedCommandList.get(rowIndex).getIds();
                break;
            case 2:
                retObject = assessedCommandList.get(rowIndex).getType();
                break;    
            case 3:
                retObject = assessedCommandList.get(rowIndex).getAnalyst();
                break;
            case 4:
                final JButton button = new JButton(); 
                retObject = button;
                break;

            default:
                String message = VBASLogger.debugAt() + "\nSee the error log file for more information. ";
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        }

        return retObject;

    }

    /*
     * JTable uses this method to determine the default renderer/editor for each cell.  
     * If we didn't implement this method,
     * then the last column would contain text ("true"/"false"), rather than a check box.
     */
    @Override
    public Class getColumnClass(int c) {
        //return getValueAt(0, c).getClass();
        return columns[c];

    }

    /*
     // cell is editable.
     // only the select column is editable.
     @Override
     public boolean isCellEditable(int row, int col) {
     // return (col == 0) ? true : false;
     return false;
     }

     // when something is selected.
     @Override
     public void setValueAt(Object value, int row, int col) {
     fireTableCellUpdated(row, col);
     }*/
}
