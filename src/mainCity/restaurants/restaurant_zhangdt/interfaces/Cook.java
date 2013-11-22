package mainCity.restaurants.restaurant_zhangdt.interfaces;

import mainCity.interfaces.MainCook;
import mainCity.restaurants.restaurant_zhangdt.DavidWaiterRole;

public interface Cook extends MainCook{
	
	public abstract void msgHereIsAnOrder (DavidWaiterRole w, String choice, int table);
	
	public abstract void msgMarketOutOfOrder();
	
	public abstract void msgMassOrderReady();
	
	public abstract void msgFinishedMovingToGrill();	

}
