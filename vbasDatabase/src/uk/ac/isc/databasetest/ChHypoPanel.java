
package uk.ac.isc.databasetest;

import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author hui
 */
public class ChHypoPanel extends JPanel {
    
    JLabel message = new JLabel("Change Hypocentre attributes: ");
    
    String[] attrStrings = {"lat","long","depth","time","etype","msec"};
        
    JComboBox changeList = new JComboBox(attrStrings);
        
    JTextField value = new JTextField(50);
        
    public ChHypoPanel()
    {
        
        this.setLayout(new BorderLayout());
        this.add(message, BorderLayout.NORTH);
        this.add(changeList,BorderLayout.CENTER);
        this.add(value, BorderLayout.SOUTH);
        
    }
    
    public void setMessage(String s)
    {
        message.setText(s);
    }
    
    public String getValue()
    {
        return value.getText();
    }
    
    public String getAttribute()
    {
        return (String) changeList.getSelectedItem();
    }
}
