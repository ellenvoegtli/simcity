package mainCity.restaurants.marcusRestaurant.interfaces;

import mainCity.restaurants.marcusRestaurant.MarcusCookRole;

public interface Market {
	public abstract void msgRequestForFood(MarcusCookRole c, String choice, int quantity);
	
	public abstract void msgHereIsPayment(Cashier c, double amount);
}
