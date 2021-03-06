/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.isc.hypooverview;

/**
 *
 *  
 * @deprecated 
 */
public class OverviewControlPanel2 extends javax.swing.JPanel {

    private final HypoOverviewPanel2 hop;
        
    /**
     * Creates new form OverviewControlPanel2
     */
    public OverviewControlPanel2(final HypoOverviewPanel2 hop) {
        initComponents();
        this.hop = hop;
        
        closeTopRB.setSelected(true);
        
        band1CheckBox.setSelected(true);
        band2CheckBox.setSelected(true);
        band3CheckBox.setSelected(true);
        band4CheckBox.setSelected(true);
        band5CheckBox.setSelected(true);
        band6CheckBox.setSelected(true);
        band7CheckBox.setSelected(true);
        band8CheckBox.setSelected(true);
        
        dotSize1Button.setSelected(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        shallowTopRB = new javax.swing.JRadioButton();
        deepTopRB = new javax.swing.JRadioButton();
        randomTopRB = new javax.swing.JRadioButton();
        closeTopRB = new javax.swing.JRadioButton();
        animOrderRB = new javax.swing.JRadioButton();
        seisOrderLable = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        band1CheckBox = new javax.swing.JCheckBox();
        band2CheckBox = new javax.swing.JCheckBox();
        band3CheckBox = new javax.swing.JCheckBox();
        band4CheckBox = new javax.swing.JCheckBox();
        band5CheckBox = new javax.swing.JCheckBox();
        band6CheckBox = new javax.swing.JCheckBox();
        band7CheckBox = new javax.swing.JCheckBox();
        band8CheckBox = new javax.swing.JCheckBox();
        bandSelectionLable = new javax.swing.JLabel();
        dotSize1Button = new javax.swing.JRadioButton();
        dotSize2Button = new javax.swing.JRadioButton();
        dotSize3Button = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        noHypoButton = new javax.swing.JRadioButton();
        staticHypoButton = new javax.swing.JRadioButton();
        highlightHypoButton = new javax.swing.JRadioButton();
        animHypoButton = new javax.swing.JRadioButton();

        jSplitPane1.setDividerLocation(780);
        jSplitPane1.setToolTipText(org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.jSplitPane1.toolTipText")); // NOI18N

        jSplitPane2.setDividerLocation(350);

        buttonGroup1.add(shallowTopRB);
        org.openide.awt.Mnemonics.setLocalizedText(shallowTopRB, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.shallowTopRB.text")); // NOI18N
        shallowTopRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shallowTopRBActionPerformed(evt);
            }
        });

        buttonGroup1.add(deepTopRB);
        org.openide.awt.Mnemonics.setLocalizedText(deepTopRB, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.deepTopRB.text")); // NOI18N
        deepTopRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deepTopRBActionPerformed(evt);
            }
        });

        buttonGroup1.add(randomTopRB);
        org.openide.awt.Mnemonics.setLocalizedText(randomTopRB, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.randomTopRB.text")); // NOI18N
        randomTopRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomTopRBActionPerformed(evt);
            }
        });

        buttonGroup1.add(closeTopRB);
        org.openide.awt.Mnemonics.setLocalizedText(closeTopRB, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.closeTopRB.text")); // NOI18N
        closeTopRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeTopRBActionPerformed(evt);
            }
        });

        buttonGroup1.add(animOrderRB);
        org.openide.awt.Mnemonics.setLocalizedText(animOrderRB, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.animOrderRB.text")); // NOI18N
        animOrderRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                animOrderRBActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(seisOrderLable, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.seisOrderLable.text_1")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(shallowTopRB)
                        .addComponent(deepTopRB)
                        .addComponent(randomTopRB)
                        .addComponent(closeTopRB)
                        .addComponent(animOrderRB))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(seisOrderLable, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(seisOrderLable, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(shallowTopRB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(deepTopRB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(randomTopRB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(closeTopRB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(animOrderRB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(25, 25, 25))
        );

        jSplitPane2.setLeftComponent(jPanel3);

        org.openide.awt.Mnemonics.setLocalizedText(band1CheckBox, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.band1CheckBox.text")); // NOI18N
        band1CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                band1CheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(band2CheckBox, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.band2CheckBox.text")); // NOI18N
        band2CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                band2CheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(band3CheckBox, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.band3CheckBox.text")); // NOI18N
        band3CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                band3CheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(band4CheckBox, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.band4CheckBox.text")); // NOI18N
        band4CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                band4CheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(band5CheckBox, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.band5CheckBox.text")); // NOI18N
        band5CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                band5CheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(band6CheckBox, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.band6CheckBox.text")); // NOI18N
        band6CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                band6CheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(band7CheckBox, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.band7CheckBox.text")); // NOI18N
        band7CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                band7CheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(band8CheckBox, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.band8CheckBox.text")); // NOI18N
        band8CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                band8CheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(bandSelectionLable, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.bandSelectionLable.text_1")); // NOI18N

        buttonGroup2.add(dotSize1Button);
        org.openide.awt.Mnemonics.setLocalizedText(dotSize1Button, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.dotSize1Button.text")); // NOI18N
        dotSize1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dotSize1ButtonActionPerformed(evt);
            }
        });

        buttonGroup2.add(dotSize2Button);
        org.openide.awt.Mnemonics.setLocalizedText(dotSize2Button, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.dotSize2Button.text")); // NOI18N
        dotSize2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dotSize2ButtonActionPerformed(evt);
            }
        });

        buttonGroup2.add(dotSize3Button);
        org.openide.awt.Mnemonics.setLocalizedText(dotSize3Button, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.dotSize3Button.text")); // NOI18N
        dotSize3Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dotSize3ButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.jLabel1.text_1")); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(band1CheckBox)
                            .addComponent(band2CheckBox)
                            .addComponent(band3CheckBox)
                            .addComponent(band4CheckBox)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bandSelectionLable)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(dotSize1Button)
                                .addGap(18, 18, 18))
                            .addComponent(jLabel1))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(band5CheckBox)
                            .addComponent(band6CheckBox)
                            .addComponent(band8CheckBox)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(dotSize3Button)
                                .addComponent(band7CheckBox)))
                        .addGap(92, 92, 92))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(dotSize2Button)
                        .addGap(209, 209, 209))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bandSelectionLable)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(band2CheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(band3CheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(band4CheckBox))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(band5CheckBox)
                            .addComponent(band1CheckBox))
                        .addGap(18, 18, 18)
                        .addComponent(band6CheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(band7CheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(band8CheckBox)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dotSize1Button)
                    .addComponent(dotSize2Button)
                    .addComponent(dotSize3Button))
                .addContainerGap())
        );

        jSplitPane2.setRightComponent(jPanel4);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 755, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jSplitPane2)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel1);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.jLabel2.text")); // NOI18N

        buttonGroup4.add(noHypoButton);
        org.openide.awt.Mnemonics.setLocalizedText(noHypoButton, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.noHypoButton.text")); // NOI18N
        noHypoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noHypoButtonActionPerformed(evt);
            }
        });

        buttonGroup4.add(staticHypoButton);
        org.openide.awt.Mnemonics.setLocalizedText(staticHypoButton, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.staticHypoButton.text")); // NOI18N

        buttonGroup4.add(highlightHypoButton);
        org.openide.awt.Mnemonics.setLocalizedText(highlightHypoButton, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.highlightHypoButton.text")); // NOI18N

        buttonGroup4.add(animHypoButton);
        org.openide.awt.Mnemonics.setLocalizedText(animHypoButton, org.openide.util.NbBundle.getMessage(OverviewControlPanel2.class, "OverviewControlPanel2.animHypoButton.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(staticHypoButton)
                    .addComponent(highlightHypoButton)
                    .addComponent(animHypoButton)
                    .addComponent(jLabel2)
                    .addComponent(noHypoButton))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(34, 34, 34)
                .addComponent(noHypoButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addComponent(staticHypoButton)
                .addGap(28, 28, 28)
                .addComponent(highlightHypoButton)
                .addGap(37, 37, 37)
                .addComponent(animHypoButton)
                .addGap(32, 32, 32))
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jSplitPane1))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void shallowTopRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shallowTopRBActionPerformed
        // TODO add your handling code here:
        hop.setDepthBandOrder(2);
    }//GEN-LAST:event_shallowTopRBActionPerformed

    private void closeTopRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeTopRBActionPerformed
        // TODO add your handling code here:
        hop.setDepthBandOrder(4);
    }//GEN-LAST:event_closeTopRBActionPerformed

    private void deepTopRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deepTopRBActionPerformed
        // TODO add your handling code here:
        hop.setDepthBandOrder(1);
    }//GEN-LAST:event_deepTopRBActionPerformed

    private void noHypoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noHypoButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_noHypoButtonActionPerformed

    private void randomTopRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomTopRBActionPerformed
        // TODO add your handling code here:
        hop.setDepthBandOrder(3);
    }//GEN-LAST:event_randomTopRBActionPerformed

    private void animOrderRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_animOrderRBActionPerformed
        // TODO add your handling code here:
        hop.setDepthBandOrder(5);
    }//GEN-LAST:event_animOrderRBActionPerformed

    private void dotSize3ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dotSize3ButtonActionPerformed
        // TODO add your handling code here:
        hop.setPixelSize(12);
    }//GEN-LAST:event_dotSize3ButtonActionPerformed

    private void dotSize2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dotSize2ButtonActionPerformed
        // TODO add your handling code here:
        hop.setPixelSize(9);
    }//GEN-LAST:event_dotSize2ButtonActionPerformed

    private void dotSize1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dotSize1ButtonActionPerformed
        // TODO add your handling code here:
        hop.setPixelSize(6);
    }//GEN-LAST:event_dotSize1ButtonActionPerformed

    private void band8CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_band8CheckBoxActionPerformed
        // TODO add your handling code here:
        if(hop.getDepthBandVisible(7)==true)
        {
            hop.setSingleDepthBandVisible(7, false);
        }
        else
        {
            hop.setSingleDepthBandVisible(7, true);
        }
    }//GEN-LAST:event_band8CheckBoxActionPerformed

    private void band7CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_band7CheckBoxActionPerformed
        // TODO add your handling code here:
        if(hop.getDepthBandVisible(6)==true)
        {
            hop.setSingleDepthBandVisible(6, false);
        }
        else
        {
            hop.setSingleDepthBandVisible(6, true);
        }
    }//GEN-LAST:event_band7CheckBoxActionPerformed

    private void band6CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_band6CheckBoxActionPerformed
        // TODO add your handling code here:
        if(hop.getDepthBandVisible(5)==true)
        {
            hop.setSingleDepthBandVisible(5, false);
        }
        else
        {
            hop.setSingleDepthBandVisible(5, true);
        }
    }//GEN-LAST:event_band6CheckBoxActionPerformed

    private void band5CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_band5CheckBoxActionPerformed
        // TODO add your handling code here:
        if(hop.getDepthBandVisible(4)==true)
        {
            hop.setSingleDepthBandVisible(4, false);
        }
        else
        {
            hop.setSingleDepthBandVisible(4, true);
        }
    }//GEN-LAST:event_band5CheckBoxActionPerformed

    private void band4CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_band4CheckBoxActionPerformed
        // TODO add your handling code here:
        if(hop.getDepthBandVisible(3)==true)
        {
            hop.setSingleDepthBandVisible(3, false);
        }
        else
        {
            hop.setSingleDepthBandVisible(3, true);
        }
    }//GEN-LAST:event_band4CheckBoxActionPerformed

    private void band3CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_band3CheckBoxActionPerformed
        // TODO add your handling code here:
        if(hop.getDepthBandVisible(2)==true)
        {
            hop.setSingleDepthBandVisible(2, false);
        }
        else
        {
            hop.setSingleDepthBandVisible(2, true);
        }
    }//GEN-LAST:event_band3CheckBoxActionPerformed

    private void band2CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_band2CheckBoxActionPerformed
        // TODO add your handling code here:
        if(hop.getDepthBandVisible(1)==true)
        {
            hop.setSingleDepthBandVisible(1, false);
        }
        else
        {
            hop.setSingleDepthBandVisible(1, true);
        }
    }//GEN-LAST:event_band2CheckBoxActionPerformed

    private void band1CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_band1CheckBoxActionPerformed
        // TODO add your handling code here:
        if(hop.getDepthBandVisible(0)==true)
        {
            hop.setSingleDepthBandVisible(0, false);
        }
        else
        {
            hop.setSingleDepthBandVisible(0, true);
        }
    }//GEN-LAST:event_band1CheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton animHypoButton;
    private javax.swing.JRadioButton animOrderRB;
    private javax.swing.JCheckBox band1CheckBox;
    private javax.swing.JCheckBox band2CheckBox;
    private javax.swing.JCheckBox band3CheckBox;
    private javax.swing.JCheckBox band4CheckBox;
    private javax.swing.JCheckBox band5CheckBox;
    private javax.swing.JCheckBox band6CheckBox;
    private javax.swing.JCheckBox band7CheckBox;
    private javax.swing.JCheckBox band8CheckBox;
    private javax.swing.JLabel bandSelectionLable;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JRadioButton closeTopRB;
    private javax.swing.JRadioButton deepTopRB;
    private javax.swing.JRadioButton dotSize1Button;
    private javax.swing.JRadioButton dotSize2Button;
    private javax.swing.JRadioButton dotSize3Button;
    private javax.swing.JRadioButton highlightHypoButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JRadioButton noHypoButton;
    private javax.swing.JRadioButton randomTopRB;
    private javax.swing.JLabel seisOrderLable;
    private javax.swing.JRadioButton shallowTopRB;
    private javax.swing.JRadioButton staticHypoButton;
    // End of variables declaration//GEN-END:variables
}
