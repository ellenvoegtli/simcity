package mainCity.market.test.mock;

import java.util.Map;

import mainCity.market.*;
import mainCity.market.interfaces.*;


public class MockCustomer extends Mock implements Customer {
        public Employee employee;
        public MarketCashier cashier;

        public MockCustomer(String name) {
                super(name);

        }
        
        public void msgAnimationFinishedGoToCashier(){
        	
        }
    	public void msgAnimationFinishedLeaveRestaurant(){
    		
    	}
    	public boolean restaurantOpen(){
    		return true;
    	}
        
    	public void goGetInventory(Map<String, Integer> inventoryNeeded){
    		
    	}
    	@Override
    	public void msgFollowMe(Employee e, int x, int y){
    		log.add(new LoggedEvent("Received msgFollowMe from " + e.getName()));
    	}
    	@Override
    	public void msgMayITakeYourOrder(Employee e){
    		log.add(new LoggedEvent("Received msgMayITakeYourOrder from " + e.getName()));
    	}
    	@Override
    	public void msgHereIsYourOrder(Map<String, Integer> inventoryFulfilled, double amount){
    		log.add(new LoggedEvent("Received msgHereIsYourOrder. Amount = $" + amount));
    	}
    	@Override
    	public void msgHereIsYourChange(double amountChange, double amountCharged){
    		log.add(new LoggedEvent("Received msgHereIsYourChange from cashier. Change = $"+ amountChange));
    	}
    	@Override
    	public void msgNotEnoughCash(double cashOwed){
    		log.add(new LoggedEvent("Received msgNotEnoughCash from cashier. Money Owed = $"+ cashOwed));
    	}
    	public void msgHereIsBill(double amount){		//from cashier, who recalculated bill and now sends a lower one
    	}
    	public void msgHereIsFinalBill(double amount){
    		log.add(new LoggedEvent("Received msgHereIsFinalBill from cashier. Amount = $"+ amount));
    	}
}