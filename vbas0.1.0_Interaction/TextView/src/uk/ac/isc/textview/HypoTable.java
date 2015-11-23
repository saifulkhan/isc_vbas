package uk.ac.isc.textview;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Font;
import java.text.SimpleDateFormat;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import uk.ac.isc.eventscontrolview.EventsControlViewTopComponent;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;



/*
 *
 *
 */

public class HypoTable extends JPanel implements SeisDataChangeListener {
  
    //hypo list
    private final HypocentresList hyposList;
    //the string to show overall information
    private String headerString;
    //the Pane to show the headerString
    private static JTextPane header;
    //table model for the hypo table
    private HypoTableModel hptvtModel = null;
    //hypocentre table
    private JTable hyposTable = null;
    private JScrollPane scrollPane = null;
    //get control window to retrieve data
    private final TopComponent tc = WindowManager.getDefault().findTopComponent("EventsControlViewTopComponent");
    private static SeisEvent currentEvent;
        
    public HypoTable() {
       
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
        
        hptvtModel = new HypoTableModel(hyposList.getHypocentres());
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
        
        /*
        super(new GridLayout(1,0));

        JTable table = new JTable(new MyTableModel());
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Set up renderer and editor for the Favorite Color column.
        table.setDefaultRenderer(Color.class,
                                 new ColorRenderer(true));
        table.setDefaultEditor(Color.class,
                               new ColorEditor());

        //Add the scroll pane to this panel.
        add(scrollPane);
        
        
        // Popupmenu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItemAdd = new JMenuItem("Add New Row");
        JMenuItem menuItemRemove = new JMenuItem("Remove Current Row");
        JMenuItem menuItemRemoveAll = new JMenuItem("Remove All Rows");

        popupMenu.add(menuItemAdd);
        popupMenu.add(menuItemRemove);
        popupMenu.add(menuItemRemoveAll);
        table.setComponentPopupMenu(popupMenu);
        
        table.addMouseListener(new TableMouseListener(table));
        */
    }

  

    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 
}



