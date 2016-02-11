package uk.ac.isc.agencypiechartview;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisEvent;

/**
 * Top component which displays pie chart view.
 */
@ConvertAsProperties(
        dtd = "-//uk.ac.isc.agencypiechartview//AgencyPieChartView//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "AgencyPieChartViewTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "properties", openAtStartup = true)
@ActionID(category = "Window", id = "uk.ac.isc.agencypiechartview.AgencyPieChartViewTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_AgencyPieChartViewAction",
        preferredID = "AgencyPieChartViewTopComponent"
)
@Messages({
    "CTL_AgencyPieChartViewAction=AgencyPieChartView",
    "CTL_AgencyPieChartViewTopComponent=AgencyPieChartView Window",
    "HINT_AgencyPieChartViewTopComponent=This is a AgencyPieChartView window"
})
public final class AgencyPieChartViewTopComponent extends TopComponent implements SeisDataChangeListener {

    //processed data with sorted percentages
    private final PieChartData pcData;
    private final JScrollPane scrollPane;
    //the key class of the piechart
    private final AgencyPieChartView apcView = new AgencyPieChartView();

    private static SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final PhasesList phasesList = Global.getPhasesList();
    
    public AgencyPieChartViewTopComponent() {
        initComponents();
        setName(Bundle.CTL_AgencyPieChartViewTopComponent());
        setToolTipText(Bundle.HINT_AgencyPieChartViewTopComponent());
 
        selectedSeisEvent.addChangeListener(this);
        
        pcData = new PieChartData(phasesList.getPhases());
        apcView.setData(pcData);

        scrollPane = new JScrollPane(apcView);
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

    //once the phase data changes, repaint the figure
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {

        pcData.UpdateData(phasesList.getPhases());
        apcView.repaint();
        //evid = ((EventsControlViewTopComponent) tc).getControlPanel().getSelectedSeisEvent().getEvid();

        //agencyLikelihood.clear();
        //SeisDataDAO.retrieveAgencyLikelihood(agencyLikelihood, evid);
        //alView.repaint();
        scrollPane.setViewportView(apcView);
    }
}
