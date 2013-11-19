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
	private int cash;
	private boolean traveling;
	private boolean onBreak;
	private PersonState state;
	private PersonEvent event;
	private CityLocation destination;
	private Semaphore isMoving = new Semaphore(0, true);
	private List<Role> roles;
	private PriorityQueue<Action> actions;
	
	public PersonAgent() {
		super();
		
		traveling = false;
		onBreak = false;
		state = PersonState.normal;//maybe 'inBuilding' if we start everyone in home
		event = PersonEvent.none;
		destination = CityLocation.home;
		roles = Collections.synchronizedList(new ArrayList<Role>());
		actions = new PriorityQueue<Action>(); 
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
		//add new action to list of actions of type work
		stateChanged();
	}

	//A message received from the system or GUI to tell person to get hungry
	public void msgGotHungry() {
		print("Person got hungry");
		//add new action to list of actions of type restaurant or home
		event = PersonEvent.gotHungry;
		stateChanged();
	}

	//A message received from the HomeAgent or GUI (possibly?) to go to the market
	public void msgGoToMarket() {
		//add new action to list of actions of type market
		stateChanged();
	}

	//A message received to tell the person to go to the bank
	public void msgGoToBank() {
		//add new action to list of actions of type bank (with desired action)
		//event = PersonEvent.needToBank;
		stateChanged();
	}

	//A message received from the MarketCustomerAgent when they are done at the market
	public void msgFinishedAtBank() {
		if(actions.peek().type == ActionType.bankDeposit || actions.peek().type == ActionType.bankWithdraw) {
			actions.peek().state = ActionState.done;
		}
		
		state = PersonState.normal;			
		stateChanged();
	}
	
	//A message received from the MarketCustomerAgent when they are done at the market
	public void msgFinishedAtMarket() {
		if(actions.peek().type == ActionType.market) {
			actions.peek().state = ActionState.done;
		}
		
		state = PersonState.normal;
		//event = PersonEvent.gotFood;
		stateChanged();
	}

	//A message received from the RestaurauntAgent when they are done at the restaurant
	public void msgFinishedAtRestaurant() {
		if(actions.peek().type == ActionType.restaurant) {
			actions.peek().state = ActionState.done;
		}
		
		state = PersonState.normal;
			
		if(onBreak)
			//add go to work
			event = PersonEvent.timeToWork;
		else
			//do nothing maybe? go home
			event = PersonEvent.gotFood; // different event maybe? this one sends them home or back to work if they were on break?

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

		if(!actions.isEmpty()) {
			if(actions.peek().state == ActionState.done) {
				actions.poll();
				return true;
			}

			if(actions.peek().state == ActionState.created) {
				actions.peek().state = ActionState.inProgress;
				handleAction(actions.peek().type);
				return true;
			}
		}

		if(state == PersonState.normal && !traveling) {
			if(event == PersonEvent.arrivedAtHome) {
				//set appropriate role to active

				if(actions.peek().type == ActionType.market || actions.peek().type == ActionType.home) {
					actions.peek().state = ActionState.done;
				}
				
				state = PersonState.inBuilding;
				return true;
			}

			if(event == PersonEvent.arrivedAtWork) {
				if(onBreak) {
					onBreak = false;
				}
				else {
					//set appropriate role to active && set that roles initial state
				}

				if(actions.peek().type == ActionType.work) {
					actions.peek().state = ActionState.done;
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
				print("arrived at the restaurant!");

				if(actions.peek().type == ActionType.restaurant) {
					actions.peek().state = ActionState.done;
				}
				
				state = PersonState.inBuilding;
				return true;
			}

			if(event == PersonEvent.arrivedAtBank) {
				//set appropriate role and initial state for different actions

				if(actions.peek().type == ActionType.bankWithdraw || actions.peek().type == ActionType.bankDeposit || actions.peek().type == ActionType.bankLoan) {
					actions.peek().state = ActionState.done;
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

		if(actions.isEmpty()) {
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
	
	/*
	public void stateChanged() {
		this.stateChanged();
	}
	*/
	
	/*
	public addRole(String occupation) {
		roles.add(new Role(this, occupation, false)); //pointer to person, occupation, isActive?
	}
	*/

	public int getCash() {
		return cash;
	}

	public void setCash(int c) {
		this.cash = c;
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
	
	private enum ActionState {created, inProgress, done}
	private enum ActionType {work, restaurant, market, bankWithdraw, bankDeposit, bankLoan, home}
	class Action {
		ActionState state;
		ActionType type;
		int priority;
	}
}
