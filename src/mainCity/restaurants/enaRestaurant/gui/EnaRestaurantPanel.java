package mainCity.restaurants.enaRestaurant.gui;

import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.market1.gui.CustomerGui;
import mainCity.restaurants.enaRestaurant.EnaCashierRole;
import mainCity.restaurants.enaRestaurant.EnaCustomerRole;
import mainCity.restaurants.enaRestaurant.EnaSharedWaiterRole;
import mainCity.restaurants.enaRestaurant.EnaWaiterRole;
import mainCity.restaurants.enaRestaurant.EnaHostRole;
import mainCity.restaurants.enaRestaurant.EnaCookRole;
import mainCity.restaurants.enaRestaurant.gui.EnaCookGui;
import mainCity.restaurants.enaRestaurant.sharedData.RevolvingStand;
import mainCity.restaurants.marcusRestaurant.gui.MarcusAnimationPanel;

import javax.swing.*;

import role.Role;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class EnaRestaurantPanel extends JPanel 
{

	
	
    private EnaHostRole host;
    private EnaHostGui hostGui;
    private EnaCookRole cook;
    private EnaCookGui cookGui;
    private EnaCashierRole cashier;
    private EnaWaiterRole waiter;
    private EnaCustomerRole customer;
    private RevolvingStand stand = new RevolvingStand();



    private Vector<EnaCustomerRole> customers = new Vector<EnaCustomerRole>();
    private Vector<EnaWaiterRole> waiters = new Vector<EnaWaiterRole>();
    private EnaAnimationPanel animation;

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private JPanel group = new JPanel();
    private ListPanel waiterPanel = new ListPanel(this, "Waiters");

    private EnaRestaurantGui gui; //reference to main gui

    public EnaRestaurantPanel(EnaAnimationPanel pnl) {
        
    	this.animation = pnl;
       
        //creating instances of people to test shared waiters  w/o rest of city
        /*PersonAgent base = new PersonAgent("Host");
		host = new EnaHostRole(base, base.getName());
		base.addRole(PersonAgent.ActionType.work, host);
		host.setActive();
		
		PersonAgent base2 = new PersonAgent("Cashier");
		cashier = new EnaCashierRole(base2, base2.getName());
		base2.addRole(PersonAgent.ActionType.work, cashier);
		cashier.setActive();
		
		PersonAgent base3 = new PersonAgent("Cook");
		cook = new EnaCookRole(base3, base3.getName());
		base3.addRole(PersonAgent.ActionType.work, cook);
		cook.setActive();
		
		PersonAgent base4 = new PersonAgent("Waiter");
		waiter = new EnaSharedWaiterRole(base4, base4.getName());
		base4.addRole(PersonAgent.ActionType.work, waiter);
		waiter.setActive();
		
		PersonAgent base5 = new PersonAgent("Customer");
		customer = new EnaCustomerRole(base5, base5.getName());
		base5.addRole(PersonAgent.ActionType.hungry, customer);
		customer.setActive();
		
		
		cookGui = new EnaCookGui(cook);
		cook.setGui(cookGui);
		animation.addGui(cookGui);
		cook.setCashier(cashier);
	
		host.setCashier(cashier);
		host.setCook(cook);
	
		

		customers.add(customer);
		//int posX = 22 * customers.size();
	EnaCustomerGui g = new EnaCustomerGui(customer, gui);
	animation.addGui(g);// dw
	customer.setHost(host);
	customer.setCashier(cashier);
	customer.setGui(g);
	customer.startThread();
	
		host.addWaiterRole(waiter);
		int pos = 22* host.waiters.size();
		EnaWaiterGui wg = new EnaWaiterGui(waiter, gui, pos);  
		waiter.setGui(wg);
		waiter.setHost(host);
		waiter.setCook(cook);
		waiter.setCashier(cashier);
		waiters.add(waiter);
		animation.addGui(wg);
		  
		g.setHungry();
	//host.setGui(hostGui);
        //cook.setGui(cookGui);
        //cook.setCashier(cashier);
        //gui.animationPanel.addGui(hostGui);
		
		//animation.addGui(gui);
		
		base.startThread();
		base2.startThread();
		base3.startThread();
		base4.startThread();
		base5.startThread();
     
		g.setHungry();

    */
        	
        ContactList.getInstance().setEnaCook(cook);
        ContactList.getInstance().setEnaCashier(cashier);
        ContactList.getInstance().setEnaHost(host);

        

       // setLayout(new GridLayout(1, 2, 20, 20));
        //group.setLayout(new GridLayout(2, 1, 10, 10));

        //group.add(waiterPanel);
        
        //group.add(customerPanel);

        //initRestLabel();
        //add(restLabel);
        //add(group);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        //restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        restLabel.setLayout(new FlowLayout());
       label.setText(
               "<html></td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Porkchops</td><td>$10.99</td></tr><tr><td>Lamb</td><td>$5.99</td></tr><tr><td>Lambchops</td><td>$8.99</td></tr></table><br></html>");

        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(label, BorderLayout.CENTER);
        restLabel.add(new JLabel("               "), BorderLayout.EAST);
        restLabel.add(new JLabel("               "), BorderLayout.WEST);
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

        if (type.equals("Customers")) 
        {

            for (int i = 0; i < customers.size(); i++) 
            {
                EnaCustomerRole temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        if(type.equals("Waiters"))
        {
        	for(int j=0; j<waiters.size(); j++)
        	{
        		EnaWaiterRole tempW = waiters.get(j);
        		if(tempW.getName()==name)
        		{
        			gui.updateInfoPanel(tempW);
        		}
        	}
        }
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    
    public void createHunger(String cust)
    {
    	for(int i=0; i<customers.size(); i++)
    	{
    		if(cust.equals(customers.get(i).getName())) 
    		{
    			   customers.get(i).getGui().goInside();

    		}
    	}
    }
    
    public void wantBreak(String waiter)
    {
    	for (int i =0; i<waiters.size(); i++)
    	{
    		if(waiter.equals(waiters.get(i).getName()))
    				{
    					waiters.get(i).getGui().setBreak();
    				}
    	}
    }
    
    
    /*public void addPerson(String type, String name) 
    {

    	if (type.equals("Customers")) 
    	{
    		EnaCustomerRole c = new EnaCustomerRole(name);	
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
    		EnaWaiterRole w = new EnaWaiterRole(name);    
    		host.addWaiterRole(w);
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
    public void handleRole(Role r) 
    {
    	if(r instanceof EnaWaiterRole) 
    	{
    		if(r instanceof EnaSharedWaiterRole) 
    		{
    			((EnaSharedWaiterRole) r).setStand(stand);
    		}
    		EnaWaiterRole w = (EnaWaiterRole) r;

          int pos = 22;
          if(host != null)  pos = 22* host.waiters.size();
           EnaWaiterGui g = new EnaWaiterGui(w, gui,pos);
   		   animation.addGui(g);

    		if(host != null) host.addWaiterRole(w);
    		w.setHost(host);
    		w.setGui(g);
            w.setCook(cook);
            w.setCashier(cashier);
            waiters.add(w);
          
    		System.out.println("Waiter has been added to the restaturant");

    		
    	}
    	
    	if(r instanceof EnaCustomerRole) 
    	{
    		EnaCustomerRole c = (EnaCustomerRole) r;
	    	
    		for(EnaCustomerRole cust : customers) { // Checking to make sure customer doesn't exist already
	    		if(cust == c) {
	    			return;
	    		}
	    	}
	    	
			customers.add(c);
			EnaCustomerGui g = new EnaCustomerGui(c, gui);
	
			animation.addGui(g);
			c.setHost(host);
			c.setCashier(cashier);
			c.setGui(g);
			
	
    	}
    	
    	if(r instanceof EnaHostRole) 
    	{
    		host = (EnaHostRole) r;
	    	
			for(EnaWaiterRole w : waiters) 
			{
    			w.setHost(host);
    			host.addWaiterRole(w);
    		}
    		for(EnaCustomerRole c : customers) 
    		{
    			c.setHost(host);
    		}
    		host.setCook(cook);
    		host.setCashier(cashier);
    		
    		if(cashier != null)
    		{
    			cashier.setHost(host);
    		}
    		ContactList.getInstance().setEnaHost(host);

    		
    	}
    	
    	
    	if(r instanceof EnaCashierRole) 
    	{
    		cashier = (EnaCashierRole) r;
    		for(EnaWaiterRole w : waiters) {
    			w.setCashier(cashier);
    		}
    		for(EnaCustomerRole c : customers) {
    			c.setCashier(cashier);
    		}
    		if(host != null)
    		{
    			host.setCashier(cashier);
    			cashier.setHost(host);
    		}
			
    	}
		ContactList.getInstance().setEnaCashier(cashier);

    	
    	if(r instanceof EnaCookRole) {
    		cook = (EnaCookRole) r;
	    	
			cookGui = new EnaCookGui(cook);
            //cook.setStand(stand);
    		animation.addGui(cookGui);
            cook.setGui(cookGui);
            cook.setCashier(cashier);
            if(host != null)
            {
            	host.setCook(cook);
            }
            	for(EnaWaiterRole w : waiters) 
            	{
            		w.setCook(cook);
            	}
        		ContactList.getInstance().setEnaCook(cook);

    	}
    }
    
    public void pauseAll()
    {
    	for ( EnaWaiterRole waiter: waiters)
    	{
    		waiter.pause();

    	} 
    	for(EnaCustomerRole customer : customers)
    	{
    		customer.pause();
    	}
    	host.pause();
    	cook.pause();
    	cashier.pause();
    	
    	
    }
    public void restartAll()
    {    	
    	host.restart();
    	cook.restart();
    	cashier.restart();
    	
    	for(EnaWaiterRole waiter: waiters)
    	{
    		 waiter.restart();
    	}
    	for(EnaCustomerRole customer: customers)
    	{
    		customer.restart();
    	}
    }
    


}
