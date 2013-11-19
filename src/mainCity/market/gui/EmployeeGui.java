package mainCity.market.gui;


import mainCity.market.*;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;

public class EmployeeGui implements Gui {

    private final int WINDOWX = 550;
    private final int WINDOWY = 350;
	

    private MarketEmployeeRole agent = null;
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
    
    public int homeX, homeY;
    private int cashierX = 20, cashierY = 250;
    
    private int waitingRoomX;
    private int waitingRoomY;
    private int stockRoomX = 300, stockRoomY = 450;
    
	Map<Integer, Integer> stationX = new TreeMap<Integer, Integer>();
	Map<Integer, Integer> stationY = new TreeMap<Integer, Integer>();
	
	private boolean atDestination = true;
	private boolean isDeliveringFood = false;
	private String customerChoice;
	
	State s;
	enum State {wantsBreak, onBreak, offBreak};

    public EmployeeGui(MarketEmployeeRole agent, MarketGui gui) {
        this.agent = agent;
        this.gui = gui;
        
        //initialize table locations map
        stationX.put(1, 200);
    	stationY.put(1, 100);
         
         stationX.put(2, 250);
         stationY.put(2, 100);
         
         stationX.put(3, 300);
         stationY.put(3, 100);
         
         stationX.put(4, 350);
         stationY.put(4, 100);

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

        /*
        if (xPos == xDestination && yPos == yDestination
        		&& (xDestination == tableX.get(1) + 20) 
        		&& (yDestination == tableY.get(1) - 20) && !atDestination) {
           agent.msgAtTable();
           atDestination = true;
        }

        else if (xPos == xDestination && yPos == yDestination
        		&& (xDestination == tableX.get(2) + 20) 
        		&& (yDestination == tableY.get(2) - 20) && !atDestination) {
           agent.msgAtTable();
           atDestination = true;
        }
        else if (xPos == xDestination && yPos == yDestination
        		&& (xDestination == tableX.get(3) + 20) 
        		&& (yDestination == tableY.get(3) - 20) && !atDestination) {
           agent.msgAtTable();
           atDestination = true;
        }
        else if (xPos == xDestination && yPos == yDestination
        		&& (xDestination == tableX.get(4) + 20) 
        		&& (yDestination == tableY.get(4) - 20) && !atDestination) {
           agent.msgAtTable();
           atDestination = true;
        }
        */
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
        else if ((xPos == xDestination && yPos == yDestination) && (xPos == homeX && yPos == homeY) && !atDestination){		//at checkpoint, "doingNothing" position (on-screen)
    		agent.msgAtStation();
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
	
    
    public void DoGoToCook() {
    	atDestination = false;
    	xDestination = cookX;
    	yDestination = cookY;
    }
   
   
    public void setIsDeliveringFood(boolean isDeliveringFood){	//for food text label
    	this.isDeliveringFood = isDeliveringFood;
    }

    
    public void DoWait() {
    	//atDestination = false;
    	xDestination = homeX;
    	yDestination = homeY;
    }
    
    public void DoGoToStart(){
    	atDestination = false;

        xDestination = startX;
        yDestination = startY;
    }
    
    public void DoPickUpWaitingCustomer(int x, int y){
    	atDestination = false;
    	waitingRoomX = x + 20;
    	waitingRoomY = y + 20;
    	
    	xDestination = waitingRoomX;
    	yDestination = waitingRoomY;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
