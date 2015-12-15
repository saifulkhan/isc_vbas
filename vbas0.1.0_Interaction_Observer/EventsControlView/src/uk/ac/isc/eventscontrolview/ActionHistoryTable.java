package uk.ac.isc.eventscontrolview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import uk.ac.isc.seisdata.ActionHistoryList;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisEvent;

public class ActionHistoryTable extends JPanel implements SeisDataChangeListener {

    private JTable table;
    private JButton buttonBanish;
    private JButton buttonDone;
    private JButton buttonAssess;
    private JButton buttonCommit;

    private final ActionHistoryList actionHistoryList; 

    // used to fetch event from the EventTable / EventControlView
    private static SeisEvent currentEvent;
    //private final TopComponent tc = WindowManager.getDefault().findTopComponent("EventsControlViewTopComponent");
    
    
    public ActionHistoryTable() {
        initLayout();
        initActionListeners();
        
        //actionHistoryList.addChangeListener(this);
        
        //currentEvent = ((EventsControlViewTopComponent) tc).getControlPanel().getSelectedSeisEvent();
        System.out.println("DEBUG: " + Thread.currentThread().getStackTrace()[1].getLineNumber() + ", " + "ActionHistoryTable::ActionHistoryTable() : currentEvent = " + currentEvent);
        
        actionHistoryList = Global.getActionHistoryList();
        actionHistoryList.addChangeListener(this);
   }

    private void initLayout() {

        // Table    
        table = new JTable(new ActionHistoryTableModel());
        table.setPreferredScrollableViewportSize(new Dimension(450, 120));
        table.setFillsViewportHeight(true);

        table.getColumnModel().getColumn(0).setPreferredWidth(25);
        table.getColumnModel().getColumn(1).setPreferredWidth(25);
        table.getColumnModel().getColumn(2).setPreferredWidth(350);
        //initColumnSizes(); 

        // Table: listener
        //table.getModel().addTableModelListener(this);

        // Layout    
        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new JPanel();
        this.add(topPanel, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.LINE_END);

        // Layout : add table in the top panel
        JScrollPane scrollPane = new JScrollPane(table);
        topPanel.add(scrollPane, BorderLayout.CENTER);

        //add(bottomPanel, BorderLayout.SOUTH);
        //topPanel.add(scrollPane);
        bottomPanel.setLayout(new FlowLayout());

        buttonBanish = new JButton("Banish");
        buttonDone = new JButton("Done");
        buttonAssess = new JButton("Assess");
        buttonCommit = new JButton("Commit");

        bottomPanel.add(buttonBanish);
        bottomPanel.add(buttonDone);
        bottomPanel.add(buttonAssess);
        bottomPanel.add(buttonCommit);

    }

    public void initActionListeners() {

        buttonAssess.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Asses: clicked!", " ", JOptionPane.WARNING_MESSAGE);
            }
        });

    }

    /*
     * This method picks good column sizes. 
     * If all column heads are wider than the column's cells' contents, then you can just use column.sizeWidthToFit().
     */
    public void initColumnSizes() {
        /* ActionHistory model = (ActionHistory) table.getModel();
         TableColumn column = null;
         Component comp = null;
         int headerWidth = 0;
         int cellWidth = 0;
        
         Object[] longValues = model.longValues;
         TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

         for (int i = 0; i < 5; i++) {
         column = table.getColumnModel().getColumn(i);

         comp = headerRenderer.getTableCellRendererComponent(
         null, column.getHeaderValue(),
         false, false, 0, 0);
         headerWidth = comp.getPreferredSize().width;

         comp = table.getDefaultRenderer(model.getColumnClass(i)).
         getTableCellRendererComponent(
         table, longValues[i],
         false, false, 0, i);
         cellWidth = comp.getPreferredSize().width;


         System.out.println("Initializing width of column "
         + i + ". "
         + "headerWidth = " + headerWidth
         + "; cellWidth = " + cellWidth);

         column.setPreferredWidth(Math.max(headerWidth, cellWidth));
         }   */
    }

    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
       
        //currentEvent = ((EventsControlViewTopComponent) tc).getControlPanel().getSelectedSeisEvent();
        
       System.out.println("DEBUG: " + Thread.currentThread().getStackTrace()[1].getLineNumber() + ", " + "ActionHistoryTable::SeisDataChanged() : currentEvent = " + currentEvent); 
       //actionHistoryList = Global.getActionHistoryList();
       
    }


}
