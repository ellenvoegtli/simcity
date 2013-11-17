package mainCity.restaurants.EllenRestaurant.interfaces;

import java.util.Collection;

//import restaurant.test.mock.MockMarket.Order;		//can Order be public in MockMarket???


//messages market receives from Cook and Cashier

public interface Market {
	public String getName();
	
	public abstract void msgINeedInventory(String choice, int amountReq);
	
	//public abstract void msgRequestDone(Order o);
	
	public abstract void msgHereIsPayment(int amount);
}