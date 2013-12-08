package mainCity.market1.gui;


import mainCity.market1.interfaces.Employee;
import mainCity.market1.interfaces.EmployeeGuiInterface;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;


public class EmployeeGui implements Gui, EmployeeGuiInterface {
    private static final int WINDOWX = 500, WINDOWY = 370;

    private Employee agent = null;
    private MarketAnimationPanel animation;
    private boolean isPresent;

    static final int waiterWidth = 20;
    static final int waiterHeight = 20;
    static final int stockRoomWidth = 25, stockRoomHeight = 50;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position
    private final int exitX = -20;
    private final int exitY = -20;
    static final int deliveryWidth = 25, deliveryHeight = 50;
    private final int deliveryX = WINDOWX - deliveryWidth, deliveryY = WINDOWY/2 + 2*deliveryHeight;
    public int homeX, homeY;
    private int cashierX = 20, cashierY = 250;
    private int waitingRoomX;
    private int waitingRoomY;
    static final int stockRoomX = stockRoomWidth*2, stockRoomY = WINDOWY +50;
    
	Map<Integer, Integer> stationX = new TreeMap<Integer, Integer>();
	Map<Integer, Integer> stationY = new TreeMap<Integer, Integer>();
	
	private boolean atDestination = true;
	private boolean isDeliveringFood = false;
	private String customerChoice;
	
	State s;
	enum State {wantsBreak, onBreak, offBreak};

    public EmployeeGui(Employee agent, MarketAnimationPanel a) {
        this.agent = agent;
        this.animation = a;
        
        //initialize table locations map
        stationX.put(1, 150);	//station 1
    	stationY.put(1, 50);
        stationX.put(2, 250);	//station 2
        stationY.put(2, 50);
        stationX.put(3, 350);	//station 3
        stationY.put(3, 50);
        stationX.put(4, 450);	//station 4
        stationY.put(4, 50);
        stationX.put(5, 150);	//station 5
        stationY.put(5, 150);
    	stationX.put(6, 250);	//station 6
    	stationY.put(6, 150);
    	stationX.put(7, 350);	//station 7
    	stationY.put(7, 150);
    	stationX.put(8, 450);	//station 8
    	stationY.put(8, 150);

    }
    
    public void setHomePosition(int x, int y){
    	this.homeX = x;
    	agent.setHomeX(x);
    	this.homeY = y;
    	agent.setHomeY(y);
    	
    	atDestination = false;
    	xDestination = homeX;
    	yDestination = homeY;
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

        
        if ((xPos == xDestination && yPos == yDestination) && (xPos == cashierX && yPos == cashierY) && !atDestination){		//at checkpoint, "doingNothing" position (on-screen)
    		agent.msgAtCashier();
    		atDestination = true;
        }
        else if ((xPos == xDestination && yPos == yDestination) && (xPos == stockRoomX && yPos == stockRoomY) && !atDestination){		//at checkpoint, "doingNothing" position (on-screen)
    		//agent.msgAtStockRoom();
    		atDestination = true;
        }
        else if ((xPos == xDestination && yPos == yDestination) && (xPos == waitingRoomX && yPos == waitingRoomY) && !atDestination){		//at checkpoint, "doingNothing" position (on-screen)
    		agent.msgAtWaitingRoom();
    		atDestination = true;
        }
        else if ((xPos == xDestination && yPos == yDestination) && (xPos == deliveryX && yPos == deliveryY) && !atDestination){		//at checkpoint, "doingNothing" position (on-screen)
    		agent.msgAtDeliveryMan();
    		atDestination = true;
        }
        else if ((xPos == xDestination && yPos == yDestination) && (xPos == homeX && yPos == homeY) && !atDestination){		//at checkpoint, "doingNothing" position (on-screen)
    		agent.msgAtStation();
    		atDestination = true;
        }
        else if ((xPos == xDestination && yPos == yDestination) && (xPos == exitX && yPos == exitY) && !atDestination){		//at checkpoint, "doingNothing" position (on-screen)
    		agent.msgDoneLeaving();
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
    public void setIsDeliveringFood(boolean isDeliveringFood){	//for food text label
    	this.isDeliveringFood = isDeliveringFood;
    }
    
    public void setReadyToWork(){
    	setPresent(true);
    	xDestination = homeX;
    	yDestination = homeY;
    	atDestination = false;
    }
    public void DoGoToStation(){
    	atDestination = false;
    	xDestination = homeX;
    	yDestination = homeY;
    }
    public void DoGoToCashier(){
    	atDestination = false;
    	xDestination = cashierX;
    	yDestination = cashierY;
    }
    public void DoFulfillOrder(){
    	atDestination = false;
    	xDestination = stockRoomX;
    	yDestination = stockRoomY;
    	//...
    }
    public void DoGoToDeliveryMan(){
    	atDestination = false;
    	xDestination = deliveryX;
    	yDestination = deliveryY;
    }
    public void DoWait() {
    	//atDestination = false;
    	xDestination = homeX;
    	yDestination = homeY;
    }
    public void DoPickUpWaitingCustomer(int x, int y){
    	atDestination = false;
    	waitingRoomX = x + 20;
    	waitingRoomY = y + 20;
    	
    	xDestination = waitingRoomX;
    	yDestination = waitingRoomY;
    }
    public void DoLeaveMarket(){
    	atDestination = false;
    	xDestination = exitX;
    	yDestination = exitY;
    }
    

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
