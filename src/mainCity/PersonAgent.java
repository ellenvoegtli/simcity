package mainCity;
import agent.Agent;
import role.*;
import role.marcusRestaurant.*;

import java.util.*;
import java.util.concurrent.Semaphore;

import mainCity.contactList.ContactList;
import mainCity.gui.PersonGui;
import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.enaRestaurant.EnaCustomerRole;
import mainCity.restaurants.enaRestaurant.EnaWaiterRole;
import mainCity.restaurants.jeffersonrestaurant.JeffersonCustomerRole;

/*
 * To Do for the personagent:
 * 	implement a way for roles to get added
 * 	handle the decision between market/restaurant since i use individual actions?
 * 	handle decision making for person (car/bus/walk) (which restaurant/market or restaurant)
 * 
 */

public class PersonAgent extends Agent {
	private enum PersonState {normal, working, inBuilding}
	private enum PersonEvent {none, arrivedAtHome, arrivedAtWork, arrivedAtMarket, arrivedAtRestaurant, arrivedAtBank, timeToWork, needMarket, gotHungry, gotFood, chooseRestaurant, decidedRestaurant, needToBank, goHome}
	public enum CityLocation {home, restaurant_david, restaurant_ellen, restaurant_ena, restaurant_jefferson, restaurant_marcus, bank, market}
	
	private PersonGui gui;
	private String name;
	private double cash;
	private boolean traveling;
	private boolean onBreak;
	private String occupation;
	private PersonState state;
	private PersonEvent event;
	private CityLocation destination;
	private Semaphore isMoving = new Semaphore(0, true);
	private HashMap<ActionType, Role> roles;
	private PriorityQueue<Action> actions;
	private Action currentAction;
	
	public PersonAgent(String n) {
		super();
		
		name = n;
		traveling = false;
		onBreak = false;
		occupation = "";
		cash = 100.0;
		state = PersonState.normal;//maybe 'inBuilding' if we start everyone in home
		event = PersonEvent.none;
		destination = CityLocation.home;
		roles = new HashMap<ActionType, Role>();
		actions = new PriorityQueue<Action>(); 
		currentAction = null;
	}
	
	public void setGui(PersonGui g) {
		this.gui = g;
	}
	
	public void updateOccupation(String o) {
		this.occupation = o;
	}
	
	//----------Messages----------//
	//From a timer to tell the person to do a checkup
	public void msgPerformCheck() {
		actions.add(new Action(ActionType.performCheck, 1));
		stateChanged();
	}
	
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

	//A message received from the system or GUI to tell person to get hungry - they will choose between restaurant and home
	public void msgGotHungry() {
		print("Person got hungry");

		actions.add(new Action(ActionType.hungry, 5));
		//actions.add(new Action(ActionType.home, 10));

		stateChanged();
	}
	
	//A message received from the HomeAgent or GUI (possibly?) to go to a restaurant
	public void msgGoToRestaurant() {
		print("Person will go to restaurant");
		actions.add(new Action(ActionType.restaurant, 4));
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
			for(Map.Entry<ActionType, Role> r : roles.entrySet()) {
				if(r.getValue().isActive()) {
					return r.getValue().pickAndExecuteAnAction();
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

		if(currentAction != null && currentAction.type == ActionType.performCheck) {
			checkSelf();
			return true;
		}
		
		if(currentAction != null && state == PersonState.normal && !traveling) {
			if(event == PersonEvent.arrivedAtHome) {
				print("Arrived at home!");

				handleRole(currentAction.type);
				roles.get(currentAction.type).setActive();

				if(currentAction != null && (currentAction.type == ActionType.market || currentAction.type == ActionType.home)) {
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
					handleRole(currentAction.type);
					roles.get(currentAction.type).setActive();
				}

				if(currentAction != null && currentAction.type == ActionType.work) {
					currentAction.state = ActionState.done;
				}
				
				state = PersonState.working;
				return true;
			}

			if(event == PersonEvent.arrivedAtMarket) {
				print("Arrived at market!");
				handleRole(currentAction.type);
				roles.get(currentAction.type).setActive();
	
				state = PersonState.inBuilding;
				return true;
			}

			if(event == PersonEvent.arrivedAtRestaurant) {
				print("Arrived at " + destination);	
				handleRole(currentAction.type);
				Role customer = roles.get(currentAction.type);
				
				if(customer instanceof MarcusCustomerRole) {
					if(!((MarcusCustomerRole) customer).getGui().goInside()) {
						//If restaurant is closed go try another --should cycle a lot now since only 1 restaurant;
						print("Restaurant closed...trying another");
						chooseRestaurant();
						return true;
					}
				}
				else if(customer instanceof EllenCustomerRole) {
					((EllenCustomerRole) customer).gotHungry();
				}
				else if(customer instanceof EnaCustomerRole) {
					((EnaCustomerRole) customer).getGui().setHungry();
				}
				else if(customer instanceof JeffersonCustomerRole){
					((JeffersonCustomerRole) customer).gotHungry();
					((JeffersonCustomerRole) customer).getGui().setHungry();
				}
				
				customer.setActive();

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

			if(event == PersonEvent.needMarket) {
				goToMarket();
				return true;
			}

			if(event == PersonEvent.gotFood || event == PersonEvent.goHome) {
				goHome();
				return true;
			}

			if(event == PersonEvent.gotHungry) {
				decideWhereToEat();
				return true;
			}

			if(event == PersonEvent.chooseRestaurant) {
				chooseRestaurant();
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

		if(actions.isEmpty() && state == PersonState.normal && !traveling) {
			print("My action list is empty. Going home");
			actions.add(new Action(ActionType.home, 10));
			return true;
		}
		
		return false;
	}

	
	//----------Actions----------//
	private void checkSelf() {
		//FOR AI - need to check self to do things? bank, eat, etc.
	}
	
	private void handleRole(ActionType action) {
		//this way works well except for the banking part
		if(!roles.containsKey(action)) {
			switch(action) {
				case work:
					switch(occupation) {
						//-----Marcus Restaurant Roles---//
						case "marcusCook":
							MarcusCookRole mco = new MarcusCookRole(this, name);
							ContactList.getInstance().getMarcusRestaurant().handleRole(mco);
							roles.put(action, mco);
							break;
						case "marcusCashier":
							MarcusCashierRole mca = new MarcusCashierRole(this, name);
							ContactList.getInstance().getMarcusRestaurant().handleRole(mca);
							roles.put(action, mca);
							break;
						case "marcusHost":
							MarcusHostRole mh = new MarcusHostRole(this, name);
							ContactList.getInstance().getMarcusRestaurant().handleRole(mh);
							roles.put(action, mh);
							break;
						case "marcusWaiter":
							MarcusNormalWaiterRole mw = new MarcusNormalWaiterRole(this, name);
							ContactList.getInstance().getMarcusRestaurant().handleRole(mw);
							roles.put(action, mw);
							break;
						case "marcusShareWaiter":
							MarcusSharedWaiterRole ms = new MarcusSharedWaiterRole(this, name);
							ContactList.getInstance().getMarcusRestaurant().handleRole(ms);
							roles.put(action, ms);
							break;
							
						//-----Ena Restaurant Roles---//
						case "enaWaiter":
							EnaWaiterRole en = new EnaWaiterRole(this, name);
							ContactList.getInstance().getEnaRestaurant().handleRoleGui(en);
							roles.put(action, en);
							break;
							
						//-----Ellen Restaurant Roles---//
						case "ellenWaiter":
							EllenNormalWaiterRole el = new EllenNormalWaiterRole(this, name);
							ContactList.getInstance().getEllenRestaurant().handleRoleGui(el);
							roles.put(action, el);
							break;
						default:
							break;
					}
					break;
				case restaurant:
					switch(destination) {
						case restaurant_marcus:
							MarcusCustomerRole m = new MarcusCustomerRole(this, name);
							ContactList.getInstance().getMarcusRestaurant().handleRole(m);
							roles.put(action, m);
							break;
						case restaurant_ellen:
							EllenCustomerRole e = new EllenCustomerRole(this, name);
							ContactList.getInstance().getEllenRestaurant().handleRoleGui(e);
							roles.put(action, e);
							break;
						case restaurant_ena:
							EnaCustomerRole en = new EnaCustomerRole(this, name);
							ContactList.getInstance().getEnaRestaurant().handleRoleGui(en);
							roles.put(action, en);
							break;
						case restaurant_jefferson:
							JeffersonCustomerRole jc = new JeffersonCustomerRole(this, name);
							ContactList.getInstance().getJeffersonRestaurant().handleRoleGui(jc);
							roles.put(action,jc);
							break;
						default:
							break;
					}
					break;
				default:
					break;
			}
		}
	}
	
	private void handleAction(ActionType action) {
		switch(action) {
			case work:
				event = PersonEvent.timeToWork;
				break;
			case hungry:
				event = PersonEvent.gotHungry;
				break;
			case market:
				event = PersonEvent.needMarket;
				break;
			case restaurant:
				event = PersonEvent.chooseRestaurant;
				break;
			case bankWithdraw:
			case bankDeposit:
			case bankLoan: 
				event = PersonEvent.needToBank;
				break;
			default://If can't find anything or if home. go home
				event = PersonEvent.goHome;
				break;
		}
		
		stateChanged();
	}

	private void travelToLocation(CityLocation d) {
		traveling = true;
		this.destination = d;

		//Check for a way to travel: public transportation, car, or walking
		boolean temp = true;
		
		if(temp) { //chose to walk
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

	private void chooseRestaurant() {
		//choose which restaurant here

		destination = CityLocation.restaurant_marcus;
		event = PersonEvent.decidedRestaurant;
		handleRole(currentAction.type);
	}
	
	private void decideWhereToEat() {
		print("Deciding where to eat..");
		//Decide between restaurant or home

		boolean temp = true;
		
		if(temp) { //chose restaurant
			print("Chose to eat at a restaurant");
			currentAction.type = ActionType.restaurant;
			handleAction(currentAction.type);
			return;
		}

		else if(temp) {//chose home
			if(temp) { //need food
				currentAction.type = ActionType.market;
				handleAction(currentAction.type);
			}
			else {
				currentAction.type = ActionType.home;
				handleAction(currentAction.type);
			}
		}

		stateChanged();
	}
	
	private void goToWork() {
		//check occupation & set destination appropriately

		if(occupation.contains("marcus")) {
			destination = CityLocation.restaurant_marcus;
		}
		
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
		travelToLocation(destination);
		event = PersonEvent.arrivedAtRestaurant;
		stateChanged();
	}

	private void goToBank() {
		travelToLocation(CityLocation.bank);
		event = PersonEvent.arrivedAtBank;
		stateChanged();
	}

	//---Other Actions functions---//
	public void addRole(ActionType type, Role role) {
		roles.put(type, role);
	}
	
	public void roleInactive() {
		state = PersonState.normal;
		stateChanged();
		//possibly have the msgFinished...messages in here instead
	}
	
	public void stateChanged() {
		super.stateChanged();
	}

	public double getCash() {
		return cash;
	}

	public void setCash(double d) {
		this.cash = d;
	}
	
	private void waitForGui() {
		try {
			isMoving.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
	
	public String toString() {
		return name;
	}
	
	//Lower the priority level, the more "important" it is (it'll get done faster)
	private enum ActionState {created, inProgress, done}
	public enum ActionType {work, performCheck, hungry, restaurant, market, bankWithdraw, bankDeposit, bankLoan, home}
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
