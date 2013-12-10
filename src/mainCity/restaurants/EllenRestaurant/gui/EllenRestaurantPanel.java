package mainCity.restaurants.EllenRestaurant.gui;

import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.EllenRestaurant.sharedData.*;
import role.Role;
import role.ellenRestaurant.*;
import mainCity.contactList.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class EllenRestaurantPanel extends JPanel implements ActionListener{
	private EllenAnimationPanel animation;

	private EllenHostRole host;
	private EllenCashierRole cashier;
	private EllenCookRole cook;
	
    private RevolvingStand revolvingStand = new RevolvingStand();
    private final int WINDOWX = 550;
    private final int WINDOWY = 350;
    private int waiterX = 550/3;
    private int waiterY = 30;
        
    private Vector<EllenCustomerRole> customers = new Vector<EllenCustomerRole>();
    private Vector<EllenWaiterRole> waiters = new Vector<EllenWaiterRole>();


    public EllenRestaurantPanel(EllenAnimationPanel panel) {
    	this.animation = panel;
        
        setLayout(new GridLayout(1, 2, 0, 0));

                
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
    
    
    public void actionPerformed(ActionEvent e) {
    }


    public void handleRole(Role r){
    	if(r instanceof EllenCashierRole) {
    		cashier = (EllenCashierRole) r;
    		
    		for(EllenWaiterRole w : waiters) {
    			w.setCashier(cashier);
    		}
    		for(EllenCustomerRole c : customers) {
    			c.setCashier(cashier);
    		}
            
    		if(host != null) {
    			host.setCashier(cashier);
    			cashier.setHost(host);
    		}
    		
    		ContactList.getInstance().setEllenCashier(cashier);
    	}
    	
    	if(r instanceof EllenCookRole) {
    		cook = (EllenCookRole) r;
            cook.setStand(revolvingStand);
            cook.setCashier(cashier);
            cook.setMenu(new EllenMenu());
            
            KitchenGui kitchenGui = new KitchenGui(animation);
            cook.setKitchenGui(kitchenGui);
            animation.addGui(kitchenGui);
            
            if(host != null) host.setCook(cook);
            for(EllenWaiterRole w : waiters) {
    			w.setCook(cook);
    		}
    		ContactList.getInstance().setEllenCook(cook);
    	}
    	
    	if(r instanceof EllenHostRole) {
    		host = (EllenHostRole) r;
    		
        	System.out.println("Setting EllenHostRole: " + host.getName());
    		for(EllenWaiterRole w : waiters) {
    			w.setHost(host);
    			host.addWaiter(w);
    		}
    		for(EllenCustomerRole c : customers) {
    			c.setHost(host);
    		}
    		
    		host.setCook(cook);
    		host.setCashier(cashier);
    		
    		if(cashier != null) cashier.setHost(host);
    		ContactList.getInstance().setEllenHost(host);
    	}
    	
    	if(r instanceof EllenWaiterRole) {
        	if (r instanceof EllenSharedDataWaiterRole){
        		((EllenSharedDataWaiterRole) r).setStand(revolvingStand);
        	}
        	EllenWaiterRole w = (EllenWaiterRole) r;
        	
        	waiterX += 30;
    		WaiterGui g = new WaiterGui(w, animation, waiterX, waiterY);
    		animation.addGui(g);
            if(host != null) host.addWaiter(w);
    		w.setHost(host);
    		w.setGui(g);
            w.setCook(cook);
            w.setCashier(cashier);
    		waiters.add(w);
    	}
    	
    	if(r instanceof EllenCustomerRole) {
    		EllenCustomerRole c = (EllenCustomerRole) r;
	    	
    		for(EllenCustomerRole cust : customers) { // Checking to make sure customer doesn't exist already
	    		if (cust == c) return;
	    	}
			customers.add(c);
			CustomerGui g = new CustomerGui(c, animation);
    		int i = 0;
    		for (EllenCustomerRole cust : customers){
    			if (cust.equals(c)) g.setWaitingAreaPosition(10 + (i%5)*25, (10 + ( (int)(Math.floor(i/5)) *25) ));
    			else i++;
    		}
			animation.addGui(g);
			c.setHost(host);
			c.setGui(g);
			c.setCashier(cashier);
    	}
    }
    
    
}
