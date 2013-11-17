package mainCity.restaurants.jeffersonrestaurant.test;

import mainCity.restaurants.jeffersonrestaurant.CashierAgent;
import mainCity.restaurants.jeffersonrestaurant.HostAgent;
import mainCity.restaurants.jeffersonrestaurant.MarketAgent;
import mainCity.restaurants.jeffersonrestaurant.WaiterAgent;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.MockCustomer;
import restaurant.test.mock.MockMarket;
import restaurant.test.mock.MockWaiter;
import junit.framework.*;


public class HostTest extends TestCase{

	HostAgent host;
	MockCustomer customer;
	WaiterAgent waiter;
	
	
	
	
	public void setUp() throws Exception{
		super.setUp();		
		host = new HostAgent("testHost");	
		customer = new MockCustomer("mockcustomer");	
		waiter = new WaiterAgent("Dan");
		host.waiters.add(waiter);
	}	
	
	
	
	public void testOneNormalCustomerScenario()
	{
		//setUp() runs first before this test!
		
		customer.host = host; 			
		
		//check preconditions
		assertEquals("Host should have 0 customers",host.waitingCustomers.size(), 0);		
		
		//step 1 of the test
		//public Bill(Cashier, Customer, int tableNum, double price) {
		host.msgIWantFood(customer);
		//cashier.HereIsBill(bill);//send the message from a waiter

		//check postconditions for step 1 and preconditions for step 2
		
		
		assertEquals("Host should have 1 waiting customer in it. It doesn't.", host.waitingCustomers.size(), 1);
		
		assertTrue("Host's scheduler should have returned true (no actions to do on a bill from a waiter), but didn't.", host.pickAndExecuteAnAction());
		/*
		
		assertEquals("MockCustomer should have an empty event log after the Host's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		*/
		//step 2 of the test
		
		/*
		//check postconditions for step 2 / preconditions for step 3
		assertTrue("CashierBill should contain a bill with state == customerApproached. It doesn't.",
				cashier.bills.get(0).state == cashierBillState.customerApproached);
		
		assertTrue("Cashier should have logged \"Received ReadyToPay\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received ReadyToPay"));

		assertTrue("CashierBill should contain a bill of price = $7.98. It contains something else instead: $" 
				+ cashier.bills.get(0).bill.netCost, cashier.bills.get(0).bill.netCost == 7.98);
		
		assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", 
					cashier.bills.get(0).bill.customer == customer);
		
		
		//step 3
		//NOTE: I called the scheduler in the assertTrue statement below (to succintly check the return value at the same time)
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's ReadyToPay), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 3 / preconditions for step 4
		assertTrue("MockCustomer should have logged an event for receiving \"HereIsYourTotal\" with the correct balance, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received HereIsYourTotal from cashier. Total = 7.98"));
	
			
		assertTrue("Cashier should have logged \"Received HereIsMyPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received HereIsMyPayment"));
		
		
		assertTrue("CashierBill should contain changeDue == 0.0. It contains something else instead: $" 
				+ cashier.bills.get(0).changeDue, cashier.bills.get(0).changeDue == 0);
		
		
		
		//step 4
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's ReadyToPay), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 4
		assertTrue("MockCustomer should have logged an event for receiving \"HereIsYourChange\" with the correct change, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received HereIsYourChange from cashier. Change = 0.0"));
	
		
		assertTrue("CashierBill should contain a bill with state == done. It doesn't.",
				cashier.bills.get(0).state == cashierBillState.done);
		
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
	
		
*/	
	}
	
	
	
	
}
