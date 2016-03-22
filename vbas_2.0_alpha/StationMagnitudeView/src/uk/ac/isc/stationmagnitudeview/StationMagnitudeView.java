package uk.ac.isc.stationmagnitudeview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.text.TextUtilities;
import org.openide.util.Exceptions;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdatainterface.SeisDataDAO;
import uk.ac.isc.seisdata.SeisUtils;
import uk.ac.isc.seisdata.Station;
import uk.ac.isc.seisdata.VBASLogger;

/**
 * The station Magnitude view
 *
 */
public class StationMagnitudeView extends JPanel {

    //image size of the source
    private final int srcImgSize = 512;
    //the image size of the station map
    private final int mapSize = 400;
    //the height of station histogram
    private final int histHeight = 200;
    //tile size
    private final int imSize = 256;

    //place to keep the data
    private Hypocentre ph;

    private ArrayList<Station> allStaMag;

    //the station list of mb, mb is in the station object
    private ArrayList<Station> mbList;

    //the station list of ms, ms is in the station object
    private ArrayList<Station> msList;

    //median value of mb
    private Double netMb;

    //median value of MS
    private Double netMs;

    final static double deg2rad = 3.14159 / 180.0;
    final static double rad2deg = 180.0 / 3.14159;

    private int mapDegree = 140;

    //how big the stations are shown on the map 
    private final int stationIconSize = 10;

    //buffer images of the maps
    private final BufferedImage srcBufferedImage = new BufferedImage(srcImgSize, srcImgSize, BufferedImage.TYPE_INT_ARGB);
    private final BufferedImage dstMbBufferedImage = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_INT_ARGB);
    private final BufferedImage dstMsBufferedImage = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_INT_ARGB);
    // draw the two histograms
    private BufferedImage histMbBufferedImage = null;
    private BufferedImage histMsBufferedImage = null;

    //for histogram
    private int mbSize = 0;
    private int msSize = 0;
    private double minMag = 10;
    private double maxMag = 0;

    //private int mbMaxCount = 1;
    //private int msMaxCount = 1;
    private JFreeChart freeChartMb = null;
    private JFreeChart freeChartMs = null;
    private HypocentresList hyposList;

    static Font chartNameFont = new Font("Verdana", Font.BOLD, 12);

    public StationMagnitudeView(HypocentresList hyposList) {

        this.hyposList = hyposList;
        for (int i = 0; i < hyposList.getHypocentres().size(); i++) {
            if (hyposList.getHypocentres().get(i).getIsPrime()) {
                ph = hyposList.getHypocentres().get(i);
            }
        }

        //fill the data
        allStaMag = new ArrayList<Station>();
        SeisDataDAO.retrieveStationMags(ph.getHypid(), allStaMag);

        //this.ph = ph;
        if (ph.getMagnitude().get("mb") != null) {
            netMb = ph.getMagnitude().get("mb");
        }

        if (ph.getMagnitude().get("MS") != null) {
            netMs = ph.getMagnitude().get("MS");
        }

        mbList = new ArrayList<Station>();
        msList = new ArrayList<Station>();

        //calculate the distance 
        for (Station sta : allStaMag) {
            sta.setDelta(SeisUtils.DeltaFromLatLon(ph.getLat(), ph.getLon(), sta.getLat(), sta.getLon()));
            sta.setAzimuth(SeisUtils.AzimuthFromLatLon(ph.getLat(), ph.getLon(), sta.getLat(), sta.getLon()));

            if (null != sta.getStaMb()) {
                mbSize++;
                if (netMb != null) {
                    sta.setMbRes(sta.getStaMb() - netMb);
                }

                if (sta.getStaMb() < minMag) {
                    minMag = sta.getStaMb();
                }

                if (sta.getStaMb() > maxMag) {
                    maxMag = sta.getStaMb();
                }

                mbList.add(sta);
            } else if (null != sta.getStaMs()) {
                msSize++;
                if (netMs != null) {
                    sta.setMsRes(sta.getStaMs() - netMs);
                }

                if (sta.getStaMs() < minMag) {
                    minMag = sta.getStaMs();
                }

                if (sta.getStaMs() > maxMag) {
                    maxMag = sta.getStaMs();
                }
                msList.add(sta);
            }
        }

        if (minMag < 10) {
            minMag = Math.max(minMag - 1, 0);
        } else {
            minMag = 0;
        }

        if (maxMag > 0) {
            maxMag = Math.min(maxMag + 1, 10);
        } else {
            maxMag = 10;
        }

        //order the list with ascending order
        if (mbList.size() > 1) {
            Collections.sort(mbList, new Comparator<Station>() {

                @Override
                public int compare(Station o1, Station o2) {
                    if (o1.getMbRes() != null && o2.getMbRes() != null) {
                        return Double.compare(Math.abs(o1.getMbRes()), Math.abs(o2.getMbRes()));
                    } else {
                        return 0;
                    }
                }

            });
        }

        if (msList.size() > 1) {
            Collections.sort(msList, new Comparator<Station>() {

                @Override
                public int compare(Station o1, Station o2) {
                    if (o1.getMsRes() != null && o2.getMsRes() != null) {
                        return Double.compare(Math.abs(o1.getMsRes()), Math.abs(o2.getMsRes()));
                    } else {
                        return 0;
                    }
                }

            });
        }

        //draw the two figures
        loadSrcImage();

        //when drawing the figure, build the histogram as well
        drawBufferedImageMb();
        drawBufferedImageMs();

        drawHist();
    }

    public void reset(HypocentresList hyposList) {
        this.hyposList = hyposList;
        for (int i = 0; i < hyposList.getHypocentres().size(); i++) {
            if (hyposList.getHypocentres().get(i).getIsPrime()) {
                ph = hyposList.getHypocentres().get(i);
            }
        }

        //fill the data
        allStaMag.clear();
        SeisDataDAO.retrieveStationMags(ph.getHypid(), allStaMag);

        //this.ph = ph;
        if (ph.getMagnitude().get("mb") != null) {
            netMb = ph.getMagnitude().get("mb");
        }

        if (ph.getMagnitude().get("MS") != null) {
            netMs = ph.getMagnitude().get("MS");
        }

        mbList = new ArrayList<Station>();
        msList = new ArrayList<Station>();

        mbSize = 0;
        msSize = 0;
        minMag = 10;
        maxMag = 0;
        //calculate the distance 
        for (Station sta : allStaMag) {
            sta.setDelta(SeisUtils.DeltaFromLatLon(ph.getLat(), ph.getLon(), sta.getLat(), sta.getLon()));
            sta.setAzimuth(SeisUtils.AzimuthFromLatLon(ph.getLat(), ph.getLon(), sta.getLat(), sta.getLon()));

            if (null != sta.getStaMb()) {
                mbSize++;
                if (netMb != null) {
                    sta.setMbRes(sta.getStaMb() - netMb);
                }

                if (sta.getStaMb() < minMag) {
                    minMag = sta.getStaMb();
                }

                if (sta.getStaMb() > maxMag) {
                    maxMag = sta.getStaMb();
                }

                mbList.add(sta);
            } else if (null != sta.getStaMs()) {
                msSize++;
                if (netMs != null) {
                    sta.setMsRes(sta.getStaMs() - netMs);
                }

                if (sta.getStaMs() < minMag) {
                    minMag = sta.getStaMs();
                }

                if (sta.getStaMs() > maxMag) {
                    maxMag = sta.getStaMs();
                }
                msList.add(sta);
            }
        }

        if (minMag < 10) {
            minMag = Math.max(minMag - 1, 0);
        } else {
            minMag = 0;
        }

        if (maxMag > 0) {
            maxMag = Math.min(maxMag + 1, 10);
        } else {
            maxMag = 10;
        }

        //order the list with ascending order
        if (mbList.size() > 1) {
            Collections.sort(mbList, new Comparator<Station>() {

                @Override
                public int compare(Station o1, Station o2) {
                    if (o1.getMbRes() != null && o2.getMbRes() != null) {
                        return Double.compare(Math.abs(o1.getMbRes()), Math.abs(o2.getMbRes()));
                    } else {
                        return 0;
                    }
                }

            });
        }

        if (msList.size() > 1) {
            Collections.sort(msList, new Comparator<Station>() {

                @Override
                public int compare(Station o1, Station o2) {
                    if (o1.getMsRes() != null && o2.getMsRes() != null) {
                        return Double.compare(Math.abs(o1.getMsRes()), Math.abs(o2.getMsRes()));
                    } else {
                        return 0;
                    }
                }

            });
        }

        //draw the two figures
        loadSrcImage();

        //when drawing the figure, build the histogram as well
        drawBufferedImageMb();
        drawBufferedImageMs();

        drawHist();
    }

    private void loadSrcImage() {

        Graphics2D g2 = srcBufferedImage.createGraphics();

        //Tile tile;
        int zoom = 1;//(int) (Math.log(srcImgSize/imSize)/Math.log(2));
        BufferedImage tmpImg;
        for (int i = 0; i < zoom * 2; i++) {
            for (int j = 0; j < zoom * 2; j++) {
                //String fileName = "/export/home/hui/perl/" + zoom + "/" + i + "/" + j + ".png";
                String fileName = "resources" + File.separator
                        + zoom + File.separator
                        + i + File.separator
                        + j + ".png";
                URL url1 = getClass().getClassLoader().getResource(fileName);
                VBASLogger.logDebug(fileName);
                try {
                    tmpImg = ImageIO.read(url1); //tmpImg = ImageIO.read(new File(fileName));
                    g2.drawImage(tmpImg, i * 256, j * 256, Color.yellow, null);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

    }

    //get the color of each station based on the residual against the mean
    private Color getColor(double residual) {
        Color retColor;
        int r, g, b;

        if (residual < 0) {
            if (residual <= -2) {
                retColor = new Color(0, 174, 239);
            } else {
                double ratio = residual / (-2);
                r = (int) (ratio * 0 + (1 - ratio) * 164);
                g = (int) (ratio * 174 + (1 - ratio) * 164);
                b = (int) (ratio * 239 + (1 - ratio) * 164);
                retColor = new Color(r, g, b);
            }
        } else if (residual > 0) {
            if (residual >= 2) {
                retColor = new Color(220, 35, 0);
            } else {
                double ratio = residual / 2;
                r = (int) (ratio * 220 + (1 - ratio) * 164);
                g = (int) (ratio * 35 + (1 - ratio) * 164);
                b = (int) (ratio * 0 + (1 - ratio) * 164);
                retColor = new Color(r, g, b);
            }
        } else {
            retColor = new Color(196, 196, 196);
        }

        return retColor;
    }

    //it is the mb map image on the left above corner
    private void drawBufferedImageMb() {

        Graphics2D g2 = dstMbBufferedImage.createGraphics();

        /*
         * draw the map (circle)
         */
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                int rgb;
                double azi;
                double lat, lon;
                double px, py;

                double dist = Math.sqrt((double) ((i - mapSize / 2) * (i - mapSize / 2) + (j - mapSize / 2) * (j - mapSize / 2)));

                if (dist > mapSize / 2) {
                    //dstMbBufferedImage.setRGB(i, j, 0xFF888888);
                    dstMbBufferedImage.setRGB(i, j, 0xF4E012);  // Issue #15 remove the gray area, color code(?)
                } else {
                    azi = rad2deg * Math.atan2(i - mapSize / 2, mapSize / 2 - j);
                    if (azi < 0.0) {
                        azi = azi + 360.0;
                    }

                    //calculate the lat and lon
                    lat = SeisUtils.LatFromAziDelta(ph.getLat(), ph.getLon(), azi, dist * mapDegree / (mapSize / 2));
                    lon = SeisUtils.LonFromAziDelta(ph.getLat(), ph.getLon(), azi, dist * mapDegree / (mapSize / 2));
                    if (lon > 180.0) {
                        lon = lon - 360;
                    } else if (lon < -180.0) {
                        lon = lon + 360;
                    }

                    //getting the level
                    int samplingLevel = (int) (Math.log(srcImgSize / imSize) / Math.log(2));
                    px = OsmMercator.LonToX(lon, samplingLevel);
                    py = OsmMercator.LatToY(lat, samplingLevel);
                    rgb = srcBufferedImage.getRGB((int) px, (int) py);
                    dstMbBufferedImage.setRGB(i, j, rgb);
                }
            }
        }

        /*
         * draw the station positions 
         * calculate the position and draw all the stations       
         */
        g2.setPaint(new Color(0, 154, 205));//orig:(0,240,240)     
        for (Station sta : mbList) {
            //distance on pixel
            if (sta.getDelta() <= mapDegree && null != sta.getStaMb()) {

                double d1 = sta.getDelta() / (double) mapDegree * (dstMbBufferedImage.getWidth() / 2);
                double azi = sta.getAzimuth() / 180.0 * Math.PI;
                double y = dstMbBufferedImage.getWidth() / 2 - d1 * Math.cos(azi);
                double x = dstMbBufferedImage.getWidth() / 2 + d1 * Math.sin(azi);
                if (sta.getMbRes() != null) {
                    g2.setPaint(getColor(sta.getMbRes()));
                    //g2.drawRect((int)(x-stationIconSize/2), (int)(y-stationIconSize/2), stationIconSize, stationIconSize);
                    g2.fillOval((int) (x - stationIconSize / 2), (int) (y - stationIconSize / 2), stationIconSize, stationIconSize);

                }
            }
        }

        // Issue #15 add gray border
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(Color.GRAY);
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(0, 0, mapSize, mapSize);
        
         /*
         * draw the prime hypocentre location
         */
        g2.setPaint(new Color(0, 0, 0));
        g2.setStroke(new BasicStroke(2));
        //innerG2.fillOval((tmpImg2.getWidth()/2-7),(tmpImg2.getWidth()/2-7),15,15);
        int tt = dstMbBufferedImage.getWidth() / 2;
        g2.drawLine(tt - 3, tt - 3, tt + 3, tt + 3);
        g2.drawLine(tt - 3, tt + 3, tt + 3, tt - 3);

    }

    //it is the MS map image on the right above corner
    private void drawBufferedImageMs() {

        Graphics2D g2 = dstMsBufferedImage.createGraphics();

        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                int rgb;
                double azi;
                double lat, lon;
                double px, py;

                double dist = Math.sqrt((double) ((i - mapSize / 2) * (i - mapSize / 2) + (j - mapSize / 2) * (j - mapSize / 2)));
                if (dist > mapSize / 2) {
                    //dstMsBufferedImage.setRGB(i, j, 0xFF888888);
                    dstMbBufferedImage.setRGB(i, j, 0xF4E012);  // Issue #15 remove the gray area, color code(?)

                } else {
                    azi = rad2deg * Math.atan2(i - mapSize / 2, mapSize / 2 - j);
                    if (azi < 0.0) {
                        azi = azi + 360.0;
                    }

                    //calculate the lat and lon
                    lat = SeisUtils.LatFromAziDelta(ph.getLat(), ph.getLon(), azi, dist * mapDegree / (mapSize / 2));
                    lon = SeisUtils.LonFromAziDelta(ph.getLat(), ph.getLon(), azi, dist * mapDegree / (mapSize / 2));
                    if (lon > 180.0) {
                        lon = lon - 360;
                    } else if (lon < -180.0) {
                        lon = lon + 360;
                    }

                    //getting the level
                    int samplingLevel = (int) (Math.log(srcImgSize / imSize) / Math.log(2));
                    px = OsmMercator.LonToX(lon, samplingLevel);
                    py = OsmMercator.LatToY(lat, samplingLevel);
                    rgb = srcBufferedImage.getRGB((int) px, (int) py);
                    dstMsBufferedImage.setRGB(i, j, rgb);
                }
            }
        }


        //draw the station positions
        //calculate the position and draw all the stations       
        g2.setPaint(new Color(0, 154, 205));//orig:(0,240,240)     

        for (Station sta : msList) {
            //distance on pixel
            if (sta.getDelta() <= mapDegree && null != sta.getStaMs()) {

                double d1 = sta.getDelta() / (double) mapDegree * (dstMsBufferedImage.getWidth() / 2);
                double azi = sta.getAzimuth() / 180.0 * Math.PI;
                double y = dstMsBufferedImage.getWidth() / 2 - d1 * Math.cos(azi);
                double x = dstMsBufferedImage.getWidth() / 2 + d1 * Math.sin(azi);

                if (sta.getMsRes() != null) {
                    g2.setPaint(getColor(sta.getMsRes()));
                } else {
                    g2.setPaint(new Color(164, 164, 164));
                }
                //innerG2.drawRect((int)(x-stationIconSize/2), (int)(y-stationIconSize/2), stationIconSize, stationIconSize);
                g2.fillOval((int) (x - stationIconSize / 2), (int) (y - stationIconSize / 2), stationIconSize, stationIconSize);
            }
        }

        
        // Issue #15 add gray border
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(Color.GRAY);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(0, 0, mapSize, mapSize);
    
        
        /*
         * draw the prime hypocentre location (x)
         */
        g2.setPaint(new Color(0, 0, 0));
        //g2.setStroke(new BasicStroke(2));
        //innerG2.fillOval((tmpImg2.getWidth()/2-7),(tmpImg2.getWidth()/2-7),15,15);
        int tt = dstMsBufferedImage.getWidth() / 2;
        g2.drawLine(tt - 3, tt - 3, tt + 3, tt + 3);
        g2.drawLine(tt - 3, tt + 3, tt + 3, tt - 3);

    }

    //draw the histograms
    private void drawHist() {

        //get the histograms first
        double[] mbData = null;
        double[] msData = null;

        if (mbSize > 0) {
            mbData = new double[mbSize];
        }

        if (msSize > 0) {
            msData = new double[msSize];
        }

        int i = 0, j = 0;
        for (Station sta : allStaMag) {
            //distance on pixel
            if (null != sta.getStaMb()) {
                mbData[i++] = sta.getStaMb();
            } else if (null != sta.getStaMs()) {
                msData[j++] = sta.getStaMs();
            }
        }

        //DefaultCategoryDataset datasetMb = new DefaultCategoryDataset();
        //DefaultCategoryDataset datasetMs = new DefaultCategoryDataset();
        HistogramDataset datasetMb = new HistogramDataset();
        HistogramDataset datasetMs = new HistogramDataset();

        if (mbSize > 0) {
            datasetMb.addSeries("Mb", mbData, (int) ((maxMag - minMag) / 0.1), minMag, maxMag);
        }

        if (msSize > 0) {
            datasetMs.addSeries("Ms", msData, (int) ((maxMag - minMag) / 0.1), minMag, maxMag);
        }
        //XYSeries seriesMb = new XYSeries("Mb");
        //XYSeries seriesMs = new XYSeries("Ms");

        //for(int i = 0; i<100; i++)
        //{
        //    seriesMb.add(((double)i)/10,countMb[i]);
        //    seriesMs.add(((double)i)/10,countMs[i]);
        //}
        //datasetMb.addSeries(seriesMb);
        //datasetMs.addSeries(seriesMs);
        XYBarRenderer renderer = new XYBarRenderer();
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new StandardXYBarPainter());
        renderer.setPaint(Color.BLACK);

        XYPlot mbPlot = new XYPlot(datasetMb, new NumberAxis(), new NumberAxis(), renderer);
        XYPlot msPlot = new XYPlot(datasetMs, new NumberAxis(), new NumberAxis(), renderer);

        mbPlot.getDomainAxis().setRange(minMag, maxMag);
        //mbPlot.getRangeAxis().setMinorTickCount(1);
        msPlot.getDomainAxis().setRange(minMag, maxMag);
        //msPlot.getRangeAxis().setMinorTickCount(1);

        if (msPlot.getRangeAxis().getUpperBound() < mbPlot.getRangeAxis().getUpperBound()) {
            msPlot.getRangeAxis().setRange(mbPlot.getRangeAxis().getRange());
        } else {
            mbPlot.getRangeAxis().setRange(msPlot.getRangeAxis().getRange());
        }
        //mbPlot.getRangeAxis().setRange(0,Math.max(mbMaxCount,msMaxCount));
        //msPlot.getRangeAxis().setRange(0,Math.max(mbMaxCount,msMaxCount));
        //mbPlot.getDomainAxis().setStandardTickUnits(ticks);//.setMinorTickCount(100);

        freeChartMb = new JFreeChart(mbPlot);
        freeChartMs = new JFreeChart(msPlot);

        // Issue #15 add labels
        mbPlot.getDomainAxis().setLabelFont(chartNameFont);
        mbPlot.getDomainAxis().setLabel("mb");
        msPlot.getDomainAxis().setLabelFont(chartNameFont);
        msPlot.getDomainAxis().setLabel("MS");

        freeChartMb.removeLegend();
        freeChartMs.removeLegend();
    }

    //draw all the images in this view
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int xGap = 20;
        int xOffset = (getWidth() - 2 * mapSize - 20) / 2;
        int yOffset = (getHeight() - mapSize - histHeight) / 2;

        g2.drawImage(dstMbBufferedImage, xOffset, yOffset, mapSize, mapSize, null);
        g2.drawImage(dstMsBufferedImage, xOffset + mapSize + 20, yOffset, mapSize, mapSize, null);

        histMbBufferedImage = freeChartMb.createBufferedImage(mapSize, histHeight);
        histMsBufferedImage = freeChartMs.createBufferedImage(mapSize, histHeight);

        g2.drawImage(histMbBufferedImage, xOffset, yOffset + mapSize + 20, mapSize, histHeight, null);
        g2.drawImage(histMsBufferedImage, xOffset + mapSize + 20, yOffset + mapSize + 20, mapSize, histHeight, null);

        /*String labelText = "mb";
         float xLabel = (float) xOffset + mapSize / 2;
         float yLabel = (float) yOffset - 10; // 10 -> height of the text.
         TextUtilities.drawAlignedString(labelText, g2, xLabel, yLabel, org.jfree.ui.TextAnchor.CENTER);
         labelText = "MS";
         xLabel = xLabel + xGap + mapSize;
         TextUtilities.drawAlignedString(labelText, g2, xLabel, yLabel, org.jfree.ui.TextAnchor.CENTER);
         */
        // TEST:
        try {
            ImageIO.write(histMsBufferedImage, "png",
                    new File(Paths.get(System.getProperty("user.dir") + File.separator + "temp" + File.separator + "StationMagnitudeView.png").toString()
                    ));
        } catch (Exception e) {
            VBASLogger.logSevere("Error creating a png.");
        }
    }

    public BufferedImage getBufferedImage() {
        BufferedImage combined = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics graphics = combined.getGraphics();
        graphics.drawImage(dstMbBufferedImage, 0, 0, mapSize, mapSize, null);
        graphics.drawImage(dstMsBufferedImage, mapSize + 20, 0, mapSize, mapSize, null);
        graphics.drawImage(histMbBufferedImage, 0, mapSize + 20, mapSize, histHeight, null);
        graphics.drawImage(histMsBufferedImage, mapSize + 20, mapSize + 20, mapSize, histHeight, null);

        return combined;
    }

    public int getViewWidth() {
        return mapSize + 20 + mapSize;
    }

    public int getViewHeight() {
        return mapSize + 20 + histHeight;
    }
    
     @Override
    public Dimension getPreferredSize() {
        return new Dimension(getViewWidth(), getViewHeight());
    }
}
