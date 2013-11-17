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

	void setUnoccupied() {
		occupiedBy = null;
	}

	Customer getOccupant() {
		return occupiedBy;
	}

	int getTableNumber() {
		return tableNumber;
	}
		
	boolean isOccupied() {
		return occupiedBy != null;
	}

	public String toString() {
		return "table " + tableNumber;
	}
}
