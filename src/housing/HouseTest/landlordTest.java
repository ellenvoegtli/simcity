package housing.HouseTest;


import mainCity.PersonAgent;
import mainCity.PersonAgent.ActionType;
import mainCity.gui.PersonGui;
import mainCity.interfaces.PersonGuiInterface;
import mainCity.restaurants.enaRestaurant.EnaCashierRole;
import mainCity.restaurants.enaRestaurant.test.mock.MockCustomer;
import mainCity.restaurants.enaRestaurant.test.mock.MockMarket;
import mainCity.restaurants.enaRestaurant.test.mock.MockWaiter;
import mainCity.test.Mock.MockPersonGui;
import housing.LandlordRole;
import housing.personHome;
import housing.HouseTest.HouseMock.MockLandLordGui;
import housing.HouseTest.HouseMock.MockOccupant;
import housing.Interfaces.Occupant;
import junit.framework.*;


public class landlordTest extends TestCase {
	
	personHome home;
	personHome home2;
	personHome home3;

	LandlordRole landlord;
	MockOccupant occupant1;
	MockOccupant occupant2;
	MockOccupant occupant3;
	MockLandLordGui lGui;
	PersonGuiInterface g;
	PersonGui pg;
	
	
	public void setUp() throws Exception{
		super.setUp();	
		
		PersonAgent p = new PersonAgent("LL");
		pg = new PersonGui(p);
		landlord = new LandlordRole(p);
		//g = new PersonGuiInterface();
		p.setGui(pg);
		
		p.addRole(ActionType.maintenance, landlord);
		
		occupant1 = new MockOccupant("occ1");	
		occupant2 = new MockOccupant("occ2");
		occupant3 = new MockOccupant("occ3");
	    home = new personHome(occupant1);
	    home2 = new personHome(occupant2);
	    home3 = new personHome(occupant3);
	    lGui = new MockLandLordGui("LL");
	    landlord.setGui(lGui);
		

		
	}
	
	
	//----------------------------Test Case One ------------------------------//
	public void testOneNormalOwnerScenario()
	{
		//---- one landlord and a renter---/ 
		occupant1.landLord  = landlord;
		occupant1.setHouse(home);
		home.setOccupant(occupant1);
		lGui.landlord = landlord;
		
		//Check Preconditions
        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 0);

		//Part 1
		
		landlord.msgPleaseFix(occupant1, "sink");
		//postconditions
		
       assertEquals("landlord should have one thing it needs to fix. It doesn't.", landlord.getFixJobs().size(), 1);
       assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 1);
       //part 2
       
        assertTrue("landlords scheduler should have returned true (needs to react to new job), but didn't.",
        		landlord.pickAndExecuteAnAction());
        
		assertTrue("Landlord should have a log that reads \"message recieved to fix\" Instead, the landlord event log reads: "  + landlord.log.getLastLoggedEvent().toString(),landlord.log.containsString("message recieved to fix"));
		  assertFalse("landlords scheduler should have returned true (needs to react to new job), but didn't.",
	        		landlord.pickAndExecuteAnAction());
		
		
	}
	
	public void testTwoNormalRenterScnenario()
	{
		//---two renters added to one landlord---//
		//Pre conditions for part one
		
        assertEquals("landlord should have no renters in his list of renters.", landlord.renters.size(), 0);
        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 0);

		//step 1
		occupant1.landLord  = landlord;
		occupant1.setHouse(home);
		landlord.setRenter(occupant1);
		home.setOccupant(occupant1);
		occupant2.landLord = landlord;
		landlord.setRenter(occupant2);
		occupant2.setHouse(home2);
		lGui.landlord =  landlord;

		//Post Conditions for step 1 and pre conditions for step 2
		
        assertEquals("landlord should have two renters in his list of renters.", landlord.renters.size(), 2);
        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 0);
		
        //Step 2
        landlord.msgPleaseFix(occupant1, "TV");
		assertTrue("Landlord should have a log that reads \"message recieved to fix\" Instead, the landlord event log reads: "  + landlord.log.getLastLoggedEvent().toString(),landlord.log.containsString("message recieved to fix"));

       
        // post conditions for step 2 and 
        assertEquals("landlord should have two renters in his list of renters.", landlord.renters.size(), 2);
        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 1);
        assertEquals("landlord should have one thing it needs to fix. It doesn't.", landlord.getFixJobs().size(), 1);
        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 1);

        assertTrue("landlords scheduler should have returned true (needs to react to new job), but didn't.",
        		landlord.pickAndExecuteAnAction());
        
        //PreConditions for next step 
        
        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 0);
        assertEquals("landlord should have one thing it needs to fix. It doesn't.", landlord.getFixJobs().size(), 0);

        //Step 3
        landlord.msgPleaseFix(occupant2, "TV");
		assertTrue("Landlord should have a log that reads \"message recieved to fix\" Instead, the landlord event log reads: "  + landlord.log.getLastLoggedEvent().toString(),landlord.log.containsString("message recieved to fix"));

        assertEquals("landlord should have two renters in his list of renters.", landlord.renters.size(), 2);
        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 1);
        assertEquals("landlord should have one thing it needs to fix. It doesn't.", landlord.getFixJobs().size(), 1);
        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 1);

        assertTrue("landlords scheduler should have returned true (needs to react to new job), but didn't.",
        		landlord.pickAndExecuteAnAction());
        
        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 0);
        assertEquals("landlord should have one thing it needs to fix. It doesn't.", landlord.getFixJobs().size(), 0);
        
        //nothing more left to do, has gone to both renters to for maintenance
        
        assertFalse("landlords scheduler should have returned true (needs to react to new job), but didn't.",
        		landlord.pickAndExecuteAnAction());

        
		
	}
	
	public void testThreeLandlordScenario()
	{
		//PreConditions for test 3, step 1
		  assertEquals("landlord should have no renters in his list of renters.", landlord.renters.size(), 0);
	        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 0);

		//Part 1	
			occupant1.landLord  = landlord;
			occupant1.setHouse(home);
			landlord.setRenter(occupant1);
			home.setOccupant(occupant1);
			occupant2.landLord = landlord;
			landlord.setRenter(occupant2);
			occupant2.setHouse(home2);
			occupant3.landLord = landlord;
			landlord.setRenter(occupant3);
			occupant3.setHouse(home3);
		//PostConditions for part 1
			
	        assertEquals("landlord should have two renters in his list of renters.", landlord.renters.size(), 3);

	        landlord.msgPleaseFix(occupant1, "TV");
	        assertTrue("Landlord should have a log that reads \"message recieved to fix\" Instead, the landlord event log reads: "  + landlord.log.getLastLoggedEvent().toString(),landlord.log.containsString("message recieved to fix"));

	        
	        // post conditions for step 2 and 
	        assertEquals("landlord should have two renters in his list of renters.", landlord.renters.size(), 3);
	        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 1);
	        assertEquals("landlord should have one thing it needs to fix. It doesn't.", landlord.getFixJobs().size(), 1);
	        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 1);

	        assertTrue("landlords scheduler should have returned true (needs to react to new job), but didn't.",
	        		landlord.pickAndExecuteAnAction());
	        
	        //PreConditions for next step 
	        
	        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 0);
	        assertEquals("landlord should have one thing it needs to fix. It doesn't.", landlord.getFixJobs().size(), 0);

	        
	        
	        landlord.msgPleaseFix(occupant2, "sink");
	        
	        assertTrue("Landlord should have a log that reads \"message recieved to fix\" Instead, the landlord event log reads: "  + landlord.log.getLastLoggedEvent().toString(),landlord.log.containsString("message recieved to fix"));

	        assertEquals("landlord should have two renters in his list of renters.", landlord.renters.size(), 3);
	        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 1);
	        assertEquals("landlord should have one thing it needs to fix. It doesn't.", landlord.getFixJobs().size(), 1);
	        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 1);

	        assertTrue("landlords scheduler should have returned true (needs to react to new job), but didn't.",
	        		landlord.pickAndExecuteAnAction());
	        
	        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 0);
	        assertEquals("landlord should have one thing it needs to fix. It doesn't.", landlord.getFixJobs().size(), 0);
	        
	        
	        
	        landlord.msgPleaseFix(occupant3, "fridge");
	        
	        assertTrue("Landlord should have a log that reads \"message recieved to fix\" Instead, the landlord event log reads: "  + landlord.log.getLastLoggedEvent().toString(),landlord.log.containsString("message recieved to fix"));

	        assertEquals("landlord should have two renters in his list of renters.", landlord.renters.size(), 3);
	        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 1);
	        assertEquals("landlord should have one thing it needs to fix. It doesn't.", landlord.getFixJobs().size(), 1);
	        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 1);

	        assertTrue("landlords scheduler should have returned true (needs to react to new job), but didn't.",
	        		landlord.pickAndExecuteAnAction());
	        
	        assertEquals("landlord should nothing it needs to fix. It doesn't.", landlord.getToDo().size(), 0);
	        assertEquals("landlord should have one thing it needs to fix. It doesn't.", landlord.getFixJobs().size(), 0);
	        
	        //nothing more left to do, has gone to both renters to for maintenance
	        
	        assertFalse("landlords scheduler should have returned true (needs to react to new job), but didn't.",
	        		landlord.pickAndExecuteAnAction());

	        

	}
	
}
