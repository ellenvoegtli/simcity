package mainCity.gui;


import mainCity.market.*;
import mainCity.market.gui.Gui;
import mainCity.market.gui.MarketGui;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;

import role.market.MarketDeliveryManRole;

public class DeliveryManGui implements Gui {

    private final int WINDOWX = 550;
    private final int WINDOWY = 350;
	

    private MarketDeliveryManRole agent = null;
    MarketGui gui;
    private boolean isPresent;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position

    static final int waiterWidth = 20;
    static final int waiterHeight = 20;
    
    private final int startX = -20;
    private final int startY = -20;
    private final int cookX = WINDOWX - 110;
    private final int cookY = WINDOWY/2;
    
    public int homeX = 530, homeY = 10;
    public int exitMarketX = 530, exitMarketY = 350/2;
    private int cashierX = 20, cashierY = 250;

	Map<String, Integer> restaurantX = new TreeMap<String, Integer>();
	Map<String, Integer> restaurantY = new TreeMap<String, Integer>();
	
	private boolean atDestination = true;
	private boolean isDeliveringFood = false;
	private String customerChoice;
	

    public DeliveryManGui(MarketDeliveryManRole agent, MarketGui gui) {
        this.agent = agent;
        this.gui = gui;
        
        //initialize restaurant locations map -- NECESSARY? or will we have a "contact list" equivalent to get restaurant locations?
        restaurantX.put("EllenRestaurant", 200);
    	restaurantY.put("EllenRestaurant", 100);
         
    	restaurantX.put("enaRestaurant", 250);
    	restaurantY.put("enaRestaurant", 100);
         
        restaurantX.put("marcusRestaurant", 300);
        restaurantY.put("marcusRestaurant", 100);
         
        restaurantX.put("davidRestaurant", 350);
        restaurantY.put("davidRestaurant", 100);
        
        restaurantX.put("jeffersonRestaurant", 400);
        restaurantY.put("jeffersonRestaurant", 100);

    }
    
    public void setHomePosition(int x, int y){
    	this.homeX = x;
    	this.homeY = y;
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
        		&& (xDestination == restaurantX.get("jeffersonRestaurant")) 
        		&& (yDestination == restaurantY.get("jeffersonRestaurant")) && !atDestination) {
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
        g.setColor(Color.BLUE);
        g.fillRect(xPos, yPos, waiterWidth, waiterHeight);
        
        if (isDeliveringFood){
        	g.setColor(Color.BLACK);
        	
        	if (customerChoice == "steak"){
        		g.drawString("STK", xPos + 20, yPos + 10); //"carrying" the food behind him
        	}
        	else if (customerChoice == "pasta"){
        		g.drawString("PST", xPos + 20, yPos + 10); //"carrying" the food behind him
        	}
        	else if (customerChoice == "pizza"){
        		g.drawString("PZA", xPos + 20, yPos + 10); //"carrying" the food behind him
        	}
        	else if (customerChoice == "soup"){
        		g.drawString("SP", xPos + 20, yPos + 10); //"carrying" the food behind him
        	}
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
    public void DoGoToCashier(){
    	atDestination = false;
    	xDestination = cashierX;
    	yDestination = cashierY;
    }
    public void DoDeliverOrder(String restaurantName){
    	xDestination = restaurantX.get(restaurantName);
    	yDestination = restaurantY.get(restaurantName);
    	atDestination = false;

    	/* but it's really supposed to be...:
    	xDestination = exitMarketX;
    	yDestination = exitMarketY;
    	*/
    	//then goes outside of market...?
    }
   
   
    public void setIsDeliveringFood(boolean isDeliveringFood){	//for food text label
    	this.isDeliveringFood = isDeliveringFood;
    }

    
    public void DoGoToStart(){
    	atDestination = false;

        xDestination = startX;
        yDestination = startY;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
