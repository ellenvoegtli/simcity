package mainCity.restaurants.EllenRestaurant.gui;

import mainCity.restaurants.EllenRestaurant.*;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import role.ellenRestaurant.EllenCustomerRole;

public class CustomerGui implements Gui{
    private final int WINDOWX = 500;
    private final int WINDOWY = 370;

	private EllenCustomerRole agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	EllenAnimationPanel animation;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, GoToCashier, LeaveRestaurant};
	private Command command=Command.noCommand;
	
	private int waitingRoomX;
	private int waitingRoomY;

	static final int customerWidth = 20;
	static final int customerHeight = 20;
	static final int exitLocationX = -40;
	static final int exitLocationY = -40;
    private final int cookX = WINDOWX + 20;
    private final int cookY = WINDOWY/2;
    private final int cashierX = 10;
    private final int cashierY = 110;
	
	Map<Integer, Integer> tableX = new TreeMap<Integer, Integer>();
	Map<Integer, Integer> tableY = new TreeMap<Integer, Integer>();
	
	private boolean orderedFood = false;	//for food text label + "?" drawing
	private boolean gotFood = false;		//for food text label drawing
	private String myChoice;
	private int myTable;
	private boolean atDestination = false;
	

	public CustomerGui(EllenCustomerRole c, EllenAnimationPanel a){ //HostAgent m) {
		animation = a;
		
		//initialize table locations map
        tableX.put(1, 200);
        tableY.put(1, 150);
        
        tableX.put(2, 300);
        tableY.put(2, 150);
        
        tableX.put(3, 200);
        tableY.put(3, 250);
        
        tableX.put(4, 300);
        tableY.put(4, 250);
		
        //initialize other variables
		agent = c;
		xPos = -40;
		yPos = -40;
		xDestination = -40;
		yDestination = -40;
		//maitreD = m;
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

		if (xPos == xDestination && yPos == yDestination) {

			if (command==Command.GoToSeat) 
				agent.msgAnimationFinishedGoToSeat();
			
			else if (command==Command.GoToCashier)
				agent.msgAnimationFinishedGoToCashier();

			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				isHungry = false;
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, customerWidth, customerHeight);
		
		g.setColor(Color.BLACK);	//draw the appropriate string under the appropriate table
		if (orderedFood){
			if (myChoice == "pasta"){
        		g.drawString("STK" + "?", tableX.get(myTable), tableY.get(myTable) + 60); //"carrying" the food behind him
        	}
        	else if (myChoice == "pizza"){
        		g.drawString("PST" + "?", tableX.get(myTable), tableY.get(myTable) + 60); //"carrying" the food behind him
        	}
        	else if (myChoice == "meatballs"){
        		g.drawString("PZA" + "?", tableX.get(myTable), tableY.get(myTable) + 60); //"carrying" the food behind him
        	}
        	else if (myChoice == "bread"){
        		g.drawString("SP" + "?", tableX.get(myTable), tableY.get(myTable) + 60); //"carrying" the food behind him
        	}
		}
		else if (gotFood){
			if (myChoice == "pasta"){
        		g.drawString("PSTA", tableX.get(myTable), tableY.get(myTable) + 60); //"carrying" the food behind him
        	}
        	else if (myChoice == "pizza"){
        		g.drawString("PIZZA", tableX.get(myTable), tableY.get(myTable) + 60); //"carrying" the food behind him
        	}
        	else if (myChoice == "meatballs"){
        		g.drawString("MTBLS", tableX.get(myTable), tableY.get(myTable) + 60); //"carrying" the food behind him
        	}
        	else if (myChoice == "bread"){
        		g.drawString("BREAD", tableX.get(myTable), tableY.get(myTable) + 60); //"carrying" the food behind him
        	}
		}
	}
	
	public void setOrderedFood(boolean hasOrderedFood){		//to manage food text label + "?" drawing
		this.orderedFood = hasOrderedFood;
	}
	
	public void setGotFood(boolean hasGotFood){			//to manage food text label drawing
		this.gotFood = hasGotFood;
	}

	public boolean isPresent() {
		return isPresent;
	}
	//public void setHungry() {
	public boolean goInside(){
		if (agent.restaurantOpen()){
			isHungry = true;
			agent.gotHungry();
			setPresent(true);
			return true;
		}
		return false;
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoDrawFood(String choice, int table){
		this.myChoice = choice;
		this.myTable = table;
	}
	
	public void setWaitingAreaPosition(int x, int y){
		this.waitingRoomX = x;
		this.waitingRoomY = y;
	}
	
	public int getWaitingPosX(){
		return waitingRoomX;
	}
	public int getWaitingPosY(){
		return waitingRoomY;
	}
	
	public void DoGoToWaitingArea(){
		xDestination = waitingRoomX;
		yDestination = waitingRoomY;
	}


	public void DoGoToSeat(int seatnumber) {	//mapped to seat coordinates
		if (seatnumber == 1){
    		xDestination = tableX.get(1);
    		yDestination = tableY.get(1);
    	}
    	else if (seatnumber == 2){
    		xDestination = tableX.get(2);
    		yDestination = tableY.get(2);
    	}
    	else if (seatnumber == 3){
    		xDestination = tableX.get(3);
    		yDestination = tableY.get(3);
    	}
    	else if (seatnumber == 4){
    		xDestination = tableX.get(4);
    		yDestination = tableY.get(4);
    	}
		
		command = Command.GoToSeat;
		
	}
	
    public void DoGoToCook() {
    	//atDestination = false;
    	xDestination = cookX;
    	yDestination = cookY;
    }
    
    public void DoGoToCashier(){
    	//atDestination = false;
    	xDestination = cashierX;
    	yDestination = cashierY;
    	
    	command = Command.GoToCashier;
    }

	public void DoExitRestaurant() {
		xDestination = exitLocationX;
		yDestination = exitLocationY;
		command = Command.LeaveRestaurant;
	}
}
