package uk.ac.isc.eventscontrolview;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * The buttons: Asses, Commit, ... 
 * Hint: see the search panel
 */

public class ActionPanel extends JPanel {

    private final JButton button_banish;
    private final JButton button_unbanish;
    private final JButton button_done;
    private final JButton button_assess;
    private final JButton button_commit;
    

    //reference of the control view
    private final AssessedCommandTable act;
 

    public ActionPanel(final AssessedCommandTable act) {
        this.act = act;

        Font font = new Font("Sans-serif", Font.PLAIN, 14);
        
        button_banish = new JButton("Banish");
        button_banish.setBackground(new Color(45, 137, 239));
        button_banish.setForeground(new Color(255, 255, 255));
        button_banish.setFont(font);
        
        button_unbanish = new JButton("Unbanish");
        button_unbanish.setBackground(new Color(45, 137, 239));
        button_unbanish.setForeground(new Color(255, 255, 255));
        button_unbanish.setFont(font);

        button_done = new JButton("Done");
        button_done.setBackground(new Color(45, 137, 239));
        button_done.setForeground(new Color(255, 255, 255));
        button_done.setFont(font);
                
        button_assess = new JButton("Assess");
        button_assess.setBackground(new Color(45, 137, 239));
        button_assess.setForeground(new Color(255, 255, 255));
        button_assess.setFont(font);
        
        button_commit = new JButton("Commit");
        button_commit.setBackground(new Color(45, 137, 239));
        button_commit.setForeground(new Color(255, 255, 255));  
        button_commit.setFont(font);
        
        this.setLayout(new FlowLayout());
        this.add(button_banish);
        this.add(button_unbanish);
        this.add(button_done);
        this.add(button_assess);
        this.add(button_commit);

    }

}
