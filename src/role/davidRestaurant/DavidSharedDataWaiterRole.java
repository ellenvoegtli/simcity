package role.davidRestaurant;

import mainCity.PersonAgent;
import mainCity.restaurants.restaurant_zhangdt.sharedData.OrderTicket;
import mainCity.restaurants.restaurant_zhangdt.sharedData.RevolvingStand;

public class DavidSharedDataWaiterRole extends DavidWaiterRole{
	private RevolvingStand stand;

	public DavidSharedDataWaiterRole(String name, PersonAgent p) {
		super(name, p);
	}

	public void setStand(RevolvingStand s) { 
		this.stand = s;
	}
	
	protected void HeresAnOrder(myCustomer c) { 
		print("Putting order onto stand..."); 
		OrderTicket order = new OrderTicket(this, c.orderChoice, c.t); 
		
		waiterGui.DoMoveToCook(); 
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		if(!stand.isFull()) { 
			print("Posting order onto revolving stand"); 
			stand.insert(order); 
		}
		else{ 
			print("Stand is full"); 
		}
		c.custState = CustomerStates.Ordered;
	}
}
