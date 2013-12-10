package mainCity.restaurants.enaRestaurant.interfaces;

import java.util.Collection;

import mainCity.restaurants.enaRestaurant.EnaCustomerRole;
import mainCity.restaurants.enaRestaurant.EnaHostRole.Table;


public interface Host {
	public abstract void msgIWantToEat(EnaCustomerRole cust) ;
	
	
	public abstract void msgWaiterArrived(Customer cust);
	
	
	public abstract void msgWantToGoOnBreak();
	
	public abstract void msgOffBreak();
	

	public abstract void msgTableIsFree(Table t) ;


	public abstract Collection<Table> getTables();
	

}
