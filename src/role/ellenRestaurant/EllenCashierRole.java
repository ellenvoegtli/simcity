
package role.ellenRestaurant;

import java.util.*;
import role.market.*;
import mainCity.PersonAgent;
import mainCity.restaurants.EllenRestaurant.interfaces.*;
import mainCity.gui.trace.*;
import role.Role;


 // Restaurant Cook Agent

public class EllenCashierRole extends Role implements Cashier {	
	private String name;
	private double cash = 500;		//modify
	Timer timer = new Timer();
	
	public List<Check> checks = Collections.synchronizedList(new ArrayList<Check>());	//from waiters
	private List<Waiter> waiters = Collections.synchronizedList(new ArrayList<Waiter>());
	public List<MarketBill> marketBills = Collections.synchronizedList(new ArrayList<MarketBill>());
	Map<String, Integer> prices = new TreeMap<String, Integer>();
	
	public enum CheckState {newCheck, computing, waitingForPayment, calculatingChange, done};
	public enum MarketBillState {newBill, computing, waitingForChange, receivedChange, oweMoney, done};	//is this ok???
	
	private boolean onDuty;
	private EllenHostRole host;

	
	public EllenCashierRole(PersonAgent p, String name) {
		super(p);
		this.name = name;
		onDuty = true;

		//initialize prices map -- or should this be from menu?? // ALSO IMPLEMENTED IN MENU
        prices.put("steak", 30);	//type, cookingTime, amount
        prices.put("pizza", 10);
        prices.put("pasta", 20);
        prices.put("soup", 5);

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
	public void msgHereIsPayment(int checkAmount, int cashAmount, Customer cust){
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
	public void msgHereIsMarketBill(Map<String, Integer>inventory, double billAmount, MarketDeliveryManRole d){
		log("Received msgHereIsMarketBill from " + d.getName() + " for $" + billAmount);
		marketBills.add(new MarketBill(d, billAmount, inventory, MarketBillState.computing));
		stateChanged();
	}
	public void msgHereIsChange(double amount, MarketDeliveryManRole deliveryPerson){
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
		b.amountOwed = Math.round(amountOwed*100.0)/100.0;
		b.s = MarketBillState.oweMoney;
		stateChanged();
	}
	
	public void msgGoOffDuty(double amount){
		addToCash(amount);
		onDuty = false;
		stateChanged();
	}
	

	 // Scheduler.  Determine what action is called for, and do it.
	 
	public boolean pickAndExecuteAnAction() {
		
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
		}

		return false;
	}
	

	// Actions
	
	public void ComputeBill(final Check c){
		log("Computing bill");
		c.w.msgHereIsCheck(prices.get(c.choice), c.cust);
		c.s = CheckState.waitingForPayment;
	}
	
	public void CalculateChange(Check c){
		log("Calculating change");
		if (c.cashAmount >= prices.get(c.choice)){
			c.cust.msgHereIsChange((c.cashAmount - prices.get(c.choice)));
			checks.remove(c);
		}
		else {
			c.cust.msgNotEnoughCash((prices.get(c.choice) - c.cashAmount));
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
			cash += b.amountChange;
			b.deliveryMan.msgChangeVerified("ellenRestaurant");
			b.s = MarketBillState.done;		//unnecessary
			marketBills.remove(b);
		}
		
		//else?******
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
		
		int cashAmount;
		
		
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
		public int getAmount(){
			return cashAmount;
		}
		public Waiter getWaiter(){
			return w;
		}
	}
	public class MarketBill {
		MarketDeliveryManRole deliveryMan;
		double billAmount;
		double amountPaid;
		double amountChange;
		double amountOwed;
		MarketBillState s;
		Map<String, Integer> itemsBought; 
		
		MarketBill(MarketDeliveryManRole d, double amount, Map<String, Integer> inventory, MarketBillState s){
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

