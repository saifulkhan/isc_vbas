
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ToolBar extends JPanel implements ActionListener  {

    private JButton helloButton;
    private JButton goodbyeButton;
    private TextPanel textPanel;
    
    public ToolBar() {
        helloButton = new JButton("hello");
        goodbyeButton = new JButton("goodbye");
        
        helloButton.addActionListener(this);
        goodbyeButton.addActionListener(this);
        
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(helloButton);
        add(goodbyeButton);
        
    }

    public void setTextPanel(TextPanel textPanel) {
        this.textPanel = textPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
         JButton clicked =  (JButton) e.getSource(); // retrieves actual sourcse of the event.
         
         if (clicked == helloButton) {
               textPanel.appendText("hello \n");
         } else if (clicked == goodbyeButton) {
            textPanel.appendText("good bye \n");
        }
    }
}