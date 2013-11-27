package housing.HouseTest.HouseMock;
import housing.OccupantRole;
import housing.Interfaces.Occupant;
import housing.Interfaces.landLord;
import housing.gui.LandlordGui;
import mainCity.test.EventLog;
import mainCity.test.LoggedEvent;

public class MockLandLord extends Mock implements landLord
{
	public EventLog log = new EventLog();

	
	public MockLandLord(String name) 
	{
		super(name);
	}

	@Override
	public void msgPleaseFix(Occupant occp, String appName)
	{
		log.add(new LoggedEvent("message recieved to fix an aplliance in renters home"));
	
	}
 
	@Override
	public void msgAtDestination() 
	{
		log.add(new LoggedEvent("message recieved at destination"));
	
	}




}
