
package uk.ac.isc.databasetest;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

/**
 *
 * @author hui
 */
public class MainFrame extends javax.swing.JFrame {

    //pane and panels for views
    private JSplitPane pairViewsPane = null;
    
    private JSplitPane controlCommandPane = null;
    
    private JScrollPane leftTopPane = null;
    
    private CommandPanel commandPanel = null;
        
    private EventsControlPanel eventsCP = null; 
       
    private JSplitPane hypophasePane = null;
    
    private JScrollPane topPane = null;
    
    private JScrollPane bottomPane = null;
    
    private JTable hyposTable = null;
    
    private HypoTextViewTableModel hptvtModel = null;

    private JTable phasesTable = null;
    
    private PhaseTextViewTableModel ptvtModel = null;
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        
        eventsCP = new EventsControlPanel();
        
        ptvtModel = new PhaseTextViewTableModel(eventsCP.getPhasesList().getPhases());
        hptvtModel = new HypoTextViewTableModel(eventsCP.getHyposList().getHypocentres());
        hyposTable = new JTable(hptvtModel);
        phasesTable = new JTable(ptvtModel);
        
        topPane = new JScrollPane(hyposTable);
        bottomPane = new JScrollPane(phasesTable);
        
        hypophasePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,topPane,bottomPane);
        hypophasePane.setResizeWeight(0.3d);
        
        leftTopPane = new JScrollPane(eventsCP.getTable());
        commandPanel = new CommandPanel();
        commandPanel.setCPRef(eventsCP);
        
        controlCommandPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,leftTopPane,commandPanel);
        controlCommandPane.setResizeWeight(0.7d);
        
        pairViewsPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,controlCommandPane,hypophasePane);
        pairViewsPane.setResizeWeight(0.5d);
        
        eventsCP.getHyposList().addChangeListener(hptvtModel);
        eventsCP.getPhasesList().addChangeListener(ptvtModel);
        
        this.setLayout(new BorderLayout());
        this.add(pairViewsPane, BorderLayout.CENTER);
        
        //first popup menue for the event level
        JPopupMenu eventPopup = new JPopupMenu();
        JMenuItem banishItem = new JMenuItem("Banish the event");
        banishItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                
                Integer selectedEvid = (Integer) eventsCP.getTable().getValueAt(eventsCP.getTable().getSelectedRow(), 0);
                String[] cmd = new String[6];
                cmd[0] = "BanishEvent";
                cmd[1] = selectedEvid.toString();
                
                commandPanel.addCommand(cmd);
            }
        
        });
        
        JMenuItem mergeItem = new JMenuItem("Merge another event to this event");
        mergeItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                
                Integer selectedEvid = (Integer) eventsCP.getTable().getValueAt(eventsCP.getTable().getSelectedRow(), 0);
                String result = JOptionPane.showInputDialog(null, "Put Evid to merge into current event:");
                if (result != null) {
                        //System.out.println(chhypoPanel.getAttribute());
                        //System.out.println(chhypoPanel.getValue());
                    try{
                        int resulttmp = Integer.valueOf(result);
                        
                        String[] cmd = new String[6];
                        cmd[0] = "Merge";
                        cmd[1] = result;
                        //cmd[2] = chhypoPanel.getAttribute();
                    
                        cmd[3] = result;
                        cmd[4] = selectedEvid.toString();
                        commandPanel.addCommand(cmd);
                    }
                    catch(NumberFormatException ne)
                    {
                        JOptionPane.showMessageDialog(null, "Please input the evid to merge into current event");
                    }
                }

            }
        
        });
        
        JMenuItem addCommentsItem = new JMenuItem("Add Comments");
        
        eventPopup.add(banishItem);
        eventPopup.add(mergeItem);
        eventPopup.add(addCommentsItem);
        eventsCP.getTable().setComponentPopupMenu(eventPopup);
        
        //probably try add popup menus
        JPopupMenu hypoPopup = new JPopupMenu();
        
        //the first one is fix prime
        JMenuItem rfItem = new JMenuItem("Fix Hypoentre To");
        rfItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(hyposTable.getSelectedRow()>-1)
                {
                Integer selectedHypid = (Integer) hyposTable.getValueAt(hyposTable.getSelectedRow(),9);
                
                String[] cmd = new String[6];
                cmd[0] = "RF";
                cmd[1] = selectedHypid.toString();
                commandPanel.addCommand(cmd);
                }
                else
                {
                   JOptionPane.showMessageDialog(null, "You must select one hypocentre from the list"); 
                }
            }
        
        });
        
        //the second is changing hypo's attribute
        JMenuItem changeHypoItem = new JMenuItem("Change Hypo Attribute");
        changeHypoItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(hyposTable.getSelectedRow()>-1)
                {
                ChHypoPanel chhypoPanel = new ChHypoPanel();
                Integer selectedHypid = (Integer) hyposTable.getValueAt(hyposTable.getSelectedRow(),9);
                chhypoPanel.setMessage("Change Hypocenter: Hypid: "+selectedHypid.toString()+" attribute on ");
                
                int result = JOptionPane.showConfirmDialog(null, chhypoPanel, 
                        "Change Hypocentre Attribute", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                        //System.out.println(chhypoPanel.getAttribute());
                        //System.out.println(chhypoPanel.getValue());
                    String[] cmd = new String[6];
                    cmd[0] = "ChangeHypo";
                    cmd[1] = selectedHypid.toString();
                    cmd[2] = chhypoPanel.getAttribute();
                    
                    switch(chhypoPanel.getAttribute()) {
                        
                        case "lat": cmd[3] = (String) hyposTable.getValueAt(hyposTable.getSelectedRow(), 2).toString();
                                         break;
                        case "long": cmd[3] = (String) hyposTable.getValueAt(hyposTable.getSelectedRow(), 3).toString();
                                         break;
                        case "depth": cmd[3] = hyposTable.getValueAt(hyposTable.getSelectedRow(), 4).toString();
                                      break;
                        case "time": cmd[3] = (String) hyposTable.getValueAt(hyposTable.getSelectedRow(), 1).toString();
                                     break;
                        default: cmd[3] = "etype not set yet";//we haven't get etype yet
                                    break;
                            
                    }
                    cmd[4] = chhypoPanel.getValue();
                    commandPanel.addCommand(cmd);
                }
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You must select at least one hypocentre from the list");
                }
            }
        });
        
        //the third is puting hypo to other event
        JMenuItem putHypoItem = new JMenuItem("Put Hypo");
        putHypoItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(hyposTable.getSelectedRow()>-1)
                {
                Integer selectedHypid = (Integer) hyposTable.getValueAt(hyposTable.getSelectedRow(),9);
                //puthypoPanel.setMessage("Put Hypocenter: Hypid: "+selectedHypid.toString()+"to ");
                                
                String result = JOptionPane.showInputDialog(null, "Put Hypocentre into another event which evid is:");
                if (result != null) {
                        //System.out.println(chhypoPanel.getAttribute());
                        //System.out.println(chhypoPanel.getValue());
                    try{
                        int resulttmp = Integer.valueOf(result);
                        
                        String[] cmd = new String[6];
                        cmd[0] = "PutHypo";
                        cmd[1] = selectedHypid.toString();
                        //cmd[2] = chhypoPanel.getAttribute();
                    
                        cmd[3] = ((Integer)eventsCP.getTable().getValueAt(eventsCP.getTable().getSelectedRow(), 0)).toString();
                        cmd[4] = result;
                        commandPanel.addCommand(cmd);
                    }
                    catch(NumberFormatException ne)
                    {
                        JOptionPane.showMessageDialog(null, "Please input the correct Hypid");
                    }
                }
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You must select at least one hypocentre from the list");
                }
            }
        });
        
        JMenuItem deleteHypoItem = new JMenuItem("Delete Hypo");
        deleteHypoItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //PutHypoPanel puthypoPanel = new PutHypoPanel();
                if(hyposTable.getSelectedRow()>-1)
                {
                Integer selectedHypid = (Integer) hyposTable.getValueAt(hyposTable.getSelectedRow(),9);
                //puthypoPanel.setMessage("Put Hypocenter: Hypid: "+selectedHypid.toString()+"to ");
                        String[] cmd = new String[6];
                        cmd[0] = "DeleteHypo";
                        cmd[1] = selectedHypid.toString();
                        //cmd[2] = chhypoPanel.getAttribute();
                        //cmd[3] = ((Integer)eventsCP.getTable().getValueAt(eventsCP.getTable().getSelectedRow(), 0)).toString();
                        //cmd[4] = result;
                        commandPanel.addCommand(cmd);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You must select at least one hypocentre from the list");
                }
                    
            }
        });
        
        JMenuItem createEventItem = new JMenuItem("Create New Event");
        createEventItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //PutHypoPanel puthypoPanel = new PutHypoPanel();
                if(hyposTable.getSelectedRow()>-1)
                {
                Integer selectedHypid = (Integer) hyposTable.getValueAt(hyposTable.getSelectedRow(),9);
                //puthypoPanel.setMessage("Put Hypocenter: Hypid: "+selectedHypid.toString()+"to ");
                        String[] cmd = new String[6];
                        cmd[0] = "CreateEvent";
                        cmd[1] = selectedHypid.toString();
                        //cmd[2] = chhypoPanel.getAttribute();
                        cmd[3] = ((Integer)eventsCP.getTable().getValueAt(eventsCP.getTable().getSelectedRow(), 0)).toString();
                        //cmd[4] = ;
                        commandPanel.addCommand(cmd);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You must select one hypocentre from the list");
                }
                    
            }
        });
        
        hypoPopup.add(rfItem);
        hypoPopup.add(changeHypoItem);
        hypoPopup.add(putHypoItem);
        hypoPopup.add(deleteHypoItem);
        hypoPopup.add(createEventItem);
        hyposTable.setComponentPopupMenu(hypoPopup);
        
        //probably try add popup menus, add another popup window to phase table
        JPopupMenu phasePopup = new JPopupMenu();
        
        //
        JMenuItem changePhaseItem = new JMenuItem("Change Phase Attribute");
        changePhaseItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(phasesTable.getSelectedRow()>-1)
                {
                ChPhasePanel chPhasePanel = new ChPhasePanel();
                //int rowNumber = hyposTable.getSelectedRow();
                Integer selectedPhid = (Integer) phasesTable.getValueAt(phasesTable.getSelectedRow(),14);
                chPhasePanel.setMessage("Change Phase: Phid: "+selectedPhid.toString()+" attribute on ");
                
                int result = JOptionPane.showConfirmDialog(null, chPhasePanel, 
                        "Change Phase Attribute", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                        //System.out.println(chhypoPanel.getAttribute());
                        //System.out.println(chhypoPanel.getValue());
                    String[] cmd = new String[6];
                    cmd[0] = "ChangePhase";
                    cmd[1] = selectedPhid.toString();
                    cmd[2] = chPhasePanel.getAttribute();
                    
                    switch(chPhasePanel.getAttribute()) {
                        
                        case "year": {  
                                for(Phase p:ptvtModel.getPhasesList())
                                {
                                if(p.getPhid().equals((Integer)phasesTable.getValueAt(phasesTable.getSelectedRow(), 14)))
                                {
                                    //Date aa = p.getArrivalTime();
                                    cmd[3] = p.getArrivalTime().toString();//(String) phasesTable.getValueAt(phasesTable.getSelectedRow(), 2).toString();
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(p.getArrivalTime());
                                    try {
                                        c.add(Calendar.YEAR,Integer.valueOf(chPhasePanel.getValue()));
                                    }
                                    catch(NumberFormatException ne) {
                                        JOptionPane.showMessageDialog(null, "Please input correct year");
                                    }
                                    cmd[4] = chPhasePanel.getValue();
                                    cmd[5] = c.getTime().toString();
                                    break;
                                }
                                
                                }
                                break;
                        }
                        case "month": {
                            for(Phase p:ptvtModel.getPhasesList())
                            {
                                if(p.getPhid().equals((Integer)phasesTable.getValueAt(phasesTable.getSelectedRow(), 14)))
                                {
                                    //Date aa = p.getArrivalTime();
                                    cmd[3] = p.getArrivalTime().toString();//(String) phasesTable.getValueAt(phasesTable.getSelectedRow(), 2).toString();
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(p.getArrivalTime());
                                    try {
                                        c.add(Calendar.MONTH,Integer.valueOf(chPhasePanel.getValue()));
                                    }
                                    catch(NumberFormatException ne) {
                                        JOptionPane.showMessageDialog(null, "Please input correct month");
                                    }
                                    cmd[4] = chPhasePanel.getValue();
                                    cmd[5] = c.getTime().toString();
                                    break;
                                }
                                
                            }
                            break;
                        }
                        case "day": {
                            for(Phase p:ptvtModel.getPhasesList())
                            {
                                if(p.getPhid().equals((Integer)phasesTable.getValueAt(phasesTable.getSelectedRow(), 14)))
                                {
                                    //Date aa = p.getArrivalTime();
                                    cmd[3] = p.getArrivalTime().toString();//(String) phasesTable.getValueAt(phasesTable.getSelectedRow(), 2).toString();
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(p.getArrivalTime());
                                    try {
                                        c.add(Calendar.DATE,Integer.valueOf(chPhasePanel.getValue()));
                                    }
                                    catch(NumberFormatException ne) {
                                        JOptionPane.showMessageDialog(null, "Please input correct day");
                                    }
                                    cmd[4] = chPhasePanel.getValue();
                                    cmd[5] = c.getTime().toString();
                                    break;
                                }
                                
                            }
                            break;
                        }
                        case "hour": {
                            for(Phase p:ptvtModel.getPhasesList())
                            {
                                if(p.getPhid().equals((Integer)phasesTable.getValueAt(phasesTable.getSelectedRow(), 14)))
                                {
                                    //Date aa = p.getArrivalTime();
                                    cmd[3] = p.getArrivalTime().toString();//(String) phasesTable.getValueAt(phasesTable.getSelectedRow(), 2).toString();
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(p.getArrivalTime());
                                    try {
                                        c.add(Calendar.HOUR,Integer.valueOf(chPhasePanel.getValue()));
                                    }
                                    catch(NumberFormatException ne) {
                                        JOptionPane.showMessageDialog(null, "Please input correct hour");
                                    }
                                    cmd[4] = chPhasePanel.getValue();
                                    cmd[5] = c.getTime().toString();
                                    break;
                                }
                                
                            }
                            break;
                        }
                        case "minute": {
                            for(Phase p:ptvtModel.getPhasesList())
                            {
                                if(p.getPhid().equals((Integer)phasesTable.getValueAt(phasesTable.getSelectedRow(), 14)))
                                {
                                    //Date aa = p.getArrivalTime();
                                    cmd[3] = p.getArrivalTime().toString();//(String) phasesTable.getValueAt(phasesTable.getSelectedRow(), 2).toString();
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(p.getArrivalTime());
                                    try {
                                        c.add(Calendar.MINUTE,Integer.valueOf(chPhasePanel.getValue()));
                                    }
                                    catch(NumberFormatException ne) {
                                        JOptionPane.showMessageDialog(null, "Please input correct min");
                                    }
                                    cmd[4] = chPhasePanel.getValue();
                                    cmd[5] = c.getTime().toString();
                                    break;
                                }
                                
                            }
                            break;
                        }
                        case "second": {
                            for(Phase p:ptvtModel.getPhasesList())
                            {
                                if(p.getPhid().equals((Integer)phasesTable.getValueAt(phasesTable.getSelectedRow(), 14)))
                                {
                                    //Date aa = p.getArrivalTime();
                                    cmd[3] = p.getArrivalTime().toString();//(String) phasesTable.getValueAt(phasesTable.getSelectedRow(), 2).toString();
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(p.getArrivalTime());
                                    try {
                                        c.add(Calendar.SECOND,Integer.valueOf(chPhasePanel.getValue()));
                                    }
                                    catch(NumberFormatException ne) {
                                        JOptionPane.showMessageDialog(null, "Please input correct second");
                                    }
                                    cmd[4] = chPhasePanel.getValue();
                                    cmd[5] = c.getTime().toString();
                                    break;
                                }
                                
                            }
                            break;
                        }
                        case "phase":{
                            
                            cmd[3] = (String) phasesTable.getValueAt(phasesTable.getSelectedRow(), 7).toString();
                            cmd[4] = chPhasePanel.getValue();
                            break;
                        }
                        case "phase_fixed":{
                            if(chPhasePanel.getValue().equals("1")||chPhasePanel.getValue().equals("0"))
                            {
                            for(Phase p:ptvtModel.getPhasesList())
                            {
                                if(p.getPhid().equals((Integer)phasesTable.getValueAt(phasesTable.getSelectedRow(), 14)))
                                {
                                    //Date aa = p.getArrivalTime();
                                    if(p.getfixing()==true)
                                    {   
                                        cmd[3] = "1";
                                    }
                                    else
                                    {
                                        cmd[3] = "0";
                                    }
                                    
                                    cmd[4] = chPhasePanel.getValue();
                                    break;
                                }
                                
                            }
                            }
                            else
                            {
                                JOptionPane.showMessageDialog(null, "Use 1 for fix and 0 for unfix");
                            }
                            break;
                        }
                        case "nondef":{
                            if(chPhasePanel.getValue().equals("1")||chPhasePanel.getValue().equals("0"))
                            {
                            for(Phase p:ptvtModel.getPhasesList())
                            {
                                if(p.getPhid().equals((Integer)phasesTable.getValueAt(phasesTable.getSelectedRow(), 14)))
                                {
                                    //Date aa = p.getArrivalTime();
                                    if(p.getDefining()==true)
                                    {   
                                        cmd[3] = "1";
                                    }
                                    else
                                    {
                                        cmd[3] = "0";
                                    }
                                    cmd[4] = chPhasePanel.getValue();
                                    break;
                                }
                                
                            }
                            }
                            else
                            {
                                JOptionPane.showMessageDialog(null, "Use 1 for define and 0 for undefine");
                            }
                            break;
                        }
                        default: //cmd[3] = "etype not set yet";//we haven't get etype yet
                                    break;
                            
                    }
                    //cmd[4] = chPhasePanel.getValue();
                    commandPanel.addCommand(cmd);
                }
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You must select at least one phase from the list");
                }
            }
        });
        
        JMenuItem putReadingItem = new JMenuItem("Put Reading");
        putReadingItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(phasesTable.getSelectedRow()>-1)
                {
                Integer selectedRdid = (Integer) phasesTable.getValueAt(phasesTable.getSelectedRow(),15);
                //puthypoPanel.setMessage("Put Hypocenter: Hypid: "+selectedHypid.toString()+"to ");
                                
                String result = JOptionPane.showInputDialog(null, "Put Reading into another event which evid is:");
                if (result != null) {
                        //System.out.println(chhypoPanel.getAttribute());
                        //System.out.println(chhypoPanel.getValue());
                    try{
                        int resulttmp = Integer.valueOf(result);
                        
                        String[] cmd = new String[6];
                        cmd[0] = "PutReading";
                        cmd[1] = selectedRdid.toString();
                        //cmd[2] = chhypoPanel.getAttribute();
                    
                        cmd[3] = ((Integer)eventsCP.getTable().getValueAt(eventsCP.getTable().getSelectedRow(), 0)).toString();
                        cmd[4] = result;
                        commandPanel.addCommand(cmd);
                    }
                    catch(NumberFormatException ne)
                    {
                        JOptionPane.showMessageDialog(null, "Please input the correct Evid");
                    }
                }
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You must select at least one reading from the list");
                }
            }
        });
        
        JMenuItem deleteReadingItem = new JMenuItem("Delete Reading");
        deleteReadingItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //PutHypoPanel puthypoPanel = new PutHypoPanel();
                if(phasesTable.getSelectedRow()>-1)
                {
                    Integer selectedRdid = (Integer) phasesTable.getValueAt(phasesTable.getSelectedRow(),15);
                //puthypoPanel.setMessage("Put Hypocenter: Hypid: "+selectedHypid.toString()+"to ");
                        String[] cmd = new String[6];
                        cmd[0] = "DeleteReading";
                        cmd[1] = selectedRdid.toString();
                        //cmd[2] = chhypoPanel.getAttribute();
                        //cmd[3] = ((Integer)eventsCP.getTable().getValueAt(eventsCP.getTable().getSelectedRow(), 0)).toString();
                        //cmd[4] = result;
                        commandPanel.addCommand(cmd);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You must select at least one phase from the list");
                }
                    
            }
        });
        
        JMenuItem takeReadingItem = new JMenuItem("Take Reading");
        takeReadingItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //PutHypoPanel puthypoPanel = new PutHypoPanel();
                if(phasesTable.getSelectedRow()>-1)
                {
                    Integer selectedRdid = (Integer) phasesTable.getValueAt(phasesTable.getSelectedRow(),15);
                //puthypoPanel.setMessage("Put Hypocenter: Hypid: "+selectedHypid.toString()+"to ");
                        String[] cmd = new String[5];
                        cmd[0] = "TakeReading";
                        cmd[1] = selectedRdid.toString();
                        //cmd[2] = chhypoPanel.getAttribute();
                        //cmd[3] = ((Integer)eventsCP.getTable().getValueAt(eventsCP.getTable().getSelectedRow(), 0)).toString();
                        //cmd[4] = result;
                        commandPanel.addCommand(cmd);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You must select at least one phase from the list");
                }
                    
            }
        });
        
        JMenuItem putPhaseItem = new JMenuItem("Put Phase");
        putPhaseItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(phasesTable.getSelectedRow()>-1)
                {
                Integer selectedPhid = (Integer) phasesTable.getValueAt(phasesTable.getSelectedRow(),14);
                //puthypoPanel.setMessage("Put Hypocenter: Hypid: "+selectedHypid.toString()+"to ");
                
                Integer selectedRdid = (Integer) phasesTable.getValueAt(phasesTable.getSelectedRow(),15);
                //check if there are more than one rdid in the list
                int i = 0;
                for(Phase p:ptvtModel.getPhasesList())
                {
                    if(p.getRdid().equals(selectedRdid))
                        i++;
                }
                
                Integer NewRdid = null;
                if(i>1)
                {
                    NewRdid = SeisDataDAO.getNextNewRdid();
                }
                
                String result = JOptionPane.showInputDialog(null, "Put Phase into another event which evid is:");
                if (result != null) {
                        //System.out.println(chhypoPanel.getAttribute());
                        //System.out.println(chhypoPanel.getValue());
                    try{
                        int resulttmp = Integer.valueOf(result);
                        
                        String[] cmd = new String[6];
                        cmd[0] = "PutPhase";
                        cmd[1] = selectedPhid.toString();
                        //cmd[2] = chhypoPanel.getAttribute();
                    
                        cmd[3] = ((Integer)eventsCP.getTable().getValueAt(eventsCP.getTable().getSelectedRow(), 0)).toString();
                        cmd[4] = result;
                        if(NewRdid!=null)
                        {
                            cmd[5] = NewRdid.toString();
                        }
                        commandPanel.addCommand(cmd);
                    }
                    catch(NumberFormatException ne)
                    {
                        JOptionPane.showMessageDialog(null, "Please input the correct Evid");
                    }
                }
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You must select at least one phase from the list");
                }
            }
        });
        
        JMenuItem deletePhaseItem = new JMenuItem("Delete Phase");
        deletePhaseItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //PutHypoPanel puthypoPanel = new PutHypoPanel();
                if(phasesTable.getSelectedRow()>-1)
                {
                    Integer selectedPhid = (Integer) phasesTable.getValueAt(phasesTable.getSelectedRow(),14);
                //puthypoPanel.setMessage("Put Hypocenter: Hypid: "+selectedHypid.toString()+"to ");
                        String[] cmd = new String[6];
                        cmd[0] = "DeletePhase";
                        cmd[1] = selectedPhid.toString();
                        //cmd[2] = chhypoPanel.getAttribute();
                        //cmd[3] = ((Integer)eventsCP.getTable().getValueAt(eventsCP.getTable().getSelectedRow(), 0)).toString();
                        //cmd[4] = result;
                        commandPanel.addCommand(cmd);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You must select at least one phase from the list");
                }
                    
            }
        });
        
        JMenuItem takePhaseItem = new JMenuItem("Take Phase");
        takePhaseItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //PutHypoPanel puthypoPanel = new PutHypoPanel();
                if(phasesTable.getSelectedRow()>-1)
                {
                    Integer selectedPhid = (Integer) phasesTable.getValueAt(phasesTable.getSelectedRow(),14);
                //puthypoPanel.setMessage("Put Hypocenter: Hypid: "+selectedHypid.toString()+"to ");
                        String[] cmd = new String[6];
                        cmd[0] = "TakePhase";
                        cmd[1] = selectedPhid.toString();
                        //cmd[2] = chhypoPanel.getAttribute();
                        //cmd[3] = ((Integer)eventsCP.getTable().getValueAt(eventsCP.getTable().getSelectedRow(), 0)).toString();
                        //cmd[4] = result;
                        commandPanel.addCommand(cmd);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You must select at least one phase from the list");
                }
                    
            }
        });
        
        phasePopup.add(putReadingItem);
        phasePopup.add(deleteReadingItem);
        phasePopup.add(takeReadingItem);
        phasePopup.add(changePhaseItem);
        phasePopup.add(putPhaseItem);
        phasePopup.add(deletePhaseItem);
        phasePopup.add(takePhaseItem);
        phasesTable.setComponentPopupMenu(phasePopup);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
