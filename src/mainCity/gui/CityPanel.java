package mainCity.gui;

import javax.swing.JPanel;

import mainCity.PersonAgent;

public class CityPanel extends JPanel{
	
	private CityGui gui; 
	
	//Hardcoding one person for now.
	private PersonAgent person = new PersonAgent();
	
	public CityPanel(CityGui gui) { 
		this.gui = gui; 
		//person.startThread(); 
		PersonGui pg = new PersonGui(person, gui); 
		gui.getAnimationPanel().addPersonGui(pg);
	}

}
