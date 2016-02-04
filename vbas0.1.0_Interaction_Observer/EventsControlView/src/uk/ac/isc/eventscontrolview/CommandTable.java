package uk.ac.isc.eventscontrolview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
    private static SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final Command formulatedCommand = Global.getFormulatedCommand();

    public CommandTable() {

        table = new JTable();
        model = new CommandTableModel(commandList.getCommandList());
        table.setModel(model);
        scrollPane = new JScrollPane(table);

        setupTableVisualAttributes();

        selectedSeisEvent.addChangeListener(this);
        formulatedCommand.addChangeListener(this);

        SeisDataDAO.readCommands(selectedSeisEvent.getEvid(), commandList.getCommandList());

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

        
        SeisDataDAO.readCommands(selectedSeisEvent.getEvid(), commandList.getCommandList());

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
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        //table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setSelectionBackground(new Color(45, 137, 239));
        table.setSelectionForeground(Color.WHITE);

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

    /*
     *****************************************************************************************
     * A panel to send the selected commands to assessed-comamnd-table .
     *****************************************************************************************
     */
    public class CommandPanel extends JPanel {

        private final JLabel label_total;
        private final JButton button_assess;
        private final JTable table;             // reference of the table

        public CommandPanel(final JTable commandTable) {
            this.table = commandTable;

            Font font = new Font("Sans-serif", Font.PLAIN, 14);

            button_assess = new JButton("Assess");
            button_assess.setBackground(new Color(45, 137, 239));
            button_assess.setForeground(new Color(255, 255, 255));
            button_assess.setFont(font);

            label_total = new JLabel("");
            label_total.setFont(font);

            button_assess.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onButtonAssessActionPerformed(e);
                }
            });

            this.setLayout(new FlowLayout());
            this.add(button_assess);
            this.add(label_total);
        }

        public void onButtonAssessActionPerformed(ActionEvent e) {

            String commandStr = "<pdf> /some/path/to/pdf/ </pdf>";
            int [] selectedRows = table.getSelectedRows();
            
            for (int i : selectedRows) {
                int commandId = (Integer) table.getValueAt(i, 0);
                commandStr =  commandStr  + " <id> " + commandId + " </id>";
            }

            System.out.print("command = " + commandStr + "\n\n");

            if (selectedRows.length <= 0) {
                JOptionPane.showMessageDialog(null, "Select command(s) to assess.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                boolean ret = SeisDataDAO.updateCommandTable(selectedSeisEvent.getEvid(), "assess", commandStr);
                if (ret) {
                    // Success
                    System.out.println(Global.debugAt() + " \nFired: New Command from the 'CommandTable'");
                    formulatedCommand.fireSeisDataChanged();
                } else {
                    JOptionPane.showMessageDialog(null, "Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        }
    }
}
