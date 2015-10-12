/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.isc;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import org.jdesktop.swingx.JXDatePicker;

/**
 *
 * @author hui
 */
public class DatePickerTestFrame {
    
    public static void main (String [] args)
    {
        // Establish a look and feel for this application’s GUI.

        setLookAndFeel ();

        // Create a frame window whose GUI presents the date picker and provides
        // a list of supported date formats.

        final JFrame frame = new JFrame ("Date Picker Month View Demo #1");
        frame.getContentPane ().setLayout (new GridLayout (2, 1));

        // Tell application to automatically exit when the user selects the Close
        // menu item from the frame window’s system menu.

        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

        // Create GUI: date picker at the top and a combo box of date formats --
        // that the date picker supports for entering dates via the editor -- at
        // the bottom.

        JPanel panel = new JPanel ();
        panel.add (new JLabel ("Enter date (if desired) and click button"));
        final JXDatePicker datePicker;
        datePicker = new JXDatePicker ();
        ActionListener al = new ActionListener ()
        {
             public void actionPerformed (ActionEvent e)
            {
                 System.out.println (datePicker.getDate ());
            }
        };
        datePicker.addActionListener (al);
        panel.add (datePicker);
        frame.getContentPane ().add (panel);

        panel = new JPanel ();
        panel.setLayout (new FlowLayout (FlowLayout.LEFT));
        panel.add (new JLabel ("Supported date formats"));
        DateFormat [] dfs = datePicker.getFormats ();
        String [] fmts = new String [dfs.length];
        for (int i = 0; i < dfs.length; i++)
            fmts [i] = (dfs [i] instanceof SimpleDateFormat)
                ? ((SimpleDateFormat) dfs [i]).toPattern ()
                 : dfs [i].toString ();
        panel.add (new JComboBox (fmts));
        frame.getContentPane ().add (panel);

        // Size frame window to fit the preferred size and layouts of its
        // components.

        frame.pack ();

        // Display GUI and start the AWT’s event-dispatching thread.

        frame.setVisible (true);
 }

 static void setLookAndFeel ()
 {
  try
  {
   // Return the name of the LookAndFeel class that implements the
   // native OS look and feel. If there is no such look and feel, return
   // the name of the default cross platform LookAndFeel class.

   String slafcn = UIManager.getSystemLookAndFeelClassName ();

   // Set the current look and feel to the look and feel identified by
   // the LookAndFeel class name.

   UIManager.setLookAndFeel (slafcn);
  }
  catch(Exception e)
  {
  }
 }
}
