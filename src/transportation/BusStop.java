package transportation;

import java.util.*;

import transportation.gui.BusGui;
import mainCity.PersonAgent;
import mainCity.PersonAgent.CityLocation;
import mainCity.test.LoggedEvent;
import mainCity.test.EventLog;

public class BusStop {
	//List of people waiting at the bus stop
	public List<PersonAgent> waitingPeople = Collections.synchronizedList(new ArrayList<PersonAgent>()); 
	public CityLocation stopLocation; 
	
	public int xLocation, yLocation; 
	
	public BusAgent currentBus;
	
	public EventLog log = new EventLog();
	
	public BusStop(int xLoc, int yLoc, CityLocation cl){ 
		xLocation = xLoc; 
		yLocation = yLoc; 
		stopLocation = cl;
	}
	
	public void ArrivedAtBusStop(PersonAgent p) { 
		log.add(new LoggedEvent(p.getName() + " arrived at stop near " + stopLocation));
		waitingPeople.add(p);
		System.out.println(waitingPeople.get(0) + " has arrived at bus stop near " + stopLocation);
	}
	
	public void LeavingBusStop(PersonAgent p) {
		log.add(new LoggedEvent(p.getName() + " leaving stop near " + stopLocation));
		waitingPeople.remove(p); 
		System.out.println(p.getName() + "Leaving Bus Stop at " + stopLocation); 
	}
	
	public void BusHasArrived(BusAgent b, int capacity) { 
		log.add(new LoggedEvent("Bus has arrived"));
		currentBus = b;
		System.out.println("Bus has arrived " + b);
		
		//Keeps track of people who are getting on the bus when there isn't room for everyone
		List<PersonAgent> tempList = new ArrayList<PersonAgent>(); 
		
		if(capacity >= waitingPeople.size()){ 
			for(int i=0; i<waitingPeople.size(); i++){ 
				waitingPeople.get(i).msgBusHasArrived(); 
			}
		}
		else { 
			for(int i=0; i<capacity; i++) { 
				tempList.add(waitingPeople.get(i)); 
				waitingPeople.get(i).msgBusHasArrived(); 
			}
		}
	}

}
