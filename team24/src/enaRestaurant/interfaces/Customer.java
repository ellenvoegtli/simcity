package enaRestaurant.interfaces;

import enaRestaurant.WaiterRole;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Customer {
	
	public abstract void msgHereIsYourChange(double change);
	public abstract Waiter getWaiter();
	public abstract double getDebt();
	public abstract double getCash();
	public abstract String getName();
	public abstract void setDebt(double i);
	
	
}