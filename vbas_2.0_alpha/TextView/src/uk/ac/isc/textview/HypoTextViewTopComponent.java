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
import uk.ac.isc.seisdatainterface.Global;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdatainterface.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.VBASLogger;

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
    "CTL_HypoTextViewAction=Hypocentre Selection",
    "CTL_HypoTextViewTopComponent=Hypocentre Selection",
    "HINT_HypoTextViewTopComponent=Hypocentre Selection"
})
public final class HypoTextViewTopComponent extends TopComponent implements SeisDataChangeListener {

    private static JTextPane header = new JTextPane();  // the Pane to show the headerString
    private String headerString;

    private JTable table = null;
    private JScrollPane scrollPane = null;
    private ListSelectionListener lsl = null;
    private final HypocentreTablePopupMenu htPopupManager;

    private static final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final HypocentresList hypocentresList = Global.getHypocentresList();
    private static final Hypocentre selectedHypocentre = Global.getSelectedHypocentre();

    public HypoTextViewTopComponent() {
        initComponents();
        setName(Bundle.CTL_HypoTextViewTopComponent());
        setToolTipText(Bundle.HINT_HypoTextViewTopComponent());
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_SLIDING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
        //setName("Hypocentre Selection");

        VBASLogger.logDebug("Loaded...");

        selectedSeisEvent.addChangeListener(this);

        table = new JTable();
        lsl = new ListSelectionListener() {
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

        table.setModel(new HypocentreTableModel(hypocentresList.getHypocentres()));
        table.getSelectionModel().addListSelectionListener(lsl);

        setupTableVisualAttributes();
        setHeaderText();

        // add the popup-menu
        htPopupManager = new HypocentreTablePopupMenu(table);

        scrollPane = new JScrollPane(table);
        this.setLayout(new BorderLayout());
        this.add(header, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);

    }

    /*
     * When a row (Hypocentre) is selected. Fire an event.
     */
    public void onValueChanged(ListSelectionEvent lse) {
        VBASLogger.logDebug("New Hypocentre is selected.");
        int selectedRowNum = table.getSelectedRow();
        Hypocentre hypocentre = hypocentresList.getHypocentres().get(selectedRowNum);

        selectedHypocentre.setValues(hypocentre);
        VBASLogger.logDebug("'SeisEvent' changed, fire an event."
                + ", Selected row=" + selectedRowNum
                + ", Hypocentre= " + (Integer) table.getValueAt(selectedRowNum, 9));
        selectedHypocentre.fireSeisDataChanged();
    }

    private void onMouseClicked(MouseEvent e) {

        Point p = e.getPoint();
        final int row = table.rowAtPoint(p);
        final int col = table.columnAtPoint(p);
        int selectedRow = table.getSelectedRow();
        int selectedCol = table.getSelectedColumn();

        if (htPopupManager.getPopupMenu().isVisible()) {
            htPopupManager.getPopupMenu().setVisible(false);
        }

        // Specify the condition(s) you want for htPopupManager display.
        // For Example: show htPopupManager only if a row & column is selected.
        if (selectedRow >= 0 && selectedCol >= 0) {
            VBASLogger.logDebug("selectedRow=" + selectedRow
                    + ", selectedCol=" + selectedCol);

            if (SwingUtilities.isRightMouseButton(e)) {
                Rectangle r = table.getCellRect(row, col, false);
                htPopupManager.getPopupMenu().show(table, r.x, r.y + r.height);
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
        VBASLogger.logDebug("Event received from " + event.getData().getClass().getName());
        // Types of event: Selected Event, Selected Hypocentre (?).

        // Remove the previous (row) selection listener, if any.
        table.getSelectionModel().removeListSelectionListener(lsl);
        table.setModel(new HypocentreTableModel(hypocentresList.getHypocentres()));

        // setup visual attributes again
        setupTableVisualAttributes();

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
        /*th.setBackground(new Color(43, 87, 151));            // Blue
         th.setForeground(Color.white);*/

        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);
        /*table.setSelectionBackground(new Color(45, 137, 239));
         table.setSelectionForeground(Color.WHITE);*/
        //hyposTable.setRowSelectionInterval(0, 0);

        table.setRowHeight(25);
        table.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        table.setShowGrid(false);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);

        // Set: Left or Right aligned
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);

        // This part of the code picks good column sizes. 
        // If all column heads are wider than the column's cells'
        // contents, then you can just use column.sizeWidthToFit().
        // EventsTableModel model = (EventsTableModel) table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;

        Object[] longValues = HypocentreTableModel.longValues;
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < table.getModel().getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
            comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = table.getDefaultRenderer(table.getModel().getColumnClass(i))
                    .getTableCellRendererComponent(table, longValues[i], false, false, 0, i);

            cellWidth = comp.getPreferredSize().width;
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
    }

    private void setHeaderText() {
        VBASLogger.logDebug(selectedSeisEvent.getEvid() + ", "
                + selectedSeisEvent.getPrimeHypo().getOrigTime());

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
        //hypocentresList.addChangeListener(this);
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
        //hypocentresList.removeChangeListener(this);
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
