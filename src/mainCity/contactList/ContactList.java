package mainCity.contactList;

import mainCity.PersonAgent;
import mainCity.market.*;
import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.EllenRestaurant.gui.*;
import mainCity.restaurants.enaRestaurant.*;
import mainCity.restaurants.enaRestaurant.gui.EnaRestaurantPanel;
import mainCity.restaurants.jeffersonrestaurant.*;
import mainCity.restaurants.jeffersonrestaurant.gui.JeffersonRestaurantPanel;
import mainCity.restaurants.marcusRestaurant.gui.MarcusRestaurantPanel;
import role.marcusRestaurant.*;
import mainCity.restaurants.restaurant_zhangdt.*;
import mainCity.restaurants.restaurant_zhangdt.gui.DavidRestaurantPanel;
import mainCity.bank.*;
import mainCity.bank.gui.BankPanel;
import mainCity.interfaces.*;
import transportation.BusStop;
import housing.gui.HomePanel;
import role.market.*;

import java.util.*;

public class ContactList {
	
	
	//Bus Stops
	public static ArrayList<BusStop> stops;
	public BusStop homeStop = new BusStop(320, 80, PersonAgent.CityLocation.home); 
	public BusStop davidRestStop = new BusStop(635, 230, PersonAgent.CityLocation.restaurant_david);
	public BusStop marcusRestStop = new BusStop(130, 180, PersonAgent.CityLocation.restaurant_marcus); 
	public BusStop jeffersonRestStop = new BusStop( 220 , 380, PersonAgent.CityLocation.restaurant_jefferson); 
	public BusStop ellenRestStop = new BusStop(130, 280, PersonAgent.CityLocation.restaurant_ellen); 
	public BusStop enaRestStop = new BusStop( 260 , 80, PersonAgent.CityLocation.restaurant_ena); 
	public BusStop bankStop = new BusStop(130, 230, PersonAgent.CityLocation.bank); 
	public BusStop marketStop = new BusStop( 455, 80 , PersonAgent.CityLocation.market);
	
	private static ContactList contactList = null;
	
	public ContactList(){
		stops = new ArrayList<BusStop>();
		stops.add(homeStop);
		stops.add(davidRestStop);
		stops.add(marcusRestStop);
		stops.add(jeffersonRestStop);
		stops.add(ellenRestStop);
		stops.add(enaRestStop);
		stops.add(bankStop); 
		stops.add(marketStop);
	}
	
	public static ContactList getInstance(){
		if (contactList == null)
			contactList = new ContactList();
		return contactList;
	}
	
	HomePanel home;
	
	
	
	public MarketGreeterRole marketGreeter;
	public MarketCashierRole marketCashier;
	public MarketDeliveryManRole marketDeliveryMan;
	
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

	//all of bank's people
	public BankManagerRole bankManager;
	

	
	//TESTING
	JeffersonRestaurantPanel jeffersonRestaurant;
	MarcusRestaurantPanel marcusRestaurant;
	EllenRestaurantPanel ellenRestaurant;
	EnaRestaurantPanel enaRestaurant;
	DavidRestaurantPanel davidRestaurant; 
	BankPanel bank;
	
	//anything else? apartment landlords?
	
	
	
	//*****SETTERS********
	
	// Home *****
	
	public void setHome(HomePanel hp)
	{
		home = hp;
	}
	
	public HomePanel getHome()
	{
		return home;
	}
	
	//Bank*******
	public void setBankManager(BankManagerRole m){
		bankManager = m;
	}
	
	public BankManagerRole getBankManager(){
		return bankManager;
	}
	
	public void setBank(BankPanel b){
		bank=b;
	}
	public BankPanel getBank(){
		return bank;
	}
	//Market********
	public void setMarketGreeter(MarketGreeterRole g){
		marketGreeter = g;
	}
	public void setMarketCashier(MarketCashierRole c){
		marketCashier = c;
	}
	public void setMarketDeliveryMan(MarketDeliveryManRole d){
		marketDeliveryMan = d;
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
	public void setEllenRestaurant(EllenRestaurantPanel m){
		ellenRestaurant = m;
	}
	public EllenRestaurantPanel getEllenRestaurant(){
		return ellenRestaurant;
	}
	
	//Marcus's Restaurant******
	public void setMarcusHost(MarcusHostRole h){
		marcusHost = h;
	}
	
	public MarcusHostRole getMarcusHost() {
		return marcusHost;
	}
	
	public void setMarcusCook(MarcusCookRole cook){
		marcusCook = cook;
	}
	public MarcusCookRole getMarcusCook() {
		return marcusCook;
	}
	
	public void setMarcusCashier(MarcusCashierRole cashier){
		marcusCashier = cashier;
	}
	public MarcusCashierRole getMarcusCashier() {
		return marcusCashier;
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
	public void setJeffersonRestaurant(JeffersonRestaurantPanel j){
			jeffersonRestaurant = j;
		}
	public JeffersonRestaurantPanel getJeffersonRestaurant(){
		return jeffersonRestaurant;
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
	public void setEnaRestaurant(EnaRestaurantPanel e) {
		this.enaRestaurant = e;
	}
	
	public EnaRestaurantPanel getEnaRestaurant() {
		return enaRestaurant;
	}
	
}