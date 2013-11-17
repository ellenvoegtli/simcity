package mainCity.restaurants.enaRestaurant.test.mock;


//import restaurant.WaiterAgent.AgentEvent;
import mainCity.restaurants.enaRestaurant.CustomerRole;
import mainCity.restaurants.enaRestaurant.WaiterRole.MyCustomers;
//import restaurant.WaiterAgent.custState;
import mainCity.restaurants.enaRestaurant.interfaces.Cashier;
import mainCity.restaurants.enaRestaurant.interfaces.Waiter;
import mainCity.restaurants.enaRestaurant.interfaces.Customer;


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
