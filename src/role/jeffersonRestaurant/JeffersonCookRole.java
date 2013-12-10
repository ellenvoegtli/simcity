public package role.jeffersonRestaurant;


import agent.Agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.TreeMap;

import role.Role;
import role.jeffersonRestaurant.JeffersonCustomerRole.*;
import role.jeffersonRestaurant.JeffersonWaiterRole.Table;
import role.marcusRestaurant.MarcusCookRole.CookStatus;
import role.market.MarketGreeterRole;
import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.restaurants.jeffersonrestaurant.gui.CookGui;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Cook;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Market;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Waiter;
import mainCity.restaurants.jeffersonrestaurant.sharedData.RevolvingStand;
import mainCity.restaurants.jeffersonrestaurant.sharedData.OrderTicket;

public class JeffersonCookRole extends Role implements Cook{

	private PersonAgent p;

	public enum OrderState
	{pending, marketOrdering, cooking, Done, Delivered};
	
	private OrderState state=OrderState.pending;
	private String name;
	public boolean onDuty;
	public boolean checkingStand;
	public CookGui cookGui = null;
	private JeffersonCashierRole cashier;
	private RevolvingStand revolvingstand;
	Map<String, Integer> cookingTimes = new HashMap<String, Integer>();
	Map<String, Integer> inventory =  new HashMap<String,Integer>();
	public List<Market> markets = Collections.synchronizedList ( new ArrayList<Market>());
	public List <Order> orders =Collections.synchronizedList ( new ArrayList<Order>());
	Timer timer = new Timer();
	Map<String,Integer> marketOrder;// = new TreeMap<String, Integer>();
	
	private List<Integer> ordersTaken =Collections.synchronizedList(new ArrayList<Integer>()); 
	
	//will hold index numbers for market in array
	int steakmarketcounter;
	int chickenmarketcounter;
	int saladmarketcounter;
	int pizzamarketcounter;
	
	
	
	public JeffersonCookRole(PersonAgent p, String name){
		super(p);
		this.name=name;
		onDuty=true;
		checkingStand=false;
		cookingTimes.put("steak",8);
		cookingTimes.put("chicken",6);
		cookingTimes.put("salad",4);
		cookingTimes.put("pizza",4);
		
		//Initial cook inventory
		inventory.put("steak",0);
		inventory.put("chicken",0);
		inventory.put("salad",0);
		inventory.put("pizza",0);
		
		//sets to first market
		steakmarketcounter =1;
		chickenmarketcounter=1;
		saladmarketcounter=1;
		pizzamarketcounter =1;
		
	}
	
	public void setGui(CookGui gui) {
		cookGui = gui;
	}

	public void setCashier(JeffersonCashierRole cashier){
		this.cashier=cashier;
	}
	
	public class Order{
		
		String Choice;
		int tableNumber;
		OrderState s;
		Waiter w;
		boolean reqRestock;
		
		public Order(int table, String choice, Waiter wa) {
			Choice=choice;
			tableNumber=table;
			s=OrderState.pending;
			w=wa;
			reqRestock=false;
			//orders.add(this);
			//print("order added to list");
		}
	}
	
	public void addMarket(Market m){
		markets.add(m);
	}
	
	public RevolvingStand getRevolvingstand() {
		return revolvingstand;
	}

	public void setRevolvingstand(RevolvingStand revolvingstand) {
		this.revolvingstand = revolvingstand;
	}
	
	public void msgCheckStand() {
		if(checkingStand==false) {
			checkingStand=true;
			stateChanged();
		}
		
	}
	
	public void depleteInventory(){
		for (Map.Entry<String, Integer> entry : inventory.entrySet()){
			entry.setValue(0);
		}
		orderStock(null, 0);
	}

	
	// Messages
	public void msgGoOffDuty(double d){
		addToCash(d);
		onDuty=false;
		stateChanged();
	}
	public void msgLeftRestaurant() {
		super.setInactive();
		onDuty=true;
		
	}

	
	public void msghereIsAnOrder (int table, String Choice, Waiter w){
		log("cook recieved order");
		orders.add(new Order (table,Choice, w));
		stateChanged();
		
	}
	
	public void msgOrderTaken(int t){
		ordersTaken.add(t);
		stateChanged();
		
	}
	
	public void msgCannotFulfill(String i, Integer q){
		log("Restock of " + i + " not fulfilled");
		//move on to next market somehow
		switch (i) {
		case "steak":
			steakmarketcounter++;
			break;
			
		case "chicken":
			chickenmarketcounter++;
			break;
			
		case "salad":
			saladmarketcounter++;
			break;
			
		case "pizza":
			pizzamarketcounter++;
			break;

		default:
			break;
		}
		synchronized(orders){
			for(Order o:orders){
				if(o.Choice==i && o.s==OrderState.marketOrdering){
				o.s=OrderState.pending;
				stateChanged();
				
				}
			}	
		}
	}
	
	public void msgRestockFulfilled(String i, Integer q){
		log("Restock of " + i + " completely fulfilled");
		int updated = inventory.get(i) + q;
		inventory.put(i, updated);
		synchronized (orders) {
		
			for(Order o:orders){
				if(o.Choice==i && o.s==OrderState.marketOrdering){
				o.s=OrderState.pending;
				stateChanged();	
				}	
			}//end for	
		}
	}
	
	public void msgPartialFulfill(String i, Integer q){
		log("Restock of " + i + " partially fulfilled");
		int updated = inventory.get(i) + q;
		inventory.put(i, updated);
		switch (i) {
		case "steak":
			steakmarketcounter++;
			break;
			
		case "chicken":
			chickenmarketcounter++;
			break;
			
		case "salad":
			saladmarketcounter++;
			break;
			
		case "pizza":
			pizzamarketcounter++;
			break;

		default:
			break;
		}
		
		synchronized(orders){
			for(Order o:orders){
				if(o.Choice==i && o.s==OrderState.marketOrdering){
				o.s=OrderState.pending;
				stateChanged();	
				}
			}	
		}
		
		
		
	}
	
	public void msgHereIsYourOrder(Map<String, Integer> restock) {
		for(Map.Entry<String,Integer> entry : restock.entrySet()) {
			  String key = entry.getKey();
			  Integer value = entry.getValue();
			  
			  inventory.put(key, inventory.get(key)+value);
			  
			  log(key + " => " + value + "added to cook inventory");
			}
		synchronized(orders){
			for(Order o:orders){
				if(o.s==OrderState.marketOrdering){
					o.s=OrderState.pending;
					stateChanged();
				}
			}
			
		}
	}

	
	// Scheduler
	
	public boolean pickAndExecuteAnAction(){
		if(onDuty){
			cookGui.DoEnterRestaurant();
		}
		if(checkingStand) {
			checkStand();
			return true;
		}
		
		
		if(!ordersTaken.isEmpty()){
			for(Integer i:ordersTaken){
				//cookGui.unDraw(i);
				ordersTaken.remove(i);
				return true;
			}
			
		}
		if(!onDuty && orders.isEmpty()){
			leaveRestaurant();
			return false;
		}
		synchronized(orders){
			for(Order o:orders){
				//print("entered for loop");
				if(o.s==OrderState.pending){	
					cook(o);
					return true;
				}
			}
		}
		
		synchronized(orders){
			for(Order o:orders){
				if(o.s==OrderState.Done){
					print(" order done");
					o.s=OrderState.Delivered;
					tellWaiterAboutFood(o);
					
					return true;
				}
			}		
		}
		
		return false;
		
	}
	

	
	
	
	
	// Actions
	
	private void leaveRestaurant() {
		cookGui.DoLeaveRestaurant();
		
	}

	private void checkStand() {
		checkingStand=false;
		
		log("Checking the stand for orders");
		
		
		if(revolvingstand.isEmpty()) {
			log("No orders here...");
			return;
		}

		while(!revolvingstand.isEmpty()) {
			OrderTicket temp = revolvingstand.remove();
			orders.add(new Order(temp.getTable(), temp.getChoice(), temp.getWaiter()));
			stateChanged();
		}
	}
	private void cook( Order o){
		cookGui.DoEnterRestaurant();
		String order=o.Choice;
		
		if(inventory.get(order)==0){
			log("Out of " + order);
			o.s=OrderState.marketOrdering;
			//o.w.msgOutOfChoice(o.tableNumber);
			log("ordering stock");
			orderStock(o, 2);
			return;
		}
		
		
		
		log("Cooking");
		//set order state to cooking
		//use timer stuffs
		//set status = done
		o.s=OrderState.cooking;
		//cookGui.drawFood(o.tableNumber);
		TimerTask t = new TimerTask() 
		{
			Order order; 
			public void run() {
				print(" done cooking");
				makeOrderDone(this.order);
				
				stateChanged();
			}
			public TimerTask init(Order o)
			{
				this.order = o;
				return this; 
			}
		}.init(o);
		String choice= o.Choice;
		Do("choice is " + choice);
		
		
		timer.schedule(t, 1000*cookingTimes.get(o.Choice));
		int current =inventory.get(order);
		inventory.put(order,current-1);
		return;
	}
	
	private void orderStock(Order o, int i) {
		log("ordering stock");
		
		marketOrder = new TreeMap<String, Integer>();
		
		if(inventory.get("steak")<2){
			marketOrder.put("steak", 5);
		}
		
		if(inventory.get("chicken")<2){
			marketOrder.put("chicken", 5);
		}
		
		if(inventory.get("pizza")<2){
			marketOrder.put("pizza", 5);
		}
		if(inventory.get("salad")<2){
			marketOrder.put("salad", 5);
		}
		
		ContactList.getInstance().marketGreeter.msgINeedInventory("jeffersonrestaurant",marketOrder);
		// TODO Insert messaging to Ellen's Market Here
	
		
		
		
	/*
	 * To TAs and Graders. I sincerely apologize for using this switch-case and recognize that there is a better way to do this
	 * 	
	 
		
		switch (o.Choice) {
		case "steak":
			if(steakmarketcounter>=4){
				o.w.msgOutOfChoice(o.tableNumber);
				Do("markets out of " + o.Choice);
				return;
			}
			Do("messaging market");
			markets.get(steakmarketcounter).msgNeedStock("steak", i);
			break;
			
		case "chicken":
			if(chickenmarketcounter>=4){
				o.w.msgOutOfChoice(o.tableNumber);
				Do("markets out of " + o.Choice);
				return;
			}
			markets.get(chickenmarketcounter).msgNeedStock("chicken", i);
			break;
			
		case "salad":
			if(saladmarketcounter>=4){
				o.w.msgOutOfChoice(o.tableNumber);
				Do("markets out of " + o.Choice);
				return;
			}
			markets.get(saladmarketcounter).msgNeedStock("salad", i);
			break;
			
		case "pizza":
			if(pizzamarketcounter>=4){
				o.w.msgOutOfChoice(o.tableNumber);
				Do("markets out of " + o.Choice);
				return;
			}
			markets.get(pizzamarketcounter).msgNeedStock("pizza", i);
			break;

		default:
			break;
		}
		
	*/	
	}

	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.JEFFERSON_RESTAURANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.JEFFERSON_COOK, this.getName(), s);
	}


	private void makeOrderDone(Order o){
		o.s=OrderState.Done;
		//cookGui.moveFood(o.tableNumber);
	}
	
	private void tellWaiterAboutFood(Order o){
		o.w.msgOrderIsReady(o.tableNumber);
		
	}

	

	




	
	
	
}
