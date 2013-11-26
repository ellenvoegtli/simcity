package mainCity.market.test;

import java.util.Map;
import java.util.TreeMap;

import role.market.MarketDeliveryManRole;
import role.market.MarketDeliveryManRole.AgentState;
import role.market.MarketDeliveryManRole.DeliveryEvent;
import role.market.MarketDeliveryManRole.DeliveryState;
import mainCity.PersonAgent;
import mainCity.PersonAgent.ActionType;
import mainCity.interfaces.*;
import mainCity.market.test.mock.*;
import mainCity.restaurants.EllenRestaurant.test.mock.MockCashier;
import mainCity.restaurants.EllenRestaurant.test.mock.MockCook;
import junit.framework.TestCase;


public class DeliveryManTest extends TestCase {
	MarketDeliveryManRole deliveryMan;
	//MockCustomer customer1;
	//MockCustomer customer2;
	MockEmployee employee;
	MockCashier cashier;
	MockCook cook;
	
	
	public void setUp() throws Exception{
        super.setUp();
        
        PersonAgent d = new PersonAgent("Delivery man");
        deliveryMan = new MarketDeliveryManRole(d, d.getName());
        d.addRole(ActionType.work, deliveryMan);
        
        //customer1 = new MockCustomer("MockCustomer1"); 
        //customer2 = new MockCustomer("MockCustomer2");        
        employee = new MockEmployee("MockEmployee");
        cashier = new MockCashier("MockCashier");
        cook = new MockCook("MockCook");
        
        
	}
	
		public void testOneNormalBusinessScenario(){
			employee.deliveryMan = deliveryMan;
			
			
			//preconditions
			assertEquals("Delivery man should have 0 deliveries in it. It doesn't.", deliveryMan.getBills().size(), 0);
            assertEquals("Delivery man should have 0 available money but does not.", deliveryMan.getAvailableMoney(), 0.0);
            assertTrue("Delivery man should be in state == doingNothing. He isn't.",
            		deliveryMan.getState() == AgentState.doingNothing);
            Map<String, Integer>inventory = new TreeMap<String, Integer>();
            inventory.put("steak", 1);		//cost of steak = 15.99
            inventory.put("soup", 2);		//cost of soup = 5.00
            
            
            //step 1
            deliveryMan.msgHereIsOrderForDelivery("ellenRestaurant", cook, cashier, inventory, 25.99);
            
            //postconditions 1/preconditions 2
            assertEquals("MockCashier should have an empty event log before the Cashier's scheduler is called. Instead, the MockEmployee's event log reads: "
                    + cashier.log.toString(), 0, cashier.log.size());
            assertEquals("MockCook should have an empty event log before the Cashier's scheduler is called. Instead, the MockCook's event log reads: "
                    + cook.log.toString(), 0, cook.log.size());
            assertEquals("Cashier should have 1 bill but does not.", deliveryMan.getBills().size(), 1);
            assertTrue("Cashier should contain a check with state == newBill. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.newBill);
            assertTrue("Cashier should contain a check with event == deliveryRequested. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.deliveryRequested);
            assertTrue("Cashier should contain a check with the correct cook. It doesn't.",
            		deliveryMan.bills.get(0).getCook() == cook);
            assertTrue("Cashier should contain a check with the correct cashier. It doesn't.",
                    deliveryMan.bills.get(0).getCashier() == cashier);
            assertTrue("Cashier should contain a check with the right restaurant in it. It doesn't.", 
            		deliveryMan.bills.get(0).getRestaurant().equalsIgnoreCase("ellenRestaurant"));
            assertTrue("Cashier should contain a check with the correct amountCharged. It doesn't.",
                    deliveryMan.bills.get(0).getAmountCharged() == 25.99);
            
            
            //step 2
            assertTrue("Delivery man's scheduler should have returned true (needs to react to new delivery request), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            //postconditions 2/preconditions 3
            assertTrue("Delivery man should be in state == makingDelivery. He isn't.",
            		deliveryMan.getState() == AgentState.makingDelivery);
            //gui...
            
            assertTrue("MockCook should have logged \"Received msgHereIsYourOrder\" but didn't. His log reads instead: " 
                    + cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Received msgHereIsYourOrder from delivery man."));
            assertTrue("MockCashier should have logged \"Received msgHereIsMarketBill\" but didn't. His log reads instead: " 
                    + cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Received msgHereIsMarketBill from Delivery man for $25.99."));
            
            
		}
		
		public void testTwoNormalBusinessScenario(){
			
		}
		
		public void testOneFlakeBusinessScenario(){
			
		}
}