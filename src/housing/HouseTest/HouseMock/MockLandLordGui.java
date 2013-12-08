package housing.HouseTest.HouseMock;

import mainCity.market1.test.mock.EventLog;
import mainCity.market1.test.mock.LoggedEvent;
import housing.Interfaces.LanLordGuiInterface;
import housing.Interfaces.landLord;

public class MockLandLordGui extends Mock implements LanLordGuiInterface
{

	public landLord  landlord;

	public MockLandLordGui(String name) {
		super(name);
	}

	@Override
	public void DoGoToAplliance(String app) {
		log.add(new LoggedEvent("Gui told to DoGoToAppliance by agent."));

	}

	@Override
	public void DoLeave() {
		log.add(new LoggedEvent("Gui told to DoLeave by agent."));
		
	}

	@Override
	public void DoGoToAppliance(int xPos, int yPos) {
		log.add(new LoggedEvent("Gui told to DoGoToAppliance by agent."));

	}

}
