package mainCity.market;

import agent.Agent;
//import restaurant.gui.CustomerGui;
import mainCity.market.gui.*;
import mainCity.market.*;
import mainCity.interfaces.*;
//import mainCity.market.OrderItem;

//import restaurant.interfaces.*;



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
public class MarketEmployeeRole extends Agent {
	private String name;
	Timer timer;
	
	private MarketGreeterRole host;
	private MarketCashierRole cashier;
	private MarketDeliveryManRole deliveryMan;
	private MarketMenu marketMenu;
	
	public EmployeeGui employeeGui = null;
	
	private List<MyCustomer> myCustomers = new ArrayList<MyCustomer>();
	private List<MyBusiness> myBusinesses = new ArrayList<MyBusiness>();
	enum CustomerState {newCustomer, waitingForOrder, ordered, waitingForBill, fulfillingOrder, doneFulfillingOrder, gotCheckFromCashier, gotOrderAndBill, leaving};
	enum BusinessState {ordered, waitingForBill, fulfillingOrder, doneFulfillingOrder, gotCheckFromCashier, sentForDelivery};
	
	WaiterState wState;
	enum WaiterState {doingNothing, busy};
	
	private Semaphore atTable = new Semaphore(0,true);
	private Semaphore atCook = new Semaphore(0, true);
	private Semaphore atStart = new Semaphore(0, true);

	
	//enum OrderItemState {fulfilled, partFulfilled, noneFulfilled};
	

	public MarketEmployeeRole(String name) {
		super();
		this.name = name;
	}
	
	public void setHost(MarketGreeterRole host){
		this.host = host;
	}
	
	public void setCashier(MarketCashierRole cashier){
		this.cashier = cashier;
	}
	
	public MarketGreeterRole getHost(){
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
    
    
    
	// Messages
    public void msgAssignedToBusiness(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer>inventory){
    	//don't do anything with deliveryMethod for now
    	print("Received msgAssignedToBusiness");
    	
    	myBusinesses.add(new MyBusiness(restaurantName, cook, cashier, inventory, BusinessState.ordered));
    	stateChanged();
    }
   
	public void msgAssignedToCustomer(MarketCustomerRole c, int waitPosX, int waitPosY){
		print("Received msgAssignedToCustomer");
		
		myCustomers.add(new MyCustomer(c, waitPosX, waitPosY, CustomerState.newCustomer));
		stateChanged();
	}
	
	public void msgHereIsMyOrder(MarketCustomerRole c, Map<String, Integer> inventory, String deliveryMethod) {
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){	//to find the myCustomer with this specific Customer within myCustomers list
			if (thisMC.c.equals(c)){
				mc = thisMC;
				break;
			}
		}
		print("Received msgHereIsMyOrder");
		
		//copy over list
		mc.inventoryOrdered = new TreeMap<String, Integer>(inventory);
		mc.s = CustomerState.ordered;
		stateChanged();
	}
	
	public void msgHereIsBill(MarketCustomerRole c, double amount){		//from cashier
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){	//to find the myCustomer with this specific Customer within myCustomers list
			if (thisMC.c.equals(c)){
				mc = thisMC;
				break;
			}
		}
		print("Received msgHereIsBill");
		mc.billAmount = amount;
		mc.s = CustomerState.gotCheckFromCashier;
		stateChanged();
	}
	public void msgHereIsBill(String name, double amount){		//from cashier
		MyBusiness mb = null;
		for (MyBusiness thisMB : myBusinesses){	//to find the myCustomer with this specific Customer within myCustomers list
			if (thisMB.restaurantName.equals(name)){
				mb = thisMB;
				break;
			}
		}
		print("Received msgHereIsBill");
		mb.billAmount = amount;
		mb.s = BusinessState.gotCheckFromCashier;
		stateChanged();
	}
	
	public void msgOrderFulfilled(MyCustomer mc){		//from timer
		mc.s = CustomerState.doneFulfillingOrder;
		stateChanged();
	}
	public void msgOrderFulfilled(MyBusiness mb){		//from timer
		mb.s = BusinessState.doneFulfillingOrder;
		stateChanged();
	}
	
	
	public void msgDoneAndLeaving(MarketCustomerRole c) {
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){ //to find the myCustomer with this specific Customer within myCustomers list
			if (thisMC.c.equals(c)){
				mc = thisMC;
				break;
			}
		}
		print("Received msgDoneAndLeaving from: " + mc.c.getName());
		mc.s = CustomerState.leaving;
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
			
			for (MyCustomer mc : myCustomers){
				if (mc.s == CustomerState.newCustomer && wState == WaiterState.doingNothing){
					GreetCustomer(mc);
					wState = WaiterState.busy;
					return true;
				}
			}
			
			//business check
			for (MyBusiness mb : myBusinesses) {
				if (mb.s == BusinessState.ordered && wState == WaiterState.doingNothing){
					ProcessOrder(mb);		//determine what we can fulfill, have cashier compute bill
					wState = WaiterState.busy;
					return true;
				}
			}
			for (MyCustomer mc : myCustomers) {
				if (mc.s == CustomerState.ordered && wState == WaiterState.doingNothing){
					ProcessOrder(mc);
					wState = WaiterState.busy;
					return true;
				}
			}
			
			//business check
			for (MyBusiness mb : myBusinesses) {
				if (mb.s == BusinessState.gotCheckFromCashier && wState == WaiterState.doingNothing){
					FulfillOrder(mb);
					wState = WaiterState.busy;
					return true;
				}
			}
			for (MyCustomer mc : myCustomers){
				if (mc.s == CustomerState.gotCheckFromCashier && wState == WaiterState.doingNothing){
					FulfillOrder(mc);
					wState = WaiterState.busy;
					return true;
				}
			}
			
			//business check
			for (MyBusiness mb : myBusinesses) {
				if (mb.s == BusinessState.doneFulfillingOrder && wState == WaiterState.doingNothing){
					DeliverOrder(mb);
					wState = WaiterState.busy;
					return true;
				}
			}
			for (MyCustomer mc : myCustomers) {
				//fulfilling order always takes longer than cashier computing bill
				if (mc.s == CustomerState.doneFulfillingOrder && wState == WaiterState.doingNothing){
					DeliverOrder(mc);
					wState = WaiterState.busy;
					return true;
				}
			}
			

			for (MyCustomer mc : myCustomers) {
				if (mc.s == CustomerState.leaving){
					RemoveCustomer(mc);
					return true;
				}
			}
			
			
			
			//else, if none of these loops or statements were entered, go to home position
			//employeeGui.DoWait();
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

	private void GreetCustomer(MyCustomer mc){
		//gui
		//semaphore
		
		mc.c.msgMayITakeYourOrder(this);
		mc.s = CustomerState.waitingForOrder;
	}
	
	private void ProcessOrder(MyCustomer mc){
		
		for (Map.Entry<String, Integer> entry : mc.inventoryOrdered.entrySet()){
			if (entry.getValue() <= marketMenu.getStock(entry.getKey())){	//if the num desired <= amount market has, add it to the inventoryFulfilled list
				mc.inventoryFulfilled.put(entry.getKey(), entry.getValue());
			}
			else {
				mc.inventoryFulfilled.put(entry.getKey(), (entry.getValue() - marketMenu.getStock(entry.getKey())));
			}
		}
		
		SendBillToCashier(mc);
		
		mc.s = CustomerState.waitingForBill;
		
		//timer for retrieving items// make gui go get them all
		
	}
	
	private void SendBillToCashier(MyCustomer mc){
		//gui to go to cashier
		cashier.msgComputeBill(mc.inventoryFulfilled, mc.c, this);
	}
	
	private void FulfillOrder(final MyCustomer mc){
		//DoFulfillOrder(mc);
		//gui semaphore (timer for now) to gather items from stock room
		
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done cooking, cookie=" + cookie);
				msgOrderFulfilled(mc);
			}
		}, 5000);
	}
	
	private void DeliverOrder(MyCustomer mc){
		//gui to go to customer
		mc.c.msgHereIsYourOrder(mc.inventoryFulfilled, mc.billAmount);
		mc.s = CustomerState.gotOrderAndBill;
	}
	
	
	
	//businesses
	private void ProcessOrder(MyBusiness mb){
		for (Map.Entry<String, Integer> entry : mb.inventoryOrdered.entrySet()){
			if (entry.getValue() <= marketMenu.getStock(entry.getKey())){	//if the num desired <= amount market has, add it to the inventoryFulfilled list
				mb.inventoryFulfilled.put(entry.getKey(), entry.getValue());
			}
			else {		//take everything the market has left
				mb.inventoryFulfilled.put(entry.getKey(), (entry.getValue() - marketMenu.getStock(entry.getKey())));
			}
		}
		
		SendBillToCashier(mb);
		mb.s = BusinessState.waitingForBill;
	}
	
	private void SendBillToCashier(MyBusiness mb){
		//gui to go to cashier
		cashier.msgComputeBill(mb.inventoryFulfilled, mb.restaurantName, this);
	}
	
	private void FulfillOrder(final MyBusiness mb){
		//DoFulfillOrder(mb);
		//gui semaphore (timer for now) to gather items from stock room
		
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done cooking, cookie=" + cookie);
				msgOrderFulfilled(mb);
			}
		}, 4000);
		//mb.s = BusinessState.doneFulfillingOrder;
	}
	
	private void DeliverOrder(MyBusiness mb){
		deliveryMan.msgHereIsOrderForDelivery(mb.restaurantName, mb.cook, mb.cashier, mb.inventoryFulfilled, mb.billAmount);
		mb.s = BusinessState.sentForDelivery;	//unnecessary
		
		//print("Removing: " + mb.r.getName());
		myBusinesses.remove(mb);
	}
	
	
	private void RemoveCustomer(MyCustomer mc){
		//print("Notifying host of leaving customer: " + mc.c.getName());
		//host.msgTableFree(mc.table);
		print("Removing: " + mc.c.getName());
		myCustomers.remove(mc);
	}
	

	
	//utilities


	public void setGui(EmployeeGui gui) {
		employeeGui = gui;
	}

	public EmployeeGui getGui() {
		return employeeGui;
	}

	
	private class MyCustomer {
		MarketCustomerRole c;
		//String deliveryMethod;
		double billAmount;
		CustomerState s;
		
		int waitingAreaX;
		int waitingAreaY;
		
		//List<OrderItem> inventoryOrdered = new ArrayList<OrderItem>();
		Map<String, Integer> inventoryOrdered;
		Map<String, Integer> inventoryFulfilled = new TreeMap<String, Integer>();
		
		MyCustomer(MarketCustomerRole c, int posX, int posY, CustomerState s){
			this.c = c;
			waitingAreaX = posX;
			waitingAreaY = posY;
			this.s = s;
		}
	}
	
	private class MyBusiness{
		String restaurantName;
		//String deliveryMethod;
		double billAmount;
		BusinessState s;
		MainCook cook;
		MainCashier cashier;
		
		Map<String, Integer> inventoryOrdered;
		Map<String, Integer> inventoryFulfilled = new TreeMap<String, Integer>();
		
		MyBusiness(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer>inventory, BusinessState s){
			this.restaurantName = restaurantName;
			this.cook = cook;
			this.cashier = cashier;
			inventoryOrdered = new TreeMap<String, Integer>(inventory);
			this.s = s;
		}
	}
	
}

