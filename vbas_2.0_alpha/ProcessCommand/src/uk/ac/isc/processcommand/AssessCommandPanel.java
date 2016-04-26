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
import uk.ac.isc.seisdata.VBASLogger;
import uk.ac.isc.seisdatainterface.FormulateCommand;
import uk.ac.isc.seisdatainterface.Global;
import uk.ac.isc.seisdatainterface.Locator;
import uk.ac.isc.seisdatainterface.SeisDataDAO;
import uk.ac.isc.seisdatainterface.SeisDataDAOAssess;

/*
 *****************************************************************************************
 * A panel (button) to send the selected commands to assessed-comamnd-table .
 *****************************************************************************************
 */
public class AssessCommandPanel extends JPanel {

    private final JLabel label_total;
    private final JButton button_assess;
    private final JTable table;             // reference of the table
    private final GenerateReport generateReport = new GenerateReport();

    private final CommandList commandList = Global.getCommandList();
    private final AssessedCommand assessedCommandEvent = Global.getAssessedComamndEvent(); // send event to AssessedCommand table

    public AssessCommandPanel(final JTable commandTable) {
        this.table = commandTable;

        Font font = new Font("Sans-serif", Font.PLAIN, 14);

        button_assess = new JButton("Assess");
        /*button_assess.setBackground(new Color(45, 137, 239));
         button_assess.setForeground(new Color(255, 255, 255));*/
        button_assess.setFont(font);

        label_total = new JLabel("");
        label_total.setFont(font);

        button_assess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onButtonAssessActionPerformed(e);
            }
        });

        this.setLayout(new FlowLayout());
        this.add(button_assess);
        this.add(label_total);
    }

    public void onButtonAssessActionPerformed(ActionEvent e) {

        button_assess.setEnabled(false);

        /*
         *  Stage-1 Write the commands in the database.
         */
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length <= 0) {
            JOptionPane.showMessageDialog(null, "Select a command.", "Warning", JOptionPane.WARNING_MESSAGE);
            button_assess.setEnabled(true);
            return;
        }

        ArrayList<Integer> commandIds = new ArrayList<Integer>();
        String commandType = "assess";
        FormulateCommand formulateCommand = new FormulateCommand(commandType, "seisevent", Global.getSelectedSeisEvent().getEvid());

        for (int row : selectedRows) {
            commandIds.add((Integer) table.getValueAt(row, 0));

            String systemCommandStr = commandList.getCommandList().get(row).getSystemCommandStr();

            // add/append the command to the formulated asses command
            VBASLogger.logDebug("Append the cmd: " + systemCommandStr);
            formulateCommand.addSystemCommand(systemCommandStr);

        }

        // the
        Date date = Global.getSelectedSeisEvent().getPrimeHypo().getOrigTime();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        /*
         * Now write the assessed details
         */
        int newAssessId = 0;
        if (formulateCommand.isValidCommand()) {

            VBASLogger.logDebug("commandProvenance= " + formulateCommand.getCmdProvenance().toString());
            VBASLogger.logDebug("systemCommand= " + formulateCommand.getSystemCommand().toString());

            String url = "http://nemesis.isc.ac.uk/assess" + "/"
                    + year + "/"
                    + (month < 10 ? ("0" + String.valueOf(month)) : String.valueOf(month)) + "/"
                    + Global.getSelectedSeisEvent().getEvid();

            newAssessId = SeisDataDAO.updateAssessedCommandTable(Global.getSelectedSeisEvent().getEvid(),
                    commandType, commandIds, url, "");

            if (newAssessId > 0) {
                VBASLogger.logDebug(" Fired: " + commandType);
                assessedCommandEvent.fireSeisDataChanged();
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
                button_assess.setEnabled(true);
                return;
            }
        } else {
            button_assess.setEnabled(true);
            return;
        }

        /*
         * ***************************************************************************
         * Locator run
         * ****************************************************************************
         */
        Path assessDir = Paths.get(SeisDataDAOAssess.getAssessDir().toString()
                + File.separator + year
                + File.separator + (month < 10 ? ("0" + String.valueOf(month)) : String.valueOf(month))
                + File.separator + Global.getSelectedSeisEvent().getEvid()
                + File.separator + newAssessId);

        Boolean ret = Locator.runLocator(assessDir, formulateCommand.getSQLFunctionArray(), formulateCommand.getLocatorArgStr());

        /*
         * ***************************************************************************
         * GenerateReport: runLocator relocator, generate html etc.. If susccess write the
         * generateReport info in the AssessedCommand table
         * ****************************************************************************
         */
        if (ret == true) {

            /*
             * Load assessed data.
             */
            generateReport.readAssessedData();
            File htmlFile = generateReport.createHTML(assessDir, newAssessId);
            generateReport.createTables();
            generateReport.createViews();
            generateReport.writeCommands(formulateCommand.getSystemCommand().toString());

            String url = "http://nemesis.isc.ac.uk/assess"
                    + "/" + year
                    + "/" + (month < 10 ? ("0" + String.valueOf(month)) : String.valueOf(month))
                    + "/" + Global.getSelectedSeisEvent().getEvid()
                    + "/" + newAssessId
                    + "/" + newAssessId + ".html";

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

            // open html as a file in browser
            /*try {
             Desktop.getDesktop().browse(htmlFile.toURI());
             } catch (IOException ex) {
             Exceptions.printStackTrace(ex);
             }*/
            JOptionPane.showMessageDialog(null, "Assess Complete. Please see the report in your browser. \n" + url,
                    "Complete", JOptionPane.NO_OPTION);

        } else {
            JOptionPane.showMessageDialog(null, "Locator command failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        button_assess.setEnabled(true);
    }
}
