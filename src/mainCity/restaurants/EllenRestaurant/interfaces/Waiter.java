package mainCity.restaurants.EllenRestaurant.interfaces;

import java.util.Collection;

import mainCity.restaurants.EllenRestaurant.*;


//messages waiter receives from Customer and Cashier

public interface Waiter {
    //public void setInactive();
	public Collection getMyCustomers();
	public String getName();
	
	
	public abstract void msgPleaseSeatCustomer(Customer cust, int waitX, int waitY, int table);
	
	public abstract void msgReadyToOrder(Customer cust);
	
	public abstract void msgHereIsChoice(Customer cust, String choice);
	
	public abstract void msgOrderDoneCooking(String choice, int tablenum);
	
	public abstract void msgOutOfFood(String choice, int tablenum);
	
	public abstract void msgDoneAndLeaving(Customer cust);
	
	public void msgBreakRequestResponse(boolean isOK);

	
	
	
	public abstract void msgIWantMyCheck(Customer cust, String choice);
	
	public abstract void msgHereIsCheck(int amount, Customer cust);

}