package mainCity.restaurants.jeffersonrestaurant.test.mock;
import role.jeffersonRestaurant.JeffersonCashierRole;
import role.jeffersonRestaurant.JeffersonCookRole;
import role.jeffersonRestaurant.JeffersonHostRole;
import role.jeffersonRestaurant.JeffersonWaiterRole;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Cashier;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Customer;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Market;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Waiter;
import mainCity.restaurants.jeffersonrestaurant.test.mock.EventLog;


public class MockMarket extends Mock implements Market {

	public MockMarket(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	public JeffersonCashierRole cashier;
	public JeffersonCookRole cook;
	public EventLog log;
	
	
	public void msgNeedStock(String item, Integer quantity) {
		log.add(new LoggedEvent("Messaged by cook requesting stock"));
		
	}


	public void msgHereIsMonies(double m) {
		log.add(new LoggedEvent("Recieved money from cashier"));
		
	}
	

}
