package mainCity.restaurants.enaRestaurant;
import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
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
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.ENA_RESTAURANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.ENA_COOK, this.getName(), s);
	}


	protected void handleOrder(MyCustomers c) {
		log("Writing down order onto order ticket..");
		OrderTicket order = new OrderTicket(this, c.choice, c.table);
		
		waiterGui.DoGoToKitchen();
		
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
}

