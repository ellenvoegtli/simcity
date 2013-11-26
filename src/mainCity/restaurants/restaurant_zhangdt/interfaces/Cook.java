package mainCity.restaurants.restaurant_zhangdt.interfaces;

import role.davidRestaurant.DavidWaiterRole;
import mainCity.interfaces.MainCook;
import role.davidRestaurant.*;

public interface Cook extends MainCook{
	
	public abstract void msgHereIsAnOrder (DavidWaiterRole w, String choice, int table);
	
	public abstract void msgMarketOutOfOrder();
	
	public abstract void msgMassOrderReady();
	
	public abstract void msgFinishedMovingToGrill();	

}
