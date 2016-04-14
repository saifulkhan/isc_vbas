package uk.ac.isc.textview;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import uk.ac.isc.seisdata.Phase;
import uk.ac.isc.seisdata.VBASLogger;

/**
 * The phase table model for the phase table
 */
public class PhaseTextViewTableModel extends AbstractTableModel {

    private final String[] columnNames = {
        "Agency",
        "Code",
        "Time",
        "Region Name",
        "Delta. Deg.",
        "ES. Az.",
        "Op. ID",
        "ISC ID",
        "ISC Res",
        "Def.",
        "AmpMag",
        "Slowness",
        "SNR",
        "SE. Az.",
        "Phase ID",
        "Reading ID"};

    private final Class[] columns = new Class[]{
        String.class,
        String.class,
        String.class,
        String.class,
        Long.class,
        Long.class,
        String.class,
        String.class,
        String.class,
        String.class,
        String.class,
        Double.class, 
        Double.class, 
        Double.class, 
        Integer.class,
        Integer.class};

    public static final Object[] longValues = {
        "XXXXX",
        "XXXXX",
        "00:00:00",
        "XXXXXXXXXX",
        "360",
        "0.0",
        "X",
        "X",
        "+0.0",
        "X",
        "0.0",
        new Double(0.0),
        new Double(0.0),
        new Double(0.0),
        new Integer(999999999),
        new Integer(999999999)};

    private ArrayList<Phase> phasesList;

    public PhaseTextViewTableModel(ArrayList<Phase> phasesList) {
        this.phasesList = phasesList;
    }

    public void setPhasesList(ArrayList<Phase> plist) {
        this.phasesList = plist;
    }

    @Override
    public int getRowCount() {
        //System.out.println(VBASLogger.debugAt() + "phasesList.size() = " + phasesList.size());
        return phasesList.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public ArrayList<Phase> getPhasesList() {
        return this.phasesList;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Object retObject = null;

        NumberFormat numFormat = DecimalFormat.getInstance();
        numFormat.setMaximumFractionDigits(1);
        numFormat.setMinimumFractionDigits(1);

        DecimalFormat df = new DecimalFormat("#.#");

        switch (columnIndex) {

            case 0:
                retObject = phasesList.get(rowIndex).getReportAgency();
                break;
            case 1:
                retObject = phasesList.get(rowIndex).getReportStation();
                break;
            case 2:
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                if (phasesList.get(rowIndex).getArrivalTime() != null) {
                    retObject = dateFormat.format(phasesList.get(rowIndex).getArrivalTime())
                            + "."
                            + phasesList.get(rowIndex).getMsec() / 100;
                } else {
                    retObject = null;
                }
                break;
            case 3:
                retObject = phasesList.get(rowIndex).getRegionName();
                break;
            case 4:
                retObject = Math.round(phasesList.get(rowIndex).getDistance());
                break;
            case 5:
                retObject = Math.round(phasesList.get(rowIndex).getAzimuth());
                break;
            case 6:
                retObject = phasesList.get(rowIndex).getOrigPhaseType();
                break;
            case 7:
                retObject = phasesList.get(rowIndex).getIscPhaseType();
                break;
            case 8:
                if (phasesList.get(rowIndex).getTimeResidual() != null) {
                    if (phasesList.get(rowIndex).getTimeResidual() > 0) {
                        retObject = "+" + numFormat.format(phasesList.get(rowIndex).getTimeResidual());
                    } else {
                        retObject = numFormat.format(phasesList.get(rowIndex).getTimeResidual());

                    }
                } else {
                    retObject = null;
                }
                break;
            case 9:
                if (phasesList.get(rowIndex).getDefining() == true) {
                    retObject = "T";
                } else {
                    retObject = null;
                }
                break;
            case 10:
                if (phasesList.get(rowIndex).getAmpMag() != null) {
                    if (phasesList.get(rowIndex).getAmpmagDefining() == null) {
                        retObject = numFormat.format(phasesList.get(rowIndex).getAmpMag());
                    } else if (phasesList.get(rowIndex).getAmpmagDefining() == true) {
                        retObject = numFormat.format(phasesList.get(rowIndex).getAmpMag()) + "D";
                    }

                } else {
                    retObject = null;
                }
                break;
            case 11:

                retObject = phasesList.get(rowIndex).getSlowness();
                break;

            case 12:
                retObject = phasesList.get(rowIndex).getSNRRate();
                break;
            case 13:
                retObject = phasesList.get(rowIndex).getSeAzimuth();
                break;
            case 14:
                retObject = phasesList.get(rowIndex).getPhid();
                break;
            case 15:
                retObject = phasesList.get(rowIndex).getRdid();
                break;
            default:
                String message = VBASLogger.debugAt() + "\nSee the error log file for more information. ";
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        }

        return retObject;
    }

    @Override
    public Class getColumnClass(int c) {
        return columns[c];
    }

}
