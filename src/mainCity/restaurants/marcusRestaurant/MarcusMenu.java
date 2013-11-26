package mainCity.restaurants.marcusRestaurant;

import java.util.ArrayList;
import java.util.List;

public class MarcusMenu {
	List<Food> menu;		
	public MarcusMenu() {
		menu = new ArrayList<Food>();
		
		menu.add(new Food("Swiss", 9));
		menu.add(new Food("American", 14));
		menu.add(new Food("Cheddar", 6));
		menu.add(new Food("Provolone", 12));
	}
	
	public void outOf(String outOf) {
		for(int i = 0; i < menu.size(); ++i ) {
			if(menu.get(i).food == outOf) {
				menu.remove(i);
				return;
			}
		}
	}
	
	public List<Food> getMenu() {
		return menu;
	}
	
	public class Food {
		public String food;
		public double price;
		
		Food(String name, double p) {
			food = name;
			price = p;
		}
	}
}
