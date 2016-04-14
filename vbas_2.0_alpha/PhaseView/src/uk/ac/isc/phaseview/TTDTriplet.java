package uk.ac.isc.phaseview;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * keeping the travel time curve data
 *
 * @author hui
 */
public class TTDTriplet implements Comparable<TTDTriplet> {

    private String phaseType;
    private Double delta;
    private Date arrivalTime;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public TTDTriplet(String s) {
        //parse the string s
        String delims = "[,]+";
        String[] tokens = s.split(delims);

        this.phaseType = tokens[0];

        this.delta = Double.parseDouble(tokens[1]);

        try {
            arrivalTime = df.parse(tokens[2]);
        } catch (ParseException e) {
            arrivalTime = null;
        } catch (NullPointerException ne) {

        }
    }

    public String getPhaseType() {
        return this.phaseType;
    }

    public Double getDelta() {
        return this.delta;
    }

    public Date getArrivalTime() {
        return this.arrivalTime;
    }

    public void setPhaseType(String pt) {
        this.phaseType = pt;
    }

    public void setDelta(Double delta) {
        this.delta = delta;
    }

    public void setArrivalTime(Date d) {
        this.arrivalTime = d;
    }

    @Override
    public String toString() {
        return "Delta: " + getDelta()
                + " ArrivalTime: " + df.format(getArrivalTime())
                + " phaseType: " + getPhaseType();
    }

    @Override
    public int compareTo(TTDTriplet obj) {
        return getArrivalTime().compareTo(obj.getArrivalTime());
    }

}
