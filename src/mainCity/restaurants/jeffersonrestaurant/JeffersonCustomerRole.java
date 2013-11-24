package mainCity.restaurants.jeffersonrestaurant;


import agent.Agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

import mainCity.PersonAgent;
import mainCity.restaurants.jeffersonrestaurant.Menu;
import mainCity.restaurants.jeffersonrestaurant.JeffersonWaiterRole.Table;
import mainCity.restaurants.jeffersonrestaurant.gui.CustomerGui;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Customer;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Waiter;
import role.*;

/**
 * Restaurant customer agent.
 */
public class JeffersonCustomerRole extends Role implements Customer {
	private String name;
	private int hungerLevel = 5;        // determines length of meal
	Timer timer = new Timer();
	private CustomerGui customerGui;

	public int table;
	private Menu menu;
	// agent correspondents
	private Waiter waiter;
	private JeffersonHostRole host;
	String choice;
	private Random randomGenerator = new Random();
	private double money;
	private boolean orderedOnce;
	private double debt;
	
	Map<String, Double> prices = new HashMap<String, Double>();
		
	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Seated, Deciding,CallingWaiter, Ordering,waitingForFood, Eating, DoneEating,waitingForCheck, 
		Leaving,reOrdering,LeavingBeforeSeated};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followHost, seated, decided,TalkingToWaiter, doneEating,DoneOrdering,gotFood, doneLeaving,cantOrder,checkRecieved};
	AgentEvent event = AgentEvent.none;

	
	public class choice{
		String Item;
		
	}
	
	
	
	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public JeffersonCustomerRole(PersonAgent p, String name){
		super(p);
		this.name = name;
		//this.money= 100;
		orderedOnce=false;
		
		debt = 0;
		
		prices.put("steak", 15.99);
		prices.put("chicken",10.99);
		prices.put("salad",5.99);
		prices.put("pizza",8.99);
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(JeffersonHostRole host) {
		this.host = host;
		
	}
	
	/**
	 * hack to establish connection to Waiter agent.
	 */
	public void setWaiter(Waiter waiter){
		this.waiter = waiter;
	}
	
	public void setMoney(double m){
		this.money=m;
		
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

	public void msgSitAtTable(int t, Menu m, Waiter w) {
		print("Received msgSitAtTable");
		table = t;
		menu=m;
		waiter=w;
		event = AgentEvent.followHost;
		stateChanged();
	}

	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	
	public void msgWhatWouldYouLike(){
		
		event =AgentEvent.TalkingToWaiter;
		stateChanged();
	}
	
	public void msgHereIsYourFood(){
		event=AgentEvent.gotFood;
		stateChanged();
	}
	
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		setInactive();
		stateChanged();
	}
	
	public void msgHereIsYourCheck(){
		//add stuff here
		event=AgentEvent.checkRecieved;
		stateChanged();
		
	}
	
	public void msgNotAvailable(){
		
		event = AgentEvent.cantOrder;
		//Holy damn this is one complex if statement
		int mensize=getMenuSize();
		for(int i=0; i< mensize; i++){
			if(this.menu.choices.get(i).itemName==choice){
				this.menu.choices.remove(i);
				break;
				
			}
		}
		
		
		stateChanged();
	}
	
	public void msgRestaurantFullLeave(){
		state=AgentState.LeavingBeforeSeated;
		Do("No tables, I'm leaving");
		stateChanged();
	}
	
	
	

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		
		if(state == AgentState.LeavingBeforeSeated){
			state= AgentState.Leaving;
			customerGui.DoExitRestaurant();
			return true;
			
		}
		

		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			System.out.println("got hungry and going to restaurant");
			goToRestaurant();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followHost ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		/*
		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			state = AgentState.Eating;
			EatFood();
			return true;
		}
		*/
		
		
		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			state = AgentState.Deciding;
			Decide();
			return true;
		}
		
		if (state == AgentState.Deciding && event == AgentEvent.decided){
			state = AgentState.CallingWaiter;
			//giveOrder();
			//print("callingwaiter");
			callWaiter();
			return true;
		}
		
		if (state==AgentState.CallingWaiter && event ==AgentEvent.TalkingToWaiter){
			state=AgentState.Ordering;
			giveOrder();
			return true;
		}
		
		if(state==AgentState.Ordering && event == AgentEvent.DoneOrdering){
			state=AgentState.waitingForFood;
			return true;
			
		}
		
		if(state == AgentState.waitingForFood && event ==AgentEvent.gotFood ){
			state=AgentState.Eating;
			EatFood();
			return true;
		}
		
		if(state==AgentState.waitingForFood && event==AgentEvent.cantOrder & this.menu.choices.size()==0){
			state = AgentState.waitingForCheck;
			Do(" out of options");
			requestCheck();
			return true;
			
		}
		
		if(state == AgentState.waitingForFood && event == AgentEvent.cantOrder){
			//reordering
			state=AgentState.Deciding;
			
			Decide();
			return true;
			
			
		}

		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			//state = AgentState.Leaving;
			state=AgentState.waitingForCheck;
			requestCheck();
			
			//leaveTable();
			return true;
		}
		if(state==AgentState.waitingForCheck && event==AgentEvent.checkRecieved){
			state=AgentState.Leaving;
			pay();
			leaveTable();
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
		
		customerGui.DoGoToRestaurant();
		host.msgIWantFood(this);//send our instance, so he can respond to us
	}

	private void SitDown() {
		//Do("Being seated. Going to table");
		customerGui.DoGoToSeat(table);
		//animation changes state to seated
	}
	
	private int getMenuSize(){
		return this.menu.choices.size();
		
	}
	
	private void Decide(){
		if(money<5.98 && money>0){
			state=AgentState.Leaving;
			leaveTable();
		}
		
		
		timer.schedule(new TimerTask() {
			Object order = 1;
			public void run() {
				
				int menusize=getMenuSize();
				int index = randomGenerator.nextInt(menusize);
				//index=2;
				choice=menu.choices.get(index).itemName;
				
				
				if(money>0 && money>5.98 && money<6.00){
					if(orderedOnce==true){
						state=AgentState.Leaving;
						leaveTable();
						return;
					}
					choice="salad";
					orderedOnce=true;
				}
				
				
				print(choice);
				print("Done deciding");
				event = AgentEvent.decided;
				stateChanged();
			}
		},
		5000);
		
		customerGui.drawFood(choice + "?");
	}
	
	private void callWaiter(){
		//print("action call waiter");
		waiter.msgImReadyToOrder(this);
		
	}

	private void EatFood() {
		Do("Eating Food");
		customerGui.drawFood(choice);
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
				print("Done eating " + choice);
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		5000);//getHungerLevel() * 1000);//how long to wait before running task
	}

	private void giveOrder(){
		waiter.msgHereIsMyChoice(this, choice);
		event=AgentEvent.DoneOrdering;
		
	}
	
	private void requestCheck(){
		waiter.msgWantCheck(this);
		
	}
	
	private void pay(){
		if(!(debt==0)){
			money=money-prices.get(choice);
			money=money-debt;
			Do("here is my money plus debt owed");
			waiter.msgHereIsPayment(this, prices.get(choice)+debt);
			leaveTable();
			return;
		}
		
		if(money<0){
			debt=debt+prices.get(choice);
			Do("Will pay next time, leaving");
			money=money+100;
			waiter.msgHereIsPayment(this, 0);
			leaveTable();
			return;
		}
		
		
		money=money-prices.get(choice);
		Do("here is my money");
		waiter.msgHereIsPayment(this,prices.get(choice));
		leaveTable();
	}
	private void leaveTable() {
		
		waiter.msgLeavingTable(this);
		customerGui.DoExitRestaurant();
		Do("Leaving.");
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




	
}


