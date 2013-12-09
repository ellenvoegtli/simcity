package mainCity.restaurants.EllenRestaurant.test;

import role.ellenRestaurant.EllenSharedDataWaiterRole;
import junit.framework.TestCase;
import mainCity.PersonAgent;
import mainCity.restaurants.EllenRestaurant.sharedData.RevolvingStand;
import mainCity.restaurants.EllenRestaurant.test.mock.*;
import role.ellenRestaurant.EllenHostRole.Table;
import role.ellenRestaurant.EllenWaiterRole.WaiterState;

public class SharedWaiterTest extends TestCase {
	EllenSharedDataWaiterRole waiter;
	MockWaiterGui waiterGui;
	
	RevolvingStand stand = new RevolvingStand();
	MockHost host;
	MockCook cook;
	MockCashier cashier;
	MockCustomer customer1;
	MockCustomer customer2;
	Table table1;
	Table table2;
	
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();
		PersonAgent base = new PersonAgent("Waiter");
		waiter = new EllenSharedDataWaiterRole(base, base.getName());
		base.addRole(PersonAgent.ActionType.work, waiter);
		waiter.setActive();

		waiterGui = new MockWaiterGui("Waiter");
		waiter.setGui(waiterGui);
		waiterGui.setWaiter(waiter);
		
		waiter.setStand(stand);
		
		host = new MockHost("Host");
		cashier = new MockCashier("cashier");
		cook = new MockCook("Cook");
		waiter.setCook(cook);
		waiter.setHost(host);
		waiter.setCashier(cashier);
		
		customer1 = new MockCustomer("mockcustomer1");		
		customer2 = new MockCustomer("mockcustomer2");	
		//table1 = host.(new Table(1));
		host.addTable(table1);
		host.addTable(table2);
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testOneNormalCustomerScenario() {
		System.out.println("Beginning One Normal Customer Scenario");
		
		//Setting up one customer
		assertEquals("Waiter should have one customer in its list. It doesn't.", waiter.getMyCustomers().size(), 0);
		assertEquals("MockCustomer should have an empty event log before the Waiters scheduler is called. Instead, the MockCustomer's event log reads: " + customer1.log.toString(), 0, customer1.log.size());
		customer1.waiter = waiter;
		customer1.cashier = cashier;
		waiter.setState(WaiterState.doingNothing);
		
		waiter.msgPleaseSeatCustomer(customer1, 0, 0, 1);
		//table1.setOccupied(true);
		//table1.setOccupant(customer1);
		assertEquals("Waiter should have one customer in its list. It doesn't.", waiter.getMyCustomers().size(), 1);
		
		//waiter.msgAtStart();
		//waiter.msgAtTable();
		assertTrue("Waiter's scheduler should have returned true (there's a pending customer waiting to be seated), but didn't.", waiter.pickAndExecuteAnAction());
		
		assertTrue("Customer1's log should have been 'Received msgFollowMe from Waiter', it instead read: " + customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received msgFollowMe from Waiter"));
		assertTrue("WaiterGui's log should have been 'Going to table 1' to take an order, it instead read: " + waiterGui.log.getLastLoggedEvent().toString(), waiterGui.log.containsString("Going to table 1"));
		
		waiter.setState(WaiterState.doingNothing);
		waiter.msgReadyToOrder(customer1);
		
		//waiter.msgAtTable();
		assertTrue("Waiter's scheduler should have returned true (there's a pending customer waiting to order), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("WaiterGui's log should have been 'Going to table 1' to take an order, it instead read: " + waiterGui.log.getLastLoggedEvent().toString(), waiterGui.log.containsString("Going to table 1"));
		assertTrue("Customer1's log should have been 'Received msgWhatDoYouWant', it instead read: " + customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received msgWhatDoYouWant"));
		waiter.msgHereIsChoice(customer1, "Pizza");
		
		assertTrue("The order stand should be empty. It isn't", stand.isEmpty());
		
		//waiter.msgAtCook();
		waiter.setState(WaiterState.doingNothing);
		assertTrue("Waiter's scheduler should have returned true (there's an order to take to the stand), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("WaiterGui's log should have been 'Going to stand to post order', it instead read: " + waiterGui.log.getLastLoggedEvent().toString(), waiterGui.log.containsString("Going to stand to post order"));
		assertFalse("The order stand should now have 1 order in it. It doesn't", stand.isEmpty());
		
		assertEquals("The stand's order should be pizza. It wasn't", stand.remove().getChoice(), "Pizza");
		assertTrue("The order stand should be empty. It isn't", stand.isEmpty());

		waiter.msgOrderDoneCooking("Pizza", 1);	
		waiter.setState(WaiterState.doingNothing);
		waiter.myCustomers.get(0).setTable(1);
		
		waiter.msgAtTable();
		assertTrue("Waiter's scheduler should have returned true (it's picking up the order from the cook's post), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("Customer1's log should have been 'Received my order of Pizza', it instead read: " + customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received my order of Pizza"));
		//waiter.msgReadyForCheck(customer1);
		//assertTrue("Waiter's scheduler should have returned true (it's picking up the check from the cashier), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("Cashier's log should have been 'Received msgComputeBill from Waiter', it instead read: " + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgComputeBill from Waiter"));

		waiter.msgHereIsCheck(16.00, customer1);
		waiter.setState(WaiterState.doingNothing);
		assertFalse("Waiter's scheduler should have returned false, but didn't.", waiter.pickAndExecuteAnAction());
		
		waiter.msgIWantMyCheck(customer1, "Pizza");
		waiter.setState(WaiterState.doingNothing);
		assertTrue("Waiter's scheduler should have returned true (needs to react to customer wanting check), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("Customer1's log should have been 'Received HereIsCheck from waiter. Amount = 16.0', it instead read: " + customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received HereIsCheck from waiter. Amount = 16.0"));

		waiter.msgDoneAndLeaving(customer1);
		waiter.setState(WaiterState.doingNothing);
		assertTrue("Waiter's scheduler should have returned true (it's telling host the table is clear), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("Host's log should have been 'Received msgTableFree for table 1', it instead read: " + host.log.getLastLoggedEvent().toString(), host.log.containsString("Received msgTableFree for table 1"));

		assertEquals("Waiter's customer list is now empty", 0, waiter.getMyCustomers().size());
		
		System.out.println("End Normative Scenario");
	}//end one normal customer scenario
	
	public void testTwoNormalCustomerScenario() {
		System.out.println("Beginning Two Normal Customer Scenario");
		
		//Setting up one customer
		assertEquals("Waiter should have one customer in its list. It doesn't.", waiter.getMyCustomers().size(), 0);
		assertEquals("MockCustomer1 should have an empty event log before the Waiters scheduler is called. Instead, the MockCustomer's event log reads: " + customer1.log.toString(), 0, customer1.log.size());
		customer1.waiter = waiter;
		customer1.cashier = cashier;
		waiter.setState(WaiterState.doingNothing);
		
		waiter.msgPleaseSeatCustomer(customer1, 0, 0, 1);
		waiter.myCustomers.get(0).setTable(1);
		
		assertEquals("Waiter should have one customer in its list. It doesn't.", waiter.getMyCustomers().size(), 1);
		assertEquals("MockCustomer2 should have an empty event log before the Waiters scheduler is called. Instead, the MockCustomer's event log reads: " + customer2.log.toString(), 0, customer2.log.size());
		customer2.waiter = waiter;
		customer2.cashier = cashier;
		waiter.setState(WaiterState.doingNothing);
		
		waiter.msgPleaseSeatCustomer(customer2, 0, 0, 2);
		waiter.myCustomers.get(1).setTable(2);
		assertEquals("Waiter should have two customers in its list. It doesn't.", waiter.getMyCustomers().size(), 2);

		assertTrue("Waiter's scheduler should have returned true (there's a pending customer waiting to be seated), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("Customer1's log should have been 'Received msgFollowMe from Waiter', it instead read: " + customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received msgFollowMe from Waiter"));
		assertTrue("WaiterGui's log should have been 'Going to table 1' to seat customer, it instead read: " + waiterGui.log.getLastLoggedEvent().toString(), waiterGui.log.containsString("Going to table 1"));
		
		waiter.setState(WaiterState.doingNothing);
		assertTrue("Waiter's scheduler should have returned true (there's a pending customer waiting to be seated), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("Customer2's log should have been 'Received msgFollowMe from Waiter', it instead read: " + customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Received msgFollowMe from Waiter"));
		assertTrue("WaiterGui's log should have been 'Going to table 2' to seat customer, it instead read: " + waiterGui.log.getLastLoggedEvent().toString(), waiterGui.log.containsString("Going to table 2"));
		
		
		waiter.setState(WaiterState.doingNothing);
		waiter.msgReadyToOrder(customer1);
		
		assertTrue("Waiter's scheduler should have returned true (there's a pending customer waiting to order), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("WaiterGui's log should have been 'Going to table 1' to take an order, it instead read: " + waiterGui.log.getLastLoggedEvent().toString(), waiterGui.log.containsString("Going to table 1"));
		assertTrue("Customer1's log should have been 'Received msgWhatDoYouWant', it instead read: " + customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received msgWhatDoYouWant"));
		waiter.msgHereIsChoice(customer1, "Pizza");
		
		assertTrue("The order stand should be empty. It isn't", stand.isEmpty());
		waiter.setState(WaiterState.doingNothing);
		assertTrue("Waiter's scheduler should have returned true (there's an order to take to the stand), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("WaiterGui's log should have been 'Going to stand to post order', it instead read: " + waiterGui.log.getLastLoggedEvent().toString(), waiterGui.log.containsString("Going to stand to post order"));
		assertFalse("The order stand should now have 1 order in it. It doesn't", stand.isEmpty());
		
		
		waiter.setState(WaiterState.doingNothing);
		waiter.msgReadyToOrder(customer2);
		
		assertTrue("Waiter's scheduler should have returned true (there's a pending customer waiting to order), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("WaiterGui's log should have been 'Going to table 2' to take an order, it instead read: " + waiterGui.log.getLastLoggedEvent().toString(), waiterGui.log.containsString("Going to table 2"));
		assertTrue("Customer2's log should have been 'Received msgWhatDoYouWant', it instead read: " + customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Received msgWhatDoYouWant"));
		waiter.msgHereIsChoice(customer2, "Pasta");
		
		assertFalse("The order stand should not be empty. It is", stand.isEmpty());
		waiter.setState(WaiterState.doingNothing);
		assertTrue("Waiter's scheduler should have returned true (there's an order to take to the stand), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("WaiterGui's log should have been 'Going to stand to post order', it instead read: " + waiterGui.log.getLastLoggedEvent().toString(), waiterGui.log.containsString("Going to stand to post order"));
		assertFalse("The order stand should now have 2 orders in it. It doesn't", stand.isEmpty());
		
		assertEquals("The stand's order should be pizza. It wasn't", stand.remove().getChoice(), "Pizza");
		assertFalse("The order stand should have one order still in it. It isn't", stand.isEmpty());
		
		//assertTrue("The order stand should be empty. It isn't", stand.isEmpty());
		waiter.msgOrderDoneCooking("Pizza", 1);	
		waiter.setState(WaiterState.doingNothing);
		//waiter.myCustomers.get(0).setTable(1);
		
		waiter.msgAtTable();
		assertTrue("Waiter's scheduler should have returned true (it's picking up the order from the cook's post), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("Customer1's log should have been 'Received my order of Pizza', it instead read: " + customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received my order of Pizza"));
		assertTrue("Cashier's log should have been 'Received msgComputeBill from Waiter', it instead read: " + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgComputeBill from Waiter"));

		
		assertEquals("The stand's order should be pasta. It wasn't", stand.remove().getChoice(), "Pasta");
		assertTrue("The order stand should have 0 orders in it. It doesn't", stand.isEmpty());
		
		waiter.msgOrderDoneCooking("Pasta", 2);	
		waiter.setState(WaiterState.doingNothing);
		//waiter.myCustomers.get(0).setTable(2);
		
		waiter.msgAtTable();
		assertTrue("Waiter's scheduler should have returned true (it's picking up the order from the cook's post), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("Customer2's log should have been 'Received my order of Pasta', it instead read: " + customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Received my order of Pasta"));
		assertTrue("Cashier's log should have been 'Received msgComputeBill from Waiter', it instead read: " + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgComputeBill from Waiter"));

		
		//checks
		waiter.msgHereIsCheck(16.00, customer1);
		waiter.setState(WaiterState.doingNothing);
		assertFalse("Waiter's scheduler should have returned false, but didn't.", waiter.pickAndExecuteAnAction());
		
		waiter.msgIWantMyCheck(customer1, "Pizza");
		waiter.setState(WaiterState.doingNothing);
		assertTrue("Waiter's scheduler should have returned true (needs to react to customer wanting check), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("Customer1's log should have been 'Received HereIsCheck from waiter. Amount = 16.0', it instead read: " + customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received HereIsCheck from waiter. Amount = 16.0"));

		//2nd
		waiter.msgHereIsCheck(16.00, customer2);
		waiter.setState(WaiterState.doingNothing);
		assertFalse("Waiter's scheduler should have returned false, but didn't.", waiter.pickAndExecuteAnAction());
		
		waiter.msgIWantMyCheck(customer2, "Pasta");
		waiter.setState(WaiterState.doingNothing);
		assertTrue("Waiter's scheduler should have returned true (needs to react to customer wanting check), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("Customer2's log should have been 'Received HereIsCheck from waiter. Amount = 16.0', it instead read: " + customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Received HereIsCheck from waiter. Amount = 16.0"));

		
		//leaving
		waiter.msgDoneAndLeaving(customer1);
		waiter.setState(WaiterState.doingNothing);
		assertTrue("Waiter's scheduler should have returned true (it's telling host the table is clear), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("Host's log should have been 'Received msgTableFree for table 1', it instead read: " + host.log.getLastLoggedEvent().toString(), host.log.containsString("Received msgTableFree for table 1"));

		assertEquals("Waiter's customer list still has 1 customer left", 1, waiter.getMyCustomers().size());
		
		//2nd
		waiter.msgDoneAndLeaving(customer2);
		waiter.setState(WaiterState.doingNothing);
		assertTrue("Waiter's scheduler should have returned true (it's telling host the table is clear), but didn't.", waiter.pickAndExecuteAnAction());
		assertTrue("Host's log should have been 'Received msgTableFree for table 2', it instead read: " + host.log.getLastLoggedEvent().toString(), host.log.containsString("Received msgTableFree for table 2"));

		assertEquals("Waiter's customer list is now empty", 0, waiter.getMyCustomers().size());
		
		System.out.println("End Normative Scenario");
	}//end one normal customer scenario
}