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

        if ("Hypocentre Edit..".equals(e.getActionCommand())) {
            editHypocentreDialog.setLocationRelativeTo(table);
            editHypocentreDialog.showHypoEditDialog();
        }

        if ("Set Prime".equals(e.getActionCommand())) {

            String commandType = "setprime";
            FormulateCommand formulateCommand = new FormulateCommand(commandType,
                    "hypocentre", selectedHypocentre.getHypid(),
                    selectedHypocentre.getAgency());

            formulateCommand.addAttribute("primehypocentre", table.getValueAt(selectedRow, 0), null);
            formulateCommand.addSQLFunction("rf ( " + selectedHypocentre.getHypid() + ", " + selectedSeisEvent.getEvid() + " )");
            formulateCommand.addLocatorArg("fix_hypo=" + table.getValueAt(selectedRow, 0));

            if (formulateCommand.isValidSystemCommand()) {

                VBASLogger.logDebug("\ncommandLog= " + formulateCommand.getCmdProvenance().toString()
                        + "\nsystemCommand= " + formulateCommand.getSystemCommand().toString());

                boolean ret = SeisDataDAO.updateCommandTable(Global.getSelectedSeisEvent().getEvid(), commandType,
                        formulateCommand.getCmdProvenance().toString(), formulateCommand.getSystemCommand().toString());

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

        if ("Create Event".equals(e.getActionCommand())) {

            String commandType = "createevent";
            FormulateCommand formulateCommand = new FormulateCommand(commandType,
                    "hypocentre", selectedHypocentre.getHypid(),
                    selectedHypocentre.getAgency());

            //formulateCommand.addAttribute("primehypocentre", table.getValueAt(selectedRow, 0), null);
            int newEvent = SeisDataDAO.getNextNewEvid(false);

            formulateCommand.addSQLFunction("create_event ( " + selectedHypocentre.getHypid() + ", " + newEvent + " )");
            //formulateCommand.addLocatorArg("fix_hypo=" + table.getValueAt(selectedRow, 0));

            if (formulateCommand.isValidSystemCommand()) {

                VBASLogger.logDebug("\ncommandLog= " + formulateCommand.getCmdProvenance().toString()
                        + "\nsystemCommand= " + formulateCommand.getSystemCommand().toString());

                boolean ret = SeisDataDAO.updateCommandTable(Global.getSelectedSeisEvent().getEvid(), commandType,
                        formulateCommand.getCmdProvenance().toString(), formulateCommand.getSystemCommand().toString());

                if (ret) {
                    VBASLogger.logDebug(" Fired: " + commandType);
                    commandEvent.fireSeisDataChanged();
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        }

        if ("Move Hypocentre..".equals(e.getActionCommand())) {

            String commandType = "movehypocentre";
            FormulateCommand formulateCommand = new FormulateCommand(commandType,
                    "hypocentre", selectedHypocentre.getHypid(),
                    selectedHypocentre.getAgency());

            String evidToStr = JOptionPane.showInputDialog(table, "Enter an Evid.: ");

            if (evidToStr == null) {
                return;
            }

            int evidTo = 0;
            try {
                evidTo = Integer.parseInt(evidToStr);
            } catch (NumberFormatException e1) {
                JOptionPane.showMessageDialog(table, "Enter an integer value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (NullPointerException e2) {
                JOptionPane.showMessageDialog(table, "Enter an integer value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //formulateCommand.addAttribute("primehypocentre", table.getValueAt(selectedRow, 0), null);
            formulateCommand.addSQLFunction("move_hypo ( " + selectedHypocentre.getHypid() + ", " + evidTo + ")");
            //formulateCommand.addLocatorArg("fix_hypo=" + table.getValueAt(selectedRow, 0));

            if (formulateCommand.isValidSystemCommand()) {

                VBASLogger.logDebug("\ncommandLog= " + formulateCommand.getCmdProvenance().toString()
                        + "\nsystemCommand= " + formulateCommand.getSystemCommand().toString());

                boolean ret = SeisDataDAO.updateCommandTable(Global.getSelectedSeisEvent().getEvid(),
                        commandType,
                        formulateCommand.getCmdProvenance().toString(),
                        formulateCommand.getSystemCommand().toString());

                if (ret) {
                    VBASLogger.logDebug(" Fired: " + commandType);
                    commandEvent.fireSeisDataChanged();
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        }

        if ("Delete Hypocentre".equals(e.getActionCommand())) {

            if (table.getRowCount() == 1) {
                int reply = JOptionPane.showConfirmDialog(table,
                        "Deleting this hypocentre will banish the event. \n Are you sure?",
                        "Delete?",
                        JOptionPane.YES_NO_OPTION);
                if(reply == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            String commandType = "deletehypocentre";
            FormulateCommand formulateCommand = new FormulateCommand(commandType,
                    "hypocentre", selectedHypocentre.getHypid(),
                    selectedHypocentre.getAgency());

            //formulateCommand.addAttribute("primehypocentre", table.getValueAt(selectedRow, 0), null);
            formulateCommand.addSQLFunction("delete_hypo ( " + selectedHypocentre.getHypid() + " )");
            //formulateCommand.addLocatorArg("fix_hypo=" + table.getValueAt(selectedRow, 0));

            if (formulateCommand.isValidSystemCommand()) {

                VBASLogger.logDebug("\ncommandLog= " + formulateCommand.getCmdProvenance().toString()
                        + "\nsystemCommand= " + formulateCommand.getSystemCommand().toString());

                boolean ret = SeisDataDAO.updateCommandTable(Global.getSelectedSeisEvent().getEvid(),
                        commandType,
                        formulateCommand.getCmdProvenance().toString(),
                        formulateCommand.getSystemCommand().toString());

                if (ret) {
                    VBASLogger.logDebug(" Fired: " + commandType);
                    commandEvent.fireSeisDataChanged();
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        }

    }

    private void setPopupMenuVisualAttributes() {
        popupMenu = new JPopupMenu();

        JMenuItem menuItem_edit = new JMenuItem("Hypocentre Edit..");
        menuItem_edit.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        popupMenu.add(menuItem_edit);
        popupMenu.addSeparator();
        menuItem_edit.addActionListener(this);

        JMenuItem menuItem_setprime = new JMenuItem("Set Prime");
        /*menuItem_setprime.setBackground(new Color(218, 83, 44));
         menuItem_setprime.setForeground(Color.WHITE);*/
        menuItem_setprime.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        popupMenu.add(menuItem_setprime);
        popupMenu.addSeparator();
        menuItem_setprime.addActionListener(this);

        JMenuItem menuItem_relocate = new JMenuItem("SeisEvent Relocate..");
        menuItem_relocate.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        popupMenu.add(menuItem_relocate);
        popupMenu.addSeparator();
        menuItem_relocate.addActionListener(this);

        JMenuItem menuItem_create = new JMenuItem("Create Event");
        menuItem_create.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        popupMenu.add(menuItem_create);
        popupMenu.addSeparator();
        menuItem_create.addActionListener(this);

        JMenuItem menuItem_move = new JMenuItem("Move Hypocentre..");
        menuItem_move.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        popupMenu.add(menuItem_move);
        popupMenu.addSeparator();
        menuItem_move.addActionListener(this);

        JMenuItem menuItem_delete = new JMenuItem("Delete Hypocentre");
        menuItem_delete.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        popupMenu.add(menuItem_delete);
        menuItem_delete.addActionListener(this);
    }

}
