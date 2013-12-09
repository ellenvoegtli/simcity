package mainCity.market1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Market1Menu {
	static final int NITEMS = 18;
	public List<Item> menuItems;


	public Market1Menu(){
		
		menuItems = Collections.synchronizedList(new ArrayList<Item>());
		
		//Ellen Restaurant
		menuItems.add(new Item("pasta", 5, 20.00));
		menuItems.add(new Item("pizza", 5, 8.99));
		menuItems.add(new Item("meatballs", 5, 5.00));
		menuItems.add(new Item("bread", 5, 3.00));
		
		//Ena Restaurant
		menuItems.add(new Item("lamb", 5, 5.99));
		menuItems.add(new Item("lambchops", 5, 8.99));
		menuItems.add(new Item("porkchops", 5, 10.99));
		menuItems.add(new Item("steak", 5, 15.99));
		
		//Marcus Restaurant
		menuItems.add(new Item("swiss", 20, 5.99));
		menuItems.add(new Item("american", 20, 6.99));
		menuItems.add(new Item("cheddar", 20, 7.99));
		menuItems.add(new Item("provolone", 20, 4.99));
		
		//Jefferson Restaurant
		menuItems.add(new Item("chicken", 20, 10.99));
		menuItems.add(new Item("salad", 20, 5.99));
		menuItems.add(new Item("soup", 20, 5.00));
		//menuItems.add(new Item("steak", , ));
		
		//David Restaurant
		menuItems.add(new Item("chickennuggets", 30, 7.99));
		menuItems.add(new Item("burger", 20, 6.00));
		menuItems.add(new Item("fries", 40, 0.50));
		//menuItems.add(new Item("pizza", , ));
		
		
	}
	
	
	public class Item {
		String item;
		int stockAmount;
		double price;
		
		Item(String id, int amount, double price){
			this.item = id;
			this.stockAmount = amount;
			this.price = price;
		}
		
		public String getItem(){
			return item;
		}
		
		public int getStock(){
			return stockAmount;
		}
		public double getPrice(){
			return price;
		}
	}
	
}