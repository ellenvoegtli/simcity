package role.ellenRestaurant;

import mainCity.PersonAgent;


public class EllenNormalWaiterRole extends EllenWaiterRole {	
	public EllenNormalWaiterRole(PersonAgent p, String name){
		super(p, name);
	}
	
	protected void sendOrderToCook(MyCustomer mc){
		log("Going to send order to cook");

		waiterGui.DoGoToCook();
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		cook.msgHereIsOrder(mc.choice, mc.table, this);
		mc.s = CustomerState.waitingForFood;
	}
}