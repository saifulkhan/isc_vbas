
package uk.ac.isc.view;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import uk.ac.isc.data.TaskBlock;

/**
 *
 * @author hui
 */
public class BlockTextPanel extends JPanel {
    
    TaskBlock indBlock;
    
    private final JScrollPane scrollPane;
    
    private final TextArea blockText;

    private SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    
    public BlockTextPanel(TaskBlock tb) {
        
        this.indBlock = tb;
        
        String text = "The Block " + indBlock.getBlockID() + ":\n" +
                "The total event number is: " + indBlock.getEventNumber() + "\n" +
                "The total phase number is: " + indBlock.getPhaseNumber() + "\n" +
                "The current status is in " + indBlock.getStatus() + " Stage.\n" +
                "The primary analyst is: " + indBlock.getAnalyst1() + "\n" +
                "The planned review date of the P stage is from" + df.format(indBlock.getPPlanStartDay()) + 
                " to " + df.format(indBlock.getPPlanEndDay()) + "\n" +
                "The secondary analyst is: " + indBlock.getAnalyst2() + "\n" +
                "The planned review date of the S stage is from" + df.format(indBlock.getSPlanStartDay()) + 
                " to " + df.format(indBlock.getSPlanEndDay()) + "\n" +
                "The final check analyst is: " + indBlock.getAnalyst3() + "\n" +
                "The planned review date of the F stage is from" + df.format(indBlock.getFPlanStartDay()) + 
                " to " + df.format(indBlock.getFPlanEndDay());
        
        blockText = new TextArea(text);
        scrollPane = new JScrollPane(blockText);
        
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public BlockTextPanel() {
        
        String text = "No Taskblock Assigned Yet!";
        
        blockText = new TextArea(text);
        scrollPane = new JScrollPane(blockText);
        
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void setTaskBlock(TaskBlock tb) {
        this.indBlock = tb;
        
        String text = "The Block " + indBlock.getBlockID() + ":\n" +
                "The total event number is: " + indBlock.getEventNumber() + "\n" +
                "The total phase number is: " + indBlock.getPhaseNumber() + "\n" +
                "The current status is in " + indBlock.getStatus() + " Stage.\n" +
                "The primary analyst is: " + indBlock.getAnalyst1() + "\n" +
                "The planned review date of the P stage is from" + df.format(indBlock.getPPlanStartDay()) + 
                " to " + df.format(indBlock.getPPlanEndDay()) + "\n" +
                "The secondary analyst is: " + indBlock.getAnalyst2() + "\n" +
                "The planned review date of the S stage is from" + df.format(indBlock.getSPlanStartDay()) + 
                " to " + df.format(indBlock.getSPlanEndDay()) + "\n" +
                "The final check analyst is: " + indBlock.getAnalyst3() + "\n" +
                "The planned review date of the F stage is from" + df.format(indBlock.getFPlanStartDay()) + 
                " to " + df.format(indBlock.getFPlanEndDay());
        
        blockText.setText(text);
        blockText.repaint();
        //this.repaint();
    }
    
}
