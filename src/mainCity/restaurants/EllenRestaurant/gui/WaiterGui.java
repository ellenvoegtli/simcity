package mainCity.restaurants.EllenRestaurant.gui;


import mainCity.restaurants.EllenRestaurant.*;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;

public class WaiterGui implements Gui {

    private final int WINDOWX = 550;
    private final int WINDOWY = 350;
	

    private EllenWaiterRole agent = null;
    EllenAnimationPanel animation;
    private boolean isPresent;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position

    static final int waiterWidth = 20;
    static final int waiterHeight = 20;
    
    private final int startX = -20;
    private final int startY = -20;
    private final int cookX = WINDOWX - 110;
    private final int cookY = WINDOWY/2;
    
    private int homeX;
    private int homeY;
    
    private int waitingRoomX;
    private int waitingRoomY;
    
	Map<Integer, Integer> tableX = new TreeMap<Integer, Integer>();
	Map<Integer, Integer> tableY = new TreeMap<Integer, Integer>();
	
	private boolean atDestination = true;
	private boolean isDeliveringFood = false;
	private String customerChoice;
	
	State s;
	enum State {wantsBreak, onBreak, offBreak};

    public WaiterGui(EllenWaiterRole agent, EllenAnimationPanel animation) {
        this.agent = agent;
        this.animation = animation;
        
        //initialize table locations map
        tableX.put(1, 200);
        tableY.put(1, 150);
        
        tableX.put(2, 300);
        tableY.put(2, 150);
        
        tableX.put(3, 200);
        tableY.put(3, 250);
        
        tableX.put(4, 300);
        tableY.put(4, 250);

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
        else if ((xPos == xDestination && yPos == yDestination) && (xPos == cookX && yPos == cookY) && !atDestination) {		//cook location (off-screen)
           agent.msgAtCook();
           atDestination = true;
        }
        else if ((xPos == xDestination && yPos == yDestination) && (xPos == homeX && yPos == homeY) && !atDestination){		//at checkpoint, "doingNothing" position (on-screen)
    		agent.msgReadyAtCheckpoint();
    		atDestination = true;
        }
        else if ((xPos == xDestination && yPos == yDestination) && (xPos == waitingRoomX && yPos == waitingRoomY) && !atDestination){
        	agent.msgAtStart();
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
    
    //getter functions
	public boolean wantsBreak() {
		return (s == State.wantsBreak);
	}
	public boolean onBreak(){
		return (s == State.onBreak);
	}
	public boolean offBreak(){
		return (s == State.offBreak);
	}
	
	//setter functions
	public void IWantBreak() {
		s = State.wantsBreak;
		agent.wantToGoOnBreak();
		//gui.updateInfoPanel(agent);
	}

	public void GoOnBreak(){
		s = State.onBreak;
		//gui.updateInfoPanel(agent);
	}
	
	public void GoOffBreak(){		//called from either RestaurantGui (if coming back) or HostAgent (if denied break request)
		if (s == State.onBreak)
			agent.wantToGoOffBreak();
		
		s = State.offBreak;
		//gui.updateInfoPanel(agent);
	}

    public void DoGoToTable(int table) {
    	atDestination = false;
    	if (table == 1){
    		xDestination = tableX.get(1) + 20;
    		yDestination = tableY.get(1) - 20;
    	}
    	else if (table == 2){
    		xDestination = tableX.get(2) + 20;
    		yDestination = tableY.get(2) - 20;
    	}
    	else if (table == 3){
    		xDestination = tableX.get(3) + 20;
    		yDestination = tableY.get(3) - 20;
    	}
    	else if (table == 4){
    		xDestination = tableX.get(4) + 20;
    		yDestination = tableY.get(4) - 20;
    	}
    }
    
    public void DoGoToCook() {
    	atDestination = false;
    	xDestination = cookX;
    	yDestination = cookY;
    }
    
    public void DoDeliverFood(int table, String choice){
    	atDestination = false;

    	if (table == 1){
    		xDestination = tableX.get(1) + 20;
    		yDestination = tableY.get(1) - 20;
    	}
    	else if (table == 2){
    		xDestination = tableX.get(2) + 20;
    		yDestination = tableY.get(2) - 20;
    	}
    	else if (table == 3){
    		xDestination = tableX.get(3) + 20;
    		yDestination = tableY.get(3) - 20;
    	}
    	else if (table == 4){
    		xDestination = tableX.get(4) + 20;
    		yDestination = tableY.get(4) - 20;
    	}
    	
    	//to display food text label:
    	isDeliveringFood = true;
    	customerChoice = choice;
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
    	waitingRoomX = x;
    	waitingRoomY = y;
    	
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
