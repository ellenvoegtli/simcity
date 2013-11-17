package mainCity.restaurants.EllenRestaurant.interfaces;

import mainCity.restaurants.EllenRestaurant.*;

//messages the cashier receives from Customer and Waiter
//(and eventually market)
public interface Cashier {
	
	
	public abstract void msgComputeBill(String choice, Customer cust, Waiter w);

	public abstract void msgHereIsPayment(int checkAmount, int cashAmount, Customer cust);

	public abstract boolean pickAndExecuteAnAction();
	
}