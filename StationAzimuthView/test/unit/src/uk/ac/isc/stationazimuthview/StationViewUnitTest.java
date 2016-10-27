package uk.ac.isc.stationazimuthview;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdatainterface.SeisDataDAO;

/**
 *
 * @author hui
 */
public class StationViewUnitTest {

    HypocentresList hyposList = new HypocentresList();
    PhasesList phasesList = new PhasesList();
    JFrame frame;

    public StationViewUnitTest() {

        Integer selectedEvid = 603644068;
        boolean retDAO = SeisDataDAO.retrieveAllPhases(selectedEvid, phasesList.getPhases());

        retDAO = SeisDataDAO.retrieveAllPhasesAmpMag(phasesList.getPhases());

        retDAO = SeisDataDAO.retrieveHypos(selectedEvid, hyposList.getHypocentres());
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
    public void TestStationView() {
        frame = new JFrame();
        frame.setSize(800, 800);
        frame.setLocation(0, 0);
        StationAzimuthView sav = new StationAzimuthView(hyposList, phasesList);
        sav.setMapDegree(30);
        JScrollPane scrollPane = new JScrollPane(sav);
        frame.add(scrollPane);
        frame.setVisible(true);
        frame.repaint();
        int result = JOptionPane.showConfirmDialog(null, "The station azimuth coverage view", "Unit Test", JOptionPane.YES_NO_OPTION);
        Assert.assertEquals(JOptionPane.YES_OPTION, result);
    }

}
