package mainCity.market.gui;

import mainCity.market.interfaces.Customer;
import mainCity.market.interfaces.CustomerGuiInterface;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import role.market.MarketCustomerRole;
import role.market.MarketGreeterRole;


public class CustomerGui implements Gui, CustomerGuiInterface{
    private final int WINDOWX = 500;
    private final int WINDOWY = 370;

	private Customer agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;
	private boolean needsInventory = false;
	
	private MarketAnimationPanel animation;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToStation, GoToSeat, GoToCashier, LeaveRestaurant};
	private Command command=Command.noCommand;
	
	private int waitingRoomX;
	private int waitingRoomY;

	static final int customerWidth = 20, customerHeight = 20;
	static final int exitLocationX = 10, exitLocationY = -40;
    private final int cookX = WINDOWX + 20, cookY = WINDOWY/2;
    private final int cashierX = 20, cashierY = 250;
    private final int hostX = 10, hostY = 10;
	
	Map<Integer, Integer> tableX = new TreeMap<Integer, Integer>();
	Map<Integer, Integer> tableY = new TreeMap<Integer, Integer>();
	
	private boolean orderedFood = false;	//for food text label + "?" drawing
	private boolean gotFood = false;		//for food text label drawing
	private String myChoice;
	private int myTable;
	

	public CustomerGui(Customer c, MarketAnimationPanel a){ //HostAgent m) {
		this.animation = a;
		
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
			//if (command==Command.GoToStation)
				//agent.msgAnimationFinishedGoToStation();

			//if (command==Command.GoToSeat) 
			//	agent.msgAnimationFinishedGoToSeat();
			
			if (command==Command.GoToCashier)
				agent.msgAnimationFinishedGoToCashier();

			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, customerWidth, customerHeight);
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

	public boolean goInside(Map<String,Integer> inventoryNeeded){
		if (agent.restaurantOpen()){
			needsInventory = true;
			agent.goGetInventory(inventoryNeeded);
			setPresent(true);
			return true;
		}
		return false;
	}
	public boolean goInside(){
		if (agent.restaurantOpen()){
			needsInventory = true;
			agent.goGetInventory();
			setPresent(true);
			return true;
		}
		return false;
	}
	public boolean needsInventory() {
		return needsInventory;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoGoToMarket(){
		xDestination = hostX;
		yDestination = hostY;
	}
	public void DoGoToEmployeeStation(int x, int y){
		xDestination = x;
		yDestination = y;
    	command = Command.GoToStation;
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
	public void DoExitMarket() {
		xDestination = exitLocationX;
		yDestination = exitLocationY;
		command = Command.LeaveRestaurant;
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
