
package mainCity.restaurants.enaRestaurant;

import agent.Agent;
import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.MainCook;
import mainCity.restaurants.enaRestaurant.EnaHostRole.Table;

import java.util.*;

import role.Role;
import mainCity.restaurants.enaRestaurant.gui.EnaCookGui;
import mainCity.restaurants.enaRestaurant.gui.EnaHostGui;
import mainCity.restaurants.enaRestaurant.interfaces.Waiter;
import mainCity.restaurants.enaRestaurant.sharedData.OrderTicket;
import mainCity.restaurants.enaRestaurant.sharedData.RevolvingStand;

/**
 * Restaurant Cook Agent
 */

public class EnaCookRole extends Role implements MainCook {
	Timer timer = new Timer();
	public List<Order> Orders= Collections.synchronizedList(new ArrayList<Order>());
	public Map<String, Food> Foods = new HashMap<String, Food>();
	//public List<Food> Foods = new ArrayList<Food>();
	private String name;
	private boolean fullOrder;
	private RevolvingStand stand;

	private boolean inventoryChecked = false;
	public enum OrderStatus 
	{pending, cooking, waiting, cooked, restocking, orderDone};
	
	public enum CookStatus {none,checkingStand};
	private CookStatus status;
	//OrderStatus status = OrderStatus.pending;
	
			public EnaHostGui hostGui;
			public EnaCookGui cookGui;
			public EnaCashierRole cashier;

	public EnaCookRole( PersonAgent p, String name) {
		super(p);
		
		this.name = name;
		Foods.put( "steak", new Food("steak", 1));
		Foods.put("porkchops", new Food("porkchops", 1));
		Foods.put("lamb" , new Food("lamb", 1));
		Foods.put("lambchops", new Food("lambchops", 1));
		
		//super().msgGoToWork();

		
	}


	public String getName()
	{
		return name;
	}
	public void setStand(RevolvingStand s) {
		this.stand = s;
	}
	
	public void depleteInventory(){
		for (Map.Entry<String, Food> entry : Foods.entrySet()){
			Food f = entry.getValue();
			f.setAmount(0);
		}
		checkInventory();
	}

	//for alert log trace statements
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.ENA_RESTAURANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.ENA_COOK, this.getName(), s);
	}

	// Messages

	public void msgHereIsTheOrder(EnaWaiterRole w, String choice, Table table)
	{
		Orders.add(new Order(w,choice,table, OrderStatus.pending));
		log("ORDER ADDED TO LIST OF TYPE:  " +choice); 
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
				log("now has more" +f);
			}
		}
		log("the cook has replenished its inventory");
		stateChanged();
	}
	
	public void msgHereIsYourOrder(Map<String, Integer> inventory)
	{
		inventoryChecked = true;
		//fullOrder = fullInvoice;
		for (String f : Foods.keySet())
		{
			Foods.get(f).setAmount(Foods.get(f).getAmount()+inventory.get(f));
			log("now has more" +f);
		}
	
			/*if(newInventory.get(f) != null)
			{
				Foods.get(f).setAmount(Foods.get(f).getAmount()+newInventory.get(f));
				log("now has more" +f);
			}
		}*/
		log("the cook has replenished its inventory");
		stateChanged();
	}
	
	public void msgCheckStand() {
		if(status == CookStatus.none) {
			status = CookStatus.checkingStand;
			stateChanged();
		}
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() 
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
				log("order status is LOOKING TO cooking");
				//DoCooking(order);
				CookFood(order);
				order.oStat = OrderStatus.waiting;
				return true;
			}
			if(order.oStat == OrderStatus.cooked)
			{
				log("Let the waiter know the cook is finished cooking");
				order.w.msgOrderReady(order.choice, order.table);
				order.oStat = OrderStatus.orderDone;
				
				return true;
			}
		}
}
if(status == CookStatus.checkingStand) 
{
	checkStand();
	return true;
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
			log("this option is out of stock, please select another option");
			Orders.remove(o);
			o.w.msgOutofFood(o.choice);
			checkInventory();
		}
		else 
		{
			cookGui.Cooking(o.choice);
			log("food being cooked");
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
		log("The cook's inventory is being checked");
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
		
		if(ContactList.getInstance().marketGreeter != null)
		 { 
			ContactList.getInstance().marketGreeter.msgINeedInventory("enaRestaurant", Stock);
		 }
			

		
	}
	
	private void checkStand() {
		status = CookStatus.none;		
		//cookGui.DoGoTo();
		
		if(stand.isEmpty()) {
			return;
		}
		
		log("There's orders on the stand. Processing...");
		while(!stand.isEmpty()) {
			OrderTicket temp = stand.remove();
			Orders.add(new Order(temp.getWaiter(), temp.getChoice(), temp.getTable(), OrderStatus.pending));
			stateChanged();
		}
	}
	//utilities

	public void setGui(EnaCookGui gui) 
	{
		cookGui = gui;
	}

	public EnaHostGui getGui() {
		return hostGui;
	}
	
	public void setCashier(EnaCashierRole cshr)
	{
		this.cashier = cshr;
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
		Waiter w;
		String choice;
		Table table;
		OrderStatus oStat;
		
		Order(Waiter wtr, String ch, Table t, OrderStatus ost)
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
			if (ch == "porkchops")
			{
				time = 8000;
			}
			if(ch == "lamb")
			{
				time= 5000;
			}
			if(ch == "lambchops")
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

