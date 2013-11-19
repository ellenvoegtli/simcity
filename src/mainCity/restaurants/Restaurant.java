package mainCity.restaurants;

import mainCity.interfaces.MainCashier;
import mainCity.interfaces.MainCook;

public abstract class Restaurant {
	String restaurantName;
	MainCook cook;
	MainCashier cashier;
}
