package uk.ac.isc.textview;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import uk.ac.isc.seisdata.Command;
import uk.ac.isc.seisdatainterface.FormulateCommand;
import uk.ac.isc.seisdatainterface.Global;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdatainterface.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.VBASLogger;

public class HypocentreEditDialog extends JDialog {

    private JButton button_cancel;
    private JButton button_ok;
    private JLabel jLabel1;
    private JLabel jLabel13;
    private JLabel jLabel14;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JScrollPane jScrollPane1;
    private JTextArea textArea_reason;
    private JLabel label_coord;
    private JLabel label_depth;
    private JLabel label_evid;
    private JLabel label_hypid;
    private JLabel label_prime;
    private JLabel label_time;
    private JTextArea text_depth;
    private JTextField text_lat;
    private JTextField text_lon;
    private JTextField text_time;

    private final Command commandEvent = Global.getCommandEvent();
    private final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final Hypocentre selectedHypocentre = Global.getSelectedHypocentre();

    public HypocentreEditDialog() {

        setTitle("Edit Hypocentre");
        setModal(true);
        setResizable(false);
        layoutComponents();

    }

    private void button_okActionPerformed(ActionEvent evt) {

        String commandType = "hypocentreedit";
        FormulateCommand formulateCommand = new FormulateCommand(commandType, "hypocentre", selectedHypocentre.getHypid());

        /*
         * Depth 
         */
        Integer newDepth = null;
        try {
            newDepth = Integer.parseInt(text_depth.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Incorrect depth format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newDepth != selectedHypocentre.getDepth()) {
            formulateCommand.addAttribute("depth", newDepth, selectedHypocentre.getDepth());
            formulateCommand.addSQLFunction("chhypo ( "
                    + selectedHypocentre.getHypid() + ", "
                    + "''depth'', "
                    + "''" + Integer.parseInt(text_depth.getText()) + "'' )");
        }

        /*
         * Date/Time 
         */
        Date newTime = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            newTime = df.parse(text_time.getText());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Incorrect date time.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (newTime.compareTo(selectedHypocentre.getOrigTime()) != 0) {  // same newDate time
            formulateCommand.addAttribute("time", newTime.toString(), selectedHypocentre.getOrigTime().toString());
            formulateCommand.addSQLFunction("chhypo ( "
                    + selectedHypocentre.getHypid() + ", "
                    + "''time'', "
                    + "''" + text_time.getText() + "'' )");
        }

        /*
         * Latitude
         */
        if (Double.parseDouble(text_lat.getText()) != selectedHypocentre.getLat()) {
            formulateCommand.addAttribute("lat", Double.parseDouble(text_lat.getText()), selectedHypocentre.getLat());
            formulateCommand.addSQLFunction("chhypo ( "
                    + selectedHypocentre.getHypid() + ", "
                    + "''lat'', "
                    + "''" + Double.parseDouble(text_lat.getText()) + "'' )");
        }

        /*
         * Longitude 
         */
        if (Double.parseDouble(text_lon.getText()) != selectedHypocentre.getLon()) {
            formulateCommand.addAttribute("lon", Double.parseDouble(text_lon.getText()), selectedHypocentre.getLon());
            formulateCommand.addSQLFunction("chhypo ( "
                    + selectedHypocentre.getHypid() + ", "
                    + "''lon'', "
                    + "''" + Double.parseDouble(text_lon.getText()) + "'' )");
        }

        if (formulateCommand.isValidCommand()) {

            /*
             * Add the reason text, only when a valid command is formulated.
             */
            if (textArea_reason.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Please give a reason.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else {
                formulateCommand.addAttribute("reason", textArea_reason.getText(), null);
            }

            VBASLogger.logDebug("\ncommandLog= " + formulateCommand.getCmdProvenance().toString()
                    + "\nsystemCommand= " + formulateCommand.getSystemCommand().toString());

            boolean ret = SeisDataDAO.updateCommandTable(selectedSeisEvent.getEvid(), commandType,
                    formulateCommand.getCmdProvenance().toString(), formulateCommand.getSystemCommand().toString());
            if (ret) {
                VBASLogger.logDebug(" Fired: " + commandType);
                commandEvent.fireSeisDataChanged();
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        this.dispose();
    }

    private void button_cancelActionPerformed(ActionEvent evt) {
        this.dispose();
    }

    public void showHypoEditDialog() {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DecimalFormat df2 = new DecimalFormat(".##");

        label_evid.setText(selectedHypocentre.getEvid().toString());
        label_hypid.setText(selectedHypocentre.getHypid().toString());
        label_time.setText(df.format(selectedHypocentre.getOrigTime()));
        label_coord.setText(df2.format(selectedHypocentre.getLat()) + "N "
                + df2.format(selectedHypocentre.getLon()) + "W");
        label_depth.setText(selectedHypocentre.getDepth().toString());
        label_prime.setText(selectedHypocentre.getIsPrime().toString());

        text_depth.setText(selectedHypocentre.getDepth().toString());
        text_lat.setText(df2.format(selectedHypocentre.getLat()));
        text_lon.setText(df2.format(selectedHypocentre.getLon()));
        text_time.setText(df.format(selectedHypocentre.getOrigTime()));

        textArea_reason.setText(null);

        setVisible(true);
    }

    private void layoutComponents() {
        button_ok = new JButton();
        button_cancel = new JButton();
        jPanel2 = new JPanel();
        jLabel1 = new JLabel();
        label_evid = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        label_time = new JLabel();
        label_depth = new JLabel();
        label_hypid = new JLabel();
        label_coord = new JLabel();
        label_prime = new JLabel();
        jPanel1 = new JPanel();
        jLabel13 = new JLabel();
        jLabel14 = new JLabel();
        text_depth = new JTextArea();
        jLabel2 = new JLabel();
        text_lat = new JTextField();
        text_lon = new JTextField();
        jLabel8 = new JLabel();
        jLabel9 = new JLabel();
        text_time = new JTextField();
        jPanel3 = new JPanel();
        jScrollPane1 = new JScrollPane();
        textArea_reason = new JTextArea();

        /*button_ok.setBackground(new Color(45, 137, 239));
         button_ok.setForeground(new Color(255, 255, 255));*/
        button_ok.setText("OK");
        button_ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                button_okActionPerformed(evt);
            }
        });

        /*button_cancel.setBackground(new java.awt.Color(45, 137, 239));
         button_cancel.setForeground(new java.awt.Color(255, 255, 255));*/
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        jPanel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Original Value"));

        jLabel1.setText("EVID:");
        label_evid.setText("evid");
        jLabel3.setText("HYPID:");
        jLabel4.setText("PRIME:");
        jLabel5.setText("DEPTH:");
        jLabel6.setText("TIME:");
        jLabel7.setText("COORD:");
        label_time.setText("time");
        label_depth.setText("depth");
        label_hypid.setText("hypid");
        label_coord.setText("coord");
        label_prime.setText("prime");

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(18, 18, 18)
                                        .addComponent(label_time))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(18, 18, 18)
                                        .addComponent(label_evid))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_depth)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(label_hypid))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_coord))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(18, 18, 18)
                                        .addComponent(label_prime)))
                        .addGap(96, 96, 96))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(label_evid)
                                .addComponent(jLabel3)
                                .addComponent(label_hypid))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(jLabel7)
                                .addComponent(label_time)
                                .addComponent(label_coord))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(jLabel4)
                                .addComponent(label_depth)
                                .addComponent(label_prime))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Edit"));

        jLabel13.setText("DEPTH:");
        jLabel14.setText("TIME:");

        //text_depth.setFormatterFactory(new text.DefaultFormatterFactory(new text.NumberFormatter()));
        text_depth.setToolTipText("");
        jLabel2.setText("COORD:");
        jLabel8.setText("N");
        jLabel9.setText("W");

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel13)
                                .addComponent(jLabel14))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(text_depth, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
                                        .addGap(41, 41, 41)
                                        .addComponent(jLabel2)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(text_lat, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel8)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(text_lon, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel9))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(text_time, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel13)
                                .addComponent(text_depth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)
                                .addComponent(text_lat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(text_lon, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8)
                                .addComponent(jLabel9))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel14)
                                .addComponent(text_time, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
        );

        jPanel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Reason"));

        textArea_reason.setColumns(20);
        textArea_reason.setRows(5);
        jScrollPane1.setViewportView(textArea_reason);

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1)
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jPanel3, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_ok, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_cancel)
                        .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(button_ok)
                                .addComponent(button_cancel))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.getAccessibleContext().setAccessibleName("Reason");

        pack();
    }
}
