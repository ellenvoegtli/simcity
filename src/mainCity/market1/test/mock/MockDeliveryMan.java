package mainCity.market1.test.mock;

import java.util.Map;

import role.market1.Market1CashierRole;
import mainCity.interfaces.MainCashier;
import mainCity.interfaces.MainCook;
import mainCity.market1.interfaces.*;

public class MockDeliveryMan extends Mock implements DeliveryMan1 {
	MockDeliveryMan(String name){
		super(name);
	}
	
	public void setCashier(MarketCashier c){
		
	}
	public boolean isActive(){
		return true;
	}
	
	@Override
	public void msgAtHome(){
		log.add(new LoggedEvent("Received msgAtHome"));
	}
	public void msgAtDestination(){
		log.add(new LoggedEvent("Received msgAtDestination"));
	}
	
	public void msgCheckForRedeliveries(){
		
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