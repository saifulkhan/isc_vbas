
package uk.ac.isc.eventscontrolview;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JViewport;

/**
 *
 * A search panel for searching event
 * @author hui
 */
public class EventSearchPanel extends JPanel {
    
    private final JLabel inputLabel;
    private final JTextField inputField;
    private final JButton searchButton;
    
    //reference of the control view
    private final EventsControlPanel ecp;
    
    private boolean searchFlag = false;
    
    public EventSearchPanel(final EventsControlPanel ecp) {
        this.ecp = ecp;
        
        Font font = new Font("SansSerif", Font.PLAIN, 16);
        inputLabel = new JLabel("Event Number: ");
        inputField = new JTextField("",8);
        searchButton = new JButton("Search");
        
        inputLabel.setFont(font);
        inputField.setFont(font);
        searchButton.setFont(font);
        
        searchButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String evidString = inputField.getText().trim();
                Integer evid = null;
                try {
                   evid = Integer.valueOf(evidString);
                }
                catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(null, "The input Evid should be an integer value", "Search Error",JOptionPane.WARNING_MESSAGE);
                }
                
                if(evid!=null)
                {
                    searchFlag = false;
                    for(int row = 0; row < ecp.getTable().getRowCount(); row++)
                    {
                        Integer searchedEv = (Integer) ecp.getTable().getValueAt(row,0);
                        if(evid.equals(searchedEv))
                        {
                            ecp.getTable().getSelectionModel().setSelectionInterval(row, row);
                        
                            //scroll to the selection
                            JViewport viewport = (JViewport) ecp.getTable().getParent();
                            Rectangle rect = ecp.getTable().getCellRect(ecp.getTable().getSelectedRow(), 0, true);
                            Rectangle r2 = viewport.getVisibleRect();
                            ecp.getTable().scrollRectToVisible(new Rectangle(rect.x, rect.y, (int) r2.getWidth(), (int) r2.getHeight()));
                            searchFlag = true;
                            break;
                        }
                    }
                }
                
                if(evid != null && searchFlag==false)
                {
                    JOptionPane.showMessageDialog(null, "Cannot find the input Evid, Please check the input!", "Search Error",JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        
        this.setLayout(new FlowLayout());
        this.add(inputLabel);
        this.add(inputField);
        this.add(searchButton);
        
    }
    
}
