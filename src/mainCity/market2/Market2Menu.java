package mainCity.market2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import agent.Agent;
//import mainCity.market.MarketEmployeeRole;


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
		menuItems.add(new Item("lamb", 10, 5.99));
		menuItems.add(new Item("lambchops", 10, 8.99));
		menuItems.add(new Item("porkchops", 10, 10.99));
		menuItems.add(new Item("steak", 10, 15.99));
		
		//Marcus Restaurant
		menuItems.add(new Item("swiss", 20, 5.99));
		menuItems.add(new Item("american", 20, 6.99));
		menuItems.add(new Item("cheddar", 20, 7.99));
		menuItems.add(new Item("provolone", 20, 4.99));
		
		//Jefferson Restaurant
		menuItems.add(new Item("chicken", 10, 10.99));
		menuItems.add(new Item("salad", 10, 5.99));
		menuItems.add(new Item("soup", 10, 5.00));
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