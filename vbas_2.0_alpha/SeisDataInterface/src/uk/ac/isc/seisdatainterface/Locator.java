package uk.ac.isc.seisdatainterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import uk.ac.isc.seisdata.VBASLogger;
 
 
public class Locator {
 
    public static Boolean runLocator(Path assessDir, ArrayList<String> functionArray, String locatorArgStr) {

        String iscLocOut = assessDir + File.separator + "iscloc.out";

        VBASLogger.logDebug("assessDir= " + assessDir);
        VBASLogger.logDebug("functionArray= " + functionArray.toString());
        VBASLogger.logDebug("locatorCommandStr= " + locatorArgStr);
        VBASLogger.logDebug("iscLocOut= " + iscLocOut);

        Boolean ret = SeisDataDAOAssess.processAssessData(Global.getSelectedSeisEvent().getEvid(), functionArray);
        if (ret == false) {
            return false;
        }

        if (!new File(assessDir.toString()).exists()) {
            boolean success = (new File(assessDir.toString())).mkdirs();
            if (!success) {
                String message = "Error creating the directory " + assessDir;
                VBASLogger.logSevere(message);
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        String runLocatorStr = "ssh beast "
                + "export PGUSER=" + SeisDataDAOAssess.getAssessUser() + "; "
                + "export PGPASSWORD=" + SeisDataDAOAssess.getAssessPassword() + "; "
                + "echo " + "\"" + Global.getSelectedSeisEvent().getEvid() + " " + locatorArgStr + "\"" + " | iscloc_parallel_db - > "
                + iscLocOut;
        VBASLogger.logDebug(runLocatorStr);

        String output = null;
        try {
            Process p = Runtime.getRuntime().exec(runLocatorStr);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            VBASLogger.logDebug("The standard output of the command:\n");
            while ((output = stdInput.readLine()) != null) {
                VBASLogger.logDebug(output);
            }

            // TODO: find out if locator failed, James has to do it.
            /*VBASLogger.logDebug("The standard error of the locator command:\n");
             while ((output = stdError.readLine()) != null) {
             String message = "The standard error of the locator command: " + output;
             VBASLogger.logSevere(message);
             JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
             return false;
             }*/
        } catch (IOException e2) {
            String message = "The standard error of the locator command: ";
            e2.printStackTrace();
            VBASLogger.logSevere(message);
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;

    }
}
