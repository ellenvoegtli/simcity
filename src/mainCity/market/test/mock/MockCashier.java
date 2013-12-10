package mainCity.market.test.mock;


import java.util.Map;

import mainCity.interfaces.DeliveryMan;
import mainCity.market.*;
import mainCity.market.interfaces.*;

public class MockCashier extends Mock implements MarketCashier {
	public DeliveryMan deliveryMan;
    public Employee employee;
    public Greeter greeter;

    public MockCashier(String name) {
            super(name);

    }
    
    public void setGreeter(Greeter g){
    }
    public void deductCash(double sub){
    }
    public void setMenu(MarketMenu m){
    }
    @Override
	public void msgComputeBill(Map<String, Integer> inventory, Customer c, Employee e){
		log.add(new LoggedEvent("Received msgComputeBill from " + e.getName() + " for " + c.getName()));
	}
    @Override
	public void msgComputeBill(Map<String, Integer> inventory, String name, Employee e){
		log.add(new LoggedEvent("Received msgComputeBill from " + e.getName() + " for " + name));
	}
	@Override
	public void msgHereIsPayment(double amount, Customer cust){
		log.add(new LoggedEvent("Received msgHereIsPayment from " + cust.getName() + " for $" + amount));
	}
	@Override
	public void msgPleaseRecalculateBill(Customer cust){
		log.add(new LoggedEvent("Received msgPleaseRecalculateBill from " + cust.getName()));
	}
	@Override
	public void msgChangeVerified(Customer cust){
		log.add(new LoggedEvent("Received msgChangeVerified from " + cust.getName()));
	}
	@Override
	public void msgPleaseRecalculateChange(Customer cust, double amount){
		log.add(new LoggedEvent("Received msgPleaseRecalculateChange from " + cust.getName() + " for $" + amount));
	}
	public void msgHereIsMoneyIOwe(Customer cust, double amount){
		
	}
}