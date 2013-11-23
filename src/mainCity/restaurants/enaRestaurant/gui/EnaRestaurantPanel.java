package mainCity.restaurants.enaRestaurant.gui;

import mainCity.contactList.ContactList;
import mainCity.restaurants.enaRestaurant.EnaCashierRole;
import mainCity.restaurants.enaRestaurant.EnaCustomerRole;
import mainCity.restaurants.enaRestaurant.EnaWaiterRole;
import mainCity.restaurants.enaRestaurant.EnaHostRole;
import mainCity.restaurants.enaRestaurant.EnaCookRole;


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
    private HostGui hostGui = new HostGui(host);
    private EnaCookRole cook = new EnaCookRole("Bob");
    private CookGui cookGui = new CookGui(cook);
    private EnaCashierRole cashier = new EnaCashierRole("Tim");



    private Vector<EnaCustomerRole> customers = new Vector<EnaCustomerRole>();
    private Vector<EnaWaiterRole> waiters = new Vector<EnaWaiterRole>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private JPanel group = new JPanel();
    private ListPanel waiterPanel = new ListPanel(this, "Waiters");

    private EnaRestaurantGui gui; //reference to main gui

    public EnaRestaurantPanel(EnaRestaurantGui gui) {
        this.gui = gui;
        host = new EnaHostRole("Mr. Jeeves");
        host.setGui(hostGui);
       // cookGui = new CookGui(cook, gui);
        cook.setGui(cookGui);
        cook.setCashier(cashier);
        gui.animationPanel.addGui(hostGui);
		gui.animationPanel.addGui(cookGui);

        host.startThread();
        cook.startThread();
        cashier.startThread();
        	
        ContactList.getInstance().setEnaCook(cook);
        ContactList.getInstance().setEnaCashier(cashier);
        ContactList.getInstance().setEnaHost(host);

        

        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new GridLayout(2, 1, 10, 10));
        //group.setLayout(new BorderLayout());

        group.add(waiterPanel);
        
        group.add(customerPanel);

        initRestLabel();
        add(restLabel);
        add(group);
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
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + host.getName() + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

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
    			   customers.get(i).getGui().setHungry();

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
    public void handleRoleGui(Role r) {
    	if(r instanceof EnaWaiterRole) {
        	EnaWaiterRole w = (EnaWaiterRole) r;

    		/*if(r.getName().contains("share")) {
    			MarcusSharedWaiterRole a = (MarcusSharedWaiterRole) r;
    			a.setStand(stand);
    		}*/
            host.addWaiterRole(w);
            int pos = 22* host.waiters.size();
    		WaiterGui g = new WaiterGui(w, gui, pos);
        	
    		
    		w.setHost(host);
    		w.setGui(g);
            w.setCook(cook);
            w.setCashier(cashier);
            waiters.add(w);
    		gui.animationPanel.addGui(g);
    		System.out.println("Waiter has been added to the restaturant");

    		
    	}
    	
    	if(r instanceof EnaCustomerRole) {
    		EnaCustomerRole c = (EnaCustomerRole) r;
	    	
    		for(EnaCustomerRole cust : customers) { // Checking to make sure customer doesn't exist already
	    		if(cust == c) {
	    			return;
	    		}
	    	}
	    	
			customers.add(c);
			CustomerGui g = new CustomerGui(c, gui);
	
			gui.animationPanel.addGui(g);
			c.setHost(host);
			c.setCashier(cashier);
			c.setGui(g);
			
	
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
