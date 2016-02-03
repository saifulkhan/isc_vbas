package uk.ac.isc.textview;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import uk.ac.isc.seisdata.Command;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;

class HypoTablePopupManager implements ActionListener {

    JTable table;
    JPopupMenu popupMenu;

    RelocateEventDialog relocateEventDialog;
    EditHypocentreDialog editHypocentreDialog;
 
    private final Command formulatedCommand = Global.getFormulatedCommand();
    private final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final Hypocentre selectedHypocentre = Global.getSelectedHypocentre();
    
    public HypoTablePopupManager(JTable hypoTable) {
        table = hypoTable;

        setPopupMenuVisualAttributes();

        relocateEventDialog = new RelocateEventDialog();
        editHypocentreDialog = new EditHypocentreDialog();
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    /*
     * Menu item selected.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(Global.debugAt());

        // Selected row values
        int selectedRow = table.getSelectedRow();
        int selectedColumn = table.getSelectedColumn();

        if ("Set Prime".equals(e.getActionCommand())) {
            
            String command = "<hypid> " + selectedHypocentre.getHypid() + " </hypid>";
            
            boolean retDAO = SeisDataDAO.updateCommandTable(selectedSeisEvent.getEvid(), "rf", command);
            if (retDAO) {
                // success
                System.out.println(Global.debugAt() + " \nCommand=" + command + " \nFired: New Command from the 'Relocate Event' dialog.");           // Notify the Command table to update from the database.
                formulatedCommand.fireSeisDataChanged();  
                
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if ("Relocate..".equals(e.getActionCommand())) {
            relocateEventDialog.setLocationRelativeTo(table);
            relocateEventDialog.showHypoTableRelocateDialog();
        }

        if ("Depricate".equals(e.getActionCommand())) {
            JOptionPane.showMessageDialog(null, "Selected Item: " + e.getActionCommand() + "\nTo be added in future version.");
        }

        if ("Edit..".equals(e.getActionCommand())) {
            editHypocentreDialog.setLocationRelativeTo(table);
            editHypocentreDialog.showHypoEditDialog();
        }

        if ("Create..".equals(e.getActionCommand())) {
            JOptionPane.showMessageDialog(null, "Selected Item: " + e.getActionCommand() + "\nTo be added in future version.");
        }

        if ("Move..".equals(e.getActionCommand())) {
            JOptionPane.showMessageDialog(null, "Selected Item: " + e.getActionCommand()  + "\nTo be added in future version.");
        }

    }

    private void setPopupMenuVisualAttributes() {
        popupMenu = new JPopupMenu();

        JMenuItem menuItem_setprime = new JMenuItem("Set Prime");
        menuItem_setprime.setBackground(new Color(218, 83, 44));
        menuItem_setprime.setForeground(Color.WHITE);
        menuItem_setprime.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_relocate = new JMenuItem("Relocate..");
        menuItem_relocate.setBackground(new Color(218, 83, 44));
        menuItem_relocate.setForeground(Color.WHITE);
        menuItem_relocate.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_depricate = new JMenuItem("Depricate");
        menuItem_depricate.setBackground(new Color(218, 83, 44));
        menuItem_depricate.setForeground(Color.WHITE);
        menuItem_depricate.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_edit = new JMenuItem("Edit..");
        menuItem_edit.setBackground(new Color(218, 83, 44));
        menuItem_edit.setForeground(Color.WHITE);
        menuItem_edit.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_create = new JMenuItem("Create..");
        menuItem_create.setBackground(new Color(218, 83, 44));
        menuItem_create.setForeground(Color.WHITE);
        menuItem_create.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_move = new JMenuItem("Move..");
        menuItem_move.setBackground(new Color(218, 83, 44));
        menuItem_move.setForeground(Color.WHITE);
        menuItem_move.setFont(new Font("Sans-serif", Font.PLAIN, 14));

        popupMenu.add(menuItem_setprime);
        //popupMenu.addSeparator();
        popupMenu.add(menuItem_relocate);
        //popupMenu.addSeparator();
        popupMenu.add(menuItem_depricate);
        //popupMenu.addSeparator();
        popupMenu.add(menuItem_edit);
        //popupMenu.addSeparator();
        popupMenu.add(menuItem_create);
        //popupMenu.addSeparator();
        popupMenu.add(menuItem_move);

        menuItem_setprime.addActionListener(this);
        menuItem_relocate.addActionListener(this);
        menuItem_depricate.addActionListener(this);
        menuItem_edit.addActionListener(this);
        menuItem_create.addActionListener(this);
        menuItem_move.addActionListener(this);

    }

}
