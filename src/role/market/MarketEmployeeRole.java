package role.market;

import mainCity.PersonAgent;
import mainCity.market.MarketMenu;
import mainCity.market.MarketMenu.Item;
import mainCity.market.interfaces.*;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.*;
import role.Role;

import java.util.*;
import java.util.concurrent.Semaphore;


public class MarketEmployeeRole extends Role implements Employee, WorkerRole {
	private String name;
	Timer timer = new Timer();
	
	private Greeter host;
	private MarketCashier cashier;
	private DeliveryMan deliveryMan;
	private MarketMenu marketMenu;
	
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
	private Semaphore doneLeaving = new Semaphore(0, true);
	
	private boolean onDuty;
	private boolean customer = false;

		

	public MarketEmployeeRole(PersonAgent p, String name) {
		super(p);
		this.name = name;
		onDuty = true;
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
	public String getName() {
		return name;
	}
	public void setMenu(MarketMenu m){
		marketMenu = m;
	}
	public Collection<MyCustomer> getMyCustomers() {
		return myCustomers;
	}
	public Collection<MyBusiness> getMyBusinesses(){
		return myBusinesses;
	}

    public boolean isReady() {
    	return (wState == WaiterState.doingNothing);
    }
    
	//for alert log trace statements
    public void log(String s){
		if (name.toLowerCase().contains("market2")){
			AlertLog.getInstance().logMessage(AlertTag.MARKET2, this.getName(), s);
	        AlertLog.getInstance().logMessage(AlertTag.MARKET2_EMPLOYEE, this.getName(), s);
		}
		else {
			AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), s);
	        AlertLog.getInstance().logMessage(AlertTag.MARKET_EMPLOYEE, this.getName(), s);
		}
	}
    
	// Messages
    public void msgAssignedToBusiness(String restaurantName, Map<String, Integer>inventory){
    	log("Received msgAssignedToBusiness");
    	myBusinesses.add(new MyBusiness(restaurantName, inventory, BusinessState.ordered));
    	stateChanged();
    }
   
	public void msgAssignedToCustomer(Customer c, int waitPosX, int waitPosY){
		log("Received msgAssignedToCustomer");		
		myCustomers.add(new MyCustomer(c, waitPosX, waitPosY, CustomerState.newCustomer));
		stateChanged();
	}
	
	public void msgHereIsMyOrder(Customer c, Map<String, Integer> inventory, String deliveryMethod) {
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){	
			if (thisMC.c.equals(c)){
				mc = thisMC;
				break;
			}
		}
		log("Received msgHereIsMyOrder");
		mc.inventoryOrdered = new TreeMap<String, Integer>(inventory); 	//copy over list
		mc.s = CustomerState.ordered;
		stateChanged();
	}
	
	public void msgHereIsBill(Customer c, double amount){		//from cashier
		MyCustomer mc = null;
		for (MyCustomer thisMC : myCustomers){	
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
		for (MyBusiness thisMB : myBusinesses){	
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
		for (MyCustomer thisMC : myCustomers){ 
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
		log("msgAtStation called");
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
	public void msgDoneLeaving(){
		log("Done leaving restaurant");
		doneLeaving.release();
		stateChanged();
	}
	
	public void msgGoOffDuty(double amount){
		log("msgGoOffDuty called");
		addToCash(amount);
		onDuty = false;
		stateChanged();
	}
	
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		
		try {
			for (MyCustomer mc : myCustomers){
				if (mc.s == CustomerState.newCustomer && wState == WaiterState.doingNothing && !customer){
					wState = WaiterState.busy;
					customer = true;
					GreetCustomer(mc);
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
			for (MyCustomer mc : myCustomers){
				if (mc.s == CustomerState.gotCheckFromCashier && wState == WaiterState.doingNothing){
					FulfillOrder(mc);
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
					wState = WaiterState.doingNothing;
					customer = false;
					return true;
				}
			}
			
			
			//business check
			for (MyBusiness mb : myBusinesses) {
				if (mb.s == BusinessState.ordered && wState == WaiterState.doingNothing && !customer){
					ProcessOrder(mb);		//determine what we can fulfill, have cashier compute bill
					wState = WaiterState.busy;
					customer = true;
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
			
			//business check
			for (MyBusiness mb : myBusinesses) {
				if (mb.s == BusinessState.doneFulfillingOrder && wState == WaiterState.doingNothing){
					DeliverOrder(mb);
					wState = WaiterState.busy;
					customer = false;
					return true;
				}
			}

			wState = WaiterState.doingNothing;
			
		}catch(ConcurrentModificationException e){
			return false;
		}
		
		if(!onDuty){
			leaveRestaurant();
			onDuty = true;
		}
		
		return false;
	}

	// Actions

	
	//=========================== IN-STORE CUSTOMERS ==============================================================
	private void GreetCustomer(MyCustomer mc){
		log("Going to pick up customer: " + mc.c.getName());
		employeeGui.DoPickUpWaitingCustomer(mc.waitingAreaX, mc.waitingAreaY);
		try {
			atWaitingRoom.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		mc.c.msgFollowMe(this, homeX, homeY - 17);
		employeeGui.DoGoToStation();		//sometimes doesn't go all the way to the station before going to the cashier
		try {
			atStation.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mc.c.msgMayITakeYourOrder(this);
		mc.s = CustomerState.waitingForOrder;
	}
	
	private void ProcessOrder(MyCustomer mc){
		log("Processing order for " + mc.c.getName());
		for (Map.Entry<String, Integer> entry : mc.inventoryOrdered.entrySet()){
			for (Item i : marketMenu.menuItems){
				if (i.getItem().equalsIgnoreCase(entry.getKey())){
					if (entry.getValue() <= i.getStock()){
						mc.inventoryFulfilled.put(entry.getKey(), entry.getValue());
						log("Market had: " + i.getStock() + " " + i.getItem() + "(s).");
						i.deductStock(entry.getValue());
						log("Market now has: " + i.getStock() + " " + i.getItem() + "(s).");
					}
					else{
						mc.inventoryFulfilled.put(entry.getKey(), i.getStock());
						log("Market had: " + i.getStock() + " " + i.getItem() + "(s).");
						i.deductStock(i.getStock());
						log("Market now has: " + i.getStock() + " " + i.getItem() + "(s).");
					}
				}
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
			e.printStackTrace();
		}
		cashier.msgComputeBill(mc.inventoryFulfilled, mc.c, this);
	}
	
	private void FulfillOrder(final MyCustomer mc){
		log("Fulfilling order for: " + mc.c.getName());
		employeeGui.DoFulfillOrder();
		mc.s = CustomerState.fulfillingOrder;
		
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
			for (Item i : marketMenu.menuItems){
				if (i.getItem().equalsIgnoreCase(entry.getKey())){
					if (entry.getValue() <= i.getStock())
						mb.inventoryFulfilled.put(entry.getKey(), entry.getValue());
					else 
						mb.inventoryFulfilled.put(entry.getKey(), (entry.getValue() - i.getStock()));
				}
			}
		}
		SendBillToCashier(mb);
	}
	
	private void SendBillToCashier(MyBusiness mb){
		log("Sending bill to cashier");
		employeeGui.DoGoToCashier();
		try {
			atCashier.acquire();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		cashier.msgComputeBill(mb.inventoryFulfilled, mb.restaurantName, this);
		mb.s = BusinessState.waitingForBill;
	}
	
	private void FulfillOrder(final MyBusiness mb){
		log("Fulfilling order for: " + mb.restaurantName);
		employeeGui.DoFulfillOrder();
		mb.s = BusinessState.fulfillingOrder;
		
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
			e.printStackTrace();
		}
		deliveryMan.msgHereIsOrderForDelivery(mb.restaurantName, /*mb.cook, mb.cashier,*/ mb.inventoryFulfilled, mb.billAmount);
		mb.s = BusinessState.sentForDelivery;	//unnecessary
		employeeGui.DoGoToStation();
		
		log("Removing customer: " + mb.restaurantName);
		myBusinesses.remove(mb);
	}
	
	private void leaveRestaurant(){
		employeeGui.DoLeaveMarket();
		try {
			doneLeaving.acquire();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		setInactive();
		onDuty = true;
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
		//MainCook cook;
		//MainCashier cashier;
		
		Map<String, Integer> inventoryOrdered;
		Map<String, Integer> inventoryFulfilled = new TreeMap<String, Integer>();
		
		MyBusiness(String restaurantName, Map<String, Integer>inventory, BusinessState s){
			this.restaurantName = restaurantName;
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

