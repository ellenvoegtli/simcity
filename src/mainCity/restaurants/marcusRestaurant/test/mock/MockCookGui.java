package mainCity.restaurants.marcusRestaurant.test.mock;

import mainCity.restaurants.marcusRestaurant.interfaces.CookGuiInterface;

public class MockCookGui extends Mock implements CookGuiInterface {
	private EventLog log;

	public MockCookGui(String name) {
		super(name);
		log = new EventLog();
		
	}


	public EventLog getLog() {
		return log;
	}

	public void DoGoToCounter() {
		log.add(new LoggedEvent("Gui going to counter"));
	}

	public void DoGoToGrill(int grill) {
		log.add(new LoggedEvent("Gui going to grill"));
	}

	public void DoClearGrill(int grill) {
		log.add(new LoggedEvent("Gui clearing grill"));	
	}

	public void DoLeaveRestaurant() {
		log.add(new LoggedEvent("Gui leaving restaurant"));
	}


	public void setPresent(boolean t){}
}
