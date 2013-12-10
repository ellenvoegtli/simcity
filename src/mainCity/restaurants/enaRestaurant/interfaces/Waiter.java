package mainCity.restaurants.enaRestaurant.interfaces;

import mainCity.restaurants.enaRestaurant.EnaHostRole.Table;


public interface Waiter {
	
	
	public abstract void msgHereIsBill(double check, Customer c);

	public abstract void msgOutofFood(String choice);

	public abstract void msgOrderReady(String choice, Table table);

	public abstract void msgReadyToOrder(Customer enaCustomer);

	public abstract void msgHereIsMyChoice(String choice, Customer enaCustomer);

	public abstract void msgDoneEating(Customer enaCustomer);

	public abstract void msgCheckPlease(Customer enaCustomer,
			String choice);
	


}
