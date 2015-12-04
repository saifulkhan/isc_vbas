
package uk.ac.isc.view;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import uk.ac.isc.data.SeisEvent;
import uk.ac.isc.data.SeisEventList;
import uk.ac.isc.data.SeisEventsDAO;

/**
 * 
 * @author hui
 */
public class AssignMainPanel extends JPanel {
    
     /*save all the events info assigned or unassigned*/
    private final SeisEventList allEvents;
    
    /*get the range of showing the data*/
    private final Date startDate;
    
    private final Date endDate;
    
    private final TimelinePanel tPanel;
    
    private final MapPanel mPanel;
    
    private final AssignControlPanel bControlPanel;
    
    /**structure panes*/
    private final JSplitPane interactPane;
    
    public AssignMainPanel(Date sDate, Date eDate, SeisEventList seList)
    {
        this.allEvents = seList;
        this.startDate = sDate;
        this.endDate = eDate;
        
        /*build the three main panel*/
        tPanel = new TimelinePanel(startDate,endDate,allEvents);
        mPanel = new MapPanel(allEvents, false);
        bControlPanel = new AssignControlPanel(tPanel,mPanel,startDate,endDate,allEvents);
        
        interactPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tPanel,mPanel);
        interactPane.setSize(1400,850);
        interactPane.setDividerLocation(0.4d);
        
        this.setSize(1400,1000);
        this.setLayout(new BorderLayout());
        this.add(bControlPanel,BorderLayout.NORTH);
        this.add(interactPane,BorderLayout.CENTER);
        
        allEvents.addObserver(tPanel);
        allEvents.addObserver(mPanel);
    }
    
    /*public static void main(String[] args)
    {
        JFrame frame = new JFrame("Assignment Panel Test");
        frame.setSize(1400, 1000);
        
        Date startDate = SeisEventsDAO.retrieveStartDate();
        startDate.setHours(0);
        startDate.setMinutes(0);
        startDate.setSeconds(0);
        Date endDate = SeisEventsDAO.retrieveEndDate();
        endDate.setHours(23);
        endDate.setMinutes(59);
        endDate.setSeconds(59);
        
        SeisEventList events = new SeisEventList();
        HashMap<Integer,Integer> selEv = new HashMap<>();
        
        //load the events
        SeisEventsDAO.retrieveAllEvents(events.getSeisEvents());
        SeisEventsDAO.retrieveAllocatedEvID(selEv);
        //set assigned flag to events
        for(SeisEvent se:events.getSeisEvents())
        {
            if(selEv.containsKey(se.getEvid()))
            {
                se.setblAssigned(true);
                se.setBlockID(selEv.get(se.getEvid()));
            }
        }
        
        frame.add(new AssignMainPanel(startDate,endDate,events));
        
                java.awt.EventQueue.invokeLater(() -> {
         frame.setVisible(true);
        });
    }*/
}
