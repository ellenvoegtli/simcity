package mainCity.restaurants.restaurant_zhangdt.test;

import mainCity.PersonAgent;
import mainCity.restaurants.restaurant_zhangdt.sharedData.OrderTicket;
import mainCity.restaurants.restaurant_zhangdt.sharedData.RevolvingStand;
import role.davidRestaurant.DavidCookRole;
import role.davidRestaurant.DavidCustomerRole;
import role.davidRestaurant.DavidHostRole;
import role.davidRestaurant.DavidHostRole.Table;
import role.davidRestaurant.DavidSharedWaiterRole;
import role.davidRestaurant.DavidWaiterRole;
import junit.framework.TestCase;

public class SharedDataTest extends TestCase {
	DavidCookRole cook; 
	DavidSharedWaiterRole waiter;
	DavidCustomerRole customer; 
	PersonAgent person;
	PersonAgent person2;
	PersonAgent person3;
	
	DavidHostRole.Table table = new DavidHostRole.Table(0, 0, 0); 
	OrderTicket order; 
	RevolvingStand stand = new RevolvingStand();
	
	public void setUp() throws Exception{ 
		super.setUp(); 
		
		PersonAgent person = new PersonAgent("testCook"); 
		PersonAgent person2 = new PersonAgent("testWaiter");
		PersonAgent person3 = new PersonAgent("testCustomer");
		
		cook = new DavidCookRole(person.getName(), person); 
		waiter = new DavidSharedWaiterRole(person2.getName(), person2); 
		customer = new DavidCustomerRole(person3.getName(), person3);
		
		order = new OrderTicket(waiter, "steak", table);
		cook.setStand(stand);
		waiter.setStand(stand);
		
	}
	
	public void testOneOrderScenario() { 
		System.out.println("Testing waiter placing one order onto stand"); 
	
		//Preconditions 
		assertEquals("Revolving stand should have no orders in it. It doesn't.", stand.getOrderList().size(), 0);
		assertEquals("Cook should have no pending orders. He doesn't", cook.getOrders().size(), 0);
		assertEquals("Cook's log should have no events in it", cook.log.size(), 0);
		
		stand.insert(order); 
		
		assertEquals("Revolving stand should have 1 order in it. It doesn't", stand.getOrderList().size(), 1);
		
		cook.msgCheckStand(); 
		
		assertEquals("Cook should still have 0 pending order. It doesn't", cook.getOrders().size(), 0);
		assertEquals("Cook's log should have 1 event in it. It doesn't.", cook.log.size(), 1);
		assertTrue("cook should have logged \"checking stand\" but didn't. His log reads instead: " 
						+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("checking stand"));
		
		cook.pickAndExecuteAnAction(); 
		
		assertTrue("cook should have logged \"adding order from stand\" but didn't. His log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("adding order from stand"));
		assertEquals("Cook should still have 1 pending order. It doesn't", cook.getOrders().size(), 1);
		assertEquals("Stand should no longer have an order on it. It still does.", stand.getOrderList().size(), 0);
	}
	
	public void testTwoOrdersScenario() { 
		System.out.println("Testing waiter placing one order onto stand"); 
	
		//Preconditions 
		assertEquals("Revolving stand should have no orders in it. It doesn't.", stand.getOrderList().size(), 0);
		assertEquals("Cook should have no pending orders. He doesn't", cook.getOrders().size(), 0);
		assertEquals("Cook's log should have no events in it", cook.log.size(), 0);
		
		stand.insert(order); 
		stand.insert(order);
		
		assertEquals("Revolving stand should have 2 orders in it. It doesn't", stand.getOrderList().size(), 2);
		
		cook.msgCheckStand(); 
		
		assertEquals("Cook should still have 0 pending order. It doesn't", cook.getOrders().size(), 0);
		assertEquals("Cook's log should have 1 event in it. It doesn't.", cook.log.size(), 1);
		assertTrue("cook should have logged \"checking stand\" but didn't. His log reads instead: " 
						+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("checking stand"));
		
		cook.pickAndExecuteAnAction(); 
		
		assertTrue("cook should have logged \"adding order from stand\" but didn't. His log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("adding order from stand"));
		assertEquals("Cook should still have 2 pending orders. It doesn't", cook.getOrders().size(), 2);
		
		
		assertEquals("Stand should no longer have an order on it. It still does.", stand.getOrderList().size(), 0);
	}
}