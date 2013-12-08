package mainCity.restaurants.EllenRestaurant.test.mock;

import java.util.Map;

import mainCity.market1.interfaces.DeliveryMan1;
import mainCity.restaurants.EllenRestaurant.interfaces.Cook;



public class MockCook extends Mock implements Cook {
	public DeliveryMan1 deliveryMan;
	
	public MockCook(String name){
		super(name);
	}
	
	
	@Override
	public void msgHereIsYourOrder(Map<String, Integer>inventoryFulfilled){
		log.add(new LoggedEvent("Received msgHereIsYourOrder from delivery man."));
	}
}