package mainCity.restaurants.EllenRestaurant;

import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.EllenRestaurant.interfaces.*;
import java.util.concurrent.*;
import mainCity.PersonAgent;


public class EllenNormalWaiterRole extends EllenWaiterRole {	
	public EllenNormalWaiterRole(PersonAgent p, String name){
		super(p, name);
	}
	
	protected void sendOrderToCook(MyCustomer mc){
		print("Going to send order to cook");
		
		waiterGui.DoGoToCook();
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cook.msgHereIsOrder(mc.choice, mc.table, this);
		mc.s = CustomerState.waitingForFood;
	}
}