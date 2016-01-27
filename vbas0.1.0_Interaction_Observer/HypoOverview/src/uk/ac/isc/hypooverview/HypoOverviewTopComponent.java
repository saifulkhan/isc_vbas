/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.isc.hypooverview;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import uk.ac.isc.eventscontrolview.EventsControlViewTopComponent;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisEvent;

/**
 * Top component which displays seismicity map and hypocentre position.
 */
@ConvertAsProperties(
        dtd = "-//uk.ac.isc.hypooverview//HypoOverview//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "HypoOverviewTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "mapview", openAtStartup = true)
@ActionID(category = "Window", id = "uk.ac.isc.hypooverview.HypoOverviewTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_HypoOverviewAction",
        preferredID = "HypoOverviewTopComponent"
)
@Messages({
    "CTL_HypoOverviewAction=HypoOverview",
    "CTL_HypoOverviewTopComponent=HypoOverview Window",
    "HINT_HypoOverviewTopComponent=This is a HypoOverview window"
})
public final class HypoOverviewTopComponent extends TopComponent implements SeisDataChangeListener {

    private final HypocentresList hypocentresList; 
    private static SeisEvent seisEvent = Global.getSelectedSeisEvent();  // to receive events
    
    private final JScrollPane scrollPane;
    private final HypoOverviewPanel2 hop;           // the main view
    private final OverviewControlPanel3 ocp;        // the control panel

    
    public HypoOverviewTopComponent() {
        initComponents();
        setName(Bundle.CTL_HypoOverviewTopComponent());
        setToolTipText(Bundle.HINT_HypoOverviewTopComponent());
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);

        //hypocentresList = ((EventsControlViewTopComponent) tc).getControlPanel().getHyposList();
        hypocentresList = Global.getHypocentresList();
        seisEvent.addChangeListener(this);
        
        
        //hop = new HypoOverviewPanel(hypocentresList);
        hop = new HypoOverviewPanel2(hypocentresList);

        scrollPane = new JScrollPane(hop);

        //ocp = new OverviewControlPanel(hop);       
        ocp = new OverviewControlPanel3(hop);

        this.setLayout(new BorderLayout());
        this.add(ocp, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
    }
    
    // repaint when the data changes
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {

        System.out.println(Global.debugAt() + " Event received from " + event.getData().getClass().getName());
        
        for (Hypocentre hypo : hypocentresList.getHypocentres()) {
            if (hypo.getIsPrime() == true) {
                hop.setCentLatLon(hypo.getLat(), hypo.getLon());
                hop.setCentDepth(hypo.getDepth());
                hop.loadSeisData(hypo.getLat(), hypo.getLon(), hop.getRangeDelta());
            }
        }

        //hop.setHypoVisOptions(2);
        //hop.setDepthBandOrder(4);
        ocp.resetToDefault();

        //hop.repaint();
        ocp.repaint();

        scrollPane.setViewportView(hop);
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
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
        // hypocentresList.removeChangeListener(this);
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
