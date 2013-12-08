package mainCity.restaurants.marcusRestaurant.test;

import role.marcusRestaurant.MarcusCookRole;
import role.marcusRestaurant.MarcusCookRole.*;
import mainCity.PersonAgent;
import mainCity.restaurants.marcusRestaurant.MarcusTable;
import mainCity.restaurants.marcusRestaurant.sharedData.*;
import mainCity.restaurants.marcusRestaurant.test.mock.*;
import junit.framework.*;

public class CookTest extends TestCase {
	MarcusCookRole cook;
	
    RevolvingStand stand = new RevolvingStand();

    MockWaiter waiter1;
    MockWaiter waiter2;
    
	MockCustomer customer1;
	MockCustomer customer2;
	
	MarcusTable table1;
	MarcusTable table2;
	
	public void setUp() throws Exception{
		super.setUp();
		PersonAgent base = new PersonAgent("Cook");
		cook = new MarcusCookRole(base, base.getName());
		base.addRole(PersonAgent.ActionType.work, cook);
		
		MockCookGui gui = new MockCookGui("cookGui");
		cook.setGui(gui);
		
		cook.setActive();
		cook.setStand(stand);
		
		waiter1 = new MockWaiter("waiter1");
		waiter2 = new MockWaiter("waiter2");
		
		table1 = new MarcusTable(1);
		table2 = new MarcusTable(2);
	}	

	public void testOneOrderScenario() {
		System.out.println("Beginning One Order Scenario");
		
		assertEquals("Cook should not have any orders in its list of orders, but it does", cook.getOrders().size(), 0);
		assertTrue("The stand should not have any orders in it, it does", stand.isEmpty());
		assertEquals("Waiter's event log should be empty, it's not", 0, waiter1.getLog().size());

		assertFalse("Cook's scheduler should've returned false because there's nothing to do", cook.pickAndExecuteAnAction());
		cook.msgCheckStand();
		assertTrue("Cook's scheduler should've returned true to check the stand", cook.pickAndExecuteAnAction());

		stand.insert(new OrderTicket(waiter1, "Swiss", table1));
		assertFalse("The stand should now have one order in it, it doesn't", stand.isEmpty());
		
		cook.msgCheckStand();
		assertTrue("Cook's scheduler should've returned true to check the stand", cook.pickAndExecuteAnAction());
		assertEquals("Cook should have 1 order in its list of orders, but it doesn't", cook.getOrders().size(), 1);
		
		assertTrue("Cook's scheduler should've returned true to handle the order", cook.pickAndExecuteAnAction());
		assertEquals("The order's cooking status should be cooking", ((Order) cook.getOrders().get(0)).status, OrderStatus.cooking);
		((Order) cook.getOrders().get(0)).status = OrderStatus.cooked;
		assertTrue("Cook's scheduler should've returned true to handle the prepared order", cook.pickAndExecuteAnAction());
		assertTrue("Cook's scheduler should've returned true to handle the prepared order", cook.pickAndExecuteAnAction());
		assertTrue("Waiter's log should've read ('Swiss is ready for table 1'), it instead read: " + waiter1.getLog().getLastLoggedEvent().toString(), waiter1.getLog().containsString("Swiss is ready for table 1"));

		System.out.println("End Normative Scenario");
	}//end one normal customer scenario
	
	public void testTwoOrderScenario() {
		System.out.println("Beginning Two Order Scenario");
		
		assertEquals("Cook should not have any orders in its list of orders, but it does", cook.getOrders().size(), 0);
		assertTrue("The stand should not have any orders in it, it does", stand.isEmpty());
		assertEquals("Waiter's event log should be empty, it's not", 0, waiter1.getLog().size());

		assertFalse("Cook's scheduler should've returned false because there's nothing to do", cook.pickAndExecuteAnAction());
		cook.msgCheckStand();
		assertTrue("Cook's scheduler should've returned true to check the stand", cook.pickAndExecuteAnAction());

		stand.insert(new OrderTicket(waiter1, "Swiss", table1));
		assertFalse("The stand should now have one order in it, it doesn't", stand.isEmpty());
		stand.insert(new OrderTicket(waiter2, "American", table2));

		cook.msgCheckStand();
		assertTrue("Cook's scheduler should've returned true to check the stand", cook.pickAndExecuteAnAction());
		assertEquals("Cook should have 2 orders in its list of orders, but it doesn't", 2, cook.getOrders().size());
		
		cook.msgCheckStand();
		assertTrue("Cook's scheduler should've returned true to check the stand", cook.pickAndExecuteAnAction());
		assertEquals("Cook should have 2 order in its list of orders, but it doesn't", 2, cook.getOrders().size());
		
		assertTrue("Cook's scheduler should've returned true to handle the order", cook.pickAndExecuteAnAction());
		assertEquals("The order's cooking status should be cooking", ((Order) cook.getOrders().get(0)).status, OrderStatus.cooking);
		((Order) cook.getOrders().get(0)).status = OrderStatus.cooked;
		assertTrue("Cook's scheduler should've returned true to handle the prepared order", cook.pickAndExecuteAnAction());
		assertTrue("Cook's scheduler should've returned true to handle the prepared order", cook.pickAndExecuteAnAction());
		assertTrue("Waiter's log should've read ('Swiss is ready for table 1'), it instead read: " + waiter1.getLog().getLastLoggedEvent().toString(), waiter1.getLog().containsString("Swiss is ready for table 1"));

		assertEquals("Cook should have 1 order in its list of orders, but it doesn't", 1, cook.getOrders().size());
		assertTrue("Cook's scheduler should've returned true to handle the order", cook.pickAndExecuteAnAction());
		assertEquals("The order's cooking status should be cooking", ((Order) cook.getOrders().get(0)).status, OrderStatus.cooking);
		((Order) cook.getOrders().get(0)).status = OrderStatus.cooked;
		assertTrue("Cook's scheduler should've returned true to handle the prepared order", cook.pickAndExecuteAnAction());
		assertTrue("Waiter's log should've read ('American is ready for table 2'), it instead read: " + waiter2.getLog().getLastLoggedEvent().toString(), waiter2.getLog().containsString("American is ready for table 2"));
		
		System.out.println("End Two Normative Scenario");
	}//end one normal customer scenario
}
