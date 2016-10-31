package uk.ac.isc.agencyrecview;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.openide.util.Exceptions;

/**
 * just a cell renderer, need be revisited
 *
 *  
 */
class PictureRenderer extends DefaultTableCellRenderer {

    private BufferedImage bigImg = null;

    private BufferedImage middleImg = null;

    private BufferedImage smallImg = null;

    private ImageIcon icon = null;

    PictureRenderer() {
        URL url1 = getClass().getClassLoader().getResource("main/resources/big.png");
        URL url2 = getClass().getClassLoader().getResource("main/resources/middle.png");
        URL url3 = getClass().getClassLoader().getResource("main/resources/small.png");

        try {
            bigImg = ImageIO.read(url1);
            middleImg = ImageIO.read(url2);
            smallImg = ImageIO.read(url3);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        //Cells are by default rendered as a JLabel.
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        //Get the status for the current row.
        //DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        if ((Double) value < 33) {
            icon = new ImageIcon(smallImg);
            setHorizontalAlignment(JLabel.CENTER);
            setVerticalAlignment(JLabel.CENTER);
            l.setIcon(icon);
            l.setText("");
        } else if ((Double) value < 67) {
            icon = new ImageIcon(middleImg);
            setHorizontalAlignment(JLabel.CENTER);
            setVerticalAlignment(JLabel.CENTER);
            l.setIcon(icon);
            l.setText("");
        } else {
            icon = new ImageIcon(bigImg);
            setHorizontalAlignment(JLabel.CENTER);
            setVerticalAlignment(JLabel.CENTER);
            l.setIcon(icon);
            l.setText("");
        }

        //Return the JLabel which renders the cell.
        return l;
    }

}
