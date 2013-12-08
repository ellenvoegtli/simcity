package mainCity.restaurants.restaurant_zhangdt.gui;

import javax.swing.*;

import mainCity.contactList.ContactList;
import mainCity.gui.CityCard;
import mainCity.gui.CityGui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class DavidAnimationPanel extends CityCard implements ActionListener {
    
    private DavidRestaurantPanel restaurant = new DavidRestaurantPanel(this); 

    private final int WINDOWX = 500;
    private final int WINDOWY = 400;
    private final int TableX = 50;
    private final int TableY = 50;
    
    private final int TableX1 = 200;
    private final int TableY1 = 250;
    
    private final int TableX2 = 300;
    private final int TableY2 = 175;
    
    private final int TableX3 = 200;
    private final int TableY3 = 100;
    
    private Image bufferImage;
    private Dimension bufferSize;

    private List<Gui> guis = new ArrayList<Gui>();
    private List<Gui> personGuis = new ArrayList<Gui>();

    public DavidAnimationPanel(CityGui gui) {
    	super(gui);
    	ContactList.getInstance().setDavidRestaurant(restaurant);
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(20, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );

        //Here is the table
        g2.setColor(Color.ORANGE);
        g2.fillRect(TableX1, TableY1, TableX, TableY);//200 and 250 need to be TABLE params
        g2.setColor(Color.BLUE);
        g2.fillRect(TableX2, TableY2, TableX, TableY);
        g2.setColor(Color.RED);
        g2.fillRect(TableX3, TableY3, TableX, TableY);
        
        g2.setColor(Color.GRAY); 
        g2.fillRect(40, 185, 20, 50);
        
        g2.setColor(Color.BLACK); 
        g2.fillRect(0, 235, 60, 20);
        
        g2.setColor(Color.GRAY); 
        g2.fillRect(0, 150, 20, 20);


        for(Gui gui : personGuis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
        
        for(Gui gui : personGuis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }
    
    public void backgroundUpdate() { 
    	for (Gui gui : personGuis) { 
    		if (gui.isPresent()) { 
    			gui.updatePosition(); 
    		}
    	}
    }

    public void addGui(CustomerGui gui) {
    	personGuis.add(gui);
    }

    public void addGui(WaiterGui gui) {
    	personGuis.add(gui);
    }
    
    public void addGui(CookGui gui) { 
    	personGuis.add(gui);
    }
    
    @Override
    public void clearPeople() {
    	personGuis.clear();
    }
}
