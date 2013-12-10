package mainCity;
import agent.Agent;
import role.*;
import role.bank.BankCustomerRole;
import role.bank.BankManagerRole;
import role.bank.BankRobberRole;
import role.bank.BankTellerRole;
import role.bank.BankerRole;
import role.davidRestaurant.*;
import role.ellenRestaurant.*;
import role.jeffersonRestaurant.*;
import role.marcusRestaurant.*;
import role.market.*;
import housing.*;
import housing.LandlordRole.landlordActive;
import housing.Interfaces.Occupant;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

import mainCity.bank.*;
import mainCity.bank.interfaces.BankCustomer;
import mainCity.contactList.ContactList;
import mainCity.gui.*;
import mainCity.gui.AnimationPanel.ApartmentObject;
import mainCity.gui.AnimationPanel.HomeObject;
import mainCity.gui.trace.*;
import mainCity.interfaces.ManagerRole;
import mainCity.interfaces.PersonGuiInterface;
import mainCity.restaurants.enaRestaurant.*;
import mainCity.test.*;
import transportation.BusAgent;

public class PersonAgent extends Agent {	
	public enum PersonState {normal, working, inBuilding, waiting, boardingBus, inCar, walkingFromBus, walkingFromCar}
	public enum PersonEvent {none, arrivedAtHome, arrivedAtWork, arrivedAtMarket, arrivedAtMarket2, arrivedAtRestaurant, arrivedAtBank, timeToWork, needMarket, needMarket2, gotHungry, gotFood, chooseRestaurant, decidedRestaurant, needToBank, maintainWork,goHome, arrivedRenterApartment}
	public enum CityLocation {home, restaurant_david, restaurant_ellen, restaurant_ena, restaurant_jefferson, restaurant_marcus, bank, bank2, market, market2, renterHome}

	private PersonGuiInterface gui;
	private String name;
	private double cash;
	private double accountnumber;
	private boolean traveling;
	private boolean hasCar; 
	private boolean forceWalk; 
	private boolean chooseTransportation; 
	private BusAgent currentBus; 
	private Building homePlace;
	public Building renterHome;
	private int time;
	private int day;
	private Job job;
	private PersonState state;
	private PersonEvent event;
	private CityLocation destination;
	private Semaphore isMoving = new Semaphore(0, true);
	private Map<ActionType, Role> roles;
	private PriorityBlockingQueue<Action> actions;
	private Action currentAction;
	private boolean alive;
	public EventLog log = new EventLog(); 
	private String restaurantHack;
	
	public PersonAgent(String n) {
		super();
		
		name = n;
		traveling = false;
		job = new Job();
		cash = 100.0;
		state = PersonState.normal;//maybe 'inBuilding' if we start everyone in home
		event = PersonEvent.none;
		destination = CityLocation.home;
		roles = Collections.synchronizedMap(new HashMap<ActionType, Role>());
		actions = new PriorityBlockingQueue<Action>();
		currentAction = null;
		alive = true;
		chooseTransportation = false;
	}
	
	public void setGui(PersonGuiInterface g) {
		this.gui = g;
	}
	
	public PersonGuiInterface getGui() {
		return gui;
	}
	
	public CityLocation getDestination() { 
		return destination;
	}
	
	public void setCar(boolean b) { 
		hasCar = b;
	}
	
	public void setWalk(boolean b) { 
		forceWalk = b;
	}
	
	public void setTransportation(boolean b) {
		chooseTransportation = true;
	}
	
	public boolean isHungryForRestaurant(){
		synchronized(actions) {
			if(actions.contains(ActionType.restaurant) || (currentAction != null && currentAction.type == ActionType.restaurant))
				return true;
			return false;
		}
	}
	public boolean isHungryForHome(){
		synchronized(actions) {
			if(actions.contains(ActionType.homeAndEat) || (currentAction != null && currentAction.type == ActionType.homeAndEat))
				return true;
			return false;
		}
	}
	public boolean isGoingOrAtWork(){
		synchronized(actions) {
			if(actions.contains(ActionType.work) || (currentAction != null && currentAction.type == ActionType.work))
				return true;
			return false;
		}
	}
	
	
	public void updateOccupation(String o, int b, int e) {
		job.occupation = o;
		job.shiftBegin = b;
		job.shiftEnd = e;
	}
	
	//----------Messages----------//	
	//A message received from the GUI
	public void msgAtDestination() {
		traveling = false;
		state = PersonState.normal;
		isMoving.release();
	}

	//A message received from the transportation vehicle when arrived at destination
	public void msgArrivedAtDestination() {
		//traveling = false;
		log.add(new LoggedEvent("msgArrivedAtDestination received")); 
		gui.DoGoOutside();
		state = PersonState.walkingFromBus;
		stateChanged();
	}
	
	//A message received from car when arrived at destination 
	public void msgArrivedAtDestinationInCar() { 
		log.add(new LoggedEvent("msgArrivedAtDestinationInCar received")); 
		gui.DoGoOutside(); 
		gui.getOutOfCar();
		state = PersonState.walkingFromCar; 
		stateChanged();
	}
	
	//A message for the landlord	
	public void msgNeedToFix(Building renterPlace) {
		this.renterHome = renterPlace;
		synchronized(actions) {
			actions.add(new Action(ActionType.maintenance, 1));
			stateChanged();
		}
	}
	

	public void msgBusHasArrived() {
		//print("msgBusHasArrived received");
		log.add(new LoggedEvent("msgBusHasArrived received"));
		state = PersonState.boardingBus;
		stateChanged();
	}

	//A message received from the system or GUI to tell person to go to work
	public void msgGoToWork() {
		synchronized(actions) {
			actions.add(new Action(ActionType.work, 1));
			stateChanged();
		}
	}

	//A message received from the system or GUI to tell person to get hungry - //they will choose between restaurant and home
	public void msgGotHungryForHome() {
		if(!actions.contains(ActionType.homeAndEat)) {
			synchronized(actions) {
				actions.add(new Action(ActionType.homeAndEat, 3));
				stateChanged();
			}
		}
	}
	
	//A message received from the HomeAgent or GUI to go to a restaurant
	public void msgGoToRestaurant() {
		if(!actionExists(ActionType.restaurant)) {
			synchronized(actions) {
				actions.add(new Action(ActionType.restaurant, 4));
				stateChanged();
			}
		}
	}
	
	//A message received from the GUI to go to a specific restaurant (hack)
	public void msgGoToRestaurant(String name) {
		if (name.toLowerCase().contains("ellen")){
			restaurantHack = "ellenRestaurant";
		}
		else if (name.toLowerCase().contains("ena")){
			restaurantHack = "enaRestaurant";
		}
		else if (name.toLowerCase().contains("marcus")){
			restaurantHack = "marcusRestaurant";
		}
		else if (name.toLowerCase().contains("jefferson")){
			restaurantHack = "jeffersonRestaurant";
		}
		else if (name.toLowerCase().contains("david")){
			restaurantHack = "davidRestaurant";
		}
		
		if(!actionExists(ActionType.restaurant)) {
			synchronized(actions) {
				actions.add(new Action(ActionType.restaurant, 4));
				stateChanged();
			}
		}
	}

	//A message received from the HomeAgent or GUI (possibly?) to go to the market
	public void msgGoToMarket() {
		synchronized(actions) {
			actions.add(new Action(ActionType.market, 3));
			stateChanged();
		}
	}
	
	//A message received from the to send personagent home
	public void msgGoHome() {
		System.out.println(name + ": Received msgGoHome");
		synchronized(actions) {
			actions.add(new Action(ActionType.home, 3));
			stateChanged();
		}
	}

	//A message received to tell the person to go to the bank
	public void msgGoToBank(String purpose) {
		boolean decide = (Math.random() < 0.5);

		synchronized(actions) {
			switch(purpose) {
				case "deposit":
					if(decide)
						actions.add(new Action(ActionType.bankDeposit, 2));
					else
						actions.add(new Action(ActionType.bankDeposit2, 2));
					break;
				case "withdraw":
					if(decide)
						actions.add(new Action(ActionType.bankWithdraw, 2));
					else
						actions.add(new Action(ActionType.bankWithdraw2, 2));
					break;
				case "loan":
					if(decide)
						actions.add(new Action(ActionType.bankLoan, 2));
					else
						actions.add(new Action(ActionType.bankLoan2, 2));
					break;
				case "rob":
					if(decide)
						actions.add(new Action(ActionType.bankRob, 5));
					else
						actions.add(new Action(ActionType.bankRob2, 5));
			}
	
			stateChanged();

		}
	}
	
	//A message received from the transportation object to remove the person
	public void msgHitByVehicle() {
		if(isMoving.availablePermits() == 0)
			isMoving.release();
		alive = false;
		stateChanged();
	}

	//----------Scheduler----------//
	public boolean pickAndExecuteAnAction() {
		//Handling active roles takes top priority
		synchronized(roles) {
			if(!roles.isEmpty()) {
				for(Map.Entry<ActionType, Role> r : roles.entrySet()) {
					if(r.getValue().isActive()) {
						return r.getValue().pickAndExecuteAnAction();
					}
				}
			}
		}

		if(!alive) {
			respawnPerson();
			return true;
		}
		
		if(currentAction != null && currentAction.state == ActionState.done) {
			currentAction = null;
			return true;
		}
		
		if(!actions.isEmpty() && currentAction == null ) {
			synchronized(actions) {
				actions.peek().state = ActionState.inProgress;
				currentAction = actions.poll();
				handleAction(currentAction.type);
				return true;
			}
		}
		
		if(currentAction != null && state == PersonState.normal && !traveling) {
			if(event == PersonEvent.arrivedAtHome) {
				synchronized(actions) {
					if(!actions.isEmpty()) { //If there's other stuff to do, don't go inside yet
						currentAction.state = ActionState.done;
						return true;
					}
				}
				
				output("Arrived at home!");
				handleRole(currentAction.type);
				
				synchronized(roles) {
					if(currentAction != null && (currentAction.type == ActionType.homeAndEat)){
						if (roles.get(ActionType.home) != null) {
							((Occupant) roles.get(ActionType.home)).gotHungry();
							roles.get(ActionType.home).setActive();
						}
	
						else {
							((Occupant) roles.get(ActionType.homeAndEat)).gotHungry();
							roles.get(ActionType.homeAndEat).setActive();
						}
					}
					
					else
						roles.get(currentAction.type).setActive();
				}
				
				enterBuilding();
				return true;
			}

			if(event == PersonEvent.arrivedRenterApartment)
			{
				output("Arrived at renter home!");
				handleRole(currentAction.type);
				synchronized(roles) {
					output("the type of action is "  +currentAction.type);
					roles.get(currentAction.type).setActive();
				}
				enterBuilding();
				return true;
				
			}
			if(event == PersonEvent.arrivedAtWork) {
				output("Arrived at work!");
				handleRole(currentAction.type);

				synchronized(roles) {
					roles.get(currentAction.type).setActive();
				}

				enterBuilding();
				return true;
			}

			if(event == PersonEvent.arrivedAtMarket) {
				handleRole(currentAction.type);
				
				synchronized(roles) {
					Role customer = roles.get(currentAction.type);
					
					if (roles.containsKey(ActionType.home) || roles.containsKey(ActionType.homeAndEat)){
						OccupantRole occupant;
						if (roles.containsKey(ActionType.home)){
							//check home agent to get a list of what they need
							occupant = (OccupantRole) (roles.get(ActionType.home));
						}
						else {
							occupant = (OccupantRole) (roles.get(ActionType.homeAndEat));
						}
						
						if (event == PersonEvent.arrivedAtMarket && !((MarketCustomerRole) customer).getGui().goInside(occupant.getFood())) {
							currentAction.state = ActionState.done;
							return true;
						}
					}
					else {
						currentAction.state = ActionState.done;
						return true;
					}
										
					customer.setActive();
				}
				
				enterBuilding();
				return true;
			}
			
			if(event == PersonEvent.arrivedAtRestaurant) {
				output("Arrived at " + destination);	
				handleRole(currentAction.type);
				
				synchronized(roles) {
					Role customer = roles.get(currentAction.type);
					
					if((customer instanceof MarcusCustomerRole && !((MarcusCustomerRole) customer).getGui().goInside()) ||
						(customer instanceof EllenCustomerRole && !((EllenCustomerRole) customer).getGui().goInside()) ||
						(customer instanceof EnaCustomerRole && !((EnaCustomerRole) customer).getGui().goInside()) ||
						(customer instanceof JeffersonCustomerRole && !((JeffersonCustomerRole) customer).getGui().goInside()) ||
						(customer instanceof DavidCustomerRole && !((DavidCustomerRole) customer).getGui().goInside())) {
						
						currentAction.state = ActionState.done;
						//chooseRestaurant();
						//actions.add(new Action(ActionType.hungry, 7));
						return true;
						}
					
				customer.setActive();
				}

				enterBuilding();
				return true;
			}
		
			if(event == PersonEvent.arrivedAtBank) {
				output("Arrived at bank");
				handleRole(currentAction.type);

				synchronized(roles) {
					Role customer = roles.get(currentAction.type);
					
					if ((customer instanceof BankRobberRole && !((BankRobberRole) customer).getGui().goInside()) || (customer instanceof BankCustomer && !((BankCustomer) customer).getGui().goInside())){
						currentAction.state = ActionState.done;
						return true;
					}

					switch(currentAction.type) {
						case bankWithdraw:
							((BankCustomer) customer).msgWantToWithdraw();
							break;
						case bankDeposit:
							((BankCustomer) customer).msgWantToDeposit();
							break;
						case bankLoan:
							((BankCustomer) customer).msgNeedLoan();
							break;
						case bankRob:
							((BankRobberRole) customer).msgWantToRobBank();
							break;
						default:
							break;
					}
					
					customer.setActive();
				}
				
				enterBuilding();
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
			
			if(currentAction.type == ActionType.maintenance) {
				
				goToRenters();
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
		
		if(state == PersonState.boardingBus) {
			boardBus();
			return true;
		}
		
		if(state == PersonState.walkingFromBus) {
			currentBus.Passengers.remove(this);
			currentBus = null; 
			travelToLocation(destination);
			return true;
		}
		
		if(state == PersonState.walkingFromCar){ 
			travelToLocation(destination); 
			return true;
		}
		
		synchronized(actions) {
			if(actions.isEmpty() && state == PersonState.normal && !traveling) {
				actions.add(new Action(ActionType.home, 10));
				return true;
			}
		}
		
		return false;
	}
	
	//----------Actions----------//
	private void enterBuilding() {
		if(currentAction != null) {
			currentAction.state = ActionState.done;
		}
		
		gui.DoGoInside();
		state = PersonState.inBuilding;

		if(currentAction.type == ActionType.work) {
			state = PersonState.working;
		}
	}
	
	private void checkSelf() {
		if((day != 0 || day != 6) && time == job.shiftBegin && state != PersonState.working && currentAction.type != ActionType.work && !actionExists(ActionType.work) && !job.occupation.equals("rich")) {
			synchronized(actions) {
				actions.add(new Action(ActionType.work, 1));
				stateChanged();
			}
		}
		
		if(job.occupation.equals("rich") && event == PersonEvent.maintainWork) {
			synchronized(actions) {
				actions.add(new Action(ActionType.maintenance , 1));
				stateChanged();
			}
		}
		
		if(time == job.shiftEnd && state == PersonState.working) {
			synchronized(roles) {
				for(Map.Entry<ActionType, Role> r : roles.entrySet()) {
					if(r.getValue() instanceof ManagerRole && r.getValue().isActive() ) {
						((ManagerRole) r.getValue()).msgEndShift();
					}
					
				}
			}
		}
		
		synchronized(actions) {
			if(cash < 50 && (!actionExists(ActionType.bankWithdraw) || !actionExists(ActionType.bankWithdraw2))){
				if((Math.random() < 0.5))
					actions.add(new Action(ActionType.bankWithdraw, 3));
				else
					actions.add(new Action(ActionType.bankWithdraw2, 3));
				stateChanged();
			}
			if(cash > 300 && (!actionExists(ActionType.bankDeposit) || !actionExists(ActionType.bankDeposit2))){
				if((Math.random() < 0.5))
					actions.add(new Action(ActionType.bankDeposit, 3));
				else
					actions.add(new Action(ActionType.bankDeposit2, 3));
				stateChanged();
			}
		}
	}
	
	private void handleRole(ActionType action) {
		synchronized(roles) {
			if(!roles.containsKey(action)) {
				switch(action) {
					case work:
						switch(job.occupation) {
							case "banker":
								BankerRole bk = new BankerRole(this, name);
								ContactList.getInstance().getBank().handleRole(bk);
								roles.put(action, bk);
								break;
							case "bankTeller":	
								BankTellerRole bt = new BankTellerRole(this, name);
								ContactList.getInstance().getBank().handleRole(bt);
								roles.put(action, bt);
								break;
							case "bankManager":
								BankManagerRole bm = new BankManagerRole(this, name);
								ContactList.getInstance().getBank().handleRole(bm);
								roles.put(action, bm);
								break;
							case "bank2er":
								BankerRole bk2 = new BankerRole(this, name);
								ContactList.getInstance().getBank2().handleRole(bk2);
								roles.put(action, bk2);
								break;
							case "bank2Teller":	
								BankTellerRole bt2 = new BankTellerRole(this, name);
								ContactList.getInstance().getBank2().handleRole(bt2);
								roles.put(action, bt2);
								break;
							case "bank2Manager":
								BankManagerRole bm2 = new BankManagerRole(this, name);
								ContactList.getInstance().getBank2().handleRole(bm2);
								roles.put(action, bm2);
								break;
							//-----Jefferson Restaurant Roles---//
							case "jeffersonCook":
								JeffersonCookRole jc = new JeffersonCookRole(this, name);
								ContactList.getInstance().getJeffersonRestaurant().handleRole(jc);
								roles.put(action,jc);
								break;
							case "jeffersonCashier":
								JeffersonCashierRole jr = new JeffersonCashierRole(this, name);
								ContactList.getInstance().getJeffersonRestaurant().handleRole(jr);
								roles.put(action, jr);
								break;
							case "jeffersonWaiter":
								JeffersonWaiterRole jw = new JeffersonNormalWaiterRole(this, name);
								ContactList.getInstance().getJeffersonRestaurant().handleRole(jw);
								roles.put(action, jw);
								break;
							case "jeffersonSharedWaiter":
								JeffersonWaiterRole jsw = new JeffersonSharedDataWaiterRole(this, name);
								ContactList.getInstance().getJeffersonRestaurant().handleRole(jsw);
								roles.put(action, jsw);
								break;
							case "jeffersonHost":
								JeffersonHostRole jh = new JeffersonHostRole(this, name);
								ContactList.getInstance().getJeffersonRestaurant().handleRole(jh);
								roles.put(action, jh);
								break;
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
								EnaNormalWaiterRole en = new EnaNormalWaiterRole(this, name);
								ContactList.getInstance().getEnaRestaurant().handleRole(en);
								roles.put(action, en);
								break;
							case "enaSharedWaiter":
								EnaSharedWaiterRole esw = new EnaSharedWaiterRole(this, name);
								ContactList.getInstance().getEnaRestaurant().handleRole(esw);
								roles.put(action, esw);
							case "enaCook":
								EnaCookRole eco = new EnaCookRole(this, name);
								ContactList.getInstance().getEnaRestaurant().handleRole(eco);
								roles.put(action, eco);
								break;
							case "enaCashier":
								EnaCashierRole eca = new EnaCashierRole(this, name);
								ContactList.getInstance().getEnaRestaurant().handleRole(eca);
								roles.put(action, eca);
								break;
							case "enaHost":
								EnaHostRole eh = new EnaHostRole(this, name);
								ContactList.getInstance().getEnaRestaurant().handleRole(eh);
								roles.put(action, eh);
								break;
								
							//-----Ellen Restaurant Roles---//
							case "ellenWaiter":
								EllenNormalWaiterRole el = new EllenNormalWaiterRole(this, name);
								ContactList.getInstance().getEllenRestaurant().handleRole(el);
								roles.put(action, el);
								break;
							case "ellenShareWaiter":
								EllenSharedDataWaiterRole esdw = new EllenSharedDataWaiterRole(this, name);
								ContactList.getInstance().getEllenRestaurant().handleRole(esdw);
								roles.put(action, esdw);
								break;
							case "ellenCook":
								EllenCookRole elco = new EllenCookRole(this, name);
								ContactList.getInstance().getEllenRestaurant().handleRole(elco);
								roles.put(action, elco);
								break;
							case "ellenCashier":
								EllenCashierRole elca = new EllenCashierRole(this, name);
								ContactList.getInstance().getEllenRestaurant().handleRole(elca);
								roles.put(action, elca);
								break;
							case "ellenHost":
								EllenHostRole elh = new EllenHostRole(this, name);
								ContactList.getInstance().getEllenRestaurant().handleRole(elh);
								roles.put(action, elh);
								break;
							
							//-----David Restaurant Roles---//
							case "davidWaiter":
								DavidWaiterRole dw = new DavidNormalWaiterRole(name, this); 
								ContactList.getInstance().getDavidRestaurant().handleRole(dw); 
								roles.put(action, dw); 
								break; 
								
							case "davidSharedWaiter":
								DavidWaiterRole dsw = new DavidSharedWaiterRole(name, this); 
								ContactList.getInstance().getDavidRestaurant().handleRole(dsw); 
								roles.put(action, dsw); 
								break;
							
							case "davidCook":
								DavidCookRole dc = new DavidCookRole(name, this); 
								ContactList.getInstance().getDavidRestaurant().handleRole(dc);
								roles.put(action, dc); 
								break;
								
							case "davidCashier":
								DavidCashierRole dca = new DavidCashierRole(name, this); 
								ContactList.getInstance().getDavidRestaurant().handleRole(dca); 
								roles.put(action, dca); 
								break; 
							
							case "davidHost":
								DavidHostRole dh = new DavidHostRole(name, this); 
								ContactList.getInstance().getDavidRestaurant().handleRole(dh);
								roles.put(action, dh);
								break;
							
							//-----Market Roles---//
							case "marketEmployee":
								MarketEmployeeRole mem = new MarketEmployeeRole(this, name);
								ContactList.getInstance().getMarket().handleRole(mem);
								roles.put(action, mem);
								break;
							case "marketGreeter":
								MarketGreeterRole mgr = new MarketGreeterRole(this, name);
								ContactList.getInstance().getMarket().handleRole(mgr);
								roles.put(action, mgr);
								break;
							case "marketCashier":
								MarketCashierRole mcsh = new MarketCashierRole(this, name);
								ContactList.getInstance().getMarket().handleRole(mcsh);
								roles.put(action, mcsh);
								break;
							case "marketDeliveryMan":
								MarketDeliveryManRole mdm = new MarketDeliveryManRole(this, name);
								ContactList.getInstance().getMarket().handleRole(mdm);
								roles.put(action, mdm);
								break;
								
							//-----Market 2 Roles ---//
							case "market2Employee":
								MarketEmployeeRole mem2 = new MarketEmployeeRole(this, name);
								ContactList.getInstance().getMarket2().handleRole(mem2);
								roles.put(action, mem2);
								break;
							case "market2Greeter":
								MarketGreeterRole mgr2 = new MarketGreeterRole(this, name);
								ContactList.getInstance().getMarket2().handleRole(mgr2);
								roles.put(action, mgr2);
								break;
							case "market2Cashier":
								MarketCashierRole mcsh2 = new MarketCashierRole(this, name);
								ContactList.getInstance().getMarket2().handleRole(mcsh2);
								roles.put(action, mcsh2);
								break;
							case "market2DeliveryMan":
								MarketDeliveryManRole mdm2 = new MarketDeliveryManRole(this, name);
								ContactList.getInstance().getMarket2().handleRole(mdm2);
								roles.put(action, mdm2);
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
								ContactList.getInstance().getEllenRestaurant().handleRole(e);
								roles.put(action, e);
								break;
							case restaurant_ena:
								EnaCustomerRole en = new EnaCustomerRole(this, name);
								ContactList.getInstance().getEnaRestaurant().handleRole(en);
								roles.put(action, en);
								break;
							case restaurant_jefferson:
								JeffersonCustomerRole jc = new JeffersonCustomerRole(this, name);
								ContactList.getInstance().getJeffersonRestaurant().handleRole(jc);
								roles.put(action,jc);
								break;
							case restaurant_david: 
								DavidCustomerRole d = new DavidCustomerRole(name, this); 
								ContactList.getInstance().getDavidRestaurant().handleRole(d); 
								roles.put(action, d);
								break;
							default:
								break;
						}
						break;
					case market :
						MarketCustomerRole mcr = new MarketCustomerRole(this, name);
						ContactList.getInstance().getMarket().handleRole(mcr);
						roles.put(action, mcr);
						break;
					case market2 : 
						MarketCustomerRole mcr2 = new MarketCustomerRole(this, name);
						ContactList.getInstance().getMarket2().handleRole(mcr2);
						roles.put(action, mcr2);
						break;
					case home :
					case homeAndEat :
						synchronized(roles) {
							if (roles.containsKey(ActionType.home) || roles.containsKey(ActionType.homeAndEat))
								return;
							OccupantRole or = new OccupantRole(this, name);
							ContactList.getInstance().getHome(or).handleRoleGui(or);
							roles.put(action, or);
						}
						break;
					case maintenance:
						LandlordRole lr = ContactList.getInstance().getLandLords().get(0);
						lr.lDActive = landlordActive.working;
						ContactList.getInstance().getHome().handleRoleGui(lr);
						roles.put(action, lr);
						break;
					case bankWithdraw2:
					case bankDeposit2:
					case bankLoan2:
						if(roles.containsKey(ActionType.bankDeposit2) || roles.containsKey(ActionType.bankLoan2) || roles.containsKey(ActionType.bankWithdraw2)){
							return;
						}
						BankCustomerRole bc2 = new BankCustomerRole(this, name);
						ContactList.getInstance().getBank2().handleRole(bc2);
						roles.put(action, bc2);
						break;
					case bankRob2:
						if(roles.containsKey(ActionType.bankRob2)){
							return;
						}
						BankRobberRole br2 = new BankRobberRole(this, name);
						ContactList.getInstance().getBank2().handleRole(br2);
						roles.put(action, br2);
						break;
					case bankWithdraw:
					case bankDeposit:
					case bankLoan:
						if(roles.containsKey(ActionType.bankDeposit) || roles.containsKey(ActionType.bankLoan) || roles.containsKey(ActionType.bankWithdraw)){
							return;
						}
						BankCustomerRole bc = new BankCustomerRole(this, name);
						ContactList.getInstance().getBank().handleRole(bc);
						roles.put(action, bc);
						break;
					case bankRob:
						if(roles.containsKey(ActionType.bankRob)){
							return;
						}
						BankRobberRole br = new BankRobberRole(this, name);
						ContactList.getInstance().getBank().handleRole(br);
						roles.put(action, br);
						break;
					default:
						break;
				}

			}
		}
	}
	
	private void handleAction(ActionType action) {
		switch(action) {
			case work:
				event = PersonEvent.timeToWork;
				break;
			
			case maintenance:
				event = PersonEvent.maintainWork;
				break;
			case hungry:
				event = PersonEvent.gotHungry;
				break;
			case homeAndEat:
				event = PersonEvent.goHome;
				break;
			case market:
				event = PersonEvent.needMarket;
				break;
			case restaurant:
			//======= restaurant hacks from gui ========
			//case restaurant_ellen:
			//case restaurant_david:
			//case restaurant_ena:
			//case restaurant_marcus:
			//case restaurant_jefferson:
				event = PersonEvent.chooseRestaurant;
				break;
			case bankWithdraw:
			case bankWithdraw2:
			case bankDeposit:
			case bankDeposit2:
			case bankLoan: 
			case bankLoan2: 
			case bankRob:
			case bankRob2:
				event = PersonEvent.needToBank;
				break;
			default:
				event = PersonEvent.goHome;
				break;
		}
		
		stateChanged();
	}

	private void travelToLocation(CityLocation d) {
		traveling = true;
		this.destination = d;

		boolean walk = (70 > ((int) (Math.random() * 100)));
		if((destination == CityLocation.home) || (destination == CityLocation.renterHome)) { 
			walk = true;
		}
		walk = false;

		if(!chooseTransportation){
			if((walk || state == PersonState.walkingFromBus || state == PersonState.walkingFromCar)) { //chose to walk
				output(name + " is walking to " + d);
				gui.DoGoToLocation(d); //call gui
				waitForGui();
				return;
			}
			else if(!walk) { //chose bus
				if(hasCar) { 
					System.out.println("Gonna drive"); 
					gui.DoGetOnRoad(); 
					waitForGui();
					state = PersonState.inCar; 
					gui.getInCar(); 
					gui.DoGoInside();
					gui.AddCarToLane();
					gui.DoGoToLocationOnCar(d); 
					return;
				}
				output(name + " is taking the bus to " + d);
				gui.DoGoToStop();
				waitForGui();
					//add self to waiting list of BusStop once arrived
				for(int i=0; i<ContactList.stops.size(); i++){ 
					if(ContactList.stops.get(i).stopLocation == gui.findNearestStop()) { 
						ContactList.stops.get(i).ArrivedAtBusStop(this);
						state = PersonState.waiting;
						return;
					}
				}
			}
		}
		else { 
			if(forceWalk || state == PersonState.walkingFromBus || state == PersonState.walkingFromCar) { //chose to walk
				output(name + " is walking to " + d);
				gui.DoGoToLocation(d); //call gui
				waitForGui();
				return;
			}
			else if(!forceWalk) { //chose bus
				if(hasCar) { 
					System.out.println("Gonna drive"); 
					gui.DoGetOnRoad(); 
					waitForGui();
					state = PersonState.inCar; 
					gui.getInCar(); 
					gui.DoGoInside();
					gui.AddCarToLane();
					gui.DoGoToLocationOnCar(d); 
					return;
				}
				output(name + " is taking the bus to " + d);
				gui.DoGoToStop();
				waitForGui();
					//add self to waiting list of BusStop once arrived
				for(int i=0; i<ContactList.stops.size(); i++){ 
					if(ContactList.stops.get(i).stopLocation == gui.findNearestStop()) { 
						ContactList.stops.get(i).ArrivedAtBusStop(this);
						state = PersonState.waiting;
						return;
					}
				}
			}
		}
	}

	private void chooseRestaurant() {
		if (restaurantHack!= null){	//gui/config hacks
			if (restaurantHack.toLowerCase().contains("ellen"))
				destination = CityLocation.restaurant_ellen;
			else if (restaurantHack.toLowerCase().contains("ena"))
				destination = CityLocation.restaurant_ena;
			else if (restaurantHack.toLowerCase().contains("jefferson"))
				destination = CityLocation.restaurant_jefferson;
			else if (restaurantHack.toLowerCase().contains("marcus"))
				destination = CityLocation.restaurant_marcus;
			else if (restaurantHack.toLowerCase().contains("david"))
				destination = CityLocation.restaurant_david;
			restaurantHack = null;
		}
		else {												//default: do random (if ActionType = restaurant)
			switch((int) (Math.random() * 5)) {
			case 0:
				destination = CityLocation.restaurant_ena;
				break;
			case 1:
				destination = CityLocation.restaurant_ellen;
				break;
			case 2:
				destination = CityLocation.restaurant_marcus;
				break;
			case 3:
				destination = CityLocation.restaurant_jefferson;
				break;
			case 4:
				destination = CityLocation.restaurant_david; 
				break;
			default:
				break;
			}
		}
		currentAction.type = ActionType.restaurant;
		event = PersonEvent.decidedRestaurant;
		handleRole(currentAction.type);
	}
	
	private void decideWhereToEat() {
		output("Deciding where to eat..");

		handleAction(currentAction.type);
		boolean temp = true;
		
		if(temp) { //chose restaurant
			output("Chose to eat at a restaurant");
			currentAction.type = ActionType.restaurant;
			handleAction(currentAction.type);
			return;
		}

		else if(temp) {//chose home
			if(temp) { //check if need food
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
		if(job.occupation.contains("market2"))
			destination = CityLocation.market2;
		else if(job.occupation.contains("market"))
			destination = CityLocation.market;
		else if(job.occupation.contains("bank2"))
			destination = CityLocation.bank2;
		else if(job.occupation.contains("bank"))
			destination = CityLocation.bank;
		else if(job.occupation.contains("marcus"))
			destination = CityLocation.restaurant_marcus;
		else if(job.occupation.contains("ena"))
			destination = CityLocation.restaurant_ena;
		else if(job.occupation.contains("ellen"))
			destination = CityLocation.restaurant_ellen;
		else if(job.occupation.contains("jefferson"))
			destination = CityLocation.restaurant_jefferson;
		else if(job.occupation.contains("david"))
			destination = CityLocation.restaurant_david;
		
		travelToLocation(destination);
		event = PersonEvent.arrivedAtWork;
		stateChanged();
	}

	private void goToRenters() 
	{
		renterHome.setXRenterHome(renterHome.getXLoc());
		renterHome.setYRenterHome(renterHome.getYLoc());
		travelToLocation(CityLocation.renterHome);
		event = PersonEvent.arrivedRenterApartment;
		stateChanged();
	}
	
	private void goToMarket() {
		switch((int) (Math.random() * 2)) {
			case 0:
				output("Going to market 1");
				travelToLocation(CityLocation.market);
				currentAction.type = ActionType.market;
				break;
			case 1:
				output("Going to market 2");
				travelToLocation(CityLocation.market2);
				currentAction.type = ActionType.market2;
				break;
			default:
				break;
		}
		
		event = PersonEvent.arrivedAtMarket;
		stateChanged();

	}

	private void goHome()  {
		output("Going home");
		travelToLocation(CityLocation.home);
		event = PersonEvent.arrivedAtHome;
		stateChanged();
	}

	private void goToRestaurant() {
		output("Going to the restaurant");
		travelToLocation(destination);
		event = PersonEvent.arrivedAtRestaurant;
		stateChanged();
	}

	private void goToBank() {
		if(!currentAction.type.toString().contains("2"))
			travelToLocation(CityLocation.bank);
		else
			travelToLocation(CityLocation.bank2);
		
		event = PersonEvent.arrivedAtBank;
		stateChanged();
	}
	
	private void respawnPerson() {
		alive = true;
		actions.clear();
		gui.DoDie();
		
		synchronized(roles) {
			if(roles.containsKey(ActionType.home))
				roles.get(ActionType.home).setActive();
			else if (roles.containsKey(ActionType.homeAndEat))
				roles.get(ActionType.homeAndEat).setActive();
		}

		stateChanged();
	}
	
	private void boardBus() {
		for(int i=0; i<ContactList.stops.size(); i++){ 
			synchronized(ContactList.stops.get(i).waitingPeople){
				for(int j=0; j<ContactList.stops.get(i).waitingPeople.size(); j++){ 
					if(this == ContactList.stops.get(i).waitingPeople.get(j)){ 
						ContactList.stops.get(i).currentBus.msgIWantToGetOnBus(this);
						currentBus = ContactList.stops.get(i).currentBus; 
						ContactList.stops.get(i).LeavingBusStop(this);
						gui.DoGoInside();
						gui.DoGoToLocationOnBus(destination);	
					}
				}
			}
		}
	}

	//---Other Actions functions---//
	public void addRole(ActionType type, Role role) {
		synchronized(roles) {
			roles.put(type, role);
		}
	}
	
	public void roleInactive() {
		state = PersonState.normal;
		gui.DoGoOutside();
		stateChanged();
	}
	
	public void stateChanged() {
		super.stateChanged();
	}

	public void updateClock(int newTime, int currentDay) {
		this.time = newTime;
		this.day = currentDay;
		checkSelf();
	}
	
	public int getTime() {
		return time;
	}
	public int getDay()
	{
		return day;
	}
	
	public int getWorkHours() {
		if(job.shiftBegin < job.shiftEnd) {
			return job.shiftEnd-job.shiftBegin;
		}
		return (24 - (job.shiftBegin-job.shiftEnd));
	}
	
	public double getCash() {
		return cash;
	}
	
	public void setCash(double d) {
		this.cash = d;
	}
	
	public void stopThread() {
		isMoving.release();
		super.stopThread();
	}

	private void waitForGui() {
		try {
			isMoving.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
	
	public String toString() {
		return name;
	}
	
	private void output(String input) {
		AlertLog.getInstance().logMessage(AlertTag.PERSON, this.getName(), input);
	}
	
	public double getAccountnumber() {
		return accountnumber;
	}

	public void setAccountnumber(double accountnumber) {
		this.accountnumber = accountnumber;
	}
	
	public void setHomePlace(boolean renter) {
		if(renter) {
			for(ApartmentObject apartment : AnimationPanel.getApartments()) {
				if(apartment.getComplex().size() <= 3) {
					this.homePlace = apartment.getBuild();
					print("assigned to apartment" +apartment.getComplex().size());
					apartment.getComplex().add(apartment.getComplex().size());
					//AnimationPanel.getApts().add(apartment.getComplexSize().size()+1);
					//AnimationPanel.apartments.put(apartment, AnimationPanel.getApts());
					break;
				}
			}
		}
		
		if(!renter) {
			for(HomeObject house : AnimationPanel.getHouses()) {
				if(house.getBusy() == false) {
					this.homePlace = house.getBuild();
					house.setBusy(true);
					return;
				}
			}
		}
	}
	
	public Building getHomePlace() {
		return homePlace;
	}
	

	public void setHomePlace(Building homePlace) {
		this.homePlace = homePlace;
	}

	//---Used for Unit Testing---//
	public Map<ActionType, Role> getRoles() {
		return roles;
	}
	
	public PriorityBlockingQueue<Action> getActions() {
		return actions;
	}
	
	public Action getCurrentAction() {
		return currentAction;
	}
	
	public void setEvent(PersonEvent e) {
		event = e;
	}
	
	public void setDestination(CityLocation dest) {
		destination = dest;
	}
	
	public PersonState getState() {
		return state;
	}
	//----------------//
	
	private boolean actionExists(ActionType type) {
		synchronized(actions) {
			for(Action a : actions) { 
				if(a.type == type) return true;
			}
			
			return false;
		}
	}

	//Lower the priority level, the more "important" it is (it'll get done earlier)
	public enum ActionState {created, inProgress, done}
	public enum ActionType {work, fixing, maintenance, self_maintenance, hungry, homeAndEat, 
		restaurant, restaurant_ellen, restaurant_marcus, restaurant_ena, restaurant_david, restaurant_jefferson,
		market, market2, bankWithdraw, bankDeposit, bankLoan, bankRob, bankWithdraw2, bankDeposit2, bankLoan2, bankRob2, home}
	public class Action implements Comparable<Object> {
		public ActionState state;
		public ActionType type;
		public int priority;
		
		public Action(ActionType t, int p) {
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
	
	class Job {
		String occupation;
		int shiftBegin = -1;
		int shiftEnd = -1;
	}
}
