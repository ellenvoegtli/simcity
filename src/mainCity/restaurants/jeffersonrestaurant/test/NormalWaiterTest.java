package mainCity.restaurants.jeffersonrestaurant.test;

import role.jeffersonRestaurant.JeffersonNormalWaiterRole;
import role.jeffersonRestaurant.JeffersonSharedDataWaiterRole;
import role.jeffersonRestaurant.JeffersonWaiterRole.waiterCustState;
import mainCity.PersonAgent;
import mainCity.restaurants.jeffersonrestaurant.test.mock.EventLog;
import mainCity.restaurants.jeffersonrestaurant.test.mock.MockCook;
import mainCity.restaurants.jeffersonrestaurant.test.mock.MockCustomer;
import mainCity.restaurants.jeffersonrestaurant.test.mock.MockWaiterGui;
import junit.framework.TestCase;

public class NormalWaiterTest extends TestCase {
	
	JeffersonNormalWaiterRole normalWaiter;
	MockCook cook;
	MockCustomer customer;
	MockCustomer customer2;
	MockWaiterGui gui;
	

	protected void setUp() throws Exception {
		super.setUp();
		PersonAgent base = new PersonAgent("normalWaiter");
		normalWaiter = new JeffersonNormalWaiterRole(base, base.getName());
		
		base.addRole(PersonAgent.ActionType.work, normalWaiter);
		gui = new MockWaiterGui("gui");
		customer = new MockCustomer("mockcustomer");	
		customer2= new MockCustomer("mockcustomer2");
		cook = new MockCook("mockcook");
		gui.log=new EventLog();
		customer.log = new EventLog();
		customer2.log = new EventLog();
		cook.log = new EventLog();
		normalWaiter.setGui(gui);
		normalWaiter.cook=cook;
		gui.waiter=normalWaiter;
		
	}
	
	public void testOneNormalCustomerScenario() {
		System.out.println("Beginning One Normal Customer Scenario");
		
		//Step 1-Check preconditions
		assertEquals("Waiter should have zero customer in its list. It doesn't.", normalWaiter.CustomerList.size(), 0);
		assertEquals("MockCustomer should have an empty event log before the Waiters scheduler is called. Instead, the MockCustomer's event log reads: " + customer.log.toString(), 0, customer.log.size());
		assertTrue("Waiter's gui should have been set, it isn't." ,normalWaiter.getGui()==gui);
		//Step 2 - Add a Customer
		normalWaiter.msgSeatAtTable(customer, 0);
		
		assertEquals("Customer should have an empty event log before waiter's scheduler is called. Instead it reads: " + customer.log.toString(),0, customer.log.size());
		
		assertEquals("Cook should have an empty event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),0, cook.log.size());
		
		assertEquals("Waiter should have onecustomer in its list. It doesn't.", normalWaiter.CustomerList.size(), 1);
		assertEquals("waiter's customer should be the mockCustomer, it isnt", normalWaiter.CustomerList.get(0).c,customer);
		assertEquals("waiter's customer should have table 0, it doesnt", normalWaiter.CustomerList.get(0).table,0);
		assertTrue("waiter's customer should be not seated, it isnt", normalWaiter.CustomerList.get(0).state==waiterCustState.notSeated);
		
		
		//Step 3- Run the Scheduler
		
		assertTrue("Waiter's scheduler should have return true. But it didn't.", normalWaiter.pickAndExecuteAnAction());
		
		//Step 4 - Check postconditions
		assertTrue("Customer should have logged \"msgSitAtTable\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgSitAtTable"));
		assertTrue("waiter's customer should be not seated, it isnt", normalWaiter.CustomerList.get(0).state==waiterCustState.seated);
		
		assertEquals("Cook should have an empty event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),0, cook.log.size());
		
		assertEquals("Waiter should have onecustomer in its list. It doesn't.", normalWaiter.CustomerList.size(), 1);
		assertEquals("waiter's customer should be the mockCustomer, it isnt", normalWaiter.CustomerList.get(0).c,customer);
		assertEquals("waiter's customer should have table 0, it doesnt", normalWaiter.CustomerList.get(0).table,0);
		assertTrue("WaiterGui should have logged \"Received msgSitAtTable\" but didn't. His log reads instead: " 
				+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoLeaveCustomer"));
		
		//step 5
		normalWaiter.msgImReadyToOrder(customer);
		
		
		assertEquals("Cook should have an empty event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),0, cook.log.size());
		
		assertEquals("Waiter should have onecustomer in its list. It doesn't.", normalWaiter.CustomerList.size(), 1);
		assertEquals("waiter's customer should be the mockCustomer, it isnt", normalWaiter.CustomerList.get(0).c,customer);
		assertEquals("waiter's customer should have table 0, it doesnt", normalWaiter.CustomerList.get(0).table,0);
		assertTrue("waiter's customer should be not seated, it isnt", normalWaiter.CustomerList.get(0).state==waiterCustState.readyToOrder);
		
		//step 6-Run the scheduler
		assertTrue("Waiter's scheduler should have return true. But it didn't.", normalWaiter.pickAndExecuteAnAction());
		
		//check postconditions
		assertTrue("waiter's customer should be not seated, it isnt", normalWaiter.CustomerList.get(0).state==waiterCustState.waitingForWaiter);
		assertTrue("WaiterGui should have logged \"DoBringToTable\" but didn't. His log reads instead: " 
				+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoBringToTable"));
		
		
		assertTrue("Customer should have logged \"Asked what I would like\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Asked what I would like."));
		

		//Step 7 
		normalWaiter.msgHereIsMyChoice(customer, "steak");
		
		assertTrue("waiter's customer should be ordered, it isnt", normalWaiter.CustomerList.get(0).state==waiterCustState.ordered);
		
		assertTrue(" waiter's mycust choice should be steak, it isnt't",normalWaiter.CustomerList.get(0).choice=="steak");
		
		//Step 8 Run scheduler
		assertTrue("Waiter's scheduler should have return true. But it didn't.", normalWaiter.pickAndExecuteAnAction());
		
		//step 9 check postconditions
		
		assertTrue("waiter's customer should be ordered, it isnt", normalWaiter.CustomerList.get(0).state==waiterCustState.waitingForOrder);
		
		
		assertTrue("WaiterGui should have logged \"DoGoToCook\" but didn't. His log reads instead: " 
				+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoGoToCook"));
		

		assertTrue("cook should have logged \"received msgHereIsYourOrder\" but didn't. His log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("received msgHereIsYourOrder"));
		
	}
	public void testTwoNormalCustomerScenario() {
		System.out.println("Beginning One Normal Customer Scenario");
		
		//Step 1-Check preconditions
		assertEquals("Waiter should have zero customer in its list. It doesn't.", normalWaiter.CustomerList.size(), 0);
		assertEquals("MockCustomer should have an empty event log before the Waiters scheduler is called. Instead, the MockCustomer's event log reads: " + customer.log.toString(), 0, customer.log.size());
		assertTrue("Waiter's gui should have been set, it isn't." ,normalWaiter.getGui()==gui);
		//Step 2 - Add a Customer
		normalWaiter.msgSeatAtTable(customer, 0);
		
		assertEquals("Customer should have an empty event log before waiter's scheduler is called. Instead it reads: " + customer.log.toString(),0, customer.log.size());
		
		assertEquals("Cook should have an empty event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),0, cook.log.size());
		
		assertEquals("Waiter should have onecustomer in its list. It doesn't.", normalWaiter.CustomerList.size(), 1);
		assertEquals("waiter's customer should be the mockCustomer, it isnt", normalWaiter.CustomerList.get(0).c,customer);
		assertEquals("waiter's customer should have table 0, it doesnt", normalWaiter.CustomerList.get(0).table,0);
		assertTrue("waiter's customer should be not seated, it isnt", normalWaiter.CustomerList.get(0).state==waiterCustState.notSeated);
		
		
		//Step 3- Run the Scheduler
		
		assertTrue("Waiter's scheduler should have return true. But it didn't.", normalWaiter.pickAndExecuteAnAction());
		
		//Step 4 - Check postconditions
		assertTrue("Customer should have logged \"msgSitAtTable\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgSitAtTable"));
		assertTrue("waiter's customer should be not seated, it isnt", normalWaiter.CustomerList.get(0).state==waiterCustState.seated);
		
		assertEquals("Cook should have an empty event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),0, cook.log.size());
		
		assertEquals("Waiter should have onecustomer in its list. It doesn't.", normalWaiter.CustomerList.size(), 1);
		assertEquals("waiter's customer should be the mockCustomer, it isnt", normalWaiter.CustomerList.get(0).c,customer);
		assertEquals("waiter's customer should have table 0, it doesnt", normalWaiter.CustomerList.get(0).table,0);
		assertTrue("WaiterGui should have logged \"Received msgSitAtTable\" but didn't. His log reads instead: " 
				+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoLeaveCustomer"));
		
		//step 5
		normalWaiter.msgImReadyToOrder(customer);
		
		
		assertEquals("Cook should have an empty event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),0, cook.log.size());
		
		assertEquals("Waiter should have onecustomer in its list. It doesn't.", normalWaiter.CustomerList.size(), 1);
		assertEquals("waiter's customer should be the mockCustomer, it isnt", normalWaiter.CustomerList.get(0).c,customer);
		assertEquals("waiter's customer should have table 0, it doesnt", normalWaiter.CustomerList.get(0).table,0);
		assertTrue("waiter's customer should be not seated, it isnt", normalWaiter.CustomerList.get(0).state==waiterCustState.readyToOrder);
		
		//step 6-Run the scheduler
		assertTrue("Waiter's scheduler should have return true. But it didn't.", normalWaiter.pickAndExecuteAnAction());
		
		//check postconditions
		assertTrue("waiter's customer should be not seated, it isnt", normalWaiter.CustomerList.get(0).state==waiterCustState.waitingForWaiter);
		assertTrue("WaiterGui should have logged \"DoBringToTable\" but didn't. His log reads instead: " 
				+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoBringToTable"));
		
		
		assertTrue("Customer should have logged \"Asked what I would like\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Asked what I would like."));
		

		//Step 7 
		normalWaiter.msgHereIsMyChoice(customer, "steak");
		
		assertTrue("waiter's customer should be ordered, it isnt", normalWaiter.CustomerList.get(0).state==waiterCustState.ordered);
		
		assertTrue(" waiter's mycust choice should be steak, it isnt't",normalWaiter.CustomerList.get(0).choice=="steak");
		
		//Step 8 Run scheduler
		assertTrue("Waiter's scheduler should have return true. But it didn't.", normalWaiter.pickAndExecuteAnAction());
		
		//step 9 check postconditions
		
		assertTrue("waiter's customer should be ordered, it isnt", normalWaiter.CustomerList.get(0).state==waiterCustState.waitingForOrder);
		
		
		assertTrue("WaiterGui should have logged \"DoGoToCook\" but didn't. His log reads instead: " 
				+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoGoToCook"));
		

		assertTrue("cook should have logged \"received msgHereIsYourOrder\" but didn't. His log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("received msgHereIsYourOrder"));
		/*///////////////////PART 2/////////////////////////////////////////*/
		
		//Step 10-Check preconditions
				assertEquals("Waiter should have one customer in its list. It doesn't.", normalWaiter.CustomerList.size(), 1);
				assertEquals("MockCustomer2 should have an empty event log before the Waiters scheduler is called. Instead, the MockCustomer's event log reads: " + customer2.log.toString(), 0, customer2.log.size());
				assertTrue("Waiter's gui should have been set, it isn't." ,normalWaiter.getGui()==gui);
				//Step 2 - Add a Customer
				normalWaiter.msgSeatAtTable(customer2, 1);
				
				assertEquals("Customer2 should have an empty event log before waiter's scheduler is called. Instead it reads: " + customer2.log.toString(),0, customer2.log.size());
				
				assertEquals("Cook should have one event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),1, cook.log.size());
				
				assertEquals("Waiter should have onecustomer in its list. It doesn't.", normalWaiter.CustomerList.size(), 2);
				assertEquals("waiter's customer should be the mockCustomer2, it isnt", normalWaiter.CustomerList.get(1).c,customer2);
				assertEquals("waiter's customer should have table 1, it doesnt", normalWaiter.CustomerList.get(1).table,1);
				assertTrue("waiter's customer should be not seated, it isnt", normalWaiter.CustomerList.get(1).state==waiterCustState.notSeated);
				
				
				//Step 3- Run the Scheduler
				
				assertTrue("Waiter's scheduler should have return true. But it didn't.", normalWaiter.pickAndExecuteAnAction());
				
				//Step 4 - Check postconditions
				assertTrue("Customer should have logged \"msgSitAtTable\" but didn't. His log reads instead: " 
						+ customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Received msgSitAtTable"));
				assertTrue("waiter's customer should be not seated, it isnt", normalWaiter.CustomerList.get(1).state==waiterCustState.seated);
				
				assertEquals("Cook should have an one event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),1, cook.log.size());
				
				assertEquals("Waiter should have onecustomer in its list. It doesn't.", normalWaiter.CustomerList.size(), 2);
				assertEquals("waiter's customer should be the mockCustomer, it isnt", normalWaiter.CustomerList.get(1).c,customer2);
				assertEquals("waiter's customer should have table 0, it doesnt", normalWaiter.CustomerList.get(1).table,1);
				assertTrue("WaiterGui should have logged \"Received msgSitAtTable\" but didn't. His log reads instead: " 
						+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoLeaveCustomer"));
				
				//step 5
				normalWaiter.msgImReadyToOrder(customer2);
				
				
				assertEquals("Cook should have an one event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),1, cook.log.size());
				
				assertEquals("Waiter should have 2 customer in its list. It doesn't.", normalWaiter.CustomerList.size(), 2);
				assertEquals("waiter's customer should be the mockCustomer, it isnt", normalWaiter.CustomerList.get(1).c,customer2);
				assertEquals("waiter's customer should have table 0, it doesnt", normalWaiter.CustomerList.get(1).table,1);
				assertTrue("waiter's customer should be not seated, it isnt", normalWaiter.CustomerList.get(1).state==waiterCustState.readyToOrder);
				
				//step 6-Run the scheduler
				assertTrue("Waiter's scheduler should have return true. But it didn't.", normalWaiter.pickAndExecuteAnAction());
				
				//check postconditions
				assertTrue("waiter's customer should be not seated, it isnt", normalWaiter.CustomerList.get(1).state==waiterCustState.waitingForWaiter);
				assertTrue("WaiterGui should have logged \"DoBringToTable\" but didn't. His log reads instead: " 
						+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoBringToTable"));
				
				
				assertTrue("Customer should have logged \"Asked what I would like\" but didn't. His log reads instead: " 
						+ customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Asked what I would like."));
				

				//Step 7 
				normalWaiter.msgHereIsMyChoice(customer2, "steak");
				
				assertTrue("waiter's customer should be ordered, it isnt", normalWaiter.CustomerList.get(1).state==waiterCustState.ordered);
				
				assertTrue(" waiter's mycust choice should be steak, it isnt't",normalWaiter.CustomerList.get(1).choice=="steak");
				
				//Step 8 Run scheduler
				assertTrue("Waiter's scheduler should have return true. But it didn't.", normalWaiter.pickAndExecuteAnAction());
				
				//step 9 check postconditions
				
				assertTrue("waiter's customer should be ordered, it isnt", normalWaiter.CustomerList.get(1).state==waiterCustState.waitingForOrder);
				
				
				assertTrue("WaiterGui should have logged \"DoGoToCook\" but didn't. His log reads instead: " 
						+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoGoToCook"));
				

				assertTrue("cook should have logged \"received msgHereIsYourOrder\" but didn't. His log reads instead: " 
						+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("received msgHereIsYourOrder"));
		
	}
	
	

}
