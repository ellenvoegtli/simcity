package mainCity.market;

import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.market.MarketEmployeeRole;
import role.Role;
import mainCity.PersonAgent;

//import market.interfaces.*;

import agent.Agent;
import mainCity.market.gui.*;

import java.util.*;
import java.util.List;
import java.awt.*;

/**
 * Restaurant customer agent.
 */
public class MarketCustomerRole extends Role{
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
	private double myCash;
	private double cashOwed = 0;
	String deliveryMethod;
	//List<OrderItem> inventoryToOrder;
	Map<String, Integer> inventoryToOrder;
	
	Bill bill;
	enum BillState {unpaid, paid};

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInMarket, WaitingForEmployee, GoingToStation, Ordering, OrderProcessing, WaitingForOrder, GoingToCashier, Paying,
		WaitingForChange, Leaving};
	private AgentState state = AgentState.DoingNothing;//The start state
	
	
	public enum AgentEvent 
	{none, toldToGetInventory, toldToWaitForEmployee, followEmployee, atStation, askedForOrder, gotOrderAndBill, 
		atCashierAgent, gotNewBill, gotChange, doneLeaving};
	AgentEvent event = AgentEvent.none;
	
	private boolean IOweMarket = false;

	
	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public MarketCustomerRole(PersonAgent p, String name){
		super(p);
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
	
	public double getMyCash(){
		return myCash;
	}
	// Messages

	
	public void goGetInventory(Map<String, Integer> inventoryNeeded){
		//print("Told to go to market to order inventory");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Told to go to market to order inventory");
		this.inventoryToOrder = inventoryNeeded;	//does this work?
		
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Items to order:");
		for (Map.Entry<String, Integer> entry : inventoryToOrder.entrySet()){
	        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Item: " + entry.getKey() + "; #: " + entry.getValue());
		}
		
		event = AgentEvent.toldToGetInventory;
		stateChanged();
	}

	public void msgFollowMe(MarketEmployeeRole e, int x, int y){
		//print("Received msgFollowMe");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msgFollowMe");
		stationX = x;
		stationY = y - 20;
		employee = e;
		
		event = AgentEvent.followEmployee;
		stateChanged();
	}

	public void msgMayITakeYourOrder(MarketEmployeeRole e){
		//print("Received msgMayITakeYourOrder from: " + e.getName());
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msgMayITakeYourOrder from: " + e.getName());
		event = AgentEvent.askedForOrder;
		stateChanged();
	}
	
	public void msgHereIsYourOrder(Map<String, Integer> inventoryFulfilled, double amount){
		//print("Received msgHereIsYourOrder");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msgHereIsYourOrder");

		bill = new Bill(inventoryFulfilled, amount);
		event = AgentEvent.gotOrderAndBill;
		stateChanged();
	}
	
	public void msgHereIsYourChange(double amountChange, double amountCharged){
		//print("Received msgHereIsYourChange: $" + amountChange);
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msgHereIsYourChange: $" + amountChange);

		bill.changeReceived = amountChange;
		event = AgentEvent.gotChange;
		stateChanged();
	}
	

	public void msgNotEnoughCash(double cashOwed){
		//print("Received msg NotEnoughCash: I owe $" + cashOwed);
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msg NotEnoughCash: I owe $" + cashOwed);

		this.cashOwed = cashOwed;
		myCash += 100;
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Now have in cash: $" + myCash);
		IOweMarket = true;
		event = AgentEvent.gotChange;		//essentially
		stateChanged();
	}
	
	public void msgHereIsBill(double amount){		//from cashier, who recalculated bill and now sends a lower one
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msgHereIsBill");
        
        bill.charge = amount;
        event = AgentEvent.gotNewBill;
        stateChanged();
	}
	
	public void msgHereIsFinalBill(double amount){
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msgHereIsBill");

        bill.charge = amount;
        bill.nonNegotiable = true;
        event = AgentEvent.gotNewBill;
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
	public boolean pickAndExecuteAnAction() {
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
			state = AgentState.GoingToCashier;
			GoToCashier();		//verifies bill first
			return true;
		}
		if (state == AgentState.GoingToCashier && event == AgentEvent.atCashierAgent){
			state = AgentState.Paying;
			PayBill();		//verifies bill first
			return true;
		}
		if (state == AgentState.Paying && event == AgentEvent.gotNewBill){		//new, cheaper, recalculated bill OR non-negotiable bill
			state = AgentState.Paying;
			PayBill();	//verifies bill again
			return true;
		}
		if (state == AgentState.Paying && event == AgentEvent.gotChange){
			//state = AgentState.Leaving;		//not necessarily
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
		//Do("Going to market");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Going to market");

		customerGui.DoGoToWaitingArea();
		host.msgINeedInventory(this, this.getGui().getWaitingPosX(), this.getGui().getWaitingPosY());
	}
	
	private void GoToEmployeeStation(){
		//Do("Going to station");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Going to station");

		customerGui.DoGoToEmployeeStation(stationX, stationY);
		
	}
	
	private void PlaceOrder(){
		//no gui to do here
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Placing order");
		employee.msgHereIsMyOrder(this, inventoryToOrder, deliveryMethod);
	}
	
	private void GoToCashier(){
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Going to cashier");

		customerGui.DoGoToCashier();
	}
	
	private void PayBill(){	
		if (IOweMarket){
			if (myCash >= cashOwed){
				cashier.msgHereIsMoneyIOwe(this, cashOwed);
				myCash -= cashOwed;
				cashOwed = 0;
				IOweMarket = false;
			} else {
				AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Still can't pay everything I owe.");
				//...
			}
		}
		
		
		if (bill.nonNegotiable){		//don't verify again. Non-norm: sue restaurant later?
			if (myCash >= bill.charge){
				AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Paying: $" + bill.charge);
				cashier.msgHereIsPayment(bill.charge, this);
				myCash -= bill.charge;
				bill.amountPaid = bill.charge;
				return;
			}
			else {
				AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "I don't have enough money to pay full bill.");
				cashier.msgHereIsPayment(myCash, this);
				//cashOwed = bill.charge - myCash;		//cashier will tell us this himself
				myCash = 0;
				return;
			}
		}
		else {
			double expected = 0;

			for (Map.Entry<String, Integer> entry : bill.inventoryFulfilled.entrySet()){
				expected += marketMenu.getPrice(entry.getKey())*entry.getValue();	//price of each item * # that was fulfilled
			}
			if (expected == bill.charge){		//if the verfication of bill charge succeeds
				if (myCash >= bill.charge){
			        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Paying: $" + bill.charge);
					cashier.msgHereIsPayment(bill.charge, this);
					myCash -= bill.charge;
					bill.amountPaid = bill.charge;
					return;
				}
				else {
					AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "I don't have enough money to pay full bill.");
					cashier.msgHereIsPayment(myCash, this);
					cashOwed = bill.charge - myCash;
					myCash = 0;
					return;
				}
			}
			else if (expected < bill.charge){		//if the verification fails (charged more than they should have been)
				cashier.msgPleaseRecalculateBill(this);
				return;
			}
			//we don't care if they don't charge us enough. take it and run!
		}
	}
	
	private void LeaveMarket(){
		if (IOweMarket) {
	        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "No change.");
	        cashier.msgChangeVerified(this);		//essentially

			employee.msgDoneAndLeaving(this);
			customerGui.DoExitMarket();
	        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Leaving market.");
	        state = AgentState.Leaving;
	        return;
		}
			
		
		if (bill.changeReceived == (bill.amountPaid - bill.charge)){
			//print("Equal. Change verified.");
	        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Equal. Change verified.");
	        cashier.msgChangeVerified(this);

			employee.msgDoneAndLeaving(this);
			customerGui.DoExitMarket();
	        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Leaving market.");
	        state = AgentState.Leaving;
		}
		else if (bill.changeReceived > (bill.amountPaid - bill.charge)){
	        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "More change than necessary. Take it and run.");
	        cashier.msgChangeVerified(this);

			employee.msgDoneAndLeaving(this);
			customerGui.DoExitMarket();
	        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Leaving market.");
	        state = AgentState.Leaving;
		}
		else if (bill.changeReceived < (bill.amountPaid - bill.charge)){
	        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Not enough change. Recalculate change.");
	        
	        //TELL CASHIER TO RECALCULATE CHANGE (just have cashier give him the amount of change he wants)
	        state = AgentState.WaitingForChange;
		}
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
	
	public boolean restaurantOpen() {
		if(host != null && host.isActive() && host.isOpen())
			return true;
		return false;
	}
	
	
	private class Bill {
		boolean nonNegotiable = false;
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

