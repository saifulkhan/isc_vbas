package uk.ac.isc.seisdata;

import java.util.Date;

public class Command extends AbstractSeisData {

    // written by VBAS
    private Integer evid;       // event id
    private String commandStr;  // the formulated command in JSON
    private String functionsStr;   // the function (s) in JSON

    // Processed by & read from the database
    private Integer id;     // command id    
    private String analyst; // analyst name
    private String pass;    // the pass information (p-primary, s-secondary, i-?)
    private Date date;      // date when the action or command was generated
    private String status;  // ?
    private String type;    // ?

    public Command() {

    }

    public Command(Integer evid,
            String command,
            String functionsStr,
            Integer id,
            String analyst,
            String pass,
            Date date,
            String status,
            String type) {

        this.evid = evid;
        this.commandStr = command;
        this.functionsStr = functionsStr;
        this.id = id;
        this.analyst = analyst;
        this.pass = pass;
        this.date = date;
        this.status = status;
        this.type = type;
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

    public String getCommandStr() {
        return commandStr;
    }

    public void setCommandStr(String command) {
        this.commandStr = command;
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

    public String getFunctionsStr() {
        return functionsStr;
    }

    public void setFunctionStr(String functionsStr) {
        this.functionsStr = functionsStr;
    }

}
