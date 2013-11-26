package role.marcusRestaurant;
import mainCity.Person;
import mainCity.restaurants.marcusRestaurant.sharedData.*;

public class MarcusSharedWaiterRole extends MarcusWaiterRole {
	private RevolvingStand stand;
	
	public MarcusSharedWaiterRole(Person p, String name) {
		super(p, name);
	}
	
	public void setStand(RevolvingStand s) {
		this.stand = s;
	}

	protected void handleOrder(MyCustomer c) {
		output("Writing down order onto order ticket..");
		OrderTicket order = new OrderTicket(this, c.choice, c.table);
		
		waiterGui.DoGoToCook();
		waitForGui();
		
		if(!stand.isFull()) {
			output("Posting order to the board...");
			stand.insert(order);
		}
		else {
			//need to do something if stand is full
		}
		
		c.state = CustomerState.waitingForFood;
		waiterGui.DoGoHome();
	}
}

