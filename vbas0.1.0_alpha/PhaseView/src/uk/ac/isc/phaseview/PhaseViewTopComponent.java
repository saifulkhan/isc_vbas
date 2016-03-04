package uk.ac.isc.phaseview;

import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import org.jfree.data.time.Second;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisEvent;

/**
 * Top component which displays the phase view.
 */
@ConvertAsProperties(
        dtd = "-//uk.ac.isc.phaseview//PhaseView//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "PhaseViewTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "phasearrivalview", openAtStartup = true)
@ActionID(category = "Window", id = "uk.ac.isc.phaseview.PhaseViewTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_PhaseViewAction",
        preferredID = "PhaseViewTopComponent"
)
@Messages({
    "CTL_PhaseViewAction=PhaseView",
    "CTL_PhaseViewTopComponent=PhaseView Window",
    "HINT_PhaseViewTopComponent=This is a PhaseView window"
})
public final class PhaseViewTopComponent extends TopComponent implements SeisDataChangeListener {

    private JSplitPane pairViewsPane = null;
    private PhaseViewControlPanel phaseViewControlPanel = null;
    private JScrollPane leftPane = null;
    private JScrollPane rightPane = null;
    private PhaseTravelViewPanel phaseTVPanel = null;
    private PhaseDetailViewPanel phaseDVPanel = null;

    /*
     * This is for saving the theoretical travel time points
     */
    //private final DuplicateUnorderTimeSeriesCollection ttdData = new DuplicateUnorderTimeSeriesCollection();
    private boolean showTTDFlag = true;

    // Other
    private Hypocentre primeHypocentre;

    // Data
    private static final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final HypocentresList hypocentresList = Global.getHypocentresList();
    private static final PhasesList phasesList = Global.getPhasesList();

    // Pane and panels for views
    public PhaseViewTopComponent() {
        initComponents();
        setName(Bundle.CTL_PhaseViewTopComponent());
        setToolTipText(Bundle.HINT_PhaseViewTopComponent());
        Global.logDebug("Loaded...");

        selectedSeisEvent.addChangeListener(this);

        /* 
         * Create 2 phase views. 
         */
        phaseTVPanel = new PhaseTravelViewPanel(phasesList, hypocentresList);
        phaseDVPanel = new PhaseDetailViewPanel(phaseTVPanel);

        /*
         DuplicateUnorderTimeSeriesCollection ttdData = LoadTTDData.loadTTDData(selectedSeisEvent.getEvid(), perlScript.toString());

         for (int i = 0; i < hypocentresList.getHypocentres().size(); i++) {
         if (hypocentresList.getHypocentres().get(i).getIsPrime()) {
         primeHypocentre = hypocentresList.getHypocentres().get(i);
         }
         }

         phaseTVPanel = new PhaseTravelViewPanel(phasesList, primeHypocentre, ttdData);
         phaseDVPanel = new PhaseDetailViewPanel(phaseTVPanel, ttdData);
         phaseTVPanel.setPrime(primeHypocentre);
         */
        phaseViewControlPanel = new PhaseViewControlPanel(phaseTVPanel, phaseDVPanel);

        // add them together to the top component
        leftPane = new JScrollPane(phaseTVPanel);
        rightPane = new JScrollPane(phaseDVPanel);
        pairViewsPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
        pairViewsPane.setResizeWeight(0.5d);
        this.setLayout(new BorderLayout());
        this.add(phaseViewControlPanel, BorderLayout.NORTH);
        this.add(pairViewsPane, BorderLayout.CENTER);

    }

    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
        String eventName = event.getData().getClass().getName();
        Global.logDebug("Event received from: " + eventName);
        switch (eventName) {
            case "uk.ac.isc.seisdata.SeisEvent":
                //SeisEvent seisEvent = (SeisEvent) event.getData();
                Global.logDebug("SeisEvent= " + selectedSeisEvent.getEvid());

                phaseViewControlPanel.reset();

                /*
                 DuplicateUnorderTimeSeriesCollection ttdData = LoadTTDData.loadTTDData(selectedSeisEvent.getEvid(), perlScript.toString());
                 for (int i = 0; i < hypocentresList.getHypocentres().size(); i++) {
                 if (hypocentresList.getHypocentres().get(i).getIsPrime()) {
                 primeHypocentre = hypocentresList.getHypocentres().get(i);
                 }
                 }
                 phaseTVPanel.setPrime(primeHypocentre);
                 phaseTVPanel.setTTDData(ttdData);
                 */
                phaseTVPanel.updateData();
                phaseDVPanel.setRange(phaseTVPanel.getRange());
                phaseDVPanel.updateData();

                break;
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
        //phasesList.addChangeListener(this);
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
        //phasesList.removeChangeListener(this);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

}
