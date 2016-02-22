package uk.ac.isc.command;

import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;
import com.orsoncharts.util.json.parser.JSONParser;
import com.orsoncharts.util.json.parser.ParseException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
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
import uk.ac.isc.seisdata.AssessedCommand;
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
    private final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final Command commandEvent = Global.getCommandEvent();
    private final AssessedCommand assessedCommandEvent = Global.getAssessedComamndEvent();

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
     * Selection related
     *****************************************************************************************
     */
    private class MyRowSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent event) {
            // disable the double calls
            if (!event.getValueIsAdjusting()) {

                /*
                 * Check if multiple SeiesEvent Relocate commands are selected for Assess.
                 */
                HashSet set = new HashSet();
                for (int r : table.getSelectedRows()) {
                    String commandType = (String) table.getValueAt(r, 4);
                    Global.logDebug("Row=" + r + ", commndType=" + commandType);
                    if (commandType.equals("seiseventrelocate")) {
                        if (set.add(commandType) == false) {
                            JOptionPane.showMessageDialog(null,
                                    "Selected multiple 'SiesEvent Relocate' commands.", "Warning",
                                    JOptionPane.WARNING_MESSAGE);
                            table.getSelectionModel().clearSelection();
                            table.getColumnModel().getSelectionModel().clearSelection();
                        }
                    }
                }
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

            /*
             *  Stage-1 Write the commands in the database.
             */
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length <= 0) {
                JOptionPane.showMessageDialog(null, "Select a command.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ArrayList<Integer> commandIds = new ArrayList<Integer>();

            JSONArray jFunctionArray = new JSONArray();

            for (int row : selectedRows) {
                commandIds.add((Integer) table.getValueAt(row, 0));

                // now concatinate
                JSONParser parser = new JSONParser();
                try {
                    String s = commandList.getCommandList().get(row).getFunctionsStr();
                    Object obj = parser.parse(s);
                    JSONArray arr = (JSONArray) obj;
                    jFunctionArray.addAll(arr);

                } catch (ParseException pe) {
                    Global.logSevere("\nPosition:" + pe.getPosition() + ", " + pe
                            + "\nError Parsing: " + jFunctionArray.toString());
                    JOptionPane.showMessageDialog(null, "Error in JSON parsing. Failed to build the jFunctionArray",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Global.logDebug("Appended functionStr: " + jFunctionArray.toString());
            }

            /*
             * ***************************************************************************
             * Assess: run relocator, generate html etc.. If susccess write the
             * assess info in the AssessedCommand table
             * ****************************************************************************
             */
            Assess assess = new Assess(jFunctionArray);
            Boolean success = assess.runLocator();
            if (success == false) {
                Global.logSevere("Assess failed.");
                return;
            }
            success = assess.generateHTMLReport();
            if (success == false) {
                Global.logSevere("HTML report generation failed.");
                return;
            }

            
            /*
             * Now writw the assessed details 
             */
            JSONObject jCommandObj = new JSONObject();
            jCommandObj.put("commandType", "assess");
            jCommandObj.put("dataType", "seisevent");
            jCommandObj.put("id", selectedSeisEvent.getEvid());
            jCommandObj.put("reportName", assess.getReportName());
            jCommandObj.put("commandIDs", commandIds.toString());

            String commandStr = jCommandObj.toJSONString();
            String functionStr = jFunctionArray.toString();

            Global.logDebug(
                    "commandStr= " + commandStr
                    + "\nfunctionStr= " + functionStr);

            boolean ret = SeisDataDAO.updateAssessedCommandTable(selectedSeisEvent.getEvid(),
                    "assess", commandStr, functionStr, commandIds);
            if (ret) {
                Global.logDebug(" Fired: 'Assess' comamnd.");
                assessedCommandEvent.fireSeisDataChanged();
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect 'Assess' command.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }

        }
    }
}
