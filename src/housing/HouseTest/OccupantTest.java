package housing.HouseTest;


import mainCity.PersonAgent;
import mainCity.PersonAgent.ActionType;
import mainCity.gui.PersonGui;
import mainCity.interfaces.PersonGuiInterface;
import mainCity.restaurants.enaRestaurant.test.mock.MockCustomer;
import mainCity.restaurants.enaRestaurant.test.mock.MockWaiter;
import housing.LandlordRole;
import housing.OccupantRole;
import housing.personHome;
import housing.HouseTest.HouseMock.MockLandLord;
import housing.HouseTest.HouseMock.MockLandLordGui;
import housing.HouseTest.HouseMock.MockOccupant;
import housing.HouseTest.HouseMock.MockOccupantGui;
import junit.framework.*;


public class OccupantTest extends TestCase
{

	OccupantRole occupant;
	MockLandLord landlord;
	
	personHome home;
	personHome home2;
	personHome home3;

	MockOccupant occupant1;
	MockOccupant occupant2;
	MockOccupant occupant3;
	MockOccupantGui oGui;
	PersonGuiInterface g;
	PersonGui pg;
	
	
	
	
	public void setUp() throws Exception{
		super.setUp();	
				
		PersonAgent p = new PersonAgent("LL");
		pg = new PersonGui(p);
		occupant = new OccupantRole(p);
		//g = new PersonGuiInterface();
		p.setGui(pg);
		
		p.addRole(ActionType.homeAndEat, occupant);
		
		
		occupant1 = new MockOccupant("occ1");	
		occupant2 = new MockOccupant("occ2");
		occupant3 = new MockOccupant("occ3");
	    home = new personHome(occupant1);
	    home2 = new personHome(occupant2);
	    home3 = new personHome(occupant3);
	    oGui = new MockOccupantGui("LL");
	    occupant.setGui(oGui);
		
		
	}	
	
	
	
	
	public void testOneNormalScenerion()
	{
	
	}
	
	
	public void testTwoScenario()
	{
		
	}
	
	
	public void testThreeScenerio()
	{
		
	}
	
	
	
	
	
}
