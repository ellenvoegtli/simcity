package mainCity.restaurants.restaurant_zhangdt.test;

import role.davidRestaurant.DavidCashierRole;
import role.davidRestaurant.DavidWaiterRole;
import role.davidRestaurant.DavidCashierRole.CashierState;
import role.davidRestaurant.DavidCashierRole.CheckState;
import role.davidRestaurant.DavidCashierRole.*;
import mainCity.restaurants.restaurant_zhangdt.test.mock.MockCashier;
import mainCity.restaurants.restaurant_zhangdt.test.mock.MockCustomer;
import mainCity.restaurants.restaurant_zhangdt.test.mock.MockMarket;
import mainCity.restaurants.restaurant_zhangdt.test.mock.MockWaiter;
//import mainCity.restaurants.restaurant_zhangdt.test.mock.MockWaiter;
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
	DavidCashierRole cashier;
	MockWaiter waiter;
	MockCustomer customer;
	MockCustomer customer2;
	MockMarket market;
	MockMarket market1;
	MockMarket market2;
	
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new DavidCashierRole("cashier", null);		
		customer = new MockCustomer("mockcustomer");
		customer2 = new MockCustomer("mockcustomer2");	
		waiter = new MockWaiter("mockwaiter");
		market = new MockMarket("mockmarket");
		market1 = new MockMarket("mockmarket1");
		market2 = new MockMarket("mockmarket2");
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testOneNormalCustomerScenario()
	{
		//setUp() runs first before this test!
					
		//check preconditions
		assertEquals("Cashier should have 0 checkList in it. It doesn't.",cashier.checkList.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());

		
		String order = "Chicken"; 
		double custPayment = 20; 
		int tableNumber = 0; 
		
		cashier.msgHeresACheck(waiter, order, tableNumber);
		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.checkList.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"MockWaiter should have an event log of size 1 after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 1, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		
		assertTrue("Cashier should have a check with state == handledCheck. It doesn't", cashier.checkList.get(0).checkState == CheckState.handledCheck); 
		
		assertTrue(
				"Cashier should have logged \"Recieved check from waiter.\" but didn't. His log reads instead: " 
						+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Recieved check from waiter."));
		
		assertTrue("Cashier should have a check of price = $10.99. Instead the check is $" + cashier.checkList.get(0).Price, cashier.checkList.get(0).Price == 10.99);
		
		assertTrue(
				"MockWaiter should have logged \"Recieved BringCheckToCustomer from cashier.\" but didn't. His log reads instead: " 
						+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Recieved BringCheckToCustomer from cashier."));
		
		cashier.msgHeresMyPayment(customer, custPayment, tableNumber); 
		
		assertTrue("Cashier should have a check with the state == recievedPayment. It didn't", cashier.checkList.get(0).checkState == CheckState.recievedPayment); 
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		System.out.println("payment: " + cashier.customers.get(0).getTableNum());
				
		assertTrue("Cashier should be giving back change = $9.01. Instead the change is $" + cashier.change, cashier.change == 9.01);
		
	}//end one normal customer scenario
	
	public void testTwoCustomer1WaiterScenario() { 
		//setUp() runs first before this test!
		
				//check preconditions
				assertEquals("Cashier should have 0 checkList in it. It doesn't.",cashier.checkList.size(), 0);		
				assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
								+ cashier.log.toString(), 0, cashier.log.size());

				
				String order = "Chicken"; 
				double custPayment = 20; 
				int tableNumber = 0;
				int tableNumber1 = 1;
				
				cashier.msgHeresACheck(waiter, order, tableNumber);
				cashier.msgHeresACheck(waiter, order, tableNumber1);
				
				assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
				
				assertEquals("Cashier should have 2 bills in it. It doesn't.", cashier.checkList.size(), 2);
				
				assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
				
				assertEquals(
						"MockWaiter should have an event log of size 1 after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
								+ waiter.log.toString(), 1, waiter.log.size());
				
				assertEquals(
						"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
								+ customer.log.toString(), 0, customer.log.size());
				
				assertTrue("Cashier should have a check with state == handledCheck. It doesn't", cashier.checkList.get(0).checkState == CheckState.handledCheck); 
				
				assertTrue(
						"Cashier should have logged \"Recieved check from waiter.\" but didn't. His log reads instead: " 
								+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Recieved check from waiter."));
				
				assertTrue("Cashier should have a check of price = $10.99. Instead the check is $" + cashier.checkList.get(0).Price, cashier.checkList.get(0).Price == 10.99);
				
				assertTrue(
						"MockWaiter should have logged \"Recieved BringCheckToCustomer from cashier.\" but didn't. His log reads instead: " 
								+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Recieved BringCheckToCustomer from cashier."));
				
				cashier.msgHeresMyPayment(customer, custPayment, tableNumber); 
				
				
				
				assertTrue("Cashier should have a check with the state == recievedPayment. It didn't", cashier.checkList.get(0).checkState == CheckState.recievedPayment); 
				
				assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
				
				System.out.println("payment: " + cashier.customers.get(0).getTableNum());
						
				assertTrue("Cashier should be giving back change = $9.01. Instead the change is $" + cashier.change, cashier.change == 9.01);
				
				//end of first check 
				
				assertTrue("Cashier should have another check with the state == recievedCheck. It didn't", cashier.checkList.get(1).checkState == CheckState.recievedCheck);
				
				assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
				
				assertTrue("Cashier should have another check with the state == handledCheck. It didn't", cashier.checkList.get(1).checkState == CheckState.handledCheck);
				
				assertTrue(
						"Cashier should have logged \"Recieved check from waiter.\" but didn't. His log reads instead: " 
								+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Recieved check from waiter."));
				
				cashier.msgHeresMyPayment(customer2, custPayment, tableNumber1); 
				
				assertTrue("Cashier should have another check with the state == recievedPayment. It didn't", cashier.checkList.get(1).checkState == CheckState.recievedPayment);
				
				assertTrue("Cashier should have a check of price = $10.99. Instead the check is $" + cashier.checkList.get(1).Price, cashier.checkList.get(1).Price == 10.99);
				 
				assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
				
				assertTrue("Cashier should be giving back change = $9.01. Instead the change is $" + cashier.change, cashier.change == 9.01);
	}
	
	public void testEnoughMoneyMarketScenario() { 
		//Testing the scenario where the cashier has enough money to pay off the market
		
		int marketNum = 0; 
		double MarketCost = 100;
		cashier.Money = 100; 
		cashier.addMarket(market);
		cashier.addMarket(market1);
		cashier.addMarket(market2);
		
		//preconditions 
		assertEquals("Cashier should have 0 checkList in it. It doesn't.",cashier.checkList.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		//cashier.msgPayMarket(marketNum, MarketCost);
		
		assertTrue ("Cashier state should be recievedMarketBill. It isn't.", cashier.cashierState == CashierState.recievedMarketBill);
		
		//Should come out to be zero since market cost is 100 and cashier has 100. 
		assertEquals("Cashier should have $0, but instead has $" + cashier.Money, cashier.Money, 0.0); 
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertTrue("Cashier state should be handledMarketBill. It isn't.", cashier.cashierState == CashierState.handledMarketBill);

		assertTrue("MarketNumber should be 0. It isnt.", cashier.MarketNumber == 0); 
		
		assertTrue(
				"Market should have logged \"Recieved msgPaymentFromCashier from cashier.\" but didn't. His log reads instead: " 
						+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Recieved msgPaymentFromCashier from cashier."));
	}
	
	public void testEnoughMoney2MarketsScenario() { 
		//Testing the scenario where the cashier has enough money to pay off the market
		
		int marketNum = 0; 
		double MarketCost1 = 50;
		double MarketCost2 = 45;
		cashier.Money = 100; 
		cashier.addMarket(market);
		cashier.addMarket(market1);
		cashier.addMarket(market2);
		
		//preconditions 
		assertEquals("Cashier should have 0 checkList in it. It doesn't.",cashier.checkList.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		//cashier.msgPayMarket(marketNum, MarketCost1);
		
		assertTrue ("Cashier state should be recievedMarketBill. It isn't.", cashier.cashierState == CashierState.recievedMarketBill);
		
		//Should come out to be zero since market cost is 100 and cashier has 100. 
		assertEquals("Cashier should have $0, but instead has $" + cashier.Money, cashier.Money, 50.0); 
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertTrue("Cashier state should be handledMarketBill. It isn't.", cashier.cashierState == CashierState.handledMarketBill);

		assertTrue("MarketNumber should be 0. It isnt.", cashier.MarketNumber == 0); 
		
		//cashier.msgPayMarket(marketNum, MarketCost2); 
		
		assertEquals("Cashier should have $0, but instead has $" + cashier.Money, cashier.Money, 5.0);
		
		assertTrue("Market should be paid off. But it isn't.", cashier.PaidMarket);
		
		assertTrue(
				"Market should have logged \"Recieved msgPaymentFromCashier from cashier.\" but didn't. His log reads instead: " 
						+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Recieved msgPaymentFromCashier from cashier."));
	}
	
	public void testNotEnoughMoneyMarketScenario() { 
		int marketNum = 0; 
		double MarketCost = 100;
		cashier.Money = 0; 
		cashier.addMarket(market);
		cashier.addMarket(market1);
		cashier.addMarket(market2);
		
		//preconditions 
		assertEquals("Cashier should have 0 checkList in it. It doesn't.",cashier.checkList.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		//cashier.msgPayMarket(marketNum, MarketCost);
		
		assertTrue ("Cashier state should be recievedMarketBill. It isn't.", cashier.cashierState == CashierState.recievedMarketBill);
		
		//Should come out to be zero since market cost is 100 and cashier has 100. 
		assertEquals("Cashier should have $0, but instead has $" + cashier.Money, cashier.Money, 0.0); 
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertTrue("Cashier state should be handledMarketBill. It isn't.", cashier.cashierState == CashierState.handledMarketBill);

		assertTrue("MarketNumber should be 0. It isnt.", cashier.MarketNumber == 0); 
		
		assertFalse("Market should not be paid off. But it is.", cashier.PaidMarket);
		
		assertTrue(
				"Market should have logged \"Recieved msgPaymentFromCashier from cashier.\" but didn't. His log reads instead: " 
						+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Recieved msgPaymentFromCashier from cashier."));
	}
	
	public void testOneNormalCustomerWithMarketScenario()
	{
		//setUp() runs first before this test!
					
		//check preconditions
		assertEquals("Cashier should have 0 checkList in it. It doesn't.",cashier.checkList.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());

		
		String order = "Chicken"; 
		double custPayment = 20; 
		int tableNumber = 0; 
		int marketNum = 0; 
		double MarketCost = 100;
		cashier.Money = 100; 
		cashier.addMarket(market);
		cashier.addMarket(market1);
		cashier.addMarket(market2);
		
		cashier.msgHeresACheck(waiter, order, tableNumber);
		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.checkList.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"MockWaiter should have an event log of size 1 after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 1, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		
		assertTrue("Cashier should have a check with state == handledCheck. It doesn't", cashier.checkList.get(0).checkState == CheckState.handledCheck); 
		
		assertTrue(
				"Cashier should have logged \"Recieved check from waiter.\" but didn't. His log reads instead: " 
						+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Recieved check from waiter."));
		
		assertTrue("Cashier should have a check of price = $10.99. Instead the check is $" + cashier.checkList.get(0).Price, cashier.checkList.get(0).Price == 10.99);
		
		assertTrue(
				"MockWaiter should have logged \"Recieved BringCheckToCustomer from cashier.\" but didn't. His log reads instead: " 
						+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Recieved BringCheckToCustomer from cashier."));
		
		cashier.msgHeresMyPayment(customer, custPayment, tableNumber); 
		
		//cashier.msgPayMarket(marketNum, MarketCost);
		
		assertTrue("Cashier should have a check with the state == recievedPayment. It didn't", cashier.checkList.get(0).checkState == CheckState.recievedPayment); 
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		System.out.println("payment: " + cashier.customers.get(0).getTableNum());
				
		assertTrue("Cashier should be giving back change = $9.01. Instead the change is $" + cashier.change, cashier.change == 9.01);
		
		System.out.println("payment: " + cashier.cashierState);
		
		assertTrue("Cashier state should be recievedMarketBill. It isn't.", cashier.cashierState == CashierState.recievedMarketBill);

		assertTrue("MarketNumber should be 0. It isnt.", cashier.MarketNumber == 0); 
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());

		assertTrue("Cashier state should be handledMarketBill. It isn't.", cashier.cashierState == CashierState.handledMarketBill);

		assertTrue("Market should be paid off. But it isn't.", cashier.PaidMarket);
		
		assertTrue(
				"Market should have logged \"Recieved msgPaymentFromCashier from cashier.\" but didn't. His log reads instead: " 
						+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Recieved msgPaymentFromCashier from cashier."));
	}
}
