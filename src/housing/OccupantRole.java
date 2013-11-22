package housing;


import housing.personHome.type;
import housing.gui.OccupantGui;
import agent.Agent;

import java.util.ArrayList;
import java.util.TimerTask;
import java.util.List;


public class OccupantRole extends Agent
{
	
//DATA
	
	private LandlordRole owner;
	//private houseAgent house;
	private personHome home;
	public OccupantGui gui;
	private boolean renter;
	private String meal;
	private String name;
	private int rent;
	enum occupantState {fixing, fixed, hungry, needMarket, shopping, reStocking, cooking, eating, nothing};
	occupantState state = occupantState.hungry;
	public List<String> needsWork = new ArrayList<String>();
	public List<String> needFd = new ArrayList<String>();

	
	
public OccupantRole(String person, personHome hm, boolean rent, LandlordRole ownr)
{
	super();
	name = person;
	setHouse(hm);
	renter = rent;
	if(!renter)
	{
			owner = ownr;

	}
	if(renter)
	{
		//this = ownr;
	}
}
	
	
public OccupantRole(String string) {
	super();
	this.name = string;
}


public String getName()
{
	return name;
}

	
//MESSAGES
	
public void gotHungry()
{
	state = occupantState.hungry;
	stateChanged();
}

public void msgNeedsMaintenance(String appName)
{
	needsWork.add(appName);
	state = occupantState.fixing;
	stateChanged();
}

public void msgFixed(String appName)
{
	state = occupantState.fixed;
	stateChanged();
}

public void msgNeedFood(List<String> buyFood)
{
	for(String f : buyFood)
	{
		needFd.add(f);
	}
	state = occupantState.needMarket;
	stateChanged();
}

public void msgFoodAvailable(String foodCh)
{
	state = occupantState.cooking;
	stateChanged();
}
public void msgCooked(String meal)
{
	state = occupantState.eating;
	stateChanged();
}
	
//SCHEDULER
protected boolean pickAndExecuteAnAction()
{

	if(renter == true)
	{
		PayRent();
		return true;
	}
	if(!needsWork.isEmpty())
	{
		serviceAppliance();	
		return true;
	}

	if (state == occupantState.hungry)
	{
		wantsToEat(meal);
		return true;
	}

	if (state == occupantState.needMarket)
	{
		goToStore();
		return true;
	}
	if (state == occupantState.shopping)
	{
		//do nothing, things put on hold since person has left the house
		return true;
	}
	if (state == occupantState.reStocking)
	{
		state = occupantState.hungry;
		return true;
	}
	if (state == occupantState.cooking)
	{
		cookAMeal();
		return true;
	}

	if (state == occupantState.eating)
	{
		EatFood();
		return true;
	}

	if (state == occupantState.nothing)
	{
		//nothing, stays at home until is called somewhere else
		return true;
	}
	
	return false;
}
	
//ACTIONS
	
public void PayRent()
{
	//timer to run for a reasonable amoutn of time to make rent due, a "week?"
	//bank.DirectDeposit(owner.id, rent);
}
public void serviceAppliance()
{
	for(String app : needsWork)
	{
		if(renter == true)
		{
			owner.msgPleaseFix(this, app);
		}
		if(renter == false)
		{
			//DoGoToKitchenApp(app);
			fixAppliance(app);
		}
		
		needsWork.remove(app);
	}
}

public void fixAppliance(String app)
{
	//timer runs for period of time to allow for appliance to be "fixed"
	state = occupantState.fixed;
	
}

public void wantsToEat(String mealChoice)
{
	home.checkSupplies(mealChoice);
	
	//getHouse().msgCheckForFood(mealChoice);
	
}

public void goToStore()
{
	//person.msgGoToMarket();
	state = occupantState.shopping;
}

public void restockKitchen()
{
	home.GroceryListDone();
	//getHouse().msgNewGroceries(needFd);
	
	state = occupantState.reStocking;
}
public void cookAMeal()
{
	home.cookFood(meal);
	//getHouse().msgCookMeal(meal);
	
}

public void EatFood()
{
	//DoGoToKitchenTable();
	//timer to eat food
	state = occupantState.nothing;
}


public personHome getHome() {
	return home;
}


public void setHouse(personHome house) {
	this.home = house;
}


public void setGui(OccupantGui occupantGui) 
{
	this.gui = occupantGui;	
}

public OccupantGui getGui() {
	return gui;
}


public void msgCookFood(String meal2) 
{
	gui.DoGoToStove();
	
}
	
}
