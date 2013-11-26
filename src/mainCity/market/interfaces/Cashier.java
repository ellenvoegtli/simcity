package mainCity.market.interfaces;

import java.util.Map;

import mainCity.market.MarketCustomerRole;
import mainCity.market.MarketEmployeeRole;


public interface Cashier {
	
	
	public abstract void msgComputeBill(Map<String, Integer> inventory, MarketCustomerRole c, MarketEmployeeRole e);
	public abstract void msgComputeBill(Map<String, Integer> inventory, String name, MarketEmployeeRole e);
	public abstract void msgHereIsPayment(double amount, MarketCustomerRole cust);
	public abstract void msgPleaseRecalculateBill(MarketCustomerRole cust);
	public abstract void msgChangeVerified(MarketCustomerRole cust);
	public abstract void msgHereIsMoneyIOwe(MarketCustomerRole cust, double amount);
	
	
}