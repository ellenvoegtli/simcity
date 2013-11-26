package mainCity.test;

import mainCity.PersonAgent;
import mainCity.PersonAgent.*;
import junit.framework.*;
import role.marcusRestaurant.*;

//TODO test with every type of agent, test priority queue, possible gui?, customers

public class PersonTest extends TestCase {
	PersonAgent person;
	MockPersonGui gui;
	
	public void setUp() throws Exception{
		super.setUp();		
		person = new PersonAgent("PersonTest");
		gui = new MockPersonGui(person.getName());
		person.setGui(gui);
	}

	public void testOneMarcusNormalWaiterRoleWork() {
		person.updateOccupation("marcusWaiter", -1, -1);
		MarcusWaiterRole waiter = null;
		assertNull("MarcusNormalWaiterRole should be null. It isn't", waiter);
		
		waiter = new MarcusNormalWaiterRole(person, person.getName());
		assertEquals("MarcusNormalWaiterRole should have current person as holder. It's not", waiter.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, waiter);
		assertEquals("MarcusNormalWaiterRole should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's MarcusNormalWaiterRole should be active. It isn't", waiter.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		waiter.setInactive();
		
		assertFalse("Person's MarcusNormalWaiterRole should be inactive. It isn't", waiter.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End marcusNormalWaiter
	
	public void testTwoMarcusSharedWaiterRoleWork() {
		person.updateOccupation("marcusSharedWaiter", -1, -1);
		MarcusWaiterRole waiter = null;
		assertNull("MarcusSharedWaiterRole should be null. It isn't", waiter);
		
		waiter = new MarcusSharedWaiterRole(person, person.getName());
		assertEquals("MarcusSharedWaiterRole should have current person as holder. It's not", waiter.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, waiter);
		assertEquals("MarcusSharedWaiterRole should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's MarcusSharedWaiterRole should be active. It isn't", waiter.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		waiter.setInactive();
		
		assertFalse("Person's MarcusSharedWaiterRole should be inactive. It isn't", waiter.isActive());
		assertEquals("Person's state should be Shared. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End marcusSharedWaiter
	
	public void testThreeMarcusCashierRoleWork() {
		person.updateOccupation("marcusCashier", -1, -1);
		MarcusCashierRole cashier = null;
		assertNull("MarcusCashierRole should be null. It isn't", cashier);
		
		cashier = new MarcusCashierRole(person, person.getName());
		assertEquals("MarcusCashierRole should have current person as holder. It's not", cashier.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, cashier);
		assertEquals("MarcusCashierRole should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's MarcusCashierRole should be active. It isn't", cashier.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		cashier.setInactive();
		
		assertFalse("Person's MarcusCashierRole should be inactive. It isn't", cashier.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End marcusCashier
	
	public void testFourMarcusCookRoleWork() {
		person.updateOccupation("marcusCook", -1, -1);
		MarcusCookRole cook = null;
		assertNull("MarcusCookRole should be null. It isn't", cook);
		
		cook = new MarcusCookRole(person, person.getName());
		assertEquals("MarcusCookRole should have current person as holder. It's not", cook.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, cook);
		assertEquals("MarcusCookRole should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's MarcusCookRole should be active. It isn't", cook.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		cook.setInactive();
		
		assertFalse("Person's MarcusCashierRole should be inactive. It isn't", cook.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End marcusCook
	
	public void testFiveMarcusHostWork() {
		person.updateOccupation("marcusHost", -1, -1);
		MarcusHostRole host = null;
		assertNull("MarcusHostRole should be null. It isn't", host);
		
		host = new MarcusHostRole(person, person.getName());
		assertEquals("MarcusHostRole should have current person as holder. It's not", host.getPerson(), person);
		//Nothing right now
		assertTrue("Person's role map should be empty. It isn't.", person.getRoles().isEmpty());		
		person.addRole(ActionType.work, host);
		assertEquals("MarcusHostRole should have 1 role in its list of roles. It doesn't", person.getRoles().size(), 1);
		
		//Telling Person to go to work
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());		
		person.msgGoToWork();
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		
		assertNull("Person's current action should be null.", person.getCurrentAction());		
		
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertTrue("Person's action list should be empty again. It isn't.", person.getActions().isEmpty());		
		assertEquals("Person's current action should be work.", person.getCurrentAction().type, ActionType.work);
		
		//Arriving at work
		person.setEvent(PersonEvent.arrivedAtWork);
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("MockPersonGui should have 1 event log after the goInside call. Instead, the MockCustomer's event log reads: " + gui.getLog().toString(), 1, gui.getLog().size());

		//Is Working
		assertTrue("Person's MarcusHostRole should be active. It isn't", host.isActive());
		assertEquals("Person's state should be working. It isn't", PersonState.working, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Going off duty
		host.setInactive();
		
		assertFalse("Person's MarcusCashierRole should be inactive. It isn't", host.isActive());
		assertEquals("Person's state should be normal. It isn't", PersonState.normal, person.getState());
		assertEquals("Person's role map should still have 1 role. It doesn't.", person.getRoles().size(), 1);		
		
		//Finished work
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertNull("Person's current action should be null. It wasn't", person.getCurrentAction());		

		//Going home
		assertTrue("Person's action list should be empty. It isn't.", person.getActions().isEmpty());	
		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's action list should have 1 action in it. It doesn't.", person.getActions().size(), 1);		

		assertTrue("Person's pickAndExecute should return true. It didn't.", person.pickAndExecuteAnAction());
		assertEquals("Person's current action should be home.", person.getCurrentAction().type, ActionType.home);		
	}//End marcusHost
}
