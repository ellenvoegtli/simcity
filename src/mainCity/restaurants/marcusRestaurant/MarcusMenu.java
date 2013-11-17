package mainCity.restaurants.marcusRestaurant;

import java.util.ArrayList;
import java.util.List;

public class MarcusMenu {
	List<Food> menu;		
	MarcusMenu() {
		menu = new ArrayList<Food>();
		
		menu.add(new Food("Steak", 16));
		menu.add(new Food("Chicken", 11));
		menu.add(new Food("Salad", 6));
		menu.add(new Food("Pizza", 9));
	}
	
	public void outOf(String outOf) {
		for(int i = 0; i < menu.size(); ++i ) {
			if(menu.get(i).food == outOf) {
				menu.remove(i);
				return;
			}
		}
	}
	
	class Food {
		String food;
		double price;
		
		Food(String name, double p) {
			food = name;
			price = p;
		}
	}
}
