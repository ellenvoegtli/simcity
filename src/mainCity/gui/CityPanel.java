package mainCity.gui;

import housing.gui.HomeGui;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.market.*;
import mainCity.market.gui.*;
import mainCity.restaurants.EllenRestaurant.gui.EllenRestaurantGui;
import mainCity.restaurants.jeffersonrestaurant.gui.JeffersonRestaurantGui;
import mainCity.restaurants.jeffersonrestaurant.gui.JeffersonRestaurantPanel;
//import mainCity.restaurants.restaurant_zhangdt.gui.DavidRestaurantPanel;
//import mainCity.restaurants.restaurant_zhangdt.gui.DavidRestaurantGui;
import mainCity.restaurants.marcusRestaurant.gui.MarcusRestaurantGui;
import mainCity.restaurants.marcusRestaurant.gui.MarcusRestaurantPanel;
import mainCity.restaurants.enaRestaurant.*;
import mainCity.restaurants.enaRestaurant.gui.*;

public class CityPanel extends JPanel{
	private CityGui gui; 
	private int clock;
	private List<PersonAgent> occupants = new ArrayList<PersonAgent>();
		
	public CityPanel(CityGui gui) {
		this.gui = gui;
		clock = 6;
		
    	MarketGui marketGui = new MarketGui();
    	//marketGui.setVisible(true);
		
	    EllenRestaurantGui ellenRestaurant = new EllenRestaurantGui();
	    ContactList.getInstance().setEllenRestaurant(ellenRestaurant.getEllenRestaurantPanel());
        //ellenRestaurant.setVisible(true);
		
		EnaRestaurantGui enaRestaurant = new EnaRestaurantGui();
	    ContactList.getInstance().setEnaRestaurant(enaRestaurant.getEnaRestaurantPanel());
	    //enaRestaurant.setVisible(true);
		
		//DavidRestaurantGui davidRestGui = new DavidRestaurantGui(); 
		//davidRestGui.setVisible(true);
		
    	MarcusRestaurantGui marcusRestaurant = new MarcusRestaurantGui();
    	ContactList.getInstance().setMarcusRestaurant(marcusRestaurant.getMarcusRestaurantPanel());
    	//marcusRestaurant.setVisible(true);

    	//JeffersonRestaurantGui jeffersonRestaurant = new JeffersonRestaurantGui();
    	//ContactList.getInstance().setJeffersonRestaurant(jeffersonRestaurant.getJeffersonRestaurantPanel());
    	//jeffersonRestaurant.setVisible(true);
    	
    	HomeGui home= new HomeGui();
    	ContactList.getInstance().setHome(home.getHomePanel());
    	home.setVisible(true);
    	
    	//Hardcoding one person for now.
    	PersonAgent person = new PersonAgent("joeMoe");
    	PersonAgent person2 = new PersonAgent("Waiter");
    	PersonAgent person3 = new PersonAgent("Cook");
    	PersonAgent person4 = new PersonAgent("Cashier");
    	PersonAgent person5 = new PersonAgent("Host");
    	
    	
    	occupants.add(person);
    	occupants.add(person2);
    	occupants.add(person3);
    	occupants.add(person4);
    	occupants.add(person5);

    	person.msgGoHome();
    	//person.msgGotHungry();
    	//person.msgGoToMarket();
    	//person.msgGoToWork();
    	
		PersonGui pg1 = new PersonGui(person, gui);
		PersonGui pg2 = new PersonGui(person2, gui); 
		PersonGui pg3 = new PersonGui(person3, gui);
		PersonGui pg4 = new PersonGui(person4, gui);
		PersonGui pg5 = new PersonGui(person5, gui);

		//person.updateOccupation("rich", -1, -1);
		//person2.updateOccupation("marcusWaiter", 8, 22);
		//person3.updateOccupation("marcusCook", 8, 22);
		//person4.updateOccupation("marcusCashier", 8, 22);
		//person5.updateOccupation("marcusHost", 7, 22);

		
		person2.updateOccupation("enaHost", 8, 11);
		person3.updateOccupation("enaCook", 8, 11);
		person4.updateOccupation("enaCashier", 8, 11);
		person5.updateOccupation("enaWaiter", 7, 10);
		 
		
		//person2.updateOccupation("ellenWaiter", 8, 11);
		//person3.updateOccupation("ellenCook", 8, 11);
		//person4.updateOccupation("ellenCashier", 8, 11);
		//person5.updateOccupation("ellenHost", 7, 10);


		gui.getAnimationPanel().addPersonGui(pg1);
		gui.getAnimationPanel().addPersonGui(pg2);
		gui.getAnimationPanel().addPersonGui(pg3);
		gui.getAnimationPanel().addPersonGui(pg4);
		gui.getAnimationPanel().addPersonGui(pg5);

		person.setGui(pg1);
		person2.setGui(pg2);
		person3.setGui(pg3);
		person4.setGui(pg4);
		person5.setGui(pg5);
		
		person.msgGoToRestaurant();
		person2.msgGoToWork();
		person3.msgGoToWork();
		person4.msgGoToWork();
		person5.msgGoToWork();		

		person.startThread(); 
		person2.startThread(); 
		person3.startThread();
		person4.startThread();
		person5.startThread();
		
		//Instantiation of the Global City Clock
		Runnable standChecker = new Runnable() {
			 public void run() {
				 clock = (clock+1) % 24;
				 updateCity();
			 }
		 };
		 
		 ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		 executor.scheduleAtFixedRate(standChecker, 0, 5, TimeUnit.SECONDS); //Timer goes off every 10 seconds
	}
	
	private void updateCity() {
		for(PersonAgent p : occupants) {
			p.updateClock(clock);
		}
	}
}
