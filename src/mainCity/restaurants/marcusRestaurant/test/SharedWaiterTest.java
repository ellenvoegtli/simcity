package mainCity.restaurants.marcusRestaurant.test;

import role.marcusRestaurant.MarcusSharedWaiterRole;
import mainCity.PersonAgent;
import mainCity.restaurants.marcusRestaurant.MarcusTable;
import mainCity.restaurants.marcusRestaurant.sharedData.RevolvingStand;
import mainCity.restaurants.marcusRestaurant.test.mock.*;
import junit.framework.*;

public class SharedWaiterTest extends TestCase {
	//these are instantiated for each test separately via the setUp() method.
	MarcusSharedWaiterRole sharedWaiter;
	MockWaiterGui waiterGui;
	
    RevolvingStand stand = new RevolvingStand();
	
    MockHost host;
    MockCashier cashier;
    
	MockCustomer customer1;
	MockCustomer customer2;
	
	MarcusTable table1;
	MarcusTable table2;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();
		PersonAgent base = new PersonAgent("Waiter");
		sharedWaiter = new MarcusSharedWaiterRole(base, base.getName());
		base.addRole(PersonAgent.ActionType.work, sharedWaiter);
		sharedWaiter.setActive();

		waiterGui = new MockWaiterGui("Waiter");
		sharedWaiter.setGui(waiterGui);
		waiterGui.setWaiter(sharedWaiter);
		
		sharedWaiter.setStand(stand);
		
		host = new MockHost("Host");
		cashier = new MockCashier("cashier");
		sharedWaiter.setHost(host);
		sharedWaiter.setCashier(cashier);
		
		customer1 = new MockCustomer("mockcustomer1");		
		customer2 = new MockCustomer("mockcustomer2");		
		table1 = new MarcusTable(1);
		table2 = new MarcusTable(2);
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testOneNormalCustomerScenario() {
		System.out.println("Beginning One Normal Customer Scenario");
		
		//Setting up one customer
		assertEquals("Waiter should have one customer in its list. It doesn't.", sharedWaiter.getCustomerCount(), 0);
		assertEquals("MockCustomer should have an empty event log before the Waiters scheduler is called. Instead, the MockCustomer's event log reads: " + customer1.getLog().toString(), 0, customer1.getLog().size());
		customer1.waiter = sharedWaiter;
		sharedWaiter.msgSeatAtTable(customer1, table1);
		table1.setOccupant(customer1);
		
		assertEquals("Waiter should have one customer in its list. It doesn't.", sharedWaiter.getCustomerCount(), 1);
		sharedWaiter.msgImReadyToOrder(customer1);
		
		assertTrue("Waiter's scheduler should have returned true (there's a pending customer waiting to order), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("WaiterGui's log should have been 'Going to table' to take an order, it instead read: " + waiterGui.getLog().getLastLoggedEvent().toString(), waiterGui.getLog().containsString("Going to table"));
		sharedWaiter.msgHereIsMyChoice(customer1, "Swiss");
		
		assertTrue("The order stand should be empty. It isn't", stand.isEmpty());
		assertTrue("Waiter's scheduler should have returned true (there's an order to take to the stand), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("WaiterGui's log should have been 'Going to stand to post order', it instead read: " + waiterGui.getLog().getLastLoggedEvent().toString(), waiterGui.getLog().containsString("Going to stand to post order"));
		assertFalse("The order stand should now have 1 order in it. It doesn't", stand.isEmpty());
		
		assertEquals("The stand's order should be swiss. It wasn't", stand.remove().getChoice(), "Swiss");
		assertTrue("The order stand should be empty. It isn't", stand.isEmpty());

		sharedWaiter.msgOrderIsReady(table1.getTableNumber(), "Swiss");		
		assertTrue("Waiter's scheduler should have returned true (it's picking up the order from the cook's post), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Customer1's log should have been 'Received my order of Swiss', it instead read: " + customer1.getLog().getLastLoggedEvent().toString(), customer1.getLog().containsString("Received my order of Swiss"));
		
		sharedWaiter.msgReadyForCheck(customer1);
		assertTrue("Waiter's scheduler should have returned true (it's picking up the check from the cashier), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Cashier's log should have been 'Received message from waiter to computer bill', it instead read: " + cashier.getLog().getLastLoggedEvent().toString(), cashier.getLog().containsString("Received message from waiter to compute bill for table 1"));

		sharedWaiter.msgHereIsCheck(16.00, table1.getTableNumber());
		assertTrue("Waiter's scheduler should have returned true (it's picking up the check from the cashier), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Customer1's log should have been 'Received the check', it instead read: " + customer1.getLog().getLastLoggedEvent().toString(), customer1.getLog().containsString("Received the check"));

		sharedWaiter.msgLeavingTable(customer1);
		assertTrue("Waiter's scheduler should have returned true (it's telling host the table is clear), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Host's log should have been '1 is now clear', it instead read: " + host.getLog().getLastLoggedEvent().toString(), host.getLog().containsString("1 is now clear"));

		assertEquals("Waiter's customer list is now empty", 0, sharedWaiter.getCustomerCount());
		
		System.out.println("End Normative Scenario");
	}//end one normal customer scenario
	
	public void testTwoNormalCustomerScenario() {
		System.out.println("Beginning Two Normal Customer Scenario");
		
		assertEquals("Waiter should have no customers in its list. It doesn't.", 0, sharedWaiter.getCustomerCount());
		assertEquals("MockCustomer1 should have an empty event log before the Waiters scheduler is called. Instead, the MockCustomer's event log reads: " + customer1.getLog().toString(), 0, customer1.getLog().size());
		customer1.waiter = sharedWaiter;
		sharedWaiter.msgSeatAtTable(customer1, table1);
		table1.setOccupant(customer1);
		
		assertEquals("Waiter should have one customer in its list. It doesn't.", sharedWaiter.getCustomerCount(), 1);
		
		assertEquals("Waiter should have one customer in its list. It doesn't.", 1, sharedWaiter.getCustomerCount());
		assertEquals("MockCustomer2 should have an empty event log before the Waiters scheduler is called. Instead, the MockCustomer's event log reads: " + customer2.getLog().toString(), 0, customer2.getLog().size());
		customer2.waiter = sharedWaiter;
		sharedWaiter.msgSeatAtTable(customer2, table2);
		table2.setOccupant(customer2);
		
		assertEquals("Waiter should have two customers in its list. It doesn't.", sharedWaiter.getCustomerCount(), 2);
		
		sharedWaiter.msgImReadyToOrder(customer1);
		assertTrue("Waiter's scheduler should have returned true (there's a pending customer waiting to order), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("WaiterGui's log should have been 'Going to table' to take an order, it instead read: " + waiterGui.getLog().getLastLoggedEvent().toString(), waiterGui.getLog().containsString("Going to table"));
		sharedWaiter.msgHereIsMyChoice(customer1, "Swiss");
		
		assertTrue("The order stand should be empty. It isn't", stand.isEmpty());
		assertTrue("Waiter's scheduler should have returned true (there's an order to take to the stand), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("WaiterGui's log should have been 'Going to stand to post order', it instead read: " + waiterGui.getLog().getLastLoggedEvent().toString(), waiterGui.getLog().containsString("Going to stand to post order"));
		assertFalse("The order stand should now have 1 order in it. It doesn't", stand.isEmpty());
		
		sharedWaiter.msgImReadyToOrder(customer2);
		assertTrue("Waiter's scheduler should have returned true (there's a pending customer waiting to order), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("WaiterGui's log should have been 'Going to table' to take an order, it instead read: " + waiterGui.getLog().getLastLoggedEvent().toString(), waiterGui.getLog().containsString("Going to table"));
		sharedWaiter.msgHereIsMyChoice(customer2, "American");
		
		assertFalse("The order stand should not be empty. It is", stand.isEmpty());
		assertTrue("Waiter's scheduler should have returned true (there's an order to take to the stand), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("WaiterGui's log should have been 'Going to stand to post order', it instead read: " + waiterGui.getLog().getLastLoggedEvent().toString(), waiterGui.getLog().containsString("Going to stand to post order"));
		assertFalse("The order stand should now have 2 orders in it. It doesn't", stand.isEmpty());
		
		assertEquals("The stand's order should be swiss. It wasn't", stand.remove().getChoice(), "Swiss");
		assertFalse("The order stand should have one order still in it. It isn't", stand.isEmpty());

		sharedWaiter.msgOrderIsReady(table1.getTableNumber(), "Swiss");		
		assertTrue("Waiter's scheduler should have returned true (it's picking up the order from the cook's post), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Customer2's log should have been 'Received my order of Swiss', it instead read: " + customer1.getLog().getLastLoggedEvent().toString(), customer1.getLog().containsString("Received my order of Swiss"));
		
		assertEquals("The stand's order should be american. It wasn't", stand.remove().getChoice(), "American");
		assertTrue("The order stand should now be empty. It isn't", stand.isEmpty());

		sharedWaiter.msgOrderIsReady(table2.getTableNumber(), "American");		
		assertTrue("Waiter's scheduler should have returned true (it's picking up the order from the cook's post), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Customer2's log should have been 'Received my order of American', it instead read: " + customer2.getLog().getLastLoggedEvent().toString(), customer2.getLog().containsString("Received my order of American"));
		
		sharedWaiter.msgReadyForCheck(customer1);
		assertTrue("Waiter's scheduler should have returned true (it's picking up the check from the cashier), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Cashier's log should have been 'Received message from waiter to computer bill', it instead read: " + cashier.getLog().getLastLoggedEvent().toString(), cashier.getLog().containsString("Received message from waiter to compute bill for table 1"));

		sharedWaiter.msgHereIsCheck(16.00, table1.getTableNumber());
		assertTrue("Waiter's scheduler should have returned true (it's picking up the check from the cashier), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Customer1's log should have been 'Received the check', it instead read: " + customer1.getLog().getLastLoggedEvent().toString(), customer1.getLog().containsString("Received the check"));

		sharedWaiter.msgLeavingTable(customer1);
		assertTrue("Waiter's scheduler should have returned true (it's telling host the table is clear), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Host's log should have been '1 is now clear', it instead read: " + host.getLog().getLastLoggedEvent().toString(), host.getLog().containsString("1 is now clear"));

		assertEquals("Waiter's customer list should now be 1", 1, sharedWaiter.getCustomerCount());
		
		sharedWaiter.msgReadyForCheck(customer2);
		assertTrue("Waiter's scheduler should have returned true (it's picking up the check from the cashier), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Cashier's log should have been 'Received message from waiter to computer bill', it instead read: " + cashier.getLog().getLastLoggedEvent().toString(), cashier.getLog().containsString("Received message from waiter to compute bill for table 1"));

		sharedWaiter.msgHereIsCheck(16.00, table2.getTableNumber());
		assertTrue("Waiter's scheduler should have returned true (it's picking up the check from the cashier), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Customer1's log should have been 'Received the check', it instead read: " + customer2.getLog().getLastLoggedEvent().toString(), customer2.getLog().containsString("Received the check"));

		sharedWaiter.msgLeavingTable(customer2);
		assertTrue("Waiter's scheduler should have returned true (it's telling host the table is clear), but didn't.", sharedWaiter.pickAndExecuteAnAction());
		assertTrue("Host's log should have been '1 is now clear', it instead read: " + host.getLog().getLastLoggedEvent().toString(), host.getLog().containsString("1 is now clear"));

		assertEquals("Waiter's customer list should now be empty", 0, sharedWaiter.getCustomerCount());
		
		System.out.println("End Two Normative Scenario");
	}//end two normal customer scenario
}
