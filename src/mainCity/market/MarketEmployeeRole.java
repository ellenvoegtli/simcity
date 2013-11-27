package mainCity.market;

import agent.Agent;
import mainCity.PersonAgent;
//import restaurant.gui.CustomerGui;
import mainCity.market.gui.*;
import mainCity.market.interfaces.*;
import mainCity.market.*;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.*;
import role.Role;
import role.market.MarketDeliveryManRole.AgentState;

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
public class MarketEmployeeRole extends Role implements Employee {
	private String name;
	Timer timer = new Timer();
	
	private Greeter host;
	private MarketCashier cashier;
	private DeliveryMan deliveryMan;
	private MarketMenu marketMenu = new MarketMenu();
	
	public EmployeeGuiInterface employeeGui = null;
	private int homeX, homeY;
	
	public List<MyCustomer> myCustomers = new ArrayList<MyCustomer>();
	public List<MyBusiness> myBusinesses = new ArrayList<MyBusiness>();
	public enum CustomerState {newCustomer, waitingForOrder, ordered, waitingForBill, fulfillingOrder, doneFulfillingOrder, gotCheckFromCashier, gotOrderAndBill, leaving};
	public enum BusinessState {ordered, waitingForBill, fulfillingOrder, waiting, doneFulfillingOrder, gotCheckFromCashier, sentForDelivery};
	
	public WaiterState wState;
	public enum WaiterState {doingNothing, busy};
	
	private Semaphore atStation = new Semaphore(0,true);
	private Semaphore atCashier = new Semaphore(0, true);
	private Semaphore atWaitingRoom = new Semaphore(0, true);
	private Semaphore atDeliveryMan = new Semaphore(0, true);

		

	public MarketEmployeeRole(PersonAgent p, String name) {
		super(p);
		this.name = name;
	}
	
	public void setHost(Greeter host){
		this.host = host;
	}
	
	public void setCashier(MarketCashier cashier){
		this.cashier = cashier;
	}
	public void setDeliveryMan(DeliveryMan d){
		this.deliveryMan = d;
	}
	public void setHomeX(int x){
		homeX = x;
	}
	public void setHomeY(int y){
		homeY = y;
	}
	public Greeter getHost(){
		return this.host;
	}
	public WaiterState getState(){
		return wState;
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
	public Collection getMyBusinesses(){
		return myBusinesses;
	}

    public boolean isReady() {
    	return (wState == WaiterState.doingNothing);
    }
    
	//for alert log trace statements
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.MARKET_EMPLOYEE, this.getName(), s);
	}
    
	// Messages
    public void msgAssignedToBusiness(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer>inventory){
    	log("Received msgAssignedToBusiness");
    	myBusinesses.add(new MyBusiness(restaurantName, cook, cashier, inventory, BusinessState.ordered));
    	stateChanged();
    }
   
	public void msgAssignedToCustomer(Customer c, int waitPosX, int waitPosY){
		log("Received msgAssignedToCustomer");		
		myCustomers.add(new MyCustomer(c, waitPosX, waitPosY, CustomerState.newCustomer));
		stateChanged();
	}
	
	public void msgHereIsMyOrder(Customer c, Map<String, Integer> inventory, String deliveryMethod) {
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){	//to find the myCustomer with this specific Customer within myCustomers list
			if (thisMC.c.equals(c)){
				mc = thisMC;
				break;
			}
		}
		log("Received msgHereIsMyOrder");
		//copy over list
		mc.inventoryOrdered = new TreeMap<String, Integer>(inventory);
		mc.s = CustomerState.ordered;
		stateChanged();
	}
	
	public void msgHereIsBill(Customer c, double amount){		//from cashier
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){	//to find the myCustomer with this specific Customer within myCustomers list
			if (thisMC.c.equals(c)){
				mc = thisMC;
				break;
			}
		}
		log("Received msgHereIsBill from cashier. Amount = $" + amount);
		mc.billAmount = amount;
		mc.s = CustomerState.gotCheckFromCashier;
		stateChanged();
	}
	
	//for businesses
	public void msgHereIsBill(String name, double amount){		//from cashier
		MyBusiness mb = null;
		for (MyBusiness thisMB : myBusinesses){	//to find the myCustomer with this specific Customer within myCustomers list
			if (thisMB.restaurantName.equals(name)){
				mb = thisMB;
				break;
			}
		}
		log("Received msgHereIsBill");
		mb.billAmount = amount;
		mb.s = BusinessState.gotCheckFromCashier;
		stateChanged();
	}
	
	public void msgOrderFulfilled(MyCustomer mc){		//from timer
		log("Done fulfilling order!");
		mc.s = CustomerState.doneFulfillingOrder;
		stateChanged();
	}
	public void msgOrderFulfilled(MyBusiness mb){		//from timer
		log("Done fulfilling order!");
		mb.s = BusinessState.doneFulfillingOrder;
		stateChanged();
	}
	
	
	public void msgDoneAndLeaving(Customer c) {
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){ //to find the myCustomer with this specific Customer within myCustomers list
			if (thisMC.c.equals(c)){
				mc = thisMC;
				break;
			}
		}
		log("Received msgDoneAndLeaving from: " + mc.c.getName());
		mc.s = CustomerState.leaving;
		stateChanged();
	}
	


	public void msgAtStation() {
		//print("msgAtStation called");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "msgAtStation called");
		atStation.release();// = true;
		stateChanged();
	}
	public void msgAtCashier(){
		log("msgAtCashier called");
		atCashier.release();
		stateChanged();
	}
	public void msgAtWaitingRoom(){
		log("msgAtWaitingRoom called");
		atWaitingRoom.release();
		stateChanged();
	}
	public void msgAtDeliveryMan(){
		log("msgAtDeliveryMan called");
		atDeliveryMan.release();
		stateChanged();
	}
	
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
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

	
	//=========================== IN-STORE CUSTOMERS ==============================================================
	private void GreetCustomer(MyCustomer mc){
		log("Going to pick up customer: " + mc.c.getName());
		employeeGui.DoPickUpWaitingCustomer(mc.waitingAreaX, mc.waitingAreaY);
		try {
			atWaitingRoom.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mc.c.msgFollowMe(this, homeX, homeY - 17);
		employeeGui.DoGoToStation();		//sometimes doesn't go all the way to the station before going to the cashier
		try {
			atStation.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mc.c.msgMayITakeYourOrder(this);
		mc.s = CustomerState.waitingForOrder;
	}
	
	private void ProcessOrder(MyCustomer mc){
		log("Processing order for " + mc.c.getName());
		for (Map.Entry<String, Integer> entry : mc.inventoryOrdered.entrySet()){
			if (entry.getValue() <= marketMenu.getStock(entry.getKey())){	//if the num desired <= amount market has, add it to the inventoryFulfilled list
				mc.inventoryFulfilled.put(entry.getKey(), entry.getValue());
			}
			else {
				mc.inventoryFulfilled.put(entry.getKey(), (entry.getValue() - marketMenu.getStock(entry.getKey())));
			}
		}
		
		mc.s = CustomerState.waitingForBill;
		
		SendBillToCashier(mc);
	}
	
	private void SendBillToCashier(MyCustomer mc){
		log("Sending bill to cashier");
		employeeGui.DoGoToCashier();
		try {
			atCashier.acquire();
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cashier.msgComputeBill(mc.inventoryFulfilled, mc.c, this);
	}
	
	private void FulfillOrder(final MyCustomer mc){
		log("Fulfilling order for: " + mc.c.getName());
		employeeGui.DoFulfillOrder();
		//gui semaphore (timer for now) to gather items from stock room
		
		timer.schedule(new TimerTask() {
			public void run() {
				msgOrderFulfilled(mc);
			}
		}, 7000);
	}
	
	private void DeliverOrder(MyCustomer mc){
		log("Delivering order to: " + mc.c.getName());
		employeeGui.DoGoToStation();
		try {
			atStation.acquire();
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mc.c.msgHereIsYourOrder(mc.inventoryFulfilled, mc.billAmount);
		mc.s = CustomerState.gotOrderAndBill;
	}
	
	private void RemoveCustomer(MyCustomer mc){
		log("Removing customer: " + mc.c.getName());
		myCustomers.remove(mc);
	}
	
	
	
	//=========================== BUSINESSES ===========================================================================
	private void ProcessOrder(MyBusiness mb){
		log("Processing order for " + mb.restaurantName);
		for (Map.Entry<String, Integer> entry : mb.inventoryOrdered.entrySet()){
			if (entry.getValue() <= marketMenu.getStock(entry.getKey())){	//if the num desired <= amount market has, add it to the inventoryFulfilled list
				mb.inventoryFulfilled.put(entry.getKey(), entry.getValue());
			}
			else {		//take everything the market has left
				mb.inventoryFulfilled.put(entry.getKey(), (entry.getValue() - marketMenu.getStock(entry.getKey())));
			}
		}
		
		SendBillToCashier(mb);
	}
	
	private void SendBillToCashier(MyBusiness mb){
		log("Sending bill to cashier");
		//gui to go to cashier
		employeeGui.DoGoToCashier();
		try {
			atCashier.acquire();
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cashier.msgComputeBill(mb.inventoryFulfilled, mb.restaurantName, this);
		mb.s = BusinessState.waitingForBill;
	}
	
	private void FulfillOrder(final MyBusiness mb){
		log("Fulfilling order for: " + mb.restaurantName);
		employeeGui.DoFulfillOrder();
		
		timer.schedule(new TimerTask() {
			public void run() {
				msgOrderFulfilled(mb);
			}
		}, 7000);
	}
	
	private void DeliverOrder(MyBusiness mb){
		log("Delivering order to delivery man");
		employeeGui.DoGoToDeliveryMan();
		try {
			atDeliveryMan.acquire();
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		deliveryMan.msgHereIsOrderForDelivery(mb.restaurantName, mb.cook, mb.cashier, mb.inventoryFulfilled, mb.billAmount);
		mb.s = BusinessState.sentForDelivery;	//unnecessary
		employeeGui.DoGoToStation();
		
		log("Removing customer: " + mb.restaurantName);
		myBusinesses.remove(mb);
	}

	

	
	//utilities


	public void setGui(EmployeeGuiInterface gui) {
		employeeGui = gui;
	}

	public EmployeeGuiInterface getGui() {
		return employeeGui;
	}

	
	public class MyCustomer {		//public only for testing purposes
		Customer c;
		double billAmount;
		CustomerState s;
		
		int waitingAreaX;
		int waitingAreaY;
		
		Map<String, Integer> inventoryOrdered;
		Map<String, Integer> inventoryFulfilled = new TreeMap<String, Integer>();
		
		MyCustomer(Customer c, int posX, int posY, CustomerState s){
			this.c = c;
			waitingAreaX = posX;
			waitingAreaY = posY;
			this.s = s;
		}
		
		public CustomerState getState(){
			return s;
		}
		public Customer getCustomer(){
			return c;
		}
		public double getBillAmount(){
			return billAmount;
		}
	}
	
	public class MyBusiness{		//public only for testing purposes
		String restaurantName;
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
		public String getRestaurant(){
			return restaurantName;
		}
		public double getBillAmount(){
			return billAmount;
		}
		
		
	}
	
}

