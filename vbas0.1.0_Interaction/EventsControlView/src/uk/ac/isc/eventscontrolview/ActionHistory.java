package uk.ac.isc.eventscontrolview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

 
public class ActionHistory extends JPanel implements TableModelListener {
    
    
    private JTable table;
    
    private final JButton buttonBanish;
    private final JButton buttonDone;
    private final JButton buttonAssess;
    private final JButton buttonCommit;
    
             
    public ActionHistory() {
        // Table    
        table = new JTable(new ActionHistoryModel());
        table.setPreferredScrollableViewportSize(new Dimension(500, 120));
        table.setFillsViewportHeight(true);
        
        initColumnSizes(); 
        
        // Table: listener
        table.getModel().addTableModelListener(this);
   
    
        // Layout    
        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new JPanel();
        this.add(topPanel, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);
        
        // Layout : add table in the top panel
        //topPanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(table);
        //add(scrollPane);
        topPanel.add(scrollPane, BorderLayout.CENTER);
        
        //bottomPanel.setLayout(new GridLayout(0, 3));        

        buttonBanish = new JButton("Banish");
        buttonDone = new JButton("Done");
        buttonAssess = new JButton("Assess");
        buttonCommit = new JButton("Commit");

        bottomPanel.add(buttonBanish);
        bottomPanel.add(buttonDone);
        bottomPanel.add(buttonAssess);
        bottomPanel.add(buttonCommit);
    }

    
    /*
     * This method picks good column sizes. 
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
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
    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        
        ActionHistoryModel model = (ActionHistoryModel) e.getSource();
        String columnName = model.getColumnName(column);
        Object data = model.getValueAt(row, column);
        
        // TODO:    
    }
    
}


    /*
     * Buttons
     */
       