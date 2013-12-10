package mainCity.restaurants.enaRestaurant.gui;

import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.market.gui.CustomerGui;
import mainCity.restaurants.enaRestaurant.gui.EnaCookGui;
import mainCity.restaurants.enaRestaurant.sharedData.RevolvingStand;
import mainCity.restaurants.marcusRestaurant.gui.MarcusAnimationPanel;

import javax.swing.*;

import role.Role;
import role.enaRestaurant.EnaCashierRole;
import role.enaRestaurant.EnaCookRole;
import role.enaRestaurant.EnaCustomerRole;
import role.enaRestaurant.EnaHostRole;
import role.enaRestaurant.EnaSharedWaiterRole;
import role.enaRestaurant.EnaWaiterRole;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
       
        	
        ContactList.getInstance().setEnaCook(cook);
        ContactList.getInstance().setEnaCashier(cashier);
        ContactList.getInstance().setEnaHost(host);
        
        Runnable standChecker = new Runnable() {
    			 public void run() {
    				try {
    					if(cook.isActive())
    						cook.msgCheckStand();
    				}
    				catch(NullPointerException e) {
    					
    				}
    			 }
    		 };
    		 
    		 ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    		 executor.scheduleAtFixedRate(standChecker, 0, 15, TimeUnit.SECONDS);
    }

   
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
            cook.setStand(stand);
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
