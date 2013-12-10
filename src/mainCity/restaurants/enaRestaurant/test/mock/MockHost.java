package mainCity.restaurants.enaRestaurant.test.mock;

import java.util.Collection;

import mainCity.restaurants.enaRestaurant.EnaCustomerRole;
import mainCity.restaurants.enaRestaurant.EnaHostRole.Table;
import mainCity.restaurants.enaRestaurant.interfaces.Customer;
import mainCity.restaurants.enaRestaurant.interfaces.Host;
import mainCity.restaurants.enaRestaurant.test.mock.EventLog;
import mainCity.restaurants.enaRestaurant.test.mock.LoggedEvent;


public class MockHost extends Mock implements Host{

	EventLog log;
	public MockHost(String name) {
		super(name);
		log = new EventLog();

	}

	
	public void msgIWantToEat(EnaCustomerRole cust) {
		log.add(new LoggedEvent("Received a message to eat from hungry customer " + cust));		
		
	}

	
	public void msgWaiterArrived(EnaCustomerRole cust) {
		log.add(new LoggedEvent("Received a message that waiter arrived for customer  " + cust));		
		
	}

	
	public void msgWantToGoOnBreak() {
		log.add(new LoggedEvent("Received a message that waiter wants to go on break"));		
		
	}

	
	public void msgOffBreak() {
		log.add(new LoggedEvent("Received a message that waiter will come off break"));		
		
	}

	
	public void msgTableIsFree(Table t) {
		log.add(new LoggedEvent("Received a message that there is a free table "));		
		
	}


	@Override
	public Collection<Table> getTables() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void msgWaiterArrived(Customer cust) {
		// TODO Auto-generated method stub
		
	}
	
}
