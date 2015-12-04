
package uk.ac.isc.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.lang3.time.DateUtils;
import uk.ac.isc.data.Analyst;
import uk.ac.isc.data.SeisEventsDAO;

/**
 *
 * @author hui
 */
public class AssignDialogPanel extends javax.swing.JPanel {

    ArrayList<Analyst> analysts = new ArrayList<>();
    
    Date pStartDate, pEndDate;
    
    Date sStartDate, sEndDate;
    
    Date fStartDate, fEndDate;
    
    /**
     * Creates new form AssignDialogPanel
     * @param str the message showing in the dialog
     */
    public AssignDialogPanel(String str) {
        initComponents();
        
        /*now start adding stuff*/
        infoLabel.setText(str);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        SeisEventsDAO.loadAnslysts(analysts);
        for(int i = 0; i<analysts.size(); i++)
        {
            String an = analysts.get(i).getName();
            
            pBox.addItem(an);
            sBox.addItem(an);
            if(!"Trainee".equals(analysts.get(i).getPosition()))
            {
                fBox.addItem(an);
            }
        }
        
        pStartDate = new Date();        
        pDatePicker.setDate(pStartDate);
        
        pEndDate = DateUtils.addDays(pDatePicker.getDate(),(Integer)pDaySpinner.getValue());
        pDatePicker.getMonthView().setLowerBound(pStartDate);
        
        /*If pDatePicker changes, all the DatePicker need be updated*/ 
        pDatePicker.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                
                pStartDate = pDatePicker.getDate();
                
                pEndDate = DateUtils.addDays(pDatePicker.getDate(),(Integer)pDaySpinner.getValue());
                sStartDate = DateUtils.addDays(pDatePicker.getDate(),(Integer)pDaySpinner.getValue());
                sDatePicker.setDate(sStartDate);
                sDatePicker.getMonthView().setLowerBound(sStartDate);
        
                sEndDate = DateUtils.addDays(sDatePicker.getDate(),(Integer)sDaySpinner.getValue());
        
                fStartDate = DateUtils.addDays(sDatePicker.getDate(),(Integer)sDaySpinner.getValue());
                fDatePicker.setDate(fStartDate);
                fDatePicker.getMonthView().setLowerBound(fStartDate);
                
                fEndDate = DateUtils.addDays(fDatePicker.getDate(),(Integer)fDaySpinner.getValue());
            }
        
        });
        
        /*when the spinner is adjusted, sDatePicker and fDatePicker need be updated*/
        pDaySpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                
                pEndDate = DateUtils.addDays(pDatePicker.getDate(),(Integer)pDaySpinner.getValue());
                sStartDate = DateUtils.addDays(pDatePicker.getDate(),(Integer)pDaySpinner.getValue());
                sDatePicker.setDate(sStartDate);
                sDatePicker.getMonthView().setLowerBound(sStartDate);
        
                sEndDate = DateUtils.addDays(sDatePicker.getDate(),(Integer)sDaySpinner.getValue());
        
                fStartDate = DateUtils.addDays(sDatePicker.getDate(),(Integer)sDaySpinner.getValue());
                fDatePicker.setDate(fStartDate);
                fDatePicker.getMonthView().setLowerBound(fStartDate);
                
                fEndDate = DateUtils.addDays(fDatePicker.getDate(),(Integer)fDaySpinner.getValue());
            }
        
        });
        
        sDatePicker.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                sStartDate = sDatePicker.getDate();
                
                sEndDate = DateUtils.addDays(sDatePicker.getDate(),(Integer)sDaySpinner.getValue());
        
                fStartDate = DateUtils.addDays(sDatePicker.getDate(),(Integer)sDaySpinner.getValue());
                fDatePicker.setDate(fStartDate);
                fDatePicker.getMonthView().setLowerBound(fStartDate);
                
                fEndDate = DateUtils.addDays(fDatePicker.getDate(),(Integer)fDaySpinner.getValue());
            }
        
        });
        
        sDaySpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                
                sEndDate = DateUtils.addDays(sDatePicker.getDate(),(Integer)sDaySpinner.getValue());
        
                fStartDate = DateUtils.addDays(sDatePicker.getDate(),(Integer)sDaySpinner.getValue());
                fDatePicker.setDate(fStartDate);
                fDatePicker.getMonthView().setLowerBound(fStartDate);
                
                fEndDate = DateUtils.addDays(fDatePicker.getDate(),(Integer)fDaySpinner.getValue());
            }
        
        });
        
        fDatePicker.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                
                fStartDate = fDatePicker.getDate();
                
                //fDatePicker.setDate(fStartDate);
                //fDatePicker.getMonthView().setLowerBound(fStartDate);
                
                fEndDate = DateUtils.addDays(fDatePicker.getDate(),(Integer)fDaySpinner.getValue());
            }
        
        });
        
        fDaySpinner.addChangeListener(new ChangeListener(){

            @Override
            public void stateChanged(ChangeEvent e) {
                fEndDate = DateUtils.addDays(fDatePicker.getDate(),(Integer)fDaySpinner.getValue());
            }
        
        });
        
        sStartDate = DateUtils.addDays(pDatePicker.getDate(),(Integer)pDaySpinner.getValue());
        sDatePicker.setDate(sStartDate);
        sDatePicker.getMonthView().setLowerBound(sStartDate);
        
        sEndDate = DateUtils.addDays(sDatePicker.getDate(),(Integer)sDaySpinner.getValue());
        
        fStartDate = DateUtils.addDays(sDatePicker.getDate(),(Integer)sDaySpinner.getValue());
        fDatePicker.setDate(fStartDate);
        fDatePicker.getMonthView().setLowerBound(fStartDate);
                
        fEndDate = DateUtils.addDays(fDatePicker.getDate(),(Integer)fDaySpinner.getValue());
        
    }

    public String getAnalyst1Name()
    {
        return this.pBox.getSelectedItem().toString();
    }
    
    public Integer getAnalyst1ID()
    {
        
        Integer id = null;
        
        for(Analyst an:analysts)
        {
            if(this.pBox.getSelectedItem().toString()==an.getName())
            {
                id = an.getID();
            }
        }
        
        return id;
    }
    
    public String getAnalyst2Name()
    {
        return this.sBox.getSelectedItem().toString();
    }
        
    public Integer getAnalyst2ID()
    {
        
        Integer id = null;
        
        for(Analyst an:analysts)
        {
            if(this.sBox.getSelectedItem().toString()==an.getName())
            {
                id = an.getID();
            }
        }
        
        return id;
    }

    
    public String getAnalyst3Name()
    {
        return this.fBox.getSelectedItem().toString();
    }
    
    public Integer getAnalyst3ID()
    {
        
        Integer id = null;
        
        for(Analyst an:analysts)
        {
            if(this.fBox.getSelectedItem().toString()==an.getName())
            {
                id = an.getID();
            }
        }
        
        return id;
    }

    public Date getPPlanStartDate()
    {
        return this.pStartDate;
    }
    
    public Date getPPlanEndDate()
    {
        return this.pEndDate;
    }
    
    public Date getSPlanStartDate()
    {
        return this.sStartDate;
    }
    
    public Date getSPlanEndDate()
    {
        return this.sEndDate;
    }
        
    public Date getFPlanStartDate()
    {
        return this.fStartDate;
    }
    
    public Date getFPlanEndDate()
    {
        return this.fEndDate;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        infoLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        fBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        pDatePicker = new org.jdesktop.swingx.JXDatePicker();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        sBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        sDatePicker = new org.jdesktop.swingx.JXDatePicker();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        pBox = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        fDatePicker = new org.jdesktop.swingx.JXDatePicker();
        jLabel9 = new javax.swing.JLabel();
        pDaySpinner = new javax.swing.JSpinner();
        sDaySpinner = new javax.swing.JSpinner();
        fDaySpinner = new javax.swing.JSpinner();

        infoLabel.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N

        jLabel1.setText("Assign Primary:");

        jLabel2.setText("Planned Start Date:");

        jLabel3.setText("Number of working days:");

        jLabel4.setText("Assign Seconday:");

        jLabel5.setText("Planned Start Date:");

        jLabel6.setText("Number of working days:");

        jLabel7.setText("Assign Final:");

        jLabel8.setText("Planned Start Date:");

        jLabel9.setText("Number of working days:");

        pDaySpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 14, 1));

        sDaySpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 14, 1));

        fDaySpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 14, 1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(infoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                        .addComponent(fDaySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(sDaySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(fDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 147, Short.MAX_VALUE)
                                .addComponent(fBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(sDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                .addComponent(sBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pDatePicker, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                            .addComponent(pDaySpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pDaySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sDaySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fDaySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(52, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox fBox;
    private org.jdesktop.swingx.JXDatePicker fDatePicker;
    private javax.swing.JSpinner fDaySpinner;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JComboBox pBox;
    private org.jdesktop.swingx.JXDatePicker pDatePicker;
    private javax.swing.JSpinner pDaySpinner;
    private javax.swing.JComboBox sBox;
    private org.jdesktop.swingx.JXDatePicker sDatePicker;
    private javax.swing.JSpinner sDaySpinner;
    // End of variables declaration//GEN-END:variables
}
