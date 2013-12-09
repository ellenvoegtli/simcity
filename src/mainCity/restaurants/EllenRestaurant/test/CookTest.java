package mainCity.restaurants.EllenRestaurant.test;

import junit.framework.TestCase;
import mainCity.PersonAgent;
import mainCity.restaurants.EllenRestaurant.EllenMenu;
import mainCity.restaurants.EllenRestaurant.sharedData.*;
import mainCity.restaurants.EllenRestaurant.test.mock.*;
import role.ellenRestaurant.EllenCookRole;
import role.ellenRestaurant.EllenCookRole.Order;
import role.ellenRestaurant.EllenCookRole.OrderState;
import role.ellenRestaurant.EllenHostRole.Table;


public class CookTest extends TestCase {
	
	EllenCookRole cook;
	MockKitchenGui kitchenGui;
	
	RevolvingStand stand = new RevolvingStand();
	MockWaiter waiter1;
	MockWaiter waiter2;
	MockCustomer customer1;
	MockCustomer customer2;
	Table table1;
	Table table2;
	
	MockHost host;
	EllenMenu menu;
	
	public void setUp() throws Exception{
		super.setUp();
		menu = new EllenMenu();
		
		PersonAgent base = new PersonAgent("Waiter");
		cook = new EllenCookRole(base, base.getName());
		base.addRole(PersonAgent.ActionType.work, cook);
		cook.setActive();

		kitchenGui = new MockKitchenGui("KitchenGui");
		cook.setKitchenGui(kitchenGui);
		//kitchenGui.setCook(cook);
		
		cook.setStand(stand);
		
		waiter1 = new MockWaiter("mockwaiter1");
		waiter2 = new MockWaiter("mockwaiter2");
		customer1 = new MockCustomer("mockcustomer1");		
		customer2 = new MockCustomer("mockcustomer2");	

		host = new MockHost("host");
		host.addTable(table1);
		host.addTable(table2);
	}	
	
	
	public void testOneOrderScenario() {
		System.out.println("Beginning One Order Scenario");
		cook.setOpened(false);
		cook.setMenu(menu);
		
		assertEquals("Cook should not have any orders in its list of orders, but it does", cook.getOrders().size(), 0);
		assertTrue("The stand should not have any orders in it, it does", stand.isEmpty());
		assertEquals("Waiter's event log should be empty, it's not", 0, waiter1.log.size());

		assertFalse("Cook's scheduler should've returned false because there's nothing to do", cook.pickAndExecuteAnAction());
		cook.msgCheckStand();
		assertTrue("Cook's scheduler should've returned true to check the stand", cook.pickAndExecuteAnAction());

		stand.insert(new OrderTicket(waiter1, "pizza", 1));
		assertFalse("The stand should now have one order in it, it doesn't", stand.isEmpty());
		
		cook.msgCheckStand();
		assertTrue("Cook's scheduler should've returned true to check the stand", cook.pickAndExecuteAnAction());
		assertEquals("Cook should have 1 order in its list of orders, but it doesn't", cook.getOrders().size(), 1);
		
		assertTrue("Cook's scheduler should've returned true to handle the order", cook.pickAndExecuteAnAction());
		assertEquals("The order's cooking status should be cooking", ((Order) cook.orders.get(0)).getState(), OrderState.cooking);
		
		cook.msgFoodDone(cook.orders.get(0));
		assertTrue("Cook's scheduler should've returned true to handle the prepared order", cook.pickAndExecuteAnAction());
		assertTrue("Waiter's log should've read ('pizza done cooking'), it instead read: " + waiter1.log.getLastLoggedEvent().toString(), waiter1.log.containsString("pizza done cooking"));

		cook.pickingUpFood(1);
		assertTrue("Cook's scheduler should've returned true to handle the picked up order", cook.pickAndExecuteAnAction());
		assertEquals("Cook should not have any orders in its list of orders, but it does", cook.getOrders().size(), 0);
		System.out.println("End Normative Scenario");
	}//end one normal customer scenario
	
	
	public void testTwoOrderScenario() {
		System.out.println("Beginning Two Order Scenario");
		cook.setOpened(false);
		cook.setMenu(menu);
		
		assertEquals("Cook should not have any orders in its list of orders, but it does", cook.getOrders().size(), 0);
		assertTrue("The stand should not have any orders in it, it does", stand.isEmpty());
		assertEquals("Waiter's event log should be empty, it's not", 0, waiter1.log.size());

		assertFalse("Cook's scheduler should've returned false because there's nothing to do", cook.pickAndExecuteAnAction());
		cook.msgCheckStand();
		assertTrue("Cook's scheduler should've returned true to check the stand", cook.pickAndExecuteAnAction());

		stand.insert(new OrderTicket(waiter1, "pizza", 1));
		assertFalse("The stand should now have one order in it, it doesn't", stand.isEmpty());
		stand.insert(new OrderTicket(waiter2, "pasta", 2));
		
		cook.msgCheckStand();
		assertTrue("Cook's scheduler should've returned true to check the stand", cook.pickAndExecuteAnAction());
		assertEquals("Cook should have 2 orders in its list of orders, but it doesn't", cook.getOrders().size(), 2);
		
		assertTrue("Cook's scheduler should've returned true to handle the order", cook.pickAndExecuteAnAction());
		assertEquals("The order's cooking status should be cooking", ((Order) cook.orders.get(0)).getState(), OrderState.cooking);
		cook.msgFoodDone(cook.orders.get(0));
		assertTrue("Cook's scheduler should've returned true to handle the prepared order", cook.pickAndExecuteAnAction());
		assertTrue("Waiter's log should've read ('pizza done cooking'), it instead read: " + waiter1.log.getLastLoggedEvent().toString(), waiter1.log.containsString("pizza done cooking"));

		cook.pickingUpFood(1);
		assertTrue("Cook's scheduler should've returned true to respond", cook.pickAndExecuteAnAction());
		assertEquals("Cook should have 1 order in its list of orders, but it doesn't", cook.getOrders().size(), 1);
		
		assertTrue("Cook's scheduler should've returned true to handle the second order", cook.pickAndExecuteAnAction());
		assertEquals("The order's cooking status should be cooking", ((Order) cook.orders.get(0)).getState(), OrderState.cooking);
		cook.msgFoodDone(cook.orders.get(0));
		assertTrue("Cook's scheduler should've returned true to handle the prepared order", cook.pickAndExecuteAnAction());
		assertTrue("Waiter's log should've read ('pasta done cooking'), it instead read: " + waiter2.log.getLastLoggedEvent().toString(), waiter2.log.containsString("pasta done cooking"));
		
		System.out.println("End Normative Scenario");
	}//end two normal customer scenario
}