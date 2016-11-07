package uk.ac.isc.textview;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import uk.ac.isc.seisdata.Duplicates;
 
public class DuplicatesTableModel extends AbstractTableModel {

    private final String[] columnNames = {
        "ownsta",
        "ownphase",
        "ownresidual",
        "owndelta",
        "dupphase",
        "dupresidual",
        "dupdelta",
        "dupevid",
        "dupready"
    };

    private final Class[] columns = new Class[]{
        String.class,
        String.class,
        String.class,
        Integer.class,
        String.class,
        String.class,
        Integer.class,
        Integer.class,
        String.class
    };

    private ArrayList<Duplicates> duplicatesList;

    public DuplicatesTableModel(ArrayList<Duplicates> duplicatesList) {
        this.duplicatesList = duplicatesList;
    }

    public void setHyposList(ArrayList<Duplicates> duplicatesList) {
        this.duplicatesList = duplicatesList;
    }

    @Override
    public int getRowCount() {
        return duplicatesList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public ArrayList<Duplicates> getHyposList() {
        return this.duplicatesList;
    }

    @Override
    public Class getColumnClass(int c) {
        return columns[c];
    }

    // return values at row and column
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Object retObject = null;

        switch (columnIndex) {
            case 0:
                retObject = duplicatesList.get(rowIndex).getOwnsta();
                break;

            case 1:
                retObject = duplicatesList.get(rowIndex).getOwnphase();
                break;

            case 2:
                retObject = duplicatesList.get(rowIndex).getOwnresidual();
                break;

            case 3:
                retObject = duplicatesList.get(rowIndex).getOwndelta();
                break;

            case 4:
                retObject = duplicatesList.get(rowIndex).getDupphase();
                break;

            case 5:
                retObject = duplicatesList.get(rowIndex).getDupresidual();
                break;

            case 6:
                retObject = duplicatesList.get(rowIndex).getDupdelta();
                break;

            case 7:
                retObject = duplicatesList.get(rowIndex).getDupevid();
                break;

            case 8:
                retObject = duplicatesList.get(rowIndex).getDupready();
                break;
        }

        return retObject;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {

    }

}
