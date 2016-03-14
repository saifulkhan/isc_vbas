package uk.ac.isc.seisdata;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The data structure for hypocentre, currently we have 21 attributes
 *
 * @author hui
 */
public class Hypocentre extends AbstractSeisData implements Cloneable {

    private Integer evid;       // keep a reference of event
    private Integer hypid;      // hypocentre id
    private Hypocentre pHypo;   // prime hypocentre
    private Double lat;         // latitude
    private Double lon;         // longitude
    private Integer depth;      // depth
    private String agency;      // agency who reports
    private Date origTime;      // the origin time
    private Integer msec;       // with millisecond
    private Double errDepth;    // get the std errors for depth, magnitude (I might ignore the err of magnitude for others), location 
    private Integer numStations; // the number of stations 
    private Integer numDefStations; // the number of defining stations
    private Integer numPhases;      // the number of phases
    private Integer numDefPhases;   // the number of defining phases
    private Boolean isFixed;        // flag to show if the hypocentre depth is fixed or not
    private Boolean isPrime;                        // flag to show if the hypocetre is the prime hypocentre
    private LinkedHashMap<String, Double> magMap;   // all the magnitudes reported 
    private Boolean isSelected = false;             // add one boolean value for indicating if it is selected for interaction
    private Double stime;                           // here are some data from hypoc_err table for the reliablility of the hypocentre
    private Double strike;
    private Double smajax;
    private Double sminax;

    public Hypocentre() {
        this.hypid = 0;
        magMap = new LinkedHashMap<String, Double>();
    }

    public Hypocentre(String agency, Date dd, double lat, double lon, int depth) {

        this.origTime = dd;
        this.agency = agency;
        this.lat = lat;
        this.lon = lon;
        this.depth = depth;
        /*setOrigTime(dd);
         setAgency(agency);
         setLat(lat);
         setLon(lon);
         setDepth(depth);*/
    }

    public void setEvid(Integer evid) {
        this.evid = evid;
    }

    public Integer getEvid() {
        return this.evid;
    }

    public void setHypid(Integer hypid) {
        this.hypid = hypid;
    }

    public Integer getHypid() {
        return this.hypid;
    }

    public void setLat(double latitude) {
        this.lat = latitude;
    }

    public Double getLat() {
        return lat;
    }

    public void setLon(double longitude) {
        this.lon = longitude;
    }

    public Double getLon() {
        return lon;
    }

    public Date getOrigTime() {
        return this.origTime;
    }

    public void setOrigTime(Date dt) {
        this.origTime = dt;
    }

    public Integer getMsec() {
        return this.msec;
    }

    public void setMsec(Integer msec) {
        this.msec = msec;
    }

    public void setMagMap(LinkedHashMap<String, Double> magMap) {
        this.magMap = magMap;
    }

    public void addMagnitude(String magType, Double magValue) {
        if (magType != null && magValue != null) {
            magMap.put(magType, magValue);
        }
    }

    public HashMap<String, Double> getMagnitude() {
        return this.magMap;
    }

    /**
     * set and get the agency of hypocentre
     *
     * @param agency String to define the reported agency
     */
    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getAgency() {
        return agency;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    //a lot of getters and setters
    public Integer getDepth() {
        return this.depth;
    }

    public void setErrDepth(Double errDepth) {
        this.errDepth = errDepth;
        //fireSeisDataChanged();
    }

    public Double getErrDepth() {
        return this.errDepth;
    }

    public void setNumStations(Integer numStations) {
        this.numStations = numStations;
    }

    public Integer getNumStations() {
        return this.numStations;
    }

    public void setNumDefStations(Integer numDefStations) {
        this.numDefStations = numDefStations;
        //fireSeisDataChanged();
    }

    public Integer getNumDefStations() {
        return numDefStations;
    }

    public void setNumPhases(Integer numPhases) {
        this.numPhases = numPhases;
        //fireSeisDataChanged();
    }

    public Integer getNumPhases() {
        return this.numPhases;
    }

    public void setNumDefPhases(Integer numDefPhases) {
        this.numDefPhases = numDefPhases;
        //fireSeisDataChanged();
    }

    public Integer getNumDefPhases() {
        return numDefPhases;
    }

    public void setIsFixed(Boolean isFixed) {
        this.isFixed = isFixed;
        //fireSeisDataChanged();

    }

    public Boolean getIsFixed() {
        return this.isFixed;
    }

    /**
     * set and get prime and historic flags of hypocentre
     *
     * @param isPrime
     */
    public void setIsPrime(Boolean isPrime) {
        this.isPrime = isPrime;
        //fireSeisDataChanged();
    }

    public Boolean getIsPrime() {
        return isPrime;
    }

    public void setPrime(Hypocentre ph) {
        this.pHypo = ph;
    }

    public Hypocentre getPrime() {
        return this.pHypo;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
        fireSeisDataChanged();
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public Double getStime() {
        return this.stime;
    }

    public void setStime(Double strime) {
        this.stime = strime;
    }

    public Double getStrike() {
        return this.strike;
    }

    public void setStrike(Double strike) {
        this.strike = strike;
    }

    public Double getSmajax() {
        return this.smajax;
    }

    public void setSmajax(Double smajax) {
        this.smajax = smajax;
    }

    public Double getSminax() {
        return this.sminax;
    }

    public void setSminax(Double sminax) {
        this.sminax = sminax;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Hypocentre)) {
            return false;
        }
        Hypocentre that = (Hypocentre) obj;

        return this.getAgency().equals(that.getAgency()) && this.getDepth().equals(that.getDepth())
                && this.lat.equals(that.getLat()) && this.lon.equals(that.getLon());
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
        hash = 61 * hash + (int) (Double.doubleToLongBits(this.lon) ^ (Double.doubleToLongBits(this.lon) >>> 32));
        hash = 61 * hash + this.depth;
        hash = 61 * hash + Objects.hashCode(this.agency);
        return hash;
    }

    @Override
    public String toString() {
        String ret = "Agency: " + this.agency + " Depth: " + this.depth + " Lat: "
                + this.lat + " Lon: " + this.lon;
        return ret;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

        Hypocentre clone = (Hypocentre) super.clone();

        if (this.agency != null) {
            clone.setAgency(this.agency);
        }
        if (this.depth != null) {
            clone.setDepth(this.depth);
        }
        if (this.lat != null) {
            clone.setLat(this.lat);
        }
        if (this.lon != null) {
            clone.setLon(this.lon);
        }
        if (this.origTime != null) {
            clone.setOrigTime(this.origTime);
        }
        if (this.errDepth != null) {
            clone.setErrDepth(this.errDepth);
        }
        if (this.evid != null) {
            clone.setEvid(this.evid);
        }
        if (this.hypid != null) {
            clone.setHypid(this.hypid);
        }
        if (this.isFixed != null) {
            clone.setIsFixed(this.isFixed);
        }
        if (this.isPrime != null) {
            clone.setIsPrime(this.isPrime);
        }
        if (this.numDefPhases != null) {
            clone.setNumDefPhases(this.numDefPhases);
        }
        if (this.numDefStations != null) {
            clone.setNumDefStations(this.numDefStations);
        }
        if (this.numPhases != null) {
            clone.setNumPhases(this.numPhases);
        }
        if (this.numStations != null) {
            clone.setNumStations(numStations);
        }
        if (this.pHypo != null) {
            clone.setPrime(this.pHypo);
        }

        clone.magMap = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> entry : this.magMap.entrySet()) {
            clone.magMap.put(entry.getKey(), entry.getValue());
        }

        return clone;

    }

    public void setValues(Hypocentre h) {

        this.evid = h.evid;
        this.hypid = h.hypid;
        this.pHypo = h.pHypo;
        this.lat = h.lat;
        this.lon = h.lon;
        this.depth = h.depth;
        this.agency = h.agency;
        this.origTime = h.origTime;
        this.msec = h.msec;
        this.errDepth = h.errDepth;
        this.numStations = h.numStations;
        this.numDefStations = h.numDefStations;
        this.numPhases = h.numPhases;
        this.numDefPhases = h.numDefPhases;
        this.isFixed = h.isFixed;
        this.isPrime = h.isPrime;
        this.magMap = h.magMap;
        this.isSelected = h.isSelected;
        this.stime = h.stime;
        this.strike = h.strike;
        this.smajax = h.smajax;
        this.sminax = h.sminax;

    }

}
