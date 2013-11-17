package mainCity.restaurants.marcusRestaurant;

import agent.Agent;

import java.util.*;

import mainCity.restaurants.marcusRestaurant.interfaces.Market;
import mainCity.restaurants.marcusRestaurant.interfaces.Cashier;

/**
 * Restaurant Cook Agent
 */

public class MarcusMarketRole extends Agent implements Market {
	private int number;
	private MarcusCashierRole cashier;
	private List<Order> orders;
	private Map<String, Food> foods;
	Timer timer = new Timer();

	public MarcusMarketRole(int i) {
		super();
		this.number = i;
		orders = Collections.synchronizedList(new ArrayList<Order>());
		foods = Collections.synchronizedMap(new HashMap<String, Food>());

		synchronized(foods) {
			foods.put("Steak", new Food("Steak", (int) (Math.random() * 10), 6));
			foods.put("Chicken", new Food("Chicken", (int) (Math.random() * 10), 4));
			foods.put("Salad", new Food("Salad", (int) (Math.random() * 10), 3));
			foods.put("Pizza", new Food("Pizza", (int) (Math.random() * 10), 4));
		}
	}

	public void setCashier(MarcusCashierRole c) {
		cashier = c;
	}
	
	// Messages
	public void msgRequestForFood(MarcusCookRole c, String choice, int quantity) {
		orders.add(new Order(c, choice, quantity));
		stateChanged();
	}
	
	public void msgHereIsPayment(Cashier c, int amount) {
		print("Received payment of $" + amount + " from " + c);
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		synchronized(orders) {
			if(!orders.isEmpty()) {
				for(Order o : orders) {
					handleOrder(o);
					return true;
				}
			}
		}

		return false;
	}

	//Actions
	private void handleOrder(Order o) {
		print("Handling order of " + o.choice);
		synchronized(foods) {
			Food f = foods.get(o.choice);
			
			if(o.requestedAmount > f.quantity) {
				print("Looks like we don't have enough to fulfill the order, sending " + f.quantity);
				print("Also billing cashier...");
				cashier.msgHereIsFoodBill(this, f.quantity*f.cost);
				o.cook.msgOrderFulfillment(o.choice, f.quantity);
				orders.remove(o);
				f.quantity = 0;
				return;
			}
			
			timer.schedule(new MarketTimer(this, f, o, o.requestedAmount), (30000 + ((int) Math.random()*10)*1000));
			orders.remove(o);
		}
	}

	public String toString() {
		return "Market #" + number;
	}
	
	class Order {
		MarcusCookRole cook;
		String choice;
		int requestedAmount;

		Order(MarcusCookRole c, String r, int t) {
			cook = c;
			choice = r;
			requestedAmount = t;
		}
	}
	
	class Food {
		String name;
		int quantity;
		int cost;
		
		Food(String n, int q, int c) {
			this.name = n;
			this.quantity = q;
			this.cost = c;
		}
	}
	
	class MarketTimer extends TimerTask {
		MarcusMarketRole market;
		Food food;
		Order timedOrder;
		int amount;
		
		public MarketTimer(MarcusMarketRole m, Food f, Order o, int a) {
			food = f;
			timedOrder = o;
			amount = a;
			market = m;
		}
		
		public void run() {
			food.quantity -= amount;
			print("Sending " + amount + " over to cook. We now have " + food.quantity + " left\nAlso sending bill to cashier");
			cashier.msgHereIsFoodBill(market, food.cost*amount);
			timedOrder.cook.msgOrderFulfillment(timedOrder.choice, amount);			
		}
	}
}

