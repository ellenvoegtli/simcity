package mainCity;
import agent.Agent;
import role.Role;

import java.util.*;
import java.util.concurrent.Semaphore;

public class PersonAgent extends Agent {
	private enum PersonState {normal, working, inBuilding}
	private enum PersonEvent {none, arrivedAtHome, arrivedAtWork, arrivedAtMarket, arrivedAtRestaurant, arrivedAtBank, timeToWork, needFood, gotFood, gotHungry, decidedRestaurant, needToBank}
	private enum CityLocation {home, restaurant_david, restaurant_ellen, restaurant_ena, restaurant_jefferson, restaurant_marcus, bank, market}
	
	private int cash;
	private boolean traveling;
	private boolean onBreak;
	private PersonState state;
	private PersonEvent event;
	private CityLocation destination;
	private Semaphore isMoving = new Semaphore(0, true);
	private List<Role> roles;
	private PriorityQueue<Action> actions;
	
	PersonAgent() {
		super();
		
		traveling = false;
		onBreak = false;
		state = PersonState.normal;//maybe different if we start everyone in home
		event = PersonEvent.none;
		destination = CityLocation.home;
		roles = Collections.synchronizedList(new ArrayList<Role>());
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
		//add new action to list of actions of type restauraunt or home
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
		event = PersonEvent.needToBank;
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
				//handleAction(action.type);
				return true;
			}
		}

		if(state == PersonState.normal && !traveling) {
			if(event == PersonEvent.arrivedAtHome) {
				//set appropriate role to active

				//if actions.front().type == market or home, set to done
				state = PersonState.inBuilding;
				return true;
			}

			if(event == PersonEvent.arrivedAtWork) {
				//set appropriate role to active && set that roles initial state?
				//different if onBreak is active -- set to false if it is

				//if actions.front().type == work, set to done
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

				//if actions.front().type == restaurant, set to done
				state = PersonState.inBuilding;
				return true;
			}

			if(event == PersonEvent.arrivedAtBank) {
				//set appropriate role and initial state for different actions

				//if actions.front().type == bankWithdraw, bankDesposit, bankLoan - set to done
				state = PersonState.inBuilding;
				return true;
			}

			if(event == PersonEvent.timeToWork) {
				//goToWork();
				return true;
			}

			if(event == PersonEvent.needFood) {
				//goToMarket();
				return true;
			}

			if(event == PersonEvent.gotFood) {
				//goHome();
				return true;
			}

			if(event == PersonEvent.gotHungry) {
				//decideWhereToEat();
				return true;
			}

			if(event == PersonEvent.decidedRestaurant) {
				//goToRestaurant();
				return true;
			}

			if(event == PersonEvent.needToBank) {
				//goToBank();
				return true;
			}
		}

		if(actions.isEmpty()) { //If nothing else to do, go home
			//goHome();
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
		this.stateChanged();
	}
	
	private enum ActionState {created, inProgress, done}
	private enum ActionType {work, restaurant, market, bankWithdraw, bankDeposit, bankLoan, home}
	class Action {
		ActionState state;
		ActionType type;
		int priority;
	}
}
