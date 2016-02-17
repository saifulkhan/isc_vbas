package uk.ac.isc.eventscontrolview;

import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
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
import uk.ac.isc.seisdata.Command;
import uk.ac.isc.seisdata.CommandList;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.Settings;

public class CommandTable extends JPanel implements SeisDataChangeListener {

    private JTable table = null;
    private JScrollPane scrollPane = null;
    private CommandTableModel model = null;
    private final CommandPanel commandPanel;

    private final CommandList commandList = Global.getCommandList();
    private final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final Command formulatedCommand = Global.getFormulatedCommand();

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

        model = new CommandTableModel(commandList.getCommandList());
        table.setModel(model);

        setupTableVisualAttributes();

        selectedSeisEvent.addChangeListener(this);
        formulatedCommand.addChangeListener(this);

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
        System.out.println(Global.debugAt() + " Event received from " + event.getData().getClass().getName());

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
                    System.out.println(Global.debugAt() + "Row=" + r + ", commndType=" + commandType);
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
            ArrayList<Integer> commandIds = new ArrayList<Integer>();

            JSONArray jCommandArray = new JSONArray();
            JSONArray jFunctionArray = new JSONArray();

            JSONObject jCommandObj = new JSONObject();
            jCommandObj.put("commandType", "assess");
            jCommandObj.put("dataType", "seisevent");
            jCommandObj.put("id", selectedSeisEvent.getEvid());
            jCommandObj.put("report", Settings.getAssessDir() + File.separator
                    + selectedSeisEvent.getEvid() + File.separator + selectedSeisEvent.getEvid() + ".html");
            jCommandArray.add(jCommandObj);

            JSONArray jAttrArray = new JSONArray();
            for (int row : selectedRows) {
                JSONObject jAttrObj = new JSONObject();
                jAttrObj.put("name", "command");
                jAttrObj.put("id", (Integer) table.getValueAt(row, 0));
                jAttrArray.add(jAttrObj);

                JSONObject jFunctionObj = new JSONObject();
                jFunctionObj.put("function", null);
                jFunctionArray.add(jFunctionObj);
            }
            
            jCommandObj.put("attributes", jAttrArray);
            jCommandArray.add(jCommandObj);

            if (jCommandArray.size() > 0) {
                String commandStr = jCommandArray.toString();
                String functionStr = jFunctionArray.toString();

                boolean ret = SeisDataDAO.updateCommandTableForAssess(selectedSeisEvent.getEvid(),
                        "assess", commandStr, functionStr, commandIds);
                if (ret) {
                    Global.logDebug(" Fired: 'Assess' comamnd."
                            + "\ncommandStr= " + commandStr
                            + "\nfunctionStr= " + functionStr);

                    formulatedCommand.fireSeisDataChanged();
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            /*
             *  Stage-2 process assess data
             */
            for (int row : selectedRows) {
                    
            }

            boolean ret = SeisDataDAO.processAssessData(selectedSeisEvent.getEvid(), commandIds);
            if (ret == false) {
                JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }
}
