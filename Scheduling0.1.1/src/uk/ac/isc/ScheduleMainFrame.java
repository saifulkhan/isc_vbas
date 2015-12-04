package uk.ac.isc;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import uk.ac.isc.data.BlockTableModel;
import uk.ac.isc.data.SeisEvent;
import uk.ac.isc.data.SeisEventList;
import uk.ac.isc.data.SeisEventsDAO;
import uk.ac.isc.view.AssignControlPanel;
import uk.ac.isc.view.MapPanel;
import uk.ac.isc.view.TimelinePanel;

/**
 *
 * @author hui
 */
public class ScheduleMainFrame extends JFrame implements TableModelListener {
    
    /*save all the events info assigned or unassigned*/
    private SeisEventList allEvents = null;
    
    /*flag to check the database loading*/
    private boolean bDataLoadSuccess;
    
    private final HashMap<Integer,Integer> selEv = new HashMap<>();
    
    /*get the range of showing the data*/
    private final Date startDate;
    
    private final Date endDate;
    
    /**Panes for presenting the panels*/
    private final JSplitPane interactPane;
    
    private final JSplitPane ctTimeMapPane;
    
    private final TimelinePanel tPanel;
    
    private final MapPanel mPanel;
    
    private final AssignControlPanel bControlPanel;
    
    /**Another pane for putting Interaction Pane and JTable*/
    private final JSplitPane interactWithTable;
    
    private final JScrollPane scrollPane;
    
    private final JTable blockTable;
    
    private final BlockTableModel blockTableModel;
    
    ScheduleMainFrame()
    {
        /*get the time range of all the data*/
        startDate = SeisEventsDAO.retrieveStartDate();
        startDate.setHours(0);
        startDate.setMinutes(0);
        startDate.setSeconds(0);
        endDate = SeisEventsDAO.retrieveEndDate();
        endDate.setHours(23);
        endDate.setMinutes(59);
        endDate.setSeconds(59);
        
        allEvents = new SeisEventList();
        
        /*load the events*/
        bDataLoadSuccess = SeisEventsDAO.retrieveAllEvents(allEvents.getSeisEvents());
        bDataLoadSuccess = SeisEventsDAO.retrieveAllocatedEvID(selEv);
        /*set assigned flag to events*/
        for(SeisEvent se:allEvents.getSeisEvents())
        {
            if(selEv.containsKey(se.getEvid()))
            {
                se.setblAssigned(true);
                se.setBlockID(selEv.get(se.getEvid()));
            }
        }
        
        this.setSize(1350,900);
        this.setLayout(new BorderLayout());
        
        tPanel = new TimelinePanel(startDate,endDate,allEvents);
        
        mPanel = new MapPanel(allEvents, false);
        bControlPanel = new AssignControlPanel(tPanel,mPanel,startDate,endDate,allEvents);
        
        ctTimeMapPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tPanel, mPanel);
        ctTimeMapPane.setSize(1350,900);
        ctTimeMapPane.setDividerLocation(0.33d);
        
        interactPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, bControlPanel,ctTimeMapPane);
        interactPane.setSize(1350,1000);
        interactPane.setDividerLocation(0.05d);
        
        blockTableModel = new BlockTableModel();
        blockTableModel.addTableModelListener(this);
        
        blockTable = new JTable(blockTableModel);
        scrollPane = new JScrollPane(blockTable);
        
        bControlPanel.setBlockTableModel(blockTableModel);
        
        interactWithTable = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,interactPane,scrollPane);
        interactWithTable.setSize(1800,1000);
        interactWithTable.setDividerLocation(0.75);
        
        this.setSize(1810,1010);
        this.setLayout(new BorderLayout());
        this.add(interactWithTable,BorderLayout.CENTER);
        
        allEvents.addObserver(tPanel);
        allEvents.addObserver(mPanel);
        
    }
    
    public static void main(String [] args)
    {
        java.awt.EventQueue.invokeLater(() -> {
        new ScheduleMainFrame().setVisible(true);
        });
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        blockTableModel.reload();
        //blockTable.repaint();
    }
    
}
