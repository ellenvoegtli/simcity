package mainCity.restaurants.jeffersonrestaurant.test;


import role.jeffersonRestaurant.JeffersonSharedDataWaiterRole;
import role.jeffersonRestaurant.JeffersonWaiterRole.waiterCustState;
import mainCity.PersonAgent;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Customer;
import mainCity.restaurants.jeffersonrestaurant.sharedData.RevolvingStand;
import mainCity.restaurants.jeffersonrestaurant.test.mock.EventLog;
import mainCity.restaurants.jeffersonrestaurant.test.mock.MockCook;
import mainCity.restaurants.jeffersonrestaurant.test.mock.MockCustomer;
import mainCity.restaurants.jeffersonrestaurant.test.mock.MockWaiterGui;
import junit.framework.TestCase;

public class SharedWaiterTest extends TestCase {
	JeffersonSharedDataWaiterRole sharedWaiter;
	MockCook cook;
	MockCustomer customer;
	MockCustomer customer2;
	MockWaiterGui gui;
	
	RevolvingStand stand = new RevolvingStand();
	protected void setUp() throws Exception {
		super.setUp();
		PersonAgent base = new PersonAgent("sharedWaiter");
		sharedWaiter = new JeffersonSharedDataWaiterRole(base, base.getName());
		
		base.addRole(PersonAgent.ActionType.work, sharedWaiter);
		gui = new MockWaiterGui("gui");
		customer = new MockCustomer("mockcustomer");	
		customer2= new MockCustomer("mockcustomer2");
		cook = new MockCook("mockcook");
		gui.log=new EventLog();
		customer.log = new EventLog();
		customer2.log = new EventLog();
		cook.log = new EventLog();
		sharedWaiter.setGui(gui);
		gui.waiter=sharedWaiter;
		sharedWaiter.stand=stand;
		
	}
	
	public void testOneNormalCustomerScenario() {
		System.out.println("Beginning One Normal Customer Scenario");
		
		//Step 1-Check preconditions
		assertEquals("Waiter should have zero customer in its list. It doesn't.", sharedWaiter.CustomerList.size(), 0);
		assertEquals("MockCustomer should have an empty event log before the Waiters scheduler is called. Instead, the MockCustomer's event log reads: " + customer.log.toString(), 0, customer.log.size());
		assertTrue("Waiter's gui should have been set, it isn't." ,sharedWaiter.getGui()==gui);
		//Step 2 - Add a Customer
		sharedWaiter.msgSeatAtTable(customer, 0);
		
		assertEquals("Customer should have an empty event log before waiter's scheduler is called. Instead it reads: " + customer.log.toString(),0, customer.log.size());
		
		assertEquals("Cook should have an empty event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),0, cook.log.size());
		
		assertEquals("Waiter should have onecustomer in its list. It doesn't.", sharedWaiter.CustomerList.size(), 1);
		assertEquals("waiter's customer should be the mockCustomer, it isnt", sharedWaiter.CustomerList.get(0).c,customer);
		assertEquals("waiter's customer should have table 0, it doesnt", sharedWaiter.CustomerList.get(0).table,0);
		assertTrue("waiter's customer should be not seated, it isnt", sharedWaiter.CustomerList.get(0).state==waiterCustState.notSeated);
		
		
		//Step 3- Run the Scheduler
		
		assertTrue("Waiter's scheduler should have return true. But it didn't.", sharedWaiter.pickAndExecuteAnAction());
		
		//Step 4 - Check postconditions
		assertTrue("Customer should have logged \"msgSitAtTable\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgSitAtTable"));
		assertTrue("waiter's customer should be not seated, it isnt", sharedWaiter.CustomerList.get(0).state==waiterCustState.seated);
		
		assertEquals("Cook should have an empty event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),0, cook.log.size());
		
		assertEquals("Waiter should have onecustomer in its list. It doesn't.", sharedWaiter.CustomerList.size(), 1);
		assertEquals("waiter's customer should be the mockCustomer, it isnt", sharedWaiter.CustomerList.get(0).c,customer);
		assertEquals("waiter's customer should have table 0, it doesnt", sharedWaiter.CustomerList.get(0).table,0);
		assertTrue("WaiterGui should have logged \"Received msgSitAtTable\" but didn't. His log reads instead: " 
				+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoLeaveCustomer"));
		
		//step 5
		sharedWaiter.msgImReadyToOrder(customer);
		
		
		assertEquals("Cook should have an empty event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),0, cook.log.size());
		
		assertEquals("Waiter should have onecustomer in its list. It doesn't.", sharedWaiter.CustomerList.size(), 1);
		assertEquals("waiter's customer should be the mockCustomer, it isnt", sharedWaiter.CustomerList.get(0).c,customer);
		assertEquals("waiter's customer should have table 0, it doesnt", sharedWaiter.CustomerList.get(0).table,0);
		assertTrue("waiter's customer should be not seated, it isnt", sharedWaiter.CustomerList.get(0).state==waiterCustState.readyToOrder);
		
		//step 6-Run the scheduler
		assertTrue("Waiter's scheduler should have return true. But it didn't.", sharedWaiter.pickAndExecuteAnAction());
		
		//check postconditions
		assertTrue("waiter's customer should be not seated, it isnt", sharedWaiter.CustomerList.get(0).state==waiterCustState.waitingForWaiter);
		assertTrue("WaiterGui should have logged \"DoBringToTable\" but didn't. His log reads instead: " 
				+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoBringToTable"));
		
		
		assertTrue("Customer should have logged \"Asked what I would like\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Asked what I would like."));
		

		//Step 7 
		sharedWaiter.msgHereIsMyChoice(customer, "steak");
		
		assertTrue("waiter's customer should be ordered, it isnt", sharedWaiter.CustomerList.get(0).state==waiterCustState.ordered);
		
		assertTrue(" waiter's mycust choice should be steak, it isnt't",sharedWaiter.CustomerList.get(0).choice=="steak");
		
		//Step 8 Run scheduler
		assertTrue("Waiter's scheduler should have return true. But it didn't.", sharedWaiter.pickAndExecuteAnAction());
		
		//step 9 check postconditions
		
		assertTrue("waiter's customer should be ordered, it isnt", sharedWaiter.CustomerList.get(0).state==waiterCustState.waitingForOrder);
		
		
		assertTrue("WaiterGui should have logged \"DoGoToCook\" but didn't. His log reads instead: " 
				+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoGoToCook"));
		assertFalse("Stand shouldn't be full, it is.",stand.isFull());
		assertFalse("Stand shouldn't be empty, it is.",stand.isEmpty());
		assertTrue("Choice should be steak, it isn't.",stand.theData.get(0).getChoice()=="steak");
		assertTrue("Table should be 0, it isn't.",stand.theData.get(0).getTable()==0);
		assertTrue("Waiter should be sharedWaiter in unit under test.",stand.theData.get(0).getWaiter()==sharedWaiter);
	}
	
	
	public void testTwoNormalCustomerScenario() {
		System.out.println("Beginning Two Normal Customer Scenario");
		
		//Step 1-Check preconditions
		assertEquals("Waiter should have zero customer in its list. It doesn't.", sharedWaiter.CustomerList.size(), 0);
		assertEquals("MockCustomer should have an empty event log before the Waiters scheduler is called. Instead, the MockCustomer's event log reads: " + customer.log.toString(), 0, customer.log.size());
		assertTrue("Waiter's gui should have been set, it isn't." ,sharedWaiter.getGui()==gui);
		//Step 2 - Add a Customer
		sharedWaiter.msgSeatAtTable(customer, 0);
		
		assertEquals("Customer should have an empty event log before waiter's scheduler is called. Instead it reads: " + customer.log.toString(),0, customer.log.size());
		
		assertEquals("Cook should have an empty event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),0, cook.log.size());
		
		assertEquals("Waiter should have onecustomer in its list. It doesn't.", sharedWaiter.CustomerList.size(), 1);
		
		assertEquals("waiter's customer should be the mockCustomer, it isnt", sharedWaiter.CustomerList.get(0).c,customer);
		
		assertEquals("waiter's customer should have table 0, it doesnt", sharedWaiter.CustomerList.get(0).table,0);
		
		assertTrue("waiter's customer should be not seated, it isnt", sharedWaiter.CustomerList.get(0).state==waiterCustState.notSeated);
		
		
		//Step 3- Run the Scheduler
		
		assertTrue("Waiter's scheduler should have return true. But it didn't.", sharedWaiter.pickAndExecuteAnAction());
		
		//Step 4 - Check postconditions
		assertTrue("Customer should have logged \"msgSitAtTable\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgSitAtTable"));
		assertTrue("waiter's customer should be not seated, it isnt", sharedWaiter.CustomerList.get(0).state==waiterCustState.seated);
		
		assertEquals("Cook should have an empty event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),0, cook.log.size());
		
		assertEquals("Waiter should have onecustomer in its list. It doesn't.", sharedWaiter.CustomerList.size(), 1);
		assertEquals("waiter's customer should be the mockCustomer, it isnt", sharedWaiter.CustomerList.get(0).c,customer);
		assertEquals("waiter's customer should have table 0, it doesnt", sharedWaiter.CustomerList.get(0).table,0);
		assertTrue("WaiterGui should have logged \"Received msgSitAtTable\" but didn't. His log reads instead: " 
				+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoLeaveCustomer"));
		
		//step 5
		sharedWaiter.msgImReadyToOrder(customer);
		
		
		assertEquals("Cook should have an empty event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),0, cook.log.size());
		
		assertEquals("Waiter should have onecustomer in its list. It doesn't.", sharedWaiter.CustomerList.size(), 1);
		assertEquals("waiter's customer should be the mockCustomer, it isnt", sharedWaiter.CustomerList.get(0).c,customer);
		assertEquals("waiter's customer should have table 0, it doesnt", sharedWaiter.CustomerList.get(0).table,0);
		assertTrue("waiter's customer should be not seated, it isnt", sharedWaiter.CustomerList.get(0).state==waiterCustState.readyToOrder);
		
		//step 6-Run the scheduler
		assertTrue("Waiter's scheduler should have return true. But it didn't.", sharedWaiter.pickAndExecuteAnAction());
		
		//check postconditions
		assertTrue("waiter's customer should be not seated, it isnt", sharedWaiter.CustomerList.get(0).state==waiterCustState.waitingForWaiter);
		assertTrue("WaiterGui should have logged \"DoBringToTable\" but didn't. His log reads instead: " 
				+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoBringToTable"));
		
		
		assertTrue("Customer should have logged \"Asked what I would like\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Asked what I would like."));
		

		//Step 7 
		sharedWaiter.msgHereIsMyChoice(customer, "steak");
		
		assertTrue("waiter's customer should be ordered, it isnt", sharedWaiter.CustomerList.get(0).state==waiterCustState.ordered);
		
		assertTrue(" waiter's mycust choice should be steak, it isnt't",sharedWaiter.CustomerList.get(0).choice=="steak");
		
		//Step 8 Run scheduler
		assertTrue("Waiter's scheduler should have return true. But it didn't.", sharedWaiter.pickAndExecuteAnAction());
		
		//step 9 check postconditions
		
		assertTrue("waiter's customer should be ordered, it isnt", sharedWaiter.CustomerList.get(0).state==waiterCustState.waitingForOrder);
		
		
		assertTrue("WaiterGui should have logged \"DoGoToCook\" but didn't. His log reads instead: " 
				+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoGoToCook"));
		
		assertFalse("Stand shouldn't be full, it is.",stand.isFull());
		
		assertFalse("Stand shouldn't be empty, it is.",stand.isEmpty());
		
		assertTrue("Choice should be steak, it isn't.",stand.theData.get(0).getChoice()=="steak");
		
		assertTrue("Table should be 0, it isn't.",stand.theData.get(0).getTable()==0);
		
		assertTrue("Waiter should be sharedWaiter in unit under test.",stand.theData.get(0).getWaiter()==sharedWaiter);
		
		/*////////////////////////SECOND CUSTOMER////////////////////////////*/

		
		//Step 10-Check preconditions
		assertEquals("Waiter should have one customer in its list. It doesn't.", sharedWaiter.CustomerList.size(), 1);
		
		assertEquals("MockCustomer should have an empty event log before the Waiters scheduler is called. Instead, the MockCustomer's event log reads: " + customer2.log.toString(), 0, customer2.log.size());
		
		assertTrue("Waiter's gui should have been set, it isn't." ,sharedWaiter.getGui()==gui);
		
		//Step 11 - Add a Customer
		sharedWaiter.msgSeatAtTable(customer2, 1);
		
		assertEquals("Customer should have an empty event log before waiter's scheduler is called. Instead it reads: " + customer2.log.toString(),0, customer2.log.size());
		
		assertEquals("Cook should have an empty event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),0, cook.log.size());
		
		assertEquals("Waiter should have onecustomer in its list. It doesn't.", sharedWaiter.CustomerList.size(), 2);
		
		assertEquals("waiter's customer should be the mockCustomer, it isnt", sharedWaiter.CustomerList.get(1).c,customer2);
		
		assertEquals("waiter's customer should have table 0, it doesnt", sharedWaiter.CustomerList.get(1).table,1);
		
		assertTrue("waiter's customer should be not seated, it isnt", sharedWaiter.CustomerList.get(1).state==waiterCustState.notSeated);
		
		
		//Step 12- Run the Scheduler
		
		assertTrue("Waiter's scheduler should have return true. But it didn't.", sharedWaiter.pickAndExecuteAnAction());
		
		//Step 13 - Check postconditions
		assertTrue("Customer should have logged \"msgSitAtTable\" but didn't. His log reads instead: " 
				+ customer2.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgSitAtTable"));
		assertTrue("waiter's customer should be not seated, it isnt", sharedWaiter.CustomerList.get(1).state==waiterCustState.seated);
		
		assertEquals("Cook should have an empty event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),0, cook.log.size());
		
		
		assertEquals("Waiter should have two customer in its list. It doesn't.", sharedWaiter.CustomerList.size(), 2);
		
		assertEquals("waiter's customer should be the mockCustomer, it isnt", sharedWaiter.CustomerList.get(1).c,customer2);
		
		assertEquals("waiter's customer should have table 1, it doesnt", sharedWaiter.CustomerList.get(1).table,1);
		
		assertTrue("WaiterGui should have logged \"Received msgSitAtTable\" but didn't. His log reads instead: " 
				+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoLeaveCustomer"));
		
		//step 14
		sharedWaiter.msgImReadyToOrder(customer2);
		
		
		assertEquals("Cook should have an empty event log before waiter's scheduler is called. Instead it reads: " + cook.log.toString(),0, cook.log.size());
		
		
		assertEquals("Waiter should have two customer in its list. It doesn't.", sharedWaiter.CustomerList.size(), 2);
		
		
		assertEquals("waiter's customer should be the mockCustomer, it isnt", sharedWaiter.CustomerList.get(1).c,customer2);
		
		assertEquals("waiter's customer should have table 1, it doesnt", sharedWaiter.CustomerList.get(1).table,1);
		
		assertTrue("waiter's customer should be not seated, it isnt", sharedWaiter.CustomerList.get(1).state==waiterCustState.readyToOrder);
		
		//step 15-Run the scheduler
		assertTrue("Waiter's scheduler should have return true. But it didn't.", sharedWaiter.pickAndExecuteAnAction());
		
		//check postconditions
		assertTrue("waiter's customer should be not seated, it isnt", sharedWaiter.CustomerList.get(1).state==waiterCustState.waitingForWaiter);
		
		assertTrue("WaiterGui should have logged \"DoBringToTable\" but didn't. His log reads instead: " 
				+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoBringToTable"));
		
		
		assertTrue("Customer should have logged \"Asked what I would like\" but didn't. His log reads instead: " 
				+ customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Asked what I would like."));
		

		//Step 16
		sharedWaiter.msgHereIsMyChoice(customer2, "steak");
		
		assertTrue("waiter's customer should be ordered, it isnt", sharedWaiter.CustomerList.get(1).state==waiterCustState.ordered);
		
		assertTrue(" waiter's mycust choice should be steak, it isnt't",sharedWaiter.CustomerList.get(1).choice=="steak");
		
		//Step 17 Run scheduler
		assertTrue("Waiter's scheduler should have return true. But it didn't.", sharedWaiter.pickAndExecuteAnAction());
		
		//step 18 check postconditions
		
		assertTrue("waiter's customer should be ordered, it isnt", sharedWaiter.CustomerList.get(1).state==waiterCustState.waitingForOrder);
		
		
		assertTrue("WaiterGui should have logged \"DoGoToCook\" but didn't. His log reads instead: " 
				+ gui.log.getLastLoggedEvent().toString(), gui.log.containsString("called DoGoToCook"));
		
		assertFalse("Stand shouldn't be full, it is.",stand.isFull());
		
		assertFalse("Stand shouldn't be empty, it is.",stand.isEmpty());
		
		assertTrue("Choice should be steak, it isn't.",stand.theData.get(1).getChoice()=="steak");
		
		assertTrue("Table should be 0, it isn't.",stand.theData.get(1).getTable()==1);
		
		assertTrue("Waiter should be sharedWaiter in unit under test.",stand.theData.get(1).getWaiter()==sharedWaiter);
		

		
	}
	
	
	
	
	
	
	
	
	

}
