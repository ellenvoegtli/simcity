package mainCity.restaurants.marcusRestaurant.test.mock;

import role.marcusRestaurant.MarcusWaiterRole;
import mainCity.restaurants.marcusRestaurant.interfaces.WaiterGuiInterface;

public class MockWaiterGui extends Mock implements WaiterGuiInterface {
	private EventLog log;
	private MarcusWaiterRole waiter;
	public MockWaiterGui(String name) {
		super(name);
		log = new EventLog();
		
	}

	public void setWaiter(MarcusWaiterRole w) {
		waiter = w;
	}
	
	@Override
	public void DoGoToTable(int tableNumber) {
		log.add(new LoggedEvent("Going to table"));	
		System.out.println("Going to table");
		waiter.msgAtDest();
	}

	@Override
	public void DoGoToCook() {
		log.add(new LoggedEvent("Going to stand to post order"));
		System.out.println("Going to stand");
		waiter.msgAtDest();
	}

	@Override
	public void DoDeliverFood(String c) {
		log.add(new LoggedEvent("Delivering food"));
		System.out.println("Delivering food to table");
		waiter.msgAtDest();
	}

	@Override
	public void DoGoHome() {
		log.add(new LoggedEvent("Going to home position"));
		waiter.msgAtDest();
	}

	@Override
	public void DoLeaveRestaunt() {
		log.add(new LoggedEvent("Leaving restaurant"));
		waiter.msgAtDest();
	}

	@Override
	public void DoPickUpCustomer(int x, int y) {
		log.add(new LoggedEvent("Picking up customer"));
		waiter.msgAtDest();
	}

	public EventLog getLog() {
		return log;
	}

	public void guiAppear() {}
}
