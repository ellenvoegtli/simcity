package role.marcusRestaurant;

import mainCity.PersonAgent;


public class MarcusNormalWaiterRole extends MarcusWaiterRole {
	public MarcusNormalWaiterRole(PersonAgent p, String name) {
		super(p, name);
	}

	protected void handleOrder(MyCustomer c) {
		waiterGui.DoGoToCook();
		waitForGui();
		
		c.state = CustomerState.waitingForFood;
		try {
			cook.msgHereIsAnOrder(this, c.choice, c.table);
		}
		catch(Exception e) {
			cook = host.getCook();
			cook.msgHereIsAnOrder(this, c.choice, c.table);
		}
		waiterGui.DoGoHome();
	}
}

