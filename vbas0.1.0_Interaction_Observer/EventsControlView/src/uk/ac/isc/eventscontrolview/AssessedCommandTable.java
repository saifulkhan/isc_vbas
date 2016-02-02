package uk.ac.isc.eventscontrolview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
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
    private static SeisEvent seisEvent = Global.getSelectedSeisEvent();    // used to fetch event from the EventTable, EventControlView

    public AssessedCommandTable() {

        table = new JTable();
        model = new AssessedCommandTableModel(assessedCommandList.getAssessedCommandList());
        table.setModel(model);
        
        scrollPane = new JScrollPane(table);

        setupTableVisualAttributes();

        seisEvent.addChangeListener(this);
        SeisDataDAO.readAssessedCommands(seisEvent.getEvid(), assessedCommandList.getAssessedCommandList());
        
        
         // Action buttons
        // layout all together
        assessedCommandPanel = new AssessedCommandPanel(table);
        this.setLayout(new BorderLayout());
        this.add(assessedCommandPanel, BorderLayout.PAGE_START);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
        System.out.println(Global.debugAt() + " Event received from " + event.getData().getClass().getName());

        SeisDataDAO.readAssessedCommands(seisEvent.getEvid(), assessedCommandList.getAssessedCommandList());

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
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
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
        int row = e.getY() / table.getRowHeight();

        if (column == 0) {
            Object value = table.getValueAt(row, column);
            if (value instanceof JButton) {
                /**
                 * Here is the code for popup a dialog to edit the phase reading
                 */
                //((JButton)value).doClick(); 
                System.out.println("JTableButtonMouseListener: Mouse Clicked." + this.table.getValueAt(row, 2));
            }
        }
    }

}
