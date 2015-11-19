import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;


public class MainFrame extends JFrame {

    private TextPanel textPanel;
    private JButton button;
    private ToolBar toolBar;
    
    public MainFrame() {
        super("Hello World");
    
        setLayout(new BorderLayout());
        textPanel = new TextPanel();
        button = new JButton("Click");
        toolBar = new ToolBar(); 
        
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                textPanel.appendText("Hello \n");
            }
        });
        
        add(toolBar, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
        
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
}
 