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
	
	Map<String, Integer> prices = new TreeMap<String, Integer>();
	Map<String, Integer> stockAmounts = new TreeMap<String, Integer>();


	public MarketMenu(){
		menuItems.add("steak");
		menuItems.add("pasta");
		menuItems.add("pizza");
		menuItems.add("soup");
		
		prices.put("steak", 30);	//type, $$price
        prices.put("pizza", 10);
        prices.put("pasta", 20);
        prices.put("soup", 5);
        
        
	}
	
	public int getStock(String choice){
		return (stockAmounts.get(choice));
	}
	
	public int getPrice(String choice){	//should return a double, not an int
		return (prices.get(choice));
	}
	
	
}