package role.marcusRestaurant;

import mainCity.Person;


public class MarcusNormalWaiterRole extends MarcusWaiterRole {
	public MarcusNormalWaiterRole(Person p, String name) {
		super(p, name);
	}

	protected void handleOrder(MyCustomer c) {
		waiterGui.DoGoToCook();
		waitForGui();
		
		c.state = CustomerState.waitingForFood;
		cook.msgHereIsAnOrder(this, c.choice, c.table);
		waiterGui.DoGoHome();
	}
}

