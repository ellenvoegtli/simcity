package mainCity.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class CityCard extends JPanel {

	public static final int CARD_WIDTH = 500, CARD_HEIGHT = 500;

	public CityCard(CityGui city) {
		super();
		this.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
		this.setVisible(true);
		//addMouseListener(this);
		//background = Color.green;
	}
	
	public CityCard(CityGui city, Color c) {
		super();
		this.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
		this.setVisible(true);
		this.setBackground(c);
	}


	public void mouseClicked(MouseEvent e) {
		
	}

	
	public void mouseEntered(MouseEvent e) {
		
	}

	
	public void mouseExited(MouseEvent e) {
		
	}

	
	public void mousePressed(MouseEvent e) {
		
	}

	
	public void mouseReleased(MouseEvent e) {
		
	}


	

}
