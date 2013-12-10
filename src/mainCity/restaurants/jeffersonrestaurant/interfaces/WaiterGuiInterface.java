package mainCity.restaurants.jeffersonrestaurant.interfaces;

import java.awt.Graphics2D;

public interface WaiterGuiInterface {

	public static final int xTable = 200;
	public static final int yTable = 300;
	public static final int width = 20;
	public static final int height = 20;

	public abstract void setOrigin(int x, int y);

	public abstract void updatePosition();

	public abstract void draw(Graphics2D g);

	public abstract boolean isPresent();

	public abstract void DoBringToTable(Customer customer, int table);

	public abstract void DoGoToCook();

	public abstract void DoTakeOrder(int table);

	public abstract void DoGetFood();

	public abstract void DoDeliverOrder(int table);

	public abstract void DoLeaveRestaurant();

	public abstract boolean atStart();

	public abstract void DoLeaveCustomer();

	public abstract void DoGoHome();

	public abstract int getXPos();

	public abstract int getYPos();

}