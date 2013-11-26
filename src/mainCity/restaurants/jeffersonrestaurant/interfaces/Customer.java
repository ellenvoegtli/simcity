package mainCity.restaurants.jeffersonrestaurant.interfaces;

import role.jeffersonRestaurant.JeffersonWaiterRole;
import role.jeffersonRestaurant.JeffersonCustomerRole.AgentEvent;
import role.jeffersonRestaurant.JeffersonCustomerRole.AgentState;
import mainCity.restaurants.jeffersonrestaurant.Menu;

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