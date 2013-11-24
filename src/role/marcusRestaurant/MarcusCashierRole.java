package role.marcusRestaurant;

import mainCity.PersonAgent;
import mainCity.restaurants.marcusRestaurant.MarcusTable;
import mainCity.restaurants.marcusRestaurant.interfaces.*;

import java.util.*;

import role.Role;
import role.WorkerRole;
import role.marcusRestaurant.MarcusCustomerRole.AgentEvent;
import role.market.MarketDeliveryManRole;

public class MarcusCashierRole extends Role implements Cashier, WorkerRole {
	private String name;
	private MarcusHostRole host;
	public List<Bill> bills;
	public List<MarketBill> marketBills;
	private Map<String, Integer> prices;
	private double cash;
	private boolean onDuty;

	public MarcusCashierRole(PersonAgent p, String n) {
		super(p);
		this.name = n;
		bills =  Collections.synchronizedList(new ArrayList<Bill>());
		marketBills =  Collections.synchronizedList(new ArrayList<MarketBill>());
		prices = Collections.synchronizedMap(new HashMap<String, Integer>());
		onDuty = true;
		
		synchronized(prices) {
			prices.put("Steak", 16);
			prices.put("Chicken", 11);
			prices.put("Salad", 6);
			prices.put("Pizza", 9);
		}
		cash = 300;
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
	
	public void setHost(MarcusHostRole h) {
		host = h;
	}
	
	public double getCash() {
		return cash;
	}
	// Messages
	public void msgPayingMyDebt(Customer c, double amount) {
		print(c + " has just settled their debt with the restaurant of $" + amount);
		cash += amount;
	}
	
	public void msgComputeBill(Waiter w, String choice, MarcusTable t) {
		print("Received a check request of for table " + t.getTableNumber());
		bills.add(new Bill(w, choice, t.getTableNumber()));
		stateChanged();
	}
	
	public void msgHereIsPayment(Customer c, double amount, int table) {
		print("Received payment from " + c + " of $" + amount + " for table " + table);
		
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
	
	public void msgGoOffDuty() {
		onDuty = false;
		stateChanged();
	}
	
/*
	public void msgHereIsFoodBill(Market m, int amount) {
		print("Received a food bill of $" + amount + " from market");
		marketBills.add(new MarketBill(m, amount));
		stateChanged();
	}
*/

	public void msgHereIsMarketBill(Map<String, Integer> inventory, double billAmount, MarketDeliveryManRole deliveryPerson) {
		print("Received a food bill of $" + billAmount + " from market");
		marketBills.add(new MarketBill(inventory, billAmount, deliveryPerson));
		stateChanged();
	}
	
	public void msgHereIsChange(double amount, MarketDeliveryManRole deliveryPerson) {
		print("Received change of $" + amount);
		cash += amount;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
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

		if(!onDuty) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					setInactive();
					onDuty = true;
				}
			},
			10000);
		}
		
		return false;
	}

	//Actions
	private void payMarket(MarketBill b) {
		print("Processing the food bill...");
		print("We currently have $" + cash);
		print(b.deliveryPerson + " has billed us $" + b.bill + "\nSending payment now...");
		
		if(b.bill > cash) {
			print("Uh oh! We don't have enough money to pay the delivery person. Sending what we have and an IOU");
			//b.market.msgHereIsPayment(this, cash);
			cash = 0;
		}
		else {
			b.deliveryPerson.msgHereIsPayment(b.bill);
			cash -= b.bill;
		}
		
		print("We now have $" + cash);
		marketBills.remove(b);
	}
	
	private void handlePayment(Bill b) {
		if(b.receivedAmount < b.bill) {
			print(b.customer + " doesn't have enough money. I'll let you pay next time you arrive..");
			b.customer.msgDebtOwed(b.receivedAmount-b.bill);
			bills.remove(b);
			return;
		}

		b.customer.msgHereIsChange((b.receivedAmount - b.bill));
		bills.remove(b);
	}
	
	private void computeBill(Bill b) {
		print("Computing bill...");
		
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
		MarketDeliveryManRole deliveryPerson;
		double bill;
		
		MarketBill(Map<String, Integer> i, double amount, MarketDeliveryManRole p) {
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
}

