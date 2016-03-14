package uk.ac.isc.textview;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import uk.ac.isc.seisdata.Command;
import uk.ac.isc.seisdatainterface.FormulateCommand;
import uk.ac.isc.seisdatainterface.Global;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdatainterface.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.VBASLogger;

class HypocentreTablePopupMenu implements ActionListener {

    JTable table;
    JPopupMenu popupMenu;

    SeisEventRelocateDialog relocateEventDialog;
    HypocentreEditDialog editHypocentreDialog;

    private final Command commandEvent = Global.getCommandEvent();
    private final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final Hypocentre selectedHypocentre = Global.getSelectedHypocentre();

    public HypocentreTablePopupMenu(JTable hypoTable) {
        table = hypoTable;

        setPopupMenuVisualAttributes();

        relocateEventDialog = new SeisEventRelocateDialog();
        editHypocentreDialog = new HypocentreEditDialog();
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    /*
     * Menu item selected.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        // Selected row values
        int selectedRow = table.getSelectedRow();
        int selectedColumn = table.getSelectedColumn();

        if ("Set Prime".equals(e.getActionCommand())) {

            String commandType = "setprime";
            FormulateCommand composeCommand = new FormulateCommand(commandType, "hypocentre", selectedHypocentre.getHypid());
            composeCommand.addAttribute("primehypocentre", table.getValueAt(selectedRow, 0), null);
            composeCommand.addSQLFunction("rf ( " + selectedHypocentre.getHypid() + ", " + selectedSeisEvent.getEvid() + " )");
            composeCommand.addLocatorArg("fix_hypo=" + table.getValueAt(selectedRow, 0));

            if (composeCommand.isValidCommand()) {

                VBASLogger.logDebug("\ncommandLog= " + composeCommand.getCmdProvenance().toString()
                        + "\nsystemCommand= " + composeCommand.getSystemCommand().toString());

                boolean ret = SeisDataDAO.updateCommandTable(Global.getSelectedSeisEvent().getEvid(), commandType,
                        composeCommand.getCmdProvenance().toString(), composeCommand.getSystemCommand().toString());

                if (ret) {
                    VBASLogger.logDebug(" Fired: " + commandType);
                    commandEvent.fireSeisDataChanged();
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        }

        if ("SeisEvent Relocate..".equals(e.getActionCommand())) {
            relocateEventDialog.setLocationRelativeTo(table);
            relocateEventDialog.showHypoTableRelocateDialog();
        }

        if ("Deprecate".equals(e.getActionCommand())) {
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
        /*menuItem_setprime.setBackground(new Color(218, 83, 44));
         menuItem_setprime.setForeground(Color.WHITE);*/
        menuItem_setprime.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_relocate = new JMenuItem("SeisEvent Relocate..");
        menuItem_relocate.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_depricate = new JMenuItem("Deprecate");
        menuItem_depricate.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_edit = new JMenuItem("Hypocentre Edit..");
        menuItem_edit.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_create = new JMenuItem("Create..");
        menuItem_create.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        JMenuItem menuItem_move = new JMenuItem("Move..");
        menuItem_move.setFont(new Font("Sans-serif", Font.PLAIN, 14));

        popupMenu.add(menuItem_setprime);
        popupMenu.addSeparator();
        popupMenu.add(menuItem_edit);
        popupMenu.addSeparator();
        popupMenu.add(menuItem_relocate);
        popupMenu.addSeparator();
        popupMenu.add(menuItem_depricate);
        popupMenu.addSeparator();
        popupMenu.add(menuItem_create);
        popupMenu.addSeparator();
        popupMenu.add(menuItem_move);

        menuItem_setprime.addActionListener(this);
        menuItem_relocate.addActionListener(this);
        menuItem_depricate.addActionListener(this);
        menuItem_edit.addActionListener(this);
        menuItem_create.addActionListener(this);
        menuItem_move.addActionListener(this);
    }

}
