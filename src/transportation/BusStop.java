package transportation;

import java.util.*;

import mainCity.PersonAgent;
import mainCity.PersonAgent.CityLocation;

public class BusStop {
	//List of people waiting at the bus stop
	List<PersonAgent> waitingPeople = new ArrayList<PersonAgent>(); 
	public CityLocation stopLocation; 
	
	public int xLocation, yLocation; 
	
	public BusStop(int xLoc, int yLoc, CityLocation cl){ 
		xLocation = xLoc; 
		yLocation = yLoc; 
		stopLocation = cl;
	}
	
	public void ArrivedAtBusStop(PersonAgent p) { 
		System.out.println(p.getName() + "has arrived at bus stop near" + stopLocation);
		waitingPeople.add(p);
	}
	
	public void BusHasArrived(int capacity) { 
		
		//Keeps track of people who are getting on the bus when there isn't room for everyone
		List<PersonAgent> tempList = new ArrayList<PersonAgent>(); 
		
		if(capacity >= waitingPeople.size()){ 
			for(int i=0; i<waitingPeople.size(); i++){ 
				waitingPeople.get(i).msgBusHasArrived(); 
			}
			waitingPeople.clear(); 
		}
		else { 
			for(int i=0; i<capacity; i++) { 
				tempList.add(waitingPeople.get(i)); 
				waitingPeople.get(i).msgBusHasArrived(); 
			}
			waitingPeople.remove(tempList); 
		}
	}

}
