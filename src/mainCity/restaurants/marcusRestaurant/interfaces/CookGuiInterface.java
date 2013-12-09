package mainCity.restaurants.marcusRestaurant.interfaces;

public interface CookGuiInterface {

	public abstract void DoGoToCounter();

	public abstract void DoGoToGrill(int grill);

	public abstract void DoClearGrill(int grill);

	public abstract void DoLeaveRestaurant();

	public abstract void setPresent(boolean t);
	
	public abstract void guiAppear();
}