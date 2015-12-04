
package uk.ac.isc.textview;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;
import uk.ac.isc.seisdata.Phase;

/**
 * The phase table model for the phase table
 * @author hui
 */
public class PhaseTextViewTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Edit","Agency", "Code", "Time", 
    "Region Name","Delta. Deg.", "ES. Az.","Op. ID","ISC ID","ISC Res","Def.", "AmpMag", "Slowness", "SNR", "SE. Az.", "Phase ID", "Reading ID"};
    
    private final Class[] columns = new Class[]{JButton.class, String.class, String.class, String.class, String.class, Double.class, Double.class, 
        String.class, String.class, String.class, String.class, String.class, Double.class, Double.class, Double.class, Integer.class, Integer.class};
    
    private ArrayList<Phase> phasesList;
    
    public PhaseTextViewTableModel(ArrayList<Phase> phasesList)
    {
        this.phasesList = phasesList;
    }
    
    public void setPhasesList(ArrayList<Phase> plist)
    {
        this.phasesList = plist;
    }
    
    @Override
    public int getRowCount() {
        return phasesList.size();
    }

    @Override
    public int getColumnCount() {
        return 17;
    }

    @Override
    public String getColumnName(int col)
    {
        return columnNames[col];
    }
    
    public ArrayList<Phase> getPhasesList()
    {
        return this.phasesList;
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        Object retObject = null;
        
        NumberFormat numFormat = DecimalFormat.getInstance();
        numFormat.setMaximumFractionDigits(1);
        numFormat.setMinimumFractionDigits(1);
         
        //editor button
        if(columnIndex==0)
        {
            JButton button = new JButton();
            
            //button.addActionListener(new ActionListener() {
           //     @Override
           //     public void actionPerformed(ActionEvent e) {
           //        System.out.println();
           //     }    
           //});
            
            retObject = button;//ImageIcon("/export/home/hui/NetBeansProjects/VBAS1/SeisData/src/uk/ac/isc/SeisData/edited.png");
        }
        else if(columnIndex==1) //Agency
        {
            retObject = phasesList.get(rowIndex).getReportAgency();
        }
        else if(columnIndex == 2)//staion code
        {
            retObject = phasesList.get(rowIndex).getReportStation();
        }
        else if(columnIndex == 3)//arrival time
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            if(phasesList.get(rowIndex).getArrivalTime()!=null)
               retObject = dateFormat.format(phasesList.get(rowIndex).getArrivalTime())+"."+phasesList.get(rowIndex).getMsec()/100;
            else
               retObject = null;
        }
        else if(columnIndex == 4) //station region 
        {
            /*TODO: change it to station region instead of full name*/
            retObject = phasesList.get(rowIndex).getRegionName();
        }
        else if(columnIndex == 5) //distance
        {
            //retObject = Double.valueOf(numFormat.format(phasesList.get(rowIndex).getDistance()));
            retObject = Math.round(phasesList.get(rowIndex).getDistance());
        }
        else if(columnIndex == 6) //event to station azimuth
        {
            //retObject = Double.valueOf(numFormat.format(phasesList.get(rowIndex).getAzimuth()));
            retObject = Math.round(phasesList.get(rowIndex).getAzimuth());
        }
        else if(columnIndex == 7) //reported phase type
        {
            retObject = phasesList.get(rowIndex).getOrigPhaseType();
        }
        else if(columnIndex == 8) //isc phase type
        {
            retObject = phasesList.get(rowIndex).getIscPhaseType();
        }
        else if(columnIndex == 9) //residual
        {   
            if(phasesList.get(rowIndex).getTimeResidual()!=null)
            {
                //retObject = Double.valueOf(numFormat.format(phasesList.get(rowIndex).getTimeResidual()));
                if(phasesList.get(rowIndex).getTimeResidual()>0)
                {
                     retObject = "+" + numFormat.format(phasesList.get(rowIndex).getTimeResidual());
                }
                else
                {
                    retObject = numFormat.format(phasesList.get(rowIndex).getTimeResidual());
            
                }
            }
            else
            {
                retObject = null;
            }
        }
        else if(columnIndex == 10) //defining or not
        {
            if(phasesList.get(rowIndex).getDefining()==true)
            {
                retObject = "T";
            }
            else
                retObject = null;
        }
        else if(columnIndex == 11) //ampmap
        {
            if(phasesList.get(rowIndex).getAmpMag()!=null)
            {
                if(phasesList.get(rowIndex).getAmpmagDefining()==null)
                {
                    retObject = numFormat.format(phasesList.get(rowIndex).getAmpMag());
                }
                else if(phasesList.get(rowIndex).getAmpmagDefining()==true)
                {
                    retObject = numFormat.format(phasesList.get(rowIndex).getAmpMag())+"D";
                }
            
            }
            else
                retObject = null;  
            
        }
        else if(columnIndex == 12)
        {
            retObject = phasesList.get(rowIndex).getSlowness();
        }
        else if(columnIndex == 13)
        {
            retObject = phasesList.get(rowIndex).getSNRRate();
        }
        else if(columnIndex == 14)
        {
            retObject = phasesList.get(rowIndex).getSeAzimuth();
        }
        else if(columnIndex == 15)
        {
            retObject = phasesList.get(rowIndex).getPhid();
        }
        else if(columnIndex == 16)
        {
            retObject = phasesList.get(rowIndex).getRdid();
        }
        
        return retObject;
    }
     
    @Override
    public Class getColumnClass(int c) {
        //Object o = getValueAt(0, c);
        //if(o!=null)
        //{
        //    return o.getClass();
        //}
        //else
        //    return String.class;
        return columns[c];
    }
    
}
