package mainCity.restaurants.marcusRestaurant.interfaces;

public interface WaiterGuiInterface {

	public abstract void DoGoToTable(int tableNumber);

	public abstract void DoGoToCook();

	public abstract void DoDeliverFood(String c);

	public abstract void DoGoHome();

	public abstract void DoLeaveRestaunt();

	public abstract void DoPickUpCustomer(int x, int y);
	
	public abstract void guiAppear();
}