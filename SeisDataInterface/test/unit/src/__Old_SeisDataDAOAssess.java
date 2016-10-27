

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.Phase;
import uk.ac.isc.seisdata.VBASLogger;

/**
 * This is the database access object which provides functions to read and write
 * ISC database
 */
public final class __Old_SeisDataDAOAssess {

    protected static String url;
    protected static String assessUser;
    protected static String assessPassword;
    protected static String pgUser;
    private static Path assessDir = null;

    static {
        //String osName = System.getProperty("os.name");
        VBASLogger.logDebug(System.getProperty("os.name"));
        //if (osName.equals("Linux")) {
        Map<String, String> env = System.getenv();
        url = "jdbc:postgresql://"
                + env.get("PGHOSTADDR") + ":"
                + env.get("PGPORT") + "/"
                + env.get("PGDATABASE");
        assessUser = env.get("ASSESS_USER");
        assessPassword = env.get("ASSESS_PW");
        pgUser = env.get("PGUSER");
        assessDir = Paths.get(env.get("ASSESSDIR"));
         
        

        VBASLogger.logDebug("url=" + url + ", user=" + assessUser
                + ", password=" + assessPassword + ", assessDir=" + assessDir);
    }

    private __Old_SeisDataDAOAssess() {
        //
    }

    

    /**
     * Get all the hypocentres
     *
     * @param evid
     * @param HypoList
     * @return
     */
    public static boolean retrieveHypos(Integer evid, ArrayList<Hypocentre> HypoList) {
        VBASLogger.logDebug("url=" + url + ", user=" + assessUser + ", password=" + assessPassword);

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        HypoList.clear();

        try {
            con = DriverManager.getConnection(url, assessUser, assessPassword);
            st = con.createStatement();

            String query
                    = "SELECT h.author, h.day, h.lat, h.lon, h.depth, h.prime, h.hypid, x.sdepth, h.epifix, x.stime, x.strike, x.smajax, x.sminax,h.nass, h.ndef, h.nsta, h.ndefsta, h.msec, x.sdobs"
                    + " FROM hypocenter h LEFT OUTER JOIN hypoc_err x ON x.hypid = h.hypid"
                    + " WHERE h.deprecated is NULL AND h.hypid = h.pref_hypid AND h.isc_evid = "
                    + evid
                    + " ORDER BY h.prime DESC, h.author";

            rs = st.executeQuery(query);
            
            VBASLogger.logDebug("Executing query:" + query);
            
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
                
                if(dd == null) {
                     String message =  "origTime = null. \nReport to the system admin.";
                        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
                        VBASLogger.logSevere(message);
                }

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
     * Retrieve hypocentre magnitudes list
     *
     * @param HypoList
     * @return
     */
    public static boolean retrieveHyposMagnitude(ArrayList<Hypocentre> HypoList) {
        VBASLogger.logDebug("url=" + url + ", user=" + assessUser + ", password=" + assessPassword);

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        Iterator<Hypocentre> iter = HypoList.iterator();
        try {
            con = DriverManager.getConnection(url, assessUser, assessPassword);
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
     *
     * @param evid all phases associated to the event id
     * @param PhasesList empty phase list
     * @return filled phases list
     */
    public static boolean retrieveAllPhases(Integer evid, ArrayList<Phase> PhasesList) {
        VBASLogger.logDebug("url=" + url + ", user=" + assessUser + ", password=" + assessPassword);

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        PhasesList.clear();

        try {

            con = DriverManager.getConnection(url, assessUser, assessPassword);
            st = con.createStatement();

            String query = "SELECT r.reporter, p.sta, p.day, a.delta, a.esaz, a.phase, a.timeres, p.phid, p.phase, a.timedef, p.rdid, s.staname, p.msec, p.slow, p.azim, i.snr, a.phase_fixed "
                    + "FROM event e, association a, report r, site s, phase p LEFT OUTER JOIN phase_info i "
                    + "ON p.phid = i.phid "
                    + "WHERE e.prime_hyp = a.hypid AND p.reporter = r.repid "
                    + "AND a.phid = p.phid  AND a.author = 'ISC' AND p.sta = s.sta AND p.net IS NULL AND s.net IS NULL AND e.evid = " + evid
                    + " ORDER BY a.delta ASC;";

            rs = st.executeQuery(query);

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
     * Retrieve all the phase ampmag and save them back to each phase in the
     * list
     *
     * @param evid
     * @param PhaseList
     * @return
     */
    public static boolean retrieveAllPhasesAmpMag(Integer evid, ArrayList<Phase> PhaseList) {
        VBASLogger.logDebug("url=" + url + ", user=" + assessUser + ", password=" + assessPassword);

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        //build a hashmap for quick search
        HashMap phaseListMap = new HashMap<Integer, Phase>();
        for (Phase p : PhaseList) {
            phaseListMap.put(p.getPhid(), p);
        }

        try {
            con = DriverManager.getConnection(url, assessUser, assessPassword);
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
        VBASLogger.logDebug("url=" + url + ", user=" + assessUser + ", password=" + assessPassword);

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String query = "SELECT s.sta, r.gr_short "
                + "FROM site s, site_grn g, region r "
                + "WHERE s.net IS NULL AND s.lat IS NOT NULL AND s.lon IS NOT NULL "
                + "AND s.sta = g.sta AND g.net IS NULL AND g.grn_ll = r.gr_number;";

        try {
            con = DriverManager.getConnection(url, assessUser, assessPassword);
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
    
}
