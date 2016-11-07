package uk.ac.isc.seisdatainterface;

import uk.ac.isc.seisdata.VBASLogger;
import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;
import java.util.Arrays;
import com.orsoncharts.util.json.parser.JSONParser;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import org.openide.util.Exceptions;
import uk.ac.isc.seisdata.CommandList;


public class FormulateCommand {

    private static final String[] COMMAND_TYPES = {
        "phaseedit",
        "hypocentreedit",
        "seiseventrelocate",
        "setprime",
        "seiseventbanish",
        "seiseventunbanish",
        "assess",
        "movehypocentre",
        "deletehypocentre",
        "createevent",
        "merge",
        "commit"
    };

    private static final String[] DATA_TYPES = {
        "seisevent",
        "hypocentre",
        "phase"
    };

    private static final String[] ATTRIBUTES = {
        "primehypocentre", /*setprime*/
        "phasetype", /*phaseedit*/
        "phase_fixed",
        "nondef",
        "timeshift",
        "deleteamp",
        "phasebreak",
        "putvalue",
        "depth", /*hypocentreedit*/
        "time",
        "lat",
        "lon",
        "fix_depth", /*seiseventrelocate*/
        "free_depth",
        "fix_depth_default",
        "fix_depth_median",
        "do_gridsearch",
        "commands", /*assess*/
        "report",
        "comment",
        "reason"
    };

    private final String commandType;
    private final String dataType;
    private final int id;           // id of the dataType
    private final String agency;    // Agency used in setprome, hypocentreedit, seiseventrelocate
    
    private  String analystRedableMergedCommand = "";


    public FormulateCommand(String commandType, String dataType, int id, String agency) {

        if (!Arrays.asList(COMMAND_TYPES).contains(commandType)) {
            String message = "commandType=" + commandType + ", Supported:" + Arrays.toString(COMMAND_TYPES);
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            VBASLogger.logSevere(message);
        }

        if (!Arrays.asList(DATA_TYPES).contains(dataType)) {
            String message = "dataType=" + dataType + ", Supported:" + Arrays.toString(DATA_TYPES);
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            VBASLogger.logSevere(message);
        }

        this.commandType = commandType;
        this.dataType = dataType;
        this.id = id;
        this.agency = agency;
    }

    /*
     **************************************************************************************
     * Related to System Command
     **************************************************************************************
     */
    private JSONArray sqlFunctionArray = new JSONArray();

    public void addSQLFunction(String functionName) {
        //Global.logDebug(functionName);
        JSONObject attrObj = new JSONObject();
        attrObj.put("name", functionName);
        sqlFunctionArray.add(attrObj);
    }

    String locatorArgStr = "";

    // no balnk space should be there at the end of the string
    public void addLocatorArg(String arg) {

        if (locatorArgStr.equals("")) {
            locatorArgStr += arg;
        } else {
            locatorArgStr += " " + arg;
        }
    }

    public JSONObject getSystemCommand() {
        JSONObject obj = new JSONObject();
        obj.put("commandType", commandType);
        obj.put("dataType", dataType);
        obj.put("id", id);

        if (sqlFunctionArray.size() > 0) {
            obj.put("sqlFunctionArray", sqlFunctionArray);
        } else {
            obj.put("sqlFunctionArray", null);
        }

        obj.put("locatorArgStr", locatorArgStr);

        return obj;
    }

    public Boolean isValidSystemCommand() {
        return !(sqlFunctionArray.size() <= 0 && locatorArgStr.equals(""));
    }

    // Used by assess - merge multiple system commands
    // Extract the SQL functions and Locator arguments 
    // Merge with the existing system command
    public  ArrayList<Integer> mergeSystemCommand(int[] selectedRows, JTable tableCommand) {

        ArrayList<Integer> commandIds = new ArrayList<Integer>();
        CommandList commandList = Global.getCommandList();

        JSONParser parser = new JSONParser();
        Object obj = null;

        int i = 0;
      
        for (int row : selectedRows) {
            commandIds.add((Integer) tableCommand.getValueAt(row, 0));
            String cmd = commandList.getCommandList().get(row).getSystemCommand();

            VBASLogger.logDebug("Append the systemCommand: " + cmd);

            try {
                obj = parser.parse(cmd);
            } catch (com.orsoncharts.util.json.parser.ParseException ex) {
                VBASLogger.logSevere("cmd=" + cmd + " is not a valid json format.");
            }

            if (isJSONArray(cmd)) { // array of System Command
                JSONArray arr = (JSONArray) obj;
                for (Object o : arr) {
                    JSONObject jObj = (JSONObject) o;
                    JSONArray array = (JSONArray) jObj.get("sqlFunctionArray");
                    if (array != null) {
                        sqlFunctionArray.addAll(array);     // append all 
                    }
                    addLocatorArg(jObj.get("locatorArgStr").toString());
                }

            } else if (isJSONObject(cmd)) {
                JSONObject jObj = (JSONObject) obj;
                JSONArray array = (JSONArray) jObj.get("sqlFunctionArray");
                if (array != null) {
                    sqlFunctionArray.addAll(array);     // append all 
                }
                addLocatorArg(jObj.get("locatorArgStr").toString());
            }

            analystRedableMergedCommand += "[" + (++i) + "] "
                    + createAnalystReadableCommand(commandList.getCommandList().get(row).getCommandProvenance());
        }
                
        this.addAttribute("analystRedableCommand", analystRedableMergedCommand, null);

        // Issue #101 : By default it should always be "do_gridsearch=0", unless otherwise it is defined as "do_gridsearch=1"
        if (!locatorArgStr.contains("do_gridsearch=1") && !locatorArgStr.contains("do_gridsearch=0")) {
            addLocatorArg("do_gridsearch=0");
        }
        
        VBASLogger.logDebug("\nMerge Complete.." 
                + "\ncommandIds = " + commandIds 
                + "\nsqlFunctionArray = " + sqlFunctionArray
                + "\nlocatorArgStr = " + locatorArgStr
                + "\nanalystRedableCommand = " + analystRedableMergedCommand 
                + "\n");
        
        return commandIds;
    }

    
    public String getAnalystRedableMergedCommand() {
        return analystRedableMergedCommand;
    }
    
    
    public ArrayList<String> getSQLFunctionArray() {
        ArrayList<String> fun = new ArrayList<String>();

        for (Object o : sqlFunctionArray) {
            JSONObject jObj = (JSONObject) o;
            String functionName = (String) jObj.get("name");
            //Global.logDebug("jObj=" + jObj.toString() +  ", name:"  + functionName);
            fun.add((String) jObj.get("name"));
        }

        return fun;
    }

    public String getLocatorArgStr() {
        // 
        return locatorArgStr;
    }

    /*
     **************************************************************************************
     * Related to Command Provenance
     **************************************************************************************
     */
    private JSONArray attrArray = new JSONArray();

    public void addAttribute(String attributeName, Object newValue, Object oldValue) {
        if (!Arrays.asList(ATTRIBUTES).contains(attributeName)) {
            VBASLogger.logSevere("attributeName=" + attributeName
                    + ", Supported:" + Arrays.toString(ATTRIBUTES));
        }

        JSONObject attrObj = new JSONObject();
        attrObj.put("name", attributeName);
        if (oldValue != null) {
            attrObj.put("oldValue", oldValue);
        }
        if (newValue != null) {
            attrObj.put("newValue", newValue);
        }

        attrArray.add(attrObj);
    }

    public JSONObject getCmdProvenance() {
        JSONObject obj = new JSONObject();
        obj.put("commandType", commandType);
        obj.put("dataType", dataType);
        obj.put("id", id);
        obj.put("agency", agency);

        if (attrArray.size() > 0) {
            obj.put("attributeArray", attrArray);
        } else {
            obj.put("attributeArray", null);
        }

        return obj;
    }

    // Analyst redable command is generated
    // Used as a static when the table is formulated
    public static String createAnalystReadableCommand(String cmd) {

        VBASLogger.logDebug("JSON Format: " + cmd);

        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(cmd);
        } catch (com.orsoncharts.util.json.parser.ParseException ex) {
            VBASLogger.logSevere("Not a valid JSON string: " + cmd);
        }

        String str = "";

        if (isJSONArray(cmd)) { // array of commands
            JSONArray commands = (JSONArray) obj;
            for (Object o : commands) {
                JSONObject command = (JSONObject) o;
                str += translate(command) + "; ";
            }

        } else if (isJSONObject(cmd)) {
            JSONObject command = (JSONObject) obj;
            str = translate(command);
        }

        VBASLogger.logDebug("Readable format: " + str);
        return str;
    }

    private static String translate(JSONObject command) {

        String commandType = command.get("commandType").toString();
        String str = "";

        switch (commandType) {
            case "setprime":
                str += "Setprime" + " ";
                str += command.get("agency").toString() + " ";
                break;

            case "movehypocentre":
                str += "MoveHypocentre" + " ";
                str += command.get("agency").toString() + " ";
                break;

            case "deletehypocentre":
                str += "DeleteHypocentre" + " ";
                str += command.get("agency").toString() + " ";
                break;

            case "createevent":
                str += "CreateEvent" + " ";
                str += command.get("agency").toString() + " ";
                break;

            case "seiseventbanish":
                str += "Banish" + " ";
                str += command.get("id").toString() + " ";
                break;

            case "seiseventunbanish":
                str += "Unbanish" + " ";
                str += command.get("id").toString() + " ";
                break;

            case "hypocentreedit":
                str += "Change" + " ";
                str += command.get("agency").toString() + " ";
                str += translateAttributes(command);
                break;

            case "phaseedit":
                str += "Change" + " ";
                str += command.get("id").toString() + " ";

                str += translateAttributes(command);
                break;

            case "seiseventrelocate":
                str += "Relocate" + " ";
                str += command.get("agency").toString() + " ";
                str += translateAttributes(command);
                break;

            case "merge":
                str += "Merge" + " ";
                str += translateAttributes(command);
                break;
            default:
                str += commandType + " ";
                str += command.get("id").toString() + " ";
                break;

        }
        return str;
    }

    private static String translateAttributes(JSONObject command) {

        String commandType = command.get("commandType").toString();
        String str = "";

        JSONArray attributes = (JSONArray) command.get("attributeArray");
        if (attributes != null) {
            for (Object o1 : attributes) {
                JSONObject attribute = (JSONObject) o1;

                switch (commandType) {
                    case "merge":
                        str += attribute.get("newValue") + " " + attribute.get("oldValue");
                        break;

                    default:
                        if (!attribute.get("name").toString().equals("reason")) {
                            str += (attribute.get("newValue") == null ? " " : attribute.get("name").toString()
                                    + "=" + attribute.get("newValue").toString()) + ", ";
                        }
                        break;
                }

            }
        }
        return str;
    }

    /*
     **************************************************************************************
     * other functions, chekc if a string is a valid JSON object {} or JSON array of objects [{}, {}]
     **************************************************************************************
     */
    private static Boolean isJSONObject(String str) {
        try {
            JSONParser parser = new JSONParser();
            Object json = parser.parse(str);
            if (json instanceof JSONObject) { // object
                return true;
            }
        } catch (com.orsoncharts.util.json.parser.ParseException ex) {
            Exceptions.printStackTrace(ex);
            VBASLogger.logSevere("str=" + str + " is not a valid json format.");
        }
        return false;
    }

    private static Boolean isJSONArray(String str) {
        try {
            JSONParser parser = new JSONParser();
            Object json = parser.parse(str);
            if (json instanceof JSONArray) { // you have an array
                return true;
            }
        } catch (com.orsoncharts.util.json.parser.ParseException ex) {
            Exceptions.printStackTrace(ex);
            VBASLogger.logSevere("str=" + str + " is not a valid json format.");
        }
        return false;
    }

}
