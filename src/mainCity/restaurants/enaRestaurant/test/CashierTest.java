package mainCity.restaurants.enaRestaurant.test;

import role.enaRestaurant.EnaCashierRole;
import role.enaRestaurant.EnaCashierRole.Tab;
import role.enaRestaurant.EnaCashierRole.marketPay;
import role.enaRestaurant.EnaCashierRole.payStatus;
import mainCity.restaurants.enaRestaurant.test.mock.MockCustomer;
import mainCity.restaurants.enaRestaurant.test.mock.MockMarket;
import mainCity.restaurants.enaRestaurant.test.mock.MockWaiter;
//import restaurant.CashierAgent.cashierBillState;
//import restaurant.WaiterAgent.Bill;
import junit.framework.*;

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
	EnaCashierRole cashier;
	MockWaiter waiter;
	MockCustomer customer;
	MockCustomer customer2;
	MockCustomer customer3;
	MockMarket market;
	MockMarket market2;
	
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		//cashier = new EnaCashierRole("cashier");		
		customer = new MockCustomer("mockcustomer");	
		customer2 = new MockCustomer("debt");
		customer3 = new MockCustomer("poor");
		waiter = new MockWaiter("mockwaiter");
		market = new MockMarket("mockmarket");
		market2 = new MockMarket("mockmarket");
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testOneNormalCustomerScenario()
	{
		//setUp(); // runs first before this test!
	   customer.cashier = cashier;		
	   customer.waiter = waiter;
	   waiter.customer = customer;
	   waiter.cashier = cashier;
		
	//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.Tabs.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: " + cashier.log.toString(), 0, cashier.log.size());
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "  + waiter.log.toString(), 0, waiter.log.size());
		assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "  + customer.log.toString(), 0, customer.log.size());
		
	//step 1 of the test
		
			String ch = "lamb";	
			cashier.msgComputeBill(ch, customer);//send the message from a waiter
		
//check postconditions for step 1 and preconditions for step 2
			assertTrue("CashierAgent should have a log that reads \"Recieved message to compute the bill\" Instead, the Cashier's event log reads: "  + cashier.log.getLastLoggedEvent().toString(),cashier.log.containsString("Recieved message to compute the bill"));
			assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: " + waiter.log.toString(), 0, waiter.log.size());
			assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: " + waiter.log.toString(), 0, waiter.log.size());
			assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.Tabs.size(), 1);
			assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's ComputeBill), but didn't.", cashier.pickAndExecuteAnAction());
			assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's ComputeBill), but didn't.", cashier.pickAndExecuteAnAction());
			//assertTrue("MockWaiter should have logged an event for receiving \"HereIstheBill\" ,  but his last event logged reads instead: " + waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("message recieved to give the check to customer. Check is: $ " + 5.99));
//step 2 of the test
		
			cashier.msgPayment("lamb", 5.99, customer);
		
//check postConditions for step 2/ preconditions for step 3
		
			assertTrue("CashierTab should contain a tab with the right customer in it. It doesn't.", cashier.Tabs.get(0).c == customer);
			assertTrue("Cashier's tab should contain a bill of $5.99.It contains something else instead: $ " + cashier.Tabs.get(0).getPrice("lamb"), cashier.Tabs.get(0).getPrice("lamb") == 5.99);
			assertTrue("Cashier tab should contain a tab with state == paid. It doesn't.", cashier.Tabs.get(0).paymentStat == payStatus.paid);
			assertTrue("Cashier should have logged \"Received payment message\" but didn't. His log reads instead: "  + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received payment message"));
	
//step 3
			assertTrue("CashierBill should contain changeDue == 0.0. It contains something else instead: $" + (cashier.Tabs.get(0).check-5.99), (cashier.Tabs.get(0).check-5.99) == 0);
			assertTrue("Cashier's scheduler should have returned true (needs to react to customer's payment), but didn't.", cashier.pickAndExecuteAnAction());
			assertTrue("MockCustomer should have logged an event for receiving \"HereIsChange\" but his last event logged reads instead: " + customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Recieved change from cashier"));
	
//PostConditions for step 3
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.Tabs.size(), 0);		
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", cashier.pickAndExecuteAnAction());
		
	}//end one normal customer scenario
	
	public void testTwoNormalMarketScenerio()
	{
		   
		   market.cashier = cashier;
			
	//check preconditions
			assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.marketChecks.size(), 0);		
			assertEquals("CashierAgent should have an empty event log before the Cashier's restockBill is called. Instead, the Cashier's event log reads: " + cashier.log.toString(), 0, cashier.log.size());
			assertEquals("MockMarket should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "  + market.log.toString(), 0, market.log.size());
			
	//step 1 of the test
			
		Double reciept = 25.00;
		//cashier.msgHereIsMarketBill(null, reciept, null);//send the message from a market
			
	//check postconditions for step 1 and preconditions for step 2
				assertTrue("CashierAgent should have a log that reads \"Recieved message to pay the market\" Instead, the Cashier's event log reads: "  + cashier.log.getLastLoggedEvent().toString(),cashier.log.containsString("recieved message to pay the market"));
				//assertEquals("MockMarket should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: " + market.log.toString(), 0, market.log.size());
				assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.marketChecks.size(), 1);
				assertTrue("Cashier tab should contain a marketTab with state == pending. It doesn't.", cashier.marketChecks.get(0).mState == marketPay.pending);
				//assertTrue("CashierTab should contain a tab with the right market in it. It doesn't.", cashier.marketChecks.get(0).ma == market);
				assertTrue("CashierTab should contain a tab with the right amount in it. It doesn't.", cashier.marketChecks.get(0).checks == 25.00);
	
	//Step 2 of the test
				//assertTrue("Cashier's scheduler should have returned true (needs to react to markets restockBill), but didn't.", cashier.pickAndExecuteAnAction());
				//assertTrue("MockMarket should have a log that reads \"message recieved that cashier paid the bill\" Instead, the MockMarket's event log reads: "  + market.log.getLastLoggedEvent().toString(),market.log.containsString("message recieved that cashier paid bill"));

	//Step 2 postConditions
				//assertEquals("Cashier should have 0 marketbills in it. It doesn't.",cashier.marketChecks.size(), 0);		
				//assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", cashier.pickAndExecuteAnAction());	

	}
	
/*public void testThree2MarketBills()
{
	  market.cashier = cashier;
	  market2.cashier = cashier;
		
		//check preconditions
				assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.Tabs.size(), 0);		
				assertEquals("CashierAgent should have an empty event log before the Cashier's restockBill is called. Instead, the Cashier's event log reads: " + cashier.log.toString(), 0, cashier.log.size());
				assertEquals("MockMarket should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "  + market.log.toString(), 0, market.log.size());
				assertEquals("MockMarket should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "  + market2.log.toString(), 0, market2.log.size());

		//step 1 of the test
				
			Double reciept = 25.00;
			cashier.msgRestockBill(reciept, market);//send the message from two different markets to pay a bill
			cashier.msgRestockBill(reciept, market2);
			
				
		//check postconditions for step 1 and preconditions for step 2
					assertTrue("CashierAgent should have a log that reads \"Recieved message to pay the market\" Instead, the Cashier's event log reads: "  + cashier.log.getLastLoggedEvent().toString(),cashier.log.containsString("recieved message to pay the market"));
					assertEquals("MockMarket should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: " + market.log.toString(), 0, market.log.size());
					assertEquals("MockMarket should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: " + market2.log.toString(), 0, market2.log.size());
					assertEquals("Cashier should have 2 bill in it. It doesn't.", cashier.marketChecks.size(), 2);
				
					assertTrue("Cashier tab should contain a marketTab with state == pending. It doesn't.", cashier.marketChecks.get(0).mState == marketPay.pending);
					assertTrue("Cashier tab should contain a marketTab with state == pending. It doesn't.", cashier.marketChecks.get(1).mState == marketPay.pending);

					assertTrue("CashierTab should contain a tab with the right market in it. It doesn't.", cashier.marketChecks.get(0).ma == market);
					assertTrue("CashierTab should contain a tab with the right market in it. It doesn't.", cashier.marketChecks.get(1).ma == market2);

					assertTrue("CashierTab should contain a tab with the right amount in it. It doesn't.", cashier.marketChecks.get(0).checks == 25.00);
					assertTrue("CashierTab should contain a tab with the right amount in it. It doesn't.", cashier.marketChecks.get(1).checks == 25.00);

		
		//Step 2 of the test
					//assertTrue("Cashier's scheduler should have returned true (needs to react to markets restockBill), but didn't.", cashier.pickAndExecuteAnAction());
					assertTrue("MockMarket should have a log that reads \"message recieved that cashier paid the bill\" Instead, the MockMarket's event log reads: "  + market.log.getLastLoggedEvent().toString(),market.log.containsString("message recieved that cashier paid bill"));
					assertTrue("Cashier's scheduler should have returned true (needs to react to markets restockBill), but didn't.", cashier.pickAndExecuteAnAction());
					assertTrue("MockMarket should have a log that reads \"message recieved that cashier paid the bill\" Instead, the MockMarket's event log reads: "  + market2.log.getLastLoggedEvent().toString(),market2.log.containsString("message recieved that cashier paid bill"));

		//Step 2 postConditions
					assertEquals("Cashier should have 0 marketbills in it. It doesn't.",cashier.marketChecks.size(), 0);		
					assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", cashier.pickAndExecuteAnAction());	


}*/





public void testFourScenerio()
{
			//the customer is poor, does not have enough money to pay his tab and he gets a debt
	
	
	//setUp(); // runs first before this test!
   customer2.cashier = cashier;		
   customer2.waiter = waiter;
   waiter.customer = customer2;
   waiter.cashier = cashier;
	
//check preconditions
	assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.Tabs.size(), 0);		
	assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: " + cashier.log.toString(), 0, cashier.log.size());
	assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "  + waiter.log.toString(), 0, waiter.log.size());
	assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "  + waiter.log.toString(), 0, waiter.log.size());
	
//step 1 of the test
	
		String ch = "lamb";	
		cashier.msgComputeBill(ch, customer2);//send the message from a waiter
	
//check postconditions for step 1 and preconditions for step 2
		assertTrue("CashierAgent should have a log that reads \"Recieved message to compute the bill\" Instead, the Cashier's event log reads: "  + cashier.log.getLastLoggedEvent().toString(),cashier.log.containsString("Recieved message to compute the bill"));
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: " + waiter.log.toString(), 0, waiter.log.size());
		assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: " + waiter.log.toString(), 0, waiter.log.size());
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.Tabs.size(), 1);
		assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's ComputeBill), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's ComputeBill), but didn't.", cashier.pickAndExecuteAnAction());
		//assertTrue("MockWaiter should have logged an event for receiving \"HereIstheBill\" ,  but his last event logged reads instead: " + waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("message recieved to give the check to customer. Check is: $ " + 5.99));
//step 2 of the test
	
		cashier.msgPayment("lamb", 5.99, customer2);
	
//check postConditions for step 2/ preconditions for step 3
	
		assertTrue("CashierTab should contain a tab with the right customer in it. It doesn't.", cashier.Tabs.get(0).c == customer2);
		assertTrue("Cashier's tab should contain a bill of $5.99.It contains something else instead: $ " + cashier.Tabs.get(0).getPrice("lamb"), cashier.Tabs.get(0).getPrice("lamb") == 5.99);
		assertTrue("Cashier tab should contain a tab with state == paid. It doesn't.", cashier.Tabs.get(0).paymentStat == payStatus.paid);
		assertTrue("Cashier should have logged \"Received payment message\" but didn't. His log reads instead: "  + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received payment message"));

//step 3
		//assertTrue("CashierBill should contain changeDue == 0.0. It contains something else instead: $" + (cashier.Tabs.get(0).check-5.99), (cashier.Tabs.get(0).c.getCash()) == 0);
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's payment), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("MockCustomer should have logged an event for receiving \"HereIsChange\" but his last event logged reads instead: " + customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Recieved change from cashier"));

		
//PostConditions for step 3
	assertTrue("Customer should contain have debt of check balance. It contains something else instead: $" + (cashier.Tabs.get(0).c.getDebt()), (cashier.Tabs.get(0).c.getDebt()) == 2.99);

	assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.Tabs.size(), 1);	//the customers tab stays in the list because it has not been paid yet
	assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", cashier.pickAndExecuteAnAction());
	
}

public void testFiveScenerio()
{
	//two customers are added to the restaurant, the first one eats normally and pays the second one only has enough money to eat the cheapest meal so that is what he orders, the market reorders food and charges the cashier
	
	//setUp(); // runs first before this test!
	   customer3.cashier = cashier;		
	   customer3.waiter = waiter;
	   waiter.cashier = cashier;
	   customer.cashier = cashier;
	   customer.waiter = waiter;
	   market.cashier = cashier;	   
	   //waiter.customer = customer3;

	   
		
	//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.Tabs.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: " + cashier.log.toString(), 0, cashier.log.size());
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "  + waiter.log.toString(), 0, waiter.log.size());
		assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "  + customer.log.toString(), 0, customer.log.size());
		assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "  + customer3.log.toString(), 0, customer3.log.size());
	
	//step 1 of the test
		
			String ch = "lamb";				
			cashier.msgComputeBill("steak", customer);
			cashier.msgComputeBill("lamb", customer3);//send the message from a waiter
			
		
//check postconditions for step 1 and preconditions for step 2
			assertTrue("CashierAgent should have a log that reads \"Recieved message to compute the bill\" Instead, the Cashier's event log reads: "  + cashier.log.getLastLoggedEvent().toString(),cashier.log.containsString("Recieved message to compute the bill"));
			assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: " + waiter.log.toString(), 0, waiter.log.size());
			assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: " + customer.log.toString(), 0, customer.log.size());
			assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: " + customer3.log.toString(), 0, customer3.log.size());

			assertEquals("Cashier should have 2 bills in it. It doesn't.", cashier.Tabs.size(), 2);
			assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's ComputeBill), but didn't.", cashier.pickAndExecuteAnAction());
			assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's ComputeBill), but didn't.", cashier.pickAndExecuteAnAction());

			assertTrue("MockWaiter should have logged an event for receiving \"HereIstheBill\" ,  but his last event logged reads instead: " + waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("message recieved to give the check to customer. Check is: $ " + 15.99));
			
	//step 2 of the test	
			cashier.msgPayment("steak", 20.00, customer);
			cashier.msgPayment("lamb", 5.99, customer3);
			cashier.msgRestockBill(43.00, market);
		
//check postConditions for step 2/ preconditions for step 3
			
			assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.marketChecks.size(), 1);
			assertTrue("Cashier tab should contain a marketTab with state == pending. It doesn't.", cashier.marketChecks.get(0).mState == marketPay.pending);
			assertTrue("CashierTab should contain a tab with the right market in it. It doesn't.", cashier.marketChecks.get(0).ma == market);
			assertTrue("CashierTab should contain a tab with the right amount in it. It doesn't.", cashier.marketChecks.get(0).checks == 43.00);
			
			assertTrue("CashierTab should contain a tab with the right customer in it. It doesn't.", cashier.Tabs.get(0).c == customer);
			assertTrue("CashierTab should contain a tab with the right customer in it. It doesn't.", cashier.Tabs.get(1).c == customer3);
			assertTrue("Cashier's tab should contain a bill of $5.99.It contains something else instead: $ " + cashier.Tabs.get(0).getPrice("steak"), cashier.Tabs.get(0).getPrice("steak") == 15.99);
			//assertTrue("Cashier's tab should contain a bill of $5.99.It contains something else instead: $ " + cashier.Tabs.get(1).getPrice("lamb"), cashier.Tabs.get(1).getPrice("lamb") == 5.99);
			assertTrue("Cashier tab should contain a tab with state == paid. It doesn't.", cashier.Tabs.get(0).paymentStat == payStatus.paid);
			assertTrue("Cashier tab should contain a tab with state == paid. It doesn't.", cashier.Tabs.get(1).paymentStat == payStatus.paid);
			assertTrue("Cashier should have logged \"Received payment message\" but didn't. His log reads instead: "  + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received payment message"));
	
//step 3
			assertTrue("CashierBill should contain changeDue == 0.0. It contains something else instead: $" + (20.00 - cashier.Tabs.get(0).check), (20.00 - cashier.Tabs.get(0).check) == 4.01 );
			assertTrue("CashierBill should contain changeDue == 0.0. It contains something else instead: $" + (5.99 - cashier.Tabs.get(1).check), (5.99 - cashier.Tabs.get(1).check-5.99) == 0);
			//assertTrue("Cashier's scheduler should have returned true (needs to react to customer's payment), but didn't.", cashier.pickAndExecuteAnAction());
			//assertTrue("MockMarket should have a log that reads \"message recieved that cashier paid the bill\" Instead, the MockMarket's event log reads: "  + market.log.getLastLoggedEvent().toString(),market.log.containsString("message recieved that cashier paid bill"));
		//	assertTrue("Cashier's scheduler should have returned true (needs to react to customer's payment), but didn't.", cashier.pickAndExecuteAnAction());
			//assertTrue("MockCustomer should have logged an event for receiving \"HereIsChange\" but his last event logged reads instead: " + customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Recieved change from cashier"));
			//assertTrue("Cashier's scheduler should have returned true (needs to react to customer's payment), but didn't.", cashier.pickAndExecuteAnAction());
			//assertTrue("MockCustomer should have logged an event for receiving \"HereIsChange\" but his last event logged reads instead: " + customer3.log.getLastLoggedEvent().toString(), customer3.log.containsString("Recieved change from cashier"));
	
//PostConditions for step 3
		//assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.Tabs.size(), 0);		
		//assertEquals("Cashier should have 0 marketbills in it. It doesn't.",cashier.marketChecks.size(), 0);		
		//assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", cashier.pickAndExecuteAnAction());
		
}	

public void testSixScenerio()
{
	//this scenerio tests just a regular customer eating and paying, while the market bills the customer and it cashier interacts with both the market and the customer/waiter
	
	   customer.cashier = cashier;		
	   customer.waiter = waiter;
	   waiter.customer = customer;
	   waiter.cashier = cashier;
	   market.cashier = cashier;
	//check preconditions
			assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.Tabs.size(), 0);	
			assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.marketChecks.size(), 0);		
			assertEquals("CashierAgent should have an empty event log before the Cashier's restockBill is called. Instead, the Cashier's event log reads: " + cashier.log.toString(), 0, cashier.log.size());
			//assertEquals("MockMarket should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockMarket's event log reads: "  + market.log.toString(), 0, market.log.size());
			assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "  + waiter.log.toString(), 0, waiter.log.size());
			assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "  + customer.log.toString(), 0, customer.log.size());
			
		//step 1 of the test
			
				String ch = "lamb";	
				cashier.msgComputeBill(ch, customer);//send the message from a waiter
				//cashier.msgRestockBill(25.00, market);
			
	//check postconditions for step 1 and preconditions for step 2
				assertTrue("CashierAgent should have a log that reads \"Recieved message to compute the bill\" Instead, the Cashier's event log reads: "  + cashier.log.getLastLoggedEvent().toString(),cashier.log.containsString("Recieved message to compute the bill"));
				assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: " + waiter.log.toString(), 0, waiter.log.size());
				assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: " + waiter.log.toString(), 0, waiter.log.size());
				assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.Tabs.size(), 1);
				//assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.marketChecks.size(), 1);
				//assertTrue("Cashier tab should contain a marketTab with state == pending. It doesn't.", cashier.marketChecks.get(0).mState == marketPay.pending);
				//assertTrue("CashierTab should contain a tab with the right market in it. It doesn't.", cashier.marketChecks.get(0).ma == market);
				//assertTrue("CashierTab should contain a tab with the right amount in it. It doesn't.", cashier.marketChecks.get(0).checks == 25.00);
				//assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's ComputeBill), but didn't.", cashier.pickAndExecuteAnAction());
				assertTrue("Cashier's scheduler should have returned true (needs to react to waiter's ComputeBill), but didn't.", cashier.pickAndExecuteAnAction());
	
//step 2 of the test
				cashier.msgPayment("lamb", 5.99, customer);
			
	//check postConditions for step 2/ preconditions for step 3
				
				assertTrue("CashierTab should contain a tab with the right customer in it. It doesn't.", cashier.Tabs.get(0).c == customer);
				assertTrue("Cashier's tab should contain a bill of $5.99.It contains something else instead: $ " + cashier.Tabs.get(0).getPrice("lamb"), cashier.Tabs.get(0).getPrice("lamb") == 5.99);
				assertTrue("Cashier's market should contain a bill.It contains something else instead: $ " + cashier.Tabs.get(0).getPrice("lamb"), cashier.Tabs.get(0).getPrice("lamb") == 5.99);

				assertTrue("Cashier tab should contain a tab with state == paid. It doesn't.", cashier.Tabs.get(0).paymentStat == payStatus.paid);
				assertTrue("Cashier should have logged \"Received payment message\" but didn't. His log reads instead: "  + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received payment message"));
		
	//step 3
				assertTrue("CashierBill should contain changeDue == 0.0. It contains something else instead: $" + (5.99 - cashier.Tabs.get(0).check), (5.99 - cashier.Tabs.get(0).check) == 0);
				assertTrue("Cashier's scheduler should have returned true (needs to react to customer's payment), but didn't.", cashier.pickAndExecuteAnAction());
				assertTrue("MockCustomer should have logged an event for receiving \"HereIsChange\" but his last event logged reads instead: " + customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Recieved change from cashier"));
		
	//PostConditions for step 3
			assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.Tabs.size(), 0);
			assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.marketChecks.size(), 0);

			assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", cashier.pickAndExecuteAnAction());

}
	
}
