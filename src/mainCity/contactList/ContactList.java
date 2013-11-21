package mainCity.contactList;

import mainCity.market.*;
import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.enaRestaurant.*;
import mainCity.restaurants.jeffersonrestaurant.*;
import mainCity.restaurants.marcusRestaurant.*;
import mainCity.restaurants.restaurant_zhangdt.*;
import mainCity.bank.*;
import mainCity.interfaces.*;

import java.util.*;


public class ContactList {
	private static ContactList contactList = null;
	protected ContactList(){
		//nothing here
	}
	public static ContactList getInstance(){
		if (contactList == null)
			contactList = new ContactList();
		return contactList;
	}
	
	BankManager bankManager;
	
	public MarketGreeterRole marketGreeter;
	MarketCashierRole marketCashier;
	
	List<MainCook> cooks = new ArrayList<MainCook>();		//will this work with different subclasses?
	
	//all of the restaurants' cooks
	EllenCookRole ellenCook;
	//EnaCookRole enaCook;
	JeffersonCookRole jeffersonCook;
	MarcusCookRole marcusCook;
	//DavidCookRole davidCook;
	
	//all of the restaurants' hosts
	EllenHostRole ellenHost;
	MarcusHostRole marcusHost;
	//EnaHostRole enaHost;
	JeffersonHostRole jeffersonHost;
	//DavidHostRole davidHost;
	
	//anything else? apartment landlords?
	

	
	public void addCook(MainCook cook){		//will this take in any cook class that extends MainCook? And differentiate between them?
		System.out.println("Adding restaurant cook");
		cooks.add(cook);
	}
	
	public void addBankManager(BankManager m){
		bankManager = m;
	}
	
	public void addMarketGreeter(MarketGreeterRole g){
		System.out.println("Setting market host");
		marketGreeter = g;
	}
	
	public void addMarketCashier(MarketCashierRole c){
		marketCashier = c;
	}
	
	public void setEllenHost(EllenHostRole h){
		System.out.println("Setting ellenRestaurant host");
		ellenHost = h;
	}
	
	public void setMarcusHost(MarcusHostRole h){
		marcusHost = h;
	}
	
	/*
	public void setEnaHost(EnaHostRole h){
		enaHost = h;
	}
	*/
	public void setJeffersonHost(JeffersonHostRole h){
		jeffersonHost = h;
	}
	/*
	public void setDavidHost(DavidHostRole h){
		davidHost = h;
	}
	*/
	
	
}