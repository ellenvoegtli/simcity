
package role.ellenRestaurant;

import java.util.*;

import role.market.*;
import mainCity.PersonAgent;
import mainCity.restaurants.EllenRestaurant.EllenMenu;
import mainCity.restaurants.EllenRestaurant.interfaces.*;
import mainCity.contactList.ContactList;
import mainCity.gui.trace.*;
import mainCity.interfaces.DeliveryMan;
import role.Role;


 // Restaurant Cook Agent

public class EllenCashierRole extends Role implements Cashier {	
	private String name;
	private double cash = 0;		//default
	Timer timer = new Timer();
	private EllenMenu menu = new EllenMenu();
	
	public List<Check> checks = Collections.synchronizedList(new ArrayList<Check>());	//from waiters
	private List<Waiter> waiters = Collections.synchronizedList(new ArrayList<Waiter>());
	public List<MarketBill> marketBills = Collections.synchronizedList(new ArrayList<MarketBill>());
	
	public enum CheckState {newCheck, computing, waitingForPayment, calculatingChange, done};
	public enum MarketBillState {newBill, computing, waitingForChange, receivedChange, oweMoney, done};	//is this ok???
	
	private boolean onDuty;
	private boolean opened;
	private EllenHostRole host;

	
	public EllenCashierRole(PersonAgent p, String name) {
		super(p);
		this.name = name;
		onDuty = true;
		opened = true;
	}

	public void addWaiter(Waiter w){	//hack
		waiters.add(w);
	}
	public String getName() {
		return name;
	}
	public List<Check> getChecks(){
		return checks;
	}
	public List<MarketBill> getMarketBills(){
		return marketBills;
	}
	public void setHost(EllenHostRole h){
		host = h;
	}

	public void deductCash(double amount){
		cash -= amount;
	}
	
	//for alert log trace statements
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.ELLEN_CASHIER, this.getName(), s);
	}
	
	// Messages
	//restaurant waiter/customer messages
	public void msgComputeBill(String choice, Customer cust, Waiter w){
		Check c = new Check(choice, cust, w);
		checks.add(c);
		c.s = CheckState.computing;
		
		log(", received msgComputeBill for " + cust.getName() + ", order: " + choice);
		stateChanged();
	}
	public void msgHereIsPayment(double checkAmount, double cashAmount, Customer cust){
		log("Received msgHereIsPayment: got $" + cashAmount + " for check: $" + checkAmount);
				
		Check c = null;
		synchronized(checks){
			for (Check thisC : checks){	
				if (thisC.cust.equals(cust)){
					c = thisC;
					break;
				}
			}
		}
		c.cashAmount = cashAmount;
		c.s = CheckState.calculatingChange;
		stateChanged();
	}
	
	
	//market delivery man messages
	public void msgHereIsMarketBill(Map<String, Integer>inventory, double billAmount, DeliveryMan d){
		log("Received msgHereIsMarketBill from " + d.getName() + " for $" + billAmount);
		marketBills.add(new MarketBill(d, billAmount, inventory, MarketBillState.computing));
		stateChanged();
	}
	public void msgHereIsChange(double amount, DeliveryMan deliveryPerson){
		log("Received msgHereIsChange: $" + amount);
		MarketBill b = null;
		synchronized(marketBills){
			for (MarketBill thisMB : marketBills){
				if (thisMB.deliveryMan == deliveryPerson){
					b = thisMB;
					break;
				}
			}
		}
		b.amountChange = amount;
		b.s = MarketBillState.receivedChange;
		stateChanged();
	}
	public void msgNotEnoughMoney(double amountOwed, double amountPaid){
		log("Received msgNotEnoughMoney: owe $" + amountOwed);
		MarketBill b = null;
		synchronized(marketBills){
			for (MarketBill thisMB : marketBills){
				if (thisMB.amountPaid == amountPaid){
					b = thisMB;
					break;
				}
			}
		}
		b.amountOwed = Math.round(amountOwed*100.00)/100.00;
		b.s = MarketBillState.oweMoney;
		stateChanged();
	}
	
	public void msgGoOffDuty(double amount){
		addToCash(amount);
		ContactList.getInstance().getBank().directDeposit("ellenrestaurant", cash);
		onDuty = false;
		stateChanged();
	}
	

	 // Scheduler.  Determine what action is called for, and do it.
	 
	public boolean pickAndExecuteAnAction() {
		if (opened){
			getDailyCapital();
			ContactList.getInstance().setEllenCashier(this);
			opened = false;
		}
		
		
		//Customer checks
		synchronized(checks){
			for (Check c : checks) {
				if (c.s == CheckState.calculatingChange){
					CalculateChange(c);
					return true;
				}
			}
		}
		synchronized(checks){
			for (Check c : checks) {
				if (c.s == CheckState.computing){
					ComputeBill(c);
					return true;
				}
			}
		}
		
		//Market bills
		synchronized(marketBills){
			for (MarketBill b : marketBills){
				if (b.s == MarketBillState.computing){
					PayMarketBill(b);
					return true;
				}
			}
		}
		synchronized(marketBills){
			for (MarketBill b : marketBills){
				if (b.s == MarketBillState.receivedChange){
					VerifyChange(b);
					return true;
				}
			}
		}
		synchronized(marketBills){
			for (MarketBill b : marketBills){
				if (b.s == MarketBillState.oweMoney){
					AcknowledgeDebt(b);
					return true;
				}
			}
		}
		
		if (checks.isEmpty() && marketBills.isEmpty() && !onDuty){
			setInactive();
			onDuty = true;
			opened = false;
		}

		return false;
	}
	

	// Actions
	private void getDailyCapital(){
		ContactList.getInstance().getBank().directWithdraw("ellenrestaurant", 1500);
		cash = 1500;
	}
	
	
	public void ComputeBill(final Check c){
		log("Computing bill");
		c.w.msgHereIsCheck(menu.getPrice(c.choice), c.cust);
		c.s = CheckState.waitingForPayment;
	}
	
	public void CalculateChange(Check c){
		log("Calculating change");
		if(c.cashAmount >= menu.getPrice(c.choice)){
			c.cust.msgHereIsChange(c.cashAmount - menu.getPrice(c.choice));
			checks.remove(c);
		}
		else {
			c.cust.msgNotEnoughCash(menu.getPrice(c.choice) - c.cashAmount);
			checks.remove(c);
		}
		
		if (!onDuty){
			setInactive();
			onDuty = true;
		}
	}
	
	public void PayMarketBill(MarketBill b){
		log("Paying market bill");

		if(cash >= b.billAmount){
			cash -= b.billAmount;
			b.amountPaid = b.billAmount;
		}
		else {
			b.amountPaid = cash;
		}
		
		//new method call
		b.deliveryMan.msgHereIsPayment(b.amountPaid, "EllenRestaurant");
		b.s = MarketBillState.waitingForChange;
	}
	
	public void VerifyChange(MarketBill b){
		log("Verifying market bill change from deliveryMan");

		if (b.amountChange == (b.amountPaid - b.billAmount)){
			//correct change
			log("Equal. Change verified.");
		}
		else if (b.amountChange > (b.amountPaid - b.billAmount)){
			//incorrect change - too much
			log("More change than necessary, but I'll take it! Change verified.");
		}
		else {
			//incorrect change - not enough
			log("Not enough change! I'll deal with you later.");
		}
		
		cash += b.amountChange;
		b.deliveryMan.msgChangeVerified("ellenRestaurant");
		marketBills.remove(b);
	}
	public void AcknowledgeDebt(MarketBill b){
		log("Acknowledging debt of: $" + b.amountOwed);
		b.deliveryMan.msgIOweYou(b.amountOwed, "ellenRestaurant");
		//message the restaurant manager to pay the market somehow?
		marketBills.remove(b);
	}
	

	public class Check {		//made public for unit testing
		Customer cust;
		Waiter w;
		String choice;
		CheckState s;
		double cashAmount;

		Check(String choice, Customer cust, Waiter w){
			this.choice = choice;
			this.cust = cust;			
			this.w = w;
			this.s = CheckState.newCheck;
		}	
		public Customer getCustomer(){
			return cust;
		}
		public CheckState getState(){
			return s;
		}
		public double getAmount(){
			return cashAmount;
		}
		public Waiter getWaiter(){
			return w;
		}
	}
	public class MarketBill {
		//Market1DeliveryManRole deliveryMan;
		DeliveryMan deliveryMan;
		double billAmount;
		double amountPaid;
		double amountChange;
		double amountOwed;
		MarketBillState s;
		Map<String, Integer> itemsBought; 
		
		MarketBill(/*Market1DeliveryManRole d,*/ DeliveryMan d, double amount, Map<String, Integer> inventory, MarketBillState s){
			deliveryMan = d;
			billAmount = amount;
			itemsBought = new TreeMap<String, Integer>(inventory);
			this.s = s;
		}
		public MarketBillState getState(){
			return s;
		}
	}
	
}

