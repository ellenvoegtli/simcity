package mainCity.restaurants.restaurant_zhangdt.test.mock;

import mainCity.restaurants.restaurant_zhangdt.interfaces.Waiter;

public class MockWaiter extends Mock implements Waiter{
	
	public EventLog log = new EventLog(); 

	public MockWaiter(String name) {
		super(name);
	}

	@Override
	public void msgBringCheckToCustomer(double price, int tableNumber) {
		//log.add(new LoggedEvent("Received BringCheckToCustomer from cashier. price = "+ price + "table number = " + tableNumber));
		log.add(new LoggedEvent("Recieved BringCheckToCustomer from cashier."));
	}

}
