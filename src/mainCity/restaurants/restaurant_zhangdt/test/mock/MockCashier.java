package mainCity.restaurants.restaurant_zhangdt.test.mock;

import mainCity.restaurants.restaurant_zhangdt.interfaces.Cashier;
import mainCity.restaurants.restaurant_zhangdt.interfaces.Customer;
import mainCity.restaurants.restaurant_zhangdt.interfaces.Waiter;

public class MockCashier extends Mock implements Cashier{

	public EventLog log = new EventLog();

	public MockCashier(String name) {
		super(name);

	}
	
	public void msgHeresACheck(Waiter w, String order, int tableNumber) { 
		//log.add(new LoggedEvent("Received Check from waiter. order = " + order + "table number = " + tableNumber));
		log.add(new LoggedEvent("Received check from waiter."));
	}

	public void msgHeresMyPayment(Customer c, double custPayment, int tableNumber) {
		log.add(new LoggedEvent("Received Payment from customer. Payment = "+ custPayment));
	}
}
