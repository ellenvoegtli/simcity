package mainCity.restaurants.enaRestaurant;

import mainCity.Person;

public class EnaNormalWaiterRole extends EnaWaiterRole 
{

	public EnaNormalWaiterRole(Person p, String name) 
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
