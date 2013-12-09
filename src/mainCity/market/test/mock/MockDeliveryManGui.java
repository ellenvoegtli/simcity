package mainCity.market.test.mock;

import mainCity.interfaces.DeliveryMan;
import mainCity.market.interfaces.DeliveryManGuiInterface;


public class MockDeliveryManGui extends Mock implements DeliveryManGuiInterface {
	public DeliveryMan deliveryMan;
	
	public MockDeliveryManGui(String name){
		super(name);
	}
	
	@Override
	public void DoGoToHomePosition(){
		log.add(new LoggedEvent("Gui is told to DoGoToHomePosition by agent."));
	}
	
	@Override
	public void DoDeliverOrder(String restaurantName){
		log.add(new LoggedEvent("Gui is told to DoDeliverOrder to " + restaurantName + " by agent."));
	}

	
}