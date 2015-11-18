/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.isc.hypodepthview;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.SeisDataDAO;

/**
 *
 * @author hui
 */
public class HypoDepthViewTest {
    
    //SeisEventsList events = new SeisEventsList();
    HypocentresList hypoList = new HypocentresList();
    
    public HypoDepthViewTest() {
        
        Integer selectedEvid = 603334701;
        
        boolean retDAO = SeisDataDAO.retrieveHypos(selectedEvid, hypoList.getHypocentres());
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
    // @Test
    // public void hello() {}
    @Test
    public void TestInputData()
    {        
        //for (Hypocentre hypo:hypoList.getHypocentres())
        //{
        //    System.out.println(hypo.getErrDepth());
        //}
        HypoDepthViewPanel hp = new HypoDepthViewPanel(hypoList.getHypocentres());
        
        for(Hypocentre hypo:hp.getHypos())
        {
            System.out.println(hypo);
        }
    }
    
    @Test
    public void TestDepthViewPanel()
    {
        JFrame frame = new JFrame();
        frame.setSize(800, 400);
        frame.setLocation(0,0);
        HypoDepthViewPanel hp = new HypoDepthViewPanel(hypoList.getHypocentres());
        frame.add(hp);
        frame.setVisible(true);
        frame.repaint();
        int result = JOptionPane.showConfirmDialog(null,"Is it right?", "Unit Test", JOptionPane.YES_NO_OPTION);
	Assert.assertEquals (JOptionPane.YES_OPTION, result);
        
    }
}
