package mainCity.restaurants.restaurant_zhangdt;

import agent.Agent;
import mainCity.restaurants.restaurant_zhangdt.DavidMarketRole;
import mainCity.restaurants.restaurant_zhangdt.DavidCustomerRole.AgentEvent;
import mainCity.restaurants.restaurant_zhangdt.DavidCustomerRole.AgentState;
import mainCity.restaurants.restaurant_zhangdt.DavidWaiterRole.myCustomer;
import mainCity.restaurants.restaurant_zhangdt.gui.CookGui;
import mainCity.restaurants.restaurant_zhangdt.gui.WaiterGui;
import mainCity.restaurants.restaurant_zhangdt.gui.RestaurantGui;

import java.util.*;
import java.util.concurrent.Semaphore;

public class DavidCookRole extends Agent{
	
/*   Data   */ 
	
	private String name; 
	Timer cookTimer = new Timer(); 
	
	enum OrderStatus 
	{none, Opening, pending, sendingOrder, ordering, recievedOrder, handledOrder, massOrderReady, cooking, cooked, done, NoFood }; 
	OrderStatus oStat = OrderStatus.Opening; 
	
	class Food {
		String Choice; 
		int quantity;
		int cookingTime; 
		int amountToOrder; 
		int low = 3; 
		
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
	
	String MarketOrder;
	
	int MarketChoice = 0;
	
	boolean cooking = false;
	
	class Order { 
		
		DavidWaiterRole Waiter; 
		String Choice; 
		int tableNum; 
		
		public Order(DavidWaiterRole w, String choice2, int table) {
			Waiter = w; 
			Choice = choice2; 
			tableNum = table; 
		}
		
	}
	
	
	List<Order> pendingOrders = new ArrayList<Order>(); 
	
	CashierAgent cashier; 
	
	private CookGui gui = null;
	
	private Semaphore atGrill = new Semaphore(0,true);
	
	//Constructor
	public DavidCookRole(String name) { 
		super(); 
		this.name = name; 
		
		Inventory.add(new Food("Steak", 5, 5000));
		Inventory.add(new Food("Chicken", 5, 5000));
		Inventory.add(new Food("Salad", 5, 5000));
		Inventory.add(new Food("Pizza", 5, 5000));
	}
	
/*   Messages   */ 
	public void msgHereIsAnOrder (DavidWaiterRole w, String choice, int table) {
		print("msgHereIsAnOrder Called");
		gui.DoMoveToFridge();
		Order newOrder = new Order(w, choice, table);
		oStat = OrderStatus.pending; 
		pendingOrders.add(newOrder);
		stateChanged();
	}
	
	public void msgOrderFromMarket () {
		print("msgOrderFromMarket Called"); 
		oStat = OrderStatus.recievedOrder; 
		stateChanged(); 
	}
	
	public void msgMarketOutOfOrder() { 
		print("msgMarketOutOfOrder"); 
		if(MarketChoice == 0) {
			MarketChoice = 1;
		}
		else if(MarketChoice == 1) {
			MarketChoice = 2;
		}
		else if(MarketChoice == 2) {
			print("Markets all unavailable"); 
			oStat = OrderStatus.none;
			stateChanged();
		}
		else {
			oStat = OrderStatus.sendingOrder;
			stateChanged();
		}
		
	}
	
	public void msgMassOrderReady() {
		print ("msgMassOrderReady recieved"); 
		oStat = OrderStatus.massOrderReady;
		stateChanged();
	}
	
	public void msgFinishedMovingToGrill() { 
		if(atGrill.availablePermits() < 1){
			print ("msgFinishedMoving recieved"); 
			atGrill.release(); 
			stateChanged();
		}
	}
	

	
/*   Scheduler   */ 
	
	protected boolean pickAndExecuteAnAction() { 
		try{	
			if(oStat == OrderStatus.Opening){
				CheckInventory();
			}
			
			if(oStat == OrderStatus.sendingOrder){ 
				orderFromMarket(MarketChoice); 
				return true;
			}
			
			if(oStat == OrderStatus.recievedOrder){ 
				handleOrder(); 
				return true;
			}
			
			if(pendingOrders.size() != 0) {
				if(oStat == OrderStatus.pending && cooking == false){ 
					cookOrder(pendingOrders.get(0)); 
					return true;
				}
				
				if(oStat == OrderStatus.cooked){
					print("Order is ready");
					OrderIsReady(pendingOrders.get(0)); 
					return true;
				}
				
			}
			
			if(oStat == OrderStatus.NoFood){ 
				print("Refilling inventory");
				RefillInventory(); 
				return true;
			}
			
			if(oStat == OrderStatus.massOrderReady){
				print("MassOrderDelivered"); 
				MassOrderDelivered();
			}
			return false; 
		}
		catch(ConcurrentModificationException r){ 
			return false;
		}
	}
	
/*   Actions   */
	
	public void CheckInventory(){
		for(int i=0; i<Inventory.size(); i++) {
			if(Inventory.get(i).quantity == 0){
				ManualOrder();
			}
		}
		oStat = OrderStatus.pending;
		stateChanged();
	}
	
	public void cookOrder(Order o) { 
		print("cookOrder called by " + o.Waiter.getName());
		cooking = true;
		Food tempFood = new Food("", 0, 0); 
		for(int i=0; i<Inventory.size(); i++) {
			if(o.Choice == Inventory.get(i).Choice){
				tempFood = Inventory.get(i); 
			}
		}
		
		if(tempFood.quantity == 0) {
			print("Currently out of " + o.Choice);
			o.Waiter.msgOutOfFood(o.Choice);
			pendingOrders.remove(o);
			MarketOrder = tempFood.Choice;
			cooking = false;
			oStat = OrderStatus.sendingOrder; 
			stateChanged();
		}
		
		else {
			oStat = OrderStatus.cooking; 
			tempFood.quantity --;
			for(int i=0; i<Inventory.size(); i++) {
				if(tempFood.Choice == Inventory.get(i).Choice){
					Inventory.get(i).quantity = tempFood.quantity; 
					print(Inventory.get(i).Choice + " quantity: " + Inventory.get(i).quantity);
				}
			}
			cookTimer.schedule(new TimerTask() {
				public void run() {
					print("Done Cooking");
					oStat = OrderStatus.cooked;
				    gui.DoMoveToPlating();
					cooking = false;
					stateChanged();
				}
			},
			tempFood.cookingTime);
			
		}
	}
	
	public void orderFromMarket(int MarketChoice) { 
		switch(MarketChoice){
			case 0: marketAgent.get(0).msgOrderFromCook(MarketOrder); break;
			case 1: marketAgent.get(1).msgOrderFromCook(MarketOrder); break;
			case 2: marketAgent.get(2).msgOrderFromCook(MarketOrder); break;
		}
		oStat = OrderStatus.ordering;
		stateChanged();
	}
	
	public void handleOrder() { 
		oStat = OrderStatus.handledOrder; 
		for(int i=0; i<Inventory.size(); i++) { 
			if(Inventory.get(i).Choice == MarketOrder) {
				Inventory.get(i).quantity += 5;  
			}
		}
		
		for(int i=0; i<Inventory.size(); i++) { 
			print("Quantity of " + Inventory.get(i).Choice + " is " + Inventory.get(i).quantity);
			
		}
		
		stateChanged(); 
	}
	
	public void OrderIsReady(Order o){ 
		for(int i=0; i<o.Waiter.customerList.size(); i++) {
			if(o.Waiter.customerList.get(i).t.getTable() == o.tableNum) {
				o.Waiter.msgOrderIsReady(o.Waiter.customerList.get(i), o);
			}
		}
		pendingOrders.remove(o);
		oStat = OrderStatus.pending; 
		stateChanged();
	}
	
	public void RefillInventory() { 
		marketAgent.get(MarketChoice).msgMassOrderRecieved();
		oStat = OrderStatus.ordering;
		stateChanged();
	}
	
	public void MassOrderDelivered() {
		oStat = OrderStatus.handledOrder; 
		for(int i=0; i<Inventory.size(); i++) { 
			Inventory.get(i).quantity += 5;  
			print(Inventory.get(i).Choice + " quantity: " + Inventory.get(i).quantity);
		}
		stateChanged();
	}
	
// utilities 
	
	public void setGui(CookGui RG) {
		gui = RG;
	}
	
	public void addMarket(DavidMarketRole m) {
		marketAgent.add(m);
	}
	
	public void addCashier(CashierAgent c) {
		cashier = c;
	}
	
	public void emptyInventory() {
		for(int i=0; i<Inventory.size(); i++) {
			Inventory.get(i).quantity = 0;
			print("Setting the quantity of " + Inventory.get(i).Choice + " to " + Inventory.get(i).quantity);
		}
	}
	
	public void emptySteak() {
		Inventory.get(0).quantity = 0;
		print("Setting the quantity of " + Inventory.get(0).Choice + " to " + Inventory.get(0).quantity);
	}
	
	public void emptyChicken() {
		Inventory.get(1).quantity = 0;
		print("Setting the quantity of " + Inventory.get(1).Choice + " to " + Inventory.get(1).quantity);
	}
	
	public void emptySalad() {
		Inventory.get(2).quantity = 0;
		print("Setting the quantity of " + Inventory.get(2).Choice + " to " + Inventory.get(2).quantity);
	}
	
	public void emptyPizza() {
		Inventory.get(3).quantity = 0;
		print("Setting the quantity of " + Inventory.get(3).Choice + " to " + Inventory.get(3).quantity);
	}
	
	public void ManualOrder() { 
		oStat = OrderStatus.NoFood; 
		stateChanged();
	}
}
