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
	
	public CityPanel(CityGui gui) { 
		this.gui = gui; 
		
    	MarketGui marketGui = new MarketGui();
    	marketGui.setVisible(true);
		
	    EllenRestaurantGui ellenRestaurant = new EllenRestaurantGui();
	    ContactList.getInstance().setEllenRestaurant(ellenRestaurant.getEllenRestaurantPanel());
        //ellenRestaurant.setVisible(true);
		
		EnaRestaurantGui enaRestaurant = new EnaRestaurantGui();
	    ContactList.getInstance().setEnaRestaurant(enaRestaurant.getEnaRestaurantPanel());
	    enaRestaurant.setVisible(true);
		
		//DavidRestaurantGui davidRestGui = new DavidRestaurantGui(); 
		//davidRestGui.setVisible(true);
		
    	MarcusRestaurantGui marcusRestaurant = new MarcusRestaurantGui();
    	ContactList.getInstance().setMarcusRestaurant(marcusRestaurant.getMarcusRestaurantPanel());
    	//marcusRestaurant.setVisible(true);
    	
    	JeffersonRestaurantGui jeffersonRestaurant = new JeffersonRestaurantGui();
    	ContactList.getInstance().setJeffersonRestaurant(jeffersonRestaurant.getJeffersonRestaurantPanel());
    	//jeffersonRestaurant.setVisible(true);
    	
		
		PersonGui pg1 = new PersonGui(person, gui); 
		gui.getAnimationPanel().addPersonGui(pg1);
		person.setGui(pg1);
		//person.msgGotHungry();
		person.msgGoToRestaurant();
		//person.msgGoToMarket();
		//person.msgGoToWork();
		
		PersonGui pg2 = new PersonGui(person2, gui); 
		person2.updateOccupation("enaWaiter");
		gui.getAnimationPanel().addPersonGui(pg2);
		person2.setGui(pg2);
		person2.msgGoToWork();
		gui.getAnimationPanel().addPersonGui(pg2);
		
		person.startThread(); 
		person2.startThread(); 
		
	}

}
