package mainCity.market.test;

import java.util.Map;
import java.util.TreeMap;

import role.market.*;
import role.market.MarketCashierRole.BillState;
import role.market.MarketCustomerRole.AgentState;
import role.market.MarketEmployeeRole.CustomerState;
import role.market.MarketEmployeeRole.WaiterState;
import mainCity.PersonAgent;
import mainCity.PersonAgent.ActionType;
import mainCity.market.MarketMenu;
import mainCity.market.test.mock.*;
import junit.framework.TestCase;


public class EmployeeTest extends TestCase {
	MarketEmployeeRole employee;
	MockEmployeeGui gui;
	MockCustomer customer1;
	MockCustomer customer2;
	MockCashier cashier;
	MarketMenu menu;
	
	public void setUp() throws Exception{
        super.setUp();
        
        PersonAgent d = new PersonAgent("Employee");
        employee = new MarketEmployeeRole(d, d.getName());
        d.addRole(ActionType.work, employee);

        customer1 = new MockCustomer("MockCustomer");
        gui = new MockEmployeeGui("MockEmployeeGui");
        employee.setGui(gui);
        gui.employee = employee;
        
        menu = new MarketMenu();
        employee.setMenu(menu);
        
        cashier = new MockCashier("MockCashier");
        employee.setCashier(cashier);
        
	}
	
		//=============================== NEXT TEST =========================================================================
		public void testOneNormalCustomerScenario(){
		
			
			//check preconditions
			Map<String, Integer>inventory = new TreeMap<String, Integer>();
            inventory.put("steak", 1);		//cost of steak = 15.99
            inventory.put("soup", 2);		//cost of soup = 5.00
            
            assertEquals("Employee should have 0 customers in it. It doesn't.", employee.getMyCustomers().size(), 0);
            assertTrue("Employee should be in state == doingNothing. He isn't. He is in state " + employee.getState() + ".",
                    employee.getState() == WaiterState.doingNothing);

			//step 1
            employee.msgAssignedToCustomer(customer1, 50, 50);		//arbitrary waiting room numbers
            
            //postconditions 1/preconditions 2
            assertEquals("MockCustomer should have an empty event log before the Employee's scheduler is called. Instead, the MockCustomer's event log reads: "
                    + customer1.log.toString(), 0, customer1.log.size());
            assertEquals("Employee should have 1 MyCustomer but does not.", employee.getMyCustomers().size(), 1);
            assertTrue("Employee should contain a customer with state == newCustomer. It doesn't.",
                    employee.myCustomers.get(0).getState() == CustomerState.newCustomer);
            assertTrue("Cashier should contain a check with the right customer in it. It doesn't.", 
            		employee.myCustomers.get(0).getCustomer() == customer1);
            
            //step 2
            assertTrue("Employee's scheduler should have returned true (needs to react to new customer), but didn't.",
            		employee.pickAndExecuteAnAction());
            
            //postconditions 2/preconditions 3
            assertTrue("Customer should be in state == busy. He isn't.",
                    employee.getState() == WaiterState.busy);
            assertTrue("MockEmployeeGui should have logged \"Told to DoPickUpWaitingCustomer\" but didn't.", 
            		gui.log.containsString("Gui told to DoPickUpWaitingCustomer by agent."));
            assertTrue("MockEmployeeGui should have logged \"Told to DoGoToStation\" but didn't. His log reads instead: " 
                    + gui.log.getLastLoggedEvent().toString(), gui.log.containsString("Gui told to DoGoToStation by agent."));
            assertTrue("MockCustomer should have logged an event for receiving \"msgFollowMe\", but he didn't.", 
            		customer1.log.containsString("Received msgFollowMe from Employee"));
            assertTrue("MockCustomer should have logged an event for receiving \"msgMayITakeYourOrder\", but his last event logged reads instead: " 
                    + customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received msgMayITakeYourOrder from Employee"));
            assertTrue("Employee should contain a customer with state == waitingForOrder. It doesn't.",
                    employee.myCustomers.get(0).getState() == CustomerState.waitingForOrder);
            assertFalse("Employee's scheduler should have returned false (waiting for order), but didn't.",
            		employee.pickAndExecuteAnAction());
            
            
            //step 3
            employee.msgHereIsMyOrder(customer1, inventory);
            
            //postconditions 3/preconditions 4
            assertTrue("Employee should contain a customer with state == ordered. It doesn't.",
                    employee.myCustomers.get(0).getState() == CustomerState.ordered);
            
            
            //step 4
            assertTrue("Employee's scheduler should have returned true (needs to react), but didn't.",
            		employee.pickAndExecuteAnAction());
            
            //postconditions 4/preconditions 5
            assertTrue("Employee should contain a customer with state == waitingForBill. It doesn't.",
                    employee.myCustomers.get(0).getState() == CustomerState.waitingForBill);
            assertTrue("MockEmployeeGui should have logged \"Told to DoGoToCashier\" but didn't. His log reads instead: " 
                    + gui.log.getLastLoggedEvent().toString(), gui.log.containsString("Gui told to DoGoToCashier by agent."));
            assertTrue("MockCashier should have logged an event for receiving \"msgComputeBill\", but his last event logged reads instead: " 
                    + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgComputeBill from Employee for MockCustomer"));
            
            
            //step 5
            employee.msgHereIsBill(customer1, 25.99);
            
            //postconditions 5/preconditions 6
            assertTrue("Employee should contain a customer with the right customer in it. It doesn't.", 
                    employee.myCustomers.get(0).getCustomer() == customer1);
            assertTrue("Employee should contain a customer with the right bill amount in it. It doesn't.", 
                    employee.myCustomers.get(0).getBillAmount() == 25.99);
            assertTrue("Employee should contain a customer with state == gotCheckFromCashier. It doesn't.",
                    employee.myCustomers.get(0).getState() == CustomerState.gotCheckFromCashier);
            
            
            //step 6
            employee.setState(WaiterState.doingNothing);	//since it won't be called in the scheduler
            assertTrue("Employee's scheduler should have returned true (needs to react), but didn't.",
            		employee.pickAndExecuteAnAction());
            
            //postconditions 6/preconditions 7
            assertTrue("MockEmployeeGui should have logged \"Told to DoFulfill\" but didn't.", 
            		gui.log.containsString("Gui told to DoFulfillOrder by agent."));
            assertTrue("Employee should contain a customer with state == fulfillingOrder. It doesn't.",
                    employee.myCustomers.get(0).getState() == CustomerState.fulfillingOrder);
            assertFalse("Employee's scheduler should have returned false (waiting for order to be fulfilled), but didn't.",
            		employee.pickAndExecuteAnAction());
            
            
            //step 7
            employee.msgOrderFulfilled(employee.myCustomers.get(0));
            
            //postconditions 7/preconditions 8
            assertTrue("Employee should contain a customer with state == doneFulfillingOrder. It doesn't.",
                    employee.myCustomers.get(0).getState() == CustomerState.doneFulfillingOrder);
            
            
            //step 8
            employee.setState(WaiterState.doingNothing);	//since it won't be called in the scheduler
            assertTrue("Employee's scheduler should have returned true (needs to react), but didn't.",
            		employee.pickAndExecuteAnAction());
            
            //postconditions 8/preconditions 9
            assertTrue("MockEmployeeGui should have logged an event for receiving \"Told to go to station\", but his last event logged reads instead: " 
                    + gui.log.getLastLoggedEvent().toString(), gui.log.containsString("Gui told to DoGoToStation by agent."));
            assertTrue("MockCustomer should have logged an event for receiving \"msgHereIsYourOrder\" for the correct bill amount, but his last event logged reads instead: " 
                    + customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received msgHereIsYourOrder. Amount = $25.99"));
            assertTrue("Employee should contain a customer with state == gotOrderAndBill. It doesn't.",
                    employee.myCustomers.get(0).getState() == CustomerState.gotOrderAndBill);
            
            //step 9
            employee.msgDoneAndLeaving(customer1);
            
            //postconditions 9/preconditions 10
            assertTrue("Employee should contain a customer with state == leaving. It doesn't.",
                    employee.myCustomers.get(0).getState() == CustomerState.leaving);
            
            //step 10
            employee.setState(WaiterState.doingNothing);
            assertTrue("Employee's scheduler should have returned true (needs to react), but didn't.",
            		employee.pickAndExecuteAnAction());
            
            //postconditions 10
            assertEquals("Employee should have 0 customers in it. It doesn't.", employee.getMyCustomers().size(), 0);

		}
		
		
		/*
		//=============================== NEXT TEST =========================================================================
		public void testTwoNormalCustomersScenario(){
			
		}
		*/
		
		
		/*
		//=============================== NEXT TEST =========================================================================
		public void testOneNormalBusinessScenario(){
			
			
		}*/
		
		
		/*
		//=============================== NEXT TEST =========================================================================
		public void testTwoNormalBusinessesScenario(){
			
		}
		*/
		
		/*
		//=============================== NEXT TEST =========================================================================
		public void testOneCustomerOneBusinessScenario(){
			
		}*/
	
}