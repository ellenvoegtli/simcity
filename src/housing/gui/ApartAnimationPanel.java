package housing.gui;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mainCity.contactList.ContactList;
import mainCity.gui.AnimationPanel;
import mainCity.gui.CityCard;
import mainCity.gui.CityGui;
import mainCity.restaurants.enaRestaurant.EnaWaiterRole;
import mainCity.restaurants.enaRestaurant.gui.EnaAnimationPanel;
import mainCity.restaurants.enaRestaurant.gui.EnaWaiterGui;
import role.Role;
import housing.LandlordRole;
import housing.OccupantRole;
import housing.personHome;
import housing.gui.OccupantGui;
import housing.gui.LandlordGui;



public class ApartAnimationPanel extends HomeAnimationPanel{


    private final int WINDOWX = 950;
    private final int WINDOWY = 550;
    private Image bufferImage;
    private Dimension bufferSize;
    private CityGui gui;
	private HomePanel home = new HomePanel(this);
    
    private List<HomeAnimationPanel> apts = new ArrayList<HomeAnimationPanel>();
    
    private HomeAnimationPanel apt1 = new HomeAnimationPanel(gui);
   
    private HomeAnimationPanel apt2 = new HomeAnimationPanel(gui);
    
    private HomeAnimationPanel apt3 = new HomeAnimationPanel(gui);
    private HomeAnimationPanel apt4 = new HomeAnimationPanel(gui);
    
	
	public ApartAnimationPanel(CityGui gui) 
	{
		super(gui);
	    apts.add(apt1);
	    apts.add(apt2);
	    apts.add(apt3);
	    apts.add(apt4);
		
	    ContactList.getInstance().setHome(home);

		//ContactList.getInstance().setApart(apts);
    	
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
       
        setLayout( new GridLayout(2,2,0,0));
        
        add(apt1);
        add(apt2);
        add(apt2);
        add(apt4);
        
        
        bufferSize = this.getSize();
	}
	

	/*public void actionPerformed(ActionEvent e) 
	{
		apt1.actionPerformed(e);
		apt2.actionPerformed(e);
		apt3.actionPerformed(e);
		apt4.actionPerformed(e);

	}*/


}
