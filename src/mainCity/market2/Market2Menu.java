package mainCity.market2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Market2Menu {
	
	public List<Item> menuItems;


	public Market2Menu(){
		menuItems = Collections.synchronizedList(new ArrayList<Item>());
		
		//Ellen Restaurant
		menuItems.add(new Item("pasta", 20, 20.00));
		menuItems.add(new Item("pizza", 20, 8.99));
		menuItems.add(new Item("meatballs", 20, 5.00));
		menuItems.add(new Item("bread", 40, 3.00));
		
		//Ena Restaurant
		menuItems.add(new Item("lamb", 20, 5.99));
		menuItems.add(new Item("lambchops", 20, 8.99));
		menuItems.add(new Item("porkchops", 20, 10.99));
		menuItems.add(new Item("steak", 20, 15.99));
		
		//Marcus Restaurant
		menuItems.add(new Item("swiss", 5, 5.99));
		menuItems.add(new Item("american", 5, 6.99));
		menuItems.add(new Item("cheddar", 5, 7.99));
		menuItems.add(new Item("provolone", 5, 4.99));
		
		//Jefferson Restaurant
		menuItems.add(new Item("chicken", 5, 10.99));
		menuItems.add(new Item("salad", 5, 5.99));
		menuItems.add(new Item("soup", 5, 5.00));
		//menuItems.add(new Item("steak", , ));
		
		//David Restaurant
		menuItems.add(new Item("chickennuggets", 5, 7.99));
		menuItems.add(new Item("burger", 5, 6.00));
		menuItems.add(new Item("fries", 5, 0.50));
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