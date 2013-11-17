package enaRestaurant;

import agent.Agent;
import enaRestaurant.CookRole.Order;
import enaRestaurant.CookRole.OrderStatus;
import enaRestaurant.HostRole.Table;
import enaRestaurant.gui.HostGui;
import enaRestaurant.interfaces.Market;
import enaRestaurant.CookRole.Food;
import enaRestaurant.CustomerRole.AgentEvent;

import java.util.*;
//import java.util.concurrent.Semaphore;

public class MarketRole extends Agent implements Market {
	Timer timer = new Timer();

	public Map<String, Integer> Stock = new HashMap<String, Integer>();
	public List<Request> ShoppingList = Collections.synchronizedList(new ArrayList<Request>());
	
	private String name;
		public CookRole cook;
		public CashierRole cashier;
	public enum ReStockStatus 
	{pending, recieving, reStocking, orderDone};
	ReStockStatus status = ReStockStatus.pending;

	public MarketRole(String name) {
		super();

		this.name = name;
		Stock.put("salad", 2);
		Stock.put("chicken", 2);
		Stock.put("pizza" , 2);
		Stock.put("steak", 2);
		
		
	}

	public String getName()
	{
		return name;
	}


	// Messages

	public void msgOrderRestock(CookRole ck, Map<String, Integer> stock)
	{
		cook = ck;
		print("messaging the market");
		ShoppingList.add(new Request(stock));
		status = status.recieving;
		stateChanged();
	}
	
	public void msgPaidMarketBill(double checks)
	{
		print("The cashier has paid the bill of $ " +checks);
	}
	public void msgRestCantPay()
	{
		print("We will still deliver the food, but restaurant will have to privide free dinner vouchers for a week!");
	}


	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		//this.print(""+this.tables.size());
synchronized(ShoppingList)
{
	for(Request request: ShoppingList)
	{
		
		if(status == ReStockStatus.recieving)
		{
			ReSupply(request);
			status = ReStockStatus.orderDone;
			return true;
		}
	}	
		return false;
}	
	}

	// Actions

	private void processTime()
	{
		timer.schedule(new TimerTask() 
		{
		public void run() 
		{
			print("Market fulfilling order...");
			status = ReStockStatus.reStocking;
			stateChanged();		
			//cook.msgDeliverRestock(Reorder,  invoice);

		}
	},
	15000);
		
	}
	private void ReSupply(Request r) 
	{
		processTime();
		boolean invoice = true;
		print("Market is resupplying the food to the cook");
		 final Map<String, Integer> Reorder = new HashMap<String, Integer>();
			for(String f: r.order.keySet())
			{
				if(Stock.get(f) !=null)
				{
					if(r.order.get(f) <= Stock.get(f))
					{
						invoice = true;
						Reorder.put(f, r.order.get(f));
						int fd = Stock.get(f) - r.order.get(f);	
						Stock.put(f, fd);
				   }
					else if (r.order.get(f) > Stock.get(f))
					{
						invoice = false;
						Reorder.put(f,  Stock.get(f));
						Stock.put(f, 0);
					}
			    }	
			}
		cook.msgDeliverRestock(Reorder,  invoice);
		int marketCharge=0;
		for(String f: Reorder.keySet())
		{
			//int marketCharge=0;
			if(f.equals("steak"))
			{
				marketCharge += Reorder.get(f) * 5; 
			}
			if(f.equals("salad"))
			{
				marketCharge += Reorder.get(f) * 1; 

			}
			if(f.equals("chicken"))
			{
				marketCharge += Reorder.get(f) * 4; 

			}
			if(f.equals("pizza"))
			{
				marketCharge += Reorder.get(f) * 2; 

			}
			
		}
		print("Market is charging the restaurant $: " +marketCharge);
					cashier.msgRestockBill(marketCharge, this);
		
	}
	

	public void setCookMarket(CookRole cook)
	{
		this.cook = cook;
	}
	
	public void setCashierRole(CashierRole cashier)
	{
		this.cashier = cashier;
	}
	//utilities

	
	
	
	////HELPER CLASSES/////
	
	
	public class Request
	{
		CookRole c;
		Map<String, Integer> order;
		
		Request(Map<String, Integer> ord)
		{
			this.order = ord;
		}
		
	}

	
}

