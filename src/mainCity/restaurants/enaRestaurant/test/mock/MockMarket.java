package mainCity.restaurants.enaRestaurant.test.mock;

import mainCity.restaurants.enaRestaurant.interfaces.Cashier;
import mainCity.restaurants.enaRestaurant.interfaces.Customer;
import mainCity.restaurants.enaRestaurant.interfaces.Market;
import mainCity.restaurants.enaRestaurant.interfaces.Waiter;

public class MockMarket extends Mock implements Market 
{
	
	public Cashier cashier;
	//public MockCustomer customer;
	public EventLog log = new EventLog();
	
	public MockMarket(String name)
	{
		super(name);
	}
	@Override
	public void msgPaidMarketBill(double check)
	{
		log.add(new LoggedEvent("message recieved that cashier paid bill"));
	}
	@Override
	public void msgRestCantPay() {
		log.add(new LoggedEvent("message recieved that restaurant cant pay bill"));
	}
	
	
	
}
