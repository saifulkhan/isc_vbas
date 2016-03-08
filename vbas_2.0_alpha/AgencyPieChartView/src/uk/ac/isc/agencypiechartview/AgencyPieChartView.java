package uk.ac.isc.agencypiechartview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.swing.JPanel;
import org.jfree.text.TextUtilities;

/**
 * The Piechart to show the percentage of phases reported by each agency
 *
 * @author hui
 */
public class AgencyPieChartView extends JPanel {

    //sorted data 
    PieChartData piedata;

    private final int centrex = 300;

    private final int centrey = 300;

    private final int radius = 150;

    private final int imWidth = 600;
    private final int imHeight = 600;

    //buffer image of the piechart
    private BufferedImage pieChartImg;

    public void setData(PieChartData piedata) {
        this.piedata = piedata;
    }

    //painting function
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        int xOffset = (getWidth() - imWidth) / 2;
        int yOffset = (getHeight() - imHeight) / 2;

        drawPieChart();

        g2.drawImage(pieChartImg, xOffset, yOffset, imWidth, imHeight, this);
        //g2.drawImage(DepthHistImg, null, 0, 0);
        g2.dispose();

    }

    public int getAgencyPieChartViewWidth() {
        return imWidth;
    }

    public int getAgencyPieChartViewHeight() {
        return imHeight;
    }

    public BufferedImage getBufferedImage() {
        return pieChartImg;
    }

    //function to generate the piechart image
    //check the number of agencies which contribution is smaller than 5
    //if the number smaller than 25, use one column to show the names of these agencies
    //else use two columns to show the names
    private void drawPieChart() {

        pieChartImg = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = pieChartImg.createGraphics();

        //shift on x direction, shift on y is 0
        int xShift = 60;

        int count = 0; //count for calculating the position of small piece
        int total = 0;

        for (Map.Entry<String, Double> entry : piedata.getMap().entrySet()) {
            total++;
            if ((entry.getValue() * 360) <= 5) //smaller than 5 degree
            {
                count++;
            }
        }

        double sum = 0.0;
        if (count <= 25) //no shift
        {
            g2.setPaint(new Color(255, 255, 255));
            g2.fillOval(centrex - radius, centrey - radius, 2 * radius, 2 * radius);

            g2.setPaint(new Color(0, 0, 0));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(centrex - radius, centrey - radius, 2 * radius, 2 * radius);

            if (total > 1) {
                g2.drawLine(centrex, centrey, centrex, centrex - radius);
            }

            //draw pies
            int tmpcount = 0;
            for (Map.Entry<String, Double> entry : piedata.getMap().entrySet()) {
                //draw pie line and Entry name
                if ((entry.getValue() * 360) > 5) //larger than 5 degree
                {
                    double labelAngle = sum + entry.getValue() * Math.PI;
                    int labelX = (int) (centrex + 180 * Math.sin(labelAngle));
                    int labelY = (int) (centrey - 180 * Math.cos(labelAngle));
                    String label = entry.getKey();

                    TextUtilities.drawRotatedString(label, g2, labelX, labelY, org.jfree.ui.TextAnchor.CENTER, 0, org.jfree.ui.TextAnchor.CENTER);

                    sum += entry.getValue() * 2 * Math.PI;
                    if (total > 1) {
                        g2.drawLine(centrex, centrey, (int) (centrex + radius * Math.sin(sum)), (int) (centrey - radius * Math.cos(sum)));
                    }
                } else {
                    int labelX;
                    int labelY;
                    String label;

                    labelX = 50;
                    labelY = 50 + tmpcount * 20;
                    label = entry.getKey();
                    TextUtilities.drawRotatedString(label, g2, labelX, labelY, org.jfree.ui.TextAnchor.CENTER, 0, org.jfree.ui.TextAnchor.CENTER);
                    tmpcount++;
                }
            }
        } else {
            g2.setPaint(new Color(255, 255, 255));
            g2.fillOval(centrex - radius + xShift, centrey - radius, 2 * radius, 2 * radius);

            g2.setPaint(new Color(0, 0, 0));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(centrex - radius + xShift, centrey - radius, 2 * radius, 2 * radius);

            if (total > 1) {
                g2.drawLine(centrex + xShift, centrey, centrex + xShift, centrex - radius);
            }
            //draw pies
            int tmpcount = 0;

            for (Map.Entry<String, Double> entry : piedata.getMap().entrySet()) {
                //draw pie line and Entry name
                if ((entry.getValue() * 360) > 5) //larger than 5 degree
                {
                    double labelAngle = sum + entry.getValue() * Math.PI;
                    int labelX = (int) (centrex + 180 * Math.sin(labelAngle)) + xShift;
                    int labelY = (int) (centrey - 180 * Math.cos(labelAngle));
                    String label = entry.getKey();

                    TextUtilities.drawRotatedString(label, g2, labelX, labelY, org.jfree.ui.TextAnchor.CENTER, 0, org.jfree.ui.TextAnchor.CENTER);

                    sum += entry.getValue() * 2 * Math.PI;
                    g2.drawLine(centrex + xShift, centrey, (int) (centrex + radius * Math.sin(sum) + xShift), (int) (centrey - radius * Math.cos(sum)));

                    //label the name on outer circle
                } else {
                    int labelX;
                    int labelY;
                    String label;

                    if (tmpcount <= 25) {
                        labelX = 50;
                        labelY = 50 + tmpcount * 20;
                        label = entry.getKey();
                        TextUtilities.drawRotatedString(label, g2, labelX, labelY, org.jfree.ui.TextAnchor.CENTER, 0, org.jfree.ui.TextAnchor.CENTER);
                        tmpcount++;
                    } else {
                        labelX = 110;
                        labelY = 50 + (tmpcount - 26) * 20;
                        label = entry.getKey();
                        TextUtilities.drawRotatedString(label, g2, labelX, labelY, org.jfree.ui.TextAnchor.CENTER, 0, org.jfree.ui.TextAnchor.CENTER);
                        tmpcount++;
                    }
                }

            }
        }

        //draw the box 
        //here because radius is equal to 150, leave 10 pixel space between the circle when drawing the two lines, the line is 100 pixels so (160+100)
        //(20,40) is the left corner to draw the rectangle for annotations, box width is 60, height is 20 pixel times number of agencies
        if (count <= 25 && count > 0) {
            g2.drawRect(20, 40, 60, count * 20);
            //draw the line to point them
            double lineAngle = sum + (2 * Math.PI - sum) / 2;
            g2.drawLine((int) (centrex + 160 * Math.sin(lineAngle)), (int) (centrex - 160 * Math.cos(lineAngle)), (int) (centrex + 260 * Math.sin(lineAngle)), (int) (centrey - 260 * Math.cos(lineAngle)));
            g2.drawLine((int) (centrex + 260 * Math.sin(lineAngle)), (int) (centrey - 260 * Math.cos(lineAngle)), 90, (int) (centrey - 260 * Math.cos(lineAngle)));
        } else if (count > 25) {
            g2.drawRect(20, 40, 60, 520);
            g2.drawRect(80, 40, 60, 520);

            //draw the line to point them
            double lineAngle = sum + (2 * Math.PI - sum) / 2;
            g2.drawLine((int) (centrex + 160 * Math.sin(lineAngle) + xShift), (int) (centrey - 160 * Math.cos(lineAngle)), (int) (centrex + 260 * Math.sin(lineAngle) + xShift), (int) (centrey - 260 * Math.cos(lineAngle)));
            g2.drawLine((int) (centrex + 260 * Math.sin(lineAngle) + xShift), (int) (centrey - 260 * Math.cos(lineAngle)), 90 + xShift, (int) (centrey - 260 * Math.cos(lineAngle)));
        }

    }
}
