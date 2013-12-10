package mainCity.restaurants.jeffersonrestaurant.test.mock;

import java.util.Map;

import mainCity.interfaces.MainCook;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Cook;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Waiter;

public class MockCook extends Mock implements Cook, MainCook {

	public MockCook(String name) {
		super(name);
	}

	public EventLog log;

	
	
	public void msgHereIsYourOrder(Map<String, Integer> inventory) {
		log.add(new LoggedEvent("received msgHereIsYourOrder"));

	}

	@Override
	public void msghereIsAnOrder(int table, String Choice, Waiter w) {
		log.add(new LoggedEvent("received msgHereIsYourOrder"));

	}

	@Override
	public void msgOrderTaken(int t) {
		log.add(new LoggedEvent("received msgOrderTaken"));
	}

	@Override
	public void msgCannotFulfill(String i, Integer q) {
		log.add(new LoggedEvent("received msgCannotFulfill"));

	}

	@Override
	public void msgRestockFulfilled(String i, Integer q) {
		log.add(new LoggedEvent("received msgRestockFulfilled"));

	}

	@Override
	public void msgPartialFulfill(String i, Integer q) {
		log.add(new LoggedEvent("received msgPartialFulfill"));

	}

}
