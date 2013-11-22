package transportation;

import java.util.*;
import java.util.concurrent.Semaphore;

import mainCity.PersonAgent;

public class BusAgent extends Agent{

		/** Data **/
			
			/**
			Class BusStop{ 
				List<PersonAgent> waitingPeople = new ArrayList<PersonAgent>(); 
				int locationX, locationY; 
			};
			Will make this a separate agent**/ 

			List<PersonAgent> Passengers = new ArrayList<PersonAgent>(); 
			List<BusStop> busStops = new ArrayList<BusStop>(); 
			
			enum CityLocation 
			{home, restaurant, bank, market}
			CityLocation currentLocation; 
			
			public enum BusState
			{none, ArrivedAtBusStop, ReadyToGo, Driving, Arrived} 	
			BusState currentState; 
			
			int DestinationX, DestinationY; 
			private Semaphore atDestination = new Semaphore(0, true); 
			
		/** Messages **/ 
			msgIWantToGetOnBus(PersonAgent p){ 
				Passengers.add(p);
			}

		/** Scheduler **/ 
			
			boolean PickAndExecuteAnAction() { 
				if(currentState == BusState.ArrivedAtBusStop){ 
					LoadPassengers(); 
				}
				
				if(currentState == BusState.ReadyToGo){ 
					Travel(); 
				}
			}
			
		/** Actions **/  
			public void LoadPassengers() { 
				//People who want to get on the bus are already added to the list, so mostly just gui stuff, timer for persons to have time to get on bus, etc.
				//Might make waitingPassengers list and Passengers list. 
				currentState = BusState.ReadyToGo;
				stateChanged();
			}
			
			public void Travel() { 
				DoGoToDestination(DestinationX, DestinationY);  
				atDestination.acquire(); 
				List<PersonAgent> LeavingPassengers = new ArrayList<PersonAgent>(); 
				
				//Tell passengers that destination has been reached. 
				for(int i=0; i<Passengers.size(); i++){ 
					if(Passengers.get(i).destination == 
						Passengers.get(i).msgArrivedAtDestination(); 
						LeavingPassengers.add(Passengers.get(i);
				}
				
				//remove passengers who left the bus from the passenger list. 
				for(int i=0; i<LeavingPassengers.getSize(); i++){
					Passengers.remove(LeavingPassengers.get(i));
				}
				
				currentState = BusState.ArrivedAtBusStop; 
				stateChanged();
			}
		}

		//Bus Stop, how to handle multiple people with different destinations 
		//When the bus arrives, call a message within all of the PersonAgents at the bus stop
		//Bus stop class? Every time bus stop is altered, send message that changes the information within all busses regarding that stop
}
