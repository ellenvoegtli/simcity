package mainCity.test.Mock;

import mainCity.PersonAgent;
import mainCity.PersonAgent.CityLocation;
import mainCity.interfaces.PersonGuiInterface;
import mainCity.test.LoggedEvent;
import mainCity.test.Mock.Mock;

//Possibly log then release then call the method to release the appropriate semaphore here?

public class MockPersonGui extends Mock implements PersonGuiInterface{
	public MockPersonGui(String name) {
		super(name);
	}


	public void DoGoToLocation(PersonAgent.CityLocation destination) {
		log.add(new LoggedEvent("Gui told to go to " + destination));
	}
	
	public void DoGoToStop() {
		log.add(new LoggedEvent("Gui told to go to bus stop"));
	}
	
	public void DoGoToLocationOnBus(PersonAgent.CityLocation destination) { 
		log.add(new LoggedEvent("Gui told to go to bus stop next to " + destination));
	}
	
	public void DoGoInside() {
		System.out.println("Gui told to go inside by Person");
		log.add(new LoggedEvent("Gui told to go inside by Person"));
	}
	
	public void DoGoOutside() {
		System.out.println("Leaving building and going outside");
		log.add(new LoggedEvent("Gui told to go outside by Person"));
	}

	public CityLocation findNearestStop() {
		return null;
	}

	public void getInCar() {
		log.add(new LoggedEvent("Gui told to go to get into car"));
	}

	public void getOutOfCar() {
		log.add(new LoggedEvent("Gui told to go to get out of car"));		
	}

	public void DoGetOnRoad() {
		log.add(new LoggedEvent("Gui told to go to go on road"));		
	}

	public void DoGoToLocationOnCar(CityLocation d) {
		log.add(new LoggedEvent("Gui told to go to " + d + " as a car"));		
	}

	public void AddCarToLane() {
		log.add(new LoggedEvent("Adding car to lane"));		
	}
	
	public void DoDie() {
		log.add(new LoggedEvent("Person died"));
	}
}