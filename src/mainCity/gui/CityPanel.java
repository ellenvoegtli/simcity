package mainCity.gui;

import javax.swing.JPanel;

import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.market.*;
import mainCity.market.gui.*;
import mainCity.restaurants.EllenRestaurant.gui.RestaurantGui;
import mainCity.restaurants.marcusRestaurant.gui.RestaurantPanel;

public class CityPanel extends JPanel{
	
	private CityGui gui; 
	public ContactList contactList;


	
	//Hardcoding one person for now.
	private PersonAgent person = new PersonAgent();
	
	public CityPanel(CityGui gui) { 
		this.gui = gui; 
		
		MarketGui marketGui = new MarketGui();
        marketGui.setVisible(true);
        
	    RestaurantGui ellenRestGui = new RestaurantGui();
        ellenRestGui.setVisible(true);
		
		
		
		
		PersonGui pg = new PersonGui(person, gui); 
		gui.getAnimationPanel().addPersonGui(pg);
		person.msgGotHungry(); 
		//person.msgGoToMarket();
		//person.msgGoToWork();
		
		person.setGui(pg);
		gui.getAnimationPanel().addPersonGui(pg);

		person.startThread(); 
		
	}

}
