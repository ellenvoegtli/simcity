
package mainCity.market;

import agent.Agent;
import mainCity.contactList.ContactList;
import mainCity.interfaces.*;
import mainCity.market.gui.*;
import role.Role;

import java.util.*;
import java.util.concurrent.*;


 // Restaurant Cook Agent

public class MarketDeliveryManRole extends Agent{			//only handles one restaurant at a time right now
	private String name;
	public ContactList contactList;
	public DeliveryManGui deliveryGui;
	
	//private int availableMoney = 500;
	Timer timer = new Timer();
	private Bill b;
	private List<MarketEmployeeRole> employees = Collections.synchronizedList(new ArrayList<MarketEmployeeRole>());
	private DeliveryState s = DeliveryState.doingNothing;
	enum DeliveryState {doingNothing, enRoute, waitingForPayment, calculatingChange, done};
	private DeliveryEvent event;
	enum DeliveryEvent {deliveryRequested, arrivedAtLocation, receivedPayment};
	
	private Semaphore atHome = new Semaphore(0, true);
	private Semaphore atDestination = new Semaphore(0, true);
	
	
	//constructor
	public MarketDeliveryManRole(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	// Messages
	
	public void msgHereIsOrderForDelivery(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer>inventory, double billAmount){
		print("Received msgHereIsOrderForDelivery");

		b = new Bill(restaurantName, cook, cashier, billAmount, inventory);
		event = DeliveryEvent.deliveryRequested;
		stateChanged();
	}
	
	public void msgArrivedAtLocation(){		//from gui
		event = DeliveryEvent.arrivedAtLocation;
		stateChanged();
	}

	
	public void msgHereIsPayment(double amount){		//sent by any restaurant's cashier
		print("Received msgHereIsPayment: got $" + amount);
		
		b.amountPaid = amount;
		event = DeliveryEvent.receivedPayment;
		stateChanged();
	}
	
	public void msgAtHome(){		//from gui
		print("msgAtHome called");
		atHome.release();
		stateChanged();
	}
	public void msgAtDestination(){
		print("msgAtDestination called");
		//atDestination.release();
		event = DeliveryEvent.arrivedAtLocation;
		stateChanged();
	}
	
	

	 // Scheduler.  Determine what action is called for, and do it.
	 
	public boolean pickAndExecuteAnAction() {
		
		if(b!=null){
			if (s == DeliveryState.doingNothing && event == DeliveryEvent.deliveryRequested){
				TravelToLocation();
				s = DeliveryState.enRoute;
				return true;
			}
			else if (s == DeliveryState.enRoute && event == DeliveryEvent.arrivedAtLocation){
				DeliverOrder();
				s = DeliveryState.waitingForPayment;
				return true;
			}
			else if (s == DeliveryState.waitingForPayment && event == DeliveryEvent.receivedPayment){
				CalculateChange();
				s = DeliveryState.calculatingChange;
				return true;
			}
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}
	

	// Actions
	public void TravelToLocation(){
		print("Traveling to delivery location: " + b.restaurantName);
		
		timer.schedule(new TimerTask() {
			public void run() {
				msgAtDestination();
			}
		}, 5000);
		
		/*
		//gui call for truck to travel to restaurant
		deliveryGui.DoDeliverOrder(b.restaurantName);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//gui will message deliveryMan when it arrives
	}
	
	public void DeliverOrder(){
		print("Delivering order");
		if (b.restaurantName.equalsIgnoreCase("ellenRestaurant")){
			b.cook = contactList.getInstance().ellenCook;
			b.cashier = contactList.getInstance().ellenCashier;
			b.cook.msgHereIsYourOrder(b.itemsBought);
			b.cashier.msgHereIsMarketBill(b.itemsBought, b.amountCharged, this);
		}
		else if (b.restaurantName.equalsIgnoreCase("enaRestaurant")){
			b.cook = contactList.getInstance().enaCook;
			b.cashier = contactList.getInstance().enaCashier;
			b.cook.msgHereIsYourOrder(b.itemsBought);
			b.cashier.msgHereIsMarketBill(b.itemsBought, b.amountCharged, this);
		}
		else if (b.restaurantName.equalsIgnoreCase("marcusRestaurant")){
			b.cook = contactList.getInstance().marcusCook;
			b.cashier = contactList.getInstance().marcusCashier;
			b.cook.msgHereIsYourOrder(b.itemsBought);
			b.cashier.msgHereIsMarketBill(b.itemsBought, b.amountCharged, this);
		}
		else if (b.restaurantName.equalsIgnoreCase("jeffersonRestaurant")){
			b.cook = contactList.getInstance().jeffersonCook;
			b.cashier = contactList.getInstance().jeffersonCashier;
			b.cook.msgHereIsYourOrder(b.itemsBought);
			b.cashier.msgHereIsMarketBill(b.itemsBought, b.amountCharged, this);
		}
		else if (b.restaurantName.equalsIgnoreCase("davidRestaurant")){
			b.cook = contactList.getInstance().davidCook;
			b.cashier = contactList.getInstance().davidCashier;
			b.cook.msgHereIsYourOrder(b.itemsBought);
			b.cashier.msgHereIsMarketBill(b.itemsBought, b.amountCharged, this);
		}
	}
	
	public void CalculateChange(){
		print("Calculating change");
		
		//check to make sure payment is large enough
		if (b.amountPaid >= b.amountCharged)
			b.cashier.msgHereIsChange((b.amountPaid - b.amountCharged), this);
		//else?
			//you still owe ..

		//delete bill?
		b = null;
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
		Map<String, Integer> itemsBought;
		double amountCharged;
		double amountPaid;
		double amountOwed;
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

