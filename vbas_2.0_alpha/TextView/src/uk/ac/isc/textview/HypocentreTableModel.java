package uk.ac.isc.textview;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import uk.ac.isc.seisdata.Hypocentre;

public class HypocentreTableModel extends AbstractTableModel {

    private final String[] columnNames = {
        "Agency",
        "Time",
        "Lat.",
        "Long.",
        "Depth",
        "Depth Err.",
        "Mag-1",
        "Mag-2",
        "RMS",
        "HypID"};

    private final Class[] columns = new Class[]{
        String.class,
        String.class,
        String.class,
        String.class,
        Integer.class,
        Long.class,
        String.class,
        String.class,
        Long.class,
        Integer.class};

    public static final Object[] longValues = {
        "999999999",
        "00:00:00",
        "180.9S",
        "180.9W",
        "800",
        "1000",
        "9.9mb",
        "9.9MS",
        "9.9",
        "999999999"};

    private ArrayList<Hypocentre> hyposList;

    public HypocentreTableModel(ArrayList<Hypocentre> hyposList) {
        this.hyposList = hyposList;
    }

    public void setHyposList(ArrayList<Hypocentre> hlist) {
        this.hyposList = hlist;
    }

    @Override
    public int getRowCount() {
        return hyposList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public ArrayList<Hypocentre> getHyposList() {
        return this.hyposList;
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    @Override
    public Class getColumnClass(int c) {
        return columns[c];   // Hui
        //return getValueAt(0, c).getClass(); // Saiful
    }

    //return values at row and column
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Object retObject = null;

        NumberFormat numFormat = DecimalFormat.getInstance();
        numFormat.setMaximumFractionDigits(1);
        numFormat.setMinimumFractionDigits(1);

        switch (columnIndex) {
            case 0: //agency
                retObject = hyposList.get(rowIndex).getAgency();
                break;

            case 1:
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                if (hyposList.get(rowIndex).getOrigTime() != null) {
                    retObject = dateFormat.format(hyposList.get(rowIndex).getOrigTime()) + "." + hyposList.get(rowIndex).getMsec() / 100;
                } else {
                    retObject = null;
                }
                break;

            case 2:  // lat
                String latString = "";
                if (hyposList.get(rowIndex).getLat() < 0) {
                    latString += numFormat.format(Math.abs(hyposList.get(rowIndex).getLat()));
                    latString += "S";
                } else {
                    latString += numFormat.format(hyposList.get(rowIndex).getLat());
                    latString += "N";
                }
                retObject = latString;
                break;

            case 3: // lon
                String lonString = "";
                if (hyposList.get(rowIndex).getLon() < 0) {
                    lonString += numFormat.format(Math.abs(hyposList.get(rowIndex).getLon()));
                    lonString += "W";
                } else {
                    lonString += numFormat.format(hyposList.get(rowIndex).getLon());
                    lonString += "E";
                }
                retObject = lonString;
                break;

            case 4:  // depth
                retObject = hyposList.get(rowIndex).getDepth();

            case 5: // depth err
                if (hyposList.get(rowIndex).getErrDepth() != null) {
                    retObject = Math.round(hyposList.get(rowIndex).getErrDepth());
                } else {
                    retObject = null;
                }

                break;

            case 6: //mag 1 + type
                if (hyposList.get(rowIndex).getMagnitude().size() > 0) {
                    Object key = hyposList.get(rowIndex).getMagnitude().keySet().toArray()[0];
                    retObject = hyposList.get(rowIndex).getMagnitude().get(key).toString() + key.toString();
                } else {
                    retObject = null;
                }

                break;

            case 7:
                if (hyposList.get(rowIndex).getMagnitude().size() > 1) {
                    Object key = hyposList.get(rowIndex).getMagnitude().keySet().toArray()[1];
                    retObject = hyposList.get(rowIndex).getMagnitude().get(key).toString() + key.toString();
                } else {
                    retObject = null;
                }
                break;

            case 8:  // RMS
                if (hyposList.get(rowIndex).getStime() != null) {
                retObject = Math.round(hyposList.get(rowIndex).getStime());
                } else {
                    retObject = null;
                }
                break;

            case 9:  // HypID
                retObject = hyposList.get(rowIndex).getHypid();
                break;
        }

        return retObject;
    }

    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant, no matter where the cell appears onscreen.
        /*
         if (col < 1) {
         return false;
         } else {
         return true;
         }*/

        return false;
    }

    public void setValueAt(Object value, int row, int col) {
        /*  
         if (DEBUG) {
         System.out.println("Setting value at " + row + "," + col
         + " to " + value
         + " (an instance of "
         + value.getClass() + ")");
         }

         data[row][col] = value;
         fireTableCellUpdated(row, col);

         if (DEBUG) {
         System.out.println("New value of data:");
         printDebugData();
         }
         */
    }

}
