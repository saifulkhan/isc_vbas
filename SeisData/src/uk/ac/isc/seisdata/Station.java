package uk.ac.isc.seisdata;

import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 *  
 */
public class Station extends AbstractSeisData {

    //station code
    private String staCode;

    //station location longitude
    private double staLon;

    //station location latitude
    private double staLat;

    //agnecies who own the station to report phases
    private final ArrayList<String> reportAgency;

    //the azimuth of the station with hypo
    private double azimuth;

    //teh distance of station with hypo
    private double delta;

    //station mb
    private Double staMb;

    //station MS
    private Double staMs;

    private Double mbRes;

    private Double msRes;

    public Station() {
        reportAgency = new ArrayList<String>();
    }

    //this constructor for keeping station magnitude data
    public Station(String staCode, double lat, double lon) {
        this.staCode = staCode;

        reportAgency = new ArrayList<String>();
        this.staLat = lat;
        this.staLon = lon;
        reportAgency.add("ISC");
    }

    public Station(String staCode, double lat, double lon, String agency) {
        this.staCode = staCode;

        reportAgency = new ArrayList<String>();
        this.staLat = lat;
        this.staLon = lon;
        reportAgency.add(agency);
    }

    public Station(String staCode, double lat, double lon, String agency, double azi, double delta) {
        this.staCode = staCode;

        this.delta = delta;
        this.azimuth = azi;
        reportAgency = new ArrayList<String>();
        this.staLat = lat;
        this.staLon = lon;

        reportAgency.add(agency);
    }

    public void setStaCode(String staCode) {
        this.staCode = staCode;
        //fireSeisDataChanged();
    }

    public String getStaCode() {
        return this.staCode;
    }

    public void setLon(double lon) {
        this.staLon = lon;
    }

    public void setLat(double lat) {
        this.staLat = lat;
    }

    public double getLon() {
        return this.staLon;
    }

    public double getLat() {
        return this.staLat;
    }

    public void addReportAgency(String agency) {
        this.reportAgency.add(agency);
    }

    public String getReportAgency(int idx) {
        return this.reportAgency.get(idx);
    }

    public ArrayList<String> getReportAgencyList() {
        return this.reportAgency;
    }

    public void clearReportAgency() {
        this.reportAgency.clear();
    }

    public int getAgenciesNumber() {
        return reportAgency.size();
    }

    public void setAzimuth(double azi) {
        this.azimuth = azi;
        //fireSeisDataChanged();
    }

    public double getAzimuth() {
        return this.azimuth;
    }

    public void setDelta(double delta) {
        this.delta = delta;
        //fireSeisDataChanged();
    }

    public double getDelta() {
        return this.delta;
    }

    public void setAziDelta(double azi, double delta) {
        this.azimuth = azi;
        this.delta = delta;
        //fireSeisDataChanged();
    }

    public void setStaMb(Double staMb) {
        this.staMb = staMb;
        //fireSeisDataChanged();
    }

    public Double getStaMb() {
        return this.staMb;
    }

    public void setStaMs(Double staMs) {
        this.staMs = staMs;
        //fireSeisDataChanged();
    }

    public Double getStaMs() {
        return this.staMs;
    }

    public void setMbRes(Double mbRes) {
        this.mbRes = mbRes;
        //fireSeisDataChanged();
    }

    public Double getMbRes() {
        return this.mbRes;
    }

    public void setMsRes(Double msRes) {
        this.msRes = msRes;
        //fireSeisDataChanged();
    }

    public Double getMsRes() {
        return this.msRes;
    }

}
