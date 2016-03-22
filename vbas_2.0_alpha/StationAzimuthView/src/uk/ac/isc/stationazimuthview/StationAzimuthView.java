package uk.ac.isc.stationazimuthview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.jfree.text.TextUtilities;
import org.openide.util.Exceptions;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.HypocentresList;
import uk.ac.isc.seisdata.Phase;
import uk.ac.isc.seisdata.PhasesList;
import uk.ac.isc.seisdatainterface.SeisDataDAO;
import uk.ac.isc.seisdata.SeisUtils;
import uk.ac.isc.seisdata.Station;
import uk.ac.isc.seisdata.VBASLogger;

/**
 * This view is for showing the stations' distribution on the map and phase
 * distributions on quantised directions. In addition, two curves shows the
 * first and second azimuthal gap of the event. Big gap from the collected
 * stations indicates large uncertainty of the estimation.
 *
 * @author hui
 */
public class StationAzimuthView extends JPanel {

    // saiful: combine imSize with height and width
    private int viewWidth = 650, viewHeight = 650;
    private int imSize = 650;

    // A reference of the phase list
    private final PhasesList phasesList;
    // A reference of the hypocentre list
    private final HypocentresList hyposList;
    //station list
    private ArrayList<Station> staList = null;

    //referenc of prime hypocentre, it is used to centralise the map because the location of the prime hypocentre is asked to be the central of the map
    private Hypocentre ph = null;
    //the parameter to define how many bins of directions
    private int binNumber = 12;

    //the number of the phases in each direction bin
    private double[] numberDirectionPhases = null;

    //the number of phases in each direction bin and in the range of plotting
    private double[] numberDirectionPhasesInMap = null;

    final static double deg2rad = 3.14159 / 180.0;
    final static double rad2deg = 180.0 / 3.14159;

    //how big of station icon (circle at the current stage)
    private final int stationIconSize = 9;

    private int mapDegree = 180;

    private int farthestDegree = 0;

    private final int srcImgSize = 512;

    private final int warpedImgSize = 256;

    //the size of the source map, need bit higher resolution in case user zooms in detail
    private final BufferedImage srcImg = new BufferedImage(srcImgSize, srcImgSize, BufferedImage.TYPE_INT_ARGB);

    private BufferedImage azImg = null;

    private BufferedImage dstImg = new BufferedImage(imSize, imSize, BufferedImage.TYPE_INT_ARGB);

    //a flag to decide if the number and percentage need be shown
    private boolean textVisible = true;

    //an array to save the first azimuth coverage,second azimuth and degree they cover
    private ArrayList<Integer> aziSummary;

    //Color definitions for different elements
    private static final Color innerColor = new Color(69, 131, 91);

    private static final Color outerColor = new Color(0, 200, 0);

    private static final Color primaryGapColor = new Color(255, 1, 243);

    private static final Color secondaryGapColor = new Color(255, 186, 85);

    /**
     * constructor need hypolist and phaselist
     *
     * @param hyposList
     * @param phasesList
     */
    public StationAzimuthView(HypocentresList hyposList, PhasesList phasesList) {
        VBASLogger.logDebug("Here...");

        this.phasesList = phasesList;
        this.hyposList = hyposList;

        //loading high resolution image for drawing the warping map we use 1 to load 512X512 as the src image
        loadSrcImage();
        //try {
        //    srcImg = ImageIO.read(new File("/export/home/hui/perl/0/0/0.png"));
        //} catch (IOException ex) {
        //    Exceptions.printStackTrace(ex);
        //}

        numberDirectionPhases = new double[binNumber];
        numberDirectionPhasesInMap = new double[binNumber];

        for (Phase p : phasesList.getPhases()) {
            int binIdx = (int) (p.getAzimuth() / (360.0 / (double) binNumber));
            numberDirectionPhases[binIdx]++;

            numberDirectionPhasesInMap[binIdx]++;

            if ((int) Math.ceil(p.getDistance()) > farthestDegree) {
                farthestDegree = (int) Math.ceil(p.getDistance());
            }
        }

        //get prime hypocentre
        for (Hypocentre hypo : hyposList.getHypocentres()) {
            if (hypo.getIsPrime()) {
                ph = hypo;
            }
        }

        aziSummary = SeisUtils.calculateAzSumm(phasesList.getPhases());
        //load station list based on evid from prime hypocentre
        staList = new ArrayList<Station>();
        SeisDataDAO.retrieveAllStations(ph.getEvid(), staList);

        drawDirectionalPie();
        drawBufferedImage();
    }

    /**
     * setter of direction binNumber
     *
     * @param binNumber
     */
    public void setBinNumber(int binNumber) {
        this.binNumber = binNumber;
    }

    public int getBinNumber() {
        return this.binNumber;
    }

    public void setImSize(int imSize) {
        this.imSize = imSize;
    }

    public int getImSize() {
        return imSize;
    }

    public int getFarthestDegree() {
        return this.farthestDegree;
    }

    public void setMapDegree(int mapDeg) {
        this.mapDegree = mapDeg;

        for (int i = 0; i < binNumber; i++) {
            numberDirectionPhasesInMap[i] = 0;
        }

        for (Phase p : phasesList.getPhases()) {
            int binIdx = (int) (p.getAzimuth() / (360.0 / (double) binNumber));
            if (p.getDistance() <= mapDeg) {
                numberDirectionPhasesInMap[binIdx]++;
            }
        }

        drawDirectionalPie();
        drawBufferedImage();
        repaint();
    }

    public int getMapDegree() {
        return this.mapDegree;
    }

    void setTextVisible(boolean selected) {
        this.textVisible = selected;

        drawDirectionalPie();
        repaint();
    }

    //when the view need be reploted 
    void updateData() {

        farthestDegree = 0;

        //update the numberDirectionPhases first
        for (int i = 0; i < binNumber; i++) {
            numberDirectionPhases[i] = 0;
            numberDirectionPhasesInMap[i] = 0;
        }

        for (Phase p : phasesList.getPhases()) {
            int binIdx = (int) (p.getAzimuth() / (360.0 / (double) binNumber));
            numberDirectionPhases[binIdx]++;

            if (p.getDistance() <= this.mapDegree) {
                numberDirectionPhasesInMap[binIdx]++;
            }

            if ((int) Math.ceil(p.getDistance()) > farthestDegree) {
                farthestDegree = (int) Math.ceil(p.getDistance());
            }
        }

        //update stations
        //get prime hypocentre
        for (Hypocentre hypo : hyposList.getHypocentres()) {
            if (hypo.getIsPrime()) {
                ph = hypo;
            }
        }
        SeisDataDAO.retrieveAllStations(ph.getEvid(), staList);

        aziSummary = SeisUtils.calculateAzSumm(phasesList.getPhases());

        drawDirectionalPie();
        drawBufferedImage();
    }

    /**
     * @param non Load the base map
     */
    private void loadSrcImage() {

        Graphics2D g2 = srcImg.createGraphics();

        //Tile tile;
        int zoom = (int) (Math.log(srcImgSize / warpedImgSize) / Math.log(2));
        BufferedImage tmpImg;

        for (int i = 0; i < zoom * 2; i++) {
            for (int j = 0; j < zoom * 2; j++) {
                //String fileName = "/export/home/hui/perl/" + zoom + "/" + i + "/" + j + ".png";
                String fileName = "resources" + File.separator
                        + zoom + File.separator
                        + i + File.separator
                        + j + ".png";
                URL url1 = getClass().getClassLoader().getResource(fileName);

                try {
                    //tmpImg = ImageIO.read(new File(fileName));
                    tmpImg = ImageIO.read(url1);
                    g2.drawImage(tmpImg, i * 256, j * 256, null);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    private void drawDirectionalPie() {

        azImg = new BufferedImage(imSize, imSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = azImg.createGraphics();

        //draw the background map
        g2.setPaint(new Color(220, 220, 220));
        g2.fillOval(0, 0, imSize, imSize);

        g2.setPaint(Color.BLACK);

        float dash1[] = {5.0f};
        BasicStroke dashed = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
        g2.setStroke(dashed);

        int angleStep = 360 / binNumber;
        double lowRadius = 0.75 * imSize;

        for (int i = 0; i < binNumber; i++) {
            double lineAngle = (i * (360 / (double) binNumber)) / 180 * Math.PI;
            g2.drawLine(imSize / 2, imSize / 2, (int) (imSize / 2 + imSize / 2 * Math.sin(lineAngle)), (int) (imSize / 2 - imSize / 2 * Math.cos(lineAngle)));
        }

        g2.setStroke(new BasicStroke(1));

        for (int i = 0; i < binNumber; i++) {
            int lineAngle = i * 360 / binNumber;
            double radius1 = lowRadius;
            double radius2 = lowRadius;

            if (!phasesList.getPhases().isEmpty()) {
                if (numberDirectionPhases[i] / phasesList.getPhases().size() == 0) {
                    radius1 = 0;
                    radius2 = 0;
                } else {
                    //as the inner circle takes 0.75 space, defined in lowRadius, so 0.25 reach to full image size.
                    //here 0.1 is used to leave 15% space for the text, otherwise, change 0.1 to 0.25.
                    radius1 = lowRadius + 0.1 * (double) (imSize * numberDirectionPhases[i]) / (double) phasesList.getPhases().size();
                    radius2 = lowRadius + 0.1 * (double) (imSize * numberDirectionPhasesInMap[i]) / (double) phasesList.getPhases().size();
                }
                //radius = (double) (imSize * numberDirectionPhases[i]) / (double)phasesList.getPhases().size();
            }

            g2.setPaint(outerColor);
            g2.fillArc((int) ((imSize - radius1) / 2), (int) ((imSize - radius1) / 2), (int) radius1, (int) radius1, (int) (90 - lineAngle), -angleStep);

            g2.setPaint(innerColor);
            g2.fillArc((int) ((imSize - radius2) / 2), (int) ((imSize - radius2) / 2), (int) radius2, (int) radius2, (int) (90 - lineAngle), -angleStep);

            g2.setPaint(Color.BLACK);
            if (textVisible) {
                double labelAngle = (((double) i + 0.5) * 2 * Math.PI / binNumber);
                int labelX = (int) (imSize / 2 + imSize * 0.45 * Math.sin(labelAngle));
                int labelY = (int) (imSize / 2 - imSize * 0.45 * Math.cos(labelAngle));
                int percent = (int) (numberDirectionPhases[i] * 100 / (double) phasesList.getPhases().size());
                int number = (int) (numberDirectionPhases[i]);

                String label;
                if ((numberDirectionPhases[i] * 100 / (double) phasesList.getPhases().size() < 1) && numberDirectionPhases[i] > 0) {
                    label = "<1%(" + ((Integer) number).toString() + ")";
                } else {
                    label = ((Integer) percent).toString() + "%(" + ((Integer) number).toString() + ")";
                }
                TextUtilities.drawRotatedString(label, g2, labelX, labelY, org.jfree.ui.TextAnchor.CENTER, 0, org.jfree.ui.TextAnchor.CENTER);
            }
        }

        //draw first azimuth angle
        g2.setPaint(primaryGapColor);
        g2.setStroke(new BasicStroke(4));
        g2.drawArc(0, 0, imSize, imSize, 90 - aziSummary.get(2), aziSummary.get(0) - 360);
        //draw Second Azimuth Angle
        g2.setPaint(secondaryGapColor);
        g2.setStroke(new BasicStroke(4));
        g2.drawArc(4, 4, imSize - 8, imSize - 8, 90 - aziSummary.get(3), aziSummary.get(1) - 360);
    }

    private void drawBufferedImage() {

        //draw the background Pie Chart
        Graphics2D g2 = dstImg.createGraphics();

        //draw the base map
        BufferedImage tmpImg1 = new BufferedImage(warpedImgSize, warpedImgSize, BufferedImage.TYPE_INT_ARGB);
        //the inner image
        BufferedImage tmpImg2 = new BufferedImage((int) (imSize * 0.75), (int) (imSize * 0.75), BufferedImage.TYPE_INT_ARGB);

        Graphics2D innerG2 = tmpImg2.createGraphics();

        for (int i = 0; i < warpedImgSize; i++) {
            for (int j = 0; j < warpedImgSize; j++) {
                int rgb;
                double azi;
                double lat, lon;
                double px, py;

                double dist = Math.sqrt((double) ((i - warpedImgSize / 2)
                        * (i - warpedImgSize / 2)
                        + (j - warpedImgSize / 2)
                        * (j - warpedImgSize / 2)));

                if (dist > warpedImgSize / 2) {
                    tmpImg1.setRGB(i, j, 0xFF000000);
                } else {
                    azi = rad2deg * Math.atan2(i - warpedImgSize / 2, warpedImgSize / 2 - j);
                    if (azi < 0.0) {
                        azi = azi + 360.0;
                    }

                    //calculate the lat and lon
                    lat = SeisUtils.LatFromAziDelta(ph.getLat(), ph.getLon(), azi, dist * mapDegree / (warpedImgSize / 2));
                    lon = SeisUtils.LonFromAziDelta(ph.getLat(), ph.getLon(), azi, dist * mapDegree / (warpedImgSize / 2));
                    if (lon > 180.0) {
                        lon = lon - 360;
                    } else if (lon < -180.0) {
                        lon = lon + 360;
                    }

                    //getting the level
                    int samplingLevel = (int) (Math.log(srcImgSize / warpedImgSize) / Math.log(2));
                    px = OsmMercator.LonToX(lon, samplingLevel);
                    py = OsmMercator.LatToY(lat, samplingLevel);
                    rgb = srcImg.getRGB((int) px, (int) py);
                    tmpImg1.setRGB(i, j, rgb);
                }
            }
        }

        AffineTransform at = new AffineTransform();
        double scaleLevel = (double) (imSize * 0.75) / (double) warpedImgSize;
        at.scale(scaleLevel, scaleLevel);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        tmpImg2 = scaleOp.filter(tmpImg1, tmpImg2);

        // draw the station positions
        // calculate the position and draw all the stations       
        innerG2.setPaint(new Color(0, 154, 205)); // sky color    
        for (Station sta : staList) {
            //distance on pixel
            if (sta.getDelta() <= mapDegree) {

                double d1 = sta.getDelta() / (double) mapDegree * (tmpImg2.getWidth() / 2);
                double azi = sta.getAzimuth() / 180.0 * Math.PI;
                double y = tmpImg2.getWidth() / 2 - d1 * Math.cos(azi);
                double x = tmpImg2.getWidth() / 2 + d1 * Math.sin(azi);

                innerG2.fillOval((int) (x - stationIconSize / 2), (int) (y - stationIconSize / 2), stationIconSize, stationIconSize);
            }
        }

        //draw the prime hypocentre location
        innerG2.setPaint(new Color(0, 0, 0));
        innerG2.setStroke(new BasicStroke(2));
        int tt = tmpImg2.getWidth() / 2;
        innerG2.drawLine(tt - 3, tt - 3, tt + 3, tt + 3);
        innerG2.drawLine(tt - 3, tt + 3, tt + 3, tt - 3);

        at = new AffineTransform();
        at.translate(0.125 * imSize, 0.125 * imSize);
        at.translate(0, 0);
        AffineTransformOp translateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        dstImg = translateOp.filter(tmpImg2, dstImg);

        g2.setStroke(new BasicStroke(3));
        g2.setPaint(new Color(196, 196, 196));
        g2.drawOval((int) (0.125 * imSize), (int) (0.125 * imSize), (int) (imSize * 0.75), (int) (imSize * 0.75));

    }

    //paint function of the view
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int xOffset = (getWidth() - imSize) / 2;
        int yOffset = (getHeight() - imSize) / 2;

        g2.drawImage(azImg, xOffset, yOffset, imSize, imSize, null);

        //g2.drawImage(srcImg, 0, 0, 512, 512, null);
        g2.setClip(new Ellipse2D.Double(xOffset + imSize / 8, yOffset + imSize / 8, imSize * 0.75, imSize * 0.75));
        g2.drawImage(dstImg, xOffset, yOffset, imSize, imSize, null);

        /*
         //VBASLogger.logDebug("Write BufferedImage.");
         try {
         ImageIO.write(getBufferedImage(), "png",
         new File(Paths.get(System.getProperty("user.dir") 
         + File.separator + "temp" 
         + File.separator + "StationAzimuthView.png").toString()));

         ImageIO.write(dstImg, "png",
         new File(Paths.get(System.getProperty("user.dir") 
         + File.separator + "temp" 
         + File.separator + 
         "StationAzimuthView-1.png").toString()));
         ImageIO.write(azImg, "png",
         new File(Paths.get(System.getProperty("user.dir") 
         + File.separator + "temp" 
         + File.separator + "StationAzimuthView-2.png").toString()));

         } catch (Exception e) {
         VBASLogger.logSevere("Error creating a png.");
         }*/
    }

    public BufferedImage getBufferedImage() {
        BufferedImage combined = new BufferedImage(viewWidth, viewHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = combined.getGraphics();
        graphics.drawImage(azImg, 0, 0, null);

        graphics.setClip(new Ellipse2D.Double(0 + viewWidth / 8,
                0 + viewWidth / 8,
                viewWidth * 0.75,
                viewWidth * 0.75));

        graphics.drawImage(dstImg, 0, 0, null);

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
        return new Dimension(getViewWidth(), getViewHeight());
    }
}
