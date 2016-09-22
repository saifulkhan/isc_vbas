/*
 * To change this license textPaneSummary, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.isc.textview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
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
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
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

    private JSplitPane splitPane;
    private JScrollPane summaryScrollPane;

    private JTable table = null;
    private JScrollPane tableScrollPane = null;
    private ListSelectionListener lsl = null;
    private final HypocentreTablePopupMenu htPopupManager;

    private final static String LINK_ATTRIBUTE = "linkact";
    private StyledDocument doc;
    private JTextPane textPane;
    private String nearbyEventsURL;

    private static final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final HypocentresList hypocentresList = Global.getHypocentresList();
    private static final Hypocentre selectedHypocentre = Global.getSelectedHypocentre();

    public HypoTextViewTopComponent() {
        initComponents();
        setName(Bundle.CTL_HypoTextViewTopComponent());
        setToolTipText(Bundle.HINT_HypoTextViewTopComponent());
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
        putClientProperty(TopComponent.PROP_SLIDING_DISABLED, Boolean.FALSE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.FALSE);
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

        summaryScrollPane = new JScrollPane(getSummaryTextPane());

        // add the popup-menu
        htPopupManager = new HypocentreTablePopupMenu(table);

        tableScrollPane = new JScrollPane(table);
        this.setLayout(new BorderLayout());
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                tableScrollPane, summaryScrollPane);
        splitPane.setResizeWeight(0.95);
        this.add(splitPane);
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

            Point p = e.getPoint();
            final int row = table.rowAtPoint(p);
            final int col = table.columnAtPoint(p);
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

        summaryScrollPane.setViewportView(getSummaryTextPane());
        summaryScrollPane.repaint();

        tableScrollPane.setViewportView(table);
        tableScrollPane.repaint();

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

    private MyTextPane getSummaryTextPane() {

        MyTextPane myTextPane = new MyTextPane();
        myTextPane.setEditable(false);
        myTextPane.addMouseListener(new MyLinkController(myTextPane));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String summary = selectedSeisEvent.getEvid() + "\n"
                + selectedSeisEvent.getLocation() + ", "
                + dateFormat.format(selectedSeisEvent.getPrimeHypo().getOrigTime()) + ", "
                + selectedSeisEvent.geteType() + "\n"
                + "Grid Depth: " + selectedSeisEvent.getDefaultDepthGrid() + "\n";

        myTextPane.append("Summary\n", Color.BLACK, 14, false, true, StyleConstants.ALIGN_CENTER);
        myTextPane.append(summary, Color.BLACK, 14, false, false, StyleConstants.ALIGN_LEFT);

        myTextPane.append("\nLocator Message\n", Color.BLACK, 14, false, true, StyleConstants.ALIGN_CENTER);
        myTextPane.append(selectedSeisEvent.getLocatorMessage(), Color.BLACK, 14, false, false, StyleConstants.ALIGN_LEFT);

        myTextPane.append("\nNearby Events\n", Color.BLACK, 14, false, true, StyleConstants.ALIGN_CENTER);

        if (selectedSeisEvent.getNearbyEvents() != null) {
            //VBASLogger.logDebug("nearbyEvents:" + selectedSeisEvent.getNearbyEvents());
            String[] nearbyEvents = selectedSeisEvent.getNearbyEvents().split(" ");
            for (String ev : nearbyEvents) {

                try {
                    URL url = new URL("http://192.168.37.88/cgi-bin/web-db-v4?event_id="
                            + ev
                            + "&out_format=IMS1.0&request=COMPREHENSIVE&table_owner="
                            + SeisDataDAO.getPgUser());

                    myTextPane.addHyperlink(url, ev + "\n", Color.blue, 14, false, false, StyleConstants.ALIGN_LEFT);
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }

            }
        }

            return myTextPane;
        
    }
        /**
         * This method is called from within the constructor to initialize the
         * form. WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
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

/**
 * *******************************************************************************************************************
 * Text formating Hint:
 * (1)https://docs.oracle.com/javase/tutorial/uiswing/components/editorpane.html
 * (2)https://www.daniweb.com/programming/software-development/threads/331500/how-can-i-add-a-clickable-url-in-a-jtextpane
 * *******************************************************************************************************************
 */
class MyTextPane extends JTextPane {

    public void append(String text,
            Color c,
            int font,
            Boolean isItalic,
            Boolean isBold,
            int alignment /*StyleConstants.ALIGN_CENTER*/) {

        try {
            Document doc = this.getDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, c);
            StyleConstants.setFontSize(attrs, font);
            if (isItalic) {
                StyleConstants.setItalic(attrs, true);
            }
            if (isBold) {
                StyleConstants.setBold(attrs, true);
            }
            StyleConstants.setAlignment(attrs, alignment);

            doc.insertString(doc.getLength(), text, attrs);

        } catch (BadLocationException e) {
            e.printStackTrace(System.err);
        }
    }

    public void addHyperlink(URL url,
            String text,
            Color c,
            int font,
            Boolean isItalic,
            Boolean isBold,
            int alignment /*StyleConstants.ALIGN_CENTER*/) {
        try {
            Document doc = this.getDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, c);
            StyleConstants.setFontSize(attrs, font);
            if (isItalic) {
                StyleConstants.setItalic(attrs, true);
            }
            if (isBold) {
                StyleConstants.setBold(attrs, true);
            }
            StyleConstants.setAlignment(attrs, alignment);

            StyleConstants.setUnderline(attrs, true);
            attrs.addAttribute(HTML.Attribute.HREF, url.toString());
            doc.insertString(doc.getLength(), text, attrs);

        } catch (BadLocationException e) {
            e.printStackTrace(System.err);
        }
    }

}

class MyLinkController extends MouseAdapter implements MouseMotionListener {

    private MyTextPane myTextPane;
    private String copiedSelection = null;
    private Clipboard clipboard;

    MyLinkController(MyTextPane myTextPane) {
        this.myTextPane = myTextPane;
    }

    public void mouseReleased(MouseEvent e) {
        /*copiedSelection = myTextPane.getSelectedText();

        if (copiedSelection != null) {
            StringSelection data = new StringSelection(copiedSelection);
            if (data != null) {
                clipboard.setContents(data, data);
            }
        }*/
    }

    public void mouseClicked(MouseEvent e) {
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        JTextPane editor = (JTextPane) e.getSource();
        Document doc = editor.getDocument();
        Point pt = new Point(e.getX(), e.getY());
        int pos = editor.viewToModel(pt);

        if (pos >= 0) {
            if (doc instanceof DefaultStyledDocument) {
                DefaultStyledDocument hdoc = (DefaultStyledDocument) doc;
                Element el = hdoc.getCharacterElement(pos);
                AttributeSet a = el.getAttributes();
                String href = (String) a.getAttribute(HTML.Attribute.HREF);

                if (href != null) {
                    try {
                        java.net.URI uri = new java.net.URI(href);
                        desktop.browse(uri);
                    } catch (Exception ev) {
                        System.err.println(ev.getMessage());
                    }
                }
            }
        }
    }

    public void mouseMoved(MouseEvent ev) {

        Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
        JTextPane editor = (JTextPane) ev.getSource();
        Point pt = new Point(ev.getX(), ev.getY());
        int pos = editor.viewToModel(pt);

        if (pos >= 0) {
            Document doc = editor.getDocument();

            if (doc instanceof DefaultStyledDocument) {
                DefaultStyledDocument hdoc = (DefaultStyledDocument) doc;
                Element e = hdoc.getCharacterElement(pos);
                AttributeSet a = e.getAttributes();
                String href = (String) a.getAttribute(HTML.Attribute.HREF);

                if (href != null) {
                    /*if (getCursor() != handCursor) {
                     editor.setCursor(handCursor);
                     }*/
                } else {
                    editor.setCursor(defaultCursor);
                }
            }
        } else {
            /*setToolTipText(null);*/
        }
    }

}//LinkController

