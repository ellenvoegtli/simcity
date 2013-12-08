package housing;


import housing.personHome.Appliance;
import housing.Interfaces.Occupant;
import housing.Interfaces.OccupantGuiInterface;
import housing.gui.HomePanel;
import housing.gui.OccupantGui;
import agent.Agent;
import mainCity.contactList.ContactList;
import mainCity.gui.AnimationPanel;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.concurrent.Semaphore;

import mainCity.PersonAgent;
import role.Role;



public class OccupantRole extends Role implements Occupant
{
	
//DATA
	Timer timer = new Timer();
	private housing.Interfaces.landLord landLord;
	//private houseAgent house;
	private personHome home;
	public OccupantGuiInterface gui;
	public boolean owner;
	public PersonAgent person;
	//private boolean checking = true;
	private String meal = "pasta";
	private String name;
	private int rent;
	
	private Semaphore destination = new Semaphore(0,true);
	
	public enum eatingState {hungry, cooking, eating, washing, nothing};
	public eatingState eState = eatingState.nothing;
	public enum fixState{fixing, fixed, nothing};
	public fixState fState = fixState.nothing;
	public enum shoppingState {needMarket, shopping, reStocking, nothing};
	public shoppingState sState = shoppingState.nothing;
	
	
	public List<String> needsWork = Collections.synchronizedList(new ArrayList<String>());
	public List<String> needFd = new ArrayList<String>();
	
	
	//for alert log trace statements
	/* (non-Javadoc)
	 * @see housing.Occupant#log(java.lang.String)
	 */
	
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.OCCUPANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.LANDLORD, this.getName(), s);
	}



@Override
public void msgAtDestination()
{
	print("RELEASING SEMAPHORE------------");
	destination.release();
	stateChanged();
	
}
	
@Override
public void msgLeaveHome()
{
	setInactive();
}
	
public OccupantRole(PersonAgent p, String personNm) 
{
	super(p);
	this.name = personNm;


	if (AnimationPanel.apartments.containsKey(p.getHomePlace()) )
	{
		owner = false;
		rent = 850;
	}
	if(AnimationPanel.houses.containsKey(p.getHomePlace()))
	{
		owner = true;
	}
	
	ContactList.getInstance().setOccInstance(this);

}

public OccupantRole(PersonAgent p) {
	super(p);
	this.person =p;
	
}


@Override
public String getName()
{
	return name;
}

	
//MESSAGES
	

@Override
public void gotHungry()
{
	log("person is hungry, will cook himself a meal");
	eState = eatingState.hungry;
	stateChanged();
}

public void applianceBroke()
{
	log("user set appliance to broken");
	String appln = "sink";
	switch((int) (Math.random() * 4)) {
	case 0:
		appln = "TV";		
		break;
	case 1:
		appln = "sink";
		break;
	case 2:
		appln = "fridge";
		break;
	case 3:
		appln = "stove";
		break;
	}
	needsWork.add(appln);
	fState = fixState.fixing;
	stateChanged();
	
}

public void msgNeedsMaintenance(String appName)
{
	needsWork.add(appName);
	fState = fixState.fixing;
	stateChanged();
}

/* (non-Javadoc)
 * @see housing.Occupant#msgFixed(java.lang.String)
 */
@Override
public void msgFixed(String appName)
{
	
	fState = fixState.fixed;
	stateChanged();
}

/* (non-Javadoc)
 * @see housing.Occupant#msgNeedFood(java.util.List)
 */
@Override
public void msgNeedFood(List<String> buyFood)
{
	for(String f : buyFood)
	{
		needFd.add(f);
	}
	sState = shoppingState.needMarket;
	stateChanged();
}


/* (non-Javadoc)
 * @see housing.Occupant#msgCookFood(java.lang.String)
 */
@Override
public void msgCookFood(String foodCh)
{
	eState = eatingState.cooking;
	stateChanged();
}

	
//SCHEDULER
/* (non-Javadoc)
 * @see housing.Occupant#pickAndExecuteAnAction()
 */
@Override
public boolean pickAndExecuteAnAction()
{
	
	/*if(needsWork.size() == 0)
	{
		for (Appliance app : home.Appliances)
		{
		if(app.working == false)
		{
			log("tool is broken" +app.appliance);
			needsWork.add(app.appliance);
			fState = fixState.fixing;
		}
		}
		return true;
	}*/
	
	if(needsWork.isEmpty() && fState == fixState.nothing && eState == eatingState.nothing )
	{
		//checkMaintenance();
		return true;
	}
	
	if (eState == eatingState.hungry && sState == shoppingState.nothing) 
	{
		wantsToEat(meal);
		return true;
	}
	if (sState == shoppingState.needMarket)
	{
		log("needs to go to the market");
		goToStore();
		return true;
	}
	if(!needsWork.isEmpty() && eState == eatingState.cooking)
	{
		log("needs to fix appliance");
		serviceAppliance();	
		return true;
	}

	
	if (sState == shoppingState.shopping)
	{
		//do nothing, things put on hold since person has left the house
		return true;
	}
	if (sState == shoppingState.reStocking)
	{
		sState = shoppingState.nothing;
		eState = eatingState.hungry;
		return true;
	}
	if (eState == eatingState.cooking)
	{
		print("******************");
		cookAMeal();
		return true;
	}
	
	if(eState == eatingState.eating)
	{
		print("----------------------");

		EatFood();
		return true;
	}
	
	if(eState == eatingState.washing)
	{
		GoWashDishes();
		return true;
	}

	if (eState == eatingState.nothing && sState == shoppingState.nothing && (fState == fixState.nothing || fState == fixState.fixed) )
	{
		GoRest();
		return true;
	}
	if(owner == false)
	{
		PayRent();
		return true;
	}
	if(!person.getRoles().isEmpty()) {//makes the person leave the home if there's something else to do
		setInactive();
		return true;
	}
	
	return false;
}
	
//ACTIONS
	
private void checkMaintenance() 
{
		home.CheckAppliances();
}

/* (non-Javadoc)
 * @see housing.Occupant#PayRent()
 */

public void PayRent()
{
	log("pay the owner rent money");
	//timer to run for a reasonable amount of time to make rent due, a "week?"
	//bank.DirectDeposit(owner.id, rent);
}
/* (non-Javadoc)
 * @see housing.Occupant#serviceAppliance()
 */

public void serviceAppliance()
{
	synchronized(needsWork)
	{
	for(String app : needsWork)
	{
		if(owner == false)
		{
			log("calling landlord for maintenance");
			landLord.msgPleaseFix(this, app);
		}
		if(owner == true)
		{
			fixAppliance(app);
		}
		
		needsWork.remove(app);
	}
	}
	fState = fixState.fixed;
}



public void fixAppliance(String app)
{
	int xPos = 0;
	int yPos = 0;
		
	for (Appliance appl : home.Appliances)
	{
		if(appl.appliance.equals(app))
		{
			xPos = appl.getXPos();
			yPos = appl.getYPos();
			appl.setWorking(true);
			
		}
		
	}
	gui.DoGoToAppliance(xPos, yPos);
	try {
		destination.acquire();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	
	timer.schedule(new TimerTask() {
		Object cookie = 1;
		public void run() {
			log("fixed appliance");
			fState = fixState.fixed;
			stateChanged();
		}
	},
	1000);
	//timer runs for period of time to allow for appliance to be "fixed"
	//state = occupantState.fixed;
	
}



public void wantsToEat(String mealChoice)
{
	log("has reached the fridge");
	print("LOOKING IN FRIDGE?????");
	if(owner) gui.DoGoToFridge();
	
	//else if(!owner) gui.DoGoToFridgeA();
	
	
	try {
		destination.acquire();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	
	
	print("LOOKING IN FRIDGE FOR FOOD");
	home.checkSupplies("pasta");
		
}



public void goToStore()
{
	log("Going To the store to buy groceries");
	gui.DoLeave();
	super.setInactive();
	person.msgGoToMarket();
	
	sState = shoppingState.shopping;
}



public void restockKitchen()
{
	home.GroceryListDone();	
	sState = shoppingState.reStocking;
}


public void cookAMeal()
{
	if(owner) gui.DoGoToStove();
	
	if(!owner) gui.DoGoToStoveA();
	
	try{
	destination.acquire();
	} catch (InterruptedException e) {
	e.printStackTrace();
}
	timer.schedule(new TimerTask() {
		//Object cookie = 1;
		public void run() {
			log("Done cooking");
			//EatFood();
			eState = eatingState.eating;
			stateChanged();
			
		}
	},
	1000);
	
	
}


public void EatFood()
{
	if (owner) gui.DoGoToKitchenTable();
	if(!owner) gui.DoGoToKitchenTableA();
	
	try{
	destination.acquire();
} catch (InterruptedException e) {
	e.printStackTrace();
}
	timer.schedule(new TimerTask() {
		public void run() {
			log("Done eating");
			
			eState = eatingState.washing;
			stateChanged();
		}
	},
	2000);
	//timer to eat food
}



public void GoWashDishes()
{
	if (owner) gui.DoGoToSink();
	if(!owner) gui.DoGoToSink();
	
	try{
		destination.acquire();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	
	timer.schedule(new TimerTask() {
		public void run() {
			log("Done washing");
			
			eState = eatingState.nothing;
			stateChanged();
		}
	},
	1000);
}

public void GoRest()
{
	if(owner) gui.DoGoRest();
	if(!owner) gui.DoGoRestA();
}

public personHome getHome() {
	return home;
}



public void setHouse(personHome house) {
	this.home = house;
}



public void setLandLord(housing.Interfaces.landLord land)
{
	this.landLord = land;
}


public void setGui(OccupantGuiInterface occupantGui) 
{
	this.gui = occupantGui;	
}


public OccupantGuiInterface getGui() {
	return gui;
}



	
}
