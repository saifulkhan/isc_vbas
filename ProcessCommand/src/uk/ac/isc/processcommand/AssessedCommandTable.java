package uk.ac.isc.processcommand;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.ImageIcon;
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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.openide.util.Exceptions;
import uk.ac.isc.seisdata.AssessedCommand;
import uk.ac.isc.seisdata.AssessedCommandList;
import uk.ac.isc.seisdatainterface.Global;
import uk.ac.isc.seisdata.VBASLogger;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdatainterface.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;


/*
 * ****************************************************************************************************************
 * Displays the assessed commands.
 * A details on how to add button in a cell (column) can be found in:
 *  http://stackoverflow.com/questions/13833688/adding-jbutton-to-jtable
 ******************************************************************************************************************
 */
public class AssessedCommandTable extends JPanel implements SeisDataChangeListener {

    private JTable table = null;
    private JScrollPane scrollPane = null;
    private AssessedCommandTableModel model;

    //private final AssessedCommandPanel assessedCommandPanel;

    private final AssessedCommandList assessedCommandList = Global.getAssessedCommandList();
    private final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();    // used to fetch event from the EventTable, EventControlView
    private final AssessedCommand assessedCommandEvent = Global.getAssessedComamndEvent();

    public AssessedCommandTable() {

        table = new JTable();
        table.addMouseListener(new JTableButtonMouseListener(table));

        VBASLogger.logDebug(" #AssessedCommands:" + assessedCommandList.getAssessedCommandList().size());
        model = new AssessedCommandTableModel(assessedCommandList.getAssessedCommandList());
        table.setModel(model);

        setupTableVisualAttributes();

        selectedSeisEvent.addChangeListener(this);
        assessedCommandEvent.addChangeListener(this);

        // Action buttons
        // layout all together
        scrollPane = new JScrollPane(table);
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
        String eventName = event.getData().getClass().getName();
        VBASLogger.logDebug("Event received from " + eventName);
        switch (eventName) {
            case "uk.ac.isc.seisdata.SeisEvent":
                break;

            case "uk.ac.isc.seisdata.AssessedCommand":
                SeisDataDAO.readAssessedCommandTable(selectedSeisEvent.getEvid(),
                        assessedCommandList.getAssessedCommandList());
                break;
        }

        VBASLogger.logDebug(" #AssessedCommands:" + assessedCommandList.getAssessedCommandList().size());

        model = new AssessedCommandTableModel(assessedCommandList.getAssessedCommandList());
        table.setModel(model);

        table.clearSelection();
        setupTableVisualAttributes(); // Note: otherwise the buttons fails to render.

        scrollPane.setViewportView(table);
        scrollPane.repaint();
    }

    private void setupTableVisualAttributes() {

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        /*th.setBackground(new Color(43, 87, 151));            // Blue
         th.setForeground(Color.white);*/

        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);
        /*table.setSelectionBackground(new Color(45, 137, 239));
         table.setSelectionForeground(Color.WHITE);*/
        //commandTable.setRowSelectionInterval(0, 0);

        table.setRowSelectionAllowed(false);

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

        TableCellRenderer buttonRenderer = new JTableButtonRenderer();
        table.getColumn("Report").setCellRenderer(buttonRenderer);
        
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        // NOTE: do not mess with the button.
        // table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); 

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

        for (int i = 0; i < model.getColumnCount() - 1 /*Note: exclude the last col (button)*/; i++) {
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
     * The cells in the "Report" column are clickable button.
     *****************************************************************************************
     */
    class JTableButtonRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JButton button = (JButton) value;

            // load resource images
            URL url = getClass().getClassLoader().getResource("resources/html.png");
            ImageIcon icon = new ImageIcon(url);

            button.setIcon(icon);
            return button;
        }
    }


    /*
     *****************************************************************************************
     * When the button is clicked, the html report opens.
     *****************************************************************************************
     */
    private static class JTableButtonMouseListener extends MouseAdapter {

        private final JTable table;

        public JTableButtonMouseListener(JTable table) {
            this.table = table;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int column = table.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
            int row = e.getY() / table.getRowHeight(); //get the row of the button

            /*Checking the row or column is valid or not*/
            if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
                Object value = table.getValueAt(row, column);
                if (value instanceof JButton) {

                    String url = Global.getAssessedCommandList().getAssessedCommandList().get(row).getReport();
                    try {
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (URISyntaxException ex) {
                        Exceptions.printStackTrace(ex);
                        JOptionPane.showMessageDialog(null, "Unable to open: " + url, 
                                "Warning", JOptionPane.WARNING_MESSAGE);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                        JOptionPane.showMessageDialog(null, "Unable to open: " + url, 
                                "Warning", JOptionPane.WARNING_MESSAGE);
                    }

                    VBASLogger.logDebug("Clicked.." + "row= " + row + ", column= " + column + ", report: " + url);
                }
            }
        }
    }

}
