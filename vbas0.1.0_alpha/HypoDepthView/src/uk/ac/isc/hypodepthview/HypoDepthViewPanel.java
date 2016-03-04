package uk.ac.isc.hypodepthview;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.Hypocentre;


/*
 *
 * The panel to show the depth of hypocentres
 */
public class HypoDepthViewPanel extends JPanel {

    int width = 600, height = 300;
    //copy the hypocentre list as it is required to be sorted
    private final ArrayList<Hypocentre> hyposList = new ArrayList<Hypocentre>();

    //data wrapper for the jfreechart
    private DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    private double upperbound = 5;

    //the jfreechart object for helping to draw the bar chart
    private JFreeChart freeChart;

    //buffer image of the bar chart
    private BufferedImage depthHistImg;

    public HypoDepthViewPanel(ArrayList<Hypocentre> hypos) {
        Global.logDebug("Here...");

        this.hyposList.addAll(hypos);
        //sort the hypolist
        Collections.sort(hyposList, new Comparator<Hypocentre>() {
            @Override
            public int compare(Hypocentre h1, Hypocentre h2) {
                return (h1.getDepth() - h2.getDepth());
            }
        });

        HyposToCategoryDataset(hyposList);

        //defining axes
        //CategoryAxis categoryAxis = new CategoryAxis();
        ISCEmphCategoryAxis categoryAxis = new ISCEmphCategoryAxis();
        categoryAxis.setTickLabelsVisible(true);
        categoryAxis.setVisible(true);
        categoryAxis.setTickLabelFont(new Font("Verdana", Font.BOLD, 11));
        ValueAxis valueAxis = new NumberAxis();
        valueAxis.setTickLabelFont(new Font("Verdana", Font.BOLD, 11));
        valueAxis.setInverted(true);
        valueAxis.setUpperBound(upperbound * 1.1);
        valueAxis.setLowerBound(0.0);

        //defining the bar renderer
        DepthBarRenderer renderer = new DepthBarRenderer(hyposList);
        //renderer.setBaseItemLabelsVisible(true);
        renderer.setShadowVisible(false);
        renderer.setPaint(Color.BLACK);
        renderer.setMaximumBarWidth(0.1);
        renderer.setBarPainter(new StandardBarPainter());

        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
        //plot.setRenderer(renderer);
        //if the list is too long, set a rotation angle
        if (hyposList.size() > 9) {
            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2.0));
        } else {
            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
        }

        plot.setDomainAxisLocation(AxisLocation.TOP_OR_LEFT);
        plot.setBackgroundPaint(Color.WHITE);

        plot.getDomainAxis().setLowerMargin(0.0);
        plot.getDomainAxis().setUpperMargin(0.0);
        plot.getDomainAxis().setCategoryMargin(0.4);

        plot.getDomainAxis().setTickLabelPaint("ISC", Color.WHITE);

        freeChart = new JFreeChart(plot);
        freeChart.removeLegend();
    }

    /**
     * Equal to setDataset
     *
     * @param hypos
     */
    public void UpdateData(ArrayList<Hypocentre> hypos) {
        this.hyposList.clear();
        this.hyposList.addAll(hypos);

        Collections.sort(hyposList, new Comparator<Hypocentre>() {

            @Override
            public int compare(Hypocentre h1, Hypocentre h2) {
                return (h1.getDepth() - h2.getDepth());
            }
        });

        HyposToCategoryDataset(hyposList);

        //CategoryAxis categoryAxis = new CategoryAxis();
        ISCEmphCategoryAxis categoryAxis = new ISCEmphCategoryAxis();
        categoryAxis.setTickLabelsVisible(true);
        categoryAxis.setVisible(true);
        categoryAxis.setTickLabelFont(new Font("Verdana", Font.BOLD, 11));
        ValueAxis valueAxis = new NumberAxis();
        valueAxis.setTickLabelFont(new Font("Verdana", Font.BOLD, 11));
        valueAxis.setInverted(true);
        valueAxis.setUpperBound(upperbound * 1.1);
        valueAxis.setLowerBound(0.0);

        DepthBarRenderer renderer = new DepthBarRenderer(hyposList);
        //renderer.setBaseItemLabelsVisible(true);
        renderer.setShadowVisible(false);
        renderer.setPaint(Color.BLACK);
        renderer.setMaximumBarWidth(0.1);
        renderer.setBarPainter(new StandardBarPainter());

        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
        //plot.setRenderer(renderer);
        if (hyposList.size() > 7) {
            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
            //categoryAxis.setLabelLocation(AxisLabelLocation.MIDDLE);
        } else {
            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
        }

        plot.setDomainAxisLocation(AxisLocation.TOP_OR_LEFT);
        plot.setBackgroundPaint(Color.WHITE);

        plot.getDomainAxis().setLowerMargin(0.0);
        plot.getDomainAxis().setUpperMargin(0.0);
        plot.getDomainAxis().setCategoryMargin(0.4);

        plot.getDomainAxis().setTickLabelPaint("ISC", Color.WHITE);

        freeChart = new JFreeChart(plot);
        freeChart.removeLegend();

    }

    /**
     * Helper class to find redundancies in the List and rename them cause
     * CategoryDataset requires unique key
     *
     * @param hypos
     */
    private void HyposToCategoryDataset(ArrayList<Hypocentre> hyposList) {
        //clear the dataset for adding new elements
        dataset.clear();
        upperbound = 5;

        List<String> namesList = new ArrayList<String>();
        for (Hypocentre hypo : hyposList) {
            namesList.add(hypo.getAgency());

            //here is just for getting the upperbound
            if (hypo.getErrDepth() != null) {
                if (((double) hypo.getDepth() + hypo.getErrDepth()) > upperbound) {
                    upperbound = (double) hypo.getDepth() + hypo.getErrDepth();
                }
            } else {
                if (hypo.getDepth() > upperbound) {
                    upperbound = (double) hypo.getDepth();
                }
            }
        }

        //find the name redundancy in the List and relabel them as *-1, *-2
        HashSet<String> nameSet = new HashSet<String>(namesList);

        for (String name : nameSet) {
            Integer tmpCount = 0;
            for (int i = 0; i < namesList.size(); i++) {
                if (namesList.get(i).equals(name)) {
                    tmpCount++;

                    if (tmpCount > 1) {
                        String relabelName = name + "-" + tmpCount.toString();
                        namesList.set(i, relabelName);
                    }
                }

            }
        }

        //now, save the values into the dataset
        for (int i = 0; i < hyposList.size(); i++) {
            dataset.addValue(hyposList.get(i).getDepth(), "", namesList.get(i));
        }

    }

    public ArrayList<Hypocentre> getHypos() {
        return this.hyposList;
    }

    public JFreeChart getJFreeChart() {
        return this.freeChart;
    }

    //draw the figure
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (this.freeChart == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        depthHistImg = freeChart.createBufferedImage(width, height);

        int xOffset = (getWidth() - width) / 2;
        int yOffset = (getHeight() - height) / 2;

        g2.drawImage(depthHistImg, xOffset, yOffset, width, height, this);
        //g2.drawImage(depthHistImg, null, 0, 0);
        g2.dispose();

        /*// TEST: 
         Global.logDebug("Write BufferedImage.");
         try {
         ImageIO.write(depthHistImg, "png",
         new File("/export/home/saiful/assess/temp/HypocentreDepthView.png"));
         } catch (Exception e) {
         Global.logSevere("Error creating a png.");
         }*/
    }

    public BufferedImage getDepthHistImg() {
        return depthHistImg;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
