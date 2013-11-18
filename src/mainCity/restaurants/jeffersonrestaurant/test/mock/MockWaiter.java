package mainCity.restaurants.jeffersonrestaurant.test.mock;

import mainCity.restaurants.jeffersonrestaurant.HostRole;
import mainCity.restaurants.jeffersonrestaurant.WaiterRole;

import mainCity.restaurants.jeffersonrestaurant.interfaces.Cashier;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Customer;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Waiter;
import mainCity.restaurants.jeffersonrestaurant.test.mock.EventLog;

public class MockWaiter extends Mock implements Waiter {

	public MockWaiter(String name) {
		super(name);
	}

	public Cashier cashier;
	
	public HostRole host;
	
	public EventLog log;



	
	public void msgSeatAtTable(Customer c, int table) {
		log.add(new LoggedEvent("Told to seat at table"));
		
	}

	
	public void msgHereIsMyChoice(Customer cust, String choice) {
		log.add(new LoggedEvent("Recieved choice from a customer"));
		
	}

	
	public void msgLeavingTable(Customer cust) {
		log.add(new LoggedEvent("Customer leaving table"));
		
	}

	
	public void msgAtTable() {
		log.add(new LoggedEvent("Now at table"));
		
	}

	
	public void msgAtHome() {
		log.add(new LoggedEvent("Now at home"));
		
	}

	
	public void msgAtPlating() {
		log.add(new LoggedEvent("Now at plating"));
		
	}

	
	public void msgAtCook() {
		log.add(new LoggedEvent("Now at cook"));
		
	}

	
	public void msgImReadyToOrder(Customer cust) {
		log.add(new LoggedEvent("Told by customer ready to order"));
		
	}

	
	public void msgOrderIsReady(int t) {
		log.add(new LoggedEvent("Told by cook order is ready"));
		
	}

	
	public void msgOutOfChoice(int t) {
		log.add(new LoggedEvent("Told by cook that choice is out"));
		
	}

	
	public void msgWantCheck(Customer c) {
		log.add(new LoggedEvent("Customer requested check"));
		
	}

	
	public void msgCheckPrinted(int t) {
		log.add(new LoggedEvent("Told by cashier check is printed"));
		
	}

	
	public void msgHereIsPayment(Customer c, double money) {
		log.add(new LoggedEvent("Recieved payment"));
		
	}

	
	public void msgPaymentComplete(int t) {
		log.add(new LoggedEvent("Payment is complete"));
		
	}


	@Override
	public void msgButtonAskBreak() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void msgYouCanBreak() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
