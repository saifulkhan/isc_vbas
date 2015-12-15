package uk.ac.isc.seisdata;

import java.util.ArrayList;

public class ActionHistoryList extends AbstractSeisData {

    private final ArrayList<ActionHistory> actionHistoryList;

    public ActionHistoryList() {
        actionHistoryList = new ArrayList<ActionHistory>();
    }

    public ArrayList<ActionHistory> getActionHistory() {
        return this.actionHistoryList;
    }
}
