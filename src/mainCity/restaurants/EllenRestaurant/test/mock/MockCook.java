package mainCity.restaurants.EllenRestaurant.test.mock;

import java.util.Map;

import mainCity.interfaces.DeliveryMan;
import mainCity.restaurants.EllenRestaurant.interfaces.Cook;



public class MockCook extends Mock implements Cook {
	public DeliveryMan deliveryMan;
	
	public MockCook(String name){
		super(name);
	}
	
	
	@Override
	public void msgHereIsYourOrder(Map<String, Integer>inventoryFulfilled){
		log.add(new LoggedEvent("Received msgHereIsYourOrder from delivery man."));
	}


	@Override
	public void pickingUpFood(int table) {
		log.add(new LoggedEvent("Received pickingUpFood for table " + table)); 
	}
}