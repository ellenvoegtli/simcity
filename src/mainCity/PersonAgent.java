package mainCity;
import agent.Agent;
import role.Role;

import java.util.*;
import java.util.concurrent.Semaphore;

public class PersonAgent extends Agent {
	private enum PersonState {normal, working, inBuilding}
	private enum PersonEvent {none, arrivedAtHome, arrivedAtWork, arrivedAtMarket, arrivedAtBank, timeToWork, needFood, gotFood, gotHungry, decidedRestaurant, needToBank}
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
	
	protected boolean pickAndExecuteAnAction() {
		
		
		return false;
	}

	public void stateChanged() {
		this.stateChanged();
	}
	
	
	private enum ActionState {created, inProgress, Done}
	private enum ActionType {work, restaurant, market, bankWithdraw, bankDeposit, bankLoan, home}
	class Action {
		ActionState state;
		ActionType type;
		int priority;
	}
}
