package role.enaRestaurant;

import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.restaurants.enaRestaurant.interfaces.Customer;

public class EnaNormalWaiterRole extends EnaWaiterRole 
{

	public EnaNormalWaiterRole(PersonAgent p, String name) 
	{
		super(p, name);
	}

	protected void PassOrder(MyCustomers c) 
	{
		
		AlertLog.getInstance().logMessage(AlertTag.ENA_RESTAURANT, this.getName(), "Gui should be taking order to the kitchen");

		//log("GUI SHOULD BE TAKING ORDER TO KITCHEN");
		waiterGui.DoGoToKitchen();
		waiterGui.SubmitOrder(c.choice);
		try {
			getAtKitchen().acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cook.msgHereIsTheOrder(this, c.choice, c.table);
		waiterGui.DoLeaveCustomer();
	
	}

	

	
	public void msgDoneEating(Customer enaCustomerRole) {
		// TODO Auto-generated method stub
		
	}

	
	public void msgCheckPlease(Customer enaCustomerRole, String choice) {
		// TODO Auto-generated method stub
		
	}



}
