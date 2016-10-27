package uk.ac.isc.seisdata;

import java.util.EventObject;

/**
 * The Change event that the seismicity data triggers
 */
public class SeisDataChangeEvent extends EventObject {

    private SeisData data;

    public SeisDataChangeEvent(Object source, SeisData data) {
        super(source);
        this.data = data;
    }

    public SeisData getData() {
        return this.data;
    }

    public void debug() {
        // Debug
        String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        System.out.println("In " + className + "." + methodName + "():" + lineNumber + ": ");
        System.out.println("Received the SiesDataChange event from: " + this.getData().getClass().getName());
        // Debug
    }
}
