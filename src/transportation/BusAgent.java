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
			Timer stopTimer = new Timer(); 
		
			List<PersonAgent> Passengers = new ArrayList<PersonAgent>(); 
			int capacity = 50; 
			CityLocation currentLocation; 
			CityLocation destination = CityLocation.restaurant_marcus;
			
			public enum BusState
			{none, ArrivedAtBusStop, ReadyToGo}; 	
			BusState currentState = BusState.ReadyToGo; 
			
			int DestinationX, DestinationY; 
			private Semaphore atDestination = new Semaphore(0, true); 
			
			public BusAgent() { 
			}
			
		/** Messages  **/ 
			public void msgIWantToGetOnBus(PersonAgent p){ 
				System.out.println(p.getName() + "getting on bus.");
				Passengers.add(p);
			}
			
			public void msgAtBusStop(CityLocation cl){
				currentLocation = cl;
				currentState = BusState.ArrivedAtBusStop; 
				stateChanged();
			}

		/** Scheduler **/ 
			
			protected boolean pickAndExecuteAnAction() {
				if(currentState == BusState.ArrivedAtBusStop){ 
					DropOffAndLoadPassengers(); 
					return true;
				}
				
				
				return false;
			}
			
		/** Actions **/  
			public void DropOffAndLoadPassengers() { 
				//People who want to get on the bus are already added to the list, so mostly just gui stuff, timer for persons to have time to get on bus, etc.
				gui.atBusStop = true;
				currentState = BusState.ReadyToGo;
				
				List<PersonAgent> LeavingPassengers = new ArrayList<PersonAgent>(); 
				
				//Tell passengers that destination has been reached. 
				if(Passengers.size() != 0){
					for(int j=0; j<Passengers.size(); j++){ 
						if(Passengers.get(j).getDestination() == currentLocation) 
							Passengers.get(j).msgArrivedAtDestination(); 
							LeavingPassengers.add(Passengers.get(j));
					}
					//remove passengers who left the bus from the passenger list. 
					for(int k=0; k<LeavingPassengers.size(); k++){
						Passengers.remove(LeavingPassengers.get(k));
					}
				}
				
				for(int i=0; i<ContactList.stops.size(); i++){ 
					if(ContactList.stops.get(i).stopLocation == currentLocation) { 
						if(ContactList.stops.get(i).waitingPeople.size() != 0) {
							ContactList.stops.get(i).BusHasArrived(this, capacity);
						} 
					}
				}
				
				stopTimer.schedule(new TimerTask() {
					public void run() {
						gui.atBusStop = false;
						stateChanged();
					}
				},
				500);
				
			}
			
		public void setGui(BusGui bg){ 
			gui = bg;
		}
							
}

		//Bus Stop, how to handle multiple people with different destinations 
		//When the bus arrives, call a message within all of the PersonAgents at the bus stop
		//Bus stop class? Every time bus stop is altered, send message that changes the information within all busses regarding that stop

