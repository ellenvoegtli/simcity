package mainCity.restaurants.restaurant_zhangdt.test.mock;

import mainCity.restaurants.restaurant_zhangdt.interfaces.Market;

public class MockMarket extends Mock implements Market{

	public EventLog log = new EventLog(); 
	
	public MockMarket(String name) {
		super(name);
	}

	@Override
	public void msgPaymentFromCashier(boolean Paid) {
		log.add(new LoggedEvent("Recieved msgPaymentFromCashier from cashier."));
	} 

}
