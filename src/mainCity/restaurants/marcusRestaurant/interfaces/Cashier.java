package mainCity.restaurants.marcusRestaurant.interfaces;
import mainCity.interfaces.MainCashier;
import mainCity.restaurants.marcusRestaurant.MarcusTable;

public interface Cashier extends MainCashier {
	public abstract void msgPayingMyDebt(Customer c, double amount);
	
	public abstract void msgComputeBill(Waiter w, String choice, MarcusTable t);
	
	public abstract void msgHereIsPayment(Customer c, double cash, int table);
}
