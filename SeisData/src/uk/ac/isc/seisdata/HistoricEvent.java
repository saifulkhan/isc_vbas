package uk.ac.isc.seisdata;

/**
 * historic data
 *
 *  
 */
public class HistoricEvent extends AbstractSeisData {

    /**
     * record of evid, need redesign to make it String
     */
    private Integer evid;

    private Integer depth;
    private Double lat;
    private Double lon;

    /**
     * the event type
     */
    private String type;

    public HistoricEvent(int evid, int depth, double lat, double lon) {
        this.evid = evid;
        this.depth = depth;
        this.lat = lat;
        this.lon = lon;
    }

    public HistoricEvent(int evid, int depth, double lat, double lon, String type) {
        this.evid = evid;
        this.depth = depth;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
    }

    public int getEvid() {
        return this.evid;
    }

    public int getDepth() {
        return this.depth;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
