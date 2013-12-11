package housing;


import housing.personHome.Appliance;
import housing.Interfaces.Occupant;
import housing.Interfaces.OccupantGuiInterface;
import housing.gui.HomePanel;
import housing.gui.OccupantGui;
import agent.Agent;
import mainCity.contactList.ContactList;
import mainCity.gui.AnimationPanel;
import mainCity.gui.AnimationPanel.ApartmentObject;
import mainCity.gui.AnimationPanel.HomeObject;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

import mainCity.PersonAgent;
import mainCity.PersonAgent.ActionType;
import role.Role;



public class OccupantRole extends Role implements Occupant
{
	
//DATA
	Timer timer = new Timer();
	private LandlordRole landLord;
	private personHome home;
	public OccupantGui gui;
	public boolean owner;
	public boolean isFree;
	public PersonAgent person;
	private String meal = "pasta";
	private String name;
	private int rent;
	private boolean needGui;
		
	private Semaphore destination = new Semaphore(0,true);
	
	public enum eatingState {hungry, cooking, eating, washing, nothing};
	public eatingState eState = eatingState.nothing;
	public enum fixState{fixing, fixed, nothing};
	public fixState fState = fixState.nothing;
	public enum shoppingState {needMarket, shopping, reStocking, nothing};
	public shoppingState sState = shoppingState.nothing;
	public enum homeState {present, gone}
	public homeState hState = homeState.present;
	
	
	public List<String> needsWork = Collections.synchronizedList(new ArrayList<String>());
	public List<String> needFd = new ArrayList<String>();
	
	
	//for alert log trace statements
	
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.OCCUPANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.LANDLORD, this.getName(), s);
	}



@Override
public void msgAtDestination()
{
	getDestinationSem().release();
	stateChanged();
}
	
@Override
public void msgLeaveHome()
{
	setInactive();
}

public Map<String, Integer> getFood(){
	Map<String, Integer> inventory = Collections.synchronizedMap(new TreeMap<String, Integer>());
	for (String s : needFd){
		inventory.put(s, 3);
	}
	return inventory;
}
	
public OccupantRole(PersonAgent p, String personNm) 
{
	super(p);
	this.name = personNm;
	this.person=p;
	needGui = false;

	for (HomeObject homer : AnimationPanel.houses)
	{	
		if(homer.getBuild().equals(p.getHomePlace()))
		{
			owner = true;
			landLord = new LandlordRole(p, this);
		}
	}
	
	//int count = 0;
	for(ApartmentObject apartment : AnimationPanel.apartments)
	{
		if(apartment.getBuild().equals(p.getHomePlace()))
		{
			owner = false;
			rent = 850;
			if(ContactList.getInstance().getLandLords().size() != 0)
			{
				setLandLord(ContactList.getInstance().getLandLords().get(ContactList.getInstance().getLandLords().size()-1));
			}
		}
		//count++;
	}
	
	
}

public OccupantRole(PersonAgent p) {
	super(p);
	this.person = p;
	
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
	synchronized(needsWork)
	{
	needsWork.add(appln);
	}
	fState = fixState.fixing;
	stateChanged();
	
}

public void msgNeedsMaintenance(String appName)
{
	log("recieved message for maintenance");
	synchronized(needsWork)
	{
		needsWork.add(appName);
	}
	fState = fixState.fixing;
	stateChanged();
}

@Override
public void msgFixed(String appName)
{
	
	fState = fixState.fixed;
	stateChanged();
}


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


@Override
public void msgCookFood(String foodCh)
{
	eState = eatingState.cooking;
	stateChanged();
}

	
//SCHEDULER

@Override
public boolean pickAndExecuteAnAction()
{
	
	if(needGui)
	{
		gui.guiAppear();
		needGui = false;
		return true;
	}
	
	if(needsWork.isEmpty() && fState == fixState.nothing && eState == eatingState.nothing )
	{
		//isFree = false;
		if(owner)
			checkMaintenance(false);
		if(!owner)
			checkMaintenance(true);

		//return true;
	}
	if(!needsWork.isEmpty() && (eState == eatingState.hungry || eState == eatingState.nothing) )
	{
		isFree = false;
		log("needs to fix appliance");
		serviceAppliance();	
		return true;
	}
	
	if (eState == eatingState.hungry && sState == shoppingState.nothing) 
	{
		isFree = false;
		wantsToEat(meal);
		return true;
	}
	if (sState == shoppingState.needMarket)
	{
		isFree = false;
		log("needs to go to the market");
		goToStore();
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
		isFree = false;
		cookAMeal();
		return true;
	}
	
	if(eState == eatingState.eating)
	{
		//print("----------------------");
		//EatFood();
		return false;
	}
	
	/*if(eState == eatingState.washing)
	{
		//print("----------------------");

		//GoWashDishes();
		return true;
	}*/

	if (eState == eatingState.nothing && sState == shoppingState.nothing && (fState == fixState.nothing || fState == fixState.fixed) && hState == homeState.present && isFree == false)
	{
		GoRest();
		isFree = true;
		return true;
	}
	if(owner == false && person.getDay() == 3)
	{
		PayRent();
		return true;
	}
<<<<<<< HEAD
	if(!person.getActions().isEmpty() && isFree == true) {//makes the person leave the home if there's something else to do
=======
	if(!person.getActions().isEmpty() && isFree == true && (person.getCurrentAction().type == ActionType.home || person.getCurrentAction().type == ActionType.homeAndEat)) {//makes the person leave the home if there's something else to do
>>>>>>> bd8f9d5cae559bff47321ea27490d6a45019ea98
		gui.DoLeave();
		
		if(owner)
			landLord.setInactive();
		setInactive();
		needGui = true;
		return true;
	}
	
	return false;
}
	

//ACTIONS
	
private void checkMaintenance(boolean renter) 
{
		home.CheckAppliances(renter);
}



public void PayRent()
{
	log("pay the owner rent money");
	
	//bank.DirectDeposit(owner.id, rent);
}


public void serviceAppliance()
{
   synchronized(needsWork)
   {
	 for(String app : needsWork)
	 {
		if(owner == false)
		{
			log("calling landlord for maintenance");
			if(landLord == null)
			{
				log("uhoh, londlord on vacation, must fix on his own! ");
				fixAppliance(app, false);
			}
			
			else
				landLord.msgPleaseFix(this, app);
		}
		if(owner == true)
		{
			log("owner is performing maintenance himself");
			fixAppliance(app, true);
		}
<<<<<<< HEAD
			needsWork.remove(app);
		
=======
		//synchronized(needsWork)
		//{
			needsWork.remove(app);
		//}
>>>>>>> bd8f9d5cae559bff47321ea27490d6a45019ea98
	  }
	}
	fState = fixState.fixed;
}



public void fixAppliance(final String app, Boolean owner)
{
	fState = fixState.nothing;
	int xPos = 0;
	int yPos = 0;
if (owner)	
{
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
		getDestinationSem().acquire();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	
	timer.schedule(new TimerTask() {
		public void run() {
			log("fixed appliance" +app);
			//fState = fixState.fixed;
			stateChanged();
		}
	},
	2000);
}

if (!owner)	
{
	for (Appliance appl : home.AAppliances)
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
		getDestinationSem().acquire();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	
	timer.schedule(new TimerTask() {
		public void run() {
			log("fixed appliance" + app);
			//fState = fixState.fixed;
			stateChanged();
		}
	},
	2000);
}
	
}



public void wantsToEat(String mealChoice)
{
	log("checking fridge for food");
	
		if(owner) gui.DoGoToFridge();
		else if(!owner) gui.DoGoToFridgeA();
	
	try {
		getDestinationSem().acquire();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	
		home.checkSupplies(mealChoice);
		
}


public void goToStore()
{
	log("Going To the store to buy groceries");
	sState = shoppingState.shopping;
	gui.DoLeave();
	try {
		getDestinationSem().acquire();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	super.setInactive();
	person.msgGoToMarket();
	
}

public void restockKitchen()
{
	home.GroceryListDone();	
	sState = shoppingState.reStocking;
}

public void cookAMeal()
{
	eState = eatingState.nothing;
	if(owner) gui.DoGoToStove();
	if(!owner) gui.DoGoToStoveA();
	
	try{
	getDestinationSem().acquire();
	} catch (InterruptedException e) {
	e.printStackTrace();
}
			System.out.println("starting timer for cooking?");
	timer.schedule(new TimerTask() {
		public void run() {
			log("Done cooking");
			//eState = eatingState.eating;
			stateChanged();
			EatFood();	
		}
	},
	1000);
}


public void EatFood()
{
	eState = eatingState.eating;
	//stateChanged();
	
	if (owner) gui.DoGoToKitchenTable();
	if(!owner) gui.DoGoToKitchenTableA();
				System.out.println("eating?");

	try{
	getDestinationSem().acquire();
    }catch (InterruptedException e) {
	e.printStackTrace();
    }
				System.out.println("starting timer for eating?");
	timer.schedule(new TimerTask() {
		public void run() {
			//eState = eatingState.washing;
			stateChanged();
			GoWashDishes();
		}
	},
	600);


	//timer to eat food
}



public void GoWashDishes()
{
	eState = eatingState.washing;
	//stateChanged();
	if (owner) gui.DoGoToSink();
	if(!owner) gui.DoGoToSink();
					System.out.println("Timer ,,,,,,, for washing dishes??");
	try{
		getDestinationSem().acquire();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
					System.out.println("Timer started for washing dishes??");
	timer.schedule(new TimerTask() {
		public void run() {			
			//eState = eatingState.nothing;
			//stateChanged();
			GoRest();
		}
	},
	500);
}

public void GoRest()
{
	isFree = true;
	if(owner) gui.DoGoRest();
	if(!owner) gui.DoGoRestA();
	try{
		getDestinationSem().acquire();
	    }catch (InterruptedException e) {
		e.printStackTrace();
	    }
	
}

public personHome getHome()
{
	return home;
}


public void setHouse(personHome house) 
{
	this.home = house;
}


public void setLandLord(LandlordRole lndlrd)
{
	this.landLord = lndlrd;
}


public void setGui(OccupantGui occupantGui) 
{
	this.gui = occupantGui;	
}


public OccupantGui getGui() {
	return gui;
}



public Semaphore getDestinationSem() {
	return destination;
}



public void setDestinationSem(Semaphore destination) {
	this.destination = destination;
}


public void setNotActive()
{
	super.setInactive();
	hState = homeState.gone;
}



public boolean isNeedGui() {
	return needGui;
}



public void setNeedGui(boolean needGui) {
	this.needGui = needGui;
}

	
}
