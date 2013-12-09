package role.jeffersonRestaurant;

import mainCity.PersonAgent;
import mainCity.restaurants.jeffersonrestaurant.sharedData.OrderTicket;
import mainCity.restaurants.jeffersonrestaurant.sharedData.RevolvingStand;


public class JeffersonSharedDataWaiterRole extends JeffersonWaiterRole {
	private RevolvingStand stand;
	
	public JeffersonSharedDataWaiterRole(PersonAgent p, String name) {
		super(p, name);
	
	}

	@Override
	protected void tellCook(int table, String choice) {
		log("Writing down order onto order ticket..");
		OrderTicket order = new OrderTicket(table,choice,this);
		waiterGui.DoGoToCook();
		
		try {
			atCook.acquire();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(!stand.isFull()) {
			log("Posting order to the board...");
			stand.insert(order);
		}
		else {
			cook.msghereIsAnOrder(table, choice,this);
		}
		waiterGui.DoLeaveCustomer();
		try {
			atHome.acquire();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}

	}

}
