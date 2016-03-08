/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.isc.seisdata;

import java.util.EventListener;

/**
 * Any class inherits this class can be a listener of seisdatachange event
 *
 * @author hui
 */
public interface SeisDataChangeListener extends EventListener {

    public void SeisDataChanged(SeisDataChangeEvent event);

}
