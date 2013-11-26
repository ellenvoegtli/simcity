package mainCity.restaurants.EllenRestaurant;
import java.util.concurrent.Semaphore;

import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.restaurants.EllenRestaurant.sharedData.*;

public class EllenSharedDataWaiterRole extends EllenWaiterRole {
	private RevolvingStand stand;
	
	public EllenSharedDataWaiterRole(PersonAgent p, String name) {
		super(p, name);
		//print("Created a shared data waiter");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Created a shared data waiter");
	}
	
	public void setStand(RevolvingStand s) {
		this.stand = s;
	}

	protected void sendOrderToCook(MyCustomer mc) {
		//print("Writing down order onto order ticket..");
		AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Writing down order onto order ticket..");
		OrderTicket order = new OrderTicket(this, mc.choice, mc.table);
		
		waiterGui.DoGoToCook();
		try {
			atCook.acquire();
			//atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		if(!stand.isFull()) {
			//print("Posting order to the board...");
			AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), "Posting order to the board..");
			stand.insert(order);
		}
		else {
			//need to do something if stand is full
		}
		
		mc.s = CustomerState.waitingForFood;
		//waiterGui.DoGoHome();
	}
}

