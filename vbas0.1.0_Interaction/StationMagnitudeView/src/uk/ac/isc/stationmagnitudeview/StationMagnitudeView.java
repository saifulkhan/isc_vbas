package uk.ac.isc.stationmagnitudeview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
import org.openide.util.Exceptions;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.SeisDataDAO;
import uk.ac.isc.seisdata.SeisUtils;
import uk.ac.isc.seisdata.Station;

/**
 * The station Magnitude view
 *
 * @author hui
 */
class StationMagnitudeView extends JPanel {

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

    //image size of the source
    private final int srcImgSize = 512;

    //the image size of the station map
    private final int StaImSize = 400;

    //the height of station histogram
    private final int StaHistHeight = 200;

    //tile size
    private final int imSize = 256;

    final static double deg2rad = 3.14159 / 180.0;
    final static double rad2deg = 180.0 / 3.14159;

    private int mapDegree = 140;

    //how big the stations are shown on the map 
    private final int stationIconSize = 10;

    //buffer images of the maps
    private final BufferedImage srcImg = new BufferedImage(srcImgSize, srcImgSize, BufferedImage.TYPE_INT_ARGB);

    private final BufferedImage dstImgMb = new BufferedImage(StaImSize, StaImSize, BufferedImage.TYPE_INT_ARGB);

    private final BufferedImage dstImgMs = new BufferedImage(StaImSize, StaImSize, BufferedImage.TYPE_INT_ARGB);

    //for histogram
    private int mbSize = 0;

    private int msSize = 0;

    private double minMag = 10;

    private double maxMag = 0;

    //private int mbMaxCount = 1;
    //private int msMaxCount = 1;
    //here are the objects to draw the two histograms
    private BufferedImage histMb = null;

    private BufferedImage histMs = null;

    private JFreeChart freeChartMb = null;

    private JFreeChart freeChartMs = null;

    public StationMagnitudeView(Hypocentre ph) {

        //fill the data
        allStaMag = new ArrayList<Station>();
        SeisDataDAO.retrieveStationMags(ph.getHypid(), allStaMag);

        this.ph = ph;

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

    public void reset(Hypocentre ph) {

        //fill the data
        allStaMag.clear();
        SeisDataDAO.retrieveStationMags(ph.getHypid(), allStaMag);

        this.ph = ph;

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

        Graphics2D g2 = srcImg.createGraphics();

        //Tile tile;
        int zoom = 1;//(int) (Math.log(srcImgSize/imSize)/Math.log(2));
        BufferedImage tmpImg;
        for (int i = 0; i < zoom * 2; i++) {
            for (int j = 0; j < zoom * 2; j++) {
                String fileName = "/export/home/hui/perl/" + zoom + "/" + i + "/" + j + ".png";

                //String fileName = "main/resources/"+zoom+"/"+i+"/"+j+".png";
                //URL url1 = HypoOverviewTopComponent.class.getClassLoader().getResource(fileName);
                try {
                    tmpImg = ImageIO.read(new File(fileName));
                    //tmpImg = ImageIO.read(url1);
                    g2.drawImage(tmpImg, i * 256, j * 256, null);
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

        Graphics2D g2 = dstImgMb.createGraphics();

        for (int i = 0; i < StaImSize; i++) {
            for (int j = 0; j < StaImSize; j++) {
                int rgb;
                double azi;
                double lat, lon;
                double px, py;

                double dist = Math.sqrt((double) ((i - StaImSize / 2) * (i - StaImSize / 2) + (j - StaImSize / 2) * (j - StaImSize / 2)));
                if (dist > StaImSize / 2) {
                    dstImgMb.setRGB(i, j, 0xFF888888);
                } else {
                    azi = rad2deg * Math.atan2(i - StaImSize / 2, StaImSize / 2 - j);
                    if (azi < 0.0) {
                        azi = azi + 360.0;
                    }

                    //calculate the lat and lon
                    lat = SeisUtils.LatFromAziDelta(ph.getLat(), ph.getLon(), azi, dist * mapDegree / (StaImSize / 2));
                    lon = SeisUtils.LonFromAziDelta(ph.getLat(), ph.getLon(), azi, dist * mapDegree / (StaImSize / 2));
                    if (lon > 180.0) {
                        lon = lon - 360;
                    } else if (lon < -180.0) {
                        lon = lon + 360;
                    }

                    //getting the level
                    int samplingLevel = (int) (Math.log(srcImgSize / imSize) / Math.log(2));
                    px = OsmMercator.LonToX(lon, samplingLevel);
                    py = OsmMercator.LatToY(lat, samplingLevel);
                    rgb = srcImg.getRGB((int) px, (int) py);
                    dstImgMb.setRGB(i, j, rgb);
                }
            }
        }

        //draw the station positions
        //calculate the position and draw all the stations       
        g2.setPaint(new Color(0, 154, 205));//orig:(0,240,240)     
        for (Station sta : mbList) {
            //distance on pixel
            if (sta.getDelta() <= mapDegree && null != sta.getStaMb()) {

                double d1 = sta.getDelta() / (double) mapDegree * (dstImgMb.getWidth() / 2);
                double azi = sta.getAzimuth() / 180.0 * Math.PI;
                double y = dstImgMb.getWidth() / 2 - d1 * Math.cos(azi);
                double x = dstImgMb.getWidth() / 2 + d1 * Math.sin(azi);
                if (sta.getMbRes() != null) {
                    g2.setPaint(getColor(sta.getMbRes()));

                    //innerG2.drawRect((int)(x-stationIconSize/2), (int)(y-stationIconSize/2), stationIconSize, stationIconSize);
                    g2.fillOval((int) (x - stationIconSize / 2), (int) (y - stationIconSize / 2), stationIconSize, stationIconSize);

                }
            }
        }

        //draw the prime hypocentre location
        g2.setPaint(new Color(0, 0, 0));
        g2.setStroke(new BasicStroke(2));
        //innerG2.fillOval((tmpImg2.getWidth()/2-7),(tmpImg2.getWidth()/2-7),15,15);
        int tt = dstImgMb.getWidth() / 2;
        g2.drawLine(tt - 3, tt - 3, tt + 3, tt + 3);
        g2.drawLine(tt - 3, tt + 3, tt + 3, tt - 3);

    }

    //it is the MS map image on the right above corner
    private void drawBufferedImageMs() {

        Graphics2D g2 = dstImgMs.createGraphics();

        for (int i = 0; i < StaImSize; i++) {
            for (int j = 0; j < StaImSize; j++) {
                int rgb;
                double azi;
                double lat, lon;
                double px, py;

                double dist = Math.sqrt((double) ((i - StaImSize / 2) * (i - StaImSize / 2) + (j - StaImSize / 2) * (j - StaImSize / 2)));
                if (dist > StaImSize / 2) {
                    dstImgMs.setRGB(i, j, 0xFF888888);
                } else {
                    azi = rad2deg * Math.atan2(i - StaImSize / 2, StaImSize / 2 - j);
                    if (azi < 0.0) {
                        azi = azi + 360.0;
                    }

                    //calculate the lat and lon
                    lat = SeisUtils.LatFromAziDelta(ph.getLat(), ph.getLon(), azi, dist * mapDegree / (StaImSize / 2));
                    lon = SeisUtils.LonFromAziDelta(ph.getLat(), ph.getLon(), azi, dist * mapDegree / (StaImSize / 2));
                    if (lon > 180.0) {
                        lon = lon - 360;
                    } else if (lon < -180.0) {
                        lon = lon + 360;
                    }

                    //getting the level
                    int samplingLevel = (int) (Math.log(srcImgSize / imSize) / Math.log(2));
                    px = OsmMercator.LonToX(lon, samplingLevel);
                    py = OsmMercator.LatToY(lat, samplingLevel);
                    rgb = srcImg.getRGB((int) px, (int) py);
                    dstImgMs.setRGB(i, j, rgb);
                }
            }
        }

        //draw the station positions
        //calculate the position and draw all the stations       
        g2.setPaint(new Color(0, 154, 205));//orig:(0,240,240)     
        for (Station sta : msList) {
            //distance on pixel
            if (sta.getDelta() <= mapDegree && null != sta.getStaMs()) {

                double d1 = sta.getDelta() / (double) mapDegree * (dstImgMs.getWidth() / 2);
                double azi = sta.getAzimuth() / 180.0 * Math.PI;
                double y = dstImgMs.getWidth() / 2 - d1 * Math.cos(azi);
                double x = dstImgMs.getWidth() / 2 + d1 * Math.sin(azi);

                if (sta.getMsRes() != null) {
                    g2.setPaint(getColor(sta.getMsRes()));
                } else {
                    g2.setPaint(new Color(164, 164, 164));
                }
                //innerG2.drawRect((int)(x-stationIconSize/2), (int)(y-stationIconSize/2), stationIconSize, stationIconSize);
                g2.fillOval((int) (x - stationIconSize / 2), (int) (y - stationIconSize / 2), stationIconSize, stationIconSize);
            }
        }

        //draw the prime hypocentre location
        g2.setPaint(new Color(0, 0, 0));
        g2.setStroke(new BasicStroke(2));
        //innerG2.fillOval((tmpImg2.getWidth()/2-7),(tmpImg2.getWidth()/2-7),15,15);
        int tt = dstImgMs.getWidth() / 2;
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

        freeChartMb.removeLegend();
        freeChartMs.removeLegend();
    }

    //draw all the images in this view
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        int xOffset = (getWidth() - 2 * StaImSize - 20) / 2;
        int yOffset = (getHeight() - StaImSize - StaHistHeight) / 2;

        g2.drawImage(dstImgMb, xOffset, yOffset, StaImSize, StaImSize, null);

        g2.drawImage(dstImgMs, xOffset + StaImSize + 20, yOffset, StaImSize, StaImSize, null);

        histMb = freeChartMb.createBufferedImage(StaImSize, StaHistHeight);
        histMs = freeChartMs.createBufferedImage(StaImSize, StaHistHeight);

        g2.drawImage(histMb, xOffset, yOffset + StaImSize + 20, StaImSize, StaHistHeight, null);
        g2.drawImage(histMs, xOffset + StaImSize + 20, yOffset + StaImSize + 20, StaImSize, StaHistHeight, null);
    }

}
