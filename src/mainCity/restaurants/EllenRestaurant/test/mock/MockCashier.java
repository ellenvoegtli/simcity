package mainCity.restaurants.EllenRestaurant.test.mock;

import java.util.Map;

import role.market.MarketDeliveryManRole;
import mainCity.interfaces.DeliveryMan;
import mainCity.restaurants.EllenRestaurant.interfaces.*;

public class MockCashier extends Mock implements Cashier {
	
	public MockCashier(String name){
		super(name);
	}
	
	@Override
	public void msgHereIsMarketBill(Map<String, Integer>inventory, double billAmount, DeliveryMan d){
		log.add(new LoggedEvent("Received msgHereIsMarketBill from " + d.getName() + " for $" + billAmount + "."));
	}
	
	public void msgHereIsChange(double amount, DeliveryMan d){
		log.add(new LoggedEvent("Received msgHereIsChange from " + d.getName() + " for $" + amount + "."));
	}
	public void msgNotEnoughMoney(double amountOwed, double amountPaid){
		log.add(new LoggedEvent("Received msgNotEnoughMoney, amount owed = $" + amountOwed + "."));
	}
	
	
	
	//from old restaurant tests
	public void msgComputeBill(String choice, Customer cust, Waiter w){
	}
	public void msgHereIsPayment(double checkAmount, double cashAmount, Customer cust){
	}
}