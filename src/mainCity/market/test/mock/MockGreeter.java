package mainCity.market.test.mock;

import java.util.Map;

import mainCity.interfaces.MainCashier;
import mainCity.interfaces.MainCook;
import mainCity.market.MarketCustomerRole;
import mainCity.market.interfaces.*;

public class MockGreeter extends Mock implements Greeter{
	
	MockGreeter(String name){
		super(name);
	}
	
	public void msgINeedInventory(MarketCustomerRole c, int x, int y){
		
	}
	public void msgINeedInventory(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer> inventoryNeeded){
		
	}
}