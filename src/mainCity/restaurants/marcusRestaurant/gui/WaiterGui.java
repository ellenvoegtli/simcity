package mainCity.restaurants.marcusRestaurant.gui;

import java.awt.*;

import role.marcusRestaurant.MarcusWaiterRole;

public class WaiterGui implements Gui {

    private MarcusWaiterRole agent = null;

    private int xPos = 460, yPos = -20;//default waiter position
    private int xDestination = 460, yDestination = -20;//default start position
    private static final int cookX = 255, cookY = 45; //default cook location to go to
    private int atTableNumber = 0;
    private String order;
    private boolean gettingCustomer;
    
    private boolean deliveringFood;
    private boolean hasFood;
    
    public static final int xTable = 100;
    public static final int yTable = 260;
    
    private final int homeX;
    private final int homeY;
    
    public static final int w = 20;
    public static final int h = 20;

    
    public WaiterGui(MarcusWaiterRole agent, int pos) {
        this.agent = agent;
        deliveringFood = false;
        hasFood = false;
        homeX = 460;
        homeY = yDestination = 50 + 40*pos-15*pos;
        gettingCustomer = false;        
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
        
        if((xPos == (xTable + w + 100*atTableNumber)) && (yPos == yTable - h)) {
        	if(deliveringFood) {
        		deliveringFood = false;
        		hasFood = false;
        	}
        	agent.msgAtDest();
        }
        
        if(xPos == cookX && yPos == cookY) {
        	agent.msgAtDest();
        	
        	if(deliveringFood)
        		hasFood = true;
        }
        
        if(xPos == xDestination && yPos == yDestination && gettingCustomer) {
        	agent.msgAtDest();
        	gettingCustomer = false;
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, w, h);
        
        if(hasFood) {
        	g.drawString(order, xPos, yPos);
        }        
    }

    public boolean isPresent() {
        return true;
    }

    public void DoGoToTable(int tableNumber) {
    	atTableNumber = tableNumber-1;
        xDestination = xTable + w + 100 * (tableNumber-1);
        yDestination = yTable - h;
    }

    public void DoGoToCook() {
    	xDestination = cookX;
    	yDestination = cookY;
    }
    
    public void DoDeliverFood(String c) {
		DoGoToCook();
		
		switch(c) {
			case "Steak": 
				order = "ST";
				break;
			case "Chicken": 
				order = "CH";
				break;
			case "Salad": 
				order = "SAL";
				break;
			case "Pizza": 
				order = "PIZ";
				break;
		}

		deliveringFood = true;
    }
    
    public void DoGoHome() {
    	xDestination = homeX;
        yDestination = homeY;
    }
    
    public void DoLeaveRestaunt() {
    	xDestination = 460;
    	yDestination = -20;
    	gettingCustomer = true; //recycling old code
    }
    
    public void DoPickUpCustomer(int x, int y) {
    	xDestination = x;
    	yDestination = y;
    	gettingCustomer = true;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
