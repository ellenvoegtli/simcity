package mainCity.market.test.mock;

import java.util.Map;

import role.market.MarketCustomerRole;
import mainCity.interfaces.MainCashier;
import mainCity.interfaces.MainCook;
import mainCity.market.interfaces.*;

public class MockGreeter extends Mock implements Greeter{
	
	public MockGreeter(String name){
		super(name);
	}
	
	public void setCashier(MarketCashier c){
	}
	public void setDeliveryMan(DeliveryMan d){
	}
	public void addEmployee(Employee e){
	}
	
	@Override
	public void msgINeedInventory(Customer c, int x, int y){
		log.add(new LoggedEvent("Received msgINeedInventory from " + c.getName() + "."));
	}
	public void msgINeedInventory(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer> inventoryNeeded){
		
	}
}