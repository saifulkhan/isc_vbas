package mytable;

import java.awt.Dimension;
import javax.swing.JFrame;

public class MyFrame extends JFrame {

    private MyTable myTable;
    
    public MyFrame() {
        super("Test: Swing Table");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(new Dimension(550, 180));
        
        myTable = new MyTable();
        setContentPane(myTable);
        add(myTable);
    }
}