package uk.ac.isc.seisdata;

public class AssessedCommand extends AbstractSeisData {

    private final Integer id; // assess id

    private final Integer evid;   // event id
    // populate from the database
    private String ids;     // command id(s)
    private final String analyst; // analyst name
    private final String report;    // the report to the pdf file (assessed report)

    public AssessedCommand() {
        this.id = 0;
        this.evid = 0;
        this.ids = "";
        this.analyst = "";
        this.report = "";
    }

    public AssessedCommand(Integer id, Integer evid, String ids, String analyst, String report) {
        this.id = id;
        this.evid = evid;
        this.ids = ids;
        this.analyst = analyst;
        this.report = report;
    }

    public Integer getId() {
        return id;
    }

    public Integer getEvid() {
        return evid;
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

    public String getReport() {
        return report;
    }

}
