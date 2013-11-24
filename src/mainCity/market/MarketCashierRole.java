
package mainCity.market;

import agent.Agent;

import java.util.*;
import role.Role;
import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;


 // Restaurant Cook Agent

public class MarketCashierRole extends Role{	
	private String name;
	MarketGreeterRole greeter;
	private double availableMoney = 0;		//we don't actually need to start with any money (don't need to buy anything)
	Timer timer = new Timer();
	private MarketMenu marketMenu = new MarketMenu();
	
	public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());	//from waiters
	private List<MarketEmployeeRole> employees = Collections.synchronizedList(new ArrayList<MarketEmployeeRole>());
		
	public enum BillState {computing, waitingForPayment, recomputingBill, calculatingChange, oweMoney, paid};
	
	//constructor
	public MarketCashierRole(PersonAgent p, String name) {
		super(p);
		this.name = name;

	}
	public void addWaiter(MarketEmployeeRole w){	//hack
		employees.add(w);
	}
	public void setGreeter(MarketGreeterRole g){
		greeter = g;
	}
	public String getName() {
		return name;
	}
	public List getBills(){
		return bills;
	}

	// Messages
	
	//customer
	public void msgComputeBill(Map<String, Integer> inventory, MarketCustomerRole c, MarketEmployeeRole e){
		bills.add(new Bill(inventory, c, BillState.computing, e));
		
		//print(e.getName() + ", received msgComputeBill for " + c.getName());
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), e.getName() + ", received msgComputeBill for " + c.getName());
		stateChanged();
	}
	
	//business
	public void msgComputeBill(Map<String, Integer> inventory, String name, MarketEmployeeRole e){
		//print("Received msgComputeBill for " + name);
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msgComputeBill for " + name);
		
		bills.add(new Bill(inventory, name, BillState.computing, e));
		stateChanged();
	}
	
	public void msgHereIsPayment(double amount, MarketCustomerRole cust){
		//print("Received msgHereIsPayment: got $" + amount);
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msgHereIsPayment: got $" + amount);

		Bill b = null;
		synchronized(bills){
			for (Bill thisB : bills){	
				if (thisB.c.equals(cust)){
					b = thisB;
					break;
				}
			}
		}
		b.amountPaid = amount;
		b.s = BillState.calculatingChange;
		stateChanged();
	}
	
	public void msgPleaseRecalculateBill(MarketCustomerRole cust){
		AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msgPleaseRecalculateBill from " + cust.getName());

		Bill b = null;
		synchronized(bills){
			for (Bill thisB : bills){	
				if (thisB.c.equals(cust)){
					b = thisB;
					break;
				}
			}
		}
		b.s = BillState.recomputingBill;
		stateChanged();
	}
	
	public void msgChangeVerified(MarketCustomerRole cust){
		AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msgChangeVerified from " + cust.getName());

		Bill b = null;
		synchronized(bills){
			for (Bill thisB : bills){	
				if (thisB.c.equals(cust)){
					b = thisB;
					break;
				}
			}
		}
		//NOW we can add the money they finally, for sure paid and are not taking back
		availableMoney += b.amountPaid;
		bills.remove(b);
	}
	
	public void msgHereIsMoneyIOwe(MarketCustomerRole cust, double amount){
		AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Received msgHereIsMoneyIOwe from " + cust.getName() + ": $" + amount);

		Bill b = null;
		synchronized(bills){
			for (Bill thisB : bills){	
				if (thisB.c.equals(cust)){
					b = thisB;
					break;
				}
			}
		}
		availableMoney += amount;
	}
	

	 // Scheduler.  Determine what action is called for, and do it.
	 
	public boolean pickAndExecuteAnAction() {
		
		//Customer checks
		synchronized(bills){
			for (Bill b: bills) {
				if (b.s == BillState.calculatingChange){
					CalculateChange(b);
					return true;
				}
			}
		}
		synchronized(bills){
			for (Bill b: bills) {
				if (b.s == BillState.computing){
					ComputeBill(b);
					return true;
				}
			}
		}
		synchronized(bills){
			for (Bill b: bills) {
				if (b.s == BillState.recomputingBill){
					RecomputeBill(b);
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
	
	public void ComputeBill(final Bill b){
		//print("Computing bill");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Computing bill");

		for (Map.Entry<String, Integer> entry : b.itemsBought.entrySet()){
			b.amountCharged += marketMenu.getPrice(entry.getKey()) * entry.getValue();
		}
		if (b.c == null)
			b.e.msgHereIsBill(b.restaurantName, b.amountCharged);
		else
			b.e.msgHereIsBill(b.c, b.amountCharged);
		b.s = BillState.waitingForPayment;

	}
	
	public void CalculateChange(Bill b){
		//print("Calculating change");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Calculating change");

		//check to make sure payment is large enough
		if (b.amountPaid >= b.amountCharged){
			b.c.msgHereIsYourChange((b.amountPaid - b.amountCharged), b.amountCharged);
			b.s = BillState.paid;
		}
		else {		//if they didn't pay enough
			b.c.msgNotEnoughCash((b.amountCharged - b.amountPaid));
			b.s = BillState.oweMoney;
		}
		
		//delete Bill?
	}

	public void RecomputeBill(final Bill b){
		//print("Recomputing bill");
        AlertLog.getInstance().logMessage(AlertTag.MARKET, this.getName(), "Recomputing bill");

		for (Map.Entry<String, Integer> entry : b.itemsBought.entrySet()){
			b.newAmountCharged += marketMenu.getPrice(entry.getKey()) * entry.getValue();
		}
		if (b.newAmountCharged < b.amountCharged){	//if they over-billed the first time, fix the bill to be correct
			b.amountCharged = b.newAmountCharged;
			
			/*if (b.c == null)
				b.e.msgHereIsBill(b.restaurantName, b.amountCharged);
			else*/ if (b.c != null)
				b.c.msgHereIsBill(b.amountCharged);
			
		} else if (b.newAmountCharged > b.amountCharged) {//else if they under-billed the first time, send them the first bill charge (they messed up, so they'll lose that money)
			//same as above
			/*if (b.c == null)
				b.e.msgHereIsBill(b.restaurantName, b.amountCharged);
			else*/
				b.e.msgHereIsBill(b.c, b.amountCharged);
			
		} else if (b.newAmountCharged == b.amountCharged){		//if it's the same, send them a final non-negotiable bill charge
			/*if (b.c == null)
				b.e.msgHereIsFinalBill(b.restaurantName, b.amountCharged);
			else*/ if (b.c != null)
				b.c.msgHereIsFinalBill(b.amountCharged);
		}
		b.s = BillState.waitingForPayment;

	}

	private class Bill {
		Map<String, Integer> itemsBought = new TreeMap<String, Integer>();
		double amountCharged;
		double newAmountCharged; 	//if customer asks cashier to recalculate
		double amountPaid;
		double amountOwed;
		MarketCustomerRole c;
		String restaurantName;
		BillState s;
		MarketEmployeeRole e;
		
		Bill(Map<String, Integer> inventory, MarketCustomerRole cust, BillState state, MarketEmployeeRole em){
			itemsBought = inventory;
			c = cust;
			s = state;
			e = em;
		}
		Bill(Map<String, Integer> inventory, String name, BillState state, MarketEmployeeRole em){
			itemsBought = inventory;
			restaurantName = name;
			s = state;
			e = em;
		}
	}

	
}

