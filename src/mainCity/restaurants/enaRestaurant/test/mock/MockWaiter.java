package mainCity.restaurants.enaRestaurant.test.mock;


//import restaurant.WaiterAgent.AgentEvent;
import role.enaRestaurant.EnaCustomerRole;
import role.enaRestaurant.EnaHostRole.Table;
import role.enaRestaurant.EnaWaiterRole.MyCustomers;
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
	@Override
	public void msgOutofFood(String choice) {
		log.add(new LoggedEvent("message that market is out of food"));
		
	}
	@Override
	public void msgOrderReady(String choice, Table table) {
		log.add(new LoggedEvent("message that order is ready"));
		
	}
	@Override
	public void msgReadyToOrder(EnaCustomerRole enaCustomerRole) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgHereIsMyChoice(String choice, EnaCustomerRole enaCustomerRole) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgDoneEating(EnaCustomerRole enaCustomerRole) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgCheckPlease(EnaCustomerRole enaCustomerRole, String choice) {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
