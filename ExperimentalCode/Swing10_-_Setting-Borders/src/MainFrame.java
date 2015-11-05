
import java.awt.BorderLayout;
import javax.swing.JFrame;

public class MainFrame extends JFrame implements StringListener {

    private TextPanel textPanel;
    private ToolBar toolBar;
    private FormPanel formPanel;

    public MainFrame() {
        super("Hello World");

        setLayout(new BorderLayout());
        textPanel = new TextPanel();
        formPanel = new FormPanel();

        toolBar = new ToolBar();

        toolBar.setStringListener(this);

        add(toolBar, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);
        add(formPanel, BorderLayout.WEST);

        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void stringEmitted(String string) {
        //System.out.println(string);
        textPanel.appendText(string);
        textPanel.appendText("\n");
    }

}
