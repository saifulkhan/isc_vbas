

import java.io.File;
import java.nio.file.Path;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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
import uk.ac.isc.seisdata.HistoricEvent;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.LikeliTriplet;
import uk.ac.isc.seisdata.Phase;
import uk.ac.isc.seisdata.SeisEvent;
import uk.ac.isc.seisdata.Station;
import uk.ac.isc.seisdata.TaskBlock;
import uk.ac.isc.seisdata.VBASLogger;

/**
 * This is the database access object which provides functions to read and write
 * ISC database
 */
public class __Old_SeisDataDAO {

    protected static final Logger logger = Logger.getLogger(__Old_SeisDataDAO.class.getName());

    // Loading pgUser name, password and scheme from system environment
    protected static String url;
    protected static String pgUser;
    protected static String password;
    protected static String sysUser;

    static long totalLocatormessageLoadingTime = 0;

    // Assess schema 
    protected static String assessUser;
    protected static String assessPassword;
    private static Path assessDir = null;
    private static Connection con = null;

    static {

        Map<String, String> env = System.getenv();
        url = "jdbc:postgresql://"
                + env.get("PGHOSTADDR") + ":"
                + env.get("PGPORT") + "/"
                + env.get("PGDATABASE");
        pgUser = env.get("PGUSER");
        password = env.get("PGPASSWORD");
        sysUser = env.get("USER");

        assessUser = env.get("ASSESS_USER");
        assessPassword = env.get("ASSESS_PW");

        /*} else {
         // Saiful: Windows 10 laptop
         url = "jdbc:postgresql://127.0.0.1:5432/isc";
         pgUser = "saiful";
         password = "saiful";
         }*/
        VBASLogger.logDebug("url=" + url
                + ", user=" + pgUser
                + ", password=" + password
                + ", sysUser=" + sysUser
                + ", assessUser=" + assessUser
                + ", assessPassword=" + assessPassword);
        try {
            con = DriverManager.getConnection(url, pgUser, password);
        } catch (SQLException ex) {
            String message = ex.toString();
            logger.log(Level.SEVERE, message);
        }  
    }

    public __Old_SeisDataDAO() {
    }

    public static String getPgUser() {
        return pgUser;
    }

    /**
     * retrieve all the events in a pgUser's schema
     *
     * @param seisEvents for saving the events list
     * @return success flag
     */
    public static boolean retrieveAllEvents(ArrayList<SeisEvent> seisEvents) {
        //clear the memory of seisEvent in order to reload events
        seisEvents.clear();

        //Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String query = null;

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        /*long startTime = System.nanoTime();*/

        query = "SELECT e.evid, h.author, h.day, h.lat, h.lon, h.depth, e.etype, get_default_depth_grid (h.lat, h.lon)"
                + " FROM event e, hypocenter h"
                + " WHERE e.prime_hyp = h.hypid"
                + " AND h.isc_evid = e.evid AND e.banished IS NULL AND e.ready IS NOT NULL"
                + " AND h.deprecated is NULL AND h.hypid = h.pref_hypid"
                + " ORDER BY h.day ASC;";

        VBASLogger.logDebug("Executing Query: " + query);

        try {
            //con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {

                int evid = rs.getInt(1);
                SeisEvent tmp = new SeisEvent(evid, 
                        rs.getString(7), 
                        rs.getDouble(8),
                        __Old_SeisDataDAO.getLocatorMessage(evid, con));

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
                /*if (con != null) {
                    con.close();
                }*/

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

        /*long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        VBASLogger.logDebug("Duration to load all the events = " + duration);
        VBASLogger.logDebug("Total duration to load all the messages = " + totalLocatormessageLoadingTime);*/

        return true;
    }

    private static String getLocatorMessage(int evid, Connection con) {

        /*long startTime1 = 0, endTime1 = 0, startTime2 = 0, endTime2 = 0, startTime3 = 0, endTime3 = 0;
        startTime1 = System.nanoTime();
        startTime2 = System.nanoTime();*/

        String msg = "";
        //Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String query = "SELECT comment FROM iscloc_comments WHERE evid = "
                + evid
                + " ORDER BY commno; ";

        //VBASLogger.logDebug("\nEvid = " + evid); 
        String tempMsg = "";

        try {
            //con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            /*endTime1 = System.nanoTime();
            startTime3 = System.nanoTime();*/

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
                    msg += locMsg + "\n";

                    if (Integer.valueOf(locMsg.substring(locMsg.lastIndexOf(" ") + 1)) > 0) {

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
                    msg += locMsg + "\n";

                    if (Integer.valueOf(locMsg.substring(locMsg.lastIndexOf(" ") + 1)) > 0) {
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
                /*if (con != null) {
                    con.close();
                }*/

            } catch (SQLException ex) {
                String message = VBASLogger.debugAt() + ex.toString();
                logger.log(Level.SEVERE, message);
            }
        }

        //VBASLogger.logDebug("Entire locator message:\n" + tempMsg + "\nFiltered locator message:\n" + msg);
        /*endTime2 = System.nanoTime();
        VBASLogger.logDebug("Evid = " + evid
                + ", T_SQL =" + (endTime1 - startTime1) / 1000000
                + ", T_Parsing = " + (endTime3 - startTime3) / 1000000
                + ", T_Total = " + (endTime2 - startTime2) / 1000000);

        totalLocatormessageLoadingTime += (endTime2 - startTime2) / 1000000;*/

        return msg;
    }

   
     /**
     * retrieve events' magnitude, actually it retrieves the magnitudes of
     * primehypo
     *
     * @param evList
     * @return all the events filling with magnitude
     */
    public static boolean retrieveEventsMagnitude(ArrayList<SeisEvent> evList) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

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
                if (con != null) {
                    con.close();
                }

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
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

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
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                return false;
            }
        }

        return true;
    }

    /* this one is under deveopment, once James finishs his procedure in the database side, we could start
     calling it for the likelihood values
     */
    public static boolean retrieveAgencyLikelihood(HashMap<String, LikeliTriplet> map, Integer evid) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        DecimalFormat df = new DecimalFormat("#.##");

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

            String query = "SELECT agency, likedist, likemag, liketime"
                    + " FROM  likelihood(" + evid + ")";

            rs = st.executeQuery(query);

            while (rs.next()) {

                Double tmpDist = Double.valueOf(df.format(rs.getDouble(2)));
                Double tmpMag = Double.valueOf(df.format(rs.getDouble(3)));
                Double tmpTime = Double.valueOf(df.format(rs.getDouble(4)));
                LikeliTriplet tmpTriplet = new LikeliTriplet(tmpDist, tmpMag, tmpTime);

                map.put(rs.getString(1), tmpTriplet);
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
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get the phase number for the event control view so that it is know
     * whether an event is big or not
     *
     * @param evList
     * @return
     */
    public static boolean retrievePhaseNumber(ArrayList<SeisEvent> evList) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

            for (SeisEvent ev : evList) {
                String query = "SELECT COUNT(*)"
                        + " FROM  association a, event e"
                        + " WHERE e.evid = " + ev.getEvid()
                        + " AND a.hypid = e.prime_hyp"
                        + " AND a.author='ISC';";

                rs = st.executeQuery(query);

                while (rs.next()) {
                    ev.setPhaseNumber(rs.getInt(1));
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
                if (con != null) {
                    con.close();
                }

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
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        Integer retEvid = null;
        try {

            if (isAssess) {
                con = DriverManager.getConnection(url, assessUser, assessPassword);
            } else {
                con = DriverManager.getConnection(url, pgUser, password);
            }

            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();
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
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                //return false;
            }
        }

        return retEvid;

    }

    //getting new reading id so that a new reading can be created
    static Integer getNextNewRdid() {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        Integer retRdid = null;
        String query = "SELECT NEXTVAL('isc.rdid');";

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                retRdid = rs.getInt(1);
            }
        } catch (SQLException ex) {

            String message = ex.toString() + "\n\n"
                    + VBASLogger.debugAt()
                    + "\nQuery : " + query
                    + "\nFailed to get new siesevent id."
                    + "\nSee the error log file for more information. ";

            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, message);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                //return false;
            }
        }

        return retRdid;

    }

    /**
     * Here is for retrieving the phase number for each event, difference with
     * the previous one is that this one can retrieve the phase nmber
     *
     * @param evList
     * @return
     */
    public static boolean retrieveAllPhaseNumber(ArrayList<SeisEvent> evList) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

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
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get all the hypocentres
     *
     * @param evid
     * @param HypoList
     * @return
     */
    public static boolean retrieveHypos(Integer evid, ArrayList<Hypocentre> HypoList) {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        HypoList.clear();

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

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
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                return false;
            }
        }
        return true;

    }

    /**
     * retrieve single hypocentre based on its hypid
     *
     * @param hypid
     * @return
     */
    public static Hypocentre retrieveSingleHypo(Integer hypid) {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        Hypocentre tmp = new Hypocentre();

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

            String query
                    = "SELECT h.author, h.day, h.lat, h.lon, h.depth, h.prime, h.hypid, x.sdepth, h.epifix, x.stime, x.strike, x.smajax, x.sminax,h.nass, h.ndef, h.nsta, h.ndefsta, h.msec, x.sdobs"
                    + " FROM hypocenter h LEFT OUTER JOIN hypoc_err x ON x.hypid = h.hypid"
                    + " WHERE h.deprecated is NULL AND h.hypid = h.pref_hypid AND h.hypid = "
                    + hypid
                    + " ORDER BY h.prime DESC, h.author";

            rs = st.executeQuery(query);

            while (rs.next()) {

                tmp.setAgency(rs.getString(1));

                Date dd = null;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    dd = df.parse(rs.getString(2));
                } catch (ParseException e) {
                    System.out.println("Parsing Date Error!");
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

            }

            rs.close();

        } catch (SQLException ex) {
            System.out.println("Database Error");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                System.out.println("Database Error");
            }
        }
        return tmp;

    }

    /*
     This is for retrieving Default Grid Depth in the region by give latitude and longitude
     */
    public static Double retrieveDefaultGridDepth(Integer hypid) {
        Double depth = null;

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

            String query = "SELECT lat, lon, get_default_depth_grid(lat,lon) "
                    + "FROM hypocenter "
                    + "WHERE hypid = " + hypid;
            rs = st.executeQuery(query);
            if (rs.next()) {
                depth = rs.getDouble(3);
            }
        } catch (SQLException ex) {
            return null;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                return null;
            }
        }

        return depth;
    }

    /**
     * Retrieve hypocentre magnitudes list
     *
     * @param HypoList
     * @return
     */
    public static boolean retrieveHyposMagnitude(ArrayList<Hypocentre> HypoList) {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        Iterator<Hypocentre> iter = HypoList.iterator();
        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

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
                if (con != null) {
                    con.close();
                }

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
        Connection con = null;
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
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();
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
                if (con != null) {
                    con.close();
                }

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
    public static boolean retrieveAllPhases(Integer evid, ArrayList<Phase> PhasesList) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String query = null;
        PhasesList.clear();

        try {

            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

            query = "SELECT r.reporter, p.sta, p.day, a.delta, a.esaz, a.phase, a.timeres, p.phid, p.phase, a.timedef, p.rdid, s.staname, p.msec, p.slow, p.azim, i.snr, a.phase_fixed "
                    + "FROM event e, association a, report r, site s, phase p LEFT OUTER JOIN phase_info i "
                    + "ON p.phid = i.phid "
                    + "WHERE e.prime_hyp = a.hypid AND p.reporter = r.repid "
                    + "AND a.phid = p.phid  AND a.author = 'ISC' AND p.sta = s.sta AND p.net IS NULL AND s.net IS NULL AND e.evid = " + evid
                    + " ORDER BY a.delta ASC;";

            rs = st.executeQuery(query);

            VBASLogger.logDebug(query);

            while (rs.next()) {
                //VBASLogger.logDebug((++i) + " ");
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
                //tmp.setAmplitude(rs.getDouble(11));
                //tmp.setPeriod(rs.getDouble(12));
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

                if (rs.getObject(3) != null && rs.getObject(13) != null) {
                    try {
                        //dd = df.parse(rs.getString(3));
                        dd = df1.parse(rs.getString(3) + "." + rs.getInt(13));
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
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                return false;
            }
        }

        return true;
    }

    /**
     * Retrieve single phase based on phid
     *
     * @param phid
     * @return
     */
    public static Phase retrieveSinglePhase(Integer phid) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        Phase tmp = new Phase();
        try {

            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

            String query = "SELECT r.reporter, p.sta, p.day, a.delta, a.esaz, a.phase, a.timeres, p.phid, p.phase, a.timedef, p.rdid, s.staname, p.msec, p.slow, p.azim, i.snr, a.phase_fixed "
                    + "FROM event e, association a, report r, site s, phase p LEFT OUTER JOIN phase_info i "
                    + "ON p.phid = i.phid "
                    + "WHERE e.prime_hyp = a.hypid AND p.reporter = r.repid "
                    + "AND a.phid = p.phid  AND a.author = 'ISC' AND p.sta = s.sta AND p.net IS NULL AND s.net IS NULL AND p.phid = " + phid
                    + ";";

            rs = st.executeQuery(query);

            while (rs.next()) {

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
                //tmp.setAmplitude(rs.getDouble(11));
                //tmp.setPeriod(rs.getDouble(12));
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

                if (rs.getObject(3) != null && rs.getObject(13) != null) {
                    try {
                        //dd = df.parse(rs.getString(3));
                        dd = df1.parse(rs.getString(3) + "." + rs.getInt(13));
                    } catch (ParseException e) {
                        String message = "Failed parsing: " + rs.getObject(3) + rs.getObject(13)
                                + "\nSee the error log file for more information. ";
                        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
                        VBASLogger.logSevere(message);
                    } catch (NullPointerException ne) {
                        JOptionPane.showMessageDialog(null, "NullPointerException", "Error", JOptionPane.ERROR_MESSAGE);
                        VBASLogger.logSevere("NullPointerException");
                    }
                    tmp.setArrivalTime(dd);
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
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
            }
        }

        return tmp;
    }

    /**
     * Retrieve all the phase ampmag and save them back to each phase in the
     * list
     *
     * @param evid
     * @param PhaseList
     * @return
     */
    public static boolean retrieveAllPhasesAmpMag(Integer evid, ArrayList<Phase> PhaseList) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        //build a hashmap for quick search
        HashMap phaseListMap = new HashMap<Integer, Phase>();
        for (Phase p : PhaseList) {
            phaseListMap.put(p.getPhid(), p);
        }

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

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
                if (con != null) {
                    con.close();
                }

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
    public static boolean retrieveAllStationsWithRegions(TreeMap<String, String> allStations) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String query = "SELECT s.sta, r.gr_short "
                + "FROM site s, site_grn g, region r "
                + "WHERE s.net IS NULL AND s.lat IS NOT NULL AND s.lon IS NOT NULL "
                + "AND s.sta = g.sta AND g.net IS NULL AND g.grn_ll = r.gr_number;";

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();
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
                if (con != null) {
                    con.close();
                }

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
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        allStations.clear();

        String query = "SELECT DISTINCT p.sta, s.lat, s.lon, r.reporter, a.esaz, a.delta FROM event e, association a, phase p, report r, site s "
                + " WHERE e.evid = " + evid
                + " AND e.prime_hyp = a.hypid "
                + "AND a.author = 'ISC' AND a.phid = p.phid AND p.reporter = r.repid AND p.sta = s.sta AND s.net IS NULL";

        try {

            if (isAssess) {
                con = DriverManager.getConnection(url, assessUser, assessPassword);
            } else {
                con = DriverManager.getConnection(url, pgUser, password);
            }

            st = con.createStatement();
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
                if (con != null) {
                    con.close();
                }

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
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        allStations.clear();

        /*String query = "SELECT DISTINCT m.sta, s.lat, s.lon, m.magnitude, m.magtype, a.esaz, a.delta FROM site s, stamag m, association a"
         + " WHERE m.hypid = " + hypid +
         " AND m.sta = s.sta" +
         " AND s.net IS NULL AND m.hypid = a.hypid AND a.author = 'ISC' ORDER BY m.magtype";
         */
        String query = "SELECT DISTINCT m.sta, s.lat, s.lon, m.magnitude, m.magtype FROM site s, stamag m"
                + " WHERE m.hypid = " + hypid
                + " AND m.sta = s.sta"
                + " AND s.net IS NULL ORDER BY m.magtype";

        try {
            if (isAssess) {
                con = DriverManager.getConnection(url, assessUser, assessPassword);
            } else {
                con = DriverManager.getConnection(url, pgUser, password);
            }
            st = con.createStatement();
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
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                return false;
            }
        }
        return true;
    }

    //write to database
    /**
     * Function 1: Fix Prime Hypocentre
     *
     * @param hypid
     * @param evid
     * @return
     */
    public static Boolean fixPrime(Integer hypid, Integer evid) {
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            cs = con.prepareCall("{? = call RF(?,?)}");

            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, hypid);
            cs.setInt(3, evid);

            cs.execute();

            int returnValue = cs.getInt(1);
            if (returnValue == 0) {
                sFlag = true;
            } else {
                sFlag = false;
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return sFlag;
    }

    /**
     * Function 2: Change attributes of hypocentre
     *
     * @param hypid
     * @param attribute
     * @param value
     * @return
     */
    static boolean changeHypo(Integer hypid, String attribute, String value) {

        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            cs = con.prepareCall("{? = call CHHypo(?,?,?)}");
            cs.registerOutParameter(1, Types.INTEGER);

            cs.setInt(2, hypid);
            cs.setString(3, attribute);
            cs.setString(4, value);

            cs.execute();

            int returnValue = cs.getInt(1);
            if (returnValue == 0) {
                sFlag = true;
            } else {
                sFlag = false;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return sFlag;
    }

    //Function 3: move hypo from one event to another
    static boolean putHypo(Integer hypid, Integer evidFrom, Integer evidTo) {

        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            cs = con.prepareCall("{? = call PutHypo(?,?,?)}");
            cs.registerOutParameter(1, Types.INTEGER);

            cs.setInt(2, hypid);
            cs.setInt(3, evidFrom);
            cs.setInt(4, evidTo);

            cs.execute();

            int returnValue = cs.getInt(1);
            if (returnValue == 0) {
                sFlag = true;
            } else {
                sFlag = false;
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return sFlag;
    }

    //Fuction 4: delete hypocentre
    static boolean deleteHypo(Integer hypid) {

        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            cs = con.prepareCall("{? = call deletehypo(?)}");

            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, hypid);
            //cs.setInt(3,evid);

            cs.execute();

            int returnValue = cs.getInt(1);
            if (returnValue == 0) {
                sFlag = true;
            } else {
                sFlag = false;
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return sFlag;
    }

    //reverse of Fuction 4
    public static boolean undeleteHypo(Integer hypid) {
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            cs = con.prepareCall("{? = call undeletehypo(?)}");

            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, hypid);
            //cs.setInt(3,evid);

            cs.execute();

            int returnValue = cs.getInt(1);
            if (returnValue == 0) {
                sFlag = true;
            } else {
                sFlag = false;
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return sFlag;
    }

    //Function 5 event level
    static boolean banishEvent(Integer evid) {
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            cs = con.prepareCall("{? = call banish(?)}");

            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, evid);
            //cs.setInt(3,evid);

            cs.execute();

            int returnValue = cs.getInt(1);
            if (returnValue == 0) {
                sFlag = true;
            } else {
                sFlag = false;
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return sFlag;
    }

    //reverse of function 5.
    static boolean unbanishEvent(Integer evid) {
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            cs = con.prepareCall("{? = call unbanish(?)}");

            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, evid);
            //cs.setInt(3,evid);

            cs.execute();

            int returnValue = cs.getInt(1);
            if (returnValue == 0) {
                sFlag = true;
            } else {
                sFlag = false;
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return sFlag;
    }

    /**
     * Separate a hypocentre and Create a new event
     *
     * @param hypid
     * @param evid
     * @return
     */
    static boolean createEvent(Integer hypid, Integer evid) {
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            cs = con.prepareCall("{? = call create_event(?,?)}");
            cs.registerOutParameter(1, Types.INTEGER);

            cs.setInt(2, hypid);
            cs.setInt(3, evid);

            cs.execute();

            int returnValue = cs.getInt(1);
            if (returnValue == 0) {
                sFlag = true;
            } else {
                sFlag = false;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return sFlag;
    }

    /**
     * Merge two events
     *
     * @param evidFrom
     * @param evidTo
     * @return
     */
    static boolean mergeEvent(Integer evidFrom, Integer evidTo) {
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            cs = con.prepareCall("{? = call merge(?,?)}");
            cs.registerOutParameter(1, Types.INTEGER);

            cs.setInt(2, evidFrom);
            cs.setInt(3, evidTo);

            cs.execute();

            int returnValue = cs.getInt(1);
            if (returnValue == 0) {
                sFlag = true;
            } else {
                sFlag = false;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return sFlag;
    }

    //Fuction 2: Change attributes of Phase
    static boolean changePhase(Integer phid, String attribute, String value) {

        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            cs = con.prepareCall("{? = call CHPhase(?,?,?)}");
            cs.registerOutParameter(1, Types.INTEGER);

            cs.setInt(2, phid);
            cs.setString(3, attribute);
            cs.setString(4, value);

            cs.execute();

            int returnValue = cs.getInt(1);
            if (returnValue == 0) {
                sFlag = true;
            } else {
                sFlag = false;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return sFlag;
    }

    //Function 3: move Phases from one event to another
    static boolean putPhase(Integer phid, Integer hypidFrom, Integer hypidTo, Integer newRDID) {

        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            cs = con.prepareCall("{? = call putphase(?,?,?,?)}");
            cs.registerOutParameter(1, Types.INTEGER);

            cs.setInt(2, phid);
            cs.setInt(3, hypidFrom);
            cs.setInt(4, hypidTo);
            if (newRDID == null) {
                cs.setNull(5, java.sql.Types.INTEGER);
            } else {
                cs.setInt(5, newRDID);
            }

            cs.execute();

            int returnValue = cs.getInt(1);
            if (returnValue == 0) {
                sFlag = true;
            } else {
                sFlag = false;
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return sFlag;
    }

    //delete phase by given phid
    static boolean deletePhase(Integer phid) {

        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            cs = con.prepareCall("{? = call deletephase(?)}");

            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, phid);
            //cs.setInt(3,evid);

            cs.execute();

            int returnValue = cs.getInt(1);
            if (returnValue == 0) {
                sFlag = true;
            } else {
                sFlag = false;
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return sFlag;
    }

    //take phase
    static boolean takePhase(Integer phid) {

        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            cs = con.prepareCall("{? = call takephase(?)}");

            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2, phid);
            //cs.setInt(3,evid);

            cs.execute();

            int returnValue = cs.getInt(1);
            if (returnValue == 0) {
                sFlag = true;
            } else {
                sFlag = false;
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        return sFlag;
    }

    //related with the scheduling 
    /**
     * as the list is not big, so use iteration to fill the events number
     *
     * @param bList the blocklist to fill the events number
     * @return
     */
    public static boolean retrieveBlockEventNumber(ArrayList<TaskBlock> bList) {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String query = "select ba.block_id, count(*)"
                + " From block_allocation ba, event_allocation ev"
                + " WHERE ba.id = ev.block_allocation_id AND ba.pass= 'p'"
                + " GROUP BY ba.block_id;";

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();
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
                if (con != null) {
                    con.close();
                }

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

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String query = "select ba.block_id, count(*)"
                + " From block_allocation ba, event_allocation ev"
                + " WHERE ba.id = ev.block_allocation_id AND ba.review = 0 AND ba.start IS NOT NULL AND ba.finish IS NULL and ev.start IS NOT NULL"
                + " GROUP BY ba.block_id;";

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();
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
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {

                return false;
            }
        }

        return true;
    }

    public static boolean loadBlocks(HashSet<TaskBlock> blockSet) {

        //clear the memory of blockArray in order to reload events
        //blockArray.clear();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String query = "SELECT b.id, b.starttime, b.endtime, b.region_id, a.name, ba.pass, ba.review, a.id, ba.planned_start, ba.planned_finish"
                + " FROM block b, block_allocation ba, analyst a"
                + " Where b.id = ba.block_id AND a.id = ba.analyst_id"
                + " ORDER BY b.id;";

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();
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
                if (con != null) {
                    con.close();
                }

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

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

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
                if (con != null) {
                    con.close();
                }

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

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        commandList.clear();

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

            String query = "SELECT ec.id, ec.command, ec.functions, a.name, ba.pass, ec.adddate, ec.type, ec.status"
                    + " FROM analyst a, edit_commands ec, block_allocation ba"
                    + " WHERE ec.evid = " + evid
                    + " AND ec.type != 'assess'"
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
                if (con != null) {
                    con.close();
                }

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

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

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
                if (con != null) {
                    con.close();
                }

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

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String query = "";

        assessedCommandList.clear();

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

            query = "SELECT ba.pass, a.name, ec.id AS assessid, ec.command AS command, eca.id AS cmdids\n"
                    + " FROM analyst a, edit_commands ec, block_allocation ba, command_group cg, edit_commands eca\n"
                    + " WHERE ec.evid = " + evid
                    + " AND ec.type = 'assess'\n"
                    + " AND ba.id = ec.block_allocation_id\n"
                    + " AND ba.analyst_id = a.id\n"
                    + " AND cg.evid = eca.evid\n"
                    + " AND cg.id = ec.id\n"
                    + " AND cg.edit_commands_id = eca.id\n"
                    + " ORDER BY ec.adddate;";

            rs = st.executeQuery(query);

            VBASLogger.logDebug("query= " + query);
            VBASLogger.logDebug("rs= " + rs);

            Hashtable<Integer, AssessedCommand> hashtable = new Hashtable<Integer, AssessedCommand>();;

            //VBASLogger.logDebug("commandId" + " | " + "analyst" + " | " + "report" + " | " + "pass" + " | " + "assessId");
            while (rs.next()) {
                String analyst = rs.getString("name");
                String pass = rs.getString("pass");
                String report = rs.getString("command");  // we store the html report in the command field.
                int assessId = rs.getInt("assessid");
                String commandId = rs.getString("cmdids");

                //VBASLogger.logDebug(commandId + " | " + analyst + " | " + report + " | " + pass + " | " + assessId);
                AssessedCommand ac = hashtable.get(assessId);
                if (ac == null) {
                    ac = new AssessedCommand(assessId, evid, commandId, analyst, report);
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
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                return false;
            }
        }
        return true;
    }

    /*
     * Update the database when an event is done.
     */
    public static Boolean updateSeiesEventDone(Integer evid) {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String query = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

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
            rs = st.executeQuery(query);
            rs.close();

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
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                return false;
            }
        }

        return true;
    }

    // Commented by Hui
    /**
     * In order to retrieve the magnitude more efficiently for the whole scheme,
     * we use one query to get all the magnitude and compare the evid for
     * setting them to the event retrieve events' magnitude, actually it
     * retrieves the magnitudes of primehypo
     *
     * @param evList
     * @return all the events filling with magnitude
     */
    /*
     public static boolean retrieveAllEventsMagnitude(ArrayList<SeisEvent> evList) 
     {
     Connection con = null;
     Statement st = null;
     ResultSet rs = null;
        
     try {
            
     con = DriverManager.getConnection(url, pgUser, password);
     st = con.createStatement();
                
     String query = "SELECT n.magtype, n.magnitude, e.evid"
     + " FROM  event e, netmag n"
     + " WHERE n.hypid = e.prime_hyp" 
     + " AND n.deprecated is NULL;";

     rs = st.executeQuery(query);

     while (rs.next()) 
     {       
     //check this later, do I need add the event magnitude into hypo?                
     for (SeisEvent ev:evList)
     {        
     if(rs.getString(3).equals(ev.getEvid()))
     {                      
     ev.getPrimeHypo().addMagnitude(rs.getString(1),rs.getDouble(2));
     //ev.setMagnitude(rs.getDouble(2));
     break;
     }
     }
     }
                
     rs.close();
                
     }
     catch (SQLException ex) {
     return false;
     } finally {
     try {
     if (rs != null) {
     rs.close();
     }
     if (st != null) {
     st.close();
     }
     if (con != null) {
     con.close();
     }

     } catch (SQLException ex) {
     return false;
     }
     }
        
     return true;
     }
     */
    
    
    // Note: Commented by Me: Saiful Khan 
    // Note: These are used by test class
    /**
     * retrieve events list first based on date defined
     *
     * @param seisEvents the space for keeping the reviewing events
     * @param from the Date from
     * @param to the Date to
     * @return all the events during the date
     * @throws java.text.ParseException
     */
    /*
     public static boolean retrieveEvents(ArrayList<SeisEvent> seisEvents, Date from, Date to) throws ParseException {
     //clear the memory of seisEvent in order to reload events
     seisEvents.clear();

     Connection con = null;
     Statement st = null;
     ResultSet rs = null;

     DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
     String dfrom = df.format(from);
     String dto = df.format(to);

     String query = "SELECT e.evid, h.author, h.day, h.lat, h.lon, h.depth, e.etype, get_default_depth_grid(h.lat,h.lon)"
     + " FROM event e, hypocenter h"
     + " WHERE e.prime_hyp = h.hypid AND h.day BETWEEN '" + dfrom
     + "' AND '" + dto
     + "' AND h.isc_evid = e.evid AND e.banished IS NULL AND e.ready IS NOT NULL"
     + " AND h.deprecated is NULL AND h.hypid = h.pref_hypid"
     + " ORDER BY h.day ASC;";

     try {
     con = DriverManager.getConnection(url, pgUser, password);
     st = con.createStatement();
     rs = st.executeQuery(query);

     while (rs.next()) {
     int evid = rs.getInt(1);
     SeisEvent tmp = new SeisEvent(evid, rs.getString(7), rs.getDouble(8), SeisDataDAO.getLocatorMessage(evid));
               
     Date dd = null;

     try {
     dd = df.parse(rs.getString(3));
     } catch (ParseException e) {
     return false;
     }

     Hypocentre ph = new Hypocentre(rs.getString(2), dd, rs.getDouble(4), rs.getDouble(5), rs.getInt(6));
     ph.setIsPrime(true);
     tmp.setPrimeHypo(ph);
     seisEvents.add(tmp);
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
     if (con != null) {
     con.close();
     }

     } catch (SQLException ex) {
     return false;
     }
     }
     return true;
     }
    
     */
    /**
     * retrieve events list first based on date defined
     *
     * @param seisEvents the space for keeping the reviewing events
     * @param evids the list of evid
     * @return all the events during the date
     * @throws java.text.ParseException
     */
    /*
     public static boolean retrieveEventsByEvList(ArrayList<SeisEvent> seisEvents, ArrayList<String> evids) throws ParseException {
     //clear the memory of seisEvent in order to reload events
     seisEvents.clear();

     Connection con = null;
     Statement st = null;
     ResultSet rs = null;

     DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     Iterator<String> iter = evids.iterator();

     try {
     con = DriverManager.getConnection(url, pgUser, password);
     st = con.createStatement();

     while (iter.hasNext()) {
     String evid = iter.next();

     String query = "SELECT e.evid, h.author, h.day, h.lat, h.lon, h.depth, e.etype, get_default_depth_grid(h.lat,h.lon)"
     + " FROM event e, hypocenter h"
     + " WHERE e.prime_hyp = h.hypid AND e.evid = " + evid
     + " AND h.isc_evid = e.evid AND e.banished IS NULL AND e.ready IS NOT NULL"
     + " AND h.deprecated is NULL AND h.hypid = h.pref_hypid"
     + " ORDER BY h.day ASC;";

     rs = st.executeQuery(query);

     while (rs.next()) {

     int eventID = rs.getInt(1);
     SeisEvent tmp = new SeisEvent(eventID, rs.getString(7), rs.getDouble(8), SeisDataDAO.getLocatorMessage(eventID));
               
     //SeisEvent tmp = new SeisEvent(rs.getInt(1), rs.getString(7), rs.getDouble(8));
     Date dd = null;

     try {
     dd = df.parse(rs.getString(3));
     } catch (ParseException e) {
     //logger later
     System.out.println("Can't parse the Date of events");
     }

     Hypocentre ph = new Hypocentre(rs.getString(2), dd, rs.getDouble(4), rs.getDouble(5), rs.getInt(6));
     ph.setIsPrime(true);
     tmp.setPrimeHypo(ph);

     seisEvents.add(tmp);
     }

     rs.close();
     }

     } catch (SQLException ex) {
     //return false;
     } finally {
     try {
     if (rs != null) {
     rs.close();
     }
     if (st != null) {
     st.close();
     }
     if (con != null) {
     con.close();
     }

     } catch (SQLException ex) {

     }
     }

     return true;
     }

     */
    
    

   
    /**
     * fill events' location in the control panel
     *
     * @param evList list with no region names
     * @return all the events filling with their region names
     */
     /*
    public static boolean retrieveRegionName(ArrayList<SeisEvent> evList) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(url, pgUser, password);
            st = con.createStatement();

            for (SeisEvent ev : evList) {
                String query = "SELECT r.gr_short, r.gr_number, r.sr_number "
                        + " FROM  region r, event e, hypocenter h"
                        + " WHERE h.isc_evid = " + ev.getEvid()
                        + " AND e.evid = h.isc_evid AND h.hypid = e.prime_hyp"
                        + " AND r.gr_number = grn_ll(h.lat,h.lon);";

                rs = st.executeQuery(query);

                while (rs.next()) {
                    ev.setLocation(rs.getString(1));
                    ev.setGrn(rs.getInt(2));
                    ev.setSrn(rs.getInt(3));
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
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                return false;
            }
        }

        return true;
    }

    */
}
