
package role.ellenRestaurant;


import java.util.*;

import mainCity.PersonAgent;
import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.EllenRestaurant.gui.*;
import mainCity.restaurants.EllenRestaurant.interfaces.*;
import mainCity.restaurants.EllenRestaurant.sharedData.*;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.contactList.*;
import role.Role;

 // Restaurant Cook Agent

public class EllenCookRole extends Role implements Cook{
	static final int NTABLES = 3;//a global for the number of tables.
	static final int WINDOWX = 500, WINDOWY = 370;
	
	private String name;
	private RevolvingStand stand;
	private EllenMenu menu;
	private Cashier cashier;
	private KitchenGuiInterface kitchenGui = null;
	boolean notAdded = true;
	boolean greeterNull = true;
	Timer timer = new Timer();
	
	public List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());	//from customers
	private Map<String, Food> inventory = Collections.synchronizedMap(new TreeMap<String, Food>());	//what the cook has available
	private Map<String, Integer> foodAtAvailableMarket = Collections.synchronizedMap(new TreeMap<String, Integer>());
		
	public enum OrderState {pending, cooking, plated, finished, pickedUp};
	enum FoodState {none, depleted, requested, delivered, tryAgain};
	
	private boolean isCheckingStand;
	private boolean opened = true;
	private boolean onDuty;
	private boolean entered;
	

	public EllenCookRole(PersonAgent p, String name/*, int steakAmount, int pizzaAmount, int pastaAmount, int soupAmount*/) {
		super(p);

		this.name = name;
		opened = true;
		isCheckingStand = false;
		onDuty = true;
		entered = false;
		
		//initialize inventory map
        inventory.put("pasta", new Food("pasta", 5000, 8));	//type, cookingTime, amount
        inventory.put("pizza", new Food("pizza", 2500, 8));
        inventory.put("meatballs", new Food("meatballs", 1000, 8));
        inventory.put("bread", new Food("bread", 2000, 8));
        
        foodAtAvailableMarket.put("pasta", 0);
        foodAtAvailableMarket.put("pizza", 0);
        foodAtAvailableMarket.put("meatballs", 0);
        foodAtAvailableMarket.put("bread", 0);
	}

	public void setCashier(Cashier c){
		this.cashier = c;
	}
	public void setMenu(EllenMenu m){
		this.menu = m;
	}
	public void setStand(RevolvingStand s){
		stand = s;
	}
	public void setOpened(boolean o){
		opened = o;
	}
	public List<Order> getOrders(){
		return orders;
	}
	public String getName() {
		return name;
	}
	public void setKitchenGui(KitchenGuiInterface gui){
		kitchenGui = gui;
	}
	//for alert log trace statements
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.ELLEN_COOK, this.getName(), s);
	}
	
	
	// Messages
	public void depleteInventory(){
		log("depleteInventory message received");
		for (Map.Entry<String, Food> entry : this.inventory.entrySet()){
			Food f = entry.getValue();
			f.amount = 0;
			f.s = FoodState.depleted;
			f.amountToOrder = f.capacity;
		}
		stateChanged();
	}
	
	public void msgHereIsOrder(String choice, int table, Waiter w){
		log(w.getName() + ", received msgHereIsOrder: " + choice);
		Order o = new Order(choice, table, w);
		orders.add(o);
		o.s = OrderState.pending;
		stateChanged();
	}

	
	public void msgFoodDone(Order o){	//from timer
		o.s = OrderState.plated;
		log(o.choice + " done cooking!");
		stateChanged();
	}
	
	//new market message
	public void msgHereIsYourOrder(Map<String, Integer>inventoryFulfilled){
		log("Received msgHereIsYourOrder from market");
		
		for (Map.Entry<String, Integer> entry : inventoryFulfilled.entrySet()){
			log("Had " + inventory.get(entry.getKey()).amount + " " + inventory.get(entry.getKey()).type + "(s).");
			inventory.get(entry.getKey()).amount += entry.getValue();
			
			log("Now have " + inventory.get(entry.getKey()).amount + " " + inventory.get(entry.getKey()).type + "(s).");
			Food f = inventory.get(entry.getKey());
			f.s = FoodState.delivered;
		}
		
		stateChanged();	
	}
	
	public void msgCantFulfill(String choice, int amountStillNeeded){
		log("Received msgCantFulfill: still need " + amountStillNeeded + " " + choice + "(s)");

		Food f = inventory.get(choice);
		f.s = FoodState.tryAgain;
		f.amountToOrder = amountStillNeeded;
		foodAtAvailableMarket.put(f.type, f.nextMarket++);
		stateChanged();
	}
	
	public void pickingUpFood(int table){
		log("Received pickingUpFood for table " + table);
		Order order = null;
		synchronized(orders){
			for (Order o: orders){
				if (o.table == table){
					order = o;
					break;
				}
			}
		}
		order.s = OrderState.pickedUp;
		stateChanged();
	}
	
	public void msgCheckStand() {		//from RestaurantPanel
		log("Told to check stand");
		if(!isCheckingStand) {
			isCheckingStand = true;
			stateChanged();
		}
	}
	
	public void msgGoOffDuty(double amount){
		addToCash(amount);
		onDuty = false;
		stateChanged();
	}
	

	 // Scheduler.  Determine what action is called for, and do it.
	 
	public boolean pickAndExecuteAnAction() {
		if (entered){
			ContactList.getInstance().setEllenCook(this);
			entered = false;
		}
		
		if (opened){
				//check inventory when restaurant opens
				OrderFoodThatIsLow();
				opened = false;
				return true;

		}
		
		synchronized(orders){
			for (Order thisOrder : orders){
				if (thisOrder.s == OrderState.pickedUp){
					InformKitchen(thisOrder);
					return true;
				}
			}
		}
		
		synchronized(orders){
			for (Order thisOrder : orders) {
				if (thisOrder.s == OrderState.plated){
					PlateIt(thisOrder);
					return true;
				}
			}
		}
		synchronized(orders){
			for (Order thisOrder : orders) {
				if (thisOrder.s == OrderState.pending){
					TryToCookIt(thisOrder);
					return true;
				}
			}
		}
		
		synchronized(menu.menuItems){
			Map<String, Integer>inventoryNeeded = Collections.synchronizedMap(new TreeMap<String, Integer>());
			synchronized(inventoryNeeded){
				for (String i : menu.menuItems){
					if (inventory.get(i).s == FoodState.depleted){
						inventoryNeeded.put(i, inventory.get(i).amountToOrder);
					}
				}
			}
			if(!inventoryNeeded.isEmpty()){
				OrderFromMarket(inventoryNeeded);	//market number?
				return true;
			}
		}
		
		if (isCheckingStand){
			checkRevolvingStand();
			return true;
		}
		
		if (orders.isEmpty() && !onDuty){
			setInactive();
			onDuty = true;
			entered = true;
		}
		
		

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}
	

	// Actions
	
	public void TryToCookIt(final Order o){
		Map<String, Integer>inventoryNeeded = Collections.synchronizedMap(new TreeMap<String, Integer>());
		Food f = inventory.get(o.choice);
		log("Amount of " + f.type + " left = " + f.amount);

		if (f.amount <= f.low && f.s == FoodState.none){
			inventoryNeeded.put(f.type, (f.capacity-f.amount));
			OrderFromMarket(inventoryNeeded);
		}
		if (f.amount == 0){
			log("Out of " + f.type);
			o.waiter.msgOutOfFood(o.choice, o.table);	//waiter doesn't come back on GUI to get this msg
			orders.remove(o);
			return;
		}
		
		f.amount--;
		o.s = OrderState.cooking;
		
		int i = 1;
		synchronized(orders){
			for (Order ord : orders){
				if (ord.equals(o)){
					log("kitchengui = " + kitchenGui);
					kitchenGui.DoGrilling(o.choice, o.table, WINDOWX-30, WINDOWY/2-60 + i*20);
					break;
				}
				else
					i++;
			}
		}
		timer.schedule(new TimerTask() {
			public void run() {
				log("Cooking...");
				msgFoodDone(o);
			}
		}, inventory.get(o.choice).cookingTime);

	}
	
	public void PlateIt(Order o){
		int i = 1;
		synchronized(orders){
			for (Order ord : orders){
				if (ord.equals(o)){
					kitchenGui.DoPlating(o.choice, o.table, WINDOWX - 90, WINDOWY/2-60 + i*20);
					break;
				}
				else
					i++;
			}
		}
		o.waiter.msgOrderDoneCooking(o.choice, o.table);
		o.s = OrderState.finished;
		
		if (!onDuty){
			setInactive();
			onDuty = true;
		}
	}
	
	public void OrderFoodThatIsLow(){
		Map<String, Integer>lowInventory = Collections.synchronizedMap(new TreeMap<String, Integer>());
		
		synchronized(menu.menuItems){
			synchronized(lowInventory){
				for (String c : menu.menuItems){
					if (inventory.get(c).amount <= inventory.get(c).low){
						log("Adding " + inventory.get(c).type + " to market order");
						inventory.get(c).amountToOrder = (inventory.get(c).capacity - inventory.get(c).amount);
						lowInventory.put(c, inventory.get(c).amountToOrder);
					}
				}
			}
		}
		
		if(!lowInventory.isEmpty()){
			OrderFromMarket(lowInventory);
		}
	}
	
	public void InformKitchen(Order o){
		kitchenGui.deleteOrderGui(o.table);
		orders.remove(o);
	}
	
	private void checkRevolvingStand(){
		log("Checking revolving stand");
		isCheckingStand = false;
		
		if(stand.isEmpty()){
			log("No orders to pick up.");
			return;
		}
		
		while(!stand.isEmpty()){
			log("Picking up order");
			OrderTicket t = stand.remove();
			Order o = new Order(t.getChoice(), t.getTable(), t.getWaiter());
			o.s = OrderState.pending;
			orders.add(o);
			stateChanged();
		}
			
	}
	
	
	public void OrderFromMarket(Map<String, Integer>inventoryNeeded){
		switch((int) (Math.random() * 2)){
		case 0:
			if (ContactList.getInstance().getMarket().getGreeter() == null){
				if (ContactList.getInstance().getMarket2().getGreeter() != null){
					ContactList.getInstance().getMarket2().getGreeter().msgINeedInventory("EllenRestaurant", inventoryNeeded);
					synchronized(inventoryNeeded){
						for (Map.Entry<String, Integer> entry : inventoryNeeded.entrySet()){
							inventory.get(entry.getKey()).s = FoodState.requested;
						}
					}
				}
				else {
					synchronized(inventoryNeeded){
						for (Map.Entry<String, Integer> entry : inventoryNeeded.entrySet()){
							inventory.get(entry.getKey()).s = FoodState.depleted;
						}
					}
				}
			}
			else {
				ContactList.getInstance().getMarket().getGreeter().msgINeedInventory("EllenRestaurant", inventoryNeeded);
				synchronized(inventoryNeeded){
					for (Map.Entry<String, Integer> entry : inventoryNeeded.entrySet()){
						inventory.get(entry.getKey()).s = FoodState.requested;
					}
				}
			}
			break;
			
		case 1:
			if (ContactList.getInstance().getMarket2().getGreeter() == null){
				if (ContactList.getInstance().getMarket().getGreeter() != null){
					ContactList.getInstance().getMarket().getGreeter().msgINeedInventory("EllenRestaurant", inventoryNeeded);
					synchronized(inventoryNeeded){
						for (Map.Entry<String, Integer> entry : inventoryNeeded.entrySet()){
							inventory.get(entry.getKey()).s = FoodState.requested;
						}
					}
				}
				else {
					synchronized(inventoryNeeded){
						for (Map.Entry<String, Integer> entry : inventoryNeeded.entrySet()){
							inventory.get(entry.getKey()).s = FoodState.depleted;
						}
					}
				}
			}
			else {
				ContactList.getInstance().getMarket2().getGreeter().msgINeedInventory("EllenRestaurant", inventoryNeeded);
				synchronized(inventoryNeeded){
					for (Map.Entry<String, Integer> entry : inventoryNeeded.entrySet()){
						inventory.get(entry.getKey()).s = FoodState.requested;
					}
				}
			}
			break;
		default:
				break;
			
		}
		
			
	}
	

	
	//inner classes
	public class Order {	//public for testing purposes only
		Waiter waiter;
		String choice;
		int table;
		OrderState s;
		
		Order(String choice, int table, Waiter w){
			this.choice = choice;
			this.table = table;
			this.waiter = w;
		}
		public OrderState getState(){
			return s;
		}
		public void setState(OrderState state){
			s = state;
		}
		
	}
	private class Food {
		String type;
		int cookingTime;
		int amount; 
		int amountToOrder;
		int capacity = 15;
		int low = 5;
		int nextMarket = 0;
		
		FoodState s;
		
		Food (String type, int cookingTime, int amount){
			this.type = type;
			this.cookingTime = cookingTime;
			this.amount = amount;
			this.s = FoodState.none;
		}
	}
}

