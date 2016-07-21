package uk.ac.isc.processcommand;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import uk.ac.isc.seisdata.Command;
import uk.ac.isc.seisdata.CommandList;
import uk.ac.isc.seisdatainterface.Global;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdatainterface.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.VBASLogger;

public class CommandTable extends JPanel implements SeisDataChangeListener {

    private JTable table = null;
    private JScrollPane scrollPane = null;
    private CommandTableModel model = null;
    private final AssessCommandPanel assessCommandPanel;

    private final CommandList commandList = Global.getCommandList();
    private final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final Command commandEvent = Global.getCommandEvent();


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

        VBASLogger.logDebug(" #Commands:" + commandList.getCommandList().size());
        model = new CommandTableModel(commandList.getCommandList());
        table.setModel(model);

        setupTableVisualAttributes();

        selectedSeisEvent.addChangeListener(this);
        commandEvent.addChangeListener(this);

        // Layout all together
        // Table and action buttons
        scrollPane = new JScrollPane(table);
        assessCommandPanel = new AssessCommandPanel(table);
        this.setLayout(new BorderLayout());
        this.add(assessCommandPanel, BorderLayout.PAGE_START);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {

        String eventName = event.getData().getClass().getName();
        VBASLogger.logDebug("Event received from " + eventName);
        switch (eventName) {
            case "uk.ac.isc.seisdata.SeisEvent":
                break;

            case "uk.ac.isc.seisdata.Command":
                SeisDataDAO.readCommandTable(selectedSeisEvent.getEvid(),
                        commandList.getCommandList());
                break;
        }

        VBASLogger.logDebug(" #Commands:" + commandList.getCommandList().size());

        model = new CommandTableModel(commandList.getCommandList());
        table.setModel(model);
        setupTableVisualAttributes();

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
        /* table.setSelectionBackground(new Color(45, 137, 239));
         table.setSelectionForeground(Color.WHITE);*/

        table.setRowHeight(25);
        table.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        table.setShowGrid(false);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        // This part of the code picks good column sizes. 
        // If all column heads are wider than the column's cells'
        // contents, then you can just use column.sizeWidthToFit().
        // SiesEventsTableModel model = (SiesEventsTableModel) table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;

        Object[] longValues = model.longValues;
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < model.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = table.getDefaultRenderer(model.getColumnClass(i))
                    .getTableCellRendererComponent(table, longValues[i], false, false, 0, i);

            cellWidth = comp.getPreferredSize().width;
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }

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
                 * Check if multiple 'seiseventrelocate' or 'setprime' commands are selected for GenerateImages.
                 */
                /*
                 //HashSet set = new HashSet();
                 Boolean similar = false;
                 for (int r : table.getSelectedRows()) {
                 String commandType = (String) table.getValueAt(r, 4);
                 VBASLogger.logDebug("Row=" + r + "; Selected commndType=" + commandType);
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
   
}
