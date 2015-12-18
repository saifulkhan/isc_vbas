package uk.ac.isc.eventscontrolview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import uk.ac.isc.seisdata.ActionHistoryList;
import uk.ac.isc.seisdata.BlockTableModel;
import uk.ac.isc.seisdata.Command;
import uk.ac.isc.seisdata.EventsTableModel;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.SeisEventsList;

/**
 * It holds all the "phases", "hypocentres", and "events" data and passes
 * reference to other views.
 */
public class EventsControlPanel extends JPanel implements ListSelectionListener, SeisDataChangeListener {

    private JTable eventsTable = null;          // the main table of the event list
    EventsTableModel eventsTableModel;
            
    private int selectedEvid = 0;               // the selected event id 
    private static SeisEvent selectedSeisEvent;

    // The space to keep all the data
    private final BlockTableModel blockTableModel = new BlockTableModel();

    private static final SeisEventsList eventsList = new SeisEventsList();
    private static final HypocentresList hypocentresList = new HypocentresList();
    private static final PhasesList phasesList = new PhasesList();
    
    // for tracking the region of each station
    TreeMap<String, String> stations = new TreeMap<String, String>();
    
    // each class may need logger to keep log file, too rush to follow the good practice 
    private static final Logger logger = Logger.getLogger(EventsControlPanel.class.getName());

    // Provenance: action history, command etc.
    private final ActionHistoryList actionHistoryList = new ActionHistoryList();
    private final Command command;
    
    
    public EventsControlPanel() {

        // 1. set the table and tooltips
        eventsTable = new JTable() {
            @Override
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    // comment row, exclude heading
                    if (rowIndex != 0) {
                        tip = getValueAt(rowIndex, colIndex).toString();
                    }
                } catch (RuntimeException e1) {
                    // catch null pointer exception if mouse is over an empty line
                    logger.log(Level.FINE, "Empty value for tooltip");
                }
                return tip;
            }
        };
        
        // fill in the events number
        boolean retDAO = SeisDataDAO.retrieveBlockEventNumber(blockTableModel.getTaskBlocks());
        retDAO = SeisDataDAO.retrieveBlockReviewedEventNumber(blockTableModel.getTaskBlocks());
        if (retDAO == false) {
            logger.log(Level.SEVERE, "Fail to load task block list from database.");
        }

        // 2. add data into evets list : retrieve the events from the database
        retDAO = SeisDataDAO.retrieveAllEvents(eventsList.getEvents());
        retDAO = SeisDataDAO.retrieveEventsMagnitude(eventsList.getEvents());
        retDAO = SeisDataDAO.retrieveAllPhaseNumber(eventsList.getEvents());
        retDAO = SeisDataDAO.retrieveAllRegionName(eventsList.getEvents());

        if (retDAO == false) {
            logger.log(Level.SEVERE, "Fail to load events list from database.");
        }

        // After getting all the events, pick the first one as selected
        selectedSeisEvent = eventsList.getEvents().get(0);
        Global.setSelectedSeisEvent(selectedSeisEvent);
        
        eventsTableModel = new EventsTableModel(eventsList.getEvents());
        eventsTable.setModel(eventsTableModel);
        setupTableVisualAttributes();
        
       
        /*3.add data into hypocentres and phases list*/
        retDAO = SeisDataDAO.retrieveHypos(eventsList.getEvents().get(0).getEvid(), hypocentresList.getHypocentres());
        retDAO = SeisDataDAO.retrieveHyposMagnitude(hypocentresList.getHypocentres());

        retDAO = SeisDataDAO.retrieveAllPhases(eventsList.getEvents().get(0).getEvid(), phasesList.getPhases());
        retDAO = SeisDataDAO.retrieveAllPhasesAmpMag(eventsList.getEvents().get(0).getEvid(), phasesList.getPhases());

        //load the correspondent map into the stataions
        retDAO = SeisDataDAO.retrieveAllStationsWithRegions(stations);

        if (retDAO == false) {
            logger.log(Level.SEVERE, "Fail to load hypocentres and phases list from database.");
        }

        //as I remove all the hypos when clicking an event to retrieve the hypos, so need reset prime hypo every time
        for (Hypocentre hypo : hypocentresList.getHypocentres()) {
            if (hypo.getIsPrime() == true) {
                selectedSeisEvent.setPrimeHypo(hypo);
            }
        }
        
        Global.setHypocentresList(hypocentresList);

        //put the region name into the pahseList
        for (int i = 0; i < phasesList.getPhases().size(); i++) {
            phasesList.getPhases().get(i).setRegionName(stations.get(phasesList.getPhases().get(i).getReportStation()));
        }
        
        Global.setPhasesList(phasesList);
        Global.setStations(stations);
        
        /*add listener for the selection change*/
        eventsTable.getSelectionModel().addListSelectionListener(this);
        
        // ActionHistory
        // Event will receive
        Global.setActionHistoryList(actionHistoryList);
        //actionHistoryList.addChangeListener(this);
        
        command = new Command();
        Global.setCommand(command);
        command.addChangeListener(this);
        
    }

    
    private void setupTableVisualAttributes() {
   
        JTableHeader th = eventsTable.getTableHeader();
        th.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        th.setBackground(new Color(43,87,151));  
        th.setForeground(Color.white);
        
        eventsTable.setRowSelectionAllowed(true);
        eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventsTable.setColumnSelectionAllowed(false);
        eventsTable.setSelectionBackground(new Color(45,137,239));
        eventsTable.setSelectionForeground(Color.WHITE);
        eventsTable.setRowSelectionInterval(0, 0);
        
        eventsTable.setRowHeight(25);
        eventsTable.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        eventsTable.setShowGrid(false);
        eventsTable.setShowVerticalLines(false);
        eventsTable.setShowHorizontalLines(false);
             
        // Set: Left or Right aligned
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        eventsTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        eventsTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        eventsTable.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);
        eventsTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        eventsTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        eventsTable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        eventsTable.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);

        // This part of the code picks good column sizes. 
        // If all column heads are wider than the column's cells'
        // contents, then you can just use column.sizeWidthToFit().
        
        // EventsTableModel model = (EventsTableModel) eventsTable.getModel();
        /*
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        
        Object[] longValues = eventsTableModel.longValues;
        TableCellRenderer headerRenderer = eventsTable.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < eventsTableModel.getColumnCount(); i++) {
            column = eventsTable.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(
                                 null, column.getHeaderValue(),
                                 false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = eventsTable.getDefaultRenderer(eventsTableModel.getColumnClass(i))
                    .getTableCellRendererComponent(eventsTable, 
                            longValues[i], false, false, 0, i);
            
            cellWidth = comp.getPreferredSize().width;

           column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }*/
        
    }
    
    
    
    /*
    public PhasesList getPhasesList() {
        return phasesList;
    }

    public HypocentresList getHyposList() {
        return hypocentresList;
    }

    public SeisEvent getSelectedSeisEvent() {
        return selectedSeisEvent;
    }

    public TreeMap<String, String> getStationsForRegion() {
        return stations;
    }*/

    public JTable getTable() {
        return this.eventsTable;
    }

    public BlockTableModel getBlockTableModel() {
        return this.blockTableModel;
    }

    
    /*
     * Table's new row or new event is selected 
     * Trigger the change of all the regestered listeners (observers)
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {

        System.out.println("DEBUG: " + Thread.currentThread().getStackTrace()[1].getLineNumber() + ", " + "public void valueChanged(ListSelectionEvent e) -> " + "call fireSeisDataChanged()");

        // disable the double calls
        if (e.getValueIsAdjusting()) {
            return;
        }

        int selectedRowNum = eventsTable.getSelectedRow();
        // Step 1. get selected evid.
        selectedEvid = (Integer) eventsTable.getValueAt(selectedRowNum, 0);
        System.out.println("Selected evid: " + selectedEvid);

        // Update the hypocentres and phases lists
        boolean retDAO = SeisDataDAO.retrieveHypos(selectedEvid, hypocentresList.getHypocentres());
        retDAO = SeisDataDAO.retrieveHyposMagnitude(hypocentresList.getHypocentres());

        retDAO = SeisDataDAO.retrieveAllPhases(selectedEvid, phasesList.getPhases());
        retDAO = SeisDataDAO.retrieveAllPhasesAmpMag(selectedEvid, phasesList.getPhases());

        if (retDAO == false) {
            logger.log(Level.SEVERE, "Fail to load hypocentres and phases list from database when selecting event.");
        }

        selectedSeisEvent = eventsList.getEvents().get(selectedRowNum);
        Global.setSelectedSeisEvent(selectedSeisEvent);
        
        for (Hypocentre hypo : hypocentresList.getHypocentres()) {
            if (hypo.getIsPrime() == true) {
                selectedSeisEvent.setPrimeHypo(hypo);
            }
        }
        
        Global.setHypocentresList(hypocentresList);

        /*verify that the hypocentres and phases lists are correct*/
        //for(int i = 0; i<hypocentresList.getHypocentres().size();i++)
        //{
        //    System.out.println(hypocentresList.getHypocentres().get(i));
        //}
        //for(int i = 0; i<phasesList.getPhases().size();i++)
        //{
        //    System.out.println(phasesList.getPhases().get(i));
        //}

        /*notify other views that the hypocentre and phases lists have been changed*/
        hypocentresList.fireSeisDataChanged();
        /*add one listener only, otherwise it will refresh twice*/
        phasesList.fireSeisDataChanged();
        actionHistoryList.fireSeisDataChanged();

    }

    
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
         JOptionPane.showMessageDialog(null, "SeisDataChanged() : command (event from HypoTable) received by the EventsTable: " + Global.getCommand() );
    }

}
