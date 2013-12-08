package mainCity.restaurants.EllenRestaurant.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import mainCity.contactList.ContactList;
import mainCity.gui.*;

public class EllenAnimationPanel extends CityCard implements ActionListener {
	private EllenRestaurantPanel restaurant = new EllenRestaurantPanel(this);
    private static final int WINDOWX = 500;
    private static final int WINDOWY = 370;
    private Image bufferImage;
    private Dimension bufferSize;
    static final int timerStart = 10;

    static final int tableWidth = 50;
    static final int tableHeight = 50;
    static final int originX = 0;
    static final int originY = 0;
    
    //kitchen/cook location variables
    static final int kitchenHeight = 140;
    static final int kitchenWidth = 35;
    static final int grillX = WINDOWX - 30;
    static final int grillY = WINDOWY/2 - 60;
    static final int platingX = WINDOWX - 90;
    static final int platingY = WINDOWY/2 - 60;
    
	Map<Integer, Integer> tableX = new TreeMap<Integer, Integer>();
	Map<Integer, Integer> tableY = new TreeMap<Integer, Integer>();

    private List<Gui> guis = new ArrayList<Gui>();
    private List<Gui> personGuis = new ArrayList<Gui>();

    public EllenAnimationPanel(CityGui gui) {
    	super(gui);
    	ContactList.getInstance().setEllenRestaurant(restaurant);
    	 tableX.put(1, 200);
         tableY.put(1, 150);
         
         tableX.put(2, 300);
         tableY.put(2, 150);
         
         tableX.put(3, 200);
         tableY.put(3, 250);
         
         tableX.put(4, 300);
         tableY.put(4, 250);
    	
    	
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(timerStart, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(originX, originY, WINDOWX, WINDOWY);

        Color purple = new Color(147, 112, 219);
        //Here is the table
        g2.setColor(purple);
        g2.fillRect(tableX.get(1), tableY.get(1), tableWidth, tableHeight);

        
        g2.setColor(purple);
        g2.fillRect(tableX.get(2), tableY.get(2), tableWidth, tableHeight);
        
        g2.setColor(purple);
        g2.fillRect(tableX.get(3), tableY.get(3), tableWidth, tableHeight);
        
        
        g2.setColor(purple);
        g2.fillRect(tableX.get(4), tableY.get(4), tableWidth, tableHeight);
        
        //cashier
        Color grun = new Color(46, 139, 87);
        g2.setColor(grun);
        g2.fillRect(10, 100, 25, 50);
        
        //cook - grill
        Color blue = new Color(135, 206, 250);
        g2.setColor(blue);
        g2.fillRect(grillX, grillY, kitchenWidth, kitchenHeight);
        
        //cook - plating area
        g2.setColor(Color.YELLOW);
        g2.fillRect(platingX, platingY, kitchenWidth, kitchenHeight);
        
        

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
    	for(Gui gui : personGuis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }
    }

    public void addGui(CustomerGui gui) {
    	personGuis.add(gui);
    }
    /*
    public void addGui(HostGui gui) {
        guis.add(gui);
    }
    */
    public void addGui(WaiterGui gui){
    	personGuis.add(gui);
    }
    public void addGui(KitchenGui gui){
    	personGuis.add(gui);
    }
    @Override
    public void clearPeople() {
    	personGuis.clear();
    }
}
