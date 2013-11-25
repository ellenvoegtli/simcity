package mainCity.restaurants.restaurant_zhangdt.gui;

import mainCity.restaurants.restaurant_zhangdt.DavidCustomerRole;
import mainCity.restaurants.restaurant_zhangdt.DavidWaiterRole;

import java.awt.*;

public class CustomerGui implements Gui{

	private DavidCustomerRole agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	//private HostAgent host;
	DavidAnimationPanel animation;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, GoToCashier, LeaveRestaurant};
	private Command command=Command.noCommand;
	
	private enum CustomerState {none, seated, decided, received, paid, finished}; 
	CustomerState cState = CustomerState.none;
	
	String Choice;
	
	int CashierLocX = 500; 
	int CashierLocY = 0;

	public static final int xTable = 200;
	public static final int yTable = 250;
	
	private int width = 20;
	private int height = 20;

	public CustomerGui(DavidCustomerRole c, DavidAnimationPanel a){ //HostAgent m) {
		agent = c;
		xPos = 40;
		yPos = 40;
		xDestination = 40;
		yDestination = 40;
		//maitreD = m;
		this.animation = a;
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

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) {
				agent.msgAnimationFinishedGoToSeat();
				cState = CustomerState.seated; 
			}
			else if (command==Command.GoToCashier) {
				agent.msgAnimationFinishedGoToCashier(); 
				cState = CustomerState.paid;
			}
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
			}
			
			command=Command.noCommand;
		}
	}
	
	public void msgOrdered(String c) {
		Choice = c; 
		cState = CustomerState.decided;
	}
	
	public void msgReceived() {
		cState = CustomerState.received; 
	}
	
	public void msgFinished() {
		cState = CustomerState.finished;
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, width, height);
		
		if(cState == CustomerState.seated) {
			g.setColor(Color.BLACK);
			g.drawRect(xPos + 20, yPos, width, height); 
			g.drawString(" ?", xPos + 30, yPos + 15);
		}
		
		if(cState == CustomerState.decided) {
			if(Choice == "Steak") {
				g.setColor(Color.BLACK);
				g.drawRect(xPos + 20, yPos, width+5, height);
				g.drawString("ST?", xPos + 20, yPos + 15);
			} 
			if(Choice == "Chicken") {
				g.setColor(Color.BLACK);
				g.drawRect(xPos + 20, yPos, width+5, height);
				g.drawString("CH?", xPos + 20, yPos + 15);
			}
			if(Choice == "Pizza") {
				g.setColor(Color.BLACK);
				g.drawRect(xPos + 20, yPos, width+5, height);
				g.drawString("PZ?", xPos + 20, yPos + 15);
			}
			if(Choice == "Salad") {
				g.setColor(Color.BLACK);
				g.drawRect(xPos + 20, yPos, width+5, height);
				g.drawString("SD?", xPos + 20, yPos + 15);
			}
		}
		
		if(cState == CustomerState.received) {
			if(Choice == "Steak") {
				g.setColor(Color.BLACK);
				g.drawRect(xPos + 20, yPos, width+5, height);
				g.drawString("ST", xPos + 20, yPos + 15);
			} 
			if(Choice == "Chicken") {
				g.setColor(Color.BLACK);
				g.drawRect(xPos + 20, yPos, width+5, height);
				g.drawString("CH", xPos + 20, yPos + 15);
			}
			if(Choice == "Pizza") {
				g.setColor(Color.BLACK);
				g.drawRect(xPos + 20, yPos, width+5, height);
				g.drawString("PZ", xPos + 20, yPos + 15);
			}
			if(Choice == "Salad") {
				g.setColor(Color.BLACK);
				g.drawRect(xPos + 20, yPos, width+5, height);
				g.drawString("SD", xPos + 20, yPos + 15);
			}
		}
		
		if(cState == CustomerState.finished) {
			g.drawString(" ", xPos + 20, yPos + 15);
		}
	}

	public boolean isPresent() {
		return isPresent;
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

	public void DoGoToSeat(int seatnumber, int xTable, int yTable) {//later you will map seatnumber to table coordinates.
		xDestination = xTable;
		yDestination = yTable;
		command = Command.GoToSeat;
	}
	
	public void DoGoToCashier(){ 
		xDestination = CashierLocX; 
		yDestination = CashierLocY;
		command = Command.GoToCashier;
	}

	public void DoExitRestaurant() {
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
	}
}
