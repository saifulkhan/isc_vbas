
package uk.ac.isc.seisdata;

import java.util.Date;


public class ActionHistory extends AbstractSeisData {

    private Integer evid;   // event id
    private String analyst; // analyst name
    private Date date;      // date when the action or command was generated
    private String command; // the formulated command
    private Boolean select; // the selection flag of the command
    private String status;  // the status (?)

    public ActionHistory() {
        
    }
    
    public Integer getEvid() {
        return evid;
    }

    public String getAnalyst() {
        return analyst;
    }

    public Date getDate() {
        return date;
    }

    public String getCommand() {
        return command;
    }

    public Boolean getSelect() {
        return select;
    }

    public String getStatus() {
        return status;
    }

    public void setEvid(Integer evid) {
        this.evid = evid;
    }

    public void setAnalyst(String analyst) {
        this.analyst = analyst;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setSelect(Boolean select) {
        this.select = select;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
