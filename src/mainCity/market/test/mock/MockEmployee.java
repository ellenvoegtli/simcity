package mainCity.market.test.mock;

import java.util.Map;

import mainCity.interfaces.MainCashier;
import mainCity.interfaces.MainCook;
import mainCity.market.*;
import mainCity.market.MarketEmployeeRole.MyBusiness;
import mainCity.market.MarketEmployeeRole.MyCustomer;
import mainCity.market.interfaces.*;


public class MockEmployee extends Mock implements Employee {
	public DeliveryMan deliveryMan;
	
	public MockEmployee(String name){
		super(name);
	}

	
	public void msgAssignedToBusiness(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer>inventory){
		
	}
	public void msgAssignedToCustomer(Customer c, int waitPosX, int waitPosY){
		
	}
	public void msgHereIsMyOrder(Customer c, Map<String, Integer> inventory, String deliveryMethod){
		
	}
	@Override
	public void msgHereIsBill(Customer c, double amount){		//from cashier
		log.add(new LoggedEvent("Received msgHereIsBill from cashier for " + c.getName() +". Amount = $"+ amount));
	}
	public void msgHereIsBill(String name, double amount){		//from cashier
		log.add(new LoggedEvent("Received msgHereIsBill from cashier for " + name +". Amount = $"+ amount));
	}
	public void msgOrderFulfilled(MyCustomer mc){		//from timer
		
	}
	public void msgOrderFulfilled(MyBusiness mb){		//from timer
		
	}
	public void msgDoneAndLeaving(Customer c){
		
	}
	
}