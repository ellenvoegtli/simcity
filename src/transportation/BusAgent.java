package transportation;

import java.util.*;
import java.util.concurrent.Semaphore;
import transportation.gui.BusGui;
import agent.Agent;
import mainCity.PersonAgent;
import mainCity.PersonAgent.CityLocation;
import mainCity.contactList.ContactList;

public class BusAgent extends Agent{

		/** Data **/
			
			BusGui gui; 
		
			List<PersonAgent> Passengers = new ArrayList<PersonAgent>(); 
			int capacity = 50; 
			CityLocation currentLocation; 
			CityLocation destination;
			
			public enum BusState
			{none, ArrivedAtBusStop, ReadyToGo, Driving, Arrived}; 	
			BusState currentState; 
			
			int DestinationX, DestinationY; 
			private Semaphore atDestination = new Semaphore(0, true); 
			
		/** Messages  **/ 
			public void msgIWantToGetOnBus(PersonAgent p){ 
				System.out.println(p.getName() + "getting on bus.");
				Passengers.add(p);
			}

		/** Scheduler **/ 
			
			protected boolean pickAndExecuteAnAction() { 
				if(currentState == BusState.ArrivedAtBusStop){ 
					LoadPassengers(); 
					return true;
				}
				
				if(currentState == BusState.ReadyToGo){ 
					Travel(); 
					return true;
				}
				
				return false;
			}
			
		/** Actions **/  
			public void LoadPassengers() { 
				//People who want to get on the bus are already added to the list, so mostly just gui stuff, timer for persons to have time to get on bus, etc.
				
				for(int i=0; i<ContactList.stops.size(); i++){ 
					if(ContactList.stops.get(i).stopLocation == currentLocation) { 
						ContactList.stops.get(i).BusHasArrived(capacity);
					}
				}
				
				currentState = BusState.ReadyToGo;
				stateChanged();
			}
			
			public void Travel() { 
				System.out.println("Travelling to " + destination);
				
				gui.atBusStop = false;
				
				for(int i=0; i<ContactList.stops.size(); i++){
					if( (gui.getXLoc() == ContactList.stops.get(i).xLocation) 
							&& (gui.getYLoc() == ContactList.stops.get(i).yLocation) ) {
						
						gui.atBusStop = true;
						
						List<PersonAgent> LeavingPassengers = new ArrayList<PersonAgent>(); 
						
						//Tell passengers that destination has been reached. 
						for(int j=0; j<Passengers.size(); j++){ 
							if(Passengers.get(j).getDestination() == currentLocation) 
								Passengers.get(j).msgArrivedAtDestination(); 
								LeavingPassengers.add(Passengers.get(j));
						}
						
						//remove passengers who left the bus from the passenger list. 
						for(int k=0; k<LeavingPassengers.size(); k++){
							Passengers.remove(LeavingPassengers.get(k));
						}
						
						currentState = BusState.ArrivedAtBusStop; 
						stateChanged();
					}
				}
			
			}

							
}

		//Bus Stop, how to handle multiple people with different destinations 
		//When the bus arrives, call a message within all of the PersonAgents at the bus stop
		//Bus stop class? Every time bus stop is altered, send message that changes the information within all busses regarding that stop

