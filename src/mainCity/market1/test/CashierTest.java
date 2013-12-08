package mainCity.market1.test;

import java.util.*;

import role.ellenRestaurant.EllenCashierRole.CheckState;
import role.market1.Market1CashierRole;
import role.market1.Market1CashierRole.BillState;
import junit.framework.*;
import mainCity.PersonAgent;
import mainCity.PersonAgent.ActionType;
import mainCity.market1.*;
import mainCity.market1.test.mock.*;


public class CashierTest extends TestCase {
	Market1CashierRole cashier;
	MockEmployee employee;
	MockCustomer customer1;
	MockCustomer customer2;
	
	//non-norms
	MockCustomer flake;
	
	public void setUp() throws Exception{
        super.setUp();
        
        PersonAgent c = new PersonAgent("Cashier");
        cashier = new Market1CashierRole(c, c.getName());
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
            assertEquals("Cashier should have 0 available money but does not.", cashier.getCash(), 0.0);
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
            assertTrue("Cashier should contain a bill where the amount the customer is charged = $25.99. It contains something else instead: $" 
                    + cashier.bills.get(0).getAmountCharged(), cashier.bills.get(0).getAmountCharged() == 25.99);
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
            assertEquals("Cashier should have available money = $25.99 but does not.", cashier.getCash(), 25.99);
            
            
            //step 6
            assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
                    cashier.pickAndExecuteAnAction());
            assertEquals("Cashier should have 0 bills but does not.", cashier.getBills().size(), 0);
            

		}
		
		
		//================= FIRST TEST ========================================================================================
		public void testOneNormalBusinessScenario(){
			/* Here, the employee tells the cashier to compute a bill for a restaurant's inventory order.
			 * Once the cashier has computed the bill, the employee hands off the bill and the inventory 
			 * to the delivery man. The delivery man takes care of the rest of the order, so the cashier 
			 * can delete the bill as soon as he computes it and gives it to the employee.
			 */
			
			//check preconditions
            assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.getBills().size(), 0);
            assertEquals("Cashier should have 0 available money but does not.", cashier.getCash(), 0.0);
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
		
		
		
		//=================NEXT TEST========================================================================================
		public void testTwoNormalCustomersScenario(){	//two
			customer1.cashier = cashier;
			customer1.employee = employee;
			customer2.cashier = cashier;
			customer2.employee = employee;
			
			//check preconditions
            assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.getBills().size(), 0);
            assertEquals("Cashier should have 0 available money but does not.", cashier.getCash(), 0.0);
            Map<String, Integer>inventory1 = new TreeMap<String, Integer>();
            inventory1.put("steak", 1);		//cost of steak = 15.99
            inventory1.put("soup", 2);		//cost of soup = 5.00
            Map<String, Integer>inventory2 = new TreeMap<String, Integer>();
            inventory2.put("fries", 10);		//cost of fries = 0.50
            inventory2.put("burger", 5);		//cost of burger = 6.00
            
            
            //step 1 - create a bill
            cashier.msgComputeBill(inventory1, customer1, employee);
            
            //postconditions 1/ preconditions 2
            assertEquals("MockEmployee should have an empty event log before the Cashier's scheduler is called. Instead, the MockEmployee's event log reads: "
                    + employee.log.toString(), 0, employee.log.size());
            assertEquals("Cashier should have 1 bill but does not.", cashier.getBills().size(), 1);
            assertTrue("Cashier should contain 1st bill with state == computing. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.computing);
            assertTrue("Cashier should contain 1st bill with the correct waiter. It doesn't.",
                    cashier.bills.get(0).getEmployee() == employee);
            assertTrue("Cashier should contain 1st bill with the right customer in it. It doesn't.", 
                    cashier.bills.get(0).getCustomer() == customer1);
            
            
            
            //step 2 - check that scheduler returns true
            assertTrue("Cashier's scheduler should have returned true (needs to react to new bill), but didn't.",
            		cashier.pickAndExecuteAnAction());
            
            //postconditions 2/preconditions 3
            assertTrue("Employee should have logged \"Received msgHereIsBill\" with amount = $25.99 but didn't. His log reads instead: " 
                    + employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Received msgHereIsBill from cashier for MockCustomer1. Amount = $25.99"));
            assertTrue("Cashier should contain a bill where the amount the customer is charged = $25.99. It contains something else instead: $" 
                    + cashier.bills.get(0).getAmountCharged(), cashier.bills.get(0).getAmountCharged() == 25.99);
            assertTrue("Cashier should contain a bill with state == waitingForPayment. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.waitingForPayment);
            assertEquals("MockCustomer1 should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
                                    + customer1.log.toString(), 0, customer1.log.size());
            
            
            //step 3 - add another bill
            cashier.msgComputeBill(inventory2, customer2, employee);

            //postconditions 3/preconditions 4
            assertTrue("Cashier should contain 2nd bill with state == computing. It doesn't.",
                    cashier.bills.get(1).getState() == BillState.computing);
            assertTrue("Cashier should contain 2nd bill with the correct waiter. It doesn't.",
                    cashier.bills.get(1).getEmployee() == employee);
            assertTrue("Cashier should contain 2nd bill with the right customer in it. It doesn't.", 
                    cashier.bills.get(1).getCustomer() == customer2);
            
            
            //step 4
            assertTrue("Cashier's scheduler should have returned true (needs to react to new bill), but didn't.",
            		cashier.pickAndExecuteAnAction());
            
            //postconditions 4/postconditions 5
            assertTrue("Employee should have logged \"Received msgHereIsBill\" with amount = $35.00 but didn't. His log reads instead: " 
                    + employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Received msgHereIsBill from cashier for MockCustomer2. Amount = $35.0"));
            assertTrue("Cashier should contain 2nd bill where the amount the customer is charged = $35.00. It contains something else instead: $" 
                    + cashier.bills.get(1).getAmountCharged(), cashier.bills.get(1).getAmountCharged() == 35.0);
            assertTrue("Cashier should contain 2nd bill with state == waitingForPayment. It doesn't.",
                    cashier.bills.get(1).getState() == BillState.waitingForPayment);
            assertEquals("MockCustomer2 should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
                                    + customer2.log.toString(), 0, customer2.log.size());
            
            
            //step 3
            cashier.msgHereIsPayment(100, customer1);	//cashAmountPaid, Customer
            cashier.msgHereIsPayment(50, customer2);
            
            //postconditions 3/preconditions 4
            assertTrue("Cashier should contain 1st bill with state == calculatingChange. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.calculatingChange);
            assertTrue("Cashier should contain 1st bill where the amount the customer paid = $100. It contains something else instead: $" 
                    + cashier.bills.get(0).getAmountPaid(), cashier.bills.get(0).getAmountPaid() == 100.0);
            
            assertTrue("Cashier should contain 2nd bill with state == calculatingChange. It doesn't.",
                    cashier.bills.get(1).getState() == BillState.calculatingChange);
            assertTrue("Cashier should contain 2nd bill where the amount the customer paid = $50.0. It contains something else instead: $" 
                    + cashier.bills.get(1).getAmountPaid(), cashier.bills.get(1).getAmountPaid() == 50.0);
            
            
            //step 4
            assertTrue("Cashier's scheduler should have returned true (needs to react to customers' payments), but didn't.", 
                    cashier.pickAndExecuteAnAction());
            
            //postconditions 4/preconditions 5
            assertTrue("MockCustomer1 should have logged an event for receiving \"msgHereIsYourChange\" with the correct change, but his last event logged reads instead: " 
                    + customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received msgHereIsYourChange from cashier. Change = $74.01"));
            assertEquals("Cashier should still have 2 bills, but does not.", cashier.getBills().size(), 2);
            assertTrue("Cashier should contain a bill with state == paid. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.paid);
            
            //step 5
            assertTrue("Cashier's scheduler should have returned true (needs to react to customer's payment), but didn't.", 
                    cashier.pickAndExecuteAnAction());
            
            //postconditions 5/preconditions 6
            assertTrue("MockCustomer2 should have logged an event for receiving \"msgHereIsYourChange\" with the correct change, but his last event logged reads instead: " 
                    + customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Received msgHereIsYourChange from cashier. Change = $15.0"));
            assertEquals("Cashier should still have 2 bills (need to make sure change is verified), but does not.", cashier.getBills().size(), 2);
            assertTrue("Cashier should contain a bill with state == paid. It doesn't.",
                    cashier.bills.get(1).getState() == BillState.paid);
            
            
            //step 6
            cashier.msgChangeVerified(customer1);
            
            
            //postconditions 6/preconditions 7
            assertEquals("Cashier should have available money = $25.99 but does not.", cashier.getCash(), 25.99);
            assertFalse("Cashier's scheduler should have returned false (no actions to do), but didn't.", 
                    cashier.pickAndExecuteAnAction());
            assertEquals("Cashier should have 1 bill but does not.", cashier.getBills().size(), 1);
            
            
            //step 7
            cashier.msgChangeVerified(customer2);
            
            //postconditions 7
            assertEquals("Cashier should have available money = $60.99 but does not.", cashier.getCash(), 60.99);
            assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
                    cashier.pickAndExecuteAnAction());
            assertEquals("Cashier should have 0 bills but does not.", cashier.getBills().size(), 0);
		}
		
		
		//=================NEXT TEST========================================================================================
		public void testTwoNormalBusinessesScenario(){
			
			//check preconditions
            assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.getBills().size(), 0);
            assertEquals("Cashier should have 0 available money but does not.", cashier.getCash(), 0.0);
            Map<String, Integer>inventory1 = new TreeMap<String, Integer>();
            inventory1.put("steak", 1);		//cost of steak = 15.99
            inventory1.put("soup", 2);		//cost of soup = 5.00
            Map<String, Integer>inventory2 = new TreeMap<String, Integer>();
            inventory2.put("fries", 10);		//cost of fries = 0.50
            inventory2.put("burger", 5);		//cost of burger = 6.00
            
            
            //step 1 - create 2 bills
            cashier.msgComputeBill(inventory1, "ellenRestaurant", employee);
            cashier.msgComputeBill(inventory2, "enaRestaurant", employee);
            
            //postconditions 1/ preconditions 2
            assertEquals("MockEmployee should have an empty event log before the Cashier's scheduler is called. Instead, the MockEmployee's event log reads: "
                    + employee.log.toString(), 0, employee.log.size());
            assertEquals("Cashier should have 2 bills but does not.", cashier.getBills().size(), 2);
            assertTrue("Cashier should contain 1st bill with state == computing. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.computing);
            assertTrue("Cashier should contain 1st bill with the correct waiter. It doesn't.",
                    cashier.bills.get(0).getEmployee() == employee);
            assertTrue("Cashier should contain 1st bill with the right restaurant in it. It doesn't.", 
                    cashier.bills.get(0).getRestaurant().equalsIgnoreCase("ellenRestaurant"));
            
            assertTrue("Cashier should contain 2nd bill with state == computing. It doesn't.",
                    cashier.bills.get(1).getState() == BillState.computing);
            assertTrue("Cashier should contain 2nd bill with the correct waiter. It doesn't.",
                    cashier.bills.get(1).getEmployee() == employee);
            assertTrue("Cashier should contain 2nd bill with the right restaurant in it. It doesn't.", 
                    cashier.bills.get(1).getRestaurant().equalsIgnoreCase("enaRestaurant"));
            
            
            
            //step 2 - check that scheduler returns true
            assertTrue("Cashier's scheduler should have returned true (needs to react to new bills), but didn't.",
            		cashier.pickAndExecuteAnAction());
            
            //postconditions 2/preconditions 3
            assertTrue("Employee should have logged \"Received msgHereIsBill\" with amount = $25.99 but didn't.", 
            		employee.log.containsString("Received msgHereIsBill from cashier for ellenRestaurant. Amount = $25.99"));
            assertEquals("Cashier should now have 1 bill but does not.", cashier.getBills().size(), 1);
            

            //step 3
            assertTrue("Cashier's scheduler should have returned true (needs to react to new bill), but didn't.",
            		cashier.pickAndExecuteAnAction());
            
            //postconditions 3/preconditions 4
            assertTrue("Employee should have logged \"Received msgHereIsBill\" with amount = $35.00 but didn't.", 
            		employee.log.containsString("Received msgHereIsBill from cashier for enaRestaurant. Amount = $35.0"));
            assertEquals("Cashier should now have 0 bills but does not.", cashier.getBills().size(), 0);
            assertFalse("Cashier's scheduler should have returned false (nothing left to do), but didn't.",
            		cashier.pickAndExecuteAnAction());
		}
		
		
		
		//=================NEXT TEST========================================================================================
		public void testOneCustomerOneBusinessScenario(){	//one, one
            customer1.cashier = cashier;
			customer1.employee = employee;

			//check preconditions
            assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.getBills().size(), 0);
            assertEquals("Cashier should have 0 available money but does not.", cashier.getCash(), 0.0);
            Map<String, Integer>inventory1 = new TreeMap<String, Integer>();
            inventory1.put("pizza", 1);		//cost of pizza = 8.99
            inventory1.put("pasta", 2);		//cost of pasta = 20.00
            Map<String, Integer>inventory2 = new TreeMap<String, Integer>();
            inventory2.put("steak", 1);		//cost of steak = 15.99
            inventory2.put("soup", 2);		//cost of soup = 5.00
            
            
            
            
            //step 1 - create a bill for restaurant, bill for customer
            cashier.msgComputeBill(inventory1, "ellenRestaurant", employee);
            cashier.msgComputeBill(inventory2, customer1, employee);

            
            //postconditions 1/ preconditions 2
            assertEquals("MockEmployee should have an empty event log before the Cashier's scheduler is called. Instead, the MockEmployee's event log reads: "
                    + employee.log.toString(), 0, employee.log.size());
            assertEquals("Cashier should have 2 bills but does not.", cashier.getBills().size(), 2);
            assertTrue("Cashier should contain 1st bill with state == computing. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.computing);
            assertTrue("Cashier should contain 1st bill with the correct waiter. It doesn't.",
                    cashier.bills.get(0).getEmployee() == employee);
            assertTrue("Cashier should contain 1st bill with the right restaurant in it. It doesn't.", 
                    cashier.bills.get(0).getRestaurant().equalsIgnoreCase("ellenRestaurant"));
            
            assertTrue("Cashier should contain 1st bill with state == computing. It doesn't.",
                    cashier.bills.get(1).getState() == BillState.computing);
            assertTrue("Cashier should contain 1st bill with the correct waiter. It doesn't.",
                    cashier.bills.get(1).getEmployee() == employee);
            assertTrue("Cashier should contain 1st bill with the right customer in it. It doesn't.", 
                    cashier.bills.get(1).getCustomer() == customer1);
            
            
            
            //step 2 - check that scheduler returns true
            assertTrue("Cashier's scheduler should have returned true (needs to react to new bills), but didn't.",
            		cashier.pickAndExecuteAnAction());
            
            //postconditions 2/preconditions 3
            assertTrue("Employee should have logged \"Received msgHereIsBill\" from restaurant with amount = $48.99 but didn't. His last event logged reads instead: " 
                    + employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Received msgHereIsBill from cashier for ellenRestaurant. Amount = $48.99"));
            assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.getBills().size(), 1);
            
            
            //step 3
            assertTrue("Cashier's scheduler should have returned true (needs to react to bill), but didn't.",
            		cashier.pickAndExecuteAnAction());
            
            
            //postconditions 3/preconditions 4
            assertTrue("Employee should have logged \"Received msgHereIsBill\" from customer with amount = $25.99 but didn't. His last event logged reads instead: " 
            		+ employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Received msgHereIsBill from cashier for MockCustomer1. Amount = $25.99"));
            
            assertEquals("Cashier should still have 1 bill in it. It doesn't.", cashier.getBills().size(), 1);
            assertTrue("Cashier should contain a bill where the amount the customer is charged = $25.99. It contains something else instead: $" 
                    + cashier.bills.get(0).getAmountCharged(), cashier.bills.get(0).getAmountCharged() == 25.99);
            assertTrue("Cashier should contain a bill with state == waitingForPayment. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.waitingForPayment);
            assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
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
            assertEquals("Cashier should have available money = $25.99 but does not.", cashier.getCash(), 25.99);
            
            
            //step 6
            assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
                    cashier.pickAndExecuteAnAction());
            assertEquals("Cashier should have 0 bills but does not.", cashier.getBills().size(), 0);
			
		}
		
		
		
		
		//=================NEXT TEST========================================================================================
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
            assertTrue("Cashier should contain a bill where the amount the customer is charged = $25.99. It contains something else instead: $" 
                    + cashier.bills.get(0).getAmountCharged(), cashier.bills.get(0).getAmountCharged() == 25.99);
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
            assertEquals("Cashier should have available money = $20 but does not.", cashier.getCash(), 20.0);
            
            
            //step 6
            assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
                    cashier.pickAndExecuteAnAction());
            assertEquals("Cashier should have 0 bills but does not.", cashier.getBills().size(), 0);

		}
	
		
		
		//=================NEXT TEST========================================================================================
		public void testOneUnhappyCustomerScenario(){
			/*
			 * Customer gets the bill, and then (hypothetically) they don't think they were charged the right amount,
			 * so they ask the cashier to recompute the bill.
			 * If the cashier recomputes and gets that the bill was too high or too low, they send back another 
			 * HereIsBill message to the customer. We, however, will look at the last scenario in this test: when 
			 * the bill was verified AGAIN by the cashier to be the correct amount. In this scenario, the cashier 
			 * recalculates, gets the same amount as the first time, and then sends a message "HereIsFinalBill" 
			 * which the customer MUST PAY (non-negotiable).
			 */
			
			customer1.cashier = cashier;
			customer1.employee = employee;
			
			//check preconditions
            assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.getBills().size(), 0);
            assertEquals("Cashier should have 0 available money but does not.", cashier.getCash(), 0.0);
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
            assertTrue("Cashier should contain a bill where the amount the customer is charged = $25.99. It contains something else instead: $" 
                    + cashier.bills.get(0).getAmountCharged(), cashier.bills.get(0).getAmountCharged() == 25.99);
            assertTrue("Cashier should contain a bill with state == waitingForPayment. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.waitingForPayment);
            assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
                                    + customer1.log.toString(), 0, customer1.log.size());
            
            
            //step 3
            /*
             * Now, let's pretend that the customer receives the check from the employee, calculates it on his own,
             * and thinks it's the wrong amount (too high). He then sends a simple message, "PleaseRecalculateBill".
             */
            cashier.msgPleaseRecalculateBill(customer1);
            
            
            //postconditions 3/preconditions 4
            assertEquals("Cashier should still have 1 bill but does not.", cashier.getBills().size(), 1);
            assertTrue("Cashier should contain a bill with state == recomputingBill. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.recomputingBill);

            
            /*
             * Let's say, for this next step, that the cashier computes it again to be the same value.
             * (See reasoning above.)
             */
            
            //step 4
            assertTrue("Cashier's scheduler should have returned true (needs to react to customer's request), but didn't.", 
                    cashier.pickAndExecuteAnAction());
            
            //postconditions 4/preconditions 5
            assertTrue("MockCustomer should have logged an event for receiving \"msgHereIsFinalBill\" with the correct change, but his last event logged reads instead: " 
                    + customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received msgHereIsFinalBill from cashier. Amount = $25.99"));
            assertEquals("Cashier should still have 1 bill (need to make sure change is verified), but does not.", cashier.getBills().size(), 1);
            assertTrue("Cashier should contain a bill with state == waitingForPayment. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.waitingForPayment);
            
            
            //step 5
            cashier.msgHereIsPayment(50, customer1);		//assume he has enough money; we already tested flake
            
            //postconditions 5/preconditions 6
            assertTrue("Cashier should contain a bill with state == calculatingChange. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.calculatingChange);
            assertTrue("Cashier should contain a bill where the amount the customer paid = $50.0. It contains something else instead: $" 
                    + cashier.bills.get(0).getAmountPaid(), cashier.bills.get(0).getAmountPaid() == 50.0);
            
            
            
            //step 6
            assertTrue("Cashier's scheduler should have returned true (needs to react to customer's payment), but didn't.", 
                    cashier.pickAndExecuteAnAction());
            
            //postconditions 6/preconditions 7
            assertTrue("MockCustomer should have logged an event for receiving \"msgHereIsYourChange\" with the correct change, but his last event logged reads instead: " 
                    + customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received msgHereIsYourChange from cashier. Change = $24.01"));
            assertTrue("Cashier should contain a bill with state == paid. It doesn't.",
                    cashier.bills.get(0).getState() == BillState.paid);
            assertFalse("Cashier's scheduler should have returned false (waiting for verification), but didn't.", 
                    cashier.pickAndExecuteAnAction());
            
            
            //step 5
            cashier.msgChangeVerified(customer1);
            
            //postconditions 5/preconditions 6
            assertEquals("Cashier should have available money = $25.99 but does not.", cashier.getCash(), 25.99);
            assertEquals("Cashier should have 0 bills but does not.", cashier.getBills().size(), 0);
            assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
                    cashier.pickAndExecuteAnAction());
            
		}
		
	
}