package housing.HouseTest.HouseMock;

import java.util.List;

import housing.personHome;
import housing.Interfaces.Occupant;
import housing.Interfaces.landLord;
import housing.gui.OccupantGui;
import mainCity.test.EventLog;
import mainCity.test.LoggedEvent;


public class MockOccupant extends Mock implements Occupant
{
	public EventLog log = new EventLog();
	public landLord landLord;
	public personHome home;


	public MockOccupant(String name) 
	{
		super(name);
	}

	@Override
	public void msgAtDestination() 
	{
		log.add(new LoggedEvent("message recieved atDestination"));
		
	}

	@Override
	public void gotHungry() 
	{
		log.add(new LoggedEvent("message recieved gotHungry"));
		
	}

	@Override
	public void msgFixed(String appName) 
	{
		log.add(new LoggedEvent("message recieved Fixed"));
		
	}

	@Override
	public void msgNeedFood(List<String> buyFood) 
	{
		log.add(new LoggedEvent("message recieved needFood"));
		
	}

	@Override
	public void msgCookFood(String foodCh) 
	{
		log.add(new LoggedEvent("message recieved cookFood"));
		
	}

	@Override
	public void msgLeaveHome() 
	{
		log.add(new LoggedEvent("message recieved leaveHome"));
		
	}

	public void setHouse(personHome home2)
	{
		home = home2;
	}

	@Override
	public personHome getHome() {
		return home;
	}

	@Override
	public void applianceBroke() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgNeedsMaintenance(String appl) {
		// TODO Auto-generated method stub
		
	}

	



	
}
