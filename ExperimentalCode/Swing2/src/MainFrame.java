

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;


public class MainFrame extends JFrame {

    private JTextArea textArea;
    private JButton button;
    
    public MainFrame() {
        super("Hello World");
    
        setLayout(new BorderLayout());
        textArea = new JTextArea();
        button = new JButton("Click");
        
        add(textArea, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
        
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    
}
 