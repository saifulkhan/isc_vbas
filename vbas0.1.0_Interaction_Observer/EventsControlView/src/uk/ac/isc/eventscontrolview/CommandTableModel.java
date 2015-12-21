package uk.ac.isc.eventscontrolview;

import javax.swing.table.AbstractTableModel;

/**
 * Hint: SiesData/.../EvensTable|Model.java
 */
public class CommandTableModel extends AbstractTableModel {

    private final String[] columnNames = {
        "Select", 
        "Analyst", 
        "Command"};
    
    private final Class[] columns = new Class[]{
        Boolean.class, 
        String.class, 
        String.class};
    
//private final ArrayList<SeisEvent> events;

    Object[][] data = {
        {false, "Smith", "Snowboarding"},
        {true, "Doe", "Rowing"},
        {false, "Black", "Knitting"},
        {false, "White", "Speed reading"},
        {false, "Brown", "Pool"}
    };
    
    public final Object[] longValues = {
        Boolean.TRUE, 
        new String(new char[15]), 
        new String(new char[200])};

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
        // See EventTableModel
        return data[rowIndex][columnIndex];
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
