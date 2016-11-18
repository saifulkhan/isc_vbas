package uk.ac.isc.agencypiechartview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import uk.ac.isc.seisdata.Phase;

/**
 * Data structure to save the agency list for drawing the pie chart
 *
 *  
 */
public class PieChartData {

    //agency with its percetage of phases
    private TreeMap<String, Double> agencyMap;
    //get all the reported agency for each phase
    private ArrayList<String> agenciesList = new ArrayList<String>();

    public PieChartData(ArrayList<Phase> phases) {
        updateData(phases);
    }

    
    /**
     * Given new phases, and update the agency list and percentages
     */
    public void updateData(ArrayList<Phase> phases) {

        if (phases.isEmpty()) {
            return;
        }

        agenciesList.clear();
        
        for (Phase phase : phases) {
            agenciesList.add(phase.getReportAgency());
        }

        //a temporary hash set and a hash map 
        HashSet<String> nameSet = new HashSet<String>(agenciesList);
        HashMap<String, Double> tmpMap = new HashMap<String, Double>();

        for (String name : nameSet) {
            //get the frequecy of an agency and put it into the map
            double tmpFreq = (double) Collections.frequency(agenciesList, name) / agenciesList.size();
            tmpMap.put(name, tmpFreq);
        }

        //agencyMap = tmpMap;
        agencyMap = SortByValue(tmpMap);

    }

    public TreeMap<String, Double> getMap() {
        return agencyMap;
    }

    //for map sorting
    public static TreeMap<String, Double> SortByValue(HashMap<String, Double> map) {
        ValueComparator vc = new ValueComparator(map);
        TreeMap<String, Double> sortedMap = new TreeMap<String, Double>(vc);
        sortedMap.putAll(map);
        return sortedMap;
    }
}

//comparator class for the sorting of the map
class ValueComparator implements Comparator<String> {

    Map<String, Double> map;

    public ValueComparator(Map<String, Double> base) {
        this.map = base;
    }

    @Override
    public int compare(String a, String b) {
        if (map.get(a) >= map.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys 
    }
}
