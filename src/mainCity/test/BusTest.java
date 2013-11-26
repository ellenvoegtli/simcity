package mainCity.test;

import junit.framework.TestCase;
import mainCity.PersonAgent;
import transportation.BusAgent;
import transportation.BusStop;
import transportation.gui.BusGui;
import mainCity.contactList.ContactList;
import mainCity.gui.CityGui;
import mainCity.gui.PersonGui;

public class BusTest extends TestCase{
	BusAgent bus; 
	BusGui bg; 
	PersonAgent person; 
	BusStop busStop; 
	PersonAgent.CityLocation testLocation = PersonAgent.CityLocation.home;
	ContactList contactList; 
	MockPersonGui pg; 
	CityGui city = new CityGui(); 
		
	public void setUp(){ 
		bus = new BusAgent(); 
		bg = new BusGui(15,15,16,16,bus); 
		bus.setGui(bg);
		person = new PersonAgent("TestPerson"); 
		pg = new MockPersonGui("testPersonGui");
		person.setGui(pg);
		busStop = new BusStop(0, 0,  testLocation);
		contactList = new ContactList(); 
	}
	
	public void testOnePersonOneBus(){ 
		//Preconditions
		assertEquals("Bus should have 0 passengers on it. It doesn't.", bus.Passengers.size(), 0); 
		assertEquals("BusAgent should have an empty event log before Bus' DropOffAndLoadPassengers() is called. Instead the Bus' event log reads: "
						+ bus.log.toString(), 0, bus.log.size());
		
		bus.msgIWantToGetOnBus(person);
		
		assertEquals("BusAgent should have an 1 event in the log after someone gets on the bus. The log reads: "
						+ bus.log.toString(), 1, bus.log.size());
		
		assertTrue("The first message in the log should read " + person.getName() + " wants to get on bus. It doesn't. Instead it says " 
						+ bus.log.getLastLoggedEvent().toString(), bus.log.containsString(person.getName() + " wants to get on bus"));
						
		assertEquals("Bus should have 1 passenger on it. It doesn't.", bus.Passengers.size(), 1); 
		
		bus.msgAtBusStop(testLocation); 
		
		assertTrue("Since bus is now at a bus stop, the state should be ArrivedAtBusStop, but it's not", bus.currentState == BusAgent.BusState.ArrivedAtBusStop); 
		
		assertEquals("The bus' log should have two logged events in it. It doesn't", bus.log.size(), 2);
		
		assertTrue("The last logged event should say At bus stop near home. Instead it says " 
						+ bus.log.getLastLoggedEvent().toString(), bus.log.containsString("At bus stop near home"));
		
		bus.pickAndExecuteAnAction(); 

		assertTrue("After the bus reaches a stop, the first thing in the person's event log should be msgArrivedAtDestination received. Instead it reads " 
						+ person.log.getLastLoggedEvent().toString(), person.log.containsString("msgArrivedAtDestination received"));
		
		assertTrue("After the scheduler runs through, the bus state should be ReadyToGo after handling actions when arriving at a bus stop. It's not.", bus.currentState == BusAgent.BusState.ReadyToGo);
		
		bus.msgImGettingOffBus(person); 
		
		assertEquals("After the person messages the bus that it is getting off, the bus should contain 0 passengers. It doesn't.", bus.Passengers.size(), 0);
		
		assertEquals("The bus' log should have 3 logged events in it. It doesn't", bus.log.size(), 3); 
		
		assertTrue("The last logged event s hould say TestPerson wants to get off bus. Instead it says " 
						+ bus.log.getLastLoggedEvent().toString(), bus.log.containsString("TestPerson wants to get off bus"));
		
	}
}
