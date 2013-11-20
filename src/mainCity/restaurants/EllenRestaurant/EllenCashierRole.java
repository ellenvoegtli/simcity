
package mainCity.restaurants.EllenRestaurant;

import agent.Agent;

import java.util.*;

import mainCity.restaurants.EllenRestaurant.interfaces.*;
import mainCity.market.*;


 // Restaurant Cook Agent

public class EllenCashierRole extends Agent implements Cashier{	
	static final int NTABLES = 3;//a global for the number of tables.

	private String name;
	private int availableMoney = 500;		//modify
	private int amountToPayMarket = 0;
	Timer timer = new Timer();
	
	public List<Check> checks = Collections.synchronizedList(new ArrayList<Check>());	//from waiters
	private List<Waiter> waiters = Collections.synchronizedList(new ArrayList<Waiter>());
	public List<MarketBill> marketBills = Collections.synchronizedList(new ArrayList<MarketBill>());
	//private List<Customer> customers = Collections.synchronizedList(new ArrayList<Customer>());
	
	Map<String, Integer> prices = new TreeMap<String, Integer>();
	
	public enum CheckState {newCheck, computing, waitingForPayment, calculatingChange, done};
	public enum MarketBillState {newBill, computing, waitingForChange, receivedChange, done};	//is this ok???
	
	
	boolean opened = true;

	
	public EllenCashierRole(String name) {
		super();

		this.name = name;
		

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
	
	public int getAmountToPayMarket(){
		return amountToPayMarket;
	}
	

	public List getChecks(){
		return checks;
	}
	public List getMarketBills(){
		return marketBills;
	}
	
	// Messages
	
	public void msgComputeBill(String choice, Customer cust, Waiter w){
		Check c = new Check(choice, cust, w);
		checks.add(c);
		c.s = CheckState.computing;
		
		print(w.getName() + ", received msgComputeBill for " + cust.getName() + ", order: " + choice);
		stateChanged();
	}
	
	public void msgHereIsPayment(int checkAmount, int cashAmount, Customer cust){
		print("Received msgHereIsPayment: got $" + cashAmount + " for check: $" + checkAmount);
		
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
	

	//not the correct message anymore; see MainCashier
	public void msgHereIsMarketBill(int amount, Market market){		
		print("Received msgHereIsMarketBill from " + market.getName() + " for $" + amount);
		marketBills.add(new MarketBill(market, amount, MarketBillState.computing));
		stateChanged();
	}

	//new message
	public void msgHereIsMarketBill(Map<String, Integer>inventory, double billAmount, MarketDeliveryManRole d){
		print("Received msgHereIsMarketBill from " + d.getName() + " for $" + billAmount);
		marketBills.add(new MarketBill(d, billAmount, inventory, MarketBillState.computing));
		stateChanged();
	}
	
	public void msgHereIsChange(double amount, MarketDeliveryManRole deliveryPerson){
		print("Received msgHereIsChange");
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
		

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}
	

	// Actions
	
	public void ComputeBill(final Check c){
		print("Computing bill");
		c.w.msgHereIsCheck(prices.get(c.choice), c.cust);
		
		c.s = CheckState.waitingForPayment;

	}
	
	public void CalculateChange(Check c){
		print("Calculating change");
		
		//check to make sure payment is large enough
		if (c.cashAmount >= prices.get(c.choice)){
			c.cust.msgHereIsChange((c.cashAmount - prices.get(c.choice)));
			checks.remove(c);
		}
		else {
			c.cust.msgNotEnoughCash((prices.get(c.choice) - c.cashAmount));
			checks.remove(c);
		}
	}
	
	public void PayMarketBill(MarketBill b){
		print("Paying market bill");
		
		
		//old implementation
		amountToPayMarket = b.checkAmount;	//for testing purposes
		//b.m.msgHereIsPayment(amountToPayMarket);
		
		if(availableMoney >= b.checkAmount){
			availableMoney -= b.checkAmount;
			b.amountPaid = b.checkAmount;
		}
		//else
			//non-norm??*****
		
		//new method call
		b.deliveryMan.msgHereIsPayment(b.checkAmount);
		b.s = MarketBillState.waitingForChange;
	}
	
	public void VerifyChange(MarketBill b){
		print("Verifying market bill change from deliveryMan");
		
		if (b.amountChange == (b.amountPaid - b.billAmount)){
			//correct change
			b.s = MarketBillState.done;		//unnecessary
			marketBills.remove(b);
		}
		//else?******
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
		Market m;
		//String deliveryPerson;
		MarketDeliveryManRole deliveryMan;
		int checkAmount;	//irrelevant for new implementation; kept to keep tests compiling
		double billAmount;
		double amountPaid;
		double amountChange;
		MarketBillState s;
		
		Map<String, Integer> itemsBought; 
		
		//old constructor
		MarketBill(Market market, int amount, MarketBillState st){
			m = market;
			checkAmount = amount;
			s = st;
		}

		MarketBill(MarketDeliveryManRole d, double amount, Map<String, Integer> inventory, MarketBillState s){
			deliveryMan = d;
			billAmount = amount;
			itemsBought = new TreeMap<String, Integer>(inventory);
			this.s = s;
		}
		

		public Market getMarket(){
			return m;
		}
		public MarketBillState getState(){
			return s;
		}
		public int getCheckAmount(){
			return checkAmount;
		}
	}
	
}

