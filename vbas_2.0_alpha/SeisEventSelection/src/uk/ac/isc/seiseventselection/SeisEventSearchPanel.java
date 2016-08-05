package uk.ac.isc.seiseventselection;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import uk.ac.isc.seisdata.Command;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.SeisEventsList;
import uk.ac.isc.seisdatainterface.Global;
import uk.ac.isc.seisdata.VBASLogger;
import uk.ac.isc.seisdatainterface.FormulateCommand;
import uk.ac.isc.seisdatainterface.SeisDataDAO;


/*
 *****************************************************************************************
 * A search panel for searching event in SiesEvent Table
 *****************************************************************************************
 */
public class SeisEventSearchPanel extends JPanel {

    private final JLabel label_input;
    private final JTextField text_search;
    private final JButton button_search;

    private final JButton button_banish;
    private final JButton button_unbanish;
    private final JButton button_done;
    private final JButton button_allocate;

    private final Command commandEvent = Global.getCommandEvent();
    private static final SeisEventsList seisEventsList = Global.getSeisEventsList();
    private static SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();

    // reference of the control view
    private final SeisEventsTable seisEventsTable;
    private final JTable table;

    public SeisEventSearchPanel(final SeisEventsTable seisEventsTable) {
        this.seisEventsTable = seisEventsTable;
        this.table = seisEventsTable.getSeisEventTable();

        Font font = new Font("Sans-serif", Font.PLAIN, 14);
        label_input = new JLabel("Event #");
        text_search = new JTextField("", 10);
        button_search = new JButton("Search");
        /*button_search.setBackground(new Color(45, 137, 239));
         button_search.setForeground(new Color(255, 255, 255));*/

        label_input.setFont(font);
        text_search.setFont(font);
        button_search.setFont(font);

        button_search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onButtonSearchActionPerformed(e);
            }
        });

        // banish and Un-banish button
        button_banish = new JButton("Banish");
        /*button_banish.setBackground(new Color(45, 137, 239));
         button_banish.setForeground(new Color(255, 255, 255));*/
        button_banish.setFont(font);
        button_banish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                onButtonBanishActionPerformed(ae);
            }
        });

        button_unbanish = new JButton("Unbanish");
        /*button_unbanish.setBackground(new Color(45, 137, 239));
         button_unbanish.setForeground(new Color(255, 255, 255));*/
        button_unbanish.setFont(font);
        button_unbanish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                onButtonUnbanishActionPerformed(ae);
            }
        });

        button_done = new JButton("Done");
        /*button_done.setBackground(new Color(45, 137, 239));
         button_done.setForeground(new Color(255, 255, 255));*/
        button_done.setFont(font);
        button_done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onButtonDoneActionPerformed(e);
            }
        });

        button_allocate = new JButton("Allocate");
        button_allocate.setFont(font);
        button_allocate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onButtonAllocateActionPerformed(e);
            }
        });

        this.setLayout(new FlowLayout());
        this.add(label_input);
        this.add(text_search);
        this.add(button_search);
        this.add(button_allocate);
        this.add(button_banish);
        this.add(button_unbanish);
        this.add(button_done);
    }

    public void onButtonSearchActionPerformed(ActionEvent e) {

        String evidString = text_search.getText().trim();
        Integer evid = null;
        try {
            evid = Integer.valueOf(evidString);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null,
                    "The input Evid should be an integer value",
                    "Search Error",
                    JOptionPane.WARNING_MESSAGE);
        }

        Boolean found = false;
        if (evid != null) {
            found = seisEventsTable.searchSeiesEvent(evid);
        }
        if (evid != null && found == false) {
            JOptionPane.showMessageDialog(null,
                    "Cannot find the input Evid, Please check the input!",
                    "Search Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onButtonBanishActionPerformed(ActionEvent ae) {
        VBASLogger.logDebug("Clicked Banish...");

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Select an event to banish.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (seisEventsList.getEvents().get(row).getIsBanish() == true) {
            return;
        }

        int seisEventId = (Integer) table.getValueAt(row, 0);

        String commandType = "seiseventbanish";
        FormulateCommand formulateCommand = new FormulateCommand(commandType, "seisevent", seisEventId, "");
        String sqlFunction = "banish ( " + seisEventId + " )";
        formulateCommand.addSQLFunction(sqlFunction);

        if (formulateCommand.isValidSystemCommand()) {

            VBASLogger.logDebug("\ncommandLog= " + formulateCommand.getCmdProvenance().toString()
                    + "\nsystemCommand= " + formulateCommand.getSystemCommand());

            boolean ret = SeisDataDAO.updateCommandTable(Global.getSelectedSeisEvent().getEvid(), commandType,
                    formulateCommand.getCmdProvenance().toString(), formulateCommand.getSystemCommand().toString());

            if (ret) {
                VBASLogger.logDebug(" Fired: " + commandType);
                commandEvent.fireSeisDataChanged();
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        SeisDataDAO.processBanishUnbanishAction(sqlFunction);

        seisEventsList.getEvents().get(row).setIsBanish(true);
        VBASLogger.logDebug("Firing an event...");
        seisEventsList.fireSeisDataChanged();
    }

    private void onButtonUnbanishActionPerformed(ActionEvent ae) {
        VBASLogger.logDebug("Clicked Unbanish...");

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Select an event to unbanish.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (seisEventsList.getEvents().get(row).getIsBanish() == false) {
            return;
        }

        int seisEventId = (Integer) table.getValueAt(row, 0);

        String commandType = "seiseventunbanish";
        FormulateCommand formulateCommand = new FormulateCommand(commandType, "seisevent", seisEventId, "");
        String sqlFunction = "RESTORE ( " + seisEventId + " )";
        formulateCommand.addSQLFunction(sqlFunction);

        if (formulateCommand.isValidSystemCommand()) {

            VBASLogger.logDebug("\ncommandLog= " + formulateCommand.getCmdProvenance().toString()
                    + "\nsystemCommand= " + formulateCommand.getSystemCommand());

            boolean ret = SeisDataDAO.updateCommandTable(Global.getSelectedSeisEvent().getEvid(), commandType,
                    formulateCommand.getCmdProvenance().toString(), formulateCommand.getSystemCommand().toString());

            if (ret) {
                VBASLogger.logDebug(" Fired: " + commandType);
                commandEvent.fireSeisDataChanged();
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        SeisDataDAO.processBanishUnbanishAction(sqlFunction);

        seisEventsList.getEvents().get(row).setIsBanish(false);
        VBASLogger.logDebug("Firing an event...");
        seisEventsList.fireSeisDataChanged();
    }

    public void onButtonDoneActionPerformed(ActionEvent e) {
        VBASLogger.logDebug("Clicked Done...");

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Done: select an event.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (seisEventsList.getEvents().get(row).getFinishDate() != null) {
            return;
        }

        /*VBASLogger.logDebug("Selected SeisEvents (compare): "
         + seisEventsList.getEvents().get(row).getEvid()
         + ", " + selectedSeisEvent.getEvid());*/
        SeisDataDAO.processDoneAction(selectedSeisEvent.getEvid());

        seisEventsList.getEvents().get(row).setFinishDate(new Date());
        VBASLogger.logDebug("Firing an event...");
        seisEventsList.fireSeisDataChanged();
    }

    private void onButtonAllocateActionPerformed(ActionEvent e) {
        VBASLogger.logDebug("Clicked Allocate...");

        String evidString = text_search.getText().trim();
        Integer evid = null;
        try {
            evid = Integer.valueOf(evidString);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null,
                    "The input Evid should be an integer value",
                    "Search Error",
                    JOptionPane.WARNING_MESSAGE);
        }

        // TODO: call SQL functiuon
        /* Load new SeiesEvent data */
        VBASLogger.logDebug("Firing an event...");
        seisEventsList.fireSeisDataChanged();
    }

}
