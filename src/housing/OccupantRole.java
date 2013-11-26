package housing;


import housing.personHome.Appliance;
import housing.personHome.type;
import housing.Interfaces.Occupant;
import housing.gui.OccupantGui;
import agent.Agent;
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
	public OccupantGui gui;
	private boolean owner;
	//private boolean checking = true;
	private String meal = "pasta";
	private String name;
	private int rent;
	
	private Semaphore destination = new Semaphore(0,true);
	
	enum eatingState {hungry, cooking, eating, washing, nothing};
	eatingState eState = eatingState.nothing;
	enum fixState{fixing, fixed, nothing};
	fixState fState = fixState.nothing;
	enum shoppingState {needMarket, shopping, reStocking, nothing};
	shoppingState sState = shoppingState.nothing;
	
	
	public List<String> needsWork = Collections.synchronizedList(new ArrayList<String>());
	public List<String> needFd = new ArrayList<String>();

	
	
	//for alert log trace statements
	/* (non-Javadoc)
	 * @see housing.Occupant#log(java.lang.String)
	 */
	
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.ENA_RESTAURANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.ENA_COOK, this.getName(), s);
	}


/* (non-Javadoc)
 * @see housing.Occupant#msgAtDestination()
 */
@Override
public void msgAtDestination()
{
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
	
	
}


/* (non-Javadoc)
 * @see housing.Occupant#getName()
 */
@Override
public String getName()
{
	return name;
}

	
//MESSAGES
	
/* (non-Javadoc)
 * @see housing.Occupant#gotHungry()
 */
@Override
public void gotHungry()
{
	log("person is hungry, will cook himself a meal");
	eState = eatingState.hungry;
	stateChanged();
}

/*public void applianceBroke()
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
	
}*/

/*public void msgNeedsMaintenance(String appName)
{
	needsWork.add(appName);
	fState = fixState.fixing;
	stateChanged();
}*/

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

	
	if(needsWork.isEmpty() && fState == fixState.nothing && eState != eatingState.hungry )
	{
		checkMaintenance();
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
		cookAMeal();
		return true;
	}
	
	if(eState == eatingState.eating)
	{
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

/* (non-Javadoc)
 * @see housing.Occupant#fixAppliance(java.lang.String)
 */

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
			appl.working = true;
			
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

/* (non-Javadoc)
 * @see housing.Occupant#wantsToEat(java.lang.String)
 */

public void wantsToEat(String mealChoice)
{
	if(owner) gui.DoGoToFridge();
	
	if(!owner) gui.DoGoToFridgeA();
	log("has reached the fridge");
	
	try {
		destination.acquire();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	
	home.checkSupplies("pasta");
		
}

/* (non-Javadoc)
 * @see housing.Occupant#goToStore()
 */

public void goToStore()
{
	log("Going To the store to buy groceries");
	gui.DoLeave();
	super.setInactive();
	person.msgGoToMarket();
	
	sState = shoppingState.shopping;
}

/* (non-Javadoc)
 * @see housing.Occupant#restockKitchen()
 */

public void restockKitchen()
{
	home.GroceryListDone();	
	sState = shoppingState.reStocking;
}
/* (non-Javadoc)
 * @see housing.Occupant#cookAMeal()
 */

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
	3000);
	
	
}

/* (non-Javadoc)
 * @see housing.Occupant#EatFood()
 */

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

/* (non-Javadoc)
 * @see housing.Occupant#GoWashDishes()
 */

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
/* (non-Javadoc)
 * @see housing.Occupant#GoRest()
 */

public void GoRest()
{
	if(owner) gui.DoGoRest();
	if(!owner) gui.DoGoRestA();
}


/* (non-Javadoc)
 * @see housing.Occupant#getHome()
 */

public personHome getHome() {
	return home;
}


/* (non-Javadoc)
 * @see housing.Occupant#setHouse(housing.personHome)
 */

public void setHouse(personHome house) {
	this.home = house;
}

/* (non-Javadoc)
 * @see housing.Occupant#setLandLord(housing.LandlordRole)
 */

public void setLandLord(housing.Interfaces.landLord land)
{
	this.landLord = land;
}

/* (non-Javadoc)
 * @see housing.Occupant#setGui(housing.gui.OccupantGui)
 */

public void setGui(OccupantGui occupantGui) 
{
	this.gui = occupantGui;	
}

/* (non-Javadoc)
 * @see housing.Occupant#getGui()
 */
public OccupantGui getGui() {
	return gui;
}



	
}
