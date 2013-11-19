package mainCity.gui;

import javax.swing.JPanel;

import mainCity.PersonAgent;

public class CityPanel extends JPanel{
	
	private CityGui gui; 
	
	//Hardcoding one person for now.
	private PersonAgent person = new PersonAgent();
	
	public CityPanel(CityGui gui) { 
		this.gui = gui; 
		PersonGui pg = new PersonGui(person, gui); 
		gui.getAnimationPanel().addPersonGui(pg);
		person.msgGotHungry(); 
		person.setGui(pg);
		gui.getAnimationPanel().addPersonGui(pg);

		person.startThread(); 
	}

}
