package mainCity.restaurants.restaurant_zhangdt.interfaces;

import mainCity.interfaces.MainCashier;

public interface Cashier extends MainCashier{
	
	public abstract void msgHeresACheck(Waiter w, String order, int tableNumber);
	
	public abstract void msgHeresMyPayment(Customer c, double custPayment, int tableNumber);
	
}
