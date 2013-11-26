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

	public MockOccupant(String name) 
	{
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgAtDestination() 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gotHungry() 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgFixed(String appName) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgNeedFood(List<String> buyFood) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgCookFood(String foodCh) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgLeaveHome() 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getHome() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
