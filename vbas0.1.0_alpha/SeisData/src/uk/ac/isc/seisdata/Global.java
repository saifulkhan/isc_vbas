package uk.ac.isc.seisdata;

import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * Used globally to register to change event and notify/fire changes.
 * Do not change the actual reference of these objects.
 */
public class Global {

    // Selected SeiesEvent and it's 
    // Hypocentre(s), Phase(s), Command(s), and  AssessedCommand(s)
    // They are fetched at the beginning when a SeisEvent is selected.
    private static SeisEvent selectedSeisEvent = new SeisEvent();
    private static HypocentresList hypocentresList = new HypocentresList();
    private static PhasesList phasesList = new PhasesList();
    private static CommandList commandList = new CommandList();
    private static AssessedCommandList assessedCommandList = new AssessedCommandList();

    // To notify an event: a Hypocentre or a Phase is selected.
    private static Hypocentre selectedHypocentre = new Hypocentre();
    private static Phase selectedPhase = new Phase();

    // To notify an event:  a new Command is generated
    private static Command commandEvent = new Command();
    private static AssessedCommand assessedComamndEvent = new AssessedCommand();

    public static SeisEvent getSelectedSeisEvent() {
        /*
        // needed when the hypocentre & phase table loads for the first time.
        // if the SeiesEventTable module loads later
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

    public static PhasesList getPhasesList() {
        return phasesList;
    }

    public static HypocentresList getHypocentresList() {
        return hypocentresList;
    }

    public static AssessedCommandList getAssessedCommandList() {
        return assessedCommandList;
    }

    public static CommandList getCommandList() {
        return commandList;
    }

    public static Command getCommandEvent() {
        return commandEvent;
    }

    public static AssessedCommand getAssessedComamndEvent() {
        return assessedComamndEvent;
    }

    /*
     *****************************************************************************************
     * TODO: in a separate class. 
     *****************************************************************************************
     */
    public static String debugAt() {
        String at = Thread.currentThread().getStackTrace()[2].getLineNumber() + ":"
                + Thread.currentThread().getStackTrace()[2].getClassName().
                substring(Thread.currentThread().getStackTrace()[2].getClassName().
                        lastIndexOf(".") + 1) + ":"
                + Thread.currentThread().getStackTrace()[2].getMethodName();

        return at;
    }

    public static void logSevere(String debugString) {
        String at = Thread.currentThread().getStackTrace()[2].getLineNumber() + ":"
                + Thread.currentThread().getStackTrace()[2].getClassName().
                substring(Thread.currentThread().getStackTrace()[2].getClassName().
                        lastIndexOf(".") + 1) + ":"
                + Thread.currentThread().getStackTrace()[2].getMethodName();

        Logger.getLogger(at).log(Level.SEVERE, debugString);
    }

    public static void logDebug(String debugString) {
        String at = Thread.currentThread().getStackTrace()[2].getLineNumber() + ":"
                + Thread.currentThread().getStackTrace()[2].getClassName().
                substring(Thread.currentThread().getStackTrace()[2].getClassName().
                        lastIndexOf(".") + 1) + ":"
                + Thread.currentThread().getStackTrace()[2].getMethodName();
        Logger.getLogger(at).log(Level.INFO, debugString);
    }

}
