package uk.ac.isc.seiseventselection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
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

    private JTable seisEventTable;
    private SiesEventsTableModel tableModel;
    private SeisEventSearchPanel eventsSearchPanel;

    /*
     * All seisevents will be loaded first.
     */
    private static final SeisEventsList seisEventsList = Global.getSeisEventsList();
    private static SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();

    public SeisEventsTable() {
        seisEventTable = new JTable();
        tableModel = new SiesEventsTableModel(seisEventsList.getEvents());
        seisEventTable.setModel(tableModel);

        // Not used this listener
        // add listener for the selection change
        seisEventTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                // disable the double calls
                if (!lse.getValueIsAdjusting()) {
                    //onValueChanged(lse);
                }
            }
        });

        seisEventTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                onMouseClicked(evt);
            }
        });

        setupTableVisualAttributes();
        seisEventTable.getSelectionModel().setSelectionInterval(0, 0); // highlight first row which is shown by other views.

        // Action buttons, search panel
        // layout all together
        eventsSearchPanel = new SeisEventSearchPanel(this);
        this.setLayout(new BorderLayout());
        this.add(eventsSearchPanel, BorderLayout.PAGE_START);
        this.add(new JScrollPane(seisEventTable), BorderLayout.CENTER);

        seisEventsList.addChangeListener(this);
    }

    public JTable getSeisEventTable() {
        return seisEventTable;
    }

    public void onMouseClicked(MouseEvent e) {
        int selectedRow = seisEventTable.getSelectedRow();
        int selectedCol = seisEventTable.getSelectedColumn();

        VBASLogger.logDebug("New SeisEvent is selected. selectedRow=" + selectedRow);

        if (selectedRow >= 0) {
            int selectedEvid = (Integer) seisEventTable.getValueAt(selectedRow, 0);
            // another SeisEvent is selected
            selectedSeisEvent.setValues(seisEventsList.getEvents().get(selectedRow));
            Global.loadSelectedSeisEventData();

            VBASLogger.logDebug("SiesEvent= " + selectedEvid + ". Fire SiesEvent selected event.");
            selectedSeisEvent.fireSeisDataChanged();
        }
    }

    // When the SeisEventList changes:
    // Fired by Done, Commit, and Allocate 
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {

        VBASLogger.logDebug("Event received from " + event.getData().getClass().getName());

        
        /* Do not load the eventlist from DB. 
        For Done/banish command*/
        /*
         tableModel = new SiesEventsTableModel(seisEventsList.getEvents());
         seisEventTable.setModel(tableModel);
         setupTableVisualAttributes();
        // highlight the row that was selected!
         seisEventTable.getSelectionModel().setSelectionInterval(0, seisEventTable.getSelectedRow()); 
         */
        
        /* Load new SeiesEvent data */
        Global.loadSeisEventsList();
        tableModel = new SiesEventsTableModel(seisEventsList.getEvents());
        seisEventTable.setModel(tableModel);
        setupTableVisualAttributes();
        
        //if after loading data (table) the previously selected event is not there, then select the first event */
        if (searchSeiesEvent(selectedSeisEvent.getEvid()) == null) {
            VBASLogger.logDebug("Selecting first SeisEvent.");
            selectedSeisEvent.setValues(seisEventsList.getEvents().get(0));
        }

        Global.loadSelectedSeisEventData();
        selectedSeisEvent.fireSeisDataChanged();
    }

    /* (a) Search a SeisEvent in the table 
     * (b) Highlight if found 
     */
    public Boolean searchSeiesEvent(Integer evid) {

        Boolean found = false;

        for (int row = 0; row < seisEventTable.getRowCount(); row++) {
            Integer searchedEv = (Integer) seisEventTable.getValueAt(row, 0);
            if (evid.equals(searchedEv)) {
                seisEventTable.getSelectionModel().setSelectionInterval(row, row);

                //scroll to the selection
                JViewport viewport = (JViewport) seisEventTable.getParent();
                Rectangle rect = seisEventTable.getCellRect(seisEventTable.getSelectedRow(), 0, true);
                Rectangle r2 = viewport.getVisibleRect();
                seisEventTable.scrollRectToVisible(new Rectangle(rect.x, rect.y, (int) r2.getWidth(),
                        (int) r2.getHeight()));
                found = true;
                break;
            }
        }
        VBASLogger.logDebug("Searching SeisEvent:" + evid + " found:" + found);
        return found;
    }

    private void setupTableVisualAttributes() {

        JTableHeader th = seisEventTable.getTableHeader();
        th.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        /*th.setBackground(new Color(43, 87, 151));
         th.setForeground(Color.white);*/

        seisEventTable.setRowSelectionAllowed(true);
        seisEventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        seisEventTable.setColumnSelectionAllowed(false);
        //table.setSelectionBackground(new Color(45, 137, 239));
        //table.setSelectionForeground(Color.WHITE);
        //table.setRowSelectionInterval(0, 0);

        seisEventTable.setRowHeight(25);
        seisEventTable.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        seisEventTable.setShowGrid(false);
        seisEventTable.setShowVerticalLines(false);
        seisEventTable.setShowHorizontalLines(false);

        // Set: Left or Right aligned
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();

        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        seisEventTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        seisEventTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        seisEventTable.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);
        seisEventTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        seisEventTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        seisEventTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        seisEventTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        seisEventTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        // done or finishDate is set
        seisEventTable.getColumnModel().getColumn(0).setCellRenderer(new SeisEventTableCellRender());
        seisEventTable.getColumnModel().getColumn(1).setCellRenderer(new SeisEventTableCellRender());
        seisEventTable.getColumnModel().getColumn(2).setCellRenderer(new SeisEventTableCellRender());
        seisEventTable.getColumnModel().getColumn(3).setCellRenderer(new SeisEventTableCellRender());
        seisEventTable.getColumnModel().getColumn(4).setCellRenderer(new SeisEventTableCellRender());
        seisEventTable.getColumnModel().getColumn(5).setCellRenderer(new SeisEventTableCellRender());
        seisEventTable.getColumnModel().getColumn(6).setCellRenderer(new SeisEventTableCellRender());
        seisEventTable.getColumnModel().getColumn(7).setCellRenderer(new SeisEventTableCellRender());

        // This part of the code picks good column sizes. 
        // If all column heads are wider than the column's cells'
        // contents, then you can just use column.sizeWidthToFit().
        // SiesEventsTableModel model = (SiesEventsTableModel) seisEventTable.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;

        Object[] longValues = tableModel.longValues;
        TableCellRenderer headerRenderer = seisEventTable.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            column = seisEventTable.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = seisEventTable.getDefaultRenderer(tableModel.getColumnClass(i))
                    .getTableCellRendererComponent(seisEventTable, longValues[i], false, false, 0, i);

            cellWidth = comp.getPreferredSize().width;
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }

    }

    /*
     **********************************************************************************
     * Code to render seisEventTable cell
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
            Boolean isBanish = seisEventsList.getEvents().get(row).getIsBanish();

            if (isBanish) {
                //VBASLogger.logDebug("Banish (Orange) evid=" + seisEventsList.getEvents().get(row).getEvid());
                cellComponent.setForeground(Color.ORANGE);

            } else if (finishDate != null) {
                //VBASLogger.logDebug("Done (Gray) evid=" + seisEventsList.getEvents().get(row).getEvid());
                cellComponent.setForeground(Color.LIGHT_GRAY);
            } else {
                cellComponent.setForeground(Color.BLACK);
            }

            return cellComponent;
        }
    }

}
