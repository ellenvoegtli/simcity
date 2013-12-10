package mainCity.restaurants.enaRestaurant.interfaces;

import java.util.List;

import role.enaRestaurant.EnaCustomerRole;
import role.enaRestaurant.EnaHostRole.Table;
import role.enaRestaurant.EnaWaiterRole.MyCustomers;


public interface Waiter {
	
	
	boolean breakTime = false;

	public abstract void msgHereIsBill(double check, Customer c);

	public abstract void msgOutofFood(String choice);

	public abstract void msgOrderReady(String choice, Table table);

	public abstract void msgReadyToOrder(Customer enaCustomer);

	public abstract void msgHereIsMyChoice(String choice, Customer enaCustomer);

	public abstract void msgDoneEating(Customer enaCustomer);

	public abstract void msgCheckPlease(Customer enaCustomer,
			String choice);

	public abstract List<MyCustomers> getMyCustomers();
	


}
