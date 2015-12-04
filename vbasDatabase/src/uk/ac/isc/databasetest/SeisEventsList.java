
package uk.ac.isc.databasetest;

import java.util.ArrayList;

/**
 *
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
