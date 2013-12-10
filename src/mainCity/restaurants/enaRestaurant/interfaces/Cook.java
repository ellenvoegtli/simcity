package mainCity.restaurants.enaRestaurant.interfaces;

import java.util.Map;

import role.enaRestaurant.EnaWaiterRole;
import role.enaRestaurant.EnaCookRole.Order;
import role.enaRestaurant.EnaCookRole.OrderStatus;
import role.enaRestaurant.EnaHostRole.Table;
import mainCity.interfaces.MainCook;

public interface Cook extends MainCook 
{
	public abstract void msgHereIsTheOrder(EnaWaiterRole w, String choice, Table table);

	public abstract void msgDeliverRestock( Map<String, Integer> newInventory, boolean fullInvoice);

}
