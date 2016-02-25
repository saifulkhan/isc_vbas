package uk.ac.isc.phaseview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.Phase;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdata.SeisUtils;

/**
 *
 * The general view to show all the phases with travel time curves
 *
 * @author hui
 */
public class PhaseTravelViewPanel extends JPanel implements MouseListener, MouseMotionListener {

    //the data of all the phases
    private final PhasesList pList;

    //the data in the seleted range
    private final PhasesList detailedPList = new PhasesList();

    private Hypocentre prime;

    //for filtering the data
    private double residualCutoffLevel = 0.0;

    //switch between the roi box (coordinates are bit different), this makes the double boxes and needs been fixed
    private boolean initFlag = true;

    //these two for showing the phases data
    private DuplicateUnorderTimeSeries phaseSeries;

    private final DuplicateUnorderTimeSeriesCollection dataset = new DuplicateUnorderTimeSeriesCollection();

    //curve data
    private DuplicateUnorderTimeSeriesCollection ttdData = null;

    //freechart object for drawing the image
    private JFreeChart freechart = null;

    private final int imageWidth = 500;

    private final int imageHeight = 1000;

    //buffer images of the view
    private BufferedImage phasesImage = null;

    private final BufferedImage phaseImageWithRect = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

    //for filtering the data with phase types
    private final boolean[] typesVisible = {true, true, true, true, true};

    private final String[][] phaseTypes = SeisUtils.getGroupedPhaseTypes();
    //private double pageNumber = 10.0;

    //get ranges, the view should be fixed
    private Long minTime;

    private Long maxTime;

    private double maxDist;

    //interaction with detailed view
    /**
     * The drawing info collected the last time the chart was drawn.
     */
    private ChartRenderingInfo info;

    /**
     * The zoom rectangle starting point (selected by the user with a mouse
     * click). This is a point on the screen, not the chart (which may have been
     * scaled up or down to fit the panel).
     */
    private Point2D zoomPoint = null;

    /**
     * The zoom rectangle (selected by the user with the mouse).
     */
    private Rectangle2D zoomRectangle = null;

    private double zoomMinTime;

    private double zoomMaxTime;

    private double zoomMinDist;

    private double zoomMaxDist;

    private int xOffset;

    private int yOffset;

    PhaseTravelViewPanel(PhasesList pList, Hypocentre ph) {
        this.pList = pList;
        setPreferredSize(new Dimension(500, 1000));

        phaseSeries = new DuplicateUnorderTimeSeries("");

        zoomMinTime = Double.MAX_VALUE;
        zoomMaxTime = Double.MIN_VALUE;
        zoomMinDist = Double.MAX_VALUE;
        zoomMaxDist = Double.MIN_VALUE;

        //put phases into the dataseries
        for (Phase p : pList.getPhases()) {
            if ((p.getArrivalTime() != null) && ((p.getTimeResidual() != null && Math.abs(p.getTimeResidual()) > residualCutoffLevel) || (p.getTimeResidual() == null))) {
                RegularTimePeriod rp = new Second(p.getArrivalTime());
                phaseSeries.add(rp, p.getDistance());
                if (detailedPList.getPhases().size() < 20) {
                    detailedPList.getPhases().add(p);

                    //get zoom range
                    if ((double) p.getArrivalTime().getTime() < zoomMinTime) {
                        zoomMinTime = (double) p.getArrivalTime().getTime();
                    }

                    if ((double) p.getArrivalTime().getTime() > zoomMaxTime) {
                        zoomMaxTime = (double) p.getArrivalTime().getTime();
                    }

                    if (p.getDistance() < zoomMinDist) {
                        zoomMinDist = p.getDistance();
                    }

                    if (p.getDistance() > zoomMaxDist) {
                        zoomMaxDist = p.getDistance();
                    }
                }

            }
        }
        dataset.addSeries(phaseSeries);

        this.prime = ph;

        minTime = Math.min(ph.getOrigTime().getTime(), phaseSeries.getMinX());
        maxTime = phaseSeries.getMaxX();
        maxDist = phaseSeries.getMaxY();

        createTravelImage();

        //get the rectangle based on the freechart, reverse the min and max
        double yMax = freechart.getXYPlot().getDomainAxis().valueToJava2D(zoomMinTime, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getDomainAxisEdge());
        double yMin = freechart.getXYPlot().getDomainAxis().valueToJava2D(zoomMaxTime, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getDomainAxisEdge());
        double xMin = freechart.getXYPlot().getRangeAxis().valueToJava2D(zoomMinDist, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getRangeAxisEdge());
        double xMax = freechart.getXYPlot().getRangeAxis().valueToJava2D(zoomMaxDist, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getRangeAxisEdge());

        //define the rectangle and draw it in the buffer
        Graphics2D g2 = (Graphics2D) phaseImageWithRect.getGraphics();
        zoomRectangle = new Rectangle2D.Double(xMin, yMin, xMax - xMin, yMax - yMin);
        drawZoomRectangle(g2, true);

        addMouseListener(this);
    }

    PhaseTravelViewPanel(PhasesList pList, Hypocentre ph, DuplicateUnorderTimeSeriesCollection ttdData) {
        this.pList = pList;
        setPreferredSize(new Dimension(500, 1000));

        phaseSeries = new DuplicateUnorderTimeSeries("");

        zoomMinTime = Double.MAX_VALUE;
        zoomMaxTime = Double.MIN_VALUE;
        zoomMinDist = Double.MAX_VALUE;
        zoomMaxDist = Double.MIN_VALUE;

        //put phases into the dataseries
        for (Phase p : pList.getPhases()) {
            if ((p.getArrivalTime() != null) && ((p.getTimeResidual() != null && Math.abs(p.getTimeResidual()) > residualCutoffLevel) || (p.getTimeResidual() == null))) {
                RegularTimePeriod rp = new Second(p.getArrivalTime());
                phaseSeries.add(rp, p.getDistance());
                if (detailedPList.getPhases().size() < 20) {
                    detailedPList.getPhases().add(p);

                    //get zoom range
                    if ((double) p.getArrivalTime().getTime() < zoomMinTime) {
                        zoomMinTime = (double) p.getArrivalTime().getTime();
                    }

                    if ((double) p.getArrivalTime().getTime() > zoomMaxTime) {
                        zoomMaxTime = (double) p.getArrivalTime().getTime();
                    }

                    if (p.getDistance() < zoomMinDist) {
                        zoomMinDist = p.getDistance();
                    }

                    if (p.getDistance() > zoomMaxDist) {
                        zoomMaxDist = p.getDistance();
                    }
                }

            }
        }
        dataset.addSeries(phaseSeries);

        this.prime = ph;

        minTime = Math.min(ph.getOrigTime().getTime(), phaseSeries.getMinX());
        maxTime = phaseSeries.getMaxX();
        maxDist = phaseSeries.getMaxY();
        this.ttdData = ttdData;

        createTravelImage();

        //get the rectangle based on the freechart, reverse the min and max
        double yMax = freechart.getXYPlot().getDomainAxis().valueToJava2D(zoomMinTime, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getDomainAxisEdge());
        double yMin = freechart.getXYPlot().getDomainAxis().valueToJava2D(zoomMaxTime, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getDomainAxisEdge());
        double xMin = freechart.getXYPlot().getRangeAxis().valueToJava2D(zoomMinDist, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getRangeAxisEdge());
        double xMax = freechart.getXYPlot().getRangeAxis().valueToJava2D(zoomMaxDist, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getRangeAxisEdge());

        //define the rectangle and draw it in the buffer
        Graphics2D g2 = (Graphics2D) phaseImageWithRect.getGraphics();
        zoomRectangle = new Rectangle2D.Double(xMin, yMin, xMax - xMin, yMax - yMin);
        drawZoomRectangle(g2, false);

        addMouseListener(this);

    }

    public void setPrime(Hypocentre ph) {
        this.prime = ph;
    }

    public void setPhaseTypeVisible(int type, boolean vis) {
        typesVisible[type] = vis;
    }

    public void setTTDData(DuplicateUnorderTimeSeriesCollection ttdData) {
        this.ttdData = ttdData;
    }

    public double[] getRange() {
        double[] range = new double[4];

        range[0] = zoomMinTime;
        range[1] = zoomMaxTime;
        range[2] = zoomMinDist;
        range[3] = zoomMaxDist;

        return range;
    }

    //once the data changes, call this function
    public void UpdateData() {
        detailedPList.getPhases().clear();
        dataset.removeAllSeries();

        setPreferredSize(new Dimension(500, 1000));
        phaseSeries = new DuplicateUnorderTimeSeries("");

        zoomMinTime = Double.MAX_VALUE;
        zoomMaxTime = Double.MIN_VALUE;
        zoomMinDist = Double.MAX_VALUE;
        zoomMaxDist = Double.MIN_VALUE;

        //put phases into the dataseries
        for (Phase p : pList.getPhases()) {
            if ((p.getArrivalTime() != null) && ((p.getTimeResidual() != null && Math.abs(p.getTimeResidual()) > residualCutoffLevel) || (p.getTimeResidual() == null))) {
                RegularTimePeriod rp = new Second(p.getArrivalTime());
                phaseSeries.add(rp, p.getDistance());
                if (detailedPList.getPhases().size() < 20) {
                    detailedPList.getPhases().add(p);

                    //get zoom range
                    if ((double) p.getArrivalTime().getTime() < zoomMinTime) {
                        zoomMinTime = (double) p.getArrivalTime().getTime();
                    }

                    if ((double) p.getArrivalTime().getTime() > zoomMaxTime) {
                        zoomMaxTime = (double) p.getArrivalTime().getTime();
                    }

                    if (p.getDistance() < zoomMinDist) {
                        zoomMinDist = p.getDistance();
                    }

                    if (p.getDistance() > zoomMaxDist) {
                        zoomMaxDist = p.getDistance();
                    }
                }

            }
        }
        dataset.addSeries(phaseSeries);

        minTime = Math.min(prime.getOrigTime().getTime(), phaseSeries.getMinX());
        maxTime = phaseSeries.getMaxX();
        maxDist = phaseSeries.getMaxY();
        //this.ttdData = ttdData;

        createTravelImage();

        //get the rectangle based on the freechart, reverse the min and max
        double yMax = freechart.getXYPlot().getDomainAxis().valueToJava2D(zoomMinTime, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getDomainAxisEdge());
        double yMin = freechart.getXYPlot().getDomainAxis().valueToJava2D(zoomMaxTime, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getDomainAxisEdge());
        double xMin = freechart.getXYPlot().getRangeAxis().valueToJava2D(zoomMinDist, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getRangeAxisEdge());
        double xMax = freechart.getXYPlot().getRangeAxis().valueToJava2D(zoomMaxDist, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getRangeAxisEdge());

        //define the rectangle and draw it in the buffer
        Graphics2D g2 = (Graphics2D) phaseImageWithRect.getGraphics();
        zoomRectangle = new Rectangle2D.Double(xMin, yMin, xMax - xMin, yMax - yMin);
        drawZoomRectangle(g2, false);

        addMouseListener(this);

        repaint();
    }

    /**
     * helper function to refresh the detailedPList buffer for the detail view
     * based on the selection range
     */
    public void updateDetailPList() {

        double yMin, yMax, xMin, xMax;

        if (initFlag == true) {
            yMin = zoomRectangle.getY();
            yMax = yMin + zoomRectangle.getHeight();
            xMin = zoomRectangle.getX();
            xMax = xMin + zoomRectangle.getWidth();
        } else {
            yMin = zoomRectangle.getY() - yOffset;
            yMax = yMin + zoomRectangle.getHeight();
            xMin = zoomRectangle.getX() - xOffset;
            xMax = xMin + zoomRectangle.getWidth();
        }

        //use zoomRectangle to get the min-max Time and min-max distance
        zoomMinTime = freechart.getXYPlot().getDomainAxis().java2DToValue(yMax, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getDomainAxisEdge());
        zoomMaxTime = freechart.getXYPlot().getDomainAxis().java2DToValue(yMin, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getDomainAxisEdge());
        //test the time
        Date minTimeTemp = new Date();
        minTimeTemp.setTime((long) zoomMinTime);
        Date maxTimeTemp = new Date();
        maxTimeTemp.setTime((long) zoomMaxTime);
        //System.out.println(minTimeTemp.toString() + " " + maxTimeTemp.toString());
        zoomMinDist = freechart.getXYPlot().getRangeAxis().java2DToValue(xMin, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getRangeAxisEdge());
        zoomMaxDist = freechart.getXYPlot().getRangeAxis().java2DToValue(xMax, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getRangeAxisEdge());

        //start finding data in the range
        detailedPList.getPhases().clear();

        for (Phase p : pList.getPhases()) {
            boolean showPhase = false;
            int group = 4;
            if (p.getIscPhaseType() != null) {
                for (int i = 0; i < phaseTypes.length; i++) {
                    String[] sub = phaseTypes[i];
                    for (String sub1 : sub) {
                        if (p.getIscPhaseType().equals(sub1)) {
                            group = i;
                        }
                    }
                }

            }
            showPhase = typesVisible[group];

            if (p.getArrivalTime() != null && showPhase == true) //&& p.getIscPhaseType()!=null)
            {
                double ptime = (double) p.getArrivalTime().getTime();
                double dist = p.getDistance();
                if (ptime > zoomMinTime && ptime < zoomMaxTime && dist > zoomMinDist && dist < zoomMaxDist) {
                    detailedPList.getPhases().add(p);
                }
            }
        }
        detailedPList.fireSeisDataChanged();

    }

    /**
     * the range won't change just filter the data
     */
    public void filterData() {

        dataset.removeAllSeries();
        phaseSeries = new DuplicateUnorderTimeSeries("");

        //put phases into the dataseries
        for (Phase p : pList.getPhases()) {

            //get phase type and check to which type group it belongs
            boolean showPhase = false;
            int group = 4; //other group
            //showPhase = typesVisible[group];

            if (p.getIscPhaseType() != null) {
                for (int i = 0; i < phaseTypes.length; i++) {
                    String[] sub = phaseTypes[i];
                    for (String sub1 : sub) {
                        if (p.getIscPhaseType().equals(sub1)) {
                            group = i;
                        }
                    }
                }

            }
            showPhase = typesVisible[group];

            if (p.getArrivalTime() != null && showPhase == true)// && p.getIscPhaseType()!=null)
            {
                if (residualCutoffLevel == 0) {
                    RegularTimePeriod rp = new Second(p.getArrivalTime());
                    phaseSeries.add(rp, p.getDistance());
                } else {
                    if (p.getTimeResidual() != null) {
                        if (Math.abs(p.getTimeResidual()) > residualCutoffLevel) {
                            RegularTimePeriod rp = new Second(p.getArrivalTime());
                            phaseSeries.add(rp, p.getDistance());

                        }
                    }
                }
            }
        }

        dataset.addSeries(phaseSeries);

        createTravelImage();

        repaint();

    }

    public PhasesList getDetailedPList() {
        return this.detailedPList;
    }

    //set the residual level to filter the data
    public void setResidualCutoffLevel(double level) {
        this.residualCutoffLevel = level;
    }

    public double getResidualCutoffLevel() {
        return this.residualCutoffLevel;
    }

    /**
     * helper function to use jfreechart to generate the bufferedimage
     */
    private void createTravelImage() {
        //define first axis
        DateAxis timeAxis = new DateAxis("");
        timeAxis.setRange((double) (minTime - 2), (double) (maxTime + 2));
        timeAxis.setLowerMargin(0.02);  // reduce the default margins
        timeAxis.setUpperMargin(0.02);
        timeAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
        NumberAxis valueAxis = new NumberAxis("");
        valueAxis.setRange(0.0, Math.min(180, maxDist + 1));
        //XYDotRenderer xyDotRend = new XYDotRenderer();
        //xyDotRend.setDotWidth(6);
        //xyDotRend.setDotHeight(6);
        XYDotRenderer renderer = new TravelViewRenderer(pList.getPhases());

        //XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, renderer);
        PhasesWithCurvePlot plot = new PhasesWithCurvePlot(dataset, timeAxis, valueAxis, renderer, ttdData);
        plot.setOrientation(PlotOrientation.HORIZONTAL);

        freechart = new JFreeChart(plot);

        freechart.removeLegend();

        phasesImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Rectangle2D bufferArea = new Rectangle2D.Double(0, 0, imageWidth, imageHeight);
        //phasesImage = freechart.createBufferedImage(imageWidth, imageHeight);
        this.info = new ChartRenderingInfo();
        Graphics2D g2 = phasesImage.createGraphics();

        freechart.draw(g2, bufferArea, info);

        Graphics2D gCopy = (Graphics2D) phaseImageWithRect.getGraphics();
        gCopy.drawImage(phasesImage, 0, 0, null);

        if (initFlag == true) {
            drawZoomRectangle(gCopy, false);
        } else {
            drawZoomRectangleManually(gCopy, false);
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        xOffset = Math.max((getWidth() - imageWidth) / 2, 0);
        yOffset = Math.max((getHeight() - imageHeight) / 2, 0);

        g2.drawImage(phaseImageWithRect, xOffset, yOffset, this);

        // TEST: 
        Global.logDebug("Write BufferedImage.");
        try {
            ImageIO.write(phaseImageWithRect, "png",
                    new File("/export/home/saiful/assess/temp/phaseImageWithRect.png"));
        } catch (Exception e) {
            Global.logSevere("Error creating a png.");
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (this.freechart == null) {
            return;
        }

        //if (this.zoomRectangle == null) {
        Rectangle2D screenDataArea = getScreenDataArea();
        if (screenDataArea != null) {
            this.zoomPoint = getPointInRectangle(e.getX(), e.getY(),
                    screenDataArea);
        } else {
            this.zoomPoint = null;
        }
        //}

    }

    @Override
    public void mouseDragged(MouseEvent e) {

        //exception with no handler
        if (this.zoomPoint == null) {
            return;
        }

        //Graphics2D g2 = (Graphics2D) phasesImage.getGraphics();
        Rectangle2D scaledDataArea = getScreenDataArea();
        // selected rectangle shouldn't extend outside the data area...
        double xmax = Math.min(e.getX(), scaledDataArea.getMaxX());
        double ymax = Math.min(e.getY(), scaledDataArea.getMaxY());
        this.zoomRectangle = new Rectangle2D.Double(
                this.zoomPoint.getX(), this.zoomPoint.getY(),
                xmax - this.zoomPoint.getX(), ymax - this.zoomPoint.getY());

        Graphics2D g2 = (Graphics2D) phaseImageWithRect.getGraphics();
        drawZoomRectangleManually(g2, false);
        repaint();

        g2.dispose();

    }

    @Override
    public void mouseReleased(MouseEvent e) {

        //exception with no handler
        if (this.zoomPoint == null) {
            return;
        }

        //Graphics2D g2 = (Graphics2D) phasesImage.getGraphics();
        Rectangle2D scaledDataArea = getScreenDataArea();
        // selected rectangle shouldn't extend outside the data area...
        double xmax = Math.min(e.getX(), scaledDataArea.getMaxX());
        double ymax = Math.min(e.getY(), scaledDataArea.getMaxY());

        if ((xmax - this.zoomPoint.getX()) > 0 && (ymax - this.zoomPoint.getY()) > 0) {
            this.zoomRectangle = new Rectangle2D.Double(
                    this.zoomPoint.getX(), this.zoomPoint.getY(),
                    xmax - this.zoomPoint.getX(), ymax - this.zoomPoint.getY());
        } else {
            return;
        }

        Graphics2D g2 = (Graphics2D) phaseImageWithRect.getGraphics();

        g2.drawImage(phasesImage, 0, 0, null);

        drawZoomRectangleManually(g2, false);
        repaint();

        updateDetailPList();

        g2.dispose();
    }

    /**
     * Returns the data area for the chart (the area inside the axes) with the
     * current scaling applied (that is, the area as it appears on screen).
     *
     * @return The scaled data area.
     */
    public Rectangle2D getScreenDataArea() {
        Rectangle2D dataArea = this.info.getPlotInfo().getDataArea();
        Insets insets = getInsets();
        double x = dataArea.getX() + insets.left + xOffset;
        double y = dataArea.getY() + insets.top + yOffset;
        double w = dataArea.getWidth();
        double h = dataArea.getHeight();
        return new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * Returns a point based on (x, y) but constrained to be within the bounds
     * of the given rectangle. This method could be moved to JCommon.
     *
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     * @param area the rectangle (<code>null</code> not permitted).
     *
     * @return A point within the rectangle.
     */
    private Point2D getPointInRectangle(int x, int y, Rectangle2D area) {
        double xx = Math.max(area.getMinX(), Math.min(x, area.getMaxX()));
        double yy = Math.max(area.getMinY(), Math.min(y, area.getMaxY()));
        return new Point2D.Double(xx, yy);
    }

    /**
     * Draws zoom rectangle (if present). The drawing is performed in XOR mode,
     * therefore when this method is called twice in a row, the second call will
     * completely restore the state of the canvas.
     *
     * @param g2 the graphics device.
     * @param xor use XOR for drawing?
     */
    private void drawZoomRectangle(Graphics2D g2, boolean xor) {
        if (this.zoomRectangle != null) {
            if (xor) {
                // Set XOR mode to draw the zoom rectangle
                g2.setXORMode(Color.gray);
            }

            g2.setPaint(Color.BLACK);

            g2.setStroke(new BasicStroke(2));
            //g2.fill(this.zoomRectangle);
            //g2.fillRect(((int)zoomRectangle.getX()),((int)zoomRectangle.getY()),(int)zoomRectangle.getWidth(),(int)zoomRectangle.getHeight());
            g2.drawRect(((int) zoomRectangle.getX()), ((int) zoomRectangle.getY()), (int) zoomRectangle.getWidth(), (int) zoomRectangle.getHeight());

            if (xor) {
                // Reset to the default 'overwrite' mode
                g2.setPaintMode();
            }
        }

        initFlag = true;
        //this.zoomRectangle = null;
    }

    /**
     * Draws zoom rectangle manually. The drawing is performed in XOR mode,
     * therefore when this method is called twice in a row, the second call will
     * completely restore the state of the canvas.
     *
     * @param g2 the graphics device.
     * @param xor use XOR for drawing?
     */
    private void drawZoomRectangleManually(Graphics2D g2, boolean xor) {
        if (this.zoomRectangle != null) {
            if (xor) {
                // Set XOR mode to draw the zoom rectangle
                g2.setXORMode(Color.gray);
            }

            g2.setPaint(Color.BLACK);
            g2.setStroke(new BasicStroke(2));

            //g2.fill(this.zoomRectangle);
            //g2.fillRect(((int)zoomRectangle.getX()-(int)xOffset),((int)zoomRectangle.getY()-(int)yOffset),(int)zoomRectangle.getWidth(),(int)zoomRectangle.getHeight());
            g2.drawRect(((int) zoomRectangle.getX() - (int) xOffset), ((int) zoomRectangle.getY() - (int) yOffset), (int) zoomRectangle.getWidth(), (int) zoomRectangle.getHeight());

            if (xor) {
                // Reset to the default 'overwrite' mode
                g2.setPaintMode();
            }
        }
        initFlag = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

}
