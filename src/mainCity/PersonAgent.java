package mainCity;
import agent.Agent;
import role.Role;

import java.util.*;
import java.util.concurrent.Semaphore;

import mainCity.gui.PersonGui;

public class PersonAgent extends Agent {
	private enum PersonState {normal, working, inBuilding}
	private enum PersonEvent {none, arrivedAtHome, arrivedAtWork, arrivedAtMarket, arrivedAtRestaurant, arrivedAtBank, timeToWork, needFood, gotHungry, gotFood, decidedRestaurant, needToBank, goHome}
	public enum CityLocation {home, restaurant_david, restaurant_ellen, restaurant_ena, restaurant_jefferson, restaurant_marcus, bank, market}
	
	private PersonGui gui;
	private double cash;
	private boolean traveling;
	private boolean onBreak;
	private PersonState state;
	private PersonEvent event;
	private CityLocation destination;
	private Semaphore isMoving = new Semaphore(0, true);
	private List<Role> roles;
	private PriorityQueue<Action> actions;
	private Action currentAction;
	
	public PersonAgent() {
		super();
		
		traveling = false;
		onBreak = false;
		state = PersonState.normal;//maybe 'inBuilding' if we start everyone in home
		event = PersonEvent.none;
		destination = CityLocation.home;
		roles = Collections.synchronizedList(new ArrayList<Role>());
		actions = new PriorityQueue<Action>(); 
		currentAction = null;
	}
	
	public void setGui(PersonGui g) {
		this.gui = g;
	}
	
	//----------Messages----------//
	//A message received from the GUI
	public void msgAtDestination() {
		traveling = false;
		isMoving.release();
	}

	//A message received from the transportation vehicle when arrived at destination
	public void msgArrivedAtDestination() {
		traveling = false;
		stateChanged();
	}

	//A message received from the system or GUI to tell person to go to work
	public void msgGoToWork() {
		actions.add(new Action(ActionType.work, 1));
		stateChanged();
	}

	//A message received from the system or GUI to tell person to get hungry
	public void msgGotHungry() {
		print("Person got hungry");

		actions.add(new Action(ActionType.restaurant, 5));
		//actions.add(new Action(ActionType.home, 10));

		stateChanged();
	}

	//A message received from the HomeAgent or GUI (possibly?) to go to the market
	public void msgGoToMarket() {
		actions.add(new Action(ActionType.market, 3));
		stateChanged();
	}

	//A message received to tell the person to go to the bank
	public void msgGoToBank(String purpose) {
		switch(purpose) {
		case "deposit":
			actions.add(new Action(ActionType.bankDeposit, 2));
			break;
		case "withdraw":
			actions.add(new Action(ActionType.bankWithdraw, 2));
			break;
		case "loan":
			actions.add(new Action(ActionType.bankLoan, 2));
			break;
		}

		stateChanged();
	}

	//A message received from the MarketCustomerAgent when they are done at the market
	public void msgFinishedAtBank() {
		if((currentAction.type == ActionType.bankDeposit || currentAction.type == ActionType.bankWithdraw) && currentAction.state == ActionState.inProgress) {
			currentAction.state = ActionState.done;
		}
		
		state = PersonState.normal;			
		stateChanged();
	}
	
	//A message received from the MarketCustomerAgent when they are done at the market
	public void msgFinishedAtMarket() {
		if(currentAction.type == ActionType.market && currentAction.state == ActionState.inProgress) {
			currentAction.state = ActionState.done;
		}
		
		state = PersonState.normal;
		//event = PersonEvent.gotFood;
		stateChanged();
	}

	//A message received from the RestaurauntAgent when they are done at the restaurant
	public void msgFinishedAtRestaurant() {
		if(currentAction.type == ActionType.restaurant && currentAction.state == ActionState.inProgress) {
			currentAction.state = ActionState.done;
		}
		
		state = PersonState.normal;
			
		if(onBreak) {
			actions.add(new Action(ActionType.work, 1));
			event = PersonEvent.timeToWork;
		}
		else {
			//do nothing maybe? go home
			event = PersonEvent.gotFood; // different event maybe? this one sends them home or back to work if they were on break?
		}
		
		stateChanged();
	}

	//----------Scheduler----------//
	public boolean pickAndExecuteAnAction() {
		//Handling active roles takes top priority
		if(!roles.isEmpty()) {
			for(Role r : roles) {
				if(r.isActive()) {
					return r.pickAndExecuteAnAction();
				}
			}
		}

		if(currentAction != null && currentAction.state == ActionState.done) {
			currentAction = null;
			return true;
		}
		
		if(!actions.isEmpty() && currentAction == null) {
			actions.peek().state = ActionState.inProgress;
			currentAction = actions.poll();
			handleAction(currentAction.type);
			return true;
		}

		if(state == PersonState.normal && !traveling) {
			if(event == PersonEvent.arrivedAtHome) {
				//set appropriate role to active

				if(currentAction != null && currentAction.type == ActionType.market || currentAction.type == ActionType.home) {
					currentAction.state = ActionState.done;
				}
				
				state = PersonState.inBuilding;
				return true;
			}

			if(event == PersonEvent.arrivedAtWork) {
				print("Arrived at work!");
				
				if(onBreak) {
					onBreak = false;
				}
				else {
					//set appropriate role to active && set that roles initial state
				}

				if(currentAction != null && currentAction.type == ActionType.work) {
					currentAction.state = ActionState.done;
				}
				
				state = PersonState.working;
				return true;
			}

			if(event == PersonEvent.arrivedAtMarket) {
				//set appropriate role active

				state = PersonState.inBuilding;
				return true;
			}

			if(event == PersonEvent.arrivedAtRestaurant) {
				//set appropriate role
				print("Arrived at the restaurant!");

				if(currentAction != null && currentAction.type == ActionType.restaurant) {
					currentAction.state = ActionState.done;
				}
				
				state = PersonState.inBuilding;
				return true;
			}

			if(event == PersonEvent.arrivedAtBank) {
				//set appropriate role and initial state for different actions

				if(currentAction != null && (currentAction.type == ActionType.bankWithdraw || currentAction.type == ActionType.bankDeposit || currentAction.type == ActionType.bankLoan)) {
					currentAction.state = ActionState.done;
				}
				
				state = PersonState.inBuilding;
				return true;
			}

			if(event == PersonEvent.timeToWork) {
				goToWork();
				return true;
			}

			if(event == PersonEvent.needFood) {
				goToMarket();
				return true;
			}

			if(event == PersonEvent.gotFood) {
				goHome();
				return true;
			}

			if(event == PersonEvent.gotHungry) {
				decideWhereToEat();
				return true;
			}

			if(event == PersonEvent.decidedRestaurant) {
				goToRestaurant();
				return true;
			}

			if(event == PersonEvent.needToBank) {
				goToBank();
				return true;
			}
		}

		if(actions.isEmpty() && state == PersonState.normal) {
			goHome();
			return true;
		}
		
		return false;
	}

	
	//----------Actions----------//
	public void roleInactive() {
		state = PersonState.normal;
		stateChanged();
	}
	
	
	public void stateChanged() {
		super.stateChanged();
	}
	
	
	/*
	public addRole(String occupation) {
		roles.add(new Role(this, occupation, false)); //pointer to person, occupation, isActive?
	}
	*/

	public double getCash() {
		return cash;
	}

	public void setCash(double d) {
		this.cash = d;
	}

	private void handleAction(ActionType action) {
		//need to check if appropriate role exists in the list, if it does not, make one, if it does--do nothing
		switch(action) {
			case work:
				event = PersonEvent.timeToWork;
				break;
			case restaurant:
				event = PersonEvent.gotHungry;//different event maybe to go strictly to restaurant
				break;
			case bankWithdraw:
			case bankDeposit:
			case bankLoan: 
				event = PersonEvent.needToBank;
				break;
			case home:
				event = PersonEvent.goHome;
				break;
			default:
		}

		stateChanged();
	}

	private void travelToLocation(CityLocation d) {
		traveling = true;
		this.destination = d;

		//Check for a way to travel: public transportation, car, or walking
		boolean temp = false;
		
		if(true) { //chose to walk
			gui.DoGoToLocation(d); //call gui
			waitForGui();
		}
		else if(temp) { //chose bus
			//DoGoToStop(); // walk to the closest bus stop or subway station?
			waitForGui();
			//bus.myDestination(d); //send message to transportation object of where they want to go
			//will receive an arrived at destination message when done
		}
		else if(temp) {//chose car
			//DoGoToCar(); //walk to car
			waitForGui();
			//car.msgGoToPlace(d); //some message to tell the car where to go, will receive an arrived at destination message when done
		}
	}

	private void goToWork() {
		//check occupation & set destination appropriately

		travelToLocation(destination);
		event = PersonEvent.arrivedAtWork;
		stateChanged();
	}

	private void goToMarket() {
		print("Going to the market");
		travelToLocation(CityLocation.market);
		event = PersonEvent.arrivedAtMarket;
		stateChanged();
	}

	private void goHome() {
		print("Going home");
		travelToLocation(CityLocation.home);
		event = PersonEvent.arrivedAtHome;
		stateChanged();
	}

	private void goToRestaurant() {
		print("Going to the restaurant");
		travelToLocation(destination); //should have been set to appropriate restaurant earlier;
		event = PersonEvent.arrivedAtRestaurant;
		stateChanged();
	}

	private void goToBank() {
		travelToLocation(CityLocation.bank);
		event = PersonEvent.arrivedAtBank;
		stateChanged();
	}

	private void decideWhereToEat() {
		print("Deciding where to eat..");
		//Decide between restaurant or home

		if(true) { //chose restaurant
			print("Chose to eat at a restaurant");
			destination = CityLocation.restaurant_david;//some sort of way to decide what restaurant to eat at
			event = PersonEvent.decidedRestaurant;
			return;
		}

		else if(true) {//chose home
			if(true) { //need food
				event = PersonEvent.needFood;
			}
			else {
				event = PersonEvent.gotFood; // already has food at home?
			}
		}

		stateChanged();
	}
	
	private void waitForGui() {
		try {
			isMoving.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
	
	
	//Lower the priority level, the more "important" it is (it'll get done faster)
	private enum ActionState {created, inProgress, done}
	private enum ActionType {work, restaurant, market, bankWithdraw, bankDeposit, bankLoan, home}
	class Action implements Comparable<Object> {
		ActionState state;
		ActionType type;
		int priority;
		
		Action(ActionType t, int p) {
			this.state = ActionState.created;
			this.type = t;
			this.priority = p;
		}
		
		public int compareTo(Object o) {
			if(o instanceof Action) {
				Action other = (Action) o;
				
				if(this.priority < other.priority) {
					return -1;
				}
				else if(this.priority > other.priority) {
					return 1;
				}	
			}			
			
			return 0;
		}
	}
}
