package mainCity.market1.test.mock;

import java.util.Map;

import mainCity.interfaces.DeliveryMan;
import mainCity.restaurants.marcusRestaurant.MarcusTable;
import mainCity.restaurants.marcusRestaurant.interfaces.*;


public class MockOtherCashier extends Mock implements Cashier{
	
	
	public MockOtherCashier(String name){
		super(name);
	}
	
	public void msgHereIsMarketBill(Map<String, Integer>inventory, double billAmount, DeliveryMan d){
		log.add(new LoggedEvent("Received msgHereIsMarketBill from " + d.getName() + " for $" + billAmount + "."));
	}
	public void msgPayingMyDebt(Customer c, double d){
		
	}
	public void msgNotEnoughMoney(double d, double d2){
		
	}
	public void msgComputeBill(Waiter w, String s, MarcusTable t){
		
	}
	public void msgHereIsChange(double d, DeliveryMan dm){
		log.add(new LoggedEvent("Received msgHereIsChange from " + dm.getName() + " for $" + d + "."));
	}
	public void msgHereIsPayment(Customer c, double d, int i){
		
	}
	
}