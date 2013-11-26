package mainCity.test;

import mainCity.PersonAgent;
import mainCity.PersonAgent.CityLocation;
import mainCity.interfaces.PersonGuiInterface;
import mainCity.test.Mock;

public class MockPersonGui extends Mock implements PersonGuiInterface{
	EventLog log;
	public MockPersonGui(String name) {
		super(name);
	}


	public void DoGoToLocation(PersonAgent.CityLocation destination) {
		
	}
	
	public void DoGoToStop() {
			
	}
	
	public void DoGoToLocationOnBus(PersonAgent.CityLocation destination) { 
			
	}
	
	public void DoGoInside() {
		log.add(new LoggedEvent("Gui told to go inside by Person"));
	}
	
	public void DoGoOutside() {
	}

	public CityLocation findNearestStop() {
		return null;
	}
}