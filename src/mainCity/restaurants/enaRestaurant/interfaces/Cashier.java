package mainCity.restaurants.enaRestaurant.interfaces;

import mainCity.interfaces.MainCashier;
import mainCity.restaurants.enaRestaurant.test.mock.MockCustomer;
import mainCity.restaurants.enaRestaurant.EnaCashierRole.Tab;
import mainCity.restaurants.enaRestaurant.EnaCashierRole.payStatus;

public interface Cashier extends MainCashier {
	
	public abstract void msgComputeBill(String choice, Customer c);
	
	public abstract void msgPayment(String choice, double cash, Customer c);

	public abstract void msgNoMoney();

	public abstract void msgRestMoney();
	
	
}
