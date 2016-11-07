package uk.ac.isc.seisdatainterface;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import uk.ac.isc.seisdata.AssessedCommand;
import uk.ac.isc.seisdata.Command;
import uk.ac.isc.seisdata.Duplicates;
import uk.ac.isc.seisdata.HistoricEvent;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.Phase;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.Station;
import uk.ac.isc.seisdata.TaskBlock;
import uk.ac.isc.seisdata.VBASLogger;

/**
 * This is the database access object which provides functions to read and write
 * ISC database
 */
public class SeisDataDAO {

    protected static final Logger logger = Logger.getLogger(SeisDataDAO.class.getName());

    // Loading pgUser name, pgPassword and scheme from system environment
    protected static String url;
    protected static String pgUser;
    protected static String pgPassword;
    protected static String sysUser;
    // Assess schema
    protected static String assessUser;
    protected static String assessPassword;
    // connections
    private static Connection pgCon = null;
    private static Connection assessCon = null;
    // other
    private static String assessDir;
    private static String commitDir;
    private static String assessUrl;
    private static String commitUrl;
    // locator binary
    private static String locatorBin;

    public static DecimalFormat df = new DecimalFormat(".0");

    static long totalLocatormessageLoadingTime = 0;
    static long totalDuplicatesLoadingTime = 0;

    static {

        Map<String, String> env = System.getenv();
        url = "jdbc:postgresql://"
                + env.get("PGHOSTADDR") + ":"
                + env.get("PGPORT") + "/"
                + env.get("PGDATABASE");
        pgUser = env.get("PGUSER");
        pgPassword = env.get("PGPASSWORD");
        sysUser = env.get("USER");
        assessUser = env.get("ASSESS_USER");
        assessPassword = env.get("ASSESS_PW");

        assessDir = env.get("ASSESSDIR");
        commitDir = env.get("COMMITDIR");
        assessUrl = env.get("ASSESS_URL");
        commitUrl = env.get("COMMIT_URL");
        locatorBin = env.get("OPSBIN") + File.separator + "iscloc";

        VBASLogger.logDebug("url=" + url
                + ", user=" + pgUser
                + ", password=" + pgPassword
                + ", sysUser=" + sysUser
                + ", assessUser=" + assessUser
                + ", assessPassword=" + assessPassword);

        try {
            pgCon = DriverManager.getConnection(url, pgUser, pgPassword);
            assessCon = DriverManager.getConnection(url, assessUser, assessPassword);

        } catch (SQLException ex) {
            String message = ex.toString() + "\n\n"
                    + VBASLogger.debugAt()
                    + "Database connection failure. report to system admin.";
            JOptionPane.showMessageDialog(null, message, "ERROR", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, message);
        }

    }

    public SeisDataDAO() {
    }

    public static String getPgUser() {
        return pgUser;
    }

    public static String getPgPassword() {
        return pgPassword;
    }

    public static String getAssessUser() {
        return assessUser;
    }

    public static String getAssessPassword() {
        return assessPassword;
    }

    public static String getAssessDir() {
        return assessDir;
    }

    public static String getCommitDir() {
        return commitDir;
    }

    public static String getAssessUrl() {
        return assessUrl;
    }

    public static String getCommitUrl() {
        return commitUrl;
    }

    public static String getLocatorBin() {
        return locatorBin;
    }

    /**
     * retrieve all the events in a pgUser's schema
     *
     * @param seisEvents for saving the events list
     * @return success flag
     */
    public static boolean retrieveAllEvents(ArrayList<SeisEvent> seisEvents) {

        long startTime = System.nanoTime();

        seisEvents.clear();

        Statement st = null;
        ResultSet rs = null;
        String query = null;

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        query = "SELECT e.evid, h.author, h.day, h.lat, h.lon, h.depth, e.etype, get_default_depth_grid (h.lat, h.lon), e.banished,"
                + " ( SELECT MAX(ea.finish) FROM event_allocation ea WHERE ea.evid = e.evid )"
                + "    FROM event e, hypocenter h"
                + "    WHERE e.prime_hyp = h.hypid"
                + "     AND h.isc_evid = e.evid"
                + "     AND e.ready IS NOT NULL"
                + "     AND h.hypid = h.pref_hypid"
                + " ORDER BY h.day ASC;";

        VBASLogger.logDebug("Executing Query: " + query);

        try {
            st = pgCon.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {

                int evid = rs.getInt(1);
                Double defaultDepthGrid = (rs.getObject(8) == null) ? null : rs.getDouble(8);
                Date finishDate = (rs.getObject(10) == null) ? null : rs.getDate(10);
                Boolean isBanish = (rs.getObject(9) == null) ? false : true;

                SeisEvent tmp = new SeisEvent(evid,
                        rs.getString(7),
                        defaultDepthGrid,
                        SeisDataDAO.getLocatorMessage(evid),
                        SeisDataDAO.getNearbyEvents(evid),
                        isBanish,
                        finishDate,
                        SeisDataDAO.getDuplicates(evid));

                Date dd = null;
                try {
                    dd = df.parse(rs.getString(3));
                } catch (ParseException e) {
                    return false;
                }

                Hypocentre ph = new Hypocentre(rs.getString(2), dd, rs.getDouble(4), rs.getDouble(5), rs.getInt(6));
                tmp.setPrimeHypo(ph);
                seisEvents.add(tmp);
            }

        } catch (SQLException ex) {
            String message = ex.toString() + "\n\n"
                    + VBASLogger.debugAt()
                    + "Query= " + query
                    + "\nFailed to run."
                    + "\nSee the error log file for more information. ";
            JOptionPane.showMessageDialog(null,
                    message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, message);
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                String message = ex.toString() + "\n\n"
                        + VBASLogger.debugAt()
                        + "Query= " + query
                        + "\nFailed to run."
                        + "\nSee the error log file for more information. ";
                JOptionPane.showMessageDialog(null,
                        message,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                logger.log(Level.SEVERE, message);
                return false;
            }
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        VBASLogger.logDebug("Duration to load all the events = " + duration);
        VBASLogger.logDebug("Total duration to load all the messages = " + totalLocatormessageLoadingTime);
        VBASLogger.logDebug("Total duration to load all the duplicates = " + totalDuplicatesLoadingTime);

        return true;
    }

    private static String getLocatorMessage(int evid) {

        long startTime1 = 0, endTime1 = 0, startTime2 = 0, endTime2 = 0, startTime3 = 0, endTime3 = 0;
        startTime1 = System.nanoTime();
        startTime2 = System.nanoTime();

        String msg = "";
        String tempMsg = "";
        Statement st = null;
        ResultSet rs = null;
        String query = "SELECT comment FROM iscloc_comments WHERE evid = "
                + evid
                + " ORDER BY commno; ";

        //VBASLogger.logDebug("\nEvid = " + evid);
        try {
            st = pgCon.createStatement();
            rs = st.executeQuery(query);

            endTime1 = System.nanoTime();
            startTime3 = System.nanoTime();
            while (rs.next()) {
                String locMsg = rs.getString(1);
                tempMsg += locMsg + "\n";

                if (locMsg.contains("ABORT")
                        || locMsg.contains("WARN")
                        || locMsg.contains("CAUTION")
                        || locMsg.contains("FAILURE")
                        || locMsg.contains("depth fixed")
                        || locMsg.contains("free-depth")) {
                    msg += locMsg + "\n";

                } else if (locMsg.contains("Depth-phase depth resolution:")) {
                    if (Integer.valueOf(locMsg.substring(locMsg.lastIndexOf(" ") + 1)) > 0) {
                        msg += locMsg + "\n";
                        try {
                            int count = 0;
                            while (rs.next() && ++count <= 2) {
                                msg += rs.getString(1) + "\n";
                                tempMsg += rs.getString(1) + "\n";
                            }
                        } catch (SQLException ex0) {
                            String message = VBASLogger.debugAt() + ex0.toString();
                            logger.log(Level.SEVERE, message);
                        }
                    }

                } else if (locMsg.contains("Depth resolution:")) {
                    if (Integer.valueOf(locMsg.substring(locMsg.lastIndexOf(" ") + 1)) > 0) {
                        msg += locMsg + "\n";
                        try {
                            int count = 0;
                            while (rs.next() && ++count <= 3) {
                                msg += rs.getString(1) + "\n";
                                tempMsg += rs.getString(1) + "\n";
                            }
                        } catch (SQLException ex0) {
                            String message = VBASLogger.debugAt() + ex0.toString();
                            logger.log(Level.SEVERE, message);
                        }
                    }
                }

            }

            /*endTime3 = System.nanoTime();*/
        } catch (SQLException ex) {
            String message = VBASLogger.debugAt() + ex.toString();
            logger.log(Level.SEVERE, message);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                String message = VBASLogger.debugAt() + ex.toString();
                logger.log(Level.SEVERE, message);
            }
        }

        // VBASLogger.logDebug("Entire locator message:\n" + tempMsg + "\nFiltered locator message:\n" + msg);
        endTime2 = System.nanoTime();
        /*VBASLogger.logDebug("Evid = " + evid
                + ", T_SQL =" + (endTime1 - startTime1) / 1000000
                + ", T_Parsing = " + (endTime3 - startTime3) / 1000000
                + ", T_Total = " + (endTime2 - startTime2) / 1000000);*/
        totalLocatormessageLoadingTime += (endTime2 - startTime2) / 1000000;

        return msg;
    }

    private static String getNearbyEvents(int evid) {
        String nearbyEvents = "";
        Statement st = null;
        ResultSet rs = null;
        String query = //"SELECT near FROM near(" + evid + "); ";
                "SELECT e.evid,h.author,h.day,h.lat,h.lon,h.depth,(SELECT MAX(n.magnitude) FROM netmag n WHERE n.hypid = h.hypid )"
                + "  FROM hypocenter h, event e"
                + " WHERE e.evid IN ( SELECT * FROM NEAR(" + evid + ") )"
                + "   AND h.hypid = e.prime_hyp"
                + " ORDER BY h.day;";

        try {
            st = pgCon.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                int nEvid = rs.getInt("evid");
                String nAuthor = rs.getString("author");
                nearbyEvents += (nearbyEvents == "")
                        ? (nEvid + ":" + nAuthor)
                        : (" " + nEvid + ":" + nAuthor);
            }
            /*endTime3 = System.nanoTime();*/
        } catch (SQLException ex) {
            String message = VBASLogger.debugAt() + ex.toString();
            logger.log(Level.SEVERE, message);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                String message = VBASLogger.debugAt() + ex.toString();
                logger.log(Level.SEVERE, message);
            }
        }

        return nearbyEvents;
    }

    private static ArrayList<Duplicates> getDuplicates(int evid) {
        long startTime = System.nanoTime();

        Statement st = null;
        ResultSet rs = null;
        String query = "SELECT * FROM DUPLICATES(" + evid + ");";
        ArrayList<Duplicates> duplicatesList = new ArrayList<>();
        
        //VBASLogger.logDebug("Evid = " + evid);
        try {
            st = pgCon.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {

               

                String ownsta = rs.getString("ownsta");
                String ownphase = rs.getString("ownphase");
                String ownresidual = rs.getDouble("ownresidual") > 0
                        ? "+" + df.format(rs.getDouble("ownresidual"))
                        : df.format(rs.getDouble("ownresidual"));

                int owndelta = rs.getInt("owndelta");
                String dupphase = rs.getString("dupphase");
                String dupresidual = rs.getDouble("dupresidual") > 0
                        ? "+" + df.format(rs.getDouble("dupresidual"))
                        : df.format(rs.getDouble("dupresidual"));

                int dupdelta = rs.getInt("dupdelta");
                int dupevid = rs.getInt("dupevid");
                String dupready = rs.getString("dupready");

                Duplicates duplicates = new Duplicates(
                        ownsta,
                        ownphase,
                        ownresidual,
                        owndelta,
                        dupphase,
                        dupresidual,
                        dupdelta,
                        dupevid,
                        dupready
                );

                duplicatesList.add(duplicates);
                //VBASLogger.logDebug(duplicates.toString());
            }

        } catch (SQLException ex) {
            String message = VBASLogger.debugAt() + ex.toString();
            logger.log(Level.SEVERE, message);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                String message = VBASLogger.debugAt() + ex.toString();
                logger.log(Level.SEVERE, message);
            }
        }

        totalDuplicatesLoadingTime += (System.nanoTime() - startTime) / 1000000;
         //VBASLogger.logDebug("Evid = " + evid + ", t = " + (System.nanoTime() - startTime) / 1000000);
                
        return duplicatesList;
    }

    /**
     * retrieve events' magnitude, actually it retrieves the magnitudes of
     * primehypo
     *
     * @param evList
     * @return all the events filling with magnitude
     */
    public static boolean retrieveEventsMagnitude(ArrayList<SeisEvent> evList) {
        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            //con = DriverManager.getConnection(url, pgUser, pgPassword);
            st = pgCon.createStatement();

            String query = "SELECT e.evid, n.magtype, n.magnitude"
                    + " FROM  event e, netmag n"
                    + " WHERE e.author='ISC'"
                    + " AND e.ready IS NOT NULL"
                    + " AND n.hypid = e.prime_hyp"
                    + " AND n.deprecated is NULL"
                    + " AND n.magid = ( SELECT ni.magid"
                    + " FROM netmag ni"
                    + " WHERE ni.hypid = n.hypid"
                    + " AND ni.deprecated is NULL"
                    + " ORDER BY ( CASE WHEN n.magtype = 'MS' THEN 10"
                    + " ELSE 1"
                    + " END ) DESC LIMIT 1);";

            rs = st.executeQuery(query);

            //for quick retrieval
            HashMap<Integer, Double> ht = new HashMap<Integer, Double>();

            while (rs.next()) {
                //check this later, do I need add the event magnitude into hypo?
                //ev.getPrimeHypo().addMagnitude(rs.getString(1),rs.getDouble(2));
                ht.put(rs.getInt(1), rs.getDouble(3));
            }

            for (SeisEvent ev : evList) {
                //int a = rs.getInt(1);
                ev.setMagnitude(ht.get(ev.getEvid()));

            }

            rs.close();

        } catch (SQLException ex) {
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
                return false;
            }
        }

        return true;
    }

    /**
     * fill events' location in the control panel
     *
     * @param evList list with no region names
     * @return all the events filling with their region names
     */
    public static boolean retrieveAllRegionName(ArrayList<SeisEvent> evList) {
        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            //con = DriverManager.getConnection(url, pgUser, pgPassword);
            st = pgCon.createStatement();

            String query = "SELECT r.gr_short, r.gr_number, r.sr_number, e.evid "
                    + " FROM  region r, event e, hypocenter h"
                    + " WHERE e.ready IS NOT NULL AND e.evid = h.isc_evid AND h.hypid = e.prime_hyp"
                    + " AND r.gr_number = COALESCE(h.grn,grn_ll(h.lat,h.lon));";

            rs = st.executeQuery(query);

            HashMap<Integer, String> hm1 = new HashMap<Integer, String>();
            HashMap<Integer, Integer> hm2 = new HashMap<Integer, Integer>();
            HashMap<Integer, Integer> hm3 = new HashMap<Integer, Integer>();

            while (rs.next()) {
                hm1.put(rs.getInt(4), rs.getString(1));
                hm2.put(rs.getInt(4), rs.getInt(2));
                hm3.put(rs.getInt(4), rs.getInt(3));
            }

            for (SeisEvent ev : evList) {
                ev.setLocation(hm1.get(ev.getEvid()));
                ev.setGrn(hm2.get(ev.getEvid()));
                ev.setSrn(hm3.get(ev.getEvid()));
            }

            rs.close();
        } catch (SQLException ex) {
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
                return false;
            }
        }

        return true;
    }

    /**
     * for getting new evid so that a new event can be created
     *
     * @return
     */
    static public Integer getNextNewEvid(Boolean isAssess) {
        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;

        Integer retEvid = null;
        try {

            if (isAssess) {
                st = assessCon.createStatement();
            } else {
                st = pgCon.createStatement();
            }

            //con = DriverManager.getConnection(url, pgUser, pgPassword);
            String query = "SELECT NEXTVAL('isc.evid');";
            rs = st.executeQuery(query);
            while (rs.next()) {
                retEvid = rs.getInt(1);
            }
        } catch (SQLException ex) {
            //return null;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
                //return false;
            }
        }

        return retEvid;

    }

    /**
     * Here is for retrieving the phase number for each event, difference with
     * the previous one is that this one can retrieve the phase nmber
     *
     * @param evList
     * @return
     */
    public static boolean retrieveAllPhaseNumber(ArrayList<SeisEvent> evList) {
        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            //con = DriverManager.getConnection(url, pgUser, pgPassword);
            st = pgCon.createStatement();

            String query = "SELECT h.nass, e.evid\n"
                    + "     FROM hypocenter h, event e\n"
                    + "    WHERE h.author = 'ISC'\n"
                    + "      AND h.isc_evid = e.evid\n"
                    + "      AND h.hypid = e.prime_hyp\n"
                    + "      AND h.deprecated IS NULL\n"
                    + "      AND e.banished IS NULL\n"
                    + "      AND e.ready IS NOT NULL\n"
                    + "    UNION\n"
                    + "   SELECT COUNT(*), e.evid\n"
                    + "     FROM hypocenter h, event e, association a\n"
                    + "    WHERE h.author != 'ISC'\n"
                    + "      AND h.isc_evid = e.evid\n"
                    + "      AND h.hypid = e.prime_hyp\n"
                    + "      AND h.deprecated IS NULL\n"
                    + "      AND e.banished IS NULL\n"
                    + "      AND e.ready IS NOT NULL\n"
                    + "      AND h.hypid = a.hypid\n"
                    + "      AND a.author = 'ISC'\n"
                    + "GROUP BY e.evid;";

            rs = st.executeQuery(query);

            while (rs.next()) {
                for (SeisEvent ev : evList) {
                    if (rs.getInt(2) == ev.getEvid()) {
                        ev.setPhaseNumber(rs.getInt(1));
                    }
                }
            }
            rs.close();

        } catch (SQLException ex) {
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }

            } catch (SQLException ex) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get all the hypocentres
     */
    public static boolean retrieveHypos(Integer evid,
            ArrayList<Hypocentre> HypoList,
            Boolean isAssess) {

        Statement st = null;
        ResultSet rs = null;

        HypoList.clear();

        try {
            if (isAssess) {
                st = assessCon.createStatement();
            } else {
                st = pgCon.createStatement();
            }

            String query
                    = "SELECT h.author, h.day, h.lat, h.lon, h.depth, h.prime, h.hypid, x.sdepth, h.epifix, x.stime, "
                    + "x.strike, x.smajax, x.sminax, h.nass, h.ndef, h.nsta, h.ndefsta, h.msec, x.sdobs, h.etype, h.depfix"
                    + " FROM hypocenter h LEFT OUTER JOIN hypoc_err x ON x.hypid = h.hypid"
                    + " WHERE h.deprecated is NULL AND h.hypid = h.pref_hypid AND h.isc_evid = "
                    + evid
                    + " ORDER BY h.prime DESC, h.author";

            rs = st.executeQuery(query);

            while (rs.next()) {

                Hypocentre tmp = new Hypocentre();

                tmp.setEvid(evid);
                tmp.setAgency(rs.getString(1));

                Date dd = null;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    dd = df.parse(rs.getString(2));
                } catch (ParseException e) {
                    return false;
                }
                tmp.setOrigTime(dd);

                tmp.setLat(rs.getDouble(3));
                tmp.setLon(rs.getDouble(4));
                tmp.setDepth(rs.getInt(5));
                tmp.setHypid(rs.getInt(7));

                if (rs.getString(6) != null) {
                    tmp.setIsPrime(true);
                } else {
                    tmp.setIsPrime(false);
                }

                if (rs.getObject(8) != null) {
                    tmp.setErrDepth(rs.getDouble(8));
                }

                if (rs.getString(9) != null) {
                    tmp.setIsFixed(true);
                } else {
                    tmp.setIsFixed(false);
                }

                tmp.setStime(rs.getDouble(10));
                tmp.setStrike(rs.getDouble(11));
                tmp.setSmajax(rs.getDouble(12));
                tmp.setSminax(rs.getDouble(13));

                tmp.setNumPhases(rs.getInt(14));
                tmp.setNumDefPhases(rs.getInt(15));
                tmp.setNumStations(rs.getInt(16));
                tmp.setNumDefStations(rs.getInt(17));
                tmp.setMsec(rs.getInt(18));
                if (rs.getObject(19) != null) {
                    tmp.setSdobs(rs.getDouble(19));
                }

                if (rs.getObject(20) != null) {
                    tmp.seteType(rs.getString(20));
                }

                if (rs.getObject(21) != null) {
                    tmp.setDepthFix(rs.getString(21));
                }

                HypoList.add(tmp);

            }

            rs.close();

        } catch (SQLException ex) {
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
                return false;
            }
        }
        return true;

    }

    /**
     * Retrieve hypocentre magnitudes list
     */
    public static boolean retrieveHyposMagnitude(ArrayList<Hypocentre> HypoList, Boolean isAssess) {

        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;

        Iterator<Hypocentre> iter = HypoList.iterator();
        try {
            if (isAssess) {
                st = assessCon.createStatement();
            } else {
                st = pgCon.createStatement();
            }

            while (iter.hasNext()) {
                Hypocentre currentHypo = iter.next();
                Integer hypid = currentHypo.getHypid();

                String query = "SELECT n.magtype, n.magnitude"
                        + " FROM netmag n"
                        + " WHERE n.deprecated is NULL AND n.magtype is NOT NULL AND n.hypid = "
                        + hypid + " ORDER BY ( CASE WHEN n.magtype = 'mb' THEN 1 WHEN magtype = 'MS' THEN 2 WHEN magtype = 'MW' THEN 3 ELSE 4 END) ASC";

                rs = st.executeQuery(query);

                while (rs.next()) {

                    currentHypo.addMagnitude(rs.getString(1), rs.getDouble(2));
                }
                rs.close();
            }

        } catch (SQLException ex) {
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
                return false;
            }
        }

        return true;
    }

    /**
     * Return the historical list
     *
     * @param histEvents
     * @param latN
     * @param latS
     * @param lonW
     * @param lonE
     * @return
     */
    public static boolean retrieveHistEvents(ArrayList<HistoricEvent> histEvents, double latN, double latS, double lonW, double lonE) {
        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;

        histEvents.clear();

        String query;
        if (lonW > -180 && lonE < 180) {
            query = "SELECT evid, depth, lat, lon FROM historical_seismicity WHERE lat >= " + String.valueOf(latS)
                    + " AND lat <= " + String.valueOf(latN) + " AND lon >= " + String.valueOf(lonW) + " AND lon <= "
                    + String.valueOf(lonE) + " ORDER BY depth ASC;";
        } else if (lonW < -180) {
            lonW += 360;
            query = "SELECT evid, depth, lat, lon FROM historical_seismicity WHERE lat >= " + String.valueOf(latS)
                    + " AND lat <= " + String.valueOf(latN)
                    + " AND (lon BETWEEN " + String.valueOf(-180) + " AND " + String.valueOf(lonE) + ") "
                    + "OR (lon BETWEEN " + String.valueOf(lonW) + " AND 180)" + " ORDER BY depth ASC;";
        } else {
            lonE -= 360;
            query = "SELECT evid, depth, lat, lon FROM historical_seismicity WHERE lat >= " + String.valueOf(latS)
                    + " AND lat <= " + String.valueOf(latN)
                    + " AND (lon BETWEEN " + String.valueOf(lonW) + " AND 180)"
                    + " OR (lon BETWEEN -180 AND " + String.valueOf(lonE) + ")" + " ORDER BY depth ASC;";
        }

        try {
            //con = DriverManager.getConnection(url, pgUser, pgPassword);
            st = pgCon.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                HistoricEvent tmp = new HistoricEvent(rs.getInt(1), rs.getInt(2), rs.getDouble(3),
                        rs.getDouble(4));

                histEvents.add(tmp);
            }

        } catch (SQLException ex) {

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
            }
        }
        return true;
    }

    /**
     *
     * @param evid all phases associated to the event id
     * @param PhasesList empty phase list
     * @return filled phases list
     */
    public static boolean retrieveAllPhases(Integer evid, ArrayList<Phase> PhasesList, Boolean isAssess) {
        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;
        String query = null;
        PhasesList.clear();

        try {
            if (isAssess) {
                st = assessCon.createStatement();
            } else {
                st = pgCon.createStatement();
            }

            query = "SELECT r.reporter, p.sta, p.day, a.delta, a.esaz, a.phase, a.timeres, p.phid, p.phase, a.timedef, p.rdid, s.staname, p.msec, p.slow, p.azim, i.snr, a.phase_fixed "
                    + "FROM event e, association a, report r, site s, phase p LEFT OUTER JOIN phase_info i "
                    + "ON p.phid = i.phid "
                    + "WHERE e.prime_hyp = a.hypid AND p.reporter = r.repid "
                    + "AND a.phid = p.phid  AND a.author = 'ISC' AND p.sta = s.sta AND p.net IS NULL AND s.net IS NULL AND e.evid = " + evid
                    + " ORDER BY a.delta ASC;";

            rs = st.executeQuery(query);

            VBASLogger.logDebug(query);

            while (rs.next()) {
                Phase tmp = new Phase();

                tmp.setReportAgency(rs.getString(1));
                tmp.setReportStation(rs.getString(2));
                tmp.setDistance(rs.getDouble(4));
                tmp.setAzimuth(rs.getDouble(5));
                tmp.setIscPhaseType(rs.getString(6));//.setOrigPhaseType(rs.getString(5));

                if (rs.getObject(7) != null) {
                    tmp.setTimeResidual(rs.getDouble(7));
                }

                tmp.setPhid(rs.getInt(8));
                tmp.setOrigPhaseType(rs.getString(9));

                if (rs.getString(10) == null) {
                    tmp.setDefining(false);
                } else {
                    tmp.setDefining(true);
                }

                tmp.setRdid(rs.getInt(11));
                tmp.setStationFullName(rs.getString(12));

                tmp.setMsec(rs.getInt(13));
                tmp.setSlowness(rs.getDouble(14));

                if (rs.getObject(15) != null) {
                    tmp.setSeAzimuth(rs.getDouble(15));
                }

                if (rs.getString(16) != null) {
                    tmp.setSNRRate(Double.valueOf(rs.getString(16)));
                }

                if (rs.getString(17) != null) {
                    tmp.setFixing(Boolean.TRUE);
                } else {
                    tmp.setFixing(Boolean.FALSE);
                }

                Date dd = null;
                //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                if (rs.getObject(3) != null) {
                    try {
                        // James: Issue#94: millisec field can be null, then treat as  0.
                        int mSec = (rs.getObject(13) == null) ? 0 : rs.getInt(13);
                        dd = df1.parse(rs.getString(3) + "." + mSec);
                        //dd = df.parse(rs.getString(3));
                    } catch (ParseException e) {
                        String message = "Failed parsing: " + rs.getObject(3) + rs.getObject(13)
                                + "\nSee the error log file for more information. ";
                        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
                        VBASLogger.logSevere(message);
                        return false;
                    } catch (NullPointerException ne) {
                        JOptionPane.showMessageDialog(null, "NullPointerException", "Error", JOptionPane.ERROR_MESSAGE);
                        VBASLogger.logSevere("NullPointerException");
                    }
                    tmp.setArrivalTime(dd);
                }
                //VBASLogger.logDebug(tmp.getPhid() + ", Arrival time: " + dd);
                PhasesList.add(tmp);
            }

            rs.close();

        } catch (SQLException ex) {
            String message = ex.toString() + "\n\n"
                    + "Failed query= " + query
                    + "\nSee the error log file for more information. ";
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            VBASLogger.logSevere(message);
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
                return false;
            }
        }

        return true;
    }

    /**
     * Retrieve all the phase ampmag and save them back to each phase in the
     * list
     *
     * @param evid
     * @param PhaseList
     * @return
     */
    public static boolean retrieveAllPhasesAmpMag(Integer evid, ArrayList<Phase> PhaseList, Boolean isAssess) {
        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;

        //build a hashmap for quick search
        HashMap phaseListMap = new HashMap<Integer, Phase>();
        for (Phase p : PhaseList) {
            phaseListMap.put(p.getPhid(), p);
        }

        try {
            if (isAssess) {
                st = assessCon.createStatement();
            } else {
                st = pgCon.createStatement();
            }

            String query = "SELECT p.amp, p.per, s.magnitude, s.ampdef, p.phid "
                    + "FROM ampmag s, amplitude p, event e, association a "
                    + "WHERE a.author = 'ISC' AND p.ampid = s.ampid AND a.phid = p.phid AND e.prime_hyp = a.hypid "
                    + "AND e.evid = " + evid;
            //+ "ORDER BY a.delta ASC;";

            rs = st.executeQuery(query);

            while (rs.next()) {

                //as we have the same order of the retrieve phases list and amplitude list, so just check or continue
                if (phaseListMap.containsKey(rs.getInt(5))) {
                    ((Phase) phaseListMap.get(rs.getInt(5))).setAmplitude(rs.getDouble(1));
                    ((Phase) phaseListMap.get(rs.getInt(5))).setPeriod(rs.getDouble(2));
                    ((Phase) phaseListMap.get(rs.getInt(5))).setAmpMag(rs.getDouble(3));

                    if (rs.getInt(4) == 1) {
                        ((Phase) phaseListMap.get(rs.getInt(5))).setAmpmagDefining(true);
                    } else if (rs.getInt(4) == 0) {
                        ((Phase) phaseListMap.get(rs.getInt(5))).setAmpmagDefining(false);
                    }
                }
            }
            rs.close();

        } catch (SQLException ex) {

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
            }
        }
        return true;

    }

    /**
     *
     * @param allStations
     * @return
     */
    public static boolean retrieveAllStationsWithRegions(TreeMap<String, String> allStations, Boolean isAssess) {
        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;

        String query = "SELECT s.sta, r.gr_short "
                + "FROM site s, site_grn g, region r "
                + "WHERE s.net IS NULL AND s.lat IS NOT NULL AND s.lon IS NOT NULL "
                + "AND s.sta = g.sta AND g.net IS NULL AND g.grn_ll = r.gr_number;";

        try {
            if (isAssess) {
                st = assessCon.createStatement();
            } else {
                st = pgCon.createStatement();
            }

            rs = st.executeQuery(query);

            while (rs.next()) {
                allStations.put(rs.getString(1), rs.getString(2));
            }

        } catch (SQLException ex) {

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
            }
        }
        return true;
    }

    /**
     * Retrieve all the registered stations
     *
     * @param evid
     * @param allStations
     * @param isAssess
     * @return
     */
    public static boolean retrieveAllStations(Integer evid, ArrayList<Station> allStations, Boolean isAssess) {
        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;

        allStations.clear();

        String query = "SELECT DISTINCT p.sta, s.lat, s.lon, r.reporter, a.esaz, a.delta FROM event e, association a, phase p, report r, site s "
                + " WHERE e.evid = " + evid
                + " AND e.prime_hyp = a.hypid "
                + "AND a.author = 'ISC' AND a.phid = p.phid AND p.reporter = r.repid AND p.sta = s.sta AND s.net IS NULL";

        try {

            if (isAssess) {
                //con = DriverManager.getConnection(url, assessUser, assessPassword);
                st = assessCon.createStatement();
            } else {
                //con = DriverManager.getConnection(url, pgUser, pgPassword);
                st = pgCon.createStatement();
            }

            //st = pgCon.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                Station tmp = new Station(rs.getString(1), rs.getDouble(2), rs.getDouble(3), rs.getString(4), rs.getDouble(5), rs.getDouble(6));

                allStations.add(tmp);
            }

        } catch (SQLException ex) {

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
            }
        }
        return true;
    }

    /**
     * Get the station magnitudes
     *
     * @param hypid
     * @param allStations
     * @param isAssess
     * @return
     */
    public static boolean retrieveStationMags(Integer hypid, ArrayList<Station> allStations, Boolean isAssess) {
        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;

        allStations.clear();

        String query = "SELECT DISTINCT m.sta, s.lat, s.lon, m.magnitude, m.magtype FROM site s, stamag m"
                + " WHERE m.hypid = " + hypid
                + " AND m.sta = s.sta"
                + " AND s.net IS NULL ORDER BY m.magtype";

        try {
            if (isAssess) {
                //con = DriverManager.getConnection(url, assessUser, assessPassword);
                st = assessCon.createStatement();
            } else {
                //con = DriverManager.getConnection(url, pgUser, pgPassword);
                st = pgCon.createStatement();
            }
            //st = pgCon.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                Station tmp = new Station(rs.getString(1), rs.getDouble(2), rs.getDouble(3));
                //tmp.setAzimuth(rs.getDouble(6));
                //tmp.setDelta(rs.getDouble(7));

                if (rs.getString(5).equals("mb")) {
                    tmp.setStaMb(rs.getDouble(4));
                    allStations.add(tmp);
                } else if (rs.getString(5).equals("MS")) {
                    tmp.setStaMs(rs.getDouble(4));
                    allStations.add(tmp);
                }
            }

        } catch (SQLException ex) {
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
                return false;
            }
        }
        return true;
    }

    //related with the scheduling
    /**
     * as the list is not big, so use iteration to fill the events number
     *
     * @param bList the blocklist to fill the events number
     * @return
     */
    public static boolean retrieveBlockEventNumber(ArrayList<TaskBlock> bList) {

        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;

        String query = "select ba.block_id, count(*)"
                + " From block_allocation ba, event_allocation ev"
                + " WHERE ba.id = ev.block_allocation_id AND ba.pass= 'p'"
                + " GROUP BY ba.block_id;";

        try {
            //con = DriverManager.getConnection(url, pgUser, pgPassword);
            st = pgCon.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {

                for (TaskBlock tb : bList) {
                    //System.out.println(tb.getBlockID());
                    //System.out.println(rs.getInt(1));

                    if (tb.getBlockID().equals(rs.getInt(1))) {
                        tb.setEventNumber(rs.getInt(2));
                    }
                }

            }

        } catch (SQLException ex) {

            StackTraceElement stack = Thread.currentThread().getStackTrace()[1];
            String message = stack.getFileName() + ": "
                    + stack.getLineNumber() + ": "
                    + stack.getClassName() + ": "
                    + stack.getMethodName();

            JOptionPane.showMessageDialog(null,
                    ex.toString()
                    + message
                    + "\n Failed to load task block list from database."
                    + "\n See the error log file for more information. ",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Failed to load task block list from database." + "\n In {0}", message);

            return false;

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {

                return false;
            }
        }

        return true;
    }

    /**
     * The
     *
     * @param bList
     * @return
     */
    public static boolean retrieveBlockReviewedEventNumber(ArrayList<TaskBlock> bList) {

        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;

        String query = "select ba.block_id, count(*)"
                + " From block_allocation ba, event_allocation ev"
                + " WHERE ba.id = ev.block_allocation_id AND ba.review = 0 AND ba.start IS NOT NULL AND ba.finish IS NULL and ev.start IS NOT NULL"
                + " GROUP BY ba.block_id;";

        try {
            //con = DriverManager.getConnection(url, pgUser, pgPassword);
            st = pgCon.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {

                for (TaskBlock tb : bList) {
                    //System.out.println(tb.getBlockID());
                    //System.out.println(rs.getInt(1));

                    if (tb.getBlockID().equals(rs.getInt(1))) {
                        tb.setReviewedEventNumber(rs.getInt(2));
                    }
                }

            }

        } catch (SQLException ex) {
            StackTraceElement stack = Thread.currentThread().getStackTrace()[1];
            String message = stack.getFileName() + ": "
                    + stack.getLineNumber() + ": "
                    + stack.getClassName() + ": "
                    + stack.getMethodName();

            JOptionPane.showMessageDialog(null,
                    ex.toString()
                    + message
                    + "\n Failed to load task block list from database."
                    + "\n See the error log file for more information. ",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Failed to load task block list from database." + "\n In {0}", message);

            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {

                return false;
            }
        }

        return true;
    }

    public static boolean loadBlocks(HashSet<TaskBlock> blockSet) {

        //clear the memory of blockArray in order to reload events
        //blockArray.clear();
        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;

        String query = "SELECT b.id, b.starttime, b.endtime, b.region_id, a.name, ba.pass, ba.review, a.id, ba.planned_start, ba.planned_finish"
                + " FROM block b, block_allocation ba, analyst a"
                + " Where b.id = ba.block_id AND a.id = ba.analyst_id"
                + " ORDER BY b.id;";

        try {
            //con = DriverManager.getConnection(url, pgUser, pgPassword);
            st = pgCon.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {

                if (blockSet.contains(new TaskBlock(rs.getInt(1)))) {
                    for (TaskBlock obj : blockSet) {
                        if (obj.getBlockID().equals(rs.getInt(1))) {
                            if ("p".equals(rs.getString(6))) {
                                if (rs.getString(5) != null) {
                                    obj.setAnalyst1(rs.getString(5));
                                    obj.setAnalyst1ID(rs.getInt(8));
                                }

                                if (rs.getInt(7) == 0) {
                                    obj.setStatus("P");
                                }

                                obj.setPPlanStartDay(new Date(rs.getTimestamp(9).getTime()));
                                obj.setPPlanEndDay(new Date(rs.getTimestamp(10).getTime()));
                            } else if ("s".equals(rs.getString(6))) {
                                if (rs.getString(5) != null) {
                                    obj.setAnalyst2(rs.getString(5));
                                    obj.setAnalyst2ID(rs.getInt(8));
                                }

                                if ((obj.getStatus() == null || obj.getStatus() == "F") && rs.getInt(7) == 0) {
                                    obj.setStatus("S");
                                }

                                obj.setSPlanStartDay(new Date(rs.getTimestamp(9).getTime()));
                                obj.setSPlanEndDay(new Date(rs.getTimestamp(10).getTime()));
                            } else if ("f".equals(rs.getString(6))) {
                                if (rs.getString(5) != null) {
                                    obj.setAnalyst3(rs.getString(5));
                                    obj.setAnalyst3ID(rs.getInt(8));

                                }

                                if (obj.getStatus() == null && rs.getInt(7) == 0) {
                                    obj.setStatus("F");
                                } else if (rs.getInt(7) == 1) {
                                    obj.setStatus("Done");
                                }

                                obj.setFPlanStartDay(new Date(rs.getTimestamp(9).getTime()));
                                obj.setFPlanEndDay(new Date(rs.getTimestamp(10).getTime()));
                            }
                        }
                    }
                } else {
                    TaskBlock tmp = new TaskBlock(rs.getInt(1));

                    tmp.setStartDay(new Date(rs.getTimestamp(2).getTime()));
                    tmp.setEndDay(new Date(rs.getTimestamp(3).getTime()));
                    tmp.setRegionID(rs.getInt(4));

                    if ("p".equals(rs.getString(6))) {
                        tmp.setAnalyst1(rs.getString(5));
                        if (rs.getInt(7) == 0) {
                            tmp.setStatus("P");
                        }
                        tmp.setAnalyst1ID(rs.getInt(8));
                        tmp.setPPlanStartDay(new Date(rs.getTimestamp(9).getTime()));
                        tmp.setPPlanEndDay(new Date(rs.getTimestamp(10).getTime()));
                    } else if ("s".equals(rs.getString(6))) {
                        tmp.setAnalyst2(rs.getString(5));
                        if (rs.getInt(7) == 0) {
                            tmp.setStatus("S");
                        }
                        tmp.setAnalyst2ID(rs.getInt(8));
                        tmp.setSPlanStartDay(new Date(rs.getTimestamp(9).getTime()));
                        tmp.setSPlanEndDay(new Date(rs.getTimestamp(10).getTime()));
                    } else if ("f".equals(rs.getString(6))) {
                        tmp.setAnalyst3(rs.getString(5));
                        if (rs.getInt(7) == 0) {
                            tmp.setStatus("F");
                        } else if (rs.getInt(7) == 1) {
                            tmp.setStatus("Done");
                        }
                        tmp.setAnalyst3ID(rs.getInt(8));
                        tmp.setFPlanStartDay(new Date(rs.getTimestamp(9).getTime()));
                        tmp.setFPlanEndDay(new Date(rs.getTimestamp(10).getTime()));
                    }

                    blockSet.add(tmp);
                }
            }

        } catch (SQLException ex) {
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {

                return false;
            }
        }

        return true;
    }

    /*
     * Update the Command table: add new command (string) generated.
     */
    public static boolean updateCommandTable(
            Integer evid,
            String type,
            String commandStr,
            String functionStr) {

        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            //con = DriverManager.getConnection(url, pgUser, pgPassword);
            st = pgCon.createStatement();

            int block_allocation_id = 0;

            String query = "SELECT ba.id"
                    + " FROM analyst a, event_allocation ea, block_allocation ba"
                    + " WHERE ea.evid = " + evid
                    + " AND ba.id = ea.block_allocation_id"
                    + " AND ba.analyst_id = a.id"
                    + " AND a.username = '" + sysUser + "' "
                    + " ORDER BY ba.start DESC"
                    + " LIMIT 1;";

            VBASLogger.logDebug("query= " + query);
            rs = st.executeQuery(query);

            while (rs.next()) {
                block_allocation_id = rs.getInt("id");
            }

            if (block_allocation_id == 0) {
                String message = "The block_allocation_id is 0, report to the system admin.";
                JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
                VBASLogger.logDebug(message);
                return false;
            }

            query = "INSERT INTO edit_commands ( "
                    + "id, "
                    + "evid, "
                    + "status, "
                    + "block_allocation_id, "
                    + "adddate, "
                    + "type, "
                    + "command, "
                    + "functions)\n"
                    + "VALUES ( "
                    + "edit_commands_id(" + evid + ") " + ", "
                    + evid + ", "
                    + "NULL, "
                    + block_allocation_id + ", "
                    + "NOW(), '"
                    + type + "', '"
                    + commandStr + "', '"
                    + functionStr + "');";

            VBASLogger.logDebug("query= " + query);
            st.executeUpdate(query);

            rs.close();

        } catch (SQLException ex) {
            ex.printStackTrace();

            String message = ex.toString() + "\n\n"
                    + VBASLogger.debugAt()
                    + "Query= " + ex.getSQLState()
                    + "\nFailed to update Command history table."
                    + "\nSee the error log file for more information. ";

            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            VBASLogger.logSevere(message);

            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
                return false;
            }
        }
        return true;

    }

    /*
     * Retrieve all Command history.
     */
    public static boolean readCommandTable(Integer evid, ArrayList<Command> commandList) {

        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;

        commandList.clear();

        try {
            //con = DriverManager.getConnection(url, pgUser, pgPassword);
            st = pgCon.createStatement();

            String query = "SELECT ec.id, ec.command, ec.functions, a.name, ba.pass, ec.adddate, ec.type, ec.status"
                    + " FROM analyst a, edit_commands ec, block_allocation ba"
                    + " WHERE ec.evid = " + evid
                    + " AND ec.type != 'assess' AND ec.type != 'commit'"
                    + " AND ba.id = ec.block_allocation_id"
                    + " AND ba.analyst_id = a.id"
                    + " ORDER BY ec.adddate;";

            rs = st.executeQuery(query);
            VBASLogger.logDebug("query= " + query + "\nrs= " + rs);

            while (rs.next()) {
                String commandStr = rs.getString("command") + "";
                String functionsStr = rs.getString("functions") + "";
                Integer id = rs.getInt("id");
                String analyst = rs.getString("name") + "";
                String pass = rs.getString("pass") + "";
                Date date = rs.getDate("adddate");
                String status = rs.getString("status") + "";
                String type = rs.getString("type") + "";

                VBASLogger.logDebug(id + " | " + analyst + " | " + commandStr + " | " + pass + " | " + date + " | " + status + " | " + type);
                Command command = new Command(evid, commandStr, functionsStr, id, analyst, pass, date, status, type);
                commandList.add(command);
            }

            rs.close();

        } catch (SQLException ex) {
            String message = ex.toString() + "\n\n"
                    + VBASLogger.debugAt()
                    + "\nFailed to load Command history list from database."
                    + "\nSee the error log file for more information. ";

            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, message);

            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
                return false;
            }
        }
        return true;

    }

    /*
     * Update the Command table: add new "Assessed" command (string) generated.
     */
    public static int updateAssessedCommandTable(
            Integer evid,
            String commandType,
            ArrayList<Integer> commandIds,
            String serverUrl,
            String systemCommandStr /*systemCommand executed*/
    ) {

        int newAssessId = 0;

        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            //con = DriverManager.getConnection(url, pgUser, pgPassword);
            st = pgCon.createStatement();

            int block_allocation_id = 0;

            String query = "SELECT ba.id"
                    + " FROM analyst a, event_allocation ea, block_allocation ba"
                    + " WHERE ea.evid = " + evid
                    + " AND ba.id = ea.block_allocation_id"
                    + " AND ba.analyst_id = a.id"
                    + " AND a.username = '" + sysUser + "'"
                    + " ORDER BY ba.start DESC"
                    + " LIMIT 1;";

            VBASLogger.logDebug("query= " + query);
            rs = st.executeQuery(query);

            while (rs.next()) {
                block_allocation_id = rs.getInt("id");
            }

            //query = "select NEXTVAL('isc.id');";
            query = "select edit_commands_id(" + evid + ") AS nextval";

            VBASLogger.logDebug("query= " + query);
            rs = st.executeQuery(query);

            while (rs.next()) {
                newAssessId = rs.getInt("nextval");
            }

            String htmlUrl = serverUrl + "/" + newAssessId + "/" + newAssessId + ".html";

            query = "INSERT INTO edit_commands ( "
                    + "id, "
                    + "evid, "
                    + "status, "
                    + "block_allocation_id, "
                    + "adddate, "
                    + "type, "
                    + "command, "
                    + "functions) "
                    + "VALUES ( "
                    + newAssessId + ", "
                    + evid + ", "
                    + "NULL, "
                    + block_allocation_id + ", "
                    + "NOW(), '"
                    + commandType + "', '"
                    + htmlUrl + "', '"
                    + systemCommandStr + "');";

            VBASLogger.logDebug("query= " + query);
            st.executeUpdate(query);

            for (int commandId : commandIds) {
                query = "INSERT INTO command_group VALUES ( "
                        + newAssessId + ", "
                        + commandId + ")";
                VBASLogger.logDebug("query= " + query);
                st.executeUpdate(query);
            }

            for (int commandId : commandIds) {
                query = "INSERT INTO command_group (id,edit_commands_id,evid) VALUES ( "
                        + newAssessId + ", "
                        + commandId + ", "
                        + evid + ")";
                VBASLogger.logDebug("query= " + query);
                st.executeUpdate(query);
            }

            rs.close();

        } catch (SQLException ex) {
            String message = ex.toString() + "\n\n"
                    + VBASLogger.debugAt()
                    + "Query= " + ex.getSQLState()
                    + "\nFailed to update Command history table."
                    + "\nSee the error log file for more information. ";

            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, message);

            return 0;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
                return 0;
            }
        }

        return newAssessId;
    }

    /*
     * Retrieve all Assessed Command history.
     */
    public static boolean readAssessedCommandTable(Integer evid, ArrayList<AssessedCommand> assessedCommandList) {

        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;
        String query = "";

        assessedCommandList.clear();

        try {
            //con = DriverManager.getConnection(url, pgUser, pgPassword);
            st = pgCon.createStatement();

            query = "SELECT ba.pass, a.name, ec.type, ec.id AS assessid, ec.command AS command, eca.id AS cmdids"
                    + " FROM edit_commands ec, command_group cg, edit_commands eca, block_allocation ba, analyst a"
                    + "   WHERE ec.evid = " + evid
                    + "      AND cg.evid = ec.evid"
                    + "      AND eca.evid = ec.evid"
                    + "      AND (ec.type = 'assess'"
                    + "            OR ec.type = 'commit')"
                    + "      AND cg.id = ec.id"
                    + "      AND eca.id = cg.edit_commands_id"
                    + "      AND ba.id = ec.block_allocation_id"
                    + "      AND ba.analyst_id = a.id"
                    + " ORDER BY ec.adddate;";

            rs = st.executeQuery(query);

            VBASLogger.logDebug("in readAssessedCommandTable :: query = " + query);

            Hashtable<Integer, AssessedCommand> hashtable
                    = new Hashtable<Integer, AssessedCommand>();;

            //VBASLogger.logDebug("commandId" + " | " + "analyst" + " | " + "report" + " | " + "pass" + " | " + "assessId");
            while (rs.next()) {
                String analyst = rs.getString("name");
                String pass = rs.getString("pass");
                String report = rs.getString("command");  // we store the html report in the command field.
                int assessId = rs.getInt("assessid");
                String commandId = rs.getString("cmdids");
                String type = rs.getString("type");

                //VBASLogger.logDebug(commandId + " | " + analyst + " | " + report + " | " + pass + " | " + assessId);
                AssessedCommand ac = hashtable.get(assessId);
                if (ac == null) {
                    ac = new AssessedCommand(assessId, evid, commandId, analyst, report, type);
                } else {
                    ac.setIds(ac.getIds() + ", " + commandId);
                }
                hashtable.put(assessId, ac);
            }

            Set<Integer> keys = hashtable.keySet();
            Iterator<Integer> itr = keys.iterator();
            while (itr.hasNext()) {
                Integer i = itr.next();
                //VBASLogger.logDebug("Key: " + str + " & Value: " + hashtable.get(str));
                assessedCommandList.add(hashtable.get(i));
            }

            rs.close();

        } catch (SQLException ex) {
            String message = ex.toString() + "\n\n"
                    + VBASLogger.debugAt()
                    + "\nFailed to load Command history list from database."
                    + "\nSee the error log file for more information. "
                    + "\nQuery: " + query;

            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, message);

            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
                return false;
            }
        }
        return true;
    }

    /*
     * Update the database when an event is "Done".
     */
    public static Boolean processDoneAction(Integer evid) {

        //Connection pgCon = null;
        Statement st = null;
        //ResultSet rs = null;
        String query = null;

        try {
            //con = DriverManager.getConnection(url, pgUser, pgPassword);
            st = pgCon.createStatement();

            query = "UPDATE event_allocation"
                    + "  SET finish = NOW()"
                    + "  WHERE evid = " + evid
                    + "  AND block_allocation_id = ("
                    + "  SELECT ba.id"
                    + "  FROM analyst a, event_allocation ea,"
                    + "  block_allocation ba "
                    + "  WHERE ea.evid = " + evid
                    + "  AND ba.id = ea.block_allocation_id "
                    + "  AND ba.analyst_id = a.id"
                    + "  AND a.username = '" + sysUser + "'"
                    + "  ORDER BY ba.start DESC"
                    + "  LIMIT 1 )";

            VBASLogger.logDebug("query= " + query);
            int succ = st.executeUpdate(query);
            // UPDATE 0    => failed, UPDATE 1    => pass, UPDATE X => somethiog wrong
            if (succ != 1) {
                String message = VBASLogger.debugAt()
                        + "\nFailed to run, the query:"
                        + query
                        + "\nReport to system admin. ";
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.SEVERE, message);
            }

        } catch (SQLException ex) {
            String message = ex.toString() + "\n\n"
                    + VBASLogger.debugAt()
                    + "Query= " + ex.getSQLState()
                    + "\nFailed to run."
                    + "\nSee the error log file for more information. ";

            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, message);
            return false;
        } finally {
            try {
                /*if (rs != null) {
                 rs.close();
                 }*/
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
                return false;
            }
        }

        return true;
    }

    /*
     * Update the database when an event is "Banished".
     */
    public static Boolean processBanishUnbanishAction(String sqlFunction) {

        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;
        String query = null;

        try {
            //con = DriverManager.getConnection(url, pgUser, pgPassword);
            st = pgCon.createStatement();
            query = "SELECT " + sqlFunction;

            VBASLogger.logDebug("query= " + query);
            rs = st.executeQuery(query);
            rs.close();

        } catch (SQLException ex) {
            String message = ex.toString() + "\n\n"
                    + VBASLogger.debugAt()
                    + "Query= " + ex.getSQLState()
                    + "\nFailed to run."
                    + "\nRepot to system administator.";

            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, message);
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                /*if (pgCon != null) {
                 pgCon.close();
                 }*/

            } catch (SQLException ex) {
                return false;
            }
        }

        return true;
    }

    /*
     * Update the database when an event is "Banished".
     */
    public static Boolean processAllocateCommand(int evid) {

        //Connection pgCon = null;
        Statement st = null;
        ResultSet rs = null;
        String query = null;
        Boolean ret = false;

        try {
            st = pgCon.createStatement();
            query = "SELECT " + "ALLOCATE ( " + evid + " , '" + sysUser + "' );";
            VBASLogger.logDebug("query= " + query);
            rs = st.executeQuery(query);

            while (rs.next()) {
                if (rs.getInt("allocate") == 1) {
                    String msg = "Failed to allocate a SeiesEvent. "
                            + "\nReport to system admin."
                            + "\nQuery: "
                            + query;

                    JOptionPane.showMessageDialog(null,
                            msg,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    logger.log(Level.SEVERE, msg);
                    ret = false;
                } else if (rs.getInt("allocate") == 0) {
                    ret = true;
                }
            }

            rs.close();

        } catch (SQLException ex) {
            String message = ex.toString() + "\n\n"
                    + VBASLogger.debugAt()
                    + "\nFailed to run."
                    + "\nRepot to system admin."
                    + "\nQuery= "
                    + query;

            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, message);
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                return false;
            }
        }

        return ret;
    }

    /*
     * Update the Assess Schema
     * Return the locatorCommandStr
     */
    public static Boolean processAssessOrCommitData(int evid,
            ArrayList<String> functionArray,
            Boolean isAssess) {

        //Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String query = null;
        Boolean ret = false;
        String notification = "Failed a database query, report to system admin.\n" + "Query: ";

        try {

            if (isAssess) {
                st = assessCon.createStatement();
            } else {
                st = pgCon.createStatement();
            }

            /* Assess: these two statement will runonly when we shall assess */
            if (isAssess) {
                /* 1: delete existing data in assess schema */
                query = "SELECT CLEAR_ASSESS();";
                VBASLogger.logDebug("query= " + query);
                rs = st.executeQuery(query);
                while (rs.next()) {
                    if (rs.getInt("clear_assess") == 1) {
                        JOptionPane.showMessageDialog(null, notification + query + "\n", "Error", JOptionPane.ERROR_MESSAGE);
                        logger.log(Level.SEVERE, notification + query + "\n");
                        ret = false;
                    } else if (rs.getInt("clear_assess") == 0) {
                        ret = true;
                        VBASLogger.logDebug("Success..");
                    }
                }

                /* 2: Fill schema with appropiate data. */
                /* NOTE: the FILL_ASSESS will read data from PGUSER. */
                /* NOTE: for "merge" command assess the evid will change to source evid*/
                Boolean isMerge = false;
                Integer srcEvid = null;
                for (String function : functionArray) {
                    if (function.contains("merge")) {
                        isMerge = true;
                        String[] token = function.split("\\s");
                        srcEvid = Integer.valueOf(token[2]);
                        break;
                    }
                }

                if (isMerge) {
                    query = "SELECT FILL_ASSESS (" + srcEvid + ", '" + pgUser + "');";
                    VBASLogger.logDebug("isMerge=" + isMerge + ", query= " + query);
                    rs = st.executeQuery(query);
                    while (rs.next()) {
                        if (rs.getInt("fill_assess") == 1) {
                            JOptionPane.showMessageDialog(null, notification + query + "\n", "Error", JOptionPane.ERROR_MESSAGE);
                            logger.log(Level.SEVERE, notification + query + "\n");
                            ret = false;
                        } else if (rs.getInt("fill_assess") == 0) {
                            ret = true;
                            VBASLogger.logDebug("Success..");
                        }
                    }
                }

                query = "SELECT FILL_ASSESS (" + evid + ", '" + pgUser + "');";
                VBASLogger.logDebug("query= " + query);
                rs = st.executeQuery(query);
                while (rs.next()) {
                    if (rs.getInt("fill_assess") == 1) {
                        JOptionPane.showMessageDialog(null, notification + query + "\n", "Error", JOptionPane.ERROR_MESSAGE);
                        logger.log(Level.SEVERE, notification + query + "\n");
                        ret = false;
                    } else if (rs.getInt("fill_assess") == 0) {
                        ret = true;
                        VBASLogger.logDebug("Success..");
                    }
                }
            }

            /*3: Commands: apply data alteration functions */
            for (String function : functionArray) {
                query = "SELECT " + function + ";";
                VBASLogger.logDebug("query= " + query);
                rs = st.executeQuery(query);

                String functionName = function.split("\\s")[0];
                while (rs.next()) {
                    if (rs.getInt(functionName) == 1) {
                        JOptionPane.showMessageDialog(null, notification + query + "\n", "Error", JOptionPane.ERROR_MESSAGE);
                        logger.log(Level.SEVERE, notification + query + "\n");
                        ret = false;
                    } else if (rs.getInt(functionName) == 0) {
                        ret = true;
                        VBASLogger.logDebug("Success..");
                    }
                }
            }

        } catch (SQLException ex) {
            String message = notification + query + "\nException: " + ex.toString() + "\n\n";
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            VBASLogger.logSevere(message);

            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                return null;
            }
        }

        return true;
    }
}
