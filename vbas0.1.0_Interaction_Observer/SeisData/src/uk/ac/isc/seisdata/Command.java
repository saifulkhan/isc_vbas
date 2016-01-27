package uk.ac.isc.seisdata;

import java.util.Date;

public class Command extends AbstractSeisData {

    private Boolean select; // the selection flag of the command
    private Integer evid;   // event id
    // populate from the database
    private Integer id;     // command id    
    private String analyst; // analyst name
    private String command; // the formulated command
    private String pass;    // the pass information (p-primary, s-secondary, i-?)
    private Date date;      // date when the action or command was generated
    private String status;  // ?
    private String type;    // ?
    

    public Command() {

    }

    public Command(Integer evid, Integer id, String analyst, String command, String pass, Date date, String status, String type) {
        this.select = false;
        this.evid = evid;
        this.id = id;
        this.analyst = analyst;
        this.command = command;
        this.pass = pass;
        this.date = date;
        this.status = status;
        this.type = type;
    }

    
    public Boolean getSelect() {
        return select;
    }
    public void setSelect(Boolean select) {
        this.select = select;
    }
    
    public Integer getEvid() {
        return evid;
    }
    public void setEvid(Integer evid) {
        this.evid = evid;
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getAnalyst() {
        return analyst;
    }
    public void setAnalyst(String analyst) {
        this.analyst = analyst;
    }

    public String getCommand() {
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }
    
    public String getPass() {
        return pass;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }
    
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    
     public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}