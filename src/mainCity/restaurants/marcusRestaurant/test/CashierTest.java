package mainCity.restaurants.marcusRestaurant.test;

import role.marcusRestaurant.MarcusCashierRole;
import role.marcusRestaurant.MarcusCashierRole.BillState;
import mainCity.Person;
import mainCity.PersonAgent;
import mainCity.restaurants.marcusRestaurant.MarcusTable;
import mainCity.restaurants.marcusRestaurant.test.mock.*;
import junit.framework.*;

public class CashierTest extends TestCase {
	//these are instantiated for each test separately via the setUp() method.
	MarcusCashierRole cashier;
	MockWaiter waiter;
	MockCustomer customer1;
	MockCustomer customer2;
	MockMarket market1;
	MockMarket market2;
	MarcusTable table1;
	MarcusTable table2;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();
		PersonAgent base = new PersonAgent("Cashier");
		cashier = new MarcusCashierRole(base, base.getName());
		base.addRole(Person.ActionType.work, cashier);
		
		customer1 = new MockCustomer("mockcustomer1");		
		customer2 = new MockCustomer("mockcustomer2");		
		waiter = new MockWaiter("mockwaiter");
		market1 = new MockMarket("Market 1");
		market2 = new MockMarket("Market 2");
		table1 = new MarcusTable(1);
		table2 = new MarcusTable(2);
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testOneNormalCustomerScenario() {
		//setUp();// runs first before this test!
		System.out.println("Beginning One Normal Customer Scenario");

		customer1.cashier = cashier;//You can do almost anything in a unit test.			
		table1.setOccupant(customer1);
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.getBills(), 0);		

		cashier.msgComputeBill(waiter, "Steak", table1);//send the message from a waiter

		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.getLog().toString(), 0, waiter.getLog().size());
		
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.getBills(), 1);
		
		assertTrue("Cashier's scheduler should have returned true (no actions to do on a bill from a waiter), but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"MockWaiter should have 1 event log after the Cashier's scheduler is called for the first time (receiving the bill). Instead, the MockWaiter's event log reads: "
						+ waiter.getLog().toString(), 1, waiter.getLog().size());
		
		assertEquals(
				"MockCustomer should have 1 event log after the setup (getting the bill). Instead, the MockCustomer's event log reads: "
						+ waiter.getLog().toString(), 1, waiter.getLog().size());
		
		//step 2 of the test - making the payment
		cashier.msgHereIsPayment(customer1, 20, 1);
		
		//check postconditions for step 2 / preconditions for step 3
		assertTrue("The bill should contain a bill with state == paying. It doesn't.",
				cashier.bills.get(0).state == BillState.paying);


		assertTrue("CashierBill should contain a bill of price = $16. It contains something else instead: $" 
				+ cashier.bills.get(0).bill, cashier.bills.get(0).bill == 16);
		
		assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", 
					cashier.bills.get(0).customer == customer1);
		
		
		//step 3
		//NOTE: I called the scheduler in the assertTrue statement below (to succintly check the return value at the same time)
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's ReadyToPay), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 3 / preconditions for step 4
		assertTrue("MockCustomer should have logged an event for receiving \"Change\" with the correct balance, but his last event logged reads instead: " 
				+ customer1.getLog().getLastLoggedEvent().toString(), customer1.getLog().containsString("Received change from cashier. Change = 4"));

		//step 4
		assertFalse("Cashier's scheduler should have returned false since it is done, but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 4
		assertTrue("MockCustomer should have logged an event for receiving \"Change\" with the correct change, but his last event logged reads instead: " 
				+ customer1.getLog().getLastLoggedEvent().toString(), customer1.getLog().containsString("Received change from cashier. Change = 4"));
	
		
		assertEquals("Cashier should not have any bills. It doesn't.", cashier.getBills(), 0);
		
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		System.out.println("End Normative Scenario");
	}//end one normal customer scenario
	
	
	public void testTwoTwoNormalCustomerScenario() {
		System.out.println("Beginning Two Customer Normal Scenario");

		customer1.cashier = cashier;
		customer2.cashier = cashier;
		table1.setOccupant(customer1);
		table2.setOccupant(customer2);

		assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.getBills(), 0);		

		cashier.msgComputeBill(waiter, "Steak", table1);
		cashier.msgComputeBill(waiter, "Chicken", table2);
		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: " + waiter.getLog().toString(), 0, waiter.getLog().size());
		assertEquals("Cashier should have 2 bills in it. It doesn't.", cashier.getBills(), 2);
		
		assertTrue("Cashier's scheduler should have returned true (needs to process bill1), but didn't.", 
				cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's scheduler should have returned true (needs to process bill2), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		cashier.msgHereIsPayment(customer1, 20, 1);
		cashier.msgHereIsPayment(customer2, 30, 2);

		assertTrue("The first bill should contain a bill with state == paying. It doesn't.",
				cashier.bills.get(0).state == BillState.paying);
		assertTrue("CashierBill should contain a bill of price = $16. It contains something else instead: $" 
				+ cashier.bills.get(0).bill, cashier.bills.get(0).bill == 16);
		
		assertTrue("The second bill should contain a bill with state == paying. It doesn't.",
				cashier.bills.get(1).state == BillState.paying);
		assertTrue("CashierBill should contain a bill of price = $11. It contains something else instead: $" 
				+ cashier.bills.get(1).bill, cashier.bills.get(1).bill == 11);
		
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer 1's ReadyToPay), but didn't.", 
				cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer 2's ReadyToPay), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		assertTrue("MockCustomer1 should have logged an event for receiving \"Change\" with the correct balance, but his last event logged reads instead: " 
				+ customer1.getLog().getLastLoggedEvent().toString(), customer1.getLog().containsString("Received change from cashier. Change = 4"));
		assertTrue("MockCustomer2 should have logged an event for receiving \"Change\" with the correct balance, but his last event logged reads instead: " 
				+ customer2.getLog().getLastLoggedEvent().toString(), customer2.getLog().containsString("Received change from cashier. Change = 19"));
		
		System.out.println("End Two Person Normal Scenario");
	}
	
	//Test if customer cannot afford to pay the bill scenario
	public void testThreeCannotPayScenario() {
		System.out.println("Beginning One Cannot Pay Customer Scenario");

		customer1.cashier = cashier;
		
		table1.setOccupant(customer1);
		assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.getBills(), 0);		
		cashier.msgComputeBill(waiter, "Steak", table1);//send the message from a waiter
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.getLog().toString(), 0, waiter.getLog().size());
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.getBills(), 1);
		assertTrue("Cashier's scheduler should have returned true (no actions to do on a bill from a waiter), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals(
				"MockWaiter should have 1 event log after the Cashier's scheduler is called for the first time (receiving the bill). Instead, the MockWaiter's event log reads: "
						+ waiter.getLog().toString(), 1, waiter.getLog().size());
		assertEquals(
				"MockCustomer should have 1 event log after the setup (getting the bill). Instead, the MockCustomer's event log reads: "
						+ waiter.getLog().toString(), 1, waiter.getLog().size());

		cashier.msgHereIsPayment(customer1, 3, 1); //sending not enough money for food payment, how does cashier react?

		//check postconditions for step 2 / preconditions for step 3
		assertTrue("The bill should contain a bill with state == paying. It doesn't.",
				cashier.bills.get(0).state == BillState.paying);

		assertTrue("CashierBill should contain a bill of price = $16. It contains something else instead: $" 
				+ cashier.bills.get(0).bill, cashier.bills.get(0).bill == 16);
		
		assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", 
					cashier.bills.get(0).customer == customer1);
		
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's ReadyToPay), but didn't.", 
				cashier.pickAndExecuteAnAction());

		//Post payment
		assertTrue("MockCustomer should have received a message from cashier about paying debt next time " 
				+ customer1.getLog().getLastLoggedEvent().toString(), customer1.getLog().containsString("Have a debt to cashier. Debt = -13"));
	
		assertFalse("Cashier's scheduler should have returned false since it is done, but didn't.", 
					cashier.pickAndExecuteAnAction());
		System.out.println("End of Cannot Pay Scenatio");
	}
	
	public void testFourPayingDebtScenario() {
		System.out.println("Beginning One Paying Debt Customer Scenario");

		customer1.cashier = cashier;	
		
		assertNotNull("Customer is not supposed to be null, it is" + customer1);
		assertNotNull("Cashier is not supposed to be null, it is" + cashier);
		cashier.setCash(20);

		assertEquals("Cashier should have $20, instead he has " + cashier.getCash(), 20.0, cashier.getCash());
		
		cashier.msgPayingMyDebt(customer1, 10.0);
		
		assertEquals("Cashier should have $30 now, instead he has " + cashier.getCash(), 30.0, cashier.getCash());

		System.out.println("End paying debt scenario");
	}
	/*
	public void testFiveOneMarketBillScenario() {
		System.out.println("Beginning One Normal Market Scenario");
		market1.cashier = cashier;
		
		assertNotNull("Market is not supposed to be null, it is" + market1);
		assertNotNull("Cashier is not supposed to be null, it is" + cashier);
		
		market1.cash = 40;
		assertEquals("Market should have $40, instead it has " + market1.cash, 40, market1.cash);
		
		cashier.setCash(100);
		assertEquals("Cashier should have $100, instead he has " + cashier.getCash(), 100, cashier.getCash());

		assertEquals("Cashier should have no food bills. It doesn't.", cashier.getMarketBills().size(), 0);
		//cashier.msgHereIsFoodBill(market1, 30);
		assertEquals("Cashier should have 1 food bill. It doesn't.", cashier.getMarketBills().size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true (needs to react to market's bill), but didn't.", cashier.pickAndExecuteAnAction());
		
		assertTrue("MockMarket should have a message from cashier about receiving payment. It doesn't: " 
				+ market1.getLog().getLastLoggedEvent().toString(), market1.getLog().containsString("Just received $30"));
		
		assertEquals("Market should have $70, instead it has " + market1.cash, 70, market1.cash);
		assertEquals("Cashier should have $70, instead he has " + cashier.getCash(), 70, cashier.getCash());
		
		assertFalse("Cashier's scheduler should have returned false since it is done, but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		System.out.println("End One Market Billing Scenario");
	}
	
	public void testSixTwoMarketBillScenario() {
		System.out.println("Beginning Two Normal Market Scenario");

		market1.cashier = cashier;
		market2.cashier = cashier;
		
		assertNotNull("Market1 is not supposed to be null, it is" + market1);
		assertNotNull("Market2 is not supposed to be null, it is" + market2);
		assertNotNull("Cashier is not supposed to be null, it is" + cashier);
		
		market1.cash = 40;
		market2.cash = 60;
		cashier.setCash(130);

		assertEquals("Market1 should have $40, instead it has " + market1.cash, 40, market1.cash);
		assertEquals("Market2 should have $60, instead it has " + market2.cash, 60, market2.cash);
		assertEquals("Cashier should have $130, instead he has " + cashier.getCash(), 130, cashier.getCash());

		assertEquals("Cashier should have no food bills. It doesn't.", cashier.getMarketBills().size(), 0);
		//cashier.msgHereIsFoodBill(market1, 40);
		//cashier.msgHereIsFoodBill(market2, 80);

		assertEquals("Cashier should have 2 food bills. It doesn't.", cashier.getMarketBills().size(), 2);
		assertTrue("Cashier's scheduler should have returned true (needs to react to market's bill), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have $90, instead he has " + cashier.getCash(), 90, cashier.getCash());
		assertTrue("Cashier's scheduler should have returned true (needs to react to market's bill), but didn't.", cashier.pickAndExecuteAnAction());

		assertTrue("MockMarket should have a message from cashier about receiving payment. It doesn't: " 
				+ market1.getLog().getLastLoggedEvent().toString(), market1.getLog().containsString("Just received $40"));
		assertTrue("MockMarket should have a message from cashier about receiving payment. It doesn't: " 
				+ market2.getLog().getLastLoggedEvent().toString(), market2.getLog().containsString("Just received $80"));
		
		assertEquals("Market should have $80, instead it has " + market1.cash, 80, market1.cash);
		assertEquals("Market should have $140, instead it has " + market2.cash, 140, market2.cash);
		assertEquals("Cashier should have $10, instead he has " + cashier.getCash(), 10, cashier.getCash());
		
		assertFalse("Cashier's scheduler should have returned false since it is done, but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		System.out.println("End Two Market Billing Scenario");
	}
	*/
}
