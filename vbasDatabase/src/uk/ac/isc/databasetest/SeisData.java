
package uk.ac.isc.databasetest;

/**
 *
 * @author hui
 */
public interface SeisData {
    
    /**
     * Add an object for notifying the change from the data
     * @param listener 
     */
    public void addChangeListener(SeisDataChangeListener listener);
    
    /**
     * Remove an object for notifying the data change
     * @param listener 
     */
    public void removeChangeListener(SeisDataChangeListener listener);
    
}
