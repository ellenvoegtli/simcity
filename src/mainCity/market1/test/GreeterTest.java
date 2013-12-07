package mainCity.market1.test;

import java.util.Map;
import java.util.TreeMap;

import role.market1.Market1EmployeeRole;
import role.market1.Market1GreeterRole;
import role.market1.Market1EmployeeRole.CustomerState;
import mainCity.PersonAgent;
import mainCity.PersonAgent.ActionType;
import mainCity.market1.test.mock.MockCustomer;
import mainCity.market1.test.mock.MockEmployee;
import mainCity.market1.test.mock.MockEmployeeGui;
import junit.framework.TestCase;
import mainCity.restaurants.EllenRestaurant.test.mock.*;

public class GreeterTest extends TestCase {
	Market1GreeterRole greeter;
	MockEmployee employee1;
	MockEmployee employee2;
	MockCustomer customer1;
	MockCustomer customer2;
	
	MockCook cook;
	MockCashier cashier;
	
	public void setUp() throws Exception{
        super.setUp();
        
        PersonAgent d = new PersonAgent("Greeter");
        greeter = new Market1GreeterRole(d, d.getName());
        d.addRole(ActionType.work, greeter);

        customer1 = new MockCustomer("MockCustomer1");
        customer2 = new MockCustomer("MockCustomer2");
        employee1 = new MockEmployee("MockEmployee1");
        employee2 = new MockEmployee("MockEmployee2");  
        
        cook = new MockCook("Cook");
        cashier = new MockCashier("Cashier");
	}
	
	
		//=============================== NEXT TEST =========================================================================
		public void testOneCustomerOneEmployeeScenario(){
			greeter.addEmployee(employee1);
			
			//check preconditions
            assertEquals("Greeter should have 0 waitingCustomers in it. It doesn't.", greeter.getWaitingCustomers().size(), 0);
            assertEquals("Employee should have 1 myEmployees in it. It doesn't.", greeter.getEmployees().size(), 1);
            
            //step 1
            greeter.msgINeedInventory(customer1, 50, 50); 	//arbitrary values
            
            
            //postconditions 1/preconditions 2
            assertEquals("Greeter should have 1 waitingCustomers in it. It doesn't.", greeter.getWaitingCustomers().size(), 1);
            assertTrue("Cashier should contain a waitingCustomer with the right customer in it. It doesn't.", 
            		greeter.waitingCustomers.get(0).getCustomer() == customer1);
            
            //step 2
            assertTrue("Greeter's scheduler should have returned true (needs to react to new customer), but didn't.",
            		greeter.pickAndExecuteAnAction());
            
            
            //postconditions 2
            assertTrue("MockEmployee should have logged an event for receiving \"msgAssignedToCustomer\", but he didn't. His log reads instead: " 
                    + employee1.log.getLastLoggedEvent().toString(), employee1.log.containsString("Received msgAssignedToCustomer: MockCustomer1"));
            assertEquals("Greeter should have 0 waitingCustomers in it. It doesn't.", greeter.getWaitingCustomers().size(), 0);
            assertFalse("Greeter's scheduler should have returned false (nothing left to do), but didn't.",
            		greeter.pickAndExecuteAnAction());
		}
	
		
		
		
		//=============================== NEXT TEST =========================================================================
		public void testTwoCustomersOneEmployeeScenario(){
			greeter.addEmployee(employee1);
			
			//check preconditions
            assertEquals("Greeter should have 0 waitingCustomers in it. It doesn't.", greeter.getWaitingCustomers().size(), 0);
            assertEquals("Employee should have 1 myEmployees in it. It doesn't.", greeter.getEmployees().size(), 1);
            
            //step 1
            greeter.msgINeedInventory(customer1, 50, 50); 	//arbitrary values
            greeter.msgINeedInventory(customer2, 60, 60); 
            
            //postconditions 1/preconditions 2
            assertEquals("Greeter should have 2 waitingCustomers in it. It doesn't.", greeter.getWaitingCustomers().size(), 2);
            assertTrue("Cashier should contain a waitingCustomer with the right customer in it. It doesn't.", 
            		greeter.waitingCustomers.get(0).getCustomer() == customer1);
            assertTrue("Cashier should contain a waitingCustomer with the right customer in it. It doesn't.", 
            		greeter.waitingCustomers.get(1).getCustomer() == customer2);
            
            
            //step 2
            assertTrue("Greeter's scheduler should have returned true (needs to react to new customers), but didn't.",
            		greeter.pickAndExecuteAnAction());
            
            
            //postconditions 2/preconditions 3
            assertTrue("MockEmployee should have logged an event for receiving \"msgAssignedToCustomer\", but he didn't. His log reads instead: " 
                    + employee1.log.getLastLoggedEvent().toString(), employee1.log.containsString("Received msgAssignedToCustomer: MockCustomer1"));
            assertEquals("Greeter should have 1 waitingCustomers in it. It doesn't.", greeter.getWaitingCustomers().size(), 1);
			
            
            //step 3
            assertTrue("Greeter's scheduler should have returned true (needs to react to second customer), but didn't.",
            		greeter.pickAndExecuteAnAction());
            
            //postconditions 3
            assertTrue("MockEmployee should have logged an event for receiving \"msgAssignedToCustomer\", but he didn't. His log reads instead: " 
                    + employee1.log.getLastLoggedEvent().toString(), employee1.log.containsString("Received msgAssignedToCustomer: MockCustomer2"));
            assertEquals("Greeter should have 0 waitingCustomers in it. It doesn't.", greeter.getWaitingCustomers().size(), 0);
            assertFalse("Greeter's scheduler should have returned false (nothing left to do), but didn't.",
            		greeter.pickAndExecuteAnAction());
            
		}
		
		
		
		
		//=============================== NEXT TEST =========================================================================
		public void testTwoCustomersTwoEmployeesScenario(){
			greeter.addEmployee(employee1);
			greeter.addEmployee(employee2);
			
			//check preconditions
            assertEquals("Greeter should have 0 waitingCustomers in it. It doesn't.", greeter.getWaitingCustomers().size(), 0);
            assertEquals("Employee should have 2 myEmployees in it. It doesn't.", greeter.getEmployees().size(), 2);
            
            //step 1
            greeter.msgINeedInventory(customer1, 50, 50); 	//arbitrary values
            greeter.msgINeedInventory(customer2, 60, 60); 
            
            //postconditions 1/preconditions 2
            assertEquals("Greeter should have 2 waitingCustomers in it. It doesn't.", greeter.getWaitingCustomers().size(), 2);
            assertTrue("Cashier should contain a waitingCustomer with the right customer in it. It doesn't.", 
            		greeter.waitingCustomers.get(0).getCustomer() == customer1);
            assertTrue("Cashier should contain a waitingCustomer with the right customer in it. It doesn't.", 
            		greeter.waitingCustomers.get(1).getCustomer() == customer2);
            
            
            //step 2
            assertTrue("Greeter's scheduler should have returned true (needs to react to new customers), but didn't.",
            		greeter.pickAndExecuteAnAction());
            
            
            //postconditions 2/preconditions 3
            assertTrue("MockEmployee2 should have logged an event for receiving \"msgAssignedToCustomer\", but he didn't. His log reads instead: " 
                    + employee2.log.getLastLoggedEvent().toString(), employee2.log.containsString("Received msgAssignedToCustomer: MockCustomer1"));
            assertEquals("Greeter should have 1 waitingCustomers in it. It doesn't.", greeter.getWaitingCustomers().size(), 1);
			
            
            //step 3
            assertTrue("Greeter's scheduler should have returned true (needs to react to second customer), but didn't.",
            		greeter.pickAndExecuteAnAction());
            
            //postconditions 3
            assertTrue("MockEmployee1 should have logged an event for receiving \"msgAssignedToCustomer\", but he didn't. His log reads instead: " 
                    + employee1.log.getLastLoggedEvent().toString(), employee1.log.containsString("Received msgAssignedToCustomer: MockCustomer2"));
            assertEquals("Greeter should have 0 waitingCustomers in it. It doesn't.", greeter.getWaitingCustomers().size(), 0);
            assertFalse("Greeter's scheduler should have returned false (nothing left to do), but didn't.",
            		greeter.pickAndExecuteAnAction());
		}
		
		
		public void testOneBusinessOneEmployeeScenario(){
			greeter.addEmployee(employee1);
			greeter.setCashierArrived(true);
			
			//check preconditions
			Map<String, Integer>inventory = new TreeMap<String, Integer>();
            inventory.put("steak", 1);		//cost of steak = 15.99
            inventory.put("soup", 2);		//cost of soup = 5.00
            assertEquals("Greeter should have 0 waitingBusinesses in it. It doesn't.", greeter.getWaitingBusinesses().size(), 0);
            assertEquals("Employee should have 1 myEmployees in it. It doesn't.", greeter.getEmployees().size(), 1);
            
            //step 1
            greeter.msgINeedInventory("ellenRestaurant", cook, cashier, inventory);
            
            
            //postconditions 1/preconditions 2
            assertEquals("Greeter should have 1 waitingBusinesses in it. It doesn't.", greeter.getWaitingBusinesses().size(), 1);
            assertTrue("Cashier should contain a waitingBusiness with the right customer in it. It doesn't.", 
            		greeter.waitingBusinesses.get(0).getRestaurant().equalsIgnoreCase("ellenRestaurant"));
            
            //step 2
            assertTrue("Greeter's scheduler should have returned true (needs to react to new business), but didn't.",
            		greeter.pickAndExecuteAnAction());
            
            
            //postconditions 2
            assertTrue("MockEmployee should have logged an event for receiving \"msgAssignedToBusiness\", but he didn't. His log reads instead: " 
                    + employee1.log.getLastLoggedEvent().toString(), employee1.log.containsString("Received msgAssignedToBusiness: ellenRestaurant"));
            assertEquals("Greeter should have 0 waitingBusinesses in it. It doesn't.", greeter.getWaitingBusinesses().size(), 0);
            assertFalse("Greeter's scheduler should have returned false (nothing left to do), but didn't.",
            		greeter.pickAndExecuteAnAction());
		}
		
		public void testTwoBusinessesTwoEmployeesScenario(){
			
			greeter.addEmployee(employee1);
			greeter.addEmployee(employee2);
			greeter.setCashierArrived(true);
			
			//check preconditions
			Map<String, Integer>inventory = new TreeMap<String, Integer>();
            inventory.put("steak", 1);		//cost of steak = 15.99
            inventory.put("soup", 2);		//cost of soup = 5.00
            Map<String, Integer>inventory2 = new TreeMap<String, Integer>();
            inventory2.put("pizza", 1);		
            inventory2.put("pasta", 2);
            assertEquals("Greeter should have 0 waitingBusinesses in it. It doesn't.", greeter.getWaitingBusinesses().size(), 0);
            assertEquals("Employee should have 2 myEmployees in it. It doesn't.", greeter.getEmployees().size(), 2);
            
            //step 1
            greeter.msgINeedInventory("ellenRestaurant", cook, cashier, inventory);
            greeter.msgINeedInventory("enaRestaurant", cook, cashier, inventory2); 		//different name for testing
            
            
            //postconditions 1/preconditions 2
            assertEquals("Greeter should have 2 waitingBusinesses in it. It doesn't.", greeter.getWaitingBusinesses().size(), 2);
            assertTrue("Cashier should contain a waitingBusiness with the right customer in it. It doesn't.", 
            		greeter.waitingBusinesses.get(0).getRestaurant().equalsIgnoreCase("ellenRestaurant"));
            assertTrue("Cashier should contain a waitingBusiness with the right customer in it. It doesn't.", 
            		greeter.waitingBusinesses.get(1).getRestaurant().equalsIgnoreCase("enaRestaurant"));
            
            
            //step 2
            assertTrue("Greeter's scheduler should have returned true (needs to react to new businesses), but didn't.",
            		greeter.pickAndExecuteAnAction());
            
            
            //postconditions 2
            assertTrue("MockEmployee should have logged an event for receiving \"msgAssignedToBusiness\", but he didn't. His log reads instead: " 
                    + employee2.log.getLastLoggedEvent().toString(), employee2.log.containsString("Received msgAssignedToBusiness: ellenRestaurant"));
            assertEquals("Greeter should have 1 waitingBusinesses in it. It doesn't.", greeter.getWaitingBusinesses().size(), 1);
            
            
            //step 3
            assertTrue("Greeter's scheduler should have returned true (needs to react to 2nd business), but didn't.",
            		greeter.pickAndExecuteAnAction());
            
            //postconditions 3
            assertTrue("MockEmployee should have logged an event for receiving \"msgAssignedToBusiness\", but he didn't. His log reads instead: " 
                    + employee1.log.getLastLoggedEvent().toString(), employee1.log.containsString("Received msgAssignedToBusiness: enaRestaurant"));
            assertEquals("Greeter should have 0 waitingBusinesses in it. It doesn't.", greeter.getWaitingBusinesses().size(), 0);
            assertFalse("Greeter's scheduler should have returned false (nothing left to do), but didn't.",
            		greeter.pickAndExecuteAnAction());
            
		}
	
}