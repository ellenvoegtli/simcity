package mainCity.restaurants.EllenRestaurant.interfaces;


public interface WaiterGuiInterface {
	
	public abstract void DoGoToTable(int t);
	public abstract void DoGoToCook();
	public abstract void DoDeliverFood(int t, String choice);
	public abstract void DoWait();
	
	public abstract void GoOffBreak();
	public abstract void GoOnBreak();
	public abstract void DoPickUpWaitingCustomer(int x, int y);
	public abstract void setIsDeliveringFood(boolean b);
	public abstract void DoLeaveRestaurant();
}