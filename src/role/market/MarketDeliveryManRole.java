package role.market;

import agent.Agent;
import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.gui.DeliveryManGui;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.*;
import mainCity.market.*;
import mainCity.market.MarketCashierRole.BillState;
import mainCity.market.gui.*;
import mainCity.market.interfaces.MarketCashier;
import mainCity.market.interfaces.DeliveryMan;
import mainCity.market.interfaces.DeliveryManGuiInterface;
import mainCity.restaurants.EllenRestaurant.interfaces.Cook;
import mainCity.restaurants.EllenRestaurant.interfaces.Cashier;
import role.Role;

import java.util.*;
import java.util.concurrent.*;


 // Restaurant Cook Agent

public class MarketDeliveryManRole extends Role implements DeliveryMan{			//only handles one restaurant at a time right now
	private String name;
	public DeliveryManGuiInterface deliveryGui;
	MarketCashier cashier;
	
	private double availableMoney = 0;
	Timer timer = new Timer();
	//private Bill b;
	public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	private List<MarketEmployeeRole> employees = Collections.synchronizedList(new ArrayList<MarketEmployeeRole>());
	/*private DeliveryState s = DeliveryState.doingNothing;
	enum DeliveryState {doingNothing, enRoute, waitingForPayment, calculatingChange, goingBackToMarket, done};
	private DeliveryEvent event;
	enum DeliveryEvent {deliveryRequested, arrivedAtLocation, receivedPayment, changeVerified, owedMoney, arrivedAtMarket};*/
	public AgentState state;
	public enum AgentState {doingNothing, makingDelivery};
	
	public enum DeliveryState {newBill, enRoute, waitingForPayment, calculatingChange, oweMoney, waitingForVerification, goingBackToMarket, done};
	public enum DeliveryEvent {deliveryRequested, arrivedAtLocation, receivedPayment, changeVerified, acknowledgedDebt, arrivedAtMarket};
	
	
	private Semaphore atHome = new Semaphore(0, true);
	private Semaphore atDestination = new Semaphore(0, true);
	
	
	//constructor
	public MarketDeliveryManRole(PersonAgent p, String name) {
		super(p);
		this.name = name;
		state = AgentState.doingNothing;
	}
	public List getBills(){
		return bills;
	}
	public double getAvailableMoney(){
		return availableMoney;
	}

	public String getName() {
		return name;
	}
	public void setCashier(MarketCashier c){
		cashier = c;
	}
	public AgentState getState(){
		return state;
	}
	public void setState(AgentState s){
		state = s;
	}

	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.MARKET_DELIVERYMAN, this.getName(), s);
	}
	
	// Messages
	
	public void msgHereIsOrderForDelivery(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer>inventory, double billAmount){
		log("Received msgHereIsOrderForDelivery");

		bills.add(new Bill(restaurantName, cook, cashier, billAmount, inventory));
		stateChanged();
	}

	
	public void msgHereIsPayment(double amount, String restaurantName){		//sent by any restaurant's cashier
		log("Received msgHereIsPayment: got $" + amount);
		Bill b = null;
		for (Bill thisB : bills){	//to find the myCustomer with this specific Customer within myCustomers list
			if (thisB.restaurantName.equalsIgnoreCase(restaurantName)){
				b = thisB;
				break;
			}
		}
		
		b.amountPaid = amount;
		b.event = DeliveryEvent.receivedPayment;
		stateChanged();
	}
	public void msgChangeVerified(String name){
		log("Received msgChangeVerified");
		Bill b = null;
		for (Bill thisB : bills){	//to find the myCustomer with this specific Customer within myCustomers list
			if (thisB.restaurantName.equalsIgnoreCase(name)){
				b = thisB;
				break;
			}
		}
		availableMoney += b.amountMarketGets;
		availableMoney = Math.round(availableMoney*100.0)/100.0;
		
		b.event = DeliveryEvent.changeVerified;
		stateChanged();
	}
	
	public void msgIOweYou(double amount, String name){
		log("Received msgIOweYou from " + name + " for $" + amount);
		Bill b = null;
		for (Bill thisB : bills){	//to find the myCustomer with this specific Customer within myCustomers list
			if (thisB.restaurantName.equalsIgnoreCase(name)){
				b = thisB;
				break;
			}
		}
		
		availableMoney += b.amountMarketGets;
		availableMoney = Math.round(availableMoney*100.0)/100.0;
		
		b.event = DeliveryEvent.acknowledgedDebt;
		stateChanged();
	}
	
	
	
	
	public void msgAtHome(){		//from gui
		log("msgAtHome called");
		atHome.release();
		stateChanged();
	}
	public void msgAtDestination(){
		log("msgAtDestination called");
		atDestination.release();
		stateChanged();
	}
	
	

	 // Scheduler.  Determine what action is called for, and do it.
	 
	public boolean pickAndExecuteAnAction() {
		
		for(Bill b: bills){
			if (b.s == DeliveryState.newBill && b.event == DeliveryEvent.deliveryRequested && state == AgentState.doingNothing){
				DeliverOrder(b);
				state = AgentState.makingDelivery;
				return true;
			}
		}
		for(Bill b: bills){
			if (b.s == DeliveryState.waitingForPayment && b.event == DeliveryEvent.receivedPayment){
				CalculateChange(b);
				return true;
			}
		}
		for(Bill b: bills){
			if (b.s == DeliveryState.waitingForVerification && b.event == DeliveryEvent.changeVerified){
				ReturnToMarket(b);
				return true;
			}
		}
		for(Bill b: bills){
			if (b.s == DeliveryState.oweMoney && b.event == DeliveryEvent.acknowledgedDebt){
				ReturnToMarket(b);
				return true;
			}
		}


		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}
	

	// Actions
	public void DeliverOrder(Bill b){
		log("Traveling to delivery location: " + b.restaurantName);
		
		//gui call for truck to travel to restaurant
		deliveryGui.DoDeliverOrder(b.restaurantName);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//gui will then message appropriate deliveryMan when it arrives
		if (b.restaurantName.equalsIgnoreCase("ellenRestaurant")){
			b.cook = ContactList.getInstance().ellenCook;
			b.cashier = ContactList.getInstance().ellenCashier;
			b.cook.msgHereIsYourOrder(b.itemsBought);
			b.cashier.msgHereIsMarketBill(b.itemsBought, b.amountCharged, this);
		}
		else if (b.restaurantName.equalsIgnoreCase("enaRestaurant")){
			b.cook = ContactList.getInstance().enaCook;
			b.cashier = ContactList.getInstance().enaCashier;
			b.cook.msgHereIsYourOrder(b.itemsBought);
			b.cashier.msgHereIsMarketBill(b.itemsBought, b.amountCharged, this);
		}
		else if (b.restaurantName.equalsIgnoreCase("marcusRestaurant")){
			b.cook = ContactList.getInstance().marcusCook;
			b.cashier = ContactList.getInstance().marcusCashier;
			b.cook.msgHereIsYourOrder(b.itemsBought);
			b.cashier.msgHereIsMarketBill(b.itemsBought, b.amountCharged, this);
		}
		else if (b.restaurantName.equalsIgnoreCase("jeffersonRestaurant")){
			b.cook = ContactList.getInstance().jeffersonCook;
			b.cashier = ContactList.getInstance().jeffersonCashier;
			b.cook.msgHereIsYourOrder(b.itemsBought);
			b.cashier.msgHereIsMarketBill(b.itemsBought, b.amountCharged, this);
		}
		else if (b.restaurantName.equalsIgnoreCase("davidRestaurant")){
			b.cook = ContactList.getInstance().davidCook;
			b.cashier = ContactList.getInstance().davidCashier;
			b.cook.msgHereIsYourOrder(b.itemsBought);
			b.cashier.msgHereIsMarketBill(b.itemsBought, b.amountCharged, this);
		}
		
		b.s = DeliveryState.waitingForPayment;
	}
	
	
	public void CalculateChange(Bill b){
		log("Calculating change");

		double dollars = 0;
		if (b.amountPaid >= b.amountCharged){
			dollars = Math.round((b.amountPaid - b.amountCharged)*100.0)/100.0;
			b.cashier.msgHereIsChange(dollars, this);
			
			b.amountMarketGets = Math.round(b.amountCharged *100.0)/100.0;		//mostly for testing purposes
			b.s = DeliveryState.waitingForVerification;
		}
		else {		//if they didn't pay enough
			b.amountOwed = Math.round((b.amountCharged - b.amountPaid)*100.0)/100.0;		//mostly for testing purposes
			b.amountMarketGets = b.amountPaid;
			
			b.cashier.msgNotEnoughMoney(b.amountOwed, b.amountPaid);
			b.s = DeliveryState.oweMoney;
		}
	}
	
	public void ReturnToMarket(Bill b){
		log("Returning to market");
		bills.remove(b);
		deliveryGui.DoGoToHomePosition();
		try {
			atHome.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		state = AgentState.doingNothing;
		
	}

	//utilities
	public void setGui(DeliveryManGuiInterface gui) {
		deliveryGui = gui;
	}

	public DeliveryManGuiInterface getGui() {
		return deliveryGui;
	}
	

	public class Bill {		//public only for testing purposes
		public DeliveryState s = DeliveryState.newBill;
		public DeliveryEvent event = DeliveryEvent.deliveryRequested;
		Map<String, Integer> itemsBought;
		double amountCharged;
		double amountPaid;
		double amountOwed;
		double amountMarketGets;
		String restaurantName;
		MainCook cook;
		MainCashier cashier;

		Bill(String name, MainCook cook, MainCashier cashier, double billAmount, Map<String, Integer> inventory){
			this.cook = cook;
			this.cashier = cashier;
			amountCharged = billAmount;
			restaurantName = name;
			itemsBought = new TreeMap<String, Integer>(inventory);
		}
		
		public double getAmountPaid(){
			return amountPaid;
		}
		public double getAmountCharged(){
			return amountCharged;
		}
		public double getAmountOwed(){
			return amountOwed;
		}
		public double getAmountMarketGets(){
			return amountMarketGets;
		}
		public MainCook getCook(){
			return cook;
		}
		public void setCook(Cook c){
			cook = c;
		}
		public MainCashier getCashier(){
			return cashier;
		}
		public void setCashier(Cashier c){
			cashier = c;
		}
		public String getRestaurant(){
			return restaurantName;
		}
		public DeliveryState getState(){
			return s;
		}
		public void setState(DeliveryState state){
			s = state;
		}
		public DeliveryEvent getEvent(){
			return event;
		}
	}

	
}

