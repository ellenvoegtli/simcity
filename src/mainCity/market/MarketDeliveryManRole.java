
package mainCity.market;

import agent.Agent;
import mainCity.interfaces.*;

import java.util.*;


 // Restaurant Cook Agent

public class MarketDeliveryManRole extends Agent{			//only handles one restaurant at a time right now
	private String name;
	//private int availableMoney = 500;
	Timer timer = new Timer();
	
	Bill b;
	
	//public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());	//from waiters
	private List<MarketEmployeeRole> employees = Collections.synchronizedList(new ArrayList<MarketEmployeeRole>());
		
	private DeliveryState s;
	enum DeliveryState {doingNothing, enRoute, waitingForPayment, calculatingChange, done};
	private DeliveryEvent event;
	enum DeliveryEvent {deliveryRequested, arrivedAtLocation, receivedPayment};
	
	
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
		print("Traveling to delivery location");
		//gui call for truck to travel to restaurant
		//gui will message deliveryMan when it arrives
	}
	
	public void DeliverOrder(){
		print("Delivering order");
		//b.r.getCook().msgHereIsYourOrder(b.itemsBought);	//*****who do i message order to?? ***/
		
		
		//b.r.getCashier().msgHereIsMarketBill(b.itemsBought, b.amountCharged);
	}
	
	public void CalculateChange(){
		print("Calculating change");
		
		//check to make sure payment is large enough
		if (b.amountPaid >= b.amountCharged)
			//b.r.getCook().msgHereIsYourChange(b.amountPaid - b.amountCharged);	//*****who do i message change to?? ***/
		//else?
			//you still owe ...

		//delete bill?
		b = null;
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

