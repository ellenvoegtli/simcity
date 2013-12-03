package mainCity.restaurants.marcusRestaurant.gui;

import javax.swing.*;

import mainCity.contactList.ContactList;
import mainCity.gui.CityCard;
import mainCity.gui.CityGui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class MarcusAnimationPanel extends CityCard implements ActionListener {
	private MarcusRestaurantPanel restaurant = new MarcusRestaurantPanel(this);
    private final int WINDOWX = 500;
    private final int WINDOWY = 410;
    private static final int x = 100;
    private static final int y = 260;
    private static final int w = 50;
    private static final int h = 50;
    private static final int tableCount = 4;
   
    private Image bufferImage;
    private Dimension bufferSize;

    private List<Gui> guis = new ArrayList<Gui>();

    public MarcusAnimationPanel(CityGui gui) {
    	super(gui);
    	ContactList.getInstance().setMarcusRestaurant(restaurant);
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
        setLayout(null);
        
    	Timer timer = new Timer(30, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        Graphics2D kitchen = (Graphics2D)g;
        
        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
	    g2.fillRect(0, 0, WINDOWX, WINDOWY );
	
	    for(int i = 0; i < tableCount; i++) {
	     	g2.setColor(Color.ORANGE);
	        g2.fillRect(x+100*i, y, w, h);
	    }
	        
	    kitchen.setColor(Color.GRAY);
	    kitchen.fillRect(160, 0, 95, 45);
        kitchen.setColor(Color.GRAY);
	    kitchen.fillRect(255, 15, 20, 30);
        
	    for(int i = 0; i < tableCount; i++) {
	    	kitchen.setColor(Color.WHITE);
	        kitchen.fillRect(170 + 20*i, 1, 15, 15);
	    }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }
        
	    for(Gui gui : guis) {
	        if (gui.isPresent()) {
	            gui.draw(g2);
	        }
	    }
    }

    public void backgroundUpdate() {
    	for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }
    }
    
    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(WaiterGui gui) {
        guis.add(gui);
    }
    
    public void addGui(CookGui gui) {
    	guis.add(gui);
    }
}
