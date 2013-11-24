package mainCity.restaurants.marcusRestaurant.gui;

import mainCity.contactList.ContactList;
import mainCity.restaurants.marcusRestaurant.sharedData.RevolvingStand;

import javax.swing.*;

import role.Role;
import role.marcusRestaurant.*;

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
    private MarcusAnimationPanel animation;
    private MarcusHostRole host;
   
    private Vector<MarcusCustomerRole> customers = new Vector<MarcusCustomerRole>();
    private Vector<MarcusWaiterRole> waiters = new Vector<MarcusWaiterRole>();
    private RevolvingStand stand = new RevolvingStand();
    
    private MarcusCashierRole cashier;
    private MarcusCookRole cook;
    private CookGui cookGui;

    public MarcusRestaurantPanel(MarcusAnimationPanel a) {
        this.animation = a;        
        setLayout(new GridLayout(1, 2, 20, 20));

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

    public void addPerson(String type, String name, boolean hungry) {
    	if (type.equals("Customers")) {
    		MarcusCustomerRole c = new MarcusCustomerRole(null, name);//should be fixed soon
    		customers.add(c);
    		CustomerGui g = new CustomerGui(c, animation, customers.indexOf(c));

    		animation.addGui(g);
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
    		
    		animation.addGui(g);
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
            
    		if(host != null) {
    			host.setCashier(cashier);
    			cashier.setHost(host);
    		}
    		
    		ContactList.getInstance().setMarcusCashier(cashier);
    	}
    	
    	if(r instanceof MarcusCookRole) {
    		cook = (MarcusCookRole) r;
    		cookGui = new CookGui(cook);
            cook.setStand(stand);
            animation.addGui(cookGui);
            cook.setGui(cookGui);
            cook.setCashier(cashier);
            
            if(host != null) host.setCook(cook);
            for(MarcusWaiterRole w : waiters) {
    			w.setCook(cook);
    		}
    		ContactList.getInstance().setMarcusCook(cook);
    	}
    	
    	if(r instanceof MarcusCustomerRole) {
    		MarcusCustomerRole c = (MarcusCustomerRole) r;
			customers.add(c);
			CustomerGui g = new CustomerGui(c, animation, customers.indexOf(c));
	
			animation.addGui(g);
			c.setHost(host);
			c.setGui(g);
			c.setCashier(cashier);
    	}
    	
    	if(r instanceof MarcusHostRole) {
    		host = (MarcusHostRole) r;
    		
    		for(MarcusWaiterRole w : waiters) {
    			w.setHost(host);
    			host.addWaiter(w);
    		}
    		for(MarcusCustomerRole c : customers) {
    			c.setHost(host);
    		}
    		
    		host.setCook(cook);
    		host.setCashier(cashier);
    		
    		if(cashier != null) cashier.setHost(host);
    		ContactList.getInstance().setMarcusHost(host);
    	}
    	
    	if(r instanceof MarcusWaiterRole) {
    		if(r instanceof MarcusSharedWaiterRole) {
    			((MarcusSharedWaiterRole) r).setStand(stand);
    		}
    		MarcusWaiterRole w = (MarcusWaiterRole) r;

    		WaiterGui g = new WaiterGui(w, waiters.size());
    		animation.addGui(g);
    		w.setHost(host);
    		w.setGui(g);
            w.setCook(cook);
            w.setCashier(cashier);
            if(host != null) host.addWaiter(w);
    		waiters.add(w);
    	}
    }
}
