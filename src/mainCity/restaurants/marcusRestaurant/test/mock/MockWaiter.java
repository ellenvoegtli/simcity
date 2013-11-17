package mainCity.restaurants.marcusRestaurant.test.mock;

import mainCity.restaurants.marcusRestaurant.MarcusTable;
import mainCity.restaurants.marcusRestaurant.interfaces.*;
import mainCity.restaurants.marcusRestaurant.test.mock.EventLog;
import mainCity.restaurants.marcusRestaurant.test.mock.LoggedEvent;

public class MockWaiter extends Mock implements Waiter {
	public Cashier cashier;
	public Waiter waiter;
	private EventLog log;
	
	public MockWaiter(String name) {
		super(name);
		log = new EventLog();
	}
	
	public void msgRequestBreak() {
		getLog().add(new LoggedEvent("Requesting break"));
	}
	
	public void msgBreakReply(boolean r) {
		getLog().add(new LoggedEvent("Host said " + r + " to my request"));
	}
	
	public void msgSeatAtTable(Customer cust, MarcusTable t) {
		getLog().add(new LoggedEvent("Host wants me to seat " + cust + " at table " + t));
	}

	public void msgImReadyToOrder(Customer customer) {
		getLog().add(new LoggedEvent(customer + " is ready to order"));
	}
	
	public void msgHereIsMyChoice(Customer customer, String choice) {
		getLog().add(new LoggedEvent(customer + " wants to eat " + choice));
	}

	public void msgOutOfFood(int table, String choice) {
		getLog().add(new LoggedEvent("Table " + table + " needs to reorder, out of" + choice));
	}
	
	public void msgOrderIsReady(int table, String choice) {
		getLog().add(new LoggedEvent(choice + " is ready for table " + table));
	}

	public void msgReadyForCheck(Customer cust) {
		getLog().add(new LoggedEvent(cust + " is ready for check"));
	}
	
	public void msgHereIsCheck(int amount, int table) {
		getLog().add(new LoggedEvent("Check received for table " + table + " of $" + amount));
	}
	
	public void msgLeavingTable(Customer cust) {
		getLog().add(new LoggedEvent(cust + " is now leaving the table"));
	}

	public EventLog getLog() {
		return log;
	}

	public void setLog(EventLog log) {
		this.log = log;
	}

	public int getCustomerCount() {
		return 0;
	}
}
