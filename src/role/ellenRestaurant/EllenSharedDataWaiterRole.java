package role.ellenRestaurant;

import mainCity.PersonAgent;
import mainCity.restaurants.EllenRestaurant.sharedData.*;

public class EllenSharedDataWaiterRole extends EllenWaiterRole {
	private RevolvingStand stand;
	
	public EllenSharedDataWaiterRole(PersonAgent p, String name) {
		super(p, name);
		log("Created a shared data waiter");
	}
	
	public void setStand(RevolvingStand s) {
		this.stand = s;
	}

	protected void sendOrderToCook(MyCustomer mc) {
		log("Writing down order onto order ticket..");
		OrderTicket order = new OrderTicket(this, mc.choice, mc.table);
		
		waiterGui.DoGoToCook();
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
		if(!stand.isFull()) {
			log("Posting order to the board...");
			stand.insert(order);
		}
		else {
			//need to do something if stand is full
		}
		
		mc.s = CustomerState.waitingForFood;
		//waiterGui.DoGoHome();
	}
}

