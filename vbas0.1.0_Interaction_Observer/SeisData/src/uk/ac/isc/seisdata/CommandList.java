package uk.ac.isc.seisdata;

import java.util.ArrayList;

public class CommandList extends AbstractSeisData {

    private final ArrayList<Command> actionHistoryList;

    public CommandList() {
        actionHistoryList = new ArrayList<Command>();
    }

    public ArrayList<Command> getActionHistory() {
        return this.actionHistoryList;
    }
}
