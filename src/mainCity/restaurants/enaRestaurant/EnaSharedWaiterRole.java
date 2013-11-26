package mainCity.restaurants.enaRestaurant;
import mainCity.PersonAgent;
import mainCity.restaurants.enaRestaurant.sharedData.OrderTicket;
import mainCity.restaurants.enaRestaurant.sharedData.RevolvingStand;
//import mainCity.restaurants.EnaRestaurant.sharedData.*;

public class EnaSharedWaiterRole extends EnaWaiterRole {
	private RevolvingStand stand;
	
	public EnaSharedWaiterRole(PersonAgent p, String name) {
		super(p, name);
	}
	
	public void setStand(RevolvingStand s) {
		this.stand = s;
	}

	protected void handleOrder(MyCustomers c) {
		print("Writing down order onto order ticket..");
		OrderTicket order = new OrderTicket(this, c.choice, c.table);
		
		waiterGui.DoGoToKitchen();
		
		if(!stand.isFull()) {
			print("Posting order to the board...");
			stand.insert(order);
		}
		else {
			//need to do something if stand is full
		}
		
		c.customerState = custState.Ordered;
		waiterGui.DoLeaveCustomer();
	}
}

