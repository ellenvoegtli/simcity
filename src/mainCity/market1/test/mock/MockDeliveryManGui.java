package mainCity.market1.test.mock;

import mainCity.market1.interfaces.DeliveryMan1;
import mainCity.market1.interfaces.DeliveryManGuiInterface;


public class MockDeliveryManGui extends Mock implements DeliveryManGuiInterface {
	public DeliveryMan1 deliveryMan;
	
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