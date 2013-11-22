package mainCity.restaurants.enaRestaurant.interfaces;

import java.util.Map;

import mainCity.interfaces.MainCook;
import mainCity.restaurants.enaRestaurant.EnaWaiterRole;
import mainCity.restaurants.enaRestaurant.CookRole.Order;
import mainCity.restaurants.enaRestaurant.CookRole.OrderStatus;
import mainCity.restaurants.enaRestaurant.EnaHostRole.Table;

public interface Cook extends MainCook 
{
	public abstract void msgHereIsTheOrder(EnaWaiterRole w, String choice, Table table);

	public abstract void msgDeliverRestock( Map<String, Integer> newInventory, boolean fullInvoice);

}
