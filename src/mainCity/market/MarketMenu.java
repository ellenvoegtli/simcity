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
		menuItems.add("steak");
		menuItems.add("pasta");
		menuItems.add("pizza");
		menuItems.add("soup");
		menuItems.add("lamb");
		menuItems.add("lambchops");
		menuItems.add("porkchops");
		menuItems.add("steak");
		
		//prices.put("steak", 30.00);	//type, $$price
        prices.put("pizza", 8.99);
        prices.put("pasta", 20.00);
        prices.put("soup", 5.00);

        prices.put("steak", 15.99);
        prices.put("porkchops", 10.99);
        prices.put("lamb", 5.99);
        prices.put("lambchops", 8.99);
        prices.put("chicken", 10.99);
        prices.put("salad", 5.99);

        stockAmounts.put("steak", 10);
        stockAmounts.put("pizza", 10);
        stockAmounts.put("pasta", 10);
        stockAmounts.put("soup", 10);
        stockAmounts.put("porkchops", 10);
        stockAmounts.put("lamb", 10);
        stockAmounts.put("lambchops", 10);
	}
	
	public int getStock(String choice){
		return (stockAmounts.get(choice));
	}
	
	public double getPrice(String choice){
		return (prices.get(choice));
	}
	
	
}