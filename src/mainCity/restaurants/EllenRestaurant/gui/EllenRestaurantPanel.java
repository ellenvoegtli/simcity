package mainCity.restaurants.EllenRestaurant.gui;

import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.EllenRestaurant.sharedData.*;
import role.Role;
import role.ellenRestaurant.EllenCashierRole;
import role.ellenRestaurant.EllenCookRole;
import role.ellenRestaurant.EllenCustomerRole;
import role.ellenRestaurant.EllenHostRole;
import role.ellenRestaurant.EllenNormalWaiterRole;
import role.ellenRestaurant.EllenWaiterRole;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mainCity.contactList.*;
/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
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

    private JPanel restLabel = new JPanel();
    public JPanel pausePanel = new JPanel();
    JButton pauseBtn = new JButton("Pause");
    JButton unpauseBtn = new JButton("Unpause");
    private JPanel cookInventoryPanel = new JPanel();
    JButton soupBtn = new JButton("Deplete soup");
    JButton pizzaBtn = new JButton("Deplete pizza");

    public EllenRestaurantPanel(EllenAnimationPanel panel) {
    	this.animation = panel;
        
        setLayout(new GridLayout(1, 2, 0, 0));

        add(restLabel);

        cookInventoryPanel.setLayout(new GridLayout(1, 2, 5, 5));
        soupBtn.addActionListener(this);
        pizzaBtn.addActionListener(this);
        cookInventoryPanel.add(soupBtn);
        cookInventoryPanel.add(pizzaBtn);
        //add(cookInventoryPanel);
                
      //Thread to tell cook to check every so often
        Runnable standChecker = new Runnable() {
			 public void run() {
				cook.msgCheckStand();
			 }
		 };
		 
		 ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		 executor.scheduleAtFixedRate(standChecker, 0, 15, TimeUnit.SECONDS);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == pauseBtn){
        	System.out.println("PAUSE BUTTON PRESSED.");
        	host.pause();
        	cook.pause();
        	for (EllenCustomerRole cust : customers){
        		cust.pause();
        	}
        	for (EllenWaiterRole waiter : waiters){
        		waiter.pause();
        	}
        }
        else if (e.getSource() == unpauseBtn){
        	System.out.println("RESTART BUTTON PRESSED.");
        	host.restart();
        	cook.restart();
        	for (EllenCustomerRole cust : customers){
        		cust.restart();
        	}
        	for (EllenWaiterRole waiter : waiters){
        		waiter.restart();
        	}
        }
        else if (e.getSource() == soupBtn){
        	System.out.println("DEPLETE SOUP BUTTON PRESSED.");
        	cook.depleteInventory("soup");
        }
        else if (e.getSource() == pizzaBtn){
        	System.out.println("DEPLETE PIZZA BUTTON PRESSED.");
        	cook.depleteInventory("pizza");
        }
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
        	EllenNormalWaiterRole w = (EllenNormalWaiterRole) r;
        	System.out.println("Setting EllenNormalWaiterRole: " + w.getName());
        	/*
    		if(r.getName().contains("share")) {
    			EllenSharedWaiterRole a = (EllenSharedWaiterRole) r;
    			a.setStand(stand);
    		}*/
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
