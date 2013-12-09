package role.market;

import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import role.Role;

//import market.interfaces.*;

import agent.Agent;
import mainCity.market1.Market1Menu;
import mainCity.market1.Market1Menu.Item;
import mainCity.market1.gui.*;
import mainCity.market1.interfaces.*;

import java.util.*;
import java.util.List;
import java.awt.*;

/**
 * Restaurant customer agent.
 */
public class MarketCustomerRole extends Role implements Customer {
	private String name;
	private int hungerLevel = 5;        // determines length of meal
	Timer timer = new Timer();
	private CustomerGuiInterface customerGui;

	// agent correspondents
	private Greeter host;
	private Employee employee;
	private MarketCashier cashier;
	private Market1Menu marketMenu = new Market1Menu();
	
	private int stationX;
	private int stationY;
	
	private double myCash;
	private double cashOwed = 0;
	String deliveryMethod;
	Map<String, Integer> inventoryToOrder;
	
	Bill bill;
	public enum BillState {unpaid, paid};

	public enum AgentState
	{DoingNothing, WaitingInMarket, WaitingForEmployee, GoingToStation, Ordering, OrderProcessing, WaitingForOrder, GoingToCashier, Paying,
		WaitingForChange, Leaving, waitingForRecalculation};
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
	public void setHost(Greeter host) {
		this.host = host;
	}
	
	public void setCashier(MarketCashier c){
		this.cashier = c;
	}
	
	public void setEmployee(Employee e){
		this.employee = e;
	}

	public String getCustomerName() {
		return name;
	}
	
	public double getMyCash(){
		return myCash;
	}
	public double getCashOwed(){
		return cashOwed;
	}
	public Bill getBill(){
		return bill;
	}
	public AgentEvent getEvent(){
		return event;
	}
	public AgentState getState(){
		return state;
	}
	public int getStationX(){
		return stationX;
	}
	public int getStationY(){
		return stationY;
	}
	public Employee getEmployee(){
		return employee;
	}
	public boolean getIOweMarket(){
		return IOweMarket;
	}


	//for alert log trace statements
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.MARKET_CUSTOMER, this.getName(), s);
	}
	
	
	// Messages
	public void goGetInventory(Map<String, Integer> inventoryNeeded){
		log("Told to go to market to order inventory");
		this.inventoryToOrder = inventoryNeeded;
		
		log("Items to order: ");
		for (Map.Entry<String, Integer> entry : inventoryToOrder.entrySet()){
			log("Item: " + entry.getKey() + "; #: " + entry.getValue());
		}
		
		event = AgentEvent.toldToGetInventory;
		stateChanged();
	}

	public void msgFollowMe(Employee e, int x, int y){
		log("Received msgFollowMe");
		stationX = x;
		stationY = y - 20;
		employee = e;
		
		event = AgentEvent.followEmployee;
		stateChanged();
	}

	public void msgMayITakeYourOrder(Employee e){
		log("Received msgMayITakeYourOrder from: " + e.getName());
		event = AgentEvent.askedForOrder;
		stateChanged();
	}
	
	public void msgHereIsYourOrder(Map<String, Integer> inventoryFulfilled, double amount){
		log("Received msgHereIsYourOrder");
		bill = new Bill(inventoryFulfilled, amount);
		event = AgentEvent.gotOrderAndBill;
		stateChanged();
	}
	
	public void msgHereIsYourChange(double amountChange, double amountCharged){
		log("Received msgHereIsYourChange: $" + amountChange);
		bill.changeReceived = amountChange;
		event = AgentEvent.gotChange;
		stateChanged();
	}
	

	public void msgNotEnoughCash(double cashOwed){
		log("Received msg NotEnoughCash: I owe $" + cashOwed);
		this.cashOwed = cashOwed;
		myCash += 100;		//ok to do in simcity?
        log("Now have in cash: $" + myCash);
		IOweMarket = true;
		event = AgentEvent.gotChange;		//essentially
		stateChanged();
	}
	
	public void msgHereIsBill(double amount){		//from cashier, who recalculated bill and now sends a lower one
        log("Received msgHereIsBill for $" + amount);
        
        bill.charge = Math.round(amount*100.0)/100.0;
        event = AgentEvent.gotNewBill;
        stateChanged();
	}
	
	public void msgHereIsFinalBill(double amount){
        log("Received msgHereIsBill");

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
	}*/
	
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
			GoToCashier();
			return true;
		}
		if (state == AgentState.GoingToCashier && event == AgentEvent.atCashierAgent){
			state = AgentState.Paying;
			PayBill();	//verifies bill first
			return true;
		}
		if (state == AgentState.Paying && event == AgentEvent.gotNewBill){		//new, cheaper, recalculated bill OR non-negotiable bill
			state = AgentState.WaitingForChange;
			PayBill();	//verifies bill again
			return true;
		}
		if (state == AgentState.Paying && event == AgentEvent.gotChange){
			//state = AgentState.Leaving;		//not necessarily
			LeaveMarket();	//verifies change first
			return true;
		}
		if (state == AgentState.WaitingForChange && event == AgentEvent.gotChange){
			//state = AgentState.Leaving;		//not necessarily
			LeaveMarket();	//verifies change first
			return true;
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			super.setInactive();
			return true;
		}
		return false;
	}

	// Actions
	private void GoToMarket(){
		log("Going to market");
		customerGui.DoGoToWaitingArea();
		host.msgINeedInventory(this, this.getGui().getWaitingPosX(), this.getGui().getWaitingPosY());
	}
	
	private void GoToEmployeeStation(){
		log("Going to station");
		customerGui.DoGoToEmployeeStation(stationX, stationY);
	}
	
	private void PlaceOrder(){
        log("Placing order");
		employee.msgHereIsMyOrder(this, inventoryToOrder, deliveryMethod);
	}
	
	private void GoToCashier(){
        log("Going to cashier");
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
				log("Still can't pay everything I owe.");
				//...
			}
		}
		
		
		if (bill.nonNegotiable){		//don't verify again
			if (myCash >= bill.charge){
				log("Paying: $" + bill.charge);
				cashier.msgHereIsPayment(bill.charge, this);
				myCash -= bill.charge;
				myCash = Math.round(myCash*100.0)/100.0;
				bill.amountPaid = bill.charge;
				return;
			}
			else {
				log("I don't have enough money to pay full bill.");
				cashier.msgHereIsPayment(myCash, this);
				myCash = 0;
				return;
			}
		}
		else {
			double expected = 0;

			for (Map.Entry<String, Integer> entry : bill.inventoryFulfilled.entrySet()){
				//expected += marketMenu.getPrice(entry.getKey())*entry.getValue();	//price of each item * # that was fulfilled
				for (Item i : marketMenu.menuItems){
					if (i.getItem().equalsIgnoreCase(entry.getKey()))
						expected += i.getPrice() * entry.getValue();
				}
			}
			if (expected >= bill.charge){		//if the verification of bill charge succeeds, or they don't charge us enough (we don't care, take it!)
				if (myCash >= bill.charge){
			        log("Paying: $" + bill.charge);
					cashier.msgHereIsPayment(bill.charge, this);
					myCash -= bill.charge;
					myCash = Math.round(myCash*100.0)/100.0;
					bill.amountPaid = bill.charge;
					return;
				}
				else {	//if my cash is less than bill charge
					log("I don't have enough money to pay full bill.");
					cashier.msgHereIsPayment(Math.round(myCash*100.0)/100, this);
					cashOwed = Math.round((bill.charge - myCash)*100.0)/100.0;
					bill.amountPaid = myCash;
					myCash = 0;
					return;
				}
			}
			else if (expected < bill.charge){		//if the verification fails (charged more than they should have been)
				cashier.msgPleaseRecalculateBill(this);
				return;
			}
		}
	}
	
	private void LeaveMarket(){
		if (IOweMarket) {
	        log("No change. Didn't pay enough.");
	        cashier.msgChangeVerified(this);		//essentially

			employee.msgDoneAndLeaving(this);
			customerGui.DoExitMarket();
	        log("Leaving market.");
	        state = AgentState.Leaving;
	        bill = null;
	        return;
		}
			
		
		if (bill.changeReceived == (bill.amountPaid - bill.charge)){
			log("Equal. Change verified.");
	        cashier.msgChangeVerified(this);

			employee.msgDoneAndLeaving(this);
			customerGui.DoExitMarket();
	        log("Leaving market.");
	        state = AgentState.Leaving;
	        bill = null;
		}
		else if (bill.changeReceived > (bill.amountPaid - bill.charge)){
	        log("More change than necessary. Take it and run.");
	        cashier.msgChangeVerified(this);

			employee.msgDoneAndLeaving(this);
			customerGui.DoExitMarket();
	        log("Leaving market.");
	        state = AgentState.Leaving;
	        bill = null;
		}
		else if (bill.changeReceived < (bill.amountPaid - bill.charge)){
	        log("Not enough change. Recalculate change.");
	        
	        //TELL CASHIER TO RECALCULATE CHANGE (just have cashier give him the amount of change he wants)
	        //state = AgentState.WaitingForChange;
	        cashier.msgChangeVerified(this);

			employee.msgDoneAndLeaving(this);
			customerGui.DoExitMarket();
	        log("Leaving market.");
	        state = AgentState.Leaving;
	        bill = null;
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


	public void setGui(CustomerGuiInterface g) {
		customerGui = g;
	}

	public CustomerGuiInterface getGui() {
		return customerGui;
	}
	
	public boolean restaurantOpen() {
		if (host instanceof MarketGreeterRole){
			MarketGreeterRole h = (MarketGreeterRole) host;
			if (h !=null && h.isActive() && h.isOpen())
				return true;
		}
		return false;
		/*
		if(host != null && host.isActive() && host.isOpen())
			return true;
		return false;*/
	}
	
	
	public class Bill {		//public only for testing purposes
		boolean nonNegotiable = false;
		Map<String, Integer> inventoryFulfilled = new TreeMap<String, Integer>();
		double charge;
		double amountPaid;
		double amountOwed;
		double changeReceived;
		BillState s;
		
		Bill(Map<String, Integer> inventoryFulfilled, double charge){
			this.inventoryFulfilled = inventoryFulfilled; 
			this.charge = charge;
			this.s = BillState.unpaid;
		}
		
		public double getAmountPaid(){
			return amountPaid;
		}
		public double getCharge(){
			return charge;
		}
		public double getChangeReceived(){
			return changeReceived;
		}
		public double getAmountOwed(){
			return amountOwed;
		}
		public BillState getState(){
			return s;
		}
		
	}
}

