
package uk.ac.isc.seisdata;

import java.util.Date;
import java.util.Objects;

/**
 * Data model to keep the phases, we leave the clone to later stage as we don't need it now
 * @author hui
 */
public class Phase extends AbstractSeisData {
    
    //phase id
    private Integer phid;
    
    //reading id
    private Integer rdid;
    
    //which agency reports it
    private String reportAgency;
    
    //which station reports it
    private String reportStation;
    
    //the full name of the station
    private String stationFullName;
    
    //the region name
    private String regionName;
       
    //the reported phase type
    private String origPhaseType;
    
    //isc labelled phase type
    private String iscPhaseType;
    
    //distance with the epicentre
    private Double distance;
    
    //angel to the epicentre
    private Double azimuth;
    
    //station event angel
    private Double seAzimuth;
    
    private Double slowness;

    //signal noise ratio
    private Double snrRate;
    
    //phase arrival time
    private Date arrivalTime;
    
    //millisecond of arrival time
    private Integer msec;
    
    //time residual caculated based on ttd
    private Double timeResidual;
    
    //amplitude of the phase
    private Double amplitude;
       
    //period of the phase
    private Double period;
    
    //amp mag of the phase    
    private Double ampmag;
    
    //defining ampmag or not
    private Boolean ampmagDefining;
    
    //defining phase or not
    private boolean defining;
    
    private Boolean fixing;
    
    //if this is a duplicated phase
    private boolean duplicated;
    
    //if the phase is deprectaed
    private boolean deprecated;
    
    public Phase()
    {
        
    }
    
    public Phase(String agcy, String sta, String iscptype)
    {
        reportAgency = agcy;
        reportStation = sta;     
        iscPhaseType = iscptype;
        timeResidual = null;
    }
       
    public Phase(String agcy, String sta, String iscptype, Double res)
    {
        reportAgency = agcy;
        reportStation = sta;     
        iscPhaseType = iscptype;
        timeResidual = res;
    }
    
    //all the setters and getters
    public void setPhid(Integer phid)
    {
        this.phid = phid;
        //fireSeisDataChanged();
    }
    
    public Integer getPhid()
    {
        return this.phid;
    }
    
    public void setRdid(Integer rdid)
    {
        this.rdid = rdid;
        //fireSeisDataChanged();
    }
    
    public Integer getRdid()
    {
        return this.rdid;
    }
    
    /* seting and getting attributes */
    public void setReportAgency(String agency)
    {
        this.reportAgency = agency;
        //fireSeisDataChanged();
    }
    
    public String getReportAgency()
    {
        return this.reportAgency;
    }
    
    public void setReportStation(String station)
    {
        this.reportStation = station;
        //fireSeisDataChanged();
    }
    
    public String getReportStation()
    {
        return this.reportStation;
    }
    
    public void setStationFullName(String station)
    {
        this.stationFullName = station;
    }
    
    public String getStationFullName()
    {
        return this.stationFullName;
    }
    
    public void setRegionName(String regionName)
    {
        this.regionName = regionName;
    }
    
    public String getRegionName()
    {
        return this.regionName;
    }
    
    public void setOrigPhaseType(String type)
    {
        this.origPhaseType = type;
    }
    
    public String getOrigPhaseType()
    {
        return this.origPhaseType;
        
    }
    
    public void setIscPhaseType(String type)
    {
        this.iscPhaseType = type;
    }
    
    public String getIscPhaseType()
    {
        return this.iscPhaseType;
    }
    
    public void setDistance(Double distance)
    {
        this.distance = distance;
    }
    
    public Double getDistance()
    {
        return this.distance;
    }
 
    public void setAzimuth(Double azi)
    {
        this.azimuth = azi;
    }
    
    public Double getAzimuth()
    {
        return azimuth;
    }
       
    public void setSeAzimuth(Double seazi)
    {
        this.seAzimuth = seazi;
    }
    
    public Double getSeAzimuth()
    {
        return this.seAzimuth;
    }
      
    public void setSlowness(Double slowness)
    {
        this.slowness = slowness;
    }
    
    public Double getSlowness()
    {
        return this.slowness;
    }
       
    public void setSNRRate(Double snr)
    {
        this.snrRate = snr;
    }
    
    public Double getSNRRate()
    {
        return this.snrRate;
    }
    
    public void setArrivalTime(Date time)
    {
        this.arrivalTime = time;
        //fireSeisDataChanged();
    }
    
    public Date getArrivalTime()
    {
        return this.arrivalTime;
    }
    
    public Integer getMsec()
    {
        return this.msec;
    }
    
    public void setMsec(Integer msec)
    {
        this.msec = msec;
    }
    
    public void setTimeResidual(Double residual)
    {
        this.timeResidual = residual;
    }
    
    public Double getTimeResidual()
    {
         return this.timeResidual; 
    }
    
    public void setAmplitude(Double amp)
    {
        this.amplitude = amp;
    }
    
    public Double getAmplitude()
    {
        return this.amplitude;
    }
    
    public void setPeriod(Double period)
    {
        this.period = period;
    }
    
    public Double getPeriod()
    {
        return this.period;
    }
    
    public void setAmpMag(Double ampmag)
    {
        this.ampmag = ampmag;
    }
    
    public Double getAmpMag()
    {
        return this.ampmag;
    }
        
    public void setAmpmagDefining(Boolean def)
    {
        this.ampmagDefining = def;
    }
    
    public Boolean getAmpmagDefining()
    {
        return this.ampmagDefining;
    }
    
    public void setDefining(boolean def)
    {
        this.defining = def;
        //fireSeisDataChanged();
    }
    
    public boolean getDefining()
    {
        return this.defining;
    }
    
    public void setFixing(Boolean fix)
    {
        this.fixing = fix;
        //fireSeisDataChanged();
    }
    
    public Boolean getfixing()
    {
        return this.fixing;
    }
    
    public void setDuplicated(boolean dup)
    {
        this.duplicated = dup;
        //fireSeisDataChanged();
    }

    public boolean getDuplicated()
    {
        return this.duplicated;
    }
    
    public void setDeprecated(boolean dep)
    {
        this.deprecated = dep;
        //fireSeisDataChanged();
    }
    
    public boolean getDeprecated()
    {
        return this.deprecated;
    }
    
    /* there would be a method to set all the data from database and call fireseisdatachanged() once */
    //public void setFromJDBC()
   
    @Override
    public boolean equals(Object obj)
    {
        if(obj==this)
        {
            return true;
        }
        
        if(!(obj instanceof Phase))
        {
            return false;
        }
        Phase that = (Phase) obj;
        
        if (!this.getReportAgency().equals(that.getReportAgency()) || this.getArrivalTime() != that.getArrivalTime()
                || (this.getReportStation() == null ? that.getReportStation() != null : !this.getReportStation().equals(that.getReportStation())) )
        {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.reportAgency);
        hash = 41 * hash + Objects.hashCode(this.reportStation);
        hash = 41 * hash + Objects.hashCode(this.arrivalTime);
        return hash;
    }

    @Override
    public String toString()
    {
       String ret = "Type: " + this.iscPhaseType + " Agency: "+ this.reportAgency + " Station: "
               + this.reportStation + " Dist: " + this.distance;
       return ret;
    }
    
}
