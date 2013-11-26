package mainCity.restaurants.EllenRestaurant.interfaces;

import java.util.Map;

import role.market.MarketDeliveryManRole;
import mainCity.restaurants.EllenRestaurant.*;
import mainCity.interfaces.*;

//messages the cashier receives from Customer and Waiter
//(and eventually market)
public interface Cashier extends MainCashier {
	
	public abstract void msgHereIsMarketBill(Map<String, Integer>inventory, double billAmount, MarketDeliveryManRole d);
	
	public abstract void msgHereIsChange(double amount, MarketDeliveryManRole deliveryPerson);
	
	public abstract void msgNotEnoughMoney(double amountOwed, double amountPaid);
	
	
	//from old restaurant tests
	public abstract void msgComputeBill(String choice, Customer cust, Waiter w);
	public abstract void msgHereIsPayment(int checkAmount, int cashAmount, Customer cust);

	
}