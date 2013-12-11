package mainCity.restaurants.enaRestaurant.interfaces;

import role.enaRestaurant.EnaWaiterRole;
import mainCity.restaurants.enaRestaurant.gui.EnaCustomerGui;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Customer {
	
	public  EnaCustomerGui gui = null;
	public abstract void msgHereIsYourChange(double change);
	public abstract Waiter getWaiter();
	public abstract double getDebt();
	public abstract double getCash();
	public abstract String getName();
	public abstract void setDebt(double i);
	public abstract String getChoice();
	public abstract void setNum(int tableNumber);
	public abstract void msgHereIsYourCheck();
	public abstract void msgWhatWouldYouLike();
	public abstract void msgWhatElseWouldYouLike();
	public abstract void setCh(String string);
	public abstract void msgHereIsYourFood(String choice);
	public abstract void setChoice(String choice);
	public abstract void msgFollowToTable();
	public abstract int getXPos();
	
	
}