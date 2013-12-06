
package role.ellenRestaurant;

import agent.Agent;

import java.util.*;

import mainCity.PersonAgent;
import mainCity.market.*;
import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.EllenRestaurant.gui.*;
import mainCity.restaurants.EllenRestaurant.interfaces.*;
import mainCity.restaurants.EllenRestaurant.sharedData.*;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
//import mainCity.role.marcusRestaurant.MarcusCookRole.CookStatus;
import mainCity.interfaces.*;
import mainCity.contactList.*;
import role.Role;

 // Restaurant Cook Agent

public class EllenCookRole extends Role implements Cook{
	static final int NTABLES = 3;//a global for the number of tables.

	private String name;
	private RevolvingStand stand;
	private EllenMenu menu;
	private EllenCashierRole cashier;
	private KitchenGui kitchenGui = null;
	boolean notAdded = true;
	boolean greeterNull = true;
	Timer timer = new Timer();
	
	private Collection<Order> orders = Collections.synchronizedList(new ArrayList<Order>());	//from customers
	//private List<EllenMarketRole> markets = Collections.synchronizedList(new ArrayList<EllenMarketRole>());
	private Map<String, Food> inventory = new TreeMap<String, Food>();	//what the cook has available
	private Map<String, Integer> foodAtAvailableMarket = new TreeMap<String, Integer>();
		
	enum OrderState {pending, cooking, plated, finished, pickedUp};
	enum FoodState {none, depleted, requested, delivered, tryAgain};
	
	private boolean isCheckingStand;
	private boolean opened = true;
	private boolean onDuty;
	

	public EllenCookRole(PersonAgent p, String name/*, int steakAmount, int pizzaAmount, int pastaAmount, int soupAmount*/) {
		super(p);

		this.name = name;
		opened = true;
		isCheckingStand = false;
		onDuty = true;
		
		//initialize inventory map
        inventory.put("pasta", new Food("pasta", 5000, 8));	//type, cookingTime, amount
        inventory.put("pizza", new Food("pizza", 2500, 8));
        inventory.put("meatballs", new Food("meatballs", 1000, 8));
        inventory.put("bread", new Food("bread", 2000, 0));
        
        foodAtAvailableMarket.put("pasta", 0);
        foodAtAvailableMarket.put("pizza", 0);
        foodAtAvailableMarket.put("meatballs", 0);
        foodAtAvailableMarket.put("bread", 0);
	}
	/*
	public void addMarket(EllenMarketRole m){	//hack
		markets.add(m);
	}
	*/
	public void setCashier(EllenCashierRole c){
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
	public String getName() {
		return name;
	}
	public void setKitchenGui(KitchenGui gui){
		kitchenGui = gui;
	}
	
	
	//for alert log trace statements
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.ELLEN_COOK, this.getName(), s);
	}
	
	
	// Messages
	public void depleteInventory(String choice){
		log("Deplete inventory message received");
		Food f = inventory.get(choice);
		f.amount = 0;
		f.s = FoodState.depleted;
		f.amountToOrder = f.capacity;
		stateChanged();
	}
	
	public void msgHereIsOrder(String choice, int table, EllenWaiterRole w){
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
		//print("In cook scheduler");
		
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
			Map<String, Integer>inventoryNeeded = new TreeMap<String, Integer>();
			for (String i : menu.menuItems){
				if (inventory.get(i).s == FoodState.depleted){
					inventoryNeeded.put(i, inventory.get(i).amountToOrder);
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
		}
		
		//include in SimCity?
		/*
		synchronized(menu.menuItems){
			for (String i : menu.menuItems){
				if (inventory.get(i).s == FoodState.tryAgain){
					//OrderFromMarket(inventory.get(i), inventory.get(i).amountToOrder, inventory.get(i).nextMarket);
					return true;
				}
			}
		}
		*/
		
		

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}
	

	// Actions
	public void OrderFromMarket(Map<String, Integer>inventoryNeeded){
		if (ContactList.getInstance().marketGreeter != null) {
			ContactList.getInstance().marketGreeter.msgINeedInventory("EllenRestaurant", this, cashier, inventoryNeeded);
			for (Map.Entry<String, Integer> entry : inventoryNeeded.entrySet()){
				inventory.get(entry.getKey()).s = FoodState.requested;
			}
		}
		else {
			for (Map.Entry<String, Integer> entry : inventoryNeeded.entrySet()){
				inventory.get(entry.getKey()).s = FoodState.depleted;
			}
		}
			
	}
	
	public void TryToCookIt(final Order o){
		Map<String, Integer>inventoryNeeded = new TreeMap<String, Integer>();
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
					kitchenGui.DoGrilling(o.choice, o.table, 550-30, 350/2-60 + i*20);
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
					kitchenGui.DoPlating(o.choice, o.table, 550-90, 350/2-60 + i*20);
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
		Map<String, Integer>lowInventory = new TreeMap<String, Integer>();
		
		for (String c : menu.menuItems){
			if (inventory.get(c).amount <= inventory.get(c).low){
				log("Adding " + inventory.get(c).type + " to market order");
				inventory.get(c).amountToOrder = (inventory.get(c).capacity - inventory.get(c).amount);
				lowInventory.put(c, inventory.get(c).amountToOrder);
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
			OrderTicket t = stand.remove();
			orders.add(new Order(t.getChoice(), t.getTable(), t.getWaiter()));
			stateChanged();
		}
			
	}
	

	
	//inner classes
	private class Order {
		Waiter waiter;
		String choice;
		int table;
		OrderState s;
		
		Order(String choice, int table, Waiter w){
			this.choice = choice;
			this.table = table;
			this.waiter = w;
		}
		
	}
	private class Food {
		String type;
		int cookingTime;
		int amount; 
		int amountToOrder;
		int capacity = 10;
		int low = 3;
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
