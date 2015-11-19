package mytable;

import javax.swing.SwingUtilities;

public class App {
    
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
         SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MyFrame();
            }
        });
    }
}
