
package uk.ac.isc.hypodepthview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
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
import uk.ac.isc.seisdata.ColorUtils;
import uk.ac.isc.seisdata.Hypocentre;

/**
 * custom the bar renderer
 * @author hui
 */
class DepthBarRenderer extends BarRenderer {

    private final ArrayList<Hypocentre> hypos;
    
    public static final Paint[] depthPaints = ColorUtils.createDepthViewPaintArray();
    
    public static final Paint[] depthEdgePaints = ColorUtils.createSeismicityPaintArray3();
    
    public DepthBarRenderer(ArrayList<Hypocentre> hypos) {
        this.hypos = hypos;
    }
   
    //draw each bar
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

        int visibleRow = state.getVisibleSeriesIndex(row);
        if (visibleRow < 0) {
            return;
        }
        
        // nothing is drawn for null values...
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }
        
        double stdError;
        if(hypos.get(column).getErrDepth()==null)
        {
            stdError = 0;
        }
        else
        {
            stdError = hypos.get(column).getErrDepth();
        }
        
        final double value = dataValue.doubleValue();//-stdError;
        
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
       
        //
        double transL1NegError = rangeAxis.valueToJava2D(barL0L1[1]-stdError, dataArea, edge);
        double transL1PosError = rangeAxis.valueToJava2D(barL0L1[1]+stdError, dataArea, edge);
        
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
            }
            else {
                barBase = RectangleEdge.LEFT;
            }
        }
        else {
            if (positive && !inverted || !positive && inverted) {
                barL0Adj = barLengthAdj;
                barBase = RectangleEdge.BOTTOM;
            }
            else {
                barBase = RectangleEdge.TOP;
            }
        }

        //draw adaptive background first before we draw the bars
        // the whole region is dataArea
        Paint savedPaint = g2.getPaint();
        
        //need be changed later
        Stroke savedStroke = g2.getStroke();
        
        Stroke edgeStroke = new BasicStroke(6);
        
//        final float dash1[] = {10.0f};
//        g2.setStroke(new BasicStroke(1.0f,
//                        BasicStroke.CAP_BUTT,
//                        BasicStroke.JOIN_MITER,
//                        10.0f, dash1, 0.0f));
//        g2.setPaint(new Color(128,128,128));
        
        double bgBound1, bgBound2;
        Rectangle2D adaptiveBG = null;
        
        //draw background if it is the first bar
        if (row ==0 && column ==0) {
            //now we won't do too much cause the bar chart is vertical
            if(rangeAxis.getUpperBound()>700)
            {
                bgBound1 = rangeAxis.valueToJava2D(700, dataArea, edge);
                bgBound2 = rangeAxis.valueToJava2D(rangeAxis.getUpperBound(), dataArea, edge); 
                adaptiveBG = new Rectangle2D.Double(dataArea.getMinX(),bgBound1,dataArea.getWidth(),bgBound2-bgBound1);
                
                g2.setPaint(depthPaints[7]);
                g2.fill(adaptiveBG);
                
                g2.setStroke(edgeStroke);
                g2.setPaint(depthEdgePaints[7]);
                g2.drawLine((int)dataArea.getMinX()+3, (int)bgBound1+3, (int)dataArea.getMinX()+3, (int)bgBound2-3);
                g2.drawLine((int)dataArea.getMaxX()-3, (int)bgBound1+3, (int)dataArea.getMaxX()-3, (int)bgBound2-3);
                g2.setStroke(savedStroke);
                //g2.drawLine((int)dataArea.getMinX(), (int)bgBound2, (int)dataArea.getMinX()+(int)dataArea.getWidth(), (int)bgBound2);
            }
            
            if(rangeAxis.getUpperBound()>500)
            {
                bgBound1 = rangeAxis.valueToJava2D(500, dataArea, edge);
                bgBound2 = (rangeAxis.getUpperBound()<700)?(dataArea.getHeight()+dataArea.getMinY()):rangeAxis.valueToJava2D(700, dataArea, edge);
                adaptiveBG = new Rectangle2D.Double(dataArea.getMinX(),bgBound1,dataArea.getWidth(),bgBound2-bgBound1);
                
                g2.setPaint(depthPaints[6]);
                g2.fill(adaptiveBG);
                
                g2.setStroke(edgeStroke);
                g2.setPaint(depthEdgePaints[6]);
                g2.drawLine((int)dataArea.getMinX()+3, (int)bgBound1+3, (int)dataArea.getMinX()+3, (int)bgBound2-3);
                g2.drawLine((int)dataArea.getMaxX()-3, (int)bgBound1+3, (int)dataArea.getMaxX()-3, (int)bgBound2-3);
                g2.setStroke(savedStroke);
                //g2.drawLine((int)dataArea.getMinX(), (int)bgBound2, (int)dataArea.getMinX()+(int)dataArea.getWidth(), (int)bgBound2);
            }
            
            if(rangeAxis.getUpperBound()>250)
            {
                bgBound1 = rangeAxis.valueToJava2D(250, dataArea, edge);
                bgBound2 = (rangeAxis.getUpperBound()<500)?(dataArea.getHeight()+dataArea.getMinY()):rangeAxis.valueToJava2D(500, dataArea, edge);
                adaptiveBG = new Rectangle2D.Double(dataArea.getMinX(),bgBound1,dataArea.getWidth(),bgBound2-bgBound1);
                
                g2.setPaint(depthPaints[5]);
                g2.fill(adaptiveBG);
                
                g2.setStroke(edgeStroke);
                g2.setPaint(depthEdgePaints[5]);
                g2.drawLine((int)dataArea.getMinX()+3, (int)bgBound1+3, (int)dataArea.getMinX()+3, (int)bgBound2-3);
                g2.drawLine((int)dataArea.getMaxX()-3, (int)bgBound1+3, (int)dataArea.getMaxX()-3, (int)bgBound2-3);
                g2.setStroke(savedStroke);
                //g2.drawLine((int)dataArea.getMinX(), (int)bgBound2, (int)dataArea.getMinX()+(int)dataArea.getWidth(), (int)bgBound2);
            }
            
            if(rangeAxis.getUpperBound()>160)
            {
                bgBound1 = rangeAxis.valueToJava2D(160, dataArea, edge);
                bgBound2 = (rangeAxis.getUpperBound()<250)?(dataArea.getHeight()+dataArea.getMinY()):rangeAxis.valueToJava2D(250, dataArea, edge);
                adaptiveBG = new Rectangle2D.Double(dataArea.getMinX(),bgBound1,dataArea.getWidth(),bgBound2-bgBound1);
                
                g2.setPaint(depthPaints[4]);
                g2.fill(adaptiveBG);
                
                g2.setStroke(edgeStroke);
                g2.setPaint(depthEdgePaints[4]);
                g2.drawLine((int)dataArea.getMinX()+3, (int)bgBound1+3, (int)dataArea.getMinX()+3, (int)bgBound2-3);
                g2.drawLine((int)dataArea.getMaxX()-3, (int)bgBound1+3, (int)dataArea.getMaxX()-3, (int)bgBound2-3);
                g2.setStroke(savedStroke);
                //g2.drawLine((int)dataArea.getMinX(), (int)bgBound2, (int)dataArea.getMinX()+(int)dataArea.getWidth(), (int)bgBound2);
            }
            
            if(rangeAxis.getUpperBound()>70)
            {
                bgBound1 = rangeAxis.valueToJava2D(70, dataArea, edge);
                bgBound2 = (rangeAxis.getUpperBound()<160)?(dataArea.getHeight()+dataArea.getMinY()):rangeAxis.valueToJava2D(160, dataArea, edge);
                adaptiveBG = new Rectangle2D.Double(dataArea.getMinX(),bgBound1,dataArea.getWidth(),bgBound2-bgBound1);
                
                g2.setPaint(depthPaints[3]);
                g2.fill(adaptiveBG);
                
                g2.setStroke(edgeStroke);
                g2.setPaint(depthEdgePaints[3]);
                g2.drawLine((int)dataArea.getMinX()+3, (int)bgBound1+3, (int)dataArea.getMinX()+3, (int)bgBound2-3);
                g2.drawLine((int)dataArea.getMaxX()-3, (int)bgBound1+3, (int)dataArea.getMaxX()-3, (int)bgBound2-3);
                g2.setStroke(savedStroke);
                //g2.drawLine((int)dataArea.getMinX(), (int)bgBound2, (int)dataArea.getMinX()+(int)dataArea.getWidth(), (int)bgBound2);
            }
            
            if(rangeAxis.getUpperBound()>35)
            {
                bgBound1 = rangeAxis.valueToJava2D(35, dataArea, edge);
                bgBound2 = (rangeAxis.getUpperBound()<70)?(dataArea.getHeight()+dataArea.getMinY()):rangeAxis.valueToJava2D(70, dataArea, edge);
                adaptiveBG = new Rectangle2D.Double(dataArea.getMinX(),bgBound1,dataArea.getWidth(),bgBound2-bgBound1);
                
                g2.setPaint(depthPaints[2]);
                g2.fill(adaptiveBG);
                
                g2.setStroke(edgeStroke);
                g2.setPaint(depthEdgePaints[2]);
                g2.drawLine((int)dataArea.getMinX()+3, (int)bgBound1+3, (int)dataArea.getMinX()+3, (int)bgBound2-3);
                g2.drawLine((int)dataArea.getMaxX()-3, (int)bgBound1+3, (int)dataArea.getMaxX()-3, (int)bgBound2-3);
                g2.setStroke(savedStroke);
                //g2.drawLine((int)dataArea.getMinX(), (int)bgBound2, (int)dataArea.getMinX()+(int)dataArea.getWidth(), (int)bgBound2);
            }
            
            if(rangeAxis.getUpperBound()>15)
            {
                bgBound1 = rangeAxis.valueToJava2D(15, dataArea, edge);
                bgBound2 = (rangeAxis.getUpperBound()<35)?(dataArea.getHeight()+dataArea.getMinY()):rangeAxis.valueToJava2D(35, dataArea, edge);
                adaptiveBG = new Rectangle2D.Double(dataArea.getMinX(),bgBound1,dataArea.getWidth(),bgBound2-bgBound1);
                
                g2.setPaint(depthPaints[1]);
                g2.fill(adaptiveBG);
                
                g2.setStroke(edgeStroke);
                g2.setPaint(depthEdgePaints[1]);
                g2.drawLine((int)dataArea.getMinX()+3, (int)bgBound1+3, (int)dataArea.getMinX()+3, (int)bgBound2-3);
                g2.drawLine((int)dataArea.getMaxX()-3, (int)bgBound1+3, (int)dataArea.getMaxX()-3, (int)bgBound2-3);
                g2.setStroke(savedStroke);
                //g2.drawLine((int)dataArea.getMinX(), (int)bgBound2, (int)dataArea.getMinX()+(int)dataArea.getWidth(), (int)bgBound2);
            }
            
            bgBound1 = rangeAxis.valueToJava2D(rangeAxis.getLowerBound(),dataArea,edge);
            bgBound2 = (rangeAxis.getUpperBound()<15)?(dataArea.getHeight()+dataArea.getMinY()):rangeAxis.valueToJava2D(15, dataArea, edge);
            adaptiveBG = new Rectangle2D.Double(dataArea.getMinX(),bgBound1,dataArea.getWidth(),bgBound2-bgBound1);
            g2.setPaint(depthPaints[0]);
            g2.fill(adaptiveBG);
            
            g2.setStroke(edgeStroke);
            g2.setPaint(depthEdgePaints[0]);
            g2.drawLine((int)dataArea.getMinX()+3, (int)bgBound1+3, (int)dataArea.getMinX()+3, (int)bgBound2-3);
            g2.drawLine((int)dataArea.getMaxX()-3, (int)bgBound1+3, (int)dataArea.getMaxX()-3, (int)bgBound2-3);
            g2.setStroke(savedStroke);
            //g2.drawLine((int)dataArea.getMinX(), (int)bgBound2, (int)dataArea.getMinX()+(int)dataArea.getWidth(), (int)bgBound2);
        }
        g2.setPaint(savedPaint);
        
        //g2.setStroke(savedStroke);
        
        // draw the bar...
        Rectangle2D bar = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(barL0 - barL0Adj, barW0,
                    barLength + barLengthAdj, state.getBarWidth());
        }
        else {
            bar = new Rectangle2D.Double(barW0+(state.getBarWidth()/2)-2, barL0 - barL0Adj,
                    4, barLength + barLengthAdj);          
        }
        /*if (getShadowsVisible()) {
            getBarPainter().paintBarShadow(g2, this, row, column, bar, barBase,
                true);
        }*/
        getBarPainter().paintBar(g2, this, row, column, bar, barBase);
        
        /*draw negative error and positive error*/       
        //Stroke savedStroke = g2.getStroke();
        g2.setStroke(new BasicStroke((float) 1.0));
        
        Line2D endLine = new Line2D.Double(barW0+state.getBarWidth()*0.25,(transL1NegError+transL1PosError)/2,
                barW0+state.getBarWidth()*0.75,(transL1NegError+transL1PosError)/2);
        g2.draw(endLine);
        
        if(hypos.get(column).getErrDepth()==null)
        {
            g2.setStroke(new BasicStroke((float) 3.0));
            Ellipse2D circleSymbol = new Ellipse2D.Double(barW0+(state.getBarWidth()/2)-4,barL0 - barL0Adj + barLength + barLengthAdj-4,8,8);
            g2.draw(circleSymbol);
        }
        else
        {
            Line2D lowerError = new Line2D.Double(barW0,transL1NegError,barW0+state.getBarWidth(),transL1NegError);
            Line2D upperError = new Line2D.Double(barW0,transL1PosError,barW0+state.getBarWidth(),transL1PosError);
            
            g2.draw(lowerError);
            g2.draw(upperError);
        }
        g2.setStroke(savedStroke);
        
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
