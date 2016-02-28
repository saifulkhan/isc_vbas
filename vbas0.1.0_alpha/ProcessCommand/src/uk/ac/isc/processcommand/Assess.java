package uk.ac.isc.processcommand;

import com.orsoncharts.util.json.JSONArray;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener; // Note: Required to avoid compilation error.
import uk.ac.isc.textview.HypocentreTableModel;
import uk.ac.isc.textview.PhaseTextViewTableModel;

public class Assess {

    private static final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    // New (relocator generated) Hypocentre & Phase data for the selected SeisEvent.
    private final HypocentresList hypocentresList = new HypocentresList();
    private final PhasesList phasesList = new PhasesList();
    private final TreeMap<String, String> stations = new TreeMap<String, String>();
    
    private Path assessDir;

    Assess() {

    }

    public Boolean runLocator(Path assessDir, ArrayList<String> functionArray, String locatorArgStr) {
        
        this.assessDir = assessDir;
        
        String iscLocOut = assessDir + File.separator
                + "iscloc." + Global.getSelectedSeisEvent().getEvid() + ".out";

        Global.logDebug("\nassessDir=" + assessDir
                + "\nfunctionArray=" + functionArray.toString()
                + "\nlocatorCommandStr=" + locatorArgStr
                + "\niscLocOut=" + iscLocOut);

        SeisDataDAOAssess.processAssessData(Global.getSelectedSeisEvent().getEvid(), functionArray);

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
                + "echo " + "\"" + Global.getSelectedSeisEvent().getEvid() + " " + locatorArgStr + "\"" + " | iscloc_parallel_db - > "
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

            // TODO: find out if locator failed, James has to do it.
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

    public void generateReport(File htmlReport) {
        Global.logDebug("htmlReport:" + htmlReport);

        /*
         * Load assessed data.
         */
        loadSelectedSeisEventData();

        Global.logDebug("Assessed Schema: #Hypocentres=" + hypocentresList.getHypocentres().size()
                + "#Phases=" + phasesList.getPhases().size());

        /*
         * Create the HTML schema.
         */
        HTMLSchema hTMLSchema = new HTMLSchema(htmlReport, new HypocentreTableModel(hypocentresList.getHypocentres()),
                new PhaseTextViewTableModel(phasesList.getPhases()));
        hTMLSchema.generateHTMLSchema();

        /*
         * Generate the PNG files
         */
        for (final String view : HTMLSchema.getViews()) {

            //generatePNG(, "HypocentreOverview");
            //generatePNG(), "HypocentreDepthview");
            Global.logDebug("Generating: " + view + ".png");

            JPanel panel = null;
            int width = 0, height = 0;

            switch (view) {
                case "Hypocentre Overview":
                    HypoOverviewPanel2 hop = new HypoOverviewPanel2(hypocentresList);
                    genetarePNG(view, hop, hop.getWidth(), hop.getHeight());
                    break;

                case "Hypocentre Depthview":
                    HypoDepthViewPanel hdv = new HypoDepthViewPanel(hypocentresList.getHypocentres());
                    genetarePNG(view, hdv, hdv.getWidth(), hdv.getHeight());
                    break;
            }

        }
    }

    private void genetarePNG(final String view, final JPanel panel, final int width, final int height) {

        final JDialog f = new JDialog();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setModal(true);
        f.setLayout(new BorderLayout());
        JButton button_ok = new JButton("OK");

        f.setPreferredSize(new Dimension(width, height));
        f.add(panel, BorderLayout.CENTER);

        f.add(button_ok, BorderLayout.PAGE_END);
        f.pack();

        /*
         * Pop-up the view.
         * Read the corresponding BufferedImage.
         */
        button_ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                File outputFile = new File(assessDir + File.separator + view + ".png");
                BufferedImage bi = null;

                switch (view) {
                    case "Hypocentre Overview":
                        HypoOverviewPanel2 hop = (HypoOverviewPanel2) panel;
                        bi = hop.getBaseMap();
                        break;

                    case "Hypocentre Depthview":
                        HypoDepthViewPanel hdp = (HypoDepthViewPanel) panel;
                        bi = hdp.getDepthHistImg();
                        break;
                }

                try {

                    ImageIO.write(bi, "png", outputFile);
                } catch (Exception e) {
                    Global.logSevere("Error creating a png file: " + outputFile.toString());
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

}
