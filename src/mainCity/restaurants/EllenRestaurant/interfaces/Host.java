package mainCity.restaurants.EllenRestaurant.interfaces;


public interface Host {
	
	public abstract void msgTableFree(int t);
	
	public abstract void msgIWantBreak(Waiter w);
	public abstract void msgComingOffBreak(Waiter w);
}