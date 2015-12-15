package uk.ac.isc.phaseview;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * keeping the travel time curve data
 *
 * @author hui
 */
public class TTDTriplet {

    private String phaseType;

    private Double delta;

    private Date arrivalTime;

    public TTDTriplet(String s) {
        //parse the string s
        String delims = "[,]+";
        String[] tokens = s.split(delims);

        this.phaseType = tokens[0];

        this.delta = Double.parseDouble(tokens[1]);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
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
        return "phaseType: " + getPhaseType() + " Delta: " + getDelta() + " ArrivalTime: " + getArrivalTime();
    }

}
