package mainCity.market1.test.mock;

import java.util.Map;

import mainCity.market1.interfaces.DeliveryMan1;
import mainCity.restaurants.marcusRestaurant.MarcusTable;
import mainCity.restaurants.marcusRestaurant.interfaces.Cook;
import mainCity.restaurants.marcusRestaurant.interfaces.Waiter;


public class MockOtherCook extends Mock implements Cook{
	public DeliveryMan1 deliveryMan;
	
	public MockOtherCook(String name){
		super(name);
	}
	
	
	public void msgHereIsYourOrder(Map<String, Integer>inventoryFulfilled){
		log.add(new LoggedEvent("Received msgHereIsYourOrder from delivery man."));
	}
	
	public void msgHereIsAnOrder(Waiter w, String s, MarcusTable t){
		
	}
	public void msgCheckStand(){
		
	}
}