package role.ellenRestaurant;

import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.EllenRestaurant.gui.*;
import mainCity.restaurants.EllenRestaurant.interfaces.*;
import agent.Agent;
import role.Role;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

/**
 * Restaurant customer agent.
 */
public class EllenCustomerRole extends Role implements Customer{
	private String name;
	private int hungerLevel = 5;        // determines length of meal
	Timer timer = new Timer();
	private CustomerGui customerGui;
	private int NFOODITEMS = 4;

	// agent correspondents
	private EllenHostRole host;
	private Waiter waiter;
	private EllenCashierRole cashier;
	private EllenMenu menu;
	
	private int waitingAreaX;
	private int waitingAreaY;
	
	private String choice;
	private double checkAmount;
	private double myCash;
	private double cashOwed = 0;

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, DecidingToStayOrLeave, BeingSeated, Seated, Deciding, 
		WaitingToOrder, Ordering, Eating, DoneEating, WaitingForCheck, GoingToCashierAgent, Paying, Punishment, Leaving};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, toldTablesAreFull, followWaiter, seated, decideChoice, doneDeciding, 
		readyToOrder, askedForChoice, gotFood, doneEating, gotCheck, atCashierAgent, paid, gotChange, assignedPunishment, doneLeaving};
	AgentEvent event = AgentEvent.none;

	int tableNum;
	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public EllenCustomerRole(PersonAgent p, String name){
		super(p);
		this.name = name;
		
		if (name.equalsIgnoreCase("flake"))		//he orders steak
			myCash = 10;
		else if (name.equalsIgnoreCase("cheap"))		//he orders soup, it's the only one he can afford
			myCash = 5;
		else if (name.equalsIgnoreCase("none"))
			myCash = 0;
		else 
			//myCash = 30;
			myCash = 100;
	
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(EllenHostRole host) {
		this.host = host;
	}
	
	public void setCashier(EllenCashierRole c){
		this.cashier = c;
	}

	public String getCustomerName() {
		return name;
	}
	
	public double getMyCash(){
		return myCash;
	}
	// Messages

	public void gotHungry() {//from animation
		//print("I'm hungry");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}
	
	public void msgRestaurantFull(){
		//print("Received msg RestaurantFull");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Received msg RestaurantFull");
		event = AgentEvent.toldTablesAreFull;
		stateChanged();
	}
	
	public void msgFollowMe(EllenMenu menu, int tablenum, Waiter w) {
		this.waiter = w;
		this.menu = menu;
		this.tableNum = tablenum;
		
		//print("Received msgFollowMe");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Received msgFollowMe");
		event = AgentEvent.followWaiter;
		stateChanged();
	}
	
	public void msgWhatDoYouWant(){
		event = AgentEvent.askedForChoice;
		stateChanged();
	}

	public void msgHereIsYourFood(String choice){
		event = AgentEvent.gotFood;
		stateChanged();
	}
	
	public void msgOutOfFoodPleaseReorder(EllenMenu menu){
		//print("Received msg OutOfFood, getting new menu");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Received msg OutOfFood, getting new menu");
		event = AgentEvent.seated;
		this.NFOODITEMS = menu.menuItems.size();
		this.menu = menu;
		stateChanged();
	}

	
	public void msgHereIsCheck(double amount){		//from waiter
		//print("Received msg HereIsCheck: $" + amount);
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Received msg HereIsCheck: $" + amount);
		checkAmount = amount;
		event = AgentEvent.gotCheck;
		stateChanged();
	}
	
	public void msgHereIsChange(double cashChange){		//from CashierAgent
		//print("Received msg HereIsChange: $" + cashChange);
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Received msg HereIsChange: $" + cashChange);
		myCash = cashChange;
		event = AgentEvent.gotChange;
		stateChanged();
	}
	
	public void msgNotEnoughCash(double cashOwed){
		//print("Received msg NotEnoughCash: I owe $" + cashOwed);
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Received msg NotEnoughCash: I owe $" + cashOwed);
		this.cashOwed += cashOwed;
		event = AgentEvent.assignedPunishment;
		stateChanged();
	}
	
	//Messages from animation
	public void msgAnimationFinishedGoToSeat() {
		event = AgentEvent.seated;
		stateChanged();
	}
	public void msgAnimationFinishedGoToCashier(){
		//print("msg AnimationFinished GoToCashierAgent");
		event = AgentEvent.atCashierAgent;
		stateChanged();
	}
	public void msgAnimationFinishedLeaveRestaurant() {
		event = AgentEvent.doneLeaving;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine

		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.toldTablesAreFull){
			state = AgentState.DecidingToStayOrLeave;
			DecideStayOrLeave();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followWaiter){
			state = AgentState.BeingSeated;
			SitDown(tableNum);
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			state = AgentState.Deciding;
			decideChoice();
			return true;
		}
		if (state == AgentState.Ordering && event == AgentEvent.seated){
			state = AgentState.Deciding;
			decideChoice();
			return true;
		}
		if (state == AgentState.Deciding && event == AgentEvent.doneDeciding){
			state = AgentState.WaitingToOrder;
			readyToOrder(this);
			return true;
		}
		if (state == AgentState.WaitingToOrder && event == AgentEvent.askedForChoice){
			state = AgentState.Ordering;
			sendChoice(this.choice, this.waiter);
			return true;
		}
		if (state == AgentState.Ordering && event == AgentEvent.gotFood){
			state = AgentState.Eating;
			EatFood();
			return true;
		}
		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Done eating");
			state = AgentState.WaitingForCheck;
			requestCheck();
			return true;
		}
		//new
		if (state == AgentState.WaitingForCheck && event == AgentEvent.gotCheck){
			state = AgentState.GoingToCashierAgent;
			goToCashier();
			return true;
		}
		//new
		if (state == AgentState.GoingToCashierAgent && event == AgentEvent.atCashierAgent){
			state = AgentState.Paying;
			payCashier();
			return true;
		}
		//new
		if (state == AgentState.Paying && event == AgentEvent.gotChange){
			state = AgentState.Leaving;
			leaveTable();
			return true;
		}
		//new
		if (state == AgentState.Paying && event == AgentEvent.assignedPunishment){
			state = AgentState.Punishment;
			goDoPunishment();
			return true;
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			//no action
			setInactive();
			return true;
		}
		return false;
	}

	// Actions

	private void goToRestaurant() {
		//Do("Going to restaurant");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Going to restaurant");
		customerGui.DoGoToWaitingArea();
		host.msgIWantFood(this, this.getGui().getWaitingPosX(), this.getGui().getWaitingPosY());
	}
	
	private void DecideStayOrLeave(){
		//Do("Deciding whether to stay or leave.");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Deciding whether to stay or leave.");
		if (name.equalsIgnoreCase("stay")){
			host.msgIWillStay(this);
			state = AgentState.WaitingInRestaurant;
			event = AgentEvent.none;
		}
		else if (name.equalsIgnoreCase("leave")){
			host.msgIAmLeaving(this);
			state = AgentState.Leaving;	
			event = AgentEvent.none;
			customerGui.DoExitRestaurant();
		}
		else {		//customers with a general name, not a hack: decide randomly
		
			Random rand = new Random();
			int n = rand.nextInt(2);
			//decide randomly
			if (n == 0) {
				host.msgIWillStay(this);
				state = AgentState.WaitingInRestaurant;
				event = AgentEvent.none;
			}
			else if (n == 1){
				host.msgIAmLeaving(this);
				state = AgentState.Leaving;	
				event = AgentEvent.none;
				customerGui.DoExitRestaurant();		//just to be safe
			}
		}
	}

	private void SitDown(int tableNum) {
		//Do("Being seated. Going to table");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Being seated. Going to table");
		customerGui.DoGoToSeat(tableNum);		//animation call
	}
	
	private void decideChoice(){
		//check prices of all foods FIRST; leaves if everything is too expensive
				int z = 0;
				for (String i : menu.menuItems){
					if (menu.getPrice(i) > myCash){
						z++;
					}
					if (z == menu.menuItems.size()){
						//print("All food is too expensive.");
						AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "All food is too expensive.");
						leaveTable();
						return;
					}
						
				}
				
		//HACK for "Customer orders food even though he can't afford it" (NON-NORM)
		if (this.getName().equalsIgnoreCase("flake")){
			choice = "steak";
		}
		//HACKS for testing inventory of each food on menu
		else if (this.getName().equalsIgnoreCase("steak")){
			choice = menu.menuItems.get(0);
		}
		else if (this.getName().equalsIgnoreCase("pasta")){
			this.choice = menu.menuItems.get(1);
		}
		else if (this.getName().equalsIgnoreCase("pizza")){
			this.choice = menu.menuItems.get(2);
		}

		else if (this.getName().equalsIgnoreCase("soup")){
			try {
				this.choice = menu.menuItems.get(3);
			}
			catch (Exception e){
				if (menu.getPrice("soup") > myCash){
					//print("All food is too expensive.");
					AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "All food is too expensive.");
					leaveTable();
					return;
				}
				else {
					Random rand = new Random();
					int n = rand.nextInt(NFOODITEMS);
					do {		
						n = rand.nextInt(NFOODITEMS);
						choice = menu.menuItems.get(n);
					} while (myCash < menu.getPrice(menu.menuItems.get(n)));
				}
			}
		}
		//NORM scenario: randomly picks choice
		else {
			Random rand = new Random();
			int n = rand.nextInt(NFOODITEMS);
			do {		
				n = rand.nextInt(NFOODITEMS);
				choice = menu.menuItems.get(n);
			} while (myCash < menu.getPrice(menu.menuItems.get(n)));
		}

		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "I am choosing " + choice);

		timer.schedule(new TimerTask() {
			public void run() {
				event = AgentEvent.doneDeciding;
				stateChanged();
			}
		},
		2000);
	}
	
	private void readyToOrder(EllenCustomerRole cust){
		waiter.msgReadyToOrder(this);
	}
	
	private void sendChoice(String choice, Waiter waiter){
		//print(waiter.getName() + ", here is my choice: " + choice);
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), waiter.getName() + ", here is my choice: " + choice);
		customerGui.DoDrawFood(choice, tableNum);		//sends GUI the choice String and where to draw it
		customerGui.setOrderedFood(true);				//lets GUI draw choice + "?" text label
		waiter.msgHereIsChoice(this, choice);
	}
	

	private void EatFood() {
		customerGui.setOrderedFood(false);
		customerGui.setGotFood(true);			//lets GUI remove "?" on food text label

		//Do("Eating Food");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Eating Food");
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() {
			public void run() {
				event = AgentEvent.doneEating;
				stateChanged();
			}
		},
		5000);//getHungerLevel() * 1000);//how long to wait before running task
	}
	
	private void requestCheck(){
		customerGui.setOrderedFood(false);
		customerGui.setGotFood(false);		//lets GUI remove food text label
		waiter.msgIWantMyCheck(this, this.choice);
	}
	
	private void goToCashier(){
		customerGui.DoGoToCashier();		
	}
	
	private void payCashier(){
		//print("Paying cashier");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Paying cashier");
		cashier.msgHereIsPayment(checkAmount, myCash, this);
	}
	
	private void goDoPunishment(){
		waiter.msgDoneAndLeaving(this); 	//this will delete the customer from that waiter's myCustomer list
		customerGui.DoGoToCook();
		//print("Going to do dishes for the rest of eternity.");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Going to do dishes for the rest of eternity.");
	}

	private void leaveTable() {
		//Do("Leaving.");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Leaving.");
		waiter.msgDoneAndLeaving(this);
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
	
	public boolean restaurantOpen() {
		if(host != null && host.isActive() && host.isOpen())
			return true;
		return false;
	}
}

