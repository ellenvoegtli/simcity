package mainCity.restaurants.marcusRestaurant.test.mock;

import mainCity.restaurants.marcusRestaurant.MarcusCookRole;
import mainCity.restaurants.marcusRestaurant.interfaces.*;
import mainCity.restaurants.marcusRestaurant.test.mock.EventLog;
import mainCity.restaurants.marcusRestaurant.test.mock.LoggedEvent;

public class MockMarket extends Mock implements Market {
	public Cashier cashier;
	public double cash;
	EventLog log;
	
	public MockMarket(String name) {
		super(name);
		log = new EventLog();
	}
	
	public EventLog getLog() {
		return log;
	}
	
	public void msgRequestForFood(MarcusCookRole c, String choice, int quantity) {
		log.add(new LoggedEvent("Cook " + c + " just requested for " + quantity + " of choice"));
	}
	
	public void msgHereIsPayment(Cashier c, double amount) {
		cash += amount;
		log.add(new LoggedEvent("Just received $" + amount + " from " + c));
	}
}
