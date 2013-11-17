package mainCity.restaurants.restaurant_zhangdt.interfaces;

public interface Cashier {
	
	public abstract void msgHeresACheck(Waiter w, String order, int tableNumber);
	
	public abstract void msgHeresMyPayment(Customer c, double custPayment, int tableNumber);
	
}
