/* 
 * @author hui
 *
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.isc.HypoOverview;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.isc.hypooverview.HypoOverviewPanel;
import uk.ac.isc.hypooverview.HypoOverviewPanel2;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.SeisUtils;

public class HypoOverViewTest {

    JFrame frame;
    HypocentresList hypoList = new HypocentresList();
    HypoOverviewPanel hp;
    HypoOverviewPanel2 hp2;

    public HypoOverViewTest() {

        Hypocentre hp1 = new Hypocentre();
        hp1.setLat(40.9);
        hp1.setLon(6.44);
        hp1.setAgency("ISC");
        hp1.setDepth(10);
        hp1.setIsPrime(true);

        Hypocentre hp2 = new Hypocentre();
        hp2.setLat(40.9);
        hp2.setLon(6.44);
        hp2.setAgency("DDD");
        hp2.setDepth(10);
        hp2.setIsPrime(false);

        Hypocentre hp3 = new Hypocentre();
        //hp3.setLat(40.9);
        //hp3.setLon(6.44);
        hp3.setLat(40);
        hp3.setLon(6);
        hp3.setAgency("NEIC");
        hp3.setDepth(10);
        hp3.setIsPrime(false);

        Hypocentre hp4 = new Hypocentre();
        hp4.setLat(41.9);
        hp4.setLon(6.64);
        //hp4.setLat(40.9);
        //hp4.setLon(6.44);
        hp4.setAgency("KKK");
        hp4.setDepth(10);
        hp4.setIsPrime(false);

        hypoList.getHypocentres().add(hp1);
        hypoList.getHypocentres().add(hp2);
        hypoList.getHypocentres().add(hp3);
        hypoList.getHypocentres().add(hp4);

    }

    /**
     * Test if the map goes to the most left, which long it will be
     */
    // @Test
    @Test
    public void TestRangeCal() {
        //find out the long delta should be equal to the distance delta
        double long1 = SeisUtils.LonFromAziDelta(0, -175, 270, 10);
        double long2 = SeisUtils.LonFromAziDelta(0, -175, 90, 10);
        double long3 = SeisUtils.LonFromAziDelta(50, -175, 270, 10);
        double long4 = SeisUtils.LonFromAziDelta(50, -175, 90, 10);
        double long5 = SeisUtils.LonFromAziDelta(-50, -175, 270, 10);
        double long6 = SeisUtils.LonFromAziDelta(-50, -175, 90, 10);
        Assert.assertEquals(long1, -185.0, 0.01);
        Assert.assertEquals(long2, -165.0, 0.01);
        Assert.assertEquals(long3, -190.28, 0.01);
        Assert.assertEquals(long4, -159.72, 0.01);
        Assert.assertEquals(long5, -190.28, 0.01);
        Assert.assertEquals(long6, -159.72, 0.01);
    }

    /*@Test
     public void TestHighLatMap()
     {
     frame = new JFrame();
     frame.setSize(1200, 1200);
     frame.setLocation(0,0);
     hp = new HypoOverviewPanel(hypoList);
     frame.add(hp);
     frame.setVisible(true);
     frame.repaint();
     int result = JOptionPane.showConfirmDialog(null,"Is it right?", "Unit Test", JOptionPane.YES_NO_OPTION);
     Assert.assertEquals (JOptionPane.YES_OPTION, result);
     }*/
    @Test
    public void TestAnimMap() {
        frame = new JFrame();
        frame.setSize(1200, 1200);
        frame.setLocation(0, 0);
        hp2 = new HypoOverviewPanel2(hypoList);

        hp2.setDepthBandOrder(5);
        frame.add(hp2);
        frame.setVisible(true);
        frame.repaint();
        int result = JOptionPane.showConfirmDialog(null, "Animation, Is it right?", "Unit Test", JOptionPane.YES_NO_OPTION);
        Assert.assertEquals(JOptionPane.YES_OPTION, result);
    }

    @Test
    public void TestMapOrder1() {
        frame = new JFrame();
        frame.setSize(1200, 1200);
        frame.setLocation(0, 0);
        hp2 = new HypoOverviewPanel2(hypoList);

        hp2.setDepthBandOrder(1);
        frame.add(hp2);
        frame.setVisible(true);
        frame.repaint();
        int result = JOptionPane.showConfirmDialog(null, "Deep on top, Is it right?", "Unit Test", JOptionPane.YES_NO_OPTION);
        Assert.assertEquals(JOptionPane.YES_OPTION, result);
    }

    @Test
    public void TestMapOrder2() {
        frame = new JFrame();
        frame.setSize(1200, 1200);
        frame.setLocation(0, 0);
        hp2 = new HypoOverviewPanel2(hypoList);

        hp2.setDepthBandOrder(2);
        frame.add(hp2);
        frame.setVisible(true);
        frame.repaint();
        int result = JOptionPane.showConfirmDialog(null, "Shallow on top, Is it right?", "Unit Test", JOptionPane.YES_NO_OPTION);
        Assert.assertEquals(JOptionPane.YES_OPTION, result);
    }

    @Test
    public void TestMapOrder3() {
        frame = new JFrame();
        frame.setSize(1200, 1200);
        frame.setLocation(0, 0);
        hp2 = new HypoOverviewPanel2(hypoList);

        hp2.setDepthBandOrder(3);
        frame.add(hp2);
        frame.setVisible(true);
        frame.repaint();
        int result = JOptionPane.showConfirmDialog(null, "Random order, Is it right?", "Unit Test", JOptionPane.YES_NO_OPTION);
        Assert.assertEquals(JOptionPane.YES_OPTION, result);
    }

    @Test
    public void TestMapOrder4() {
        frame = new JFrame();
        frame.setSize(1200, 1200);
        frame.setLocation(0, 0);
        hp2 = new HypoOverviewPanel2(hypoList);

        hp2.setDepthBandOrder(4);
        frame.add(hp2);
        frame.setVisible(true);
        frame.repaint();
        int result = JOptionPane.showConfirmDialog(null, "Close last, Is it right?", "Unit Test", JOptionPane.YES_NO_OPTION);
        Assert.assertEquals(JOptionPane.YES_OPTION, result);
    }

    @Test
    public void TestHypoAnimation() {
        frame = new JFrame();
        frame.setSize(1200, 1200);
        frame.setLocation(0, 0);
        hp2 = new HypoOverviewPanel2(hypoList);

        hp2.setDepthBandOrder(4);
        hp2.setHypoVisOptions(3);

        frame.add(hp2);
        frame.setVisible(true);
        frame.repaint();
        int result = JOptionPane.showConfirmDialog(null, "HypoAnimation, Is it right?", "Unit Test", JOptionPane.YES_NO_OPTION);
        Assert.assertEquals(JOptionPane.YES_OPTION, result);
    }
}
