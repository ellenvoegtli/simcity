package housing.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.BorderFactory;
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
    private ListPanel occupantPanel = new ListPanel(this, "Occupant");
    private JPanel group = new JPanel();
    private ListPanel waiterPanel = new ListPanel(this, "Landlord");

    private HomeGui gui; //reference to main gui
    private OccupantRole occupant;
    private OccupantGui occupantGui = new OccupantGui(occupant, gui);

    public HomePanel(HomeGui gui) {
        this.gui = gui;
        occupant = new OccupantRole("mark");
        occupant.setGui(occupantGui);
      
       // cookGui = new CookGui(cook, gui);
        
        gui.animationPanel.addGui(occupantGui);

        occupant.startThread();
      

        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new GridLayout(2, 1, 10, 10));
        //group.setLayout(new BorderLayout());

        group.add(occupantPanel);
        
        //group.add(customerPanel);

        initRestLabel();
        add(homeLabel);
        add(group);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        //restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        homeLabel.setLayout(new FlowLayout());
        label.setText(
                "<html><h3><u>House's Features</u></h3><table><tr><td>occupant:</td><td>" + occupant.getName());

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
    public void showInfo(String type, String name) {

    	if(type.equals("Occupant"))
    	{
    		   gui.updateInfoPanel(name);

    	}
       /* if (type.equals("Customers")) 
        {

            for (int i = 0; i < customers.size(); i++) 
            {
                CustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }*/
    	if(type.equals("Landlord"))
    	{
    		gui.updateInfoPanel(name);
    	}
       /* if(type.equals("Waiters"))
        {
        	for(int j=0; j<waiters.size(); j++)
        	{
        		WaiterAgent tempW = waiters.get(j);
        		if(tempW.getName()==name)
        		{
        			gui.updateInfoPanel(tempW);
        		}
        	}
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
    
    /*public void wantBreak(String waiter)
    {
    	for (int i =0; i<waiters.size(); i++)
    	{
    		if(waiter.equals(waiters.get(i).getName()))
    				{
    					waiters.get(i).getGui().setBreak();
    				}
    	}
    }*/
    
    
   /* public void addPerson(String type, String name) 
    {

    	if (type.equals("Occupant")) 
    	{
    		CustomerAgent c = new CustomerAgent(name);	
    		customers.add(c);
    			//int posX = 22 * customers.size();
    		CustomerGui g = new CustomerGui(c, gui);
    		gui.animationPanel.addGui(g);// dw
    		c.setHost(host);
    		c.setCashier(cashier);
    		c.setGui(g);
    		c.startThread();
    	}
    	if(type.equals("Waiters"))
    	{
    		WaiterAgent w = new WaiterAgent(name);    
    		host.addWaiterAgent(w);
    		int pos = 22* host.waiters.size();
    		WaiterGui wg = new WaiterGui(w, gui, pos);  
    		w.setGui(wg);
    		w.setHost(host);
    		w.setCook(cook);
    		w.setCashier(cashier);
    		waiters.add(w);
    		gui.animationPanel.addGui(wg);
    		w.startThread();
    		System.out.println("Waiter has been added to the restaturant:  " + name);
    	}
    }*/
   
    
   
    }
