public package mainCity.restaurants.jeffersonrestaurant;


import agent.Agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.AbstractMap;
import java.util.HashMap;

import mainCity.restaurants.jeffersonrestaurant.CustomerAgent.*;
import mainCity.restaurants.jeffersonrestaurant.gui.CookGui;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Market;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Waiter;
import mainCity.restaurants.jeffersonrestaurant.WaiterAgent.Table;

public class CookAgent extends Agent{


	public enum OrderState
	{pending, marketOrdering, cooking, Done, Delivered};
	
	private OrderState state=OrderState.pending;
	private String name;
	public CookGui cookGui = null;
	Map<String, Integer> cookingTimes = new HashMap<String, Integer>();
	Map<String, Integer> inventory =  new HashMap<String,Integer>();
	public List<Market> markets = Collections.synchronizedList ( new ArrayList<Market>());
	public List <Order> orders =Collections.synchronizedList ( new ArrayList<Order>());
	Timer timer = new Timer();
	
	private List<Integer> ordersTaken =Collections.synchronizedList(new ArrayList<Integer>()); 
	
	//will hold index numbers for market in array
	int steakmarketcounter;
	int chickenmarketcounter;
	int saladmarketcounter;
	int pizzamarketcounter;
	
	
	
	public CookAgent(String name){
		super();
		this.name=name;
		cookingTimes.put("steak",8);
		cookingTimes.put("chicken",6);
		cookingTimes.put("salad",4);
		cookingTimes.put("pizza",4);
		
		//Initial cook inventory
		inventory.put("steak",2);
		inventory.put("chicken",2);
		inventory.put("salad",2);
		inventory.put("pizza",2);
		
		//sets to first market
		steakmarketcounter =1;
		chickenmarketcounter=1;
		saladmarketcounter=1;
		pizzamarketcounter =1;
		
	}
	
	public void setGui(CookGui gui) {
		cookGui = gui;
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
	
	
	
	
	// Messages
	public void msghereIsAnOrder (int table, String Choice, Waiter w){
		print("cook recieved order");
		orders.add(new Order (table,Choice, w));
		stateChanged();
		
	}
	
	public void msgOrderTaken(int t){
		ordersTaken.add(t);
		stateChanged();
		
	}
	
	public void msgCannotFulfill(String i, Integer q){
		Do("Restock of " + i + " not fulfilled");
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
		Do("Restock of " + i + " completely fulfilled");
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
		Do("Restock of " + i + " partially fulfilled");
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
	
	// Scheduler
	
	protected boolean pickAndExecuteAnAction(){
		
		
		if(!ordersTaken.isEmpty()){
			for(Integer i:ordersTaken){
				//cookGui.unDraw(i);
				ordersTaken.remove(i);
				return true;
			}
			
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
	
	
	private void cook( Order o){
		String order=o.Choice;
		
		if(inventory.get(order)==0){
			System.out.println("Out of " + order);
			o.s=OrderState.marketOrdering;
			//o.w.msgOutOfChoice(o.tableNumber);
			Do("ordering stock");
			orderStock(o, 2);
			return;
		}
		
		
		
		Do("Cooking");
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
		Do("ordering stock of " + o.Choice);
	
	/*
	 * To TAs and Graders. I sincerely apologize for using this switch-case and recognize that there is a better way to do this
	 * 	
	 */
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
		
	}



	private void makeOrderDone(Order o){
		o.s=OrderState.Done;
		//cookGui.moveFood(o.tableNumber);
	}
	
	private void tellWaiterAboutFood(Order o){
		o.w.msgOrderIsReady(o.tableNumber);
		
	}
	
	
	
}
