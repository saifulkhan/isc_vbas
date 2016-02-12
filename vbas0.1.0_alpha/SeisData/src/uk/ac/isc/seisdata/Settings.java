 
package uk.ac.isc.seisdata;

import java.util.Calendar;
import java.util.Map;

public final class Settings {
    
    private static final String assessUser;
    private static final String assessPassword;
    private static final String pgDatabase;
    private static final String pgHostAddr;
    private static final String assessDir;
    
    static {
        String osName = System.getProperty("os.name");
        if (osName.equals("Linux")) {
            Map<String, String> env = System.getenv();
            assessUser = env.get("ASSESS_USER");
            assessPassword = env.get("ASSESS_PW");
            pgDatabase = env.get("PGDATABASE");            
            pgHostAddr = env.get("PGHOSTADDR");
            
            Calendar c = Calendar.getInstance();
            assessDir = env.get("ASSESSDIR") 
                    + "/" + c.get(Calendar.YEAR)
                    + "/" + c.get(Calendar.MONTH);
        } else {
            // Saiful: Windows 10 laptop
            assessUser = " ";
            assessPassword = " ";
            pgDatabase = " ";            
            pgHostAddr = " ";
            assessDir = " ";
        }         
    }
    
    public Settings() {
        
    }
    

    public static String getAssessUser() {
        return assessUser;
    }

    public static String getAssessPassword() {
        return assessPassword;
    }

    public static String getPgDatabase() {
        return pgDatabase;
    }

    public static String getPgHostAddr() {
        return pgHostAddr;
    }

    public static String getAssessDir() {
        return assessDir;
    }
   
}
