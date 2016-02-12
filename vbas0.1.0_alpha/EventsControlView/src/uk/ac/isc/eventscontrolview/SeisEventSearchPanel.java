package uk.ac.isc.eventscontrolview;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import uk.ac.isc.seisdata.Command;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.SeisDataDAO;
import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;


/**
 *
 * A search panel for searching event
 */
public class SeisEventSearchPanel extends JPanel {

    private final JLabel label_input;
    private final JTextField text_search;
    private final JButton button_search;

    private final JButton button_banish;
    private final JButton button_unbanish;
    private final JButton button_done;

    private final Command formulatedCommand = Global.getFormulatedCommand();

    // reference of the control view
    private final JTable table;

    private boolean searchFlag = false;

    public SeisEventSearchPanel(final JTable eventsTable) {
        this.table = eventsTable;

        Font font = new Font("Sans-serif", Font.PLAIN, 14);
        label_input = new JLabel("Event Number: ");
        text_search = new JTextField("", 10);
        button_search = new JButton("Search");
        button_search.setBackground(new Color(45, 137, 239));
        button_search.setForeground(new Color(255, 255, 255));

        label_input.setFont(font);
        text_search.setFont(font);
        button_search.setFont(font);

        button_search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onButtonSearchActionPerformed(e);
            }
        });

        // banish and Un-banish button
        button_banish = new JButton("Banish");
        button_banish.setBackground(new Color(45, 137, 239));
        button_banish.setForeground(new Color(255, 255, 255));
        button_banish.setFont(font);
        button_banish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                onButtonBanishActionPerformed(ae);
            }
        });

        button_unbanish = new JButton("Unbanish");
        button_unbanish.setBackground(new Color(45, 137, 239));
        button_unbanish.setForeground(new Color(255, 255, 255));
        button_unbanish.setFont(font);
        button_unbanish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                onButtonUnbanishActionPerformed(ae);
            }
        });

        button_done = new JButton("Done");
        button_done.setBackground(new Color(45, 137, 239));
        button_done.setForeground(new Color(255, 255, 255));
        button_done.setFont(font);
        button_done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onButtonDoneActionPerformed(e);
            }
        });

        this.setLayout(new FlowLayout());
        this.add(label_input);
        this.add(text_search);
        this.add(button_search);
        this.add(button_banish);
        this.add(button_unbanish);
        this.add(button_done);
    }

    public void onButtonSearchActionPerformed(ActionEvent e) {

        String evidString = text_search.getText().trim();
        Integer evid = null;
        try {
            evid = Integer.valueOf(evidString);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null,
                    "The input Evid should be an integer value",
                    "Search Error",
                    JOptionPane.WARNING_MESSAGE);
        }

        if (evid != null) {
            searchFlag = false;

            for (int row = 0; row < table.getRowCount(); row++) {
                Integer searchedEv = (Integer) table.getValueAt(row, 0);
                if (evid.equals(searchedEv)) {
                    table.getSelectionModel().setSelectionInterval(row, row);

                    //scroll to the selection
                    JViewport viewport = (JViewport) table.getParent();
                    Rectangle rect = table.getCellRect(table.getSelectedRow(), 0, true);
                    Rectangle r2 = viewport.getVisibleRect();
                    table.scrollRectToVisible(new Rectangle(rect.x, rect.y, (int) r2.getWidth(),
                            (int) r2.getHeight()));
                    searchFlag = true;
                    break;
                }
            }
        }

        if (evid != null && searchFlag == false) {
            JOptionPane.showMessageDialog(null,
                    "Cannot find the input Evid, Please check the input!",
                    "Search Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onButtonBanishActionPerformed(ActionEvent ae) {

        System.out.print(Global.debugAt());
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Select an event to banish.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int seisEventId = (Integer) table.getValueAt(row, 0);

        JSONArray jCommandArray = new JSONArray();
        JSONArray jFunctionArray = new JSONArray();
        
        JSONObject jCommandObj = new JSONObject();
        
        jCommandObj.put("commandType", "seiseventbanish");
        jCommandObj.put("dataType", "seisevent");
        jCommandObj.put("id", seisEventId);
        jCommandArray.add(jCommandObj);

        JSONObject jFunctionObj = new JSONObject();
        jFunctionObj.put("function", "banish ( " + seisEventId + " INTEGER )");
        jFunctionArray.add(jFunctionObj);
        
        
        if (jCommandArray.size() > 0) {
            String commandStr = jCommandArray.toString();
            String functionStr = jFunctionArray.toString();
            
            boolean ret = SeisDataDAO.updateCommandTable(seisEventId, "seiseventbanish", commandStr, functionStr);
            if (ret) {
                // success
                System.out.println(Global.debugAt() + " \ncommandStr= " + commandStr 
                        + "\nfunctionStr= " + functionStr 
                        + "\nFired: 'SeisEvent Banish' comamnd.");
                formulatedCommand.fireSeisDataChanged();  // Notify the Command table to update from the database.

            } else {
                JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onButtonUnbanishActionPerformed(ActionEvent ae) {
        JOptionPane.showMessageDialog(null, "Sorry, this feature will be implemented in future release.",
                "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public void onButtonDoneActionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null, "Sorry, this feature will be implemented in future release.",
                "Warning", JOptionPane.WARNING_MESSAGE);
    }
}
