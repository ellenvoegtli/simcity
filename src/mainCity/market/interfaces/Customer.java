package mainCity.market.interfaces;

import java.util.Map;

import mainCity.market.MarketEmployeeRole;


public interface Customer {
	//public abstract void 
	//all MESSAGE method stubs
	
	public abstract void goGetInventory(Map<String, Integer> inventoryNeeded);
	public abstract void msgFollowMe(MarketEmployeeRole e, int x, int y);
	public abstract void msgMayITakeYourOrder(MarketEmployeeRole e);
	public abstract void msgHereIsYourOrder(Map<String, Integer> inventoryFulfilled, double amount);
	public abstract void msgHereIsYourChange(double amountChange, double amountCharged);
	public abstract void msgNotEnoughCash(double cashOwed);
	public abstract void msgHereIsBill(double amount);
	public abstract void msgHereIsFinalBill(double amount);
	
}