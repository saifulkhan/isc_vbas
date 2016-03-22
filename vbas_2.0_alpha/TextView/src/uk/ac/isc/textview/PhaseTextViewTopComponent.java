package uk.ac.isc.textview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import uk.ac.isc.seisdatainterface.Global;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.VBASLogger;

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
    "CTL_PhaseTextViewAction=Phase Selection",
    "CTL_PhaseTextViewTopComponent=Phase Selection",
    "HINT_PhaseTextViewTopComponent=Phase Selection"
})

public final class PhaseTextViewTopComponent extends TopComponent implements SeisDataChangeListener {

    private PhaseTextViewTableModel model = null; // phase table model for the table
    private JTable table = null;
    private JScrollPane scrollPane = null;
    private PhaseTableSortPanel phaseTableSortPanel = null;
    private final PhaseTablePopupManager ptPopupManager;

    private static SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private static final PhasesList phasesList = Global.getPhasesList();
    // selected phase in Phase Selection table
    private static final PhasesList selectedPhaseList = Global.getSelectedPhaseList();

    public PhaseTextViewTopComponent() {
        initComponents();
        setName(Bundle.CTL_PhaseTextViewTopComponent());
        setToolTipText(Bundle.HINT_PhaseTextViewTopComponent());
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_SLIDING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);
        //setName("Phase Selection");

        VBASLogger.logDebug("Here...");

        selectedSeisEvent.addChangeListener(this);

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

        /*        
         * Selection : selection of row(s) and col(s) generate mouse-event, handled by MyRowSelectionListener.
         * Click : Mouse-click generate another event, handled by MyMouseAdapter.
         */
        MyRowSelectionListener rowListener = new MyRowSelectionListener();
        table.getSelectionModel().addListSelectionListener(rowListener);
        MyMouseAdapter myMouseAdapter = new MyMouseAdapter();
        table.addMouseListener(myMouseAdapter);

        // add the popup-menu
        ptPopupManager = new PhaseTablePopupManager(table);

        // layout
        scrollPane = new JScrollPane(table);
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        //phaseTableSortPanel = new PhaseTableSortPanel(table);  // TODO: Add default sort later.
        //this.add(phaseTableSortPanel, BorderLayout.PAGE_START);
    }

    // Receive SeisEvent changes and redraw the table.
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
        VBASLogger.logDebug("Event received from " + event.getData().getClass().getName());
        selectedSeisEvent = Global.getSelectedSeisEvent();

        VBASLogger.logDebug("#Phases= " + phasesList.getPhases().size());
        model = new PhaseTextViewTableModel(phasesList.getPhases());
        table.setModel(model);
        setupTableVisualAttributes();

        table.clearSelection();
        scrollPane.setViewportView(table);
        scrollPane.repaint();
    }

    private void setupTableVisualAttributes() {

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        /*th.setBackground(new Color(43, 87, 151));            // Blue
         th.setForeground(Color.white);*/

        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setColumnSelectionAllowed(false);
        //table.setCellSelectionEnabled(false);
        /*table.setSelectionBackground(new Color(45, 137, 239));
         table.setSelectionForeground(Color.WHITE);*/
        //phaseTable.setRowSelectionInterval(0, 0);

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

        table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(10).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(11).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(12).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(13).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(14).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(15).setCellRenderer(centerRenderer);

        // This part of the code picks good column sizes. 
        // If all column heads are wider than the column's cells'
        // contents, then you can just use column.sizeWidthToFit().
        // SiesEventsTableModel model = (SiesEventsTableModel) table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;

        Object[] longValues = model.longValues;
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < model.getColumnCount(); i++) {
            //VBASLogger.logDebug("i=" + i);
            column = table.getColumnModel().getColumn(i);
            comp = headerRenderer.getTableCellRendererComponent( null, column.getHeaderValue(), false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = table.getDefaultRenderer(model.getColumnClass(i))
                    .getTableCellRendererComponent(table, longValues[i], false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }

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

    /*
     *****************************************************************************************
     * Selection related
     *****************************************************************************************
     */
    private class MyRowSelectionListener implements ListSelectionListener {

        // Row selection event
        @Override
        public void valueChanged(ListSelectionEvent event) {
            // disable the double calls
            if (!event.getValueIsAdjusting()) {
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length > 0) {
                    VBASLogger.logDebug("selected rows: " + Arrays.toString(selectedRows) + ", Fired event...");

                    //selectedPhaseList
                    selectedPhaseList.fireSeisDataChanged();
                }

                /*
                 System.out.println("ROW SELECTION EVENT. ");

                 System.out.print(String.format("Lead: %d, %d. ",
                 table.getSelectionModel().getLeadSelectionIndex(),
                 table.getColumnModel().getSelectionModel().
                 getLeadSelectionIndex()));
                 System.out.print("Rows:");
                
                 for (int c : table.getSelectedRows()) {
                 System.out.println(String.format(" %d", c));
                 }
                 System.out.print(". Columns:");
                 for (int c : table.getSelectedColumns()) {
                 System.out.print(String.format(" %d", c));
                 }
                 System.out.print(".\n\n");

                 // fire event for the */
            }
        }
    }


    /*
     *****************************************************************************************
     * Mouse click event related 
     * Mouse click will open the popupmenu
     *****************************************************************************************
     */
    private class MyMouseAdapter extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {
            //VBASLogger.logDebug("");

            Point p = e.getPoint();
            final int row = table.rowAtPoint(p);
            final int col = table.columnAtPoint(p);
            int[] selectedRows = table.getSelectedRows();
            int[] selectedCols = table.getSelectedColumns();

            if (ptPopupManager.getPopupMenu().isVisible()) {
                ptPopupManager.getPopupMenu().setVisible(false);
            }

            // Specify the condition(s) you want for the popup display.
            // For Example: show popup only if a row & column is selected, and mouse right clicked.
            if (selectedRows.length > 0 && selectedCols.length > 0 && SwingUtilities.isRightMouseButton(e)) {
                Rectangle r = table.getCellRect(row, col, false);
                ptPopupManager.getPopupMenu().show(table, r.x, r.y + r.height);
                VBASLogger.logDebug("selected rows: " + Arrays.toString(selectedRows));
            } else {
                e.consume();
            }

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

}
