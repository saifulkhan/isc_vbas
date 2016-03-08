package uk.ac.isc.hypomagnitudeview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;

/**
 * customed bar renderer
 *
 * @author hui
 */
public class MagBarRenderer extends BarRenderer {

    private String label;

    MagBarRenderer(String label) {
        this.label = label;
    }

    //overide function for drawing each bar
    @Override
    public void drawItem(Graphics2D g2,
            CategoryItemRendererState state,
            Rectangle2D dataArea,
            CategoryPlot plot,
            CategoryAxis domainAxis,
            ValueAxis rangeAxis,
            CategoryDataset dataset,
            int row,
            int column,
            int pass) {

        // nothing is drawn if the row index is not included in the list with
        // the indices of the visible rows...
        int visibleRow = state.getVisibleSeriesIndex(row);
        if (visibleRow < 0) {
            return;
        }
        // nothing is drawn for null values...
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }

        final double value = dataValue.doubleValue();
        PlotOrientation orientation = plot.getOrientation();
        double barW0 = calculateBarW0(plot, orientation, dataArea, domainAxis,
                state, visibleRow, column);
        double[] barL0L1 = calculateBarL0L1(value);
        if (barL0L1 == null) {
            return;  // the bar is not visible
        }

        RectangleEdge edge = plot.getRangeAxisEdge();
        double transL0 = rangeAxis.valueToJava2D(barL0L1[0], dataArea, edge);
        double transL1 = rangeAxis.valueToJava2D(barL0L1[1], dataArea, edge);

        //label region
        //double upperBound = rangeAxis.valueToJava2D(10.0,dataArea,edge);
        //double lowerBound = rangeAxis.valueToJava2D(9.0,dataArea,edge);
        //float labelX =(float)(dataArea.getX() + dataArea.getWidth()/2);
        //float labelY = (float) ((upperBound+lowerBound)/2);
        //only draw the label once
        Paint savedTextPaint = g2.getPaint();
        //draw background strips
        if (column == 0) {
            g2.setPaint(new Color(240, 240, 240));
            for (int i = 0; i < 10; i = i + 2) {
                g2.fillRect((int) rangeAxis.valueToJava2D(i, dataArea, edge), (int) dataArea.getY(), (int) (rangeAxis.valueToJava2D(i + 1, dataArea, edge) - rangeAxis.valueToJava2D(i, dataArea, edge)), (int) dataArea.getHeight());
            }
            //FontMetrics fm = g2.getFontMetrics();
            //Rectangle2D labelBounds = TextUtilities.getTextBounds(label, g2, fm);
            //g2.setPaint(Color.BLACK);
            //TextUtilities.drawRotatedString(label, g2, labelX, labelY, TextAnchor.CENTER, 0, TextAnchor.CENTER);
            //g2.setPaint(savedTextPaint);
        }
        // in the following code, barL0 is (in Java2D coordinates) the LEFT
        // end of the bar for a horizontal bar chart, and the TOP end of the
        // bar for a vertical bar chart.  Whether this is the BASE of the bar
        // or not depends also on (a) whether the data value is 'negative'
        // relative to the base value and (b) whether or not the range axis is
        // inverted.  This only matters if/when we apply the minimumBarLength
        // attribute, because we should extend the non-base end of the bar
        boolean positive = (value >= getBase());
        boolean inverted = rangeAxis.isInverted();
        double barL0 = Math.min(transL0, transL1);
        double barLength = Math.abs(transL1 - transL0);
        double barLengthAdj = 0.0;
        if (barLength > 0.0 && barLength < getMinimumBarLength()) {
            barLengthAdj = getMinimumBarLength() - barLength;
        }
        double barL0Adj = 0.0;
        RectangleEdge barBase;
        if (orientation == PlotOrientation.HORIZONTAL) {
            if (positive && inverted || !positive && !inverted) {
                barL0Adj = barLengthAdj;
                barBase = RectangleEdge.RIGHT;
            } else {
                barBase = RectangleEdge.LEFT;
            }
        } else {
            if (positive && !inverted || !positive && inverted) {
                barL0Adj = barLengthAdj;
                barBase = RectangleEdge.BOTTOM;
            } else {
                barBase = RectangleEdge.TOP;
            }
        }

        // draw the bar...
        Rectangle2D bar = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(barL0 - barL0Adj, barW0,
                    barLength + barLengthAdj, state.getBarWidth());
        } else {
            bar = new Rectangle2D.Double(barW0, barL0 - barL0Adj,
                    state.getBarWidth(), barLength + barLengthAdj);
        }
        //if (getShadowsVisible()) {
        //    getBarPainter().paintBarShadow(g2, this, row, column, bar, barBase,
        //        true);
        //}

        if ("mb".equals(label)) {
            //dark blue
            g2.setPaint(new Color(0, 153, 255));
        } else if ("MS".equals(label)) {
            //scalet
            g2.setPaint(new Color(197, 100, 100));
        } else if ("MW".equals(label)) {
            //wine
            g2.setPaint(new Color(148, 0, 107));
        } else if ("local".equals(label)) {
            //lavender
            g2.setPaint(new Color(40, 150, 40));
        } else {
            g2.setPaint(new Color(227, 127, 28));
        }

        g2.fill(bar);
        //getBarPainter().paintBar(g2, this, row, column, bar, barBase);

        g2.setPaint(savedTextPaint);

        CategoryItemLabelGenerator generator = getItemLabelGenerator(row,
                column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, dataset, row, column, plot, generator, bar,
                    (value < 0.0));
        }

        // submit the current data point as a crosshair candidate
        int datasetIndex = plot.indexOf(dataset);
        updateCrosshairValues(state.getCrosshairState(),
                dataset.getRowKey(row), dataset.getColumnKey(column), value,
                datasetIndex, barW0, barL0, orientation);

        // add an item entity, if this information is being collected
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addItemEntity(entities, dataset, row, column, bar);
        }

    }

}
