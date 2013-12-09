package mainCity.market1.test;

import java.util.Map;
import java.util.TreeMap;

import role.market.MarketCustomerRole;
import role.market.MarketCustomerRole.AgentEvent;
import role.market.MarketCustomerRole.AgentState;
import role.market.MarketCustomerRole.BillState;
import mainCity.PersonAgent;
import mainCity.PersonAgent.ActionType;
import mainCity.market1.gui.CustomerGui;
import mainCity.market1.test.mock.MockCashier;
import mainCity.market1.test.mock.MockCustomerGui;
import mainCity.market1.test.mock.MockEmployee;
import mainCity.market1.test.mock.MockGreeter;
import junit.framework.TestCase;


public class CustomerTest extends TestCase {
	MarketCustomerRole customer;
	MarketCustomerRole flake;
	MockCustomerGui gui;
	MockEmployee employee;
	MockGreeter greeter;
	MockCashier cashier;
	
	
	public void setUp() throws Exception{
        super.setUp();
        
        PersonAgent d = new PersonAgent("Customer");
        customer = new MarketCustomerRole(d, d.getName());
        d.addRole(ActionType.work, customer);
        
        PersonAgent c = new PersonAgent("Flake");
        flake = new MarketCustomerRole(c, c.getName());
        c.addRole(ActionType.work, flake);
 
        gui = new MockCustomerGui("MockCustomerGui");
        customer.setGui(gui);
        flake.setGui(gui);
        
        employee = new MockEmployee("MockEmployee");
        greeter = new MockGreeter("MockGreeter");
        cashier = new MockCashier("MockCashier");
        
	}
	
		//=============================== NEXT TEST =========================================================================
		public void testNormalScenario(){
			customer.setCashier(cashier);
			customer.setHost(greeter);
			//customer.setEmployee(employee);
			
			//check preconditions
			assertEquals("Customer should have $100 in myCash but does not.", customer.getMyCash(), 100.0);
			Map<String, Integer>inventory = new TreeMap<String, Integer>();
            inventory.put("steak", 1);		//cost of steak = 15.99
            inventory.put("soup", 2);		//cost of soup = 5.00
            assertTrue("Customer should be in state == doingNothing. He isn't.",
                    customer.getState() == AgentState.DoingNothing);
            
            
			//step 1
			customer.goGetInventory(inventory);
			
			//postconditions 1/preconditions 2
			assertTrue("Customer should have event == toldToGetInventory. He doesn't.",
                    customer.getEvent() == AgentEvent.toldToGetInventory);
			assertEquals("MockGreeter should have an empty event log before the Customer's scheduler is called. Instead, the MockGreeters's event log reads: "
                    + greeter.log.toString(), 0, greeter.log.size());
			
			//step 2
			assertTrue("Customer's scheduler should have returned true (needs to react to new message), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//postconditions 2/preconditions 3
			assertTrue("MockCustomerGui should have logged \"Told to DoGoToWaitingArea\" but didn't. His log reads instead: " 
                    + gui.log.getLastLoggedEvent().toString(), gui.log.containsString("Gui told to DoGoToWaitingArea by agent."));
			assertTrue("MockGreeter should have logged \"Received msgINeedInventory\" but didn't. His log reads instead: " 
                    + greeter.log.getLastLoggedEvent().toString(), greeter.log.containsString("Received msgINeedInventory from Customer."));
			assertTrue("Customer should be in state == waitingInMarket. He isn't.",
                    customer.getState() == AgentState.WaitingInMarket);
			assertFalse("Customer's scheduler should have returned false (waiting), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//step 3
			customer.msgFollowMe(employee, 50, 50);		//random numbers, but we can check them
			
			
			//postconditions 3/preconditions 4
			assertTrue("Customer should have event == followEmployee. He doesn't.",
                    customer.getEvent() == AgentEvent.followEmployee);
			assertEquals("Customer's stationX should equal 50 but it does not.", customer.getStationX(), 50);
			assertEquals("Customer's stationY should equal 30 but it does not.", customer.getStationY(), 30);
			assertEquals("Customer's employee should equal MockEmployee, but it does not.", customer.getEmployee(), employee);
			
			
			//step 4
			assertTrue("Customer's scheduler should have returned true (needs to react to new message), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//postconditions 4/preconditions 5
			assertTrue("MockCustomerGui should have logged \"Told to DoGoToEmployeeStation\" but didn't. His log reads instead: " 
                    + gui.log.getLastLoggedEvent().toString(), gui.log.containsString("Gui told to DoGoToEmployeeStation by agent."));
			assertTrue("Customer should be in state == GoingToStation. He isn't.",
                    customer.getState() == AgentState.GoingToStation);
			assertFalse("Customer's scheduler should have returned false (waiting), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			
			//step 5
			customer.msgMayITakeYourOrder(employee);
			
			//postconditions 5/preconditions 6
			assertTrue("Customer should have event == askedForOrder. He doesn't.",
                    customer.getEvent() == AgentEvent.askedForOrder);
			
			
			//step 6
			assertTrue("Customer's scheduler should have returned true (needs to react to new message), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//postconditions 6/preconditions 7
			assertTrue("MockEmployee should have logged \"Received HereIsMyOrder\" but didn't. His log reads instead: " 
                    + employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Received msgHereIsMyOrder from Customer"));
			assertTrue("Customer should be in state == OrderProcessing. He isn't.",
                    customer.getState() == AgentState.OrderProcessing);
			assertFalse("Customer's scheduler should have returned false (waiting), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			
			//step 7
			customer.msgHereIsYourOrder(inventory, 25.99);
			
			//postconditions 7/preconditions 8
			assertTrue("Customer should have a bill with a bill charge. He doesn't.",
					customer.getBill().getCharge() == 25.99);
			assertTrue("Customer should have event == gotOrderAndBill. He doesn't.",
                    customer.getEvent() == AgentEvent.gotOrderAndBill);
			assertTrue("Customer should have a bill with state = unpaid. He doesn't.",
                    customer.getBill().getState() == BillState.unpaid);
			
			
			//step 8
			assertTrue("Customer's scheduler should have returned true (needs to react to new message), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//postconditions 8/preconditions 9
			assertTrue("MockCustomerGui should have logged \"Told to DoGoToCashier\" but didn't. His log reads instead: " 
                    + gui.log.getLastLoggedEvent().toString(), gui.log.containsString("Gui told to DoGoToCashier by agent."));
			assertTrue("Customer should be in state == GoingToCashier. He isn't.",
                    customer.getState() == AgentState.GoingToCashier);
			assertFalse("Customer's scheduler should have returned false (waiting), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//step 9
			customer.msgAnimationFinishedGoToCashier();
			
			//postconditions 9/preconditions 10
			assertTrue("Customer should have event == atCashierAgent. He doesn't.",
                    customer.getEvent() == AgentEvent.atCashierAgent);
			
			
			//step 10
			assertTrue("Customer's scheduler should have returned true (needs to react to new message), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//postconditions 10/preconditions 11
			assertTrue("MockCashier should have logged \"Received msgHereIsPayment\" with correct payment but didn't. His log reads instead: " 
                    + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsPayment from Customer for $25.99"));
			assertEquals("Customer should have $74.01 in myCash but does not.", customer.getMyCash(), 74.01);
			assertEquals("Customer's bill's amountPaid should be $74.01 but it is not.", customer.getBill().getAmountPaid(), 25.99);
			assertFalse("Customer's scheduler should have returned false (waiting for change), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			
			//step 11
			customer.msgHereIsYourChange(74.01, 25.99);
			
			//postconditions 11/preconditions 12
			assertEquals("Customer's bill's changeReceived should be $74.01 but it is not.", customer.getBill().getChangeReceived(), 74.01);
			assertTrue("Customer should have event == gotChange. He doesn't.",
                    customer.getEvent() == AgentEvent.gotChange);
			
			
			//setp 12
			assertTrue("Customer's scheduler should have returned true (needs to react), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//postconditions 12/preconditions 13
			assertTrue("MockCashier should have logged \"Received msgChangeVerified\" but didn't. His log reads instead: " 
                    + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgChangeVerified from Customer"));
			assertTrue("MockEmployee should have logged \"Received msgDoneAndLeaving\" but didn't. His log reads instead: " 
                    + employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Received msgDoneAndLeaving from Customer"));
			assertTrue("MockCustomerGui should have logged \"Told to DoExitMarket\" but didn't. His log reads instead: " 
                    + gui.log.getLastLoggedEvent().toString(), gui.log.containsString("Gui told to DoExitMarket by agent."));
			assertTrue("Customer should be in state == Leaving. He isn't.",
                    customer.getState() == AgentState.Leaving);
			assertTrue("Customer should have a null bill. He doesn't.",
                    customer.getBill() == null);
			
			
			//step 13
			customer.msgAnimationFinishedLeaveRestaurant();
			
			
			//postconditons 13/preconditions 14
			assertTrue("Customer should have event == doneLeaving. He doesn't.",
                    customer.getEvent() == AgentEvent.doneLeaving);
			
			
			//step 14
			assertTrue("Customer's scheduler should have returned true (needs to react), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//postconditions 14
			assertTrue("Customer should be in state == DoingNothing. He isn't.",
                    customer.getState() == AgentState.DoingNothing);
			
		}
		
		
		
		//=============================== NEXT TEST =========================================================================
		public void testNotEnoughMoneyScenario(){
			flake.setCashier(cashier);
			flake.setHost(greeter);

			
			//check preconditions
			assertEquals("Customer should have $10 in myCash but does not.", flake.getMyCash(), 10.0);
			Map<String, Integer>inventory = new TreeMap<String, Integer>();
            inventory.put("steak", 1);		//cost of steak = 15.99
            inventory.put("soup", 2);		//cost of soup = 5.00
            assertTrue("Customer should be in state == doingNothing. He isn't.",
                    flake.getState() == AgentState.DoingNothing);
            
            
			//step 1
			flake.goGetInventory(inventory);
			
			//postconditions 1/preconditions 2
			assertTrue("Customer should have event == toldToGetInventory. He doesn't.",
                    flake.getEvent() == AgentEvent.toldToGetInventory);
			assertEquals("MockGreeter should have an empty event log before the Customer's scheduler is called. Instead, the MockGreeters's event log reads: "
                    + greeter.log.toString(), 0, greeter.log.size());
			
			//step 2
			assertTrue("Customer's scheduler should have returned true (needs to react to new message), but didn't.",
            		flake.pickAndExecuteAnAction());
			
			//postconditions 2/preconditions 3
			assertTrue("MockCustomerGui should have logged \"Told to DoGoToWaitingArea\" but didn't. His log reads instead: " 
                    + gui.log.getLastLoggedEvent().toString(), gui.log.containsString("Gui told to DoGoToWaitingArea by agent."));
			assertTrue("MockGreeter should have logged \"Received msgINeedInventory\" but didn't. His log reads instead: " 
                    + greeter.log.getLastLoggedEvent().toString(), greeter.log.containsString("Received msgINeedInventory from Flake."));
			assertTrue("Customer should be in state == waitingInMarket. He isn't.",
                    flake.getState() == AgentState.WaitingInMarket);
			assertFalse("Customer's scheduler should have returned false (waiting), but didn't.",
            		flake.pickAndExecuteAnAction());
			
			//step 3
			flake.msgFollowMe(employee, 50, 50);		//random numbers, but we can check them
			
			
			//postconditions 3/preconditions 4
			assertTrue("Customer should have event == followEmployee. He doesn't.",
                    flake.getEvent() == AgentEvent.followEmployee);
			assertEquals("Customer's stationX should equal 50 but it does not.", flake.getStationX(), 50);
			assertEquals("Customer's stationY should equal 30 but it does not.", flake.getStationY(), 30);
			assertEquals("Customer's employee should equal MockEmployee, but it does not.", flake.getEmployee(), employee);
			
			
			//step 4
			assertTrue("Customer's scheduler should have returned true (needs to react to new message), but didn't.",
            		flake.pickAndExecuteAnAction());
			
			//postconditions 4/preconditions 5
			assertTrue("MockCustomerGui should have logged \"Told to DoGoToEmployeeStation\" but didn't. His log reads instead: " 
                    + gui.log.getLastLoggedEvent().toString(), gui.log.containsString("Gui told to DoGoToEmployeeStation by agent."));
			assertTrue("Customer should be in state == GoingToStation. He isn't.",
                    flake.getState() == AgentState.GoingToStation);
			assertFalse("Customer's scheduler should have returned false (waiting), but didn't.",
            		flake.pickAndExecuteAnAction());
			
			
			//step 5
			flake.msgMayITakeYourOrder(employee);
			
			//postconditions 5/preconditions 6
			assertTrue("Customer should have event == askedForOrder. He doesn't.",
                    flake.getEvent() == AgentEvent.askedForOrder);
			
			
			//step 6
			assertTrue("Customer's scheduler should have returned true (needs to react to new message), but didn't.",
            		flake.pickAndExecuteAnAction());
			
			//postconditions 6/preconditions 7
			assertTrue("MockEmployee should have logged \"Received HereIsMyOrder\" but didn't. His log reads instead: " 
                    + employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Received msgHereIsMyOrder from Flake"));
			assertTrue("Customer should be in state == OrderProcessing. He isn't.",
                    flake.getState() == AgentState.OrderProcessing);
			assertFalse("Customer's scheduler should have returned false (waiting), but didn't.",
            		flake.pickAndExecuteAnAction());
			
			
			//step 7
			flake.msgHereIsYourOrder(inventory, 25.99);
			
			//postconditions 7/preconditions 8
			assertTrue("Customer should have a bill with a bill charge. He doesn't.",
					flake.getBill().getCharge() == 25.99);
			assertTrue("Customer should have event == gotOrderAndBill. He doesn't.",
                    flake.getEvent() == AgentEvent.gotOrderAndBill);
			assertTrue("Customer should have a bill with state = unpaid. He doesn't.",
                    flake.getBill().getState() == BillState.unpaid);
			
			
			//step 8
			assertTrue("Customer's scheduler should have returned true (needs to react to new message), but didn't.",
            		flake.pickAndExecuteAnAction());
			
			//postconditions 8/preconditions 9
			assertTrue("MockCustomerGui should have logged \"Told to DoGoToCashier\" but didn't. His log reads instead: " 
                    + gui.log.getLastLoggedEvent().toString(), gui.log.containsString("Gui told to DoGoToCashier by agent."));
			assertTrue("Customer should be in state == GoingToCashier. He isn't.",
                    flake.getState() == AgentState.GoingToCashier);
			assertFalse("Customer's scheduler should have returned false (waiting), but didn't.",
            		flake.pickAndExecuteAnAction());
			
			//step 9
			flake.msgAnimationFinishedGoToCashier();
			
			//postconditions 9/preconditions 10
			assertTrue("Customer should have event == atCashierAgent. He doesn't.",
                    flake.getEvent() == AgentEvent.atCashierAgent);
			
			
			//step 10
			assertTrue("Customer's scheduler should have returned true (needs to react to new message), but didn't.",
            		flake.pickAndExecuteAnAction());
			
			//postconditions 10/preconditions 11
			assertTrue("MockCashier should have logged \"Received msgHereIsPayment\" with correct payment but didn't. His log reads instead: " 
                    + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsPayment from Flake for $10.0"));
			assertEquals("Customer should have $0 in myCash but does not.", flake.getMyCash(), 0.0);
			assertEquals("Customer's bill's amountPaid should be $10.0 but it is not.", flake.getBill().getAmountPaid(), 10.0);
			assertFalse("Customer's scheduler should have returned false (waiting for change), but didn't.",
            		flake.pickAndExecuteAnAction());
			
			
			//step 11
			flake.msgNotEnoughCash(15.99);		//amountOwed
			
			//postconditions 11/preconditions 12
			assertEquals("Customer's bill's cashOwed should be $15.99 but it is not.", flake.getCashOwed(), 15.99);
			assertTrue("Customer should have event == gotChange. He doesn't.",
                    flake.getEvent() == AgentEvent.gotChange);
			assertTrue("Customer should have boolean IOweMarket == true. He doesn't.",
                    flake.getIOweMarket() == true);
			assertEquals("Customer's myCash should now equal $100.0 but it does not.", flake.getMyCash(), 100.0);
			
			
			
			
			//setp 12
			assertTrue("Customer's scheduler should have returned true (needs to react), but didn't.",
            		flake.pickAndExecuteAnAction());
			
			//postconditions 12/preconditions 13
			assertTrue("MockCashier should have logged \"Received msgChangeVerified\" but didn't. His log reads instead: " 
                    + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgChangeVerified from Flake"));
			assertTrue("MockEmployee should have logged \"Received msgDoneAndLeaving\" but didn't. His log reads instead: " 
                    + employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Received msgDoneAndLeaving from Flake"));
			assertTrue("MockCustomerGui should have logged \"Told to DoExitMarket\" but didn't. His log reads instead: " 
                    + gui.log.getLastLoggedEvent().toString(), gui.log.containsString("Gui told to DoExitMarket by agent."));
			assertTrue("Customer should be in state == Leaving. He isn't.",
                    flake.getState() == AgentState.Leaving);
			assertTrue("Customer should have a null bill. He doesn't.",
                    flake.getBill() == null);
			
			
			//step 13
			flake.msgAnimationFinishedLeaveRestaurant();
			
			
			//postconditions 13/preconditions 14
			assertTrue("Customer should have event == doneLeaving. He doesn't.",
                    flake.getEvent() == AgentEvent.doneLeaving);
			
			
			//step 14
			assertTrue("Customer's scheduler should have returned true (needs to react), but didn't.",
            		flake.pickAndExecuteAnAction());
			
			//postconditions 14
			assertTrue("Customer should be in state == DoingNothing. He isn't.",
                    flake.getState() == AgentState.DoingNothing);
			
		}
		
		
		
		//=============================== NEXT TEST =========================================================================
		public void testWrongChargeScenario(){
			//here we are testing to see what happens when the employee charges too much
			
			customer.setCashier(cashier);
			customer.setHost(greeter);
			
			//check preconditions
			assertEquals("Customer should have $100 in myCash but does not.", customer.getMyCash(), 100.0);
			Map<String, Integer>inventory = new TreeMap<String, Integer>();
            inventory.put("steak", 1);		//cost of steak = 15.99
            inventory.put("soup", 2);		//cost of soup = 5.00
            assertTrue("Customer should be in state == doingNothing. He isn't.",
                    customer.getState() == AgentState.DoingNothing);
            
            
			//step 1
			customer.goGetInventory(inventory);
			
			//postconditions 1/preconditions 2
			assertTrue("Customer should have event == toldToGetInventory. He doesn't.",
                    customer.getEvent() == AgentEvent.toldToGetInventory);
			assertEquals("MockGreeter should have an empty event log before the Customer's scheduler is called. Instead, the MockGreeters's event log reads: "
                    + greeter.log.toString(), 0, greeter.log.size());
			
			//step 2
			assertTrue("Customer's scheduler should have returned true (needs to react to new message), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//postconditions 2/preconditions 3
			assertTrue("MockCustomerGui should have logged \"Told to DoGoToWaitingArea\" but didn't. His log reads instead: " 
                    + gui.log.getLastLoggedEvent().toString(), gui.log.containsString("Gui told to DoGoToWaitingArea by agent."));
			assertTrue("MockGreeter should have logged \"Received msgINeedInventory\" but didn't. His log reads instead: " 
                    + greeter.log.getLastLoggedEvent().toString(), greeter.log.containsString("Received msgINeedInventory from Customer."));
			assertTrue("Customer should be in state == waitingInMarket. He isn't.",
                    customer.getState() == AgentState.WaitingInMarket);
			assertFalse("Customer's scheduler should have returned false (waiting), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//step 3
			customer.msgFollowMe(employee, 50, 50);		//random numbers, but we can check them
			
			
			//postconditions 3/preconditions 4
			assertTrue("Customer should have event == followEmployee. He doesn't.",
                    customer.getEvent() == AgentEvent.followEmployee);
			assertEquals("Customer's stationX should equal 50 but it does not.", customer.getStationX(), 50);
			assertEquals("Customer's stationY should equal 30 but it does not.", customer.getStationY(), 30);
			assertEquals("Customer's employee should equal MockEmployee, but it does not.", customer.getEmployee(), employee);
			
			
			//step 4
			assertTrue("Customer's scheduler should have returned true (needs to react to new message), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//postconditions 4/preconditions 5
			assertTrue("MockCustomerGui should have logged \"Told to DoGoToEmployeeStation\" but didn't. His log reads instead: " 
                    + gui.log.getLastLoggedEvent().toString(), gui.log.containsString("Gui told to DoGoToEmployeeStation by agent."));
			assertTrue("Customer should be in state == GoingToStation. He isn't.",
                    customer.getState() == AgentState.GoingToStation);
			assertFalse("Customer's scheduler should have returned false (waiting), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			
			//step 5
			customer.msgMayITakeYourOrder(employee);
			
			//postconditions 5/preconditions 6
			assertTrue("Customer should have event == askedForOrder. He doesn't.",
                    customer.getEvent() == AgentEvent.askedForOrder);
			
			
			//step 6
			assertTrue("Customer's scheduler should have returned true (needs to react to new message), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//postconditions 6/preconditions 7
			assertTrue("MockEmployee should have logged \"Received HereIsMyOrder\" but didn't. His log reads instead: " 
                    + employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Received msgHereIsMyOrder from Customer"));
			assertTrue("Customer should be in state == OrderProcessing. He isn't.",
                    customer.getState() == AgentState.OrderProcessing);
			assertFalse("Customer's scheduler should have returned false (waiting), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			
			//step 7
			customer.msgHereIsYourOrder(inventory, 30.0);
			
			//postconditions 7/preconditions 8
			assertTrue("Customer should have a bill with a bill charge. He doesn't.",
					customer.getBill().getCharge() == 30.0);
			assertTrue("Customer should have event == gotOrderAndBill. He doesn't.",
                    customer.getEvent() == AgentEvent.gotOrderAndBill);
			assertTrue("Customer should have a bill with state = unpaid. He doesn't.",
                    customer.getBill().getState() == BillState.unpaid);
			
			
			//step 8
			assertTrue("Customer's scheduler should have returned true (needs to react to new message), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//postconditions 8/preconditions 9
			assertTrue("MockCustomerGui should have logged \"Told to DoGoToCashier\" but didn't. His log reads instead: " 
                    + gui.log.getLastLoggedEvent().toString(), gui.log.containsString("Gui told to DoGoToCashier by agent."));
			assertTrue("Customer should be in state == GoingToCashier. He isn't.",
                    customer.getState() == AgentState.GoingToCashier);
			assertFalse("Customer's scheduler should have returned false (waiting), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//step 9
			customer.msgAnimationFinishedGoToCashier();
			
			//postconditions 9/preconditions 10
			assertTrue("Customer should have event == atCashierAgent. He doesn't.",
                    customer.getEvent() == AgentEvent.atCashierAgent);
			
			
			//step 10
			assertTrue("Customer's scheduler should have returned true (needs to react to new message), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//postconditions 10/preconditions 11
			assertTrue("MockCashier should have logged \"Received msgPleaseRecalculateBill\" but didn't. His log reads instead: " 
                    + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPleaseRecalculateBill from Customer"));
			assertTrue("Customer should be in state == Paying. He isn't.",
                    customer.getState() == AgentState.Paying);
			assertFalse("Customer's scheduler should have returned false (waiting for recalculation), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			
			//step 11
			customer.msgHereIsBill(25.99);		//new amount
			
			//postconditions 11/preconditions 12
			assertEquals("Customer's bill's charge should be $25.99 but it is not.", customer.getBill().getCharge(), 25.99);
			assertTrue("Customer should have event == gotNewBill. He doesn't.",
                    customer.getEvent() == AgentEvent.gotNewBill);
			
			//step 12
			assertTrue("Customer's scheduler should have returned true (needs to react to new message), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			
			//postconditions 12/preconditions 13
			assertTrue("Customer should be in state == WaitingForChange. He isn't.",
                    customer.getState() == AgentState.WaitingForChange);
			assertTrue("MockCashier should have logged \"Received msgHereIsPayment\" but didn't. His log reads instead: " 
                    + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsPayment from Customer for $25.99"));
			assertEquals("Customer should have $74.01 in myCash but does not.", customer.getMyCash(), 74.01);
			assertEquals("Customer's bill's amountPaid should be $25.99 but it is not.", customer.getBill().getAmountPaid(), 25.99);
			assertFalse("Customer's scheduler should have returned false (waiting for change), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			
			//step 13
			customer.msgHereIsYourChange(74.01, 25.99);
			
			//postconditions 11/preconditions 12
			assertEquals("Customer's bill's changeReceived should be $74.01 but it is not.", customer.getBill().getChangeReceived(), 74.01);
			assertTrue("Customer should have event == gotChange. He doesn't.",
                    customer.getEvent() == AgentEvent.gotChange);
			
			
			//setp 12
			assertTrue("Customer's scheduler should have returned true (needs to react), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//postconditions 12/preconditions 13
			assertTrue("MockCashier should have logged \"Received msgChangeVerified\" but didn't. His log reads instead: " 
                    + cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgChangeVerified from Customer"));
			assertTrue("MockEmployee should have logged \"Received msgDoneAndLeaving\" but didn't. His log reads instead: " 
                    + employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Received msgDoneAndLeaving from Customer"));
			assertTrue("MockCustomerGui should have logged \"Told to DoExitMarket\" but didn't. His log reads instead: " 
                    + gui.log.getLastLoggedEvent().toString(), gui.log.containsString("Gui told to DoExitMarket by agent."));
			assertTrue("Customer should be in state == Leaving. He isn't.",
                    customer.getState() == AgentState.Leaving);
			assertTrue("Customer should have a null bill. He doesn't.",
                    customer.getBill() == null);
			
			
			//step 13
			customer.msgAnimationFinishedLeaveRestaurant();
			
			
			//postconditons 13/preconditions 14
			assertTrue("Customer should have event == doneLeaving. He doesn't.",
                    customer.getEvent() == AgentEvent.doneLeaving);
			
			
			//step 14
			assertTrue("Customer's scheduler should have returned true (needs to react), but didn't.",
            		customer.pickAndExecuteAnAction());
			
			//postconditions 14
			assertTrue("Customer should be in state == DoingNothing. He isn't.",
                    customer.getState() == AgentState.DoingNothing);
			
			
		}
}