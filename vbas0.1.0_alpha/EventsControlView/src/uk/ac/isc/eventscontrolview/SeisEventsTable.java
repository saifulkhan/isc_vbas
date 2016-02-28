package uk.ac.isc.eventscontrolview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.TreeMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import uk.ac.isc.seisdata.AssessedCommandList;
import uk.ac.isc.seisdata.BlockTableModel;
import uk.ac.isc.seisdata.CommandList;
import uk.ac.isc.seisdata.EventsTableModel;
import uk.ac.isc.seisdata.Global;
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
public class SeisEventsTable extends JPanel implements ListSelectionListener {

    private JTable table;
    private EventsTableModel tableModel;
    private SeisEventSearchPanel eventsSearchPanel;

    /*
     * All seisevents will be loaded first.
     */
    private static SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final BlockTableModel blockTableModel = new BlockTableModel();
    private static final SeisEventsList eventsList = new SeisEventsList();

    /*
     * All seisevents will be loaded first and data, e.g., hypocentres, phase, comamnds, 
     * assessedcommands related to the selected seisevent will be loaded.
     * These data (array list) will be used by the tables & views.
     */
    private final HypocentresList hypocentresList = Global.getHypocentresList();
    private static final PhasesList phasesList = Global.getPhasesList();
    private final TreeMap<String, String> stations = new TreeMap<String, String>();
    private final AssessedCommandList assessedCommandList = Global.getAssessedCommandList();
    private final CommandList commandList = Global.getCommandList();

    public SeisEventsTable() {
        loadSeisEvents();

        table = new JTable();
        tableModel = new EventsTableModel(eventsList.getEvents());
        table.setModel(tableModel);

        // add listener for the selection change
        table.getSelectionModel().addListSelectionListener(this);
        setupTableVisualAttributes();

        // Action buttons, search panel
        // layout all together
        eventsSearchPanel = new SeisEventSearchPanel(table);
        this.setLayout(new BorderLayout());
        this.add(eventsSearchPanel, BorderLayout.PAGE_START);
        this.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void loadSeisEvents() {
        Global.logDebug("Load SeisEvents list.");
        // fill in the events number
        SeisDataDAO.retrieveBlockEventNumber(blockTableModel.getTaskBlocks());
        SeisDataDAO.retrieveBlockReviewedEventNumber(blockTableModel.getTaskBlocks());

        // add data into evets list : retrieve the events from the database
        SeisDataDAO.retrieveAllEvents(eventsList.getEvents());
        SeisDataDAO.retrieveEventsMagnitude(eventsList.getEvents());
        SeisDataDAO.retrieveAllPhaseNumber(eventsList.getEvents());
        SeisDataDAO.retrieveAllRegionName(eventsList.getEvents());

        selectedSeisEvent.setValues(eventsList.getEvents().get(0));
        loadSelectedSeisEventData();
    }

    public void loadSelectedSeisEventData() {

        System.out.println(Global.debugAt() + "Load list of Hypocentre, Phase, Commmands, and AssessedCommands for SeisEvent: "
                + selectedSeisEvent.getEvid());

        /*
         * Hypocentre
         */
        SeisDataDAO.retrieveHypos(selectedSeisEvent.getEvid(),
                hypocentresList.getHypocentres());
        SeisDataDAO.retrieveHyposMagnitude(hypocentresList.getHypocentres());
        // as I remove all the hypos when clicking an event to retrieve the hypos, 
        // so need reset prime hypo every time
        // TODO: Saiful, What is this?
        for (Hypocentre hypo : hypocentresList.getHypocentres()) {
            if (hypo.getIsPrime() == true) {
                selectedSeisEvent.setPrimeHypo(hypo);
            }
        }

        /*
         * Phase
         */
        SeisDataDAO.retrieveAllPhases(selectedSeisEvent.getEvid(), phasesList.getPhases());
        SeisDataDAO.retrieveAllPhasesAmpMag(selectedSeisEvent.getEvid(),
                phasesList.getPhases());
        SeisDataDAO.retrieveAllStationsWithRegions(stations);                                       
        // load the correspondent map into the stataions
        // put the region name into the pahseList
        for (int i = 0; i < phasesList.getPhases().size(); i++) {
            phasesList.getPhases()
                    .get(i)
                    .setRegionName(stations
                            .get(phasesList
                                    .getPhases()
                                    .get(i)
                                    .getReportStation()));
        }

        /*
         * Commands
         */
        //SeisDataDAO.readCommandTable(selectedSeisEvent.getEvid(), commandList.getCommandList());

        /*
         * AssessedCommand 
         */
        //SeisDataDAO.readAssessedCommandTable(selectedSeisEvent.getEvid(), assessedCommandList.getAssessedCommandList());

        Global.logDebug("#Hypocentres:" + hypocentresList.getHypocentres().size()
                + " #Phases:" + phasesList.getPhases().size()
                + " #Commands:" + commandList.getCommandList().size()
                + " #AssessedCommands:" + assessedCommandList.getAssessedCommandList().size());

    }
    /*
     * Table's new row or new event is selected 
     * Trigger the change of all the regestered listeners (observers)
     */

    @Override
    public void valueChanged(ListSelectionEvent e) {
        System.out.println(Global.debugAt() + " New SeisEvent is selected.");
        // disable the double calls
        if (!e.getValueIsAdjusting()) {
            int selectedRowNum = table.getSelectedRow();
            // get selected evid.
            int selectedEvid = (Integer) table.getValueAt(selectedRowNum, 0);
            selectedSeisEvent.setValues(eventsList.getEvents().get(selectedRowNum));

            loadSelectedSeisEventData();

            System.out.println(Global.debugAt() + "Selected SiesEventId= " + selectedEvid + ". Fire an event.");
            selectedSeisEvent.fireSeisDataChanged();
        }
    }

    private void setupTableVisualAttributes() {

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        th.setBackground(new Color(43, 87, 151));
        th.setForeground(Color.white);

        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);
        table.setSelectionBackground(new Color(45, 137, 239));
        table.setSelectionForeground(Color.WHITE);
        //table.setRowSelectionInterval(0, 0);

        table.setRowHeight(25);
        table.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        table.setShowGrid(false);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);

        // Set: Left or Right aligned
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);

        // This part of the code picks good column sizes. 
        // If all column heads are wider than the column's cells'
        // contents, then you can just use column.sizeWidthToFit().
        // EventsTableModel model = (EventsTableModel) table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;

        Object[] longValues = tableModel.longValues;
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(
                    null, column.getHeaderValue(),
                    false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = table.getDefaultRenderer(tableModel.getColumnClass(i))
                    .getTableCellRendererComponent(table,
                            longValues[i], false, false, 0, i);

            cellWidth = comp.getPreferredSize().width;

            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }

    }

    public BlockTableModel getBlockTableModel() {
        return this.blockTableModel;
    }

}
