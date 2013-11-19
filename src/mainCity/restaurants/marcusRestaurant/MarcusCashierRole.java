package mainCity.restaurants.marcusRestaurant;

import agent.Agent;
import mainCity.restaurants.marcusRestaurant.interfaces.*;

import java.util.*;

public class MarcusCashierRole extends Agent implements Cashier {
	public List<Bill> bills;
	public List<MarketBill> marketBills;
	private Map<String, Integer> prices;
	private int cash;

	public MarcusCashierRole() {
		super();
		bills =  Collections.synchronizedList(new ArrayList<Bill>());
		marketBills =  Collections.synchronizedList(new ArrayList<MarketBill>());
		prices = Collections.synchronizedMap(new HashMap<String, Integer>());
		
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
	
	public int getCash() {
		return cash;
	}
	// Messages
	public void msgPayingMyDebt(Customer c, int amount) {
		print(c + " has just settled their debt with the restaurant of $" + amount);
		cash += amount;
	}
	
	public void msgComputeBill(Waiter w, String choice, MarcusTable t) {
		print("Received a check request of for table " + t.getTableNumber());
		bills.add(new Bill(w, choice, t.getTableNumber()));
		stateChanged();
	}
	
	public void msgHereIsPayment(Customer c, int amount, int table) {
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

	public void msgHereIsFoodBill(Market m, int amount) {
		print("Received a food bill of $" + amount + " from market");
		marketBills.add(new MarketBill(m, amount));
		stateChanged();
	}
	

	public void msgHereIsMarketBill(Map<String, Integer> inventory, double billAmount, String deliveryPerson) {
		
	}
	
	public void msgHereIsChange(double amount) {
		
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

		return false;
	}

	//Actions
	private void payMarket(MarketBill b) {
		print("Processing the food bill...");
		print("We currently have $" + cash);
		print(b.market + " has billed us $" + b.bill + "\nSending payment now...");
		
		if(b.bill > cash) {
			print("Uh oh! We don't have enough money to pay the market. Sending what we have and an IOU");
			b.market.msgHereIsPayment(this, cash);
			cash = 0;
		}
		else {
			b.market.msgHereIsPayment(this, b.bill);
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
		return "Cashier";
	}
	
	public class MarketBill {
		Market market;
		int bill;
		
		MarketBill(Market m, int amount) {
			market = m;
			bill = amount;
		}
	}
	
	public enum BillState {unpaid, processed, paying};

	public class Bill {
		Waiter waiter;
		public Customer customer;
		String choice;
		int table;
		public int bill;
		int receivedAmount;
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

