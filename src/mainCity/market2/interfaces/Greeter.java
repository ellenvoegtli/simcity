package mainCity.market2.interfaces;

import java.util.Map;

import role.market1.Market1CustomerRole;
import role.market1.Market1DeliveryManRole;
import mainCity.interfaces.MainCashier;
import mainCity.interfaces.MainCook;


public interface Greeter {
	public abstract void setCashier(MarketCashier c);
	public abstract void setDeliveryMan(DeliveryMan2 d);
	public abstract void addEmployee(Employee e);
	
	public abstract void msgINeedInventory(Customer c, int x, int y);
	public abstract void msgINeedInventory(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer> inventoryNeeded);
	
}