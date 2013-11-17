package mainCity.restaurants.jeffersonrestaurant.interfaces;

import mainCity.restaurants.jeffersonrestaurant.Menu;
import mainCity.restaurants.jeffersonrestaurant.WaiterAgent;
import mainCity.restaurants.jeffersonrestaurant.CustomerAgent.AgentEvent;
import mainCity.restaurants.jeffersonrestaurant.CustomerAgent.AgentState;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Customer {
	
	

	public abstract void msgSitAtTable(int t,Menu menu, Waiter waiterAgent);

	public abstract void msgAnimationFinishedGoToSeat();
	
	public abstract void msgWhatWouldYouLike();
	
	
	public abstract void msgHereIsYourFood();
	
	public abstract void msgAnimationFinishedLeaveRestaurant();
	
	public abstract void msgHereIsYourCheck();
		
	public abstract void msgNotAvailable();
	
	
	public abstract void msgRestaurantFullLeave();
	
	
	

	
	

}