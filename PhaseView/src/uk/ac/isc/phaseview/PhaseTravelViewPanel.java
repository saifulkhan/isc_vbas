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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.ui.RectangleInsets;
import uk.ac.isc.seisdatainterface.Global;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.Phase;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdata.SeisUtils;
import uk.ac.isc.seisdata.VBASLogger;

/**
 *
 * The general view to show all the phases with travel time curves
 */
public class PhaseTravelViewPanel extends JPanel implements MouseListener, MouseMotionListener {

    //for filtering the data
    private double residualCutoffLevel = 0.0;
    //switch between the roi box (coordinates are bit different), this makes the double boxes and needs been fixed
    //private boolean initFlag = true;

    //freechart object for drawing the image
    private JFreeChart freechart = null;

    private final int imageWidth = 500;
    private final int imageHeight = 1000;
    // Gap between the container panel and the actual graph view.
    private int xOffset = 0;
    private int yOffset = 0;

    // Axis draw once
    private DateAxis timeAxis = null;
    private NumberAxis valueAxis = null;
    private XYDotRenderer renderer = null;

    //buffer images of the view
    private BufferedImage phasesImage = null;
    private BufferedImage phaseImageWithRect = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

    //for filtering the data with phase types
    private final boolean[] typesVisible = {true, true, true, true, true};
    private final String[][] phaseTypes = SeisUtils.getGroupedPhaseTypes();

    //get ranges, the view should be fixed
    private Long minTime;
    private Long maxTime;
    private double maxDist;

    /**
     * The drawing info collected the last time the chart was drawn.
     */
    private ChartRenderingInfo info;

    // The zoom rectangle starting point (selected by the user with a mouse click). 
    // This is a point on the screen, not the chart (which may have been scaled up or down to fit the panel).
    private Point2D zoomPoint = null;
    // The zoom rectangle (selected by the user with the mouse).
    private Rectangle2D zoomRectangle = null;
    private boolean zoomRect = false;
    private double zoomMinTime, zoomMaxTime, zoomMinDist, zoomMaxDist;
    private double zoomRextX, zoomRextY, zoomRextW, zoomRextH;
    Rectangle2D zoomRectDim;

    private final PhasesList pList;
    private final HypocentresList hList;
    private Hypocentre prime = null;
    // seleted list of Phases for details view
    private final PhasesList detailedPList = new PhasesList();
    // selected phases in the Phase Selection table
    private final PhasesList selectedPhaseList = new PhasesList();

    //these two for showing the phases data
    private File ttimesScript;
    private DuplicateUnorderTimeSeries phaseTimeSeries;
    private DuplicateUnorderTimeSeriesCollection phaseTimeSeriesCollection;
    //curve data
    private DuplicateUnorderTimeSeriesCollection ttdData = null;

    public PhaseTravelViewPanel(PhasesList pList, HypocentresList hList) {
        this.pList = pList;
        this.hList = hList;

        // Read the perl script, a resourse file inside jar.
        if (getClass().getClassLoader().getResource("resources" + File.separator + "ttimes.pl") == null) {
            VBASLogger.logDebug("Resource does not exist. resource: "
                    + getClass().getClassLoader().getResource("resources" + File.separator + "ttimes.pl"));
        }

        InputStream inSream = getClass().getClassLoader().getResourceAsStream("resources" + File.separator + "ttimes.pl");
        if (inSream != null) {
            // Create a temp directory in the current directory and copy the content of the perl script from resource folder.
            try {
                Path scriptpath = Paths.get(System.getProperty("user.dir") + File.separator + "temp");
                if (!new File(scriptpath.toString()).exists()) {
                    boolean success = (new File(scriptpath.toString())).mkdirs();
                    if (!success) {
                        String message = "Error creating the directory " + scriptpath;
                        VBASLogger.logSevere(message);
                        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                //ttimesScript = File.createTempFile("ttimes", ".pl"); 
                ttimesScript = new File(scriptpath + File.separator + "ttimes.pl");
                ttimesScript.setReadable(true, true);
                ttimesScript.setWritable(true, true);
                ttimesScript.setExecutable(true, true);
                
                VBASLogger.logDebug("Perl script location for TTDData, ttimesScript= " + ttimesScript.toPath());
                if (!ttimesScript.canRead() && !ttimesScript.canExecute()) {
                    String message = ttimesScript.toPath() + " is not readable and writable. Please report to system admin.";
                    VBASLogger.logDebug(message);
                    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
                    //logger.log(Level.SEVERE, message);
                }

                Files.copy(inSream, ttimesScript.toPath(), StandardCopyOption.REPLACE_EXISTING);
                inSream.close();
            } catch (IOException e) {
                // TODO
            }
        } else {
            VBASLogger.logDebug("Null 'inStream', resource: "
                    + getClass().getClassLoader().getResource("resources" + File.separator + "ttimes.pl").toString());
        }

        updateData();
    }

    public DuplicateUnorderTimeSeriesCollection getTtdData() {
        return ttdData;
    }

    public void setPhaseTypeVisible(int type, boolean vis) {
        typesVisible[type] = vis;
    }

    private void resetZoomRange() {
        zoomMinTime = 0;
        zoomMaxTime = 0;
        zoomMinDist = 0;
        zoomMaxDist = 0;
    }

    public double[] getZoomRange() {
        double[] range = new double[4];
        range[0] = zoomMinTime;
        range[1] = zoomMaxTime;
        range[2] = zoomMinDist;
        range[3] = zoomMaxDist;
        return range;
    }

    // Process the data : once the data changes, call this function
    public void updateData() {
        if (detailedPList.getPhases() != null) {
            detailedPList.getPhases().clear();
        }
        phaseTimeSeries = new DuplicateUnorderTimeSeries("");
        phaseTimeSeriesCollection = new DuplicateUnorderTimeSeriesCollection();
        resetZoomRange();
        zoomRectangle = null;
        prime = null;

        // get the prime hypocentre from the new hypocentres list
        for (int i = 0; i < hList.getHypocentres().size(); i++) {
            if (hList.getHypocentres().get(i).getIsPrime()) {
                prime = hList.getHypocentres().get(i);
            }
        }

        // get travel time curve data
        ttdData = LoadTTDData.loadTTDData(Global.getSelectedSeisEvent().getEvid(), ttimesScript);

        //put phases into the dataseries
        for (Phase p : pList.getPhases()) {
            if ((p.getArrivalTime() != null)
                    && ((p.getTimeResidual() != null && Math.abs(p.getTimeResidual()) > residualCutoffLevel)
                    || (p.getTimeResidual() == null))) {

                RegularTimePeriod rp = new Millisecond(p.getArrivalTime());
                phaseTimeSeries.add(rp, p.getDistance());
            }
        }

        phaseTimeSeriesCollection.addSeries(phaseTimeSeries);

        /*if empty then create a blank plot*/
        if (prime == null && phaseTimeSeries.isEmpty()) {
            VBASLogger.logDebug("prime=" + prime + ", phaseTimeSeries.isEmpty()=" + phaseTimeSeries.isEmpty());
            minTime = new Long(1);
            maxTime = new Long(1);
            maxDist = 1.0;
        } else {
            VBASLogger.logDebug("prime=" + prime + ", phaseTimeSeries.isEmpty()=" + phaseTimeSeries.isEmpty());
            VBASLogger.logDebug("prime.getOrigTime()=" + String.valueOf(prime.getOrigTime())
                    + "phaseTimeSeries.getMinX()=" + String.valueOf(phaseTimeSeries.getMinX()));

            minTime = Math.min(prime.getOrigTime().getTime(), phaseTimeSeries.getMinX());
            maxTime = phaseTimeSeries.getMaxX();
            maxDist = phaseTimeSeries.getMaxY();
            if (maxDist <= 0) {
                maxDist = 1.0; // TODO: fix the script
                JOptionPane.showMessageDialog(null, "Possible corrupted traveltime data (script).",
                        "WARNING", JOptionPane.ERROR_MESSAGE);
            }

            VBASLogger.logDebug("minTime=" + minTime + ", maxTime=" + maxTime + ", maxDist=" + maxDist);
        }

        //VBASLogger.logDebug("#phaseTimeSeries:" + phaseTimeSeries.getItemCount() + " minTime=" + minTime + " maxTime=" + maxTime + " maxDist=" + maxDist);
        setupAxis();
        drawPlots();

        addMouseListener(this);
        repaint();
    }

    /**
     * Function for jfreechart to generate the bufferedimage
     */
    private void setupAxis() {

        setPreferredSize(new Dimension(imageWidth, imageHeight));

        timeAxis = new DateAxis("");
        double gapY = 10 * (maxTime - minTime) / imageHeight; // Issue# 44
        gapY = (gapY > 0 ? gapY : 1);
        timeAxis.setRange((double) (minTime - gapY), (double) (maxTime + gapY));
        timeAxis.setLowerMargin(0.02);  // reduce the default margins
        timeAxis.setUpperMargin(0.02);
        timeAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss.S"));

        valueAxis = new NumberAxis("");
        double gapX = 10 * Math.min(180, maxDist) / imageWidth; // issue# 44
        //VBASLogger.logDebug("gapX=" + gapX + ", (Math.min(180, maxDist) + gapX)="  +(Math.min(180, maxDist) + gapX));
        valueAxis.setRange(gapX, (Math.min(180, maxDist) + gapX));

        renderer = new TravelViewRenderer(pList.getPhases());
    }

    private void drawPlots() {

        PhasesWithCurvePlot plot = new PhasesWithCurvePlot(phaseTimeSeriesCollection, timeAxis, valueAxis, renderer, ttdData);
        plot.setOrientation(PlotOrientation.HORIZONTAL);

        freechart = new JFreeChart(plot);
        freechart.removeLegend();

        phasesImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Rectangle2D bufferArea = new Rectangle2D.Double(0, 0, imageWidth, imageHeight);

        this.info = new ChartRenderingInfo();
        Graphics2D g2 = phasesImage.createGraphics();

        freechart.draw(g2, bufferArea, info);

        Graphics2D gCopy = (Graphics2D) phaseImageWithRect.getGraphics();
        gCopy.drawImage(phasesImage, 0, 0, null);
    }

    /**
     * the range won't change just filter the data
     */
    public void filterData() {

        phaseTimeSeriesCollection.removeAllSeries();
        phaseTimeSeries.clear();
        phaseTimeSeries = new DuplicateUnorderTimeSeries("");
        phaseTimeSeriesCollection = new DuplicateUnorderTimeSeriesCollection();

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
                    RegularTimePeriod rp = new Millisecond(p.getArrivalTime());
                    phaseTimeSeries.add(rp, p.getDistance());
                } else {
                    if (p.getTimeResidual() != null) {
                        if (Math.abs(p.getTimeResidual()) > residualCutoffLevel) {
                            RegularTimePeriod rp = new Millisecond(p.getArrivalTime());
                            phaseTimeSeries.add(rp, p.getDistance());

                        }
                    }
                }
            }
        }

        phaseTimeSeriesCollection.addSeries(phaseTimeSeries);
        drawPlots();

        // draw the zoom rect if it was there
        Graphics2D g2 = (Graphics2D) phaseImageWithRect.getGraphics();
        drawZoomRectangle(g2, false);
        repaint();
        g2.dispose();
    }

    /**
     * helper function to refresh the detailedPList buffer for the detail view
     * based on the selection range
     */
    public void updateDetailPList() {

        if (this.zoomRectangle == null) {
            return;
        }

        double yMin, yMax, xMin, xMax;

        /*if (zoomRect == true) {
         yMin = zoomRectangle.getY();
         yMax = yMin + zoomRectangle.getHeight();
         xMin = zoomRectangle.getX();
         xMax = xMin + zoomRectangle.getWidth();
         } else {*/
        yMin = zoomRectangle.getY() - yOffset;
        yMax = yMin + zoomRectangle.getHeight();
        xMin = zoomRectangle.getX() - xOffset;
        xMax = xMin + zoomRectangle.getWidth();
        //}

        //use zoomRectangle to get the min-max Time and min-max distance
        zoomMinTime = freechart.getXYPlot().getDomainAxis().java2DToValue(yMax, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getDomainAxisEdge());
        zoomMaxTime = freechart.getXYPlot().getDomainAxis().java2DToValue(yMin, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getDomainAxisEdge());
        zoomMinDist = freechart.getXYPlot().getRangeAxis().java2DToValue(xMin, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getRangeAxisEdge());
        zoomMaxDist = freechart.getXYPlot().getRangeAxis().java2DToValue(xMax, info.getPlotInfo().getDataArea(), freechart.getXYPlot().getRangeAxisEdge());

        // Debug
        Date minTimeTemp = new Date();
        minTimeTemp.setTime((long) zoomMinTime);
        Date maxTimeTemp = new Date();
        maxTimeTemp.setTime((long) zoomMaxTime);
        VBASLogger.logDebug("xOffset=" + xOffset + "\nTime (min-max): " + minTimeTemp.toString() + "-" + maxTimeTemp.toString()
                + ", Dist (min-max): " + zoomMinDist + "-" + zoomMaxDist);

        // Start finding data in the range
        if (detailedPList.getPhases() != null) {
            detailedPList.getPhases().clear();
        }

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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        //VBASLogger.logDebug("getWidth()=" + getWidth() + ", imageWidth=" + imageWidth + ", getHeight()=" + getHeight() + ", imageHeight=" + imageHeight);
        xOffset = Math.max((getWidth() - imageWidth) / 2, 0);
        yOffset = Math.max((getHeight() - imageHeight) / 2, 0);

        g2.drawImage(phaseImageWithRect, xOffset, yOffset, this);

        /*// TEST: 
         //VBASLogger.logDebug("Write BufferedImage.");
         try {
         ImageIO.write(phaseImageWithRect, "png",
         new File("/export/home/saiful/assess/temp/phaseImageWithRect.png"));
         } catch (Exception e) {
         Global.logSevere("Error creating a png.");
         }*/
    }

    public BufferedImage getBufferedImage() {
        return phaseImageWithRect;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    /*
     * ***********************************************************************
     * Mouse interaction, e.g., draw zoom rect.
     *************************************************************************
     */
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
        VBASLogger.logDebug("Zoom: mouseDragged");

        /*
         // TODO: Saiful: commented as I'm not sure if this is needed!
        
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
         g2.dispose();*/
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        VBASLogger.logDebug("Zoom: mouseReleased");

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
            this.zoomRectangle = new Rectangle2D.Double(this.zoomPoint.getX(),
                    this.zoomPoint.getY(),
                    xmax - this.zoomPoint.getX(),
                    ymax - this.zoomPoint.getY());
        } else {
            return;
        }

        Graphics2D g2 = (Graphics2D) phaseImageWithRect.getGraphics();
        g2.drawImage(phasesImage, 0, 0, null);
        drawZoomRectangle(g2, false);
        repaint();
        g2.dispose();
        updateDetailPList();
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

        VBASLogger.logDebug("dataArea.getX()=" + dataArea.getX()
                + ", insets.left=" + insets.left
                + ", xOffset=" + xOffset
                + ", dataArea.getY()=" + dataArea.getY()
                + ", insets.top=" + insets.top
                + ", yOffset=" + yOffset
                + ", w=" + w
                + ", h=" + h);

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
     * Draws zoom rectangle manually. The drawing is performed in XOR mode,
     * therefore when this method is called twice in a row, the second call will
     * completely restore the state of the canvas.
     *
     * @param g2 the graphics device.
     * @param xor use XOR for drawing?
     */
    /*private void drawZoomRectangleManually(Graphics2D g2, boolean xor) {
     if (this.zoomRectangle != null) {
     if (xor) {
     // Set XOR mode to draw the zoom rectangle
     g2.setXORMode(Color.gray);
     }

     g2.setPaint(Color.BLACK);
     g2.setStroke(new BasicStroke(2));

     g2.drawRect(((int) zoomRectangle.getX() - (int) xOffset), 
     ((int) zoomRectangle.getY() - (int) yOffset), 
     (int) zoomRectangle.getWidth(), 
     (int) zoomRectangle.getHeight());

     if (xor) {
     // Reset to the default 'overwrite' mode
     g2.setPaintMode();
     }
     }
     zoomRect = true;
     }*/
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

            VBASLogger.logDebug("zoomRectangle = ["
                    + (int) zoomRectangle.getX() + ", "
                    + (int) zoomRectangle.getY() + ", "
                    + (int) zoomRectangle.getWidth() + ", "
                    + (int) zoomRectangle.getHeight() + "]");

            if (xor) {
                // Set XOR mode to draw the zoom rectangle
                g2.setXORMode(Color.gray);
            }

            g2.setPaint(Color.BLACK);

            g2.setStroke(new BasicStroke(2));

            /*g2.drawRect((int) zoomRectangle.getX(), 
             (int) zoomRectangle.getY(), 
             (int) zoomRectangle.getWidth(), 
             (int) zoomRectangle.getHeight());*/
            g2.drawRect(((int) zoomRectangle.getX() - (int) xOffset),
                    ((int) zoomRectangle.getY() - (int) yOffset),
                    (int) zoomRectangle.getWidth(),
                    (int) zoomRectangle.getHeight());

            if (xor) {
                // Reset to the default 'overwrite' mode
                g2.setPaintMode();
            }
        }
        //initFlag = true;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

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
