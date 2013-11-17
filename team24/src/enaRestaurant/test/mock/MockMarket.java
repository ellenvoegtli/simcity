package enaRestaurant.test.mock;

import enaRestaurant.interfaces.Cashier;
import enaRestaurant.interfaces.Customer;
import enaRestaurant.interfaces.Market;
import enaRestaurant.interfaces.Waiter;

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
