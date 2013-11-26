package mainCity.test;

import junit.framework.TestCase;
import mainCity.PersonAgent;
import transportation.BusAgent;
import transportation.BusStop;
import transportation.gui.BusGui;
import mainCity.contactList.ContactList;

public class BusTest extends TestCase{
	BusAgent bus; 
	BusGui bg; 
	PersonAgent person; 
	BusStop busStop; 
	PersonAgent.CityLocation testLocation = PersonAgent.CityLocation.home;
	ContactList contactList; 
		
	public void setUp(){ 
		bus = new BusAgent(); 
		bg = new BusGui(15,15,16,16,bus); 
		bus.setGui(bg);
		person = new PersonAgent("TestPerson"); 
		busStop = new BusStop(0, 0,  testLocation);
		contactList = new ContactList(); 
	}
	
	public void testOnePersonOnBus(){ 
		//Preconditions
		assertEquals("Bus should have 0 passengers on it. It doesn't.", bus.Passengers.size(), 0); 
		assertEquals("BusAgent should have an empty event log before Bus' DropOffAndLoadPassengers() is called. Instead the Bus' event log reads: "
						+ bus.log.toString(), 0, bus.log.size());
		
		bus.msgIWantToGetOnBus(person);
		
		assertEquals("BusAgent should have an 1 event in the log after someone gets on the bus. The log reads: "
						+ bus.log.toString(), 1, bus.log.size());
		assertEquals("Bus should have 1 passenger on it. It doesn't.", bus.Passengers.size(), 1); 
		
		bus.msgAtBusStop(testLocation); 
		
		assertTrue("Since bus is now at a bus stop, the state should be ArrivedAtBusStop, but it's not", bus.currentState == BusAgent.BusState.ArrivedAtBusStop); 
		
		bus.pickAndExecuteAnAction(); 
		
		assertTrue("After the scheduler runs through, the bus state should be ReadyToGo after handling actions when arriving at a bus stop. It's not.", bus.currentState == BusAgent.BusState.ReadyToGo);
		
		
	}
}
