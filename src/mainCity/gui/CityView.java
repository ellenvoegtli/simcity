package mainCity.gui;

import housing.gui.HomeAnimationPanel;
import housing.gui.HomePanel;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.JPanel;

import mainCity.restaurants.enaRestaurant.gui.EnaAnimationPanel;
import mainCity.restaurants.marcusRestaurant.gui.MarcusAnimationPanel;
import mainCity.restaurants.restaurant_zhangdt.gui.DavidAnimationPanel;

public class CityView extends JPanel implements MouseListener, ActionListener {

	HashMap<String, CityCard> cards = new HashMap<String, CityCard>();
	CityGui city;
	public static final int VIEW_WIDTH = 500, VIEW_HEIGHT = 500;
	CardLayout layout;
	
	public CityView(CityGui city) {
		
		this.setPreferredSize(new Dimension(VIEW_WIDTH, VIEW_HEIGHT));
		this.setVisible(true);
		addMouseListener(this);
		this.city = city;
		
		for(int i=0; i<7; i++){ 
			cards.put("tophouse"+i, new CityCard(city, Color.red));
		}
		for(int i=0; i<7; i++){ 
			cards.put("bothouse"+i, new CityCard(city, Color.cyan));
		}
		for(int i=2; i<5; i++){
			cards.put("rest"+i, new CityCard(city, Color.DARK_GRAY));
		}
		cards.put("bank", new CityCard(city, Color.green));
		cards.put("market", new CityCard(city, Color.orange));
		
		cards.put("marcusRestaurant", new MarcusAnimationPanel(city));
		cards.put("davidRestaurant", new DavidAnimationPanel(city));
		cards.put("enarestaurant",  new EnaAnimationPanel(city));
		
		for(int i=1; i<8; i++)
		{
					cards.put("house"+ i, new HomeAnimationPanel(city));

		}
		for(int j=0; j<8; j++)
		{
			cards.put("apartment" +j, new HomeAnimationPanel(city));

		}
		
		layout = new CardLayout();
		this.setLayout(layout);
		for (String key:cards.keySet()) {
			this.add(cards.get(key), key);
		}

		layout.show(this, "null");
	}
	
	public boolean addView(CityCard panel, String key) {
		if (cards.containsKey(key))
			return false;
		cards.put(key, panel);
		this.add(cards.get(key), key);
		return true;
	}
	
	public void setView(String key) {
		if (cards.containsKey(key)) {
			layout.show(this, key);
			//city.info.setText(key);
		}
	}
	
	public HashMap<String, CityCard> getCards() {
		return cards;
	}
	
	public void actionPerformed(ActionEvent arg0) {

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
