package mainCity.market1.interfaces;

import java.util.Map;

import role.market.MarketEmployeeRole;


public interface Customer {
	//public abstract void 
	//all MESSAGE method stubs
	public abstract void msgAnimationFinishedGoToCashier();
	public abstract void msgAnimationFinishedLeaveRestaurant();
	public abstract boolean restaurantOpen();
	
	
	public abstract String getName();
	public abstract void goGetInventory(Map<String, Integer> inventoryNeeded);
	public abstract void msgFollowMe(Employee e, int x, int y);
	public abstract void msgMayITakeYourOrder(Employee e);
	public abstract void msgHereIsYourOrder(Map<String, Integer> inventoryFulfilled, double amount);
	public abstract void msgHereIsYourChange(double amountChange, double amountCharged);
	public abstract void msgNotEnoughCash(double cashOwed);
	public abstract void msgHereIsBill(double amount);
	public abstract void msgHereIsFinalBill(double amount);
	
}