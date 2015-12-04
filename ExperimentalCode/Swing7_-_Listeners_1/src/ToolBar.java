
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ToolBar extends JPanel implements ActionListener  {

    private JButton helloButton;
    private JButton goodbyeButton;
    
    
    public ToolBar() {
        helloButton = new JButton("hello");
        goodbyeButton = new JButton("goodbye");
        
        // 1. 
        helloButton.addActionListener(this);
        goodbyeButton.addActionListener(this);
        
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(helloButton);
        add(goodbyeButton);
    }

    /*public void setTextPanel(TextPanel textPanel) {
        this.textPanel = textPanel;
    }*/
    
    private StringListener stringListener;
    public void setStringListener(StringListener listener) {
        this.stringListener = listener; 
    }
     
    // 2.
    @Override
    public void actionPerformed(ActionEvent e) {
         JButton clicked =  (JButton) e.getSource(); // retrieves actual sourcse of the event.
         
         if (clicked == helloButton) {
             if(stringListener != null) {
                 stringListener.stringEmitted("hello");
             }
         } else if (clicked == goodbyeButton) {
            if(stringListener != null) {
                 stringListener.stringEmitted("goodbye");
             }
        }
    }
}