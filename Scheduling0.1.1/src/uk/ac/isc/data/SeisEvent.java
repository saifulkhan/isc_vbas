package uk.ac.isc.data;

import java.util.Date;

/**
 * this is an earthquake event class for th scheduling system
 * @author hui
 * 
 */
public class SeisEvent {
    
    private Integer evid;
    
    private Double lat;
    
    private Double lon;
    
    private Date origTime;
    
    private Integer hypoNumber;
    
    private Integer phaseNumber;
    
    //private Integer msec;
    
    /** two boolean variables for selection*/
    private boolean timeSelection;
    
    private boolean geoSelection;
    
    /** one boolean variable to record block assigned*/
    private boolean blAssigned;
    
    private boolean bReviewed;
    
    private Integer blockID;
    
    public SeisEvent(Integer evid)
    {
        this.evid = evid;
        
        this.timeSelection = true;
        this.geoSelection = false;
        
        this.hypoNumber = 0;
        this.phaseNumber = 0;
        
        this.blAssigned = false;
        this.bReviewed = false;
        
        this.blockID = null;
    }
    
    public void setEvid(Integer evid)
    {
        this.evid = evid;
    }   
    
    public Integer getEvid()
    {
        return this.evid;
    }

    public void setLat(double latitude)
    {
        this.lat = latitude;
    }
    
    public Double getLat()
    {
        return lat;
    }
    
    public void setLon(double longitude)
    {
        this.lon = longitude;
    }
    
    public Double getLon()
    {
        return lon;
    }
    
    public Date getOrigTime()
    {
        return this.origTime;
    }
    
    public void setOrigTime(Date dt)
    {
        this.origTime = dt;
    }
        
    public Integer getHypoNumber()
    {
        return this.hypoNumber;
    }
    
    public void setHypoNumber(Integer hypoNumber)
    {
        this.hypoNumber = hypoNumber;
    }
        
    public Integer getPhaseNumber()
    {
        return this.phaseNumber;
    }
    
    public void setPhaseNumber(Integer phaseNumber)
    {
        this.phaseNumber = phaseNumber;
    }
    
    public void setTSelection(boolean tsel)
    {
        this.timeSelection = tsel;
    }
    
    public boolean getTSelction()
    {
        return this.timeSelection;
    }
    
    public void setGSelection(boolean gsel)
    {
        this.geoSelection = gsel;
    }
    
    public boolean getGSelction()
    {
        return this.geoSelection;
    }
    
    public void setblAssigned(boolean bAss)
    {
        this.blAssigned = bAss;
    }
    
    public boolean getblAssigned()
    {
        return this.blAssigned;
    }
        
    public void setbReviewed(boolean bRev)
    {
        this.bReviewed = bRev;
    }
    
    public boolean getbReviewed()
    {
        return this.bReviewed;
    }
    
    public void setBlockID(Integer bid)
    {
        this.blockID = bid;
    }
    
    public Integer getBlockID()
    {
        return this.blockID;
    }
    
}
