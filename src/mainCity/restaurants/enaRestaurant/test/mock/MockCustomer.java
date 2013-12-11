package mainCity.restaurants.enaRestaurant.test.mock;


import role.enaRestaurant.EnaWaiterRole;
import role.enaRestaurant.EnaCustomerRole.AgentEvent;
import mainCity.restaurants.enaRestaurant.interfaces.Cashier;
import mainCity.restaurants.enaRestaurant.interfaces.Customer;
import mainCity.restaurants.enaRestaurant.interfaces.Waiter;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends Mock implements Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public MockWaiter waiter;
	
	public double cash;
	public double debt = 0;
	public EventLog log = new EventLog();
	public MockCustomer(String name) 
	{
		super(name);
		if(name.equals("poor"));
		{
			cash = 5.99;
		}
		if(name.equals("debt"))
		{
			cash = 3.0;
		}

	}
	@Override
	public void msgHereIsYourChange(double change)
	{	
		//cash= change;
		log.add(new LoggedEvent("Recieved change from cashier"));
	}

	public Waiter getWaiter() 
	{
		return waiter;
	}
	@Override
	public double getDebt() 
	{
		return debt;
	}
	@Override
	public double getCash() 
	{
		return cash;
	}
	@Override
	public void setDebt(double i) 
	{
		debt = i;
	}
	@Override
	public String getChoice() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setNum(int tableNumber) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgHereIsYourCheck() {
		log.add(new LoggedEvent("Recieved message for check"));
		
	}
	@Override
	public void msgWhatWouldYouLike() {
		log.add(new LoggedEvent("recieved msgWhatWouldYouLike"));
		
	}
	@Override
	public void msgWhatElseWouldYouLike() {
		log.add(new LoggedEvent("need new choice"));
		
	}
	@Override
	public void setCh(String string) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgHereIsYourFood(String choice) {
		log.add(new LoggedEvent("Recieved msgHereIsYourFood"));
		
	}
	@Override
	public void setChoice(String choice) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgFollowToTable() {
		log.add(new LoggedEvent("Recieved msgFollowToTable"));
		
	}
	@Override
	public int getXPos() {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
