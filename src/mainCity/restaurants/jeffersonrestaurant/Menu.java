package mainCity.restaurants.jeffersonrestaurant;

import java.util.ArrayList;


public class Menu{
	
	public ArrayList<Item> choices = new ArrayList<Item>();
	
	
	public class Item{
		public String itemName;
		double itemPrice;
		
		public Item(String name, double price){
			itemName=name;
			itemPrice=price;
		}
	}
	void addItemToMenu(String itemName, double itemPrice){
		choices.add(new Item (itemName, itemPrice));
	}
	
	public Menu(){
		choices.add(new Item("steak", 15.99)); 
		choices.add(new Item("chicken", 10.99));
		choices.add(new Item("salad", 5.99));
		choices.add(new Item("pizza", 8.99));
		
	}
	
}