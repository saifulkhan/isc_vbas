/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.isc.agencypiechartview;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdata.SeisDataDAO;

/**
 *
 * @author hui
 */
public class AgencyPieChartViewTest {
            
    PhasesList phasesList = new PhasesList();
     
    public AgencyPieChartViewTest() {
        Integer selectedEvid = 603334701;
        boolean retDAO = SeisDataDAO.retrieveAllPhases(selectedEvid, phasesList.getPhases());
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
    public void TestPieChartData() 
    {
        PieChartData piedata = new PieChartData(phasesList.getPhases());
        Iterator<Map.Entry<String, Double>> entries = piedata.getMap().entrySet().iterator();
        while(entries.hasNext())
        {
            Entry entry = entries.next();
            System.out.print(entry.getKey());
            System.out.println(entry.getValue());
        }
    }
    
    @Test
    public void TestPieChartView() 
    {
        PieChartData piedata = new PieChartData(phasesList.getPhases());
        AgencyPieChartView apcView = new AgencyPieChartView();
        apcView.setData(piedata);
        
        JFrame frame = new JFrame();
        frame.setSize(600, 600);
        frame.setLocation(0,0);
        frame.add(apcView);
        frame.setVisible(true);
        frame.repaint();
        int result = JOptionPane.showConfirmDialog(null,"Is it right?", "Unit Test", JOptionPane.YES_NO_OPTION);
	org.junit.Assert.assertEquals (JOptionPane.YES_OPTION, result);
        
    }
}
