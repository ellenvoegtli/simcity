package mainCity.restaurants.jeffersonrestaurant.test;

import role.jeffersonRestaurant.JeffersonCashierRole;
import role.marcusRestaurant.MarcusCashierRole;
import junit.framework.TestCase;
import mainCity.PersonAgent;
import mainCity.restaurants.jeffersonrestaurant.test.mock.MockCustomer;
import mainCity.restaurants.jeffersonrestaurant.test.mock.MockMarket;
import mainCity.restaurants.jeffersonrestaurant.test.mock.MockWaiter;
import mainCity.restaurants.jeffersonrestaurant.test.mock.EventLog;


/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 * @author Monroe Ekilah
 */
public class CashierTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	JeffersonCashierRole cashier;
	MockWaiter waiter;
	MockCustomer customer;
	MockMarket market1;
	MockMarket market2;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();
		PersonAgent base = new PersonAgent("Cashier");
		cashier = new JeffersonCashierRole(base, base.getName());
		base.addRole(PersonAgent.ActionType.work, cashier);
		
		customer = new MockCustomer("mockcustomer");		
		waiter = new MockWaiter("mockwaiter");
		market1 = new MockMarket("mockmarket");
		market2 = new MockMarket("mockmarket");
		market1.cashier=cashier;
		market2.cashier=cashier;
		market1.log= new EventLog();
		market2.log = new EventLog();
		waiter.log = new EventLog();
		customer.log = new EventLog();
		
	}	
	/**
	 * This tests the cashier under 
	 * very simple terms: one customer is ready to pay the exact bill.
	 */
	/*
	public void testOneOrderOneMarketBillPaidFull(){
		
		cashier.profits=100;
		//preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);	
		
		//Step 1
		cashier.msgHereIsMarketBill(10, market1);
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.bills.size(), 1);
		assertFalse("Bill should contain a bill with paid==false. It doesn't.",
				cashier.bills.get(0).paid);
		assertTrue("Bills should contain a bill with amount == 10. It doesn't.",
				cashier.bills.get(0).amount==10);
			
		
		//Step 2
		assertTrue("Cashier's scheduler should have returned true , but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertTrue("Bills should contain a bill with paid==true. It doesn't.",
				cashier.bills.get(0).paid);
			
		assertTrue("Market should have logged \"Recieved money from cashier\" but didn't. His log reads instead: " 
				+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Recieved money from cashier"));
		
		//Step3
		assertFalse("Cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have $90. He doesnt", cashier.profits, 90.0);
	}
	
	public void testOneOrderTwoMarketsBillsPaidFull(){
		
		cashier.profits=100;
		//preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);	
		
		//Step 1
		cashier.msgHereIsMarketBill(10, market1);
		cashier.msgHereIsMarketBill(10, market2);
		assertEquals("Cashier should have 2 bills in it. It doesn't.", cashier.bills.size(), 2);
		assertFalse("Bill should contain a bill with paid==false. It doesn't.",
				cashier.bills.get(0).paid);
		assertFalse("Bill should contain a bill with paid==false. It doesn't.",
				cashier.bills.get(1).paid);
		assertTrue("Bills should contain a bill with amount == 10. It doesn't.",
				cashier.bills.get(0).amount==10);
		assertTrue("Bills should contain a bill with amount == 10. It doesn't.",
				cashier.bills.get(1).amount==10);	
		
		//Step 2
		//First Scheduler run to pay market1
		assertTrue("Cashier's scheduler should have returned true , but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertTrue("Bills should contain a bill with paid==true. It doesn't.",
				cashier.bills.get(0).paid);
			
		assertTrue("Market should have logged \"Recieved money from cashier\" but didn't. His log reads instead: " 
				+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Recieved money from cashier"));
		
		
		//Step 3
		assertTrue("Cashier's scheduler should have returned true , but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the second time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the second time. Instead, the MockCustomer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertTrue("Bills should contain a bill with paid==true. It doesn't.",
				cashier.bills.get(1).paid);
			
		assertTrue("Market should have logged \"Recieved money from cashier\" but didn't. His log reads instead: " 
				+ market2.log.getLastLoggedEvent().toString(), market1.log.containsString("Recieved money from cashier"));
		
		//Step 4
		assertFalse("Cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have $80. He doesnt", cashier.profits, 80.0);
	}
	*/
	public void testNormalCustomerScenario(){		
		cashier.profits=100;
		//preconditions
		assertEquals("Cashier should have 0 checks in it. It doesn't.",cashier.checks.size(), 0);
		
		//Step 1
		cashier.msgCustWantsCheck(1, "steak", waiter);
		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("market1 should have an empty event log before the Cashier's scheduler is called. Instead, the market1's event log reads: "
				+ market1.log.toString(), 0, market1.log.size());
		assertEquals("market2 should have an empty event log before the Cashier's scheduler is called. Instead, the market2's event log reads: "
				+ market2.log.toString(), 0, market2.log.size());
		assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals("Cashier should have 1 check in it. It doesn't.", cashier.checks.size(), 1);
		assertFalse("Checks should contain a check with processed==false. It doesn't.",
				cashier.checks.get(0).processed);
		assertFalse("Checks should contain a check with paid==false. It doesn't.",
				cashier.checks.get(0).paid);
		assertFalse("Checks should contain a check with complete==false. It doesn't.",
				cashier.checks.get(0).complete);
		
		//Step 2
		assertTrue("Cashier's scheduler should have returned true , but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Checks should contain a check with processed==true. It doesn't.",
				cashier.checks.get(0).processed);
		/*
		assertTrue("Waiter should have logged \"Told by cashier check is printed\" but didn't. His log reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Told by cashier check is printed"));
		*/		
		assertFalse("Cashier's scheduler should have returned false , but didn't.", cashier.pickAndExecuteAnAction());
		
		
		//Step 3
		cashier.msgHereisPayment(1, waiter, 15.99);
		assertEquals("Cashier should have 1 check in it. It doesn't.", cashier.checks.size(), 1);
		assertTrue("Checks should contain a check with processed==true. It doesn't.",
				cashier.checks.get(0).processed);
		assertTrue("Checks should contain a check with paid==true. It doesn't.",
				cashier.checks.get(0).paid);
		assertFalse("Checks should contain a check with complete==false. It doesn't.",
				cashier.checks.get(0).complete);
		
		//Step 4
		assertTrue("Cashier's scheduler should have returned true , but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Checks should contain a check with processed==true. It doesn't.",
				cashier.checks.get(0).processed);
		assertTrue("Checks should contain a check with paid==true. It doesn't.",
				cashier.checks.get(0).paid);
		assertTrue("Checks should contain a check with complete==true. It doesn't.",
				cashier.checks.get(0).complete);
		assertTrue("Waiter should have logged \"Payment is complete\" but didn't. His log reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Payment is complete"));
		assertFalse("Cashier's scheduler should have returned false , but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have $115.99. He doesnt", cashier.profits, 115.99);
		
	}
	
	public void testBrokeCustomerScenario(){		
		cashier.profits=100;
		//preconditions
		assertEquals("Cashier should have 0 checks in it. It doesn't.",cashier.checks.size(), 0);
		
		//Step 1
		cashier.msgCustWantsCheck(1, "steak", waiter);
		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("market1 should have an empty event log before the Cashier's scheduler is called. Instead, the market1's event log reads: "
				+ market1.log.toString(), 0, market1.log.size());
		assertEquals("market2 should have an empty event log before the Cashier's scheduler is called. Instead, the market2's event log reads: "
				+ market2.log.toString(), 0, market2.log.size());
		assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals("Cashier should have 1 check in it. It doesn't.", cashier.checks.size(), 1);
		assertFalse("Checks should contain a check with processed==false. It doesn't.",
				cashier.checks.get(0).processed);
		assertFalse("Checks should contain a check with paid==false. It doesn't.",
				cashier.checks.get(0).paid);
		assertFalse("Checks should contain a check with complete==false. It doesn't.",
				cashier.checks.get(0).complete);
		
		//Step 2
		assertTrue("Cashier's scheduler should have returned true , but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Checks should contain a check with processed==true. It doesn't.",
				cashier.checks.get(0).processed);
		/*
		assertTrue("Waiter should have logged \"Told by cashier check is printed\" but didn't. His log reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Told by cashier check is printed"));
		*/		
		assertFalse("Cashier's scheduler should have returned false , but didn't.", cashier.pickAndExecuteAnAction());
		
		
		//Step 3
		cashier.msgHereisPayment(1, waiter, 0);
		assertEquals("Cashier should have 1 check in it. It doesn't.", cashier.checks.size(), 1);
		assertTrue("Checks should contain a check with processed==true. It doesn't.",
				cashier.checks.get(0).processed);
		assertTrue("Checks should contain a check with paid==true. It doesn't.",
				cashier.checks.get(0).paid);
		assertFalse("Checks should contain a check with complete==false. It doesn't.",
				cashier.checks.get(0).complete);
		
		//Step 4
		assertTrue("Cashier's scheduler should have returned true , but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Checks should contain a check with processed==true. It doesn't.",
				cashier.checks.get(0).processed);
		assertTrue("Checks should contain a check with paid==true. It doesn't.",
				cashier.checks.get(0).paid);
		assertTrue("Checks should contain a check with complete==true. It doesn't.",
				cashier.checks.get(0).complete);
		assertTrue("Waiter should have logged \"Payment is complete\" but didn't. His log reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Payment is complete"));
		assertFalse("Cashier's scheduler should have returned false , but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have $100. He doesnt", cashier.profits, 100.0);
		
	}
/*	
	public void testOneMarketOrderandOneCustomer(){
		cashier.profits=100;
		//preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);	
		
		//Step 1
		cashier.msgHereIsMarketBill(10, market1);
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.bills.size(), 1);
		assertFalse("Bill should contain a bill with paid==false. It doesn't.",
				cashier.bills.get(0).paid);
		assertTrue("Bills should contain a bill with amount == 10. It doesn't.",
				cashier.bills.get(0).amount==10);
			
		
		//Step 2
		assertTrue("Cashier's scheduler should have returned true , but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertTrue("Bills should contain a bill with paid==true. It doesn't.",
				cashier.bills.get(0).paid);
			
		assertTrue("Market should have logged \"Recieved money from cashier\" but didn't. His log reads instead: " 
				+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Recieved money from cashier"));
		assertEquals("Cashier should have $90. He doesnt", cashier.profits, 90.0);
		
		
		//Step3
		assertFalse("Cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());
		
		
		//Step4
		assertEquals("Cashier should have 0 checks in it. It doesn't.",cashier.checks.size(), 0);
		
		cashier.msgCustWantsCheck(1, "steak", waiter);
		
		assertTrue("Market should have logged \"Recieved money from cashier\" but didn't. His log reads instead: " 
				+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Recieved money from cashier"));
		assertEquals("market1 should have one event log before the Cashier's scheduler is called. Instead, the market1's event log reads: "
				+ market1.log.toString(), 1, market1.log.size());
		assertEquals("market2 should have an empty event log before the Cashier's scheduler is called. Instead, the market2's event log reads: "
				+ market2.log.toString(), 0, market2.log.size());
		assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals("Cashier should have 1 check in it. It doesn't.", cashier.checks.size(), 1);
		assertFalse("Checks should contain a check with processed==false. It doesn't.",
				cashier.checks.get(0).processed);
		assertFalse("Checks should contain a check with paid==false. It doesn't.",
				cashier.checks.get(0).paid);
		assertFalse("Checks should contain a check with complete==false. It doesn't.",
				cashier.checks.get(0).complete);
		
		//Step 5
		assertTrue("Cashier's scheduler should have returned true , but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Checks should contain a check with processed==true. It doesn't.",
				cashier.checks.get(0).processed);
		/*
		assertTrue("Waiter should have logged \"Told by cashier check is printed\" but didn't. His log reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Told by cashier check is printed"));
			
		assertFalse("Cashier's scheduler should have returned false , but didn't.", cashier.pickAndExecuteAnAction());
		
		
		//Step 6
		cashier.msgHereisPayment(1, waiter, 15.99);
		assertEquals("Cashier should have 1 check in it. It doesn't.", cashier.checks.size(), 1);
		assertTrue("Checks should contain a check with processed==true. It doesn't.",
				cashier.checks.get(0).processed);
		assertTrue("Checks should contain a check with paid==true. It doesn't.",
				cashier.checks.get(0).paid);
		assertFalse("Checks should contain a check with complete==false. It doesn't.",
				cashier.checks.get(0).complete);
		
		//Step 7
		assertTrue("Cashier's scheduler should have returned true , but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Checks should contain a check with processed==true. It doesn't.",
				cashier.checks.get(0).processed);
		assertTrue("Checks should contain a check with paid==true. It doesn't.",
				cashier.checks.get(0).paid);
		assertTrue("Checks should contain a check with complete==true. It doesn't.",
				cashier.checks.get(0).complete);
		assertTrue("Waiter should have logged \"Payment is complete\" but didn't. His log reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Payment is complete"));
		assertFalse("Cashier's scheduler should have returned false , but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have $105.99. He doesnt", cashier.profits, 105.99);
		
	}
*/	
/*	
public void testCashierBalanceLessThanMarketBill(){
		
		cashier.profits=0;
		//preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);	
		
		//Step 1
		cashier.msgHereIsMarketBill(10, market1);
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.bills.size(), 1);
		assertFalse("Bill should contain a bill with paid==false. It doesn't.",
				cashier.bills.get(0).paid);
		assertTrue("Bills should contain a bill with amount == 10. It doesn't.",
				cashier.bills.get(0).amount==10);
			
		
		//Step 2
		assertTrue("Cashier's scheduler should have returned true , but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertTrue("Bills should contain a bill with paid==true. It doesn't.",
				cashier.bills.get(0).paid);
			
		assertTrue("Market should have logged \"Recieved money from cashier\" but didn't. His log reads instead: " 
				+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Recieved money from cashier"));
		
		//Step3
		assertFalse("Cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have -$10. He doesnt", cashier.profits, -10.0);
	}
*/
}
