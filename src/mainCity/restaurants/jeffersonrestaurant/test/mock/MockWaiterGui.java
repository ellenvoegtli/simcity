package mainCity.restaurants.jeffersonrestaurant.test.mock;

import java.awt.Graphics2D;

import mainCity.restaurants.jeffersonrestaurant.interfaces.Customer;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Waiter;
import mainCity.restaurants.jeffersonrestaurant.interfaces.WaiterGuiInterface;

public class MockWaiterGui extends Mock implements WaiterGuiInterface {

	public EventLog log;
	
	public Waiter waiter;
	
	public MockWaiterGui(String name) {
		super(name);
		
	}

	@Override
	public void setOrigin(int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updatePosition() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void DoBringToTable(Customer customer, int table) {
		log.add(new LoggedEvent("called DoBringToTable"));
		waiter.msgAtTable();

	}

	@Override
	public void DoGoToCook() {
		log.add(new LoggedEvent("called DoGoToCook"));
		waiter.msgAtCook();

	}

	@Override
	public void DoTakeOrder(int table) {
		log.add(new LoggedEvent("called DoTakeOrder"));


	}

	@Override
	public void DoGetFood() {
		log.add(new LoggedEvent("called DoGetFood"));


	}

	@Override
	public void DoDeliverOrder(int table) {
		log.add(new LoggedEvent("called DoDeliverOrder"));


	}

	@Override
	public void DoLeaveRestaurant() {
		log.add(new LoggedEvent("called DoLeaveRestaurant"));


	}

	@Override
	public boolean atStart() {
		log.add(new LoggedEvent("called atStart"));

		return true;
	}

	@Override
	public void DoLeaveCustomer() {
		log.add(new LoggedEvent("called DoLeaveCustomer"));
		waiter.msgAtHome();


	}

	@Override
	public void DoGoHome() {
		log.add(new LoggedEvent("called DoGoHome"));


	}

	@Override
	public int getXPos() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getYPos() {
		// TODO Auto-generated method stub
		return 0;
	}

}
