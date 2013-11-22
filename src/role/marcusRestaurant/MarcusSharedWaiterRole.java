package role.marcusRestaurant;
import mainCity.restaurants.marcusRestaurant.sharedData.*;

public class MarcusSharedWaiterRole extends MarcusWaiterRole {
	private RevolvingStand stand;
	
	public MarcusSharedWaiterRole(String name) {
		super(name);
		print("Created a shared data waiter");
	}
	
	public void setStand(RevolvingStand s) {
		this.stand = s;
	}

	protected void handleOrder(MyCustomer c) {
		print("Writing down order onto order ticket..");
		OrderTicket order = new OrderTicket(this, c.choice, c.table);
		
		waiterGui.DoGoToCook();
		waitForGui();
		
		if(!stand.isFull()) {
			print("Posting order to the board...");
			stand.insert(order);
		}
		else {
			//need to do something if stand is full
		}
		
		c.state = CustomerState.waitingForFood;
		waiterGui.DoGoHome();
	}
}

