/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.isc.textview;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import uk.ac.isc.eventscontrolview.EventsControlViewTopComponent;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;

/**
 * Top component which displays the hypocentre table of the selected event.
 * It is an observer of the SeisDataChangeEvent.
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
    
    //hypo list
    private final HypocentresList hyposList;

    //the string to show overall information
    private String headerString;
    //the Pane to show the headerString
    private static JTextPane header;
            
    //table model for the hypo table
    private HypoTextViewTableModel hptvtModel = null;
    
    //hypocentre table
    private JTable hyposTable = null;
    
    private JScrollPane scrollPane = null;
    
    //get control window to retrieve data
    private final TopComponent tc = WindowManager.getDefault().findTopComponent("EventsControlViewTopComponent");
    
    private static SeisEvent currentEvent;// = ((EventsControlViewTopComponent) tc).getSelectedSeisEvent();
    
    public HypoTextViewTopComponent() {
        initComponents();
        setName(Bundle.CTL_HypoTextViewTopComponent());
        setToolTipText(Bundle.HINT_HypoTextViewTopComponent());
        
        currentEvent = ((EventsControlViewTopComponent) tc).getControlPanel().getSelectedSeisEvent();
        hyposList = ((EventsControlViewTopComponent) tc).getControlPanel().getHyposList();
        
        //start concatenating the header string
        headerString = "";
        headerString += currentEvent.getLocation();
        headerString += "  ";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        headerString += sdf.format(currentEvent.getPrimeHypo().getOrigTime());
        headerString += "\n Default Grid Depth: ";
        if(SeisDataDAO.retrieveDefaultGridDepth(currentEvent.getPrimeHypo().getHypid())!=null)
        {
            headerString += SeisDataDAO.retrieveDefaultGridDepth(currentEvent.getPrimeHypo().getHypid()).toString();
        }
        else
        {
            headerString += "N/A";
        }
        
        header = new JTextPane();
        header.setText(headerString);
        header.setEditable(false);
        Font font1 = new Font("SansSerif", Font.BOLD, 20);
        header.setFont(font1);
        StyledDocument doc = header.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        
        hptvtModel = new HypoTextViewTableModel(hyposList.getHypocentres());
        hyposTable = new JTable(hptvtModel);
        
        hyposTable.setRowHeight(40);
        hyposTable.setFont(new Font("monospaced",Font.PLAIN, 16));
        
        hyposTable.setShowGrid(false);
        hyposTable.setShowVerticalLines(false);
        hyposTable.setShowHorizontalLines(false);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        //DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        //leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        hyposTable.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        hyposTable.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);
        
        scrollPane = new JScrollPane(hyposTable);
        this.setLayout(new BorderLayout());
        
        this.add(header, BorderLayout.NORTH);
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
        hyposList.addChangeListener(this);
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
        hyposList.removeChangeListener(this);
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

    //repaint if data changes
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
        
        //start concatenating the header string
        currentEvent = ((EventsControlViewTopComponent) tc).getControlPanel().getSelectedSeisEvent();
        
        headerString = "";
        headerString += currentEvent.getLocation();
        headerString += "  ";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        headerString += sdf.format(currentEvent.getPrimeHypo().getOrigTime());
        headerString += "\n Default Grid Depth: ";
        if(SeisDataDAO.retrieveDefaultGridDepth(currentEvent.getPrimeHypo().getHypid())!=null)
        {
            headerString += SeisDataDAO.retrieveDefaultGridDepth(currentEvent.getPrimeHypo().getHypid()).toString();
        }
        else
        {
            headerString += "N/A";
        }
        
        header.setText(headerString);
        header.setEditable(false);
        //Font font1 = new Font("SansSerif", Font.BOLD, 20);
        //header.setFont(font1);
        //header.setHorizontalAlignment(JTextField.CENTER);
        
        header.repaint();
        
        hyposTable.clearSelection();
        scrollPane.setViewportView(hyposTable);
        scrollPane.repaint();
        
    }
}
