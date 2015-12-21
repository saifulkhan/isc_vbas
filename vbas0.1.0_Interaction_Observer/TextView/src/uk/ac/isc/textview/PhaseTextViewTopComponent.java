package uk.ac.isc.textview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.TreeMap;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import uk.ac.isc.eventscontrolview.EventsControlViewTopComponent;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;

/**
 * Top component which displays phase table.
 */
@ConvertAsProperties(
        dtd = "-//uk.ac.isc.textview//PhaseTextView//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "PhaseTextViewTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "phasetext", openAtStartup = true)
@ActionID(category = "Window", id = "uk.ac.isc.textview.PhaseTextViewTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_PhaseTextViewAction",
        preferredID = "PhaseTextViewTopComponent"
)
@Messages({
    "CTL_PhaseTextViewAction=PhaseTextView",
    "CTL_PhaseTextViewTopComponent=PhaseTextView Window",
    "HINT_PhaseTextViewTopComponent=This is a PhaseTextView window"
})

public final class PhaseTextViewTopComponent extends TopComponent implements SeisDataChangeListener {

    private final PhasesList phasesList; // reference of phase list
    private final TreeMap<String, String> stations;
    
    private PhaseTextViewTableModel ptvtModel = null; // phase table model for the table
    private JTable phaseTable = null;
    private JScrollPane longScrollPane = null;

    public PhaseTextViewTopComponent() {
        initComponents();
        setName(Bundle.CTL_PhaseTextViewTopComponent());
        setToolTipText(Bundle.HINT_PhaseTextViewTopComponent());
        
        phasesList = Global.getPhasesList();
        stations = Global.getStations();
        
        ptvtModel = new PhaseTextViewTableModel(phasesList.getPhases());
        phaseTable = new JTable(ptvtModel);
        setupTableVisualAttributes();


        longScrollPane = new JScrollPane(phaseTable);
        this.setLayout(new BorderLayout());
        this.add(longScrollPane, BorderLayout.CENTER);
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
        phasesList.addChangeListener(this);
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
        phasesList.removeChangeListener(this);
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

    //repaint when data changes
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {

        ptvtModel = new PhaseTextViewTableModel(phasesList.getPhases());
        
        // put the region name into the pahseList
        for (int i = 0; i < phasesList.getPhases().size(); i++) {
            phasesList.getPhases()
                    .get(i)
                    .setRegionName(stations.get(phasesList.getPhases().get(i).getReportStation()));
        }


        phaseTable.clearSelection();
        //setupLongTableVisualAttributes(phaseTable);

        longScrollPane.setViewportView(phaseTable);
        longScrollPane.repaint();
    }

     
    private void setupTableVisualAttributes() {
             
        JTableHeader th = phaseTable.getTableHeader();
        th.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        th.setBackground(new Color(43,87,151));            // Blue
        th.setForeground(Color.white);
        
        phaseTable.setRowSelectionAllowed(true);
        phaseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        phaseTable.setColumnSelectionAllowed(false);
        phaseTable.setSelectionBackground(new Color(45,137,239));
        phaseTable.setSelectionForeground(Color.WHITE);
        phaseTable.setRowSelectionInterval(0, 0);
        
        phaseTable.setRowHeight(25);
        phaseTable.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        phaseTable.setShowGrid(false);
        phaseTable.setShowVerticalLines(false);
        phaseTable.setShowHorizontalLines(false);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        phaseTable.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        phaseTable.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);
        
    }
    
    
    // set long table format, e.g. column width etc.
    private void setupLongTableVisualAttributes(JTable longTable) {
 
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        //DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        //leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        longTable.setShowGrid(false);
        longTable.setShowVerticalLines(false);
        longTable.setShowHorizontalLines(false);
        longTable.setAutoCreateRowSorter(true);
        longTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        longTable.getColumnModel().getColumn(0).setResizable(false);
        longTable.getColumnModel().getColumn(0).setMinWidth(50);
        longTable.getColumnModel().getColumn(0).setMaxWidth(50);
        longTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        longTable.getColumnModel().getColumn(2).setPreferredWidth(80); //station code
        longTable.getColumnModel().getColumn(3).setPreferredWidth(120); //time
        longTable.getColumnModel().getColumn(4).setPreferredWidth(200); //region name

        longTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        longTable.getColumnModel().getColumn(6).setPreferredWidth(60);

        longTable.getColumnModel().getColumn(7).setPreferredWidth(60); //phase name
        longTable.getColumnModel().getColumn(8).setPreferredWidth(60); //isc phase name

        longTable.getColumnModel().getColumn(10).setMinWidth(40);
        longTable.getColumnModel().getColumn(10).setMaxWidth(40);

        longTable.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);
        longTable.getColumnModel().getColumn(8).setCellRenderer(rightRenderer);
        longTable.getColumnModel().getColumn(9).setCellRenderer(rightRenderer);
        longTable.getColumnModel().getColumn(10).setCellRenderer(rightRenderer);
        longTable.getColumnModel().getColumn(11).setCellRenderer(rightRenderer);

        longTable.setRowHeight(25);
        longTable.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        longTable.setShowGrid(false);
        longTable.setShowVerticalLines(false);
        longTable.setShowHorizontalLines(false);
                 
    }
    
}