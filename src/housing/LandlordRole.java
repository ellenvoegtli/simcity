package housing;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;




public class LandlordRole
{

	
	//DATA
	
	int id;
	List<Property> properties = new ArrayList<Property>();
	Map<OccupantRole, String> ToDo = new HashMap<OccupantRole, String>(); 
	
	
	//MESSAGES
	
	public void msgPleaseFix(OccupantRole occp, String appName)
	{
		ToDo.put(occp, occp.getName());
	}
	//SCHEDULER
	public boolean pickAndExecuteAnAction() 
	{
		if(ToDo.isEmpty() == false)
		{
			serviceRenter();
			return true;
		}
		return false;
	}	
	
	

	
	//ACTIONS
	
	public void serviceRenter()
	{
		for(OccupantRole occ : ToDo.keySet())
		{
			//DoGoToRenterHome(occ.getHouse().getLocation());
			//DoGoToKitchenApp(ToDo.get(occ));
			repair();
			ToDo.remove(occ);
			//DoGoBackHome();
		}
	}
	
	public void repair()
	{
		//timer to execute while owner is fixing appliance;
	}
	
	
	public class Property
	{
		personHome house;
		OccupantRole renter;
		
		Property()
		{
			
		}
		
	}
}
