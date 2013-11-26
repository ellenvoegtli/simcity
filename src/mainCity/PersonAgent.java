package mainCity;
import agent.Agent;
import role.*;
import role.davidRestaurant.*;
import role.jeffersonRestaurant.*;
import role.marcusRestaurant.*;
import housing.LandlordRole;
import housing.OccupantRole;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

import mainCity.bank.BankCustomerRole;
import mainCity.bank.BankManagerRole;
import mainCity.bank.BankTellerRole;
import mainCity.bank.BankerRole;
import mainCity.contactList.ContactList;
import mainCity.gui.*;
import mainCity.gui.trace.*;
import mainCity.interfaces.ManagerRole;
import mainCity.interfaces.PersonGuiInterface;
import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.enaRestaurant.*;
import mainCity.market.*;
import role.market.*;
import transportation.BusAgent;

public class PersonAgent extends Agent {
	public enum PersonState {normal, working, inBuilding, waiting, boardingBus, walkingFromBus}
	public enum PersonEvent {none, arrivedAtHome, arrivedAtWork, arrivedAtMarket, arrivedAtRestaurant, arrivedAtBank, timeToWork, needMarket, gotHungry, gotFood, chooseRestaurant, decidedRestaurant, needToBank, maintainWork,goHome}
	public enum CityLocation {home, restaurant_david, restaurant_ellen, restaurant_ena, restaurant_jefferson, restaurant_marcus, bank, market}
	
	private PersonGuiInterface gui;
	private String name;
	private double cash;
	private double accountnumber;
	private boolean traveling;
	private boolean onBreak;
	private BusAgent currentBus; 
	private Building homePlace;
	private int time;
	private Job job;
	private PersonState state;
	private PersonEvent event;
	private CityLocation destination;
	private Semaphore isMoving = new Semaphore(0, true);
	private Map<ActionType, Role> roles;
	private PriorityBlockingQueue<Action> actions;
	private Action currentAction;
	
	public PersonAgent(String n) {
		super();
		
		name = n;
		traveling = false;
		onBreak = false;
		job = new Job();
		cash = 100.0;
		state = PersonState.normal;//maybe 'inBuilding' if we start everyone in home
		event = PersonEvent.none;
		destination = CityLocation.home;
		roles = Collections.synchronizedMap(new HashMap<ActionType, Role>());
		actions = new PriorityBlockingQueue<Action>();
		currentAction = null;
	}
	
	public void setGui(PersonGuiInterface g) {
		this.gui = g;
	}
	
	public CityLocation getDestination() { 
		return destination;
	}
	
	public boolean isHungryForRestaurant(){
		if(actions.contains(ActionType.restaurant) || (currentAction != null && currentAction.type == ActionType.restaurant))
			return true;
		return false;
	}
	public boolean isHungryForHome(){
		if(actions.contains(ActionType.homeAndEat) || (currentAction != null && currentAction.type == ActionType.homeAndEat))
			return true;
		return false;
	}
	public boolean isGoingOrAtWork(){
		if(actions.contains(ActionType.work) || (currentAction != null && currentAction.type == ActionType.work))
			return true;
		return false;
	}
	public Building getBuilding(){
		return homePlace;
	}
	
	//----------Messages----------//
	//From a timer to tell the person to do a checkup
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
		gui.DoGoOutside();
		state = PersonState.walkingFromBus;
		stateChanged();
	}
	//A message for the landlord	
	public void msgNeedToFix() {
		actions.add(new Action(ActionType.maintenance, 1));
		stateChanged();
	}
	
	
	public void msgBusHasArrived() {
		//print("msgBusHasArrived received");
		state = PersonState.boardingBus;
		stateChanged();
	}

	//A message received from the system or GUI to tell person to go to work
	public void msgGoToWork() {
		actions.add(new Action(ActionType.work, 1));
		stateChanged();
	}

	//A message received from the system or GUI to tell person to get hungry - //they will choose between restaurant and home
	public void msgGotHungryForHome() {
		if(!actions.contains(ActionType.homeAndEat)) {
			output(name + " got hungry, will go home");
			actions.add(new Action(ActionType.homeAndEat, 5));
			stateChanged();
		}
	}
	
	//A message received from the HomeAgent or GUI (possibly?) to go to a restaurant
	public void msgGoToRestaurant() {
		if(!actionExists(ActionType.restaurant)) {
			//output(name + " will go to restaurant");
			actions.add(new Action(ActionType.restaurant, 4));
			stateChanged();
		}
	}

	//A message received from the HomeAgent or GUI (possibly?) to go to the market
	public void msgGoToMarket() {
		actions.add(new Action(ActionType.market, 3));
		stateChanged();
	}
	
	public void msgGoHome() {
		actions.add(new Action(ActionType.home, 3));
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
		
		if(!actions.isEmpty() && currentAction == null ) {
			actions.peek().state = ActionState.inProgress;
			currentAction = actions.poll();
			handleAction(currentAction.type);
			return true;
		}
		
		if(currentAction != null && state == PersonState.normal && !traveling) {
			if(event == PersonEvent.arrivedAtHome) {
				if(!actions.isEmpty()) { //If there's other stuff to do, don't go inside yet
					currentAction.state = ActionState.done;
					return true;
				}
				
				output("Arrived at home!");
				
				handleRole(currentAction.type);
				
				if(currentAction != null && (currentAction.type == ActionType.homeAndEat)){
					if (roles.get(ActionType.home) != null) {
						((OccupantRole) roles.get(ActionType.home)).gotHungry();
						roles.get(ActionType.home).setActive();
					}

					else {
						((OccupantRole) roles.get(ActionType.homeAndEat)).gotHungry();
						roles.get(ActionType.homeAndEat).setActive();
					}
				}
				else
					roles.get(currentAction.type).setActive();
				
				if(currentAction != null && (currentAction.type == ActionType.market || currentAction.type == ActionType.home)) {
					currentAction.state = ActionState.done;
				}
				
				gui.DoGoInside();
				state = PersonState.inBuilding;
				return true;
			}

			if(event == PersonEvent.arrivedAtWork) {
				output("Arrived at work!");
				
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
				
				gui.DoGoInside();
				state = PersonState.working;
				return true;
			}

			if(event == PersonEvent.arrivedAtMarket) {
				//output("Arrived at market!");
				handleRole(currentAction.type);
				Role customer = roles.get(currentAction.type);
				if (!((MarketCustomerRole) customer).getGui().goInside()){
					//System.out.println("Waiting for restaurant to open");
					return true;
				}
				//check home agent to get a list of what they need?
				customer.setActive();
				
				if(currentAction != null && currentAction.type == ActionType.restaurant) {
					currentAction.state = ActionState.done;
				}
				gui.DoGoInside();
				state = PersonState.inBuilding;
				return true;
			}

			
			if(event == PersonEvent.arrivedAtRestaurant) {
				output("Arrived at " + destination);	
				handleRole(currentAction.type);
				Role customer = roles.get(currentAction.type);
				
				if(customer instanceof MarcusCustomerRole) {
					if(!((MarcusCustomerRole) customer).getGui().goInside()) {
						//If restaurant is closed go try another --should cycle a lot now since only 1 restaurant;
						//output("Restaurant closed...trying another");
						//may also need to check if cook and cashier are at restaurant.
						chooseRestaurant();
						return true;
					}
				}
				else if(customer instanceof EllenCustomerRole) {
					if (!((EllenCustomerRole) customer).getGui().goInside()){
						chooseRestaurant();
						return true;
					}
					//((EllenCustomerRole) customer).gotHungry();
				}
				else if(customer instanceof EnaCustomerRole)
				{
					print("is customer hungry?");
					if (!((EnaCustomerRole) customer).getGui().goInside())
					{	chooseRestaurant();
						return true;
					}
				}
				else if(customer instanceof JeffersonCustomerRole){
					//((JeffersonCustomerRole) customer).gotHungry();
					if(!((JeffersonCustomerRole) customer).getGui().goInside()){
						chooseRestaurant();
						return true;
					}
				}
				else if(customer instanceof DavidCustomerRole){
					if(!((DavidCustomerRole) customer).getGui().goInside()){
						chooseRestaurant(); 
						return true;
					}
				}
				
				customer.setActive();

				if(currentAction != null && currentAction.type == ActionType.restaurant) {
					currentAction.state = ActionState.done;
				}
				
				gui.DoGoInside();
				state = PersonState.inBuilding;
				return true;
			}
		
			if(event == PersonEvent.arrivedAtBank) {
				//set appropriate role and initial state for different actions
				handleRole(currentAction.type);
				Role customer = roles.get(currentAction.type);
				if (!((BankCustomerRole) customer).getGui().goInside()){
					//System.out.println("bank closed");
					currentAction.state=ActionState.done;
					return true;
				}
				if(roles.containsKey(ActionType.bankWithdraw)){
					roles.get(ActionType.bankWithdraw).setActive();
					Role bankCustomer = roles.get(ActionType.bankWithdraw);
					((BankCustomerRole) bankCustomer).msgWantToWithdraw();
				}
				else if(roles.containsKey(ActionType.bankDeposit)){
					roles.get(ActionType.bankDeposit).setActive();
					Role bankCustomer = roles.get(ActionType.bankDeposit);
					((BankCustomerRole) bankCustomer).msgWantToDeposit();
				}
				else if(roles.containsKey(ActionType.bankLoan)){
					roles.get(ActionType.bankLoan).setActive();
					Role bankCustomer = roles.get(ActionType.bankLoan);
					((BankCustomerRole) bankCustomer).msgNeedLoan();
				}

				if(currentAction != null && (currentAction.type == ActionType.bankWithdraw || currentAction.type == ActionType.bankDeposit || currentAction.type == ActionType.bankLoan)) {
					currentAction.state = ActionState.done;
				}
				gui.DoGoInside();
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
			
			if(event == PersonEvent.maintainWork)
			{
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

		if(actions.isEmpty() && state == PersonState.normal && !traveling) {
			output("My action list is empty. Going home");
			actions.add(new Action(ActionType.home, 10));
			return true;
		}
		
		return false;
	}
	
	//----------Actions----------//
	private void checkSelf() {
		//FOR AI - need to check self to do things? bank, eat, etc. -- this is called from the global timer
		if(time == job.shiftBegin && state != PersonState.working && !actionExists(ActionType.work) && !job.occupation.equals("rich")) {
			actions.add(new Action(ActionType.work, 1));
			stateChanged();
		}
		
		if(job.occupation.equals("rich") && event == PersonEvent.maintainWork)
		{
			actions.add(new Action(ActionType.maintenance , 1));
		}
		
		if(time == job.shiftEnd && state == PersonState.working) {
			for(Map.Entry<ActionType, Role> r : roles.entrySet()) {
				if(r.getValue() instanceof ManagerRole && r.getValue().isActive() ) {
					output("Closing up shop");
					((ManagerRole) r.getValue()).msgEndShift();
				}
				
			}
		}
		
		if(cash < 50 && !actionExists(ActionType.bankWithdraw)){
			actions.add(new Action(ActionType.bankWithdraw,3));
			stateChanged();
		}
		if(cash > 200 && !actionExists(ActionType.bankDeposit)){

			actions.add(new Action(ActionType.bankDeposit,3));
			stateChanged();
		}
		
	}
	
	private void handleRole(ActionType action) {
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
							JeffersonWaiterRole jw = new JeffersonWaiterRole(this, name);
							ContactList.getInstance().getJeffersonRestaurant().handleRole(jw);
							roles.put(action, jw);
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
							EnaWaiterRole en = new EnaWaiterRole(this, name);
							ContactList.getInstance().getEnaRestaurant().handleRole(en);
							roles.put(action, en);
							break;
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
							DavidWaiterRole dw = new DavidWaiterRole(name, this); 
							ContactList.getInstance().getDavidRestaurant().handleRole(dw); 
							roles.put(action, dw); 
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
				case home :
				case homeAndEat : 
					if (actions.contains(ActionType.home) || actions.contains(ActionType.homeAndEat))
						return;
					OccupantRole or = new OccupantRole(this, name);
					ContactList.getInstance().getHome().handleRoleGui(or);
					roles.put(action, or);
					break;

				case maintenance:
					LandlordRole lr = new LandlordRole(this);
					ContactList.getInstance().getHome().handleRoleGui(lr);
					roles.put(action, lr);
					break;
					
				case bankWithdraw:
				case bankDeposit:
				case bankLoan:
					if(roles.containsKey("bankDeposit") || roles.containsKey("bankLoan") || roles.containsKey("bankWithdraw")){
						return;
					}
					BankCustomerRole bc = new BankCustomerRole(this, name);
					ContactList.getInstance().getBank().handleRole(bc);
					roles.put(action, bc);
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
				event = PersonEvent.chooseRestaurant;
				break;
			case bankWithdraw:
				event = PersonEvent.needToBank;
				break;
			case bankDeposit:
				event = PersonEvent.needToBank;
				break;
			case bankLoan: 
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

		if(walk || state == PersonState.walkingFromBus) { //chose to walk
			output(name + " is walking to " + d);
			gui.DoGoToLocation(d); //call gui
			waitForGui();
			return;
		}
		else if(!walk) { //chose bus
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
			//bus.myDestination(d); //send message to transportation object of where they want to go
			//will receive an arrived at destination message when done
		}
		else if(walk) {//chose car
			//DoGoToCar(); //walk to car
			waitForGui();
			//car.msgGoToPlace(d); //some message to tell the car where to go, will receive an arrived at destination message when done
		}
	}

	private void chooseRestaurant() {
		
		//destination = CityLocation.restaurant_david;
		
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
		

		event = PersonEvent.decidedRestaurant;
		handleRole(currentAction.type);
	}
	
	private void decideWhereToEat() {
		output("Deciding where to eat..");
		//Decide between restaurant or home
		handleAction(currentAction.type);
		boolean temp = true;
		
		if(temp) { //chose restaurant
			output("Chose to eat at a restaurant");
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
		if(job.occupation.contains("market")) {
			destination = CityLocation.market;
		}
		else if(job.occupation.contains("bank")) {
			destination = CityLocation.bank;
		}
		else if(job.occupation.contains("marcus")) {
			destination = CityLocation.restaurant_marcus;
		}
		else if(job.occupation.contains("ena")) {
			destination = CityLocation.restaurant_ena;
		}
		else if(job.occupation.contains("ellen")) {
			destination = CityLocation.restaurant_ellen;
		}
		else if(job.occupation.contains("jefferson")){
			destination =CityLocation.restaurant_jefferson;
		}
		else if(job.occupation.contains("david")){ 
			destination =CityLocation.restaurant_david;
		}
		travelToLocation(destination);
		event = PersonEvent.arrivedAtWork;
		stateChanged();
	}

	private void goToRenters()
	{
		output("Going to a renters home for maintenance");
		
		travelToLocation(CityLocation.home);
		
		stateChanged();
	}
	
	private void goToMarket() {
		output("Going to the market");
		travelToLocation(CityLocation.market);
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
		travelToLocation(CityLocation.bank);
		event = PersonEvent.arrivedAtBank;
		stateChanged();
	}
	
	private void boardBus() {
		///message the bus
		for(int i=0; i<ContactList.stops.size(); i++){ 
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

	//---Other Actions functions---//
	public void addRole(ActionType type, Role role) {
		roles.put(type, role);
	}
	
	public void roleInactive() {
		state = PersonState.normal;
		gui.DoGoOutside();
		stateChanged();
	}
	
	public void stateChanged() {
		super.stateChanged();
	}

	public void updateClock(int newTime) {
		this.time = newTime;
		checkSelf();
	}
	
	public int getTime() {
		return time;
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
	
	private void output(String input) {
		AlertLog.getInstance().logMessage(AlertTag.PERSON, this.getName(), input);
	}
	
	public double getAccountnumber() {
		return accountnumber;
	}

	public void setAccountnumber(double accountnumber) {
		this.accountnumber = accountnumber;
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
	
	public PersonState getState() {
		return state;
	}
	//---      ---//
	
	public void setHomePlace(boolean renter)
	{
		if(renter)
		{
			for(Building apartment : AnimationPanel.getApartments().keySet())
			{
				if(AnimationPanel.getApartments().get(apartment).size() <= 3)
				{
					this.homePlace = apartment;
					AnimationPanel.getApts().add(AnimationPanel.getApartments().get(apartment).size()+1);
					AnimationPanel.apartments.put(apartment, AnimationPanel.getApts());
					break;
				}
			}
		}
		
		if(!renter)
		{
			for(Building house : AnimationPanel.getHouses().keySet())
			{
				if(AnimationPanel.getHouses().get(house) == false)
				{
					this.homePlace = house;
					AnimationPanel.houses.put(house, true);
					break;
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
	
	private boolean actionExists(ActionType type) {
		for(Action a : actions) { 
			if(a.type == type) return true;
		}
		return false;
	}

	//Lower the priority level, the more "important" it is (it'll get done faster)
	private enum ActionState {created, inProgress, done}
	public enum ActionType {work, maintenance, self_maintenance, hungry, homeAndEat, restaurant, market, bankWithdraw, bankDeposit, bankLoan, home}
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
