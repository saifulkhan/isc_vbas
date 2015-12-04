
package uk.ac.isc.databasetest;

/**
 *
 * @author hui
 * this is a real earthquake event, it is different with changeEvent which triggers listener as observer model
 */
public class SeisEvent extends AbstractSeisData {
    
    private Integer evid;
    
    private Hypocentre primeHypo;
    
    private Integer phaseNumber;
    
    private Double magnitude;
    
    private String location;
    
    private Integer grn;
    
    private Integer srn;
    
    private Integer defaultDepth;
    
    public SeisEvent(Integer evid)
    {
        this.evid = evid;
        this.phaseNumber = 0;
    }
    
    public void setEvid(Integer evid)
    {
        this.evid = evid;
    }   
    
    public Integer getEvid()
    {
        return this.evid;
    }
    
    public void setPrimeHypo(Hypocentre prime)
    {
        this.primeHypo = prime;
    }
    
    public Hypocentre getPrimeHypo()
    {
        return this.primeHypo;
    }
    
    public Integer getPhaseNumber()
    {
        return this.phaseNumber;
    }
    
    public void setPhaseNumber(Integer phaseNumber)
    {
        this.phaseNumber = phaseNumber;
    }
    
    //normally, there are a lot of magnitude types, we use ISC magnitude here 
    //no matter if it is mb or Ms
    public Double getMagnitude()
    {
        return this.magnitude;
    }
    
    public void setMagnitude(Double magnitude)
    {
        this.magnitude = magnitude;
    }
    
    //the location actually is a seismic region, sorry for the misuse of term
    public String getLocation()
    {
        return this.location;
    }
    
    public void setLocation(String location)
    {
        this.location = location;
    }
    
    public Integer getGrn()
    {
        return this.grn;
    }
    
    public void setGrn(Integer grn)
    {
        this.grn = grn;
    }
    
    
    public Integer getSrn()
    {
        return this.srn;
    }
    
    public void setSrn(Integer srn)
    {
        this.srn = srn;
    }
    
    public Integer getDefaultDepth()
    {
        return this.defaultDepth;
    }
    
    public void setDefaultDepth(Integer defaultDepth)
    {
        this.defaultDepth = defaultDepth;
    }
    
}
