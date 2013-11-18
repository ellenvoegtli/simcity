package mainCity.restaurants.jeffersonrestaurant.gui;

import javax.swing.*;

import mainCity.restaurants.jeffersonrestaurant.CashierRole;
import mainCity.restaurants.jeffersonrestaurant.CookRole;
import mainCity.restaurants.jeffersonrestaurant.CustomerRole;
import mainCity.restaurants.jeffersonrestaurant.HostRole;
import mainCity.restaurants.jeffersonrestaurant.MarketRole;
import mainCity.restaurants.jeffersonrestaurant.WaiterRole;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel {

    //Host, cook, waiters and customers
  // private WaiterAgent waiter = new WaiterAgent("Sarah");
    private HostRole host =new HostRole ("Sal");
  // private WaiterGui waiterGui = new WaiterGui(waiter);
    private CookRole cook = new CookRole("Jim");
    private CashierRole cashier = new CashierRole("Dave");
    
    private MarketRole m1 = new MarketRole();
    private MarketRole m2 = new MarketRole();
    private MarketRole m3 = new MarketRole();
    private MarketRole m4 = new MarketRole();
    
    
    
    
  

    private Vector<CustomerRole> customers = new Vector<CustomerRole>();
    private Vector<WaiterRole> waiters = new Vector<WaiterRole>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel(this,"Waiters");
    
    private JPanel group = new JPanel();

    private RestaurantGui gui; //reference to main gui
    
    public void addWaiterToList(WaiterRole w){
    	host.waiters.add(w);
    	
    }
    



    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;
       // waiter.setGui(waiterGui);
        //waiter.setCook(cook);
       // waiter.setHost(host);
        //gui.animationPanel.addGui(waiterGui);
       // waiter.startThread();
        cook.startThread();
        m1.startThread();
        m2.startThread();
        m3.startThread();
        m4.startThread();
        
        
        m1.setCook(cook);
        m2.setCook(cook);
        m3.setCook(cook);
        m4.setCook(cook);
        
        m1.setCashier(cashier);
        m2.setCashier(cashier);
        m3.setCashier(cashier);
        m4.setCashier(cashier);
        
        cook.addMarket(m1);
        cook.addMarket(m2);
        cook.addMarket(m3);
        cook.addMarket(m4);
        
        CookGui cg = new CookGui(cook,gui);
        cook.setGui(cg);
        gui.animationPanel.addGui(cg);
        
        host.startThread();
        cashier.startThread();
        //host.waiters.add(waiter);
       // waiters.add(waiter);

        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new GridLayout(1, 2, 10, 10));
        
        group.add(customerPanel);
        group.add(waiterPanel);

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
        restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        restLabel.setLayout(new BorderLayout());
      label.setText(
              "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + "Sal" + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

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

        if (type.equals("Customers")) {

            for (int i = 0; i < customers.size(); i++) {
                CustomerRole temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        if (type.equals("Waiters")) {

            for (int i = 0; i < waiters.size(); i++) {
                WaiterRole temp = waiters.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    /*
    public void addPerson(String type, String name) {

    	if (type.equals("Customers")) {
    		CustomerAgent c = new CustomerAgent(name);	
    		CustomerGui g = new CustomerGui(c, gui);

    		gui.animationPanel.addGui(g);// dw
    		c.setWaiter(waiter);
    		
    		c.setHost(host);
    		
    		c.setGui(g);
    		customers.add(c);
    		c.startThread();
    	}
    }
    */
    public void addPerson(String type, String name, boolean hungry) {
    	
    	
    	if (type.equals("Customers")) {
    		CustomerRole c = new CustomerRole(name);
    		c.setHost(host);
    		//host.waitingCustomers.add(c);
    		//for now only setting 1 waiter, not addlist
    		//host.setWaiter(waiter);
    		CustomerGui g = new CustomerGui(c, gui);
    		if (hungry){g.setHungry();}

    		gui.animationPanel.addGui(g);// dw
    	//	c.setWaiter(waiter);
    		c.setGui(g);
    		
    		customers.add(c);
    		c.startThread();
    		if(name.equalsIgnoreCase("broke")){
    			c.setMoney(0);	
    		}
    		
    		else if(name.equalsIgnoreCase("poor")){
    			c.setMoney(5.99);
    			
    		}
    		else if(name.equalsIgnoreCase("cheapskate")){
    			c.setMoney(-1);
    		}
    		else{
    			c.setMoney(100);
    		}
    		
    		
    		
    	}
    	if (type.equals("Waiters")) {
    		WaiterRole w =  new WaiterRole(name);
        	WaiterGui waiterGui = new WaiterGui(w);
        	w.setCook(cook);
            w.setHost(host);
            w.setCashier(cashier);
            gui.animationPanel.addGui(waiterGui);
    		w.setGui(waiterGui);
    		waiters.add(w);
    		waiterGui.setOrigin(waiters.size()*25 + 50, 170);
    		host.waiters.add(w);
    		
    		System.out.println("waiter added");
            w.startThread();
            //host.msgWaitersUpdate();
            //host.msgWaiterAdded();
    	}
    	
    }
    /*
    public void addWaiter(String waitername){
    	WaiterAgent w =  new WaiterAgent(waitername);
    	WaiterGui waiterGui = new WaiterGui(w);
    	
        w.setGui(waiterGui);
        w.setCook(cook);
        w.setHost(host);
        host.waiters.add(w);
        gui.animationPanel.addGui(waiterGui);
        waiters.add(w);
        System.out.println("waiter added");
        w.startThread();
    	
    }
   */ 
}
