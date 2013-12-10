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
import role.Role;



public class OccupantRole extends Role implements Occupant
{
	
//DATA
	Timer timer = new Timer();
	private LandlordRole landLord;
	private personHome home;
	public OccupantGuiInterface gui;
	public boolean owner;
	public PersonAgent person;
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
	
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.OCCUPANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.LANDLORD, this.getName(), s);
	}



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

	for (HomeObject homer : AnimationPanel.houses)
	{	
		if(homer.getBuild().equals(p.getHomePlace()))
		{
			owner = true;
			landLord = new LandlordRole(p);
		}
	}
	
	int count = 0;
	for(ApartmentObject apartment : AnimationPanel.apartments)
	{
		if(apartment.getBuild().equals(p.getHomePlace()))
		{
			owner = false;
			needsWork.add("stove");
			rent = 850;
			setLandLord(ContactList.getInstance().getLandLords().get(count));
		}
		count++;
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
		checkMaintenance();
		//return true;
	}
	if(!needsWork.isEmpty() && (eState == eatingState.hungry || eState == eatingState.nothing) )
	{
		System.out.println("++++++++++++++++++++++++++++++++++++");
		log("needs to fix appliance");
		serviceAppliance();	
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
		//print("----------------------");
		//EatFood();
		return false;
	}
	
	if(eState == eatingState.washing)
	{
		//print("----------------------");

		//GoWashDishes();
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
		//return true;
	}
	if(!person.getRoles().isEmpty()) {//makes the person leave the home if there's something else to do
		setInactive();
		return true;
	}
	
	System.out.println("FOUND NOTHING, END OF SCHEDULER");
	return false;
}
	
//ACTIONS
	
private void checkMaintenance() 
{
		home.CheckAppliances();
}



public void PayRent()
{
	log("pay the owner rent money");
	//timer to run for a reasonable amount of time to make rent due, a "week?"
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
	
	else if(!owner) gui.DoGoToFridgeA();
	
	
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
	sState = shoppingState.shopping;
	gui.DoLeave();
	try {
		destination.acquire();
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
	destination.acquire();
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
	destination.acquire();
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
		destination.acquire();
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
	if(owner) gui.DoGoRest();
	if(!owner) gui.DoGoRestA();
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


public void setGui(OccupantGuiInterface occupantGui) 
{
	this.gui = occupantGui;	
}


public OccupantGuiInterface getGui() {
	return gui;
}


	
}
