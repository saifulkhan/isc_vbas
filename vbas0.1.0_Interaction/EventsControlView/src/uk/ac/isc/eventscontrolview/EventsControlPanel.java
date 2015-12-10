package uk.ac.isc.eventscontrolview;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import uk.ac.isc.seisdata.BlockTableModel;
import uk.ac.isc.seisdata.EventsTableModel;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdata.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.SeisEventsList;

/**
 * It holds all the "phases", "hypocentres", and "events" data and passes
 * reference to other views.
 */
public class EventsControlPanel extends JPanel implements ListSelectionListener {


    //the main table of the event list
    private JTable eventsTable = null;

    //the selected event id
    private int selectedEvid = 0;

    private static SeisEvent selectedEvent;

    /*The space to keep all the data*/
    private final BlockTableModel blockTableModel = new BlockTableModel();

    private static final SeisEventsList eventsList = new SeisEventsList();
    private static final HypocentresList hyposList = new HypocentresList();
    private static final PhasesList phasesList = new PhasesList();

    /*for tracking the region of each station*/
    TreeMap<String, String> stations = new TreeMap<String, String>();

    //private static Handler handler = null;
    //each class may need logger to keep log file, too rush to follow the good practice 
    private static final Logger logger = Logger.getLogger(EventsControlPanel.class.getName());

    /*
     * Methods
     */
    public EventsControlPanel() {

        /*1. set the table and tooltips*/
        eventsTable = new JTable() {
            @Override
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    //comment row, exclude heading
                    if (rowIndex != 0) {
                        tip = getValueAt(rowIndex, colIndex).toString();
                    }
                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                    logger.log(Level.FINE, "Empty value for tooltip");
                }

                return tip;
            }
        };

        /*fill in the events number*/
        boolean retDAO = SeisDataDAO.retrieveBlockEventNumber(blockTableModel.getTaskBlocks());
        retDAO = SeisDataDAO.retrieveBlockReviewedEventNumber(blockTableModel.getTaskBlocks());
        if (retDAO == false) {
            logger.log(Level.SEVERE, "Fail to load task block list from database.");
        }

        /*2.add data into evets list*/
        /*retrieve the events from the database*/
        retDAO = SeisDataDAO.retrieveAllEvents(eventsList.getEvents());
        retDAO = SeisDataDAO.retrieveEventsMagnitude(eventsList.getEvents());
        retDAO = SeisDataDAO.retrieveAllPhaseNumber(eventsList.getEvents());
        retDAO = SeisDataDAO.retrieveAllRegionName(eventsList.getEvents());

        if (retDAO == false) {
            logger.log(Level.SEVERE, "Fail to load events list from database.");
        }

        //after getting all the events, pick the first one as selected
        selectedEvent = eventsList.getEvents().get(0);

        EventsTableModel etm = new EventsTableModel(eventsList.getEvents());
        eventsTable.setModel(etm);
        eventsTable.setRowSelectionAllowed(true);
        eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventsTable.setColumnSelectionAllowed(false);
        //eventsTable.setSelectionBackground(new Color(255,255,153));
        eventsTable.setRowSelectionInterval(0, 0);

        /*Change the skin and appearance of the control panel*/
        //eventsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        eventsTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        eventsTable.getColumnModel().getColumn(0).setMinWidth(120);
        eventsTable.getColumnModel().getColumn(0).setMaxWidth(120);

        eventsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        eventsTable.getColumnModel().getColumn(2).setPreferredWidth(180);

        eventsTable.getColumnModel().getColumn(3).setPreferredWidth(180);//origin time
        eventsTable.getColumnModel().getColumn(3).setMinWidth(180);
        eventsTable.getColumnModel().getColumn(3).setMaxWidth(180);

        eventsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        eventsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        eventsTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        eventsTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        eventsTable.setRowHeight(40);
        eventsTable.setFont(new Font("monospaced", Font.BOLD, 16));
        eventsTable.setShowGrid(false);
        eventsTable.setShowVerticalLines(false);
        eventsTable.setShowHorizontalLines(false);

        JTableHeader th = eventsTable.getTableHeader();
        th.setFont(new Font("monospaced", Font.PLAIN, 16));
        //th.setBackground(new Color(255,255,153));

        //make the evid right aligned
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        eventsTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        //eventsTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        eventsTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        eventsTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        eventsTable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        eventsTable.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);

        /*3.add data into hypocentres and phases list*/
        retDAO = SeisDataDAO.retrieveHypos(eventsList.getEvents().get(0).getEvid(), hyposList.getHypocentres());
        retDAO = SeisDataDAO.retrieveHyposMagnitude(hyposList.getHypocentres());

        retDAO = SeisDataDAO.retrieveAllPhases(eventsList.getEvents().get(0).getEvid(), phasesList.getPhases());
        retDAO = SeisDataDAO.retrieveAllPhasesAmpMag(eventsList.getEvents().get(0).getEvid(), phasesList.getPhases());

        //load the correspondent map into the stataions
        retDAO = SeisDataDAO.retrieveAllStationsWithRegions(stations);

        if (retDAO == false) {
            logger.log(Level.SEVERE, "Fail to load hypocentres and phases list from database.");
        }

        //as I remove all the hypos when clicking an event to retrieve the hypos, so need reset prime hypo every time
        for (Hypocentre hypo : hyposList.getHypocentres()) {
            if (hypo.getIsPrime() == true) {
                selectedEvent.setPrimeHypo(hypo);
            }
        }

        //put the region name into the pahseList
        for (int i = 0; i < phasesList.getPhases().size(); i++) {
            phasesList.getPhases().get(i).setRegionName(stations.get(phasesList.getPhases().get(i).getReportStation()));
        }

        /*add listener for the selection change*/
        eventsTable.getSelectionModel().addListSelectionListener(this);
    }

    //get the phase list
    public PhasesList getPhasesList() {
        return phasesList;
    }

    public HypocentresList getHyposList() {
        return hyposList;
    }

    public SeisEvent getSelectedSeisEvent() {
        return selectedEvent;
    }

    public TreeMap<String, String> getStationsForRegion() {
        return stations;
    }

    public JTable getTable() {
        return this.eventsTable;
    }

    public BlockTableModel getBlockTableModel() {
        return this.blockTableModel;
    }

    //when selection changes, trigger the change of all the regestered listeners (observers)
    @Override
    public void valueChanged(ListSelectionEvent e) {

        System.out.println(this.toString());

        //disable the double calls
        if (e.getValueIsAdjusting()) {
            return;
        }

        int rowNumber = eventsTable.getSelectedRow();

        /*Step 1. get selected evid*/
        selectedEvid = (Integer) eventsTable.getValueAt(rowNumber, 0);
        //System.out.println(selectedEvid);

        /*update the hypocentres and phases lists*/
        boolean retDAO = SeisDataDAO.retrieveHypos(selectedEvid, hyposList.getHypocentres());
        retDAO = SeisDataDAO.retrieveHyposMagnitude(hyposList.getHypocentres());

        retDAO = SeisDataDAO.retrieveAllPhases(selectedEvid, phasesList.getPhases());
        retDAO = SeisDataDAO.retrieveAllPhasesAmpMag(selectedEvid, phasesList.getPhases());

        if (retDAO == false) {
            logger.log(Level.SEVERE, "Fail to load hypocentres and phases list from database when selecting event.");
        }

        selectedEvent = eventsList.getEvents().get(rowNumber);
        for (Hypocentre hypo : hyposList.getHypocentres()) {
            if (hypo.getIsPrime() == true) {
                selectedEvent.setPrimeHypo(hypo);
            }
        }

        /*verify that the hypocentres and phases lists are correct*/
        //for(int i = 0; i<hyposList.getHypocentres().size();i++)
        //{
        //    System.out.println(hyposList.getHypocentres().get(i));
        //}
        //for(int i = 0; i<phasesList.getPhases().size();i++)
        //{
        //    System.out.println(phasesList.getPhases().get(i));
        //}

        /*notify other views that the hypocentre and phases lists have been changed*/
        hyposList.fireSeisDataChanged();

        /*add one listener only, otherwise it will refresh twice*/
        phasesList.fireSeisDataChanged();

    }
}
