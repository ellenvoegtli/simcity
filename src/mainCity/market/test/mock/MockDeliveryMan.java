package mainCity.market.test.mock;

import java.util.Map;

import mainCity.interfaces.MainCashier;
import mainCity.interfaces.MainCook;
import mainCity.market.interfaces.*;

public class MockDeliveryMan extends Mock implements DeliveryMan {
	MockDeliveryMan(String name){
		super(name);
	}
	
	public void msgHereIsOrderForDelivery(String restaurantName, MainCook cook, MainCashier cashier, Map<String, Integer>inventory, double billAmount){
		
	}
	public void msgHereIsPayment(double amount, String restaurantName){		//sent by any restaurant's cashier
		
	}
	public void msgChangeVerified(String name){
		
	}
	public void msgIOweYou(double amount, String name){
		
	}

}