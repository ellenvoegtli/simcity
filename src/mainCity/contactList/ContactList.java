package mainCity.contactList;

import mainCity.PersonAgent;
import mainCity.restaurants.EllenRestaurant.gui.*;
import mainCity.restaurants.enaRestaurant.*;
import mainCity.restaurants.enaRestaurant.gui.EnaRestaurantPanel;
import mainCity.restaurants.jeffersonrestaurant.gui.JeffersonRestaurantPanel;
import mainCity.restaurants.marcusRestaurant.gui.MarcusRestaurantPanel;
import mainCity.restaurants.marcusRestaurant.interfaces.Host;
import role.davidRestaurant.DavidCashierRole;
import role.davidRestaurant.DavidCookRole;
import role.davidRestaurant.DavidHostRole;
import role.jeffersonRestaurant.JeffersonCashierRole;
import role.jeffersonRestaurant.JeffersonCookRole;
import role.jeffersonRestaurant.JeffersonHostRole;
import role.marcusRestaurant.*;
import mainCity.restaurants.restaurant_zhangdt.gui.DavidRestaurantPanel;
import role.ellenRestaurant.EllenCashierRole;
import role.ellenRestaurant.EllenCookRole;
import role.ellenRestaurant.EllenHostRole;
import mainCity.bank.gui.BankPanel;
import mainCity.bank.interfaces.BankManager;
import mainCity.gui.CityPanel;
import transportation.BusStop;
import housing.OccupantRole;
import housing.gui.HomePanel;
import role.market1.*;
import mainCity.market1.gui.*;

import java.util.*;

public class ContactList {
	
	
	//Bus Stops
	public static ArrayList<BusStop> stops;
	public BusStop homeStop = new BusStop(320, 80, PersonAgent.CityLocation.home); 
	public BusStop davidRestStop = new BusStop(635, 230, PersonAgent.CityLocation.restaurant_david);
	public BusStop marcusRestStop = new BusStop(130, 155, PersonAgent.CityLocation.restaurant_marcus); 
	public BusStop jeffersonRestStop = new BusStop( 220 , 380, PersonAgent.CityLocation.restaurant_jefferson); 
	public BusStop ellenRestStop = new BusStop(130, 305, PersonAgent.CityLocation.restaurant_ellen); 
	public BusStop enaRestStop = new BusStop( 215 , 80, PersonAgent.CityLocation.restaurant_ena); 
	public BusStop bankStop = new BusStop(130, 230, PersonAgent.CityLocation.bank); 
	public BusStop marketStop = new BusStop( 440, 80 , PersonAgent.CityLocation.market);
	
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
	
	
	
	
	public Market1GreeterRole marketGreeter;
	public Market1CashierRole marketCashier;
	public Market1DeliveryManRole marketDeliveryMan;
		
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
	public Host marcusHost;
	public EnaHostRole enaHost;
	public JeffersonHostRole jeffersonHost;
	public DavidHostRole davidHost;

	//all of bank's people
	public BankManager bankManager;
	

	
	//TESTING
	JeffersonRestaurantPanel jeffersonRestaurant;
	MarcusRestaurantPanel marcusRestaurant;
	EllenRestaurantPanel ellenRestaurant;
	EnaRestaurantPanel enaRestaurant;
	DavidRestaurantPanel davidRestaurant; 
	BankPanel bank;
	BankPanel bank2;
	MarketPanel market;
	MarketPanel market2;
	
	CityPanel city;
	
	//anything else? apartment landlords?
	
	
	
	//*****SETTERS********
	
	//City
	public void setCity(CityPanel cp){
		city = cp;
	}
	public CityPanel getCity(){
		return city;
	}
	
	// Home *****   \\
	
	HomePanel homeP;
	public boolean rent;

	public List<PersonAgent> peopleList = new ArrayList<PersonAgent>();
	public List<PersonAgent> RenterList = new ArrayList<PersonAgent>();

	public Map<PersonAgent, HomePanel> LivingPlaces = new HashMap<PersonAgent,HomePanel>();
	public List<HomePanel> homeInstances = new ArrayList<HomePanel> ();

	public void setHome(HomePanel hp)
	{
		homeP = hp;
		homeInstances.add(hp);
	}
	
	
	public void setPersonInstance(PersonAgent p, boolean renter)
	{
		rent = renter;
		if(renter == false)
		{
			peopleList.add(p);
		}
		if(renter == true)
		{
			RenterList.add(p);
		}
		
		 HomePanel home = null;
		  if(homeInstances.size() != 0)
		  {
			 if (renter==false)
			 {
				  home = homeInstances.get(peopleList.size()-1);
					 LivingPlaces.put(p, home);
			 }
			 
			else //if(!oR.owner)
			 {
				  home = homeInstances.get(6+RenterList.size());
					 LivingPlaces.put(p, home);
			 }
		  }
		
	}
	
	public HomePanel getHome(OccupantRole oR)
	{
		return LivingPlaces.get(oR.person);
	}
	

	public HomePanel getHome()
	{
		return homeP;
	}
	
	
	
	//Bank*******
	public void setBankManager(BankManager m){
		bankManager = m;
	}
	
	public BankManager getBankManager(){
		return bankManager;
	}
	
	public void setBank(BankPanel b){
		bank=b;
	}
	public void setBank2(BankPanel b){
		bank2=b;
	}
	public BankPanel getBank(){
		return bank;
	}
	public BankPanel getBank2(){
		return bank2;
	}
	
	//Market********
	public void setMarketGreeter(Market1GreeterRole g){
		marketGreeter = g;
	}
	public void setMarketCashier(Market1CashierRole c){
		marketCashier = c;
	}
	public void setMarketDeliveryMan(Market1DeliveryManRole d){
		marketDeliveryMan = d;
	}
	public void setMarket(MarketPanel m){
		market = m;
	}
	public MarketPanel getMarket(){
		return market;
	}
	//MARKET 2********
	public void setMarket2(MarketPanel m){
		market2 = m;
	}
	public MarketPanel getMarket2(){
		return market2;
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
	public void setMarcusHost(Host h){
		marcusHost = h;
	}
	
	public Host getMarcusHost() {
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
	public void setDavidRestaurant(DavidRestaurantPanel d){ 
		this.davidRestaurant = d;
	}
	public DavidRestaurantPanel getDavidRestaurant(){ 
		return davidRestaurant; 
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