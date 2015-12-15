package uk.ac.isc.hypomagnitudeview;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPosition;
import org.jfree.chart.axis.CategoryTick;
import org.jfree.chart.entity.CategoryLabelEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.text.TextBlock;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;

/**
 *
 * @author hui
 */
public class MagSimpleCategoryAxis extends CategoryAxis {

    @Override
    protected AxisState drawCategoryLabels(Graphics2D g2,
            Rectangle2D plotArea,
            Rectangle2D dataArea,
            RectangleEdge edge,
            AxisState state,
            PlotRenderingInfo plotState) {

        if (state == null) {
            throw new IllegalArgumentException("Null 'state' argument.");
        }

        if (isTickLabelsVisible()) {
            List ticks = refreshTicks(g2, state, plotArea, edge);
            state.setTicks(ticks);

            int categoryIndex = 0;
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {

                CategoryTick tick = (CategoryTick) iterator.next();
                g2.setFont(getTickLabelFont(tick.getCategory()));
                g2.setPaint(getTickLabelPaint(tick.getCategory()));

                CategoryLabelPosition position
                        = getCategoryLabelPositions().getLabelPosition(edge);
                double x0 = 0.0;
                double x1 = 0.0;
                double y0 = 0.0;
                double y1 = 0.0;
                if (edge == RectangleEdge.TOP) {
                    x0 = getCategoryStart(categoryIndex, ticks.size(),
                            dataArea, edge);
                    x1 = getCategoryEnd(categoryIndex, ticks.size(), dataArea,
                            edge);
                    y1 = state.getCursor() - getCategoryLabelPositionOffset();
                    y0 = y1 - state.getMax();
                } else if (edge == RectangleEdge.BOTTOM) {
                    x0 = getCategoryStart(categoryIndex, ticks.size(),
                            dataArea, edge);
                    x1 = getCategoryEnd(categoryIndex, ticks.size(), dataArea,
                            edge);
                    y0 = state.getCursor() + getCategoryLabelPositionOffset();
                    y1 = y0 + state.getMax();
                } else if (edge == RectangleEdge.LEFT) {
                    y0 = getCategoryStart(categoryIndex, ticks.size(),
                            dataArea, edge);
                    y1 = getCategoryEnd(categoryIndex, ticks.size(), dataArea,
                            edge);
                    x1 = state.getCursor() - getCategoryLabelPositionOffset();
                    x0 = x1 - state.getMax();
                } else if (edge == RectangleEdge.RIGHT) {
                    y0 = getCategoryStart(categoryIndex, ticks.size(),
                            dataArea, edge);
                    y1 = getCategoryEnd(categoryIndex, ticks.size(), dataArea,
                            edge);
                    x0 = state.getCursor() + getCategoryLabelPositionOffset();
                    x1 = x0 - state.getMax();
                }
                Rectangle2D area = new Rectangle2D.Double(x0, y0, (x1 - x0),
                        (y1 - y0));

                Point2D anchorPoint = RectangleAnchor.coordinates(area,
                        position.getCategoryAnchor());
                TextBlock tmpblock = tick.getLabel();
                TextBlock block;

                if (tmpblock.getLastLine() != null) {
                    String tmpString = tmpblock.getLastLine().getFirstTextFragment().getText();
                    String visString = tmpString.substring(0, tmpString.indexOf("-"));
                    block = TextUtilities.createTextBlock(visString, getTickLabelFont(tick.getCategory()), getTickLabelPaint(tick.getCategory()));
                } else {
                    block = tmpblock;
                }

                //move 2 pixel on x direction
                if (edge == RectangleEdge.TOP) {
                    block.draw(g2, (float) (anchorPoint.getX() + 6),
                            (float) anchorPoint.getY(), position.getLabelAnchor(),
                            (float) (anchorPoint.getX() + 6), (float) anchorPoint.getY(),
                            position.getAngle());
                } else if (edge == RectangleEdge.BOTTOM) {
                    block.draw(g2, (float) (anchorPoint.getX() - 6),
                            (float) anchorPoint.getY(), position.getLabelAnchor(),
                            (float) (anchorPoint.getX() - 6), (float) anchorPoint.getY(),
                            position.getAngle());
                } else {
                    block.draw(g2, (float) (anchorPoint.getX()),
                            (float) anchorPoint.getY(), position.getLabelAnchor(),
                            (float) (anchorPoint.getX()), (float) anchorPoint.getY(),
                            position.getAngle());
                }

                Shape bounds = block.calculateBounds(g2,
                        (float) (anchorPoint.getX()), (float) anchorPoint.getY(),
                        position.getLabelAnchor(), (float) (anchorPoint.getX()),
                        (float) anchorPoint.getY(), position.getAngle());

                if (plotState != null && plotState.getOwner() != null) {
                    EntityCollection entities
                            = plotState.getOwner().getEntityCollection();
                    if (entities != null) {
                        String tooltip = getCategoryLabelToolTip(
                                tick.getCategory());
                        entities.add(new CategoryLabelEntity(tick.getCategory(),
                                bounds, tooltip, null));
                    }
                }
                categoryIndex++;
            }

            if (edge.equals(RectangleEdge.TOP)) {
                double h = state.getMax() + getCategoryLabelPositionOffset();
                state.cursorUp(h);
            } else if (edge.equals(RectangleEdge.BOTTOM)) {
                double h = state.getMax() + getCategoryLabelPositionOffset();
                state.cursorDown(h);
            } else if (edge == RectangleEdge.LEFT) {
                double w = state.getMax() + getCategoryLabelPositionOffset();
                state.cursorLeft(w);
            } else if (edge == RectangleEdge.RIGHT) {
                double w = state.getMax() + getCategoryLabelPositionOffset();
                state.cursorRight(w);
            }
        }
        return state;
    }

}
