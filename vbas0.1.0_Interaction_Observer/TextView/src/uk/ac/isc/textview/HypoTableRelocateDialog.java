/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.isc.textview;

import java.awt.Checkbox;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import uk.ac.isc.seisdata.Command;
import uk.ac.isc.seisdata.Global;


public class HypoTableRelocateDialog extends JDialog {

    private final Command command;
    
    private JButton buttonOK;
    private JButton buttonCancel;

    private Checkbox checkbox1;
    private JFormattedTextField formattedTextFieldDepth;
    private JLabel jLabel1;
    private JLabel jLabel10;
    private JLabel jLabel11;
    private JLabel jLabel12;
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
    private JTextArea jTextArea1;
    private JRadioButton radioButtonDefault;
    private JRadioButton radioButtonFix;
    private JRadioButton radioButtonFree;
    private JRadioButton radioButtonMedian;
    
    
    public HypoTableRelocateDialog() {

            command = Global.getCommand();  
            
            layoutComponents();
            groupRadioButton();
            
    }
    
        
        private void groupRadioButton() {
        ButtonGroup group = new ButtonGroup();
        group.add(this.radioButtonFix);
        group.add(this.radioButtonDefault);
        group.add(this.radioButtonMedian);
        group.add(this.radioButtonFree);
    }

        
        
    private void layoutComponents() {
        
        buttonOK = new JButton();
        buttonCancel = new JButton();
        jPanel2 = new JPanel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        jLabel8 = new JLabel();
        jLabel9 = new JLabel();
        jLabel10 = new JLabel();
        jLabel11 = new JLabel();
        jLabel12 = new JLabel();
        jPanel1 = new JPanel();
        jLabel13 = new JLabel();
        radioButtonFix = new JRadioButton();
        radioButtonDefault = new JRadioButton();
        radioButtonMedian = new JRadioButton();
        radioButtonFree = new JRadioButton();
        jLabel14 = new JLabel();
        checkbox1 = new java.awt.Checkbox();
        formattedTextFieldDepth = new JFormattedTextField();
        jPanel3 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jTextArea1 = new JTextArea();

        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setTitle("Relocate Event");

        buttonOK.setText("OK");
        buttonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOKActionPerformed(evt);
            }
        });

        buttonCancel.setText("Cancel");
        buttonCancel.setActionCommand("Cancel");
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        jPanel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Original Value"));

        jLabel1.setText("EVID:");

        jLabel2.setText("jLabel2");

        jLabel3.setText("HYPID:");

        jLabel4.setText("PRIME");

        jLabel5.setText("DEPTH:");

        jLabel6.setText("TIME:");

        jLabel7.setText("COORD:");

        jLabel8.setText("jLabel8");

        jLabel9.setText("jLabel9");

        jLabel10.setText("jLabel10");

        jLabel11.setText("jLabel11");

        jLabel12.setText("jLabel12");

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
                        .addComponent(jLabel8))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel10))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)))
                .addGap(96, 96, 96))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel10))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel11))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel9)
                    .addComponent(jLabel12))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Input"));

        jLabel13.setText("DEPTH:");

        radioButtonFix.setText("Fix");
        radioButtonFix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonFixActionPerformed(evt);
            }
        });

        radioButtonDefault.setText("Default");
        radioButtonDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonDefaultActionPerformed(evt);
            }
        });

        radioButtonMedian.setText("Median");
        radioButtonMedian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonMedianActionPerformed(evt);
            }
        });

        radioButtonFree.setText("Free");
        radioButtonFree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonFreeActionPerformed(evt);
            }
        });

        jLabel14.setText("FIX:");

        checkbox1.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        //formattedTextFieldDepth.setFormatterFactory(new text.DefaultFormatterFactory(new text.NumberFormatter()));
        //formattedTextFieldDepth.setToolTipText("");

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
                        .addComponent(formattedTextFieldDepth, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(radioButtonFix)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioButtonDefault)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioButtonMedian)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioButtonFree))
                    .addComponent(checkbox1, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(radioButtonFix)
                    .addComponent(radioButtonDefault)
                    .addComponent(radioButtonMedian)
                    .addComponent(radioButtonFree)
                    .addComponent(formattedTextFieldDepth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel14))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(checkbox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Comment"));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

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
                .addComponent(buttonOK, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonCancel)
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
                    .addComponent(buttonOK)
                    .addComponent(buttonCancel))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }
  


    
    private void radioButtonFreeActionPerformed(java.awt.event.ActionEvent evt) {                                                
        // TODO add your handling code here:
        if(this.radioButtonFree.isSelected()) {
            this.formattedTextFieldDepth.setEditable(false);
            this.formattedTextFieldDepth.setEnabled(false);
        } 
    }                                               

    private void radioButtonDefaultActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        // TODO add your handling code here:
        if(this.radioButtonDefault.isSelected()) {
            this.formattedTextFieldDepth.setEditable(false);
            this.formattedTextFieldDepth.setEnabled(false);
        } 
    }                                                  

    private void radioButtonMedianActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        // TODO add your handling code here:
        if(this.radioButtonMedian.isSelected()) {
            this.formattedTextFieldDepth.setEditable(false);
            this.formattedTextFieldDepth.setEnabled(false);
        }
    }                                                 

    
    /*
     * OK / Cancel
     */
    private void buttonOKActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
        
                    
            //System.out.print("Depth value: " + this.textFieldDepth.getText().equals(""));
            
            if(this.formattedTextFieldDepth.getText().equals(" ") && this.radioButtonFix.isSelected()) {
                JOptionPane.showMessageDialog(null, "Enter Depth.");
            }
            
        //double area, perimeter, length, width;
        //length = Double.parseDouble(this.jTextFieldLength.getText());
        //this.jTextFieldArea.setText(String.format("%f", length));          
        
        command.setCmdName("New Command from the Relocate Event Dialog...");
        Global.setCommand(command);
        command.fireSeisDataChanged();
        System.out.println("Fired: New Command from the Relocate Event Dialog...");
        this.dispose();
    }                                        

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // TODO add your handling code here:
        this.dispose();
    }                                            

    private void radioButtonFixActionPerformed(java.awt.event.ActionEvent evt) {                                               
        // TODO add your handling code here:
        if(this.radioButtonFix.isSelected()) {
            this.formattedTextFieldDepth.setEditable(true);
            this.formattedTextFieldDepth.setEnabled(true);
        }   
    }                                              

    
}
