package mainCity.restaurants.EllenRestaurant;

import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.EllenRestaurant.gui.*;
import mainCity.restaurants.EllenRestaurant.interfaces.*;
import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public abstract class EllenWaiterRole extends Agent implements Waiter {
	protected String name;
	
	protected EllenHostRole host;
	protected EllenCookRole cook;
	protected EllenCashierRole cashier;
	public WaiterGui waiterGui = null;
	
	protected Collection<MyCustomer> myCustomers;
	enum CustomerState {waiting, seated, readyToOrder, deciding, askedToOrder, hasOrdered, 
							needToReorder, orderReady, waitingForFood,
							gotFood, waitingForCheck, paying, leavingRestaurant};
	
	protected Semaphore atTable = new Semaphore(0,true);
	protected Semaphore atCook = new Semaphore(0, true);
	protected Semaphore atStart = new Semaphore(0, true);

	
	protected WaiterState wState;
	enum WaiterState {doingNothing, busy};
	protected BreakState bState;
	enum BreakState {wantToGoOnBreak, acceptedToGoOnBreak, onBreak, offBreak};
	
	protected boolean wantToGoOnBreak = false;
	protected boolean wantToGoOffBreak = false;
	

	public EllenWaiterRole(String name) {
		super();
		this.name = name;
		
		myCustomers = new ArrayList<MyCustomer>();
	}
	
	public void setCook(EllenCookRole cook) {
		this.cook = cook;
	}
	
	public void setHost(EllenHostRole host){
		this.host = host;
	}
	
	public void setCashier(EllenCashierRole cashier){
		this.cashier = cashier;
	}
	
	public EllenHostRole getHost(){
		return this.host;
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}
	public Collection getMyCustomers() {
		return myCustomers;
	}

    public boolean isReady() {
    	return (wState == WaiterState.doingNothing);
    }
    
    public boolean isOnBreak(){
    	boolean onBreak = false;
    	if (bState == BreakState.onBreak)
    		onBreak = true;
    	else if (bState == BreakState.offBreak)
    		onBreak = false;
    	
    	return onBreak;
    }
    
    
	// Messages
   
	public void msgPleaseSeatCustomer(Customer cust, int waitPosX, int waitPosY, int table){
		print("Received msgPleaseSeatCustomer");
		
		MyCustomer mc = new MyCustomer(cust, table, CustomerState.waiting);
		mc.setWaitingPosition(waitPosX, waitPosY);
		
		myCustomers.add(mc);
		stateChanged();
	}

	public void msgReadyToOrder(Customer cust){
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){	//to find the myCustomer with this specific Customer within myCustomers list
			if (thisMC.c.equals(cust)){
				mc = thisMC;
				break;
			}
		}
		print("Received msgReadyToOrder from: " + mc.c.getName());
		mc.s = CustomerState.readyToOrder;
		stateChanged();
	}
	
	public void msgHereIsChoice(Customer cust, String choice) {
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){	//to find the myCustomer with this specific Customer within myCustomers list
			if (thisMC.c.equals(cust)){
				mc = thisMC;
				break;
			}
		}
		print("Received msgHereIsChoice");
		mc.choice = choice;
		mc.s = CustomerState.hasOrdered;
		stateChanged();
	}
	
	public void msgOrderDoneCooking(String choice, int tablenum) {
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){	//to find the myCustomer with this specific table number within myCustomers list
			if (thisMC.table == tablenum){
				mc = thisMC;
				break;
			}
		}
		print("Received msgOrderDoneCooking()");
		mc.s = CustomerState.orderReady;
		stateChanged();
	}
	
	public void msgOutOfFood(String choice, int tablenum){
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){	//to find the myCustomer with this specific table number within myCustomers list
			if (thisMC.table == tablenum){
				mc = thisMC;
				break;
			}
		}
		print("Received msgOutOfFood()");
		mc.s = CustomerState.needToReorder;	//set back to original
		stateChanged();
	}
	
	public void msgDoneAndLeaving(Customer cust) {
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){ //to find the myCustomer with this specific Customer within myCustomers list
			if (thisMC.c.equals(cust)){
				mc = thisMC;
				break;
			}
		}
		print("Received msgDoneAndLeaving from: " + mc.getCustomer().getName());
		mc.s = CustomerState.leavingRestaurant;
		stateChanged();
	}
	
	
	public void wantToGoOnBreak() {		//from gui
		wantToGoOnBreak = true;
		stateChanged();
	}
	
	public void wantToGoOffBreak() {	//from gui
		if (bState == BreakState.onBreak){
			bState = BreakState.offBreak;
			host.msgComingOffBreak(this);
			stateChanged();
		}
	}
	
	public void msgBreakRequestResponse(boolean isOK){
		if (isOK){
			print("Break request accepted");
			bState = BreakState.acceptedToGoOnBreak;
			stateChanged();
		}
		else {
			print("Break request denied");
			bState = BreakState.offBreak;
			this.getGui().GoOffBreak();
			stateChanged();
		}
	}
	//Check messages
	public void msgIWantMyCheck(Customer cust, String choice){
		print("Received msgIWantMyCheck from: " + cust.getName());
		
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){ //to find the myCustomer with this specific Customer within myCustomers list
			if (thisMC.c.equals(cust)){
				mc = thisMC;
				break;
			}
		}
		mc.s = CustomerState.waitingForCheck;
		stateChanged();
	}
	
	
	//Cashier message
	public void msgHereIsCheck(int amount, Customer cust){
		print("Received msgHereIsCheck");
		
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){ //to find the myCustomer with this specific Customer within myCustomers list
			if (thisMC.c.equals(cust)){
				mc = thisMC;
				break;
			}
		}
		mc.checkAmount = amount;
	}
	
	
	//Location messages from WaiterGui (animation)
    public void msgReadyAtCheckpoint(){
    	wState = WaiterState.doingNothing;
    	stateChanged();
    }


	public void msgAtTable() {
		atTable.release();// = true;
		stateChanged();
	}
	
	public void msgAtCook() {
		atCook.release();// = true;
		stateChanged();
	}
	
	public void msgAtStart(){
		atStart.release();// = true;
		stateChanged();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/*
		Always check to see if the waiter is at the "checkpoint" position ("doingNothing")
		before carrying out the next action.
		In the case of a waiting customer, the waiter can either be at the start position ("inactive")
		or at the checkpoint position ("doingNothing").
		
		Note: have to loop through myCustomers for each condition because otherwise, the first
		customer in the list will be picked before the rest each time. (1 for loop with all the 
		conditions would check each of the states for the first myCustomer before even looking 
		at the rest of the myCustomers.)
		 */
		
		try {
			if (wantToGoOnBreak){
				host.msgIWantBreak(this);
				wantToGoOnBreak = false;
			}
			
			for (MyCustomer mc : myCustomers){
				if (mc.s == CustomerState.waiting && wState == WaiterState.doingNothing && bState != BreakState.onBreak){
					seatCustomer(mc);
					wState = WaiterState.busy;
					return true;
				}
			}
			for (MyCustomer mc : myCustomers) {
				if (mc.s == CustomerState.readyToOrder && wState == WaiterState.doingNothing){
					takeOrder(mc);
					wState = WaiterState.busy;
					return true;
				}
			}
			for (MyCustomer mc : myCustomers) {
				if (mc.s == CustomerState.hasOrdered && wState == WaiterState.doingNothing){
					sendOrderToCook(mc);
					wState = WaiterState.busy;
					return true;
				}
			}
			for (MyCustomer mc : myCustomers) {
				if (mc.s == CustomerState.needToReorder && wState == WaiterState.doingNothing){
					warnCustomerOutOfFood(mc);
					wState = WaiterState.busy;
					return true;
				}
			}
			for (MyCustomer mc : myCustomers) {
				if (mc.s == CustomerState.waitingForFood && wState == WaiterState.doingNothing){
					//no action - waiting
					return true;
				}
			}
			for (MyCustomer mc : myCustomers) {
				if (mc.s == CustomerState.orderReady && wState == WaiterState.doingNothing){
					deliverFood(mc);
					wState = WaiterState.busy;
					return true;
				}
			}
			for (MyCustomer mc : myCustomers) {
				if (mc.s == CustomerState.waitingForCheck && wState == WaiterState.doingNothing){
					sendCheck(mc);
					wState = WaiterState.busy;
					return true;
				}
			}
			for (MyCustomer mc : myCustomers) {
				if (mc.s == CustomerState.leavingRestaurant){
					notifyHost(mc);
					return true;
				}
			}
			
			if (bState == BreakState.acceptedToGoOnBreak && myCustomers.isEmpty()){
				bState = BreakState.onBreak;
				this.getGui().GoOnBreak();
				return false;
			}
			
			if (bState == BreakState.onBreak){
				return false;
			}
			
			
			//else, if none of these loops or statements were entered, go to home position
			waiterGui.DoWait();
			wState = WaiterState.doingNothing;
			
		}catch(ConcurrentModificationException e){
			return false;
		}
		
		return false;
		
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions


	private void seatCustomer(MyCustomer mc) {
		waiterGui.DoPickUpWaitingCustomer(mc.waitingAreaX + 20, mc.waitingAreaY + 20);
		try {
			atStart.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mc.c.msgFollowMe(new EllenMenu(), mc.table, this);	//WaiterAgent is now at start location
		DoSeatCustomer(mc.c, mc.table); //animation
		try {
			atTable.acquire();
			//atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mc.s = CustomerState.seated;
	}
	
	// The animation DoXYZ() routines
	public void DoSeatCustomer(Customer customer, int table) {
		print("Seating " + customer + " at " + table);
		
		waiterGui.DoGoToTable(table);
	}
	
	public void takeOrder(MyCustomer mc){
		waiterGui.DoGoToTable(mc.table);
		try {
			atTable.acquire();
			//atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mc.c.msgWhatDoYouWant();
		mc.s = CustomerState.askedToOrder;
	}
	
	public void warnCustomerOutOfFood(MyCustomer mc){
		waiterGui.DoGoToTable(mc.table);
		try {
			atTable.acquire();
			//atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		EllenMenu menu = new EllenMenu();
		menu.menuItems.remove(mc.choice);			
		mc.c.msgOutOfFoodPleaseReorder(menu); 	//need to delete the mc.choice from the menu though
		mc.s = CustomerState.deciding;			//don't do anything yet
	}
	
	protected abstract void sendOrderToCook(MyCustomer mc);
	
	/*
	public void sendOrderToCook(MyCustomer mc){
		print("Going to send order to cook");
		
		waiterGui.DoGoToCook();
		try {
			atCook.acquire();
			//atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cook.msgHereIsOrder(mc.choice, mc.table, this);
		mc.s = CustomerState.waitingForFood;
	}
	*/
	
	public void deliverFood(MyCustomer mc){
		print("Going to pick up finished order");
		waiterGui.DoGoToCook();
		try {
			atCook.acquire();
			//atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cook.pickingUpFood(mc.table);
		print("Going to deliver food");
		print("Telling cashier to compute bill");
		cashier.msgComputeBill(mc.choice, mc.c, this);
		
		waiterGui.DoDeliverFood(mc.table, mc.choice);	//sends destination & details for waiter's food text label to waiter
		try {
			atTable.acquire();
			//atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		print("Food delivered");
		mc.c.msgHereIsYourFood(mc.choice);
		mc.s = CustomerState.gotFood;
		waiterGui.setIsDeliveringFood(false);	//lets animation remove food text label from waiter
	}
	
	public void sendCheck(MyCustomer mc){
		waiterGui.DoGoToTable(mc.table);
		try {
			atTable.acquire();
			//atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mc.c.msgHereIsCheck(mc.checkAmount);	//to customer
		mc.s = CustomerState.paying;
	}
	
	public void notifyHost(MyCustomer mc){
		print("Notifying host of free table: " + mc.table);
		host.msgTableFree(mc.table);
		print("Removing: " + mc.getCustomer().getName());
		myCustomers.remove(mc);
	}

	
	//utilities

	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}
	
	
	protected class MyCustomer {
		Customer c;
		int table;
		String choice;
		int checkAmount;
		
		int waitingAreaX;
		int waitingAreaY;
		
		CustomerState s;
		
		MyCustomer(Customer c, int table, CustomerState s){
			this.c = c;
			this.table = table;
			this.s = s;
		}
		
		public void setWaitingPosition(int x, int y){
			waitingAreaX = x;
			waitingAreaY = y;
		}
		
		public Customer getCustomer(){
			return this.c;
		}
	}
	
}

