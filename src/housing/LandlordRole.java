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
	
	
//public List<String> needsWork = Collections.synchronizedList(new ArrayList<String>());
//DO THID ******************
	
	
	int id;
	List<Property> properties = new ArrayList<Property>();
	Map<OccupantRole, List<String>> ToDo = new HashMap<OccupantRole, List<String>>(); 
	LanLordGuiInterface gui;
	private Semaphore atDest = new Semaphore(0,true);
	private List <String> fixJobs = new ArrayList<String>(); 
	public List<Occupant> renters = new ArrayList<Occupant>();
	private OccupantRole occupant;
	private OccupantRole occ;
	public PersonAgent person;
	 public EventLog log = new EventLog();

	
	

	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.OCCUPANT, this.getName(), s);
	}

	
	//MESSAGES
	
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

		if(ToDo.isEmpty() == false)
		{
			serviceRenter();
			return true;
		}
		return false;
	}	
	
	

	
	//Actions
	
	public void serviceRenter()
	{
	if(occ.isFree)
	{
		occ.isFree = true;
		occ.gui.DoLeave();
		try {
			occ.getDestinationSem().acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		occ.setNotActive();
		for(OccupantRole oc : ToDo.keySet())
		{
			log("the landlord is going to renters home");
			log.add(new LoggedEvent("going to the renters home"));
			
			System.out.println(oc.person.getHomePlace().getXLoc());
			System.out.println(oc.person.getHomePlace().getYLoc());
			
			person.msgNeedToFix(oc.person.getHomePlace());
			
			int xPos = 0;
			int yPos = 0;
			
				for (String a : ToDo.get(oc))
				{
				  for (Appliance appl : oc.getHome().getAAList())
				  {
					  if(appl.appliance.equals(a))
					  {
						xPos = appl.getXPos();
						yPos = appl.getYPos();
						appl.setWorking(true);
					
					  }
				   }
				  ToDo.get(oc).remove(a);
				  if(ToDo.get(oc).size() == 0) break;
				}
				
			//gui.DoGoToAppliance(xPos, yPos);
			repair();
			ToDo.remove(oc);
			if(ToDo.size() == 0)break;
		}
		//gui.DoLeave();	
		try {
			atDest.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.setInactive();
		setInactive();	

		}
	}


	
	
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
	
	
	
	public Map<OccupantRole, List<String>> getToDo()
	{
		return ToDo;
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


	public void setGui(LanLordGuiInterface landlordGui) 
	{
			this.gui = landlordGui;		
	}


	public void setRenter(OccupantRole occupant) 
	{
		this.occupant = occupant;
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
