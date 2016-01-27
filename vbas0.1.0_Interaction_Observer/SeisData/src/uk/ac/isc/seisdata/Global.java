
package uk.ac.isc.seisdata;

import java.util.TreeMap;


/*
 * Used globally to register to change event and notify/fire changes.
 * Do not change the actual reference of these objects.
 */

public class Global {
    
    // Selected Seies Event
    private static SeisEvent seisEvent = new SeisEvent();
    // Selected Hypocentre
    
    // Selected Phase
    
    private static HypocentresList hypocentresList = new HypocentresList();
    private static PhasesList phasesList = new PhasesList(); 
    private static CommandList commandList = new CommandList(); 
    private static AssessedCommandList assessedCommandList = new AssessedCommandList();

 
    public static SeisEvent getSelectedSeisEvent() {
       
        // needed when the hypocentre & phase table loads for the first time.
        if (seisEvent.getEvid() == 0) {
            SeisEventsList eventsList = new SeisEventsList();
            SeisDataDAO.retrieveAllEvents(eventsList.getEvents());
            seisEvent.setValues(eventsList.getEvents().get(0));
        }
        return seisEvent;
    }
                
    public static CommandList getCommandList() {
        return commandList;
    }

    public static PhasesList getPhasesList() {
        return phasesList;
    }
   
    public static HypocentresList getHypocentresList() {
        return hypocentresList;
    }
   
    public static AssessedCommandList getAssessedCommandList() {
        return assessedCommandList;
    }
    
    public static String debugAt() {
        // Debug
        String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        return "Debug At-->> " + lineNumber + ":" + className + "." + methodName + "()-->> ";
        // Debug
    }
       
}
