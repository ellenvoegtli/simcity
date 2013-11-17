package mainCity.restaurants.marcusRestaurant.sharedData;
import mainCity.restaurants.marcusRestaurant.MarcusTable;
import mainCity.restaurants.marcusRestaurant.interfaces.Waiter;

public class OrderTicket {
	Waiter waiter;
	String choice;
	MarcusTable table;
	
	public OrderTicket(Waiter w, String c, MarcusTable t) {
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
	
	public MarcusTable getTable() {
		return table;
	}
}