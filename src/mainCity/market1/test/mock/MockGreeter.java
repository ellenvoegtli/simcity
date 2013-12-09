package mainCity.market1.test.mock;

import java.util.Map;

import mainCity.interfaces.DeliveryMan;
import mainCity.market1.interfaces.*;

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
	public void msgINeedInventory(String restaurantName, Map<String, Integer> inventoryNeeded){
		
	}
}