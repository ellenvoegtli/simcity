package mainCity.restaurants.jeffersonrestaurant.sharedData;

import mainCity.restaurants.jeffersonrestaurant.interfaces.Waiter;



public class OrderTicket {
	Waiter waiter;
	String choice;
	int table;
	
	public OrderTicket(int t, String c, Waiter w) {
		waiter = w;
		choice = c;
		table = t;
	}
	
	public Waiter getWaiter() {
		return waiter;
	}
	
	public String getChoice() {
		return choice;
	}
	
	public int getTable() {
		return table;
	}

}
