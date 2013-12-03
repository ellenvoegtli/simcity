package mainCity.gui;


import mainCity.market.*;
import mainCity.market.interfaces.DeliveryMan;
import mainCity.market.interfaces.DeliveryManGuiInterface;
import mainCity.gui.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;

import role.market.MarketDeliveryManRole;

public class DeliveryManGui implements Gui, DeliveryManGuiInterface {

    public DeliveryMan agent = null;
    CityGui gui;
    private boolean isPresent;

    private int xPos = 415, yPos = 215;//default delivery position
    private int xDestination = 415, yDestination = 215;//default start position
    static final int waiterWidth = 20;
    static final int waiterHeight = 20;
    public int homeX = 415, homeY = 215;
    public int exitMarketX = 530, exitMarketY = 350/2;
    private int cashierX = 20, cashierY = 250;
    private BufferedImage truckImg = null;

	Map<String, Integer> restaurantX = new TreeMap<String, Integer>();
	Map<String, Integer> restaurantY = new TreeMap<String, Integer>();
	
	private boolean atDestination = true;
	private boolean isDeliveringFood = false;
	private BufferedImage myImg = null;
	

    public DeliveryManGui(DeliveryMan agent) {
    	StringBuilder path = new StringBuilder("imgs/");
		try {
			truckImg = ImageIO.read(new File(path.toString() + "truck.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
        this.agent = agent;
        //this.gui = gui;
        
        //initialize restaurant locations map -- NECESSARY? or will we have a "contact list" equivalent to get restaurant locations?
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

        /*
        else if ((xPos == xDestination && yPos == yDestination) && (xPos == cashierX && yPos == cashierY) && !atDestination){		//at checkpoint, "doingNothing" position (on-screen)
    		agent.msgAtCashier();
    		atDestination = true;
        }
        */
        else if ((xPos == xDestination && yPos == yDestination) && (xPos == homeX && yPos == homeY) && !atDestination){		//at checkpoint, "doingNothing" position (on-screen)
    		agent.msgAtHome();
    		atDestination = true;
        }
    }
    
    public void draw(Graphics2D g) {
        //g.setColor(Color.BLUE);
        //g.fillRect(xPos, yPos, waiterWidth, waiterHeight);
        g.drawImage(truckImg,xPos,yPos,null);
    	
        if (isDeliveringFood){
        	g.setColor(Color.BLACK);
        }
        
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

    public void setIsDeliveringFood(boolean isDeliveringFood){	//for food text label
    	this.isDeliveringFood = isDeliveringFood;
    }


    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
