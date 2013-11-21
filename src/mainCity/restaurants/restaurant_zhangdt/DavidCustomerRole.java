package mainCity.restaurants.restaurant_zhangdt;

import mainCity.restaurants.restaurant_zhangdt.gui.CustomerGui;
import mainCity.restaurants.restaurant_zhangdt.gui.RestaurantGui;
import mainCity.restaurants.restaurant_zhangdt.interfaces.Customer;
import agent.Agent;

import java.util.*;

/**
 * Restaurant customer agent.
 */
public class DavidCustomerRole extends Agent implements Customer {
	
/*   Data   */ 
	
	private String name;
	private int hungerLevel = 10;        // determines length of meal
	Timer timer = new Timer();
	Timer timer1 = new Timer(); 
	private CustomerGui customerGui;
	List<String> choiceList = new ArrayList<String>();
	private double Money;
	private double Debt;
	private double Change;
	private String Order; 
	private boolean morallyUpright = true;

	// agent correspondents
	private DavidWaiterRole cWaiter;
	private CashierAgent cashier;
	private DavidHostRole host; 
	
	private int tableNum = 0;
	private int tableX = 0;
	private int tableY = 0;
	
	
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Seated, DecidingOnOrder, ReadyToOrder, Ordered, OrderPending, GotOrder, Eating, DoneEating, askForCheck, OutOfFood, Paid, Leaving, ExitingRestaurant};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followHost, seated, decided, ordering, ordered, GotOrder, Eating, doneEating, GotCheck, ReachedCashier, GotChange, doneLeaving, orderUnavailable};
	AgentEvent event = AgentEvent.none;
	

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public DavidCustomerRole(String name){
		super();
		this.name = name;
		Random ChoiceGenerator = new Random(); 
		Money = ChoiceGenerator.nextInt(100); 
		Debt = 0;
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(DavidHostRole h) {
		this.host = h;
	}
	
	public void setWaiter(DavidWaiterRole w) {
		this.cWaiter = w;
	}
	
	public void setCashier(CashierAgent c) {
		this.cashier = c;
	}

	public String getCustomerName() {
		return name;
	}
	
/*   Messages   */

	public void gotHungry() {//from animation
		print("I'm hungry. Going to restaurant with: $" + Money);
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgFollowWaiter(int tableNum, int tX, int tY) {
		print("Received msgFollowHost");
		event = AgentEvent.followHost;
		this.setTableNum(tableNum);
		this.tableX = tX;
		this.tableY = tY;
		stateChanged();
	}
	
	public void msgWhatWouldYouLike() {
		print("Received msgWhatWouldYoulLike"); 
		event = AgentEvent.ordering; 
		stateChanged(); 
	}
	
	public void msgOrderUnavailable() { 
		event = AgentEvent.orderUnavailable;
		customerGui.msgFinished();
		stateChanged();
	}
	
	public void msgBackInStock(String Choice) { 
		choiceList.add(Choice);
	}
	
	public void msgHereIsYourFood() {
		print("Received msgHereIsYourFood"); 
		event = AgentEvent.GotOrder; 
		stateChanged();
	}

	public void msgCheckIsRecieved(double price) { 
		print("Recieved CheckIsReady"); 
		if(Debt == 0){
			Debt = price;
		} 
		else{ 
			Debt = Debt + price;
		}
		print("My debt is " + Debt);
		event = AgentEvent.GotCheck; 
		stateChanged();
	}
	
	public void msgHeresYourChange(double change) {
		print("msgHeresYourChange recieved");
		Change = change;
		event = AgentEvent.GotChange; 
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	public void msgAnimationFinishedGoToCashier() {
		event = AgentEvent.ReachedCashier;
		stateChanged();
	}
	
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged();
	}
	

/*   Scheduler   */
	
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		try{
			if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
				state = AgentState.WaitingInRestaurant;
				goToRestaurant();
				return true;
			}
			if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followHost ){
				state = AgentState.BeingSeated;
				SitDown();
				return true;
			}
			if (state == AgentState.BeingSeated && event == AgentEvent.seated){
				state = AgentState.DecidingOnOrder; 
				DecideOrder(); 
				return true;
			}
			
			if (state == AgentState.DecidingOnOrder && event == AgentEvent.decided){ 
				state = AgentState.ReadyToOrder; 
				ImReadyToOrder();
				return true; 
			}
	
			if (state == AgentState.ReadyToOrder && event == AgentEvent.ordering){
				state = AgentState.Ordered; 
				HeresMyOrder(Order); 
				return true; 
			}
			
			if (state == AgentState.Ordered && event == AgentEvent.orderUnavailable) {
				state = AgentState.OrderPending;
				OrderAgain(Order); 
				return true;
			}
			
			if (state == AgentState.Ordered && event == AgentEvent.GotOrder){
				state = AgentState.Eating;
				EatFood();
				return true;
			}
			
			if (state == AgentState.Eating && event == AgentEvent.doneEating){
				state = AgentState.askForCheck;
				ReadyForCheck();
				return true;
			}
			
			if (state == AgentState.askForCheck && event == AgentEvent.GotCheck){ 
				state = AgentState.Leaving; 
				leaveTable();
			}
			
	
			if (state == AgentState.Leaving && event == AgentEvent.ReachedCashier){ 
				state = AgentState.Paid; 
				PayCashier();
			}
			
			if (state == AgentState.Paid && event == AgentEvent.GotChange) { 
				state = AgentState.ExitingRestaurant; 
				ExitRestaurant();
			}
			
			if (state == AgentState.ExitingRestaurant && event == AgentEvent.doneLeaving){
				state = AgentState.DoingNothing;
				//no action
				return true;
			}
			
			if (state == AgentState.OutOfFood){ 
				state = AgentState.DoingNothing;
				LeaveWithoutOrdering();
			}
			return false;
		}
		catch(ConcurrentModificationException r){ 
			return false;
		}
	}

/*   Actions   */

	private void goToRestaurant() {
		Do("Going to restaurant");
		host.msgIWantFood(this);//send our instance, so he can respond to us
	}

	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(getTableNum(), tableX, tableY);//hack; 
	}

	private void DecideOrder() { 
		Do("Deciding on Order"); 
		
		if(morallyUpright == true){
			if(Money > 15.99){
				print("I can order anything.");
				Random ChoiceGenerator = new Random(); 
				int choice = ChoiceGenerator.nextInt(3); 
				switch(choice) {
					case 0: Order = choiceList.get(0); break;  
					case 1: Order = choiceList.get(1); break;
					case 2: Order = choiceList.get(2); break;
					case 3: Order = choiceList.get(3); break;
				}
			}
			
			else if(Money > 10.99 && Money < 15.99){
				print("No money for steak");
				Random ChoiceGenerator = new Random(); 
				int choice = ChoiceGenerator.nextInt(2); 
				switch(choice) { 
					case 0: Order = choiceList.get(1); break;
					case 1: Order = choiceList.get(2); break;
					case 2: Order = choiceList.get(3); break;
				}
			}
			
			else if(Money > 8.99 && Money < 10.99){
				print("No money for chicken or steak");
				Random ChoiceGenerator = new Random(); 
				int choice = ChoiceGenerator.nextInt(1); 
				switch(choice) { 
					case 0: Order = choiceList.get(2); break;
					case 1: Order = choiceList.get(3); break;
				}
			}
			
			else if(Money > 5.99 && Money < 8.99){ 
				print("Only have money for salad");
				Order = choiceList.get(2);
			}
			
			else { 
				print("I can't afford anything"); 
				state = AgentState.OutOfFood;
				Order = "nothing";
			}
		}
		
		else {
			print("I'll order anything regardless of whether I can pay");
			Random ChoiceGenerator = new Random(); 
			int choice = ChoiceGenerator.nextInt(3); 
			switch(choice) {
				case 0: Order = choiceList.get(0); break;  
				case 1: Order = choiceList.get(1); break;
				case 2: Order = choiceList.get(2); break;
				case 3: Order = choiceList.get(3); break;
			}
		}
			
		timer1.schedule(new TimerTask() {
			public void run() {
				print("Done deciding. Ordering... " + Order);
				event = AgentEvent.decided;
				customerGui.msgOrdered(Order);
				stateChanged();
			}
		},
		1000);
	}
	
	
	private void ImReadyToOrder() { 
		Do("Im Ready to Order"); 
		cWaiter.msgReadyToOrder(this); 
	}
	
	private void HeresMyOrder(String Order){ 
		Do("Here's my Order"); 
		cWaiter.msgHeresMyOrder(this, Order); 
	}

	private void OrderAgain(String Order){ 
		Do("Ordering again because cook is out of " + Order); 
		choiceList.remove(Order); 
		
		if(choiceList.size() == 0){
			state = AgentState.OutOfFood;
			stateChanged();
		}
		
		else if(Money < 8.99){ 
			state = AgentState.OutOfFood;
			stateChanged();
		}
		
		else {
			Random ChoiceGenerator = new Random();  
			
			if(choiceList.size() == 3){
				int choice = ChoiceGenerator.nextInt(choiceList.size()-1);
				switch(choice) {
					case 0: setOrder(choiceList.get(0)); break;  
					case 1: setOrder(choiceList.get(1)); break;
					case 2: setOrder(choiceList.get(2)); break;
				}
			}
			
			if(choiceList.size() == 2){
				int choice = ChoiceGenerator.nextInt(choiceList.size()-1);
				switch(choice) {
					case 0: setOrder(choiceList.get(0)); break;  
					case 1: setOrder(choiceList.get(1)); break;
				}
			}
			
			if(choiceList.size() == 1){
				setOrder(choiceList.get(0)); 
			}
	
			print("I'll have " + getOrder() + " instead");
			customerGui.msgOrdered(getOrder());
			state = AgentState.ReadyToOrder;
			event = AgentEvent.ordering;
			stateChanged();
		}
	}
	
	private void EatFood() {
		customerGui.msgReceived();
		Do("Eating Food");
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
				print("Done eating, " + Order);
				event = AgentEvent.doneEating;
				customerGui.msgFinished();
				stateChanged();
			}
		},
		getHungerLevel() * 1000);//how long to wait before running task
	}
	
	private void ReadyForCheck(){ 
		cWaiter.msgReadyForCheck(this); 
		stateChanged();
	}
	
	private void PayCashier(){ 
		double Payment = 0;
		if(Debt > Money){ 
			print("I don't have enough money"); 
			Payment = 0; 
		}
		else {
			if(Debt >= 0 && Debt <= 20){
				Payment = 20; 
				Debt = 0; 
				Money -= 20; 
			}
			if(Debt >= 20 && Debt <= 40){
				Payment = 40; 
				Debt = 0; 
				Money -= 40; 
			}
			if(Debt >= 40 && Debt <= 60){
				Payment = 60; 
				Debt = 0; 
				Money -= 60; 
			}
		}
		cashier.msgHeresMyPayment(this, Payment, getTableNum());
		stateChanged();
	}

	private void leaveTable() {
		Do("Leaving.");
		cWaiter.msgCustomerLeaving(this);
		//customerGui.DoExitRestaurant();
		customerGui.DoGoToCashier();
	}
	
	private void ExitRestaurant() { 
		Do("Exitting Restaurant"); 
		Money += Change;
		print("I currently have: $" + Money);
		customerGui.DoExitRestaurant();
	}
	
	private void LeaveWithoutOrdering() { 
		Do("Leaving without Ordering"); 
		cWaiter.msgCustomerLeaving(this);
		customerGui.DoExitRestaurant();
	}

	// Accessors, Utilities etc.

	public DavidWaiterRole getWaiter() { 
		return cWaiter;
	}
	
	public String getName() {
		return name;
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
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
	
	public String getOrder() {
		return Order;
	}
	
	public void addToMenu(String s){
		choiceList.add(s);
	}

	public int getTableNum() {
		return tableNum;
	}

	public void setTableNum(int tableNum) {
		this.tableNum = tableNum;
	}
	
	public void setOrder(String o){
		Order = o;
	}
	
	public void setMoney(double m){
		Money = m;
		print("Money set to " + Money);
	}
	
	public void setMoral(){
		morallyUpright = false;
	}

	
}

