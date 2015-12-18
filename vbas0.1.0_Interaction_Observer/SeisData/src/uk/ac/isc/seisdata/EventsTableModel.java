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
 * Just need to override some standard functions to make the table model working
 */

public class EventsTableModel extends AbstractTableModel {

    private final String[] columnNames = {
        "Event ID.", 
        "Prime", 
        "Region", 
        "Origin Time", 
        "Lat.", 
        "Long.", 
        "Mag.()", 
        "Nass"};
    
    private final Class[] columns = new Class[] {
        Integer.class, 
        String.class, 
        String.class, 
        String.class, 
        String.class, 
        String.class, 
        String.class, 
        Integer.class};
    
    public final Object[] longValues = {
        new Integer(0), 
        new String(new char[5]), 
        new String(new char[20]),
        "00:00:00",
        "",
        "",
        "9.9",
        new Integer(9999)};
 
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
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    // overide it for setting values in each row and each column
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Object retObject;

        NumberFormat numFormat = DecimalFormat.getInstance();
        numFormat.setMaximumFractionDigits(1);
        numFormat.setMinimumFractionDigits(1);

        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        if (columnIndex == 0) {
            retObject = events.get(rowIndex).getEvid();
        } else if (columnIndex == 1) {
            retObject = events.get(rowIndex).getPrimeHypo().getAgency();
        } else if (columnIndex == 2) {
            retObject = events.get(rowIndex).getLocation();
        } else if (columnIndex == 3) {
            Date datetmp = events.get(rowIndex).getPrimeHypo().getOrigTime();            
            String dateOut = formatter.format(datetmp);
            retObject = dateOut;
        } else if (columnIndex == 4) {
            if (events.get(rowIndex).getPrimeHypo().getLat() != null) {
                Double lat = events.get(rowIndex).getPrimeHypo().getLat();
                String sign = lat > 0 ? "N" : "S";
                retObject = numFormat.format(Math.abs(lat)) + sign;
                
            } else {
                retObject = null;
            }
        } else if (columnIndex == 5) {
            if (events.get(rowIndex).getPrimeHypo().getLon() != null) {
                Double lon = events.get(rowIndex).getPrimeHypo().getLon();
                String sign = lon > 0 ? "E" : "W";
                retObject = numFormat.format(Math.abs(lon)) + sign;
                //retObject = numFormat.format(events.get(rowIndex).getPrimeHypo().getLon());
                
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
