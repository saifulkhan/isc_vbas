package uk.ac.isc.hypomagnitudeview;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisEvent;

/**
 * Top component which displays the network magnitudes.
 */
@ConvertAsProperties(
        dtd = "-//uk.ac.isc.hypomagnitudeview//HypoMagnitudeView//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "HypoMagnitudeViewTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "magnitudeview", openAtStartup = true)
@ActionID(category = "Window", id = "uk.ac.isc.hypomagnitudeview.HypoMagnitudeViewTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_HypoMagnitudeViewAction",
        preferredID = "HypoMagnitudeViewTopComponent"
)
@Messages({
    "CTL_HypoMagnitudeViewAction=HypoMagnitudeView",
    "CTL_HypoMagnitudeViewTopComponent=HypoMagnitudeView Window",
    "HINT_HypoMagnitudeViewTopComponent=This is a HypoMagnitudeView window"
})
public final class HypoMagnitudeViewTopComponent extends TopComponent implements SeisDataChangeListener {

    private final JScrollPane scrollPane;

    // the panel to have the figure
    HypoMagnitudeViewPanel hmag = null;

    // Data
    private final HypocentresList hyposList = Global.getHypocentresList(); 
    private static final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();   
    
   
    public HypoMagnitudeViewTopComponent() {
        initComponents();
        setName(Bundle.CTL_HypoMagnitudeViewTopComponent());
        setToolTipText(Bundle.HINT_HypoMagnitudeViewTopComponent());
        
        selectedSeisEvent.addChangeListener(this);
        
        hmag = new HypoMagnitudeViewPanel(hyposList.getHypocentres());
        scrollPane = new JScrollPane(hmag);

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
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

    //repaint the view when data chages
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {

        hmag.UpdateData(hyposList.getHypocentres());

        hmag.repaint();
        scrollPane.setViewportView(hmag);
    }

}
