package mainCity.restaurants.marcusRestaurant.interfaces;

import mainCity.restaurants.marcusRestaurant.MarcusTable;

public interface Waiter {
	public abstract void msgRequestBreak();
	
	public abstract void msgBreakReply(boolean r);
	
	public abstract void msgSeatAtTable(Customer cust, MarcusTable t);

	public abstract void msgImReadyToOrder(Customer customer);
	
	public abstract void msgHereIsMyChoice(Customer customer, String choice);
	
	public abstract void msgOutOfFood(int table, String choice);
	
	public abstract void msgOrderIsReady(int table, String choice);

	public abstract void msgReadyForCheck(Customer cust);
	
	public abstract void msgHereIsCheck(int amount, int table);
	
	public abstract void msgLeavingTable(Customer cust);
	
	public abstract int getCustomerCount();
}
