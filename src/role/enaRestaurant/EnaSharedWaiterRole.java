package role.enaRestaurant;
import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.restaurants.enaRestaurant.interfaces.Customer;
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
	
	//for alert log trace statements
	


	protected void PassOrder(MyCustomers c) {
		log("Writing down order onto order ticket..");
		OrderTicket order = new OrderTicket(this, c.choice, c.table);
		
		waiterGui.DoGoToKitchen();
		try {
			getAtKitchen().acquire();
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
		
		c.customerState = custState.Ordered;
		waiterGui.DoLeaveCustomer();
	}

	
	public void msgDoneEating(Customer enaCustomer) {
		// TODO Auto-generated method stub
		
	}

	
	public void msgCheckPlease(Customer enaCustomer, String choice) {
		// TODO Auto-generated method stub
		
	}

}

