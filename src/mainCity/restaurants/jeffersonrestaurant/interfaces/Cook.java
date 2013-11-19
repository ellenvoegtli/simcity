package mainCity.restaurants.jeffersonrestaurant.interfaces;

public interface Cook {

	public abstract void msghereIsAnOrder (int table, String Choice, Waiter w);
	
	public abstract void msgOrderTaken(int t);
	
	public abstract void  msgCannotFulfill(String i, Integer q);
	
	public abstract void msgRestockFulfilled(String i, Integer q);
	
	public abstract void msgPartialFulfill(String i, Integer q);
	
	
	
}
