package uk.ac.isc.seiseventselection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
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
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdatainterface.Global;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.SeisEventsList;
import uk.ac.isc.seisdata.VBASLogger;

/**
 * It holds all the "phases", "hypocentres", and "events" data and passes
 * reference to other views.
 */
public class SeisEventsTable extends JPanel implements SeisDataChangeListener {

    private JTable table;
    private SiesEventsTableModel tableModel;
    private SeisEventSearchPanel eventsSearchPanel;

    /*
     * All seisevents will be loaded first.
     */
    private static final SeisEventsList seisEventsList = Global.getSeisEventsList();
    private static SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();

    public SeisEventsTable() {
        table = new JTable();
        tableModel = new SiesEventsTableModel(seisEventsList.getEvents());
        table.setModel(tableModel);

        // Not used this listener
        // add listener for the selection change
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                // disable the double calls
                if (!lse.getValueIsAdjusting()) {
                    //onValueChanged(lse);
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                onMouseClicked(evt);
            }
        });

        setupTableVisualAttributes();
        table.getSelectionModel().setSelectionInterval(0, 0); // highlight first row which is shown by other views.

        // Action buttons, search panel
        // layout all together
        eventsSearchPanel = new SeisEventSearchPanel(table);
        this.setLayout(new BorderLayout());
        this.add(eventsSearchPanel, BorderLayout.PAGE_START);
        this.add(new JScrollPane(table), BorderLayout.CENTER);

        seisEventsList.addChangeListener(this);
    }


    /*
     * Table's new row or new event is selected 
     * Trigger the change of all the regestered listeners (observers)
     */
    /*
    public void onValueChanged(ListSelectionEvent e) {
        int selectedRowNum = table.getSelectedRow();             // get selected evid.
        VBASLogger.logDebug("New SeisEvent is selected. selectedRowNum=" + selectedRowNum);

        if (selectedRowNum > 0) {
            int selectedEvid = (Integer) table.getValueAt(selectedRowNum, 0);
            // another SeisEvent is selected
            selectedSeisEvent.setValues(seisEventsList.getEvents().get(selectedRowNum));
            Global.loadSelectedSeisEventData();

            VBASLogger.logDebug("SiesEvent= " + selectedEvid + ". Fire SiesEvent selected event.");
            selectedSeisEvent.fireSeisDataChanged();
        }
    }
    */
    
    public void onMouseClicked(MouseEvent e) {
        int selectedRow = table.getSelectedRow();
        int selectedCol = table.getSelectedColumn();

        VBASLogger.logDebug("New SeisEvent is selected. selectedRow=" + selectedRow);

        if (selectedRow > 0) {
            int selectedEvid = (Integer) table.getValueAt(selectedRow, 0);
            // another SeisEvent is selected
            selectedSeisEvent.setValues(seisEventsList.getEvents().get(selectedRow));
            Global.loadSelectedSeisEventData();

            VBASLogger.logDebug("SiesEvent= " + selectedEvid + ". Fire SiesEvent selected event.");
            selectedSeisEvent.fireSeisDataChanged();
        }
    }

    private void setupTableVisualAttributes() {

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        /*th.setBackground(new Color(43, 87, 151));
         th.setForeground(Color.white);*/

        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);
        //table.setSelectionBackground(new Color(45, 137, 239));
        //table.setSelectionForeground(Color.WHITE);
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
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(0).setCellRenderer(new SeisEventTableCellRender());

        // This part of the code picks good column sizes. 
        // If all column heads are wider than the column's cells'
        // contents, then you can just use column.sizeWidthToFit().
        // SiesEventsTableModel model = (SiesEventsTableModel) table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;

        Object[] longValues = tableModel.longValues;
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = table.getDefaultRenderer(tableModel.getColumnClass(i))
                    .getTableCellRendererComponent(table, longValues[i], false, false, 0, i);

            cellWidth = comp.getPreferredSize().width;
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }

    }

    // When the SeisEventList changes - Done command
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {

        VBASLogger.logDebug("Event received from " + event.getData().getClass().getName());

        int rowWasSelected = table.getSelectedRow();

        VBASLogger.logDebug("rowWasSelected=" + rowWasSelected);

        tableModel = new SiesEventsTableModel(seisEventsList.getEvents());
        table.setModel(tableModel);

        setupTableVisualAttributes();
        table.getSelectionModel().setSelectionInterval(0, rowWasSelected); // highlight the row that was selected!

    }

    /*
     **********************************************************************************
     * Code to render table cell
     * 
     ***********************************************************************************
     */
    class SeisEventTableCellRender extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            Component cellComponent
                    = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            Date finishDate = seisEventsList.getEvents().get(row).getFinishDate();
                if (finishDate != null) {
                VBASLogger.logDebug("#seisEventsList=" + seisEventsList.getEvents().size()
                        + ", row=" + row
                        + ", finishDate=" + (finishDate == null ? "null" : finishDate.toString())
                        + ", evid=" + seisEventsList.getEvents().get(row).getEvid());
                cellComponent.setForeground(Color.LIGHT_GRAY);
            } else {
                    cellComponent.setForeground(Color.BLACK);
                }

            return cellComponent;
        }
    }

    // FOR FUTURE REFERENCE    
    // NOTE: For future reference: How Block table is loaded?
    /*public void loadSeisEventsList() {
     VBASLogger.logDebug("Load SeisEvents list.");
     // fill in the events number
     SeisDataDAO.retrieveBlockEventNumber(blockTableModel.getTaskBlocks());
     SeisDataDAO.retrieveBlockReviewedEventNumber(blockTableModel.getTaskBlocks());

     // add data into evets list : retrieve the events from the database
     SeisDataDAO.retrieveAllEvents(seisEventsList.getEvents());
     SeisDataDAO.retrieveEventsMagnitude(seisEventsList.getEvents());
     SeisDataDAO.retrieveAllPhaseNumber(seisEventsList.getEvents());
     SeisDataDAO.retrieveAllRegionName(seisEventsList.getEvents());

     selectedSeisEvent.setValues(seisEventsList.getEvents().get(0));
     loadSelectedSeisEventData();
     }*/
//    public void loadSelectedSeisEventData() {
//
//        System.out.println(Global.debugAt() + "Load list of Hypocentre, Phase, Commmands, and AssessedCommands for SeisEvent: "
//                + selectedSeisEvent.getEvid());
//
//        /*
//         * Hypocentre
//         */
//        SeisDataDAO.retrieveHypos(selectedSeisEvent.getEvid(),
//                hypocentresList.getHypocentres());
//        SeisDataDAO.retrieveHyposMagnitude(hypocentresList.getHypocentres());
//        // as I remove all the hypos when clicking an event to retrieve the hypos, 
//        // so need reset prime hypo every time
//        // TODO: Saiful, What is this?
//        for (Hypocentre hypo : hypocentresList.getHypocentres()) {
//            if (hypo.getIsPrime() == true) {
//                selectedSeisEvent.setPrimeHypo(hypo);
//            }
//        }
//
//        /*
//         * Phase
//         */
//        SeisDataDAO.retrieveAllPhases(selectedSeisEvent.getEvid(), phasesList.getPhases());
//        SeisDataDAO.retrieveAllPhasesAmpMag(selectedSeisEvent.getEvid(),
//                phasesList.getPhases());
//        SeisDataDAO.retrieveAllStationsWithRegions(stations);
//        // load the correspondent map into the stataions
//        // put the region name into the pahseList
//        for (int i = 0; i < phasesList.getPhases().size(); i++) {
//            phasesList.getPhases()
//                    .get(i)
//                    .setRegionName(stations
//                            .get(phasesList
//                                    .getPhases()
//                                    .get(i)
//                                    .getReportStation()));
//        }
//
//        /*
//         * Commands
//         */
//        SeisDataDAO.readCommandTable(selectedSeisEvent.getEvid(), commandList.getCommandList());
//
//        /*
//         * AssessedCommand 
//         */
//        SeisDataDAO.readAssessedCommandTable(selectedSeisEvent.getEvid(), assessedCommandList.getAssessedCommandList());
//
//        VBASLogger.logDebug("#Hypocentres:" + hypocentresList.getHypocentres().size()
//                + " #Phases:" + phasesList.getPhases().size()
//                + " #Commands:" + commandList.getCommandList().size()
//                + " #AssessedCommands:" + assessedCommandList.getAssessedCommandList().size());
//
//    }
    /*public BlockTableModel getBlockTableModel() {
     return this.blockTableModel;
     }*/
}
