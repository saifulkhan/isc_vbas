/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.isc.databasetest;

import java.util.EventObject;

/**
 *
 * @author hui
 */
public class SeisDataChangeEvent extends EventObject {
    private SeisData data;
    
    public SeisDataChangeEvent(Object source, SeisData data)
    {
        super(source);
        this.data = data;
    }
    
    public SeisData getData() {
        return this.data;
    }
}
