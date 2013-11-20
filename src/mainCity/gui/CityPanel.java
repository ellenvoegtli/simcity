package mainCity.gui;

import javax.swing.JPanel;

import mainCity.PersonAgent;
import mainCity.market.*;
import mainCity.market.gui.*;

public class CityPanel extends JPanel{
	
	private CityGui gui; 
	
	//Hardcoding one person for now.
	private PersonAgent person = new PersonAgent();
	
	public CityPanel(CityGui gui) { 
		this.gui = gui; 
		PersonGui pg = new PersonGui(person, gui); 
		gui.getAnimationPanel().addPersonGui(pg);
		//person.msgGotHungry(); 
		//person.msgGoToMarket();
		//person.msgGoToWork();
		
		person.setGui(pg);
		gui.getAnimationPanel().addPersonGui(pg);

		person.startThread(); 
		
	}

}
