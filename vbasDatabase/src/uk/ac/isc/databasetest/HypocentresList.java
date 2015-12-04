
package uk.ac.isc.databasetest;

import java.util.ArrayList;

/**
 *
 * @author hui
 */
public class HypocentresList extends AbstractSeisData {
    
    private final ArrayList<Hypocentre> hypos;
    
    public HypocentresList() 
    {
        hypos = new ArrayList<Hypocentre>();
    }
    
    public ArrayList<Hypocentre> getHypocentres()
    {
        return this.hypos;
    }

}
