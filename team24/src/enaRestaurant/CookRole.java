
package enaRestaurant;

import agent.Agent;
import enaRestaurant.HostRole.Table;

import java.util.*;
//import java.util.concurrent.Semaphore;

import enaRestaurant.gui.CookGui;
import enaRestaurant.gui.HostGui;

/**
 * Restaurant Cook Agent
 */

public class CookRole extends Agent {
	Timer timer = new Timer();

	public List<MarketRole> Bazaar = Collections.synchronizedList(new ArrayList<MarketRole>());
	public List<Order> Orders= Collections.synchronizedList(new ArrayList<Order>());
	public Map<String, Food> Foods = new HashMap<String, Food>();
	//public List<Food> Foods = new ArrayList<Food>();
	private int marketCount = 1;
	private String name;
	private boolean fullOrder;
	private boolean inventoryChecked = false;
	public enum OrderStatus 
	{pending, cooking, waiting, cooked, restocking, orderDone};
	//OrderStatus status = OrderStatus.pending;
	
			public HostGui hostGui;
			public CookGui cookGui;
			public MarketRole market;

	public CookRole(String name) {
		super();

		this.name = name;
		Foods.put( "steak", new Food("steak", 0));
		Foods.put("chicken", new Food("chicken", 0));
		Foods.put("salad" , new Food("salad", 0));
		Foods.put("pizza", new Food("pizza", 0));

		
	}
	
	public void addMarkets(MarketRole market)
	{
		Bazaar.add(market);
	}

	public String getName()
	{
		return name;
	}

	// Messages

	public void msgHereIsTheOrder(WaiterRole w, String choice, Table table)
	{
		Orders.add(new Order(w,choice,table, OrderStatus.pending));
		print("ORDER ADDED TO LIST OF TYPE:  " +choice); 
		stateChanged();
	}

	public void msgDeliverRestock( Map<String, Integer> newInventory, boolean fullInvoice)
	{
		inventoryChecked = true;
		fullOrder = fullInvoice;
		for (String f : Foods.keySet())
		{
			if(newInventory.get(f) != null)
			{
				Foods.get(f).setAmount(Foods.get(f).getAmount()+newInventory.get(f));
				print("now has more" +f);
			}
		}
		print("the cook has replenished its inventory");
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() 
	{
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
synchronized(Orders)
{
		for (Order order : Orders) 
		{
			if (order.oStat == OrderStatus.pending) 
			{
					cookOrder(order);
					 //status = OrderStatus.cooked;
					return true;
					//return true to the abstract agent to reinvokes the scheduler.
			}
			if (order.oStat == OrderStatus.cooking)
			{
				System.out.println("order status is LOOKING TO cooking");
				//DoCooking(order);
				CookFood(order);
				order.oStat = OrderStatus.waiting;
				return true;
			}
			if(order.oStat == OrderStatus.cooked)
			{
				System.out.println("Let the waiter know the cook is finished cooking");
				order.w.msgOrderReady(order.choice, order.table);
				order.oStat = OrderStatus.orderDone;
				
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

	private void cookOrder(Order o)
	{	
		o.oStat = OrderStatus.cooking;
		
	}
	
	
	private void CookFood( final Order o) 
	{
		
		Food f = Foods.get(o.choice);
		if(f.amount == 0)
		{
			print("this option is out of stock, please select another option");
			Orders.remove(o);
			o.w.msgOutofFood(o.choice);
			checkInventory();
		}
		else 
		{
			cookGui.Cooking(o.choice);
			System.out.println("food being cooked");
			timer.schedule(new TimerTask() 
			{
						public void run() 
							{
							o.oStat = OrderStatus.cooked;							
							cookGui.Plating(o.choice);
							stateChanged();
							}
			},
				o.cookTime(o.choice));
			f.amount--;
			if(f.amount<=f.threshold)
			{
				checkInventory();
			}
		}
	}
	

	public void checkInventory()
	{
		print("The cook's inventory is being checked");
		Map<String, Integer> Stock = new HashMap<String, Integer>();
		for(Food f: Foods.values())
		{
			if(inventoryChecked)
			{
				if(f.getAmount() < f.capacity)
				{
					Stock.put(f.choice,  f.capacity - f.getAmount());
				}
			}
			else
			{
				if(f.getAmount() <= f.threshold)
				{
					Stock.put(f.choice, f.capacity-f.getAmount());
				}
			}
		}
		//Random rnd = new Random();
		//int mk = rnd.nextInt(3)+1;
		if(marketCount == 1)
			Bazaar.get(0).msgOrderRestock(this, Stock);
		if(marketCount == 2)
			Bazaar.get(1).msgOrderRestock(this, Stock);
		if(marketCount == 3)
			Bazaar.get(2).msgOrderRestock(this, Stock);

		print("Going to market" + marketCount);
		
		marketCount++;
		
		if(marketCount == 3)
			marketCount = 1;
		for(int i=0; i<Bazaar.size(); i++)
		{
			
		}
		
	}
	//utilities

	public void setGui(CookGui gui) 
	{
		cookGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}
	public void setMarket(MarketRole market)
	{
		this.market = market;
	}
	
	////HELPER CLASSES/////
	
	public class Food
	{
		//WaiterAgent w;
		String choice;
		int CookTime;
		int amount;
		int threshold = 1;
		int capacity = 5;
		Food(String ch, int amnt)
		{
			choice = ch;
			amount = amnt;
		
		}
		
		private int getAmount()
		{
			return amount;
		}
		private void setAmount(int newAmt)
		{
			amount = newAmt;
		}
		
	}

	public class Order 
	{
		WaiterRole w;
		String choice;
		Table table;
		OrderStatus oStat;
		
		Order(WaiterRole wtr, String ch, Table t, OrderStatus ost)
		{
			this.choice = ch;
			this.table = t;
			this.w = wtr;
			this.oStat = ost;
			
		}
		public int cookTime(String ch)
		{
			int time = 10;
			if (ch == "steak")
			{
				time = 10000;
			}
			if (ch == "chicken")
			{
				time = 8000;
			}
			if(ch == "salad")
			{
				time= 5000;
			}
			if(ch == "pizza")
			{
				time= 9000;
			}
			
			return time;
		}

		public String toString() 
		{
			return "order " + choice;
		}
		
		public int getTableNumber()
		{
			return table.getTableNumber();
		}
	}

	public void msgRemovePlating(String fdPlate)
	{
		cookGui.platingDone(fdPlate);
	}
}

