package mainCity.market1.interfaces;

import java.util.Map;

import role.market.MarketCustomerRole;
import role.market.MarketDeliveryManRole;
import mainCity.interfaces.DeliveryMan;
import mainCity.interfaces.MainCashier;
import mainCity.interfaces.MainCook;


public interface Greeter {
	public abstract void setCashier(MarketCashier c);
	public abstract void setDeliveryMan(DeliveryMan d);
	public abstract void addEmployee(Employee e);
	
	public abstract void msgINeedInventory(Customer c, int x, int y);
	public abstract void msgINeedInventory(String restaurantName, Map<String, Integer> inventoryNeeded);
	
}