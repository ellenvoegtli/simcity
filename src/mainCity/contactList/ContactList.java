package mainCity.contactList;

import mainCity.market.*;
import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.EllenRestaurant.gui.*;
import mainCity.restaurants.enaRestaurant.*;
import mainCity.restaurants.jeffersonrestaurant.*;
import mainCity.restaurants.marcusRestaurant.gui.MarcusRestaurantPanel;
import role.marcusRestaurant.*;
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
	public MarketCashierRole marketCashier;
	
	//List<MainCook> cooks = new ArrayList<MainCook>();		//will this work with different subclasses?
	
	//all of the restaurants' cooks
	public EllenCookRole ellenCook;
	public EnaCookRole enaCook;
	public JeffersonCookRole jeffersonCook;
	public MarcusCookRole marcusCook;
	public DavidCookRole davidCook;
	
	public EllenCashierRole ellenCashier;
	public EnaCashierRole enaCashier;
	public JeffersonCashierRole jeffersonCashier;
	public MarcusCashierRole marcusCashier;
	public DavidCashierRole davidCashier;
	
	//all of the restaurants' hosts
	public EllenHostRole ellenHost;
	public MarcusHostRole marcusHost;
	public EnaHostRole enaHost;
	public JeffersonHostRole jeffersonHost;
	public DavidHostRole davidHost;
	
	//TESTING
	MarcusRestaurantPanel marcusRestaurant;
	EllenRestaurantPanel ellenRestaurant;
	
	//anything else? apartment landlords?
	
	
	
	//*****SETTERS********
	
	
	//Bank*******
	public void setBankManager(BankManager m){
		bankManager = m;
	}
	
	//Market********
	public void setMarketGreeter(MarketGreeterRole g){
		marketGreeter = g;
	}
	public void setMarketCashier(MarketCashierRole c){
		marketCashier = c;
	}
	
	//Ellen's Restaurant******
	public void setEllenHost(EllenHostRole h){
		ellenHost = h;
	}
	public void setEllenCook(EllenCookRole cook){
		ellenCook = cook;
	}
	public void setEllenCashier(EllenCashierRole cashier){
		ellenCashier = cashier;
	}
	
	//Marcus's Restaurant******
	public void setMarcusHost(MarcusHostRole h){
		marcusHost = h;
	}
	
	public MarcusHostRole getMarcusHost() {
		return marcusHost;
	}
	
	public void setMarcusRestaurant(MarcusRestaurantPanel m) {
		this.marcusRestaurant = m;
	}
	
	public MarcusRestaurantPanel getMarcusRestaurant() {
		return marcusRestaurant;
	}

	
	//Jefferson's Restaurant******
	public void setJeffersonHost(JeffersonHostRole h){
		jeffersonHost = h;
	}
	public void setJeffersonCook(JeffersonCookRole cook){
		jeffersonCook = cook;
	}
	public void setJeffersonCashier(JeffersonCashierRole c){
		jeffersonCashier = c;
	}
	
	//David's Restaurant*****
	public void setDavidHost(DavidHostRole h){
		davidHost = h;
	}
	public void setDavidCook(DavidCookRole cook){
		davidCook = cook;
	}
	public void setDavidCashier(DavidCashierRole cashier){
		davidCashier = cashier;
	}
	
	//Ena's Restaurant
	public void setEnaHost(EnaHostRole h){
		enaHost = h;
	}
	public void setEnaCook(EnaCookRole cook){
		enaCook = cook;
	}
	public void setEnaCashier(EnaCashierRole c){
		enaCashier = c;
	}
	
}