package mainCity.market;

import agent.Agent;
//import restaurant.gui.CustomerGui;
import mainCity.market.gui.*;
import mainCity.market.*;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.*;
import role.Role;
import mainCity.PersonAgent;

import java.util.*;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import role.market.MarketDeliveryManRole;



/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class MarketEmployeeRole extends Role {
	private String name;
	Timer timer = new Timer();
	
	private MarketGreeterRole host;
	private MarketCashierRole cashier;
	private MarketDeliveryManRole deliveryMan;
	private MarketMenu marketMenu = new MarketMenu();
	
	public EmployeeGui employeeGui = null;
	
	private List<MyCustomer> myCustomers = new ArrayList<MyCustomer>();
	private List<MyBusiness> myBusinesses = new ArrayList<MyBusiness>();
	enum CustomerState {newCustomer, waitingForOrder, ordered, waitingForBill, fulfillingOrder, doneFulfillingOrder, gotCheckFromCashier, gotOrderAndBill, leaving};
	enum BusinessState {ordered, waitingForBill, fulfillingOrder, waiting, doneFulfillingOrder, gotCheckFromCashier, sentForDelivery};
	
	WaiterState wState;
	enum WaiterState {doingNothing, busy};
	
	private Semaphore atStation = new Semaphore(0,true);
	private Semaphore atCashier = new Semaphore(0, true);
	private Semaphore atWaitingRoom = new Semaphore(0, true);
	private Semaphore atDeliveryMan = new Semaphore(0, true);

		

	public MarketEmployeeRole(PersonAgent p, String name) {
		super(p);
		this.name = name;
	}
	
	public void setHost(MarketGreeterRole host){
		this.host = host;
	}
	
	public void setCashier(MarketCashierRole cashier){
		this.cashier = cashier;
	}
	
	public void setDeliveryMan(MarketDeliveryManRole d){
		this.deliveryMan = d;
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
    	//print("Received msgAssignedToBusiness");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msgAssignedToBusiness");

    	myBusinesses.add(new MyBusiness(restaurantName, cook, cashier, inventory, BusinessState.ordered));
    	stateChanged();
    }
   
	public void msgAssignedToCustomer(MarketCustomerRole c, int waitPosX, int waitPosY){
		//print("Received msgAssignedToCustomer");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Recevied msgAssignedToCustomer");
		
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
		//print("Received msgHereIsMyOrder");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msgHereIsMyOrder");

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
		//print("Received msgHereIsBill");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msgHereIsBill for " + c.getName() + ": $" + amount);
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
		//print("Received msgHereIsBill");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msgHereIsBill for " + name + ": $" + amount);
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
		//print("Received msgDoneAndLeaving from: " + mc.c.getName());
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msgDoneAndLeaving from: " + mc.c.getName());
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
		//print("msgAtCashier called");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "msgAtCashier called");
		atCashier.release();
		stateChanged();
	}
	public void msgAtWaitingRoom(){
		//print("msgAtWaitingRoom called");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "msgAtWaitingRoom called");
		atWaitingRoom.release();
		stateChanged();
	}
	public void msgAtDeliveryMan(){
		//print("msgAtDeliveryMan called");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "msgAtDeliveryMan called");
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

	private void GreetCustomer(MyCustomer mc){
		employeeGui.DoPickUpWaitingCustomer(mc.waitingAreaX, mc.waitingAreaY);
		try {
			atWaitingRoom.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mc.c.msgFollowMe(this, this.getGui().homeX, this.getGui().homeY - 17);	//****don't access these directly
		employeeGui.DoGoToStation();		//sometimes doesn't go all the way to the station before going to the cashier...
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
		
		for (Map.Entry<String, Integer> entry : mc.inventoryOrdered.entrySet()){
			//print("entry: key = " + entry.getKey());
			//print("entry: value = " + entry.getValue());
			//print("marketMenu: stock = " + marketMenu.getStock(entry.getKey()));
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
		employeeGui.DoFulfillOrder();		//in actual implementation, pass in inventory strings?
		//gui semaphore (timer for now) to gather items from stock room
		
		timer.schedule(new TimerTask() {
			public void run() {
				msgOrderFulfilled(mc);
			}
		}, 5000);
		
		//mc.s = CustomerState.waiting;
	}
	
	private void DeliverOrder(MyCustomer mc){
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
	
	
	
	//businesses
	private void ProcessOrder(MyBusiness mb){
		//print("Processing order for " + mb.restaurantName);
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Processing order for " + mb.restaurantName);
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
		employeeGui.DoFulfillOrder();
		//print("Fulfilling order for " + mb.restaurantName);
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Fulfilling order for " + mb.restaurantName);
		//gui semaphore (timer for now) to gather items from stock room
		
		timer.schedule(new TimerTask() {
			public void run() {
				msgOrderFulfilled(mb);
			}
		}, 5000);
	}
	
	private void DeliverOrder(MyBusiness mb){
		//print("Delivering order to delivery man");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Delivering order to delivery man");
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
		
		myBusinesses.remove(mb);
	}
	
	
	private void RemoveCustomer(MyCustomer mc){
		//print("Removing: " + mc.c.getName());
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Removing: " + mc.c.getName());
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
		double billAmount;
		CustomerState s;
		
		int waitingAreaX;
		int waitingAreaY;
		
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

