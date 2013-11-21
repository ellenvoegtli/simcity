package mainCity.gui;

import javax.swing.JPanel;

import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.market.*;
import mainCity.market.gui.*;
import mainCity.restaurants.EllenRestaurant.gui.RestaurantGui;
import mainCity.restaurants.marcusRestaurant.gui.MarcusRestaurantGui;
import mainCity.restaurants.marcusRestaurant.gui.MarcusRestaurantPanel;

public class CityPanel extends JPanel{
	
	private CityGui gui; 
	public ContactList contactList;


	
	//Hardcoding one person for now.
	private PersonAgent person = new PersonAgent();
	private PersonAgent person2 = new PersonAgent();
	
	public CityPanel(CityGui gui) { 
		this.gui = gui; 
		
		//MarketGui marketGui = new MarketGui();
        //marketGui.setVisible(true);
        
	    //RestaurantGui ellenRestGui = new RestaurantGui();
        //ellenRestGui.setVisible(true);
		
    	MarcusRestaurantGui marcusRestaurant = new MarcusRestaurantGui();
    	ContactList.getInstance().setMarcusRestaurant(marcusRestaurant.getMarcusRestaurantPanel());
    	marcusRestaurant.setVisible(true);
		
		PersonGui pg1 = new PersonGui(person, gui); 
		gui.getAnimationPanel().addPersonGui(pg1);
		//person.msgGotHungry();
		person.msgGoToRestaurant();
		//person.msgGoToMarket();
		//person.msgGoToWork();
		person.setGui(pg1);
		gui.getAnimationPanel().addPersonGui(pg1);
		
		PersonGui pg2 = new PersonGui(person2, gui); 
		gui.getAnimationPanel().addPersonGui(pg2);
		person2.msgGoToRestaurant();
		person2.setGui(pg2);
		gui.getAnimationPanel().addPersonGui(pg2);
		
		person.startThread(); 
		person2.startThread(); 
		
	}

}
