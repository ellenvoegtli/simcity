
package mainCity.market;

import agent.Agent;

import java.util.*;


 // Restaurant Cook Agent

public class MarketCashierRole extends Agent{	
	private String name;
	//private int availableMoney = 500;
	Timer timer = new Timer();
	private MarketMenu marketMenu;
	
	public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());	//from waiters
	private List<MarketEmployeeRole> employees = Collections.synchronizedList(new ArrayList<MarketEmployeeRole>());
		
	public enum BillState {computing, waitingForPayment, calculatingChange, paid};
	
	
	//constructor
	public MarketCashierRole(String name) {
		super();

		this.name = name;

	}

	public void addWaiter(MarketEmployeeRole w){	//hack
		employees.add(w);
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
		
		print(e.getName() + ", received msgComputeBill for " + c.getName());
		stateChanged();
	}
	
	//business
	public void msgComputeBill(Map<String, Integer> inventory, String name, MarketEmployeeRole e){
		print("Received msgComputeBill");
		
		bills.add(new Bill(inventory, name, BillState.computing, e));
		stateChanged();
	}
	
	public void msgHereIsPayment(double amount, MarketCustomerRole cust){
		print("Received msgHereIsPayment: got $" + amount);
		
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


		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}
	

	// Actions
	
	public void ComputeBill(final Bill b){
		print("Computing bill");
		
		for (Map.Entry<String, Integer> entry : b.itemsBought.entrySet()){
			b.amountCharged += marketMenu.getPrice(entry.getKey()) * entry.getValue();
		}
		b.e.msgHereIsBill(b.c, b.amountCharged);
		b.s = BillState.waitingForPayment;

	}
	
	public void CalculateChange(Bill b){
		print("Calculating change");
		
		//check to make sure payment is large enough
		if (b.amountPaid >= b.amountCharged)
			b.c.msgHereIsYourChange((b.amountPaid - b.amountCharged), b.amountCharged);
		//else?
			//you still owe ...
		
		//delete Bill?
	}

	

	private class Bill {
		//List<OrderItem> itemsBought;
		Map<String, Integer> itemsBought = new TreeMap<String, Integer>();
		double amountCharged;
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

