package mainCity.market;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import agent.Agent;
//import mainCity.market.MarketEmployeeRole;


public class MarketMenu {
	
	public List<String> menuItems
	= Collections.synchronizedList(new ArrayList<String>());
	
	Map<String, Double> prices = new TreeMap<String, Double>();
	Map<String, Integer> stockAmounts = new TreeMap<String, Integer>();


	public MarketMenu(){
		//====== ITEMS MENU =============
		
		//Ellen Restaurant
		menuItems.add("pasta");
		menuItems.add("pizza");
		menuItems.add("meatballs");
		menuItems.add("bread");
		
		//Ena Restaurant (Meat)
		menuItems.add("lamb");
		menuItems.add("lambchops");
		menuItems.add("porkchops");
		menuItems.add("steak");
		
		//Marcus Restaurant (Cheese)
		menuItems.add("swiss");
		menuItems.add("american");
		menuItems.add("cheddar");
		menuItems.add("provolone");
		
		//Jefferson Restaurant (All-American)
		menuItems.add("chicken");
		menuItems.add("salad");
		menuItems.add("soup");
		//menuItems.add("steak");
		
		//David Restaurant (Fast food)
		menuItems.add("chickenNuggets");
		menuItems.add("burger");
		menuItems.add("fries");
		//menuItems.add("pizza");
		
		
		
		
		
		//======== PRICES MENU =========
        prices.put("pasta", 20.00);
        prices.put("pizza", 8.99);
        prices.put("meatballs", 5.00);
        prices.put("bread", 3.00);
        
        prices.put("lamb", 5.99);
        prices.put("lambchops", 8.99);
        prices.put("porkchops", 10.99);
        prices.put("steak", 15.99);

        prices.put("chicken", 10.99);
        prices.put("salad", 5.99);
        prices.put("soup", 5.00);
        
        prices.put("chickenNuggets", 7.99);
        prices.put("burger", 6.00);
        prices.put("fries", 0.50);
        
        prices.put("swiss", 5.99);
        prices.put("american", 6.99);
        prices.put("cheddar", 7.99);
        prices.put("provolone", 4.99);
        
        
        
        //======= STOCK =================
        stockAmounts.put("pasta", 20);
        stockAmounts.put("pizza", 20);
        stockAmounts.put("meatballs", 20);
        stockAmounts.put("bread", 40);

        stockAmounts.put("lamb", 10);
        stockAmounts.put("lambchops", 10);
        stockAmounts.put("porkchops", 10);
        stockAmounts.put("steak", 10);
        
        stockAmounts.put("chicken", 10);
        stockAmounts.put("salad", 10);
        stockAmounts.put("soup", 10);
        
        stockAmounts.put("chickenNuggets", 30);
        stockAmounts.put("burger", 20);
        stockAmounts.put("fries", 40);
        
        stockAmounts.put("swiss", 20);
        stockAmounts.put("american", 20);
        stockAmounts.put("cheddar", 20);
        stockAmounts.put("provolone", 20);
       
	}
	
	public int getStock(String choice){
		String temp = choice.toLowerCase();
		return (stockAmounts.get(temp));
	}
	
	public double getPrice(String choice){
		String temp = choice.toLowerCase();
		return (prices.get(temp));
	}
	
	
}