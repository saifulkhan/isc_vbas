package uk.ac.isc.eventscontrolview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import uk.ac.isc.seisdata.CommandList;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisEvent;

public class CommandTable extends JPanel implements SeisDataChangeListener {

    private JTable actionHistoryTable;
    private CommandTableModel actionHistoryTableModel;
    private JButton buttonBanish;
    private JButton buttonDone;
    private JButton buttonAssess;
    private JButton buttonCommit;

    private final CommandList actionHistoryList; 

    // used to fetch event from the EventTable / EventControlView
    private static SeisEvent currentEvent;
    //private final TopComponent tc = WindowManager.getDefault().findTopComponent("EventsControlViewTopComponent");
    
    
    public CommandTable() {
        
        
        setupLayout();                  // 1 
        setupTableVisualAttributes();   // 2
        initActionListeners();
       
        //actionHistoryList.addChangeListener(this);
           
        System.out.println("DEBUG: " + Thread.currentThread().getStackTrace()[1].getLineNumber() + ", " + "ActionHistoryTable::ActionHistoryTable() : currentEvent = " + currentEvent);
        
        actionHistoryList = Global.getActionHistoryList();
        actionHistoryList.addChangeListener(this);
   }

    
    private void setupLayout() {
        
        actionHistoryTableModel = new CommandTableModel();
        actionHistoryTable = new JTable(actionHistoryTableModel);
         
        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new JPanel();
        this.add(topPanel, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.LINE_END);

        // Layout : add table in the top panel
        JScrollPane scrollPane = new JScrollPane(actionHistoryTable);
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
    
    
    private void setupTableVisualAttributes() {

        JTableHeader th = actionHistoryTable.getTableHeader();
        th.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        th.setBackground(new Color(43,87,151));            // Blue
        th.setForeground(Color.white);
        
        actionHistoryTable.setRowSelectionAllowed(true);
        actionHistoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        actionHistoryTable.setColumnSelectionAllowed(false);
        actionHistoryTable.setSelectionBackground(new Color(45,137,239));
        actionHistoryTable.setSelectionForeground(Color.WHITE);
        actionHistoryTable.setRowSelectionInterval(0, 0);
        
        
        actionHistoryTable.setRowHeight(25);
        actionHistoryTable.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        actionHistoryTable.setShowGrid(false);
        actionHistoryTable.setShowVerticalLines(false);
        actionHistoryTable.setShowHorizontalLines(false);
        
        
        // This part of the code picks good column sizes. 
        // If all column heads are wider than the column's cells'
        // contents, then you can just use column.sizeWidthToFit().
                
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        
        
        Object[] longValues = actionHistoryTableModel.longValues;
        TableCellRenderer headerRenderer = actionHistoryTable.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < actionHistoryTableModel.getColumnCount(); i++) {
            column = actionHistoryTable.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(
                                 null, column.getHeaderValue(),
                                 false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = actionHistoryTable.getDefaultRenderer(actionHistoryTableModel.getColumnClass(i))
                    .getTableCellRendererComponent(actionHistoryTable, 
                            longValues[i], false, false, 0, i);
            
            cellWidth = comp.getPreferredSize().width;

           column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
        
    }
    
    public JTable getActionHistoryTable() {
        return actionHistoryTable;
    }  
    
    public void initActionListeners() {

        buttonAssess.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Asses: clicked!", " ", JOptionPane.WARNING_MESSAGE);
            }
        });

    }


    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
       
        //currentEvent = ((EventsControlViewTopComponent) tc).getControlPanel().getSelectedSeisEvent();
        
       System.out.println("DEBUG: " + Thread.currentThread().getStackTrace()[1].getLineNumber() + ", " + "ActionHistoryTable::SeisDataChanged() : currentEvent = " + currentEvent); 
       //actionHistoryList = Global.getActionHistoryList();
       
    }

}
