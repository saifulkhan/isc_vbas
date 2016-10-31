package uk.ac.isc.phaseview;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The control panel for setting criteria of data filtering
 *
 *  
 */
class PhaseViewControlPanel extends JPanel {

    private JSlider slider_residualFilter = new JSlider(0, 10, 0);
    private final JCheckBox checkBox_crustal = new JCheckBox("Crustal Phases");
    private final JCheckBox checkBox_mantle = new JCheckBox("Mantle Phases");
    private final JCheckBox checkBox_core = new JCheckBox("Core Phases");
    private final JCheckBox checkBox_depth = new JCheckBox("Depth Phases");
    private final JCheckBox checkBox_others = new JCheckBox("Other Phases");

    public PhaseViewControlPanel(final PhaseTravelViewPanel ptvp, final PhaseDetailViewPanel pdvp) {

        slider_residualFilter.setMajorTickSpacing(2);
        slider_residualFilter.setMinorTickSpacing(1);
        slider_residualFilter.setPaintTicks(true);
        slider_residualFilter.setPaintLabels(true);

        checkBox_crustal.setSelected(true);
        checkBox_mantle.setSelected(true);
        checkBox_core.setSelected(true);
        checkBox_depth.setSelected(true);
        checkBox_others.setSelected(true);

        this.setLayout(new FlowLayout());
        this.add(slider_residualFilter);
        this.add(checkBox_crustal);
        this.add(checkBox_mantle);
        this.add(checkBox_core);
        this.add(checkBox_depth);
        this.add(checkBox_others);

        slider_residualFilter.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    int fps = (int) source.getValue();
                    ptvp.setResidualCutoffLevel((double) fps);
                    pdvp.setResidualCutoffLevel((double) fps);
                    ptvp.filterData();
                    pdvp.filterData();
                }

            }

        });

        checkBox_crustal.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox_crustal.isSelected()) {
                    ptvp.setPhaseTypeVisible(0, true);
                } else {
                    ptvp.setPhaseTypeVisible(0, false);
                }
                ptvp.filterData();
                ptvp.updateDetailPList();
                //pdvp.filterData();
            }
        });

        checkBox_mantle.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox_mantle.isSelected()) {
                    ptvp.setPhaseTypeVisible(1, true);
                } else {
                    ptvp.setPhaseTypeVisible(1, false);
                }

                ptvp.filterData();
                ptvp.updateDetailPList();
                //pdvp.filterData();
            }
        });

        checkBox_core.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox_core.isSelected()) {
                    ptvp.setPhaseTypeVisible(2, true);
                } else {
                    ptvp.setPhaseTypeVisible(2, false);
                }
                ptvp.filterData();
                ptvp.updateDetailPList();
                //pdvp.filterData();
            }
        });

        checkBox_depth.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox_depth.isSelected()) {
                    ptvp.setPhaseTypeVisible(3, true);
                } else {
                    ptvp.setPhaseTypeVisible(3, false);
                }
                ptvp.filterData();
                ptvp.updateDetailPList();
                //pdvp.filterData();
            }
        });

        checkBox_others.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox_others.isSelected()) {
                    ptvp.setPhaseTypeVisible(4, true);
                } else {
                    ptvp.setPhaseTypeVisible(4, false);
                }
                ptvp.filterData();
                ptvp.updateDetailPList();
                //pdvp.filterData();
            }
        });
    }

    public void reset() {
        slider_residualFilter.setValue(0);
        checkBox_crustal.setSelected(true);
        checkBox_mantle.setSelected(true);
        checkBox_core.setSelected(true);
        checkBox_depth.setSelected(true);
        checkBox_others.setSelected(true);
    }
}
