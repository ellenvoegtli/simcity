package mainCity.restaurants.EllenRestaurant.test.mock;

import java.util.Collection;
import java.util.List;


//import restaurant.MarketAgent.Order;		//can Order be a public inner class?**********
import mainCity.restaurants.EllenRestaurant.interfaces.*;


public class MockMarket extends Mock implements Market{
	public Cashier cashier;
	
	//cook?
	public MockMarket(String name) {
        super(name);

	}

	
	@Override
	public void msgINeedInventory(String choice, int amountReq){
		log.add(new LoggedEvent("Received msgINeedInventory from cook. Needs " + amountReq + " " + choice + "(s)."));

		/* 		//******should I do it this way???
		if (choice.equalsIgnoreCase("soup"))
			
		else if (choice.equalsIgnoreCase("steak")){
			
		}
		*/
	}
	
	/*
	@Override
	public void msgRequestDone(Order o){
		
	}
	*/
	
	@Override
	public void msgHereIsPayment(int amount){
		log.add(new LoggedEvent("Received HereIsPayment from cashier. Paid = $" + amount));
	}
	
	
}