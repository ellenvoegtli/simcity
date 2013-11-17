package mainCity.restaurants.marcusRestaurant.interfaces;
import mainCity.restaurants.marcusRestaurant.MarcusTable;

public interface Cashier {
	public abstract void msgPayingMyDebt(Customer c, int amount);
	
	public abstract void msgComputeBill(Waiter w, String choice, MarcusTable t);
	
	public abstract void msgHereIsPayment(Customer c, int cash, int table);
}
