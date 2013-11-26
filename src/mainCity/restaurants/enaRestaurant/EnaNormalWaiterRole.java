package mainCity.restaurants.enaRestaurant;

import mainCity.PersonAgent;

public class EnaNormalWaiterRole extends EnaWaiterRole 
{

	public EnaNormalWaiterRole(PersonAgent p, String name) 
	{
		super(p, name);
	}

	protected void handleOrder(MyCustomers c) 
	{
		waiterGui.DoGoToKitchen();
		
		c.customerState = custState.Ordered;
		cook.msgHereIsTheOrder(this, c.choice, c.table);
		waiterGui.DoLeaveCustomer();
	
	}

}
