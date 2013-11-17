package enaRestaurant.test.mock;


import enaRestaurant.CustomerRole.AgentEvent;
import enaRestaurant.WaiterRole;
import enaRestaurant.interfaces.Cashier;
import enaRestaurant.interfaces.Customer;
import enaRestaurant.interfaces.Waiter;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends Mock implements Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public MockWaiter waiter;
	
	public double cash;
	public double debt = 0;
	public EventLog log = new EventLog();
	public MockCustomer(String name) 
	{
		super(name);
		if(name.equals("poor"));
		{
			cash = 5.99;
		}
		if(name.equals("debt"))
		{
			cash = 3.0;
		}

	}
	@Override
	public void msgHereIsYourChange(double change)
	{	
		//cash= change;
		log.add(new LoggedEvent("Recieved change from cashier"));
	}

	public Waiter getWaiter() 
	{
		return waiter;
	}
	@Override
	public double getDebt() 
	{
		return debt;
	}
	@Override
	public double getCash() 
	{
		return cash;
	}
	@Override
	public void setDebt(double i) 
	{
		debt = i;
	}
	

}
