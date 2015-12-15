package uk.ac.isc.seisdata;

import java.util.ArrayList;

/**
 * A wrapper to keep the list of Hypocentres
 *
 * @author hui
 */
public class HypocentresList extends AbstractSeisData {

    private final ArrayList<Hypocentre> hypos;

    public HypocentresList() {
        hypos = new ArrayList<Hypocentre>();
    }

    public ArrayList<Hypocentre> getHypocentres() {
        return this.hypos;
    }

}
