package mainCity.restaurants.enaRestaurant.interfaces;

import mainCity.restaurants.enaRestaurant.EnaHostRole.Table;


public interface WaiterGuiInterface {
	public abstract void DoGoToTable(Customer cust, Table table);
	public abstract void DoGoToKitchen();
	public abstract void DoGetCustomer(Customer cust);
	public abstract void GoOnBreak();
	public abstract void DoGoToCashier();
	public abstract void DoServe(String ch, Table t);
	public abstract void DoLeaveCustomer();
	public abstract void Arriving(String choice);
	public abstract void Arriving();
	public abstract void setXNum(int tableNumber);
	public abstract void setBreak();
	public abstract void DoBringToTable(Customer cust, int tableNumber);
	public abstract void SubmitOrder(String choice);


}
