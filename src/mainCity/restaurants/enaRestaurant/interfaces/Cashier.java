package mainCity.restaurants.enaRestaurant.interfaces;

import mainCity.restaurants.enaRestaurant.test.mock.MockCustomer;
import mainCity.restaurants.enaRestaurant.CashierRole.Tab;
import mainCity.restaurants.enaRestaurant.CashierRole.payStatus;

public interface Cashier {
	
	public abstract void msgComputeBill(String choice, Customer c);
	
	public abstract void msgPayment(String choice, double cash, Customer c);
	
	public abstract void msgRestockBill( double reciept, Market ma);
	
}
