
import java.awt.Dimension;
import javax.swing.JPanel;


public class FormPanel extends JPanel {

    public FormPanel() {
        Dimension dim = getPreferredSize();
        System.out.println("Dimension= " + dim); 
        dim.width = 250;
        setPreferredSize(dim);
    } 
}
