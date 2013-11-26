package mainCity.restaurants.enaRestaurant;

import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.restaurants.enaRestaurant.EnaWaiterRole.MyCustomers;
import role.Role;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import mainCity.restaurants.enaRestaurant.gui.EnaCustomerGui;
import mainCity.restaurants.enaRestaurant.interfaces.Customer;

/**
 * Restaurant customer agent.
 */
public class EnaCustomerRole extends Role implements Customer{
	private String name;
	private int hungerLevel = 5;        // determines length of meal
	Timer timer = new Timer();
	private EnaCustomerGui customerGui;
	public int tableNum;
	private String choice; 
	public double cash;
	public double debt;
	private boolean returnCustomer = false;
	// agent correspondents
	private EnaHostRole host;
	private EnaWaiterRole waiter;
	private EnaCashierRole cashier;

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, Waiting, BeingSeated, Decided, Ordering, Ordered, ReOrdered, DoneEating, payingBill, paidLeaving};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followHost, seated, WaiterCall, AskedByWaiter, ReAskedByWaiter,  FoodArrived, recievedCheck, recievedChange,  Done};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public EnaCustomerRole(PersonAgent p, String name)
	{
		super(p);
		this.name = name;
		this.debt = 0;
		
		
					if(name.equals("poor"))
					{
						cash = 5.0;
						log("customer has $ "  + cash);
					}
					
					if(name.equals("debt"))
					{
						cash = 5.0;
						log("customer has $ "  + cash);
						Random rnd = new Random();
						int meal = rnd.nextInt(4)+1;
						if (meal == 1)
							this.choice = "steak";
						 if(meal == 2)
							this.choice = "porkchops";
						 if(meal == 3)
							this.choice = "lamb";
						 if (meal == 4)
							this.choice = "lambchops";
					}
					if(name.equals("cheapest"))
					{
						cash = 6.0;
						log("customer has $ "  +cash);
						this.choice = "lamb";	
					}
					
					if(name.equals("onlyChoice"))
					{
						cash = 6.0;
						log("customer has $ "  +cash);
						this.choice = "lamb";
					}
					else
					{	//assigning a random amount of money to each customer. 
						Random rndC = new Random();
						int money = rndC.nextInt(20)+5;
						cash = money;
						log("customer has $-- "  + cash);
		
						Random rnd = new Random();
						int meal = rnd.nextInt(4)+1;
						if (meal == 1)
							this.choice = "steak";
						 if(meal == 2)
							this.choice = "porkchops";
						 if(meal == 3)
							this.choice = "lamb";
						 if (meal == 4)
							this.choice = "lambchops";
					}
		
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	
	public String getChoice()
	{
		return choice;
	}
	public void setChoice(String ch)
	{
		choice = ch;
	}
	public void setWaiter(EnaWaiterRole waiter) 
	{
		this.waiter = waiter;
	}
	public EnaWaiterRole getWaiter()
	{
		return waiter;
	}
	public void setHost(EnaHostRole host)
	{
		this.host = host;
	}
	public void setCashier(EnaCashierRole cashier)
	{
		this.cashier = cashier;
	}

	public String getCustomerName() 
	{
		return name;
	}
	public void setCh(String ch)
	{
		choice = ch;
	}
	public double getDebt() 
	{
		return debt;
	}
	public void setDebt(double r)
	{
		debt = r;
	}

	public double getCash() 
	{
		return cash;
	}
	
	
	//for alert log trace statements
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.ENA_RESTAURANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.ENA_COOK, this.getName(), s);
	}

	// Messages

	public void gotHungry() {//from animation
		log("I'm hungry");
		if(returnCustomer)
		{
			cash = cash + 10;
		}
		log("customer now has $  " +cash);
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgFollowToTable() 
	{
		log("Received msgFollowToTable");
		event = AgentEvent.followHost;
		stateChanged();
	}

	public void msgWhatWouldYouLike() 
	{
		log("recieves message asking what would customer like");
		
		//from animation
		event = AgentEvent.AskedByWaiter;
		stateChanged();
	}
	public void msgWhatElseWouldYouLike()
	{
		log("recieves message for new order");
		event = AgentEvent.ReAskedByWaiter;
		stateChanged();
	}
	public void msgHereIsYourFood(String choice) 
	{
		//from animation
		log("received "+choice);
		event = AgentEvent.FoodArrived;
		stateChanged();
	}
	
	public void msgHereIsYourCheck()
	{
		event = AgentEvent.recievedCheck;
		stateChanged();
	}
	public void msgHereIsYourChange(double change)
	{
		event = AgentEvent.recievedChange;
		this.cash = change;
		log("the change is" +change) ;
		stateChanged();
	}
	
	public void setNum(int tablePlace)
	{
		tableNum = tablePlace;
	}

	public void msgAnimationFinishedGoToSeat()
	{
		event = AgentEvent.seated;
		stateChanged();
	}
	public void msgAnimationFinishedLeaveRestaurant()
	{
		event = AgentEvent.Done;
		stateChanged();
		
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() 
	{
		//	CustomerAgent is a finite state machine
		
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry )
		{	
			log("agent doing nothing and got hungry");
			state = AgentState.Waiting;
			goToRestaurant();
			return true;
		}
		if (state == AgentState.Waiting && event == AgentEvent.followHost )
		{
			log("agent waiting and following host, then calls SIT done");
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated)
		{
			state = AgentState.Decided;
			MenuBrowse();
			return true;
		}

		if (state == AgentState.Decided && event == AgentEvent.WaiterCall)
		{
			log("customer is finished looking at the menu and is looking to the waiter");
			state = AgentState.Ordering;
			getCustomerGui().Deciding();
			RequestOrder();
			return true;
		}
		if (state == AgentState.Ordering && event == AgentEvent.AskedByWaiter)
		{
			log("it is time to order a choice from the menu");
			state = AgentState.Ordered;
			OrderFood();
			return true;
		}
		
		if (state == AgentState.Ordered && event == AgentEvent.ReAskedByWaiter)
		{
			log("customer picking new item from menu");
			state = AgentState.ReOrdered;
			ReOrderFood();
			return true;
		}
		if ((state == AgentState.Ordered || state == AgentState.ReOrdered) && event == AgentEvent.FoodArrived)
		{
			log("the customer is eating his food/finishing");
			state = AgentState.DoneEating;
			getCustomerGui().Decided();
			EatFood();
			return true;
		}
		if (state == AgentState.DoneEating && event == AgentEvent.Done)
		{
			log("customer is preparing to leave");
			state = AgentState.payingBill;
			FinishedMeal();
			return true;
		}
		if(state == AgentState.payingBill && event == AgentEvent.recievedCheck)
		{
			log("customer is paying the bill");
			state = AgentState.paidLeaving;
			PayCashier();
			return true;
		}
		if (state == AgentState.paidLeaving && event == AgentEvent.recievedChange)
		{
			log("done");
			state = AgentState.DoingNothing;
			getCustomerGui().DoExitRestaurant();
			super.setInactive();
			//no action
			return true;
		}
		
		
		return false;
	}



	//Actions
	private void goToRestaurant() 
	{
		Do("Going to restaurant");
		if(name.equals("poor"))
		{
			log ("no money must leave");
		}
		
		else
		{
			host.msgIWantToEat(this);//send our instance, so he can respond to us

		}
	}
	

	private void SitDown() 
	{
		returnCustomer = true;
		Do("Being seated. Going to table");
		getCustomerGui().DoGoToSeat(tableNum);
	}
	
	private void MenuBrowse()
	{
		timer.schedule(new TimerTask() 
		{
			public void run() 
			{
				log("Browsing the menu");
				event = AgentEvent.WaiterCall;
				stateChanged();
			}
		},
		7000);//getHungerLevel() * 1000);//how long to wait before running task
		//timer for customer to spend some time looking over the menu
		//FoodSelection();
	}
	private void RequestOrder()
	{
		waiter.msgReadyToOrder(this);
		
	}
	
	private void OrderFood()
	{
		log("Ordering a choice from menu");
		waiter.msgHereIsMyChoice(choice, this);
	}
	private void ReOrderFood()
	{
		log("customer choosing new order");
		waiter.msgHereIsMyChoice(choice, this);
	}

	private void EatFood() 
	{
		getCustomerGui().Arriving(choice);
		
		Do("Eating Food");
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				log("Done eating, cookie=" + cookie);
				event = AgentEvent.Done;
				stateChanged();
			}
		},
		8000);//getHungerLevel() * 1000);//how long to wait before running task
	}

		private void FinishedMeal()
		{
			getCustomerGui().Arrived(choice);
			waiter.msgDoneEating(this);
			//leaveTable();
			//PayCashier();
			waiter.msgCheckPlease(this, choice);	
		}

	
	private void PayCashier()
	{			
		cashier.msgPayment(choice, cash, this);
	}
	
	// Accessors, etc.

	public String getName() {
		return name;
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
		//could be a state change. Maybe you don't
		//need to eat until hunger lever is > 5?
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(EnaCustomerGui g) {
		setCustomerGui(g);
	}

	public EnaCustomerGui getGui() {
		return getCustomerGui();
	}

	public EnaCustomerGui getCustomerGui() {
		return customerGui;
	}

	public void setCustomerGui(EnaCustomerGui customerGui) {
		this.customerGui = customerGui;
	}

	public void setXPos(int xp) {
			customerGui.xPos = xp;
	}
	public int getXPos() 
	{
		return customerGui.xPos;
}

	public void setXDest(int xd) 
	{
		customerGui.xDestination = xd;

	}
	public boolean restaurantOpen() {
		if(host != null && host.isActive() && host.isItOpen())
			{
				return true;
			}
		return false;
	}

	
}

