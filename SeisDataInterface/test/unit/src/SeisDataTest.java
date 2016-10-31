/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.isc.seisdatainterface;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdata.SeisEventsList;
import uk.ac.isc.seisdata.SeisUtils;
import uk.ac.isc.seisdata.Station;

/**
 *
 *  
 */
public class SeisDataTest {

    SeisEventsList events = new SeisEventsList();
    HypocentresList hypos = new HypocentresList();
    PhasesList phasesList = new PhasesList();

    Integer hypid = 601755435;

    TreeMap<String, String> stations = new TreeMap<String, String>();

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

        Assert.assertEquals(hp1.getAgency(), "ISC");
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
     * Test case to verify if the time to load all the events (takes about
     * seconds to load)
     */
    @Test
    public void TestSeisEventsLoadingSpeed() {
        Long currentTime = System.currentTimeMillis();
        boolean retDAO = SeisDataDAO.retrieveAllEvents(events.getEvents());
        Long retrievalTime = System.currentTimeMillis();
        Assert.assertTrue((retrievalTime - currentTime) < 4000);
    }

    /**
     * Test case to verify if the time to load phase number of all the events
     * (takes about 6.4 seconds to load)
     */
    @Test
    public void TestRegionRetrievalLoadingSpeed() {
        boolean retDAO = SeisDataDAO.retrieveAllEvents(events.getEvents());

        Long currentTime = System.currentTimeMillis();
        retDAO = SeisDataDAO.retrieveAllRegionName(events.getEvents());
        Long retrievalTime = System.currentTimeMillis();
        //assert the retrieval time from database smaller than one min
        Assert.assertTrue((retrievalTime - currentTime) < 7000);
    }

    /**
     * Test case to verify if the time to load phase number of all the events
     * (it takes 24.59 seconds to load now)
     */
    @Test
    public void TestPhaseNumberLoadingSpeed() {
        boolean retDAO = SeisDataDAO.retrieveAllEvents(events.getEvents());

        Long currentTime = System.currentTimeMillis();
        retDAO = SeisDataDAO.retrievePhaseNumber(events.getEvents());
        Long retrievalTime = System.currentTimeMillis();
        //assert the retrieval time from database smaller than one min
        Assert.assertTrue((retrievalTime - currentTime) < 35000);
    }

    @Test
    public void TestMagnitudeLoadingSpeed() {
        boolean retDAO = SeisDataDAO.retrieveAllEvents(events.getEvents());

        Long currentTime = System.currentTimeMillis();
        retDAO = SeisDataDAO.retrieveEventsMagnitude(events.getEvents());
        Long retrievalTime = System.currentTimeMillis();
        //assert the retrieval time from database smaller than one min
        Assert.assertTrue((retrievalTime - currentTime) < 20000);
    }

    /**
     *
     */
    @Test
    public void TestPhaseLoadingSpeed() {
        Long currentTime = System.currentTimeMillis();
        Integer selectedEvid = 602068873;
        boolean retDAO = SeisDataDAO.retrieveAllPhases(selectedEvid, phasesList.getPhases());

        Long retrievalTime = System.currentTimeMillis();

        System.out.println(retrievalTime - currentTime);
        retDAO = SeisDataDAO.retrieveAllPhasesAmpMag(selectedEvid, phasesList.getPhases());

        retrievalTime = System.currentTimeMillis();
        //assert the retrieval time from database smaller than one min
        Assert.assertTrue((retrievalTime - currentTime) < 9000);
    }

    /**
     *
     */
    @Test
    public void TestStationMagLoadingSpeed() {
        Long currentTime = System.currentTimeMillis();

        Integer hypid = 605893376;
        ArrayList<Station> allStaMag = new ArrayList<Station>();

        boolean retDAO = SeisDataDAO.retrieveStationMags(hypid, allStaMag);

        Long retrievalTime = System.currentTimeMillis();
        //assert the retrieval time from database smaller than one min
        Assert.assertTrue((retrievalTime - currentTime) < 9000);
    }

    /**
     * Test case to verify if the loading region name time is less than 2
     * seconds
     */
    @Test
    public void TestRegionNameLoadingSpeed() {
        Long currentTime = System.currentTimeMillis();
        boolean retDAO = SeisDataDAO.retrieveAllStationsWithRegions(stations);
        Long retrievalTime = System.currentTimeMillis();
        //assert the retrieval time from database smaller than one min
        Assert.assertTrue((retrievalTime - currentTime) < 3000);
    }

    /**
     * Test the longitude calculation based on the fix distance and latitude
     */
    @Test
    public void TestLongitudeCal() {
        //find out the long delta should be equal to the distance delta
        double long1 = SeisUtils.LonFromAziDelta(0, 0, 270, 10);
        double long2 = SeisUtils.LonFromAziDelta(0, 10.1, 90, 10);
        double long3 = SeisUtils.LonFromAziDelta(50, 0, 90, 10);
        double long4 = SeisUtils.LonFromAziDelta(50, 0, 270, 10);
        Assert.assertEquals(long1, -10.0, 0.01);
        Assert.assertEquals(long2, 20.1, 0.01);
        Assert.assertEquals(long3, 15.28, 0.01);
        Assert.assertEquals(long4, -15.28, 0.01);

    }

}
