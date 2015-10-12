
package uk.ac.isc.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import uk.ac.isc.data.SeisEvent;
import uk.ac.isc.data.SeisEventList;

/**
 * This panel is for selecting events on the map and manipulating
 * @author hui
 */
public class MapPanel extends JPanel implements MouseListener, Observer{
    
    /** A data reference for showing the figure*/
    private SeisEventList seList;
    
    /**the map size to select region*/
    private double mapSize = 800;
    
    /**Original map is 256 by 256, scale factor when bigger map is needed*/
    private double scale; 
    
    /*we shift the map to make it seismicity meaningful, while we need recalculate the cooridinate to project the events*/
    private double transX;
    
    private int leftX, rightX;
    
    /**map displacement on the panel*/        
    int dispx, dispy;
        
    /**Set true for polygon selection, otherwise, use rectangle selection*/
    private boolean polySelect = false;
    
    private double tempLat, tempLon;
    
    private Double Lat1, Lat2, Lon1, Lon2;
        
    private boolean assignedFlag = false;
    
    /**
     * map to show all the events on top
     */
    private BufferedImage origMap= new BufferedImage(256,256,BufferedImage.TYPE_INT_ARGB); 
    
    private BufferedImage miniMap= new BufferedImage(256,256,BufferedImage.TYPE_INT_ARGB); 
    
    /*variables for drawing polygon*/
    private boolean polyStart = true;
    
    private Path2D path;
    
    public MapPanel(SeisEventList se, boolean assigned)
    {
        this.seList = se;
        
        this.assignedFlag = assigned;
        
        URL url1 = getClass().getClassLoader().getResource("main/resource/0.png");
        
        try {
            origMap = ImageIO.read(url1);
        } catch (IOException ex) {
        }  
        
        genSwappedMap(151.0);
        //this.setSize(new Dimension((int)mapSize,(int)mapSize));
        
        scale = mapSize/256;
        Lat1 = null; Lat2 = null; Lon1 = null; Lon2 = null;
        
        addMouseListener(this);
        
    }

    public void setMapSize(int mapSize)
    {
        this.mapSize = mapSize; 
        scale = (double)mapSize/256;
        this.repaint();
    }
    
    public void setEvents(SeisEventList evList)
    {
        this.seList = evList;
        this.repaint();
    }
    
    public void setRegionUnselection()
    {
        for(SeisEvent ev:seList.getSeisEvents())
        {
             ev.setGSelection(false);
        }
        
        if(polySelect == false)
        {
            this.Lat1 = null;
            this.Lat2 = null;
            this.Lon1 = null;
            this.Lon2 = null;
        }
        else
        {
            this.Lat1 = null;
            this.Lat2 = null;
            this.Lon1 = null;
            this.Lon2 = null;
            path = null;
        }
        
        this.repaint();
    }
        
    private void genSwappedMap(double longInMiddle) {
    
       double px = OsmMercator.LonToX(longInMiddle, 0);
       transX = 128 - px;
       
       BufferedImage leftMap;
       BufferedImage rightMap;
       if(transX<0)
       {
           leftMap = origMap.getSubimage(0,0,(int)-transX, 256);
           rightMap = origMap.getSubimage((int)-transX, 0, (int)(256 + transX), 256);
                  
           //swap the left and right and draw the new image
           Graphics2D g2 = miniMap.createGraphics();
           g2.drawImage(rightMap,null,0,0);
           g2.drawImage(leftMap,null,(int)(256+transX),0);
       }
       else
       {
           leftMap = origMap.getSubimage(0, 0,(int)(256-transX), 256);
           rightMap = origMap.getSubimage((int)(256-transX), 0, (int)transX, 256);
       
           //swap the left and right and draw the new image
           Graphics2D g2 = miniMap.createGraphics();
           g2.drawImage(rightMap,null,0,0);
           g2.drawImage(leftMap,null,(int)transX,0);
       }
       
    }
    
    private void setGeoRange(double Lat1, double Lon1, double Lat2, double Lon2) {
        
        //geoEventsNum = 0;
        if(Lon1>0 && Lon2 < 0) //it means selection is in two regions
        {
            for(SeisEvent ev:seList.getSeisEvents())
            {
            if(ev.getLat()>=Lat1 && ev.getLat()<=Lat2 && ev.getLon()>=Lon1 && ev.getLon()<=180 && ev.getTSelction()==true)
            {
                ev.setGSelection(true);
            }
            else if(ev.getLat()>=Lat1 && ev.getLat()<=Lat2 && ev.getLon()>-180 && ev.getLon()<=Lon2 && ev.getTSelction()==true)
            {
                ev.setGSelection(true);
            }
            else
            {
                ev.setGSelection(false);
            }
            }
        }
        else
        {
            for(SeisEvent ev:seList.getSeisEvents())
            {
            if(ev.getLat()>=Lat1 && ev.getLat()<=Lat2 && ev.getLon()>=Lon1 && ev.getLon()<=Lon2 && ev.getTSelction()==true)
            {
                ev.setGSelection(true);
            }
            else
            {
                ev.setGSelection(false);
            }
            }
        }
        
        this.Lat1 = Lat1;
        this.Lat2 = Lat2;
        this.Lon1 = Lon1;
        this.Lon2 = Lon2;
        
        this.repaint();
    }
    
    public void setPolySelect(boolean isPoly)
    {
        this.polySelect = isPoly;
        setRegionUnselection();
    }
    
    public Double getLat1()
    {
        return Lat1;
    }
    
    public Double getLat2()
    {
        return Lat2;
    }
    
    public Double getLon1()
    {
        return Lon1;
    }
    
    public Double getLon2()
    {
        return Lon2;
    }
        
    @Override
    public void paintComponent(Graphics g) {
    
        super.paintComponent(g);
    
        Graphics2D g2 = (Graphics2D) g.create();
        Paint savedPaint = g2.getPaint();
        Stroke savedStroke = g2.getStroke();
        
        Dimension size = getSize();

        if(size.getWidth()>mapSize)
        {
            dispx = (int)(size.getWidth() - mapSize)/2;
        }
        else
        {
            dispx = 0;
        }
        if(size.getHeight()>mapSize)
        {
            dispy = (int)(size.getHeight() - mapSize)/2;
        }
        else
        {
            dispy = 0;
        }
        
        //Insets insets = getInsets();
        
        g2.drawImage(miniMap, dispx,dispy,(int)mapSize,(int)mapSize, this);
        
        int px,py;
        
        /**the first time, draw all the data outside the selection*/
        g2.setPaint(Color.DARK_GRAY);
        for(SeisEvent ev:seList.getSeisEvents())
        {
            //Day currDay = new Day(ev.getOrigTime());
            //if(startDay == null || endDay == null || currDay.compareTo(startDay)<0 || currDay.compareTo(endDay)>0)
            if(ev.getTSelction()==false && ev.getGSelction()==false && ev.getblAssigned()== assignedFlag)
            {
                //gMini.setStroke(new BasicStroke(3));               
                //px = (int) (dispx + (OsmMercator.LonToX(ev.getLon(), 0)*scale));
                py = (int) (dispy + (OsmMercator.LatToY(ev.getLat(),0)*scale));
                px = ((OsmMercator.LonToX(ev.getLon(), 0)+transX)>0) ? (int)((OsmMercator.LonToX(ev.getLon(), 0)+transX)*scale) : (int)((OsmMercator.LonToX(ev.getLon(), 0)+transX+256)*scale);
                px = (int)(dispx + px);
                g2.fillOval(px-2, py-2, 5, 5);
            }
            
        }
        
        /*Time selected but geo not*/
        g2.setPaint(Color.ORANGE);
        for(SeisEvent ev:seList.getSeisEvents())
        {
            //Day currDay = new Day(ev.getOrigTime());
            //if(startDay != null && endDay != null && currDay.compareTo(startDay)>=0 && currDay.compareTo(endDay)<=0)
            if(ev.getTSelction()==true && ev.getGSelction()==false && ev.getblAssigned()== assignedFlag)
            {
                //px = (int) (dispx + (OsmMercator.LonToX(ev.getLon(), 0)*scale));
                py = (int) (dispy + (OsmMercator.LatToY(ev.getLat(),0)*scale));
                px = ((OsmMercator.LonToX(ev.getLon(), 0)+transX)>0) ? (int)((OsmMercator.LonToX(ev.getLon(), 0)+transX)*scale) : (int)((OsmMercator.LonToX(ev.getLon(), 0)+transX+256)*scale);
                px = (int)(dispx + px);
                //g2.setPaint(Color.BLUE);
                //g2.setStroke(new BasicStroke(3));
                g2.fillOval(px-2, py-2, 5, 5);
            }
        }
        
        g2.setPaint(Color.GREEN);
        for(SeisEvent ev:seList.getSeisEvents())
        {
            //Day currDay = new Day(ev.getOrigTime());
            //if(startDay != null && endDay != null && currDay.compareTo(startDay)>=0 && currDay.compareTo(endDay)<=0)
            if(ev.getTSelction()==true && ev.getGSelction()==true && ev.getblAssigned()== assignedFlag)
            {
                //px = (int) (dispx + (OsmMercator.LonToX(ev.getLon(), 0)*scale));
                py = (int) (dispy + (OsmMercator.LatToY(ev.getLat(),0)*scale));
                px = ((OsmMercator.LonToX(ev.getLon(), 0)+transX)>0) ? (int)((OsmMercator.LonToX(ev.getLon(), 0)+transX)*scale) : (int)((OsmMercator.LonToX(ev.getLon(), 0)+transX+256)*scale);
                px = (int)(dispx + px);
                //g2.setPaint(Color.BLUE);
                //g2.setStroke(new BasicStroke(3));
                g2.fillOval(px-2, py-2, 5, 5);
            }
        }
        
        g2.setPaint(savedPaint);
        g2.setStroke(savedStroke);

        if(Lat1!=null)
        {
            int width=0;
            
            if(Lon1<0 && Lon2<0)
            {
                width = (int) ((OsmMercator.LonToX(Lon2, 0)*scale)-(OsmMercator.LonToX(Lon1, 0)*scale));
            }
            else if(Lon1>0 && Lon2<0)
            {
                width = (int) ((OsmMercator.LonToX(Lon2, 0)-OsmMercator.LonToX(-180, 0))*scale + (OsmMercator.LonToX(180, 0)-OsmMercator.LonToX(Lon1, 0))*scale);
            }
            else
            {
                width = (int) ((OsmMercator.LonToX(Lon2, 0)*scale)-(OsmMercator.LonToX(Lon1, 0)*scale));
            }
            
            int height = (int) ((OsmMercator.LatToY(Lat1, 0)*scale)-(OsmMercator.LatToY(Lat2, 0)*scale));

            if((OsmMercator.LonToX(Lon1, 0)+transX)*scale+dispx >0 )
            {
                g2.drawRect((int) ((OsmMercator.LonToX(Lon1, 0)+transX)*scale+dispx), (int) (OsmMercator.LatToY(Lat2, 0)*scale+dispy), width, height);
            }
            else
            {
                g2.drawRect((int) ((OsmMercator.LonToX(Lon1, 0)+transX+256)*scale+dispx), (int) (OsmMercator.LatToY(Lat2, 0)*scale+dispy), width, height);
            }
            //Rectangle2D rect = new Rectangle2D.Double((OsmMercator.LonToX(Lon1, 0)*scale),(OsmMercator.LatToY(Lat2, 0)*scale),(double)width,(double)height);
        }
        
        /*draw polygon*/
        if(polySelect == true && path != null)
        {
            g2.draw(path);
        }

        //g2.setXORMode(Color.gray);
        //g2.setPaint(Color.GREEN);
        //g2.draw(rect);
        //g2.setPaintMode();
        
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        
        //System.out.println(e.getX());
        if(e.getClickCount()==1)
        {
            if(polySelect == true && polyStart == true)
            {
                path = new Path2D.Double();
                path.moveTo(e.getX(), e.getY());
                polyStart = false;
                //set all the geoselection to false
                for(SeisEvent ev:seList.getSeisEvents())
                {
                    ev.setGSelection(false);
                }
            }
            else if(polySelect == true && polyStart == false)
            {
                path.lineTo(e.getX(), e.getY());
            }
            
        }
        else if(e.getClickCount() == 2)
        {
            path.closePath();
            //now set the selection flags
            setGeoSelection();
            polyStart = true;
            //this.repaint();
        }
        
        this.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
        if(polySelect != true)
        {
            leftX = e.getX()-dispx;
            
            //tempLon = OsmMercator.XToLon((int)(leftX/scale-transX),0);
            Lon1 = OsmMercator.XToLon((int)(leftX/scale-transX),0)>180 ? OsmMercator.XToLon((int)(leftX/scale-transX),0)-360:OsmMercator.XToLon((int)(leftX/scale-transX),0);
            tempLat = OsmMercator.YToLat((int)((e.getY()-dispy)/scale),0);
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
        if(polySelect != true)
        {
            if((e.getX()-dispx)<leftX)
            {
                //show warning, should select from left to right
                
                setRegionUnselection();
            }
            else
            {
                rightX = e.getX()-dispx;
                //Lon1 =  Math.min(tempLon,OsmMercator.XToLon((int)((e.getX()-dispx)/scale),0));
                //Lon2 =  Math.max(tempLon,OsmMercator.XToLon((int)((e.getX()-dispx)/scale),0));
                Lon2 = OsmMercator.XToLon((int)(rightX/scale-transX),0)>180 ? OsmMercator.XToLon((int)(rightX/scale-transX),0)-360:OsmMercator.XToLon((int)(rightX/scale-transX),0);
                Lat1 =  Math.min(tempLat,OsmMercator.YToLat((int)((e.getY()-dispy)/scale),0));
                Lat2 =  Math.max(tempLat,OsmMercator.YToLat((int)((e.getY()-dispy)/scale),0));
            }
            
            //System.out.println(Lon1 + "," + Lon2);
            
            if(Lon1 != null)
                setGeoRange(Lat1,Lon1,Lat2,Lon2);
            //System.out.println(Lon1+" "+ Lon2 + " "+ Lat1 + " "+Lat2);
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(Observable o, Object arg) {
        this.repaint();
    }

    /**helper function to set the geo selection to true if the event is in the selected polygon*/
    private void setGeoSelection() {
        
        int px, py;
        //get the coordinates of each event and use path.contain
        for(SeisEvent ev:seList.getSeisEvents())
        {
            //lat lon to x, y
            py = (int)(OsmMercator.LatToY(ev.getLat(),0)*scale + dispy);
            if((OsmMercator.LonToX(ev.getLon(), 0)+transX)<0)
            {
                px = (int)((OsmMercator.LonToX(ev.getLon(), 0) + transX + 256) * scale + dispx);
            }
            else
            {
                px = (int)((OsmMercator.LonToX(ev.getLon(), 0) + transX) * scale + dispx);
            }
            
            if(path.contains(px, py))
            {
                ev.setGSelection(true);
            }
        }
        
    }

}
