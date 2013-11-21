package mainCity.restaurants.restaurant_zhangdt;

import agent.Agent;
import mainCity.restaurants.restaurant_zhangdt.CustomerAgent.AgentState;
import mainCity.restaurants.restaurant_zhangdt.gui.WaiterGui;
import mainCity.restaurants.restaurant_zhangdt.CookAgent.Order; 
import mainCity.restaurants.restaurant_zhangdt.gui.RestaurantGui;
import mainCity.restaurants.restaurant_zhangdt.interfaces.Waiter;

import java.util.*;
import java.util.concurrent.Semaphore;

import mainCity.restaurants.restaurant_zhangdt.HostAgent.Table; 

/**
 * Restaurant Waiter Agent
 */

public class WaiterAgent extends Agent implements Waiter {

/*   Data   */	

	public enum CustomerStates 
	{none, Waiting, Seated, ReadyToOrder, Ordering, Ordered, WaitingForOrder, OrderIsReady, OutOfFood, Eating, ReadyForCheck, WaitingForCheck, CheckIsReady, CheckDelivered, Leaving, };
	
	public enum WaiterStates 
	{none, AskForBreak, GotBreak, ComingOffBreak, OffBreak, breakGranted, breakDenied, onBreak, notOnBreak}; 
	private WaiterStates wState = WaiterStates.none;
	
	private RestaurantGui restGui;
	
	class myCustomer { 
		CustomerAgent c; 
		Table t; 
		String orderChoice; 
		CustomerStates custState = CustomerStates.none; 
		
	}
	
	class Menu {
		public String choices[] = {"Steak", "Chicken", "Salad", "Pizza"}; 
		public double prices[] = {15.99, 10.99, 5.99, 8.99}; 
		int size = 4;
	}
	
	Menu menu = new Menu();
	
	List<myCustomer> customerList = new ArrayList<myCustomer>(); 
	List<Order> orderList = new ArrayList<Order>();
	
	private HostAgent hAgent; 
	private CookAgent cookAgent; 
	private CashierAgent cashierAgent;
	
	private String name;
	
	//False when waiter is interacting with host
	private boolean requestingBreak = true;
	
	private boolean onBreak = false;
	
	Order currentOrder; 
	private String orderChoice;
	private double bill;
	
	private Semaphore atTable = new Semaphore(0,true);
	private Semaphore hostFree = new Semaphore(0, true); 
	private Semaphore atCook = new Semaphore(0, true); 
	private Semaphore atCashier = new Semaphore(0,true);
	private Semaphore WaiterFree = new Semaphore(0, true);
	
	public WaiterGui waiterGui = null;

	public WaiterAgent(String name) {
		super();
		this.name = name;
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}
	
	public void setHost(HostAgent h) {
		hAgent = h;
	}

	public void setCook(CookAgent c){ 
		cookAgent = c; 
	}
	
	public void setCashier(CashierAgent c){
		cashierAgent = c;
	}
	
/*   Messages   */
	
	public void msgSeatCustomer(CustomerAgent cust, Table table) { 
		print("msgSeatCustomer() called"); 
		myCustomer newCustomer = new myCustomer();
		newCustomer.c = cust; 
		newCustomer.t = table;
		newCustomer.custState = CustomerStates.Waiting;  
		customerList.add(newCustomer); 
		stateChanged();
	}
	
	public void msgReadyToOrder(CustomerAgent c) {
		print("msgReadyToOrder() called");
		for(myCustomer customer : customerList){ 
			if(c == customer.c){ 
				customer.custState = CustomerStates.ReadyToOrder; 
			}
		}
		stateChanged(); 
	}
	
	
	public void msgHeresMyOrder(CustomerAgent c, String o){ 
		print("msgHeresMyOrder() called"); 
		for(myCustomer customer : customerList){ 
			if(c == customer.c){ 
				print("Current Order " + o);
				customer.orderChoice = o; 
				customer.custState = CustomerStates.Ordered; 
			}
		}
		stateChanged(); 
	}
	
	public void msgOrderIsReady(myCustomer c, Order o){
		print("msgOrderIsReady() called"); 
		c.custState = CustomerStates.OrderIsReady; 
		currentOrder = o;
		orderChoice = o.Choice;
		stateChanged();
	}
	
	public void msgFinishedTakingOrder() { 
		if(atCook.availablePermits() < 1) {
			atCook.release(); 
			stateChanged(); 
		} 
	} 
	
	public void msgFinishedDeliveringFood() {
		if(atTable.availablePermits() < 1) {
			atTable.release(); 
			stateChanged(); 
		}
	} 
	
	public void msgOutOfFood(String c) {
		for(int i=0; i<customerList.size(); i++) {
			if (c == customerList.get(i).orderChoice){
				customerList.get(i).custState = CustomerStates.OutOfFood; 
			}
		}
		stateChanged();
	}
	
	public void msgReadyForCheck(CustomerAgent c){ 
		for(myCustomer customer : customerList){ 
			if(c == customer.c){ 
				customer.custState = CustomerStates.ReadyForCheck; 
			}
		}
		stateChanged();
	}
	
	public void msgBringCheckToCustomer(double price, int tableNumber){ 
		for(int i=0; i<customerList.size(); i++){ 
			if(customerList.get(i).t.tableNumber == tableNumber){
				customerList.get(i).custState = CustomerStates.CheckIsReady;
			}
		}
		bill = price;
		stateChanged();
	}
	
	public void msgAtCashier(){ 
		print ("msgAtCashier called"); 
		if(atCashier.availablePermits() < 1) { 
			atCashier.release(); 
			stateChanged();
		}
	}
	
	public void msgCustomerLeaving(CustomerAgent c) {
		//print ("msgCustomerLeaving() called"); 
		for(myCustomer customer : customerList){ 
			if(c == customer.c){
				customer.custState = CustomerStates.Leaving; 
			}
		}
		stateChanged();
	}
	
	public void msgAtTable() {  //from animation
		print("msgAtTable() called");
		if(atTable.availablePermits() < 1){
			atTable.release();// = true;
			stateChanged();
		}
	}
	
	public void msgHostFree() {
		print("msgHostFree() called");
		if(hostFree.availablePermits() < 1) {
			hostFree.release();
			stateChanged();
		}
	}
	
	public void msgWaiterFree() { 
		if(WaiterFree.availablePermits() < 1) {
			WaiterFree.release();
			stateChanged();
		}
	}

	public void msgAskForBreak(){
		print("msgAskForBreak called"); 
		wState = WaiterStates.AskForBreak;
		setRequestingBreak(false);
		stateChanged();
	}
	
	public void msgOffBreak() {
		print("msgOffBreak called"); 
		wState = WaiterStates.ComingOffBreak;
		stateChanged();
	}
	
	public void msgBreakGranted() { 
		print("msgBreakGranted called");
		wState = WaiterStates.breakGranted;
		stateChanged();
	}
	
	public void msgBreakDenied() {
		print("msgBreakDenied called");
		wState = WaiterStates.breakDenied;
		stateChanged();
	}
	
    
/*   Scheduler   */
	
	protected boolean pickAndExecuteAnAction() {
		try{
			if(customerList.size() != 0) {
				for(myCustomer customer : customerList) {
					if(customer.custState == CustomerStates.Waiting) {
						seatCustomer(menu, customer);
						return true;
					}
					if(customer.custState == CustomerStates.ReadyToOrder){ 
						WhatWouldYouLike(customer); 
						return true;
					}
					if(customer.custState == CustomerStates.Ordered){
						HeresAnOrder(customer);
						return true;
					}
					if(customer.custState == CustomerStates.OrderIsReady){
						if(currentOrder != null){
							if(currentOrder.tableNum == customer.t.tableNumber){
								HereIsYourFood(customer);
								return true; 
							}
						}
						
					}
					if(customer.custState == CustomerStates.ReadyForCheck){
						GrabCheck(customer); 
						return true;
					}
					if(customer.custState == CustomerStates.CheckIsReady){ 
						DeliverCheck(customer);
						return true;
					}
					if(customer.custState == CustomerStates.Leaving){
						CustomerLeaving(customer);
						return true;
					}
					if(customer.custState == CustomerStates.OutOfFood) {
						OrderUnavailable(customer); 
						return true;
					}
				}
			}
		
		
			if(wState == WaiterStates.AskForBreak){
				RequestBreak();
				return true;
			}
			
			if(wState == WaiterStates.ComingOffBreak){
				ComeOffBreak();
				return true;
			}
			
			if(wState == WaiterStates.breakGranted){ 
				breakGranted(); 
				return true;
			}
			
			if(wState == WaiterStates.breakDenied){ 
				breakDenied();
				return true;
			}
			
			
			
			hostFree.tryAcquire();
			waiterGui.DoLeaveCustomer();
			return false;
			//we have tried all our rules and found
			//nothing to do. So return false to main loop of abstract agent
			//and wait.
		}
		catch(ConcurrentModificationException r){ 
			return false;
		}
	}

/*   Actions   */

	private void seatCustomer(Menu m, myCustomer customer) {
		
		if(this.waiterGui.getXPos() != 60 && this.waiterGui.getYPos() != 60) {
			waiterGui.DoLeaveCustomer();
			try {
				hostFree.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		for(int i=0; i<m.size; i++){
			customer.c.addToMenu(m.choices[i]);
		}
		
		waiterGui.DoMoveToCustomer();
		try {
			WaiterFree.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		customer.c.msgFollowWaiter(customer.t.getTable(), customer.t.getX(), customer.t.getY());
		DoSeatCustomer(customer.c, customer.t);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		customer.custState = CustomerStates.Seated;
		
	}

	// The animation DoXYZ() routines
	private void DoSeatCustomer(CustomerAgent customer, Table table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer + " at " + table);
		print("table x: " + table.getX() + " table y: " + table.getY());
		waiterGui.DoBringToTable(customer, table.getX(), table.getY());  

	}
	
	private void WhatWouldYouLike(myCustomer customer) { 
		hostFree.tryAcquire();
		waiterGui.DoBringToTable(customer.c, customer.t.getX(), customer.t.getY()); 
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		customer.c.msgWhatWouldYouLike(); 
		customer.custState = CustomerStates.Ordering; 
	}
	
	private void HeresAnOrder(myCustomer customer){
		customer.custState = CustomerStates.WaitingForOrder; 
		hostFree.tryAcquire();
		waiterGui.DoMoveToCook();
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cookAgent.msgHereIsAnOrder(this, customer.orderChoice, customer.t.tableNumber);
	}
	
	private void HereIsYourFood(myCustomer customer){ 
		hostFree.tryAcquire();
		waiterGui.DoMoveToCook(); 
		try {
			atCook.acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		waiterGui.msgOrderInHand();
		waiterGui.DoBringToTable(customer.c, customer.t.getX(), customer.t.getY());
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		customer.c.msgHereIsYourFood();
		waiterGui.msgDelivered();
		customer.custState = CustomerStates.Eating; 
	}
	
	private void GrabCheck(myCustomer customer) {
		customer.custState = CustomerStates.WaitingForCheck; 
		hostFree.tryAcquire(); 
		waiterGui.DoBringToTable(customer.c, customer.t.getX(), customer.t.getY());
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		waiterGui.DoMoveToCashier(); 
		try {
			atCashier.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		cashierAgent.msgHeresACheck(this, customer.orderChoice, customer.t.tableNumber);
	}
	
	private void DeliverCheck(myCustomer customer) { 
		print("Delivering Check");
		hostFree.tryAcquire(); 
		waiterGui.DoBringToTable(customer.c, customer.t.getX(), customer.t.getY());
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		customer.custState = CustomerStates.CheckDelivered;
		customer.c.msgCheckIsRecieved(bill); 
	}
	
	private void CustomerLeaving(myCustomer customer) {
		hAgent.msgLeavingTable(customer.c); 
		customerList.remove(customer);
	}
	
	private void OrderUnavailable(myCustomer customer) {
		hostFree.tryAcquire(); 
		waiterGui.DoBringToTable(customer.c, customer.t.getX(), customer.t.getY());
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		customer.c.msgOrderUnavailable(); 
		customer.custState = CustomerStates.Ordering; 
	}
	
	private void RequestBreak(){
		hAgent.msgRequestBreak(this);
		wState = WaiterStates.GotBreak;
	}
	
	private void ComeOffBreak(){
		hAgent.msgImOffBreak(this);
		wState = WaiterStates.OffBreak;
	}
	
	private void breakGranted() { 
		print("break granted"); 
		wState = WaiterStates.onBreak;
		setOnBreak(true);
		setRequestingBreak(true); 
		stateChanged(); 
	}
	
	private void breakDenied() { 
		print("break denied");
		restGui.deselectBreakCB();
		wState = WaiterStates.notOnBreak;
		setOnBreak(false);
		setRequestingBreak(true); 
		stateChanged(); 
	}

//utilities

	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}
	
	public void setRGui(RestaurantGui gui){ 
		restGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}
	
	public CookAgent getCook() {
		return cookAgent; 
	}
	
	public List<myCustomer> getCustomerList() { 
		return customerList; 
	}
	
	public Order getCurrentOrder() {
		return currentOrder;
	}
	
	public String getOrder() {
		return orderChoice;
	}

	public boolean isRequestingBreak() {
		return requestingBreak;
	}

	public void setRequestingBreak(boolean onBreak) {
		this.requestingBreak = onBreak;
	}

	public boolean isOnBreak() {
		return onBreak;
	}

	public void setOnBreak(boolean onBreak) {
		this.onBreak = onBreak;
	}


}


