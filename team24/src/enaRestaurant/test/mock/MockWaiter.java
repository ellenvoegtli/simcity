package enaRestaurant.test.mock;


//import restaurant.WaiterAgent.AgentEvent;
import enaRestaurant.CustomerRole;
import enaRestaurant.WaiterRole.MyCustomers;
//import restaurant.WaiterAgent.custState;
import enaRestaurant.interfaces.Cashier;
import enaRestaurant.interfaces.Waiter;
import enaRestaurant.interfaces.Customer;


public class MockWaiter extends Mock implements Waiter {
	
	public Cashier cashier;
	public MockCustomer customer;
	public EventLog log = new EventLog();
	//public double cash;
	public MockWaiter(String name)
	{
		super(name);
	}
	@Override
	public void msgHereIsBill(double check, Customer c)
	{
		log.add(new LoggedEvent("message recieved to give the check to customer. Check is: $ " +check));
	}
	
	
	

}
