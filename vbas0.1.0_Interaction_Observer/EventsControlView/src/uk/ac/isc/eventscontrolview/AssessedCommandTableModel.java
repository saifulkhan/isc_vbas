package uk.ac.isc.eventscontrolview;


import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import uk.ac.isc.seisdata.AssessedCommand;
import uk.ac.isc.seisdata.Command;
import uk.ac.isc.seisdata.Global;


public class AssessedCommandTableModel extends AbstractTableModel {

    private final String[] columnNames = {
        "Select",
        "IDs",
        "Analyst",
        "Report"};
    
    private final Class[] columns = new Class[]{
        Boolean.class, 
        String.class,
        String.class, 
        String.class,
        JButton.class};
    
    public final Object[] longValues = {
        Boolean.TRUE,
        new Integer(999999),
        new String(new char[50]), 
        new String(new char[500]),
        new String(new char[10]),
        new String(new char[10])};
        
    private final ArrayList<AssessedCommand> assessedCommandList;
    
    Object[][] data = {
        {false, 1, "Smith", "Relocate ..."},
        {true, 2, "Doe", "Edit Hypocentre ..."},
        {false, 3, "Black", "Set Prime ..."},
        {false, 4, "White", "Edit Phase"},
        {false, 5, "Brown", "Other Command ..."}
    };

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
        
        switch(columnIndex) {
            case 0:
                retObject = assessedCommandList.get(rowIndex).getSelect();
                break;
            case 1:
                retObject = assessedCommandList.get(rowIndex).getIds();
                break;
            case 2:
                retObject = assessedCommandList.get(rowIndex).getAnalyst();
                break;
            case 3:
                //retObject = assessedCommandList.get(rowIndex).getReport();
                final JButton button = new JButton();              
                return button;
                //break;
            default:
                String message = Global.debugAt() + "\nSee the error log file for more information. ";
                JOptionPane.showMessageDialog(null, message, "Error",  JOptionPane.ERROR_MESSAGE);
            
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
        return getValueAt(0, c).getClass();
    }

    // cell is editable.
    // only the select column is editable.
    @Override
    public boolean isCellEditable(int row, int col) {
        return (col == 0) ? true : false;
    }

    // when something is selected.
    @Override
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
        //printDebugData();
    }

    private void printDebugData() {
        int numRows = getRowCount();
        int numCols = getColumnCount();

        for (int i = 0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                System.out.print("  " + data[i][j]);
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }

}