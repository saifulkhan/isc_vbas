package uk.ac.isc.eventscontrolview;

import javax.swing.table.AbstractTableModel;

/**
 * Hint: SiesData/.../EvensTable|Model.java
 */
public class ActionHistoryTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Select", "Analyst", "Command"};
    private final Class[] columns = new Class[]{Boolean.class, String.class, String.class};
    //private final ArrayList<SeisEvent> events;

    Object[][] data = {
        {false, "Smith", "Snowboarding"},
        {true, "Doe", "Rowing"},
        {false, "Black", "Knitting"},
        {false, "White", "Speed reading"},
        {false, "Brown", "Pool"}
    };
    public final Object[] longValues = {"Jane", "Kathy", "None of the above", new Integer(20), Boolean.TRUE};

    /*    
     public ActionHistoryModel(ArrayList<ActionHistoryList> actionHistoryList) {
     this.actionHistoryList = actionHistoryList;
     }
     */
    @Override
    public int getRowCount() {
        //return actionHistoryList.size();
        return data.length;
    }

    @Override
    public int getColumnCount() {
        //return 3;
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    //overide it for setting values in each row and each column
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

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

    /*
     * Don't need to implement this method unless your table's editable.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant, no matter where the cell appears onscreen.
        if (col < 2) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Don't need to implement this method unless your table's data can change.
     */
    @Override
    public void setValueAt(Object value, int row, int col) {

        System.out.println("Setting value at " + row + "," + col
                + " to " + value
                + " (an instance of "
                + value.getClass() + ")");

        data[row][col] = value;
        fireTableCellUpdated(row, col);

        System.out.println("New value of data:");
        printDebugData();

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
