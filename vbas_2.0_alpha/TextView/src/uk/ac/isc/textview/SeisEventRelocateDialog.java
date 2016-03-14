/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.isc.textview;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import uk.ac.isc.seisdata.Command;
import uk.ac.isc.seisdatainterface.FormulateCommand;
import uk.ac.isc.seisdatainterface.Global;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdatainterface.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.VBASLogger;

public class SeisEventRelocateDialog extends JDialog {

    private final Command commandEvent = Global.getCommandEvent();
    private final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final Hypocentre selectedHypocentre = Global.getSelectedHypocentre();

    private JButton button_ok;
    private JButton button_cancel;

    private JCheckBox checkbox_gridSearch;
    private JTextArea text_depth;
    private JLabel jLabel1;
    private JLabel label_hypid;
    private JLabel label_coord;
    private JLabel label_prime;
    private JLabel jLabel13;
    private JLabel jLabel14;
    private JLabel label_evid;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel label_time;
    private JLabel label_depth;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JScrollPane jScrollPane1;
    private JTextArea text_comment;
    private JRadioButton radio_default;
    private JRadioButton radio_fix;
    private JRadioButton radio_free;
    private JRadioButton radio_median;

    public SeisEventRelocateDialog() {
        setTitle("SeisEvent Relocate");
        setModal(true);
        layoutComponents();
        groupRadioButton();
    }

    private void groupRadioButton() {
        ButtonGroup group = new ButtonGroup();
        group.add(this.radio_free);
        group.add(this.radio_fix);
        group.add(this.radio_default);
        group.add(this.radio_median);
    }

    private void radioButtonFixActionPerformed(ActionEvent evt) {
        if (this.radio_fix.isSelected()) {
            this.text_depth.setEditable(true);
            this.text_depth.setEnabled(true);
            this.checkbox_gridSearch.setEnabled(true);
        }
    }

    private void radioButtonFreeActionPerformed(ActionEvent evt) {
        if (this.radio_free.isSelected()) {
            this.text_depth.setEditable(true);
            this.text_depth.setEnabled(true);
            this.checkbox_gridSearch.setEnabled(true);
        }
    }

    private void radioButtonDefaultActionPerformed(ActionEvent evt) {
        if (this.radio_default.isSelected()) {
            this.text_depth.setEditable(false);
            this.text_depth.setEnabled(false);
            this.checkbox_gridSearch.setEnabled(false);
        }
    }

    private void radioButtonMedianActionPerformed(ActionEvent evt) {
        if (this.radio_median.isSelected()) {
            this.text_depth.setEditable(false);
            this.text_depth.setEnabled(false);
            this.checkbox_gridSearch.setEnabled(false);
        }
    }

    public void showHypoTableRelocateDialog() {
        label_evid.setText(selectedHypocentre.getEvid().toString());
        label_hypid.setText(selectedHypocentre.getHypid().toString());
        label_time.setText(selectedHypocentre.getOrigTime().toString());
        label_coord.setText(selectedHypocentre.getLat().toString() + "N " + selectedHypocentre.getLon().toString() + "W");
        label_depth.setText(selectedHypocentre.getDepth().toString());
        label_prime.setText(selectedHypocentre.getIsPrime().toString());

        text_depth.setEditable(true);
        text_depth.setEnabled(true);
        text_depth.setText(selectedHypocentre.getDepth().toString());
        radio_free.setSelected(true);
        checkbox_gridSearch.setEnabled(true);
        checkbox_gridSearch.setSelected(false);

        text_comment.setText(null);

        setVisible(true);
    }

    private void button_okActionPerformed(ActionEvent evt) {

        if (this.text_depth.getText().equals(" ") && this.radio_fix.isSelected()) {
            JOptionPane.showMessageDialog(null, "Enter Depth.");
            return;
        }

        String commandType = "seiseventrelocate";
        FormulateCommand composeCommand = new FormulateCommand(commandType, "seisevent", selectedSeisEvent.getEvid());

        /*
         * Depth : fix & free
         */
        if (text_depth.getText() != null && (radio_fix.isSelected() || radio_free.isSelected())) {

            Integer depth = null;
            try {
                depth = Integer.parseInt(text_depth.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Incorrect depth format.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (radio_fix.isSelected()) {
                composeCommand.addAttribute("fix_depth", depth, selectedHypocentre.getDepth());
                composeCommand.addLocatorArg("fix_depth=" + depth);
            } else if (radio_free.isSelected()) {
                composeCommand.addAttribute("free_depth", depth, selectedHypocentre.getDepth());
                composeCommand.addLocatorArg("free_depth=" + depth);
            }

        }

        /*
         * Depth : default & median
         */
        if (radio_default.isSelected()) {
            composeCommand.addAttribute("fix_depth_default", null, null);
            composeCommand.addLocatorArg("fix_depth_default");
        }

        if (radio_median.isSelected()) {
            composeCommand.addAttribute("fix_depth_median", null, null);
            composeCommand.addLocatorArg("fix_depth_median");
        }

        if ((radio_fix.isSelected() || radio_free.isSelected()) && checkbox_gridSearch.isSelected()) {
            composeCommand.addAttribute("do_gridsearch", (checkbox_gridSearch.isEnabled() ? 1 : 0), null);
            composeCommand.addLocatorArg("do_gridsearch=" + (checkbox_gridSearch.isEnabled() ? 1 : 0));
        }

        if (composeCommand.isValidCommand()) {
            /*
             * Comment (not mandatory) text description. Include it only if a valif command is formulated.
             */
            if (!text_comment.getText().equals("")) {
                composeCommand.addAttribute("comment", text_comment.getText(), null);
            }

            VBASLogger.logDebug("\ncommandLog= " + composeCommand.getCmdProvenance().toString()
                    + "\nsystemCommand= " + composeCommand.getSystemCommand().toString());

            boolean ret = SeisDataDAO.updateCommandTable(selectedSeisEvent.getEvid(), commandType,
                    composeCommand.getCmdProvenance().toString(), composeCommand.getSystemCommand().toString());

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
        radio_fix = new JRadioButton();
        radio_default = new JRadioButton();
        radio_median = new JRadioButton();
        radio_free = new JRadioButton();
        jLabel14 = new JLabel();
        checkbox_gridSearch = new JCheckBox();
        text_depth = new JTextArea();
        jPanel3 = new JPanel();
        jScrollPane1 = new JScrollPane();
        text_comment = new JTextArea();

        /*button_ok.setBackground(new Color(45, 137, 239));
         button_ok.setForeground(new Color(255, 255, 255));*/
        button_ok.setText("OK");
        button_ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                button_okActionPerformed(evt);
            }
        });

        /*button_cancel.setBackground(new Color(45, 137, 239));
         button_cancel.setForeground(new Color(255, 255, 255));*/
        button_cancel.setText("Cancel");
        button_cancel.setActionCommand("Cancel");
        button_cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        jPanel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Original Values"));

        jLabel1.setText("EVID:");
        label_evid.setText("evid");
        jLabel3.setText("HYPID:");
        label_hypid.setText("hypid");
        jLabel4.setText("PRIME:");
        label_prime.setText("prime");
        jLabel5.setText("DEPTH:");
        label_depth.setText("depth");
        jLabel6.setText("TIME:");
        label_time.setText("time");
        jLabel7.setText("COORD:");
        label_coord.setText("coord");

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
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
        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
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

        jPanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Input"));

        jLabel13.setText("DEPTH:");

        radio_fix.setText("Fix");
        radio_fix.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                radioButtonFixActionPerformed(evt);
            }
        });

        radio_default.setText("Default");
        radio_default.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                radioButtonDefaultActionPerformed(evt);
            }
        });

        radio_median.setText("Median");
        radio_median.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                radioButtonMedianActionPerformed(evt);
            }
        });

        radio_free.setText("Free");
        radio_free.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                radioButtonFreeActionPerformed(evt);
            }
        });

        jLabel14.setText("GRID SEARCH:");
        checkbox_gridSearch.setCursor(new Cursor(Cursor.TEXT_CURSOR));

        //formattedTextFieldDepth.setFormatterFactory(new text.DefaultFormatterFactory(new text.NumberFormatter()));
        //formattedTextFieldDepth.setToolTipText("");
        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel13)
                                .addComponent(jLabel14))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(text_depth, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(radio_free)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(radio_fix)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(radio_default)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(radio_median))
                                .addComponent(checkbox_gridSearch, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel13)
                                .addComponent(radio_free)
                                .addComponent(radio_fix)
                                .addComponent(radio_default)
                                .addComponent(radio_median)
                                .addComponent(text_depth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel14))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(checkbox_gridSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
        );

        jPanel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Comment"));

        text_comment.setColumns(20);
        text_comment.setRows(5);
        jScrollPane1.setViewportView(text_comment);

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
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
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
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
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

        pack();
    }

}
