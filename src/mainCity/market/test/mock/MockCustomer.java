package mainCity.market.test.mock;

import java.util.Map;

import mainCity.market.*;
import mainCity.market.interfaces.*;


public class MockCustomer extends Mock implements Customer {
        public Employee employee;
        public Cashier cashier;

        public MockCustomer(String name) {
                super(name);

        }
        
    	public void goGetInventory(Map<String, Integer> inventoryNeeded){
    		
    	}
    	public void msgFollowMe(MarketEmployeeRole e, int x, int y){
    		
    	}
    	public void msgMayITakeYourOrder(MarketEmployeeRole e){
    	}
    	public void msgHereIsYourOrder(Map<String, Integer> inventoryFulfilled, double amount){
    	}
    	public void msgHereIsYourChange(double amountChange, double amountCharged){
    	}
    	public void msgNotEnoughCash(double cashOwed){
    	}
    	public void msgHereIsBill(double amount){		//from cashier, who recalculated bill and now sends a lower one
    	}
    	public void msgHereIsFinalBill(double amount){
    	}
}