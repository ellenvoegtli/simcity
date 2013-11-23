package mainCity.gui;

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
	
	//Hardcoding one person for now.
	private PersonAgent person = new PersonAgent("Customer");
	private PersonAgent person2 = new PersonAgent("Waiter");
	private PersonAgent person3 = new PersonAgent("Cook");
	private PersonAgent person4 = new PersonAgent("Cashier");
	private PersonAgent person5 = new PersonAgent("Host");
	
	public CityPanel(CityGui gui) { 
		this.gui = gui; 
		
    	MarketGui marketGui = new MarketGui();
    	//marketGui.setVisible(true);
		
	    EllenRestaurantGui ellenRestaurant = new EllenRestaurantGui();
	    ContactList.getInstance().setEllenRestaurant(ellenRestaurant.getEllenRestaurantPanel());
        //ellenRestaurant.setVisible(true);
		
		//EnaRestaurantGui enaRestaurant = new EnaRestaurantGui();
	    //ContactList.getInstance().setEnaRestaurant(enaRestaurant.getEnaRestaurantPanel());
	    //enaRestaurant.setVisible(true);
		
		//DavidRestaurantGui davidRestGui = new DavidRestaurantGui(); 
		//davidRestGui.setVisible(true);
		
    	MarcusRestaurantGui marcusRestaurant = new MarcusRestaurantGui();
    	ContactList.getInstance().setMarcusRestaurant(marcusRestaurant.getMarcusRestaurantPanel());
    	marcusRestaurant.setVisible(true);
    	
    	JeffersonRestaurantGui jeffersonRestaurant = new JeffersonRestaurantGui();
    	ContactList.getInstance().setJeffersonRestaurant(jeffersonRestaurant.getJeffersonRestaurantPanel());
    	//jeffersonRestaurant.setVisible(true);
    	
    	//person.msgGotHungry();
    	//person.msgGoToMarket();
    	//person.msgGoToWork();
    	
		PersonGui pg1 = new PersonGui(person, gui); 
		PersonGui pg2 = new PersonGui(person2, gui); 
		PersonGui pg3 = new PersonGui(person3, gui);
		PersonGui pg4 = new PersonGui(person4, gui);
		PersonGui pg5 = new PersonGui(person5, gui);

		person2.updateOccupation("marcusWaiter");
		person3.updateOccupation("marcusCook");
		person4.updateOccupation("marcusCashier");
		person5.updateOccupation("marcusHost");

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
	}
}
