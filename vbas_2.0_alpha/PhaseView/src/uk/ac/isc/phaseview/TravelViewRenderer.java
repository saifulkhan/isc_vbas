package uk.ac.isc.phaseview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
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
import org.jfree.ui.RectangleEdge;
import uk.ac.isc.seisdata.Phase;
import uk.ac.isc.seisdata.SeisUtils;

/**
 * New dot renderer to render the glyphs on the plot, can be re-written for
 * complicated glyphs.
 */
public class TravelViewRenderer extends XYDotRenderer {

    private final String[][] phaseTypes = SeisUtils.getGroupedPhaseTypes();

    // default color scheme for Crustal, Mantle, Core and depth pahses. 
    // Crustal - cyan,  Mantle - melon, Core - cherry, depth - denim (dark blue)
    private static final Paint[] DEFAULT_PAINT = {
        new Color(0, 255, 255),
        new Color(252, 189, 181),
        new Color(222, 48, 99),
        new Color(71, 61, 140),
        new Color(196, 196, 196)};

    // private static final int DEPTH_DOT_RADIUS = 11;
    private final double dotSize = 4;
    private final ArrayList<Phase> pslist;

    public TravelViewRenderer(ArrayList<Phase> pslist) {
        this.pslist = pslist;
    }

    // draw each phase glyph
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
        double radius = dotSize;

        //double adjx = (4 - 1) / 2.0;
        //double adjy = (4 - 1) / 2.0;
        if (!Double.isNaN(y)) {
            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            double transX = domainAxis.valueToJava2D(x, dataArea,
                    xAxisLocation) - radius;//- adjx;
            double transY = rangeAxis.valueToJava2D(y, dataArea, yAxisLocation)
                    - radius; //- adjy;

            g2.setPaint(getItemPaint(series, item));
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                //g2.fillOval((int) transY, (int) transX, (int)(radius*2),
                //        (int) (radius*2));
                drawGlyph(g2, (int) transY, (int) transX, (int) radius, x, y);

            } else if (orientation == PlotOrientation.VERTICAL) {
                g2.fillOval((int) transX, (int) transY, (int) (radius * 2), (int) (radius * 2));
            }

            int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
            int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
            updateCrosshairValues(crosshairState, x, y, domainAxisIndex,
                    rangeAxisIndex, transX, transY, orientation);
        }

    }

    // item is the idx fro retrieving triplet
    private void drawGlyph(Graphics2D  g2, int transHor, int transVer, int radius, double x, double y) {
        //get all the atttributes
        //PhaseTriplet pt = ptlist.get(item);

        String reportPhaseType = null;
        String iscPhaseType = null;
        //String stationName = null;
        Boolean defining = null;
        //Double residual = null;

        for (Phase p : pslist) {
            //double aa = (double)p.getArrivalTime().getTime();
            //double bb = p.getDistance();
            if ((p.getArrivalTime() != null)
                    && (Math.abs((double) p.getArrivalTime().getTime() - x)) < 0.0001
                    && Math.abs((p.getDistance() - y)) < 0.0001) {

                reportPhaseType = p.getOrigPhaseType();
                iscPhaseType = p.getIscPhaseType();
                //stationName = p.getReportStation();
                //residual = p.getTimeResidual();
                defining = p.getDefining();
                break;
            }
        }

        Paint savedPaint = g2.getPaint();
        Stroke savedStroke = g2.getStroke();
        //Stroke solidStroke = new BasicStroke(1);
        Stroke hollowStroke = new BasicStroke(2);

        float dash1[] = {1.0f};
        BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

        if (iscPhaseType == null || iscPhaseType.equals("x")) {
            g2.setPaint(Color.BLACK);
        } else {
            g2.setPaint(DEFAULT_PAINT[4]);

            // get the paint color
            for (int i = 0; i < phaseTypes.length; i++) {
                String[] sub = phaseTypes[i];
                for (int j = 0; j < sub.length; j++) {
                    if (iscPhaseType.equals(sub[j])) {
                        g2.setPaint(DEFAULT_PAINT[i]);
                    }
                }
            }
        }

        //g2.fillOval(transHor, transVer, radius*2, radius*2);
        //new code to set different shapes and colors for different cases
        g2.setStroke(hollowStroke);
        if (reportPhaseType == null || iscPhaseType == null) {  // draw plus
            //g2.setPaint(Color.red); g2.setStroke(dashed);
            g2.drawLine(transHor - 2, transVer, transHor + 2, transVer);
            g2.drawLine(transHor, transVer - 2, transHor, transVer + 2);
        } else {
            if (reportPhaseType.equals("x") || iscPhaseType.equals("x")) { // draw cross
                //g2.setStroke(hollowStroke);
                g2.drawLine(transHor - 2, transVer - 2, transHor + 2, transVer + 2);
                g2.drawLine(transHor - 2, transVer + 2, transHor + 2, transVer - 2);
            } else if (reportPhaseType.equals("LR")) {
                g2.drawRect(transHor - 2, transVer - 2, 5, 5);
            } else if (reportPhaseType.equals(iscPhaseType) && defining) { // same and defining // solid circle 
                //g2.setPaint(Color.red); g2.setStroke(dashed);
                g2.fillOval(transHor, transVer, radius * 2, radius * 2);
            } else if (reportPhaseType.equals(iscPhaseType) && !defining) { // hollow circle
                //g2.setStroke(hollowStroke);               
                g2.drawOval(transHor, transVer, radius * 2, radius * 2);
            } else if (!reportPhaseType.equals(iscPhaseType) && defining) { // solid triangle
                //g2.setPaint(Color.red); g2.setStroke(dashed);
                int[] trix = {transHor, transHor - 4, transHor + 4};
                int[] triy = {transVer - 4, transVer + 4, transVer + 4};
                Polygon p = new Polygon(trix, triy, 3);
                g2.fillPolygon(p);
            } else if (!reportPhaseType.equals(iscPhaseType) && !defining) { // hollow triangle
                //g2.setPaint(Color.red); g2.setStroke(dashed);
                //int[] trix = {transHor, transHor - 4, transHor + 4};
                //int[] triy = {transVer - 4, transVer + 4, transVer + 4};
                int[] trix = {transHor + 4, transHor, transHor + 8};
                int[] triy = {transVer, transVer + 8, transVer + 8};
                Polygon p = new Polygon(trix, triy, 3);
                g2.drawPolygon(p);
            }
        }

        g2.setPaint(savedPaint);
        g2.setStroke(savedStroke);
    }

}
