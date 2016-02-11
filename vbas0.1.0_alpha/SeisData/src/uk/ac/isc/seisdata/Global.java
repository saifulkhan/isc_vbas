
package uk.ac.isc.seisdata;

import java.util.TreeMap;


/*
 * Used globally to register to change event and notify/fire changes.
 * Do not change the actual reference of these objects.
 */

public class Global {
    
    // Selected Seies Event
    private static SeisEvent selectedSeisEvent = new SeisEvent();
    // Selected Hypocentre
    private static Hypocentre selectedHypocentre = new Hypocentre();
    // Selected Phase
    private static Phase selectedPhase = new Phase();
    // Formulated command in: 1. Hypocentre table dialogs and popupmenus, 
    // 2. Comamnd table, 3. AssessedComamnd table
    // Not used: Set the comamnd as a java String.
    private static Command formulatedCommand = new Command();
    
    private static HypocentresList hypocentresList = new HypocentresList();
    private static PhasesList phasesList = new PhasesList(); 

    // TODO: Do I need these?
    private static CommandList commandList = new CommandList(); 
    private static AssessedCommandList assessedCommandList = new AssessedCommandList();

 
    public static SeisEvent getSelectedSeisEvent() {
       
        // TODO: chek dependency again
        // needed when the hypocentre & phase table loads for the first time.
        // if the SeiesEventTable module loads later
        /*
        if (selectedSeisEvent.getEvid() == 0) {
            SeisEventsList eventsList = new SeisEventsList();
            SeisDataDAO.retrieveAllEvents(eventsList.getEvents());
            selectedSeisEvent.setValues(eventsList.getEvents().get(0));
        } */
        return selectedSeisEvent;
    }

    public static Hypocentre getSelectedHypocentre() {
        return selectedHypocentre;
    }
  
    
    public static Phase getSelectedPhase() {
        return selectedPhase;
    }

    public static Command getFormulatedCommand() {
        return formulatedCommand;
    }
         
    
    public static PhasesList getPhasesList() {
        return phasesList;
    }
   
    public static HypocentresList getHypocentresList() {
        return hypocentresList;
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

  
    public static AssessedCommandList getAssessedCommandList() {
        return assessedCommandList;
    }
    
    public static CommandList getCommandList() {
        return commandList;
    }
   
}
