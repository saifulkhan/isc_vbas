
package uk.ac.isc.seisdata;

import java.util.TreeMap;

 
public class Global {
    
    private static SeisEvent selectedSeisEvent;
    private static HypocentresList hypocentresList;
    private static PhasesList phasesList; 
    private static CommandList actionHistoryList; 
    private static Command command; 

    public static void setCommand(Command command) {
        Global.command = command;
    }

    public static Command getCommand() {
        return command;
    }

    public static CommandList getActionHistoryList() {
        return actionHistoryList;
    }

    public static void setActionHistoryList(CommandList actionHistoryList) {
        Global.actionHistoryList = actionHistoryList;
    }

    public static void setPhasesList(PhasesList phasesList) {
        Global.phasesList = phasesList;
    }

    public static void setStations(TreeMap<String, String> stations) {
        Global.stations = stations;
    }

    public static PhasesList getPhasesList() {
        return phasesList;
    }

    public static TreeMap<String, String> getStations() {
        return stations;
    }
    private static  TreeMap<String, String> stations; 
    
    public static HypocentresList getHypocentresList() {
        return hypocentresList;
    }

    public static void setHypocentresList(HypocentresList hypocentresList) {
        Global.hypocentresList = hypocentresList;
    }
    
    public static SeisEvent getSelectedSeisEvent() {
        return selectedSeisEvent;
    }

    public static void setSelectedSeisEvent(SeisEvent selectedSeisEvent) {
        Global.selectedSeisEvent = selectedSeisEvent;
    }
}
