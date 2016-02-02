package uk.ac.isc.eventscontrolview;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;


/*
 * A panel to commit from assedded commands.
 */

public class AssessedCommandPanel extends JPanel {

    private final JLabel label_total;
    private final JButton button_commit;
    private final JTable table;             // reference of the table

    public AssessedCommandPanel(final JTable commandTable) {
        this.table = commandTable;

        Font font = new Font("Sans-serif", Font.PLAIN, 14);
      
        button_commit = new JButton("Commit");
        button_commit.setBackground(new Color(45, 137, 239));
        button_commit.setForeground(new Color(255, 255, 255));
        button_commit.setFont(font);
        
        label_total = new JLabel("");
        label_total.setFont(font);
       
               button_commit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onButtonCommitActionPerformed(e);
            }
        });


        this.setLayout(new FlowLayout());
        
        this.add(button_commit);
        this.add(label_total);
    }

    
    public void onButtonCommitActionPerformed(ActionEvent e) {
        //
    }

}
