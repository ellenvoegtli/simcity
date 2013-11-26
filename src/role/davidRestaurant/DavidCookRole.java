package role.davidRestaurant;

import agent.Agent;
import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.restaurants.restaurant_zhangdt.gui.CookGui;
import mainCity.restaurants.restaurant_zhangdt.gui.WaiterGui;
import mainCity.restaurants.restaurant_zhangdt.interfaces.Cook;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import role.Role;
import role.davidRestaurant.DavidMarketRole;
import role.davidRestaurant.DavidCustomerRole.AgentEvent;
import role.davidRestaurant.DavidCustomerRole.AgentState;
import role.davidRestaurant.DavidWaiterRole.myCustomer;

public class DavidCookRole extends Role implements Cook{
	
/*   Data   */ 
	
	private String name; 
	Timer cookTimer = new Timer(); 
	
	boolean onDuty;
	
	enum CookStatus
	{none, Opening, sendingOrder, Checked, massOrderReady, recievedOrder, NoFood, ordering} 
	CookStatus cstate = CookStatus.Opening; 
	
	enum OrderStatus 
	{none, Opening, pending, handledOrder, massOrderReady, cooking, cooked, done, NoFood }; 
	
	enum FoodStatus 
	{none, empty, requested, delivered, marketEmpty} 
	
	class Food {
		String Choice; 
		int quantity;
		int cookingTime; 
		int amountToOrder; 
		int low = 3; 
		FoodStatus fstate = FoodStatus.none; 
		
		public Food(String c, int q, int ct){
			Choice = c; 
			quantity = q; 
			cookingTime = ct; 
		}
	}
	
	//List<Food> Inventory = new ArrayList<Food>(); 
	Map<String, Food> Inventory = new TreeMap<String, Food>(); 
	Map<String, Integer> foodAvailableAtMarket = new TreeMap<String, Integer>(); 
	
	List<DavidMarketRole> marketAgent = new ArrayList<DavidMarketRole>();
	
	List<String> menu = new ArrayList<String>();
	
	String MarketOrder;
	int MarketChoice = 0;
	
	boolean cooking = false;
	
	class Order { 
		
		DavidWaiterRole Waiter; 
		String Choice; 
		int tableNum; 
		OrderStatus os; 
		
		public Order(DavidWaiterRole w, String choice2, int table) {
			Waiter = w; 
			Choice = choice2; 
			tableNum = table; 
		}
		
	}
	
	
	List<Order> pendingOrders = new ArrayList<Order>(); 
	
	DavidCashierRole cashier; 
	
	private CookGui gui = null;
	
	private Semaphore atGrill = new Semaphore(0,true);
	
	//Constructor
	public DavidCookRole(String name, PersonAgent p) { 
		super(p); 
		onDuty = true;
		this.name = name; 
		
		menu.add("Steak");
		menu.add("Chicken");
		menu.add("Salad");
		menu.add("Pizza");
		
		Inventory.put("Steak", new Food("Steak", 3, 5000));	//type, cookingTime, amount
        Inventory.put("Chicken", new Food("Chicken", 3, 2500));
        Inventory.put("Salad", new Food("Salad", 3, 1500));
        Inventory.put("Pizza", new Food("Pizza", 3, 2000));
        
        log("Inventory Steak: " + Inventory.get("Steak").quantity);
		log("Inventory Chicken: " + Inventory.get("Chicken").quantity);
		log("Inventory Salad: " + Inventory.get("Salad").quantity);
		log("Inventory Pizza: " + Inventory.get("Pizza").quantity);
        
        foodAvailableAtMarket.put("Steak", 0);
        foodAvailableAtMarket.put("Chicken", 0);
        foodAvailableAtMarket.put("Salad", 0);
        foodAvailableAtMarket.put("Pizza", 0);
	}
	
	public void log(String s) { 
		AlertLog.getInstance().logMessage(AlertTag.DAVID_RESTAURANT, this. getName(), s); 
		AlertLog.getInstance().logMessage(AlertTag.DAVID_COOK, this.getName(), s);
	}
	
/*   Messages   */ 
	public void msgHereIsAnOrder (DavidWaiterRole w, String choice, int table) {
		log("msgHereIsAnOrder Called");
		//gui.DoMoveToFridge();
		Order newOrder = new Order(w, choice, table);
		newOrder.os = OrderStatus.pending; 
		pendingOrders.add(newOrder);
		stateChanged();
	}
	
	public void msgDoneCooking(Order o) {
		log(o.Choice + " done cooking..."); 
		o.os = OrderStatus.cooked; 
		stateChanged();
	}
	
	public void msgHereIsYourOrder (Map<String, Integer> inventoryFulfilled) {
		log("msgHereIsYourOrder Called"); 
		for (Map.Entry<String, Integer> entry : inventoryFulfilled.entrySet()){
			log("Had " + Inventory.get(entry.getKey()).quantity + " " + Inventory.get(entry.getKey()).Choice + "(s).");
			Inventory.get(entry.getKey()).quantity += entry.getValue();
			log("Now have " + Inventory.get(entry.getKey()).quantity + " " + Inventory.get(entry.getKey()).Choice + "(s).");
			Food f = Inventory.get(entry.getKey());
			f.fstate = FoodStatus.delivered;
		}
		stateChanged(); 
	}
	
	public void msgMarketOutOfOrder() { 
		log("msgMarketOutOfOrder"); 
		if(MarketChoice == 0) {
			MarketChoice = 1;
		}
		else if(MarketChoice == 1) {
			MarketChoice = 2;
		}
		else if(MarketChoice == 2) {
			log("Markets all unavailable"); 
			cstate = CookStatus.none;
			stateChanged();
		}
		else {
			cstate = CookStatus.sendingOrder;
			stateChanged();
		}
		
	}
	
	public void msgMassOrderReady() {
		log ("msgMassOrderReady recieved"); 
		cstate = CookStatus.massOrderReady;
		stateChanged();
	}
	
	public void msgFinishedMovingToGrill() { 
		if(atGrill.availablePermits() < 1){
			log ("msgFinishedMoving recieved"); 
			atGrill.release(); 
			stateChanged();
		}
	}
	
	public void msgGoOffDuty(double d) {
		addToCash(d); 
		onDuty = false; 
		stateChanged();
	}
	

	
/*   Scheduler   */ 
	
	public boolean pickAndExecuteAnAction() { 
		try{	
			if(cstate == CookStatus.Opening){
				CheckInventory();
				return true;
			}
			
			if(pendingOrders.size() != 0){
				for(Order currentOrder : pendingOrders) {
					if(currentOrder.os == OrderStatus.pending && cooking == false){ 
						cookOrder(currentOrder);
						return true;
					}
					if(currentOrder.os == OrderStatus.cooked){ 
						log("Order is ready");
						OrderIsReady(currentOrder); 
						return true;
					}
				}
			}
			
		synchronized(menu){	
			Map<String, Integer> inventoryNeeded = new TreeMap<String, Integer>(); 
			for(String i : menu) { 
				if(Inventory.get(i).fstate == FoodStatus.empty) { 
					inventoryNeeded.put(i, Inventory.get(i).amountToOrder);
				}
			}
			if(!inventoryNeeded.isEmpty()) { 
				OrderFromMarket(inventoryNeeded); 
				return true;
			}
		}
			
			return false; 
		}
		catch(ConcurrentModificationException r){ 
			return false;
		}
	}
	
/*   Actions   */
	
	public void CheckInventory(){
		Map<String, Integer>lowInventory = new TreeMap<String, Integer>();
		
		for (String c : menu){
			if (Inventory.get(c).quantity <= Inventory.get(c).low){
				log("Adding " + Inventory.get(c).Choice + " to market order");
				Inventory.get(c).amountToOrder = 10;
				lowInventory.put(c, Inventory.get(c).amountToOrder);
				//OrderFromMarket(inventory.get(c), inventory.get(c).amountToOrder, inventory.get(c).nextMarket);
			}
		}
		if(!lowInventory.isEmpty()){
			OrderFromMarket(lowInventory);
		}
		
		cstate = CookStatus.Checked; 
		stateChanged();
	}
	
	public void cookOrder(final Order o) { 
		log("cookOrder called by " + o.Waiter.getName());
		cooking = true;
		Map<String, Integer> inventoryNeeded = new TreeMap<String, Integer>(); 
		Food tempFood = Inventory.get(o.Choice);
		
		//quantity of food is low. Ordering 10 more
		if(tempFood.quantity <= tempFood.low) {
			log("checking food stats: " + tempFood.quantity + " " + tempFood.low);
			inventoryNeeded.put(tempFood.Choice, 10); 
			OrderFromMarket(inventoryNeeded);
			
		}
		
		if(tempFood.quantity == 0) {
			log("Currently out of " + o.Choice);
			o.Waiter.msgOutOfFood(o.Choice);
			pendingOrders.remove(o);
			cooking = false;
			stateChanged();
		}
		
			o.os = OrderStatus.cooking; 
			tempFood.quantity --;
			Inventory.get(tempFood.Choice).quantity--; 
			
			cookTimer.schedule(new TimerTask() {
				public void run() {
					log("Done Cooking");
					msgDoneCooking(o); 
				    //gui.DoMoveToPlating();
					cooking = false;
					stateChanged();
				}
			},
			tempFood.cookingTime);
			
	}
	
	public void OrderFromMarket(Map<String, Integer> inventoryNeeded){ 
		if (ContactList.getInstance().marketGreeter != null) {
			ContactList.getInstance().marketGreeter.msgINeedInventory("DavidRestaurant", this, cashier, inventoryNeeded); 
			for(Map.Entry<String, Integer> entry : inventoryNeeded.entrySet()) { 
				Inventory.get(entry.getKey()).fstate = FoodStatus.requested; 
			}
		}
		else{ 
			for (Map.Entry<String, Integer> entry : inventoryNeeded.entrySet()) { 
				Inventory.get(entry.getKey()).fstate = FoodStatus.empty; 
			}
		}
	}
 
	
	public void OrderIsReady(Order o){ 
		for(int i=0; i<o.Waiter.customerList.size(); i++) {
			if(o.Waiter.customerList.get(i).t.getTable() == o.tableNum) {
				o.Waiter.msgOrderIsReady(o.Waiter.customerList.get(i), o);
			}
		}
		pendingOrders.remove(o);
		o.os = OrderStatus.done; 
		stateChanged();
	}
	
// utilities 
	
	public void setGui(CookGui RG) {
		gui = RG;
	}
	
	public void addMarket(DavidMarketRole m) {
		marketAgent.add(m);
	}
	
	public void addCashier(DavidCashierRole c) {
		cashier = c;
	}
	
	public void emptyInventory() {
		for(int i=0; i<Inventory.size(); i++) {
			Inventory.get(i).quantity = 0;
			log("Setting the quantity of " + Inventory.get(i).Choice + " to " + Inventory.get(i).quantity);
		}
	}
	
	public void emptySteak() {
		Inventory.get(0).quantity = 0;
		log("Setting the quantity of " + Inventory.get(0).Choice + " to " + Inventory.get(0).quantity);
	}
	
	public void emptyChicken() {
		Inventory.get(1).quantity = 0;
		log("Setting the quantity of " + Inventory.get(1).Choice + " to " + Inventory.get(1).quantity);
	}
	
	public void emptySalad() {
		Inventory.get(2).quantity = 0;
		log("Setting the quantity of " + Inventory.get(2).Choice + " to " + Inventory.get(2).quantity);
	}
	
	public void emptyPizza() {
		Inventory.get(3).quantity = 0;
		log("Setting the quantity of " + Inventory.get(3).Choice + " to " + Inventory.get(3).quantity);
	}
	
	public void ManualOrder() { 
		cstate = CookStatus.NoFood; 
		stateChanged();
	}

}
