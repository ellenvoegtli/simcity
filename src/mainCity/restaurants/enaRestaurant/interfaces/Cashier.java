package mainCity.restaurants.enaRestaurant.interfaces;

import role.enaRestaurant.EnaCashierRole.Tab;
import role.enaRestaurant.EnaCashierRole.payStatus;
import mainCity.interfaces.MainCashier;
import mainCity.restaurants.enaRestaurant.test.mock.MockCustomer;

public interface Cashier extends MainCashier {
	
	public abstract void msgComputeBill(String choice, Customer c);
	
	public abstract void msgPayment(String choice, double cash, Customer c);

	public abstract void msgNoMoney();

	public abstract void msgRestMoney();
	
	
}
