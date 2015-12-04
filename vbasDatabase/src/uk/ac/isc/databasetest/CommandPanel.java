
package uk.ac.isc.databasetest;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * 
 * @author hui
 */
public class CommandPanel extends JPanel {
    
    EventsControlPanel cpRef;
    
    JTable commandTable;
    
    DefaultTableModel commandTableModel;
    
    JPanel buttonsPanel;
    
    boolean bflagSave, bflagLoad;
    
    public CommandPanel()
    {
        
        final String[] COLUMNS = {"Command Name", "Operation ID", "Attribute", "Old Value", "Change Value", "Other"};
        
        commandTableModel = new DefaultTableModel(COLUMNS,0);
        
        commandTable = new JTable(commandTableModel);
        
        commandTable.setFocusable(false);
        commandTable.setRowSelectionAllowed(true);
        
        commandTable.setFont(new Font("helvetica",Font.BOLD,18));
        
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        
        JButton btRemove = new JButton("Remove Selection");
        btRemove.addActionListener((ActionEvent e) -> {
            while(commandTable.getSelectedRowCount()>0)
            {
                commandTableModel.removeRow(commandTable.getSelectedRow());
            }
            commandTable.updateUI();
        });
        
        JButton btClearAll = new JButton("Clear All");
        btClearAll.addActionListener((ActionEvent e) -> {
            while(commandTableModel.getRowCount()>0)
            {
                commandTableModel.removeRow(0);
            }
            commandTable.updateUI();
        });
        
        JButton btExecute = new JButton("Run");

        btExecute.addActionListener((ActionEvent e) -> {
            for(int row = 0; row<commandTable.getRowCount();row++)
            {
                switch((String)commandTable.getValueAt(row, 0)) {
                    case "Merge": {
                        
                        bflagSave = SeisDataDAO.mergeEvent(Integer.valueOf((String)commandTable.getValueAt(row, 1)),Integer.valueOf((String)commandTable.getValueAt(row, 4)));
                        
                        bflagLoad = SeisDataDAO.retrieveHypos((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getHyposList().getHypocentres());
                        bflagLoad = SeisDataDAO.retrieveHyposMagnitude(cpRef.getHyposList().getHypocentres());
                        bflagLoad = SeisDataDAO.retrieveAllPhases((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getPhasesList().getPhases());
                        bflagLoad = SeisDataDAO.retrieveAllPhasesAmpMag(cpRef.getPhasesList().getPhases());
                        
                        cpRef.getHyposList().fireSeisDataChanged();
                        break;
                    }
                    case "RF": {
                        bflagSave = SeisDataDAO.fixPrime(Integer.valueOf((String)commandTable.getValueAt(row, 1)),(Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0));
                        
                        bflagLoad = SeisDataDAO.retrieveHypos((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getHyposList().getHypocentres());
                        bflagLoad = SeisDataDAO.retrieveHyposMagnitude(cpRef.getHyposList().getHypocentres());
                        cpRef.getHyposList().fireSeisDataChanged();
                        break;
                    }
                    case "ChangeHypo": {
                        bflagSave = SeisDataDAO.changeHypo(Integer.valueOf((String)commandTable.getValueAt(row, 1)), 
                                (String)commandTable.getValueAt(row, 2), (String)commandTable.getValueAt(row, 4));
                    
                        bflagLoad = SeisDataDAO.retrieveHypos((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getHyposList().getHypocentres());
                        bflagLoad = SeisDataDAO.retrieveHyposMagnitude(cpRef.getHyposList().getHypocentres());
                        cpRef.getHyposList().fireSeisDataChanged();
                        break;
                    }
                    case "PutHypo": {
                        bflagSave = SeisDataDAO.putHypo(Integer.valueOf((String)commandTable.getValueAt(row, 1)), 
                                 Integer.valueOf((String)commandTable.getValueAt(row, 3)), Integer.valueOf((String)commandTable.getValueAt(row, 4)));
                         
                        bflagLoad = SeisDataDAO.retrieveHypos((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getHyposList().getHypocentres());
                        bflagLoad = SeisDataDAO.retrieveHyposMagnitude(cpRef.getHyposList().getHypocentres());
                        
                        //need reload phases as well
                        //bflagLoad = SeisDataDAO.retrieveAllPhases((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getPhasesList().getPhases());
                        //bflagLoad = SeisDataDAO.retrieveAllPhasesAmpMag(cpRef.getPhasesList().getPhases());
                        cpRef.getHyposList().fireSeisDataChanged();
                        break;
                    }
                    case "DeleteHypo": {
                        bflagSave = SeisDataDAO.deleteHypo(Integer.valueOf((String)commandTable.getValueAt(row, 1)));
                        
                        bflagLoad = SeisDataDAO.retrieveHypos((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getHyposList().getHypocentres());
                        bflagLoad = SeisDataDAO.retrieveHyposMagnitude(cpRef.getHyposList().getHypocentres());
                        
                        //need reload phases as well
                        //bflagLoad = SeisDataDAO.retrieveAllPhases((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getPhasesList().getPhases());
                        //bflagLoad = SeisDataDAO.retrieveAllPhasesAmpMag(cpRef.getPhasesList().getPhases());
                        cpRef.getHyposList().fireSeisDataChanged();
                        break;
                    }
                    case "CreateEvent": {
                        Integer evid2 = SeisDataDAO.getNextNewEvid();
        
                        bflagSave = SeisDataDAO.createEvent(Integer.valueOf((String)commandTable.getValueAt(row, 1)),evid2);
                        
                        bflagLoad = SeisDataDAO.retrieveHypos((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getHyposList().getHypocentres());
                        bflagLoad = SeisDataDAO.retrieveHyposMagnitude(cpRef.getHyposList().getHypocentres());
                        
                        //need reload phases as well
                        //bflagLoad = SeisDataDAO.retrieveAllPhases((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getPhasesList().getPhases());
                        //bflagLoad = SeisDataDAO.retrieveAllPhasesAmpMag(cpRef.getPhasesList().getPhases());
                        cpRef.getHyposList().fireSeisDataChanged();
                        break;
                    }
                    case "ChangePhase": {  //phase level from here
                        
                        bflagSave = SeisDataDAO.changePhase(Integer.valueOf((String)commandTable.getValueAt(row, 1)), 
                                (String)commandTable.getValueAt(row, 2), (String)commandTable.getValueAt(row, 4));
                    
                        //need reload phases as well
                        bflagLoad = SeisDataDAO.retrieveAllPhases((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getPhasesList().getPhases());
                        bflagLoad = SeisDataDAO.retrieveAllPhasesAmpMag(cpRef.getPhasesList().getPhases());
                        cpRef.getPhasesList().fireSeisDataChanged();
                        break;
                    }
                    case "PutReading": {  //phase level from here
                        
                        bflagSave = SeisDataDAO.putReading(Integer.valueOf((String)commandTable.getValueAt(row, 1)), 
                                Integer.valueOf((String)commandTable.getValueAt(row, 3)), Integer.valueOf((String)commandTable.getValueAt(row, 4)));
                    
                        //need reload phases as well
                        bflagLoad = SeisDataDAO.retrieveAllPhases((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getPhasesList().getPhases());
                        bflagLoad = SeisDataDAO.retrieveAllPhasesAmpMag(cpRef.getPhasesList().getPhases());
                        cpRef.getPhasesList().fireSeisDataChanged();
                        break;
                    }
                    case "DeleteReading": {  //phase level from here

                        bflagSave = SeisDataDAO.deleteReading(Integer.valueOf((String)commandTable.getValueAt(row, 1)));
                             
                        //need reload phases as well
                        bflagLoad = SeisDataDAO.retrieveAllPhases((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getPhasesList().getPhases());
                        bflagLoad = SeisDataDAO.retrieveAllPhasesAmpMag(cpRef.getPhasesList().getPhases());
                        cpRef.getPhasesList().fireSeisDataChanged();
                        break;
                    }
                    case "TakeReading": {  //phase level from here

                        bflagSave = SeisDataDAO.takeReading(Integer.valueOf((String)commandTable.getValueAt(row, 1)));
                             
                        //need reload phases as well
                        bflagLoad = SeisDataDAO.retrieveAllPhases((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getPhasesList().getPhases());
                        bflagLoad = SeisDataDAO.retrieveAllPhasesAmpMag(cpRef.getPhasesList().getPhases());
                        cpRef.getPhasesList().fireSeisDataChanged();
                        break;
                    }
                    case "PutPhase": {  //phase level from here
                        
                        Integer newRDID = null;
                        if(commandTable.getValueAt(row, 5)!=null)
                        {
                            newRDID = Integer.valueOf((String)commandTable.getValueAt(row, 4));
                        }
        
                        bflagSave = SeisDataDAO.putPhase(Integer.valueOf((String)commandTable.getValueAt(row, 1)), 
                                Integer.valueOf((String)commandTable.getValueAt(row, 3)), Integer.valueOf((String)commandTable.getValueAt(row, 4)), newRDID);
                    
                        //need reload phases as well
                        bflagLoad = SeisDataDAO.retrieveAllPhases((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getPhasesList().getPhases());
                        bflagLoad = SeisDataDAO.retrieveAllPhasesAmpMag(cpRef.getPhasesList().getPhases());
                        cpRef.getPhasesList().fireSeisDataChanged();
                        break;
                    }
                    case "DeletePhase": {  //phase level from here

                        bflagSave = SeisDataDAO.deletePhase(Integer.valueOf((String)commandTable.getValueAt(row, 1)));
                             
                        //need reload phases as well
                        bflagLoad = SeisDataDAO.retrieveAllPhases((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getPhasesList().getPhases());
                        bflagLoad = SeisDataDAO.retrieveAllPhasesAmpMag(cpRef.getPhasesList().getPhases());
                        cpRef.getPhasesList().fireSeisDataChanged();
                        break;
                    }
                    case "TakePhase": {  //phase level from here

                        bflagSave = SeisDataDAO.takePhase(Integer.valueOf((String)commandTable.getValueAt(row, 1)));
                             
                        //need reload phases as well
                        bflagLoad = SeisDataDAO.retrieveAllPhases((Integer)cpRef.getTable().getValueAt(cpRef.getTable().getSelectedRow(), 0),cpRef.getPhasesList().getPhases());
                        bflagLoad = SeisDataDAO.retrieveAllPhasesAmpMag(cpRef.getPhasesList().getPhases());
                        cpRef.getPhasesList().fireSeisDataChanged();
                        break;
                    }
                    default: System.out.println("Not implemented yet");
                    break;
                }
            }
        });
        
        buttonsPanel.add(btRemove);
        buttonsPanel.add(btClearAll);
        buttonsPanel.add(btExecute);
        
        this.setLayout(new BorderLayout());
        this.add(commandTable.getTableHeader(),BorderLayout.NORTH);
        this.add(commandTable, BorderLayout.CENTER);
        this.add(buttonsPanel,BorderLayout.SOUTH);
        
    }
    
    public void addCommand(String[] cmd)
    {
        commandTableModel.addRow(cmd);
        commandTable.updateUI();
    }

    void setCPRef(EventsControlPanel eventsCP) {
        cpRef = eventsCP;
    }
}
