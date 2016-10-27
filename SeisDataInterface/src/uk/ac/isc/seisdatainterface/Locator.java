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

    public static Boolean runLocator(Path dir,
            ArrayList<String> functionArray,
            String locatorArgStr,
            Boolean isAssess) {

        String iscLocOut = dir + File.separator + "iscloc.out";

        VBASLogger.logDebug("assessDir= " + dir);
        VBASLogger.logDebug("functionArray= " + functionArray.toString());
        VBASLogger.logDebug("locatorCommandStr= " + locatorArgStr);
        VBASLogger.logDebug("iscLocOut= " + iscLocOut);

        /* Run SQL functions */
        Boolean ret = SeisDataDAO.processAssessOrCommitData(
                Global.getSelectedSeisEvent().getEvid(),
                functionArray,
                isAssess);

        if (ret == false) {
            return false;
        }

        if (!new File(dir.toString()).exists()) {
            boolean success = (new File(dir.toString())).mkdirs();
            if (!success) {
                String message = "Error creating the directory: "
                        + dir
                        + "\nReport to the system admin.";
                VBASLogger.logSevere(message);
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        String runLocatorStr = null;
        if (isAssess) {
            runLocatorStr = "ssh beast "
                    + "export PGUSER=" + SeisDataDAO.getAssessUser() + "; "
                    + "export PGPASSWORD=" + SeisDataDAO.getAssessPassword() + "; "
                    + "echo " + "\""
                    + Global.getSelectedSeisEvent().getEvid() + " "
                    + locatorArgStr + "\"" + " | " + SeisDataDAO.getLocatorBin() + " - > "
                    + iscLocOut;
        } else {
            runLocatorStr = "ssh beast "
                    + "export PGUSER=" + SeisDataDAO.getPgUser() + "; "
                    + "export PGPASSWORD=" + SeisDataDAO.getPgPassword() + "; "
                    + "echo " + "\""
                    + Global.getSelectedSeisEvent().getEvid() + " "
                    + locatorArgStr + "\"" + " | " + SeisDataDAO.getLocatorBin() + " - > "
                    + iscLocOut;
        }

        VBASLogger.logDebug("Running locator: " + runLocatorStr);

        String output = null;
        try {
            Process p = Runtime.getRuntime().exec(runLocatorStr);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            VBASLogger.logDebug("The standard output of the locator command:\n");
            while ((output = stdInput.readLine()) != null) {
                VBASLogger.logDebug(output);
            }
            // TODO: find out if locator failed, James has to do it.
            // Notofy the user.

        } catch (IOException e2) {
            String message = "The standard error of the locator command:\n";
            e2.printStackTrace();
            VBASLogger.logSevere(message);
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}
