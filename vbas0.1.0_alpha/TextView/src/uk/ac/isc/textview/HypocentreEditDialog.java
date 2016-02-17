package uk.ac.isc.textview;

import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;
import com.orsoncharts.util.json.parser.JSONParser;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import uk.ac.isc.seisdata.Command;
import uk.ac.isc.seisdata.Global;
import uk.ac.isc.seisdata.Hypocentre;
import uk.ac.isc.seisdata.SeisDataDAO;
import uk.ac.isc.seisdata.SeisEvent;

public class HypocentreEditDialog extends JDialog {

    private JButton button_cancel;
    private JButton button_ok;
    private JLabel jLabel1;
    private JLabel jLabel13;
    private JLabel jLabel14;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JScrollPane jScrollPane1;
    private JTextArea textArea_reason;
    private JLabel label_coord;
    private JLabel label_depth;
    private JLabel label_evid;
    private JLabel label_hypid;
    private JLabel label_prime;
    private JLabel label_time;
    private JTextArea text_depth;
    private JTextField text_lat;
    private JTextField text_lon;
    private JTextField text_time;

    private final Command formulatedCommand = Global.getFormulatedCommand();
    private final SeisEvent selectedSeisEvent = Global.getSelectedSeisEvent();
    private final Hypocentre selectedHypocentre = Global.getSelectedHypocentre();

    public HypocentreEditDialog() {

        setTitle("Edit Hypocentre");
        setModal(true);
        layoutComponents();

    }

    private void button_okActionPerformed(ActionEvent evt) {

        JSONArray jCommandArray = new JSONArray();
        // Add all the changed "attributes" in the array.
        JSONArray jAttrArray = new JSONArray();
        JSONArray jFunctionArray = new JSONArray();

        JSONObject jCommandObj = new JSONObject();
        jCommandObj.put("commandType", "hypocentreedit");
        jCommandObj.put("dataType", "hypocentre");
        jCommandObj.put("id", selectedHypocentre.getHypid());

        /*
         * Depth 
         */
        Integer depth = null;
        try {
            depth = Integer.parseInt(text_depth.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Incorrect depth format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (depth != selectedHypocentre.getDepth()) {
            JSONObject jAttrObj = new JSONObject();
            jAttrObj.put("name", "depth");
            jAttrObj.put("oldValue", selectedHypocentre.getDepth());
            jAttrObj.put("newvalue", depth);
            jAttrArray.add(jAttrObj);

            JSONObject jFunctionObj = new JSONObject();
            jFunctionObj.put("commandType", "hypocentreedit");
            jFunctionObj.put("function", "hypocentreedit ( "
                    + selectedHypocentre.getHypid() + ", depth, " + Integer.parseInt(text_depth.getText()) + " )");
            jFunctionArray.add(jFunctionObj);
        }

        /*
         * Date 
         */
        Date date = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = df.parse(text_time.getText());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Incorrect date time.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (date.compareTo(selectedHypocentre.getOrigTime()) != 0) {  // same date time

            JSONObject jAttrObj = new JSONObject();
            jAttrObj.put("name", "time");
            jAttrObj.put("oldValue", selectedHypocentre.getOrigTime());
            jAttrObj.put("newvalue", date);
            jAttrArray.add(jAttrObj);

            JSONObject jFunctionObj = new JSONObject();
            jFunctionObj.put("commandType", "hypocentreedit");
            jFunctionObj.put("function", "hypocentreedit ( "
                    + selectedHypocentre.getHypid() + ", time, '" + text_time.getText() + "' )");
            jFunctionArray.add(jFunctionObj);
        }

        /*
         * Latitude
         */
        if (Double.parseDouble(text_lat.getText()) != selectedHypocentre.getLat()) {
            JSONObject jAttrObj = new JSONObject();
            jAttrObj.put("name", "lat");
            jAttrObj.put("oldValue", selectedHypocentre.getLat());
            jAttrObj.put("newvalue", Double.parseDouble(text_lat.getText()));
            jAttrArray.add(jAttrObj);

            JSONObject jFunctionObj = new JSONObject();
            jFunctionObj.put("commandType", "hypocentreedit");
            jFunctionObj.put("function", "hypocentreedit ( "
                    + selectedHypocentre.getHypid() + ", lat, " + Double.parseDouble(text_lat.getText()) + " )");
            jFunctionArray.add(jFunctionObj);
        }

        /*
         * Longitude 
         */
        if (Double.parseDouble(text_lon.getText()) != selectedHypocentre.getLon()) {
            JSONObject jAttrObj = new JSONObject();
            jAttrObj.put("name", "lon");
            jAttrObj.put("oldValue", selectedHypocentre.getLon());
            jAttrObj.put("newvalue", Double.parseDouble(text_lon.getText()));
            jAttrArray.add(jAttrObj);

            JSONObject jFunctionObj = new JSONObject();
            jFunctionObj.put("commandType", "hypocentreedit");
            jFunctionObj.put("function", "hypocentreedit ( "
                    + selectedHypocentre.getHypid() + ", lon, " + Double.parseDouble(text_lon.getText()) + " )"
            );
            jFunctionArray.add(jFunctionObj);
        }

        if (jAttrArray.size() > 0) {

            /*
             * Reason text description. Include it only if a valid command is formulated.
             */
            if (textArea_reason.getText() == null) {
                JOptionPane.showMessageDialog(null, "Please give a reason.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            } else {
                JSONObject jAttrObj = new JSONObject();
                jAttrObj.put("name", "reason");
                jAttrObj.put("oldValue", "");
                jAttrObj.put("newvalue", textArea_reason.getText());
                jAttrArray.add(jAttrObj);
            }

            jCommandObj.put("attributes", jAttrArray);
            jCommandArray.add(jCommandObj);
        }

        if (jCommandArray.size() > 0) {
            String commandStr = jCommandArray.toString();
            String functionStr = jFunctionArray.toString();
            Global.logDebug(" Fired: 'Edit Hypocentre' comamnd."
                    + "\ncommandStr= " + commandStr
                    + "\nfunctionStr= " + functionStr);

            // Debug JSON
            JSONParser parser = new JSONParser();
            try {
                String s = jFunctionArray.toString();
                Object obj = parser.parse(s);
                JSONArray arr = (JSONArray) obj;
                for (Object o : arr) {
                    JSONObject jObj = (JSONObject) o;
                    Global.logDebug("commandType: " + (String) jObj.get("commandType")
                            + ", function: " + (String) jObj.get("function"));
                }

            } catch (com.orsoncharts.util.json.parser.ParseException pe) {
                System.out.println("position: " + pe.getPosition());
                System.out.println(pe);
            }
            // end Debug

            /*boolean ret = SeisDataDAO.updateCommandTable(selectedSeisEvent.getEvid(), ""
             + "hypocentreedit", commandStr, functionStr);
             if (ret) {
             Global.logDebug(" Fired: 'Edit Hypocentre' comamnd."
             + "\ncommandStr= " + commandStr
             + "\nfunctionStr= " + functionStr);

             formulatedCommand.fireSeisDataChanged();
             this.dispose();
             } else {
             JOptionPane.showMessageDialog(null, "Incorrect Command.", "Error", JOptionPane.ERROR_MESSAGE);
             }*/
        }

    }

    private void button_cancelActionPerformed(ActionEvent evt) {
        this.dispose();
    }

    public void showHypoEditDialog() {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        label_evid.setText(selectedHypocentre.getEvid().toString());
        label_hypid.setText(selectedHypocentre.getHypid().toString());
        label_time.setText(df.format(selectedHypocentre.getOrigTime()));
        label_coord.setText(selectedHypocentre.getLat().toString() + "N " + selectedHypocentre.getLon().toString() + "W");
        label_depth.setText(selectedHypocentre.getDepth().toString());
        label_prime.setText(selectedHypocentre.getIsPrime().toString());

        text_depth.setText(selectedHypocentre.getDepth().toString());
        text_lat.setText(selectedHypocentre.getLat().toString());
        text_lon.setText(selectedHypocentre.getLon().toString());
        text_time.setText(df.format(selectedHypocentre.getOrigTime()));

        textArea_reason.setText(null);

        setVisible(true);
    }

    private void layoutComponents() {
        button_ok = new JButton();
        button_cancel = new JButton();
        jPanel2 = new JPanel();
        jLabel1 = new JLabel();
        label_evid = new JLabel();
        jLabel3 = new JLabel();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        label_time = new JLabel();
        label_depth = new JLabel();
        label_hypid = new JLabel();
        label_coord = new JLabel();
        label_prime = new JLabel();
        jPanel1 = new JPanel();
        jLabel13 = new JLabel();
        jLabel14 = new JLabel();
        text_depth = new JTextArea();
        jLabel2 = new JLabel();
        text_lat = new JTextField();
        text_lon = new JTextField();
        jLabel8 = new JLabel();
        jLabel9 = new JLabel();
        text_time = new JTextField();
        jPanel3 = new JPanel();
        jScrollPane1 = new JScrollPane();
        textArea_reason = new JTextArea();

        button_ok.setBackground(new Color(45, 137, 239));
        button_ok.setForeground(new Color(255, 255, 255));
        button_ok.setText("OK");
        button_ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                button_okActionPerformed(evt);
            }
        });

        button_cancel.setBackground(new java.awt.Color(45, 137, 239));
        button_cancel.setForeground(new java.awt.Color(255, 255, 255));
        button_cancel.setText("Cancel");
        button_cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        jPanel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Original Value"));

        jLabel1.setText("EVID:");
        label_evid.setText("evid");
        jLabel3.setText("HYPID:");
        jLabel4.setText("PRIME:");
        jLabel5.setText("DEPTH:");
        jLabel6.setText("TIME:");
        jLabel7.setText("COORD:");
        label_time.setText("time");
        label_depth.setText("depth");
        label_hypid.setText("hypid");
        label_coord.setText("coord");
        label_prime.setText("prime");

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(18, 18, 18)
                                        .addComponent(label_time))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(18, 18, 18)
                                        .addComponent(label_evid))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_depth)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(label_hypid))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_coord))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(18, 18, 18)
                                        .addComponent(label_prime)))
                        .addGap(96, 96, 96))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(label_evid)
                                .addComponent(jLabel3)
                                .addComponent(label_hypid))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6)
                                .addComponent(jLabel7)
                                .addComponent(label_time)
                                .addComponent(label_coord))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(jLabel4)
                                .addComponent(label_depth)
                                .addComponent(label_prime))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Edit"));

        jLabel13.setText("DEPTH:");
        jLabel14.setText("TIME:");

        //text_depth.setFormatterFactory(new text.DefaultFormatterFactory(new text.NumberFormatter()));
        text_depth.setToolTipText("");
        jLabel2.setText("COORD:");
        jLabel8.setText("N");
        jLabel9.setText("W");

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel13)
                                .addComponent(jLabel14))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(text_depth, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
                                        .addGap(41, 41, 41)
                                        .addComponent(jLabel2)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(text_lat, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel8)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(text_lon, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel9))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(text_time, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel13)
                                .addComponent(text_depth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)
                                .addComponent(text_lat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(text_lon, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8)
                                .addComponent(jLabel9))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel14)
                                .addComponent(text_time, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
        );

        jPanel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Reason"));

        textArea_reason.setColumns(20);
        textArea_reason.setRows(5);
        jScrollPane1.setViewportView(textArea_reason);

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1)
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jPanel3, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_ok, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_cancel)
                        .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(button_ok)
                                .addComponent(button_cancel))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.getAccessibleContext().setAccessibleName("Reason");

        pack();
    }
}
