package mainCity.restaurants.marcusRestaurant.interfaces;
import mainCity.interfaces.MainCook;
import mainCity.restaurants.marcusRestaurant.MarcusTable;

public interface Cook extends MainCook{
	public abstract void msgHereIsAnOrder(Waiter w, String choice, MarcusTable t);
	
	//public abstract void msgOrderFulfillment(String choice, int q);

	public abstract void msgCheckStand();
	
}
