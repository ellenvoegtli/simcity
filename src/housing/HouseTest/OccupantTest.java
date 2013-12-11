package housing.HouseTest;


import java.util.ArrayList;
import java.util.List;

import mainCity.PersonAgent;
import mainCity.PersonAgent.ActionType;
import mainCity.gui.PersonGui;
import mainCity.interfaces.PersonGuiInterface;
import mainCity.restaurants.enaRestaurant.test.mock.MockCustomer;
import mainCity.restaurants.enaRestaurant.test.mock.MockWaiter;
import housing.LandlordRole;
import housing.OccupantRole;
import housing.OccupantRole.eatingState;
import housing.OccupantRole.fixState;
import housing.OccupantRole.shoppingState;
import housing.personHome;
import housing.HouseTest.HouseMock.MockLandLord;
import housing.HouseTest.HouseMock.MockLandLordGui;
import housing.HouseTest.HouseMock.MockOccupant;
import housing.HouseTest.HouseMock.MockOccupantGui;
import junit.framework.*;


public class OccupantTest extends TestCase
{

	OccupantRole occupant;
	OccupantRole occupant2;
	OccupantRole occupant3;
	MockLandLord landlord;
	
	personHome home;
	personHome home2;
	personHome home3;

	//MockOccupant occupant1;
	//MockOccupant occupant2;
	//MockOccupant occupant3;
	MockOccupantGui oGui;
	MockOccupantGui oGui2;
	MockOccupantGui oGui3;

	PersonGuiInterface g;
	PersonGui pg1;
	PersonGui pg2;
	PersonGui pg3;

	
	
	
	public void setUp() throws Exception{
		super.setUp();	
				
		PersonAgent p = new PersonAgent("LL");
		pg1 = new PersonGui(p);
		occupant = new OccupantRole(p);
		//g = new PersonGuiInterface();
		p.setGui(pg1);
		p.addRole(ActionType.homeAndEat, occupant);
		
		
		PersonAgent pA = new PersonAgent("LL");
		pg2 = new PersonGui(pA);
		occupant2 = new OccupantRole(pA);
		//g = new PersonGuiInterface();
		p.setGui(pg2);
		p.addRole(ActionType.home, occupant);
		
		
		PersonAgent pB = new PersonAgent("LL");
		pg3 = new PersonGui(pB);
		occupant3 = new OccupantRole(pB);
		//g = new PersonGuiInterface();
		p.setGui(pg3);
		p.addRole(ActionType.homeAndEat, occupant);
		
	    home = new personHome(occupant);
	    home2 = new personHome(occupant2);
	    home3 = new personHome(occupant3);
	    oGui = new MockOccupantGui("ll");
	    oGui2 = new MockOccupantGui("rr");
	    oGui3 = new MockOccupantGui("ff");
	    occupant.setGui(oGui);
	    
		
		
	}	
	
	
	
	
	public void testOneNormalScenario()
	{
		//normal scenario where home owner cooks himself food
		
	       assertEquals("occupant should have one thing it needs to fix. It doesn't.", occupant.eState.hungry, eatingState.hungry);
	       assertEquals("occupant should have one thing it needs to fix. It doesn't.", occupant.fState.nothing, fixState.nothing);
	       assertEquals("occupant should have one thing it needs to fix. It doesn't.", occupant.sState.nothing, shoppingState.nothing);

		
		home.setOccupant(occupant);
		oGui.occupant = occupant;
		occupant.owner = true;
		
		occupant.gotHungry();
		
	       assertEquals("occupant should have one thing it needs to fix. It doesn't.", occupant.eState.hungry, eatingState.hungry);

	       assertTrue("occupants scheduler should have returned true (needs to react to new job), but didn't.",
	        		occupant.pickAndExecuteAnAction());
	       
	       assertEquals("occupant should have one thing it needs to fix. It doesn't.", occupant.eState.eating, eatingState.eating);

		     
	    		   assertTrue("occupants scheduler should have returned true (needs to react to new job), but didn't.",
	   	        		occupant.pickAndExecuteAnAction());
	   	       	        	
	    	       assertEquals("occupant should have one thing it needs to fix. It doesn't.", occupant.eState.washing, eatingState.washing);

	    	       assertTrue("occupants scheduler should have returned true (needs to react to new job), but didn't.",
		   	        		occupant.pickAndExecuteAnAction());
	
	    	       assertEquals("occupant should have one thing it needs to fix. It doesn't.", occupant.eState.nothing, eatingState.nothing);

	    	       
	    	       assertFalse("occupants scheduler should have returned true (needs to react to new job), but didn't.",
		   	        		occupant.pickAndExecuteAnAction());
	}
	
	
	public void testTwoScenario()
	{
		
		//eating then gets message that something is broken
		
		assertEquals("occupant should have one thing it needs to fix. It doesn't.", occupant.eState.hungry, eatingState.hungry);
	       assertEquals("occupant should have one thing it needs to fix. It doesn't.", occupant.fState.nothing, fixState.nothing);
	       assertEquals("occupant should have one thing it needs to fix. It doesn't.", occupant.sState.nothing, shoppingState.nothing);

		
		home2.setOccupant(occupant2);
		oGui2.occupant = occupant2;
		occupant2.owner = true;
		
		occupant2.gotHungry();
		
	       assertEquals("occupant should have one thing it needs to fix. It doesn't.", occupant2.eState.hungry, eatingState.hungry);

	       assertTrue("occupants scheduler should have returned true (needs to react to new job), but didn't.",
	        		occupant2.pickAndExecuteAnAction());
	       
	       assertEquals("occupant should have one thing it needs to fix. It doesn't.", occupant2.eState.eating, eatingState.eating);

		     
	    		   assertTrue("occupants scheduler should have returned true (needs to react to new job), but didn't.",
	   	        		occupant2.pickAndExecuteAnAction());
	    		   
	    	       assertEquals("occupant should have one thing it needs to fix. It doesn't.", occupant2.eState.washing, eatingState.washing);

	   	       	        	
		occupant2.fixAppliance("sink", false);
	       assertEquals("occupant should have one thing it needs to fix. It doesn't.", occupant2.fState.fixed, fixState.fixed);

	       assertTrue("occupants scheduler should have returned true (needs to react to new job), but didn't.",
  	        		occupant2.pickAndExecuteAnAction());
	       assertEquals("occupant should have nothing it needs to fix. It doesn't.", occupant2.needsWork.size(), 0);

	       
	       
	       List<String> needFoods = new ArrayList<String>();
			needFoods.add("pasta");
			
			occupant2.msgNeedFood(needFoods);
			
		       assertEquals("occupant should have one thing it needs to buy. It doesn't.", occupant2.needFd.size(), 1);;
		       
		       //assertEquals("occupant should have one thing it needs to fix. It doesn't.", occupant2.sState.needMarket, shoppingState.needMarket);

		       assertTrue("occupants scheduler should have returned true (needs to react to new job), but didn't.",
		    		   occupant2.pickAndExecuteAnAction());



	       
	       //nothing left to do
	       assertFalse("occupants scheduler should have returned true (needs to react to new job), but didn't.",
 	        		occupant2.pickAndExecuteAnAction());
		
	}
	
	
	
	
	
	
	
}
