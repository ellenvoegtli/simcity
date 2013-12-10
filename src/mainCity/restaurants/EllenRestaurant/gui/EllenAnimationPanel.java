package mainCity.restaurants.EllenRestaurant.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    static final int cashierX = 0;
    static final int cashierY = 110;
    
	Map<Integer, Integer> tableX = new TreeMap<Integer, Integer>();
	Map<Integer, Integer> tableY = new TreeMap<Integer, Integer>();

    private List<Gui> guis = new ArrayList<Gui>();
    private List<Gui> personGuis = new ArrayList<Gui>();
    
    private BufferedImage tableImg = null;
    private BufferedImage platingImg = null;
    private BufferedImage grillingImg = null;
    private BufferedImage cashierImg = null;

    public EllenAnimationPanel(CityGui gui) {
    	super(gui);
    	ContactList.getInstance().setEllenRestaurant(restaurant);

    	StringBuilder path = new StringBuilder("imgs/");
        try {
			tableImg = ImageIO.read(new File(path.toString() + "resttable.png"));
			grillingImg = ImageIO.read(new File(path.toString() + "bigstove.png"));
			platingImg = ImageIO.read(new File(path.toString() + "plating.png"));
			cashierImg = ImageIO.read(new File(path.toString() + "marketcashierstation.png"));
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}
        
        
        bufferSize = this.getSize();
    	
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

        
        //Tables
        g.drawImage(tableImg, tableX.get(1), tableY.get(1), null);
        g.drawImage(tableImg, tableX.get(2), tableY.get(2), null);
        g.drawImage(tableImg, tableX.get(3), tableY.get(3), null);
        g.drawImage(tableImg, tableX.get(4), tableY.get(4), null);
        
        //cashier
        g.drawImage(cashierImg, cashierX, cashierY, null);
        
        //cook-grilling
        g.drawImage(grillingImg, grillX, grillY, null);
        g.drawImage(grillingImg, grillX, grillY + 30, null);
        g.drawImage(grillingImg, grillX, grillY + 60, null);
        g.drawImage(grillingImg, grillX, grillY + 90, null);
        g.drawImage(grillingImg, grillX, grillY + 120, null);
        g.drawImage(grillingImg, grillX, grillY + 150, null);
        g.drawImage(grillingImg, grillX, grillY + 180, null);
        
        //cook-plating
        g.drawImage(platingImg, platingX, platingY, null);
        g.drawImage(platingImg, platingX, platingY + 30, null);
        g.drawImage(platingImg, platingX, platingY + 60, null);
        g.drawImage(platingImg, platingX, platingY + 90, null);
        g.drawImage(platingImg, platingX, platingY + 120, null);
        g.drawImage(platingImg, platingX, platingY + 150, null);
        g.drawImage(platingImg, platingX, platingY + 180, null);
        
        
        

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
