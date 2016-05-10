package uk.ac.isc.seisdata;

/**
 * this is a real earthquake event, it is different with changeEvent which
 * triggers listener as observer model
 *
 * @author hui
 *
 */
public class SeisEvent extends AbstractSeisData {

    private Integer evid;           //event id
    private Hypocentre primeHypo;   //reference of prime hypocentre
    private Integer phaseNumber;    //phse number associated with this event
    private Double magnitude;       //a reference magnitude    
    private String location;        //the region name
    private Integer grn;            //these two ref, please ask James
    private Integer srn;
    private Integer defaultDepth;   //default Depth of the event
    private String eType;

    public SeisEvent() {
        this.evid = 0;
    }

    public SeisEvent(Integer evid, String eType) {
        this.evid = evid;
        this.eType = eType;
        this.phaseNumber = 0;
    }

    public void setValues(SeisEvent another) {
        this.evid = another.evid;
        this.primeHypo = another.primeHypo;
        this.phaseNumber = another.defaultDepth;
        this.magnitude = another.magnitude;
        this.location = another.location;
        this.grn = another.defaultDepth;
        this.srn = another.defaultDepth;
        this.defaultDepth = another.defaultDepth;
        this.eType = another.eType;
    }

    public void setEvid(Integer evid) {
        this.evid = evid;
    }

    public Integer getEvid() {
        return this.evid;
    }

    public void setPrimeHypo(Hypocentre prime) {
        this.primeHypo = prime;
    }

    public Hypocentre getPrimeHypo() {
        return this.primeHypo;
    }

    public Integer getPhaseNumber() {
        return this.phaseNumber;
    }

    public void setPhaseNumber(Integer phaseNumber) {
        this.phaseNumber = phaseNumber;
    }

    //normally, there are a lot of magnitude types, we use ISC magnitude here 
    //no matter if it is mb or Ms
    public Double getMagnitude() {
        return this.magnitude;
    }

    public void setMagnitude(Double magnitude) {
        this.magnitude = magnitude;
    }

    //the location actually is a seismic region, sorry for the misuse of term
    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getGrn() {
        return this.grn;
    }

    public void setGrn(Integer grn) {
        this.grn = grn;
    }

    public Integer getSrn() {
        return this.srn;
    }

    public void setSrn(Integer srn) {
        this.srn = srn;
    }

    public Integer getDefaultDepth() {
        return this.defaultDepth;
    }

    public void setDefaultDepth(Integer defaultDepth) {
        this.defaultDepth = defaultDepth;
    }

    public String geteType() {
        return eType;
    }

    public void seteType(String eType) {
        this.eType = eType;
    }
}
