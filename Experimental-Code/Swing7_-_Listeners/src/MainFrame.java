import java.awt.BorderLayout;
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
        
        toolBar = new ToolBar(); 
        //toolBar.setTextPanel(textPanel);
        
        toolBar.setStringListener(new StringListener() {

            @Override
            public void stringEmitted(String string) {
                //System.out.println(string);
                textPanel.appendText(string);
                textPanel.appendText("\n");
            }
        });
            
        add(toolBar, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);
        
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
}
 