package uk.ac.isc.eventscontrolview;

import java.util.ArrayList;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdata.SeisDataDAOAssess;
import uk.ac.isc.seisdata.SeisEvent;

public class Assess {

    private static final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final HypocentresList hypocentresList = new HypocentresList();
    private final PhasesList phasesList = new PhasesList();
    private final TreeMap<String, String> stations = new TreeMap<String, String>();

    
    /*
    public Assess(ArrayList<Integer> commandIds) {
        
             for (int row : selectedRows) {
                    
            }

            boolean ret = SeisDataDAOAssess.processAssessData(selectedSeisEvent.getEvid(), commandIds);
            if (ret == false) {
                JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
            }
    
    }*/

    Assess() {
        Global.logDebug(SeisDataDAOAssess.getAssessDir().toString());
    }
    

    public void loadSelectedSeisEventData() {

        System.out.println(Global.debugAt() + "Load list of Hypocentre and Phase for SeisEvent: "
                + selectedSeisEvent.getEvid());

        /*
         * Hypocentre
         */
        SeisDataDAOAssess.retrieveHypos(selectedSeisEvent.getEvid(),
                hypocentresList.getHypocentres());
        SeisDataDAOAssess.retrieveHyposMagnitude(hypocentresList.getHypocentres());
        // as I remove all the hypos when clicking an event to retrieve the hypos, 
        // so need reset prime hypo every time
        // TODO: Saiful, What is this?
        for (Hypocentre hypo : hypocentresList.getHypocentres()) {
            if (hypo.getIsPrime() == true) {
                selectedSeisEvent.setPrimeHypo(hypo);
            }
        }

        /*
         * Phase
         */
        SeisDataDAOAssess.retrieveAllPhases(selectedSeisEvent.getEvid(), phasesList.getPhases());
        SeisDataDAOAssess.retrieveAllPhasesAmpMag(selectedSeisEvent.getEvid(),
                phasesList.getPhases());
        SeisDataDAOAssess.retrieveAllStationsWithRegions(stations);
        // load the correspondent map into the stataions
        // put the region name into the pahseList
        for (int i = 0; i < phasesList.getPhases().size(); i++) {
            phasesList.getPhases()
                    .get(i)
                    .setRegionName(stations
                            .get(phasesList
                                    .getPhases()
                                    .get(i)
                                    .getReportStation()));
        }

        Global.logDebug("#Hypocentres:" + hypocentresList.getHypocentres().size()
                + " #Phases:" + phasesList.getPhases().size());

    }

    
    
    
}
