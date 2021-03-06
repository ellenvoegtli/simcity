package mainCity.market.test.mock;

import java.util.Map;

import role.market.MarketCashierRole;
import role.market.MarketDeliveryManRole.Bill;
import mainCity.interfaces.DeliveryMan;
import mainCity.interfaces.MainCashier;
import mainCity.interfaces.MainCook;
import mainCity.market.interfaces.*;

public class MockDeliveryMan extends Mock implements DeliveryMan {
	public MockDeliveryMan(String name){
		super(name);
	}
	
	public void setCashier(MarketCashier c){
		
	}
	public boolean isActive(){
		return true;
	}
	public boolean restaurantOpen(Bill b){
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
	@Override
	public void msgHereIsOrderForDelivery(String restaurantName, Map<String, Integer>inventory, double billAmount){
		log.add(new LoggedEvent("Received msgHereIsOrderForDelivery for " + restaurantName));
	}
	public void msgHereIsPayment(double amount, String restaurantName){		//sent by any restaurant's cashier
		
	}
	public void msgChangeVerified(String name){
		
	}
	public void msgIOweYou(double amount, String name){
		
	}

}