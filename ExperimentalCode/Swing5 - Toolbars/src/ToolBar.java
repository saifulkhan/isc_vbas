
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ToolBar extends JPanel {

    private JButton helloButton;
    private JButton goodbyeButton;
    
    public ToolBar() {
        helloButton = new JButton("hello");
        goodbyeButton = new JButton("goodbye");
        
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(helloButton);
        add(goodbyeButton);
    }
}
