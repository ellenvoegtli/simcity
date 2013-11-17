package mainCity.restaurants.marcusRestaurant;

public class MarcusNormalWaiterRole extends MarcusWaiterRole {
	public MarcusNormalWaiterRole(String name) {
		super(name);
	}

	protected void handleOrder(MyCustomer c) {
		waiterGui.DoGoToCook();
		waitForGui();
		
		c.state = CustomerState.waitingForFood;
		cook.msgHereIsAnOrder(this, c.choice, c.table);
		waiterGui.DoGoHome();
	}
}

