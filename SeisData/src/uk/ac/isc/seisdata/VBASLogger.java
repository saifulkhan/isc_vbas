/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.isc.seisdata;


import java.util.logging.Level;
import java.util.logging.Logger;

public class VBASLogger {
    
      /*
     *****************************************************************************************
     * TODO: in a separate class. 
     *****************************************************************************************
     */
    public static String debugAt() {
        String at = Thread.currentThread().getStackTrace()[2].getLineNumber() + ":"
                + Thread.currentThread().getStackTrace()[2].getClassName().
                substring(Thread.currentThread().getStackTrace()[2].getClassName().
                        lastIndexOf(".") + 1) + ":"
                + Thread.currentThread().getStackTrace()[2].getMethodName();

        return at;
    }

    public static void logSevere(String debugString) {
        String at = Thread.currentThread().getStackTrace()[2].getLineNumber() + ":"
                + Thread.currentThread().getStackTrace()[2].getClassName().
                substring(Thread.currentThread().getStackTrace()[2].getClassName().
                        lastIndexOf(".") + 1) + ":"
                + Thread.currentThread().getStackTrace()[2].getMethodName();

        Logger.getLogger(at).log(Level.SEVERE, debugString);
        //System.out.println("[" + at + "]->" + debugString);
    }

    public static void logDebug(String debugString) {
        String at = Thread.currentThread().getStackTrace()[2].getLineNumber() + ":"
                + Thread.currentThread().getStackTrace()[2].getClassName().
                substring(Thread.currentThread().getStackTrace()[2].getClassName().
                        lastIndexOf(".") + 1) + ":"
                + Thread.currentThread().getStackTrace()[2].getMethodName();
        Logger.getLogger(at).log(Level.INFO, debugString);
        //System.out.println("[" + at + "]->" + debugString);
    }
}
