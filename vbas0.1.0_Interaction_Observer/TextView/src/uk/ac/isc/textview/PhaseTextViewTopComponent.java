package uk.ac.isc.textview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;

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

    private PhaseTextViewTableModel model = null; // phase table model for the table
    private JTable table = null;
    private JScrollPane scrollPane = null;
    private PhaseTableSortPanel phaseTableSortPanel = null;
    
    private static SeisEvent seisEvent = Global.getSelectedSeisEvent(); 
    private static final PhasesList phasesList = Global.getPhasesList();
    private final TreeMap<String, String> stations = new TreeMap<String, String> ();

    
    public PhaseTextViewTopComponent() {
        initComponents();
        setName(Bundle.CTL_PhaseTextViewTopComponent());
        setToolTipText(Bundle.HINT_PhaseTextViewTopComponent());
        
        seisEvent.addChangeListener(this);
        
        boolean retDAO = SeisDataDAO.retrieveAllPhases(seisEvent.getEvid(), phasesList.getPhases());
        retDAO = SeisDataDAO.retrieveAllPhasesAmpMag(seisEvent.getEvid(), phasesList.getPhases());
        retDAO = SeisDataDAO.retrieveAllStationsWithRegions(stations);                                  // load the correspondent map into the stataions
        // put the region name into the pahseList
        for (int i = 0; i < phasesList.getPhases().size(); i++) {
            phasesList.getPhases().get(i).setRegionName(stations.get(phasesList.getPhases().get(i).getReportStation()));
        }
        
        model = new PhaseTextViewTableModel(phasesList.getPhases());
        table = new JTable();
        table.setModel(model);
        
        setupTableVisualAttributes();

        // Enable sorting of Phase table
        // See: https://groups.google.com/forum/#!topic/comp.lang.java.help/T5avbBnxgkk
        table.setAutoCreateRowSorter(true);
        List<SortKey> sortKeys = new ArrayList<SortKey>();
        sortKeys.add(new SortKey(1, SortOrder.ASCENDING));
        sortKeys.add(new SortKey(0, SortOrder.ASCENDING));
        table.getRowSorter().setSortKeys(sortKeys);

        scrollPane = new JScrollPane(table);
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);

        phaseTableSortPanel = new PhaseTableSortPanel(table);
        this.add(phaseTableSortPanel, BorderLayout.PAGE_START);

    }

    
    // repaint when event changes
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
        System.out.println(Global.debugAt() + " Event received from " + event.getData().getClass().getName());             
        seisEvent = Global.getSelectedSeisEvent();
        
        //phasesList.getPhases().clear();
        //System.out.println(Global.debugAt() + "phasesList.size() = " + phasesList.getPhases().size());
        
        boolean retDAO = SeisDataDAO.retrieveAllPhases(seisEvent.getEvid(), phasesList.getPhases());
        retDAO = SeisDataDAO.retrieveAllPhasesAmpMag(seisEvent.getEvid(), phasesList.getPhases());
       
        //System.out.println(Global.debugAt() + "phasesList.getPhases().size()= " + phasesList.getPhases().size());
        
        // put the region name into the pahseList
        for (int i = 0; i < phasesList.getPhases().size(); i++) {
            phasesList.getPhases()
                    .get(i)
                    .setRegionName(stations.get(phasesList.getPhases().get(i).getReportStation()));
        }
        
        //System.out.println(Global.debugAt() + "phasesList.getPhases().size()= " + phasesList.getPhases().size());
        
        model = new PhaseTextViewTableModel(phasesList.getPhases());
        table.setModel(model);
                
        table.clearSelection();
        scrollPane.setViewportView(table);
        scrollPane.repaint();
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

    private void setupTableVisualAttributes() {

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        th.setBackground(new Color(43, 87, 151));            // Blue
        th.setForeground(Color.white);

        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);
        table.setSelectionBackground(new Color(45, 137, 239));
        table.setSelectionForeground(Color.WHITE);
        //phaseTable.setRowSelectionInterval(0, 0);

        table.setRowHeight(25);
        table.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        table.setShowGrid(false);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);

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
