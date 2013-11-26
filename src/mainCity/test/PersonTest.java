package mainCity.test;

import mainCity.PersonAgent;
import mainCity.PersonAgent.*;
import junit.framework.*;
import role.marcusRestaurant.*;

//TODO test with every type of agent, test priority queue

public class PersonTest extends TestCase {
	PersonAgent person;
	MockPersonGui gui;
	
	public void setUp() throws Exception{
		super.setUp();		
		person = new PersonAgent("PersonTest");
		gui = new MockPersonGui(person.getName());
		person.setGui(gui);
	}

	public void testOneMarcusNormalWaiterWork() {
		person.updateOccupation("marcusWaiter", -1, -1);
		MarcusWaiterRole waiter = null;
		assertNull("MarcusNormalWaiterRole should be null. It isn't", waiter);
		
		waiter = new MarcusNormalWaiterRole(person, person.getName());
		assertEquals("MarcusNormalWaiterRole should have current person as holder. It's not", waiter.getPerson(), person);
		
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, waiter);
		assertEquals("MarcusNormalWaiterRole should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());

	}
}
