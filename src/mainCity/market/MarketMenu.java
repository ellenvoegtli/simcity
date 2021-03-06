package mainCity.market;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MarketMenu {
	static final int NITEMS = 18;
	public List<Item> menuItems = Collections.synchronizedList(new ArrayList<Item>());

	public MarketMenu(){	//used for testing; sets all stock values = 5
		
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

	public MarketMenu(int pasta, int pizza, int mtbls, int bread, int lamb, int lchops, int pchops,
			int steak, int swiss, int american, int cheddar, int prov, int chicken, int salad, int soup,
			int nuggets, int burger, int fries){
		
		menuItems = Collections.synchronizedList(new ArrayList<Item>());
		
		//Ellen Restaurant
		menuItems.add(new Item("pasta", pasta, 20.00));
		menuItems.add(new Item("pizza", pizza, 8.99));
		menuItems.add(new Item("meatballs", mtbls, 5.00));
		menuItems.add(new Item("bread", bread, 3.00));
		
		//Ena Restaurant
		menuItems.add(new Item("lamb", lamb, 5.99));
		menuItems.add(new Item("lambchops", lchops, 8.99));
		menuItems.add(new Item("porkchops", pchops, 10.99));
		menuItems.add(new Item("steak", steak, 15.99));
		
		//Marcus Restaurant
		menuItems.add(new Item("swiss", swiss, 5.99));
		menuItems.add(new Item("american", american, 6.99));
		menuItems.add(new Item("cheddar", cheddar, 7.99));
		menuItems.add(new Item("provolone", prov, 4.99));
		
		//Jefferson Restaurant
		menuItems.add(new Item("chicken", chicken, 10.99));
		menuItems.add(new Item("salad", salad, 5.99));
		menuItems.add(new Item("soup", soup, 5.00));
		//menuItems.add(new Item("steak", , ));
		
		//David Restaurant
		menuItems.add(new Item("chickennuggets", nuggets, 7.99));
		menuItems.add(new Item("burger", burger, 6.00));
		menuItems.add(new Item("fries", fries, 0.50));
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
		
		public void deductStock(int s){
			stockAmount -= s;
		}
	}
	
}