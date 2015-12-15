package uk.ac.isc.seisdata;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.table.AbstractTableModel;

/**
 * This is an extended class for saving events list showing in an table data in
 * rows are events, columns are Evid, lat, region, lon and Orig time
 *
 * Just need to override some standard functions to make the table model working
 *
 * @author hui
 */
public class EventsTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Event ID.", "Prime", "Region", "Origin Time", "Lat.", "Long.", "Mag.", "Phase No."};
    private final Class[] columns = new Class[]{Integer.class, String.class, String.class, String.class, String.class, String.class, String.class, Integer.class};
    private final ArrayList<SeisEvent> events;

    public EventsTableModel(ArrayList<SeisEvent> events) {
        this.events = events;
    }

    @Override
    public int getRowCount() {
        return events.size();
    }

    @Override
    public int getColumnCount() {
        return 8;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    //overide it for setting values in each row and each column
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Object retObject;

        NumberFormat numFormat = DecimalFormat.getInstance();
        numFormat.setMaximumFractionDigits(1);
        numFormat.setMinimumFractionDigits(1);

        //DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        DateFormat formatter = new SimpleDateFormat("dd-MM HH:mm:ss");

        if (columnIndex == 0) {
            retObject = events.get(rowIndex).getEvid();
        } else if (columnIndex == 1) {
            retObject = events.get(rowIndex).getPrimeHypo().getAgency();
        } else if (columnIndex == 2) {
            retObject = events.get(rowIndex).getLocation();
        } else if (columnIndex == 3) {
            Date datetmp = events.get(rowIndex).getPrimeHypo().getOrigTime();
            //DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,DateFormat.DEFAULT);
            String dateOut = formatter.format(datetmp);
            retObject = dateOut;
        } else if (columnIndex == 4) {
            if (events.get(rowIndex).getPrimeHypo().getLat() != null) {
                retObject = numFormat.format(events.get(rowIndex).getPrimeHypo().getLat());
            } else {
                retObject = null;
            }
        } else if (columnIndex == 5) {
            if (events.get(rowIndex).getPrimeHypo().getLon() != null) {
                retObject = numFormat.format(events.get(rowIndex).getPrimeHypo().getLon());
            } else {
                retObject = null;
            }
        } else if (columnIndex == 6) {
            if (events.get(rowIndex).getMagnitude() != null) {
                retObject = numFormat.format(events.get(rowIndex).getMagnitude());
            } else {
                retObject = null;
            }
        } else {
            retObject = events.get(rowIndex).getPhaseNumber();
        }

        return retObject;
    }

    @Override
    public Class getColumnClass(int c) {
        //return getValueAt(0, c).getClass();
        return columns[c];
    }

}
