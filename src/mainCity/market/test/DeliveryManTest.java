package mainCity.market.test;

import java.util.Map;
import java.util.TreeMap;

import role.market.MarketDeliveryManRole;
import role.market.MarketDeliveryManRole.AgentState;
import role.market.MarketDeliveryManRole.DeliveryEvent;
import role.market.MarketDeliveryManRole.DeliveryState;
import mainCity.PersonAgent;
import mainCity.PersonAgent.ActionType;
import mainCity.market.test.mock.*;
import mainCity.restaurants.EllenRestaurant.test.mock.MockCashier;
import mainCity.restaurants.EllenRestaurant.test.mock.MockCook;
import junit.framework.TestCase;


public class DeliveryManTest extends TestCase {
	MarketDeliveryManRole deliveryMan;
	MockDeliveryManGui gui;
	MockEmployee employee;
	MockCashier ellenCashier;	//from ellen's restaurant
	MockCook ellenCook;			//from ellen's restaurant
	MockOtherCashier otherCashier;	//to simulate cashier from marcus' restaurant
	MockOtherCook otherCook;		//to simulate cook from marcus' restaurant
	
	
	public void setUp() throws Exception{
        super.setUp();
        
        PersonAgent d = new PersonAgent("Delivery man");
        deliveryMan = new MarketDeliveryManRole(d, d.getName());
        d.addRole(ActionType.work, deliveryMan);

        gui = new MockDeliveryManGui("MockDeliveryGui");
        deliveryMan.setGui(gui);
        
        employee = new MockEmployee("MockEmployee");
        ellenCashier = new MockCashier("MockCashier");
        ellenCook = new MockCook("MockCook");
        otherCashier = new MockOtherCashier("MarcusCashier");
        otherCook = new MockOtherCook("MarcusCook");
        
        
	}
	
		//=============================== NEXT TEST =========================================================================
		public void testOneNormalBusinessScenario(){
			gui.deliveryMan = deliveryMan;
			
			
			//preconditions
			assertEquals("Delivery man should have 0 bills in it. It doesn't.", deliveryMan.getBills().size(), 0);
            assertEquals("Delivery man should have 0 available money but does not.", deliveryMan.getCash(), 0.0);
            assertTrue("Delivery man should be in state == doingNothing. He isn't.",
            		deliveryMan.getState() == AgentState.doingNothing);
            Map<String, Integer>inventory = new TreeMap<String, Integer>();
            inventory.put("steak", 1);		//cost of steak = 15.99
            inventory.put("soup", 2);		//cost of soup = 5.00
            
            
            //step 1
            deliveryMan.msgHereIsOrderForDelivery("mockEllenRestaurant", inventory, 25.99);
            
            //postconditions 1/preconditions 2
            assertEquals("MockCashier should have an empty event log before the Cashier's scheduler is called. Instead, the MockEmployee's event log reads: "
                    + ellenCashier.log.toString(), 0, ellenCashier.log.size());
            assertEquals("MockCook should have an empty event log before the Cashier's scheduler is called. Instead, the MockCook's event log reads: "
                    + ellenCook.log.toString(), 0, ellenCook.log.size());
            assertEquals("Cashier should have 1 bill but does not.", deliveryMan.getBills().size(), 1);
            assertTrue("Delivery man should contain a check with state == newBill. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.newBill);
            assertTrue("Delivery man should contain a check with event == deliveryRequested. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.deliveryRequested);
            assertTrue("Delivery man should contain a check with a null cook. It doesn't.",
            		deliveryMan.bills.get(0).getCook() == null);
            assertTrue("Delivery man should contain a check with a null cashier. It doesn't.",
                    deliveryMan.bills.get(0).getCashier() == null);
            assertTrue("Delivery man should contain a check with the right restaurant in it. It doesn't.", 
            		deliveryMan.bills.get(0).getRestaurant().equalsIgnoreCase("mockEllenRestaurant"));
            assertTrue("Delivery man should contain a check with the correct amountCharged. It doesn't.",
                    deliveryMan.bills.get(0).getAmountCharged() == 25.99);
            
            
            
            //step 2 - we have to manually set the cashier and cook beforehand 
            // (the ContactList as is won't accept interfaces, so we can't run through the action normally 
            // - see hack in Market1DeliveryManRole)
            deliveryMan.bills.get(0).setCashier(ellenCashier);
            deliveryMan.bills.get(0).setCook(ellenCook);
            deliveryMan.msgAtDestination();
            assertTrue("Delivery man's scheduler should have returned true (needs to react), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());

            //postconditions 2/preconditions 3
            assertTrue("MockCook should have logged \"Received msgHereIsYourOrder\" but didn't. His log reads instead: " 
                    + ellenCook.log.getLastLoggedEvent().toString(), ellenCook.log.containsString("Received msgHereIsYourOrder from delivery man."));
            assertTrue("MockCashier should have logged \"Received msgHereIsMarketBill\" but didn't. His log reads instead: " 
                    + ellenCashier.log.getLastLoggedEvent().toString(), ellenCashier.log.containsString("Received msgHereIsMarketBill from Delivery man for $25.99."));
            assertTrue("Delivery man should contain a check with state == waitingForPayment. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForPayment);
            assertTrue("Delivery man should be in state == makingDelivery. He isn't.",
            		deliveryMan.getState() == AgentState.makingDelivery);
            assertFalse("Delivery man's scheduler should have returned false (waiting), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            
            
            //step 3
            deliveryMan.msgHereIsPayment(50, "mockEllenRestaurant");
            
            //postconditions 3/preconditions 4
            assertTrue("Delivery man should contain a check with the correct amountPaid. It doesn't.",
                    deliveryMan.bills.get(0).getAmountPaid() == 50);
            assertTrue("Delivery man should contain a check with state == waitingForPayment. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForPayment);
            assertTrue("Delivery man should contain a check with event == receivedPayment. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.receivedPayment);
            
            
            //step 4
            assertTrue("Delivery man's scheduler should have returned true (needs to react), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            //postconditions 4/preconditions 5
            assertTrue("MockCashier should have logged \"Received msgHereIsChange\" but didn't. His log reads instead: " 
                    + ellenCashier.log.getLastLoggedEvent().toString(), ellenCashier.log.containsString("Received msgHereIsChange from Delivery man for $24.01."));
            assertTrue("Delivery man should contain a check with the correct amountMarketGets. It doesn't.",
                    deliveryMan.bills.get(0).getAmountMarketGets() == 25.99);
            assertTrue("Delivery man should contain a check with state == waitingForVerification. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForVerification);
            
            
            //step 5
            deliveryMan.msgChangeVerified("mockEllenRestaurant");
            
            //postconditions 5/preconditions 6
            assertEquals("Delivery man should have $25.99 available money but does not.", deliveryMan.getCash(), 25.99);
            assertTrue("Delivery man should contain a check with event == changeVerified. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.changeVerified);
            
            
            //step 6 - message semaphore before it's acquired to avoid locking
			deliveryMan.msgAtHome();
            assertTrue("Delivery man's scheduler should have returned true (needs to react), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            //postconditions 6
			assertEquals("Delivery man should have 0 bills in it. It doesn't.", deliveryMan.getBills().size(), 0);
			assertTrue("MockDeliveryManGui should have logged \"Told to DoGoToHomePosition\" but didn't.", 
					gui.log.containsString("Gui is told to DoGoToHomePosition by agent."));
			assertTrue("Delivery man should be in state == doingNothing. He isn't.",
            		deliveryMan.getState() == AgentState.doingNothing);
			assertFalse("Delivery man's scheduler should have returned false (nothing left to do), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            
		}
		
		
		
		//=============================== NEXT TEST =========================================================================
		public void testTwoNormalBusinessScenario(){
			gui.deliveryMan = deliveryMan;
			
			
			//preconditions
			assertEquals("Delivery man should have 0 bills in it. It doesn't.", deliveryMan.getBills().size(), 0);
            assertEquals("Delivery man should have 0 available money but does not.", deliveryMan.getCash(), 0.0);
            assertTrue("Delivery man should be in state == doingNothing. He isn't.",
            		deliveryMan.getState() == AgentState.doingNothing);
            Map<String, Integer>inventory = new TreeMap<String, Integer>();
            inventory.put("steak", 1);		//cost of steak = 15.99
            inventory.put("soup", 2);		//cost of soup = 5.00
            Map<String, Integer>inventory2 = new TreeMap<String, Integer>();
            inventory2.put("pizza", 1);		//cost of pizza = 8.99
            inventory2.put("pasta", 2);		//cost of pasta = 20.00
            

            //step 1
            deliveryMan.msgHereIsOrderForDelivery("mockEllenRestaurant", inventory, 25.99);
            deliveryMan.msgHereIsOrderForDelivery("mockMarcusRestaurant", inventory2, 48.99);
            
            //postconditions 1/preconditions 2
            assertEquals("MockCashier should have an empty event log before the Cashier's scheduler is called. Instead, the MockEmployee's event log reads: "
                    + ellenCashier.log.toString(), 0, ellenCashier.log.size());
            assertEquals("MockCook should have an empty event log before the Cashier's scheduler is called. Instead, the MockCook's event log reads: "
                    + ellenCook.log.toString(), 0, ellenCook.log.size());
            assertEquals("Cashier should have 2 bills but does not.", deliveryMan.getBills().size(), 2);
            assertTrue("Delivery man should contain a check with state == newBill. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.newBill);
            assertTrue("Delivery man should contain a check with event == deliveryRequested. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.deliveryRequested);
            assertTrue("Delivery man should contain a check with a null cook. It doesn't.",
            		deliveryMan.bills.get(0).getCook() == null); 
            assertTrue("Delivery man should contain a check with a null cashier. It doesn't.",
                    deliveryMan.bills.get(0).getCashier() == null);
            assertTrue("Delivery man should contain a check with the right restaurant in it. It doesn't.", 
            		deliveryMan.bills.get(0).getRestaurant().equalsIgnoreCase("mockEllenRestaurant"));
            assertTrue("Delivery man should contain a check with the correct amountCharged. It doesn't.",
                    deliveryMan.bills.get(0).getAmountCharged() == 25.99);
            
            assertTrue("Delivery man should contain a check with state == newBill. It doesn't.",
            		deliveryMan.bills.get(1).getState() == DeliveryState.newBill);
            assertTrue("Delivery man should contain a check with event == deliveryRequested. It doesn't.",
            		deliveryMan.bills.get(1).getEvent() == DeliveryEvent.deliveryRequested);
            assertTrue("Delivery man should contain a check with a null cook. It doesn't.",
            		deliveryMan.bills.get(1).getCook() == null);
            assertTrue("Delivery man should contain a check with a null cashier. It doesn't.",
                    deliveryMan.bills.get(1).getCashier() == null);
            assertTrue("Delivery man should contain a check with the right restaurant in it. It doesn't.", 
            		deliveryMan.bills.get(1).getRestaurant().equalsIgnoreCase("mockMarcusRestaurant"));
            assertTrue("Delivery man should contain a check with the correct amountCharged. It doesn't.",
                    deliveryMan.bills.get(1).getAmountCharged() == 48.99);
            

            //step 2
            deliveryMan.bills.get(0).setCashier(ellenCashier);
            deliveryMan.bills.get(0).setCook(ellenCook);
            deliveryMan.msgAtDestination();
            assertTrue("Delivery man's scheduler should have returned true (needs to react to new bill), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            //postconditions 2/preconditions 3
            assertTrue("MockCook should have logged \"Received msgHereIsYourOrder\" but didn't. His log reads instead: " 
                    + ellenCook.log.getLastLoggedEvent().toString(), ellenCook.log.containsString("Received msgHereIsYourOrder from delivery man."));
            assertTrue("MockCashier should have logged \"Received msgHereIsMarketBill\" but didn't. His log reads instead: " 
                    + ellenCashier.log.getLastLoggedEvent().toString(), ellenCashier.log.containsString("Received msgHereIsMarketBill from Delivery man for $25.99."));
            assertTrue("Delivery man should contain a check with state == waitingForPayment. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForPayment);
            assertTrue("Delivery man should be in state == makingDelivery. He isn't.",
            		deliveryMan.getState() == AgentState.makingDelivery);
            assertFalse("Delivery man's scheduler should have returned false (waiting), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            

            //step 3
            deliveryMan.msgHereIsPayment(50, "mockEllenRestaurant");
            
            //postconditions 3/preconditions 4
            assertTrue("Delivery man should contain a check with the correct amountPaid. It doesn't.",
                    deliveryMan.bills.get(0).getAmountPaid() == 50);
            assertTrue("Delivery man should contain a check with state == waitingForPayment. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForPayment);
            assertTrue("Delivery man should contain a check with event == receivedPayment. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.receivedPayment);
            assertTrue("Delivery man should be in state == makingDelivery. He isn't.",
            		deliveryMan.getState() == AgentState.makingDelivery);
            

            //step 4
            deliveryMan.msgAtDestination();
            assertTrue("Delivery man's scheduler should have returned true (needs to react to newest bill), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            //postconditions 4/preconditions 5
            assertTrue("MockCashier should have logged \"Received msgHereIsChange\" but didn't. His log reads instead: " 
                    + ellenCashier.log.getLastLoggedEvent().toString(), ellenCashier.log.containsString("Received msgHereIsChange from Delivery man for $24.01."));
            assertTrue("Delivery man should contain a check with the correct amountMarketGets. It doesn't.",
                    deliveryMan.bills.get(0).getAmountMarketGets() == 25.99);
            assertTrue("Delivery man should contain a check with state == waitingForVerification. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForVerification);
            
            
            //step 5
            deliveryMan.msgChangeVerified("mockEllenRestaurant");
            
            //postconditions 5/preconditions 6
            assertEquals("Delivery man should have $25.99 available money but does not.", deliveryMan.getCash(), 25.99);
            assertTrue("Delivery man should contain a check with event == changeVerified. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.changeVerified);
            

            //step 6 - message semaphore before it's acquired to avoid locking
			deliveryMan.msgAtHome();
            assertTrue("Delivery man's scheduler should have returned true (needs to react and remove business), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            //postconditions 6/preconditions 7
			assertEquals("Delivery man should have 1 bill in it. It doesn't.", deliveryMan.getBills().size(), 1);
			assertTrue("MockDeliveryManGui should have logged \"Told to DoGoToHomePosition\" but didn't.", 
					gui.log.containsString("Gui is told to DoGoToHomePosition by agent."));
			assertTrue("Delivery man should be in state == doingNothing. He isn't.",
            		deliveryMan.getState() == AgentState.doingNothing);
			
			
			//step 7 - message semaphore before it's acquired to avoid locking
			deliveryMan.bills.get(0).setCashier(otherCashier);
			deliveryMan.bills.get(0).setCook(otherCook);
			deliveryMan.msgAtDestination();
            assertTrue("Delivery man's scheduler should have returned true (needs to react to newest bill), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());

            //postconditions 7/preconditions 8
            assertTrue("MockCook should have logged \"Received msgHereIsYourOrder\" but didn't. His log reads instead: " 
                    + otherCook.log.getLastLoggedEvent().toString(), otherCook.log.containsString("Received msgHereIsYourOrder from delivery man."));
            assertTrue("MockCashier should have logged \"Received msgHereIsMarketBill\" but didn't. His log reads instead: " 
                    + otherCashier.log.getLastLoggedEvent().toString(), otherCashier.log.containsString("Received msgHereIsMarketBill from Delivery man for $48.99."));
            assertTrue("Delivery man should contain a check with state == waitingForPayment. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForPayment);
            assertTrue("Delivery man should be in state == makingDelivery. He isn't.",
            		deliveryMan.getState() == AgentState.makingDelivery);
            assertFalse("Delivery man's scheduler should have returned false (waiting), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            
            //step 8
            deliveryMan.msgHereIsPayment(50, "mockMarcusRestaurant");
            
            //postconditions 8/preconditions 9
            assertTrue("Delivery man should contain a check with the correct amountPaid. It doesn't.",
                    deliveryMan.bills.get(0).getAmountPaid() == 50);
            assertTrue("Delivery man should contain a check with state == waitingForPayment. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForPayment);
            assertTrue("Delivery man should contain a check with event == receivedPayment. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.receivedPayment);
            
            
            //step 9
            assertTrue("Delivery man's scheduler should have returned true (needs to react), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());

            //postconditions 9/preconditions 10
            assertTrue("MockCashier should have logged \"Received msgHereIsChange\" but didn't. His log reads instead: " 
                    + otherCashier.log.getLastLoggedEvent().toString(), otherCashier.log.containsString("Received msgHereIsChange from Delivery man for $1.01."));
            assertTrue("Delivery man should contain a check with the correct amountMarketGets. It doesn't.",
                    deliveryMan.bills.get(0).getAmountMarketGets() == 48.99);
            assertTrue("Delivery man should contain a check with state == waitingForVerification. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForVerification);
            
            
            //step 10
            deliveryMan.msgChangeVerified("mockMarcusRestaurant");
            
            //postconditions 10/preconditions 11
            assertEquals("Delivery man should have $74.98 available money but does not.", deliveryMan.getCash(), 74.98);
            assertTrue("Delivery man should contain a check with event == changeVerified. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.changeVerified);
            
            
            //step 11 - message semaphore before it's acquired to avoid locking
			deliveryMan.msgAtHome();
            assertTrue("Delivery man's scheduler should have returned true (needs to react), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            //postconditions 11
			assertEquals("Delivery man should have 0 bills in it. It doesn't.", deliveryMan.getBills().size(), 0);
			assertTrue("MockDeliveryManGui should have logged \"Told to DoGoToHomePosition\" but didn't.", 
					gui.log.containsString("Gui is told to DoGoToHomePosition by agent."));
			assertTrue("Delivery man should be in state == doingNothing. He isn't.",
            		deliveryMan.getState() == AgentState.doingNothing);
			
		}
		
		
		
		//=============================== NEXT TEST =========================================================================
		public void testOneFlakeBusinessScenario(){
			gui.deliveryMan = deliveryMan;
			deliveryMan.deliveryGui = gui;
			

			//preconditions
			assertEquals("Delivery man should have 0 bills in it. It doesn't.", deliveryMan.getBills().size(), 0);
            assertEquals("Delivery man should have 0 available money but does not.", deliveryMan.getCash(), 0.0);
            assertTrue("Delivery man should be in state == doingNothing. He isn't.",
            		deliveryMan.getState() == AgentState.doingNothing);
            Map<String, Integer>inventory = new TreeMap<String, Integer>();
            inventory.put("steak", 1);		//cost of steak = 15.99
            inventory.put("soup", 2);		//cost of soup = 5.00
            
            
            //step 1
            deliveryMan.msgHereIsOrderForDelivery("mockEllenRestaurant", inventory, 25.99);
            
            //postconditions 1/preconditions 2
            assertEquals("MockCashier should have an empty event log before the Cashier's scheduler is called. Instead, the MockEmployee's event log reads: "
                    + ellenCashier.log.toString(), 0, ellenCashier.log.size());
            assertEquals("MockCook should have an empty event log before the Cashier's scheduler is called. Instead, the MockCook's event log reads: "
                    + ellenCook.log.toString(), 0, ellenCook.log.size());
            assertEquals("Delivery man should have 1 bill but does not.", deliveryMan.getBills().size(), 1);
            assertTrue("Delivery man should contain a check with state == newBill. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.newBill);
            assertTrue("Delivery man should contain a check with event == deliveryRequested. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.deliveryRequested);
            assertTrue("Delivery man should contain a check with a null cook. It doesn't.",
            		deliveryMan.bills.get(0).getCook() == null);
            assertTrue("Delivery man should contain a check with a null cashier. It doesn't.",
                    deliveryMan.bills.get(0).getCashier() == null);
            assertTrue("Delivery man should contain a check with the right restaurant in it. It doesn't.", 
            		deliveryMan.bills.get(0).getRestaurant().equalsIgnoreCase("mockEllenRestaurant"));
            assertTrue("Delivery man should contain a check with the correct amountCharged. It doesn't.",
                    deliveryMan.bills.get(0).getAmountCharged() == 25.99);

            
            //step 2
            deliveryMan.bills.get(0).setCashier(ellenCashier);
            deliveryMan.bills.get(0).setCook(ellenCook);
            deliveryMan.msgAtDestination();
            assertTrue("Delivery man's scheduler should have returned true (needs to react), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());

            //postconditions 2/preconditions 3
            assertTrue("MockCook should have logged \"Received msgHereIsYourOrder\" but didn't. His log reads instead: " 
                    + ellenCook.log.getLastLoggedEvent().toString(), ellenCook.log.containsString("Received msgHereIsYourOrder from delivery man."));
            assertTrue("MockCashier should have logged \"Received msgHereIsMarketBill\" but didn't. His log reads instead: " 
                    + ellenCashier.log.getLastLoggedEvent().toString(), ellenCashier.log.containsString("Received msgHereIsMarketBill from Delivery man for $25.99."));
            assertTrue("Delivery man should contain a check with state == waitingForPayment. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForPayment);
            assertTrue("Delivery man should be in state == makingDelivery. He isn't.",
            		deliveryMan.getState() == AgentState.makingDelivery);
            assertFalse("Delivery man's scheduler should have returned false (waiting), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            

            //step 3
            deliveryMan.msgHereIsPayment(10, "mockEllenRestaurant");		//less than bill charge
            
            //postconditions 3/preconditions 4
            assertTrue("Delivery man should contain a check with the correct amountPaid. It doesn't.",
                    deliveryMan.bills.get(0).getAmountPaid() == 10);
            assertTrue("Delivery man should contain a check with state == waitingForPayment. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.waitingForPayment);
            assertTrue("Delivery man should contain a check with event == receivedPayment. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.receivedPayment);
            
            
            //step 4
            assertTrue("Delivery man's scheduler should have returned true (needs to react), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            //postconditions 4/preconditions 5
            assertTrue("MockCashier should have logged \"Received msgNotEnoughMoney\" but didn't. His log reads instead: " 
                    + ellenCashier.log.getLastLoggedEvent().toString(), ellenCashier.log.containsString("Received msgNotEnoughMoney, amount owed = $15.99"));
            assertTrue("Delivery man should contain a check with the correct amountMarketGets. It doesn't.",
                    deliveryMan.bills.get(0).getAmountMarketGets() == 10);
            assertTrue("Delivery man should contain a check with the correct amountOwed. It doesn't. Its amountOwed = " + deliveryMan.bills.get(0).getAmountMarketGets(),
                    deliveryMan.bills.get(0).getAmountOwed() == 15.99);
            assertTrue("Delivery man should contain a check with state == oweMoney. It doesn't.",
            		deliveryMan.bills.get(0).getState() == DeliveryState.oweMoney);
            
            
            
            //step 5
            deliveryMan.msgIOweYou(15.99, "mockEllenRestaurant");
            
            //postconditions 5/preconditions 6
            assertEquals("Delivery man should have $10 available money but does not.", deliveryMan.getCash(), 10.0);
            assertTrue("Delivery man should contain a check with event == acknowledgedDebt. It doesn't.",
            		deliveryMan.bills.get(0).getEvent() == DeliveryEvent.acknowledgedDebt);
            
            
            //step 6 - call semaphore release before it's acquired to avoid locking
            deliveryMan.msgAtHome();
            assertTrue("Delivery man's scheduler should have returned true (needs to react), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
            
            
            //postconditions 6
			assertEquals("Delivery man should have 0 bills in it. It doesn't.", deliveryMan.getBills().size(), 0);
			assertTrue("MockDeliveryManGui should have logged \"Told to DoGoToHomePosition\" but didn't.", 
					gui.log.containsString("Gui is told to DoGoToHomePosition by agent."));

			assertTrue("Delivery man should be in state == doingNothing. He isn't.",
            		deliveryMan.getState() == AgentState.doingNothing);
			assertFalse("Delivery man's scheduler should have returned false (nothing left to do), but didn't.",
            		deliveryMan.pickAndExecuteAnAction());
			
		}
}