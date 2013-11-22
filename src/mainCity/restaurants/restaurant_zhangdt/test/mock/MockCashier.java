package mainCity.restaurants.restaurant_zhangdt.test.mock;

import java.util.Map;

import role.market.MarketDeliveryManRole;
import mainCity.restaurants.restaurant_zhangdt.interfaces.Cashier;
import mainCity.restaurants.restaurant_zhangdt.interfaces.Customer;
import mainCity.restaurants.restaurant_zhangdt.interfaces.Waiter;

public class MockCashier extends Mock implements Cashier{

	public EventLog log = new EventLog();

	public MockCashier(String name) {
		super(name);

	}
	
	public void msgHeresACheck(Waiter w, String order, int tableNumber) { 
		//log.add(new LoggedEvent("Received Check from waiter. order = " + order + "table number = " + tableNumber));
		log.add(new LoggedEvent("Received check from waiter."));
	}

	public void msgHeresMyPayment(Customer c, double custPayment, int tableNumber) {
		log.add(new LoggedEvent("Received Payment from customer. Payment = "+ custPayment));
	}

	@Override
	public void msgHereIsMarketBill(Map<String, Integer> inventory,
			double billAmount, MarketDeliveryManRole deliveryPerson) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Received Market Bill of $" + billAmount));
	}

	@Override
	public void msgHereIsChange(double amount,
			MarketDeliveryManRole deliveryPerson) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Received Change From Market. Change = $"+ amount));

	}
}
