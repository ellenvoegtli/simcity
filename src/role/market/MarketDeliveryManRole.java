package role.market;

import agent.Agent;
import mainCity.contactList.ContactList;
import mainCity.gui.DeliveryManGui;
import mainCity.interfaces.*;
import mainCity.market.*;
import mainCity.market.MarketCashierRole.BillState;
import mainCity.market.gui.*;
import role.Role;
import mainCity.PersonAgent;

import java.util.*;
import java.util.concurrent.*;


 // Restaurant Cook Agent

public class MarketDeliveryManRole extends Role{			//only handles one restaurant at a time right now
	private String name;
	public DeliveryManGui deliveryGui;
	MarketCashierRole cashier;
	
	//private int availableMoney = 500;
	Timer timer = new Timer();
	//private Bill b;
	private List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	private List<MarketEmployeeRole> employees = Collections.synchronizedList(new ArrayList<MarketEmployeeRole>());
	/*private DeliveryState s = DeliveryState.doingNothing;
	enum DeliveryState {doingNothing, enRoute, waitingForPayment, calculatingChange, goingBackToMarket, done};
	private DeliveryEvent event;
	enum DeliveryEvent {deliveryRequested, arrivedAtLocation, receivedPayment, changeVerified, owedMoney, arrivedAtMarket};*/
	private AgentState state;
	enum AgentState {doingNothing, makingDelivery};
	
	enum DeliveryState {newBill, enRoute, waitingForPayment, calculatingChange, oweMoney, waitingForVerification, goingBackToMarket, done};
	enum DeliveryEvent {deliveryRequested, arrivedAtLocation, receivedPayment, changeVerified, acknowledgedDebt, arrivedAtMarket};
	
	
	private Semaphore atHome = new Semaphore(0, true);
	private Semaphore atDestination = new Semaphore(0, true);
	
	
	//constructor
	public MarketDeliveryManRole(PersonAgent p, String name) {
		super(p);
		this.name = name;
		state = AgentState.doingNothing;
	}
	public void setCashier(MarketCashierRole c){
		cashier = c;
	}

	public String getName() {
		return name;
	}

	// Messages
	
	public void msgHereIsOrderForDelivery(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer>inventory, double billAmount){
		print("Received msgHereIsOrderForDelivery");

		bills.add(new Bill(restaurantName, cook, cashier, billAmount, inventory));
		//event = DeliveryEvent.deliveryRequested;
		stateChanged();
	}

	
	public void msgHereIsPayment(double amount, String restaurantName){		//sent by any restaurant's cashier
		print("Received msgHereIsPayment: got $" + amount);
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
		print("Received msgChangeVerified");
		Bill b = null;
		for (Bill thisB : bills){	//to find the myCustomer with this specific Customer within myCustomers list
			if (thisB.restaurantName.equalsIgnoreCase(name)){
				b = thisB;
				break;
			}
		}
		
		b.event = DeliveryEvent.changeVerified;
		stateChanged();
	}
	
	public void msgIOweYou(double amount, String name){
		print("Received msgIOweYou from " + name + " for $" + amount);
		Bill b = null;
		for (Bill thisB : bills){	//to find the myCustomer with this specific Customer within myCustomers list
			if (thisB.restaurantName.equalsIgnoreCase(name)){
				b = thisB;
				break;
			}
		}
		b.event = DeliveryEvent.acknowledgedDebt;
		stateChanged();
	}
	
	
	
	
	public void msgAtHome(){		//from gui
		print("msgAtHome called");
		atHome.release();
		stateChanged();
	}
	public void msgAtDestination(){
		print("msgAtDestination called");
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
		print("Traveling to delivery location: " + b.restaurantName);
		
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
		print("Calculating change");

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
		print("Returning to market");
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
	public void setGui(DeliveryManGui gui) {
		deliveryGui = gui;
	}

	public DeliveryManGui getGui() {
		return deliveryGui;
	}
	

	private class Bill {
		//List<OrderItem> itemsBought;
		private DeliveryState s = DeliveryState.newBill;
		private DeliveryEvent event = DeliveryEvent.deliveryRequested;
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
	}

	
}

