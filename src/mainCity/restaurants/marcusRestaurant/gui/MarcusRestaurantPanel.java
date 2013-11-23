package mainCity.restaurants.marcusRestaurant.gui;

import mainCity.restaurants.marcusRestaurant.sharedData.RevolvingStand;

import javax.swing.*;

import role.Role;
import role.marcusRestaurant.MarcusCashierRole;
import role.marcusRestaurant.MarcusCookRole;
import role.marcusRestaurant.MarcusCustomerRole;
import role.marcusRestaurant.MarcusHostRole;
import role.marcusRestaurant.MarcusNormalWaiterRole;
import role.marcusRestaurant.MarcusSharedWaiterRole;
import role.marcusRestaurant.MarcusWaiterRole;

import java.awt.*;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class MarcusRestaurantPanel extends JPanel {
    private MarcusRestaurantGui gui; //reference to main gui

    //Host, cook, waiters and customers
    private MarcusHostRole host;// = new MarcusHostRole("Sarah");
   
    private Vector<MarcusCustomerRole> customers = new Vector<MarcusCustomerRole>();
    private Vector<MarcusWaiterRole> waiters = new Vector<MarcusWaiterRole>();
    private RevolvingStand stand = new RevolvingStand();
    
    private MarcusCashierRole cashier;// = new MarcusCashierRole();
    private MarcusCookRole cook; //= new MarcusCookRole();
    private CookGui cookGui;

    //private JPanel restLabel = new JPanel();
    //private ListPanel customerPanel = new ListPanel(this, "Customers");
    //private ListPanel waiterPanel = new ListPanel(this, "Waiters");
    //private JPanel group = new JPanel();


    public MarcusRestaurantPanel(MarcusRestaurantGui gui) {
        this.gui = gui;
        //cookGui = new CookGui(cook, gui);
        //cook.setStand(stand);
		//gui.animationPanel.addGui(cookGui);
        //cook.setGui(cookGui);
        //cook.setCashier(cashier);
        
        //host.startThread();
        //cashier.startThread();
        
        setLayout(new GridLayout(1, 2, 20, 20));
        //group.setLayout(new GridLayout(1, 2, 10, 10));

        //group.add(customerPanel);
        //group.add(waiterPanel);

        //initRestLabel();
        //add(restLabel);
        //add(group);
                
        //Thread to tell cook to check every so often
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

/*
    private void initRestLabel() {
        JLabel label = new JLabel();
        //restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        restLabel.setLayout(new BorderLayout());
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + host.getName() + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(label, BorderLayout.CENTER);
        restLabel.add(new JLabel("               "), BorderLayout.EAST);
        restLabel.add(new JLabel("               "), BorderLayout.WEST);
    }
*/
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
                MarcusCustomerRole temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        
        if(type.equals("Waiters")) {
        	for(int i = 0; i < waiters.size(); i++) {
        		MarcusWaiterRole temp = waiters.get(i);

        		if(temp.getName().equals(name)) {
        			gui.updateInfoPanel(temp);
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
    
    public void addPerson(String type, String name, boolean hungry) {
    	if (type.equals("Customers")) {
    		MarcusCustomerRole c = new MarcusCustomerRole(null, name);//should be fixed soon
    		customers.add(c);
    		CustomerGui g = new CustomerGui(c, gui, customers.indexOf(c));

    		gui.animationPanel.addGui(g);
    		//c.setWaiter(waiter1);
    		c.setHost(host);
    		c.setGui(g);
    		c.setCashier(cashier);
    		
    		if(hungry) {
    			c.getGui().goInside();
    		}
    		
    		c.startThread();
    	}
    }

    public void addWaiter(String name) {
    		MarcusWaiterRole w;
    		if(name.contains("share")) {
    			w = new MarcusSharedWaiterRole(null, name);
    			MarcusSharedWaiterRole a = (MarcusSharedWaiterRole) w;
    			a.setStand(stand);
    		}
    		else {
    			w = new MarcusNormalWaiterRole(null, name);
    		}
    		
    		WaiterGui g = new WaiterGui(w, waiters.size());
    		
    		gui.animationPanel.addGui(g);
    		w.setHost(host);
    		w.setGui(g);
            w.setCook(cook);
            w.setCashier(cashier);
            host.addWaiter(w);
    		waiters.add(w);

    		w.startThread();
    }
    
    public void callPause() {
    	host.pause();
    	cook.pause();
    	
    	for(int i = 0; i < waiters.size(); ++i) {
    		waiters.get(i).pause();
    	}
    	
    	for(int i = 0; i < customers.size(); ++i) {
    		customers.get(i).pause();
    	}
    }
    
    public void callResume() {
    	host.restart();
    	cook.restart();
    	
    	for(int i = 0; i < waiters.size(); ++i) {
    		waiters.get(i).restart();
    	}
    	
    	for(int i = 0; i < customers.size(); ++i) {
    		customers.get(i).restart();
    	}
    }
    
    public MarcusHostRole getHost() {
    	return host;
    }
    
    public MarcusCashierRole getCashier() {
    	return cashier;
    }
    
    public void handleRole(Role r) {
    	if(r instanceof MarcusCashierRole) {
    		cashier = (MarcusCashierRole) r;
    		
    		for(MarcusWaiterRole w : waiters) {
    			w.setCashier(cashier);
    		}
    		for(MarcusCustomerRole c : customers) {
    			c.setCashier(cashier);
    		}
    	}
    	
    	if(r instanceof MarcusCookRole) {
    		cook = (MarcusCookRole) r;
    		cookGui = new CookGui(cook, gui);
            cook.setStand(stand);
    		gui.animationPanel.addGui(cookGui);
            cook.setGui(cookGui);
            cook.setCashier(cashier);
            
            for(MarcusWaiterRole w : waiters) {
    			w.setCook(cook);
    		}
    	}
    	
    	if(r instanceof MarcusCustomerRole) {
    		MarcusCustomerRole c = (MarcusCustomerRole) r;
			customers.add(c);
			CustomerGui g = new CustomerGui(c, gui, customers.indexOf(c));
	
			gui.animationPanel.addGui(g);
			c.setHost(host);
			c.setGui(g);
			c.setCashier(cashier);
    	}
    	
    	if(r instanceof MarcusHostRole) {
    		host = (MarcusHostRole) r;
    		
    		for(MarcusWaiterRole w : waiters) {
    			w.setHost(host);
    		}
    		for(MarcusCustomerRole c : customers) {
    			c.setHost(host);
    		}
    	}
    	
    	if(r instanceof MarcusWaiterRole) {
    		if(r instanceof MarcusSharedWaiterRole) {
    			((MarcusSharedWaiterRole) r).setStand(stand);
    		}
    		MarcusWaiterRole w = (MarcusWaiterRole) r;

    		WaiterGui g = new WaiterGui(w, waiters.size());
    		gui.animationPanel.addGui(g);
    		w.setHost(host);
    		w.setGui(g);
            w.setCook(cook);
            w.setCashier(cashier);
            host.addWaiter(w);
    		waiters.add(w);
    	}
    }
}
