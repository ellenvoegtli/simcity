package mainCity.market2.interfaces;

import java.util.Map;

import mainCity.interfaces.DeliveryMan;
import mainCity.interfaces.MainCashier;
import mainCity.interfaces.MainCook;



public interface DeliveryMan2 extends DeliveryMan {	
	/*
	public abstract void msgAtHome();
	public abstract void msgAtDestination();
	public abstract void setCashier(Market2Cashier c);
	
	public abstract void msgHereIsOrderForDelivery(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer>inventory, double billAmount);
	public abstract void msgHereIsPayment(double amount, String restaurantName);		//sent by any restaurant's cashier
	public abstract void msgChangeVerified(String name);
	public abstract void msgIOweYou(double amount, String name);
	*/
	
	
	//public abstract void msgAtHome();
	//public abstract void msgAtDestination();
	public abstract void setCashier(MarketCashier c);
	//public abstract void msgHereIsOrderForDelivery(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer>inventory, double billAmount);
}