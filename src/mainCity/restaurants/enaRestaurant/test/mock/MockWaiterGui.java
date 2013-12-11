package mainCity.restaurants.enaRestaurant.test.mock;

import role.enaRestaurant.EnaCustomerRole;
import role.enaRestaurant.EnaWaiterRole;
import role.enaRestaurant.EnaHostRole.Table;
import mainCity.restaurants.enaRestaurant.interfaces.Customer;
import mainCity.restaurants.enaRestaurant.interfaces.WaiterGuiInterface;
import mainCity.restaurants.enaRestaurant.test.mock.LoggedEvent;

public class MockWaiterGui extends Mock implements WaiterGuiInterface {
 EnaWaiterRole waiter;
 EventLog log;
	
	public MockWaiterGui(String name){
		super(name);
		log = new EventLog();
	}
	
	public void setWaiter(EnaWaiterRole w){
		waiter = w;
	}
	

	@Override
	public void DoBringToTable(Customer cust, int tableN) {
		log.add(new LoggedEvent("Going to table " + tableN));	
		
	}

	@Override
	public void DoGoToKitchen() {
		log.add(new LoggedEvent("Going to kitchen"));	
		
	}

	@Override
	public void DoGetCustomer(Customer customer) {
		log.add(new LoggedEvent("Going to curtome " + customer.getName()));	
		
	}

	

	@Override
	public void DoGoToCashier() {
		log.add(new LoggedEvent("Going to cashier"));	
		
	}

	@Override
	public void DoServe(String ch, Table t) {
		log.add(new LoggedEvent("Bringing food to table " +t.getTableNumber()));	
		
	}

	@Override
	public void DoLeaveCustomer() {
		log.add(new LoggedEvent("leaving customer"));	
		
	}

	@Override
	public void DoGoToTable(Customer cust, Table table) {
		log.add(new LoggedEvent("Going to table " + table.getTableNumber()));	
		
	}
	
	@Override
	public void DoGoOnBreak()
	{
		log.add(new LoggedEvent(" waiter going on break"));	

	}

	@Override
	public void setXNum(int tableNumber) {
		
	}

	@Override
	public void setBreak() {
		
	}

	@Override
	public void SubmitOrder(String choice) {
		log.add(new LoggedEvent(" submit order to cook"));	
		
	}

	@Override
	public void guiAppear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void DoLeaveRestaurant() {

		log.add(new LoggedEvent(" waiter leaving restaurant"));	

	}

	@Override
	public void Arriving() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Arriving(String choice) {
		// TODO Auto-generated method stub
		
	}

	public Object getLog() {
		return log;
	}

	


}
