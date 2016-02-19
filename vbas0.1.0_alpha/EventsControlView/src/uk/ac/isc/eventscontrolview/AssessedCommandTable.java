package uk.ac.isc.eventscontrolview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import uk.ac.isc.seisdata.AssessedCommand;
import uk.ac.isc.seisdata.AssessedCommandList;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;

public class AssessedCommandTable extends JPanel implements SeisDataChangeListener {

    private JTable table = null;
    private JScrollPane scrollPane = null;
    private AssessedCommandTableModel model;

    private final AssessedCommandPanel assessedCommandPanel;

    private final AssessedCommandList assessedCommandList = Global.getAssessedCommandList();
    private final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();    // used to fetch event from the EventTable, EventControlView
    private final AssessedCommand assessedCommandEvent = Global.getAssessedComamndEvent();

    public AssessedCommandTable() {

        table = new JTable();
        scrollPane = new JScrollPane(table);
        model = new AssessedCommandTableModel(assessedCommandList.getAssessedCommandList());
        table.setModel(model);

        setupTableVisualAttributes();

        selectedSeisEvent.addChangeListener(this);
        assessedCommandEvent.addChangeListener(this);

        // Action buttons
        // layout all together
        assessedCommandPanel = new AssessedCommandPanel(table);
        this.setLayout(new BorderLayout());
        this.add(assessedCommandPanel, BorderLayout.PAGE_START);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
        String eventName = event.getData().getClass().getName();
        Global.logDebug("Event received from " + eventName);
        switch (eventName) {
            case "uk.ac.isc.seisdata.SeisEvent":
                break;

            case "uk.ac.isc.seisdata.AssessedCommand":
                SeisDataDAO.readAssessedCommandTable(selectedSeisEvent.getEvid(),
                        assessedCommandList.getAssessedCommandList());
                Global.logDebug(" #AssessedCommands:" + assessedCommandList.getAssessedCommandList().size());
                break;
        }

        model = new AssessedCommandTableModel(assessedCommandList.getAssessedCommandList());
        table.setModel(model);

        table.clearSelection();
        scrollPane.setViewportView(table);
        scrollPane.repaint();
    }

    private void setupTableVisualAttributes() {

        TableCellRenderer buttonRenderer = new JTableButtonRenderer();
        table.getColumn("Report").setCellRenderer(buttonRenderer);
        table.addMouseListener(new JTableButtonMouseListener(table));

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

        table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

    }

    /*
     *****************************************************************************************
     * XX
     *****************************************************************************************
     */
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
            Image img = icon.getImage();
            Image newimg = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(newimg);

            button.setIcon(icon);
            return button;
        }

    }


    /*
     *****************************************************************************************
     * xx
     *****************************************************************************************
     */
    class JTableButtonMouseListener extends MouseAdapter {

        private final JTable table;

        public JTableButtonMouseListener(JTable phasesTable) {
            this.table = phasesTable;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int column = table.getColumnModel().getColumnIndexAtX(e.getX());
            int row = e.getY() / table.getRowHeight();

            if (column == 0) {
                Object value = table.getValueAt(row, column);
                if (value instanceof JButton) {
                    /**
                     * Here is the code for popup a dialog to edit the phase
                     * reading
                     */
                    //((JButton)value).doClick(); 
                    System.out.println("JTableButtonMouseListener: Mouse Clicked." + this.table.getValueAt(row, 2));
                }
            }
        }

    }


    /*
     *****************************************************************************************
     * A panel to 'commit' the assessed command that is selected.
     *****************************************************************************************
     */
    public class AssessedCommandPanel extends JPanel {

        private final JLabel label_total;
        private final JButton button_commit;
        private final JTable table;             // reference of the table

        public AssessedCommandPanel(final JTable commandTable) {
            this.table = commandTable;

            Font font = new Font("Sans-serif", Font.PLAIN, 14);

            button_commit = new JButton("Commit");
            button_commit.setBackground(new Color(45, 137, 239));
            button_commit.setForeground(new Color(255, 255, 255));
            button_commit.setFont(font);

            label_total = new JLabel("");
            label_total.setFont(font);

            button_commit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    onButtonCommitActionPerformed(e);
                }
            });

            this.setLayout(new FlowLayout());

            this.add(button_commit);
            this.add(label_total);
        }

        public void onButtonCommitActionPerformed(ActionEvent e) {

        }
    }

}
