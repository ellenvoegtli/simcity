package mainCity.market;

import agent.Agent;
//import restaurant.gui.CustomerGui;
import mainCity.market.gui.*;
import mainCity.market.MarketCashierRole;
import mainCity.market.MarketGreeterRole;
import mainCity.market.MarketMenu;
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
	
	private MarketGreeterRole host;
	private MarketCashierRole cashier;
	private MarketMenu marketMenu;
	
	public EmployeeGui employeeGui = null;
	
	private List<MyCustomer> myCustomers = new ArrayList<MyCustomer>();
	enum CustomerState {waiting, ordered, waitingForOrder, gotBill, gotOrder, leaving};
	
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
   
	public void msgAssignedToCustomer(MarketCustomerRole c, int waitPosX, int waitPosY){
		print("Received msgAssignedToCustomer");
		
		myCustomers.add(new MyCustomer(c, waitPosX, waitPosY, CustomerState.waiting));
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
		mc.inventoryOrdered = inventory;
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
		mc.s = CustomerState.gotBill;
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
				if (mc.s == CustomerState.waiting && wState == WaiterState.doingNothing){
					GreetCustomer(mc);
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
			for (MyCustomer mc : myCustomers) {
				if (mc.s == CustomerState.gotBill && wState == WaiterState.doingNothing){
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
		/*
		for (OrderItem i : mc.inventoryOrdered){
			if (i.numDesired <= marketMenu.getStock(i.itemType)){
				//i.s = OrderItemState.fulfilled;
				i.setOrderItemState("fulfilled");
				i.numFulfilled = i.numDesired;
			}
			else {
				i.numFulfilled = marketMenu.getStock(i.itemType);
				if (i.numFulfilled == 0)
					//i.s = OrderItemState.noneFulfilled;
					i.setOrderItemState("noneFulfilled");
				else
					//i.s = OrderItemState.partFulfilled;
					i.setOrderItemState("partFulfilled");
			}
		}
		*/
		
		for (Map.Entry<String, Integer> entry : mc.inventoryOrdered.entrySet()){
			if (entry.getValue() <= marketMenu.getStock(entry.getKey())){	//if the num desired <= amount market has, add it to the inventoryFulfilled list
				mc.inventoryFulfilled.put(entry.getKey(), entry.getValue());
			}
			else {
				mc.inventoryFulfilled.put(entry.getKey(), (entry.getValue() - marketMenu.getStock(entry.getKey())));
			}
		}
		
		SendBillToCashier(mc);
		
		//timer for retrieving items OR make gui go get them all
		
	}
	
	private void SendBillToCashier(MyCustomer mc){
		//gui to go to cashier
		cashier.msgComputeBill(mc.inventoryFulfilled, mc.c, this);
	}
	
	private void DeliverOrder(MyCustomer mc){
		//gui to go to customer
		mc.c.msgHereIsYourOrder(mc.inventoryFulfilled, mc.billAmount);
		mc.s = CustomerState.gotOrder;
	}
	
	
	private void RemoveCustomer(MyCustomer mc){
		//print("Notifying host of leaving customer: " + mc.c.getName());
		//host.msgTableFree(mc.table);
		print("Removing: " + mc.c.getName());
		myCustomers.remove(mc);
	}
	
	/*
	private void seatCustomer(MyCustomer mc) {
		employeeGui.DoPickUpWaitingCustomer(mc.waitingAreaX + 20, mc.waitingAreaY + 20);
		try {
			atStart.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mc.c.msgFollowMe(new Menu(), mc.table, this);	//WaiterAgent is now at start location
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
	private void DoSeatCustomer(Customer customer, int table) {
		print("Seating " + customer + " at " + table);
		
		employeeGui.DoGoToTable(table);
	}
	
	private void takeOrder(MyCustomer mc){
		employeeGui.DoGoToTable(mc.table);
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
	
	private void warnCustomerOutOfFood(MyCustomer mc){
		employeeGui.DoGoToTable(mc.table);
		try {
			atTable.acquire();
			//atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Menu menu = new Menu();
		menu.menuItems.remove(mc.choice);			
		mc.c.msgOutOfFoodPleaseReorder(menu); 	//need to delete the mc.choice from the menu though
		mc.s = CustomerState.deciding;			//don't do anything yet
	}
	
	private void sendOrderToCook(MyCustomer mc){
		print("Going to send order to cook");
		
		employeeGui.DoGoToCook();
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
	
	public void deliverFood(MyCustomer mc){
		print("Going to pick up finished order");
		employeeGui.DoGoToCook();
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
		
		employeeGui.DoDeliverFood(mc.table, mc.choice);	//sends destination & details for waiter's food text label to waiter
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
		employeeGui.setIsDeliveringFood(false);	//lets animation remove food text label from waiter
	}
	
	private void sendCheck(MyCustomer mc){
		employeeGui.DoGoToTable(mc.table);
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
	*/
	
	/* not really necessary...
	private void notifyHost(MyCustomer mc){
		print("Notifying host of leaving customer: " + mc.c.getName());
		//host.msgTableFree(mc.table);
		print("Removing: " + mc.c.getName());
		myCustomers.remove(mc);
	}
	*/
	
	//utilities


	public void setGui(EmployeeGui gui) {
		employeeGui = gui;
	}

	public EmployeeGui getGui() {
		return employeeGui;
	}

	
	private class MyCustomer {
		MarketCustomerRole c;
		String deliveryMethod;
		double billAmount;
		CustomerState s;
		
		int waitingAreaX;
		int waitingAreaY;
		
		//List<OrderItem> inventoryOrdered = new ArrayList<OrderItem>();
		Map<String, Integer> inventoryOrdered = new TreeMap<String, Integer>();
		Map<String, Integer> inventoryFulfilled = new TreeMap<String, Integer>();
		
		MyCustomer(MarketCustomerRole c, int posX, int posY, CustomerState s){
			this.c = c;
			waitingAreaX = posX;
			waitingAreaY = posY;
			this.s = s;
		}
	}
	
	/*
	private class OrderItem {
		int numDesired;
		int numFulfilled;
		String itemType;
		OrderItemState s;
		
		OrderItem(){
			
		}
	}
	*/
	
}

