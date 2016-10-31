package uk.ac.isc.phaseview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import uk.ac.isc.seisdata.Phase;

/**
 * This is for rendering the glyph of phase on the travel curves trying to make
 * difference between 2K and one K as well as ab, bc and df change all the K
 * type representation to straight lines
 *
 *  
 */
public class DetailGlyphRenderer extends XYDotRenderer {

    //default color scheme for P, S and K.   
    private static final Paint DEFAULT_PPHASE_PAINT = new Color(100, 0, 255);

    private static final Paint DEFAULT_SPHASE_PAINT = new Color(210, 80, 80);

    private static final Paint DEFAULT_KPHASE_PAINT = new Color(110, 220, 0);

    private static final int DEPTH_DOT_RADIUS = 11;

    private static final Stroke DEFAULT_STROKE = new BasicStroke(3);

    //default circle size
    private double radius = 2;

    //phase data
    private final ArrayList<Phase> pslist;

    public DetailGlyphRenderer(ArrayList<Phase> pslist) {
        this.pslist = pslist;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    //override function to draw each node (glyph)
    @Override
    public void drawItem(Graphics2D g2,
            XYItemRendererState state,
            Rectangle2D dataArea,
            PlotRenderingInfo info,
            XYPlot plot,
            ValueAxis domainAxis,
            ValueAxis rangeAxis,
            XYDataset dataset,
            int series,
            int item,
            CrosshairState crosshairState,
            int pass) {

        // do nothing if item is not visible
        if (!getItemVisible(series, item)) {
            return;
        }

        // get the data point...
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        //double radius = 24;

        //double adjx = (4 - 1) / 2.0;
        //double adjy = (4 - 1) / 2.0;
        if (!Double.isNaN(y)) {
            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            double transX = domainAxis.valueToJava2D(x, dataArea,
                    xAxisLocation);// - radius;//- adjx;
            double transY = rangeAxis.valueToJava2D(y, dataArea, yAxisLocation);
            //- radius; //- adjy;

            g2.setPaint(getItemPaint(series, item));
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                //g2.fillOval((int) transY, (int) transX, (int)(radius*2),
                //        (int) (radius*2));
                drawGlyph(g2, (int) transY, (int) transX, (int) radius, x, y);

            } else if (orientation == PlotOrientation.VERTICAL) {
                g2.fillOval((int) transX, (int) transY, (int) (radius * 2),
                        (int) (radius * 2));
            }

            int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
            int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
            updateCrosshairValues(crosshairState, x, y, domainAxisIndex,
                    rangeAxisIndex, transX, transY, orientation);
        }

    }

    //item is the idx fro retrieving triplet
    private void drawGlyph(Graphics2D g2, int transHor, int transVer, int radius, double x, double y) {
        //get all the atttributes
        //PhaseTriplet pt = ptlist.get(item);

        String reportPhaseType = null;
        String iscPhaseType = null;
        //String stationName = null;
        Double residual = null;
        boolean defining = true;

        for (Phase p : pslist) {
            //double aa = (double)p.getArrivalTime().getTime();
            //double bb = p.getDistance();
            if ((p.getArrivalTime() != null) && (Math.abs((double) p.getArrivalTime().getTime() - x)) < 0.0001 && Math.abs((p.getDistance() - y)) < 0.0001) {
                iscPhaseType = p.getIscPhaseType();
                reportPhaseType = p.getOrigPhaseType();
                residual = p.getTimeResidual();
                defining = p.getDefining();
                break;
            }

        }

        Paint savedPaint = g2.getPaint();
        Stroke savedStroke = g2.getStroke();

        //draw node contour
        g2.setPaint(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.fillOval(transHor - radius, transVer - radius, radius * 2 + 1, radius * 2 + 1);

        if (residual == null || residual < 0) //draw a line to the bottom
        {
            g2.drawLine(transHor, transVer, transHor + 8, transVer + 8);
            g2.drawRect(transHor + 8, transVer + 8, 30, 24);

            TextUtilities.drawRotatedString(iscPhaseType, g2, transHor + 23, transVer + 14, TextAnchor.CENTER, 0, TextAnchor.CENTER);
            TextUtilities.drawRotatedString(reportPhaseType, g2, transHor + 23, transVer + 26, TextAnchor.CENTER, 0, TextAnchor.CENTER);
        } else {
            g2.drawLine(transHor, transVer, transHor - 8, transVer - 8);
            g2.drawRect(transHor - 38, transVer - 32, 30, 24);

            TextUtilities.drawRotatedString(iscPhaseType, g2, transHor - 23, transVer - 26, TextAnchor.CENTER, 0, TextAnchor.CENTER);
            TextUtilities.drawRotatedString(reportPhaseType, g2, transHor - 23, transVer - 14, TextAnchor.CENTER, 0, TextAnchor.CENTER);
        }

        //set back to normal paint and stroke
        g2.setPaint(savedPaint);
        g2.setStroke(savedStroke);
    }
}
