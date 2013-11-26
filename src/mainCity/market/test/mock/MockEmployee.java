package mainCity.market.test.mock;

import java.util.Map;

import mainCity.interfaces.MainCashier;
import mainCity.interfaces.MainCook;
import mainCity.market.MarketCustomerRole;
import mainCity.market.MarketEmployeeRole.MyBusiness;
import mainCity.market.MarketEmployeeRole.MyCustomer;
import mainCity.market.interfaces.Employee;

public class MockEmployee extends Mock implements Employee {
	MockEmployee(String name){
		super(name);
	}
	
	public void msgAssignedToBusiness(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer>inventory){
		
	}
	public void msgAssignedToCustomer(MarketCustomerRole c, int waitPosX, int waitPosY){
		
	}
	public void msgHereIsMyOrder(MarketCustomerRole c, Map<String, Integer> inventory, String deliveryMethod){
		
	}
	public void msgHereIsBill(MarketCustomerRole c, double amount){		//from cashier
		
	}
	public void msgHereIsBill(String name, double amount){		//from cashier
		
	}
	public void msgOrderFulfilled(MyCustomer mc){		//from timer
		
	}
	public void msgOrderFulfilled(MyBusiness mb){		//from timer
		
	}
	public void msgDoneAndLeaving(MarketCustomerRole c){
		
	}
	
}