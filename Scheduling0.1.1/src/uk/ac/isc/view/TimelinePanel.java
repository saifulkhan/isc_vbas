
package uk.ac.isc.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import uk.ac.isc.data.SeisEvent;
import uk.ac.isc.data.SeisEventList;

/**
 *
 * This panel shows all the assigned and unassigned events number 
 * from the start date to the end date of the data load from schema
 * 
 * @author hui
 */
public class TimelinePanel extends JPanel implements Observer {
   
    /** A data reference for showing the figure*/
    private SeisEventList seList;
    
    /** Two variables to draw the histograms, two JFreechart object first, probably merge later*/
    JFreeChart unassignedChart;
    
    JFreeChart assignedChart;
    
    JFreeChart phaseChart;
    
    /** A flag that indicates that the buffer should be refreshed. */
    private boolean refreshBuffer;

    /** A buffer for the rendered chart. */
    private transient Image chartBuffer;

    /** The height of the chart buffer. */
    private final int chartBufferHeight;

    /** The width of the chart buffer. */
    private final int chartBufferWidth;

    /** The chart anchor point. */
    private Point2D anchor1, anchor2;

    /** The drawing info collected the last time the chart was drawn. */
    private final ChartRenderingInfo info1, info2;

    /** two dataset for the charts*/
    IntervalXYDataset unassignedDataset;
    
    IntervalXYDataset assignedDataset;
    
    IntervalXYDataset phaseDataset;
    
    /** the two variable to save the selected start date and end date*/
    Date startDate;
    
    Date endDate;
    
    Date selectedStartDate;
    
    Date selectedEndDate;
       
    Rectangle2D selectedRegion;
    
    public TimelinePanel(Date startDate, Date endDate, SeisEventList se)
    {
        this.seList = se;
        createDatasets();
        
        chartBufferHeight = 800;
        chartBufferWidth = 500;
        
        createChart();
        
        this.startDate = startDate;
        this.endDate = endDate;
        selectedStartDate = new Date(startDate.getTime());
        selectedEndDate = new Date(endDate.getTime());
        
        info1 = new ChartRenderingInfo();
        info2 = new ChartRenderingInfo();
        //this.setSize(400, 900);
    }
    
    public void setData(SeisEventList se)
    {
        this.seList = se;
    }
    
    public void setSelectedStartDate(Date selectedStartDate)
    {
        //if(this.selectedEndDate.before(selectedStartDate))
        //{
        //    this.selectedStartDate = new Date(startDate.getTime());
        //} else {
            this.selectedStartDate = selectedStartDate;
        //}
        for(SeisEvent se:seList.getSeisEvents())
        {
            if((se.getOrigTime().after(selectedStartDate)||se.getOrigTime().equals(selectedStartDate) )&& se.getOrigTime().before(selectedEndDate))
            {
                se.setTSelection(true);
            }
            else
            {
                se.setTSelection(false);
            }
        }
        //System.out.println(this.selectedStartDate);
        //this.repaint();
        seList.setChangeFlag();
        seList.notifyObservers();
    }
    
    public Date getSelectedStartDate()
    {
        return this.selectedStartDate;
    }
    
    public void setSelectedEndDate(Date selectedEndDate)
    {
        //if(this.selectedStartDate.after(selectedEndDate))
        //{
        //    this.selectedEndDate = new Date(endDate.getTime());
        //} else {
            this.selectedEndDate = selectedEndDate;
        //}
        for(SeisEvent se:seList.getSeisEvents())
        {
            if(se.getOrigTime().after(selectedStartDate) && se.getOrigTime().before(selectedEndDate))
            {
                se.setTSelection(true);
            }
            else
            {
                se.setTSelection(false);
            }
        }
        //System.out.println(this.selectedEndDate);
        //this.repaint();
        seList.setChangeFlag();
        seList.notifyObservers();
    }
    
    public Date getSelectedEndDate()
    {
        return this.selectedEndDate;
    }
    
    /*set the data into the dataset for plotting*/
    private void createDatasets()
    {
        TimeSeries s1 = new TimeSeries("Unassigned Events Distribution");
        TimeSeries s2 = new TimeSeries("Assigned Events Distribution");
        TimeSeries s3 = new TimeSeries("Phase data Districution");
        
        int unassignedCount = 0, assignedCount = 0;
        int phaseCount = 0;
        
        Day curr = new Day(seList.getSeisEvents().get(0).getOrigTime());
        
        for (SeisEvent seisEvent : seList.getSeisEvents()) {
            if(new Day(seisEvent.getOrigTime()).equals(curr) && seisEvent.getblAssigned()==true)
            {
                assignedCount++;
                phaseCount += seisEvent.getPhaseNumber();
            }
            else if(new Day(seisEvent.getOrigTime()).equals(curr) && seisEvent.getblAssigned()==false)
            {
                unassignedCount++;
                phaseCount += seisEvent.getPhaseNumber();
            }
            else if(!new Day(seisEvent.getOrigTime()).equals(curr)) {
                s1.add(curr, unassignedCount);
                s2.add(curr, assignedCount);
                s3.add(curr, phaseCount);
                
                //System.out.println(phaseCount);
                curr = (Day)curr.next();
                assignedCount = 0;
                unassignedCount = 0;
                phaseCount = 0;
            } 
        }
    
        s1.add(curr,unassignedCount);
        s2.add(curr,assignedCount);
        s3.add(curr, phaseCount);
        
        unassignedDataset = new TimeSeriesCollection(s1);
        assignedDataset = new TimeSeriesCollection(s2);
        phaseDataset = new TimeSeriesCollection(s3);
        
    }

    private void createChart() {

        unassignedChart = ChartFactory.createXYBarChart(
            "",  // title
            "",// x-axis label   
            true,
            "Unassigned",   // y-axis label
            unassignedDataset, // data
            PlotOrientation.HORIZONTAL,
            false,
            false,
            false
        );
        
        assignedChart = ChartFactory.createXYBarChart(
            "",  // title
            "",// x-axis label   
            true,
            "Assigned",   // y-axis label
            assignedDataset, // data
            PlotOrientation.HORIZONTAL,
            false,
            false,
            false
        );
            
        phaseChart = ChartFactory.createXYBarChart(
            "",  // title
            "",// x-axis label   
            true,
            "Phase Number",   // y-axis label
            phaseDataset, // data
            PlotOrientation.HORIZONTAL,
            false,
            false,
            false
        );
        //unassignedChart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) unassignedChart.getPlot();
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
        //axis.setDateFormatOverride(new SimpleDateFormat("DD-MMM"));
        
        XYPlot plot2 = (XYPlot) assignedChart.getPlot();
        plot2.setBackgroundPaint(Color.LIGHT_GRAY);
        plot2.getRenderer().setSeriesPaint(0, Color.GREEN);
        plot2.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        plot2.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);
        //plot2.getRangeAxis().setInverted(true);
        plot2.getRangeAxis().setLowerBound(0.0);
        //plot.setDomainGridlinePaint(Color.white);
        //plot.setRangeGridlinePaint(Color.white);
        //plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        //plot.setDomainCrosshairVisible(true);
        //plot.setRangeCrosshairVisible(true);
        
        //XYItemRenderer r = plot.getRenderer();
        //if (r instanceof XYBarRenderer) {
        //    XYBarRenderer renderer = (XYBarRenderer) r;
        //}
        DateAxis axis2 = (DateAxis) plot2.getDomainAxis();       
        axis2.setLowerMargin(0);
        axis2.setUpperMargin(0);
        axis2.setInverted(true);
        axis2.setVisible(false);
        
        XYBarRenderer renderer = (XYBarRenderer)plot.getRenderer();
        //renderer.setShadowVisible(false);
        renderer.setBarPainter(new StandardXYBarPainter());
           
        XYBarRenderer renderer2 = (XYBarRenderer)plot2.getRenderer();
        //renderer.setShadowVisible(false);
        renderer2.setBarPainter(new StandardXYBarPainter());
        
        /** set the two upperbound equal to each other*/
        plot.getRangeAxis().setUpperBound(Math.max(plot.getRangeAxis().getUpperBound(), plot2.getRangeAxis().getUpperBound()));
        plot2.getRangeAxis().setUpperBound(Math.max(plot.getRangeAxis().getUpperBound(), plot2.getRangeAxis().getUpperBound()));
        
        /*add configurations for phase chart*/
        XYPlot plot3 = (XYPlot) phaseChart.getPlot();
        plot3.setBackgroundPaint(Color.LIGHT_GRAY);
        plot3.getRenderer().setSeriesPaint(0, Color.DARK_GRAY);
        plot3.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        plot3.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);
        plot3.getRangeAxis().setInverted(true);
        DateAxis axis3 = (DateAxis) plot3.getDomainAxis();
        axis3.setLowerMargin(0);
        axis3.setUpperMargin(0);
        axis3.setInverted(true);
        axis3.setVisible(false);
        XYBarRenderer renderer3 = (XYBarRenderer)plot3.getRenderer();
        renderer3.setBarPainter(new StandardXYBarPainter());
        
    }
    
    /**
     * Paints the component by drawing the chart to fill the entire component,
     * but allowing for the insets (which will be non-zero if a border has been
     * set for this component).  To increase performance (at the expense of
     * memory), an off-screen buffer image can be used.
     *
     * @param g  the graphics device for drawing on.
     */
    @Override
    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        
        if (this.unassignedChart == null || this.assignedChart == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        
        Color savedColor = g2.getColor();
        
        // first determine the size of the chart rendering area...
        Dimension size = getSize();
        Insets insets = getInsets();
        Rectangle2D available = new Rectangle2D.Double(insets.left, insets.top,
                size.getWidth() - insets.left - insets.right,
                size.getHeight() - insets.top - insets.bottom);

        // work out if scaling is required...
        //boolean scale = false;
        double drawWidth = (available.getWidth()-50)/3;
        double drawHeight = available.getHeight();
                
        Rectangle2D chartArea = new Rectangle2D.Double(0.0, 0.0, drawWidth,
                drawHeight);
                
        Rectangle2D chartArea1 = new Rectangle2D.Double(drawWidth, 0.0, drawWidth+50,
                drawHeight);
        
        Rectangle2D chartArea2 = new Rectangle2D.Double(2*drawWidth+50, 0, drawWidth,
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
        this.assignedChart.draw(g2, chartArea2, this.anchor2, this.info2);
        
        this.phaseChart.draw(g2, chartArea, this.anchor1, this.info2);
        
        this.unassignedChart.draw(g2, chartArea1, this.anchor1, this.info1);
                
        //draw the selected rectangle, set a trasnparent layer for it
                //define selected rectangle
        double x1 = info1.getPlotInfo().getDataArea().getMinX();
        double x2 = info1.getPlotInfo().getDataArea().getMaxX();
        
        //System.out.println(info1.getPlotInfo().getDataArea());
        //System.out.println(selectedStartDate.getTime());
        //System.out.println(this.unassignedChart.getXYPlot().getDomainAxisEdge());
        
        double y1 = this.unassignedChart.getXYPlot().getDomainAxis().valueToJava2D(selectedStartDate.getTime(), info1.getPlotInfo().getDataArea(),
                this.unassignedChart.getXYPlot().getDomainAxisEdge());
        
        double y2 = this.unassignedChart.getXYPlot().getDomainAxis().valueToJava2D(selectedEndDate.getTime(), info1.getPlotInfo().getDataArea(),
                this.unassignedChart.getXYPlot().getDomainAxisEdge());
        
        selectedRegion = new Rectangle2D.Double(x1,y1,(x2-x1),(y2-y1));
        
        // redraw the zoom rectangle (if present) - if useBuffer is false,
        // we use XOR so we can XOR the rectangle away again without redrawing
        // the chart
        g2.setColor(new Color(1f,153/255f,0.0f,0.5f));

        g2.fillRect((int)selectedRegion.getX(),(int)selectedRegion.getY(),(int)selectedRegion.getWidth(),(int)selectedRegion.getHeight());
     
        g2.setColor(savedColor);
        //drawZoomRectangle(g2, !this.useBuffer);

        //g2.dispose();

    }

    @Override
    public void update(Observable o, Object arg) {
        
        createDatasets();
        createChart();
        
        this.repaint();
        // new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /** Helper classes*/
    /**
     * Translates a Java2D point on the chart to a screen location.
     *
     * @param java2DPoint  the Java2D point.
     *
     * @return The screen location.
     */
    public Point translateJava2DToScreen(Point2D java2DPoint) {
        Insets insets = getInsets();
        int x = (int) (java2DPoint.getX() + insets.left);
        int y = (int) (java2DPoint.getY() + insets.top);
        return new Point(x, y);
    }

    /**
     * Translates a panel (component) location to a Java2D point.
     *
     * @param screenPoint  the screen location (<code>null</code> not
     *                     permitted).
     *
     * @return The Java2D coordinates.
     */
    public Point2D translateScreenToJava2D(Point screenPoint) {
        Insets insets = getInsets();
        double x = (screenPoint.getX() - insets.left);
        double y = (screenPoint.getY() - insets.top);
        return new Point2D.Double(x, y);
    }
    
    

}
