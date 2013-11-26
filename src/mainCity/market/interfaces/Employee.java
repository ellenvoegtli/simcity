package mainCity.market.interfaces;

import java.util.Map;

import mainCity.interfaces.MainCashier;
import mainCity.interfaces.MainCook;
import mainCity.market.MarketCustomerRole;
import mainCity.market.MarketEmployeeRole.MyBusiness;
import mainCity.market.MarketEmployeeRole.MyCustomer;


public interface Employee {
	//public abstract void 
	//all MESSAGE method stubs
	
	
    public abstract void msgAssignedToBusiness(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer>inventory);
	public abstract void msgAssignedToCustomer(MarketCustomerRole c, int waitPosX, int waitPosY);
	public abstract void msgHereIsMyOrder(MarketCustomerRole c, Map<String, Integer> inventory, String deliveryMethod);
	public abstract void msgHereIsBill(MarketCustomerRole c, double amount);		//from cashier
	public abstract void msgHereIsBill(String name, double amount);		//from cashier
	public abstract void msgOrderFulfilled(MyCustomer mc);		//from timer
	public abstract void msgOrderFulfilled(MyBusiness mb);		//from timer
	public abstract void msgDoneAndLeaving(MarketCustomerRole c);
	
}