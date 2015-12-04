
package uk.ac.isc.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author hui
 */
public class BlockTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Block ID.","Start Date","End Date","Region", "Analyst 1", "Analyst 2", "Analyst 3", "Status", "Events Num.", "Reviewed Number"};

    private final Class[] columns = new Class[]{Integer.class, String.class, String.class, Integer.class, String.class, String.class, String.class, String.class, Integer.class,
        Integer.class};
       
    private ArrayList<TaskBlock> blockArray; 
    
    private HashSet<TaskBlock> blockSet; 
    
    public BlockTableModel()
    {
        blockSet = new HashSet<>();
                
        //load the block table from the database
        SeisEventsDAO.loadBlocks(blockSet);
                        
        blockArray = new ArrayList<>(blockSet);
        
        Collections.sort(blockArray);
        
        //System.out.println(blockArray);
    }
    
    public ArrayList<TaskBlock> getTaskBlocks()
    {
        return this.blockArray;
    }
    
    @Override
    public int getRowCount() {
        return this.blockArray.size();
    }

    @Override
    public int getColumnCount() {
        return 10;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        Object retObject = null;
        
        if(columnIndex==0)
        {
            retObject = blockArray.get(rowIndex).getBlockID();
        }
        else if(columnIndex==1)
        {
            retObject = blockArray.get(rowIndex).getStartDay().toString();
        }
        else if(columnIndex==2)
        {
            retObject = blockArray.get(rowIndex).getEndDay().toString();
        }
        else if(columnIndex==3)
        {
            retObject = blockArray.get(rowIndex).getRegionID();
        }
        else if(columnIndex==4)
        {
            retObject = blockArray.get(rowIndex).getAnalyst1();
        }
        else if(columnIndex==5)
        {
            retObject = blockArray.get(rowIndex).getAnalyst2();
        }
        else if(columnIndex==6)
        {
            retObject = blockArray.get(rowIndex).getAnalyst3();
        }
        else if(columnIndex==7)
        {
            retObject = blockArray.get(rowIndex).getStatus();
        }
        else if(columnIndex==8)
        {
            retObject = blockArray.get(rowIndex).getEventNumber();
        }
        else
        {
            retObject = blockArray.get(rowIndex).getReviewedEventNumber();
        }
        
        return retObject;
    }

    @Override
    public String getColumnName(int col)
    {
        return columnNames[col];
    }
    
    @Override
    public Class getColumnClass(int c) {
        //return getValueAt(0, c).getClass();
        return columns[c];
    }
    
    public void reload() {
        
        blockSet = new HashSet<>();
        
        //load the block table from the database
        SeisEventsDAO.loadBlocks(blockSet);
        
        blockArray.clear();
                
        blockArray.addAll(blockSet);
        
        Collections.sort(blockArray);
        
        SeisEventsDAO.retrieveBlockEventNumber(blockArray);
        SeisEventsDAO.retrieveBlockReviewedEventNumber(blockArray);
        
        //this.fireTableDataChanged();
    }
    
}
