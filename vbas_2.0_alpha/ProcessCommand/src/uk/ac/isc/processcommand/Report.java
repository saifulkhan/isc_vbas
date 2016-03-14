package uk.ac.isc.processcommand;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
 import java.io.File;
 import java.nio.file.Path;
 import java.util.TreeMap;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
 import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import uk.ac.isc.hypodepthview.HypoDepthViewPanel;
import uk.ac.isc.hypooverview.HypoOverviewPanel2;
import uk.ac.isc.seisdata.VBASLogger;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.PhasesList;
 import uk.ac.isc.seisdata.SeisEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener; // Note: Required to avoid compilation error.
import uk.ac.isc.agencypiechartview.AgencyPieChartView;
import uk.ac.isc.agencypiechartview.PieChartData;
import uk.ac.isc.hypomagnitudeview.HypoMagnitudeViewPanel;
import uk.ac.isc.phaseview.PhaseDetailViewPanel;
import uk.ac.isc.phaseview.PhaseTravelViewPanel;
import uk.ac.isc.seisdatainterface.Global;
import uk.ac.isc.seisdatainterface.SeisDataDAOAssess;
import uk.ac.isc.stationazimuthview.StationAzimuthView;
import uk.ac.isc.stationmagnitudeview.StationMagnitudeView;
import uk.ac.isc.textview.HypocentreTableModel;
import uk.ac.isc.textview.PhaseTextViewTableModel;

public class Report {

    private static final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    // New (relocator generated) Hypocentre & Phase data for the selected SeisEvent.
    private final HypocentresList hypocentresList = new HypocentresList();
    private final PhasesList phasesList = new PhasesList();
    private final TreeMap<String, String> stations = new TreeMap<String, String>();

    private Path assessDir;
    private final JDialog f;
    JProgressBar pbar;
        
    public Report() {
        f = new JDialog();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setModal(true);
        f.setLayout(new BorderLayout());

        pbar = new JProgressBar();
        pbar.setMinimum(0);
        pbar.setMaximum(100);
    }

    public void generateReport(Path assessDir, File htmlReport) {
        this.assessDir = assessDir;
        VBASLogger.logDebug("assessDir: " + assessDir + ", htmlReport:" + htmlReport);
        
        /*
         * Load assessed data.
         */
        loadAssessedData();

        VBASLogger.logDebug("Assessed Data: " 
                + "#Hypocentres=" + hypocentresList.getHypocentres().size()
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
            VBASLogger.logDebug("Generating: " + view + ".png");

            switch (view) {
                case "HypocentreOverview":
                    HypoOverviewPanel2 hop = new HypoOverviewPanel2(hypocentresList);
                    genetarePNG(view, hop, hop.getWidth(), hop.getHeight());
                    break;

                case "PhaseDetailView":

                    PhaseTravelViewPanel phaseTVPanel = new PhaseTravelViewPanel(phasesList, hypocentresList);
                    PhaseDetailViewPanel phaseDVPanel = new PhaseDetailViewPanel(phaseTVPanel);

                    genetarePNG("PhaseDetailView", phaseDVPanel, phaseDVPanel.getWidth(), phaseDVPanel.getHeight());
                    genetarePNG("PhaseTravelView", phaseTVPanel, phaseTVPanel.getWidth(), phaseTVPanel.getHeight());

                    break;

                case "PhaseTravelView":
                    // drawn along with detail view
                    break;

                case "HypocentreDepthView":
                    HypoDepthViewPanel hdv = new HypoDepthViewPanel(hypocentresList.getHypocentres());
                    genetarePNG(view, hdv, hdv.getWidth(), hdv.getHeight());
                    break;

                case "HypocentreMagnitudeView":
                    HypoMagnitudeViewPanel hmag = new HypoMagnitudeViewPanel(hypocentresList.getHypocentres());
                    genetarePNG(view, hmag, hmag.getHypocentreMagnitudeViewWidth(), hmag.getHypocentreMagnitudeViewHeight());
                    break;

                case "StationAzimuthView":
                    StationAzimuthView saView = new StationAzimuthView(hypocentresList, phasesList);
                    genetarePNG(view, saView, saView.getStationAzimuthViewWidth(), saView.getStationAzimuthViewheight());
                    break;

                case "StationMagnitudeView":
                    StationMagnitudeView smView = new StationMagnitudeView(hypocentresList);
                    genetarePNG(view, smView, smView.getStationMagnitudeViewWidth(), smView.getStationMagnitudeViewHeight());
                    break;

                case "AgencyPieChartView":
                    AgencyPieChartView apcView = new AgencyPieChartView();
                    apcView.setData(new PieChartData(phasesList.getPhases()));
                    genetarePNG(view, apcView, apcView.getAgencyPieChartViewWidth(), apcView.getAgencyPieChartViewHeight());
                    break;
            }

        }

        f.dispose();
    }

    private void genetarePNG(final String view, final JPanel panel, final int width, final int height) {
        f.setPreferredSize(new Dimension(width + 40, height + 40));
        f.add(panel, BorderLayout.CENTER);
        f.pack();

        // Note: see tutorial
        // http://stackoverflow.com/questions/1306868/can-i-set-a-timer-on-a-java-swing-jdialog-box-to-close-after-a-number-of-millise
        Timer timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                f.remove(panel);
                f.setVisible(false);
            }
        });
        timer.setRepeats(false);
        timer.start();
        f.setVisible(true);

        File outputFile = new File(assessDir + File.separator + view + ".png");
        BufferedImage bi = null;

        switch (view) {
            case "HypocentreOverview":
                HypoOverviewPanel2 hop = (HypoOverviewPanel2) panel;
                bi = hop.getBaseMap();
                break;

            case "PhaseDetailView":
                PhaseDetailViewPanel phaseDVPanel = (PhaseDetailViewPanel) panel;
                bi = phaseDVPanel.getBufferedImage();
                break;

            case "PhaseTravelView":
                PhaseTravelViewPanel phaseTVPanel = (PhaseTravelViewPanel) panel;
                bi = phaseTVPanel.getBufferedImage();
                break;

            case "HypocentreDepthView":
                HypoDepthViewPanel hdp = (HypoDepthViewPanel) panel;
                bi = hdp.getDepthHistImg();
                break;

            case "HypocentreMagnitudeView":
                HypoMagnitudeViewPanel hmag = (HypoMagnitudeViewPanel) panel;
                bi = hmag.getBufferedImage();
                break;

            case "StationAzimuthView":
                StationAzimuthView saView = (StationAzimuthView) panel;
                bi = saView.getBufferedImage();
                break;

            case "StationMagnitudeView":
                StationMagnitudeView smView = (StationMagnitudeView) panel;
                bi = smView.getBufferedImage();
                break;

            case "AgencyPieChartView":
                AgencyPieChartView apcView = (AgencyPieChartView) panel;
                bi = apcView.getBufferedImage();
                break;
        }

        try {

            ImageIO.write(bi, "png", outputFile);
        } catch (Exception e) {
            VBASLogger.logSevere("Error creating a png file: " + outputFile.toString());
            e.printStackTrace();
        }

    }

    private void loadAssessedData() {

        System.out.println(VBASLogger.debugAt() + "Load list of Hypocentre and Phase for SeisEvent: "
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

        VBASLogger.logDebug("SeisEvent=" + selectedSeisEvent.getEvid()
                + ", #Hypocentres:" + hypocentresList.getHypocentres().size()
                + ", #Phases:" + phasesList.getPhases().size());

    }

    /*
     * ***************************************************************************
     * Add a ProgressBar in future
     * ****************************************************************************
     */
    public class Progress {

        Timer t;
        JProgressBar prg;

        public Progress() {
            JFrame frame = new JFrame();
            frame.setLayout(new FlowLayout());
            prg = new JProgressBar(0, 100);
            prg.setValue(0);
            prg.setStringPainted(true);
        }

    }

}
