package role.ellenRestaurant;

import mainCity.PersonAgent;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.EllenRestaurant.interfaces.*;

import java.util.concurrent.*;


public class EllenNormalWaiterRole extends EllenWaiterRole {	
	public EllenNormalWaiterRole(PersonAgent p, String name){
		super(p, name);
	}
	
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.ELLEN_RESTAURANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.ELLEN_WAITER, this.getName(), s);
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