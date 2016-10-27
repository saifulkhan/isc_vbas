package uk.ac.isc.seisdata;

import java.util.ArrayList;

public class CommandList extends AbstractSeisData {

    private final ArrayList<Command> commandList;

    public CommandList() {
        commandList = new ArrayList<Command>();
    }

    public ArrayList<Command> getCommandList() {
        return this.commandList;
    }
}
