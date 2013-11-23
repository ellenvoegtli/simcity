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

import housing.LandlordRole;
import housing.OccupantRole;
import housing.personHome;
import housing.gui.OccupantGui;
import housing.gui.LandlordGui;


public class HomePanel extends JPanel
{
	   

    private LandlordRole landLord;
    private LandlordGui landLordGui = new LandlordGui(landLord);


    private JPanel homeLabel = new JPanel();
    private JPanel group = new JPanel();
    private personHome house;
    private HomeGui gui; //reference to main gui
    private OccupantRole occupant;
    private OccupantGui occupantGui; //= new OccupantGui(occupant, gui);

    public HomePanel(HomeGui gui) {
        this.gui = gui;
        occupant = new OccupantRole("mark");
        occupant.setHouse(house);
        house = new personHome(occupant);
        house.setOccupant(occupant);
        occupantGui = new OccupantGui(occupant, gui);
        occupant.setGui(occupantGui);
              
        gui.animationPanel.addGui(occupantGui);

        occupant.startThread();
        occupantGui.setHungry();


        setLayout(new GridLayout(1, 2, 20, 20));
 

        initRestLabel();
        add(homeLabel);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        homeLabel.setLayout(new BoxLayout((Container)homeLabel, BoxLayout.Y_AXIS));
        //homeLabel.setLayout(new FlowLayout());
        label.setText(
                "<html><h3><u>House's Belongs To: </u></h3><table><tr><td>occupant:</td><td>" + occupant.getName());

        homeLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        homeLabel.add(label, BorderLayout.CENTER);
        homeLabel.add(new JLabel("               "), BorderLayout.EAST);
        homeLabel.add(new JLabel("               "), BorderLayout.WEST);
    }

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
    public void showInfo(String type, String name) 
    {

    	if(type.equals("Occupant"))
    	{
    		   gui.updateInfoPanel(name);

    	}
      
    	if(type.equals("Landlord"))
    	{
    		gui.updateInfoPanel(name);
    	}
      
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
    
    
    
    public void addPerson(String type, String name) 
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
			  occupant.getGui().setHungry();

    	}
    	
    }
   
    
   
    }
