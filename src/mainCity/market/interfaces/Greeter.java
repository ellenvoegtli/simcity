package mainCity.market.interfaces;

import java.util.Map;
import mainCity.interfaces.DeliveryMan;


public interface Greeter {
	public abstract void setCashier(MarketCashier c);
	public abstract void setDeliveryMan(DeliveryMan d);
	public abstract void addEmployee(Employee e);
	
	public abstract void msgINeedInventory(Customer c, int x, int y);
	public abstract void msgINeedInventory(String restaurantName, Map<String, Integer> inventoryNeeded);
	
}