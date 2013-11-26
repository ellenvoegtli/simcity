package mainCity.market.test.mock;


import java.util.Map;

import mainCity.market.interfaces.*;
import mainCity.market.*;

public class MockCashier extends Mock implements Cashier {
	public DeliveryMan deliveryMan;
    public Employee employee;
    public Greeter greeter;

    public MockCashier(String name) {
            super(name);

    }
    
	public void msgComputeBill(Map<String, Integer> inventory, MarketCustomerRole c, MarketEmployeeRole e){
		
	}
	public void msgComputeBill(Map<String, Integer> inventory, String name, MarketEmployeeRole e){
		
	}
	public void msgHereIsPayment(double amount, MarketCustomerRole cust){
		
	}
	public void msgPleaseRecalculateBill(MarketCustomerRole cust){
		
	}
	public void msgChangeVerified(MarketCustomerRole cust){
		
	}
	public void msgHereIsMoneyIOwe(MarketCustomerRole cust, double amount){
		
	}
}