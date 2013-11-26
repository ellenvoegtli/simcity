package mainCity.market.test;

import java.util.*;

import junit.framework.*;
import mainCity.PersonAgent;
import mainCity.PersonAgent.ActionType;
import mainCity.market.*;
import mainCity.market.MarketCashierRole.BillState;
import mainCity.market.test.mock.*;
import mainCity.restaurants.EllenRestaurant.EllenCashierRole.CheckState;


public class CashierTest extends TestCase {
	MarketCashierRole cashier;
	MockEmployee employee;
	MockCustomer customer1;
	MockCustomer customer2;
	
	//non-norms
	MockCustomer flake;
	
	public void setUp() throws Exception{
        super.setUp();
        
        PersonAgent c = new PersonAgent("Cashier");
        cashier = new MarketCashierRole(c, c.getName());
        c.addRole(ActionType.work, cashier);
        
        customer1 = new MockCustomer("MockCustomer1"); 
        customer2 = new MockCustomer("MockCustomer2");
        flake = new MockCustomer("MockFlake");
        
        employee = new MockEmployee("MockEmployee");
        
        
	}
	
		public void testOneNormalCustomerScenario(){	//one
			customer1.cashier = cashier;
			customer1.employee = employee;
			
			//check preconditions
            assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.getBills().size(), 0);
            assertEquals("Cashier should have 0 available money but does not.", cashier.getAvailableMoney(), 0.0);
            Map<String, Integer>inventory = new TreeMap<String, Integer>();
            inventory.put("steak", 1);		//cost of steak = 15.99
            inventory.put("soup", 2);		//cost of soup = 5.00
            
            
            //step 1 - create a bill
            cashier.msgComputeBill(inventory, customer1, employee);
            
            //postconditions 1/ preconditions 2
            assertEquals("MockEmployee should have an empty event log before the Cashier's scheduler is called. Instead, the MockEmployee's event log reads: "
                    + employee.log.toString(), 0, employee.log.size());
            assertEquals("Cashier should have 1 bill but does not.", cashier.getBills().size(), 1);
            assertTrue("Cashier should contain a check with state == computing. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.computing);
            assertTrue("Cashier should contain a check with the correct waiter. It doesn't.",
                    cashier.bills.get(0).getEmployee() == employee);
            assertTrue("Cashier should contain a check with the right customer in it. It doesn't.", 
                    cashier.bills.get(0).getCustomer() == customer1);
            
            //step 2 - check that scheduler returns true
            assertTrue("Cashier's scheduler should have returned true (needs to react to new check), but didn't.",
            		cashier.pickAndExecuteAnAction());
            
            //postconditions 2/preconditions 3
            assertTrue("Employee should have logged \"Received msgHereIsBill\" with amount = $25.99 but didn't. His log reads instead: " 
                    + employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Received msgHereIsBill from cashier for MockCustomer1. Amount = $25.99"));
            assertTrue("Cashier should contain a bill with state == waitingForPayment. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.waitingForPayment);
            assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
                                    + customer1.log.toString(), 0, customer1.log.size());
            
            
            //step 3
            cashier.msgHereIsPayment(100, customer1);	//cashAmountPaid, Customer
            
            //postconditions 3/preconditions 4
            assertTrue("Cashier should contain a bill with state == calculatingChange. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.calculatingChange);
            assertTrue("Cashier should contain a bill where the amount the customer paid = $100. It contains something else instead: $" 
                    + cashier.bills.get(0).getAmountPaid(), cashier.bills.get(0).getAmountPaid() == 100);
            
            //step 4
            assertTrue("Cashier's scheduler should have returned true (needs to react to customer's payment), but didn't.", 
                    cashier.pickAndExecuteAnAction());
            
            //postconditions 4/preconditions 5
            assertTrue("MockCustomer should have logged an event for receiving \"msgHereIsYourChange\" with the correct change, but his last event logged reads instead: " 
                    + customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received msgHereIsYourChange from cashier. Change = $74.01"));
            assertEquals("Cashier should still have 1 bill (need to make sure change is verified), but does not.", cashier.getBills().size(), 1);
            assertTrue("Cashier should contain a bill with state == paid. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.paid);
            
            
            //step 5
            cashier.msgChangeVerified(customer1);
            
            
            //postconditions 5/preconditions 6
            assertEquals("Cashier should have available money = $25.99 but does not.", cashier.getAvailableMoney(), 25.99);
            
            
            //step 6
            assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
                    cashier.pickAndExecuteAnAction());
            assertEquals("Cashier should have 0 bills but does not.", cashier.getBills().size(), 0);
            

		}
		
		public void testOneNormalBusinessScenario(){
			/* Here, the employee tells the cashier to compute a bill for a restaurant's inventory order.
			 * Once the cashier has computed the bill, the employee hands off the bill and the inventory 
			 * to the delivery man. The delivery man takes care of the rest of the order, so the cashier 
			 * can delete the bill as soon as he computes it and gives it to the employee.
			 */
			
			//check preconditions
            assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.getBills().size(), 0);
            assertEquals("Cashier should have 0 available money but does not.", cashier.getAvailableMoney(), 0.0);
            Map<String, Integer>inventory = new TreeMap<String, Integer>();
            inventory.put("steak", 1);		//cost of steak = 15.99
            inventory.put("soup", 2);		//cost of soup = 5.00
            
            
            //step 1 - create a bill
            cashier.msgComputeBill(inventory, "ellenRestaurant", employee);
            
            //postconditions 1/ preconditions 2
            assertEquals("MockEmployee should have an empty event log before the Cashier's scheduler is called. Instead, the MockEmployee's event log reads: "
                    + employee.log.toString(), 0, employee.log.size());
            assertEquals("Cashier should have 1 bill but does not.", cashier.getBills().size(), 1);
            assertTrue("Cashier should contain a bill with state == computing. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.computing);
            assertTrue("Cashier should contain a bill with the correct waiter. It doesn't.",
                    cashier.bills.get(0).getEmployee() == employee);
            assertTrue("Cashier should contain a bill with the right restaurant in it. It doesn't.", 
                    cashier.bills.get(0).getRestaurant().equalsIgnoreCase("ellenRestaurant"));
            
            //step 2 - check that scheduler returns true
            assertTrue("Cashier's scheduler should have returned true (needs to react to new bill), but didn't.",
            		cashier.pickAndExecuteAnAction());
            
            //postconditions 2/preconditions 3
            assertTrue("Employee should have logged \"Received msgHereIsBill\" with amount = $25.99 but didn't. His log reads instead: " 
                    + employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Received msgHereIsBill from cashier for ellenRestaurant. Amount = $25.99"));
            assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.getBills().size(), 0);

            
		}
		
		public void testTwoNormalCustomersScenario(){	//two
			
		}
		
		public void testTwoNormalBusinessesScenario(){
			
		}
		
		public void testOneCustomerOneBusinessScenario(){	//one, one
			
		}
		
		public void testTwoCustomersTwoBusinessesScenario(){	//two, two
			
		}
		
		public void testOneFlakeCustomerScenario(){	//one flake customer
			flake.cashier = cashier;
			flake.employee = employee;
			
			//check preconditions
            assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.getBills().size(), 0);
            Map<String, Integer>inventory = new TreeMap<String, Integer>();
            inventory.put("steak", 1);		//cost of steak = 15.99
            inventory.put("soup", 2);		//cost of soup = 5.00
            
            //step 1 - create a check
            cashier.msgComputeBill(inventory, flake, employee);
            
            //postconditions for step 1/preconditions for step 2
            assertEquals("MockEmployee should have an empty event log before the Cashier's scheduler is called. Instead, the MockEmployee's event log reads: "
                    + employee.log.toString(), 0, employee.log.size());
            assertEquals("Cashier should have 1 bill but does not.", cashier.getBills().size(), 1);
            assertTrue("Cashier should contain a bill with state == computing. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.computing);
            assertTrue("Cashier should contain a bill with the right customer in it. It doesn't.", 
                            cashier.bills.get(0).getCustomer() == flake);
            assertTrue("Cashier should contain a bill with the right employee in it. It doesn't.", 
                    cashier.bills.get(0).getEmployee() == employee);
            
            
            //step 2 - check that scheduler returns true 1st time
            assertTrue("Cashier's scheduler should have returned true (needs to react to new bill), but didn't.",
            		cashier.pickAndExecuteAnAction());

            //postconditions for step 2/preconditions for step 3
            assertTrue("Employee should have logged \"Received msgHereIsBill\" with amount = $25.99 but didn't. His log reads instead: " 
                    + employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Received msgHereIsBill from cashier for MockFlake. Amount = $25.99"));
            assertTrue("Cashier should contain a bill with state == waitingForPayment. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.waitingForPayment);
            assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
                                    + flake.log.toString(), 0, flake.log.size());
            
            
            //step 3
            cashier.msgHereIsPayment(20, flake);	//cashAmountPaid, Customer
            
            //postconditions step 3/preconditions step 4
            assertTrue("Cashier should contain a check with state == calculatingChange. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.calculatingChange);
            assertTrue("Cashier should contain a check where the amount the customer paid = $20. It contains something else instead: $" 
                    + cashier.bills.get(0).getAmountPaid(), cashier.bills.get(0).getAmountPaid() == 20);
            
            
            //step 4
            assertTrue("Cashier's scheduler should have returned true (needs to react to customer's payment), but didn't.", 
                    cashier.pickAndExecuteAnAction());

            //postconditions step 4
            assertTrue("MockCustomer should have logged an event for receiving \"NotEnoughCash\" with the correct amount owed, but his last event logged reads instead: " 
                    + flake.log.getLastLoggedEvent().toString(), flake.log.containsString("Received msgNotEnoughCash from cashier. Money Owed = $5.99"));
            assertTrue("Cashier should contain a bill with state == oweMoney. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.oweMoney);
            
            
            //step 5
            cashier.msgChangeVerified(flake);

            //postconditions 5/preconditions 6
            assertEquals("Cashier should have available money = $20 but does not.", cashier.getAvailableMoney(), 20.0);
            
            
            //step 6
            assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
                    cashier.pickAndExecuteAnAction());
            assertEquals("Cashier should have 0 bills but does not.", cashier.getBills().size(), 0);

		}
	
		
	
}