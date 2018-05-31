import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageDisplay extends JFrame implements KeyListener {
	JFrame j;
	
	ImageDisplay(BufferedImage img){
		j = new JFrame();
		j.setSize(img.getWidth(), img.getHeight());
		// CHANGED: from height, width to width, height
		j.setDefaultCloseOperation(EXIT_ON_CLOSE);
		JPanel pane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, null);
            }
        };
        j.add(pane);
		addKeyListener(this);
	    setFocusable(true);
	    j.setVisible(true);		
	}
	
	@Override
	public void keyPressed(KeyEvent key) {
		if(key.getKeyCode() == KeyEvent.VK_ESCAPE)
			this.j.getDefaultCloseOperation();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
