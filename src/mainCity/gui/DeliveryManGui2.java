package mainCity.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import mainCity.market1.interfaces.DeliveryMan1;
import mainCity.market2.gui.*;
import mainCity.market2.interfaces.*;

public class DeliveryManGui2 implements Gui, DeliveryManGuiInterface {
	
	public DeliveryMan1 agent = null;
    CityGui gui;
    private boolean isPresent;

    private int xPos = 655, yPos = 135;//default delivery position
    private int xDestination = 655, yDestination = 135;//default start position
    static final int waiterWidth = 20;
    static final int waiterHeight = 20;
    public int homeX = 655, homeY = 135;
    private BufferedImage truckImg = null;

	Map<String, Integer> restaurantX = new TreeMap<String, Integer>();
	Map<String, Integer> restaurantY = new TreeMap<String, Integer>();
	
	private boolean atDestination = true;
	private boolean isDeliveringFood = false;
	private BufferedImage myImg = null;
	

    public DeliveryManGui2(DeliveryMan1 agent) {
    	StringBuilder path = new StringBuilder("imgs/");
		try {
			truckImg = ImageIO.read(new File(path.toString() + "truck.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
        this.agent = agent;
        
        restaurantX.put("EllenRestaurant", 105);
    	restaurantY.put("EllenRestaurant", 280);
         
    	restaurantX.put("enaRestaurant", 347);
    	restaurantY.put("enaRestaurant", 180);
         
        restaurantX.put("marcusRestaurant", 105);
        restaurantY.put("marcusRestaurant", 180);
         
        restaurantX.put("davidRestaurant", 585);
        restaurantY.put("davidRestaurant", 230);
        
        restaurantX.put("jeffersonrestaurant", 347);
        restaurantY.put("jeffersonrestaurant", 280);
    }

    public void updatePosition() {
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;

        if (xPos == xDestination && yPos == yDestination
        		&& (xDestination == restaurantX.get("EllenRestaurant")) 
        		&& (yDestination == restaurantY.get("EllenRestaurant")) && !atDestination) {
        	agent.msgAtDestination();
        	atDestination = true;
        }

        else if (xPos == xDestination && yPos == yDestination
        		&& (xDestination == restaurantX.get("enaRestaurant")) 
        		&& (yDestination == restaurantY.get("enaRestaurant")) && !atDestination) {
        	agent.msgAtDestination();
        	atDestination = true;
        }
        else if (xPos == xDestination && yPos == yDestination
        		&& (xDestination == restaurantX.get("marcusRestaurant")) 
        		&& (yDestination == restaurantY.get("marcusRestaurant")) && !atDestination) {
        	agent.msgAtDestination();
        	atDestination = true;
        }
        else if (xPos == xDestination && yPos == yDestination
        		&& (xDestination == restaurantX.get("davidRestaurant")) 
        		&& (yDestination == restaurantY.get("davidRestaurant")) && !atDestination) {
        	agent.msgAtDestination();
        	atDestination = true;
        }
        else if (xPos == xDestination && yPos == yDestination
        		&& (xDestination == restaurantX.get("jeffersonrestaurant")) 
        		&& (yDestination == restaurantY.get("jeffersonrestaurant")) && !atDestination) {
        	agent.msgAtDestination();
        	atDestination = true;
        }
        else if ((xPos == xDestination && yPos == yDestination) && (xPos == homeX && yPos == homeY) && !atDestination){		//at checkpoint, "doingNothing" position (on-screen)
    		agent.msgAtHome();
    		atDestination = true;
        }
    }
    
    public void draw(Graphics2D g) {
        g.drawImage(truckImg,xPos,yPos,null);
    }


    public boolean isPresent() {
        return true;
    }
    
    public void setPresent(boolean p){
    	isPresent = p;
    }
    
    public void setReadyToWork(){
    	setPresent(true);
    	xDestination = homeX;
    	yDestination = homeY;
    	atDestination = false;
    }
    
    public void DoGoToHomePosition(){
    	atDestination = false;
    	xDestination = homeX;
    	yDestination = homeY;
    }
    public void DoDeliverOrder(String restaurantName){
    	xDestination = restaurantX.get(restaurantName);
    	yDestination = restaurantY.get(restaurantName);
    	atDestination = false;
    }


    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
	
}