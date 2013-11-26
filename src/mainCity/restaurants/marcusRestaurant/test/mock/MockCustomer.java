package mainCity.restaurants.marcusRestaurant.test.mock;

import mainCity.restaurants.marcusRestaurant.interfaces.*;
import mainCity.restaurants.marcusRestaurant.test.mock.EventLog;
import mainCity.restaurants.marcusRestaurant.test.mock.LoggedEvent;
import mainCity.restaurants.marcusRestaurant.MarcusMenu;

public class MockCustomer extends Mock implements Customer {
	public Waiter waiter;
	public Cashier cashier;
	String name;
	EventLog log;
	
	public MockCustomer(String name) {
		super(name);
		this.name = name;
		log = new EventLog();
	}
	
	public String toString() {
		return name;
	}
	
	public EventLog getLog() {
		return log;
	}
	
	public void msgWantToWait() {
		log.add(new LoggedEvent("Host asked me if I want to wait"));
	}
	
	public void msgFollowMeToTable(int table, MarcusMenu m, Waiter w) {
		log.add(new LoggedEvent("Following waiter to table " + table));
	}

	public void msgWhatWouldYouLike() {
		log.add(new LoggedEvent("Waiter just asked what I want to order"));

	}
	
	public void msgPleaseReorder(MarcusMenu m) {
		log.add(new LoggedEvent("Out of food, need to reorder"));

	}
	
	public void msgHereIsYourOrder(String choice) {
		log.add(new LoggedEvent("Received my order of " + choice));
	}

	public void msgHereIsCheck() {
		log.add(new LoggedEvent("Received the check"));
	}
	
	public void msgDebtOwed(double remaining_cost) {
		log.add(new LoggedEvent("Have a debt to cashier. Debt = "+ remaining_cost));
	}
	
	public void msgHereIsChange(double total) {
		log.add(new LoggedEvent("Received change from cashier. Change = "+ total));
	}
	
	public int getXPos() {
		log.add(new LoggedEvent("Accessing x position"));
		return 0;
	}
	
	public int getYPos() {
		log.add(new LoggedEvent("Accessing y position"));
		return 0;
	}
}
