
package uk.ac.isc.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JPanel;
import org.jfree.text.TextUtilities;
import uk.ac.isc.data.TaskBlock;

/**
 *
 * @author hui
 */
public class BlockVisPanel extends JPanel {
     
    private final ArrayList<TaskBlock> bList;
    
    /*every 100 events, 5 pixel is showed*/
    private double pixelPerUnit = 0.1;
    
    private int blockHeight = 30;
    
    private int blockInterval = 60;
    
    private int maxEventNumber = 0;
    
    private final Color pColor = new Color(135,206,235);
    
    private final Color sColor = new Color(240,128,128);
    
    private final Color fColor = new Color(34,139,34);
    
    public BlockVisPanel(ArrayList<TaskBlock> bList)
    {
        this.bList = bList;
        
    }
    
      @Override
    protected void paintComponent(Graphics g) {
    
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        Dimension size = getSize();
        
        for(TaskBlock tb:bList)
        {
            if(tb.getEventNumber()>maxEventNumber)
            {
                maxEventNumber = tb.getEventNumber();
            }
        }
        //System.out.println(size);
        
        //Insets insets = getInsets();
        //Rectangle2D available = new Rectangle2D.Double(insets.left, insets.top,
        //        size.getWidth() - insets.left - insets.right,
        //        size.getHeight() - insets.top - insets.bottom);
        //double drawWidth = available.getWidth();
        //double drawHeight = available.getHeight();
        
        blockHeight = (int) (size.getHeight()/(bList.size()*3+2));
        blockInterval = blockHeight * 2;
        if(maxEventNumber>0 && size.getWidth()>300)
        {
            pixelPerUnit = (double)(size.getWidth()-300)/(double)maxEventNumber;
        }
        
        Paint savedColor = g2.getPaint();
        //Stroke savedStroke = g2.getStroke();
        Font savedFont = g2.getFont();
        
        String dateStartLabel = null;
        String dateEndLabel = null;
        //label start date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
            
        for(int i = 0; i<bList.size(); i++)
        {
            g2.setFont(new Font("Monospaced", Font.BOLD,14));
            double startX = 150;
            double idStartY = (i+1)*(blockInterval)+(i-1)*blockHeight;
            double startY = (i+1)*(blockInterval)+i*blockHeight;
            
            double width1 = bList.get(i).getReviewedEventNumber()*pixelPerUnit;
            double width2 = (bList.get(i).getEventNumber()-bList.get(i).getReviewedEventNumber())*pixelPerUnit;
            Rectangle2D blockBar = new Rectangle2D.Double(startX, startY, width1, blockHeight);
            Rectangle2D blockBar2 = new Rectangle2D.Double(startX+width1, startY, width2, blockHeight);
            
            //label the block ID first on top of the bar
            String label = "Block ID " + bList.get(i).getBlockID();
            TextUtilities.drawRotatedString(label, g2, (int)startX, (int)idStartY, org.jfree.ui.TextAnchor.CENTER_LEFT, 0, org.jfree.ui.TextAnchor.CENTER); 
            
            if("Done".equals(bList.get(i).getStatus()))
            {
                width1 = bList.get(i).getEventNumber()*pixelPerUnit;
                blockBar = new Rectangle2D.Double(startX, startY, width1, blockHeight);
                g2.setPaint(fColor);
                g2.fill(blockBar);
            }
            else
            {
                /*draw reviewed event bar*/
            
                if("P".equals(bList.get(i).getStatus()))
                {
                    dateStartLabel = sdf.format(bList.get(i).getPPlanStartDay());
                    dateEndLabel = sdf.format(bList.get(i).getPPlanEndDay());
                    g2.setPaint(pColor);
                }
                else if("S".equals(bList.get(i).getStatus()))
                {
                    g2.setPaint(sColor);
                    dateStartLabel = sdf.format(bList.get(i).getSPlanStartDay());
                    dateEndLabel = sdf.format(bList.get(i).getSPlanEndDay());
                }
                else if("F".equals(bList.get(i).getStatus()))
                {
                    g2.setPaint(fColor);
                    dateStartLabel = sdf.format(bList.get(i).getFPlanStartDay());
                    dateEndLabel = sdf.format(bList.get(i).getFPlanEndDay());
                }
                g2.fill(blockBar);
            
                /*draw unreviewed event bar*/
            
                g2.setPaint(new Color(128,128,128));
                g2.fill(blockBar2);
            }
            
            g2.setPaint(savedColor);
            g2.setFont(new Font("Monospaced", Font.BOLD,12));
            TextUtilities.drawRotatedString(dateStartLabel, g2, (int)(startX-50), (int)(startY+blockHeight/2), org.jfree.ui.TextAnchor.CENTER, 0, org.jfree.ui.TextAnchor.CENTER); 
            TextUtilities.drawRotatedString(dateEndLabel, g2, (int)(startX+width1+width2+50), (int)(startY+blockHeight/2), org.jfree.ui.TextAnchor.CENTER, 0, org.jfree.ui.TextAnchor.CENTER);
                    
            g2.setFont(savedFont);
        }
        
    }
}
