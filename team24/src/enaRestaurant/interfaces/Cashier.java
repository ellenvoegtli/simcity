package enaRestaurant.interfaces;

import enaRestaurant.test.mock.MockCustomer;
import enaRestaurant.CashierRole.Tab;
import enaRestaurant.CashierRole.payStatus;

public interface Cashier {
	
	public abstract void msgComputeBill(String choice, Customer c);
	
	public abstract void msgPayment(String choice, double cash, Customer c);
	
	public abstract void msgRestockBill( double reciept, Market ma);
	
}
