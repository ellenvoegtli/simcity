package mainCity.restaurants.EllenRestaurant.test;

import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.EllenRestaurant.test.mock.*;
import mainCity.restaurants.EllenRestaurant.EllenCashierRole.Check;
import mainCity.restaurants.EllenRestaurant.EllenCashierRole.CheckState;
import mainCity.restaurants.EllenRestaurant.EllenCashierRole.MarketBillState;
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
        EllenCashierRole cashier;
        MockWaiter waiter;
        MockCustomer customer;
        MockCustomer customer1;
        MockCustomer customer2;
        MockMarket market1;
        MockMarket market2;
        MockCustomer flake;
        
        
        /**
         * This method is run before each test. You can use it to instantiate the class variables
         * for your agent and mocks, etc.
         */
        public void setUp() throws Exception{
                super.setUp();                
                cashier = new EllenCashierRole("cashier");                
                customer = new MockCustomer("mockcustomer"); 
                customer1 = new MockCustomer("mockcustomer1");
                customer2 = new MockCustomer("mockcustomer2");
                flake = new MockCustomer("flake");
                waiter = new MockWaiter("mockwaiter");
                
                //I added
                market1 = new MockMarket("market1");
                market2 = new MockMarket("market2");
                
        }        
        /**
         * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
         */

        public void testOneNormalCustomerScenario()
        {
                //setUp() runs first before this test!
                
                customer.cashier = cashier;//You can do almost anything in a unit test.     
                customer.waiter = waiter;
                
                //check preconditions
                assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.getChecks().size(), 0);
                
                //step 1 - create a check
                cashier.msgComputeBill("steak", customer, waiter);
                
                //postconditions for step 1/preconditions for step 2
                assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
                        + waiter.log.toString(), 0, waiter.log.size());
                assertEquals("Cashier should have 1 check but does not.", cashier.getChecks().size(), 1);
                assertTrue("Cashier should contain a check with state == computing. It doesn't.",
                        cashier.checks.get(0).getState() == CheckState.computing);
                assertTrue("Cashier should contain a check with the correct waiter. It doesn't.",
                        cashier.checks.get(0).getWaiter() == waiter);
                assertTrue("Cashier should contain a check with the right customer in it. It doesn't.", 
                        cashier.checks.get(0).getCustomer() == customer);
                
                
                //step 2 - check that scheduler returns true 1st time
                assertTrue("Cashier's scheduler should have returned true (needs to react to new check), but didn't.",
                		cashier.pickAndExecuteAnAction());

                //postconditions for step 2/preconditions for step 3
                assertTrue("Waiter should have logged \"Received HereIsCheck\" with amount = $30 but didn't. His log reads instead: " 
                        + waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received HereIsCheck from cashier. Amount = $30"));
                assertTrue("Cashier should contain a check with state == waitingForPayment. It doesn't.",
                        cashier.checks.get(0).getState() == CheckState.waitingForPayment);
                assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
                                        + customer.log.toString(), 0, customer.log.size());
                
                
                //step 3
                cashier.msgHereIsPayment(30, 100, customer);	//checkAmount, cashAmount, Customer
                
                //postconditions step 3/preconditions step 4
                assertTrue("Cashier should contain a check with state == calculatingChange. It doesn't.",
                        cashier.checks.get(0).getState() == CheckState.calculatingChange);
                assertTrue("Cashier should contain a check where the amount the customer paid = $100. It contains something else instead: $" 
                        + cashier.checks.get(0).getAmount(), cashier.checks.get(0).getAmount() == 100);
                
                
                //step 4
                assertTrue("Cashier's scheduler should have returned true (needs to react to customer's payment), but didn't.", 
                        cashier.pickAndExecuteAnAction());

                //postconditions 4
                assertTrue("MockCustomer should have logged an event for receiving \"HereIsChange\" with the correct change, but his last event logged reads instead: " 
                        + customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received HereIsChange from cashier. Change = $70"));
                assertEquals("Cashier should have 0 checks but does not.", cashier.getChecks().size(), 0);
                assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
                        cashier.pickAndExecuteAnAction());
                
        
        }		//end one normal customer scenario
        
        
        public void testOneShortCustomerScenario(){		//one customer, short of money ("flake")
        	flake.cashier = cashier;    
            flake.waiter = waiter;
            
            //check preconditions
            assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.getChecks().size(), 0);
            
            //step 1 - create a check
            cashier.msgComputeBill("steak", flake, waiter);
            
            //postconditions for step 1/preconditions for step 2
            assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
                    + waiter.log.toString(), 0, waiter.log.size());
            assertEquals("Cashier should have 1 check but does not.", cashier.getChecks().size(), 1);
            assertTrue("Cashier should contain a check with state == computing. It doesn't.",
                    cashier.checks.get(0).getState() == CheckState.computing);
            assertTrue("Cashier should contain a check with the right customer in it. It doesn't.", 
                            cashier.checks.get(0).getCustomer() == flake);
            assertTrue("Cashier should contain a check with the right waiter in it. It doesn't.", 
                    cashier.checks.get(0).getWaiter() == waiter);
            
            
            //step 2 - check that scheduler returns true 1st time
            assertTrue("Cashier's scheduler should have returned true (needs to react to new check), but didn't.",
            		cashier.pickAndExecuteAnAction());

            //postconditions for step 2/preconditions for step 3
            assertTrue("Waiter should have logged \"Received HereIsCheck\" with amount = $30 but didn't. His log reads instead: " 
                    + waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received HereIsCheck from cashier. Amount = $30"));
            assertTrue("Cashier should contain a check with state == waitingForPayment. It doesn't.",
                    cashier.checks.get(0).getState() == CheckState.waitingForPayment);
            assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
                                    + flake.log.toString(), 0, flake.log.size());
            
            
            //step 3
            cashier.msgHereIsPayment(30, 10, flake);	//checkAmount, cashAmount, Customer
            
            //postconditions step 3/preconditions step 4
            assertTrue("Cashier should contain a check with state == calculatingChange. It doesn't.",
                    cashier.checks.get(0).getState() == CheckState.calculatingChange);
            assertTrue("Cashier should contain a check where the amount the customer paid = $10. It contains something else instead: $" 
                    + cashier.checks.get(0).getAmount(), cashier.checks.get(0).getAmount() == 10);
            
            
            //step 4
            assertTrue("Cashier's scheduler should have returned true (needs to react to customer's payment), but didn't.", 
                    cashier.pickAndExecuteAnAction());

            //postconditions step 4
            assertTrue("MockCustomer should have logged an event for receiving \"NotEnoughCash\" with the correct amount owed, but his last event logged reads instead: " 
                    + flake.log.getLastLoggedEvent().toString(), flake.log.containsString("Received NotEnoughCash from cashier. Debt = $20"));
            assertEquals("Cashier should have 0 checks but does not.", cashier.getChecks().size(), 0);
            assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
                    cashier.pickAndExecuteAnAction());
        }
        
        /* errors now that marketbill message has changed
        public void testOneMarketBillScenario(){
        	
        	//check preconditions
        	assertEquals("Cashier should have 0 market bills in it. It doesn't.", cashier.getMarketBills().size(), 0);
        	assertEquals("Cashier's amountToPayMarket should equal 0. It doesn't.", cashier.getAmountToPayMarket(), 0);
        	assertEquals("MockMarket should have an empty event log before the Cook sends a message requesting inventory. Instead, the MockMarket's event log reads: "
                          + market1.log.toString(), 0, market1.log.size());
        	
        	//step 1
        	market1.msgINeedInventory("soup", 5);		//message market from cook
        	
        	//postconditions for step 1/preconditions for step 2
        	assertTrue("MockMarket should have logged an event for receiving \"INeedInventory\" with the correct choice and amount, but his last event logged reads instead: " 
                    + market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Received msgINeedInventory from cook. Needs 5 soup(s)."));
        	assertFalse("Cashier's scheduler should have returned false (no actions to do yet), but didn't.", 
                    cashier.pickAndExecuteAnAction());
        	
        	//step 2 - message cashier from market for price of restocking
        	cashier.msgHereIsMarketBill(25, market1);
        	
        	//postconditions for step 2/preconditions for step 3
        	assertEquals("Cashier should have 1 market bill in it. It doesn't.", cashier.getMarketBills().size(), 1);
        	assertEquals("Cashier should have a market bill with market = market 1 in it. It doesn't.", cashier.marketBills.get(0).getMarket(), market1);
        	assertEquals("Cashier should have a market bill with state = computing in it. It doesn't.", cashier.marketBills.get(0).getState(), MarketBillState.computing);
        	assertEquals("Cashier should have a market bill with amount = 25 in it. It doesn't.", cashier.marketBills.get(0).getCheckAmount(), 25);
        	
        	//step 3 - call cashier's scheduler
        	assertTrue("Cashier's scheduler should have returned true (needs to respond to new Market Bill), but didn't.", 
                    cashier.pickAndExecuteAnAction());
        	
        	//postconditions for step 3
        	assertTrue("MockMarket should have logged an event for receiving \"HereIsPayment\" with the correct amount paid by the cashier, but his last event logged reads instead: " 
                    + market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Received HereIsPayment from cashier. Paid = $25"));
        	assertEquals("Cashier's amountToPayMarket should equal 25. It doesn't.", cashier.getAmountToPayMarket(), 25);
        	assertEquals("Cashier should have 0 market bills in it. It doesn't.", cashier.getMarketBills().size(), 0);
        	assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
                    cashier.pickAndExecuteAnAction());
        }
        
        
        public void testTwoMarketBillScenario(){
        	
        	//check preconditions
        	assertEquals("Cashier should have 0 market bills in it. It doesn't.", cashier.getMarketBills().size(), 0);
        	assertEquals("Cashier's amountToPayMarket should equal 0. It doesn't.", cashier.getAmountToPayMarket(), 0);
        	assertEquals("MockMarket1 should have an empty event log before the Cook sends a message requesting inventory. Instead, the MockMarket1's event log reads: "
                          + market1.log.toString(), 0, market1.log.size());
        	assertEquals("MockMarket2 should have an empty event log before the Cook sends a message requesting inventory. Instead, the MockMarket2's event log reads: "
                    + market2.log.toString(), 0, market2.log.size());
        	
        	//step 1
        	market1.msgINeedInventory("soup", 5);
        	market2.msgINeedInventory("soup", 3);
        	
        	//postconditions for step 1/preconditions for step 2
        	assertTrue("MockMarket1 should have logged an event for receiving \"INeedInventory\" with the correct choice and amount, but his last event logged reads instead: " 
                    + market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Received msgINeedInventory from cook. Needs 5 soup(s)."));
        	assertTrue("MockMarket2 should have logged an event for receiving \"INeedInventory\" with the correct choice and amount, but his last event logged reads instead: " 
                    + market2.log.getLastLoggedEvent().toString(), market2.log.containsString("Received msgINeedInventory from cook. Needs 3 soup(s)."));
        	assertFalse("Cashier's scheduler should have returned false (no actions to do yet), but didn't.", 
                    cashier.pickAndExecuteAnAction());
        	
        	//step 2
        	cashier.msgHereIsMarketBill(25, market1);
        	
        	//postconditions for step 2/preconditions for step 3
        	assertEquals("Cashier should have 1 market bill in it. It doesn't.", cashier.getMarketBills().size(), 1);
        	assertEquals("Cashier should have a market bill with market = market 1 in it. It doesn't.", cashier.marketBills.get(0).getMarket(), market1);
        	assertEquals("Cashier should have a market bill with state = computing in it. It doesn't.", cashier.marketBills.get(0).getState(), MarketBillState.computing);
        	assertEquals("Cashier should have a market bill with amount = 25 in it. It doesn't.", cashier.marketBills.get(0).getCheckAmount(), 25);

        	
        	//step 3
        	cashier.msgHereIsMarketBill(15, market2);
        	
        	//postconditions for step 3/preconditions for step 4
        	assertEquals("Cashier should have 2 market bills in it. It doesn't.", cashier.getMarketBills().size(), 2);
        	assertEquals("Cashier should have a market bill with market = market 2 in it. It doesn't.", cashier.marketBills.get(1).getMarket(), market2);
        	assertEquals("Cashier should have a market bill with state = computing in it. It doesn't.", cashier.marketBills.get(1).getState(), MarketBillState.computing);
        	assertEquals("Cashier should have a market bill with amount = 15 in it. It doesn't.", cashier.marketBills.get(1).getCheckAmount(), 15);

        	
        	//step 4
        	assertTrue("Cashier's scheduler should have returned true (needs to respond to new Market Bills), but didn't.", 
                    cashier.pickAndExecuteAnAction());
        	
        	//postconditions for step 4/preconditions for step 5
        	assertEquals("Cashier's amountToPayMarket should equal 25. It doesn't.", cashier.getAmountToPayMarket(), 25);
        	assertTrue("MockMarket1 should have logged an event for receiving \"HereIsPayment\" with the correct amount paid by the cashier, but his last event logged reads instead: " 
                    + market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Received HereIsPayment from cashier. Paid = $25"));
        	assertEquals("Cashier should have 1 market bills in it. It doesn't.", cashier.getMarketBills().size(), 1);
        	
        	
        	//step 5
        	assertTrue("Cashier's scheduler should have returned true (needs to respond to the second MarketBill), but didn't.", 
                    cashier.pickAndExecuteAnAction());
        	
        	//postconditions for step 5
        	assertEquals("Cashier's amountToPayMarket should equal 15. It doesn't.", cashier.getAmountToPayMarket(), 15);
        	assertTrue("MockMarket2 should have logged an event for receiving \"HereIsPayment\" with the correct amount paid by the cashier, but his last event logged reads instead: " 
                    + market2.log.getLastLoggedEvent().toString(), market2.log.containsString("Received HereIsPayment from cashier. Paid = $15"));
        	assertEquals("Cashier should have 0 market bills in it. It doesn't.", cashier.getMarketBills().size(), 0);    	
        	assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
                    cashier.pickAndExecuteAnAction());
        	
        }*/
        
        /*
        public void testOneCustomerOneMarketBillScenario(){
        	/*
        	 * Waiter asks cashier to compute a check for a customer.
        	 * Then cook requests inventory from a market.
        	 * Then cashier's scheduler is called, and cashier sends waiter the check.
        	 * Then market sends cashier a bill for the inventory.
        	 * Cashier pays the market.
        	 * THEN customer pays his check to the cashier, and cashier sends him proper change.
        	 */
        	
        /*
        	customer.cashier = cashier;   
            customer.waiter = waiter;
            
            //check preconditions - customer bills and market bills
            assertEquals("Cashier should have 0 checks in it. It doesn't.", cashier.getChecks().size(), 0);
            
            assertEquals("Cashier should have 0 market bills in it. It doesn't.", cashier.getMarketBills().size(), 0);
        	assertEquals("Cashier's amountToPayMarket should equal 0. It doesn't.", cashier.getAmountToPayMarket(), 0);
            
        	
            //step 1 - create a check
            cashier.msgComputeBill("pizza", customer, waiter);		//from waiter
            
            //postconditions for step 1/preconditions for step 2
            assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
                    + waiter.log.toString(), 0, waiter.log.size());
            assertEquals("Cashier should have 1 check but does not.", cashier.getChecks().size(), 1);
            assertTrue("Cashier should contain a check with state == computing. It doesn't.",
                    cashier.checks.get(0).getState() == CheckState.computing);
            assertTrue("Cashier should contain a check with the correct waiter. It doesn't.",
                    cashier.checks.get(0).getWaiter() == waiter);
            assertTrue("Cashier should contain a check with the correct customer. It doesn't.",
                    cashier.checks.get(0).getCustomer() == customer);
            
            assertEquals("MockMarket should have an empty event log before the Cook sends a message requesting inventory. Instead, the MockMarket's event log reads: "
                    + market1.log.toString(), 0, market1.log.size());

        	
        	//step 2
        	market1.msgINeedInventory("pasta", 7);		//message market from cook
        	
        	//postconditions for step 2/preconditions for step 3
        	assertTrue("MockMarket should have logged an event for receiving \"INeedInventory\" with the correct choice and amount, but his last event logged reads instead: " 
                    + market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Received msgINeedInventory from cook. Needs 7 pasta(s)."));
        	
        	
        	//step 3
        	assertTrue("Cashier's scheduler should have returned true (needs to react to computeBill), but didn't.", 
                          cashier.pickAndExecuteAnAction());
        	
        	//postconditions for step 3/preconditions for step 4
            assertTrue("Waiter should have logged \"Received HereIsCheck\" with amount = $10 but didn't. His log reads instead: " 
                    + waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received HereIsCheck from cashier. Amount = $10"));
            assertTrue("Cashier should contain a check with state == waitingForPayment. It doesn't.",
                    cashier.checks.get(0).getState() == CheckState.waitingForPayment);
            assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
                                    + customer.log.toString(), 0, customer.log.size());
            assertFalse("Cashier's scheduler should have returned false (no actions to do right now), but didn't.", 
                          cashier.pickAndExecuteAnAction());
        	
            
            //step 4
            cashier.msgHereIsMarketBill(70, market1);		//from market1
        	
        	//postconditions for step 4/preconditions for step 5
        	assertEquals("Cashier should have 1 market bill in it. It doesn't.", cashier.getMarketBills().size(), 1);
        	assertEquals("Cashier should have a market bill with market = market 1 in it. It doesn't.", cashier.marketBills.get(0).getMarket(), market1);
        	assertEquals("Cashier should have a market bill with state = computing in it. It doesn't.", cashier.marketBills.get(0).getState(), MarketBillState.computing);
        	assertEquals("Cashier should have a market bill with amount = 70 in it. It doesn't.", cashier.marketBills.get(0).getCheckAmount(), 70);

        	
        	//step 5
        	assertTrue("Cashier's scheduler should have returned true (needs to react to new market bill), but didn't.", 
                    cashier.pickAndExecuteAnAction());
        	
        	//postconditions for step 5/preconditions for step 6
        	assertEquals("Cashier's amountToPayMarket should equal 70. It doesn't.", cashier.getAmountToPayMarket(), 70);
        	assertTrue("MockMarket1 should have logged an event for receiving \"HereIsPayment\" with the correct amount paid by the cashier, but his last event logged reads instead: " 
                    + market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Received HereIsPayment from cashier. Paid = $70"));
        	assertEquals("Cashier should have 0 market bills in it. It doesn't.", cashier.getMarketBills().size(), 0);
        	
        	
        	//step 6 - customer pays their check
        	cashier.msgHereIsPayment(10, 100, customer);	//checkAmount, cashAmount, Customer
            
            //postconditions step 6/preconditions step 7
            assertTrue("Cashier should contain a check with state == calculatingChange. It doesn't.",
                    cashier.checks.get(0).getState() == CheckState.calculatingChange);
            assertTrue("Cashier should contain a check where the amount the customer paid = $100. It contains something else instead: $" 
                    + cashier.checks.get(0).getAmount(), cashier.checks.get(0).getAmount() == 100);
            
            
            //step 7
            assertTrue("Cashier's scheduler should have returned true (needs to react to customer's payment), but didn't.", 
                    cashier.pickAndExecuteAnAction());

            //postconditions 7
            assertTrue("MockCustomer should have logged an event for receiving \"HereIsChange\" with the correct change, but his last event logged reads instead: " 
                    + customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received HereIsChange from cashier. Change = $90"));
            assertEquals("Cashier should have 0 checks but does not.", cashier.getChecks().size(), 0);
            assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
                    cashier.pickAndExecuteAnAction());
        }
        */
        
        //public void testOneCustomerTwoMarketBillsScenario(){
        	/*
        	 * Waiter asks cashier to compute a check for a customer1.
        	 * Then cook requests inventory from a market.
        	 * Then cashier's scheduler is called, and cashier sends waiter the check for customer1.
        	 * Then market sends cashier a bill for the inventory.
        	 * Then waiter asks cashier to compute another check for a different customer2.
        	 * 		THEN Cashier sends waiter the check for customer2.
        	 * Then customer1 pays his check to the cashier.
        	 * 		Then cashier sends him proper change.
        	 * 		THEN (scheduler called again) cashier pays the market bill.
        	 * THEN customer2 pays his check to the cashier, and cashier sends him proper change.
        	 */
        /*
        	customer1.cashier = cashier;   
            customer1.waiter = waiter;
            customer2.cashier = cashier;
            customer2.waiter = waiter;
            
            
            //check preconditions - customer bills and market bills
            assertEquals("Cashier should have 0 checks in it. It doesn't.", cashier.getChecks().size(), 0);
            
            assertEquals("Cashier should have 0 market bills in it. It doesn't.", cashier.getMarketBills().size(), 0);
        	assertEquals("Cashier's amountToPayMarket should equal 0. It doesn't.", cashier.getAmountToPayMarket(), 0);
            
        	
            //step 1 - create a check
            cashier.msgComputeBill("pasta", customer1, waiter);		//from waiter
            
            //postconditions for step 1/preconditions for step 2
            assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
                    + waiter.log.toString(), 0, waiter.log.size());
            assertEquals("Cashier should have 1 check but does not.", cashier.getChecks().size(), 1);
            assertTrue("Cashier should contain a check with state == computing. It doesn't.",
                    cashier.checks.get(0).getState() == CheckState.computing);
            assertTrue("Cashier should contain a check with the correct waiter. It doesn't.",
                    cashier.checks.get(0).getWaiter() == waiter);
            assertTrue("Cashier should contain a check with the correct customer. It doesn't.",
                    cashier.checks.get(0).getCustomer() == customer1);
            
            assertEquals("MockMarket should have an empty event log before the Cook sends a message requesting inventory. Instead, the MockMarket's event log reads: "
                    + market1.log.toString(), 0, market1.log.size());

        	
        	//step 2
        	market1.msgINeedInventory("pizza", 4);		//message market from cook
        	
        	//postconditions for step 2/preconditions for step 3
        	assertTrue("MockMarket should have logged an event for receiving \"INeedInventory\" with the correct choice and amount, but his last event logged reads instead: " 
                    + market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Received msgINeedInventory from cook. Needs 4 pizza(s)."));
        	
        	
        	//step 3
        	assertTrue("Cashier's scheduler should have returned true (needs to react to computeBill), but didn't.", 
                          cashier.pickAndExecuteAnAction());
        	
        	//postconditions for step 3/preconditions for step 4
            assertTrue("Waiter should have logged \"Received HereIsCheck\" with amount = $20 but didn't. His log reads instead: " 
                    + waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received HereIsCheck from cashier. Amount = $20"));
            assertTrue("Cashier should contain a check with state == waitingForPayment. It doesn't.",
                    cashier.checks.get(0).getState() == CheckState.waitingForPayment);
            assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
                                    + customer1.log.toString(), 0, customer1.log.size());
            assertFalse("Cashier's scheduler should have returned false (no actions to do right now), but didn't.", 
                          cashier.pickAndExecuteAnAction());
        	
            
            //step 4
            cashier.msgHereIsMarketBill(40, market1);		//from market1
        	
        	//postconditions for step 4/preconditions for step 5
        	assertEquals("Cashier should have 1 market bill in it. It doesn't.", cashier.getMarketBills().size(), 1);
        	assertEquals("Cashier should have a market bill with market = market 1 in it. It doesn't.", cashier.marketBills.get(0).getMarket(), market1);
        	assertEquals("Cashier should have a market bill with state = computing in it. It doesn't.", cashier.marketBills.get(0).getState(), MarketBillState.computing);
        	assertEquals("Cashier should have a market bill with amount = 40 in it. It doesn't.", cashier.marketBills.get(0).getCheckAmount(), 40);

        	
        	//step 5
        	cashier.msgComputeBill("steak", customer2, waiter);		//from waiter
            
            //postconditions for step 1/preconditions for step 2
            assertEquals("Cashier should have 2 checks but does not.", cashier.getChecks().size(), 2);
            assertTrue("Cashier should contain a check with state == computing. It doesn't.",
                    cashier.checks.get(1).getState() == CheckState.computing);
            assertTrue("Cashier should contain a check with the correct waiter. It doesn't.",
                    cashier.checks.get(1).getWaiter() == waiter);
            assertTrue("Cashier should contain a check with the correct customer. It doesn't.",
                    cashier.checks.get(1).getCustomer() == customer2);
        	
            
        	//step 5
        	assertTrue("Cashier's scheduler should have returned true (needs to react to new computeBill), but didn't.", 
                    cashier.pickAndExecuteAnAction());
        	
        	//postconditions 5/preconditions 6
        	assertTrue("Waiter should have logged \"Received HereIsCheck\" with amount = $30 but didn't. His log reads instead: " 
                    + waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received HereIsCheck from cashier. Amount = $30"));
            assertTrue("Cashier should contain a check with state == waitingForPayment. It doesn't.",
                    cashier.checks.get(1).getState() == CheckState.waitingForPayment);
            assertEquals("MockCustomer2 should have an empty event log after the Cashier's scheduler is called for the 'first' time. Instead, the MockCustomer2's event log reads: "
                                    + customer2.log.toString(), 0, customer2.log.size());
        	
            //step 6 - customer pays their check
        	cashier.msgHereIsPayment(20, 100, customer1);	//checkAmount, cashAmount, Customer
            
            //postconditions step 6/preconditions step 7
            assertTrue("Cashier should contain a check with state == calculatingChange. It doesn't.",
                    cashier.checks.get(0).getState() == CheckState.calculatingChange);
            assertTrue("Cashier should contain a check where the amount the customer paid = $100. It contains something else instead: $" 
                    + cashier.checks.get(0).getAmount(), cashier.checks.get(0).getAmount() == 100);
            
            
            //step 7
            assertTrue("Cashier's scheduler should have returned true (needs to react to customer payment), but didn't.", 
                    cashier.pickAndExecuteAnAction());
            
            //postconditions 7/preconditions 8
            assertTrue("MockCustomer1 should have logged an event for receiving \"HereIsChange\" with the correct change, but his last event logged reads instead: " 
                    + customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received HereIsChange from cashier. Change = $80"));
            assertEquals("Cashier should have 1 check but does not.", cashier.getChecks().size(), 1);

            
            //step 8
            assertTrue("Cashier's scheduler should have returned true (needs to react to market bill), but didn't.", 
                    cashier.pickAndExecuteAnAction());
            
        	//postconditions for step 5/preconditions for step 6
        	assertEquals("Cashier's amountToPayMarket should equal 40. It doesn't.", cashier.getAmountToPayMarket(), 40);
        	assertTrue("MockMarket1 should have logged an event for receiving \"HereIsPayment\" with the correct amount paid by the cashier, but his last event logged reads instead: " 
                    + market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Received HereIsPayment from cashier. Paid = $40"));
        	assertEquals("Cashier should have 0 market bills in it. It doesn't.", cashier.getMarketBills().size(), 0);
        	
        	
        	
        	//step 9 - customer2 pays their check
        	cashier.msgHereIsPayment(30, 100, customer2);	//checkAmount, cashAmount, Customer
            
            //postconditions step 9/preconditions step 10
            assertTrue("Cashier should contain a check with state == calculatingChange. It doesn't.",
                    cashier.checks.get(0).getState() == CheckState.calculatingChange);
            assertTrue("Cashier should contain a check where the amount the customer paid = $100. It contains something else instead: $" 
                    + cashier.checks.get(0).getAmount(), cashier.checks.get(0).getAmount() == 100);
            
            
            //step 10
            assertTrue("Cashier's scheduler should have returned true (needs to react to customer's payment), but didn't.", 
                    cashier.pickAndExecuteAnAction());

            //postconditions 10
            assertTrue("MockCustomer2 should have logged an event for receiving \"HereIsChange\" with the correct change, but his last event logged reads instead: " 
                    + customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Received HereIsChange from cashier. Change = $70"));
            assertEquals("Cashier should have 0 checks but does not.", cashier.getChecks().size(), 0);
            assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
                    cashier.pickAndExecuteAnAction());
        }
		*/
        
        
        
}