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
	
	Map<String, Double> prices 
	= Collections.synchronizedMap(new TreeMap<String, Double>());


	public EllenMenu(){
		menuItems.add("pasta");
		menuItems.add("pizza");
		menuItems.add("meatballs");
		menuItems.add("bread");
		
		prices.put("pasta", 30.00);	//type, $$price
        prices.put("pizza", 12.99);
        prices.put("meatballs", 9.99);
        prices.put("bread", 5.00);
	}
	
	public double getPrice(String choice){
		return (prices.get(choice));
	}
	
	
}