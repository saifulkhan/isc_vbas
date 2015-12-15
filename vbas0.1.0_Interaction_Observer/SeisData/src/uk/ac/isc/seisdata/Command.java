 
package uk.ac.isc.seisdata;

 
public class Command extends AbstractSeisData implements Cloneable {
    
    private String cmdName; 

    public Command() {
    
    }
    
    public void setCmdName(String cmdName) {
        this.cmdName = cmdName;
    }

    public String getCmdName() {
        return cmdName;
    }
    
    
}
