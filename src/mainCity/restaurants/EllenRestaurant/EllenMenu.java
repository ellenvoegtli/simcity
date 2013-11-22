package mainCity.restaurants.EllenRestaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.EllenRestaurant.gui.*;
import mainCity.restaurants.EllenRestaurant.interfaces.*;
import agent.Agent;


public class EllenMenu {
	
	public List<String> menuItems
	= Collections.synchronizedList(new ArrayList<String>());
	
	Map<String, Integer> prices 
	= new TreeMap<String, Integer>();


	public EllenMenu(){
		menuItems.add("steak");
		menuItems.add("pasta");
		menuItems.add("pizza");
		menuItems.add("Soup");
		
		prices.put("steak", 30);	//type, $$price
        prices.put("pizza", 10);
        prices.put("pasta", 20);
        prices.put("Soup", 5);
	}
	
	public int getPrice(String choice){
		return (prices.get(choice));
	}
	
	
}