package housing;

import housing.personHome.Appliance;
import housing.OccupantRole.fixState;
import housing.gui.LandlordGui;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import agent.Agent;
import mainCity.PersonAgent;
import role.Role;




public class LandlordRole extends Role
{

	
	//DATA
	Timer timer = new Timer();

	int id;
	List<Property> properties = new ArrayList<Property>();
	Map<OccupantRole, String> ToDo = new HashMap<OccupantRole, String>(); 
	LandlordGui gui;
	private Semaphore atDest = new Semaphore(0,true);

	private OccupantRole occupant;

	
	//MESSAGES
	
	public LandlordRole(PersonAgent p)
	{
		super(p);
		
	}
	
	public void msgPleaseFix(OccupantRole occp, String appName)
	{
		for(Property pr: properties)
		{
			if(pr.renter == occp )		
			{
				ToDo.put(occp, occp.getName());
			}

		}
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
			print("the landlord is going to renters home");
			person.msgNeedToFix();
			//gui.DoGoToRenterHome(occ.getHome());
			int xPos = 0;
			int yPos = 0;
			for (Appliance appl : occ.getHome().Appliances)
			{
				if(appl.appliance.equals(ToDo.get(occ)))
				{
					xPos = appl.getXPos();
					yPos = appl.getYPos();
					appl.working = true;
					
				}
				
			}
			
			gui.DoGoToAppliance(xPos, yPos);
			repair();
			ToDo.remove(occ);
			if(ToDo.size() == 0)break;
		}			
		gui.DoGoBackHome();

	}
	
	public void repair()
	{
		/*try {
			occupant.destination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("fixed appliance");
				stateChanged();
			}
		},
		1000);
		//timer to execute while owner is fixing appliance;
	}
	
	
	public class Property
	{
		personHome house;
		OccupantRole renter;
		
		Property(OccupantRole prs)
		{
			
		}
		
	}


	public void msgAtDestination() {
		atDest.release();
		stateChanged();		
	}

	public void setGui(LandlordGui landLordGui) 
	{
			this.gui = landLordGui;		
	}

	public void setRenter(OccupantRole occupant) 
	{
		//this.occupant = occupant;
		//renters.add(occupant);
	}
}
