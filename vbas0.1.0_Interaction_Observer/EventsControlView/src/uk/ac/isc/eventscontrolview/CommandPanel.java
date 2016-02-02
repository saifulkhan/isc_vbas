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
 * A panel to assess the selected comands from command table.
 */

public class CommandPanel extends JPanel {

    private final JLabel label_total;
    private final JButton button_assess;
    private final JTable table;             // reference of the table

    public CommandPanel(final JTable commandTable) {
        this.table = commandTable;

        Font font = new Font("Sans-serif", Font.PLAIN, 14);
        
        button_assess = new JButton("Assess");
        button_assess.setBackground(new Color(45, 137, 239));
        button_assess.setForeground(new Color(255, 255, 255));
        button_assess.setFont(font);

        label_total = new JLabel("");
        label_total.setFont(font);
                
        button_assess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onButtonAssessActionPerformed(e);
            }
        });

       

        this.setLayout(new FlowLayout());
        this.add(button_assess);
        this.add(label_total);
    }

    public void onButtonAssessActionPerformed(ActionEvent e) {
        //
    }

}
