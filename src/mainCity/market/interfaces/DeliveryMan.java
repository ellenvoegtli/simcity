package mainCity.market.interfaces;

import java.util.Map;

import mainCity.interfaces.MainCashier;
import mainCity.interfaces.MainCook;
import mainCity.market.MarketCashierRole;


public interface DeliveryMan {	
	public abstract void msgAtHome();
	public abstract void msgAtDestination();
	
	public abstract void msgHereIsOrderForDelivery(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer>inventory, double billAmount);
	public abstract void msgHereIsPayment(double amount, String restaurantName);		//sent by any restaurant's cashier
	public abstract void msgChangeVerified(String name);
	public abstract void msgIOweYou(double amount, String name);
	
}