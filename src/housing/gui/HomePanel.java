package housing.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mainCity.restaurants.enaRestaurant.EnaWaiterRole;
import mainCity.restaurants.enaRestaurant.gui.EnaAnimationPanel;
import mainCity.restaurants.enaRestaurant.gui.EnaWaiterGui;
import role.Role;
import housing.LandlordRole;
import housing.OccupantRole;
import housing.personHome;
import housing.gui.OccupantGui;
import housing.gui.LandlordGui;


public class HomePanel extends JPanel
{
	   

    private LandlordRole landLord;
    private LandlordGui landLordGui;


    private JPanel homeLabel = new JPanel();
    private JPanel group = new JPanel();
    private personHome house;
    //private HomeGui gui; //reference to main gui
    private OccupantRole occupant;
   private OccupantGui occupantGui; 
   private HomeAnimationPanel animation;

    public HomePanel(HomeAnimationPanel homeAnimationPanel)
    {
       
    	this.animation = homeAnimationPanel;
       // setLayout(new GridLayout(1, 2, 20, 20));
       // initRestLabel();
        //add(homeLabel);
    }

    /**
     * Sets up the home label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        homeLabel.setLayout(new BoxLayout((Container)homeLabel, BoxLayout.Y_AXIS));
        /*label.setText(
                "<html><h3><u>House's Belongs To: </u></h3><table><tr><td>occupant:</td><td>" + occupant.getName());*/

        homeLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        homeLabel.add(label, BorderLayout.CENTER);
        homeLabel.add(new JLabel("               "), BorderLayout.EAST);
        homeLabel.add(new JLabel("               "), BorderLayout.WEST);
    }

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     */
    public void showInfo(String type, String name) 
    {

    	/*if(type.equals("Occupant"))
    	{
    		   gui.updateInfoPanel(name);

    	}
      
    	if(type.equals("Landlord"))
    	{
    		gui.updateInfoPanel(name);
    	}*/
      
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    
    public void createHunger(String occ)
    {
    		if(occ.equals(occupant.getName()))
    		{
    			  occupant.getGui().setHungry();
    		}
    	
    }
    
    
    
   /* public void addPerson(String type, String name) 
    {

    	if (type.equals("Occupant")) 
    	{
    		OccupantRole c = new OccupantRole(name);	
    		
			c.setHouse(house);
    		//customers.add(c);
    			//int posX = 22 * customers.size();
    		OccupantGui g = new OccupantGui(c, gui);
    		gui.animationPanel.addGui(g);// dw
    		//c.setHost(host);
    		//c.setCashier(cashier);
    		c.setGui(g);
    		c.startThread();

    	}
    	
    }*/
   
    public void handleRoleGui(Role r) 
    {
    	if(r instanceof OccupantRole) 
    	{
        	occupant = (OccupantRole) r;
        	        	 house = new personHome(occupant);

        	occupant.setHouse(house);
        	occupant.setLandLord(landLord);
            house.setOccupant(occupant);
    		occupantGui = new OccupantGui(occupant); 	
    		occupant.setGui(occupantGui);
    		//occupantGui.setHungry();    
    		animation.addGui(occupantGui);
    		System.out.println("Occupant has been returned home");

    		
    	}
    	if( r instanceof LandlordRole)
    	{
    		landLord = (LandlordRole) r;
    		landLord.setRenter(occupant);
        	occupant.setLandLord(landLord);

    		landLordGui = new LandlordGui(landLord);
    		landLord.setGui(landLordGui);
    		animation.addGui(landLordGui);
    	}
    
   
    }
    
}
