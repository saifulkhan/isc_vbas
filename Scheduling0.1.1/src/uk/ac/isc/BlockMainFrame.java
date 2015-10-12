
package uk.ac.isc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import uk.ac.isc.data.BlockTableModel;
import uk.ac.isc.data.SeisEvent;
import uk.ac.isc.data.SeisEventList;
import uk.ac.isc.data.SeisEventsDAO;
import uk.ac.isc.data.TaskBlock;
import uk.ac.isc.view.BlockControlPanel;
import uk.ac.isc.view.BlockInfoPanel;
import uk.ac.isc.view.BlockTextPanel;
import uk.ac.isc.view.MapPanel;

/**
 *
 * @author hui
 */
public class BlockMainFrame extends JFrame implements ListSelectionListener {
    
    /*save all the events info assigned or unassigned*/
    private SeisEventList allEvents = null;
    
    /*this for the selcted block*/
    private SeisEventList blockEvents = null;
    
    /*flag to check the database loading*/
    private boolean bDataLoadSuccess;
    
    private final HashMap<Integer,Integer> selEv = new HashMap<>();
    
    /*get the range of showing the data*/
    private final Date startDate;
    
    private final Date endDate;
    
    /**Views here: we need two panels, one is blockinfo panel and the other is blockdetailpanel*/
    private final BlockTableModel blockTableModel;
    
    private final JTable blockTable;
    
    private int bid;
    
    private final BlockControlPanel bcp;
    
    private final MapPanel mpanel;

    private final BlockInfoPanel biPanel;
    
    private final BlockTextPanel bTextPanel;
    
    /**structure the view*/
    private final JSplitPane individualBlockPane;
    
    private final JSplitPane mainPane;
    
    public BlockMainFrame()
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
        
        //load the block infos first
        blockTableModel = new BlockTableModel();
        
        /*fill in the events number*/
        bDataLoadSuccess = SeisEventsDAO.retrieveBlockEventNumber(blockTableModel.getTaskBlocks());
        bDataLoadSuccess = SeisEventsDAO.retrieveBlockReviewedEventNumber(blockTableModel.getTaskBlocks());
        
        blockTable = new JTable(blockTableModel);
        blockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        blockTable.setColumnSelectionAllowed(false);
        
        if(blockTableModel.getTaskBlocks().size()>0)
        {
            blockTable.setRowSelectionInterval(0, 0);
            bid = (Integer) blockTable.getValueAt(0, 0);
        }
        
        blockTable.getSelectionModel().addListSelectionListener(this);
        
        allEvents = new SeisEventList();
        
        /*load the events*/
        bDataLoadSuccess = SeisEventsDAO.retrieveAllEvents(allEvents.getSeisEvents());
        bDataLoadSuccess = SeisEventsDAO.retrieveAllocatedEvID(selEv);
        
        /*set assigned flag to events*/
        //set the phase number for the info
        //initialize the phase number of blocks
        for(TaskBlock tb:blockTableModel.getTaskBlocks())
        {
            tb.setPhaseNumber(0);
        }
        
        for(SeisEvent se:allEvents.getSeisEvents())
        {
            
            if(selEv.containsKey(se.getEvid()))
            {
                se.setblAssigned(true);
                se.setBlockID(selEv.get(se.getEvid()));
                for(TaskBlock tb:blockTableModel.getTaskBlocks())
                {
                    if(Objects.equals(tb.getBlockID(), selEv.get(se.getEvid())))
                    {
                        tb.setPhaseNumber(tb.getPhaseNumber()+se.getPhaseNumber());
                    }
                }
                
            }
        }
        
        this.setSize(1350,900);
        this.setLayout(new BorderLayout());
        
        biPanel = new BlockInfoPanel(blockTableModel,blockTable);
        //allEvents.addObserver(biPanel.getAPanel());
        //allEvents.addObserver(biPanel.getBPanel());
                
        bcp = new BlockControlPanel(startDate, endDate, allEvents,biPanel);
        
        if(blockTableModel.getTaskBlocks().size()>0)
        {
            bid = (Integer) blockTable.getValueAt(blockTable.getSelectedRow(), 0);
        }
        
        blockEvents = new SeisEventList();
        for(SeisEvent se:allEvents.getSeisEvents())
        {
            if(se.getBlockID()!= null && se.getBlockID().equals(bid))
            {
                blockEvents.getSeisEvents().add(se);
            }
        }
        
        /*map panel and text area*/
        mpanel = new MapPanel(blockEvents,true);
        mpanel.setMapSize(400);
        mpanel.removeMouseListener(mpanel);
        
        if(blockTableModel.getTaskBlocks().size()>0)
        {
            bTextPanel = new BlockTextPanel(blockTableModel.getTaskBlocks().get(0));
        }
        else
        {
            bTextPanel = new BlockTextPanel();
        }
        
        individualBlockPane =  new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,mpanel,bTextPanel);
        individualBlockPane.setSize(new Dimension(800,450));
        individualBlockPane.setDividerLocation(0.7);
        
        mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,biPanel,individualBlockPane);
        mainPane.setSize(800,900);
        mainPane.setDividerLocation(0.5);
        
        this.add(mainPane,BorderLayout.CENTER);
        this.add(bcp,BorderLayout.NORTH);
        
    }
    
    public static void main(String [] args)
    {
        java.awt.EventQueue.invokeLater(() -> {
        new BlockMainFrame().setVisible(true);
        });
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        
        bid = (Integer) blockTable.getValueAt(blockTable.getSelectedRow(), 0);
        blockEvents = new SeisEventList();
        for(SeisEvent se:allEvents.getSeisEvents())
        {
            if(se.getBlockID()!=null && se.getBlockID().equals(bid))
            {
                blockEvents.getSeisEvents().add(se);
            }
        }
                
        mpanel.setEvents(blockEvents); 
        
        bTextPanel.setTaskBlock(blockTableModel.getTaskBlocks().get(blockTable.getSelectedRow()));
        //this.repaint();
    }

}

