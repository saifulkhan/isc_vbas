
package uk.ac.isc.databasetest;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hui
 */
public class Station extends AbstractSeisData {
    
    private String staCode;
    
    private double staLon;
    
    private double staLat;
    
    private final ArrayList<String> reportAgency;
    
    private double azimuth;
    
    private double delta;
    
    private double staMb;
    
    private double staMs;
    
    public Station()
    {
        reportAgency = new ArrayList<String>();
    }
    
    public Station(String staCode, double lat, double lon, String agency)
    {
        this.staCode = staCode;
        
        reportAgency = new ArrayList<String>();
        this.staLat = lat;
        this.staLon = lon;
        reportAgency.add(agency);
    }
    
    public Station(String staCode, double lat, double lon, String agency, double azi, double delta)
    {
        this.staCode = staCode;

        this.delta = delta;
        this.azimuth = azi;
        reportAgency = new ArrayList<String>();
        this.staLat = lat;
        this.staLon = lon;
        
        reportAgency.add(agency);
    }
    
    public void setStaCode(String staCode)
    {
        this.staCode = staCode;
        //fireSeisDataChanged();
    }
    
    public String getStaCode()
    {
        return this.staCode;
    }
    
    public void setLon(double lon)
    {
        this.staLon = lon;
    }
    
    public void setLat(double lat)
    {
        this.staLat = lat;
    }
    
    public double getLon()
    {
        return this.staLon;
    }
    
    public double getLat()
    {
        return this.staLat;
    }
    
    public void addReportAgency(String agency)
    {
        this.reportAgency.add(agency);
    }
    
    public String getReportAgency(int idx)
    {
        return this.reportAgency.get(idx);
    }
    
    public ArrayList<String> getReportAgencyList()
    {
        return this.reportAgency;
    }
    
    public void clearReportAgency()
    {
        this.reportAgency.clear();
    }
    
    public int getAgenciesNumber()
    {
        return reportAgency.size();
    }
    
    public void setAzimuth(double azi)
    {
        this.azimuth = azi;
        //fireSeisDataChanged();
    }
    
    public double getAzimuth()
    {
        return this.azimuth;
    }
    
    public void setDelta(double delta)
    {
        this.delta = delta;
        //fireSeisDataChanged();
    }
    
    public double getDelta()
    {
        return this.delta;
    }
    
    public void setAziDelta(double azi, double delta)
    {
        this.azimuth = azi;
        this.delta = delta;
        //fireSeisDataChanged();
    }
    public void setStaMb(double staMb)
    {
        this.staMb = staMb;
        //fireSeisDataChanged();
    }
    
    public double getStaMb()
    {
        return this.staMb;
    }
    
    public void setStaMs(double staMs)
    {
        this.staMs = staMs;
        //fireSeisDataChanged();
    }
    
    public double getStaMs()
    {
        return this.staMs;
    }  
   
}
