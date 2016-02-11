/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.isc.textview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.Hypocentre;
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

    private static JTextPane header = new JTextPane();  // the Pane to show the headerString
    private String headerString;

    private JTable table = null;
    private JScrollPane scrollPane = null;
    private ListSelectionListener  lsl = null;
    private final HypoTablePopupManager htPopupManager;

    private static final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final HypocentresList hypocentresList = Global.getHypocentresList();
    private static final Hypocentre selectedHypocentre = Global.getSelectedHypocentre();
        
                  
    public HypoTextViewTopComponent() {
        initComponents();
        setName(Bundle.CTL_HypoTextViewTopComponent());
        setToolTipText(Bundle.HINT_HypoTextViewTopComponent());

        selectedSeisEvent.addChangeListener(this);

        table = new JTable();
        
        lsl= new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                // disable the double calls
                if (!lse.getValueIsAdjusting()) {
                    onValueChanged(lse);
                }
            }
        };
            
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                onMouseClicked(evt);
            }

        });
      
        table.setModel(new HypoTableModel(hypocentresList.getHypocentres()));
        table.getSelectionModel().addListSelectionListener(lsl);
        
        setupTableVisualAttributes();
        setHeaderText();

        // add the popup-menu
        htPopupManager = new HypoTablePopupManager(table);

        scrollPane = new JScrollPane(table);
        this.setLayout(new BorderLayout());
        this.add(header, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /*
     * When a row (Hypocentre) is selected. Fire an event.
     */
    public void onValueChanged(ListSelectionEvent lse) {
        System.out.println(Global.debugAt() + " New Hypocentre is selected.");
        int selectedRowNum = table.getSelectedRow();
        
        Hypocentre hypocentre = hypocentresList.getHypocentres().get(selectedRowNum);
        selectedHypocentre.setValues(hypocentre);   
        System.out.println("Selected: row= " + selectedRowNum + "Hypocentre= " + (Integer) table.getValueAt(selectedRowNum, 9) + ". Fire an event.");
        selectedHypocentre.fireSeisDataChanged();
    }
    
    
    private void onMouseClicked(MouseEvent e) {
        System.out.println(Global.debugAt());
        
        Point p = e.getPoint();
        final int row = table.rowAtPoint(p);
        final int col = table.columnAtPoint(p);
        int selectedRow = table.getSelectedRow();
        int selectedCol = table.getSelectedColumn();
       
        if(htPopupManager.getPopupMenu().isVisible())
            htPopupManager.getPopupMenu().setVisible(false);
        
        /*
        // Update the current selection for correct htPopupManager behavior
        // in case a new selection is made with the right mouse button.
        if(row != selectedRow || col != selectedCol) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    table.changeSelection(row, col, true, false);
                }
            });
        }*/
        
        System.out.println(Global.debugAt() + "1. selectedRow= " + selectedRow + ", selectedCol= " + selectedCol);
        
        // Specify the condition(s) you want for htPopupManager display.
        // For Example: show htPopupManager only if a row & column is selected.
        if(selectedRow >= 0 && selectedCol >= 0) {
            System.out.println(Global.debugAt() + "2. selectedRow= " + selectedRow + ", selectedCol= " + selectedCol);
            if(SwingUtilities.isRightMouseButton(e)) {
                Rectangle r = table.getCellRect(row, col, false);
                htPopupManager.getPopupMenu().show(table, r.x, r.y+r.height);
                System.out.println(Global.debugAt() + "selectedRow= " + selectedRow + ", selectedCol= " + selectedCol);
            } else {
                e.consume();
            }
        }
    }

    
    /*
     * Receive new event selection event. 
     * Repaint if data changes.
     */
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
        System.out.println(Global.debugAt() + " Event received from " + event.getData().getClass().getName());
        // Types of event: Selected Event, Selected Hypocentre (?).
                
        // Remove the previous (row) selection listener, if any.
        table.getSelectionModel().removeListSelectionListener(lsl);
        table.setModel(new HypoTableModel(hypocentresList.getHypocentres()));
        
        table.clearSelection();
        setHeaderText();
        header.repaint();

        scrollPane.setViewportView(table);
        scrollPane.repaint();
        
        // Note: keep this call here! 
        // Add the (row) selection listener.  
        table.getSelectionModel().addListSelectionListener(lsl);
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
        //hyposTable.setRowSelectionInterval(0, 0);

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

    private void setHeaderText() {
        // The hypocentre table header: shows overall information
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        headerString = selectedSeisEvent.getLocation()
                + "  "
                + dateFormat.format(selectedSeisEvent.getPrimeHypo().getOrigTime())
                + "\n "
                + "Default Grid Depth: ";

        if (SeisDataDAO.retrieveDefaultGridDepth(selectedSeisEvent.getPrimeHypo().getHypid()) != null) {
            headerString += SeisDataDAO.retrieveDefaultGridDepth(selectedSeisEvent.getPrimeHypo().getHypid()).toString();
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

}
