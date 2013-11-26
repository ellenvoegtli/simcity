package housing;

import housing.personHome.Appliance;
import housing.personHome;
import housing.Interfaces.Occupant;
import housing.Interfaces.landLord;
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
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import role.Role;




public class LandlordRole extends Role implements landLord
{

	
	//DATA
	Timer timer = new Timer();

	int id;
	List<Property> properties = new ArrayList<Property>();
	Map<OccupantRole, List<String>> ToDo = new HashMap<OccupantRole, List<String>>(); 
	LandlordGui gui;
	private Semaphore atDest = new Semaphore(0,true);
	private List <String> fixJobs = new ArrayList<String>(); 

	//private OccupantRole occupant;

	
	
	//for alert log trace statements
	/* (non-Javadoc)
	 * @see housing.landLord#log(java.lang.String)
	 */
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.ENA_RESTAURANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.ENA_COOK, this.getName(), s);
	}

	
	//MESSAGES
	
	public LandlordRole(PersonAgent p)
	{
		super(p);
		
	}
	
	/* (non-Javadoc)
	 * @see housing.landLord#msgPleaseFix(housing.OccupantRole, java.lang.String)
	 */
	@Override
	public void msgPleaseFix(OccupantRole occp, String appName)
	{
		for(Property pr: properties)
		{
			if(pr.renter == occp )	
				
			{
				fixJobs.add(appName);
				ToDo.put(occp, fixJobs);
			}

		}
	}
	//SCHEDULER
	/* (non-Javadoc)
	 * @see housing.landLord#pickAndExecuteAnAction()
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see housing.landLord#serviceRenter()
	 */
	
	public void serviceRenter()
	{
		for(Occupant occ : ToDo.keySet())
		{
			log("the landlord is going to renters home");
			person.msgNeedToFix();
			//gui.DoGoToRenterHome(occ.getHome());
			int xPos = 0;
			int yPos = 0;
			
				for (String a : ToDo.get(occ))
				{
				  for (Appliance appl : occ.getHome().AAppliances)
				  {
					  if(appl.appliance.equals(a))
					  {
						xPos = appl.getXPos();
						yPos = appl.getYPos();
						appl.working = true;
					
					  }
				   }
				  ToDo.get(occ).remove(a);
				  if(ToDo.get(occ).size() == 0) break;
				}
				
			gui.DoGoToAppliance(xPos, yPos);
			repair();
			ToDo.remove(occ);
			if(ToDo.size() == 0)break;
		}
		gui.DoLeave();	
		setInactive();	
		//gui.DoGoBackHome();

		}			


	
	/* (non-Javadoc)
	 * @see housing.landLord#repair()
	 */
	public void repair()
	{
		/*try {
			occupant.destination.acquire();
		} catch (InterruptedException e) {
			e.logStackTrace();
		}*/
		
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				log("fixed appliance");
				stateChanged();
			}
		},
		1000);
		//timer to execute while owner is fixing appliance;
	}
	
	
	public class Property
	{
		personHome house;
		Occupant renter;
		
		Property(Occupant prs)
		{
			
		}
		
	}


	/* (non-Javadoc)
	 * @see housing.landLord#msgAtDestination()
	 */
	@Override
	public void msgAtDestination() {
		atDest.release();
		stateChanged();		
	}


	public void setGui(LandlordGui landLordGui) 
	{
			this.gui = landLordGui;		
	}


	public void setRenter(Occupant occupant) 
	{
		//this.occupant = occupant;
		//renters.add(occupant);
	}


	@Override
	public void setRenter(OccupantRole occupant) {
		// TODO Auto-generated method stub
		
	}
}
