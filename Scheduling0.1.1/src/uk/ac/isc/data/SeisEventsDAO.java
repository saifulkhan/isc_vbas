
package uk.ac.isc.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;


/**
 *
 * @author hui
 * Data Access Object for getting events, allocation, block and assignment info
 * 
 */
public class SeisEventsDAO {
    
    /**
     * Loading user name, password and scheme from system environment
     */
    static {         
        Map<String, String> env = System.getenv();
        url = "jdbc:postgresql://"+env.get("PGHOSTADDR")+":"+env.get("PGPORT")+"/"+env.get("PGDATABASE");
        user = env.get("PGUSER");
        password = env.get("PGPASSWORD"); 
        //url = "jdbc:postgresql://127.0.0.1:5432/isc";
        //user = "hui";
        //password = "njustga";
    }
    
    /**
     * variables for the database access
     */
    private static final String url; 
    private static final String user; 
    private static final String password; 
    
    /**
     * Get the start Date of all the events
     * @return startDate
     */
    public static Date retrieveStartDate()
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        
        String query = "SELECT MIN(h.day)" +
                " FROM event e, hypocenter h" +
                " WHERE e.prime_hyp = h.hypid" +
                " AND h.isc_evid = e.evid AND e.banished IS NULL AND e.ready IS NOT NULL;";
        
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                                
                try {
                   // System.out.println(rs.getString(1));
                   startDate = df.parse(rs.getString(1));
                } catch (ParseException e)
                {
                    return null;
                }
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
        
        return startDate;
        
    }
         
    /**
     * Get the last Date of all the events
     * @return 
     */
    public static Date retrieveEndDate()
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date endDate = null;
        
        String query = "SELECT MAX(h.day)" +
                " FROM event e, hypocenter h" +
                " WHERE e.prime_hyp = h.hypid" +
                " AND h.isc_evid = e.evid AND e.banished IS NULL AND e.ready IS NOT NULL;";
        
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                                
                try {
                   // System.out.println(rs.getString(1));
                   endDate = df.parse(rs.getString(1));
                } catch (ParseException e)
                {
                    return null;
                }
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
        
        return endDate;       
    }
    
    /**
     * retrieve all events in the schema first
     * @param seisEvents
     * @return 
     */
    public static boolean retrieveAllEvents(ArrayList<SeisEvent> seisEvents) {
         
        //clear the memory of seisEvent in order to reload events
        seisEvents.clear();
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                
        String query = "SELECT e.evid, h.day, h.lat, h.lon, " +
                "( SELECT COUNT(*) FROM association a WHERE a.hypid = h.hypid AND a.author = 'ISC' )" +
                " FROM event e, hypocenter h" +
                " WHERE e.prime_hyp = h.hypid" +
                " AND h.isc_evid = e.evid AND e.banished IS NULL AND e.ready IS NOT NULL" +
                " ORDER BY h.day ASC;";

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                
                SeisEvent tmp = new SeisEvent(rs.getInt(1));
                Date dd = null;
                
                try {
                   dd = df.parse(rs.getString(2));
                } catch (ParseException e)
                {
                    return false;
                }

                tmp.setOrigTime(dd);
                tmp.setLat(rs.getDouble(3));
                tmp.setLon(rs.getDouble(4));
                tmp.setPhaseNumber(rs.getInt(5));
                
                //System.out.println(tmp.getPhaseNumber());
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
     * @obsolete
     * @param seisEvents to save all unallocated events to show on timeline and map
     * @return success or not
     */
    public static boolean retrieveUnallocEvents(ArrayList<SeisEvent> seisEvents) {
         
        //clear the memory of seisEvent in order to reload events
        seisEvents.clear();
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        
        String query = "SELECT e.evid, h.day, h.lat, h.lon" +
                " FROM event e, hypocenter h" +
                " WHERE e.prime_hyp = h.hypid" +
                " AND h.isc_evid = e.evid AND e.banished IS NULL AND e.ready IS NOT NULL" +
                " AND NOT EXISTS ( SELECT a.evid FROM allocation a WHERE a.evid = e.evid )" +
                " ORDER BY h.day ASC;";

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                
                SeisEvent tmp = new SeisEvent(rs.getInt(1));
                Date dd = null;
                
                try {
                   dd = df.parse(rs.getString(2));
                } catch (ParseException e)
                {
                    return false;
                }

                tmp.setOrigTime(dd);
                tmp.setLat(rs.getDouble(3));
                tmp.setLon(rs.getDouble(4));
                
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
     *
     * @param evSet
     * @return
     */
    public static boolean retrieveAllocatedEvID(HashMap<Integer,Integer> evSet) {
                
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        String query = "SELECT ea.evid, b.block_id" +
                " FROM event e, hypocenter h, event_allocation ea, block_allocation b" +
                " WHERE e.prime_hyp = h.hypid" +
                " AND h.isc_evid = e.evid AND e.banished IS NULL AND e.ready IS NOT NULL" +
                " AND ea.evid = e.evid AND b.id = ea.block_allocation_id" +
                " ORDER BY h.day ASC;";

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                
                evSet.put(rs.getInt(1),rs.getInt(2));
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
     * @param blockID block ID number 
     * @param seisEvents to save all unallocated events to show on timeline and map
     * @return success or not
     */
    public static boolean retrieveBlockEvents(int blockID, ArrayList<SeisEvent> seisEvents) {
         
        //clear the memory of seisEvent in order to reload events
        seisEvents.clear();
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        String query = "SELECT e.evid, h.day, h.lat, h.lon" +
                " FROM event e, hypocenter h, allocation a" +
                " WHERE e.prime_hyp = h.hypid" +
                " AND h.isc_evid = e.evid AND e.banished IS NULL AND e.ready IS NOT NULL" +
                " AND a.evid = e.evid AND a.block_id = " + blockID +
                " ORDER BY h.day ASC;";

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                
                SeisEvent tmp = new SeisEvent(rs.getInt(1));
                Date dd = null;
                
                try {
                   dd = df.parse(rs.getString(2));
                } catch (ParseException e)
                {
                    return false;
                }

                tmp.setOrigTime(dd);
                tmp.setLat(rs.getDouble(3));
                tmp.setLon(rs.getDouble(4));
                
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
        
        return !seisEvents.isEmpty();
    }
    
    /**as the list is not big, so use iteration to fill the events number
     * @param bList the blocklist to fill the events number
     * @return */
    public static boolean retrieveBlockEventNumber(ArrayList<TaskBlock> bList) {
                
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
             
        String query = "select ba.block_id, count(*)" +
                " From block_allocation ba, event_allocation ev" +
                " WHERE ba.id = ev.block_allocation_id AND ba.pass= 'p'" +
                " GROUP BY ba.block_id;"; 

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                
                for(TaskBlock tb:bList)
                {
                    //System.out.println(tb.getBlockID());
                    //System.out.println(rs.getInt(1));
                    
                    if(tb.getBlockID().equals(rs.getInt(1)))
                    {
                        tb.setEventNumber(rs.getInt(2));
                    }
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
    
    /**The
     * 
     * @param bList
     * @return */
    public static boolean retrieveBlockReviewedEventNumber(ArrayList<TaskBlock> bList) {
                
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
             
        String query = "select ba.block_id, count(*)" +
                " From block_allocation ba, event_allocation ev" +
                " WHERE ba.id = ev.block_allocation_id AND ba.review = 0 AND ba.start IS NOT NULL AND ba.finish IS NULL and ev.start IS NOT NULL" +
                " GROUP BY ba.block_id;"; 

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                
                for(TaskBlock tb:bList)
                {
                    //System.out.println(tb.getBlockID());
                    //System.out.println(rs.getInt(1));
                    
                    if(tb.getBlockID().equals(rs.getInt(1)))
                    {
                        tb.setReviewedEventNumber(rs.getInt(2));
                    }
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
    
    public static int retrieveNewBlockID() {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        int newID = 0;
        
        String query = "SELECT NEXTID('id','block')";
        
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
            
                newID = rs.getInt(1);
            }
        }catch (SQLException ex) {
            
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
        
        return newID;        
            
    }
    
     /**
     * change this
     * @param seisEvents to save all unallocated events to show on timeline and map
     * @param startDay selected startday
     * @param endDay selected endday
     * @param timeAndGeo
     * @param tb
     * @return success or not
     */
    public static boolean createBlock(ArrayList<SeisEvent> seisEvents, Date startDay, Date endDay, boolean timeAndGeo, TaskBlock tb)
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        int blockID = tb.getBlockID();
        int allocID1 = 0, allocID2 = 0, allocID3 = 0;
        int selectID = 0;
        int regionID = 0;
        System.out.println("Entering the create block function successfully.");
        
        try {
            con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(false);
            
            st = con.createStatement();
            rs = st.executeQuery("SELECT NEXTID('selectid','block');");
            if (rs.next()) {
                selectID = rs.getInt(1);
            }
            rs.close();       
            System.out.println("Getting a selectID successfully");
            
            //st = con.createStatement();
            rs = st.executeQuery("SELECT NEXTID('region_id','block');");
            if (rs.next()) {
                regionID = rs.getInt(1);
            }
            rs.close();
            System.out.println(regionID);          
            
            /*update block table*/
            //st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            PreparedStatement st1 = con.prepareStatement("INSERT INTO block VALUES (?,?,?,?,?)");
            st1.setInt(1,blockID);
            st1.setInt(2, selectID);
            st1.setTimestamp(3, new Timestamp(startDay.getTime()));
            st1.setTimestamp(4, new Timestamp(endDay.getTime()));
            st1.setInt(5, regionID);
            try {
            st1.executeUpdate(); } catch(SQLException ex) {
                System.out.println("Fail to insert block row.");
            }
           
            //con.commit();
            //rs.close();
            /*update first row of blockallocation the p pass*/
            rs = st.executeQuery("SELECT NEXTID('id','block_allocation');");
            while (rs.next()) {
                allocID1 = rs.getInt(1);
            }
            rs.close();
            System.out.println(allocID1);
            
            String sql2 = "INSERT INTO block_allocation "
                    + "(id, block_id, analyst_id, pass, planned_start, planned_finish, review) "
                    + "VALUES (?,?,?,?,?,?,?)";
            PreparedStatement st2 = con.prepareStatement(sql2);
            
            st2.setInt(1,allocID1);
            st2.setInt(2, blockID);
            st2.setInt(3, tb.getAnalyst1ID());
            st2.setString(4, "p");
            st2.setTimestamp(5, new Timestamp(tb.getPPlanStartDay().getTime()));
            st2.setTimestamp(6, new Timestamp(tb.getPPlanEndDay().getTime()));
            st2.setInt(7, 0);
            try {st2.executeUpdate();} catch(SQLException ex) {
                System.out.println("Fail to insert block allocation row.");
            }
            
            /*second row in the block_allocation table the s pass*/
            rs = st.executeQuery("SELECT NEXTID('id','block_allocation');");
            while (rs.next()) {
                allocID2 = rs.getInt(1);
            }
            rs.close();
            System.out.println(allocID2);
            
            String sql3 = "INSERT INTO block_allocation "
                    + "(id, block_id, analyst_id, pass, planned_start, planned_finish, review) "
                    + "VALUES (?,?,?,?,?,?,?)";
            PreparedStatement st3 = con.prepareStatement(sql3);
            
            st3.setInt(1,allocID2);
            st3.setInt(2, blockID);
            st3.setInt(3, tb.getAnalyst2ID());
            st3.setString(4, "s");
            st3.setTimestamp(5, new Timestamp(tb.getSPlanStartDay().getTime()));
            st3.setTimestamp(6, new Timestamp(tb.getSPlanEndDay().getTime()));
            st3.setInt(7, 0);
            try { st3.executeUpdate();} catch(SQLException ex) {
                System.out.println("Fail to insert block allocation row.");
            }
            
            /*third row in the block_allocation table the f pass*/
            rs = st.executeQuery("SELECT NEXTID('id','block_allocation');");
            while (rs.next()) {
                allocID3 = rs.getInt(1);
            }
            rs.close();
            System.out.println(allocID3);
            
            String sql4 = "INSERT INTO block_allocation "
                    + "(id, block_id, analyst_id, pass, planned_start, planned_finish, review) "
                    + "VALUES (?,?,?,?,?,?,?)";
            PreparedStatement st4 = con.prepareStatement(sql4);
            
            st4.setInt(1,allocID3);
            st4.setInt(2, blockID);
            st4.setInt(3, tb.getAnalyst3ID());
            st4.setString(4, "f");
            st4.setTimestamp(5, new Timestamp(tb.getFPlanStartDay().getTime()));
            st4.setTimestamp(6, new Timestamp(tb.getFPlanEndDay().getTime()));
            st4.setInt(7, 0);
            try{
                st4.executeUpdate();
            } catch(SQLException ex) {
                System.out.println("Fail to insert block allocation row.");
            }
            
            /*insert into the event_allocation table*/
            String sql5 = "INSERT INTO event_allocation "
                    + "(evid, block_allocation_id, block_selectid) "
                    + "VALUES (?,?,?)";
            PreparedStatement st5 = con.prepareStatement(sql5);
                        
            for(SeisEvent ev:seisEvents)
            {
                if(timeAndGeo == true)
                {
                    //both selected, commit into the database
                    if(ev.getTSelction()==true && ev.getGSelction()==true && ev.getblAssigned()!= true && blockID>0)
                    {
                        st5.setInt(1, ev.getEvid());
                        st5.setInt(2, allocID1);
                        st5.setInt(3, selectID);
                        try {st5.executeUpdate();}catch(SQLException ex) {
                            System.out.println("Fail to insert event allocation row.");
                        }
                        
                        st5.setInt(1, ev.getEvid());
                        st5.setInt(2, allocID2);
                        st5.setInt(3, selectID);
                        try { st5.executeUpdate();}
                        catch(SQLException ex) {
                            System.out.println("Fail to insert event allocation row.");
                        }
                        
                        st5.setInt(1, ev.getEvid());
                        st5.setInt(2, allocID3);
                        st5.setInt(3, selectID);
                        try {st5.executeUpdate();}
                        catch(SQLException ex) {
                            System.out.println("Fail to insert event allocation row.");
                        }
                    }
                }
                else
                {
                    //only commit for time
                    if(ev.getTSelction()==true && ev.getblAssigned()!= true && blockID>0)
                    {
                        st5.setInt(1, ev.getEvid());
                        st5.setInt(2, allocID1);
                        st5.setInt(3, selectID);
                        try {st5.executeUpdate();}
                        catch(SQLException ex) {
                            System.out.println("Fail to insert event allocation row.");
                        }
                        
                        st5.setInt(1, ev.getEvid());
                        st5.setInt(2, allocID2);
                        st5.setInt(3, selectID);
                        try {st5.executeUpdate();}
                        catch(SQLException ex) {
                            System.out.println("Fail to insert event allocation row.");
                        }
                        
                        st5.setInt(1, ev.getEvid());
                        st5.setInt(2, allocID3);
                        st5.setInt(3, selectID);
                        try { st5.executeUpdate();}
                        catch(SQLException ex) {
                            System.out.println("Fail to insert event allocation row.");
                        }
                    }
                }
            }
                        
            con.commit();
            st1.close();
            st2.close();
            st3.close();
            st4.close();
            st5.close();
         
        } catch(SQLException e) {
            System.out.println(e);
            
            try {
                con.rollback();
            } catch(SQLException e2)
            {
                System.out.println("Rollback failure ");
            }
            return false;
        }
        finally {
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

    /**The
     * set 
     * @param seList
     * @return */
    public static boolean retrieveReviewedEvent(ArrayList<SeisEvent> seList, Integer bid) {
                
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
          
        HashMap<Integer, SeisEvent> seMap = new HashMap<>();
        for(SeisEvent se: seList)
        {
            seMap.put(se.getEvid(), se);
            se.setbReviewed(false);
        }
            
        String query = "select ev.evid" +
                " From block_allocation ba, event_allocation ev" +
                " WHERE ba.id = ev.block_allocation_id AND ba.review = 0 AND ba.start IS NOT NULL AND ba.finish IS NULL and ev.start IS NOT NULL" +
                " AND ba.block_id = " + bid; 
        
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                
                for(SeisEvent se:seList)
                {
                    //System.out.println(rs.getInt(1));
                    if(seMap.containsKey(rs.getInt(1)))
                    {
                        seMap.get(rs.getInt(1)).setbReviewed(true);
                    }
                    
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
    
    public static boolean splitBlock(ArrayList<SeisEvent> seisEvents, TaskBlock tb, Integer old_bid)
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        int selectID = 0;
        int allocID1 = 0, allocID2 = 0, allocID3 = 0;
        
        try {
            con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(false);
                
                        st = con.createStatement();
            rs = st.executeQuery("SELECT NEXTID('selectid','block');");
            if (rs.next()) {
                selectID = rs.getInt(1);
            }
            rs.close();       
            
            /*update block table*/
            //st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            PreparedStatement st1 = con.prepareStatement("INSERT INTO block VALUES (?,?,?,?,?)");
            st1.setInt(1,tb.getBlockID());
            st1.setInt(2, selectID);
            st1.setTimestamp(3, new Timestamp(tb.getStartDay().getTime()));
            st1.setTimestamp(4, new Timestamp(tb.getEndDay().getTime()));
            st1.setInt(5, tb.getRegionID());
            st1.executeUpdate();
           
            //con.commit();
            //rs.close();
            /*update first row of blockallocation the p pass*/
            rs = st.executeQuery("SELECT NEXTID('id','block_allocation');");
            while (rs.next()) {
                allocID1 = rs.getInt(1);
            }
            rs.close();
            //System.out.println(allocID1);
            
            String sql2 = "INSERT INTO block_allocation "
                    + "(id, block_id, analyst_id, pass, planned_start, planned_finish, review) "
                    + "VALUES (?,?,?,?,?,?,?)";
            PreparedStatement st2 = con.prepareStatement(sql2);
            
            st2.setInt(1,allocID1);
            st2.setInt(2, tb.getBlockID());
            st2.setInt(3, tb.getAnalyst1ID());
            st2.setString(4, "p");
            st2.setTimestamp(5, new Timestamp(tb.getPPlanStartDay().getTime()));
            st2.setTimestamp(6, new Timestamp(tb.getPPlanEndDay().getTime()));
            st2.setInt(7, 0);
            st2.executeUpdate();
            
            /*second row in the block_allocation table the s pass*/
            rs = st.executeQuery("SELECT NEXTID('id','block_allocation');");
            while (rs.next()) {
                allocID2 = rs.getInt(1);
            }
            rs.close();
            //System.out.println(allocID2);
            
            String sql3 = "INSERT INTO block_allocation "
                    + "(id, block_id, analyst_id, pass, planned_start, planned_finish, review) "
                    + "VALUES (?,?,?,?,?,?,?)";
            PreparedStatement st3 = con.prepareStatement(sql3);
            
            st3.setInt(1,allocID2);
            st3.setInt(2, tb.getBlockID());
            st3.setInt(3, tb.getAnalyst2ID());
            st3.setString(4, "s");
            st3.setTimestamp(5, new Timestamp(tb.getSPlanStartDay().getTime()));
            st3.setTimestamp(6, new Timestamp(tb.getSPlanEndDay().getTime()));
            st3.setInt(7, 0);
            st3.executeUpdate();
            
            /*third row in the block_allocation table the f pass*/
            rs = st.executeQuery("SELECT NEXTID('id','block_allocation');");
            while (rs.next()) {
                allocID3 = rs.getInt(1);
            }
            rs.close();
            //System.out.println(allocID3);
            
            String sql4 = "INSERT INTO block_allocation "
                    + "(id, block_id, analyst_id, pass, planned_start, planned_finish, review) "
                    + "VALUES (?,?,?,?,?,?,?)";
            PreparedStatement st4 = con.prepareStatement(sql4);
            
            st4.setInt(1,allocID3);
            st4.setInt(2, tb.getBlockID());
            st4.setInt(3, tb.getAnalyst3ID());
            st4.setString(4, "f");
            st4.setTimestamp(5, new Timestamp(tb.getFPlanStartDay().getTime()));
            st4.setTimestamp(6, new Timestamp(tb.getFPlanEndDay().getTime()));
            st4.setInt(7, 0);
            st4.executeUpdate();
            
             /*delete the record in the event_allocation table where the event not been reviewed*/
            String sql6 = "DELETE FROM event_allocation" +
                         " WHERE evid IN ( SELECT ev.evid" +
                         " FROM block_allocation ba, event_allocation ev" +
                         " WHERE pass = 'p' AND ba.id = ev.block_allocation_id AND ba.review = 0" +
                         " AND ba.start IS NOT NULL AND ba.finish IS NULL AND ev.start IS NULL" +  
                         " AND ba.block_id = " + old_bid + ");";
            PreparedStatement st6 = con.prepareStatement(sql6);
            st6.executeUpdate();
 
            /*insert into the event_allocation table*/
            String sql5 = "INSERT INTO event_allocation "
                    + "(evid, block_allocation_id, block_selectid) "
                    + "VALUES (?,?,?)";
            PreparedStatement st5 = con.prepareStatement(sql5);
                        
            for(SeisEvent ev:seisEvents)
            {
                //System.out.println(ev.getBlockID());
                //System.out.println(tb.getBlockID());
                
                    //both selected, commit into the database
                    if(Objects.equals(ev.getBlockID(), tb.getBlockID()))
                    {
                        st5.setInt(1, ev.getEvid());
                        st5.setInt(2, allocID1);
                        st5.setInt(3, selectID);
                        try {
                        st5.executeUpdate();}catch(SQLException ex){
                            System.out.println("Insert failed");
                        }
                        
                        
                        st5.setInt(1, ev.getEvid());
                        st5.setInt(2, allocID2);
                        st5.setInt(3, selectID);
                        try {
                        st5.executeUpdate();}catch(SQLException ex){
                            System.out.println("Insert failed");
                        }
                        
                        st5.setInt(1, ev.getEvid());
                        st5.setInt(2, allocID3);
                        st5.setInt(3, selectID);
                        try {
                        st5.executeUpdate();}catch(SQLException ex){
                         System.out.println("Insert failed");
                        }
                    }
                    
            }
                                  
            con.commit();
            st1.close();
            st2.close();
            st3.close();
            st4.close();
            st5.close();
         
        } catch(SQLException e) {
            try {
                con.rollback();
            } catch(SQLException e2)
            {
                System.out.println("Rollback failure ");
            }
            return false;
        }
        finally {
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
     * change this
     * @param tb
     * @return success or not
    */
    public static boolean updateBlock(TaskBlock tb)
    {
        Connection con = null;
        //Statement st = null;
        //ResultSet rs = null;
                
        try {
            con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(false);         
            //st = con.createStatement();
            /*update block table*/

            String sql = "UPDATE block_allocation"
                    + " SET analyst_id = null" 
                    + " WHERE block_id = " + tb.getBlockID();
            
            /*update first row of blockallocation the p pass*/      
            String sql2 = "UPDATE block_allocation"
                    + " SET analyst_id = ?, planned_start = ?, planned_finish = ?" 
                    + " WHERE pass = 'p' and block_id = " + tb.getBlockID();
            
            String sql3 = "UPDATE block_allocation "
                        + "SET analyst_id = ?, planned_start = ?, planned_finish = ?"
                        + " WHERE pass = 's' and block_id = " + tb.getBlockID();
            
            String sql4 = "UPDATE block_allocation "
                        + "SET analyst_id = ?, planned_start = ?, planned_finish = ?"
                        + " WHERE pass = 'f' and block_id = " + tb.getBlockID();
                            
            try (PreparedStatement st = con.prepareStatement(sql)) {
                try{
                    st.executeUpdate();
                } catch(SQLException e) {System.out.println("Cannot set analysts to null");}
                    
            }
            
            try (PreparedStatement st2 = con.prepareStatement(sql2)) {
                st2.setInt(1,tb.getAnalyst1ID());
                st2.setTimestamp(2, new Timestamp(tb.getPPlanStartDay().getTime()));
                st2.setTimestamp(3, new Timestamp(tb.getPPlanEndDay().getTime()));
                //System.out.println(st2);
                
                st2.executeUpdate();
            } catch(SQLException e) {}
            
            try (PreparedStatement st3 = con.prepareStatement(sql3)) {
                
                //second row in the block_allocation table the s pass
                st3.setInt(1,tb.getAnalyst2ID());
                st3.setTimestamp(2, new Timestamp(tb.getSPlanStartDay().getTime()));
                st3.setTimestamp(3, new Timestamp(tb.getSPlanEndDay().getTime()));
                
                //System.out.println(st3);
                st3.executeUpdate();
            } catch(SQLException e){}
            
            /*third row in the block_allocation table the f pass*/
            
            try (PreparedStatement st4 = con.prepareStatement(sql4)) {
                //third row in the block_allocation table the f pass
                st4.setInt(1,tb.getAnalyst3ID());
                st4.setTimestamp(2, new Timestamp(tb.getFPlanStartDay().getTime()));
                st4.setTimestamp(3, new Timestamp(tb.getFPlanEndDay().getTime()));
                //System.out.println(st4);
                try {
                st4.executeUpdate();              
                
                } catch (SQLException e) {
                    System.out.println("cannot update");}
                
                //con.commit();
            } catch(SQLException e) {}
            
            con.commit();
            
        } catch(SQLException e) {
            try {
                con.rollback();
            } catch(SQLException e2)
            {
                System.out.println("Rollback failure ");
            }
            return false;
        }
        finally {
            try {
                //if (rs != null) {
                //    rs.close();
                //}

                //if (st != null) {
                //    st.close();
                //}
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
        
        String query = "SELECT b.id, b.starttime, b.endtime, b.region_id, a.name, ba.pass, ba.review, a.id, ba.planned_start, ba.planned_finish" +
                " FROM block b, block_allocation ba, analyst a" +
                " Where b.id = ba.block_id AND a.id = ba.analyst_id" +
                " ORDER BY b.id;";

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                
                if(blockSet.contains(new TaskBlock(rs.getInt(1))))
                {
                    for (TaskBlock obj : blockSet) {
                        if (obj.getBlockID().equals(rs.getInt(1))) 
                        {
                            if("p".equals(rs.getString(6)))
                            {
                                if(rs.getString(5)!= null)
                                {
                                    obj.setAnalyst1(rs.getString(5));
                                    obj.setAnalyst1ID(rs.getInt(8));
                                }
                                
                                if(rs.getInt(7)==0)
                                {
                                    obj.setStatus("P");
                                }
                                
                                obj.setPPlanStartDay(new Date(rs.getTimestamp(9).getTime()));
                                obj.setPPlanEndDay(new Date(rs.getTimestamp(10).getTime()));
                            }
                            else if("s".equals(rs.getString(6)))
                            {
                                if(rs.getString(5)!= null)
                                {
                                    obj.setAnalyst2(rs.getString(5));
                                    obj.setAnalyst2ID(rs.getInt(8));
                                }
                                
                                if((obj.getStatus()==null || obj.getStatus() == "F") && rs.getInt(7)==0)
                                {
                                    obj.setStatus("S");
                                }
                                
                                obj.setSPlanStartDay(new Date(rs.getTimestamp(9).getTime()));
                                obj.setSPlanEndDay(new Date(rs.getTimestamp(10).getTime()));
                            }
                            else if("f".equals(rs.getString(6)))
                            {
                                if(rs.getString(5)!= null)
                                {
                                    obj.setAnalyst3(rs.getString(5));
                                    obj.setAnalyst3ID(rs.getInt(8));
                                    
                                }
                                
                                if(obj.getStatus()==null && rs.getInt(7)==0)
                                {
                                    obj.setStatus("F");
                                }
                                else if(rs.getInt(7)==1)
                                {
                                    obj.setStatus("Done");
                                }
                                
                                obj.setFPlanStartDay(new Date(rs.getTimestamp(9).getTime()));
                                obj.setFPlanEndDay(new Date(rs.getTimestamp(10).getTime()));
                            }
                        }
                    } 
                }
                else
                {
                    TaskBlock tmp = new TaskBlock(rs.getInt(1));

                    tmp.setStartDay(new Date(rs.getTimestamp(2).getTime()));
                    tmp.setEndDay(new Date(rs.getTimestamp(3).getTime()));
                    tmp.setRegionID(rs.getInt(4));
                    
                    if("p".equals(rs.getString(6)))
                    {
                        tmp.setAnalyst1(rs.getString(5));
                        if(rs.getInt(7)==0)
                        {
                            tmp.setStatus("P");
                        }
                        tmp.setAnalyst1ID(rs.getInt(8));
                        tmp.setPPlanStartDay(new Date(rs.getTimestamp(9).getTime()));
                        tmp.setPPlanEndDay(new Date(rs.getTimestamp(10).getTime()));
                    }
                    else if("s".equals(rs.getString(6)))
                    {
                        tmp.setAnalyst2(rs.getString(5));
                        if(rs.getInt(7)==0)
                        {
                            tmp.setStatus("S");
                        }
                        tmp.setAnalyst2ID(rs.getInt(8));
                        tmp.setSPlanStartDay(new Date(rs.getTimestamp(9).getTime()));
                        tmp.setSPlanEndDay(new Date(rs.getTimestamp(10).getTime()));
                    }
                    else if("f".equals(rs.getString(6)))
                    {
                        tmp.setAnalyst3(rs.getString(5));
                        if(rs.getInt(7)==0)
                        {
                            tmp.setStatus("F");
                        }
                        else if(rs.getInt(7)==1)
                        {
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
    
 public static String[] loadEmails() {
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        ArrayList<String> emails = new ArrayList<>();
        
        String query = "SELECT email FROM analyst;";
        
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                emails.add(rs.getString(1));
            }            
        } 
        catch (SQLException ex) {
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
        
        String[] retEmails = emails.toArray(new String[emails.size()]);
        
        return retEmails;
    }
 
    public static boolean loadAnslysts(ArrayList<Analyst> anList) {
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        
        String query = "SELECT id, name, email, position FROM analyst;";
        
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            while (rs.next()) {
                
                Analyst an = new Analyst(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4));
                anList.add(an);
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

    public static boolean deleteBlock(int bid) {
        Connection con = null;
        Statement st = null;
        
        String q1 = "DELETE FROM block b WHERE b.id = " + bid;
        String q2 = "DELETE FROM event_allocation ea WHERE ea.block_allocation_id" +
                 " IN ( SELECT ba.id FROM block_allocation ba WHERE ba.block_id = " + bid + ")";
        String q3 = "DELETE FROM block_allocation ba WHERE ba.block_id = " + bid;
        
        try {
            con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(false);
        
            st = con.createStatement();
            st.executeUpdate(q1);
            st.executeUpdate(q2);
            st.executeUpdate(q3);
            
            con.commit();
        }  catch (SQLException ex) {                
                return false;
        }
        
        return true;
    }

    public static boolean mergeBlock(Integer sBid, Integer mergeID) {
                
        Connection con = null;
        Statement st = null;
        
        //update event_allocation table
        String q1 = "UPDATE event_allocation ea SET ea.block_allocation_id = " + mergeID +
                 " WHERE ea.block_allocation_id = " + sBid;
        
        String q2 = "UPDATE event_allocation ea SET ea.analyst_id = " + mergeID +
                 " WHERE ea.block_allocation_id = " + sBid;
        
        //String q3 = "DELETE FROM block_allocation ba WHERE ba.block_id = " + bid;
        
        try {
            con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(false);
        
            st = con.createStatement();
            st.executeUpdate(q1);
            st.executeUpdate(q2);
            //st.executeUpdate(q3);
            
            con.commit();
        }  catch (SQLException ex) {                
                return false;
        }
        
        return true;
    }

}