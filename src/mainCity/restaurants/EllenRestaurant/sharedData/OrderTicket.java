package mainCity.restaurants.EllenRestaurant.sharedData;
import mainCity.restaurants.EllenRestaurant.interfaces.*;

public class OrderTicket {
	Waiter waiter;
	String choice;
	int table;
	
	public OrderTicket(Waiter w, String c, int tableNum) {
		waiter = w;
		choice = c;
		table = tableNum;
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