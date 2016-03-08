package uk.ac.isc.seisdata;

import java.util.ArrayList;

public class AssessedCommandList extends AbstractSeisData {

    private final ArrayList<AssessedCommand> assessedCommandList;

    public AssessedCommandList() {
        assessedCommandList = new ArrayList<AssessedCommand>();
    }

    public ArrayList<AssessedCommand> getAssessedCommandList() {
        return this.assessedCommandList;
    }
}
