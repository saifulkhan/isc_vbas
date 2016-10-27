package uk.ac.isc.textview;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.LayoutStyle;

/*
 * Sort panel for the phase table.
 * Sort JTable: https://groups.google.com/forum/#!topic/comp.lang.java.help/T5avbBnxgkk
 * Check: http://www.codejava.net/java-se/swing/6-techniques-for-sorting-jtable-you-should-know
 * 
 * TODO: Default sort order (by James)-- delta, code, reading id, init time,  phase id
 */
public class PhaseTableSortPanel extends JPanel {

    private ButtonGroup buttonGroup1;
    private JRadioButton customRadioButton;
    private JRadioButton defRadioButton;
    private JComboBox jComboBox1;
    private JComboBox jComboBox2;
    private JComboBox jComboBox3;
    private JPanel jPanel1;

    //reference of the control view
    private final JTable table;

    public PhaseTableSortPanel(final JTable table) {
        this.table = table;
        layoutComponents();
        groupRadioButtons();
    }

    void layoutComponents() {

        buttonGroup1 = new ButtonGroup();
        jPanel1 = new JPanel();
        defRadioButton = new JRadioButton();
        customRadioButton = new JRadioButton();
        jComboBox1 = new JComboBox();
        jComboBox2 = new JComboBox();
        jComboBox3 = new JComboBox();

        jPanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Sort List By"));

        defRadioButton.setText("Default");

        jComboBox1.setModel(new DefaultComboBoxModel(new String[]{"-", "-", "-"}));
        jComboBox2.setModel(new DefaultComboBoxModel(new String[]{"-", "-", "-"}));
        jComboBox3.setModel(new DefaultComboBoxModel(new String[]{"-", "-", "-"}));

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(defRadioButton)
                        .addGap(39, 39, 39)
                        .addComponent(customRadioButton)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(47, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jComboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jComboBox2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jComboBox3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(4, 4, 4)
                                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                .addComponent(customRadioButton)
                                                .addComponent(defRadioButton))))
                        .addContainerGap(10, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );

    }

    private void groupRadioButtons() {
        ButtonGroup group = new ButtonGroup();
        group.add(this.defRadioButton);
        group.add(this.customRadioButton);
    }

}
