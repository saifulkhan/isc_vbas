package uk.ac.isc.eventscontrolview;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

/**
 * Hint: SiesData/.../EvensTable|Model.java
 * See bitbucket commit Hui's code: 
 * https://bitbucket.org/saifulkhan/vbas/commits/60877119fe05304042e21df0740df50531acc209
 */


public class AssessedCommandTableModel extends AbstractTableModel {

    private final String[] columnNames = {
        "Select",
        "Assessed Commands", 
        "Analyst",
        "Report"};
    
    private final Class[] columns = new Class[]{
        Boolean.class, 
        String.class, 
        String.class, 
        JButton.class};
    
//private final ArrayList<SeisEvent> events;

    Object[][] data = {
        {false, "1,2", "Smith", "file location"},
        {false, "3", "Brown", "file location"},
        {false, "3,4,5", "Black","file location"}
    };
    
    public final Object[] longValues = {
        Boolean.TRUE, 
        new String(new char[100]), 
        new String(new char[500])};

    /*    
     public ActionHistoryModel(ArrayList<ActionHistoryList> commandList) {
     this.commandList = commandList;
     }
     */
    @Override
    public int getRowCount() {
        //return commandList.size();
        return data.length;
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
        // Hint: See EventTableModel
        
        Object retObject = null;
        
        switch (columnIndex) {

            case 0:
                return data[rowIndex][columnIndex];
            case 1:
                return data[rowIndex][columnIndex];
            case 2:
                return data[rowIndex][columnIndex];
            case 3:
                final JButton button = new JButton();              
                return button;
            
            default:    
                return "Error";
        }
      

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
