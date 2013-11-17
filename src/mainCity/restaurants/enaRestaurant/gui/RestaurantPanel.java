package mainCity.restaurants.enaRestaurant.gui;

import mainCity.restaurants.enaRestaurant.CashierRole;
import mainCity.restaurants.enaRestaurant.CustomerRole;
import mainCity.restaurants.enaRestaurant.MarketRole;
import mainCity.restaurants.enaRestaurant.WaiterRole;
import mainCity.restaurants.enaRestaurant.HostRole;
import mainCity.restaurants.enaRestaurant.CookRole;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel {

    //Host, cook, waiters and customers
    //private WaiterRole waiter;
    //private WaiterGui waiterGui;
    private HostRole host;
    private HostGui hostGui = new HostGui(host);
    private CookRole cook = new CookRole("Bob");
    private CookGui cookGui = new CookGui(cook);
    private CashierRole cashier = new CashierRole("Tim");
    private MarketRole market1 = new MarketRole("market1");
    private MarketRole market2 = new MarketRole("market2");
    private MarketRole market3 = new MarketRole("market3");


    private Vector<CustomerRole> customers = new Vector<CustomerRole>();
    private Vector<WaiterRole> waiters = new Vector<WaiterRole>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private JPanel group = new JPanel();
    private ListPanel waiterPanel = new ListPanel(this, "Waiters");

    private RestaurantGui gui; //reference to main gui

    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;
        host = new HostRole("Mr. Jeeves");
        host.setGui(hostGui);
       // cookGui = new CookGui(cook, gui);
        cook.setGui(cookGui);
        gui.animationPanel.addGui(hostGui);
		gui.animationPanel.addGui(cookGui);

        host.startThread();
        cook.startThread();
        cashier.startThread();
        market1.startThread();
        market2.startThread();
        market3.startThread();
        cook.addMarkets(market1);
        cook.addMarkets(market2);
        cook.addMarkets(market3);
        market1.setCashierRole(cashier);
        market2.setCashierRole(cashier);
        market3.setCashierRole(cashier);

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
                CustomerRole temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        if(type.equals("Waiters"))
        {
        	for(int j=0; j<waiters.size(); j++)
        	{
        		WaiterRole tempW = waiters.get(j);
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
    
    
    public void addPerson(String type, String name) 
    {

    	if (type.equals("Customers")) 
    	{
    		CustomerRole c = new CustomerRole(name);	
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
    		WaiterRole w = new WaiterRole(name);    
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
    }
   
    
    public void pauseAll()
    {
    	for ( WaiterRole waiter: waiters)
    	{
    		waiter.pause();

    	} 
    	for(CustomerRole customer : customers)
    	{
    		customer.pause();
    	}
    	host.pause();
    	cook.pause();
    	cashier.pause();
    	market1.pause();
    	market2.pause();
    	market3.pause();
    	
    }
    public void restartAll()
    {    	
    	host.restart();
    	cook.restart();
    	cashier.restart();
    	market1.restart();
    	market2.restart();
    	market3.restart();
    	for(WaiterRole waiter: waiters)
    	{
    		 waiter.restart();
    	}
    	for(CustomerRole customer: customers)
    	{
    		customer.restart();
    	}
    }

}
