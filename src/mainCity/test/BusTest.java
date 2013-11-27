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
	BusAgent bus2;
	BusGui bg; 
	BusGui bg2;
	PersonAgent person; 
	PersonAgent person2;
	BusStop busStop; 
	PersonAgent.CityLocation testLocation = PersonAgent.CityLocation.home;
	ContactList contactList; 
	MockPersonGui pg;
	MockPersonGui pg2;
	CityGui city = new CityGui(); 
		
	public void setUp(){ 
		bus = new BusAgent(); 
		bg = new BusGui(15,15,16,16,bus); 
		bus.setGui(bg);
		bus2 = new BusAgent(); 
		bg2 = new BusGui(15,15,16,16,bus2); 
		bus2.setGui(bg2);
		person = new PersonAgent("TestPerson"); 
		pg = new MockPersonGui("testPersonGui");
		person.setGui(pg);
		person2 = new PersonAgent("TestPerson2"); 
		pg2 = new MockPersonGui("testPersonGui2"); 
		person2.setGui(pg2);
		busStop = new BusStop(0, 0,  testLocation);
		contactList = new ContactList(); 
	}
	
	public void testOnePersonOneBus(){ 
		//Preconditions
		assertEquals("Bus should have 0 passengers on it. It doesn't.", bus.Passengers.size(), 0); 
		assertEquals("Person should have an empty event log. It doesn't.", person.log.size(), 0);
		assertEquals("BusAgent should have an empty event log before Bus' DropOffAndLoadPassengers() is called. Instead the Bus' event log reads: "
						+ bus.log.toString(), 0, bus.log.size());
		
		//Person messages bus that they are getting on the bus
		bus.msgIWantToGetOnBus(person);
		
		assertEquals("BusAgent should have an 1 event in the log after someone gets on the bus. The log reads: "
						+ bus.log.toString(), 1, bus.log.size());
		
		assertTrue("The first message in the log should read " + person.getName() + " wants to get on bus. It doesn't. Instead it says " 
						+ bus.log.getLastLoggedEvent().toString(), bus.log.containsString(person.getName() + " wants to get on bus"));
						
		assertEquals("Bus should have 1 passenger on it. It doesn't.", bus.Passengers.size(), 1); 
		
		//Bus reaches bus stop that is the destination of the person on the bus
		bus.msgAtBusStop(testLocation); 
		
		assertTrue("Since bus is now at a bus stop, the state should be ArrivedAtBusStop, but it's not", bus.currentState == BusAgent.BusState.ArrivedAtBusStop); 
		
		assertEquals("The bus' log should have two logged events in it. It doesn't", bus.log.size(), 2);
		
		assertTrue("The last logged event should say At bus stop near home. Instead it says " 
						+ bus.log.getLastLoggedEvent().toString(), bus.log.containsString("At bus stop near home"));
		
		//Scheduler activate
		bus.pickAndExecuteAnAction(); 

		assertTrue("After the bus reaches a stop, the first thing in the person's event log should be msgArrivedAtDestination received. Instead it reads " 
						+ person.log.getLastLoggedEvent().toString(), person.log.containsString("msgArrivedAtDestination received"));
		
		assertTrue("After the scheduler runs through, the bus state should be ReadyToGo after handling actions when arriving at a bus stop. It's not.", bus.currentState == BusAgent.BusState.ReadyToGo);
		
		//Person messages bus that he is getting off
		bus.msgImGettingOffBus(person); 
		
		assertEquals("After the person messages the bus that it is getting off, the bus should contain 0 passengers. It doesn't.", bus.Passengers.size(), 0);
		
		assertEquals("The bus' log should have 3 logged events in it. It doesn't", bus.log.size(), 3); 
		
		assertTrue("The last logged event s hould say TestPerson wants to get off bus. Instead it says " 
						+ bus.log.getLastLoggedEvent().toString(), bus.log.containsString("TestPerson wants to get off bus"));
		
	}
	
	public void testTwoPeopleOneBus() { 
		//Preconditions
		assertEquals("Bus should have 0 passengers on it. It doesn't.", bus.Passengers.size(), 0); 

		assertEquals("Person should have an empty event log. It doesn't", person.log.size(), 0);
		assertEquals("Person2 should have an empty event log. It doesn't", person2.log.size(), 0);
		assertEquals("BusAgent should have an empty event log before Bus' DropOffAndLoadPassengers() is called. Instead the Bus' event log reads: "
						+ bus.log.toString(), 0, bus.log.size());
		
		//Two people message same bus that they want to get on
		bus.msgIWantToGetOnBus(person);
		bus.msgIWantToGetOnBus(person2);
		
		assertEquals("BusAgent should have an 2 event in the log after someone gets on the bus. The log reads: "
						+ bus.log.toString(), 2, bus.log.size());
		
		assertTrue("The last message in the log should read " + person2.getName() + " wants to get on bus. It doesn't. Instead it says " 
						+ bus.log.getLastLoggedEvent().toString(), bus.log.containsString(person2.getName() + " wants to get on bus"));
						
		assertEquals("Bus should have 2 passengers on it. It doesn't.", bus.Passengers.size(), 2); 
		
		//bus reaches stop that is destination for both people
		bus.msgAtBusStop(testLocation); 
		
		assertTrue("Since bus is now at a bus stop, the state should be ArrivedAtBusStop, but it's not", bus.currentState == BusAgent.BusState.ArrivedAtBusStop); 
		
		assertEquals("The bus' log should have three logged events in it. It doesn't", bus.log.size(), 3);
		
		assertTrue("The last logged event should say At bus stop near home. Instead it says " 
						+ bus.log.getLastLoggedEvent().toString(), bus.log.containsString("At bus stop near home"));
		
		//Bus scheduler activate
		bus.pickAndExecuteAnAction(); 

		assertTrue("After the bus reaches the stop, the first thing in the first person's event log should be msgArrivedAtDestination received. Instead it reads " 
						+ person.log.getLastLoggedEvent().toString(), person.log.containsString("msgArrivedAtDestination received"));
		
		assertTrue("After the bus reaches the stop, the first thing in the second person's event log should be msgArrivedAtDestination received. Instead it reads " 
				+ person2.log.getLastLoggedEvent().toString(), person2.log.containsString("msgArrivedAtDestination received"));
		
		assertTrue("After the scheduler runs through, the bus state should be ReadyToGo after handling actions when arriving at a bus stop. It's not.", bus.currentState == BusAgent.BusState.ReadyToGo);
		
		//Person messages bus that they are getting off
		bus.msgImGettingOffBus(person); 
				
		assertEquals("After the person messages the bus that it is getting off, the bus should contain 1 passenger. It doesn't.", bus.Passengers.size(), 1);
		
		assertEquals("The bus' log should have 4 logged events in it. It doesn't", bus.log.size(), 4); 
		
		assertTrue("The last logged event s hould say TestPerson wants to get off bus. Instead it says " 
						+ bus.log.getLastLoggedEvent().toString(), bus.log.containsString("TestPerson wants to get off bus"));	
		
		//Other person messages bus that they are getting off
		bus.msgImGettingOffBus(person2);
		
		assertEquals("After the person messages the bus that it is getting off, the bus should contain 0 passengers. It doesn't.", bus.Passengers.size(), 0);
		
		assertEquals("The bus' log should have 5 logged events in it. It doesn't", bus.log.size(), 5); 
		
		assertTrue("The last logged event should say TestPerson2 wants to get off bus. Instead it says " 
						+ bus.log.getLastLoggedEvent().toString(), bus.log.containsString("TestPerson2 wants to get off bus"));	
	}
	
	public void testTwoPeopleTwoBuses() { 
		//Preconditions
		assertEquals("Bus should have 0 passengers on it. It doesn't.", bus.Passengers.size(), 0); 

		assertEquals("Person should have an empty event log. It doesn't", person.log.size(), 0);
		assertEquals("Person2 should have an empty event log. It doesn't", person2.log.size(), 0);
		assertEquals("Bus2 should have 0 passengers on it. It doesn't.", bus2.Passengers.size(), 0); 
		
		assertEquals("Bus should have an empty event log before Bus' DropOffAndLoadPassengers() is called. Instead Bus' event log reads: "
						+ bus.log.toString(), 0, bus.log.size());
		
		assertEquals("Bus2 should have an empty event log before Bus2's DropOffAndLoadPassengers() is called. Instead Bus2's event log reads: "
				+ bus2.log.toString(), 0, bus2.log.size());
		
		//Each person getting on a different bus
		bus.msgIWantToGetOnBus(person);
		bus2.msgIWantToGetOnBus(person2);
		
		assertEquals("Bus should have an 1 event in the log after someone gets on the bus. The log reads: "
						+ bus.log.toString(), 1, bus.log.size());
		
		assertEquals("Bus2 should have an 1 event in the log after someone gets on the bus. The log reads: "
				+ bus2.log.toString(), 1, bus2.log.size());
		
		assertTrue("The last message in the log should read " + person.getName() + " wants to get on bus. It doesn't. Instead it says " 
						+ bus.log.getLastLoggedEvent().toString(), bus.log.containsString(person.getName() + " wants to get on bus"));
		
		assertTrue("The last message in the log should read " + person2.getName() + " wants to get on bus. It doesn't. Instead it says " 
				+ bus2.log.getLastLoggedEvent().toString(), bus2.log.containsString(person2.getName() + " wants to get on bus"));
						
		assertEquals("Bus should have 1 passenger on it. It doesn't.", bus.Passengers.size(), 1); 
		
		assertEquals("Bus2 should have 1 passenger on it. It doesn't.", bus2.Passengers.size(), 1);
		
		//Bus travels to the stop that is the destination of the person
		bus.msgAtBusStop(testLocation); 
		bus2.msgAtBusStop(testLocation);
		
		assertTrue("Since bus is now at a bus stop, the state should be ArrivedAtBusStop, but it's not", bus.currentState == BusAgent.BusState.ArrivedAtBusStop); 
		
		assertTrue("Since bus2 is now at a bus stop, the state should be ArrivedAtBusStop, but it's not", bus2.currentState == BusAgent.BusState.ArrivedAtBusStop); 
		
		assertEquals("Bus' log should have two logged events in it. It doesn't", bus.log.size(), 2);
		
		assertEquals("Bus2's log should have two logged events in it. It doesn't", bus.log.size(), 2);
		
		assertTrue("The last logged event in bus should say At bus stop near home. Instead it says " 
						+ bus.log.getLastLoggedEvent().toString(), bus.log.containsString("At bus stop near home"));
		
		assertTrue("The last logged event in bus2 should say At bus stop near home. Instead it says " 
						+ bus2.log.getLastLoggedEvent().toString(), bus2.log.containsString("At bus stop near home"));
		
		//Activate scheduler
		bus.pickAndExecuteAnAction();
		bus2.pickAndExecuteAnAction();

		assertTrue("After the bus reaches the stop, the first thing in the first person's event log should be msgArrivedAtDestination received. Instead it reads " 
						+ person.log.getLastLoggedEvent().toString(), person.log.containsString("msgArrivedAtDestination received"));
		
		assertTrue("After the bus reaches the stop, the first thing in the second person's event log should be msgArrivedAtDestination received. Instead it reads " 
						+ person2.log.getLastLoggedEvent().toString(), person2.log.containsString("msgArrivedAtDestination received"));
		
		assertTrue("After the scheduler runs through, the bus state should be ReadyToGo after handling actions when arriving at a bus stop. It's not.", bus.currentState == BusAgent.BusState.ReadyToGo);
		
		assertTrue("After the scheduler runs through, the bus2 state should be ReadyToGo after handling actions when arriving at a bus stop. It's not.", bus2.currentState == BusAgent.BusState.ReadyToGo);
		
		//People message the bus that they are getting off
		bus.msgImGettingOffBus(person); 
		bus2.msgImGettingOffBus(person2);
				
		assertEquals("After the person messages the bus that it is getting off, the bus should contain 0 passengers. It doesn't.", bus.Passengers.size(), 0);
		
		assertEquals("After the person messages the bus that it is getting off, the bus2 should contain 0 passengers. It doesn't.", bus2.Passengers.size(), 0);
		
		assertEquals("The bus' log should have 3 logged events in it. It doesn't", bus.log.size(), 3); 
		
		assertEquals("The bus2' log should have 3 logged events in it. It doesn't", bus2.log.size(), 3); 
		
		assertTrue("The last logged event in bus should say TestPerson wants to get off bus. Instead it says " 
						+ bus.log.getLastLoggedEvent().toString(), bus.log.containsString("TestPerson wants to get off bus"));	
		
		assertTrue("The last logged event in bus2 should say TestPerson wants to get off bus. Instead it says " 
						+ bus2.log.getLastLoggedEvent().toString(), bus2.log.containsString("TestPerson2 wants to get off bus"));	
		
	}
	
	public void testBusStop() { 
		//Preconditions
		assertEquals("BusStop should have 0 waitingPeople. It doesn't.", busStop.waitingPeople.size(), 0); 
		assertEquals("Person should have an empty event log. It doesn't", person.log.size(), 0);
		assertEquals("BusStop should have an empty event log. Instead the Bus' event log reads: "
						+ busStop.log.toString(), 0, busStop.log.size());
		
		busStop.ArrivedAtBusStop(person);
		
		assertEquals("BusStop should have 1 waitingPerson. It doesn't.", busStop.waitingPeople.size(), 1);
		
		assertTrue("After one person gets to the stop, the last logged event should be TestPerson arrived at stop near home. Instead it says " 
						+ busStop.log.getLastLoggedEvent().toString(), busStop.log.containsString("TestPerson arrived at stop near home"));
		
		busStop.BusHasArrived(bus, 10); 
		
		assertEquals("Person log should have 1 event in it. It doesn't", person.log.size(), 1);
		
		assertTrue("The last logged event should be msgBusHasArrived received. It instead reads " 
						+ person.log.getLastLoggedEvent().toString(), person.log.containsString("msgBusHasArrived received"));
		
		busStop.LeavingBusStop(person); 
		
		assertEquals("Bus Stop should have 0 people waiting. It doesn't", busStop.waitingPeople.size(), 0);
		
		assertTrue("After the person leaves the stop, the last logged event should be TestPerson leaving stop near home. instead it reads "
						+ busStop.log.getLastLoggedEvent().toString(), busStop.log.containsString("TestPerson leaving stop near home"));
	}
	
	public void testTwoPeopleAtBusStop() { 
		//preconditions
		assertEquals("BusStop should have 0 waitingPeople. It doesn't.", busStop.waitingPeople.size(), 0); 
		assertEquals("Person should have an empty event log. It doesn't", person.log.size(), 0);
		assertEquals("Person2 should have an empty event log. It doesn't", person2.log.size(), 0);
		assertEquals("BusStop should have an empty event log. Instead the Bus' event log reads: "
						+ busStop.log.toString(), 0, busStop.log.size());
		
		busStop.ArrivedAtBusStop(person);
		busStop.ArrivedAtBusStop(person2);
		
		assertEquals("BusStop should have 2 waitingPerson. It doesn't.", busStop.waitingPeople.size(), 2);
		
		assertTrue("After two people gets to the stop, the last logged event should be TestPerson2 arrived at stop near home. Instead it says " 
						+ busStop.log.getLastLoggedEvent().toString(), busStop.log.containsString("TestPerson2 arrived at stop near home"));
		
		assertEquals("busStop should have two events logged. It doesn't", busStop.log.size(), 2);
		
		busStop.BusHasArrived(bus, 10); 
		
		assertEquals("Person log should have 1 event in it. It doesn't", person.log.size(), 1);
		
		assertEquals("Person2 log should have 1 event in it. It doesn't", person2.log.size(), 1);
		
		assertTrue("The last logged event for person 1 should be msgBusHasArrived received. It instead reads " 
						+ person.log.getLastLoggedEvent().toString(), person.log.containsString("msgBusHasArrived received"));
		
		assertTrue("The last logged event for person 2 should be msgBusHasArrived received. It instead reads " 
						+ person2.log.getLastLoggedEvent().toString(), person2.log.containsString("msgBusHasArrived received"));
		
		busStop.LeavingBusStop(person);  
		
		assertEquals("Bus Stop should have 1 person waiting. It doesn't", busStop.waitingPeople.size(), 1);
		
		assertTrue("After the person leaves the stop, the last logged event should be TestPerson leaving stop near home. instead it reads "
						+ busStop.log.getLastLoggedEvent().toString(), busStop.log.containsString("TestPerson leaving stop near home"));
		
		busStop.LeavingBusStop(person2);  
		
		assertEquals("Bus Stop should have 0 people waiting. It doesn't", busStop.waitingPeople.size(), 0);
		
		assertTrue("After the person2 leaves the stop, the last logged event should be TestPerson2 leaving stop near home. instead it reads "
						+ busStop.log.getLastLoggedEvent().toString(), busStop.log.containsString("TestPerson2 leaving stop near home"));
	}
}
