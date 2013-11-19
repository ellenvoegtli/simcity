package mainCity.restaurants.marcusRestaurant;

import agent.Agent;
import mainCity.restaurants.marcusRestaurant.sharedData.*;
import mainCity.restaurants.marcusRestaurant.gui.CookGui;
import mainCity.restaurants.marcusRestaurant.interfaces.*;

import java.util.*;

/**
 * Restaurant Cook Agent
 */

public class MarcusCookRole extends Agent implements Cook {
	private CookGui cookGui;
	private List<Market> markets;
	private int tracker, selector;
	private List<Order> orders;
	private Map<String, Food> foods;
	private RevolvingStand stand;

	Timer timer = new Timer();
	public enum CookStatus {normal, checkingStand, lowFood, checkingFulfillment};
	private CookStatus status;
	private String order;
	private int grill;

	public MarcusCookRole() {
		super();
		orders = Collections.synchronizedList(new ArrayList<Order>());
		markets = Collections.synchronizedList(new ArrayList<Market>());
		status = CookStatus.normal;
		
		foods = Collections.synchronizedMap(new HashMap<String, Food>());
		
		synchronized(foods) {
			foods.put("Steak", new Food("Steak", 7500, 2, 4, 15));//Name, CookTime, Quantity, Threshold, Capacity
			foods.put("Chicken", new Food("Chicken", 5500, 5, 2, 15));
			foods.put("Salad", new Food("Salad", 3500, 0, 2, 15));
			foods.put("Pizza", new Food("Pizza", 6000, 2, 2, 15));
		}
		
		tracker = 0;
		selector = 0;
		grill = 0;
	}

	public void setGui(CookGui g) {
		cookGui = g;
		cookGui.setPresent(true);
	}
	
	public void setStand(RevolvingStand s) {
		this.stand = s;
	}
	
	public List getOrders() {
		return orders;
	}

	public void addMarket(Market m) {
		markets.add(m);
	}
	// Messages

	public void msgHereIsAnOrder(Waiter w, String choice, MarcusTable t) {
		print("msgHereIsAnOrder: received an order of "+ choice + " for table " + t.getTableNumber());
		orders.add(new Order(w, choice, t.getTableNumber()));
		cookGui.DoGoToCounter();
		stateChanged();
	}
	
	public void msgOrderFulfillment(String choice, int q) {
		print("Received " + q + " of " + choice);
		
		synchronized(foods) {
			foods.get(choice).addQuantity(q);
		}
		
		status = CookStatus.checkingFulfillment;
		stateChanged();
	}

	public void msgCheckStand() {
		if(status == CookStatus.normal) {
			status = CookStatus.checkingStand;
			stateChanged();
		}
	}
	
	public void msgHereIsYourOrder(HashMap<String, Integer> inventory) {
		//stuff for market
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {		
		synchronized(markets) {
			if(status == CookStatus.lowFood) {
				if(tracker < markets.size()) {
					orderFromMarket();
				}
				else {
					tracker = 0;
					status = CookStatus.normal;
				}
				return true;
			}
		}
		
		if(status == CookStatus.checkingFulfillment) {
			checkFulfillment();
			return true;
		}
		
		synchronized(orders) {
			if(!orders.isEmpty()) {
				for(Order o : orders) {
					if(o.status == OrderStatus.cooked) {
						callWaiter(o);
						return true;
					}
					else if(o.status == OrderStatus.pending) {
						o.status = OrderStatus.cooking;
						cookOrder(o);
						return true;
					}
				}
			}
		}
		
		if(status == CookStatus.checkingStand) {
			checkStand();
			return true;
		}

		return false;
	}

	//Actions
	private void cookOrder(Order o) {
		synchronized(foods) {
			Food f = foods.get(o.choice);
		
			if(f.amount == 0) {
				print("Looks like we're all out of " + o.choice + ", Telling waiter " + o.waiter);
				o.waiter.msgOutOfFood(o.table, o.choice);
				order = o.choice;
				status = CookStatus.lowFood;
				orders.remove(o);
				return;
			}
			
			f.amount--;
			
			if(f.amount < f.threshold) {
				print("I'm running low on " + o.choice);
				order = o.choice;
				status = CookStatus.lowFood;
			}
			
			print("Cooking order: " + o.choice + " with " + f.amount + " left");
			o.grill = (++grill) % 4;
			cookGui.DoGoToGrill(o.grill);
			timer.schedule(new CookTimer(o), foods.get(o.choice).cookTime);
		}
	}
	
	private void callWaiter(Order o) {
		print("Calling back waiter for table " + o.table);
		o.waiter.msgOrderIsReady(o.table, o.choice);
		orders.remove(o);
	}
	
	private void checkFulfillment() {
		synchronized(foods) {
			Food f = foods.get(order);
	
			if(f.amount < f.threshold) {
				status = CookStatus.lowFood;
				selector = (selector+1) % markets.size();
				tracker++;
	
				stateChanged();
				return;
			}

			status = CookStatus.normal;
			tracker = 0;
		}
	}
	
	private void orderFromMarket() {
		print("Sending an order to Market #" + selector);
		
		synchronized(foods) {
			Food f = foods.get(order);
			markets.get(selector).msgRequestForFood(this, order, (f.capacity - f.amount));
			//msgINeedInventory(String name, MainCook (self), MainCashier cashier, HashMap<String, Integer> inventory)
			status = CookStatus.normal;
		}
	}
	
	private void checkStand() {
		status = CookStatus.normal;
		
		print("Checking the stand for orders");
		cookGui.DoGoToCounter();
		
		if(stand.isEmpty()) {
			print("No orders here...");
			return;
		}

		while(!stand.isEmpty()) {
			OrderTicket temp = stand.remove();
			orders.add(new Order(temp.getWaiter(), temp.getChoice(), temp.getTable().tableNumber));
			stateChanged();
		}
	}
	
	public String toString() {
		return "Cook";
	}
	
	public enum OrderStatus {pending, cooking, cooked};
	class Order {
		Waiter waiter;
		String choice;
		int table;
		OrderStatus status;
		int grill;

		Order(Waiter w, String c, int t) {
			waiter = w;
			choice = c;
			table = t;
			status = OrderStatus.pending;
		}
	}
	
	class Food {
		String name;
		int cookTime;
		int amount;
		final int threshold;
		final int capacity;
		
		Food(String n, int time, int a, int thresh, int cap) {
			this.name = n;
			this.cookTime = time;
			this.amount = a;
			this.threshold = thresh;
			this.capacity = cap;
		}
		
		void addQuantity(int q) {
			amount += q;
		}
	}
	
	class CookTimer extends TimerTask {
		Order timedOrder;
		
		public CookTimer(Order o) {
			timedOrder = o;
		}
		
		public void run() {
			System.out.println(timedOrder.choice + " is ready!");
			timedOrder.status = OrderStatus.cooked;
			cookGui.DoClearGrill(timedOrder.grill);
			cookGui.DoGoToCounter();
			stateChanged();
		}
	}
}
