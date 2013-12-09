package mainCity.restaurants.EllenRestaurant.test.mock;

import mainCity.restaurants.EllenRestaurant.interfaces.KitchenGuiInterface;


public class MockKitchenGui extends Mock implements KitchenGuiInterface {
	
	public MockKitchenGui(String name){
		super(name);
	}

	@Override
	public void DoGrilling(String choice, int table, int i, int j) {
		
	}

	@Override
	public void DoPlating(String choice, int table, int i, int j) {
		
	}

	@Override
	public void deleteOrderGui(int table) {
		
	}
}