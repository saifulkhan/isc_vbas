/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.isc.seisdata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author hui
 */
public class SeisDataTest {
    
    SeisEventsList events = new SeisEventsList();
    HypocentresList hypos = new HypocentresList(); 
    PhasesList phasesList = new PhasesList();
    
    Integer hypid = 601755435;
    
    TreeMap<String, String> stations = new TreeMap<String,String>();
    
    public SeisDataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
    }
    
    @AfterClass
    public static void tearDownClass() {
        
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void TestHypocentreClone() {
    
        Hypocentre hp1 = new Hypocentre();
        hp1.setLat(110.0);
        hp1.setLon(20.0);
        hp1.setAgency("ISC");
        hp1.setDepth(150);
        
        Hypocentre hp2 = null;
        try {
            hp2 = (Hypocentre) hp1.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(SeisDataTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        hp2.setAgency("NEIC");
        hp2.setDepth(30);
        
        Assert.assertEquals(hp1.getAgency(),"ISC");
        //Assert.assertEquals(hp1.getAgency(),"NEIC");
        //System.out.println(hp1);
        //System.out.println(hp2);
    }
    /*
    @Test
    public void TestSeisDataDAO() throws ParseException {
        
        //test to load events based on time interval
        String fromString = "01-11-2011 00:00:00";
        String toString = "02-11-2011 00:00:00";
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        
        Date from = df.parse(fromString);
        Date to = df.parse(toString);
        
        
        //boolean retDAO = SeisDataDAO.retrieveEvents(events.getEvents(), from, to);
        //Assert.assertTrue(retDAO);
        
        //test to load events based on events id
        ArrayList<String> miniEvids = new ArrayList<String>();
        miniEvids.add("600753561");
        miniEvids.add("17578970");
        miniEvids.add("604366012");
        miniEvids.add("601187434");
        miniEvids.add("600008752");
        miniEvids.add("600000157");
        SeisDataDAO.retrieveEventsByEvList(events.getEvents(),miniEvids);
        SeisDataDAO.retrieveRegionName(events.getEvents());
        
        //print it and have a look if they are correct
        for(int i = 0; i<events.getEvents().size();i++)
        {
            System.out.println(events.getEvents().get(i).getPrimeHypo());
        }
        for(int i = 0; i<events.getEvents().size();i++)
        {
            System.out.println(events.getEvents().get(i).getLocation());
        } 
    }*/
    
    /**
    * Test case to verify if the time to load all the events (takes about  seconds to load)
    */
    @Test
    public void TestSeisEventsLoadingSpeed() 
    {
        Long currentTime = System.currentTimeMillis();
        boolean retDAO = SeisDataDAO.retrieveAllEvents(events.getEvents());
        Long retrievalTime = System.currentTimeMillis();
        Assert.assertTrue((retrievalTime-currentTime)<4000); 
    }
    
        /**
    * Test case to verify if the time to load phase number of all the events (takes about 6.4 seconds to load)
    */
    @Test
    public void TestRegionRetrievalLoadingSpeed() 
    {
        boolean retDAO = SeisDataDAO.retrieveAllEvents(events.getEvents());
        
        Long currentTime = System.currentTimeMillis();
        retDAO = SeisDataDAO.retrieveAllRegionName(events.getEvents());
        Long retrievalTime = System.currentTimeMillis();
        //assert the retrieval time from database smaller than one min
        Assert.assertTrue((retrievalTime-currentTime)<7000); 
    }
    
    /**
    * Test case to verify if the time to load phase number of all the events (it takes 24.59 seconds to load now)
    */
    
    @Test
    public void TestPhaseNumberLoadingSpeed() 
    {
        boolean retDAO = SeisDataDAO.retrieveAllEvents(events.getEvents());
        
        Long currentTime = System.currentTimeMillis();
        retDAO = SeisDataDAO.retrievePhaseNumber(events.getEvents());
        Long retrievalTime = System.currentTimeMillis();
        //assert the retrieval time from database smaller than one min
        Assert.assertTrue((retrievalTime-currentTime)<35000); 
    }
   
    @Test
    public void TestMagnitudeLoadingSpeed() 
    {
        boolean retDAO = SeisDataDAO.retrieveAllEvents(events.getEvents());
        
        Long currentTime = System.currentTimeMillis();
        retDAO = SeisDataDAO.retrieveEventsMagnitude(events.getEvents());
        Long retrievalTime = System.currentTimeMillis();
        //assert the retrieval time from database smaller than one min
        Assert.assertTrue((retrievalTime-currentTime)<20000); 
    }
    
    /**
     * 
     */
    @Test
    public void TestPhaseLoadingSpeed()
    {
        Long currentTime = System.currentTimeMillis();
        Integer selectedEvid = 602068873;
        boolean retDAO = SeisDataDAO.retrieveAllPhases(selectedEvid, phasesList.getPhases());
        
        Long retrievalTime = System.currentTimeMillis();
        
        System.out.println(retrievalTime-currentTime);
        retDAO = SeisDataDAO.retrieveAllPhasesAmpMag(selectedEvid, phasesList.getPhases());
               
        retrievalTime = System.currentTimeMillis();
        //assert the retrieval time from database smaller than one min
        Assert.assertTrue((retrievalTime-currentTime)<9000); 
    }
    
        /**
     * 
     */
    @Test
    public void TestStationMagLoadingSpeed()
    {
        Long currentTime = System.currentTimeMillis();
        
        Integer hypid = 605893376;
        ArrayList<Station> allStaMag = new ArrayList<Station>();
        
        boolean retDAO = SeisDataDAO.retrieveStationMags(hypid, allStaMag);
                   
        Long retrievalTime = System.currentTimeMillis();
        //assert the retrieval time from database smaller than one min
        Assert.assertTrue((retrievalTime-currentTime)<9000); 
    }
    
    /**
     * Test case to verify if the loading region name time is less than 2 seconds
     */
    @Test
    public void TestRegionNameLoadingSpeed() 
    {
        Long currentTime = System.currentTimeMillis();
        boolean retDAO = SeisDataDAO.retrieveAllStationsWithRegions(stations);
        Long retrievalTime = System.currentTimeMillis();
        //assert the retrieval time from database smaller than one min
        Assert.assertTrue((retrievalTime-currentTime)<3000); 
    }
    
    /**
     * Test the longitude calculation based on the fix distance and latitude
     */
    @Test
    public void TestLongitudeCal()
    {
        //find out the long delta should be equal to the distance delta
        double long1 = SeisUtils.LonFromAziDelta(0, 0, 270, 10);
        double long2 = SeisUtils.LonFromAziDelta(0, 10.1, 90, 10);
        double long3 = SeisUtils.LonFromAziDelta(50, 0, 90, 10);
        double long4 = SeisUtils.LonFromAziDelta(50,0,270,10);
        Assert.assertEquals(long1, -10.0, 0.01);
        Assert.assertEquals(long2, 20.1, 0.01);
        Assert.assertEquals(long3, 15.28, 0.01);
        Assert.assertEquals(long4, -15.28, 0.01);
        
    }
    
    //starting test a set of operations, including fixe hypo RF, Ch phase type
    //T,P: move phase from one event to another event
    //call iscloc, banish event, merge event, delete hypos or phase types, create event,
    //add comments,put hypos and change hypid date etc.
    /*
    @Test 
    public void TestRF()
    {
        events.getEvents().clear();
        hypos.getHypocentres().clear();
        
        Integer evid = 601775507;
        //Integer evid = 601676529;
        
        //SeisDataDAO.retrieveEventsByEvList(events.getEvents(),miniEvids);
        
        SeisDataDAO.retrieveHypos(evid,hypos.getHypocentres());
        
        Integer hypid = null;
        String hypoName = null;
        
        for(int i = 0; i<hypos.getHypocentres().size();i++)
        {
            if(hypos.getHypocentres().get(i).getIsPrime()!=true)
            {
                hypid = hypos.getHypocentres().get(i).getHypid();
                hypoName = hypos.getHypocentres().get(i).getAgency();
                break;
            }
        }
        
        boolean bflag = SeisDataDAO.fixPrime(hypid,evid);
        
        Assert.assertTrue(bflag);
        
        hypos.getHypocentres().clear();
        SeisDataDAO.retrieveHypos(evid,hypos.getHypocentres());
        
        for(int i = 0; i<hypos.getHypocentres().size();i++)
        {
            if(hypos.getHypocentres().get(i).getHypid().equals(hypid))
            {
                Assert.assertTrue(hypos.getHypocentres().get(i).getIsPrime());
            }
        }
        
    }*/
    
    /*@Test 
    public void TestFailRFCase()
    {
        events.getEvents().clear();
        hypos.getHypocentres().clear();
        
        Integer hypid = 602970537;
        Integer evid = 606542219;
        
        boolean bflag = SeisDataDAO.fixPrime(hypid,evid);
        
        Assert.assertFalse(bflag);
        
    }*/
    /*
    @Test
    public void TestCHHyposType()
    {
        //Integer hypid = 601952174;
        //case one:change phase type
        String attribute = "depth";
        String value = "20";
        //String value="Pn";
        
        boolean bflag = SeisDataDAO.changeHypo(hypid, attribute, value);
        Assert.assertTrue(bflag);
        
        Hypocentre hypo = SeisDataDAO.retrieveSingleHypo(hypid);
            //will use cases to set different attributes later, first step is testing the phase type only
        Integer depth = hypo.getDepth();
        Date beforeChangeDate = hypo.getOrigTime();
        
        Assert.assertEquals(depth.toString(), value);
        
        //case two: change arrival time
        attribute = "time";
        Date newDate = new Date(beforeChangeDate.getTime()-2000);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        value = df.format(newDate);
        
        bflag = SeisDataDAO.changeHypo(hypid, attribute, value);
        Assert.assertTrue(bflag);
        Hypocentre tmp2 = SeisDataDAO.retrieveSingleHypo(hypid);
            //will use cases to set different attributes later, first step is testing the phase type only
        Date afterChangeDate = tmp2.getOrigTime();
        Assert.assertEquals(2000, beforeChangeDate.getTime()-afterChangeDate.getTime());
        
    }
    
     @Test
    public void TestPutHypo()
    {
        //Integer hypid = 601952174;
                
        Integer evidFrom = 601779506;
        Integer evidTo = 601996336;
        
        boolean bflag = SeisDataDAO.putHypo(hypid, evidFrom, evidTo);
        Assert.assertTrue(bflag);
        
        //to make sure that the hypo has been moved to the new event
        ArrayList<Hypocentre> hypos = new ArrayList<Hypocentre>();
        SeisDataDAO.retrieveHypos(evidTo, hypos);
        
        boolean check1 = false;
        for(Hypocentre hypo:hypos)
        {
           if(hypo.getHypid().equals(hypid))
           {
               check1 = true;
               break;
           }
        }
        Assert.assertTrue(check1);
        
        hypos.clear();
        SeisDataDAO.retrieveHypos(evidFrom, hypos);
        boolean check2 = false;
        for(Hypocentre hypo:hypos)
        {
           if(hypo.getHypid().equals(hypid))
           {
               check2 = true;
               break;
           }
        }
        Assert.assertFalse(check2);
        
        //move it back in case we need test it next time
        bflag = SeisDataDAO.putHypo(hypid, evidTo, evidFrom);
        Assert.assertTrue(bflag);
    }
    
    @Test
    public void TestDeleteHypo()
    {
        //need change the hypid
        //Integer hypid = 601952174;
        Integer evid = 601779506;
        
        boolean bflag = SeisDataDAO.deleteHypo(hypid);
        Assert.assertTrue(bflag);
        
        hypos.getHypocentres().clear();
        SeisDataDAO.retrieveHypos(evid, hypos.getHypocentres());
        boolean check1 = false;
        for(int i = 0; i<hypos.getHypocentres().size();i++)
        {
            if(hypos.getHypocentres().get(i).getHypid().equals(hypid))
            {
                check1 = true;
                break;
            }
        }
        Assert.assertFalse(check1);
        
        //bring it back for the test of next stage
        bflag = SeisDataDAO.undeleteHypo(hypid);
        Assert.assertTrue(bflag);
        
        hypos.getHypocentres().clear();
        SeisDataDAO.retrieveHypos(evid, hypos.getHypocentres());
        boolean check2 = false;
        for(int i = 0; i<hypos.getHypocentres().size();i++)
        {
            if(hypos.getHypocentres().get(i).getHypid().equals(hypid))
            {
                check2 = true;
                break;
            }
        }
        Assert.assertTrue(check2);
    }*/
    /*
    @Test
    public void TestBanishEvent()
    {
        Integer evid = 601779506;
        boolean bflag = SeisDataDAO.banishEvent(evid);
        Assert.assertTrue(bflag);
    
        //unbanish the event
        bflag = SeisDataDAO.unbanishEvent(evid);
        Assert.assertTrue(bflag);
        
    }*/
    /*
    @Test
    public void TestCreateMergeEvent()
    {
        //Integer hypid = 601952174;
        Integer evid1 = 601779506;
                
        Integer evid2 = SeisDataDAO.getNextNewEvid();
        
        boolean bflag = SeisDataDAO.createEvent(hypid,evid2);
        Assert.assertTrue(bflag);
    
        //unbanish the event
        bflag = SeisDataDAO.mergeEvent(evid2,evid1);
        Assert.assertTrue(bflag);
    }
    
    @Test
    public void TestCHPhasesType()
    {
        Integer phid = 659800723;
        
        //case one:change phase type
        String attribute = "phase";
        String value = "Pg";
        //String value="Pn";
        
        boolean bflag = SeisDataDAO.changePhase(phid, attribute, value);
        Assert.assertTrue(bflag);
        
        Phase tmp = SeisDataDAO.retrieveSinglePhase(phid);
            //will use cases to set different attributes later, first step is testing the phase type only
        Date beforeChangeDate = tmp.getArrivalTime();
        
        Assert.assertEquals(tmp.getIscPhaseType(), value);
        
        //case two: change arrival time
        attribute = "second";
        value = "-2";
        bflag = SeisDataDAO.changePhase(phid, attribute, value);
        Assert.assertTrue(bflag);
        Phase tmp2 = SeisDataDAO.retrieveSinglePhase(phid);
            //will use cases to set different attributes later, first step is testing the phase type only
        Date afterChangeDate = tmp2.getArrivalTime();
        Assert.assertEquals(2000, beforeChangeDate.getTime()-afterChangeDate.getTime());
        
    }
    
    
    @Test
    public void TestPutPhase()
    {
        //test individual phase without link with other phas by reading id
        Integer phid = 655172792;
                
        Integer evidFrom = 601779506;
        Integer evidTo = 601996336;
        
        //Phase tmp = SeisDataDAO.retrieveSinglePhase(phid);
        //Integer oldRDID = tmp.getRdid();
        
        Integer newRDID = null;//SeisDataDAO.getNextNewRdid();
        
        boolean bflag = SeisDataDAO.putPhase(phid, evidFrom, evidTo,newRDID);
        Assert.assertTrue(bflag);
        
        //to make sure that the phase has been moved to the new event
        ArrayList<Phase> phases = new ArrayList<Phase>();
        SeisDataDAO.retrieveAllPhases(evidTo, phases);
        
        boolean check1 = false;
        for(Phase phase:phases)
        {
           if(phase.getPhid().equals(phid))
           {
               check1 = true;
               break;
           }
        }
        Assert.assertTrue(check1);
        
        phases.clear();
        SeisDataDAO.retrieveAllPhases(evidFrom, phases);
        boolean check2 = false;
        for(Phase phase:phases)
        {
           if(phase.getPhid().equals(phid))
           {
               check2 = true;
               break;
           }
        }
        Assert.assertFalse(check2);
        
        //now put it back for next time
        bflag =  SeisDataDAO.putPhase(phid,evidTo,evidFrom,null);
        Assert.assertTrue(bflag);
    }
    
    @Test
    public void TestDeletePhase()
    {
        //need change the hypid
        Integer phid = 655172789;
        Integer evid = 601779506;
        
        boolean bflag = SeisDataDAO.deletePhase(phid);
        Assert.assertTrue(bflag);
        
        phasesList.getPhases().clear();
        SeisDataDAO.retrieveAllPhases(evid, phasesList.getPhases());
        boolean check1 = false;
        for(int i = 0; i<phasesList.getPhases().size();i++)
        {
            if(phasesList.getPhases().get(i).getPhid().equals(phid))
            {
                check1 = true;
                break;
            }
        }
        Assert.assertFalse(check1);
    }
    
    @Test
    public void TestTakePhase()
    {
        //need change the hypid
        Integer evid = 602390261;
        Integer phid = 641084824;
                
        boolean bflag = SeisDataDAO.takePhase(phid);
        Assert.assertTrue(bflag);
        
        phasesList.getPhases().clear();
        SeisDataDAO.retrieveAllPhases(evid, phasesList.getPhases());
        boolean check1 = false;
        for(int i = 0; i<phasesList.getPhases().size();i++)
        {
            if(phasesList.getPhases().get(i).getPhid().equals(phid))
            {
                check1 = true;
                break;
            }
        }
        Assert.assertFalse(check1);
    }
    */
}
