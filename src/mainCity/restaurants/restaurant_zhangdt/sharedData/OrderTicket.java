package mainCity.restaurants.restaurant_zhangdt.sharedData;

import role.davidRestaurant.DavidHostRole.Table;
import role.davidRestaurant.DavidWaiterRole;
import mainCity.restaurants.restaurant_zhangdt.interfaces.Waiter;

public class OrderTicket {
	DavidWaiterRole waiter; 
	String choice;  
	Table table; 
	
	public OrderTicket(DavidWaiterRole w, String c, Table t) { 
		waiter = w; 
		choice = c; 
		table = t; 
	}
	
	public DavidWaiterRole getWaiter() { 
		return waiter; 
	}
	
	public String getChoice() { 
		return choice; 
	}
	public Table getTable() { 
		return table;
	}
}
