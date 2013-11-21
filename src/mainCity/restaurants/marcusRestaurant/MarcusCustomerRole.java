package mainCity.restaurants.marcusRestaurant;

import mainCity.restaurants.marcusRestaurant.gui.CustomerGui;
import mainCity.restaurants.marcusRestaurant.interfaces.*;
import mainCity.restaurants.marcusRestaurant.MarcusMenu;

import agent.Agent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Restaurant customer agent.
 */
public class MarcusCustomerRole extends Agent implements Customer {
	private String name;
	Timer timer = new Timer();

	private int hungerLevel = 5;        // determines length of meal
	private int tableNumber;
	private double cash;
	private int orderCount;
	MarcusMenu menu;
	
	// agent correspondents
	private CustomerGui customerGui;
	private MarcusHostRole host;
	private Waiter waiter;
	private Cashier cashier;

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Seated, Eating, DoneEating, requestingCheck, receivedCheck, waitingForChange, Leaving};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, restaurantFull, followWaiter, seated, choosing, readyToOrder, waiting, respond, ordered, hereIsFood, doneEating, payingCheck, doneLeaving};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public MarcusCustomerRole(String name){
		super();
		this.name = name;
		orderCount = 0;
		cash = (int) (Math.random() * 35);
		if(name.equals("THIEF")) {
			cash = 0;
		}
		if(name.equals("POOR")) {
			cash = 7;
		}
		print("I currently have $" + cash);
	}

	/**
	 * hack to establish connection to Waiter agent.
	 */
	public void setWaiter(Waiter w) {
		this.waiter = w;
	}

	public void setHost(MarcusHostRole h) {
		this.host = h;
	}
	
	public void setCashier(Cashier c) {
		this.cashier = c;
	}
	
	public String getCustomerName() {
		return name;
	}
	// Messages
	public void gotHungry() {//from animation
		print("I'm hungry");
		
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgWantToWait() {
		print("Restaurant is full, do I want to wait?");
		event = AgentEvent.restaurantFull;
		stateChanged();
	}
	
	public void msgFollowMeToTable(int table, MarcusMenu m, Waiter w) {
		print("Received msgSitAtTable");
		this.tableNumber = table;
		this.waiter = w;
		this.menu = m;
		this.event = AgentEvent.followWaiter;	
		stateChanged();
	}
	
	public void msgWhatWouldYouLike() {
		print("msgWhatWouldYouLike: Waiter just asked what I want");
		this.event = AgentEvent.respond;
		stateChanged();
	}
	
	public void msgPleaseReorder(MarcusMenu m) {
		print("Reordering...");
		this.menu = m;
		this.event = AgentEvent.respond;
		stateChanged();
	}
	
	public void msgHereIsYourOrder(String choice) {
		print("msgHereIsYourOrder: just received my order of " + choice);
		this.event = AgentEvent.hereIsFood;
		stateChanged();
	}
	
	public void msgHereIsCheck() {
		this.state = AgentState.receivedCheck;
		stateChanged();
	}
	
	public void msgDebtOwed(double amount) {
		this.cash = amount;
		print("Next time I come back, I'll be sure to settle my debt of $" + -amount);
		
		state = AgentState.DoingNothing;
		event = AgentEvent.doneLeaving;
	}
	
	public void msgHereIsChange(double c) {
		this.cash = c;
		print("Received my change, I now have $" + cash);
		print("I can now leave");
		
		state = AgentState.DoingNothing;
		event = AgentEvent.doneLeaving;
	}

	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.payingCheck;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.restaurantFull ){
			decide();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followWaiter ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if (state == AgentState.Seated && event == AgentEvent.seated){
			chooseFromMenu();
			return true;
		}
		if (state == AgentState.Seated && event == AgentEvent.readyToOrder){
			callWaiter();
			return true;
		}
		
		if (state == AgentState.Seated && event == AgentEvent.respond){
			makeSelection();
			return true;
		}
		
		if (state == AgentState.Seated && event == AgentEvent.hereIsFood){
			state = AgentState.Eating;
			EatFood();
			return true;
		}
		
		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.requestingCheck;
			askForCheck();
			return true;
		}
		
		if (state == AgentState.receivedCheck && event == AgentEvent.doneEating){
			state = AgentState.Leaving;
			leaveTable();
			return true;
		}
		
		if (state == AgentState.Leaving && event == AgentEvent.payingCheck){
			payCheck();
			return true;
		}

		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			//no action
			return true;
		}

		return false;
	}

	// Actions

	private void goToRestaurant() {
		Do("Going to restaurant");
		
		if(cash < 0) {
			print("I also have a debt to pay of $" + -cash);			
			cashier.msgPayingMyDebt(this, -cash);
			cash += (-cash) + ((int) (Math.random()*25));
			print("I have $" + cash + " in my wallet");
		}
		
		host.msgIWantToEat(this);//send our instance, so he can respond to us
	}

	private void decide() {
		print("Deciding...");
		event = AgentEvent.gotHungry;
		
		//If restaurant is full, there's a 35% chance the customer will leave
		if((int)(Math.random() * 100) > 35){
			print("I'm leaving...");
			state = AgentState.DoingNothing;
			event = AgentEvent.doneLeaving;
			customerGui.DoExitRestaurant();
			return;
		}
		
		host.msgIWillWait(this);
	}
	
	private void SitDown() {
		customerGui.DoGoToSeat(tableNumber);//hack; only one table
		state = AgentState.Seated;
	}
	
	private void chooseFromMenu() {
		event = AgentEvent.choosing;
		System.out.println(this + " is deciding what to eat...");

		timer.schedule(new TimerTask() {
			public void run() {
				event = AgentEvent.readyToOrder;
				stateChanged();
			}
		},
		(9000 + ((int) Math.random()*10)*1000));
	}
	
	private void callWaiter() {
		waiter.msgImReadyToOrder(this);
		event = AgentEvent.waiting;
	}
	
	private void makeSelection() {
		String choice = null;
		++orderCount;
		
		//Chooses from menu based on how much money they have
		for(int i = 0; i < menu.menu.size(); ++i) {
			if(cash > menu.menu.get(i).price || name.equals("THIEF")) {
				choice = menu.menu.get(i).food;
				break;
			}
		}
		
		if(choice == null) {
			print("Looks like I can't afford anything...leaving");
			hasToLeave();
			return;
		}
		
		if(orderCount > 3) {
			print("This is ridiculous! I'm leaving..");
			hasToLeave();
			return;
		}
		
		waiter.msgHereIsMyChoice(this, choice);
		
		switch(choice) {
		case "Steak":
			customerGui.DoCreateLabel("ST?");
			break;
		case "Chicken":
			customerGui.DoCreateLabel("CH?");
			break;
		case "Salad":
			customerGui.DoCreateLabel("SAL?");
			break;
		case "Pizza":
			customerGui.DoCreateLabel("PIZ?");
			break;
		}
		
		event = AgentEvent.ordered;
	}
	
	private void EatFood() {
		Do("Eating Food");
		customerGui.DoUpdateLabel();
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done eating, cookie=" + cookie);
				event = AgentEvent.doneEating;
				customerGui.DoClearLabel();
				stateChanged();
			}
		},
		7000);//getHungerLevel() * 1000);//how long to wait before running task
	}

	private void askForCheck() {
		print("Requesting check");
		waiter.msgReadyForCheck(this);
	}
	
	private void leaveTable() {
		//Do("Leaving.");
		orderCount = 0;
		waiter.msgLeavingTable(this);
		customerGui.DoExitRestaurant();
	}

	private void payCheck() {
		print("Going to cashier to pay check...");
		cashier.msgHereIsPayment(this, cash, tableNumber);
		state = AgentState.waitingForChange;
	}
	
	private void hasToLeave() {
		state = AgentState.DoingNothing;
		event = AgentEvent.doneLeaving;
		orderCount = 0;
		customerGui.DoClearLabel();

		waiter.msgLeavingTable(this);
		customerGui.DoExitRestaurant();
	}
	
	// Accessors, etc.

	public String getName() {
		return name;
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
		//could be a state change. Maybe you don't
		//need to eat until hunger lever is > 5?
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
	
	public int getXPos() {
		return customerGui.getX();
	}
	
	public int getYPos() {
		return customerGui.getY();
	}
}

