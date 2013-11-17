
package mainCity.restaurants.EllenRestaurant;

import agent.Agent;

import java.util.*;

import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.EllenRestaurant.gui.*;
import mainCity.restaurants.EllenRestaurant.interfaces.*;

 // Restaurant Cook Agent

public class EllenMarketRole extends Agent implements Market{
	private String name;
	private EllenCookRole cook;
	private EllenCashierRole cashier;

	Timer timer = new Timer();
	Map<String, Food> inventory = new TreeMap<String, Food>();	//the MARKET'S inventory
	enum RequestState {processed, updating, fulfilled, delivered};
	private List<Order>restockRequests = Collections.synchronizedList(new ArrayList<Order>());
	
	
	public EllenMarketRole(String name, int steakAmount, int pizzaAmount, int pastaAmount, int soupAmount) {
		super();

		this.name = name;
		
		//initialize inventory map
        inventory.put("steak", new Food("steak", steakAmount, 20));	//type, amount, price$$
        inventory.put("pizza", new Food("pizza", pizzaAmount, 5));
        inventory.put("pasta", new Food("pasta", pastaAmount, 10));
        inventory.put("soup", new Food("soup", soupAmount, 5));
	}

	public void setCook(EllenCookRole cook){
		this.cook = cook;
	}
	
	public void setCashier(EllenCashierRole cashier){
		this.cashier = cashier;
	}
	
	public Collection<Order> getRestockRequests() {
		return restockRequests;
	}
	
	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}
	
	// Messages
	
	public void msgINeedInventory(String choice, int amountReq){		//from cook, when out of food
		print("Received msgINeedInventory for " + amountReq + " " + choice + "s");
		Order o = new Order(choice, amountReq);		//don't need to know which cook it's from; there's only one
		restockRequests.add(o);
		o.s = RequestState.processed;
		
		stateChanged();
	}
	
	public void msgRequestDone(Order o){
		print("Timer: Request Done!");
		o.s = RequestState.fulfilled;
		stateChanged();
	}
	
	public void msgHereIsPayment(int amount){	//from cashier
		print("Received msgHereIsPayment: $" + amount);
		
		//...?
	}

	 // Scheduler.  Determine what action is called for, and do it.
	 
	protected boolean pickAndExecuteAnAction() {
		
		synchronized(restockRequests){
			for (Order r : restockRequests) {
				if (r.s == RequestState.fulfilled){
					DeliverIt(r);
					return true;
				}
			}
		}

		synchronized (restockRequests){
			for (Order r : restockRequests) {
				if (r.s == RequestState.processed){
					TryToFulfillIt(r);
					return true;
				}
			}
		}


		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}
	

	// Actions
	
	public void TryToFulfillIt(Order o){
		Food f = inventory.get(o.choice);
		print("Amount of " + o.choice + " inventory = " + f.quantityLeft);
		if (f.quantityLeft == 0){
			cook.msgCantFulfill(o.choice, o.amountRequested);	
			print("Sorry, completely out of " + f.type);
			o.s = RequestState.delivered;		//not necessary
			restockRequests.remove(o);
			return;
		}
		if (f.quantityLeft < o.amountRequested){
			cook.msgCantFulfill(o.choice, (o.amountRequested - f.quantityLeft));		//fulfills all that it can
		}
		
		FulfillIt(o);		
	}
	
	private void FulfillIt(final Order o){
		Food f = inventory.get(o.choice);
		
		if (f.quantityLeft > o.amountRequested){
			f.quantityLeft -= o.amountRequested;
			o.amountFulfilled = o.amountRequested;
		}
		else {
			o.amountFulfilled = f.quantityLeft;
			f.quantityLeft = 0;
		}
		print("Quantity left of " + inventory.get(o.choice).type + ": " + inventory.get(o.choice).quantityLeft);
		
		o.s = RequestState.updating;
		
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done fulfilling order, cookie=" + cookie);
				msgRequestDone(o);
			}
		}, 30000);		//the amount of time it takes to fulfill an inventory request
	}
	
	private void DeliverIt(Order o){
		//DoPlating(o);	//cook animation
		
		cook.msgHereIsInventory(o.choice, o.amountFulfilled, this);
		//message cashier here
		print("Charging cashier: $" + (inventory.get(o.choice).price)*o.amountFulfilled);
		cashier.msgHereIsMarketBill((inventory.get(o.choice).price)*o.amountFulfilled, this);
		
		o.s = RequestState.delivered;		//not really necessary
		restockRequests.remove(o);
	}
	
	
	//inner classes for Market
	private class Order {		//what the cook wants
		String choice;
		int amountRequested;
		int amountFulfilled;
		RequestState s;
		
		Order(String choice, int amountReq){
			this.choice = choice;
			this.amountRequested = amountReq;
		}
		
	}
	private class Food {		//what the market has
		String type;
		int quantityLeft; 
		int price;
						
		Food (String type, int totalQuantity, int price){
			this.type = type;
			this.quantityLeft = totalQuantity;
			this.price = price;
		}
	}
}

