package uk.ac.isc.hypomagnitudeview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedRangeCategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.SeisUtils;
import uk.ac.isc.seisdata.VBASLogger;

/**
 * The panel to draw the view
 *
 */
public class HypoMagnitudeViewPanel extends JPanel {

    private final ArrayList<Hypocentre> hyposList = new ArrayList<Hypocentre>();

    private int viewWidth = 300, viewHeight = 600;
    //set a vertical or horizontal layout
    private final boolean isVertical = false;

    //median magnitude, probably will change to principle magnitude in future
    private Double medMag = null;

    int totalMag = 0; //which is total magnitude number, probably need rename

    private HashMap<Integer, HashMap<String, Double>> magTypeMap = null;

    //hash maps to keep all the different magnitude types
    private HashMap<String, Double> mlHypoMap = null;
    private HashMap<String, Double> mbHypoMap = null;
    private HashMap<String, Double> msHypoMap = null;
    private HashMap<String, Double> mwHypoMap = null;
    private HashMap<String, Double> otherHypoMap = null;

    //the jfreechart for drawing the main view
    private JFreeChart freeChartMain = null;
    //the jfreechart for drawing the delta view
    private JFreeChart freeChartDelta = null;

    //image of hypo magnitude
    private BufferedImage hypoMagImg;
    //image of delta hypo magnitude
    private BufferedImage magDeltaImg;

    static Font chartNameFont = new Font("Verdana", Font.BOLD, 12);
    static Font domainLabelFont = new Font("Verdana", Font.PLAIN, 12);
    static Font tickMarkRangeLabelFont = new Font("Verdana", Font.PLAIN, 12);
    static Font tickMarkDomainLabelFont = new Font("Verdana", Font.PLAIN, 12);

    public HypoMagnitudeViewPanel(ArrayList<Hypocentre> hypos) {
        VBASLogger.logDebug("Here...");

        this.hyposList.addAll(hypos);

        // get medium value of the magnitude no matter which type it is
        medMag = getMediumMagnitude();
        fillMagTypeMap();
        if (isVertical) {
            setTwoVtcCharts();
        } else {
            setTwoHztCharts();
        }

        freeChartMain.fireChartChanged();
        freeChartDelta.fireChartChanged();
    }

    /**
     * Equal to setDataset
     *
     * @param hypos
     */
    public void updateData(ArrayList<Hypocentre> hypos) {
        this.hyposList.clear();
        this.hyposList.addAll(hypos);

        medMag = getMediumMagnitude();
        fillMagTypeMap();
        if (isVertical) {
            setTwoVtcCharts();
        } else {
            setTwoHztCharts();
        }

        freeChartMain.fireChartChanged();
        freeChartDelta.fireChartChanged();
    }

    /**
     * Helper class
     *
     * @param hypos
     */
    private Double getMediumMagnitude() {
        Double retMag = null;

        ArrayList<Double> sortList = new ArrayList<Double>();

        for (Hypocentre hypo : hyposList) {
            for (Double value : hypo.getMagnitude().values()) {
                sortList.add(value);
            }
        }

        if (sortList.size() > 0) {
            Collections.sort(sortList);
            retMag = sortList.get(sortList.size() / 2);
        }

        return retMag;
    }

    //fill those maps by checking the hypo list
    private void fillMagTypeMap() {
        totalMag = 0;

        HashMap<String, String> groupMag = SeisUtils.getGroupMagnitudeTypes();

        magTypeMap = new HashMap<Integer, HashMap<String, Double>>();
        mlHypoMap = new HashMap<String, Double>();
        mbHypoMap = new HashMap<String, Double>();
        msHypoMap = new HashMap<String, Double>();
        mwHypoMap = new HashMap<String, Double>();
        otherHypoMap = new HashMap<String, Double>();

        for (Hypocentre hypo : hyposList) {

            for (Map.Entry<String, Double> entry : hypo.getMagnitude().entrySet()) {
                if (groupMag.containsKey(entry.getKey())) {
                    if (groupMag.get(entry.getKey()).equals("local")) {
                        mlHypoMap.put(hypo.getAgency() + "-" + entry.getKey(), entry.getValue());
                    } else if (groupMag.get(entry.getKey()).equals("mb")) {
                        mbHypoMap.put(hypo.getAgency() + "-" + entry.getKey(), entry.getValue());
                    } else if (groupMag.get(entry.getKey()).equals("MS")) {
                        msHypoMap.put(hypo.getAgency() + "-" + entry.getKey(), entry.getValue());
                    } else //MW
                    {
                        mwHypoMap.put(hypo.getAgency() + "-" + entry.getKey(), entry.getValue());
                    }
                } else //Other
                {
                    otherHypoMap.put(hypo.getAgency() + "-" + entry.getKey(), entry.getValue());
                }
            }
        }

        if (mbHypoMap.isEmpty()) {
            mbHypoMap.put("", -2.0);
            totalMag++;
        }
        if (msHypoMap.isEmpty()) {
            msHypoMap.put("", -2.0);
            totalMag++;
        }
        if (mwHypoMap.isEmpty()) {
            mwHypoMap.put("", -2.0);
            totalMag++;
        }
        if (mlHypoMap.isEmpty()) {
            mlHypoMap.put("", -2.0);
            totalMag++;
        }
        if (otherHypoMap.isEmpty()) {
            otherHypoMap.put("", -2.0);
            totalMag++;
        }

        magTypeMap.put(0, mlHypoMap);
        magTypeMap.put(2, mbHypoMap);
        magTypeMap.put(1, msHypoMap);
        magTypeMap.put(3, mwHypoMap);
        magTypeMap.put(4, otherHypoMap);
    }

    //draw horizontal layout
    private void setTwoHztCharts() {
        CombinedRangeCategoryPlot cPlotMain = new CombinedRangeCategoryPlot(new NumberAxis());;

        CombinedRangeCategoryPlot cPlotDelta = new CombinedRangeCategoryPlot(new NumberAxis());

        for (int i = 0; i < 5; i++) {
            DefaultCategoryDataset datasetMain = new DefaultCategoryDataset();
            DefaultCategoryDataset datasetDelta = new DefaultCategoryDataset();

            String rowKey = "MagType";
            String columnKey;

            for (Map.Entry<String, Double> entry : magTypeMap.get(i).entrySet()) {
                columnKey = entry.getKey();
                if (entry.getValue() > -1.0) {
                    datasetMain.addValue(entry.getValue(), rowKey, columnKey);
                    datasetDelta.addValue(entry.getValue() - medMag, rowKey, columnKey);
                } else {
                    datasetMain.addValue(0, rowKey, columnKey);
                    datasetDelta.addValue(0, rowKey, columnKey);
                }
            }

            BarRenderer renderer = new BarRenderer();
            renderer.setShadowVisible(false);
            renderer.setBarPainter(new StandardBarPainter());
            renderer.setPaint(Color.BLACK);
            renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());

            MagBarRenderer rend2;

            if (i == 0) {
                rend2 = new MagBarRenderer("local");
            } else if (i == 2) {
                rend2 = new MagBarRenderer("mb");
            } else if (i == 1) {
                rend2 = new MagBarRenderer("MS");
            } else if (i == 3) {
                rend2 = new MagBarRenderer("MW");
            } else {
                rend2 = new MagBarRenderer("Other");
            }
            renderer.setShadowVisible(false);
            renderer.setBarPainter(new StandardBarPainter());

            //control bar viewHeight
            if (magTypeMap.get(i).size() > 1) {
                renderer.setMaximumBarWidth(0.3);
                rend2.setMaximumBarWidth(0.3);
            } else {
                renderer.setMaximumBarWidth(0.5);
                rend2.setMaximumBarWidth(0.5);
            }

            MagSimpleCategoryAxis categoryAxisMain = new MagSimpleCategoryAxis();
            MagSimpleCategoryAxis categoryAxisDelta = new MagSimpleCategoryAxis();

            categoryAxisDelta.setTickLabelsVisible(true);

            CategoryPlot plotMain = new CategoryPlot(datasetMain, categoryAxisMain, null, rend2);
            CategoryPlot plotDelta = new CategoryPlot(datasetDelta, categoryAxisDelta, null, renderer);

            plotMain.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            plotDelta.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            //plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            //plot.setBackgroundPaint(Color.GRAY);
            //plot.setBackgroundAlpha((float) 0.1);
            //plot.getDomainAxis().setLabelPaint(eventPaint[i%5]);
            plotDelta.getDomainAxis().setTickLabelFont(tickMarkDomainLabelFont);
            plotMain.getDomainAxis().setTickLabelFont(tickMarkDomainLabelFont);
            plotMain.getDomainAxis().setVisible(true);
            plotDelta.getDomainAxis().setVisible(true);

            plotMain.getDomainAxis().setLabelFont(domainLabelFont);
            if (i == 0) {
                plotMain.getDomainAxis().setLabel("local");
            } else if (i == 2) {
                plotMain.getDomainAxis().setLabel("mb");
            } else if (i == 1) {
                plotMain.getDomainAxis().setLabel("MS");
            } else if (i == 3) {
                plotMain.getDomainAxis().setLabel("MW");
            } else {
                plotMain.getDomainAxis().setLabel("Other");
            }

            plotMain.getDomainAxis().setLabelFont(domainLabelFont);
            plotMain.getDomainAxis().setLabelAngle(Math.PI / 2);

            plotMain.getDomainAxis().setTickLabelsVisible(false);
            //cPlot.getRangeAxis().setInverted(true);

            int weight = datasetMain.getColumnCount();
            cPlotMain.add(plotMain, weight);
            cPlotDelta.add(plotDelta, weight);
        }

        cPlotMain.getRangeAxis().setLabelFont(chartNameFont);
        cPlotMain.getRangeAxis().setLabel("Magnitude");
        cPlotMain.getRangeAxis().setTickLabelFont(tickMarkRangeLabelFont);
        cPlotMain.getRangeAxis().setRange(0, 10);

        cPlotDelta.getRangeAxis().setLabelFont(chartNameFont);
        String label2 = "Residual " + "\u229F  " + (medMag == null ? " " : medMag.toString()); // Bug#7 
        cPlotDelta.getRangeAxis().setLabel(label2);
        cPlotDelta.getRangeAxis().setTickLabelFont(tickMarkRangeLabelFont);
        cPlotDelta.getRangeAxis().setRange(-2, 2);

        cPlotMain.setOrientation(PlotOrientation.HORIZONTAL);
        cPlotDelta.setOrientation(PlotOrientation.HORIZONTAL);

        freeChartMain = new JFreeChart(cPlotMain);
        freeChartDelta = new JFreeChart(cPlotDelta);

        freeChartMain.removeLegend();
        freeChartDelta.removeLegend();
    }

    public ArrayList<Hypocentre> getHypos() {
        return this.hyposList;
    }

    //paint the figure
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (this.freeChartMain == null || this.freeChartDelta == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        // Saiful: isVertical is false (hardcoded)
        if (isVertical) {
            hypoMagImg = freeChartMain.createBufferedImage(600, 300);
            magDeltaImg = freeChartDelta.createBufferedImage(600, 150);
            int xOffset = (getWidth() - 600) / 2;
            int yOffset = (getHeight() - 450) / 2;

            g2.drawImage(magDeltaImg, xOffset, yOffset, 600, 150, this);
            g2.drawImage(hypoMagImg, xOffset, yOffset + 150, 600, 300, this);

        } else {
            hypoMagImg = freeChartMain.createBufferedImage(viewWidth, viewHeight);
            magDeltaImg = freeChartDelta.createBufferedImage(viewWidth, viewHeight);

            int xOffset = (getWidth() - viewWidth * 2) / 2;
            int yOffset = (getHeight() - viewHeight) / 2;

            g2.drawImage(hypoMagImg, xOffset, yOffset, viewWidth, viewHeight, this);
            g2.drawImage(magDeltaImg, xOffset + viewWidth, yOffset, viewWidth, viewHeight, this);
        }

        g2.dispose();

        /*
         // TEST:
         // create the new image, canvas size is the max. of both image sizes
         try {
         ImageIO.write(this.getBufferedImage(), "png",
         new File("/export/home/saiful/assess/temp/HypocentreMagnitudeview.png"));
         } catch (Exception e) {
         Global.logSevere("Error creating a png.");
         }*/
    }

    public BufferedImage getBufferedImage() {
        BufferedImage combined = new BufferedImage(getViewWidth() * 2,
                getViewHeight(), BufferedImage.TYPE_INT_ARGB);

        // paint both images, preserving the alpha channels
        Graphics graphics = combined.getGraphics();
        graphics.drawImage(hypoMagImg, 0, 0, null);
        graphics.drawImage(magDeltaImg, viewWidth, 0, null);

        // Debug
        /*try {
            ImageIO.write(combined, "png",
                    new File("/export/home/saiful/assess/temp/HypocentreMagnitudeview.png"));
        } catch (Exception e) {
        }*/
        return combined;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(viewWidth * 2, viewHeight);
    }

    /*
     * If the layout is vertical.
     * Not used: hard coded now.
     */
    private void setTwoVtcCharts() {
        CombinedRangeCategoryPlot cPlotMain = new CombinedRangeCategoryPlot(new NumberAxis());

        CombinedRangeCategoryPlot cPlotDelta = new CombinedRangeCategoryPlot(new NumberAxis());

        for (int i = 0; i < 5; i++) {
            DefaultCategoryDataset datasetMain = new DefaultCategoryDataset();
            DefaultCategoryDataset datasetDelta = new DefaultCategoryDataset();

            String rowKey = "MagType";
            String columnKey;

            for (Map.Entry<String, Double> entry : magTypeMap.get(i).entrySet()) {
                columnKey = entry.getKey();
                if (entry.getValue() > -1.0) {
                    datasetMain.addValue(entry.getValue(), rowKey, columnKey);
                    datasetDelta.addValue(entry.getValue() - medMag, rowKey, columnKey);
                } else {
                    datasetMain.addValue(0, rowKey, columnKey);
                    datasetDelta.addValue(0, rowKey, columnKey);
                }
            }

            BarRenderer renderer = new BarRenderer();
            renderer.setShadowVisible(false);
            renderer.setBarPainter(new StandardBarPainter());
            renderer.setPaint(Color.BLACK);
            renderer.setMaximumBarWidth(0.3);
            //renderer.setToolTipGenerator(renderer.getBaseToolTipGenerator());

            CategoryAxis categoryAxisMain = new CategoryAxis();
            CategoryAxis categoryAxisDelta = new CategoryAxis();

            categoryAxisMain.setTickLabelsVisible(true);

            CategoryPlot plotMain = new CategoryPlot(datasetMain, categoryAxisMain, null, renderer);
            CategoryPlot plotDelta = new CategoryPlot(datasetDelta, categoryAxisDelta, null, renderer);

            plotMain.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            plotDelta.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
            //plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            //plot.setBackgroundPaint(Color.GRAY);
            //plot.setBackgroundAlpha((float) 0.1);
            //plot.getDomainAxis().setLabelPaint(eventPaint[i%5]);
            plotDelta.getDomainAxis().setTickLabelFont(tickMarkDomainLabelFont);
            plotMain.getDomainAxis().setTickLabelFont(tickMarkDomainLabelFont);

            plotMain.getDomainAxis().setVisible(true);
            plotMain.getDomainAxis().setLabelFont(domainLabelFont);
            if (i == 0) {
                plotDelta.getDomainAxis().setLabel("local");
            } else if (i == 2) {
                plotDelta.getDomainAxis().setLabel("mb");
            } else if (i == 1) {
                plotDelta.getDomainAxis().setLabel("MS");
            } else if (i == 3) {
                plotDelta.getDomainAxis().setLabel("MW");
            } else {
                plotDelta.getDomainAxis().setLabel("Other");
            }

            plotMain.getDomainAxis().setTickLabelFont(tickMarkDomainLabelFont);
            plotMain.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2.0));

            plotDelta.getDomainAxis().setVisible(true);
            plotDelta.getDomainAxis().setLabelFont(domainLabelFont);
            plotDelta.getDomainAxis().setTickLabelsVisible(false);
            //cPlot.getRangeAxis().setInverted(true);

            int weight = datasetMain.getColumnCount();
            cPlotMain.add(plotMain, weight);
            cPlotDelta.add(plotDelta, weight);
        }

        cPlotMain.getRangeAxis().setLabelFont(domainLabelFont);
        cPlotMain.getRangeAxis().setLabel("Magnitude");
        cPlotMain.getRangeAxis().setTickLabelFont(tickMarkRangeLabelFont);
        cPlotMain.getRangeAxis().setRange(0, 10);

        cPlotDelta.getRangeAxis().setLabelFont(domainLabelFont);
        cPlotDelta.getRangeAxis().setLabel("Residual");
        cPlotDelta.getRangeAxis().setTickLabelFont(tickMarkRangeLabelFont);
        cPlotDelta.getRangeAxis().setRange(-2, 2);

        freeChartMain = new JFreeChart(cPlotMain);
        freeChartDelta = new JFreeChart(cPlotDelta);

        freeChartMain.removeLegend();
        freeChartDelta.removeLegend();
    }

}
