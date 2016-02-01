package uk.ac.isc.eventscontrolview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import uk.ac.isc.seisdata.BlockTableModel;
import uk.ac.isc.seisdata.EventsTableModel;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.SeisEventsList;

/**
 * It holds all the "phases", "hypocentres", and "events" data and passes
 * reference to other views.
 */
public class EventsTable extends JPanel implements ListSelectionListener {

    private JTable table = new JTable();                                      // the main table of the event list
    EventsTableModel tableModel;

    private static SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final BlockTableModel blockTableModel = new BlockTableModel();  // The space to keep all the data
    private static final SeisEventsList eventsList = new SeisEventsList();
      
    private static final Logger logger = Logger.getLogger(EventsTable.class.getName()); // each class may need logger to keep log file, too rush to follow the good practice 

    
    public EventsTable() {

        // 1. set the table and tooltips
       /* table = new JTable() {
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
        };*/

        // fill in the events number
        boolean retDAO = SeisDataDAO.retrieveBlockEventNumber(blockTableModel.getTaskBlocks());
        retDAO = SeisDataDAO.retrieveBlockReviewedEventNumber(blockTableModel.getTaskBlocks());
        if (retDAO == false) {
            logger.log(Level.SEVERE, "Fail to load task block list from database.");
        }

        // add data into evets list : retrieve the events from the database
        retDAO = SeisDataDAO.retrieveAllEvents(eventsList.getEvents());
        retDAO = SeisDataDAO.retrieveEventsMagnitude(eventsList.getEvents());
        retDAO = SeisDataDAO.retrieveAllPhaseNumber(eventsList.getEvents());
        retDAO = SeisDataDAO.retrieveAllRegionName(eventsList.getEvents());

        if (retDAO == false) {
            logger.log(Level.SEVERE, "Fail to load events list from database.");
        }

        // do not change the actual address of the selected SiesEvent.
        selectedSeisEvent.setValues(eventsList.getEvents().get(0));  
                
        tableModel = new EventsTableModel(eventsList.getEvents());
        table.setModel(tableModel);
        
        // add listener for the selection change
        table.getSelectionModel().addListSelectionListener(this);  
        setupTableVisualAttributes();
       
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
            int selectedEvid = (Integer) table.getValueAt(selectedRowNum, 0);     // get selected evid.
            selectedSeisEvent.setValues(eventsList.getEvents().get(selectedRowNum));            // do not change the actual address of the selected SiesEvent.
        
            System.out.println(Global.debugAt() + "Selected SiesEventId= " + selectedEvid + ". Fire an event.");
            selectedSeisEvent.fireSeisDataChanged();
        }
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
        table.setRowSelectionInterval(0, 0);

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

    public JTable getTable() {
        return this.table;
    }

    public BlockTableModel getBlockTableModel() {
        return this.blockTableModel;
    }

}
