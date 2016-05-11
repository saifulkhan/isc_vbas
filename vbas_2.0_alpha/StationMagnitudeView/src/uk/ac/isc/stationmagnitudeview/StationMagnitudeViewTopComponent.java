package uk.ac.isc.stationmagnitudeview;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import uk.ac.isc.seisdatainterface.Global;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.VBASLogger;

/**
 * Top component which displays the station magnitude view.
 */
@ConvertAsProperties(
        dtd = "-//uk.ac.isc.stationmagnitudeview//StationMagnitudeView//EN",
        autostore = false
)


@TopComponent.Description(
        preferredID = "StationMagnitudeViewTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "uk.ac.isc.stationmagnitudeview.StationMagnitudeViewTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_StationMagnitudeViewAction",
        preferredID = "StationMagnitudeViewTopComponent"
)
@Messages({
    "CTL_StationMagnitudeViewAction=Station Magnitudes",
    "CTL_StationMagnitudeViewTopComponent=Station Magnitudes",
    "HINT_StationMagnitudeViewTopComponent=Station Magnitudes"
})
public final class StationMagnitudeViewTopComponent extends TopComponent implements SeisDataChangeListener {

    private JScrollPane scrollPane = null;

    private final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final HypocentresList hyposList = Global.getHypocentresList();
    //prime hypo
    private Hypocentre primeHypocentre;
    //the key object of the view    
    private StationMagnitudeView smView;

    public StationMagnitudeViewTopComponent() {
        initComponents();
        setName(Bundle.CTL_StationMagnitudeViewTopComponent());
        setToolTipText(Bundle.HINT_StationMagnitudeViewTopComponent());
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_SLIDING_DISABLED, Boolean.FALSE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.FALSE);
        //setName("Station Magnitudes");

        VBASLogger.logDebug("Loaded..."
                + ", #SiesEvent=" + Global.getSelectedSeisEvent().getEvid()
                + ", #Hypocentre=" + hyposList.getHypocentres().size());

        selectedSeisEvent.addChangeListener(this);

        /*for (int i = 0; i < hyposList.getHypocentres().size(); i++) {
         if (hyposList.getHypocentres().get(i).getIsPrime()) {
         primeHypocentre = hyposList.getHypocentres().get(i);
         }
         }*/
        smView = new StationMagnitudeView(hyposList, false);

        scrollPane = new JScrollPane(smView);
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {

        String eventName = event.getData().getClass().getName();
        VBASLogger.logDebug(" Event received from " + eventName
                + ", SiesEvent=" + Global.getSelectedSeisEvent().getEvid()
                + ", #Hypocentres=" + hyposList.getHypocentres().size());

        // It only received SeiesEvent selected/changed now
        switch (eventName) {
            case ("uk.ac.isc.seisdata.SeisEvent"):
                //SeisEvent seisEvent = (SeisEvent) event.getData();
                /*for (int i = 0; i < hyposList.getHypocentres().size(); i++) {
                 if (hyposList.getHypocentres().get(i).getIsPrime()) {
                 primeHypocentre = hyposList.getHypocentres().get(i);
                 }
                 }*/
                smView.reset(hyposList);
                scrollPane.setViewportView(smView);
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
        //hyposList.addChangeListener(this);
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
        //hyposList.removeChangeListener(this);
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
