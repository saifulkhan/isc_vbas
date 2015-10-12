
package uk.ac.isc.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.JPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedRangeCategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import uk.ac.isc.data.Analyst;
import uk.ac.isc.data.TaskBlock;

/**
 * This panel is to show the blocks in a barchart
 * @author hui
 */
public class AnalystVisPanel extends JPanel {
    
    /*data to show on this panel*/
    private final ArrayList<Analyst> analystList;
    
    private final ArrayList<TaskBlock> bList;
    
    /*data related to the plot*/
    CombinedRangeCategoryPlot cPlotMain = new CombinedRangeCategoryPlot(new NumberAxis());
     
    /*jfreechart*/
    private JFreeChart freeChartMain = null;
    
    /** The chart anchor point. */
    private Point2D anchor;

    /** The drawing info collected the last time the chart was drawn. */
    private final ChartRenderingInfo info;
    
    private final Color pColor = new Color(135,206,235);
    
    private final Color sColor = new Color(240,128,128);
    
    private final Color fColor = new Color(34,139,34);
            
    public AnalystVisPanel(ArrayList<Analyst> aList, ArrayList<TaskBlock> bList)
    {
        this.analystList = aList;
        this.bList = bList;
                    
        for(int i = 0; i<analystList.size(); i++)
        {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            String rowKey1, rowKey2;
            String columnKey;
            for(int j = 0; j<bList.size(); j++)
            {
                /*?redundancy or some more changes?*/
                if(analystList.get(i).getName().equals(bList.get(j).getAnalyst1()))
                {
                    //rowKey = bList.get(j).getAnalyst1();
                    //columnKey = bList.get(j).getBlockID().toString()+"P";
                    //dataset.addValue(bList.get(j).getEventNumber(), rowKey, columnKey);
                    rowKey1 = "reviewed";
                    rowKey2 = "unreviewed";
                    columnKey = bList.get(j).getBlockID().toString()+"P";
                    /*the analyst review P but the status is not P, it means the P has been finished*/
                    if(!"P".equals(bList.get(j).getStatus()))
                    {
                        dataset.addValue(bList.get(j).getEventNumber(), rowKey1, columnKey);
                                            
                        dataset.addValue(0, rowKey2, columnKey);
                    }
                    else
                    {
                        dataset.addValue(bList.get(j).getReviewedEventNumber(), rowKey1, columnKey);
                        
                        dataset.addValue(bList.get(j).getEventNumber()-bList.get(j).getReviewedEventNumber(), rowKey2, columnKey);
                    }
                    
                }
                else if(analystList.get(i).getName().equals(bList.get(j).getAnalyst2()))
                {
                    //rowKey = bList.get(j).getAnalyst2();
                    //columnKey = bList.get(j).getBlockID().toString() + "S";
                    //dataset.addValue(bList.get(j).getEventNumber(), rowKey, columnKey);
                    rowKey1 = "reviewed";
                    rowKey2 = "unreviewed";
                    columnKey = bList.get(j).getBlockID().toString()+"S";
                    
                    /*the status is P so S should be set to unreviewed*/
                    if("P".equals(bList.get(j).getStatus()))
                    {
                        dataset.addValue(0, rowKey1, columnKey);

                        dataset.addValue(bList.get(j).getEventNumber(), rowKey2, columnKey);
                    }
                    else if("S".equals(bList.get(j).getStatus()))
                    {
                        dataset.addValue(bList.get(j).getReviewedEventNumber(), rowKey1, columnKey);

                        dataset.addValue(bList.get(j).getEventNumber()-bList.get(j).getReviewedEventNumber(), rowKey2, columnKey);
                    }
                    else //status at F or Done, S is all reviewed
                    {
                        dataset.addValue(bList.get(j).getEventNumber(), rowKey1, columnKey);
                        
                        dataset.addValue(0, rowKey2, columnKey);
                    }
                }
                else if(analystList.get(i).getName().equals(bList.get(j).getAnalyst3()))
                {
                    //rowKey = bList.get(j).getAnalyst3();
                    //columnKey = bList.get(j).getBlockID().toString()+"F";
                    //dataset.addValue(bList.get(j).getEventNumber(), rowKey, columnKey);
                    rowKey1 = "reviewed";
                    rowKey2 = "unreviewed";
                    columnKey = bList.get(j).getBlockID().toString()+"F";
                    
                    if("P".equals(bList.get(j).getStatus()) || "S".equals(bList.get(j).getStatus()))
                    {
                        dataset.addValue(0, rowKey1, columnKey);

                        dataset.addValue(bList.get(j).getEventNumber(), rowKey2, columnKey);
                    }
                    else if("F".equals(bList.get(j).getStatus()))
                    {
                        dataset.addValue(bList.get(j).getReviewedEventNumber(), rowKey1, columnKey);
                    
                        dataset.addValue(bList.get(j).getEventNumber()-bList.get(j).getReviewedEventNumber(), rowKey2, columnKey);
                    }
                    else //done
                    {
                        dataset.addValue(bList.get(j).getEventNumber(), rowKey1, columnKey);
                        
                        dataset.addValue(0, rowKey2, columnKey);
                    }
                }
                
            }
            
            //BarRenderer renderer = new BarRenderer();
            StackedBarRenderer renderer;
            renderer = new StackedBarRenderer() {
                
                @Override
                public Paint getItemPaint(int row, int col) {
                    //System.out.println(row + " " + col + " " + super.getItemPaint(row, col));
                    //System.out.println(dataset.getRowKey(row));
                    //System.out.println(dataset.getColumnKey(col));
                    if(dataset.getRowKey(row)=="unreviewed")
                    {
                        return new Color(128,128,128);
                    }
                    else
                    {
                        String flag = ((String)dataset.getColumnKey(col)).substring(((String)dataset.getColumnKey(col)).length()-1);
                        //System.out.println(flag);
                        //return super.getItemPaint(row, col);
                        if("F".equals(flag))
                        {
                            return fColor;
                        }
                        else if("S".equals(flag))
                        {
                            return sColor;
                        }
                        else if("P".equals(flag))
                        {
                            return pColor;
                        }
                        else
                        {
                            return fColor;
                        }
                            
                    }
                }
            };
            
            renderer.setShadowVisible(false);
            renderer.setBarPainter(new StandardBarPainter());
            
            CategoryAxis categoryAxis = new CategoryAxis();
            CategoryPlot plot = new CategoryPlot(dataset,categoryAxis, null,renderer);  
            plot.getDomainAxis().setLabel(analystList.get(i).getName());
            
            int weight = dataset.getColumnCount();
            if(weight!=0)
            {
                cPlotMain.add(plot,weight);
            }
            cPlotMain.setOrientation(PlotOrientation.HORIZONTAL);
        }
        
        freeChartMain = new JFreeChart(cPlotMain); 
        freeChartMain.removeLegend();
        
        info = new ChartRenderingInfo();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
    
        super.paintComponent(g);

        if (this.freeChartMain == null) {
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g.create();
        Dimension size = getSize();
        Insets insets = getInsets();
        Rectangle2D available = new Rectangle2D.Double(insets.left, insets.top,
                size.getWidth() - insets.left - insets.right,
                size.getHeight() - insets.top - insets.bottom);

        double drawWidth = available.getWidth();
        double drawHeight = available.getHeight();
        
        Rectangle2D chartArea = new Rectangle2D.Double(0.0, 0.0, drawWidth,
                drawHeight);
        
        //ChartRenderingInfo info = new ChartRenderingInfo();
        freeChartMain.draw(g2, chartArea, this.anchor, this.info);
        
        g2.dispose();
        
    }
    
    public void updateBlocks()
    {
        cPlotMain = new CombinedRangeCategoryPlot(new NumberAxis());
        
        for(int i = 0; i<analystList.size(); i++)
        {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            String rowKey1, rowKey2;
            String columnKey;
            for(int j = 0; j<bList.size(); j++)
            {
                /*?redundancy or some more changes?*/
                if(analystList.get(i).getName().equals(bList.get(j).getAnalyst1()))
                {
                    //rowKey = bList.get(j).getAnalyst1();
                    //columnKey = bList.get(j).getBlockID().toString()+"P";
                    //dataset.addValue(bList.get(j).getEventNumber(), rowKey, columnKey);
                    rowKey1 = "reviewed";
                    rowKey2 = "unreviewed";
                    columnKey = bList.get(j).getBlockID().toString()+"P";
                    /*the analyst review P but the status is not P, it means the P has been finished*/
                    if(!"P".equals(bList.get(j).getStatus()))
                    {
                        dataset.addValue(bList.get(j).getEventNumber(), rowKey1, columnKey);
                                            
                        dataset.addValue(0, rowKey2, columnKey);
                    }
                    else
                    {
                        dataset.addValue(bList.get(j).getReviewedEventNumber(), rowKey1, columnKey);
                        
                        dataset.addValue(bList.get(j).getEventNumber()-bList.get(j).getReviewedEventNumber(), rowKey2, columnKey);
                    }
                    
                }
                else if(analystList.get(i).getName().equals(bList.get(j).getAnalyst2()))
                {
                    //rowKey = bList.get(j).getAnalyst2();
                    //columnKey = bList.get(j).getBlockID().toString() + "S";
                    //dataset.addValue(bList.get(j).getEventNumber(), rowKey, columnKey);
                    rowKey1 = "reviewed";
                    rowKey2 = "unreviewed";
                    columnKey = bList.get(j).getBlockID().toString()+"S";
                    
                    /*the status is P so S should be set to unreviewed*/
                    if("P".equals(bList.get(j).getStatus()))
                    {
                        dataset.addValue(0, rowKey1, columnKey);

                        dataset.addValue(bList.get(j).getEventNumber(), rowKey2, columnKey);
                    }
                    else if("S".equals(bList.get(j).getStatus()))
                    {
                        dataset.addValue(bList.get(j).getReviewedEventNumber(), rowKey1, columnKey);

                        dataset.addValue(bList.get(j).getEventNumber()-bList.get(j).getReviewedEventNumber(), rowKey2, columnKey);
                    }
                    else //status at F or Done, S is all reviewed
                    {
                        dataset.addValue(bList.get(j).getEventNumber(), rowKey1, columnKey);
                        
                        dataset.addValue(0, rowKey2, columnKey);
                    }
                }
                else if(analystList.get(i).getName().equals(bList.get(j).getAnalyst3()))
                {
                    //rowKey = bList.get(j).getAnalyst3();
                    //columnKey = bList.get(j).getBlockID().toString()+"F";
                    //dataset.addValue(bList.get(j).getEventNumber(), rowKey, columnKey);
                    rowKey1 = "reviewed";
                    rowKey2 = "unreviewed";
                    columnKey = bList.get(j).getBlockID().toString()+"F";
                    
                    if("P".equals(bList.get(j).getStatus()) || "S".equals(bList.get(j).getStatus()))
                    {
                        dataset.addValue(0, rowKey1, columnKey);

                        dataset.addValue(bList.get(j).getEventNumber(), rowKey2, columnKey);
                    }
                    else if("F".equals(bList.get(j).getStatus()))
                    {
                        dataset.addValue(bList.get(j).getReviewedEventNumber(), rowKey1, columnKey);
                    
                        dataset.addValue(bList.get(j).getEventNumber()-bList.get(j).getReviewedEventNumber(), rowKey2, columnKey);
                    }
                    else //done
                    {
                        dataset.addValue(bList.get(j).getEventNumber(), rowKey1, columnKey);
                        
                        dataset.addValue(0, rowKey2, columnKey);
                    }
                }
                
            }
            
            //BarRenderer renderer = new BarRenderer();
            StackedBarRenderer renderer;
            renderer = new StackedBarRenderer() {
                
                @Override
                public Paint getItemPaint(int row, int col) {
                    //System.out.println(row + " " + col + " " + super.getItemPaint(row, col));
                    //System.out.println(dataset.getRowKey(row));
                    //System.out.println(dataset.getColumnKey(col));
                    if(dataset.getRowKey(row)=="unreviewed")
                    {
                        return new Color(128,128,128);
                    }
                    else
                    {
                        String flag = ((String)dataset.getColumnKey(col)).substring(((String)dataset.getColumnKey(col)).length()-1);
                        //System.out.println(flag);
                        //return super.getItemPaint(row, col);
                        if("F".equals(flag))
                        {
                            return fColor;
                        }
                        else if("S".equals(flag))
                        {
                            return sColor;
                        }
                        else if("P".equals(flag))
                        {
                            return pColor;
                        }
                        else
                        {
                            return fColor;
                        }
                            
                    }
                }
            };
            
            renderer.setShadowVisible(false);
            renderer.setBarPainter(new StandardBarPainter());
            
            CategoryAxis categoryAxis = new CategoryAxis();
            CategoryPlot plot = new CategoryPlot(dataset,categoryAxis, null,renderer);  
            plot.getDomainAxis().setLabel(analystList.get(i).getName());
            
            int weight = dataset.getColumnCount();
            if(weight!=0)
            {
                cPlotMain.add(plot,weight);
            }
            cPlotMain.setOrientation(PlotOrientation.HORIZONTAL);
        }
        
        freeChartMain = new JFreeChart(cPlotMain); 
        freeChartMain.removeLegend();

        this.repaint();
    }
}
