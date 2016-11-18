/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.isc.processcommand;

import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import org.openide.util.Exceptions;
import uk.ac.isc.seisdata.AssessedCommand;
import uk.ac.isc.seisdata.CommandList;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.SeisEventsList;
import uk.ac.isc.seisdata.VBASLogger;
import uk.ac.isc.seisdatainterface.FormulateCommand;
import uk.ac.isc.seisdatainterface.Global;
import uk.ac.isc.seisdatainterface.Locator;
import uk.ac.isc.seisdatainterface.SeisDataDAO;

/*
 *****************************************************************************************
 * A panel (button) to send the selected commands to assessed-comamnd-tableCommand .
 *****************************************************************************************
 */
public class CommandTablePanel extends JPanel {

    private final JLabel label_total;
    private final JLabel label_totalValue;
    private final JButton button_assess;
    private final JButton button_manualCommand;
    private final ManualCommand manualCommand = new ManualCommand();

    private final JButton button_commit;

    private final JTable tableCommand;             // reference of the tableCommand

    private final SeisEventsList seisEventList = Global.getSeisEventsList();
    private final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final CommandList commandList = Global.getCommandList();
    private final AssessedCommand assessedCommandEvent = Global.getAssessedComamndEvent(); // send event to AssessedCommand tableCommand

    Calendar calendar = new GregorianCalendar();

    public CommandTablePanel(final JTable commandTable) {
        this.tableCommand = commandTable;

        Font font = new Font("Sans-serif", Font.PLAIN, 14);

        button_assess = new JButton("Assess");
        /*button_assess.setBackground(new Color(45, 137, 239));
         button_assess.setForeground(new Color(255, 255, 255));*/
        button_assess.setFont(font);
        button_manualCommand = new JButton("Manual Command");
        button_manualCommand.setFont(font);

        label_total = new JLabel(""); // No. of comamnds selected for assess:
        label_total.setFont(font);
        label_totalValue = new JLabel("");
        label_totalValue.setFont(font);

        button_assess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onButtonAssessActionPerformed(e);
            }
        });

        button_manualCommand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                onButtonManualComamndActionPerformed(ae);
            }

        });

        button_commit = new JButton("Commit");
        button_commit.setFont(font);

        button_commit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onButtonCommitActionPerformed(e);
            }
        });

        this.setLayout(new FlowLayout());
        this.add(button_manualCommand);
        this.add(button_assess);
        this.add(new JLabel("                       "));
        this.add(button_commit);
        //this.add(label_total);
        //this.add(label_totalValue);
    }

    public void onButtonCommitActionPerformed(ActionEvent e) {
        VBASLogger.logDebug("Commit...");
        button_commit.setEnabled(false);
        if (this.assessOrCommit(false)) {
            button_commit.setEnabled(true);
            VBASLogger.logDebug("SeiesEventList fire...");
            seisEventList.fireSeisDataChanged();
        }
    }

    public void onButtonAssessActionPerformed(ActionEvent e) {
        VBASLogger.logDebug("Assess...");
        button_assess.setEnabled(false);
        if (this.assessOrCommit(true)) {
            button_assess.setEnabled(true);
        }
    }

    private Boolean assessOrCommit(Boolean isAssess) {

        /* Access/Commit directory and URL related */
        calendar.setTime(Global.getSelectedSeisEvent().getPrimeHypo().getOrigTime());
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = ((calendar.get(Calendar.MONTH) + 1) < 10
                ? ("0" + String.valueOf(calendar.get(Calendar.MONTH) + 1))
                : String.valueOf(calendar.get(Calendar.MONTH) + 1));

        /* Ex. 2013/04/678905 */
        String offsetPath = year + File.separator
                + month + File.separator
                + Global.getSelectedSeisEvent().getEvid();

        /* Ex. http://nemesis.isc.ac.uk/(assess)(commit)/2013/04/678905*/
        String offsetUrl = (isAssess ? SeisDataDAO.getAssessUrl() : SeisDataDAO.getCommitUrl())
                + "/" + offsetPath;

        String commandType = (isAssess ? "assess" : "commit");

        /* Stage-1 Write the commands in the database. */
        int[] selectedRows = tableCommand.getSelectedRows();
        if (selectedRows.length <= 0) {
            JOptionPane.showMessageDialog(null, "Select a command.", "Warning", JOptionPane.WARNING_MESSAGE);
            button_assess.setEnabled(true);
            button_commit.setEnabled(true);
            return false;
        }

        // create a new command merged all the selected commands.
        ArrayList<Integer> commandIds = new ArrayList<Integer>();
        FormulateCommand formulateCommand
                = new FormulateCommand(commandType, "seisevent", Global.getSelectedSeisEvent().getEvid(), "");

        commandIds = formulateCommand.mergeSystemCommand(selectedRows, tableCommand);
                 
      
        /* Update the assessed-command table */
        int newAssessId = 0;
        
        if (formulateCommand.isValidSystemCommand()) {
            VBASLogger.logDebug("isAssess=" + isAssess + ", offsetUrl=" + offsetUrl);
            VBASLogger.logDebug("commandProvenance= " + formulateCommand.getCmdProvenance().toString());
            VBASLogger.logDebug("systemCommand= " + formulateCommand.getSystemCommand().toString());
            
            newAssessId = SeisDataDAO.updateAssessedCommandTable(Global.getSelectedSeisEvent().getEvid(),
                    commandType, commandIds, offsetUrl, "");

            if (newAssessId > 0) {
                VBASLogger.logDebug("Fired: " + commandType);
                assessedCommandEvent.fireSeisDataChanged();
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect Command. \nReport to system admin.", 
                        "ERROR", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Incorrect Command. \nReport to system admin.", 
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }


        /* Invoke locator */
        Path dir = Paths.get((isAssess ? SeisDataDAO.getAssessDir() : SeisDataDAO.getCommitDir())
                + File.separator + offsetPath 
                + File.separator + newAssessId);

        VBASLogger.logDebug("Run locator, dir: " + dir.toString());

        Boolean ret = Locator.runLocator(dir,
                formulateCommand.getSQLFunctionArray(),
                formulateCommand.getLocatorArgStr(),
                isAssess);

        /* if assesss, then generate HTML report */
        if (ret) {
            /* generate report */
            GenerateReport generateReport = new GenerateReport(dir,
                    newAssessId,
                    formulateCommand.getAnalystRedableMergedCommand(),
                    formulateCommand.getSystemCommand().toString(),
                    isAssess);

            String url = offsetUrl + "/" + newAssessId + "/" + newAssessId + ".html";

            /* open the html file */
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
            JOptionPane.showMessageDialog(null, commandType + " is complete. Please see the report in your browser. \n" + url,
                    "Complete", JOptionPane.NO_OPTION);

        } else if (isAssess) {
            JOptionPane.showMessageDialog(null, "Locator command failed. \nReport to system admin.", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    
    private void onButtonManualComamndActionPerformed(ActionEvent ae) {
        manualCommand.setLocationRelativeTo(button_manualCommand);
        manualCommand.showManualCommandDialog(selectedSeisEvent.getEvid());
    }
}
