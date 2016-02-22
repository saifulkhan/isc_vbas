package uk.ac.isc.eventscontrolview;

import com.orsoncharts.util.json.JSONArray;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    // New (relocator generated) Hypocentre & Phase data for the selected SeisEvent.
    private final HypocentresList hypocentresList = new HypocentresList();
    private final PhasesList phasesList = new PhasesList();
    private final TreeMap<String, String> stations = new TreeMap<String, String>();

    private final JSONArray jFunctionArray;
    private final Path assessDir;
    private final File reportName;;
    
    Assess(JSONArray jFunctionArray) {
        this.jFunctionArray = jFunctionArray;
        
        int evid = Global.getSelectedSeisEvent().getEvid();
        assessDir = Paths.get(SeisDataDAOAssess.getAssessDir().toString() + File.separator + evid);
        reportName = new File(assessDir + File.separator + evid + ".html");
        Global.logDebug("assessDir=" + assessDir + ", reportName=" + reportName);
    }

    public File getReportName() {
        return reportName;
    }

    public Boolean runLocator() {

        String locatorCommandStr
                = SeisDataDAOAssess.processAssessData(Global.getSelectedSeisEvent().getEvid(), jFunctionArray);

        if (locatorCommandStr == null) {
            String message = "Incorrect locator command (locatorCommandStr= " + locatorCommandStr + ")";
            Global.logSevere(message);
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String iscLocOut = assessDir + File.separator
                + "iscloc." + Global.getSelectedSeisEvent().getEvid() + ".out";
        Global.logDebug("locatorCommandStr= " + locatorCommandStr + "\niscLocOut=" + iscLocOut);

        if (!new File(assessDir.toString()).exists()) {
            boolean success = (new File(assessDir.toString())).mkdirs();
            if (!success) {
                String message = "Error creating the directory " + assessDir;
                Global.logSevere(message);
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        String runLocatorStr = "ssh beast "
                + "export PGUSER=" + SeisDataDAOAssess.getAssessUser() + "; "
                + "export PGPASSWORD=" + SeisDataDAOAssess.getAssessPassword() + "; "
                + "echo " + "\"" + locatorCommandStr + "\"" + " | iscloc_parallel_db - > "
                + iscLocOut;
        Global.logDebug(runLocatorStr);

        String output = null;
        try {
            Process p = Runtime.getRuntime().exec(runLocatorStr);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            Global.logDebug("The standard output of the command:\n");
            while ((output = stdInput.readLine()) != null) {
                Global.logDebug(output);
            }

            Global.logDebug("The standard error of the locator command:\n");
            while ((output = stdError.readLine()) != null) {
                String message = "The standard error of the locator command: " + output;
                Global.logSevere(message);
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

        } catch (IOException e2) {
            String message = "The standard error of the locator command: ";
            e2.printStackTrace();
            Global.logSevere(message);
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;

    }

        
    Boolean generateHTMLReport() {
        loadSelectedSeisEventData();
        
        
        
        return true;
    }

        
    private void loadSelectedSeisEventData() {

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
    
    
    private void writeHypocentreTable() {
        
         try {
            // if the reportName file doesnt exists, then create it
            if (!reportName.exists()) {
                reportName.createNewFile();
            }

            FileWriter fileWritter = new FileWriter(reportName, true);
            BufferedWriter bw = new BufferedWriter(fileWritter);

            
            bw.write("<div>");
            bw.write("<h2>Hypocentre Table</h2>");
            bw.write("<table>");
            
            //System.out.println("Rows: " + model.getRowCount());
            for (int r = 0; r < model.getRowCount(); ++r) {
                bw.write("<tr>");
                for (int c = 0; c < model.getColumnCount(); ++c) {
                    bw.write("<td>");
                    bw.write(model.getValueAt(r, c).toString());
                    //System.out.println(model.getValueAt(r, c).toString());
                    bw.write("</td>");
                }
            }
            
            HypocntreTableModel = new HypocentreTableModel(hypocentresList.getHypocentres());
            
            for (int r = 0; r < model.getRowCount(); ++r) {
                bw.write("<tr>");
                for (int c = 0; c < model.getColumnCount(); ++c) {
                    bw.write("<td>");
                    bw.write(model.getValueAt(r, c).toString());
                    //System.out.println(model.getValueAt(r, c).toString());
                    bw.write("</td>");
                }
            }
            
            
            
            bw.write("</table>");
            bw.write("</div>");
            //bw.newLine();
            
            bw.write("<h2>Hypocentre Overview</h2>");
            bw.write("<img src=\"pic_mountain.jpg\" alt=\"Mountain View\" style=\"width:304px;height:228px;\">");

        
            bw.close();
        } catch (IOException e) {
             System.out.println("Error writing to file");
            // Or we could just do this:
            // ex.printStackTrace();
        } 
        
    }

}
