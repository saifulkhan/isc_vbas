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
 * @author hui
 */
class PhaseViewControlPanel extends JPanel {

    private JSlider residualFilter = new JSlider(0, 10, 0);
    private final JCheckBox crustalCheckBox = new JCheckBox("Crustal Phases");
    private final JCheckBox mantleCheckBox = new JCheckBox("Mantle Phases");
    private final JCheckBox coreCheckBox = new JCheckBox("Core Phases");
    private final JCheckBox depthCheckBox = new JCheckBox("Depth Phases");
    private final JCheckBox othersCheckBox = new JCheckBox("Other Phases");

    public PhaseViewControlPanel(final PhaseTravelViewPanel ptvp, final PhaseDetailViewPanel pdvp) {

        residualFilter.setMajorTickSpacing(2);
        residualFilter.setMinorTickSpacing(1);
        residualFilter.setPaintTicks(true);
        residualFilter.setPaintLabels(true);

        crustalCheckBox.setSelected(true);
        mantleCheckBox.setSelected(true);
        coreCheckBox.setSelected(true);
        depthCheckBox.setSelected(true);
        othersCheckBox.setSelected(true);

        this.setLayout(new FlowLayout());
        this.add(residualFilter);
        this.add(crustalCheckBox);
        this.add(mantleCheckBox);
        this.add(coreCheckBox);
        this.add(depthCheckBox);
        this.add(othersCheckBox);

        residualFilter.addChangeListener(new ChangeListener() {

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

        crustalCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (crustalCheckBox.isSelected()) {
                    ptvp.setPhaseTypeVisible(0, true);
                } else {
                    ptvp.setPhaseTypeVisible(0, false);
                }
                ptvp.filterData();
                ptvp.updateDetailPList();
                //pdvp.filterData();
            }
        });

        mantleCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (mantleCheckBox.isSelected()) {
                    ptvp.setPhaseTypeVisible(1, true);
                } else {
                    ptvp.setPhaseTypeVisible(1, false);
                }

                ptvp.filterData();
                ptvp.updateDetailPList();
                //pdvp.filterData();
            }
        });

        coreCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (coreCheckBox.isSelected()) {
                    ptvp.setPhaseTypeVisible(2, true);
                } else {
                    ptvp.setPhaseTypeVisible(2, false);
                }
                ptvp.filterData();
                ptvp.updateDetailPList();
                //pdvp.filterData();
            }
        });

        depthCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (depthCheckBox.isSelected()) {
                    ptvp.setPhaseTypeVisible(3, true);
                } else {
                    ptvp.setPhaseTypeVisible(3, false);
                }
                ptvp.filterData();
                ptvp.updateDetailPList();
                //pdvp.filterData();
            }
        });

        othersCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (othersCheckBox.isSelected()) {
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
        residualFilter.setValue(0);

        crustalCheckBox.setSelected(true);
        mantleCheckBox.setSelected(true);
        coreCheckBox.setSelected(true);
        depthCheckBox.setSelected(true);
        othersCheckBox.setSelected(true);
    }
}
