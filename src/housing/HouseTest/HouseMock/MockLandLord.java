package housing.HouseTest.HouseMock;
import housing.OccupantRole;
import housing.Interfaces.landLord;
import housing.gui.LandlordGui;
import mainCity.test.EventLog;
import mainCity.test.LoggedEvent;

public class MockLandLord extends Mock implements landLord
{

	public MockLandLord(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgPleaseFix(OccupantRole occp, String appName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAtDestination() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRenter(OccupantRole occupant) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGui(LandlordGui landLordGui) {
		// TODO Auto-generated method stub
		
	}


}
