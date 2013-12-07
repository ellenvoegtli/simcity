package mainCity.restaurants.marcusRestaurant.test.mock;

import java.util.Map;

import mainCity.interfaces.DeliveryMan;
import mainCity.restaurants.marcusRestaurant.MarcusTable;
import mainCity.restaurants.marcusRestaurant.interfaces.Cashier;
import mainCity.restaurants.marcusRestaurant.interfaces.Customer;
import mainCity.restaurants.marcusRestaurant.interfaces.Waiter;

public class MockCashier extends Mock implements Cashier {
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

	public void msgPayingMyDebt(Customer c, double amount) {
		log.add(new LoggedEvent("Customer " + c + " just paid his/her debt of $"+ amount));		
	}

	public void msgComputeBill(Waiter w, String choice, MarcusTable t) {
		log.add(new LoggedEvent("Received message from waiter to compute bill for table " + t.getTableNumber()));		
	}

	public void msgHereIsPayment(Customer c, double cash, int table) {
		log.add(new LoggedEvent("Received payment of $" + cash + " from customer " + c));		
	}

	public EventLog getLog() {
		return log;
	}

}
