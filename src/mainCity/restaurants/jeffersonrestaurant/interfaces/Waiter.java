package mainCity.restaurants.jeffersonrestaurant.interfaces;

import mainCity.restaurants.jeffersonrestaurant.WaiterAgent.WaiterCust;
import mainCity.restaurants.jeffersonrestaurant.WaiterAgent.waiterCustState;

public interface Waiter {

	
	
	
	
	
	public abstract void msgButtonAskBreak();
	
	public abstract void msgYouCanBreak();
	
	public abstract void msgSeatAtTable(Customer c, int table) ;
	
	public abstract void msgHereIsMyChoice(Customer cust, String choice);

	public abstract void msgLeavingTable(Customer cust);

	public abstract void msgAtTable();
	
	public abstract void msgAtHome();
	
	public abstract void msgAtPlating();
	
	public abstract void msgAtCook();
	
	public abstract void msgImReadyToOrder(Customer cust);
	
	public abstract void msgOrderIsReady(int t);
	
	public abstract void msgOutOfChoice(int t);
	
	public abstract void msgWantCheck(Customer c);
	
	public abstract void msgCheckPrinted(int t);
	
	public abstract void msgHereIsPayment(Customer c, double money);

	public abstract void msgPaymentComplete(int t);
	
}
