
package uk.ac.isc.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.lang3.time.DateUtils;
import uk.ac.isc.data.BlockTableModel;
import uk.ac.isc.data.SeisEvent;
import uk.ac.isc.data.SeisEventList;
import uk.ac.isc.data.SeisEventsDAO;
import uk.ac.isc.data.TaskBlock;

/**
 * This is the control panel to set time range and geo-selection mode for the mapPanel
 * it keeps two references - time-line panel and map panel
 * 
 * @author hui
 */
public class AssignControlPanel extends JPanel {
    
    /** A data reference for showing the figure*/
    private final SeisEventList seList;
    
    private BlockTableModel btModel = null;
    
    /* a reference to the timeline panel*/
    private final TimelinePanel tpRef;
    /* a reference to the map panel in order to change states in map seleciton*/
    private final MapPanel mpRef;
    
    /** the start date and end date of the time-line*/
    //private final Date startDate;
    
    //private final Date endDate;
    
    /*for appearance and buttons, a flowlayout is chose for the control panel*/
    private final JLabel fromLabel = new JLabel("From");
    
    private final JLabel toLabel = new JLabel("To");
    
    //for setting day
    private final DateHourPickerPanel dtPickerFrom;
    
    private final DateHourPickerPanel dtPickerTo;
    
    private JTextField fromHourText = new JTextField("00:00");
    
    private JTextField toHourText = new JTextField("23:59");
    
    //for setting hours
    //private final SpinnerDateModel smFrom;

    //private final SpinnerDateModel smTo;

    //private final JSpinner spinnerFrom;
    
    //private final JSpinner spinnerTo;
    
    /*controller for the map states*/
    private final JLabel mapShapeLabel = new JLabel("  Map selection shape:");
    
    private final JRadioButton rectSetButton;
    
    private final JRadioButton polySetButton;
    
    private final ButtonGroup interShapeButtonGroup = new ButtonGroup();
    
    private final JButton unselectButton;
    
    private final JButton createButton;
    
    public AssignControlPanel(TimelinePanel tp,MapPanel mp,Date startDate, Date endDate,SeisEventList se)
    {
        this.tpRef = tp;
        this.mpRef = mp;
        
        this.seList = se;
        //this.startDate = startDate;
        //this.endDate = endDate;
        
        dtPickerFrom = new DateHourPickerPanel(startDate);
        dtPickerTo = new DateHourPickerPanel(endDate);
        
        dtPickerFrom.getMonthView().setFirstDisplayedDay(startDate);
        dtPickerTo.getMonthView().setFirstDisplayedDay(endDate);
        dtPickerFrom.getMonthView().setLowerBound(startDate);
        dtPickerFrom.getMonthView().setUpperBound(endDate);
        dtPickerTo.getMonthView().setLowerBound(startDate);
        dtPickerTo.getMonthView().setUpperBound(endDate);
        
        //smFrom = new SpinnerDateModel(tpRef.getSelectedStartDate(), null, null, Calendar.HOUR);
        //spinnerFrom= new JSpinner(smFrom);
        
        //smTo = new SpinnerDateModel(tpRef.getSelectedEndDate(), null, null, Calendar.HOUR);
        //spinnerTo = new JSpinner(smTo);
        
        //JSpinner.DateEditor de = new JSpinner.DateEditor(spinnerFrom, "HH:mm");
        //de.getTextField().setEditable( false );
        //spinnerFrom.setEditor(de);
        
        //JSpinner.DateEditor deTo = new JSpinner.DateEditor(spinnerTo, "HH:mm");
        //deTo.getTextField().setEditable( false );
        //spinnerTo.setEditor(deTo);
        
        fromHourText.setEditable(false);
        toHourText.setEditable(false);
        
        this.setLayout(new FlowLayout());
        this.add(fromLabel);
        this.add(dtPickerFrom);
        this.add(fromHourText);
        //this.add(spinnerFrom);
        this.add(toLabel);
        this.add(dtPickerTo);
        this.add(toHourText);
        //this.add(spinnerTo);
        
        dtPickerFrom.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //setSelectedStartDate(dtPickerFrom.getSelectDay());
                mpRef.setRegionUnselection();
                
                //setSpinnerModelFromDate(dtPickerFrom.getSelectDay());
                Date exactTime = dtPickerFrom.getSelectDay();
                exactTime.setHours(0);
                
                HourPanel hPanel = new HourPanel(seList,exactTime,0);
                hPanel.addMouseListener(hPanel);
                
                hPanel.setSize(300,600);
                UIManager.put("OptionPane.minimumSize", new Dimension(350,650));
                int result = JOptionPane.showConfirmDialog(null,hPanel,"StartDay",JOptionPane.OK_CANCEL_OPTION);
            
                if(result == JOptionPane.OK_OPTION) {
                    Integer hour = exactTime.getHours();
                    String hourString;
                    if(hour<10)
                    {
                        hourString = "0"+hour;
                    }
                    else
                    {
                        hourString = hour.toString();
                    }
                    fromHourText.setText(hourString+":00");
                }
                else
                {
                    exactTime.setHours(0);
                    exactTime.setMinutes(0);
                    exactTime.setSeconds(0);
                    fromHourText.setText("00:00");
                }
                
                if(exactTime.after(dtPickerTo.getDate()))
                {
                    UIManager.put("OptionPane.minimumSize", new Dimension(50,50));
                    JOptionPane.showMessageDialog(null, "Selected start date is after the end Date, it will be re-assigned to the initial start date");
                    exactTime = new Date(startDate.getTime());
                }
                //System.out.println("setStartTime"+exactTime);
                setSelectedStartDate(exactTime);
                dtPickerFrom.setDate(exactTime);
            }
        });
        
        dtPickerTo.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //setSelectedEndDate(dtPickerTo.getSelectDay());
                mpRef.setRegionUnselection();
                
                Date exactTime = dtPickerTo.getSelectDay();
                exactTime.setHours(23);
                exactTime.setMinutes(59);
                exactTime.setSeconds(59);
                
                //setSelectedEndDate(exactTime);
                HourPanel hPanel = new HourPanel(seList, exactTime,1);
                hPanel.addMouseListener(hPanel);
                
                hPanel.setSize(300,600);
                UIManager.put("OptionPane.minimumSize", new Dimension(350,650));
                int result = JOptionPane.showConfirmDialog(null,hPanel,"EndDay",JOptionPane.OK_CANCEL_OPTION);
            
                if(result == JOptionPane.OK_OPTION) {
                    Integer hour = exactTime.getHours();
                    
                    String hourString;
                    if(hour<10)
                    {
                        hourString = "0"+hour;
                    }
                    else
                    {
                        hourString = hour.toString();
                    }
                    int minute = exactTime.getMinutes();
                    toHourText.setText(hourString+":"+minute);
                    exactTime = DateUtils.addSeconds(exactTime, 1);
                }
                else
                {
                    exactTime.setHours(23);
                    exactTime.setMinutes(59);
                    exactTime.setSeconds(59);

                    toHourText.setText("23:59");
                    exactTime = DateUtils.addSeconds(exactTime, 1);
                }
                
                if(exactTime.before(dtPickerFrom.getDate()))
                {
                    UIManager.put("OptionPane.minimumSize", new Dimension(50,50));
                    JOptionPane.showMessageDialog(null, "Selected end date is before the start date, it will be re-assigned to the initial end date");
                    exactTime = new Date(endDate.getTime());
                }

                    //System.out.println("setEndTime"+exactTime);
                    setSelectedEndDate(exactTime);
                    exactTime = DateUtils.addSeconds(exactTime, -1);
                    dtPickerTo.setDate(exactTime);
            }
        });
        
        //spinnerFrom.addChangeListener(this);
        //spinnerTo.addChangeListener(this);
        
        /**add map controller buttons*/
        rectSetButton = new JRadioButton("Rect. Selection");
        rectSetButton.setSelected(true);
        polySetButton = new JRadioButton("Poly. Selection");
        
        rectSetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //rectSetButton.setSelected(true);
                mpRef.setPolySelect(false);
            }
        });
        
        polySetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //polySetButton.setSelected(true);
                mpRef.setPolySelect(true);
            }
        });
        interShapeButtonGroup.add(rectSetButton);
        interShapeButtonGroup.add(polySetButton);
        
        this.add(mapShapeLabel);
        this.add(rectSetButton);
        this.add(polySetButton);
        
        unselectButton = new JButton("Unselect");
        this.add(unselectButton);
        unselectButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                mpRef.setRegionUnselection();
            }
        });
        
        createButton = new JButton("Create Block");
        this.add(createButton);
        createButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                /*get selection number*/
                int tSelNum = 0, gSelNum = 0;
                for(SeisEvent ev:seList.getSeisEvents())
                {
                    if(ev.getTSelction()==true && ev.getblAssigned()!=true)
                    {
                        tSelNum++;
                        if(ev.getGSelction()==true)
                        {
                            gSelNum++;
                        }
                    }
                }
                
                UIManager.put("OptionPane.minimumSize", new Dimension(250,450));
                /*get a option panel to decide the block building based on time or geo*/
                
                String str;
                if(gSelNum>0)
                {
                    str = "Create block by time+geo selection ( " + gSelNum + " Events)";
                }
                else
                {
                    str = "Create block by time selection ( "+ tSelNum + " Events)";
                } 
                
                AssignDialogPanel myPanel = new AssignDialogPanel(str);
                                                
                int result = JOptionPane.showConfirmDialog(null, myPanel, "Select Creation Mode",JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {

                    /*get the block id from database first*/
                    int alloc_id = SeisEventsDAO.retrieveNewBlockID();
                    
                    TaskBlock tb = new TaskBlock(alloc_id);
                    tb.setAnalyst1(myPanel.getAnalyst1Name());
                    tb.setAnalyst1ID(myPanel.getAnalyst1ID());
                    tb.setAnalyst2(myPanel.getAnalyst2Name());
                    tb.setAnalyst2ID(myPanel.getAnalyst2ID());
                    tb.setAnalyst3(myPanel.getAnalyst2Name());
                    tb.setAnalyst3ID(myPanel.getAnalyst3ID());
                    
                    tb.setPPlanStartDay(myPanel.getPPlanStartDate());
                    tb.setPPlanEndDay(myPanel.getPPlanEndDate());
                    tb.setSPlanStartDay(myPanel.getSPlanStartDate());
                    tb.setSPlanEndDay(myPanel.getSPlanEndDate());
                    tb.setFPlanStartDay(myPanel.getFPlanStartDate());
                    tb.setFPlanEndDay(myPanel.getFPlanEndDate());
                    
                    boolean commitSuccess = false;
                    
                    if(gSelNum>0)
                    {
                        commitSuccess = SeisEventsDAO.createBlock(seList.getSeisEvents(),tpRef.getSelectedStartDate(), tpRef.getSelectedEndDate(), true, tb);
                        /*after commitment, change the flag to selection*/
                        for(SeisEvent ev:seList.getSeisEvents())
                        {
                            if(ev.getTSelction()==true && ev.getGSelction()==true)
                            {
                                ev.setblAssigned(true);
                                ev.setBlockID(tb.getBlockID());
                            }
                        }
                    }
                    else
                    {
                        commitSuccess = SeisEventsDAO.createBlock(seList.getSeisEvents(),tpRef.getSelectedStartDate(), tpRef.getSelectedEndDate(), false,tb);
                        /*after commitment, change the flag to selection*/
                        for(SeisEvent ev:seList.getSeisEvents())
                        {
                            if(ev.getTSelction()==true)
                            {
                                ev.setblAssigned(true);
                                ev.setBlockID(tb.getBlockID());
                            }
                        }
                    }
                    
                    if(commitSuccess == true)
                    {
                        JOptionPane.showMessageDialog(null, "Block has been assigned successfully.");
                    }
                    //now call the observer to update
                    seList.setChangeFlag();
                    seList.notifyObservers();

                    //btModel.fireTableDataChanged();
                }
                
            }
        });
    }
        
    public void setBlockTableModel(BlockTableModel btModel)
    {
        this.btModel = btModel;
    }
    
    private void setSelectedStartDate(Date selectedStartDate)
    {
       tpRef.setSelectedStartDate(selectedStartDate);
    }
    
    private void setSelectedEndDate(Date selectedEndDate)
    {
        tpRef.setSelectedEndDate(selectedEndDate);
    }

    
}
