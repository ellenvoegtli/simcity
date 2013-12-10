package mainCity.restaurants.enaRestaurant.test.mock;

import java.util.Map;

import mainCity.interfaces.DeliveryMan;
import mainCity.restaurants.enaRestaurant.interfaces.Cashier;
import mainCity.restaurants.enaRestaurant.interfaces.Customer;
import mainCity.restaurants.enaRestaurant.test.mock.EventLog;
import mainCity.restaurants.enaRestaurant.test.mock.LoggedEvent;

public class MockCashier extends Mock implements Cashier{
	EventLog log;

	public MockCashier(String name) {
		super(name);
		log = new EventLog();
	}

	
	public void msgHereIsChange(double amount, DeliveryMan deliveryPerson) {
		log.add(new LoggedEvent("Received change of $" + amount));
		
	}

	public void msgNotEnoughMoney(double amountOwed, double amountPaid) {
		log.add(new LoggedEvent("Received an amount containing debt to market of $" + amountOwed));		
		
	}

	public void msgHereIsMarketBill(Map<String, Integer> inventory, double billAmount, DeliveryMan deliveryPerson) {
		log.add(new LoggedEvent("Received a market bill of $" + billAmount));		
		
	}

	
	public void msgComputeBill(String choice, Customer c) {
		log.add(new LoggedEvent("Received message from waiter to compute bill for customer " + c.getName()));		
		
	}

	
	public void msgPayment(String choice, double cash, Customer c) {
		log.add(new LoggedEvent("Received payment of $" + cash + " from customer " + c));		
		
	}


	@Override
	public void msgNoMoney() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void msgRestMoney() {
		// TODO Auto-generated method stub
		
	}



	
	
}
