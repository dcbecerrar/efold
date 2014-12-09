package plots;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel{ 
	 
    private BufferedImage image; 
 
    public ImagePanel(BufferedImage img) { 
       image = img;
    } 
 
    public void paintComponent(Graphics g) { 
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    } 
 
} 
