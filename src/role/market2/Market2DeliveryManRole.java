package role.market2;

import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.*;
import mainCity.market2.interfaces.DeliveryManGuiInterface;
import mainCity.market2.interfaces.MarketCashier;
import mainCity.market2.interfaces.DeliveryMan2;
import mainCity.restaurants.EllenRestaurant.interfaces.Cook;
import mainCity.restaurants.EllenRestaurant.interfaces.Cashier;
import role.Role;
import role.market1.Market1DeliveryManRole.AgentState;
import role.market1.Market1DeliveryManRole.Bill;
import role.market1.Market1DeliveryManRole.DeliveryEvent;
import role.market1.Market1DeliveryManRole.DeliveryState;

import java.util.*;
import java.util.concurrent.*;


 // Restaurant Cook Agent

public class Market2DeliveryManRole extends Role implements DeliveryMan2{			//only handles one restaurant at a time right now
	private String name;
	public DeliveryManGuiInterface deliveryGui;
	MarketCashier cashier;
	
	private double cash = 0;
	Timer timer = new Timer();
	public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	public AgentState state;
	public enum AgentState {doingNothing, makingDelivery};
	
	public enum DeliveryState {newBill, enRoute, waitingToRedeliver, waitingForPayment, calculatingChange, oweMoney, waitingForVerification, goingBackToMarket, done};
	public enum DeliveryEvent {checkRedeliveryOn, checkRedeliveryOff, deliveryRequested, arrivedAtLocation, receivedPayment, changeVerified, acknowledgedDebt, arrivedAtMarket};
	
	
	private Semaphore atHome = new Semaphore(0, true);
	private Semaphore atDestination = new Semaphore(0, true);
	private boolean onDuty;
	
	
	//constructor
	public Market2DeliveryManRole(PersonAgent p, String name) {
		super(p);
		this.name = name;
		state = AgentState.doingNothing;
		onDuty = true;
	}
	public List<Bill> getBills(){
		return bills;
	}
	public double getCash(){
		return cash;
	}

	public String getName() {
		return name;
	}
	public void setCashier(MarketCashier c){
		cashier = c;
	}
	public AgentState getState(){
		return state;
	}
	public void setState(AgentState s){
		state = s;
	}

	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.MARKET2, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.MARKET2_DELIVERYMAN, this.getName(), s);
	}
	
	// Messages
	
	public void msgHereIsOrderForDelivery(String restaurantName, Map<String, Integer>inventory, double billAmount){
		log("Received msgHereIsOrderForDelivery");
		bills.add(new Bill(restaurantName, billAmount, inventory));
		stateChanged();
	}

	
	public void msgHereIsPayment(double amount, String restaurantName){		//sent by any restaurant's cashier
		log("Received msgHereIsPayment: got $" + amount);
		Bill b = null;
		for (Bill thisB : bills){
			if (thisB.restaurantName.equalsIgnoreCase(restaurantName)){
				b = thisB;
				break;
			}
		}
		b.amountPaid = amount;
		b.event = DeliveryEvent.receivedPayment;
		stateChanged();
	}
	public void msgChangeVerified(String name){
		log("Received msgChangeVerified");
		Bill b = null;
		for (Bill thisB : bills){	
			if (thisB.restaurantName.equalsIgnoreCase(name)){
				b = thisB;
				break;
			}
		}
		cash += b.amountMarketGets;
		cash = Math.round(cash*100.0)/100.0;
		b.event = DeliveryEvent.changeVerified;
		stateChanged();
	}
	
	public void msgIOweYou(double amount, String name){
		log("Received msgIOweYou from " + name + " for $" + amount);
		Bill b = null;
		for (Bill thisB : bills){	
			if (thisB.restaurantName.equalsIgnoreCase(name)){
				b = thisB;
				break;
			}
		}
		cash += b.amountMarketGets;
		cash = Math.round(cash*100.0)/100.0;
		b.event = DeliveryEvent.acknowledgedDebt;
		stateChanged();
	}
	
	
	
	public void msgCheckForRedeliveries(){
		log("Checking for bills that need redelivery");
		for (Bill b : bills){
			if (b.s == DeliveryState.waitingToRedeliver){
				b.event = DeliveryEvent.checkRedeliveryOn;
				stateChanged();
				return;
			}
		}
	}
	
	
	public void msgAtHome(){		//from gui
		log("msgAtHome called");
		atHome.release();
		stateChanged();
	}
	public void msgAtDestination(){
		log("msgAtDestination called");
		atDestination.release();
		stateChanged();
	}
	
	public void msgGoOffDuty(double amount){
		addToCash(amount);
		onDuty = false;
		stateChanged();
	}
	
	

	 // Scheduler.  Determine what action is called for, and do it.
	 
	public boolean pickAndExecuteAnAction() {
		
		for(Bill b: bills){
			if (b.s == DeliveryState.newBill && b.event == DeliveryEvent.deliveryRequested && state == AgentState.doingNothing){
				log("NEW BILL, DELIVERY REQUESTED! :" + b.restaurantName);
				DeliverOrder(b);
				state = AgentState.makingDelivery;
				return true;
			}
		}
		for(Bill b: bills){
			if (b.s == DeliveryState.waitingForPayment && b.event == DeliveryEvent.receivedPayment){
				CalculateChange(b);
				return true;
			}
		}
		for(Bill b: bills){
			if (b.s == DeliveryState.waitingForVerification && b.event == DeliveryEvent.changeVerified){
				ReturnToMarket(b);
				state = AgentState.doingNothing;
				return true;
			}
		}
		for(Bill b: bills){
			if (b.s == DeliveryState.oweMoney && b.event == DeliveryEvent.acknowledgedDebt){
				ReturnToMarket(b);
				state = AgentState.doingNothing;
				return true;
			}
		}
		
		for (Bill b: bills){
			if (b.s == DeliveryState.waitingToRedeliver && b.event == DeliveryEvent.checkRedeliveryOn && state == AgentState.doingNothing){
				log("RE-delivering order...");
				DeliverOrder(b);
				state = AgentState.makingDelivery;
				b.event = DeliveryEvent.checkRedeliveryOff;
				return true;
			}
		}
		
		
		if (bills.isEmpty() && !onDuty){
			deliveryGui.DoGoToHomePosition();
			super.setInactive();
			onDuty = true;
		}


		return false;
	}
	

	// Actions
	public void DeliverOrder(Bill b){
		log("Traveling to delivery location: " + b.restaurantName);
		
		deliveryGui.DoDeliverOrder(b.restaurantName);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (!restaurantOpen(b)){
			deliveryGui.DoGoToHomePosition();
			try {
				atHome.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			b.s = DeliveryState.waitingToRedeliver;
			return;
		}
		else {
			//delivery man will then message appropriate cashier and cook
			if (b.restaurantName.equalsIgnoreCase("ellenRestaurant")){
				b.cook = ContactList.getInstance().ellenCook;
				b.cashier = ContactList.getInstance().ellenCashier;
				b.cook.msgHereIsYourOrder(b.itemsBought);
				b.cashier.msgHereIsMarketBill(b.itemsBought, b.amountCharged, this);
			}
			else if (b.restaurantName.equalsIgnoreCase("enaRestaurant")){
				b.cook = ContactList.getInstance().enaCook;
				b.cashier = ContactList.getInstance().enaCashier;
				b.cook.msgHereIsYourOrder(b.itemsBought);
				b.cashier.msgHereIsMarketBill(b.itemsBought, b.amountCharged, this);
			}
			else if (b.restaurantName.equalsIgnoreCase("marcusRestaurant")){
				b.cook = ContactList.getInstance().marcusCook;
				b.cashier = ContactList.getInstance().marcusCashier;
				b.cook.msgHereIsYourOrder(b.itemsBought);
				b.cashier.msgHereIsMarketBill(b.itemsBought, b.amountCharged, this);
			}
			else if (b.restaurantName.equalsIgnoreCase("jeffersonRestaurant")){
				b.cook = ContactList.getInstance().jeffersonCook;
				b.cashier = ContactList.getInstance().jeffersonCashier;
				b.cook.msgHereIsYourOrder(b.itemsBought);
				b.cashier.msgHereIsMarketBill(b.itemsBought, b.amountCharged, this);
			}
			else if (b.restaurantName.equalsIgnoreCase("davidRestaurant")){
				b.cook = ContactList.getInstance().davidCook;
				b.cashier = ContactList.getInstance().davidCashier;
				b.cook.msgHereIsYourOrder(b.itemsBought);
				b.cashier.msgHereIsMarketBill(b.itemsBought, b.amountCharged, this);
			}
			
			
			b.s = DeliveryState.waitingForPayment;
		}
	}
	
	
	public void CalculateChange(Bill b){
		log("Calculating change");

		double dollars = 0;
		if (b.amountPaid >= b.amountCharged){
			dollars = Math.round((b.amountPaid - b.amountCharged)*100.0)/100.0;
			b.cashier.msgHereIsChange(dollars, this);
			
			b.amountMarketGets = Math.round(b.amountCharged *100.0)/100.0;		//mostly for testing purposes
			b.s = DeliveryState.waitingForVerification;
		}
		else {		//if they didn't pay enough
			b.amountOwed = Math.round((b.amountCharged - b.amountPaid)*100.0)/100.0;		//mostly for testing purposes
			b.amountMarketGets = b.amountPaid;
			
			b.cashier.msgNotEnoughMoney(b.amountOwed, b.amountPaid);
			b.s = DeliveryState.oweMoney;
		}
	}
	
	public void ReturnToMarket(Bill b){
		log("Returning to market");
		bills.remove(b);
		deliveryGui.DoGoToHomePosition();
		try {
			atHome.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		state = AgentState.doingNothing;
		
		if(!onDuty){
			super.setInactive();
			onDuty = true;
		}
	}

	//utilities
	public void setGui(DeliveryManGuiInterface gui) {
		deliveryGui = gui;
	}

	public DeliveryManGuiInterface getGui() {
		return deliveryGui;
	}
	
	
	public boolean restaurantOpen(Bill b){
		if (b.restaurantName.equalsIgnoreCase("ellenrestaurant")){
			if (ContactList.getInstance().ellenHost !=null){
				if (ContactList.getInstance().ellenHost.isOpen()){
					log("Ellen's host says restaurant is OPEN!");
					return true;
				}
			}
			log("Ellen's restaurant is CLOSED.");
			return false;
		}
		else if (b.restaurantName.equalsIgnoreCase("enarestaurant")){
			if (ContactList.getInstance().enaHost !=null)
				if (ContactList.getInstance().enaHost.isItOpen()){
					log("Ena's host says restaurant is OPEN!");
					return true;
				}
			log("Ena's restaurant is CLOSED.");
			return false;
		}
		else if (b.restaurantName.equalsIgnoreCase("marcusrestaurant")){
			if (ContactList.getInstance().marcusHost != null)
				if (ContactList.getInstance().marcusHost.isOpen()){
					log("Marcus' host says restaurant is OPEN!");
					return true;
				}
			log("Marcus' restaurant is CLOSED.");
			return false;
		}
		else if (b.restaurantName.equalsIgnoreCase("davidrestaurant")){
			if (ContactList.getInstance().davidHost != null)
				if(ContactList.getInstance().davidHost.isOpen()){
					log("David's host says restaurant is OPEN!");
					return true;
				}
			log("David's restaurant is CLOSED.");
			return false;
		}
		else if (b.restaurantName.equalsIgnoreCase("jeffersonrestaurant")){
			if (ContactList.getInstance().jeffersonHost != null)
				if (ContactList.getInstance().jeffersonHost.isOpen()){
					log("Jefferson's host says restaurant is OPEN!");
					return true;
				}
			log("Jefferson's restaurant is CLOSED.");
			return false;
		}
		
		return false;	//last resort if something is wrong
	}
	
	
	

	public class Bill {		//public only for testing purposes
		public DeliveryState s = DeliveryState.newBill;
		public DeliveryEvent event = DeliveryEvent.deliveryRequested;
		Map<String, Integer> itemsBought;
		double amountCharged;
		double amountPaid;
		double amountOwed;
		double amountMarketGets;
		String restaurantName;
		MainCook cook;
		MainCashier cashier;

		Bill(String name, double billAmount, Map<String, Integer> inventory){
			amountCharged = billAmount;
			restaurantName = name;
			itemsBought = new TreeMap<String, Integer>(inventory);
		}
		
		public double getAmountPaid(){
			return amountPaid;
		}
		public double getAmountCharged(){
			return amountCharged;
		}
		public double getAmountOwed(){
			return amountOwed;
		}
		public double getAmountMarketGets(){
			return amountMarketGets;
		}
		public MainCook getCook(){
			return cook;
		}
		public void setCook(Cook c){
			cook = c;
		}
		public MainCashier getCashier(){
			return cashier;
		}
		public void setCashier(Cashier c){
			cashier = c;
		}
		public String getRestaurant(){
			return restaurantName;
		}
		public DeliveryState getState(){
			return s;
		}
		public void setState(DeliveryState state){
			s = state;
		}
		public DeliveryEvent getEvent(){
			return event;
		}
	}

	
}

