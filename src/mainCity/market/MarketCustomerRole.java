package mainCity.market;

import mainCity.market.MarketEmployeeRole;

//import market.interfaces.*;

import agent.Agent;
import mainCity.market.gui.*;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.*;
import java.util.List;
import java.awt.*;

/**
 * Restaurant customer agent.
 */
public class MarketCustomerRole extends Agent{
	private String name;
	private int hungerLevel = 5;        // determines length of meal
	Timer timer = new Timer();
	private CustomerGui customerGui;

	// agent correspondents
	private MarketGreeterRole host;
	private MarketEmployeeRole employee;
	private MarketCashierRole cashier;
	private MarketMenu marketMenu = new MarketMenu();
	
	private int stationX;
	private int stationY;
	
	//private int checkAmount;
	private int myCash;
	private int cashOwed = 0;
	String deliveryMethod;
	//List<OrderItem> inventoryToOrder;
	Map<String, Integer> inventoryToOrder;
	
	Bill bill;
	enum BillState {unpaid, paid};

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInMarket, WaitingForEmployee, GoingToStation, Ordering, OrderProcessing, WaitingForOrder, Paying,
		WaitingForChange, Leaving};
	private AgentState state = AgentState.DoingNothing;//The start state
	
	
	public enum AgentEvent 
	{none, toldToGetInventory, toldToWaitForEmployee, followEmployee, atStation, askedForOrder, gotOrderAndBill, 
		atCashierAgent, gotChange, doneLeaving};
	AgentEvent event = AgentEvent.none;

	
	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public MarketCustomerRole(String name){
		super();
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
	public void setHost(MarketGreeterRole host) {
		this.host = host;
	}
	
	public void setCashier(MarketCashierRole c){
		this.cashier = c;
	}

	public String getCustomerName() {
		return name;
	}
	
	public int getMyCash(){
		return myCash;
	}
	// Messages

	
	public void goGetInventory(Map<String, Integer> inventoryNeeded){
		print("Told to go to market to order inventory");
		this.inventoryToOrder = inventoryNeeded;	//does this work?
		for (Map.Entry<String, Integer> entry : inventoryToOrder.entrySet()){
			System.out.println("Entry: " + entry.getKey() + "; Value: " + entry.getValue());
		}
		
		event = AgentEvent.toldToGetInventory;
		stateChanged();
	}

	public void msgFollowMe(MarketEmployeeRole e, int x, int y){
		print("Received msgFollowMe");
		stationX = x;
		stationY = y - 20;
		employee = e;
		
		event = AgentEvent.followEmployee;
		stateChanged();
	}

	public void msgMayITakeYourOrder(MarketEmployeeRole e){
		print("Received msgMayITakeYourOrder from: " + e.getName());
		//employee = e;
		event = AgentEvent.askedForOrder;
		stateChanged();
	}
	
	public void msgHereIsYourOrder(Map<String, Integer> inventoryFulfilled, double amount){
		print("Received msgHereIsYourOrder");
		bill = new Bill(inventoryFulfilled, amount);
		event = AgentEvent.gotOrderAndBill;
		stateChanged();
	}
	
	public void msgHereIsYourChange(double amountChange, double amountCharged){
		print("Received msgHereIsYourChange: " + amountChange);
		bill.changeReceived = amountChange;
		
		event = AgentEvent.gotChange;
		stateChanged();
	}
	
	//need to modify********
	public void msgNotEnoughCash(int cashOwed){
		print("Received msg NotEnoughCash: I owe $" + cashOwed);
		this.cashOwed += cashOwed;
		//event = AgentEvent.assignedPunishment;
		stateChanged();
	}
	
	//Messages from animation
	/*
	public void msgAnimationFinishedGoToSeat() {
		event = AgentEvent.seated;
		stateChanged();
	}
	public void msgAnimationFinishedGoToStation(){
		event = AgentEvent.atStation;
		stateChanged();
	}
	*/
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
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine

		if (state == AgentState.DoingNothing && event == AgentEvent.toldToGetInventory ){
			state = AgentState.WaitingInMarket;
			GoToMarket();
			return true;
		}
		if (state == AgentState.WaitingInMarket && event == AgentEvent.followEmployee){
			state = AgentState.GoingToStation;
			GoToEmployeeStation();
			return true;
		}
		if (state == AgentState.GoingToStation && event == AgentEvent.askedForOrder){
			state = AgentState.OrderProcessing;
			PlaceOrder();
			return true;
		}
		if (state == AgentState.OrderProcessing && event == AgentEvent.gotOrderAndBill){
			state = AgentState.Paying;
			PayBill();		//verifies bill first
			return true;
		}
		if (state == AgentState.Paying && event == AgentEvent.gotChange){
			state = AgentState.Leaving;
			LeaveMarket();	//verifies change first
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
	private void GoToMarket(){
		Do("Going to market");
		customerGui.DoGoToWaitingArea();
		host.msgINeedInventory(this, this.getGui().getWaitingPosX(), this.getGui().getWaitingPosY());
	}
	
	private void GoToEmployeeStation(){
		Do("Going to station");
		customerGui.DoGoToEmployeeStation(stationX, stationY);
		
	}
	
	private void PlaceOrder(){
		//no gui to do here
		employee.msgHereIsMyOrder(this, inventoryToOrder, deliveryMethod);
	}
	
	private void PayBill(){
		double expected = 0;

		for (Map.Entry<String, Integer> entry : bill.inventoryFulfilled.entrySet()){
			expected += marketMenu.getPrice(entry.getKey())*entry.getValue();	//price of each item * # that was fulfilled
		}
		if (expected == bill.charge){
			if (myCash >= bill.charge){
				cashier.msgHereIsPayment(bill.charge, this);
				myCash -= bill.charge;
				bill.amountPaid = bill.charge;
				return;
			}
			//else?
		}
		//else?
	}
	
	private void LeaveMarket(){
		if (bill.changeReceived == (bill.amountPaid - bill.charge)){
			print("Equal");
			employee.msgDoneAndLeaving(this);
			customerGui.DoExitMarket();
		}
		//else?
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
	
	private class Bill {
		Map<String, Integer> inventoryFulfilled = new TreeMap<String, Integer>();
		double charge;
		double amountPaid;
		double amountOwed;
		double changeReceived;
		String owedTo;
		BillState s;
		
		Bill(Map<String, Integer> inventoryFulfilled, double charge){
			this.inventoryFulfilled = inventoryFulfilled; 
			this.charge = charge;
			this.s = BillState.unpaid;
		}
		
	}
}

