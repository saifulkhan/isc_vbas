package uk.ac.isc.processcommand;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import org.openide.util.Exceptions;
import uk.ac.isc.seisdata.AssessedCommand;
import uk.ac.isc.seisdata.Command;
import uk.ac.isc.seisdata.CommandList;
import uk.ac.isc.seisdata.FormulateCommand;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisDataDAO;
import uk.ac.isc.seisdata.SeisDataDAOAssess;
import uk.ac.isc.seisdata.SeisEvent;

public class CommandTable extends JPanel implements SeisDataChangeListener {

    private JTable table = null;
    private JScrollPane scrollPane = null;
    private CommandTableModel model = null;
    private final CommandPanel commandPanel;

    private final CommandList commandList = Global.getCommandList();
    private final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final Command commandEvent = Global.getCommandEvent();
    private final AssessedCommand assessedCommandEvent = Global.getAssessedComamndEvent(); // send event to AssessedCommand table

    private final Assess assess = new Assess();

    public CommandTable() {

        table = new JTable() {
            // Implement table cell tool tips.
            public String getToolTipText(MouseEvent e) {
                String tip = "";
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                int realColumnIndex = convertColumnIndexToModel(colIndex);

                if (realColumnIndex == 2) { //Command column
                    tip += getValueAt(rowIndex, colIndex);
                } else {
                    // You can omit this part if you know you don't have any renderers
                    // that supply their own tool tips.
                    tip = super.getToolTipText(e);
                }
                return tip;
            }
        };

        MyRowSelectionListener rowListener = new MyRowSelectionListener();
        table.getSelectionModel().addListSelectionListener(rowListener);

        Global.logDebug(" #Commands:" + commandList.getCommandList().size());
        model = new CommandTableModel(commandList.getCommandList());
        table.setModel(model);

        setupTableVisualAttributes();

        selectedSeisEvent.addChangeListener(this);
        commandEvent.addChangeListener(this);

        // Layout all together
        // Table and action buttons
        scrollPane = new JScrollPane(table);
        commandPanel = new CommandPanel(table);
        this.setLayout(new BorderLayout());
        this.add(commandPanel, BorderLayout.PAGE_START);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {

        String eventName = event.getData().getClass().getName();
        Global.logDebug("Event received from " + eventName);
        switch (eventName) {
            case "uk.ac.isc.seisdata.SeisEvent":
                break;

            case "uk.ac.isc.seisdata.Command":
                SeisDataDAO.readCommandTable(selectedSeisEvent.getEvid(),
                        commandList.getCommandList());
                break;
        }

        Global.logDebug(" #Commands:" + commandList.getCommandList().size());

        model = new CommandTableModel(commandList.getCommandList());
        table.setModel(model);

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
     * Selection related
     *****************************************************************************************
     */
    private class MyRowSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent event) {
            // disable the double calls
            if (!event.getValueIsAdjusting()) {

                /*
                 * Check if multiple 'seiseventrelocate' or 'setprime' commands are selected for Assess.
                 */
                /*
                 //HashSet set = new HashSet();
                 Boolean similar = false;
                 for (int r : table.getSelectedRows()) {
                 String commandType = (String) table.getValueAt(r, 4);
                 Global.logDebug("Row=" + r + "; Selected commndType=" + commandType);
                 if (commandType.equals("seiseventrelocate") || commandType.equals("setprime")) {
                 //if (set.add(commandType) == false) {
                 if (similar == false) {
                 similar = true;
                 } else if (similar == true) {
                 JOptionPane.showMessageDialog(null,
                 "Selected multiple 'SiesEvent Relocate' commands.", "Warning",
                 JOptionPane.WARNING_MESSAGE);
                 }
                 table.getSelectionModel().clearSelection();
                 table.getColumnModel().getSelectionModel().clearSelection();
                 }
                 }*/
            }

        }
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
            /*button_assess.setBackground(new Color(45, 137, 239));
             button_assess.setForeground(new Color(255, 255, 255));*/
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

            button_assess.setEnabled(false);

            /*
             *  Stage-1 Write the commands in the database.
             */
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length <= 0) {
                JOptionPane.showMessageDialog(null, "Select a command.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ArrayList<Integer> commandIds = new ArrayList<Integer>();
            String commandType = "assess";
            FormulateCommand formulateCommand = new FormulateCommand(commandType, "seisevent", Global.getSelectedSeisEvent().getEvid());

            for (int row : selectedRows) {
                commandIds.add((Integer) table.getValueAt(row, 0));

                String systemCommandStr = commandList.getCommandList().get(row).getSystemCommandStr();

                // add/append the command to the formulated asses command
                Global.logDebug("Append the cmd: " + systemCommandStr);
                formulateCommand.addSystemCommand(systemCommandStr);

            }

            Path eventLogDir = Paths.get(SeisDataDAOAssess.getAssessDir().toString()
                    + File.separator + Global.getSelectedSeisEvent().getEvid());

            //formulateCommand.addAttribute("commands", commandIds.toString(), null);
            //formulateCommand.addAttribute("report", htmlReport, null);
            /*
             * Now writw the assessed details 
             */
            int newAssessId = 0;
            if (formulateCommand.isValidCommand()) {

                Global.logDebug("commandProvenance= " + formulateCommand.getCmdProvenance().toString());
                Global.logDebug("systemCommand= " + formulateCommand.getSystemCommand().toString());

                newAssessId = SeisDataDAO.updateAssessedCommandTable(Global.getSelectedSeisEvent().getEvid(), commandType, commandIds, eventLogDir,
                        ""); // TODO write the nsystemCommand

                if (newAssessId > 0) {
                    Global.logDebug(" Fired: " + commandType);
                    assessedCommandEvent.fireSeisDataChanged();
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                return;
            }

            /*
             * ***************************************************************************
             * Assess: run relocator, generate html etc.. If susccess write the
             * assess info in the AssessedCommand table
             * ****************************************************************************
             */
            Path assessDir = Paths.get(eventLogDir + File.separator + newAssessId);
            File htmlReport = new File(assessDir + File.separator + newAssessId + ".html");

            assess.runLocator(assessDir, formulateCommand.getSQLFunctionArray(), formulateCommand.getLocatorArgStr());
            assess.generateReport(htmlReport);

            try {
                Desktop.getDesktop().browse(htmlReport.toURI());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

            JOptionPane.showMessageDialog(null, "Assess Complete. Please see the report in your browser.", 
                    "Complete", JOptionPane.NO_OPTION);
            button_assess.setEnabled(true);

        }
    }
}
