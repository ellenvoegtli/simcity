package housing.HouseTest;


import mainCity.restaurants.enaRestaurant.test.mock.MockCustomer;
import mainCity.restaurants.enaRestaurant.test.mock.MockWaiter;
import housing.OccupantRole;
import housing.HouseTest.HouseMock.MockLandLord;
import junit.framework.*;


public class OccupantTest extends TestCase
{

	OccupantRole occupant;
	MockLandLord landlord;
	
	public void setUp() throws Exception{
		super.setUp();	
		
		landlord = new MockLandLord("landlord");
		
	}	
	
	
	
	
	public void testOneNormalScenerion()
	{
		//landlord.
	}
}
