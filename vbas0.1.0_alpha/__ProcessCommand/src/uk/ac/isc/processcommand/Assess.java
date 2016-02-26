package uk.ac.isc.processcommand;

import com.orsoncharts.util.json.JSONArray;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import uk.ac.isc.hypodepthview.HypoDepthViewPanel;
import uk.ac.isc.hypooverview.HypoOverviewPanel2;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdata.SeisDataDAOAssess;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.textview.HypocentreTableModel;

public class Assess {

    private final JSONArray jFunctionArray;
    private final Path assessDir;
    private final File reportName;
    ;
    
    private static final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    // New (relocator generated) Hypocentre & Phase data for the selected SeisEvent.
    private final HypocentresList hypocentresList = new HypocentresList();
    private final PhasesList phasesList = new PhasesList();
    private final TreeMap<String, String> stations = new TreeMap<String, String>();

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

            // TODO: 
            /*Global.logDebug("The standard error of the locator command:\n");
             while ((output = stdError.readLine()) != null) {
             String message = "The standard error of the locator command: " + output;
             Global.logSevere(message);
             JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
             return false;
             }*/
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
        Global.logDebug("Writing the Hypocentre table.");
        loadSelectedSeisEventData();

        writeHypocentreTable();

        Global.logDebug("#Hypocentres=" + hypocentresList.getHypocentres().size());
        writeOverview(new HypoOverviewPanel2(hypocentresList), "HypocentreOverview");
        writeOverview(new HypoDepthViewPanel(hypocentresList.getHypocentres()), "HypocentreDepthview");

        return true;
    }

    private void writeOverview(final JPanel viewPanel, final String panelName) {

        Global.logDebug("Generating: " + panelName);

        int width = 0, height = 0;

        switch (panelName) {
            case "HypocentreOverview":
                HypoOverviewPanel2 hop = (HypoOverviewPanel2) viewPanel;
                width = hop.getMapWidth();
                height = hop.getMapHeight();
                break;

            case "HypocentreDepthview":
                HypoDepthViewPanel hdp = (HypoDepthViewPanel) viewPanel;
                width = hdp.getWidth();
                height = hdp.getHeight();
                break;
        }

        final JDialog f = new JDialog();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setModal(true);
        f.setLayout(new BorderLayout());
        JButton button_ok = new JButton("OK");

        f.setPreferredSize(new Dimension(width, height));
        f.add(viewPanel, BorderLayout.CENTER);

        f.add(button_ok, BorderLayout.PAGE_END);
        f.pack();

        button_ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                File outputFile = new File(assessDir + File.separator + panelName + ".png");
                BufferedImage bi = null;
                int width = 0, height = 0;

                switch (panelName) {
                    case "HypocentreOverview":
                        HypoOverviewPanel2 hop = (HypoOverviewPanel2) viewPanel;
                        bi = hop.getBaseMap();
                        width = hop.getMapWidth();
                        height = hop.getMapHeight();
                        break;

                    case "HypocentreDepthview":
                        HypoDepthViewPanel hdp = (HypoDepthViewPanel) viewPanel;
                        bi = hdp.getDepthHistImg();
                        width = hdp.getWidth();
                        height = hdp.getHeight();
                        break;
                }
                
                try {

                    ImageIO.write(bi, "png", outputFile);
                } catch (Exception e) {
                    Global.logSevere("Error creating a png file: " + outputFile.toString());
                    e.printStackTrace();
                }

                try {
                    FileWriter fileWritter = new FileWriter(reportName, true);
                    BufferedWriter bw = new BufferedWriter(fileWritter);

                    bw.write("<div>");
                    bw.write("<h2> " + panelName + " </h2>");
                    bw.write("<img src=\"" + panelName + ".png\" "
                            + "alt=\"" + panelName + "\" "
                            + "width= " + "\"" + width + "\""
                            + "height= " + "\"" + height + "\"");
                    bw.write("</div>");

                    bw.close();
                } catch (IOException e) {
                    Global.logSevere("Error writing to HTML file.");
                    e.printStackTrace();
                }
                f.dispose();
            }
        });

        f.setVisible(true);
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

        Global.logDebug("#Hypocentres=" + hypocentresList.getHypocentres().size());
        HypocentreTableModel model = new HypocentreTableModel(hypocentresList.getHypocentres());

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

            for (int r = 0; r < model.getRowCount(); ++r) {
                bw.write("<tr>");
                for (int c = 0; c < model.getColumnCount(); ++c) {
                    bw.write("<td>");
                    //Global.logDebug(model.getValueAt(r, c) == null ? "" : model.getValueAt(r, c).toString());
                    bw.write(model.getValueAt(r, c) == null ? "" : model.getValueAt(r, c).toString());
                    bw.write("</td>");
                }
            }

            bw.write("</table>");
            bw.write("</div>");

            bw.close();
        } catch (IOException e) {
            Global.logSevere("Error writing to file");
            // Or we could just do this:
            // ex.printStackTrace();
        }

    }

}
