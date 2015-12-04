
package uk.ac.isc.databasetest;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author hui
 */
public final class SeisDataDAO {
    
    /**
     * Loading user name, password and scheme from system environment
     */
    static {         
        //will load from from bashrc for personal login later, here is for testing
        //url = "jdbc:postgresql://192.168.37.91:5432/isc";  
        //url = "jdbc:postgresql://127.0.0.1:5432/isc";
        //user = "hui";
        //password = "njustga";
        Map<String, String> env = System.getenv();
        url = "jdbc:postgresql://"+env.get("PGHOSTADDR")+":"+env.get("PGPORT")+"/"+env.get("PGDATABASE");
        user = env.get("PGUSER");
        password = env.get("PGPASSWORD"); 
    }
    
    private static final String url; //= "jdbc:postgresql://192.168.37.91:5432/isc";      
    //private static final String url = "jdbc:postgresql://127.0.0.1:5432/isc"; 
    private static final String user; //= "hui";
    private static final String password; //= "njustga"; 

    private SeisDataDAO() {

    }
    
    /**
     * retrieve all the events in a user's schema
     * @param seisEvents for saving the events list
     * @return success flag
     */
    public static boolean retrieveAllEvents(ArrayList<SeisEvent> seisEvents) {
         //clear the memory of seisEvent in order to reload events
        seisEvents.clear();
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        
        String query = "SELECT e.evid, h.author, h.day, h.lat, h.lon, h.depth" +
                " FROM event e, hypocenter h" +
                " WHERE e.prime_hyp = h.hypid" +
                " AND h.isc_evid = e.evid AND e.banished IS NULL AND e.ready IS NOT NULL" +
                " AND h.deprecated is NULL AND h.hypid = h.pref_hypid" +
                " ORDER BY h.day ASC;";

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                
                SeisEvent tmp = new SeisEvent(rs.getInt(1));
                Date dd = null;
                
                try {
                   dd = df.parse(rs.getString(3));
                } catch (ParseException e)
                {
                    return false;
                }
                
                Hypocentre ph = new Hypocentre(rs.getString(2), dd, rs.getDouble(4),rs.getDouble(5), rs.getInt(6));
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
    
    /**
     * retrieve events list first based on date defined
     * @param seisEvents the space for keeping the reviewing events 
     * @param from the Date from 
     * @param to the Date to
     * @return all the events during the date
     * @throws java.text.ParseException
     */
    public static boolean retrieveEvents(ArrayList<SeisEvent> seisEvents, Date from, Date to) throws ParseException
    {
        //clear the memory of seisEvent in order to reload events
        seisEvents.clear();
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
         
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dfrom = df.format(from);
        String dto = df.format(to);
                
        String query = "SELECT e.evid, h.author, h.day, h.lat, h.lon, h.depth" +
                " FROM event e, hypocenter h" +
                " WHERE e.prime_hyp = h.hypid AND h.day BETWEEN '" + dfrom +
                "' AND '" + dto +
                "' AND h.isc_evid = e.evid AND e.banished IS NULL AND e.ready IS NOT NULL" +
                " AND h.deprecated is NULL AND h.hypid = h.pref_hypid" +
                " ORDER BY h.day ASC;";

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                
                SeisEvent tmp = new SeisEvent(rs.getInt(1));
                Date dd = null;
                
                try {
                   dd = df.parse(rs.getString(3));
                } catch (ParseException e)
                {
                    return false;
                }
                
                Hypocentre ph = new Hypocentre(rs.getString(2), dd, rs.getDouble(4),rs.getDouble(5), rs.getInt(6));
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
    
    /**
     * retrieve events list first based on date defined
     * @param seisEvents the space for keeping the reviewing events 
     * @param evids the list of evid
     * @return all the events during the date
     * @throws java.text.ParseException
     */
    public static boolean retrieveEventsByEvList(ArrayList<SeisEvent> seisEvents, ArrayList<String> evids) throws ParseException
    {
        //clear the memory of seisEvent in order to reload events
        seisEvents.clear();
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");       
        Iterator<String> iter = evids.iterator();
         
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            
            while(iter.hasNext())
            {
                String evid = iter.next();
                
                String query = "SELECT e.evid, h.author, h.day, h.lat, h.lon, h.depth" +
                " FROM event e, hypocenter h" +
                " WHERE e.prime_hyp = h.hypid AND e.evid = " + evid
                + " AND h.isc_evid = e.evid AND e.banished IS NULL AND e.ready IS NOT NULL" +
                " AND h.deprecated is NULL AND h.hypid = h.pref_hypid" +
                " ORDER BY h.day ASC;";

                rs = st.executeQuery(query);

                while (rs.next()) {
                
                    SeisEvent tmp = new SeisEvent(rs.getInt(1));
                    Date dd = null;
                
                    try {
                        dd = df.parse(rs.getString(3));
                    } catch (ParseException e)
                    {
                        //logger later
                        System.out.println("Can't parse the Date of events");
                    }
                
                    Hypocentre ph = new Hypocentre(rs.getString(2), dd, rs.getDouble(4),rs.getDouble(5), rs.getInt(6));
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
    
    /**
     * retrieve events' magnitude, actually it retrieves the magnitudes of primehypo
     * @param evList
     * @return all the events filling with magnitude
     */
    public static boolean retrieveEventsMagnitude(ArrayList<SeisEvent> evList) 
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        try {
                con = DriverManager.getConnection(url, user, password);
                st = con.createStatement();
                
//                String query = "SELECT e.evid, n.magtype, n.magnitude"
//                        + " FROM  event e, netmag n"
//                        + " WHERE n.hypid = e.prime_hyp" 
//                        + " AND n.deprecated is NULL"
//                        + " ORDER BY evid, ( CASE WHEN n.magtype = 'MS' THEN 10"
//                        + " ELSE 1" 
//                        + " END ) DESC;";

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
                HashMap<Integer,Double> ht = new HashMap<Integer,Double>();
                
                while (rs.next()) {       
                        //check this later, do I need add the event magnitude into hypo?
                        //ev.getPrimeHypo().addMagnitude(rs.getString(1),rs.getDouble(2));
                        ht.put(rs.getInt(1), rs.getDouble(3));
                }   
                
                for (SeisEvent ev:evList)
                {
                        //int a = rs.getInt(1);
                    ev.setMagnitude(ht.get(ev.getEvid()));
      
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
    
    /**In order to retrieve the magnitude more efficiently for the whole scheme,
     * we use one query to get all the magnitude and compare the evid for setting them
     * to the event
     * retrieve events' magnitude, actually it retrieves the magnitudes of primehypo
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
            
            con = DriverManager.getConnection(url, user, password);
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
    
    /**
     * fill events' location in the control panel
     * @param evList list with no region names
     * @return all the events filling with their region names
     */
    public static boolean retrieveRegionName(ArrayList<SeisEvent> evList) 
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        try {
                con = DriverManager.getConnection(url, user, password);
                st = con.createStatement();
                
                for (SeisEvent ev:evList)
                {        
                    String query = "SELECT r.gr_name, r.gr_number, r.sr_number "
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
    
    /**
     * fill events' location in the control panel
     * @param evList list with no region names
     * @return all the events filling with their region names
     */
    public static boolean retrieveAllRegionName(ArrayList<SeisEvent> evList) 
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        try {
                con = DriverManager.getConnection(url, user, password);
                st = con.createStatement();
                
                String query = "SELECT r.gr_name, r.gr_number, r.sr_number, e.evid "
                        + " FROM  region r, event e, hypocenter h"
                        + " WHERE e.ready IS NOT NULL AND e.evid = h.isc_evid AND h.hypid = e.prime_hyp"
                        + " AND r.gr_number = COALESCE(h.grn,grn_ll(h.lat,h.lon));";

                rs = st.executeQuery(query);
                 
                HashMap<Integer,String> hm1 = new HashMap<Integer,String>();
                HashMap<Integer,Integer> hm2 = new HashMap<Integer,Integer>();
                HashMap<Integer,Integer> hm3 = new HashMap<Integer,Integer>();
                
                while (rs.next()) {       
                    hm1.put(rs.getInt(4),rs.getString(1));
                    hm2.put(rs.getInt(4),rs.getInt(2));
                    hm3.put(rs.getInt(4),rs.getInt(3));
                }
                
                for (SeisEvent ev:evList)
                {    
                    ev.setLocation(hm1.get(ev.getEvid()));
                    ev.setGrn(hm2.get(ev.getEvid()));
                    ev.setSrn(hm3.get(ev.getEvid())); 
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
    
    public static boolean retrievePhaseNumber(ArrayList<SeisEvent> evList) 
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        try {
                con = DriverManager.getConnection(url, user, password);
                st = con.createStatement();
                
                for (SeisEvent ev:evList)
                {        
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
    
    static Integer getNextNewEvid() {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        Integer retEvid = null;
        try {
                con = DriverManager.getConnection(url, user, password);
                st = con.createStatement();
                String query = "SELECT NEXTVAL('isc.evid');";
                rs = st.executeQuery(query);
                while(rs.next())
                {
                    retEvid = rs.getInt(1);
                }
        }
        catch (SQLException ex) {
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
    
    static Integer getNextNewRdid() {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        Integer retRdid = null;
        try {
                con = DriverManager.getConnection(url, user, password);
                st = con.createStatement();
                String query = "SELECT NEXTVAL('isc.rdid');";
                rs = st.executeQuery(query);
                while(rs.next())
                {
                    retRdid = rs.getInt(1);
                }
        }
        catch (SQLException ex) {
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
        
        return retRdid;
        
    }
    
    /**
     * Here is for retrieving the phase number for each event, 
     * difference with the previous one is that this one can retrieve the phase nmber
     * @param evList
     * @return 
     */
    public static boolean retrieveAllPhaseNumber(ArrayList<SeisEvent> evList) 
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        try {
                con = DriverManager.getConnection(url, user, password);
                st = con.createStatement();
                
                String query = "SELECT COUNT(*), e.evid"
                        + " FROM  association a, event e"
                        + " WHERE a.hypid = e.prime_hyp"
                        + " AND a.author='ISC' group by e.evid;";

                rs = st.executeQuery(query);
                
                while (rs.next()) {
                    for (SeisEvent ev:evList)
                    {     
                        if(rs.getInt(2)==ev.getEvid())
                        {
                            ev.setPhaseNumber(rs.getInt(1));
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
    
    /**
     * retrieve all the hypocentres into the event
     * @param evid
     * @param HypoList
     * @return 
     */
    /*public static boolean retrieveHypos(ArrayList<String> evids, ArrayList<Hypocentre> HypoList) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        HypoList.clear();
        Iterator<String> iter = evids.iterator();

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            
            while(iter.hasNext())
            {
                String evid = iter.next();
                
                String query = "SELECT h.author, h.day, h.lat, h.lon, h.depth, h.prime, h.hypid, x.sdepth, h.epifix, x.stime, x.strike, x.smajax, x.sminax,h.nass, h.ndef, h.nsta, h.ndefsta "
                + " FROM hypocenter h, hypoc_err x "
                + " WHERE h.deprecated is NULL AND h.hypid = h.pref_hypid AND x.hypid = h.hypid AND h.isc_evid = " 
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
                    } catch (ParseException e)
                    {
                        return false;
                    }
                    tmp.setOrigTime(dd);
                
                    tmp.setLat(rs.getDouble(3));
                    tmp.setLon(rs.getDouble(4));
                    tmp.setDepth(rs.getInt(5));
                    tmp.setHypid(rs.getString(7));
                    
                    if (rs.getString(6) != null)
                    {
                        tmp.setIsPrime(true);
                    }
                 
                    tmp.setErrDepth(rs.getDouble(8));
                    
                    if (rs.getString(9) != null)
                    {
                        tmp.setIsFixed(true);
                    }
                    
                    tmp.setStime(rs.getDouble(10));
                    tmp.setStrike(rs.getDouble(11));
                    tmp.setSmajax(rs.getDouble(12));
                    tmp.setSminax(rs.getDouble(13));
                    
                    tmp.setNumPhases(rs.getInt(14));
                    tmp.setNumDefPhases(rs.getInt(15));
                    tmp.setNumStations(rs.getInt(16));
                    tmp.setNumDefStations(rs.getInt(17));
                    
                    HypoList.add(tmp);

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
        
    }*/
    
    public static boolean retrieveHypos(Integer evid, ArrayList<Hypocentre> HypoList) {
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        HypoList.clear();

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
                
            String query = "SELECT h.author, h.day, h.lat, h.lon, h.depth, h.prime, h.hypid, x.sdepth, h.epifix, x.stime, x.strike, x.smajax, x.sminax,h.nass, h.ndef, h.nsta, h.ndefsta, h.msec "
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
                } catch (ParseException e)
                {
                   return false;
                }
                tmp.setOrigTime(dd);
                
                tmp.setLat(rs.getDouble(3));
                tmp.setLon(rs.getDouble(4));
                tmp.setDepth(rs.getInt(5));
                tmp.setHypid(rs.getInt(7));
                    
                if (rs.getString(6) != null)
                {
                    tmp.setIsPrime(true);
                }
                else
                {
                    tmp.setIsPrime(false);
                }
                 
                if(rs.getObject(8)!=null)
                {
                    tmp.setErrDepth(rs.getDouble(8));
                }
                    
                if (rs.getString(9) != null)
                {
                    tmp.setIsFixed(true);
                }
                else
                {
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
    
    public static Hypocentre retrieveSingleHypo(Integer hypid) {
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        Hypocentre tmp = new Hypocentre(); 

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
                
            String query = "SELECT h.author, h.day, h.lat, h.lon, h.depth, h.prime, h.hypid, x.sdepth, h.epifix, x.stime, x.strike, x.smajax, x.sminax,h.nass, h.ndef, h.nsta, h.ndefsta, h.msec "
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
                } catch (ParseException e)
                {
                   System.out.println("Parsing Date Error!");
                }
                tmp.setOrigTime(dd);
                
                tmp.setLat(rs.getDouble(3));
                tmp.setLon(rs.getDouble(4));
                tmp.setDepth(rs.getInt(5));
                tmp.setHypid(rs.getInt(7));
                    
                if (rs.getString(6) != null)
                {
                    tmp.setIsPrime(true);
                }
                else
                {
                    tmp.setIsPrime(false);
                }
                 
                if(rs.getObject(8)!=null)
                {
                    tmp.setErrDepth(rs.getDouble(8));
                }
                    
                if (rs.getString(9) != null)
                {
                    tmp.setIsFixed(true);
                }
                else
                {
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
    
    public static Double retrieveDefaultGridDepth(Integer hypid)
    {
        Double depth = null;
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        try {
            con = DriverManager.getConnection(url, user, password);
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
            
    public static boolean retrieveHyposMagnitude(ArrayList<Hypocentre> HypoList) {
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        Iterator<Hypocentre> iter = HypoList.iterator();
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            
            while(iter.hasNext())
            {
                Hypocentre currentHypo = iter.next();
                Integer hypid = currentHypo.getHypid();
                
                String query = "SELECT n.magtype, n.magnitude"
                + " FROM netmag n"
                + " WHERE n.deprecated is NULL AND n.magtype is NOT NULL AND n.hypid = " 
                + hypid + " ORDER BY ( CASE WHEN n.magtype = 'mb' THEN 1 WHEN magtype = 'MS' THEN 2 WHEN magtype = 'MW' THEN 3 ELSE 4 END) ASC";

                rs = st.executeQuery(query);

                while (rs.next()) {
                    
                    currentHypo.addMagnitude(rs.getString(1),rs.getDouble(2));
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
    
    
    public static boolean retrieveHistEvents(ArrayList<HistoricEvent> histEvents, double latN, double latS, double lonW, double lonE)
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        histEvents.clear();
        
        String query;
        if(lonW>-180 && lonE <180)
        {
            query = "SELECT evid, depth, lat, lon FROM historical_seismicity WHERE lat >= " + String.valueOf(latS) + 
                " AND lat <= " + String.valueOf(latN) + " AND lon >= " + String.valueOf(lonW) + " AND lon <= "
                + String.valueOf(lonE) + " ORDER BY depth ASC;";
        }
        else if(lonW<-180)
        {
            lonW += 360;
            query = "SELECT evid, depth, lat, lon FROM historical_seismicity WHERE lat >= " + String.valueOf(latS) + 
                " AND lat <= " + String.valueOf(latN) + 
                " AND (lon BETWEEN " + String.valueOf(-180) + " AND " + String.valueOf(lonE) +") "
                    + "OR (lon BETWEEN "+ String.valueOf(lonW) + " AND 180)" + " ORDER BY depth ASC;";
        }
        else 
        {
            lonE -= 360;
            query = "SELECT evid, depth, lat, lon FROM historical_seismicity WHERE lat >= " + String.valueOf(latS) + 
                " AND lat <= " + String.valueOf(latN) + 
                " AND (lon BETWEEN " + String.valueOf(lonW) + " AND 180)" +
                    " OR (lon BETWEEN -180 AND "+ String.valueOf(lonE) + ")" + " ORDER BY depth ASC;";
        }
        
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                HistoricEvent tmp = new HistoricEvent(rs.getInt(1),rs.getInt(2),rs.getDouble(3),
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
      
    /*public static boolean retrieveAllPhases(ArrayList<String> evids, ArrayList<Phase> PhaseList)
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        PhaseList.clear();
        Iterator<String> iter = evids.iterator();

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            
            while(iter.hasNext())
            {
                String evid = iter.next();
                
                //String query = "SELECT DISTINCT r.reporter, p.sta, p.day, a.delta, a.esaz, a.phase, a.timeres, p.phid, p.phase, a.timedef, "
                //+ "t.amp, t.per, g.magnitude "
                //+ "FROM event e, association a, phase p, report r, amplitude t, ampmag g "
                //+ "WHERE e.prime_hyp = a.hypid AND p.reporter = r.repid "
                //+ "AND t.phid = p.phid AND t.ampid = p.ampid AND g.ampid = t.ampid "
                //+ "AND a.phid = p.phid  AND a.author = 'ISC' AND e.evid = " + evid 
                //+ " ORDER BY a.delta ASC;";
                
                String query = "SELECT DISTINCT r.reporter, p.sta, p.day, a.delta, a.esaz, a.phase, a.timeres, p.phid, p.phase, a.timedef, p.rdid, s.staname "
                + "FROM event e, association a, phase p, report r, site s "
                + "WHERE e.prime_hyp = a.hypid AND p.reporter = r.repid "
                + "AND a.phid = p.phid  AND a.author = 'ISC' AND p.sta = s.sta AND e.evid = " + evid 
                + " ORDER BY a.delta ASC;";
                
                rs = st.executeQuery(query);

                while (rs.next()) {
                
                    //PrimeHypocentre primetmp = null;
                    Phase tmp = new Phase();
                    
                    tmp.setReportAgency(rs.getString(1));
                    tmp.setReportStation(rs.getString(2));
                    tmp.setDistance(rs.getDouble(4));
                    tmp.setAzimuth(rs.getDouble(5));
                    tmp.setIscPhaseType(rs.getString(6));//.setOrigPhaseType(rs.getString(5));
                    tmp.setTimeResidual(rs.getDouble(7));
                    tmp.setPhid(rs.getString(8));
                    tmp.setOrigPhaseType(rs.getString(9));
                    
                    if(rs.getString(10) == null)
                    {
                        tmp.setDefining(false);
                    }
                    else
                    {
                        tmp.setDefining(true);
                    }
                    
                    tmp.setRdid(rs.getString(11));
                    tmp.setStationFullName(rs.getString(12));
                    //tmp.setAmplitude(rs.getDouble(11));
                    //tmp.setPeriod(rs.getDouble(12));
                    //tmp.setAmpMag(rs.getDouble(13));
                    
                    Date dd = null;
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        dd = df.parse(rs.getString(3));
                    } catch (ParseException e)
                    {

                    }
                    catch (NullPointerException ne)
                    {
                        //System.out.println(rs.getString(3));
                    }
                                        
                    tmp.setArrivalTime(dd);
                    
                    PhaseList.add(tmp);

                }
                rs.close();
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
    
    }*/
    
    /**
     * 
     * @param evid all phases associated to the event id
     * @param PhasesList empty phase list 
     * @return filled phases list
     */
    public static boolean retrieveAllPhases(Integer evid, ArrayList<Phase> PhasesList)
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        PhasesList.clear();

        try {
            
            con = DriverManager.getConnection(url, user, password);
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
                
                if(rs.getObject(7)!=null)
                {
                    tmp.setTimeResidual(rs.getDouble(7));
                }
                
                tmp.setPhid(rs.getInt(8));
                tmp.setOrigPhaseType(rs.getString(9));
                
                if(rs.getString(10) == null)
                {
                    tmp.setDefining(false);
                }
                else
                {
                    tmp.setDefining(true);
                }
                    
                tmp.setRdid(rs.getInt(11));
                tmp.setStationFullName(rs.getString(12));
                    //tmp.setAmplitude(rs.getDouble(11));
                    //tmp.setPeriod(rs.getDouble(12));
                tmp.setMsec(rs.getInt(13));   
                tmp.setSlowness(rs.getDouble(14));
                
                if(rs.getObject(15)!=null)
                {
                    tmp.setSeAzimuth(rs.getDouble(15));
                }
                
                if(rs.getString(16)!=null)
                {
                    tmp.setSNRRate(Double.valueOf(rs.getString(16)));
                }
                
                if(rs.getString(17)!=null)
                {
                    tmp.setFixing(Boolean.TRUE);
                }
                else
                {
                    tmp.setFixing(Boolean.FALSE);
                }
                
                Date dd = null;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    dd = df.parse(rs.getString(3));
                } catch (ParseException e)
                {
                    return false;
                }
                catch (NullPointerException ne)
                {
                        //System.out.println(rs.getString(3));
                }                        
                tmp.setArrivalTime(dd);
                    
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
    
    public static Phase retrieveSinglePhase(Integer phid)
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

                        
        Phase tmp = new Phase();
        try {
            
            con = DriverManager.getConnection(url, user, password);
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
                
                if(rs.getObject(7)!=null)
                {
                    tmp.setTimeResidual(rs.getDouble(7));
                }
                
                tmp.setPhid(rs.getInt(8));
                tmp.setOrigPhaseType(rs.getString(9));
                
                if(rs.getString(10) == null)
                {
                    tmp.setDefining(false);
                }
                else
                {
                    tmp.setDefining(true);
                }
                    
                tmp.setRdid(rs.getInt(11));
                tmp.setStationFullName(rs.getString(12));
                    //tmp.setAmplitude(rs.getDouble(11));
                    //tmp.setPeriod(rs.getDouble(12));
                tmp.setMsec(rs.getInt(13));   
                tmp.setSlowness(rs.getDouble(14));
                
                if(rs.getObject(15)!=null)
                {
                    tmp.setSeAzimuth(rs.getDouble(15));
                }
                
                if(rs.getString(16)!=null)
                {
                    tmp.setSNRRate(Double.valueOf(rs.getString(16)));
                }
                
                if(rs.getString(17)!=null)
                {
                    tmp.setFixing(Boolean.TRUE);
                }
                else
                {
                    tmp.setFixing(Boolean.FALSE);
                }
                                
                Date dd = null;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    dd = df.parse(rs.getString(3));
                } catch (ParseException e)
                {
                  
                }
                catch (NullPointerException ne)
                {
                        //System.out.println(rs.getString(3));
                }                        
                tmp.setArrivalTime(dd);
                
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
    
    public static boolean retrieveAllPhasesAmpMag(ArrayList<Phase> PhaseList)
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
                
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            
            for(int i = 0; i<PhaseList.size(); i++)
            {
                Integer phid = PhaseList.get(i).getPhid();
                                                
                String query = "SELECT p.amp, p.per, s.magnitude, s.ampdef "
                + "FROM ampmag s, amplitude p "
                + "WHERE s.author = 'ISC' AND p.ampid = s.ampid "
                + "AND p.phid = " + phid;
                
                rs = st.executeQuery(query);

                while (rs.next()) {
                    PhaseList.get(i).setAmplitude(rs.getDouble(1));
                    PhaseList.get(i).setPeriod(rs.getDouble(2));
                    PhaseList.get(i).setAmpMag(rs.getDouble(3));
                    
                    if(rs.getInt(4)==1)
                    {
                        PhaseList.get(i).setAmpmagDefining(true);
                    }
                    else if(rs.getInt(4)==0)
                    {
                        PhaseList.get(i).setAmpmagDefining(false);
                    }
                }
                rs.close();
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
     
    public static boolean retrieveAllStationsWithRegions(TreeMap<String,String> allStations)
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
                
        String query = "SELECT s.sta, r.gr_name "
                + "FROM site s, site_grn g, region r "
                + "WHERE s.net IS NULL AND s.lat IS NOT NULL AND s.lon IS NOT NULL "
                + "AND s.sta = g.sta AND g.net IS NULL AND g.grn_ll = r.gr_number;";
        
        try {
            con = DriverManager.getConnection(url, user, password);
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
    
    public static boolean retrieveAllStations(Integer evid, ArrayList<Station> allStations)
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        allStations.clear();
        
        String query = "SELECT DISTINCT p.sta, s.lat, s.lon, r.reporter, a.esaz, a.delta FROM event e, association a, phase p, report r, site s "
                + " WHERE e.evid = " + evid +
                " AND e.prime_hyp = a.hypid " +
                "AND a.author = 'ISC' AND a.phid = p.phid AND p.reporter = r.repid AND p.sta = s.sta AND s.net IS NULL";
        
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                Station tmp = new Station(rs.getString(1),rs.getDouble(2),rs.getDouble(3),rs.getString(4), rs.getDouble(5), rs.getDouble(6));
              
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
    
    //write to database
    //Function 1: Fix Prime Hypocentre
    public static Boolean fixPrime(Integer hypid, Integer evid) {
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;
        
        try {
            con = DriverManager.getConnection(url, user, password);
            cs = con.prepareCall("{? = call RF(?,?)}");
            
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2,hypid);
            cs.setInt(3,evid);
            
            cs.execute();
            
            int returnValue = cs.getInt(1);
            if(returnValue == 0)
            {
                sFlag = true;
            }
            else
            {
                sFlag = false;
            }
            
        }
        catch(SQLException e)
        {
            System.out.println(e);
        }
        
        return sFlag;
    }
    
    
    //Fuction 2: Change attributes of hypocentre
    static boolean changeHypo(Integer hypid, String attribute, String value) {
        
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;
        
        try {
            con = DriverManager.getConnection(url, user, password);
            cs = con.prepareCall("{? = call CHHypo(?,?,?)}");
            cs.registerOutParameter(1, Types.INTEGER);
            
     
            cs.setInt(2,hypid);
            cs.setString(3,attribute);
            cs.setString(4,value);
            
            cs.execute();
            
            int returnValue = cs.getInt(1);
            if(returnValue == 0)
            {
                sFlag = true;
            }
            else
            {
                sFlag = false;
            }
        }
        catch(SQLException e)
        {
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
            con = DriverManager.getConnection(url, user, password);
            cs = con.prepareCall("{? = call PutHypo(?,?,?)}");
            cs.registerOutParameter(1, Types.INTEGER);
            

            cs.setInt(2,hypid);
            cs.setInt(3,evidFrom);
            cs.setInt(4,evidTo);
                
            cs.execute();
            
            int returnValue = cs.getInt(1);
            if(returnValue == 0)
            {
                sFlag = true;
            }
            else
            {
                sFlag = false;       
            }
            
        }
        catch(SQLException e)
        {
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
            con = DriverManager.getConnection(url, user, password);
            cs = con.prepareCall("{? = call deletehypo(?)}");
            
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2,hypid);
            //cs.setInt(3,evid);
            
            cs.execute();
            
            int returnValue = cs.getInt(1);
            if(returnValue == 0)
            {
                sFlag = true;
            }
            else
            {
                sFlag = false;
            }
            
        }
        catch(SQLException e)
        {
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
            con = DriverManager.getConnection(url, user, password);
            cs = con.prepareCall("{? = call undeletehypo(?)}");
            
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2,hypid);
            //cs.setInt(3,evid);
            
            cs.execute();
            
            int returnValue = cs.getInt(1);
            if(returnValue == 0)
            {
                sFlag = true;
            }
            else
            {
                sFlag = false;
            }
            
        }
        catch(SQLException e)
        {
            System.out.println(e);
        }
        
        return sFlag;
    }
    
    //Function 5 event level
    static boolean banishEvent(Integer evid)
    {
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;
        
        try {
            con = DriverManager.getConnection(url, user, password);
            cs = con.prepareCall("{? = call banish(?)}");
            
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2,evid);
            //cs.setInt(3,evid);
            
            cs.execute();
            
            int returnValue = cs.getInt(1);
            if(returnValue == 0)
            {
                sFlag = true;
            }
            else
            {
                sFlag = false;
            }
            
        }
        catch(SQLException e)
        {
            System.out.println(e);
        }
        
        return sFlag;
    }
    
    //reverse of function 5.
    static boolean unbanishEvent(Integer evid)
    {
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;
        
        try {
            con = DriverManager.getConnection(url, user, password);
            cs = con.prepareCall("{? = call unbanish(?)}");
            
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2,evid);
            //cs.setInt(3,evid);
            
            cs.execute();
            
            int returnValue = cs.getInt(1);
            if(returnValue == 0)
            {
                sFlag = true;
            }
            else
            {
                sFlag = false;
            }
            
        }
        catch(SQLException e)
        {
            System.out.println(e);
        }
        
        return sFlag;
    }
    
    static boolean createEvent(Integer hypid, Integer evid) {
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;
        
        try {
            con = DriverManager.getConnection(url, user, password);
            cs = con.prepareCall("{? = call create_event(?,?)}");
            cs.registerOutParameter(1, Types.INTEGER);
     
            cs.setInt(2,hypid);
            cs.setInt(3,evid);
              
            cs.execute();
            
            int returnValue = cs.getInt(1);
            if(returnValue == 0)
            {
                sFlag = true;
            }
            else
            {
                sFlag = false;
            }
        }
        catch(SQLException e)
        {
            System.out.println(e);
        }
        
        return sFlag;
    }

    static boolean mergeEvent(Integer evidFrom, Integer evidTo) {
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;
        
        try {
            con = DriverManager.getConnection(url, user, password);
            cs = con.prepareCall("{? = call merge(?,?)}");
            cs.registerOutParameter(1, Types.INTEGER);
     
            cs.setInt(2,evidFrom);
            cs.setInt(3,evidTo);
              
            cs.execute();
            
            int returnValue = cs.getInt(1);
            if(returnValue == 0)
            {
                sFlag = true;
            }
            else
            {
                sFlag = false;
            }
        }
        catch(SQLException e)
        {
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
            con = DriverManager.getConnection(url, user, password);
            cs = con.prepareCall("{? = call CHPhase(?,?,?)}");
            cs.registerOutParameter(1, Types.INTEGER);
            
     
            cs.setInt(2,phid);
            cs.setString(3,attribute);
            cs.setString(4,value);
              
            cs.execute();
            
            int returnValue = cs.getInt(1);
            if(returnValue == 0)
            {
                sFlag = true;
            }
            else
            {
                sFlag = false;
            }
        }
        catch(SQLException e)
        {
            System.out.println(e);
        }
        
        return sFlag;
    }

    static boolean putReading(Integer rdid, Integer evidFrom, Integer evidTo) {
        
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;
        
        try {
            con = DriverManager.getConnection(url, user, password);
            cs = con.prepareCall("{? = call putread(?,?,?)}");
            cs.registerOutParameter(1, Types.INTEGER);
            

            cs.setInt(2,rdid);
            cs.setInt(3,evidFrom);
            cs.setInt(4,evidTo);
            
            cs.execute();
            
            int returnValue = cs.getInt(1);
            if(returnValue == 0)
            {
                sFlag = true;
            }
            else
            {
                sFlag = false;       
            }
            
        }
        catch(SQLException e)
        {
            System.out.println(e);
        }
        
        return sFlag;
    }
    
    static boolean deleteReading(Integer rdid) {
                     
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;
        
        try {
            con = DriverManager.getConnection(url, user, password);
            cs = con.prepareCall("{? = call deleteread(?)}");
            
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2,rdid);
            //cs.setInt(3,evid);
            
            cs.execute();
            
            int returnValue = cs.getInt(1);
            if(returnValue == 0)
            {
                sFlag = true;
            }
            else
            {
                sFlag = false;
            }
            
        }
        catch(SQLException e)
        {
            System.out.println(e);
        }
        
        return sFlag;
    }
    
    static boolean takeReading(Integer rdid) {
       
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;
        
        try {
            con = DriverManager.getConnection(url, user, password);
            cs = con.prepareCall("{? = call takeread(?)}");
            
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2,rdid);
            //cs.setInt(3,evid);
            
            cs.execute();
            
            int returnValue = cs.getInt(1);
            if(returnValue == 0)
            {
                sFlag = true;
            }
            else
            {
                sFlag = false;
            }
            
        }
        catch(SQLException e)
        {
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
            con = DriverManager.getConnection(url, user, password);
            cs = con.prepareCall("{? = call putphase(?,?,?,?)}");
            cs.registerOutParameter(1, Types.INTEGER);
            

            cs.setInt(2,phid);
            cs.setInt(3,hypidFrom);
            cs.setInt(4,hypidTo);
            if(newRDID == null)
            {
                cs.setNull(5, java.sql.Types.INTEGER);
            }
            else
            {
                cs.setInt(5, newRDID);
            }
            
            cs.execute();
            
            int returnValue = cs.getInt(1);
            if(returnValue == 0)
            {
                sFlag = true;
            }
            else
            {
                sFlag = false;       
            }
            
        }
        catch(SQLException e)
        {
            System.out.println(e);
        }
        
        return sFlag;
    }

    static boolean deletePhase(Integer phid) {
                     
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;
        
        try {
            con = DriverManager.getConnection(url, user, password);
            cs = con.prepareCall("{? = call deletephase(?)}");
            
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2,phid);
            //cs.setInt(3,evid);
            
            cs.execute();
            
            int returnValue = cs.getInt(1);
            if(returnValue == 0)
            {
                sFlag = true;
            }
            else
            {
                sFlag = false;
            }
            
        }
        catch(SQLException e)
        {
            System.out.println(e);
        }
        
        return sFlag;
    }

    static boolean takePhase(Integer phid) {
       
        Connection con = null;
        CallableStatement cs = null;
        Boolean sFlag = null;
        
        try {
            con = DriverManager.getConnection(url, user, password);
            cs = con.prepareCall("{? = call takephase(?)}");
            
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setInt(2,phid);
            //cs.setInt(3,evid);
            
            cs.execute();
            
            int returnValue = cs.getInt(1);
            if(returnValue == 0)
            {
                sFlag = true;
            }
            else
            {
                sFlag = false;
            }
            
        }
        catch(SQLException e)
        {
            System.out.println(e);
        }
        
        return sFlag;
    }
}
