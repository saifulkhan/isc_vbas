
package uk.ac.isc.seisdata;

import java.util.ArrayList;

/**
 * A wrapper for the Event List
 * @author hui
 */
public class SeisEventsList extends AbstractSeisData {
    
    private final ArrayList<SeisEvent> events;
    
    public SeisEventsList() 
    {
        events = new ArrayList<SeisEvent>();
    }
    
    public ArrayList<SeisEvent> getEvents()
    {
        return this.events;
    }
    
}
