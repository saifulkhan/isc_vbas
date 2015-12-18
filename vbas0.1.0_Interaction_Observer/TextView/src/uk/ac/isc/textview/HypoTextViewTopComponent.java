/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.isc.textview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.SimpleDateFormat;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;

/**
 * Top component which displays the hypocentre table of the selected event. It
 * is an observer of the SeisDataChangeEvent.
 */
@ConvertAsProperties(
        dtd = "-//uk.ac.isc.textview//HypoTextView//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "HypoTextViewTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "hypotext", openAtStartup = true)
@ActionID(category = "Window", id = "uk.ac.isc.textview.HypoTextViewTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_HypoTextViewAction",
        preferredID = "HypoTextViewTopComponent"
)
@Messages({
    "CTL_HypoTextViewAction=HypoTextView",
    "CTL_HypoTextViewTopComponent=HypoTextView Window",
    "HINT_HypoTextViewTopComponent=This is a HypoTextView window"
})
public final class HypoTextViewTopComponent extends TopComponent implements SeisDataChangeListener {

    private final HypocentresList hypocentresList;      // hypo list
    private static JTextPane header = new JTextPane();  // the Pane to show the headerString
    private String headerString;
    
    private HypoTableModel hptvtModel = null;           //table model for the hypo table
  
    private JTable hyposTable = null;
    private JScrollPane scrollPane = null;

    // get control window to retrieve data
    //private final TopComponent tc = WindowManager.getDefault().findTopComponent("EventsControlViewTopComponent");
    //private static SeisEvent currentEvent = ((EventsControlViewTopComponent) tc).getSelectedSeisEvent();
    private static SeisEvent currentEvent; 
    
    
    public HypoTextViewTopComponent() {
        initComponents();
        setName(Bundle.CTL_HypoTextViewTopComponent());
        setToolTipText(Bundle.HINT_HypoTextViewTopComponent());

        //currentEvent = ((EventsControlViewTopComponent) tc).getControlPanel().getSelectedSeisEvent();
        //hypocentresList = ((EventsControlViewTopComponent) tc).getControlPanel().getHyposList();
        currentEvent = Global.getSelectedSeisEvent();
        hypocentresList = Global.getHypocentresList();
       
        hptvtModel = new HypoTableModel(hypocentresList.getHypocentres());
        hyposTable = new JTable(hptvtModel);
        setupTableVisualAttributes();
        setHeaderText();
        
        // add the popup-menu
        new HypoTablePopupManager(hyposTable);
        
        scrollPane = new JScrollPane(hyposTable);
        this.setLayout(new BorderLayout());
        this.add(header, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    
    private void setupTableVisualAttributes() {
             
        JTableHeader th = hyposTable.getTableHeader();
        th.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        th.setBackground(new Color(43,87,151));            // Blue
        th.setForeground(Color.white);
        
        hyposTable.setRowSelectionAllowed(true);
        hyposTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hyposTable.setColumnSelectionAllowed(false);
        hyposTable.setSelectionBackground(new Color(45,137,239));
        hyposTable.setSelectionForeground(Color.WHITE);
        hyposTable.setRowSelectionInterval(0, 0);
        
        hyposTable.setRowHeight(25);
        hyposTable.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        hyposTable.setShowGrid(false);
        hyposTable.setShowVerticalLines(false);
        hyposTable.setShowHorizontalLines(false);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        hyposTable.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        hyposTable.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);
        
    }
    
    private void setHeaderText() {
         // The hypocentre table header: shows overall information
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        headerString = currentEvent.getLocation() 
                + "  "
                + dateFormat.format(currentEvent.getPrimeHypo().getOrigTime())
                + "\n "
                + "Default Grid Depth: ";
        
        if (SeisDataDAO.retrieveDefaultGridDepth(currentEvent.getPrimeHypo().getHypid()) != null) {
            headerString += SeisDataDAO.retrieveDefaultGridDepth(currentEvent.getPrimeHypo().getHypid()).toString();
        } else {
            headerString += "N/A";
        }

        //header = new JTextPane();
        header.setText(headerString);
        header.setEditable(false);
        header.setFont(new Font("Sans-Serif", Font.BOLD, 14));
        StyledDocument doc = header.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
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
        hypocentresList.addChangeListener(this);
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
        hypocentresList.removeChangeListener(this);
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

    /*
     * notification event / fire event generated.
     * Repaint if data changes.
     */
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {

        System.out.println("DEBUG: " + Thread.currentThread().getStackTrace()[1].getLineNumber() + ", " + "public void SeisDataChanged(SeisDataChangeEvent event)");

        //start concatenating the header string
        //currentEvent = ((EventsControlViewTopComponent) tc).getControlPanel().getSelectedSeisEvent();
        currentEvent = Global.getSelectedSeisEvent();
         
        setHeaderText();
        header.repaint();

        hyposTable.clearSelection();
        scrollPane.setViewportView(hyposTable);
        scrollPane.repaint();
    }

}
