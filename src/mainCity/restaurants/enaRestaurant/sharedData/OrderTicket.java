package mainCity.restaurants.enaRestaurant.sharedData;
import mainCity.restaurants.enaRestaurant.EnaHostRole.Table;
import mainCity.restaurants.enaRestaurant.interfaces.Waiter;

public class OrderTicket {
	Waiter waiter;
	String choice;
	Table table;
	
	public OrderTicket(Waiter w, String c, Table t) {
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
	
	public Table getTable() {
		return table;
	}
}