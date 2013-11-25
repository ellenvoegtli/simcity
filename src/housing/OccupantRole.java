package housing;


import housing.personHome.Appliance;
import housing.personHome.type;
import housing.gui.OccupantGui;
import agent.Agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.concurrent.Semaphore;

import mainCity.PersonAgent;
import role.Role;



public class OccupantRole extends Role
{
	
//DATA
	Timer timer = new Timer();
	private LandlordRole landLord;
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

	
	


public void msgAtDestination()
{
	destination.release();
	stateChanged();
	
}
	
	
public OccupantRole(PersonAgent p, String personNm, boolean owner) 
{
	super(p);
	this.name = personNm;
	this.owner = owner;
	
	if(owner == false)
	{
		rent = 850;
		//what else needs to happen if he rents the house
	}
	if(owner == true)
	{
		//what happens if he owns the house
	}
	
}


public String getName()
{
	return name;
}

	
//MESSAGES
	
public void gotHungry()
{
	System.out.println("person is hungry, will cook himself a meal");
	eState = eatingState.hungry;
	stateChanged();
}

/*public void msgNeedsMaintenance(String appName)
{
	needsWork.add(appName);
	fState = fixState.fixing;
	stateChanged();
}*/

public void msgFixed(String appName)
{
	
	fState = fixState.fixed;
	stateChanged();
}

public void msgNeedFood(List<String> buyFood)
{
	for(String f : buyFood)
	{
		needFd.add(f);
	}
	sState = shoppingState.needMarket;
	stateChanged();
}


public void msgCookFood(String foodCh)
{
	eState = eatingState.cooking;
	stateChanged();
}

	
//SCHEDULER
public boolean pickAndExecuteAnAction()
{
	
	/*if(needsWork.size() == 0)
	{
		for (Appliance app : home.Appliances)
		{
		if(app.working == false)
		{
			print("tool is broken" +app.appliance);
			needsWork.add(app.appliance);
			fState = fixState.fixing;
		}
		}
		return true;
	}*/

	
	if(needsWork.isEmpty() && fState == fixState.nothing )
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
		print("needs to go to the market");
		goToStore();
		return true;
	}
	if(!needsWork.isEmpty() && eState == eatingState.cooking)
	{
		print("needs to fix appliance");
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

public void PayRent()
{
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
			print("calling landlord for maintenance");
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
			print("fixed appliance");
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
	gui.DoGoToFridge();
	try {
		destination.acquire();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	
	home.checkSupplies("pasta");
		
}

public void goToStore()
{
	print("Going To the store to buy groceries");
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
	gui.DoGoToStove();
	
	try{
	destination.acquire();
	} catch (InterruptedException e) {
	e.printStackTrace();
}
	timer.schedule(new TimerTask() {
		//Object cookie = 1;
		public void run() {
			print("Done cooking");
			//EatFood();
			eState = eatingState.eating;
			stateChanged();
			
		}
	},
	3000);
	
	
}

public void EatFood()
{
	gui.DoGoToKitchenTable();
	try{
	destination.acquire();
} catch (InterruptedException e) {
	e.printStackTrace();
}
	timer.schedule(new TimerTask() {
		public void run() {
			print("Done eating");
			
			eState = eatingState.washing;
			stateChanged();
		}
	},
	2000);
	//timer to eat food
}

public void GoWashDishes()
{
	gui.DoGoToSink();
	try{
		destination.acquire();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	
	timer.schedule(new TimerTask() {
		public void run() {
			print("Done washing");
			
			eState = eatingState.nothing;
			stateChanged();
		}
	},
	1000);
}
public void GoRest()
{
	gui.DoGoRest();
}


public personHome getHome() {
	return home;
}


public void setHouse(personHome house) {
	this.home = house;
}

public void setLandLord(LandlordRole land)
{
	this.landLord = land;
}

public void setGui(OccupantGui occupantGui) 
{
	this.gui = occupantGui;	
}

public OccupantGui getGui() {
	return gui;
}



	
}
