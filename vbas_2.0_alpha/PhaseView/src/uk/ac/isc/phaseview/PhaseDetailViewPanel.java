package uk.ac.isc.phaseview;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import javax.swing.JPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import uk.ac.isc.seisdata.Phase;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;
import uk.ac.isc.seisdata.VBASLogger;

/**
 * PhaseDetailView is the right panel for showing the zoom-in version of the
 * phase view
 *
 */
public class PhaseDetailViewPanel extends JPanel implements SeisDataChangeListener {

    private final int imageWidth = 500;
    private final int imageHeight = 1000;

    private final PhasesList detailPList;

    //reference of travel view in order to get range information 
    private final PhaseTravelViewPanel phaseTVPanel;

    private double residualCutoffLevel = 0.0;

    private DuplicateUnorderTimeSeries detailPhaseTimeSeries = new DuplicateUnorderTimeSeries("");
    private DuplicateUnorderTimeSeriesCollection detailPhaseTimeSeriesCollection = new DuplicateUnorderTimeSeriesCollection();

    private JFreeChart freechart = null;

    private BufferedImage phasesImage = null;

    private double zoomMinTime;
    private double zoomMaxTime;
    private double zoomMinDist;
    private double zoomMaxDist;

    //curve data
    private DuplicateUnorderTimeSeriesCollection ttdData = null;

    public PhaseDetailViewPanel(PhaseTravelViewPanel phaseTVPanel) {
        this.phaseTVPanel = phaseTVPanel;
        this.detailPList = phaseTVPanel.getDetailedPList();
        this.ttdData = phaseTVPanel.getTtdData();

        detailPList.addChangeListener(this);

        updateData();
    }

    // repaint when the data changes
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
        updateData();
    }

    public void updateData() {

        detailPhaseTimeSeries = new DuplicateUnorderTimeSeries("");
        detailPhaseTimeSeriesCollection = new DuplicateUnorderTimeSeriesCollection();

        VBASLogger.logDebug("range=" + Arrays.toString(phaseTVPanel.getZoomRange()) + ", #detailPList: " + detailPList.getPhases().size());

        //put phases into the dataseries
        if (detailPList != null) {
            for (Phase p : detailPList.getPhases()) {

                if (p.getArrivalTime() != null) {
                    if (residualCutoffLevel == 0) {
                        RegularTimePeriod rp = new Millisecond(p.getArrivalTime());
                        detailPhaseTimeSeries.add(rp, p.getDistance());
                    } else {
                        if (p.getTimeResidual() != null) {
                            if (Math.abs(p.getTimeResidual()) > residualCutoffLevel) {
                                RegularTimePeriod rp = new Millisecond(p.getArrivalTime());
                                detailPhaseTimeSeries.add(rp, p.getDistance());
                            }
                        } else {
                            RegularTimePeriod rp = new Millisecond(p.getArrivalTime());
                            detailPhaseTimeSeries.add(rp, p.getDistance());
                        }
                    }
                }
            }

            detailPhaseTimeSeriesCollection.addSeries(detailPhaseTimeSeries);
            VBASLogger.logDebug("#detailPhaseTimeSeries: " + detailPhaseTimeSeries.getItemCount());
        }

        createTravelImage();
        repaint();
    }

    /**
     * helper function to use jfreechart to generate the bufferedimage
     */
    private void createTravelImage() {

        setPreferredSize(new Dimension(imageWidth, imageHeight));
        setRange(phaseTVPanel.getZoomRange());

        //define first axis
        DateAxis timeAxis = new DateAxis("");
        double gapY = 10 * (zoomMaxTime - zoomMinTime) / imageHeight;
        gapY = (gapY > 0 ? gapY : 1);
        //VBASLogger.logDebug("zoomMinTime - gapY = " + (double) (zoomMinTime - gapY) + " zoomMaxTime - gapY =" + (double) (zoomMaxTime + gapY) + " zoomMinDist=" + zoomMinDist + " zoomMaxDist=" + zoomMaxDist);
        timeAxis.setRange((double) (zoomMinTime - gapY), (double) (zoomMaxTime + gapY));
        timeAxis.setLowerMargin(0.02);  // reduce the default margins
        timeAxis.setUpperMargin(0.02);
        timeAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss.SSS"));

        NumberAxis valueAxis = new NumberAxis("");
        double gapX = 10 * (Math.min(180, zoomMaxDist) - Math.max(0, zoomMinDist)) / imageWidth;
        gapX = (gapX > 0 ? gapX : 1);
        valueAxis.setRange(Math.max(0, zoomMinDist) - gapX, Math.min(180, zoomMaxDist) + gapX);
        XYDotRenderer xyDotRend = new DetailGlyphRenderer(detailPList.getPhases());//new XYDotRenderer();

        PhasesWithCurvePlot plot = new PhasesWithCurvePlot(detailPhaseTimeSeriesCollection, timeAxis, valueAxis, xyDotRend, ttdData);
        plot.setOrientation(PlotOrientation.HORIZONTAL);

        freechart = new JFreeChart(plot);
        freechart.removeLegend();

        phasesImage = freechart.createBufferedImage(imageWidth, imageHeight);
        // Saiful: Idea
        //backgroundImage = freechart.createBufferedImage(imageWidth, imageHeight);
        //phasesImage = backgroundImage.copy
    }

    /**
     * the range won't change just filter the data
     */
    public void filterData() {
        Long minTime = detailPhaseTimeSeries.getMinX();
        Long maxTime = detailPhaseTimeSeries.getMaxX();
        double minDist = detailPhaseTimeSeries.getMinY();
        double maxDist = detailPhaseTimeSeries.getMaxY();

        detailPhaseTimeSeries = new DuplicateUnorderTimeSeries("");
        detailPhaseTimeSeriesCollection = new DuplicateUnorderTimeSeriesCollection();

        //put phases into the dataseries
        for (Phase p : detailPList.getPhases()) {

            if (p.getArrivalTime() != null) {
                if (residualCutoffLevel == 0) {
                    RegularTimePeriod rp = new Millisecond(p.getArrivalTime());
                    detailPhaseTimeSeries.add(rp, p.getDistance());
                } else {
                    if (p.getTimeResidual() != null) {
                        if (Math.abs(p.getTimeResidual()) > residualCutoffLevel) {
                            RegularTimePeriod rp = new Millisecond(p.getArrivalTime());
                            detailPhaseTimeSeries.add(rp, p.getDistance());

                        }
                    } else //show phases with null residual
                    {
                        RegularTimePeriod rp = new Millisecond(p.getArrivalTime());
                        detailPhaseTimeSeries.add(rp, p.getDistance());
                    }
                }

            }
        }

        detailPhaseTimeSeries.setMinX(minTime);
        detailPhaseTimeSeries.setMaxX(maxTime);
        detailPhaseTimeSeries.setMinY(minDist);
        detailPhaseTimeSeries.setMaxY(maxDist);
 

        detailPhaseTimeSeriesCollection.addSeries(detailPhaseTimeSeries);
        VBASLogger.logDebug("detailPhaseSeries=" + detailPhaseTimeSeries);

        createTravelImage();
        repaint();
    }

    //paint the detail view on the right side
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        int xOffset = Math.max((getWidth() - imageWidth) / 2, 0);
        int yOffset = Math.max((getHeight() - imageHeight) / 2, 0);

        g2.drawImage(phasesImage, xOffset, yOffset, this);

        /*// TEST: BufferedImage to png
         //Global.logDebug("Write BufferedImage.");
         try {
         ImageIO.write(phasesImage, "png",
         new File("/export/home/saiful/assess/temp/phaseDetailView.png"));
         } catch (Exception e) {
         Global.logSevere("Error creating a png.");
         }*/
    }

    public BufferedImage getBufferedImage() {
        return phasesImage;
    }

    public void setResidualCutoffLevel(double level) {
        this.residualCutoffLevel = level;
    }

    public double getResidualCutoffLevel() {
        return this.residualCutoffLevel;
    }

    public void setRange(double[] range) {
        zoomMinTime = range[0];
        zoomMaxTime = range[1];
        zoomMinDist = range[2];
        zoomMaxDist = range[3];
    }

}
