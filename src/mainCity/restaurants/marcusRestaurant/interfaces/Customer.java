package mainCity.restaurants.marcusRestaurant.interfaces;

import mainCity.restaurants.marcusRestaurant.MarcusMenu;

public interface Customer {
	public abstract void msgWantToWait();
	
	public abstract void msgFollowMeToTable(int table, MarcusMenu m, Waiter w);
	
	public abstract void msgWhatWouldYouLike();
	
	public abstract void msgPleaseReorder(MarcusMenu m);
	
	public abstract void msgHereIsYourOrder(String choice);
	
	public abstract void msgHereIsCheck();
	
	public abstract void msgDebtOwed(double amount);
	
	public abstract void msgHereIsChange(double c);
	
	public abstract int getXPos();
	
	public abstract int getYPos();
}
