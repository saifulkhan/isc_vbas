package uk.ac.isc.stationazimuthview;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Control panel for the station azimuth view, it set different scale level of
 * the map and enable/disable the text of presenting the number and percentage
 * of phase from directions
 *
 * @author hui
 */
public class StationAzimuthControlPanel extends JPanel {

    private final ButtonGroup bg = new ButtonGroup();

    private final JRadioButton scaleBt1 = new JRadioButton("180 Deg.");

    private final JRadioButton scaleBt2 = new JRadioButton("110 Deg.");

    private final JRadioButton scaleBt3 = new JRadioButton("30 Deg.");

    private final JRadioButton scaleBt4 = new JRadioButton("Fit Station");

    private final JCheckBox textCheckbox = new JCheckBox("Show Text");

    //private StationAzimuthView sav;
    //bad practice, will move to ioc later
    StationAzimuthControlPanel(final StationAzimuthView sav) {
        //sav = savs;
        this.setLayout(new FlowLayout());

        scaleBt1.setSelected(true);
        textCheckbox.setSelected(true);

        bg.add(scaleBt1);
        bg.add(scaleBt2);
        bg.add(scaleBt3);
        bg.add(scaleBt4);

        this.add(scaleBt1);
        this.add(scaleBt2);
        this.add(scaleBt3);
        this.add(scaleBt4);

        this.add(textCheckbox);

        scaleBt1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sav.setMapDegree(180);
            }

        });

        scaleBt2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sav.setMapDegree(110);
            }

        });

        scaleBt3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sav.setMapDegree(30);
            }

        });

        scaleBt4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //if(sav.getFarthestDegree()>30)
                sav.setMapDegree(sav.getFarthestDegree());
                //else
                //    sav.setMapDegree(30);
            }

        });

        textCheckbox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                sav.setTextVisible(textCheckbox.isSelected());
            }

        });
    }

}
