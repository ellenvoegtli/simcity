package mainCity.market1.interfaces;

import java.util.Map;

import role.market.MarketCustomerRole;
import role.market.MarketEmployeeRole.MyBusiness;
import role.market.MarketEmployeeRole.MyCustomer;
import mainCity.interfaces.MainCashier;
import mainCity.interfaces.MainCook;


public interface Employee {
	//public abstract void 
	//all MESSAGE method stubs
	public abstract String getName();
	public abstract void setHomeX(int x);
	public abstract void setHomeY(int y);
	
	public abstract void msgAtCashier();
	public abstract void msgAtWaitingRoom();
	public abstract void msgAtDeliveryMan();
	public abstract void msgAtStation();
	public abstract void msgDoneLeaving();
	
    public abstract void msgAssignedToBusiness(String restaurantName, Map<String, Integer>inventory);
	public abstract void msgAssignedToCustomer(Customer c, int waitPosX, int waitPosY);
	public abstract void msgHereIsMyOrder(Customer c, Map<String, Integer> inventory, String deliveryMethod);
	public abstract void msgHereIsBill(Customer c, double amount);		//from cashier
	public abstract void msgHereIsBill(String name, double amount);		//from cashier
	public abstract void msgOrderFulfilled(MyCustomer mc);		//from timer
	public abstract void msgOrderFulfilled(MyBusiness mb);		//from timer
	public abstract void msgDoneAndLeaving(Customer c);
	
}