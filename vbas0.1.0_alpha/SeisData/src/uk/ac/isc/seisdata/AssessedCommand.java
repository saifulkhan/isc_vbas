package uk.ac.isc.seisdata;



public class AssessedCommand extends AbstractSeisData {

    private Boolean select; // the selection flag of the assessed command
    private Integer evid;   // event id
    // populate from the database
    private String ids;     // command id(s)
    private String analyst; // analyst name
    private String report;    // the report to the pdf file (assessed report)
    
    public AssessedCommand() {
        
    }

    public AssessedCommand(Integer evid, String ids, String analyst, String report) {
        this.select = false;
        this.evid = evid;
        this.ids = ids;
        this.analyst = analyst;
        this.report = report;
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

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getAnalyst() {
        return analyst;
    }

    public void setAnalyst(String analyst) {
        this.analyst = analyst;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String path) {
        this.report = path;
    }

    

}
