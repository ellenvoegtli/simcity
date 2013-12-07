package mainCity.interfaces;

import java.util.Map;


public interface DeliveryMan {
	public abstract String getName();
	public abstract void msgAtHome();
	public abstract void msgAtDestination();
	
	public abstract void msgHereIsOrderForDelivery(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer>inventory, double billAmount);
	public abstract void msgHereIsPayment(double amount, String restaurantName);		//sent by any restaurant's cashier
	public abstract void msgChangeVerified(String name);
	public abstract void msgIOweYou(double amount, String name);
	
}