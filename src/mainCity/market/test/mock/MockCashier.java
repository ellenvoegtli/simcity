package mainCity.market.test.mock;


import java.util.Map;

import mainCity.market.interfaces.*;
import mainCity.market.*;

public class MockCashier extends Mock implements MarketCashier {
	public DeliveryMan deliveryMan;
    public Employee employee;
    public Greeter greeter;

    public MockCashier(String name) {
            super(name);

    }
    
    public void setGreeter(Greeter g){
    	
    }
    
	public void msgComputeBill(Map<String, Integer> inventory, Customer c, Employee e){
		
	}
	public void msgComputeBill(Map<String, Integer> inventory, String name, Employee e){
		
	}
	public void msgHereIsPayment(double amount, Customer cust){
		
	}
	public void msgPleaseRecalculateBill(Customer cust){
		
	}
	public void msgChangeVerified(Customer cust){
		
	}
	public void msgHereIsMoneyIOwe(Customer cust, double amount){
		
	}
}