package mainCity.restaurants.marcusRestaurant.interfaces;

import role.marcusRestaurant.MarcusCookRole;
import mainCity.restaurants.marcusRestaurant.MarcusTable;

public interface Host {

	// Messages	
	public abstract void msgIWantToEat(Customer cust);

	public abstract void msgIWillWait(Customer c);

	public abstract void msgTableIsClear(MarcusTable t);

	public abstract void msgWantToGoOnBreak(Waiter w);

	public abstract void msgBackOnDuty(Waiter w);

	public abstract MarcusCookRole getCook();
}