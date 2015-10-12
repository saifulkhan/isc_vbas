
package uk.ac.isc.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import uk.ac.isc.data.SeisEvent;
import uk.ac.isc.data.SeisEventList;

/**
 *
 * @author hui
 */
public class HourPanel extends JPanel implements MouseListener {

    private final SeisEventList seList;
    
    private final Date selectDay;
    
    /*if it is 0, it is the from selection; while 1 means to selection*/
    private int fromToFlag;
    
    private JFreeChart hourChart;
    
    private IntervalXYDataset unassignedDayDataset;
    
    /** The chart anchor point. */
    private Point2D anchor = null;

    /** The drawing info collected the last time the chart was drawn. */
    private final ChartRenderingInfo info;
    
    HourPanel(SeisEventList seList, Date selectDay, int fromToFlag) {
        
        this.seList = seList;
        this.selectDay = selectDay;
        this.fromToFlag = fromToFlag;
        
        info = new ChartRenderingInfo();
        
        //this.setSize(100,300);
        createDatasets();
        createChart();
    }
    
    private void createDatasets()
    {
        TimeSeries s1 = new TimeSeries("Unassigned Events by hours");
        Day curr;
        Hour currHour;
        int unassignedCount = 0;

        curr = new Day(selectDay);
        currHour = new Hour(0,curr);

        for (SeisEvent seisEvent : seList.getSeisEvents()) {

            if(new Day(seisEvent.getOrigTime()).equals(curr) && seisEvent.getblAssigned()==false)
            {
                if(new Hour(seisEvent.getOrigTime()).equals(currHour))
                {
                    unassignedCount++;
                }
                else if(!new Hour(seisEvent.getOrigTime()).equals(currHour))
                {
                     s1.add(currHour, unassignedCount);
                     currHour = (Hour)currHour.next();
                     unassignedCount = 0;
                }
            }
            
            //while(currHour.getHour()<23)
            //{
            //    s1.add(currHour, 0);
            //    currHour = (Hour)currHour.next();
            //}
        }
        s1.add(currHour,unassignedCount);
        
        while(currHour.getHour()<23)
        {
            currHour = (Hour)currHour.next();
            s1.add(currHour,0);
        }
        
        unassignedDayDataset = new TimeSeriesCollection(s1);
    }
    
    private void createChart() {

        hourChart = ChartFactory.createXYBarChart(
            "",  // title
            "",// x-axis label   
            true,
            "Unassigned Events by Hours",   // y-axis label
            unassignedDayDataset, // data
            PlotOrientation.HORIZONTAL,
            false,
            false,
            false
        );
        
        XYPlot plot = (XYPlot) hourChart.getPlot();
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        plot.getRenderer().setSeriesPaint(0, Color.DARK_GRAY);
        plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        plot.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);
        plot.getRangeAxis().setInverted(true);
        //plot.setDomainGridlinePaint(Color.white);
        //plot.setRangeGridlinePaint(Color.white);
        //plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        //plot.setDomainCrosshairVisible(true);
        //plot.setRangeCrosshairVisible(true);
        
        //XYItemRenderer r = plot.getRenderer();
        //if (r instanceof XYBarRenderer) {
        //    XYBarRenderer renderer = (XYBarRenderer) r;
        //}
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setLowerMargin(0);
        axis.setUpperMargin(0);
        axis.setInverted(true);
    }
     
    @Override
    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        
        if (this.hourChart == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        
        Color savedColor = g2.getColor();
        Stroke savedStroke = g2.getStroke();
        
        // first determine the size of the chart rendering area...
        Dimension size = getSize();
        Insets insets = getInsets();
        Rectangle2D available = new Rectangle2D.Double(insets.left, insets.top,
                size.getWidth() - insets.left - insets.right,
                size.getHeight() - insets.top - insets.bottom);

        // work out if scaling is required...
        //boolean scale = false;
        double drawWidth = 300;//available.getWidth();
        double drawHeight = 600;//available.getHeight();
        
        
        Rectangle2D chartArea = new Rectangle2D.Double(0.0, 0.0, drawWidth,
                drawHeight);
        
       /* if ((this.chartBuffer == null)
                    || (this.chartBufferWidth != available.getWidth())
                    || (this.chartBufferHeight != available.getHeight())) {
                this.chartBufferWidth = (int) available.getWidth();
                this.chartBufferHeight = (int) available.getHeight();
                GraphicsConfiguration gc = g2.getDeviceConfiguration();
                this.chartBuffer = gc.createCompatibleImage(
                        this.chartBufferWidth, this.chartBufferHeight,
                        Transparency.TRANSLUCENT);
                this.refreshBuffer = true;
            }

            // do we need to redraw the buffer?
            if (this.refreshBuffer) {

                this.refreshBuffer = false; // clear the flag

                Rectangle2D bufferArea = new Rectangle2D.Double(
                        0, 0, this.chartBufferWidth, this.chartBufferHeight);

                // make the background of the buffer clear and transparent
                Graphics2D bufferG2 = (Graphics2D)
                        this.chartBuffer.getGraphics();
                Composite savedComposite = bufferG2.getComposite();
                bufferG2.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.CLEAR, 0.0f));
                Rectangle r = new Rectangle(0, 0, this.chartBufferWidth,
                        this.chartBufferHeight);
                bufferG2.fill(r);
                bufferG2.setComposite(savedComposite);
                
                this.unassignedChart.draw(bufferG2, bufferArea, this.anchor,
                            this.info);

            // zap the buffer onto the panel...
            g2.drawImage(this.chartBuffer, insets.left, insets.top, this);

        }*/               
        this.hourChart.draw(g2, chartArea, this.anchor, this.info);
        
        double x1 = info.getPlotInfo().getDataArea().getMinX();
        double x2 = info.getPlotInfo().getDataArea().getMaxX();
        double y = hourChart.getXYPlot().getDomainAxis().valueToJava2D(selectDay.getTime(), info.getPlotInfo().getDataArea(),
                this.hourChart.getXYPlot().getDomainAxisEdge());
        
        g2.setPaint(Color.RED);
        g2.setStroke(new BasicStroke(5));
        
        g2.drawLine((int)x1, (int)y, (int)x2, (int)y);

        g2.setColor(savedColor);
        g2.setStroke(savedStroke);
        //g2.dispose();

    }

    @Override
    public void mouseClicked(MouseEvent e) {
       
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
        if (this.hourChart == null) {
            return;
        }
        //Plot plot = this.chart.getPlot();
        //Rectangle2D screenDataArea = getScreenDataArea(e.getX(), e.getY());
        //System.out.println(e.getX());
        
        Double time = this.hourChart.getXYPlot().getDomainAxis().java2DToValue(e.getY(), info.getPlotInfo().getDataArea(), this.hourChart.getXYPlot().getDomainAxisEdge());
        
        Date selectedTime = new Date(time.longValue());
        if(fromToFlag == 1)
        {
            selectDay.setHours(selectedTime.getHours());
            selectDay.setMinutes(59);
            selectDay.setSeconds(59);
        }
        else
        {
            selectDay.setHours(selectedTime.getHours());
            selectDay.setMinutes(0);
            selectDay.setSeconds(0);
        }
        
        repaint();
        //System.out.println(new Date(time.longValue()));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
       
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

}
