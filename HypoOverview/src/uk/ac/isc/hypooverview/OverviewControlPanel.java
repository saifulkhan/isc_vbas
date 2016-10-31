
package uk.ac.isc.hypooverview;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This is the panel to control the attributes and flags in the overviewMainPanel
 *  
 * @deprecated 
 */
public final class OverviewControlPanel extends JPanel {

    private final JCheckBox showSeismicity = new JCheckBox("Seismicity Visible");
    private final JCheckBox showMiniMap = new JCheckBox("MiniMap Visible");
    private final JCheckBox showHypocentres = new JCheckBox("Hypocentres Visible");
    private final JCheckBox reverseSeismicity = new JCheckBox("Seismicity Reversed");
    private final JCheckBox showBorder = new JCheckBox("Border Visible");
    private final JCheckBox showAll = new JCheckBox("Force to fit all Hypos");
    private final JSlider depthCut = new JSlider(JSlider.HORIZONTAL,0,8,8);

    private final HypoOverviewPanel hop;
    
    OverviewControlPanel(final HypoOverviewPanel hop) {
    
        this.hop = hop;
        
        this.setLayout(new FlowLayout());
        this.add(showSeismicity);
        showSeismicity.setSelected(hop.getShowSeismicity());
        this.add(showMiniMap);
        showMiniMap.setSelected(hop.getShowMiniMap());
        this.add(showHypocentres);
        showHypocentres.setSelected(hop.getShowHypos());
        
        this.add(reverseSeismicity);
        reverseSeismicity.setSelected(hop.getSeisReversed());
        this.add(depthCut);
                
        this.add(showBorder);
        showBorder.setSelected(hop.getShowBorder());
        this.add(showAll);
        showAll.setSelected(hop.getForceFit());
        
        Hashtable<Integer,JLabel> table = new Hashtable<Integer,JLabel>();
        table.put(0, new JLabel("Shallow (0Km)"));
        table.put(1, new JLabel(""));
        table.put(2, new JLabel(""));
        table.put(3, new JLabel(""));
        table.put(4, new JLabel(""));
        table.put(5, new JLabel(""));
        table.put(6, new JLabel(""));
        table.put(7, new JLabel(""));
        table.put(8, new JLabel("Deep (800Km+)"));
        depthCut.setLabelTable(table);
        
        depthCut.setMajorTickSpacing(1);
        depthCut.setPaintLabels(true);
        depthCut.setPaintTicks(true);
        
        depthCut.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (!source.getValueIsAdjusting()) {
                    int depthValue = (int)source.getValue();
                    hop.setDepthCutOff(depthValue);
                }
            }
        });
        
        showSeismicity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hop.setShowSeismicity(showSeismicity.isSelected());
            }
        });
        
        reverseSeismicity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hop.setSeisReversed(reverseSeismicity.isSelected());
                if(reverseSeismicity.isSelected())
                {
                    depthCut.setValue(0);
                    hop.setDepthCutOff(0);
                }
                else
                {
                    depthCut.setValue(8);
                    hop.setDepthCutOff(8);
                }
                
            }
        });
        
        showHypocentres.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hop.setShowHypos(showHypocentres.isSelected());
            }
        });
                
        showMiniMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hop.setShowMiniMap(showMiniMap.isSelected());
            }
        });
        
        showBorder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hop.setShowBorder(showBorder.isSelected());
            }
        });
        
        showAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hop.setForceFit(showAll.isSelected());
            }
        });
    }
    
    public void setDepthCutoff(int cutoff)
    {
        this.depthCut.setValue(cutoff);
        hop.setDepthCutOff(cutoff);
    }
    
    public boolean getReverseChecked()
    {
        return this.reverseSeismicity.isSelected();
    }
}
