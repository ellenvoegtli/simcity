package mainCity.restaurants.marcusRestaurant.test.mock;

import role.marcusRestaurant.MarcusCookRole;
import mainCity.restaurants.marcusRestaurant.MarcusTable;
import mainCity.restaurants.marcusRestaurant.interfaces.*;

public class MockHost extends Mock implements Host {
	EventLog log;

	public MockHost(String name) {
		super(name);
		log = new EventLog();
	}


	public void msgIWantToEat(Customer cust) {
		log.add(new LoggedEvent("Received a message to eat from hungry customer " + cust));		
	}

	public void msgIWillWait(Customer c) {
		log.add(new LoggedEvent(c + " will wait to be seated"));		
	}

	public void msgTableIsClear(MarcusTable t) {
		log.add(new LoggedEvent(t.getTableNumber() + " is now clear"));		
	}

	public void msgWantToGoOnBreak(Waiter w) {
		log.add(new LoggedEvent(w + " just asked to go on break"));		
	}

	public void msgBackOnDuty(Waiter w) {
		log.add(new LoggedEvent(w + " is now on duty"));		
	}

	public MarcusCookRole getCook() {
		return null;
	}

	public EventLog getLog() {
		return log;
	}
}
