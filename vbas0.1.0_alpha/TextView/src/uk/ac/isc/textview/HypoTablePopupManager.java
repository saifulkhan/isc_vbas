package uk.ac.isc.textview;

import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;
import java.awt.Color;
import java.awt.Font;
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

    EventRelocateDialog relocateEventDialog;
    HypoEditDialog editHypocentreDialog;

    private final Command formulatedCommand = Global.getFormulatedCommand();
    private final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final Hypocentre selectedHypocentre = Global.getSelectedHypocentre();

    public HypoTablePopupManager(JTable hypoTable) {
        table = hypoTable;

        setPopupMenuVisualAttributes();

        relocateEventDialog = new EventRelocateDialog();
        editHypocentreDialog = new HypoEditDialog();
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

            JSONArray jCommandArray = new JSONArray();
            JSONObject jCommandObj = new JSONObject();
            jCommandObj.put("commandType", "setprime");
            jCommandObj.put("dataType", "hypocentre");
            jCommandObj.put("id", selectedHypocentre.getHypid());

            jCommandArray.add(jCommandObj);

            if (jCommandArray.size() > 0) {
                String command = jCommandArray.toString();
                System.out.print("formulated json commad:" + command);

                boolean ret = SeisDataDAO.updateCommandTable(selectedSeisEvent.getEvid(), "setprime", command);
                if (ret) {
                    // success
                    System.out.println(Global.debugAt() + " \nCommand=" + command + " \nFired: New Command 'Set Prime'.");
                    formulatedCommand.fireSeisDataChanged();  // Notify the Command table to update from the database.

                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        }

        if ("Event Relocate..".equals(e.getActionCommand())) {
            relocateEventDialog.setLocationRelativeTo(table);
            relocateEventDialog.showHypoTableRelocateDialog();
        }

        if ("Depricate".equals(e.getActionCommand())) {
            JOptionPane.showMessageDialog(null, "Selected Item: " + e.getActionCommand() + "\nTo be added in future version.");
        }

        if ("Hypocentre Edit..".equals(e.getActionCommand())) {
            editHypocentreDialog.setLocationRelativeTo(table);
            editHypocentreDialog.showHypoEditDialog();
        }

        if ("Create..".equals(e.getActionCommand())) {
            JOptionPane.showMessageDialog(null, "Selected Item: " + e.getActionCommand() + "\nTo be added in future version.");
        }

        if ("Move..".equals(e.getActionCommand())) {
            JOptionPane.showMessageDialog(null, "Selected Item: " + e.getActionCommand() + "\nTo be added in future version.");
        }

    }

    private void setPopupMenuVisualAttributes() {
        popupMenu = new JPopupMenu();

        JMenuItem menuItem_setprime = new JMenuItem("Set Prime");
        menuItem_setprime.setBackground(new Color(218, 83, 44));
        menuItem_setprime.setForeground(Color.WHITE);
        menuItem_setprime.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_relocate = new JMenuItem("Event Relocate..");
        menuItem_relocate.setBackground(new Color(218, 83, 44));
        menuItem_relocate.setForeground(Color.WHITE);
        menuItem_relocate.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_depricate = new JMenuItem("Depricate");
        menuItem_depricate.setBackground(new Color(218, 83, 44));
        menuItem_depricate.setForeground(Color.WHITE);
        menuItem_depricate.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_edit = new JMenuItem("Hypocentre Edit..");
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
