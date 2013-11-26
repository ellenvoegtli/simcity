package mainCity.restaurants.EllenRestaurant.test.mock;

import java.util.Map;

import role.market.MarketDeliveryManRole;
import mainCity.restaurants.EllenRestaurant.interfaces.*;

public class MockCashier extends Mock implements Cashier {
	
	public MockCashier(String name){
		super(name);
	}
	
	@Override
	public void msgHereIsMarketBill(Map<String, Integer>inventory, double billAmount, MarketDeliveryManRole d){
		log.add(new LoggedEvent("Received msgHereIsMarketBill from " + d.getName() + " for $" + billAmount + "."));
	}
	public void msgHereIsChange(double amount, MarketDeliveryManRole deliveryPerson){
		
	}
	public void msgNotEnoughMoney(double amountOwed, double amountPaid){
		
	}
	
	
	
	//from old restaurant tests
	public void msgComputeBill(String choice, Customer cust, Waiter w){
	}
	public void msgHereIsPayment(int checkAmount, int cashAmount, Customer cust){
	}
}