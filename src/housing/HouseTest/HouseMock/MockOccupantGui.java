package housing.HouseTest.HouseMock;

import mainCity.market.test.mock.LoggedEvent;
import housing.Interfaces.Occupant;
import housing.Interfaces.OccupantGuiInterface;
import housing.Interfaces.landLord;

public class MockOccupantGui extends Mock implements OccupantGuiInterface{

	public Occupant occupant;

	//LandLord landLord;
	public MockOccupantGui(String name) {
		super(name);
	}



	@Override
	public void DoGoToFridge() {
		log.add(new LoggedEvent("Gui told to DoGoToFridge by agent."));
		
	}

	@Override
	public void DoGoToFridgeA() {
		log.add(new LoggedEvent("Gui told to DoGoToFridgeA by agent."));
		
	}

	@Override
	public void DoGoToStove() {
		log.add(new LoggedEvent("Gui told to DoGoToStove by agent."));
		
	}

	@Override
	public void DoGoToStoveA() {
		log.add(new LoggedEvent("Gui told to DoGoToStoveA by agent."));
		
	}

	@Override
	public void DoGoToSink() {
		log.add(new LoggedEvent("Gui told to DoGoToSink by agent."));
		
	}

	@Override
	public void DoGoToSinkA() {
		log.add(new LoggedEvent("Gui told to DoGoToSinkA by agent."));
		
	}

	@Override
	public void DoGoToKitchenTable() {
		log.add(new LoggedEvent("Gui told to DoGoToKitchenTable by agent."));
		
	}

	@Override
	public void DoGoToKitchenTableA() {
		log.add(new LoggedEvent("Gui told to DoGoToKitchenTableA by agent."));
		
	}

	@Override
	public void DoGoRest() {
		log.add(new LoggedEvent("Gui told to DoGoRest by agent."));
		
	}

	@Override
	public void DoGoRestA() {
		log.add(new LoggedEvent("Gui told to DoGoRestA by agent."));
		
	}

	@Override
	public void DoLeave() {
		log.add(new LoggedEvent("Gui told to DoLeave by agent."));
		
	}

	@Override
	public void DoGoToAppliance(int xPos, int yPos) {
		log.add(new LoggedEvent("Gui told to DoGoToAppliance by agent."));

	}

	@Override
	public boolean isHungry() {
		log.add(new LoggedEvent("Gui told to isHungry by agent."));

		return false;
	}

	@Override
	public void setHungry() {
		log.add(new LoggedEvent("Gui told to setHungry by agent."));

		
	}

}
