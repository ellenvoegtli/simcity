package role.davidRestaurant;

import role.davidRestaurant.DavidWaiterRole.CustomerStates;
import mainCity.PersonAgent;

public class DavidNormalWaiterRole extends DavidWaiterRole {

	public DavidNormalWaiterRole(String name, PersonAgent p) {
		super(name, p);
	}
	
	protected void HeresAnOrder(myCustomer customer) { 
		customer.custState = CustomerStates.WaitingForOrder; 
		hostFree.tryAcquire();
		waiterGui.DoMoveToCook();
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cookAgent.msgHereIsAnOrder(this, customer.orderChoice, customer.t.tableNumber);
	}

}
