
package uk.ac.isc.data;

import java.util.Date;
import java.util.Objects;

/**
 *
 * The class to keep the record of each assigned block
 * @author hui
 */
public class TaskBlock implements Comparable<TaskBlock> {
    
    private Integer blockID;
    
    private Date startDay;
    
    private Date endDay;
    
    private int regionID;
    
    private Integer analyst1ID;
    
    private Integer analyst2ID;
    
    private Integer analyst3ID;
    
    private String analyst1;
    
    private String analyst2;
    
    private String analyst3;
    
    private String reviewStatus;
    
    private int eventNumber;
        
    private int reviewedNumber;
    
    private int totalPhaseNumber;
    
    /*add three planned dates*/
    private Date pPlanStartDay;
    
    private Date pPlanEndDay;
    
    private Date sPlanStartDay;
    
    private Date sPlanEndDay;
    
    private Date fPlanStartDay;
    
    private Date fPlanEndDay;
    
    
    public TaskBlock(Integer bid)
    {
        this.blockID = bid;
    }
    
    public void setBlockID(Integer bid)
    {
        blockID = bid;
    }
    
    public Integer getBlockID()
    {
        return blockID;
    }
    
    public void setStartDay(Date sDay)
    {
        this.startDay = sDay;
    }
    
    public Date getStartDay()
    {
        return this.startDay;
    }
    
    public void setEndDay(Date eDay)
    {
        this.endDay = eDay;
    }
    
    public Date getEndDay()
    {
        return this.endDay;
    }
    
    public void setRegionID(int rid)
    {
        this.regionID = rid;
    }
    
    public int getRegionID()
    {
        return this.regionID;
    }
   
    public void setAnalyst1(String a1)
    {
        this.analyst1 = a1;
    }
    
    public String getAnalyst1()
    {
        return this.analyst1;
    }
        
    public void setAnalyst2(String a2)
    {
        this.analyst2 = a2;
    }
    
    public String getAnalyst2()
    {
        return this.analyst2;
    }
    
    public void setAnalyst3(String a3) {
        this.analyst3 = a3;
    }
    
    public String getAnalyst3()
    {
        return this.analyst3;
    }
   
    public void setAnalyst1ID(Integer id)
    {
        this.analyst1ID = id;
    }
    
    public Integer getAnalyst1ID()
    {
        return this.analyst1ID;
    }
    
    public void setAnalyst2ID(Integer id)
    {
        this.analyst2ID = id;
    }
    
    public Integer getAnalyst2ID()
    {
        return this.analyst2ID;
    }
    
    public void setAnalyst3ID(Integer id)
    {
        this.analyst3ID = id;
    }
    
    public Integer getAnalyst3ID()
    {
        return this.analyst3ID;
    }
    
    public void setStatus(String status)
    {
       this.reviewStatus = status; 
    }
    
    public String getStatus()
    {
        return this.reviewStatus;
    }
    
    public void setEventNumber(int evNum)
    {
        this.eventNumber = evNum;
    }
    
    public int getEventNumber()
    {
        return this.eventNumber;
    }
    
    public void setPhaseNumber(int phNum)
    {
        this.totalPhaseNumber = phNum;
    }
    
    public int getPhaseNumber()
    {
        return this.totalPhaseNumber;
    }
    
    public void setReviewedEventNumber(int reNum) {
        reviewedNumber = reNum;
    }
    
    public int getReviewedEventNumber()
    {
        if(!"Done".equals(this.getStatus()))
        {
            return this.reviewedNumber;
        }
        else
        {
            return this.eventNumber;
        }
    }
    
    /*primary review*/
    public void setPPlanStartDay(Date psDay)
    {
        this.pPlanStartDay = psDay;
    }
    
    public Date getPPlanStartDay()
    {
        return this.pPlanStartDay;
    }
    
    public void setPPlanEndDay(Date peDay)
    {
        this.pPlanEndDay = peDay;
    }
    
    public Date getPPlanEndDay()
    {
        return this.pPlanEndDay;
    }
    
    /*secondary review*/       
    public void setSPlanStartDay(Date ssDay)
    {
        this.sPlanStartDay = ssDay;
    }
    
    public Date getSPlanStartDay()
    {
        return this.sPlanStartDay;
    }
    
    public void setSPlanEndDay(Date seDay)
    {
        this.sPlanEndDay = seDay;
    }
    
    public Date getSPlanEndDay()
    {
        return this.sPlanEndDay;
    }
    
    /*final review*/    
    public void setFPlanStartDay(Date fsDay)
    {
        this.fPlanStartDay = fsDay;
    }
    
    public Date getFPlanStartDay()
    {
        return this.fPlanStartDay;
    }
    
    public void setFPlanEndDay(Date feDay)
    {
        this.fPlanEndDay = feDay;
    }
    
    public Date getFPlanEndDay()
    {
        return this.fPlanEndDay;
    }
    
    @Override
    public boolean equals(Object another)
    {
        if(!(another instanceof TaskBlock))
        {
            return false;
        }
        
        TaskBlock tb = (TaskBlock) another; 
        return (this.blockID.equals(tb.blockID));
        
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.blockID);
        return hash;
    }
    
    @Override
    public String toString() {
        
        return this.blockID.toString();
    }

    @Override
    public int compareTo(TaskBlock o) {
        
        int idComp = this.blockID.compareTo(o.blockID);
        
        return idComp;
        
    }
    
}
