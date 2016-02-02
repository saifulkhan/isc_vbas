package uk.ac.isc.eventscontrolview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import uk.ac.isc.seisdata.Command;
import uk.ac.isc.seisdata.CommandList;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;

public class CommandTable extends JPanel implements SeisDataChangeListener {

    private JTable table = null;
    private JScrollPane scrollPane = null;
    private CommandTableModel model = null;
    private final CommandPanel commandPanel;
    
    private final CommandList commandList = Global.getCommandList();
    private static SeisEvent seisEvent = Global.getSelectedSeisEvent();    // used to fetch event from the EventTable, EventControlView
    private final Command formulatedCommand = Global.getFormulatedCommand();
    
    public CommandTable() {

        table = new JTable();
        model = new CommandTableModel(commandList.getCommandList());
        table.setModel(model);
        scrollPane = new JScrollPane(table);

        setupTableVisualAttributes();

        seisEvent.addChangeListener(this);
        formulatedCommand.addChangeListener(this);
        
        SeisDataDAO.readCommands(seisEvent.getEvid(), commandList.getCommandList());
        
        
        // Action buttons
        // layout all together
        commandPanel = new CommandPanel(table);
        this.setLayout(new BorderLayout());
        this.add(commandPanel, BorderLayout.PAGE_START);
        this.add(scrollPane, BorderLayout.CENTER);
    }
    

    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
        System.out.println(Global.debugAt() + " Event received from " + event.getData().getClass().getName());
        
        SeisDataDAO.readCommands(seisEvent.getEvid(), commandList.getCommandList());
        
        model = new CommandTableModel(commandList.getCommandList());
        table.setModel(model);
        
        table.clearSelection();
        scrollPane.setViewportView(table);
        scrollPane.repaint();
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
        //commandTable.setRowSelectionInterval(0, 0);

        table.setRowSelectionAllowed(false);

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
 
        table.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
    
    }
   
}
