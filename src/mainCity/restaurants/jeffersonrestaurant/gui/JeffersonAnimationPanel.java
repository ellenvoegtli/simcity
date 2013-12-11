package mainCity.restaurants.jeffersonrestaurant.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import mainCity.contactList.ContactList;
import mainCity.gui.CityCard;
import mainCity.gui.CityGui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class JeffersonAnimationPanel extends CityCard implements ActionListener {

    private final int WINDOWX = 650;
    private final int WINDOWY = 550;
    private Image bufferImage;
    private Dimension bufferSize;
    static final int  X = 200;
    static final int  Y = 300;
    static final int width = 50;
    static final int height = 50;
    private BufferedImage resttableImg = null;
    private BufferedImage bigstoveImg = null;
    private BufferedImage platingImg = null;
    
    private JeffersonRestaurantPanel JRestPanel = new JeffersonRestaurantPanel(this);
    

    private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());
    private List<Gui> personGuis = Collections.synchronizedList(new ArrayList<Gui>());

    public JeffersonAnimationPanel(CityGui gui) {
    	super(gui);
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        StringBuilder path = new StringBuilder("imgs/");
        try {
			resttableImg = ImageIO.read(new File(path.toString() + "resttable.png"));
			bigstoveImg = ImageIO.read(new File(path.toString() + "bigstove.png"));
			platingImg = ImageIO.read(new File(path.toString() + "plating.png"));
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}
        
        
        bufferSize = this.getSize();
        ContactList.getInstance().setJeffersonRestaurant(JRestPanel);
 
    	Timer timer = new Timer(30, this );
    	timer.start();
    }

    
    public void backgroundUpdate() {
    	synchronized(guis){
	    	for(Gui guit : personGuis) {
	            if (guit.isPresent()) {
	                guit.updatePosition();
	            }
	        }
    	}
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
        //g2.fillRect(X, Y, width, height);//200 and 250 need to be table params
        g.drawImage(resttableImg,X, Y,null);
        
        //g2.fillRect(X+100, Y, width, height);//200 and 250 need to be table params
        g.drawImage(resttableImg,X+100, Y,null);
        //g2.fillRect(X+200, Y, width, height);//200 and 250 need to be table params
        g.drawImage(resttableImg,X+200, Y,null);
        //Draw customer waiting area
        
        g2.fillRect( 0, 0, 2*width, 3*width );
        
        g2.setColor(Color.WHITE);
        g2.fillRect(230, 180, 200, 60);
        
        g2.setColor(Color.cyan);
        
        
        g.drawImage(platingImg,240, 200,null);
        //g2.fillRect(240, 200,30,30);
        g2.drawString("Plating", 240, 200);
        
        g2.setColor(Color.BLUE);
        //g2.fillRect(400, 200,30,30);
        g.drawImage(bigstoveImg,400, 200,null);
        g2.drawString("Cooking", 400, 200);
        
        synchronized(personGuis){
	        for(Gui gui : personGuis) {
	            if (gui.isPresent()) {
	                gui.updatePosition();
	            }
	        }
        }
        synchronized(guis){
	        for(Gui gui : guis) {
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
	        }
        } 
        
        synchronized(personGuis){
	        for(Gui gui : personGuis) {
	            if (gui.isPresent()) {
	                gui.draw(g2);
	            }
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
