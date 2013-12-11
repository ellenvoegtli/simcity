package role.ellenRestaurant;

import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.EllenRestaurant.gui.*;
import mainCity.restaurants.EllenRestaurant.interfaces.*;
import agent.Agent;
import role.Role;

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
public abstract class EllenWaiterRole extends Role implements Waiter {
	protected String name;
	
	protected Host host;
	protected Cook cook;
	protected Cashier cashier;
	public WaiterGuiInterface waiterGui = null;
	
	public List<MyCustomer> myCustomers;	//public only for testing purposes
	enum CustomerState {waiting, seated, readyToOrder, deciding, askedToOrder, hasOrdered, 
							needToReorder, orderReady, waitingForFood,
							gotFood, waitingForCheck, paying, leavingRestaurant};
	
	protected Semaphore atTable = new Semaphore(0,true);
	protected Semaphore atCook = new Semaphore(0, true);
	protected Semaphore atStart = new Semaphore(0, true);
	protected Semaphore doneLeaving = new Semaphore(0, true);

	
	public WaiterState wState;
	public enum WaiterState {doingNothing, busy};
	protected BreakState bState;
	enum BreakState {wantToGoOnBreak, acceptedToGoOnBreak, onBreak, offBreak};
	
	protected boolean wantToGoOnBreak = false;
	protected boolean wantToGoOffBreak = false;
	private boolean onDuty;
	private boolean needGui;
	

	public EllenWaiterRole(PersonAgent p, String name) {
		super(p);
		this.name = name;
		onDuty = true;
		
		myCustomers = new ArrayList<MyCustomer>();
		needGui = false;
	}
	
	public void setCook(Cook cook) {
		this.cook = cook;
	}
	
	public void setHost(Host host){
		this.host = host;
	}
	
	public void setCashier(Cashier cashier){
		this.cashier = cashier;
	}
	
	public Host getHost(){
		return this.host;
	}

	public void setState(WaiterState s){
		wState = s;
	}

	public String getName() {
		return name;
	}
	public Collection<MyCustomer> getMyCustomers() {
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
    public void msgGoOffDuty(double amount){
    	addToCash(amount);
    	onDuty = false;
    	stateChanged();
    }
    public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.ELLEN_WAITER, this.getName(), s);
	}
    
    
	// Messages
   
	public void msgPleaseSeatCustomer(Customer cust, int waitPosX, int waitPosY, int table){
		log("Received msgPleaseSeatCustomer");
		
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
		log("Received msgReadyToOrder from: " + mc.c.getName());
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
		log("Received msgHereIsChoice");
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
		log("Received msgOrderDoneCooking()");
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
		log("Received msgOutOfFood()");
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
		log("Received msgDoneAndLeaving from: " + mc.getCustomer().getName());
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
			log("Break request accepted");
			bState = BreakState.acceptedToGoOnBreak;
			stateChanged();
		}
		else {
			log("Break request denied");
			bState = BreakState.offBreak;
			this.getGui().GoOffBreak();
			stateChanged();
		}
	}
	//Check messages
	public void msgIWantMyCheck(Customer cust, String choice){
		log("Received msgIWantMyCheck from: " + cust.getName());
		
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){
			if (thisMC.c.equals(cust)){
				mc = thisMC;
				break;
			}
		}
		mc.s = CustomerState.waitingForCheck;
		stateChanged();
	}
	
	
	//Cashier message
	public void msgHereIsCheck(double amount, Customer cust){
		log("Received msgHereIsCheck");
		
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){
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
	public void msgDoneLeaving(){
		doneLeaving.release();
		stateChanged();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if (needGui){
			waiterGui.guiReappear();
			needGui = false;
		}
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
		
		if (myCustomers.isEmpty() && !onDuty){
			leaveRestaurant();
			onDuty = true;
			needGui = true;
		}
		
		return false;
		
	}

	// Actions


	private void seatCustomer(MyCustomer mc) {
		waiterGui.DoPickUpWaitingCustomer(mc.waitingAreaX + 20, mc.waitingAreaY + 20);
		try {
			atStart.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		mc.c.msgFollowMe(new EllenMenu(), mc.table, this);	//WaiterAgent is now at start location
		DoSeatCustomer(mc.c, mc.table); //animation
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		mc.s = CustomerState.seated;
	}
	
	// The animation DoXYZ() routines
	public void DoSeatCustomer(Customer customer, int table) {
		log("Seating " + customer + " at " + table);		
		waiterGui.DoGoToTable(table);
	}
	
	public void takeOrder(MyCustomer mc){
		waiterGui.DoGoToTable(mc.table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		mc.c.msgWhatDoYouWant();
		mc.s = CustomerState.askedToOrder;
	}
	
	public void warnCustomerOutOfFood(MyCustomer mc){
		waiterGui.DoGoToTable(mc.table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		EllenMenu menu = new EllenMenu();
		menu.menuItems.remove(mc.choice);			
		mc.c.msgOutOfFoodPleaseReorder(menu); 	//need to delete the mc.choice from the menu though
		mc.s = CustomerState.deciding;			//don't do anything yet
	}
	
	protected abstract void sendOrderToCook(MyCustomer mc);

	
	public void deliverFood(MyCustomer mc){
		log("Going to pick up finished order");
		
		waiterGui.DoGoToCook();
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cook.pickingUpFood(mc.table);
		log("Going to deliver food");
		log("Telling cashier to compute bill");
		
		cashier.msgComputeBill(mc.choice, mc.c, this);
		waiterGui.DoDeliverFood(mc.table, mc.choice);	//sends destination & details for waiter's food text label to waiter
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		log("Food delivered");		
		mc.c.msgHereIsYourFood(mc.choice);
		mc.s = CustomerState.gotFood;
		waiterGui.setIsDeliveringFood(false);	//lets animation remove food text label from waiter
	}
	
	public void sendCheck(MyCustomer mc){
		waiterGui.DoGoToTable(mc.table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		mc.c.msgHereIsCheck(mc.checkAmount);	//to customer
		mc.s = CustomerState.paying;
	}
	
	public void notifyHost(MyCustomer mc){
		log("Notifying host of free table: " + mc.table);
		host.msgTableFree(mc.table);
		log("Removing: " + mc.getCustomer().getName());
		myCustomers.remove(mc);
	}
	
	public void leaveRestaurant(){
		waiterGui.DoLeaveRestaurant();
		try {
			doneLeaving.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		setInactive();
		onDuty = true;
	}

	
	//utilities

	public void setGui(WaiterGuiInterface gui) {
		waiterGui = gui;
	}

	public WaiterGuiInterface getGui() {
		return waiterGui;
	}
	
	
	public class MyCustomer {
		Customer c;
		int table;
		String choice;
		double checkAmount;
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
		public void setTable(int t){
			table = t;
		}
	}
	
}

