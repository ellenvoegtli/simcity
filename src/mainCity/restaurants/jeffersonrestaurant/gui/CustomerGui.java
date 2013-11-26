package mainCity.restaurants.jeffersonrestaurant.gui;

import java.awt.*;
import java.util.Random;

import mainCity.restaurants.jeffersonrestaurant.JeffersonCustomerRole;
import mainCity.restaurants.jeffersonrestaurant.JeffersonWaiterRole.*;

public class CustomerGui implements Gui{

	private JeffersonCustomerRole agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	//private HostAgent host;
	//JeffersonRestaurantGui gui;
	Graphics2D g2;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand,GoToRestaurant, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;

	public static final int xTable = 200;
	public static final int yTable = 300;
	public static final int width = 20;
	public static final int height = 20;
	public static final int exitX= -40;
	public static final int exitY= -40;
	int tablecount = 3;
	int waitingloc;

	public CustomerGui(JeffersonCustomerRole c){ //HostAgent m) {
		agent = c;
		xPos = -40;
		yPos = -40;
		xDestination = -40;
		yDestination = -40;
		//maitreD = m;
		//this.gui = gui;
	}

	public void updatePosition() {
		if (xPos < xDestination)
			//xPos++;
			xPos = xPos+5;
		else if (xPos > xDestination)
			//xPos--;
			xPos=xPos-5;

		if (yPos < yDestination)
			//yPos++;
			yPos=yPos+5;
		else if (yPos > yDestination)
			//yPos--;
			yPos=yPos-5;
		if (yPos < yDestination &&  yPos < 170)
			//xPos++;
			yPos = yPos+10;

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat){ 
				agent.msgAnimationFinishedGoToSeat();
			}
			
			
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				isHungry = false;
				//gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g2=g;
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, width, height);
	}
	public void drawFood(String food){
		
		 //g2.drawString(food, xPos, yPos);
	}

	public boolean isPresent() {
		return isPresent;
	}
	public boolean goInside(){
		if(agent.restaurantOpen()){
			setHungry();
			return true;
		}
		return false;
	}
	public void setHungry() {
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoGoToRestaurant() {
		Random gen = new Random();
		waitingloc=gen.nextInt(10)*15;
		
		xDestination= gen.nextInt(10)*10;
		yDestination = waitingloc;
		command = Command.GoToRestaurant;
		
	}

	public void DoGoToSeat(int seatnumber) {//later you will map seatnumber to table coordinates.
		
		System.out.println("going to seat");
		xDestination = xTable + 100*seatnumber -100;
		yDestination = yTable;
		command = Command.GoToSeat;
	}

	public void DoExitRestaurant() {
		xDestination = exitX;
		yDestination = exitY;
		command = Command.LeaveRestaurant;
	}

	
}
