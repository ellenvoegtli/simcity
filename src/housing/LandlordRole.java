package housing;

import housing.personHome.Appliance;
import housing.personHome;
import housing.HouseTest.HouseMock.EventLog;
import housing.HouseTest.HouseMock.LoggedEvent;
import housing.HouseTest.HouseMock.MockLandLordGui;
import housing.Interfaces.LanLordGuiInterface;
import housing.Interfaces.Occupant;
import housing.Interfaces.landLord;
import housing.OccupantRole.fixState;
import housing.gui.LandlordGui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import agent.Agent;
import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
//import mainCity.test.EventLog;
import role.Role;




public class LandlordRole extends Role implements landLord
{

	
	//DATA
	Timer timer = new Timer();
	int id;
	Map<OccupantRole, List<String>> ToDo = new HashMap<OccupantRole, List<String>>(); 
	LanLordGuiInterface gui;
	private Semaphore atDest = new Semaphore(0,true);
	private List <String> fixJobs = new ArrayList<String>(); 
	public List<Occupant> renters = new ArrayList<Occupant>();
	private OccupantRole occ;
	public PersonAgent person;
	 public EventLog log = new EventLog();
	 public enum landlordActive {leaving, working, nothing, done, end};
	 public landlordActive lDActive = landlordActive.nothing;
	 
	
	 //for trace panel
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.OCCUPANT, this.getName(), s);
	}

	


	public LandlordRole(PersonAgent p)
	{
		super(p);
		ContactList.getInstance().setLandLordInstance(this);
		this.person = p;
	}
	public LandlordRole(PersonAgent p, OccupantRole oR)
	{
		super(p);
		this.occ = oR;
		this.person = p;
		ContactList.getInstance().setLandLordInstance(this);
	}
	
	//MESSAGES
	@Override
	public void msgPleaseFix(OccupantRole occp, String appName)
	{
		System.out.println("LANDLORD GETS MESSAGE TO FIX APPLIANCE FOR RENTER");
		log.add(new LoggedEvent("message recieved to fix"));
				fixJobs.add(appName);
				ToDo.put(occp, fixJobs);

			stateChanged();
			pickAndExecuteAnAction();
		
	}
	
	public boolean pickAndExecuteAnAction() 
	{
		if(lDActive == landlordActive.nothing && occ.isActive() == true && occ.isFree == true)
		{
			if(ToDo.isEmpty() == false)
			{
				goToRenter();
				return true;
			}
		}
		if(lDActive == landlordActive.nothing)
		{
			if(ToDo.isEmpty() == false)
			{
				lDActive = landlordActive.working;	
				return true;
			}
		}

		if(lDActive == landlordActive.working)
		{
			serviceRenter();
			return true;
		}
		
		if(lDActive == landlordActive.done)
		{
			goBackHome();
		}

		return false;
	}	
	
	

	
	//Actions
	
	public void goToRenter()
	{
		if(occ.isFree)
		{
			lDActive = landlordActive.leaving;
			occ.isFree = true;
			occ.gui.DoLeave();
		try {
			occ.getDestinationSem().acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		occ.gui.getAnimationP().getPersonGuis().remove(occ.gui);
		super.setInactive();
		occ.setNotActive();

		
		for(OccupantRole oc : ToDo.keySet())
		{
			log("the landlord is going to renters home");
			log.add(new LoggedEvent("going to the renters home"));
			
			super.setInactive();
			person.msgNeedToFix(oc.person.getHomePlace());
			stateChanged();
			break;

		}
	}
}

	public void serviceRenter()
	{
		log("the landlord is working on home");
		int xPos = 0;
		int yPos = 0;
		for(OccupantRole oc : ToDo.keySet())
		{
			for (String a : ToDo.get(oc))
			{
			  for (Appliance appl : oc.getHome().getAAList())
			  {
				  if(appl.appliance.equals(a))
				  {
					xPos = appl.getXPos();
					yPos = appl.getYPos();
					appl.setWorking(true);
					gui.DoGoToAppliance(xPos, yPos);
					repair();
				  }
			   }
			  ToDo.get(oc).remove(a);
			  if(ToDo.get(oc).size() == 0) break;
			}
			
		ToDo.remove(oc);
		break;
	}
	gui.DoLeave();	
	try {
		atDest.acquire();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	super.setInactive();
	person.roleInactive();
	
	if(ToDo.size() != 0)
	{
		for(OccupantRole oc : ToDo.keySet())
		{
			lDActive = landlordActive.nothing;

			person.msgNeedToFix(oc.person.getHomePlace());
			stateChanged();
			break;
		}

	}
	lDActive = landlordActive.done;
	stateChanged();

}
		
public void goBackHome()
{
	super.setInactive();
	person.roleInactive();
	person.msgGoHome();

	lDActive = landlordActive.end;
	stateChanged();

}


	
	
	public void repair()
	{
		try {
			atDest.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		timer.schedule(new TimerTask() {
			public void run() {
				log("fixed appliance");
				stateChanged();
			}
		},
		1000);
		//timer to execute while owner is fixing appliance;
	}
	
	
	
	public Map<OccupantRole, List<String>> getToDo()
	{
		return ToDo;
	}
	

	@Override
	public void msgAtDestination() {
		atDest.release();
		stateChanged();		
	}


	public void setGui(LanLordGuiInterface landlordGui) 
	{
			this.gui = landlordGui;		
	}


	public void setRenter(OccupantRole occupant) 
	{
		this.occ = occupant;
		renters.add(occupant);
	}


	public List <String> getFixJobs() {
		return fixJobs;
	}


	public void setFixJobs(List <String> fixJobs) {
		this.fixJobs = fixJobs;
	}


	@Override
	public void msgPleaseFix(Occupant occp, String appName) {
		// TODO Auto-generated method stub
		
	}


	



}
