package mainCity.restaurants.restaurant_zhangdt.gui;

import role.davidRestaurant.*;

import java.awt.*;

import role.davidRestaurant.DavidCookRole;
import role.davidRestaurant.DavidCustomerRole;
import role.davidRestaurant.DavidWaiterRole;

public class WaiterGui implements Gui {

    private DavidWaiterRole agent = null;

    enum WaiterStates
    { none, OrderInHand, Delivered }; 
    
    WaiterStates waiterState = WaiterStates.none;
    
    private int xPos, yPos;//default waiter position
    private int xDestination, yDestination;//default start position 
    
    private int cookLocX = 60; 
    private int cookLocY = 185; 
    
    private int cashierLocX = 500;
    private int cashierLocY = 0;

    private int xTable = 5; 
    private int yTable = 5; 
    
    private int width = 20; 
    private int height = 20; 
    
    public int startY;
    public int startX;
    
    private boolean onlyOnce = false;

    public WaiterGui(DavidWaiterRole agent, int xStart, int yStart, int xStartDest, int yStartDest) {
        this.agent = agent;
        xPos = xStart; 
        yPos = yStart; 
        xDestination = xStartDest; 
        yDestination = yStartDest;
        startX = xStart;
        startY = yStart;
    }

    public void updatePosition() {
        if (xPos < xDestination)
            xPos+=5;
        else if (xPos > xDestination)
            xPos-=5;

        if (yPos < yDestination)
            yPos+=5;
        else if (yPos > yDestination)
            yPos-=5;
        
        //When the waiter reaches the table
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable + 20) & (yDestination == yTable - 20)) {
        	onlyOnce = true;
            agent.msgAtTable();  
            
        }
        
        //When the waiter reaches the cook
        if (xPos == xDestination && yPos == yDestination 
        		& (xDestination == cookLocX) & (yDestination == cookLocY)) {
        	agent.msgFinishedTakingOrder();
        }
        	
        //When the waiter reaches the cashier 
        if (xPos == xDestination && yPos == yDestination 
        		& (xDestination == cashierLocX) & (yDestination == cashierLocY)) {
        	agent.msgAtCashier();
        }
        
        //When the waiter reaches the lobby again
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == startX) & (yDestination == startY)
        		  & onlyOnce == true) {
           onlyOnce = false;
           agent.msgHostFree();
        }
        
        //When waiter leaves 
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == -20) & (yDestination == -20)) {
        	agent.msgHostFree();
        }
        
        //When the waiter reaches the customer's initial position
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == 60) & (yDestination == 60))
        	agent.msgWaiterFree();
    }
    
    public void msgOrderInHand() {
    	waiterState = WaiterStates.OrderInHand; 
    }
    
    public void msgDelivered() {
    	waiterState = WaiterStates.Delivered; 
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
        
        if(waiterState == WaiterStates.OrderInHand) {
        	if(agent.getOrder() == "Steak") {
				g.setColor(Color.BLACK);
				g.drawRect(xPos, yPos + 20, width+5, height);
				g.drawString("ST", xPos, yPos + 35);
			} 
			if(agent.getOrder() == "Chicken") {
				g.setColor(Color.BLACK);
				g.drawRect(xPos, yPos + 20, width+5, height);
				g.drawString("CH", xPos, yPos + 35);
			}
			if(agent.getOrder() == "Pizza") {
				g.setColor(Color.BLACK);
				g.drawRect(xPos, yPos + 20, width+5, height);
				g.drawString("PZ", xPos, yPos + 35);
			}
			if(agent.getOrder() == "Salad") {
				g.setColor(Color.BLACK);
				g.drawRect(xPos, yPos + 20, width+5, height);
				g.drawString("SD", xPos, yPos + 35);
			}
        }
        
        if(waiterState == WaiterStates.Delivered) {
        	g.drawString(" ", xPos, yPos + 35);
        }
    }

    public boolean isPresent() {
        return true;
    }

    public void DoMoveToCustomer() { 
    	System.out.println("Moving to customer");
    	xDestination = 60; 
    	yDestination = 60;
    }
    
    public void DoBringToTable(DavidCustomerRole customer, int tableX, int tableY) {
    	xTable = tableX;
    	yTable = tableY;
        xDestination = tableX + 20;
        yDestination = tableY - 20;
    }
    
    public void DoMoveToCook() {
    	xDestination = cookLocX; 
    	yDestination = cookLocY; 
    }
    
    public void DoMoveToCashier() { 
    	xDestination = cashierLocX; 
    	yDestination = cashierLocY; 
    }

    public void DoLeaveCustomer() {
        xDestination = startX;
        yDestination = startY;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

	public void DoLeaveRestaurant() {
		xDestination = -20; 
		yDestination = -20;
	}
	
	public void guiAppear() { 
		this.DoLeaveCustomer();
	}
}
