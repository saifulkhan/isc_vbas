package uk.ac.isc.seisdata;

import java.util.ArrayList;
import java.util.Date;

/**
 * this is a real earthquake event, it is different with changeEvent which
 * triggers listener as observer model
 *
 */
public class SeisEvent extends AbstractSeisData {

    private Integer evid = null;    // event id
    private Hypocentre primeHypo;   // reference of prime hypocentre
    private Integer phaseNumber;    // phse number associated with this event
    private Double magnitude;       // a reference magnitude    
    private String location;        // the region name
    private Integer grn;            // these two ref, please ask James
    private Integer srn;
    private Integer defaultDepth;   // default Depth of the event
    private String eType;

    private Double defaultDepthGrid = null;
    private String locatorMessage = null;
    private String nearbyEvents = null;
    
    /*
    'Done' will set this to 'now' date. 
     If not null, i.e., the event is done and highlight as gray.
    */
    private Date finishDate;
    private Boolean isBanish = null;

    private ArrayList<Duplicates> duplicatesList = null;

    public SeisEvent() {
        //
    }

    public SeisEvent(Integer evid,
            String eType,
            Double defaultDepthGrid,
            String locatorMessage,
            String nearbyEvents,
            Boolean isBanish,
            Date finishDate,
            ArrayList<Duplicates> duplicatesList) {
        
        this.evid = evid;
        this.eType = eType;
        this.defaultDepthGrid = defaultDepthGrid;
        this.locatorMessage = locatorMessage;
        this.nearbyEvents = nearbyEvents;
        this.finishDate = finishDate;
        this.isBanish = isBanish;
        this.duplicatesList = duplicatesList;

        this.phaseNumber = 0;
        //VBASLogger.logDebug(this.evid + ", " + this.defaultDepthGrid + ", " + this.locatorMessage);
    }

    public void setValues(SeisEvent e) {
        this.evid = e.evid;
        this.primeHypo = e.primeHypo;
        this.phaseNumber = e.phaseNumber;
        this.magnitude = e.magnitude;
        this.location = e.location;
        this.grn = e.grn;
        this.srn = e.srn;
        this.defaultDepth = e.defaultDepth;
        this.eType = e.eType;
        this.defaultDepthGrid = e.defaultDepthGrid;
        this.locatorMessage = e.locatorMessage;
        this.nearbyEvents = e.nearbyEvents;
        this.finishDate = e.finishDate;
        this.isBanish = e.isBanish;
        this.duplicatesList = e.duplicatesList;
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

    public Double getDefaultDepthGrid() {
        return defaultDepthGrid;
    }

    public String getLocatorMessage() {
        return locatorMessage;
    }

    public String getNearbyEvents() {
        return nearbyEvents;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Boolean getIsBanish() {
        return isBanish;
    }

    public void setIsBanish(Boolean isBanish) {
        this.isBanish = isBanish;
    }

    public ArrayList<Duplicates> getDuplicates() {
        return duplicatesList;
    }
    
}