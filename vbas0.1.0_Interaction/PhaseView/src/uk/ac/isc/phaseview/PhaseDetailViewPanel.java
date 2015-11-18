
package uk.ac.isc.phaseview;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import uk.ac.isc.seisdata.Phase;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdata.SeisDataChangeEvent;
import uk.ac.isc.seisdata.SeisDataChangeListener;

/**
 * PhaseDetailView is the right panel for showing the zoom-in version of the phase view
 * 
 * @author hui
 */
class PhaseDetailViewPanel extends JPanel implements SeisDataChangeListener {
    
    private final PhasesList detailPList;
    
    //reference of travel view in order to get range information 
    private final PhaseTravelViewPanel pgvp;
    
    private double residualCutoffLevel = 0.0;
    
    private DuplicateUnorderTimeSeries detailPhaseSeries;
   
    private final DuplicateUnorderTimeSeriesCollection detailDataset = new DuplicateUnorderTimeSeriesCollection();
    
    private JFreeChart freechart = null;
    
    private final int imageWidth = 500;
    
    private final int imageHeight = 1000;
    
    private BufferedImage phasesImage = null;
    
    private double zoomMinTime;
    
    private double zoomMaxTime;
    
    private double zoomMinDist;
    
    private double zoomMaxDist;
    
    //curve data
    private DuplicateUnorderTimeSeriesCollection ttdData = null;
        
    PhaseDetailViewPanel(PhaseTravelViewPanel pgvp,  DuplicateUnorderTimeSeriesCollection ttdData)
    {
        this.pgvp = pgvp;
        this.detailPList = pgvp.getDetailedPList();
        
        setPreferredSize(new Dimension(500,1000));
        
        detailPhaseSeries = new DuplicateUnorderTimeSeries("");
        
        //put phases into the dataseries
        for(Phase p:detailPList.getPhases())
        {    
            if(p.getArrivalTime()!=null)// && ((p.getTimeResidual()!=null && Math.abs(p.getTimeResidual())>residualCutoffLevel)||(p.getTimeResidual()==null)))
            {
                RegularTimePeriod rp = new Second(p.getArrivalTime());
                detailPhaseSeries.add(rp,p.getDistance());
            }
        }
        detailDataset.addSeries(detailPhaseSeries);      
        
        detailPList.addChangeListener(this);
        double[] range = pgvp.getRange();
        
        zoomMinTime = range[0];
        zoomMaxTime = range[1];
        zoomMinDist = range[2];
        zoomMaxDist = range[3];
        
        this.ttdData = ttdData;
        
        createTravelImage();
    }
    
    public void setRange(double[] range)
    {
        zoomMinTime = range[0];
        zoomMaxTime = range[1];
        zoomMinDist = range[2];
        zoomMaxDist = range[3];
    }
    
    public void UpdateData()
    {
        //this.detailPList = pList;
        detailDataset.removeAllSeries();
        detailPhaseSeries = new DuplicateUnorderTimeSeries("");
        
        //put phases into the dataseries
        if(detailPList!=null)
        {
        for(Phase p:detailPList.getPhases())
        {
            
            if(p.getArrivalTime()!=null)// && ((p.getTimeResidual()!=null && Math.abs(p.getTimeResidual())>residualCutoffLevel)||(p.getTimeResidual()==null)))
            {
                if(residualCutoffLevel==0)
                {
                    RegularTimePeriod rp = new Second(p.getArrivalTime());
                    detailPhaseSeries.add(rp,p.getDistance());
                }
                else
                {
                    if(p.getTimeResidual()!=null)
                    {
                        if(Math.abs(p.getTimeResidual())>residualCutoffLevel)
                        {
                            RegularTimePeriod rp = new Second(p.getArrivalTime());
                            detailPhaseSeries.add(rp,p.getDistance());
                        }
                    }
                    else
                    {
                        RegularTimePeriod rp = new Second(p.getArrivalTime());
                        detailPhaseSeries.add(rp,p.getDistance());
                    }
                }
            }
        }
        detailDataset.addSeries(detailPhaseSeries);
        }
        createTravelImage();
        repaint();
    }
    
     /**
     * the range won't change just filter the data
     */
    public void filterData()
    {
        Long minTime = detailPhaseSeries.getMinX();
        Long maxTime = detailPhaseSeries.getMaxX();
        double minDist = detailPhaseSeries.getMinY();
        double maxDist = detailPhaseSeries.getMaxY();
        
        detailDataset.removeAllSeries();
        detailPhaseSeries = new DuplicateUnorderTimeSeries("");
        
        //put phases into the dataseries
        for(Phase p:detailPList.getPhases())
        {
            
            if(p.getArrivalTime()!=null)
            {    
                if(residualCutoffLevel==0)
                {
                    RegularTimePeriod rp = new Second(p.getArrivalTime());
                    detailPhaseSeries.add(rp,p.getDistance());
                }
                else
                {
                    if(p.getTimeResidual()!=null)
                    {
                        if(Math.abs(p.getTimeResidual())>residualCutoffLevel)
                        {
                            RegularTimePeriod rp = new Second(p.getArrivalTime());
                            detailPhaseSeries.add(rp,p.getDistance());

                        }
                    }
                    else //show phases with null residual
                    {
                        RegularTimePeriod rp = new Second(p.getArrivalTime());
                        detailPhaseSeries.add(rp,p.getDistance());
                    }
                }
                
            }
        }
        
        detailPhaseSeries.setMinX(minTime);
        detailPhaseSeries.setMaxX(maxTime);
        detailPhaseSeries.setMinY(minDist);
        detailPhaseSeries.setMaxY(maxDist);
        /*
        double firstTopTime = minTime + (maxTime - minTime) / pageNumber;
        Double firstTopDistance = minDist + (maxDist) / (double) pageNumber;
        for(Phase p:pList.getPhases())
        {
            if(p.getArrivalTime()!=null && (double)p.getArrivalTime().getTime()<firstTopTime && p.getDistance()<firstTopDistance)
            {
                detailedPList.getPhases().add(p);
            }
        }*/

        detailDataset.addSeries(detailPhaseSeries);
        
        createTravelImage();
        
        repaint();
    }
    
    public void setResidualCutoffLevel(double level)
    {
        this.residualCutoffLevel = level;
    }
    
    public double getResidualCutoffLevel()
    {
        return this.residualCutoffLevel;
    }
    
    /**
     * helper function to use jfreechart to generate the bufferedimage
     */
    private void createTravelImage() {
        //define first axis
        DateAxis timeAxis = new DateAxis("");
        timeAxis.setLowerMargin(0.02);  // reduce the default margins
        timeAxis.setUpperMargin(0.02);
        timeAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
        
        timeAxis.setRange((double)(zoomMinTime-2),(double)(zoomMaxTime+2));
        
        NumberAxis valueAxis = new NumberAxis("");
        valueAxis.setRange(Math.max(0,zoomMinDist-1), Math.min(180,zoomMaxDist+1));
        
        XYDotRenderer xyDotRend = new DetailGlyphRenderer(detailPList.getPhases());//new XYDotRenderer();
        //xyDotRend.setDotWidth(6);
        //xyDotRend.setDotHeight(6);
        
        PhasesWithCurvePlot plot = new PhasesWithCurvePlot(detailDataset, timeAxis, valueAxis, xyDotRend,ttdData);
        //XYPlot plot = new XYPlot(detailDataset, timeAxis, valueAxis, xyDotRend);
        plot.setOrientation(PlotOrientation.HORIZONTAL);
        
        freechart = new JFreeChart(plot);
        
        freechart.removeLegend();
        phasesImage = freechart.createBufferedImage(imageWidth, imageHeight);
    }
    
    //paint the detail view on the right side
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        
        int xOffset = Math.max((getWidth() - imageWidth)/2,0);
        int yOffset = Math.max((getHeight() - imageHeight)/2,0);
        
        g2.drawImage(phasesImage, xOffset, yOffset, this);
        
    }

    //repaint when the data changes
    @Override
    public void SeisDataChanged(SeisDataChangeEvent event) {
        
        setRange(pgvp.getRange());
        
        UpdateData();
    }
    
}
