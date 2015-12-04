/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.isc.data;

import java.util.ArrayList;
import java.util.Observable;

/**
 *
 * @author hui
 */
public class SeisEventList extends Observable {
    
    ArrayList<SeisEvent> seisEvents = null;
    
    public SeisEventList()
    {
        seisEvents = new ArrayList<>();
    }
    
    public void setSeisEvents(ArrayList<SeisEvent> se)
    {
        this.seisEvents = se;
    }
    
    public ArrayList<SeisEvent> getSeisEvents()
    {
        return this.seisEvents;
    }
    
    public void setChangeFlag()
    {
        setChanged();
    }
    
}
