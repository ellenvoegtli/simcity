package mainCity;

import java.util.*;

public class BusStop {
	//List of people waiting at the bus stop
	List<PersonAgent> waitingPeople = new ArrayList<PersonAgent>(); 
	
	int xLocation, yLocation; 
	
	BusStop(int xLoc, int yLoc){ 
		xLocation = xLoc; 
		yLocation = yLoc; 
	}
	
	public void ArrivedAtBusStop(PersonAgent p) { 
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
