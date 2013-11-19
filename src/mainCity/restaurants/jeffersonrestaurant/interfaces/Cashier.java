package mainCity.restaurants.jeffersonrestaurant.interfaces;

import mainCity.interfaces.MainCashier;

public interface Cashier extends MainCashier {

	
	public abstract void ReadyToPay(Customer c);
	
	
	public abstract void HereIsMymoney (Customer c, double money);
	
	
	
	
	
	
}
