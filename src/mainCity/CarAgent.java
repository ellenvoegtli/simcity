package mainCity;

import java.util.*; 
import java.util.concurrent.Semaphore;

import agent.Agent;

public class CarAgent extends Agent { 

		/** Data **/
			
			PersonAgent Owner;
			List<PersonAgent> Passengers = new ArrayList<PersonAgent>();
			
			public enum CarState
			{none, StandBy, Occupied, Arrived}  
			CarState currentState; 
			
			enum CityLocation 
			{home, restaurant, bank, market}
			CityLocation currentLocation; 
			CityLocation Destination;
			
			int DestinationX, DestinationY; 
			int CurrentLocX, CurrentLocY; 
			private Semaphore atDestination = new Semaphore(0, true); 
			
		/** Messages **/ 
			
			private void msgIWantToGoTo(PersonAgent p, int LocationX, int LocationY, CityLocation c){ 
				Passengers.add(p);
				DestinationX = LocationX; 
				DestinationY = LocationY; 
				Destination = c; 
				currentState = CarState.Occupied; 
				stateChanged();
			}

		/** Scheduler **/ 
			
			boolean PickAndExecuteAnAction() { 
				if(currentState == CarState.Occupied){ 
					Travel(); 
				}
			}
			
		/** Actions **/ 
			
			public void Travel() { 
				DoGoToDestination(DestinationX, DestinationY);  
				atDestination.acquire(); 
				currentLocation = Destination;
				
				//Tell passengers that destination has been reached. 
				for(int i=0; i<Passengers.size(); i++){ 
					Passengers.get(i).msgArrivedAtDestination(); 
				}
				
				currentState = CarState.Arrived; 
				stateChanged();
			}

		}

		//Owner of car can't talk to car if its not at the same place as the owner. 
}
