package role.marcusRestaurant;

import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.DeliveryMan;
import mainCity.interfaces.WorkerRole;
import mainCity.restaurants.marcusRestaurant.MarcusTable;
import mainCity.restaurants.marcusRestaurant.interfaces.*;

import java.util.*;

import role.Role;
import role.market.MarketDeliveryManRole;

public class MarcusCashierRole extends Role implements Cashier, WorkerRole {
	private String name;
	private Host host;
	public List<Bill> bills;
	public List<MarketBill> marketBills;
	private Map<String, Integer> prices;
	private double cash;
	private boolean onDuty;
	private boolean entered;
	
	public MarcusCashierRole(PersonAgent p, String n) {
		super(p);
		this.name = n;
		bills =  Collections.synchronizedList(new ArrayList<Bill>());
		marketBills =  Collections.synchronizedList(new ArrayList<MarketBill>());
		prices = Collections.synchronizedMap(new HashMap<String, Integer>());
		onDuty = true;
		entered = true;
		
		synchronized(prices) {
			prices.put("Swiss", 9);
			prices.put("American", 14);
			prices.put("Cheddar", 6);
			prices.put("Provolone", 12);
		}
		
		//cash = 1675;
	}

	public int getBills() {
		return bills.size();
	}
	
	public List<MarketBill> getMarketBills() {
		return marketBills;
	}
	
	public void setCash(int c) {
		cash = c;
	}
	
	public void setHost(Host h) {
		host = h;
	}
	
	public double getCash() {
		return cash;
	}
	
	public void deductCash(double min) {
		cash -= min;
		output("Depositing $" + cash + " from bank");
		ContactList.getInstance().getBank().directDeposit("marcusrestaurant", cash);
		cash = 0;
	}
	
	// Messages
	public void msgPayingMyDebt(Customer c, double amount) {
		output(c + " has just settled their debt with the restaurant of $" + amount);
		cash += amount;
	}
	
	public void msgComputeBill(Waiter w, String choice, MarcusTable t) {
		output("Received a check request of for table " + t.getTableNumber());
		bills.add(new Bill(w, choice, t.getTableNumber()));
		stateChanged();
	}
	
	public void msgHereIsPayment(Customer c, double amount, int table) {
		output("Received payment from " + c + " of $" + amount + " for table " + table);
		
		synchronized(bills) {
			for(Bill b : bills) {
				if(b.table == table) {
					b.state = BillState.paying;
					b.customer = c;
					b.receivedAmount = amount;
					this.cash += amount;
					stateChanged();
					return;
				}
			}
		}
	}
	
	public void msgGoOffDuty(double amount) {
		addToCash(amount);
		onDuty = false;
		stateChanged();
	}

	public void msgHereIsMarketBill(Map<String, Integer> inventory, double billAmount, DeliveryMan deliveryPerson) {
		output("Received a food bill of $" + billAmount + " from market");
		marketBills.add(new MarketBill(inventory, billAmount, deliveryPerson));
		stateChanged();
	}
	
	public void msgNotEnoughMoney(double amountOwed, double amountPaid) {		
		output("I paid $" + amountPaid + " to the delivery man. I still owe $" + amountOwed);
	}
	
	public void msgHereIsChange(double amount, DeliveryMan deliveryPerson) {
		output("Received change of $" + amount);
		cash += amount;
		deliveryPerson.msgChangeVerified("marcusRestaurant");
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if(entered) {
			output("Withdrawing $1200 from bank");
			ContactList.getInstance().getBank().directWithdraw("marcusrestaurant", 1200);
			cash = 1200;
			entered = false;
		}
		
		synchronized(marketBills) {
			if(!marketBills.isEmpty()) {
				payMarket(marketBills.get(0));
				return true;
			}
		}
		
		synchronized(bills) {
			if(!bills.isEmpty()) {
				for(Bill b : bills) {
					if(b.state == BillState.paying) {
						handlePayment(b);
						return true;
					}
				}
				
				for(Bill b : bills) {
					if(b.state == BillState.unpaid) {
						computeBill(b);
						return true;
					}
				}
			}
		}
		
		if(bills.isEmpty() && !onDuty) {
			setInactive();
			onDuty = true;
			entered = true;
		}
		
		return false;
	}

	//Actions
	private void payMarket(MarketBill b) {
		output("Processing the food bill...");
		output("We currently have $" + cash);
		output(b.deliveryPerson + " has billed us $" + b.bill + "\nSending payment now...");
		
		if(b.bill > cash) {
			output("Uh oh! We don't have enough money to pay the delivery person. Sending what we have and an IOU");
			b.deliveryPerson.msgHereIsPayment(cash, "marcusRestaurant");
			cash = 0;
		}
		else {
			b.deliveryPerson.msgHereIsPayment(b.bill, "marcusRestaurant");
			cash -= b.bill;
		}
		
		output("We now have $" + cash);
		marketBills.remove(b);
	}
	
	private void handlePayment(Bill b) {
		if(b.receivedAmount < b.bill) {
			output(b.customer + " doesn't have enough money. I'll let you pay next time you arrive..");
			b.customer.msgDebtOwed(b.receivedAmount-b.bill);
			bills.remove(b);
			return;
		}

		b.customer.msgHereIsChange((b.receivedAmount - b.bill));
		bills.remove(b);
		
		stateChanged();
	}
	
	private void computeBill(Bill b) {
		output("Computing bill...");
		
		synchronized(prices) {
			b.bill = prices.get(b.choice);
			b.waiter.msgHereIsCheck(b.bill, b.table);
			b.state = BillState.processed;
		}
		//bills.remove(b);
	}
	
	public String toString() {
		return "Cashier " + name;
	}
	
	public class MarketBill {
		Map<String, Integer> inventory;
		DeliveryMan deliveryPerson;
		double bill;
		
		MarketBill(Map<String, Integer> i, double amount, DeliveryMan p) {
			inventory = i;
			bill = amount;
			deliveryPerson = p;
		}
	}
	
	public enum BillState {unpaid, processed, paying};

	public class Bill {
		Waiter waiter;
		public Customer customer;
		String choice;
		int table;
		public double bill;
		double receivedAmount;
		public BillState state = BillState.unpaid;

		Bill(Waiter w, String c, int t) {
			waiter = w;
			choice = c;
			table = t;
			bill = 0;
			receivedAmount = 0;
			customer = null;
		}
	}
	
	private void output(String input) {
		AlertLog.getInstance().logMessage(AlertTag.MARCUS_RESTAURANT, this.getName(), input);
		AlertLog.getInstance().logMessage(AlertTag.MARCUS_CASHIER, this.getName(), input);
	}
}

