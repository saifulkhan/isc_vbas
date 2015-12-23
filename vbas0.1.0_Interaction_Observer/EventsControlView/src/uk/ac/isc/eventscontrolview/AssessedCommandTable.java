package uk.ac.isc.eventscontrolview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import uk.ac.isc.seisdata.CommandList;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisEvent;

public class AssessedCommandTable extends JPanel implements SeisDataChangeListener {

    private JTable table = null;
    private AssessedCommandTableModel model;
  
    private final CommandList commandList; 
    // used to fetch event from the EventTable / EventControlView
    private static SeisEvent currentEvent;

    private ImageIcon errorIcon = (ImageIcon) UIManager.getIcon("OptionPane.errorIcon");
    private ImageIcon infoIcon = (ImageIcon) UIManager.getIcon("OptionPane.informationIcon");
    private ImageIcon warnIcon = (ImageIcon) UIManager.getIcon("OptionPane.warningIcon");
    private ImageIcon questIcon = (ImageIcon) UIManager.getIcon("OptionPane.questionIcon");

   
    public AssessedCommandTable() {
 
        model = new AssessedCommandTableModel();
        table = new JTable();
        
        table.setModel(model);
        
        setupTableVisualAttributes();   // 2
                 
        System.out.println("DEBUG: " + Thread.currentThread().getStackTrace()[1].getLineNumber() + ", " + "ActionHistoryTable::ActionHistoryTable() : currentEvent = " + currentEvent);
        
        commandList = Global.getActionHistoryList();
        commandList.addChangeListener(this);
   }

    
    
    
    private void setupTableVisualAttributes() {

         TableCellRenderer buttonRenderer = new JTableButtonRenderer();
         table.getColumn("Report").setCellRenderer(buttonRenderer);
         table.addMouseListener(new JTableButtonMouseListener(table));


        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        th.setBackground(new Color(43,87,151));            // Blue
        th.setForeground(Color.white);
        
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);
        table.setSelectionBackground(new Color(45,137,239));
        table.setSelectionForeground(Color.WHITE);
        //commandTable.setRowSelectionInterval(0, 0);
                
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
       
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
                     
        
        // This part of the code picks good column sizes. 
        // If all column heads are wider than the column's cells'
        // contents, then you can just use column.sizeWidthToFit().
        /*        
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        
        
        Object[] longValues = model.longValues;
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < model.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(
                                 null, column.getHeaderValue(),
                                 false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = table.getDefaultRenderer(model.getColumnClass(i))
                    .getTableCellRendererComponent(table, 
                            longValues[i], false, false, 0, i);
            
            cellWidth = comp.getPreferredSize().width;

           column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }*/
        
    }
    
    public JTable getTable() {
        return this.table;
    }  


    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
       
        //currentEvent = ((EventsControlViewTopComponent) tc).getControlPanel().getSelectedSeisEvent();
        
       System.out.println("DEBUG: " + Thread.currentThread().getStackTrace()[1].getLineNumber() + ", " + "ActionHistoryTable::SeisDataChanged() : currentEvent = " + currentEvent); 
       //commandList = Global.getActionHistoryList();
       
    }

}



class JTableButtonRenderer implements TableCellRenderer {

    /*the three reference variables for the use of the actionlistener*/
    //private JTable lctable = null;
    //private int lcRow;
    //private int lcColumn;
    private JButton button;
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        button = (JButton) value;
        ImageIcon icon = new ImageIcon(getClass().getClassLoader()
                .getResource("uk/ac/isc/eventscontrolview/pdf-icon.png"));
        // Resize the image
        Image img = icon.getImage() ;  
        Image newimg = img.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH) ;  
        icon = new ImageIcon(newimg);
        
        button.setIcon(icon);
        
        //this.lctable = table;
        //this.lcRow = row;
        //this.lcColumn = column;
        
        return button;
    }
    
}



class JTableButtonMouseListener extends MouseAdapter {

    private final JTable table;
    
    public JTableButtonMouseListener(JTable phasesTable) {
        this.table = phasesTable;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int column = table.getColumnModel().getColumnIndexAtX(e.getX());
        int row    = e.getY()/table.getRowHeight(); 
        
        if(column == 0)
        {
            Object value = table.getValueAt(row,column);
            if (value instanceof JButton) {
                /**Here is the code for popup a dialog to edit the phase reading*/
                //((JButton)value).doClick(); 
                System.out.println("JTableButtonMouseListener: Mouse Clicked." + this.table.getValueAt(row, 2));
            }
        }
    }
    
}