/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.isc.databasetest;

import java.util.EventListener;

/**
 *
 * @author hui
 */
public interface SeisDataChangeListener extends EventListener {
    
    public void SeisDataChanged(SeisDataChangeEvent event);
    
}
