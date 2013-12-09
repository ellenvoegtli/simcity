package housing.gui;

import housing.LandlordRole;
import housing.OccupantRole;
import housing.personHome;
import housing.Interfaces.landLord;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import role.Role;



public class HomePanel extends JPanel
{
	   

    private landLord landLord;
    private LandlordGui landLordGui;
    private Vector<OccupantRole> renting = new Vector<OccupantRole>();


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
    	house = new personHome(occupant);		
    
    }
 
    public void createHunger(String occ)
    {
    		if(occ.equals(occupant.getName()))
    		{
    			  occupant.getGui().setHungry();
    		}
    	
    }
    
    

   
    public void handleRoleGui(Role r) 
    {
    	
    	if(r instanceof OccupantRole) 
    	{

        	occupant = (OccupantRole) r;
        	house = new personHome(occupant);
        	//homeList.add(house);

        	occupant.setHouse(house);
        	if(!occupant.owner)
        	{
        		renting.add(occupant); 
        		occupant.setLandLord(landLord);

        	}
            house.setOccupant(occupant);
    		occupantGui = new OccupantGui(occupant, animation); 
    		animation.addGui(occupantGui);
    		occupant.setGui(occupantGui);
    		
    		occupantGui.setHungry();    

    		
    	}
    	if( r instanceof LandlordRole)
    	{
    		landLord = (housing.Interfaces.landLord) r;
    		
    		for(OccupantRole oc : renting) 
        	{
        		((LandlordRole) landLord).setRenter(oc);
        	    oc.setLandLord(landLord);

        	}
    		landLordGui = new LandlordGui(landLord, animation);
    		((LandlordRole) landLord).setGui(landLordGui);
    		animation.addGui(landLordGui);
    	}
    
   
    }
    
}
