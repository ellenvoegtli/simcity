package mainCity.restaurants.marcusRestaurant;
import mainCity.restaurants.marcusRestaurant.interfaces.*;


public class MarcusTable {
	Customer occupiedBy;
	int tableNumber;

	public MarcusTable(int tableNumber) {
		this.tableNumber = tableNumber;
	}

	public void setOccupant(Customer cust) {
		occupiedBy = cust;
	}

	public void setUnoccupied() {
		occupiedBy = null;
	}

	Customer getOccupant() {
		return occupiedBy;
	}

	public int getTableNumber() {
		return tableNumber;
	}
		
	public boolean isOccupied() {
		return occupiedBy != null;
	}

	public String toString() {
		return "table " + tableNumber;
	}
}
